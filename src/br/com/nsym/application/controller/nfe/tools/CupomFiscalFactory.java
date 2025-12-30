package br.com.nsym.application.controller.nfe.tools;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import br.com.nsym.domain.model.entity.financeiro.FormaDePagamento;
import br.com.nsym.domain.model.entity.financeiro.RecebimentoParcial;
import br.com.nsym.domain.model.entity.financeiro.tools.ParcelasNfe;
import br.com.nsym.domain.model.entity.fiscal.Cfe.CFe;
import br.com.nsym.domain.model.entity.fiscal.Cfe.ItemCFe;
import br.com.nsym.domain.model.entity.fiscal.Cfe.Nfce;
import br.com.nsym.domain.model.entity.fiscal.Cfe.NfceItem;

/**
 * Fábrica de CupomFiscalCaixa.
 *
 * Nesta fase de migração, a origem ainda é um CFe (objeto de venda do caixa),
 * mas o objetivo é você passar a construir o CupomFiscalCaixa direto do Pedido/Agrupado.
 */
public final class CupomFiscalFactory {

    private CupomFiscalFactory() {
    }

    public static CupomFiscalCaixa fromCfe(CFe cfe) {
        if (cfe == null) {
            throw new IllegalArgumentException("CFe não informado");
        }

        CupomFiscalCaixa cupom = new CupomFiscalCaixa();

        // origemId: na migração, usa o id do CFe
        Long id = tryGetLong(cfe, "getId");
        cupom.setOrigemId(id);

        cupom.setEmitente(cfe.getEmitente());
        cupom.setDestinatario(cfe.getDestinatario());

        if (cfe.getListaItem() != null) {
            cupom.getItens().addAll(cfe.getListaItem());
        }

        // Totais
        cupom.setBaseIcms(cfe.getBaseIcms());
        cupom.setValorIcms(cfe.getValorIcms());
        cupom.setBaseIcmsSt(cfe.getBaseIcmsSubstituicao());
        cupom.setValorIcmsSt(cfe.getValorIcmsSubstituicao());

        cupom.setValorTotalProdutos(cfe.getValorTotalProdutos());
        cupom.setValorFrete(cfe.getValorFrete());
        cupom.setValorSeguro(cfe.getValorSeguro());
        cupom.setDesconto(cfe.getDesconto());
        cupom.setValorDespesas(cfe.getOutrasDespesas());
        cupom.setValorIPI(cfe.getValorTotalIpi());

        cupom.setValorTotalPis(cfe.getValorTotalPis());
        cupom.setValorTotalCofins(cfe.getValorTotalCofins());

        cupom.setValorTotalNota(cfe.getValorTotalNota());
        cupom.setValorTotalTributos(cfe.getValorTotalTributos());
        cupom.setStatusEmissao(cfe.getStatusEmissao());
//        if (cfe.getListaParcelas() != null) {
//        	cupom.getListaParcelas().addAll(cfe.getListaParcelas());
//        }
//        cupom.setStatusEmissao(cfe.getStatusEmissao());
//        if (cfe.getNumeroNota() != null) {
//        	cupom.setChave(cfe.getNumeroNota());
//        }
//        if (cfe.getCaminho() != null) {
//        	cupom.setCaminho(cfe.getCaminho());
//        }

        // Pagamentos agregados (usado para montar [PAGxxx] no INI)
        if (cfe.getListaRecebimentosAgrupados() != null) {
            cupom.getListaRecebimentosAgrupados().addAll(cfe.getListaRecebimentosAgrupados());
        }

        /*
         * Fallback: o SATBean (modo avulso) normalmente NÃO preenche listaRecebimentosAgrupados;
         * nesses casos o pagamento pode vir em CFe.formaPagamento e/ou CFe.listaParcelas.
         */
        if (cupom.getListaRecebimentosAgrupados().isEmpty()) {

            // 1) Tenta montar a partir de ParcelasNfe (pode ter múltiplas formas)
            if (cfe.getListaParcelas() != null && !cfe.getListaParcelas().isEmpty()) {
                Map<FormaDePagamento, BigDecimal> somaPorForma = new LinkedHashMap<>();
                for (ParcelasNfe p : cfe.getListaParcelas()) {
                    if (p == null || p.getFormaPag() == null) {
                        continue;
                    }
                    BigDecimal v = firstNonZero(p.getValorRecebido(), p.getValorOriginal(), p.getValorParcela(), BigDecimal.ZERO);
                    somaPorForma.put(p.getFormaPag(), somaPorForma.getOrDefault(p.getFormaPag(), BigDecimal.ZERO).add(v));
                }
                for (Map.Entry<FormaDePagamento, BigDecimal> e : somaPorForma.entrySet()) {
                    RecebimentoParcial rp = new RecebimentoParcial();
                    rp.setFormaPagamento(e.getKey());
                    if (e.getKey().getTipoPagamento() != null) {
                        rp.setTipoPagamento(e.getKey().getTipoPagamento());
                    }
                    rp.setValorRecebido(e.getValue());
                    cupom.getListaRecebimentosAgrupados().add(rp);
                }
            }

            // 2) Se ainda não tem pagamento, cria 1 pagamento "avulso" usando formaPagamento
            if (cupom.getListaRecebimentosAgrupados().isEmpty() && cfe.getFormaPagamento() != null) {
                RecebimentoParcial rp = new RecebimentoParcial();
                rp.setFormaPagamento(cfe.getFormaPagamento());
                if (cfe.getFormaPagamento().getTipoPagamento() != null) {
                    rp.setTipoPagamento(cfe.getFormaPagamento().getTipoPagamento());
                }
                rp.setValorRecebido(firstNonNull(cfe.getValorTotalNota(), cfe.getVCFe(), BigDecimal.ZERO));
                rp.setTroco(parseBigDecimal(cfe.getVTroco()));
                cupom.getListaRecebimentosAgrupados().add(rp);
            }

            // 3) Se veio por parcelas (ou apenas 1 pagamento), aplica troco (se existir)
            BigDecimal troco = parseBigDecimal(cfe.getVTroco());
            if (troco.compareTo(BigDecimal.ZERO) > 0 && !cupom.getListaRecebimentosAgrupados().isEmpty()) {
                RecebimentoParcial alvo = null;
                for (RecebimentoParcial rp : cupom.getListaRecebimentosAgrupados()) {
                    if (rp != null && rp.getTipoPagamento() != null && "01".equals(rp.getTipoPagamento().getCod())) {
                        alvo = rp;
                        break;
                    }
                }
                if (alvo == null) {
                    alvo = cupom.getListaRecebimentosAgrupados().get(0);
                }
                BigDecimal trocoAtual = (alvo.getTroco() != null ? alvo.getTroco() : BigDecimal.ZERO);
                alvo.setTroco(trocoAtual.add(troco));
            }
        }
        return cupom;
    }


