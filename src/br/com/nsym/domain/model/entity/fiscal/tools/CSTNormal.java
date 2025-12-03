package br.com.nsym.domain.model.entity.fiscal.tools;

public enum CSTNormal {
	
	C00("00 - Tributada integralmente","00"),
	C10("10 - Tributada com cobrança do ICMS por ST","10"),
	C10N("10 - Tributada com cobrança do ICMS por ST(com partilha do ICMS entre UF de origem e destino ou a UF definida na legislação","10"),
	C20("20 - Com redução da base de cálculo","20"),
	C30("30 - Isenta ou não tributada e com cobrança do ICMS por ST","30"),
	C40("40 - Isenta","40"),
	C41("41 - Não Tributada","41"),
	C41N("41 - Não Tributada (ICMSST devido para UF de destino, nas operações interestaduais de produtos que tiveram retenção antecipada de ICMS por ST ou na UF do remetente)","41"),
	C50("50 - Suspensão","50"),
	C51("51 - Diferimento","51"),
	C60("60 - Cobrado anteriormente por ST","60"),
	C70("70 - Com redução da base de cálculo e cobrança do ICMS por ST","70"),
	C90N("90 - Outras(Com partilha do ICMS entre a UF de origem e de destino ou a UF definida na legislação","90"),
	C90("90 - Outras","90");
	
	
	
private final String description;
private final String cst;
	
	private CSTNormal(String description,String cst){
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

}
