package br.com.nsym.domain.model.entity.cadastro;

import lombok.Getter;

public enum TipoCadastro {
	CLI("CLIENTE",0),
	EMP("EMPRESA",1),
	FIL("FILIAL",2),
	FOR("FORNECEDOR",3),
	COLAB("COLABORADOR",4),
	TRANSP("TRANSPORTADOR",5);
	
private final String description;
@Getter
private final int codigo;
	
	private TipoCadastro(String description,int codigo){
		this.description = description;
		this.codigo = codigo;
	}
	
	@Override
	public String toString(){
		return this.description;
	}
}
