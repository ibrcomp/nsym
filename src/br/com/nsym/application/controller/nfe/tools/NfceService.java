package br.com.nsym.application.controller.nfe.tools;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.Consumer;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import br.com.nsym.domain.model.entity.financeiro.RecebimentoParcial;
import br.com.nsym.domain.model.entity.fiscal.Cfe.CFe;
import br.com.nsym.domain.model.entity.fiscal.Cfe.ItemCFe;
import br.com.nsym.domain.model.entity.fiscal.Cfe.Nfce;
import br.com.nsym.domain.model.entity.fiscal.Cfe.NfceItem;
import br.com.nsym.domain.model.repository.fiscal.nfce.NfceRepository;

@RequestScoped
public class NfceService {

    @Inject
    private NfceRepository nfceRepository;

    @Inject
    private NfceNumeroService numeroService;

    /**
     * Fluxo novo: monta a NFC-e a partir de um DTO neutro do caixa.
     *
     * A ideia é o Caixa/Pedido montar um CupomFiscalCaixa e este serviço
     * persistir a NFC-e (com itens) para emissão via ACBr.
     */
    @Transactional
    public Nfce emitirCupom(CupomFiscalCaixa cupom, Long idEmpresa, Long idFilial) {
        if (cupom == null) {
            throw new IllegalArgumentException("CupomFiscalCaixa não informado");
        }

        Nfce nfce = new Nfce();
        nfce.setEmitente(cupom.getEmitente());
        nfce.setDestinatario(cupom.getDestinatario());
        nfce.setNumero(numeroService.proximoNumero(idEmpresa, idFilial));
        nfce.setSerie(numeroService.resolveSerie(idEmpresa, idFilial));
        nfce.setDataEmissao(LocalDate.now());
        nfce.setVProd(cupom.getValorTotalProdutos());
        nfce.setVNf(cupom.getValorTotalNota());

        // Totais CBS/IBS/IS (temporário: até você ligar na nova estrutura correta)
        nfce.setTotVCbs(cupom.getValorTotalCofins());
        nfce.setTotVIbs(cupom.getValorTotalPis());
        nfce.setTotVIs(cupom.getValorIcms());

        // rastreio da origem (na migração: id do CFe)
//        if (cupom.getOrigemId() != null) {
//            nfce.setCfeIdOrigem(cupom.getOrigemId());
//        }

        if (cupom.getItens() != null) {
            for (ItemCFe itemCFe : cupom.getItens()) {
                nfce.addItem(copiaItem(itemCFe));
            }
        }
        if (cupom.getListaRecebimentosAgrupados() != null) {
        	for (RecebimentoParcial rec : cupom.getListaRecebimentosAgrupados()) {
        		nfce.addRecebimentoParcial(rec);
        	}
        	
        }

        return nfceRepository.save(nfce);
    }
    
    @Transactional
    public Nfce emitirCupomAvulso(CupomFiscalCaixa cupom, Long idEmpresa, Long idFilial) {
        if (cupom == null) {
            throw new IllegalArgumentException("CupomFiscalCaixa não informado");
        }

        Nfce nfce = new Nfce();
        nfce.setEmitente(cupom.getEmitente());
        nfce.setDestinatario(cupom.getDestinatario());
        nfce.setNumero(numeroService.proximoNumero(idEmpresa, idFilial));
        nfce.setSerie(numeroService.resolveSerie(idEmpresa, idFilial));
        nfce.setDataEmissao(LocalDate.now());
        nfce.setVProd(cupom.getValorTotalProdutos());
        nfce.setVNf(cupom.getValorTotalNota());
        nfce.setStatusEmissao(cupom.getStatusEmissao());

        // Totais CBS/IBS/IS (temporário: até você ligar na nova estrutura correta)
        nfce.setTotVCbs(cupom.getValorTotalCofins());
        nfce.setTotVIbs(cupom.getValorTotalPis());
        nfce.setTotVIs(cupom.getValorIcms());

        // rastreio da origem (na migração: id do CFe)
//        if (cupom.getOrigemId() != null) {
//            nfce.setCfeIdOrigem(cupom.getOrigemId());
//        }

        if (cupom.getItens() != null) {
            for (ItemCFe itemCFe : cupom.getItens()) {
                nfce.addItem(copiaItem(itemCFe));
            }
        }
        if (cupom.getListaRecebimentosAgrupados() != null) {
        	for (RecebimentoParcial rec : cupom.getListaRecebimentosAgrupados()) {
        		nfce.addRecebimentoParcial(rec);
        	}
        	
        }

        return nfceRepository.save(nfce);
//        return nfce;
    }

    @Transactional
    public Nfce emitir(CFe cfe, Long idEmpresa, Long idFilial) {
        // Compatibilidade: mantém assinatura antiga, mas agora passa pelo DTO neutro.
        return emitirCupom(CupomFiscalFactory.fromCfe(cfe), idEmpresa, idFilial);
    }

