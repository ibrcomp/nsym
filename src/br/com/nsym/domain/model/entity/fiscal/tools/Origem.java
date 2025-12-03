package br.com.nsym.domain.model.entity.fiscal.tools;

public enum Origem {
	
	C0("0 - Nacional, exceto as indicadas nos códigos 3,4,5 e 8",0),
	C1("1 - Estrangeira - Importação Direta, exceto a indicada no código 6",1),
	C2("2 - Estrangeira - Adquirida no Mercado Interno, exceto a indicada no código 7",2),
	C3("3 - Nacional, mercadoria ou bem com conteúdo de Importação superior a 40% e inferior a 70%",3),
	C4("4 - Nacional, cuja produção tenha sido feita em conformidade com os processos produtivos básicos de que tratam as legislações citadas nos Ajustes",4),
	C5("5 - Nacional, mercadoria ou bem com conteúdo de Importação inferior ou igual a 40%",5),
	C6("6 - Estrangeira - Importação direta, sem similar nacional, constante em lista da CAMEX e gás natural",6),
	C7("7 - Estrangeira - Adquirida no mercado interno -  sem similar nacional, constante em lista da CAMEX e gás natural",7),
	C8("8 - Nacional, mercadoria ou bem com conteúdo de Importação superior a 70%",8);
	
	
private final String description;
private final int cst;
	
	private Origem(String description,int cst){
		this.description = description;
		this.cst=cst;
	}
	
	@Override
	public String toString(){
		return this.description;
	}

	public int getCst() {
		return cst;
	}
}
