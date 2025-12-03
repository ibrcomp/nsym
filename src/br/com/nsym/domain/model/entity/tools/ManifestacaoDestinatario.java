package br.com.nsym.domain.model.entity.tools;

/**
 * 
 * @author Ibrahim Yousef Quatani
 * @version 1.0.0
 * @since 1.0.0, 21/09/2021
 *
 */

public enum ManifestacaoDestinatario {
	
	Cop("Confirmação de Operação","210200"),
	Cem("Ciencia da Emissão","210210"),
	Dop("Desconhecimento da Operação","210220"),
	Nma("Aguardando status","000000"),
	Onr("Operação não realizada","210240");
	
	private final String description;
	private final String sigla;
	
	private ManifestacaoDestinatario(String description,String sigla) {
		this.description = description;
		this.sigla = sigla;
	}
	
	@Override
	public String toString() {
		return this.description;
	}

	public String getSigla() {
		return this.sigla;
	}

}
