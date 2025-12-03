package br.com.nsym.domain.model.entity.tools;

public enum TipoFrete {
	EM("Contratação do frete por conta do Emitente (CIF)",0),
	DE("Contratação do frete por conta do Destinatário (FOB)",1),
	SE("Sem Frete",9),
	TE("Contratação do frete por conta de Terceiros",2),
	PR("Transporte Próprio por conta do Remetente",3),
	PD("Transporte Próprio por conta do Destinatário",4);
	
	
	private final String description;
	private int codigo;
	
	
	private TipoFrete(String description, int codigo){
		this.description = description;
		this.codigo = codigo;
	}
	
	@Override
	public String toString(){
		return this.description;
	}
	
	public int getCodigo(){
		return this.codigo;
	}
}
