package br.com.nsym.domain.model.entity.financeiro.Enum;

public enum CarteiraEnvio {
	
	tpCedente("Cedente",0),
	tpBanco("Banco",1),
	tpBancoEmail("Banco - Email",2);
	
	private final String description;
	private int sigla;


	private CarteiraEnvio(String description, int sigla){
		this.description = description;
		this.sigla = sigla;
	}

	@Override
	public String toString(){
		return this.description;
	}

	public int getSigla(){
		return this.sigla;
	}

}
