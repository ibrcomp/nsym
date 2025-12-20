
package br.com.nsym.application.controller.nfe.tools;

import java.math.BigDecimal;

import br.com.nsym.domain.model.entity.fiscal.ParamReforma2026;
import br.com.nsym.domain.model.entity.fiscal.nfe.ItemNfe;

/**
 * Delegate auxiliar para aplicar as regras da Reforma Tributária
 * (IBS / CBS / IS) em um ItemNfe, a partir de uma ParamReforma2026 já selecionada.
 *
 * A ideia é ser chamado de dentro do seu CalculaTributos atual, sem acoplar
 * diretamente toda a lógica nova nele.
 */
public final class CalculaTributosReformaDelegateREMOVER {

    private CalculaTributosReformaDelegateREMOVER() {}

    public static void aplicarReformaNoItem(ParamReforma2026 regra, ItemNfe item) {

        if (regra == null || item == null) {
            return;
        }

        BigDecimal base = item.getValorTotal() != null
                ? item.getValorTotal()
                : item.getValorTotal();

        if (base == null) {
            return;
        }

        // ========== IBS ==========
        if (regra.getPIbs() != null && regra.getPIbs().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal pIbs = regra.getPIbs();
            BigDecimal vIbs = base.multiply(pIbs).divide(new BigDecimal("100"));
            item.setVbcIbs(base);
            item.setPIbs(pIbs);
            item.setVIbs(vIbs);

            if (regra.getPDifIbs() != null && regra.getPDifIbs().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal vDif = vIbs.multiply(regra.getPDifIbs())
                                       .divide(new BigDecimal("100"));
                item.setVDifIbs(vDif);
            }

            item.setCstIbs(regra.getCstIbs());
        }

        // ========== CBS ==========
        if (regra.getPCbs() != null && regra.getPCbs().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal pCbs = regra.getPCbs();
            BigDecimal vCbs = base.multiply(pCbs).divide(new BigDecimal("100"));
            item.setVbcCbs(base);
            item.setPCbs(pCbs);
            item.setVCbs(vCbs);

            if (regra.getPDifCbs() != null && regra.getPDifCbs().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal vDif = vCbs.multiply(regra.getPDifCbs())
                                       .divide(new BigDecimal("100"));
                item.setVDifCbs(vDif);
            }

            item.setCstCbs(regra.getCstCbs());
        }

        // ========== IS ==========
        if (regra.getPIs() != null && regra.getPIs().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal pIs = regra.getPIs();
            BigDecimal vIs = base.multiply(pIs).divide(new BigDecimal("100"));
            item.setVbcIs(base);
            item.setPIs(pIs);
            item.setVIs(vIs);

            item.setCstIs(regra.getCstIs().getCstIs());
        }

        // IBSMun poderia ser calculado aqui, conforme regras futuras (UF destino, etc.).
    }
}
