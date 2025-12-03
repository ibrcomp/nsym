package br.com.nsym.domain.model.entity.fiscal.nfe;

public enum TipoIntermedio {

	I1("1 - Importação por conta propria",1),        
	I2("2 - Importação por conta e ordem",2),                             
	I3("3 - Importação por encomenda",3);                      
	

	private final String description;
	private final int codigo;

	private TipoIntermedio(String description,int cst){
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
