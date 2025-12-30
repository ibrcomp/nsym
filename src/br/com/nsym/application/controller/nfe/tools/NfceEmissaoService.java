package br.com.nsym.application.controller.nfe.tools;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import br.com.nsym.application.channels.AcbrComunica;
import br.com.nsym.application.channels.DadosDeConexaoSocket;
import br.com.nsym.domain.model.entity.financeiro.RecebimentoParcial;
import br.com.nsym.domain.model.entity.fiscal.Cfe.CFe;
import br.com.nsym.domain.model.entity.fiscal.Cfe.Nfce;
import br.com.nsym.domain.model.entity.fiscal.tools.StatusNfe;
import br.com.nsym.domain.model.repository.fiscal.nfce.NfceRepository;

/**
 * Orquestra a emissão real da NFC-e via ACBr (gera INI/XML + envia + valida retorno + imprime DANFCE).
 *
 * IMPORTANTE:
 * - Eu deixei as chamadas ao AcbrComunica por *reflexão* para ficar compatível com o seu "padrão" novo,
 *   mesmo que o nome do método/assinatura varie.
 * - Você só precisa garantir que exista no AcbrComunica um método para:
 *   1) gerar ini/xml da NFC-e
 *   2) enviar para SEFAZ
 *   3) imprimir DANFCE (opcional)
 */
@RequestScoped
public class NfceEmissaoService {

	@Inject
	private AcbrComunica acbr;

	@Inject
	private NfceService nfceService;

	@Inject
	private NfceRepository nfceRepository;
	
	@Transactional
	public Nfce criaSalvaNfce(CFe cfe, Long idEmpresa, Long idFilial) {
		CupomFiscalCaixa cupom = CupomFiscalFactory.fromCfe(cfe);
		return nfceService.emitirCupomAvulso(cupom,idEmpresa,idFilial);
	}

	@Transactional
	public NfceEmissaoResultado emitir(DadosDeConexaoSocket conexao, String nomeArquivo, CFe cfe, Long idEmpresa, Long idFilial, boolean imprimir,boolean caixa) {
		// Compatibilidade: constrói o DTO neutro e emite a partir dele
		CupomFiscalCaixa cupom = CupomFiscalFactory.fromCfe(cfe);
		return emitirCupom(conexao, nomeArquivo, cupom, idEmpresa, idFilial, imprimir,caixa);
	}

	@Transactional
	public NfceEmissaoResultado emitirCupom(DadosDeConexaoSocket conexao, String nomeArquivo, CupomFiscalCaixa cupom, Long idEmpresa, Long idFilial, boolean imprimir,boolean caixa) {
		Nfce nfce = new Nfce();
		List<RecebimentoParcial> lista = null;
		if (caixa) {
			 nfce = nfceService.emitirCupom(cupom, idEmpresa, idFilial);
			 lista = nfceRepository.pegaListaRecebimentoParcial(nfce.getId(), idEmpresa, idFilial);
		}else {
			nfce = nfceService.emitirCupomAvulso(cupom,idEmpresa,idFilial);
			lista = nfceRepository.pegaListaRecebimentoParcial(nfce.getId(), idEmpresa, idFilial);
		}
		
		try {
			// 1) cria ini/xml NFC-e
				Object iniObj = invokeBest(acbr,
                        Arrays.asList(
                                "criarArqIniNfce"),
						conexao, nomeArquivo, cupom, nfce,lista);

                // guarda o INI gerado quando o seu padrão retornar texto/caminho
                if (iniObj instanceof String) {
                    trySet(nfce, "setIniEnviado", String.class, (String) iniObj);
                }
// 2) envia (autorização)
			String retorno = invokeBestString(acbr,
					Arrays.asList(
							"nfceCriarEnviarNfce",
							"nfeCriarEnviarNfce",
							"nfeCriarEnviarNFe",
							"nfeCriarEnviar"),
					conexao, nomeArquivo);

			// guarda retorno bruto do ACBr (para auditoria)
			trySet(nfce, "setRetornoAcbr", String.class, retorno);

			NfceRespostaAcbr resp = NfceRespostaAcbr.parse(retorno);

			// 3) atualiza NFC-e no banco (tolerante a campos inexistentes)
			aplicarRetornoEmNfce(nfce, resp);

			nfceRepository.save(nfce);

			if (!resp.isValido()) {
				return NfceEmissaoResultado.erro(nfce, resp);
			}

			// 4) impressão / PDF (se existir no seu padrão)
			if (imprimir) {
				String alvoImpressao = !isBlank(resp.getCaminhoXml()) ? resp.getCaminhoXml()
						: (!isBlank(resp.getChaveAcesso()) ? resp.getChaveAcesso() : nomeArquivo);

				// PDF
				invokeBestVoidQuiet(acbr,
						Arrays.asList(
								"geraPDFDanfceVenda",
								"geraPDFDanfeNfceVenda",
								"geraPDFDanfeNfce",
								"geraPDFDanfe"),
						conexao, alvoImpressao, (resp.getChaveAcesso() == null ? "nfce" : resp.getChaveAcesso()) + ".pdf");

				// Impressão
				invokeBestVoidQuiet(acbr,
						Arrays.asList(
								"nfceImprimiDanfceVenda",
								"nfeImprimirDanfce",
								"nfeImprimirDanfe",
								"imprimirDanfeNfce"),
						conexao, alvoImpressao);
			}

			return NfceEmissaoResultado.ok(nfce, resp);

		} catch (Exception e) {
			NfceRespostaAcbr resp = new NfceRespostaAcbr();
			resp.setValido(false);
			resp.setMotivo("Falha ao emitir NFC-e via ACBr: " + e.getMessage());
			aplicarRetornoEmNfce(nfce, resp);
			nfceRepository.save(nfce);
			return NfceEmissaoResultado.erro(nfce, resp);
		}
	}
	
