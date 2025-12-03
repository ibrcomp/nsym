package br.com.nsym.domain.model.entity.fabrica.util;

public enum StatusAndamento {


	AND("Em andamento"),
	CON("Concluido-Dep."),
	AGU("Aguardando início"),
	FIM("Finalizado");

private final String description;

private StatusAndamento(String description) {
	this.description = description;
}

public String getDescription() {
	return description;
}
@Override
public String toString(){
	return this.description;
}

}