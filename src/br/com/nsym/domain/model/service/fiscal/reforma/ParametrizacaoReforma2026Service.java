package br.com.nsym.domain.model.service.fiscal.reforma;

import java.math.BigDecimal;
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

import br.com.nsym.domain.model.entity.fiscal.Ncm;
import br.com.nsym.domain.model.entity.fiscal.ParamReforma2026;
import br.com.nsym.domain.model.entity.fiscal.Cfe.NfceItem;
import br.com.nsym.domain.model.entity.fiscal.nfe.ItemNfe;
import br.com.nsym.domain.model.repository.fiscal.NCMRepository;
import br.com.nsym.domain.model.repository.fiscal.ParamReforma2026Repository;

@RequestScoped
public class ParametrizacaoReforma2026Service {

    @Inject ParamReforma2026Repository repo;
    @Inject NCMRepository ncmRepo;

    public static class Aliquotas {
        public BigDecimal pCbs, pIbs, pIs;
        public Aliquotas(BigDecimal cbs, BigDecimal ibs, BigDecimal is) { this.pCbs=cbs; this.pIbs=ibs; this.pIs=is; }
    }
    
    @Transactional
    public ParamReforma2026 aliquotaItemNfeRef(ItemNfe it,String ufOrig,String ufDest,String cnaeEmit,String tipoCliente,Boolean consumidorFinal,
    											String ibgeMunDest,LocalDate dataOp,Long idEmpresa,Long idFilial) {

        // 1) Busca regras aplicáveis
        List<ParamReforma2026> regras = repo.buscarPorFiltros(
                it.getProduto().getNcm().getNcm(),it.getCfopItem().getCfop(),ufOrig,ufDest,
                cnaeEmit,true,it.getInclusionAsLocalDate(),1,5,true,idEmpresa,idFilial);

        // 2) Escolhe a melhor regra
        ParamReforma2026 r = escolherRegra(
        		regras,it.getProduto().getNcm().getNcm(),it.getCfopItem().getCfop(),safe(it, "getCest"),safe(it, "getExTipi"),safe(it, "getCst"),
        		safe(it, "getCsosn"),ufOrig,ufDest,ibgeMunDest,cnaeEmit,tipoCliente,consumidorFinal,safe(it.getProduto(), "getReferencia"),dataOp);

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

            return r;
        }

        // 4) Sem regra - retorna objeto com valores padrão
        ParamReforma2026 paramDefault = new ParamReforma2026();
        paramDefault.setPCbs(new BigDecimal("0.90"));
        paramDefault.setPIbs(new BigDecimal("0.10"));
        paramDefault.setPIs(BigDecimal.ZERO);

        return paramDefault;
    }

    
    @Transactional
    public ParamReforma2026 aliquotaItemNFceRef(NfceItem it, String ufOrig, String ufDest, String cnaeEmit,
                                          String tipoCliente, Boolean consumidorFinal, String ibgeMunDest, LocalDate dataOp,Long idEmpresa, Long idFilial) {
    	
    	List<ParamReforma2026> regras = repo.buscarPorFiltros(it.getProduto().getNcm().getNcm(),it.getCfopItem().getCfop() ,ufOrig, ufDest, cnaeEmit,true, it.getInclusionAsLocalDate(),1,5,true,idEmpresa,idFilial);
//        List<ParamReforma2026> regras = repo.listarAtivosOrdenados(idEmpresa,idFilial);
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
            if (!matchExact(r.getTipoCliente().toString(), tipoCliente)) continue;
            if (!matchBool(r.getConsumidorFinal(), consumidorFinal)) continue;
            if (!matchSku(r, sku)) continue;
            return r;
        }
        return null;
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
}
