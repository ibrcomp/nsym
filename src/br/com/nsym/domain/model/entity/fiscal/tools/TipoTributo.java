package br.com.nsym.domain.model.entity.fiscal.tools;


public enum TipoTributo {
	 	IBS("Ibs"),
	    IBS_MUN("Ibs_Mun"),
	    CBS("Cbs"),
	    IS("IS");
	
	private final String description;

	private TipoTributo(String description){
		this.description = description;
	}

	@Override
	public String toString(){
		return this.description;
	}

}
