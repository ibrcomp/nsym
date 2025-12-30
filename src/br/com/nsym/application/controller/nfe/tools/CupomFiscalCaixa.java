package br.com.nsym.application.controller.nfe.tools;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.nsym.domain.model.entity.financeiro.RecebimentoParcial;
import br.com.nsym.domain.model.entity.fiscal.Cfe.DestinatarioCFe;
import br.com.nsym.domain.model.entity.fiscal.Cfe.EmitenteCFe;
import br.com.nsym.domain.model.entity.fiscal.Cfe.ItemCFe;
import br.com.nsym.domain.model.entity.fiscal.tools.StatusNfe;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO neutro de venda fiscal do caixa.
 *
 * Ideia: o seu caixa monta um "cupom" (itens, totais, pagamentos) e, a partir dele,
 * você pode gerar NFC-e (e no futuro qualquer outro documento), sem depender de CFe/SAT.
 *
 * Neste patch, ele ainda é preenchido a partir do CFe (fase de migração).
 */
@Getter
@Setter
public class CupomFiscalCaixa {

    /** Id apontando para a origem no seu sistema (pedido/agrupado/caixa). Na migração, pode ser o id do CFe. */
    private Long origemId;

    private EmitenteCFe emitente;
    private DestinatarioCFe destinatario;

    private List<ItemCFe> itens = new ArrayList<>();

    // ===== Totais (espelha os totals que o ACBr espera no INI) =====
    private BigDecimal baseIcms;
    private BigDecimal valorIcms;
    private BigDecimal baseIcmsSt;
    private BigDecimal valorIcmsSt;

    private BigDecimal valorTotalProdutos;
    private BigDecimal valorFrete;
    private BigDecimal valorSeguro;
    private BigDecimal desconto;
    private BigDecimal valorDespesas;
    private BigDecimal valorIPI;

    private BigDecimal valorTotalPis;
    private BigDecimal valorTotalCofins;

    private BigDecimal valorTotalNota;
    private BigDecimal valorTotalTributos;
    private StatusNfe statusEmissao;

    /** Recebimentos/pagamentos agregados (utilizado pelo INI da NFC-e). */
    private List<RecebimentoParcial> listaRecebimentosAgrupados = new ArrayList<>();
//    private List<ParcelasNfe> listaParcelas = new ArrayList<>();

}
