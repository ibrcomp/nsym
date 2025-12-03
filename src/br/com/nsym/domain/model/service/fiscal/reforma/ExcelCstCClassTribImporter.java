package br.com.nsym.domain.model.service.fiscal.reforma;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import br.com.nsym.domain.model.entity.fiscal.reforma.CClassTrib;
import br.com.nsym.domain.model.entity.fiscal.reforma.CstIbsCbs;
import br.com.nsym.domain.model.repository.fiscal.reforma.CClassTribRepository;
import br.com.nsym.domain.model.repository.fiscal.reforma.CstIbsCbsRepository;

@RequestScoped
public class ExcelCstCClassTribImporter {

    @Inject
    private CstIbsCbsRepository cstRepo;

    @Inject
    private CClassTribRepository clasRepo;

    public static class Relatorio {
        private int qtdeCst;
        private int qtdeCClassTrib;

        public Relatorio(int cst, int clas) {
            this.qtdeCst = cst;
            this.qtdeCClassTrib = clas;
        }

        public int getQtdeCst() {
            return qtdeCst;
        }

        public int getQtdeCClassTrib() {
            return qtdeCClassTrib;
        }
    }

    @Transactional
    public Relatorio importar(InputStream in) throws Exception {
        Workbook wb = null;
        try {
            wb = new XSSFWorkbook(in);
            int cstCount = importarCst(wb.getSheet("CST"));
            int clasCount = importarCClassTrib(wb.getSheet("cClassTrib"));
            return new Relatorio(cstCount, clasCount);
        } finally {
            if (wb != null) {
                wb.close();
            }
        }
    }

    /* =============== Aba CST ================= */

    private int importarCst(Sheet sheet) throws Exception {
        if (sheet == null) {
            return 0;
        }
        Row header = sheet.getRow(0);
        HeaderIdx idx = new HeaderIdx(header);

        int upserts = 0;
        int lastRow = sheet.getLastRowNum();
        for (int r = 1; r <= lastRow; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }

            String cst = str(row, idx.col("CST-IBS/CBS"));
            if (isBlank(cst)) {
                continue;
            }

            CstIbsCbs ent = cstRepo.findByCst(cst);
            if (ent == null) {
                ent = new CstIbsCbs();
            }

            ent.setCstIbsCbs(cst);
            ent.setDescricaoCstIbsCbs(str(row, idx.col("Descrição CST-IBS/CBS")));
            ent.setIndGibscbs(bool(row, idx.col("Ind GIBSCBS")));
            ent.setIndVigente(bool(row, idx.col("IND VIGENTE")));
            ent.setTipoNfeNfce(str(row, idx.col("Tipo NF-e/NFC-e")));
            ent.setNaturezaDaReceita(str(row, idx.col("Natureza da Receita")));
            ent.setNotas(str(row, idx.col("Notas")));

            cstRepo.save(ent);
            upserts++;
        }
        return upserts;
    }

    /* =============== Aba cClassTrib ================= */

    private int importarCClassTrib(Sheet sheet) throws Exception {
        if (sheet == null) {
            return 0;
        }
        Row header = sheet.getRow(0);
        HeaderIdx idx = new HeaderIdx(header);

        int upserts = 0;
        int lastRow = sheet.getLastRowNum();
        for (int r = 1; r <= lastRow; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }

            String cst = str(row, idx.col("CST-IBS/CBS"));
            String cclass = str(row, idx.col("cClassTrib"));
            if (isBlank(cst) || isBlank(cclass)) {
                continue;
            }

            CClassTrib ent = clasRepo.findByCstAndCClassTrib(cst, cclass);
            if (ent == null) {
                ent = new CClassTrib();
            }

            ent.setCstIbsCbs(cst);
            ent.setCClassTrib(cclass);

            ent.setNomeCClassTrib(str(row, idx.col("Nome cClassTrib")));
            ent.setTipoDeAliquota(str(row, idx.col("Tipo de alíquota")));
            ent.setPRedIbs(dec(row, idx.col("% Red. IBS")));
            ent.setPRedCbs(dec(row, idx.col("% Red. CBS")));
            ent.setIndIbsRecuperavel(bool(row, idx.col("ind IBS Recuperável")));
            ent.setIndCbsRecuperavel(bool(row, idx.col("ind CBS Recuperável")));
            ent.setIndExigMus(bool(row, idx.col("ind Exig MUS")));
            ent.setIndIsSeletivo(bool(row, idx.col("ind IS Seletivo")));
            ent.setIndOutraCamada(bool(row, idx.col("ind Outra Camada")));
            ent.setIndMonofasico(bool(row, idx.col("ind Monofásico")));
            ent.setIndCbsPorDentro(bool(row, idx.col("ind CBS por dentro")));
            ent.setIndIbsPorDentro(bool(row, idx.col("ind IBS por dentro")));
            ent.setTipoIs(str(row, idx.col("Tipo IS")));
            ent.setNotas(str(row, idx.col("Notas")));
            ent.setAnexo(str(row, idx.col("ANEXO")));
            ent.setLink(str(row, idx.col("Link")));
            ent.setIndCstAjustada(bool(row, idx.col("ind CST ajustada")));
            ent.setIndApenasPessoaFisica(bool(row, idx.col("ind Apenas Pessoa Física")));
            ent.setIndApenasContribuinte(bool(row, idx.col("ind Apenas Contribuinte")));
            ent.setIndCestObrigatorio(bool(row, idx.col("ind CEST Obrigatório")));
            ent.setIndPfNfce60B(bool(row, idx.col("ind PF NFC-e 60B")));
            ent.setIndNfseVia(bool(row, idx.col("ind NFSe Via")));
            ent.setIndOutros(bool(row, idx.col("ind Outros")));

            clasRepo.save(ent);
            upserts++;
        }
        return upserts;
    }

    /* =============== Helpers POI =============== */

    private static class HeaderIdx {
        private final Map<String, Integer> map = new HashMap<String, Integer>();

        HeaderIdx(Row header) {
            if (header != null) {
                short lastCell = header.getLastCellNum();
                for (int i = 0; i < lastCell; i++) {
                    Cell c = header.getCell(i);
                    String key = normalize(cellStr(c));
                    if (key != null && !key.isEmpty()) {
                        map.put(key, i);
                    }
                }
            }
        }

        int col(String name) {
            return map.containsKey(normalize(name)) ? map.get(normalize(name)) : -1;
        }

        private String normalize(String s) {
            if (s == null) {
                return null;
            }
            return s.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
        }

        private static String cellStr(Cell c) {
            if (c == null) {
                return null;
            }
            switch (c.getCellType()) {
                case STRING:
                    return c.getStringCellValue();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(c)) {
                        return c.getDateCellValue().toString();
                    }
                    return String.valueOf(c.getNumericCellValue());
                case BOOLEAN:
                    return String.valueOf(c.getBooleanCellValue());
                case FORMULA:
                    return c.getCellFormula();
                default:
                    return null;
            }
        }
    }

    private String str(Row r, int idx) {
        if (idx < 0) {
            return null;
        }
        Cell c = r.getCell(idx);
        return HeaderIdx.cellStr(c);
    }

    private Boolean bool(Row r, int idx) {
        String s = str(r, idx);
        if (s == null) {
            return null;
        }
        String v = s.trim().toLowerCase(Locale.ROOT);
        return "1".equals(v)
                || "true".equals(v)
                || "sim".equals(v)
                || "s".equals(v)
                || "yes".equals(v);
    }

    private BigDecimal dec(Row r, int idx) {
        String s = str(r, idx);
        if (s == null || s.isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(s.replace(",", "."));
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}

