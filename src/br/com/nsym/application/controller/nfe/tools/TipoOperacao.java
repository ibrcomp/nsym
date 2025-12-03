package br.com.nsym.application.controller.nfe.tools;

public enum TipoOperacao {
	
	O0("0 - Não se aplica (Nfe Complementar ou Ajuste)",0),        
	O1("1 - Operação Presencial",1),        
	O2("2 - Operação não presencial, pela Internet",2),                             
	O3("3 - Operação não presencial, Teleatendimento",3),                      
	O4("4 - NFC-e em operação com entrega a domicílio",4),                              
	O9("9 - Operação não presencial, outros",9);                     
	

	private final String description;
	private final int codigo;

	private TipoOperacao(String description,int cst){
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