	@Transactional
	public NfceEmissaoResultado emitirCupomSalvo(DadosDeConexaoSocket conexao, String nomeArquivo, CupomFiscalCaixa cupom,Nfce nfce,List<RecebimentoParcial> lista, Long idEmpresa, Long idFilial, boolean imprimir) throws Exception {
//		try {
			// 1) cria ini/xml NFC-e
				Object iniObj = invokeBest(acbr,
                        Arrays.asList(
                                "criarArqIniNfce"),
						conexao, nomeArquivo, cupom, nfce,lista);

                // guarda o INI gerado quando o seu padrão retornar texto/caminho
                if (iniObj instanceof String) {
                    trySet(nfce, "setIniEnviado", String.class, (String) iniObj);
                }
// 2) envia (autorização)
			String retorno = invokeBestString(acbr,
					Arrays.asList(
							"nfceCriarEnviarNfce",
							"nfeCriarEnviarNfce",
							"nfeCriarEnviarNFe",
							"nfeCriarEnviar"),
					conexao, nomeArquivo);

			// guarda retorno bruto do ACBr (para auditoria)
			trySet(nfce, "setRetornoAcbr", String.class, retorno);

			NfceRespostaAcbr resp = NfceRespostaAcbr.parse(retorno);

			// 3) atualiza NFC-e no banco (tolerante a campos inexistentes)
			aplicarRetornoEmNfce(nfce, resp);

			nfceRepository.save(nfce);

			if (!resp.isValido()) {
				return NfceEmissaoResultado.erro(nfce, resp);
			}

			// 4) impressão / PDF (se existir no seu padrão)
			if (imprimir) {
				String alvoImpressao = !isBlank(resp.getCaminhoXml()) ? resp.getCaminhoXml()
						: (!isBlank(resp.getChaveAcesso()) ? resp.getChaveAcesso() : nomeArquivo);

				// PDF
				invokeBestVoidQuiet(acbr,
						Arrays.asList(
								"geraPDFDanfceVenda",
								"geraPDFDanfeNfceVenda",
								"geraPDFDanfeNfce",
								"geraPDFDanfe"),
						conexao, alvoImpressao, (resp.getChaveAcesso() == null ? "nfce" : resp.getChaveAcesso()) + ".pdf");

				// Impressão
				invokeBestVoidQuiet(acbr,
						Arrays.asList(
								"nfceImprimiDanfceVenda",
								"nfeImprimirDanfce",
								"nfeImprimirDanfe",
								"imprimirDanfeNfce"),
						conexao, alvoImpressao);
			}

			return NfceEmissaoResultado.ok(nfce, resp);

//		} catch (Exception e) {
//			NfceRespostaAcbr resp = new NfceRespostaAcbr();
//			resp.setValido(false);
//			resp.setMotivo("Falha ao emitir NFC-e via ACBr: " + e.getMessage());
//			aplicarRetornoEmNfce(nfce, resp);
//			nfceRepository.save(nfce);
//			return NfceEmissaoResultado.erro(nfce, resp);
//		}
	}

	private void aplicarRetornoEmNfce(Nfce nfce, NfceRespostaAcbr resp) {
		// Campos mais comuns
		trySet(nfce, "setChaveAcesso", String.class, resp.getChaveAcesso());
		trySet(nfce, "setChave", String.class, resp.getChaveAcesso());
		trySet(nfce, "setProtocoloAutorizacao", String.class, resp.getProtocolo());
		trySet(nfce, "setProtocolo", String.class, resp.getProtocolo());
		trySet(nfce, "setCaminhoXml", String.class, resp.getCaminhoXml());
		trySet(nfce, "setCaminho", String.class, resp.getCaminhoXml());
		trySet(nfce, "setMotivoRetorno", String.class, resp.getMotivo());
		trySet(nfce, "setMotivo", String.class, resp.getMotivo());
		trySet(nfce, "setEmitido", boolean.class, resp.isValido());

		// cStat/código de retorno (quando existir)
		Integer cStat = parseIntOrNull(resp.getCodigoRetorno());
		if (cStat != null) {
			trySet(nfce, "setCStat", Integer.class, cStat);
		}
		if (resp.isValido()) {
			trySet(nfce, "setStatusEmissao", StatusNfe.class, StatusNfe.EN);
		}else {
			trySet(nfce,"setStatusEmissao", StatusNfe.class, StatusNfe.EE);
		}

		// número/serie (se você quiser persistir também no retorno)
		if (resp.getNumero() != null) {
			trySet(nfce, "setNumero", Integer.class, resp.getNumero());
			trySet(nfce, "setNumeroNota", Integer.class, resp.getNumero());
		}
		if (resp.getSerie() != null) {
			trySet(nfce, "setSerie", Integer.class, resp.getSerie());
		}
	}

