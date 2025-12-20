package br.com.nsym.domain.model.entity.fiscal.tools;

public enum CSTSimples {
	
	C101("101 - Tributada com permissão de crédito","101"),
	C102("102 - Tributada sem permissão de crédito","102"),
	C103("103 - Isenção do ICMS para faixa de receita bruta","103"),
	C201("201 - Tributada com permissão de crédito e com cobrança do ICMS por ST","201"),
	C202("202 - Tributada sem premissão de crédito e com cobrança do ICMS por ST","202"),
	C203("203 - Isenção do ICMS para faixa de receita bruta e com cobrança do ICMS por ST","203"),
	C300("300 - Imune","300"),
	C400("400 - Não Tributada","400"),
	C500("500 - ICMS cobrado anteriormente por ST ou por antecipação","500"),
	C900("900 - Outros","900");
	
	
	
private final String description;
private final String cst;
	
	private CSTSimples(String description,String cst){
		this.description = description;
		this.cst=cst;
	}
	
	@Override
	public String toString(){
		return this.description;
	}

	public String getCst() {
		return cst;
	}
	
	public static CSTSimples fromCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) return null;

        String c = codigo.trim();
        for (CSTSimples e : values()) {
            if (e.cst.equals(c)) {
                return e;
            }
        }
        return null; // ou lança exception com mensagem melhor
    }
	

}