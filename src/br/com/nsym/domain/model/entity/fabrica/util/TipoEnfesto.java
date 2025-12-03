package br.com.nsym.domain.model.entity.fabrica.util;

public enum TipoEnfesto {

	ZIG("ZIGZAGUE OU ACORDEAO","As camadas ou folhas do tecido são dispostas direito com direito e avesso com avesso."
			+ " É o sistema mais rápido, porque aproveita a ida e a volta dos funcionários na estendida.)"),
	DAV("DIRETO COM AVESSO","As camadas ou folhas de tecido são dispostas direito com avesso, isto é, após cada camada colocada volta-se à "
			+ "extremidade inicial da mesa de enfesto para estender a próxima. "
			+ "Essa forma de enfestar é aplicada quando se tem o tecido com estampas orientadas, com direção, também chamado estampa com pé."),
	DDI("DIRETO COM DIREITO EM SENTIDOS OPOSTOS","Alguns tipos de tecido, como o chenile e o veludo, "
			+ "precisam que a estendida seja iniciada sempre na mesma extremidade da mesa de enfesto, "
			+ "mas com o sentido do tecido em direções opostas, para que o atrito entre as faces evite o deslizamento entre as folhas.");
	
	private final String description;
	private final String explicacao;
	
	private TipoEnfesto(String description,String explicacao){
		this.description = description;
		this.explicacao=explicacao;
	}

	@Override
	public String toString(){
		return this.description;
	}

	public String getExplicacao() {
		return this.explicacao;
	}
}