    /**
     * Converte uma NFC-e já persistida (entidade {@link Nfce}) para o DTO neutro do caixa
     * ({@link CupomFiscalCaixa}), para que a tela/relatório consiga exibir tanto CFe quanto NFC-e
     * usando o mesmo componente.
     *
     * <p>
     * Observação: os pagamentos (listaRecebimentosAgrupados) normalmente não ficam gravados na NFC-e
     * no seu modelo atual. Este método monta emitente/destinatário/itens e recalcula totais com base
     * nos itens e/ou campos agregados da NFC-e (vProd/vNf).
     * </p>
     */
    
    public static CupomFiscalCaixa fromNfce(Nfce nfce) {
            if (nfce == null) {
                throw new IllegalArgumentException("NFC-e não informada");
            }
    
            CupomFiscalCaixa cupom = new CupomFiscalCaixa();
    
            // origemId: id da própria NFC-e
            cupom.setOrigemId(tryGetLong(nfce, "getId"));
    
            // Emitente/Destinatário
            cupom.setEmitente(nfce.getEmitente());
            cupom.setDestinatario(nfce.getDestinatario());
    
            // Itens (NfceItem -> ItemCFe)
            if (nfce.getItens() != null) {
                for (NfceItem it : nfce.getItens()) {
                    if (it == null) continue;
                    cupom.getItens().add(toItemCFe(it));
                }
            }
    
            // Totais: tenta usar os totais gravados na NFC-e; se não houver, soma pelos itens
            BigDecimal vProd = firstNonNull(nfce.getVProd(),
                    sumItens(cupom.getItens(), "getValorTotalBruto", "getValorTotal"),
                    BigDecimal.ZERO);
    
            BigDecimal vDesc = sumItens(cupom.getItens(), "getDesconto");
            BigDecimal vFrete = sumItens(cupom.getItens(), "getValorFrete");
            BigDecimal vSeg = sumItens(cupom.getItens(), "getValorSeguro");
            BigDecimal vOut = sumItens(cupom.getItens(), "getValorDespesas", "getValorOutro");
            BigDecimal vIPI = sumItens(cupom.getItens(), "getValorIPI");
    
            BigDecimal vNF = firstNonZero(nfce.getVNf(),
                    vProd.add(vFrete).add(vSeg).add(vOut).add(vIPI).subtract(vDesc));
    
            cupom.setValorTotalProdutos(vProd);
            cupom.setDesconto(vDesc);
            cupom.setValorFrete(vFrete);
            cupom.setValorSeguro(vSeg);
            cupom.setValorDespesas(vOut);
            cupom.setValorIPI(vIPI);
    
            // Tributos (somatórios por item)
            cupom.setBaseIcms(sumItens(cupom.getItens(), "getBaseICMS"));
            cupom.setValorIcms(sumItens(cupom.getItens(), "getValorIcms"));
            cupom.setBaseIcmsSt(sumItens(cupom.getItens(), "getBaseICMSSt"));
            cupom.setValorIcmsSt(sumItens(cupom.getItens(), "getValorIcmsSt"));
    
            cupom.setValorTotalPis(sumItens(cupom.getItens(), "getValorPis"));
            cupom.setValorTotalCofins(sumItens(cupom.getItens(), "getValorCofins"));
    
            // Total de tributos: soma o total por item 
            cupom.setValorTotalTributos(sumItens(cupom.getItens(), "getValorTotalTributoItem"));
    
            cupom.setValorTotalNota(vNF);
            cupom.setStatusEmissao(nfce.getStatusEmissao());
            return cupom;
        }
    
