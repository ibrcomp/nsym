package br.com.nsym.domain.model.entity.tools;

public enum TipoControleEstoque {

		BA("Básico",1), // controle apenas quantidade
		MO("Médio",2), // controla cor
		MA("Máximo",3); // Controla cor e tamanho
		
		private final String description;
		private int codigo;
		
		
		private TipoControleEstoque(String description, int codigo){
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