package br.com.nsym.domain.model.service.fiscal.reforma;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import br.com.nsym.domain.model.entity.fiscal.CFOP;
import br.com.nsym.domain.model.entity.fiscal.Ncm;
import br.com.nsym.domain.model.entity.fiscal.ParamReforma2026;
import br.com.nsym.domain.model.entity.fiscal.Cfe.NfceItem;
import br.com.nsym.domain.model.entity.fiscal.nfe.ItemNfe;
import br.com.nsym.domain.model.entity.fiscal.reforma.CClassTrib;
import br.com.nsym.domain.model.entity.fiscal.reforma.CCredPres;
import br.com.nsym.domain.model.repository.fiscal.NCMRepository;
import br.com.nsym.domain.model.repository.fiscal.ParamReforma2026Repository;
import br.com.nsym.domain.model.repository.fiscal.reforma.CCredPresRepository;

@RequestScoped
public class ParametrizacaoReforma2026Service {

	@Inject ParamReforma2026Repository repo;
	@Inject NCMRepository ncmRepo;
	@Inject
	private CCredPresRepository cCredPresRepo;

	public static class Aliquotas {
		public BigDecimal pCbs, pIbs, pIs;
		public Aliquotas(BigDecimal cbs, BigDecimal ibs, BigDecimal is) { this.pCbs=cbs; this.pIbs=ibs; this.pIs=is; }
	}

	@Transactional
	public ParamReforma2026 aliquotaItemNfeRef(ItemNfe it,String ufOrig,String ufDest,String cnaeEmit,String tipoCliente,Boolean consumidorFinal,
			String ibgeMunDest,LocalDate dataOp,Long idEmpresa,Long idFilial) {

		// 1) Busca regras aplicáveis

		List<ParamReforma2026> regras = repo.listaCriteriaPorFilial(idEmpresa, idFilial, false, false); // pego TODAS AS REGRAS para empresa/filial
		
		// 2) Escolhe a melhor regra
		ParamReforma2026 r = escolherRegra(
				regras,it.getProduto().getNcm().getNcm(),it.getCfopItem().getCfop(),safe(it, "getCest"),safe(it, "getExTipi"),safe(it, "getCst"),
				safe(it, "getCsosn"),ufOrig,ufDest,ibgeMunDest,cnaeEmit,tipoCliente,consumidorFinal,safe(it.getProduto(), "getReferencia"),dataOp);

		// 3) Caso exista regra encontrada
		if (r != null) {
			System.out.println("entrei no EscolherRegra != null ");
			BigDecimal cbs = ns(r.getPCbs(), "0.90");
			BigDecimal ibs = ns(r.getPIbs(), "0.10");
			BigDecimal is  = ns(r.getPIs(), "0");

			if (suprimirISSeIsento(it.getProduto().getNcm().getNcm(), it, idEmpresa)) {
				is = BigDecimal.ZERO;
			}

			// Usa a própria instância da regra como "resultado",
			// apenas ajustando as alíquotas finais calculadas
			r.setPCbs(cbs);
			r.setPIbs(ibs);
			r.setPIs(is);

			aplicarCreditoPresumido(r, dataOp,idEmpresa,idFilial);

			return r;
		}

		// 4) Sem regra - retorna objeto com valores padrão
		ParamReforma2026 paramDefault = new ParamReforma2026();
		paramDefault.setPCbs(new BigDecimal("0.90"));
		paramDefault.setPIbs(new BigDecimal("0.10"));
		paramDefault.setPIs(BigDecimal.ZERO);

		return paramDefault;
	}

