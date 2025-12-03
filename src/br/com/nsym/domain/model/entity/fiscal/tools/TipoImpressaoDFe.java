package br.com.nsym.domain.model.entity.fiscal.tools;

public enum TipoImpressaoDFe {
	
	C0("0 - Sem geração de DANFE",0),
	C1("1 - DANFE normal, Retrato",1),
	C2("2 - DANFE normal, Paisagem",2),
	C3("3 - DANFE Simplificado",3),
	C4("4 - DANFE NFC-e;",4),
	C5("5 - DANFE NFC-e em mensagem eletrônica (o envio de mensagem eletrônica pode ser feita de forma simultânea com a impressão do DANFE;"
			+ " usar o tpImp=5 quando esta for a única forma de disponibilização do DANFE).",5);
	
	
	
private final String description;
private final int tpImp;
	
	private TipoImpressaoDFe(String description,int tpImp){
		this.description = description;
		this.tpImp=tpImp;
	}
	
	@Override
	public String toString(){
		return this.description;
	}

	public int getTpImp() {
		return tpImp;
	}

}
