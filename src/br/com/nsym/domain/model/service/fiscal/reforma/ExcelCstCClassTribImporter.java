package br.com.nsym.domain.model.service.fiscal.reforma;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
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

    	        Sheet cstSheet   = findSheet(wb, "cst");
    	        Sheet classSheet = findSheet(wb, "cclasstrib", "cclass");

    	        int cstCount  = importarCst(cstSheet);
    	        int clasCount = importarCClassTrib(classSheet);

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

            String cst = tentaNumero(str(row, idx.col("CST-IBS/CBS")));
            if (isBlank(cst)) {
                continue;
            }

            CstIbsCbs ent = cstRepo.findByCst(cst); 
            if (ent == null) {
                ent = new CstIbsCbs();
            }

            ent.setCstIbsCbs(cst);
            ent.setDescricaoCstIbsCbs(str(row, idx.col("Descrição CST-IBS/CBS")));
            ent.setIndGibscbs(bool(row, idx.colAny("Ind GIBSCBS", "ind_gIBSCBS")));
            ent.setIndgIBSCBSMono(bool(row, idx.colAny("ind_gIBSCBSMono","ind gIBSCBSMono")));
            ent.setIndgRed(bool(row, idx.colAny("ind_gRed","ind gRed")));
            ent.setIndgDif(bool(row, idx.colAny("ind_gDif","ind gDif")));
            ent.setIndgTransfCred(bool(row, idx.colAny("ind_gTransfCred","ind gTransfCred")));
            ent.setIndgCredPresIBSZFM(bool(row, idx.colAny("ind_ gCredPresIBSZFM","ind_gCredPresIBSZFM", "ind gCredPresIBSZFM")));
            ent.setIndgAjusteCompet(bool(row, idx.colAny("ind_gAjusteCompet","ind gAjusteCompet")));
            ent.setIndRedutorBC(bool(row, idx.colAny("ind_RedutorBC","ind RedutorBC")));
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

            String cst = tentaNumero(str(row, idx.col("CST-IBS/CBS")));
            String cclass = tentaNumero(str(row, idx.col("cClassTrib")));
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
            ent.setPRedIbs(dec(row, idx.colAny("% Red. IBS", "pRedIBS")));
            ent.setPRedCbs(dec(row, idx.colAny("% Red. CBS", "pRedCBS")));
            ent.setIndIbsRecuperavel(bool(row, idx.col("ind IBS Recuperável")));
            ent.setIndCbsRecuperavel(bool(row, idx.col("ind CBS Recuperável")));
            ent.setIndExigMus(bool(row, idx.col("ind Exig MUS")));
            ent.setIndIsSeletivo(bool(row, idx.col("ind IS Seletivo")));
            ent.setIndOutraCamada(bool(row, idx.col("ind Outra Camada")));
            ent.setIndMonofasico(bool(row, idx.col("ind Monofásico")));
            ent.setIndCbsPorDentro(bool(row, idx.col("ind CBS por dentro")));
            ent.setIndIbsPorDentro(bool(row, idx.col("ind IBS por dentro")));
            ent.setIndgTribRegular(   bool(row, idx.col("ind_gTribRegular")));
            ent.setIndgCredPresOper(  bool(row, idx.col("ind_gCredPresOper")));
            ent.setIndgMonoPadrao(    bool(row, idx.col("ind_gMonoPadrao")));
            ent.setIndgMonoReten(     bool(row, idx.col("ind_gMonoReten")));
            ent.setIndgMonoRet(       bool(row, idx.col("ind_gMonoRet")));
            ent.setIndgMonoDif(       bool(row, idx.col("ind_gMonoDif")));
            ent.setIndgEstornoCred(   bool(row, idx.col("ind_gEstornoCred")));
            ent.setTipoIs(str(row, idx.col("Tipo IS")));
            ent.setNotas(str(row, idx.col("Notas")));
            ent.setAnexo(tentaNumero(str(row, idx.col("ANEXO"))));
            ent.setLink(str(row, idx.col("Link")));
            ent.setIndCstAjustada(bool(row, idx.col("ind CST ajustada")));
            ent.setIndApenasPessoaFisica(bool(row, idx.col("ind Apenas Pessoa Física")));
            ent.setIndApenasContribuinte(bool(row, idx.col("ind Apenas Contribuinte")));
            ent.setIndCestObrigatorio(bool(row, idx.col("ind CEST Obrigatório")));
            ent.setIndPfNfce60B(bool(row, idx.col("ind PF NFC-e 60B")));
            ent.setIndNfseVia(bool(row, idx.colAny("ind NFSe Via", "indNFSe Via")));
            ent.setIndOutros(bool(row, idx.col("ind Outros")));
            ent.setDIniVig(   date(row, idx.col("dIniVig")));
            ent.setDFimVig(   date(row, idx.col("dFimVig")));
            ent.setDAtualizado(date(row, idx.col("DataAtualização")));
            ent.setIndNFeABI( bool(row, idx.col("indNFeABI")));
            ent.setIndNFe(    bool(row, idx.col("indNFe")));
            ent.setIndNFCe(   bool(row, idx.col("indNFCe")));
            ent.setIndCTe(    bool(row, idx.col("indCTe")));
            ent.setIndCTeOS(  bool(row, idx.col("indCTeOS")));
            ent.setIndBPe(    bool(row, idx.col("indBPe")));
            ent.setIndBPeTA(  bool(row, idx.col("indBPeTA")));
            ent.setIndBPeTM(  bool(row, idx.col("indBPeTM")));
            ent.setIndNF3e(   bool(row, idx.col("indNF3e")));
            ent.setIndNFSe(   bool(row, idx.col("indNFSe")));
            ent.setIndNFCom(  bool(row, idx.col("indNFCom")));
            ent.setIndNFAg(   bool(row, idx.col("indNFAg")));
            ent.setIndNFGas(  bool(row, idx.col("indNFGas")));
            ent.setIndDERE(   bool(row, idx.col("indDERE")));

            clasRepo.save(ent);
            upserts++;
        }
        return upserts;
    }

    /* =============== Helpers POI =============== */
    
    
    private LocalDate date(Row r, int idx) {
        if (idx < 0) {
            return null;
        }
        Cell c = r.getCell(idx);
        if (c == null) {
            return null;
        }

        try {
            if (c.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(c)) {
                java.util.Date dt = c.getDateCellValue();
                if (dt != null) {
                    return dt.toInstant()
                             .atZone(java.time.ZoneId.systemDefault())
                             .toLocalDate();
                }
            }
        } catch (Exception e) {
            // se der algum erro, cai para parsing por String
        }

        String s = HeaderIdx.cellStr(c);
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        try {
            // ajuste o padrão conforme o formato que você usar
            java.time.format.DateTimeFormatter fmt =
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(s.trim(), fmt);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Procura uma planilha cujo nome (normalizado) comece com um dos prefixos desejados.
     * Ex.: "CST", "CST 19-11-2025", "cClass 19-11-2025", etc.
     */
    private Sheet findSheet(Workbook wb, String... desiredPrefixes) {
        if (wb == null) {
            return null;
        }
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            Sheet sheet = wb.getSheetAt(i);
            if (sheet == null) {
                continue;
            }
            String normalized = normalizeSheetName(sheet.getSheetName());
            for (String prefix : desiredPrefixes) {
                if (normalized.startsWith(prefix.toLowerCase(Locale.ROOT))) {
                    return sheet;
                }
            }
        }
        return null;
    }

    /** Normaliza o nome da aba: trim, lower-case e remoção de espaços. */
    private String normalizeSheetName(String s) {
        if (s == null) {
            return "";
        }
        return s.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", "");
    }


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

        int colAny(String... names) {
            if (names == null) {
                return -1;
            }
            for (String n : names) {
                int idx = col(n);
                if (idx >= 0) {
                    return idx;
                }
            }
            return -1;
        }

        private String normalize(String s) {
            if (s == null) {
                return null;
            }
            // normaliza: trim, lower-case, "_" tratado como espaço e múltiplos espaços colapsados
            s = s.trim().toLowerCase(Locale.ROOT);
            s = s.replace('_', ' ');
            s = s.replaceAll("\\s+", " ");
            return s;
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

    private BigDecimal parseNumeroFlex(String s) {
        if (s == null) return null;

        String v = s.trim();
        if (v.isEmpty()) return null;

        // tira espaço estranho
        v = v.replace("\u00A0", "").trim();

        boolean hasDot   = v.contains(".");
        boolean hasComma = v.contains(",");

        if (hasDot && hasComma) {
            // Tem ponto e vírgula juntos: assume que o último é o decimal
            int lastDot   = v.lastIndexOf('.');
            int lastComma = v.lastIndexOf(',');

            if (lastComma > lastDot) {
                // Formato PT-BR: 1.234,56  (vírgula é decimal)
                v = v.replace(".", "");   // tira milhar
                v = v.replace(",", ".");  // vírgula vira decimal
            } else {
                // Formato EN: 1,234.56  (ponto é decimal)
                v = v.replace(",", "");   // tira milhar
                // ponto já é decimal
            }
        } else if (hasComma) {
            // Só vírgula: trata como decimal PT-BR
            v = v.replace(",", ".");
        }
        // Só ponto ou nenhum: deixa como está

        return new BigDecimal(v);
    }

    private String tentaNumero(String s) {
        if (s == null) {
            return null;
        }
        String v = s.trim();
        if (v.isEmpty()) {
            return v;
        }

        try {
            BigDecimal num = parseNumeroFlex(v);
            if (num == null) {
                return s;
            }

            // remove zeros desnecessários (1.0 -> 1, 2.50 -> 2.5)
            num = num.stripTrailingZeros();
            String txt = num.toPlainString(); // ex: "1", "1.5", "0.5"

            // Se não tem ponto, já é inteiro: "1", "2", "123"
            if (!txt.contains(".")) {
                return txt;
            }

            // Tem parte decimal: remove o ponto
            String semPonto = txt.replace(".", "");

            // tira zeros à esquerda, mas deixa um zero se for tudo zero
            String resultado = semPonto.replaceFirst("^0+(?!$)", "");

            return resultado;
        } catch (NumberFormatException e) {
            // não era número, devolve como veio
            return s;
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
        if (v.isEmpty()) {
            return null;
        }

        // 1) Tenta interpretar como número: 0, 1, 1.0, 0.0, 1,00 etc.
        try {
            java.math.BigDecimal num = new java.math.BigDecimal(v.replace(",", "."));
            if (num.compareTo(java.math.BigDecimal.ZERO) == 0) {
                return Boolean.FALSE;
            }
            if (num.compareTo(java.math.BigDecimal.ONE) == 0) {
                return Boolean.TRUE;
            }
        } catch (NumberFormatException e) {
            // não era número, continua no tratamento textual
        }

        // 2) Trata valores textuais
        return "1".equals(v)
                || "true".equals(v)
                || "sim".equals(v)
                || "s".equals(v)
                || "yes".equals(v)
                || "x".equals(v); // opcional, caso use "X" na planilha
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