        private static ItemCFe toItemCFe(NfceItem src) {
            ItemCFe dst = new ItemCFe();
    
            // Copia "core" (mesmos campos utilizados para emitir via ACBr)
            copySameProperty(src, dst, "Produto");
            copySameProperty(src, dst, "Barras");
            copySameProperty(src, dst, "Tributo");
            copySameProperty(src, dst, "ItemST");
            copySameProperty(src, dst, "ObsItem");
            copySameProperty(src, dst, "Unidade");
            copySameProperty(src, dst, "ValorUnitario");
            copySameProperty(src, dst, "Quantidade");
            copySameProperty(src, dst, "ValorTotal");
            copySameProperty(src, dst, "ValorTotalBruto");
            copySameProperty(src, dst, "BaseICMS");
            copySameProperty(src, dst, "ValorIcms");
            copySameProperty(src, dst, "AliqIcms");
            copySameProperty(src, dst, "BaseICMSSt");
            copySameProperty(src, dst, "ValorIcmsSt");
            copySameProperty(src, dst, "MvaSt");
            copySameProperty(src, dst, "AliqIcmsSt");
            copySameProperty(src, dst, "ValorIPI");
            copySameProperty(src, dst, "AliqIPI");
            copySameProperty(src, dst, "AliqCofins");
            copySameProperty(src, dst, "ValorCofins");
            copySameProperty(src, dst, "AliqPis");
            copySameProperty(src, dst, "ValorPis");
            copySameProperty(src, dst, "ValorFrete");
            copySameProperty(src, dst, "ValorSeguro");
            copySameProperty(src, dst, "ValorDespesas");
            copySameProperty(src, dst, "Desconto");
            copySameProperty(src, dst, "Porcentagem");
            copySameProperty(src, dst, "PFCP");
            copySameProperty(src, dst, "VFCP");
            copySameProperty(src, dst, "ValorTotalTributoItem");
            copySameProperty(src, dst, "CfopItem");
            copySameProperty(src, dst, "Ii");
            copySameProperty(src, dst, "Ref");
    
            // Campos adicionais que você guardou na NfceItem (úteis p/ reconstrução/validação)
            copySameProperty(src, dst, "VRatDesc");
            copySameProperty(src, dst, "VRatAcr");
            copySameProperty(src, dst, "AliqIcmsSat");
            copySameProperty(src, dst, "VBasePis");
            copySameProperty(src, dst, "VBaseCofins");
            copySameProperty(src, dst, "ValorCSLL");
            copySameProperty(src, dst, "AliqCSLL");
            copySameProperty(src, dst, "ValorIRRF");
            copySameProperty(src, dst, "AliqIRRF");
            copySameProperty(src, dst, "VTrfIcms");
            copySameProperty(src, dst, "VFundoAmpara");
            copySameProperty(src, dst, "ValorOutro");
    
            // (Bônus) flags/valores da reforma 2026, se existirem no seu ItemCFe
            copySameProperty(src, dst, "IndGIBSCBS");
            copySameProperty(src, dst, "IndGIBSCBSMono");
            copySameProperty(src, dst, "IndGRed");
            copySameProperty(src, dst, "IndGDif");
            copySameProperty(src, dst, "IndGTransfCred");
            copySameProperty(src, dst, "IndGCredPresIBSZFM");
            copySameProperty(src, dst, "IndGAjusteCompet");
            copySameProperty(src, dst, "IndRedutorBC");
            copySameProperty(src, dst, "PCbs");
            copySameProperty(src, dst, "VCbs");
            copySameProperty(src, dst, "PIbs");
            copySameProperty(src, dst, "VIbs");
            copySameProperty(src, dst, "PIs");
            copySameProperty(src, dst, "VIs");
    
            return dst;
        }
    