	private void aplicarCreditoPresumido(ParamReforma2026 regra, LocalDate dataOp,Long idEmpresa,Long idFilial) {

		if (regra == null) {
			return;
		}

		String codCred = regra.getCodigoCredPres(); 
		if (codCred == null || codCred.trim().isEmpty()) {
			regra.setRegraCredPres(null);
			regra.setPCredPres(BigDecimal.ZERO);
			regra.setVCredPres(BigDecimal.ZERO);
			return;
		}

		CCredPres cfg = cCredPresRepo.findVigente(codCred, dataOp, idEmpresa, idFilial);
		if (cfg == null) {
			regra.setRegraCredPres(null);
			regra.setPCredPres(BigDecimal.ZERO);
			regra.setVCredPres(BigDecimal.ZERO);
			return;
		}

		// Anexa a regra oficial de crédito no próprio ParamReforma2026 (campo @Transient)
		regra.setRegraCredPres(cfg);

		// Alíquota do crédito presumido (%)
		regra.setPCredPres(cfg.getPAliq() != null ? cfg.getPAliq() : BigDecimal.ZERO);

		// Valor orientativo:
		//  - se vBC_CredPres vier preenchido, calcula vCredPres = vBC * pAliq / 100
		//  - senão, usa vCredPres da própria tabela, se existir
		BigDecimal p = cfg.getPAliq() != null ? cfg.getPAliq() : BigDecimal.ZERO;
		BigDecimal baseOrientativa = cfg.getVBcCredPres();
		BigDecimal valorOrientativo = BigDecimal.ZERO;

		if (baseOrientativa != null && p.compareTo(BigDecimal.ZERO) > 0) {
			valorOrientativo = baseOrientativa
					.multiply(p)
					.divide(new BigDecimal("100"));
		} else if (cfg.getVCredPres() != null) {
			valorOrientativo = cfg.getVCredPres();
		}

		regra.setVCredPres(valorOrientativo);
		// Se quiser, você ainda pode expor o texto de impedimento:
		// regra.setMotivoImpedimentoCredPres(cfg.getImpedimentoCredPres());
	}


	@Transactional
	public ParamReforma2026 aliquotaItemNFceRef(NfceItem it, String ufOrig, String ufDest, String cnaeEmit,
			String tipoCliente, Boolean consumidorFinal, String ibgeMunDest, LocalDate dataOp,Long idEmpresa, Long idFilial) {
		
		
		List<ParamReforma2026> regras = repo.listaCriteriaPorFilial(idEmpresa, idFilial, false, false);
		ParamReforma2026 r = escolherRegra(regras, it.getProduto().getNcm().getNcm(), it.getCfopItem().getCfop(), safe(it,"getCest"), safe(it,"getExTipi"),
				safe(it,"getCst"), safe(it,"getCsosn"), ufOrig, ufDest, ibgeMunDest, cnaeEmit, tipoCliente, consumidorFinal, safe(it,"getSku"), dataOp);
		// 3) Caso exista regra encontrada
		if (r != null) {
			BigDecimal cbs = ns(r.getPCbs(), "0.90");
			BigDecimal ibs = ns(r.getPIbs(), "0.10");
			BigDecimal is  = ns(r.getPIs(), "0");

			if (suprimirISSeIsento(it.getProduto().getNcm().getNcm(), it, idEmpresa)) {
				is = BigDecimal.ZERO;
			}

			// Usa a própria instância da regra como "resultado",
			// apenas ajustando as alíquotas finais calculadas
			r.setPCbs(cbs);
			r.setPIbs(ibs);
			r.setPIs(is);

			aplicarCreditoPresumido(r,dataOp,idEmpresa,idFilial);

			return r;
		}

		// 4) Sem regra - retorna objeto com valores padrão
		ParamReforma2026 paramDefault = new ParamReforma2026();
		paramDefault.setPCbs(new BigDecimal("0.90"));
		paramDefault.setPIbs(new BigDecimal("0.10"));
		paramDefault.setPIs(BigDecimal.ZERO);

		return paramDefault;
	}

