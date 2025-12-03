package br.com.nsym.domain.model.entity.tools;

public enum TipoMedida {
	UNID("Unidade","UNID"),
	UN("Unidade UN","UN"),
	PARES("Pares","PARES"),
	DUZIA("Duzia","DUZIA"),
	PC("Peça","PC"),
	CJ("Conjunto","CJ"),
	JOGO("JOGO","JOGO"),
	SACOLA("SACOLA","SACOLA"),
	M("Metro","M"),
	M2("METRO QUADRADO","M2"),
	M3("METRO CÚBICO","M3"),
	CM("Centimentros","CM"),
	CART("Cartela","CART"),
	MM("Milimetros","MM"),
	KG("Kilo Grama","KG"),
	KIT("KIT","KIT"),
	GRAMAS("GRAMAS","GRAMAS"),
	ML("Mililitros","ML"),
	PACOTE("PACOTE","PACOTE"),
	MILHEI("Milheiro","MILHEI"),
	CX("CAIXA","CX"),
	CX2("CAIXA COM 2 UNIDADES","CX2"),
	CX3("CAIXA COM 3 UNIDADES","CX3"),
	CX5("CAIXA COM 5 UNIDADES","CX5"),
	CX10("CAIXA COM 10 UNIDADES","CX10"),
	CX15("CAIXA COM 15 UNIDADES","CX15"),
	CX20("CAIXA COM 20 UNIDADES","CX20"),
	CX25("CAIXA COM 25 UNIDADES","CX25"),
	CX50("CAIXA COM 50 UNIDADES","CX50"),
	CX100("CAIXA COM 100 UNIDADES","CX100"),
	VASIL("VASILHAME","VASIL"),
	LITRO("Litro","LITRO");


	private final String description;
	private String sigla;


	private TipoMedida(String description, String sigla){
		this.description = description;
		this.sigla = sigla;
	}

	@Override
	public String toString(){
		return this.description;
	}

	public String getSigla(){
		return this.sigla;
	}

	public boolean achei(){
		boolean resultado = false;
		for (TipoMedida tp : TipoMedida.values()) {
			if (tp.equals(this.description.toUpperCase())){
				resultado = true;
			}
		}
		return resultado;
	}
	
}