        private static BigDecimal sumItens(java.util.List<?> itens, String... getters) {
            if (itens == null || itens.isEmpty()) {
                return BigDecimal.ZERO;
            }
            BigDecimal total = BigDecimal.ZERO;
            for (Object it : itens) {
                if (it == null) continue;
    
                Object val = null;
                for (String g : getters) {
                    val = tryInvoke(it, g);
                    if (val != null) break;
                }
                BigDecimal bd = parseBigDecimal(val);
                if (bd != null) {
                    total = total.add(bd);
                }
            }
            return total;
        }
    
        private static Object tryInvoke(Object src, String methodName) {
            try {
                Method m = src.getClass().getMethod(methodName);
                return m.invoke(src);
            } catch (Exception e) {
                return null;
            }
        }
    
        private static boolean copySameProperty(Object src, Object dst, String property) {
            if (src == null || dst == null || property == null || property.trim().isEmpty()) {
                return false;
            }
    
            Object val = tryInvoke(src, "get" + property);
    
            // boolean costuma ser "isX"
            if (val == null) {
                val = tryInvoke(src, "is" + property);
            }
            if (val == null) {
                return false;
            }
    
            return invokeBestSetter(dst, "set" + property, val);
        }
    
        private static boolean invokeBestSetter(Object dst, String setterName, Object val) {
            if (dst == null || setterName == null || val == null) return false;
    
            for (Method m : dst.getClass().getMethods()) {
                if (!m.getName().equals(setterName)) continue;
                if (m.getParameterCount() != 1) continue;
    
                Class<?> pt = m.getParameterTypes()[0];
                if (pt.isPrimitive()) pt = primitiveToWrapper(pt);
    
                if (pt.isAssignableFrom(val.getClass())) {
                    try {
                        m.invoke(dst, val);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }
            }
            return false;
        }
    
        private static Class<?> primitiveToWrapper(Class<?> pt) {
            if (pt == boolean.class) return Boolean.class;
            if (pt == int.class) return Integer.class;
            if (pt == long.class) return Long.class;
            if (pt == double.class) return Double.class;
            if (pt == float.class) return Float.class;
            if (pt == short.class) return Short.class;
            if (pt == byte.class) return Byte.class;
            if (pt == char.class) return Character.class;
            return pt;
        }

private static Long tryGetLong(Object src, String getter) {
        try {
            Method m = src.getClass().getMethod(getter);
            Object v = m.invoke(src);
            if (v instanceof Long) {
                return (Long) v;
            }
            if (v instanceof Number) {
                return ((Number) v).longValue();
            }
        } catch (Exception ignored) {
        }
        return null;
    }
    private static BigDecimal parseBigDecimal(String s) {
        if (s == null || s.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            // aceita "1,23" ou "1.23"
            return new BigDecimal(s.trim().replace(",", "."));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
    
    private static BigDecimal parseBigDecimal(Object o) {
        if (o == null) return BigDecimal.ZERO;
        if (o instanceof BigDecimal) return (BigDecimal) o;
        if (o instanceof Number) return new BigDecimal(o.toString());
        return parseBigDecimal(o.toString());
    }

    @SafeVarargs
    private static <T> T firstNonNull(T... values) {
        if (values == null ) {
            return null;
        }
        for (T v : values) {
            if (v != null) {
                return v;
            }
        }
        return null;
    }
    @SafeVarargs
    private static BigDecimal firstNonZero(BigDecimal... vals) {
        if (vals == null) return BigDecimal.ZERO;
        for (BigDecimal v : vals) {
            if (v != null && v.compareTo(BigDecimal.ZERO) != 0) {
                return v;
            }
        }
        return BigDecimal.ZERO;
    }

}