	/**
	 * Regra: não calcular IS quando NCM/Item é isento.
	 * Usa flag na NCM (excluirSeIsento) + possíveis getters do item (isIsento/getIsento) + CST isento.
	 */
	private boolean suprimirISSeIsento(String ncmCodigo, Object item,Long idEmpresa) {
		try { Ncm n = ncmRepo != null ? ncmRepo.localizaNCM(ncmCodigo,idEmpresa) : null;
		if (n != null && Boolean.TRUE.equals(n.isExcluirSeIsento())) return true; } catch (Exception ignore) {}
		try { Object v = item.getClass().getMethod("isIsento").invoke(item);
		if (v instanceof Boolean && ((Boolean)v)) return true; } catch (Exception ignore) {}
		try { Object v = item.getClass().getMethod("getIsento").invoke(item);
		if (v instanceof Boolean && ((Boolean)v)) return true; } catch (Exception ignore) {}
		String cst = safe(item,"getCst");
		if (cst != null && (cst.equals("40") || cst.equals("41") || cst.equals("50"))) return true;
		return false;
	}

	private ParamReforma2026 escolherRegra(List<ParamReforma2026> regras, String ncm, String cfop, String cest, String exTipi,
			String cst, String csosn, String ufOrig, String ufDest, String ibgeMunDest,
			String cnaeEmit, String tipoCliente, Boolean consumidorFinal, String sku,
			LocalDate dataOp) {
		 
		ParamReforma2026 best = null;
		    int bestScore = -1;

		    for (ParamReforma2026 r : regras) {
		        if (!boolTrue(r.getAtivo())) continue;
		        if (!matchVigencia(r.getVigenciaIni(), r.getVigenciaFim(), dataOp)) continue;

		        if (!matchPrefix(r.getNcmPrefix(), ncm)) continue;
		        if (!matchRange(r.getNcmIni(), r.getNcmFim(), ncm)) continue;
		        if (!matchList(r.getNcmList(), ncm)) continue;

		        if (!matchCfop(r, cfop)) continue;

		        if (!matchExact(r.getCest(), cest)) continue;
		        if (!matchExact(r.getExTipi(), exTipi)) continue;
		        if (!matchExact(r.getCst(), cst)) continue;
		        if (!matchExact(r.getCsosn(), csosn)) continue;

		        if (!matchExact(r.getUfOrig(), ufOrig)) continue;
		        if (!matchExact(r.getUfDest(), ufDest)) continue;
		        if (!matchExact(r.getIbgeMunDest(), ibgeMunDest)) continue;
		        if (!matchExact(r.getCnae(), cnaeEmit)) continue;

		        String tipoClienteRegra = (r.getTipoCliente() == null ? null : r.getTipoCliente().toString());
		        if (!matchExact(tipoClienteRegra, tipoCliente)) continue;

		        if (!matchBool(r.getConsumidorFinal(), consumidorFinal)) continue;
		        if (!matchSku(r, sku)) continue;

		        int score = scoreEspecificidade(r);

		        // Se sua lista já vem ordenada por prioridade desc/id desc, isso aqui
		        // garante que, entre as que casam, ganha a mais específica.
		        if (score > bestScore) {
		            best = r;
		            bestScore = score;
		        }
		    }
		    return best;
	}
	/* =================== Helpers de matching =================== */
	private boolean boolTrue(Boolean b) { return b != null && b; }
	private boolean matchExact(String rule, String value) { if (empty(rule)) return true; return Objects.equals(rule, value); }
	private boolean matchBool(Boolean rule, Boolean value) { if (rule == null) return true; return Objects.equals(rule, value); }
	private boolean matchPrefix(String prefix, String value) { if (empty(prefix)) return true; if (empty(value)) return false; return value.startsWith(prefix); }

