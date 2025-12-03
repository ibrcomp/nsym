package br.com.nsym.application.controller.nfe.tools;

import java.math.BigDecimal;
import java.util.List;

import br.com.nsym.domain.model.entity.fiscal.nfe.ItemNfe;

/**
 * Helper para geração dos blocos da Reforma Tributária (IS / IBS / CBS)
 * no INI do ACBr, tanto para NFe (mod 55) quanto para NFC-e (mod 65).
 *
 * É agnóstico a NFe/NFC-e: quem chama decide quando usar.
 */
public final class AcbrComunicaReformaHelper {

    private AcbrComunicaReformaHelper() {
    }

    /* ========= UTILITÁRIOS INTERNOS ========= */

    private static String section(String base, int contador) {
        // gera [IS001], [IBSCBS001], [gIBSCBS001] etc
        return "[" + base + String.format("%03d", contador) + "]\n";
    }

    private static String kv(String k, Object v) {
        return String.valueOf(k) + "=" + (v == null ? "" : String.valueOf(v)) + "\n";
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private static boolean hasPositive(BigDecimal v) {
        return v != null && v.compareTo(BigDecimal.ZERO) > 0;
    }

    /* ========= POR ITEM ========= */

    /**
     * Gera todos os blocos de Reforma (IS / IBSCBS / gIBSCBS / gIBSUF / gIBSMun / gCBS)
     * para um item específico.
     *
     * @param sb        StringBuilder do arquivo INI
     * @param item      ItemNfe já com os campos de Reforma preenchidos pelo CalculaTributos
     * @param contador  número sequencial do item (001, 002, ...)
     */
    public static void appendBlocosReformaItem(StringBuilder sb, ItemNfe item, int contador) {

        // ====== IS ====== (grupo [ISnnn])
        // Campos no ItemNfe:
        //  - cstIs, vbcIs, pIs, vIs  (já criados)
        if (item.getCstIs() != null || hasPositive(item.getVIs())) {
            sb.append(section("IS", contador))
              // deixar em branco ou usar cstis000
              .append(kv("CSTIS", item.getCstIs()))
              // deixar em branco ou usar 000001 (classificação IS, se você já tiver)
              .append(kv("cClassTribIS", null))
              .append(kv("vBCIS", item.getVbcIs()))
              .append(kv("pIS", item.getPIs()))
              // por enquanto não estamos tratando pISEspec / uTrib / qTrib
              .append(kv("pISEspec", null))
              .append(kv("uTrib", null))
              .append(kv("qTrib", null))
              .append(kv("vIS", item.getVIs()));
        }

        // ====== IBSCBS ====== (grupo [IBSCBSnnn])
        // Aqui usam-se os CSTs de IBS/CBS que vêm do CstIbsCbs via CalculaTributos
        if (item.getCstIbs() != null || item.getCstCbs() != null) {
            sb.append(section("IBSCBS", contador))
              // '','000', '010', '011', '200', ...
              .append(kv("CST", escolherCstIbscbs(item)))
              // Se você tiver CClassTrib amarrado ao item, pode pegar o código aqui.
              .append(kv("cClassTrib", item.getCclassTrib().getCClassTrib()));
        }

        // ====== gIBSCBS ====== (grupo [gIBSCBSnnn])
        // Usamos vBC comum e vIBS (pode ser refinado depois).
        if (hasPositive(item.getVbcIbs()) || hasPositive(item.getVIbs())) {
            sb.append(section("gIBSCBS", contador))
              .append(kv("vBC", item.getVbcIbs()))
              .append(kv("vIBS", item.getVIbs()));
        }

        // ====== gIBSUF ====== (grupo [gIBSUFnnn])
        // Neste momento não temos separação explícita UF x Mun,
        // então deixamos os campos mais “avançados” em branco.
        // Você pode especializar depois se criar campos específicos.
        sb.append(section("gIBSUF", contador))
          .append(kv("pIBSUF", null))
          .append(kv("vIBSUF", null))
          .append(kv("pDif", null))
          .append(kv("vDif", null))
          .append(kv("vDevTrib", null))
          .append(kv("pRedAliq", null))
          .append(kv("pAliqEfet", null));

        // ====== gIBSMun ====== (grupo [gIBSMunnnn]) respeitando indSemIbsm
        // Se indSemIbsm = TRUE, não geramos IBS municipal.
        boolean suprimirIbsm = Boolean.TRUE.equals(getIndSemIbsmSafe(item));
        if (!suprimirIbsm && hasPositive(item.getVIbs())) {
            sb.append(section("gIBSMun", contador))
              .append(kv("pIBSMun", item.getPIbs()))
              .append(kv("vIBSMun", item.getVIbs()))
              .append(kv("pDif", null))
              .append(kv("vDif", null))
              .append(kv("vDevTrib", null))
              .append(kv("pRedAliq", null))
              .append(kv("pAliqEfet", null));
        }

        // ====== gCBS ====== (grupo [gCBSnnn])
        if (hasPositive(item.getVCbs()) || hasPositive(item.getPCbs())) {
            sb.append(section("gCBS", contador))
              .append(kv("pCBS", item.getPCbs()))
              .append(kv("vCBS", item.getVCbs()))
              .append(kv("pDif", null))
              .append(kv("vDif", null))
              .append(kv("vDevTrib", null))
              .append(kv("pRedAliq", null))
              .append(kv("pAliqEfet", null));
        }

        // Os demais grupos (gTribRegular, gIBSCredPres, gCBSCredPres,
        // gTribCompraGov, gIBSCBSMono, gMonoPadrao, gMonoReten,
        // gMonoRet, gMonoDif, gTransfCred, gCredPresIBSZFM)
        // podem ser adicionados aqui no futuro, quando você tiver
        // os campos necessários mapeados no ItemNfe.
    }

    private static String escolherCstIbscbs(ItemNfe item) {
        // prioridade: se houver CST específico de IBS/CBS, você pode decidir qual usar aqui.
        // por exemplo, preferir IBS se existir:
        if (item.getCstIbs() != null && !item.getCstIbs().isEmpty()) {
            return item.getCstIbs();
        }
        if (item.getCstCbs() != null && !item.getCstCbs().isEmpty()) {
            return item.getCstCbs();
        }
        return null;
    }

    /**
     * Lê indSemIbsm de forma segura, caso ainda não exista no ItemNfe
     * na sua versão local (evita NPE em runtime enquanto você ajusta).
     */
    private static Boolean getIndSemIbsmSafe(ItemNfe item) {
        try {
            // se você já criou o campo transient indSemIbsm em ItemNfe com Lombok,
            // isso aqui vai funcionar:
            return (Boolean) ItemNfe.class
                    .getMethod("getIndSemIbsm")
                    .invoke(item);
        } catch (Exception e) {
            // se ainda não existir o campo, consideramos FALSE (não suprime IBS Mun)
            return Boolean.FALSE;
        }
    }

    /* ========= TOTAIS ========= */

    /**
     * Gera os grupos de totais da Reforma Tributária:
     * [ISTot], [IBSCBSTot], [gIBS], [gIBSUFTot], [gIBSMunTot], [gCBSTot], [gMono]
     * a partir da lista de itens já calculados.
     *
     * Pode ser chamado depois do [Total] padrão do ACBr.
     */
    public static void appendBlocosTotaisReforma(StringBuilder sb, List<ItemNfe> itens) {

        BigDecimal totalIS  = BigDecimal.ZERO;
        BigDecimal totalIBS = BigDecimal.ZERO;
        BigDecimal totalCBS = BigDecimal.ZERO;
        BigDecimal baseIBSCBS = BigDecimal.ZERO;

        for (ItemNfe it : itens) {
            totalIS    = totalIS.add(nz(it.getVIs()));
            totalIBS   = totalIBS.add(nz(it.getVIbs()));
            totalCBS   = totalCBS.add(nz(it.getVCbs()));
            baseIBSCBS = baseIBSCBS.add(nz(it.getVbcIbs())).add(nz(it.getVbcCbs()));
        }

        // [ISTot]
        sb.append("[ISTot]\n")
          .append(kv("vIS", hasPositive(totalIS) ? totalIS : null));

        // [IBSCBSTot]
        sb.append("[IBSCBSTot]\n")
          .append(kv("vBCIBSCBS", hasPositive(baseIBSCBS) ? baseIBSCBS : null));

        // [gIBS] – totais de IBS agregados
        sb.append("[gIBS]\n")
          .append(kv("vIBS", hasPositive(totalIBS) ? totalIBS : null))
          .append(kv("vCredPres", null))
          .append(kv("vCredPresCondSus", null));

        // [gIBSUFTot]
        sb.append("[gIBSUFTot]\n")
          .append(kv("vDif", null))
          .append(kv("vDevTrib", null))
          // hoje usamos o total IBS aqui; se no futuro você separar UF/Mun,
          // basta trocar por um campo específico de UF
          .append(kv("vIBSUF", hasPositive(totalIBS) ? totalIBS : null));

        // [gIBSMunTot] – idem acima, usando IBS como base enquanto não há separação
        sb.append("[gIBSMunTot]\n")
          .append(kv("vDif", null))
          .append(kv("vDevTrib", null))
          .append(kv("vIBSMun", hasPositive(totalIBS) ? totalIBS : null));

        // [gCBSTot] – totais de CBS
        sb.append("[gCBSTot]\n")
          .append(kv("vDif", null))
          .append(kv("vDevTrib", null))
          .append(kv("vCBS", hasPositive(totalCBS) ? totalCBS : null))
          .append(kv("vCredPres", null))
          .append(kv("vCredPresCondSus", null));

        // [gMono] – deixado preparado, mas sem valores por enquanto
        sb.append("[gMono]\n")
          .append(kv("vIBSMono", null))
          .append(kv("vCBSMono", null))
          .append(kv("vIBSMonoReten", null))
          .append(kv("vCBSMonoReten", null))
          .append(kv("vIBSMonoRet", null))
          .append(kv("vCBSMonoRet", null));
    }
}
