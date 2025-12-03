package br.com.nsym.domain.model.entity.financeiro;

/**
 * Lista simplicada utilizada em alguns relatórios para reduzir as opções 
 *
 */
public enum ParcelaStatusSimplificada {
	
	REC("Recebido","REC"),
	AGR("AGRUPADO","AGR"),
	ALL("TODOS","ALL"),
	ABE("Pendente","ABE");
	
	
	
	private final String description;
	private final String cod;
	
	private ParcelaStatusSimplificada(String description, String cod) {
		this.description = description;
		this.cod = cod;
	}
	
	
	@Override
	public String toString(){
		return this.description;
	}
	
	public String getCod(){
		return this.cod;
	}
	
	public ParcelaStatusSimplificada achaParcelaPorCodigo(String cod){
		ParcelaStatusSimplificada tipoTemporario = ParcelaStatusSimplificada.REC;
		for (ParcelaStatusSimplificada tipo : ParcelaStatusSimplificada.values()) {
			if (cod.equals(tipo.getCod())){
				tipoTemporario = tipo;
			}
		}
		return tipoTemporario;
	}

}