	private boolean matchRange(String ini, String fim, String value) {
		if (empty(ini) && empty(fim)) return true; if (empty(value)) return false;
		String v = digits(value), i = empty(ini) ? null : digits(ini), f = empty(fim) ? null : digits(fim);
		if (i != null && v.compareTo(pad(i)) < 0) return false;
		if (f != null && v.compareTo(pad(f)) > 0) return false;
		return true;
	}
	private boolean matchList(String csv, String value) {
		if (empty(csv)) return true; if (empty(value)) return false;
		Set<String> set = Arrays.stream(csv.split(",")).map(String::trim).filter(s->!s.isEmpty()).collect(Collectors.toSet());
		return set.contains(value);
	}
	private boolean matchRegex(String pattern, String value) { if (empty(pattern)) return true; if (empty(value)) return false; return Pattern.compile(pattern).matcher(value).matches(); }
	private boolean matchCfop(ParamReforma2026 r, String cfop) {
		if (!empty(r.getCfop())) {
			String p = r.getCfop();
			if (p.startsWith("^")) { if (!matchRegex(p, cfop)) return false; }
			else { if (!matchExact(p, cfop)) return false; }
		}
		if (!matchList(r.getCfopList(), cfop)) return false;
		if (!matchRegex(r.getCfopRegex(), cfop)) return false;
		return true;
	}
	private boolean matchVigencia(LocalDate ini, LocalDate fim, LocalDate op) {
		if (op == null) return true; if (ini != null && op.isBefore(ini)) return false; if (fim != null && op.isAfter(fim)) return false; return true;
	}
	private boolean matchSku(ParamReforma2026 r, String sku) {
		if (!empty(r.getSkuIncluirList())) { Set<String> inc = toSet(r.getSkuIncluirList()); if (!inc.contains(nvl(sku))) return false; }
		if (!empty(r.getSkuExcluirList())) { Set<String> exc = toSet(r.getSkuExcluirList()); if (exc.contains(nvl(sku))) return false; }
		return true;
	}
	private Set<String> toSet(String csv) { return Arrays.stream(csv.split(",")).map(String::trim).filter(s->!s.isEmpty()).collect(Collectors.toSet()); }

	private boolean empty(String s) { return s == null || s.isEmpty(); }
	private String nvl(String s) { return s == null ? "" : s; }
	private String digits(String s) {return (s == null) ? "" : s.replaceAll("\\\\D+", "");}
	private String pad(String s) { return String.format("%08d", Integer.parseInt(s)); }
	private BigDecimal ns(BigDecimal v, String d) { return v != null ? v : new BigDecimal(d); }
	private String safe(Object obj, String getter) { try { return Objects.toString(obj.getClass().getMethod(getter).invoke(obj), null); } catch (Exception e){ return null; } }

	private BigDecimal nz(BigDecimal v) {
		return v != null ? v : BigDecimal.ZERO;
	}

	/**
	 * Base contábil da operação para cálculo do crédito presumido.
	 * vTotalBruto + frete + seguro + despesas - desconto
	 */
	private BigDecimal calcularBaseOperacao(ItemNfe it) {
		BigDecimal base = BigDecimal.ZERO;

		base = base.add(nz(it.getValorTotalBruto()));
		base = base.add(nz(it.getValorFrete()));
		base = base.add(nz(it.getValorSeguro()));
		base = base.add(nz(it.getValorDespesas()));
		base = base.subtract(nz(it.getDesconto()));

		if (base.compareTo(BigDecimal.ZERO) < 0) {
			base = BigDecimal.ZERO;
		}

		return base;
	}
	
	private int scoreEspecificidade(ParamReforma2026 r) {
	    int s = 0;

	    if (!empty(r.getNcmPrefix())) s += 3;
	    if (!empty(r.getNcmIni()) || !empty(r.getNcmFim())) s += 2;
	    if (!empty(r.getNcmList())) s += 2;

	    if (!empty(r.getCfop())) s += 4;         // CFOP exato (bem específico)
	    if (!empty(r.getCfopList())) s += 3;     // lista
	    if (!empty(r.getCfopRegex())) s += 2;    // regex

	    if (!empty(r.getCest())) s += 1;
	    if (!empty(r.getExTipi())) s += 1;
	    if (!empty(r.getCst())) s += 1;
	    if (!empty(r.getCsosn())) s += 1;

	    if (!empty(r.getUfOrig())) s += 1;
	    if (!empty(r.getUfDest())) s += 1;
	    if (!empty(r.getIbgeMunDest())) s += 1;
	    if (!empty(r.getCnae())) s += 1;

	    if (r.getTipoCliente() != null) s += 1;
	    if (r.getConsumidorFinal() != null) s += 1;

	    if (!empty(r.getSkuIncluirList()) || !empty(r.getSkuExcluirList())) s += 1;

	    return s;
	}