	private void trySet(Object target, String methodName, Class<?> paramType, Object value) {
		try {
			Method m = target.getClass().getMethod(methodName, paramType);
			m.invoke(target, value);
		} catch (Exception ignored) {
			// campo/assinatura não existe -> ignora para não quebrar
		}
	}

	private boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}

	private void invokeBestVoid(Object target, List<String> methodNames, Object... args) throws Exception {
		Object res = invokeBest(target, methodNames, args);
		if (res == NoSuchMethod.INSTANCE) {
			throw new NoSuchMethodException("Nenhum método encontrado em " + target.getClass().getSimpleName()
					+ " para nomes " + methodNames + " com args=" + Arrays.toString(args));
		}
	}

	private void invokeBestVoidQuiet(Object target, List<String> methodNames, Object... args) {
		try {
			invokeBest(target, methodNames, args);
		} catch (Exception ignored) {
			// impressão é opcional; não deve quebrar emissão
		}
	}

	private String invokeBestString(Object target, List<String> methodNames, Object... args) throws Exception {
		Object res = invokeBest(target, methodNames, args);
		if (res == NoSuchMethod.INSTANCE) {
			throw new NoSuchMethodException("Nenhum método encontrado em " + target.getClass().getSimpleName()
					+ " para nomes " + methodNames + " com args=" + Arrays.toString(args));
		}
		return res == null ? "" : String.valueOf(res);
	}

	private Object invokeBest(Object target, List<String> methodNames, Object... args) throws Exception {
		for (String name : methodNames) {
			// 1) tenta assinatura exata (mesmo número de args)
			Method m = findMethod(target.getClass(), name, args);
			if (m != null) {
				return m.invoke(target, args);
			}
		}

		// 2) tenta variações de args para o gerador de INI (alguns padrões não recebem nfce/cfe juntos)
		if (args != null && args.length == 4) {
			// args: conexao, nomeArquivo, cfe, nfce
			Object[] a1 = new Object[] { args[0], args[1], args[2] }; // (con, nome, cfe)
			Object[] a2 = new Object[] { args[0], args[1], args[3] }; // (con, nome, nfce)
			Object[] a3 = new Object[] { args[0], args[1] }; // (con, nome)
			for (String name : methodNames) {
				for (Object[] tryArgs : Arrays.asList(a1, a2, a3)) {
					Method m = findMethod(target.getClass(), name, tryArgs);
					if (m != null) {
						return m.invoke(target, tryArgs);
					}
				}
			}
		}

		return NoSuchMethod.INSTANCE;
	}

	private Method findMethod(Class<?> clazz, String name, Object... args) {
		Method[] methods = clazz.getMethods();
		for (Method m : methods) {
			if (!m.getName().equals(name)) continue;
			Class<?>[] p = m.getParameterTypes();
			if (p.length != (args == null ? 0 : args.length)) continue;
			boolean ok = true;
			for (int i = 0; i < p.length; i++) {
				Object a = args[i];
				if (a == null) continue;
				if (p[i].isPrimitive()) {
					Class<?> boxed = box(p[i]);
					if (!boxed.isInstance(a)) { ok = false; break; }
				} else if (!p[i].isInstance(a)) {
					ok = false; break;
				}
			}
			if (ok) return m;
		}
		return null;
	}

	private Class<?> box(Class<?> primitive) {
		if (primitive == boolean.class) return Boolean.class;
		if (primitive == int.class) return Integer.class;
		if (primitive == long.class) return Long.class;
		if (primitive == double.class) return Double.class;
		if (primitive == float.class) return Float.class;
		if (primitive == short.class) return Short.class;
		if (primitive == byte.class) return Byte.class;
		if (primitive == char.class) return Character.class;
		return primitive;
	}

	private Integer parseIntOrNull(String s) {
		if (s == null) return null;
		String digits = s.replaceAll("[^0-9]", "");
		if (digits.isEmpty()) return null;
		try {
			return Integer.parseInt(digits);
		} catch (Exception e) {
			return null;
		}
	}


	private enum NoSuchMethod { INSTANCE }
}