    private NfceItem copiaItem(ItemCFe itemCFe) {
        NfceItem item = new NfceItem();

        // Campos já utilizados na emissão / cálculos
        item.setProduto(itemCFe.getProduto());
        item.setBarras(itemCFe.getBarras());
        item.setTributo(itemCFe.getTributo());
        item.setItemST(itemCFe.isItemST());
        item.setObsItem(itemCFe.getObsItem());
        item.setUnidade(itemCFe.getUnidade());
        item.setValorUnitario(itemCFe.getValorUnitario());
        item.setQuantidade(itemCFe.getQuantidade());
        item.setValorTotal(itemCFe.getValorTotal());
        item.setValorTotalBruto(itemCFe.getValorTotalBruto());
        item.setBaseICMS(itemCFe.getBaseICMS());
        item.setValorIcms(itemCFe.getValorIcms());
        item.setAliqIcms(itemCFe.getAliqIcms());
        item.setBaseICMSSt(itemCFe.getBaseICMSSt());
        item.setValorIcmsSt(itemCFe.getValorIcmsSt());
        item.setMvaSt(itemCFe.getMvaSt());
        item.setAliqIcmsSt(itemCFe.getAliqIcmsSt());
        item.setValorIPI(itemCFe.getValorIPI());
        item.setAliqIPI(itemCFe.getAliqIPI());
        item.setAliqCofins(itemCFe.getAliqCofins());
        item.setValorCofins(itemCFe.getValorCofins());
        item.setAliqPis(itemCFe.getAliqPis());
        item.setValorPis(itemCFe.getValorPis());
        item.setValorFrete(itemCFe.getValorFrete());
        item.setValorSeguro(itemCFe.getValorSeguro());
        item.setValorDespesas(itemCFe.getValorDespesas());
        item.setDesconto(itemCFe.getDesconto());
        item.setPorcentagem(itemCFe.isPorcentagem());
        item.setPFCP(itemCFe.getPFCP());
        item.setVFCP(itemCFe.getVFCP());
        item.setValorTotalTributoItem(itemCFe.getValorTotalTributoItem());
        item.setCfopItem(itemCFe.getCfopItem());
        item.setIi(itemCFe.getIi());
        item.setRef(itemCFe.getRef());

        // ===== Campos adicionais da NfceItem (úteis para reconstrução/validação posterior) =====
        copyBD(itemCFe, "getVRatDesc", item::setVRatDesc);
        copyBD(itemCFe, "getVRatAcr", item::setVRatAcr);
        copyBD(itemCFe, "getAliqIcmsSat", item::setAliqIcmsSat);

        // Base PIS/COFINS: tenta getBasePis/getBaseCofins e, se não existir, tenta getVBasePis/getVBaseCofins
        if (!copyBD(itemCFe, "getBasePis", item::setVBasePis)) {
            copyBD(itemCFe, "getVBasePis", item::setVBasePis);
        }
        if (!copyBD(itemCFe, "getBaseCofins", item::setVBaseCofins)) {
            copyBD(itemCFe, "getVBaseCofins", item::setVBaseCofins);
        }

        // Retenções/outros (opcionais no seu modelo — copia se existir)
        copyBD(itemCFe, "getValorCSLL", item::setValorCSLL);
        copyBD(itemCFe, "getAliqCSLL", item::setAliqCSLL);
        copyBD(itemCFe, "getValorIRRF", item::setValorIRRF);
        copyBD(itemCFe, "getAliqIRRF", item::setAliqIRRF);
        copyBD(itemCFe, "getVTrfIcms", item::setVTrfIcms);
        copyBD(itemCFe, "getVFundoAmpara", item::setVFundoAmpara);
        copyBD(itemCFe, "getValorOutro", item::setValorOutro);

        // (Bônus) se existirem no ItemCFe e no NfceItem (via herança do Item), copia flags/valores da reforma 2026 sem quebrar build
        copySameProperty(itemCFe, item, "IndGIBSCBS");
        copySameProperty(itemCFe, item, "IndGIBSCBSMono");
        copySameProperty(itemCFe, item, "IndGRed");
        copySameProperty(itemCFe, item, "IndGDif");
        copySameProperty(itemCFe, item, "IndGTransfCred");
        copySameProperty(itemCFe, item, "IndGCredPresIBSZFM");
        copySameProperty(itemCFe, item, "IndGAjusteCompet");
        copySameProperty(itemCFe, item, "IndRedutorBC");
        copySameProperty(itemCFe, item, "PCbs");
        copySameProperty(itemCFe, item, "VCbs");
        copySameProperty(itemCFe, item, "PIbs");
        copySameProperty(itemCFe, item, "VIbs");
        copySameProperty(itemCFe, item, "PIs");
        copySameProperty(itemCFe, item, "VIs");

        return item;
    }

    private Long tryGetLong(Object src, String getter) {
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

    /**
     * Copia BigDecimal (ou Number/String numérica) via reflection. Retorna true se conseguiu copiar.
     */
    private boolean copyBD(Object src, String getter, Consumer<BigDecimal> setter) {
        try {
            Method m = src.getClass().getMethod(getter);
            Object v = m.invoke(src);
            if (v == null) {
                return false;
            }
            if (v instanceof BigDecimal) {
                setter.accept((BigDecimal) v);
                return true;
            }
            if (v instanceof Number) {
                setter.accept(new BigDecimal(v.toString()));
                return true;
            }
            if (v instanceof String) {
                String s = ((String) v).trim().replace(",", ".");
                if (!s.isEmpty()) {
                    setter.accept(new BigDecimal(s));
                    return true;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * Copia uma propriedade quando existe getX em src e setX em dest, sem depender do tipo em tempo de compilação.
     */
    private void copySameProperty(Object src, Object dest, String suffix) {
        try {
            Method getter = src.getClass().getMethod("get" + suffix);
            Object value = getter.invoke(src);
            if (value == null) {
                return;
            }
            Method setter = findSetter(dest.getClass(), "set" + suffix, getter.getReturnType());
            if (setter != null) {
                setter.invoke(dest, value);
            }
        } catch (Exception ignored) {
        }
    }

    private Method findSetter(Class<?> clazz, String name, Class<?> paramType) {
        try {
            return clazz.getMethod(name, paramType);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