	private enum TipoOperacaoCredito {
		COMPRA_REVENDA,
		COMPRA_ATIVO_IMOBILIZADO,
		COMPRA_USO_CONSUMO,
		OUTRA_ENTRADA,
		SAIDA,
		SEM_CREDITO
	}

	private TipoOperacaoCredito tipoOperacaoCredito(ItemNfe it) {
		CFOP cfopEnt = it.getCfopItem();
		String cfop = (cfopEnt != null && cfopEnt.getCfop() != null)
				? cfopEnt.getCfop().trim()
						: null;

		if (cfop == null || cfop.isEmpty()) {
			return TipoOperacaoCredito.SEM_CREDITO;
		}

		char primeira = cfop.charAt(0);
		boolean entrada = (primeira == '1' || primeira == '2' || primeira == '3');

		if (!entrada) {
			// crédito normalmente só na entrada
			return TipoOperacaoCredito.SAIDA;
		}

		// *** AJUSTAR AQUI conforme sua tabela de CFOP x tipo ***
		// Exemplos genéricos (ICMS-style):
		if (cfop.endsWith("02") || cfop.endsWith("03")) {
			return TipoOperacaoCredito.COMPRA_REVENDA;
		}

		if (cfop.startsWith("155") || cfop.startsWith("255") || cfop.startsWith("355")) {
			return TipoOperacaoCredito.COMPRA_ATIVO_IMOBILIZADO;
		}

		if (cfop.startsWith("191") || cfop.startsWith("291") || cfop.startsWith("391")) {
			return TipoOperacaoCredito.COMPRA_USO_CONSUMO;
		}

		return TipoOperacaoCredito.OUTRA_ENTRADA;
	}

	private boolean permiteCreditoPorCliente(String tipoCliente, Boolean consumidorFinal) {
		if (Boolean.TRUE.equals(consumidorFinal)) {
			// consumidor final → sem crédito de entrada
			return false;
		}

		if (tipoCliente == null) {
			return true; // assume contribuinte
		}

		String t = tipoCliente.trim().toUpperCase(java.util.Locale.ROOT);

		// Ajuste conforme os valores reais do teu sistema
		if (t.contains("NAO CONTRIB") || t.contains("NÃO CONTRIB") || t.contains("EXTERIOR")) {
			return false;
		}

		return true;
	}

	private boolean isOperacaoZFM(String ufDest, String ibgeMunDest) {
		if (ufDest == null || ibgeMunDest == null) {
			return false;
		}
		// Exemplo mínimo: Manaus. Se quiser mais municípios ZFM, expande aqui.
		return "AM".equalsIgnoreCase(ufDest) && "1302603".equals(ibgeMunDest);
	}

	private boolean permiteCreditoPorClassTrib(ItemNfe item,
			String ufDest,
			String ibgeMunDest) {

		CClassTrib cclass = (item != null) ? item.getCclassTrib() : null;
		if (cclass == null) {
			// sem classificação → por segurança, não gera crédito
			return false;
		}

		// flag da planilha: ind_gCredPresOper
		if (Boolean.FALSE.equals(cclass.getIndgCredPresOper())) {
			return false;
		}

		boolean zfm = isOperacaoZFM(ufDest, ibgeMunDest);
		if (zfm) {
			Boolean indZfm = null;
			try {
				indZfm = cclass.getIndgCredPresIBSZFM(); // se você criou esse getter
			} catch (NoSuchMethodError e) {
				// se ainda não criou o campo, simplesmente ignora esse teste
			}
			if (Boolean.FALSE.equals(indZfm)) {
				return false;
			}
		}

		return true;
	}

