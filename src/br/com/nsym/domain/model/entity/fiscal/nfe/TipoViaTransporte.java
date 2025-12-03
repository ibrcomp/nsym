package br.com.nsym.domain.model.entity.fiscal.nfe;

public enum TipoViaTransporte {

	T1("1 - Marítima",1),        
	T2("2 - Fluvial",2),                             
	T3("3 - Lacustre",3),
	T4("4 - Aérea",4),
	T5("5 - Postal",5),
	T6("6 - Ferroviária",6),
	T7("7 - Rodoviária",7),
	T8("8 - Conduto / Rede Transmissão",8),
	T9("9 - Meios Próprios",9),
	T10("10 - Entrada / Saída ficta.",10),
	T11("11 - Courier",11),
	T12("12 - Handcarry",12);
	

	private final String description;
	private final int codigo;

	private TipoViaTransporte(String description,int cst){
		this.description = description;
		this.codigo=cst;
	}

	@Override
	public String toString(){
		return this.description;
	}

	public int getCodigo() {
		return codigo;
	}
	
}