	private BigDecimal calcularBaseCreditoPorTipo(TipoOperacaoCredito tipo, ItemNfe it) {
		BigDecimal baseOperacao = calcularBaseOperacao(it);

		switch (tipo) {
			case COMPRA_REVENDA:
				// Crédito integral sobre a base da mercadoria
				return baseOperacao;

			case COMPRA_ATIVO_IMOBILIZADO:
				// Aqui dá pra tratar rateio em 48 meses futuramente
				return baseOperacao;

			case COMPRA_USO_CONSUMO:
				// Se chegou aqui é porque cliente + CClassTrib permitiram
				return baseOperacao;

			case OUTRA_ENTRADA:
				// Por padrão, sem crédito (ajuste se tiver alguma entrada específica)
				return BigDecimal.ZERO;

			case SAIDA:
			case SEM_CREDITO:
			default:
				return BigDecimal.ZERO;
		}
	}

	public BigDecimal calcularCreditoPresumidoItemNfe(ItemNfe item,
			ParamReforma2026 regra,
			String ufDest,
			String ibgeMunDest,
			String tipoCliente,
			Boolean consumidorFinal) {

		if (item == null || regra == null) {
			return BigDecimal.ZERO;
		}

		CCredPres cfg = regra.getRegraCredPres();
		if (cfg == null) {
			return BigDecimal.ZERO;
		}

		BigDecimal pCred = regra.getPCredPres();
		if (pCred == null || pCred.compareTo(BigDecimal.ZERO) <= 0) {
			return BigDecimal.ZERO;
		}

		// 1) Cliente / consumidor final
		if (!permiteCreditoPorCliente(tipoCliente, consumidorFinal)) {
			return BigDecimal.ZERO;
		}

		// 2) Flags da CClassTrib (ind_gCredPresOper / ind_gCredPresIBSZFM)
		if (!permiteCreditoPorClassTrib(item, ufDest, ibgeMunDest)) {
			return BigDecimal.ZERO;
		}

		// 3) CFOP → tipo de operação
		TipoOperacaoCredito tipoOp = tipoOperacaoCredito(item);
		if (tipoOp == TipoOperacaoCredito.SAIDA || tipoOp == TipoOperacaoCredito.SEM_CREDITO) {
			return BigDecimal.ZERO;
		}

		// 4) Base
		BigDecimal base = calcularBaseCreditoPorTipo(tipoOp, item);
		if (base == null || base.compareTo(BigDecimal.ZERO) <= 0) {
			return BigDecimal.ZERO;
		}

		// 5) Crédito = base * pCred / 100
		BigDecimal cem = new BigDecimal("100");
		return base
				.multiply(pCred)
				.divide(cem, 2, RoundingMode.HALF_UP);
	}
	
		
	public BigDecimal calcularBaseOperacaoSemImpostos(ItemNfe it) {
			BigDecimal base = BigDecimal.ZERO;

			base = base.add(nz(it.getValorTotalBruto()));
			base = base.add(nz(it.getValorFrete()));
			base = base.add(nz(it.getValorSeguro()));
			base = base.add(nz(it.getValorDespesas()));
			base = base.subtract(nz(it.getDesconto()));
			base = base.subtract(nz(it.getValorIcms()));
			base = base.subtract(nz(it.getValorIcmsSt()));
			base = base.subtract(nz(it.getValorIPI()));
			base = base.subtract(nz(it.getValorPis()));
			base = base.subtract(nz(it.getValorCofins()));
			

			if (base.compareTo(BigDecimal.ZERO) < 0) {
				base = BigDecimal.ZERO;
			}

			return base;
		}

}
