package br.com.nsym.domain.misc;

public enum ModeloImpressoraAcbr {
	M1("Texto","ppTexto"),
	M2("Epson Pos","ppEscPosEpson"),
	M3("Bematech","ppEscBematech"),
	M4("Daruma","ppEscDaruma"),
	M5("Vox","ppEscVox"),
	M6("Diebold","ppEscDiebold"),
	M7("Epson Pos 2","ppEscEpsonP2"),
	M8("Custom","ppCustomPos"),
	M9("Epson Pos Star","ppEscPosStar"),
	M10("ZJiang","ppEscZJiang"),
	M11("GPrinter","ppEscGPrinter"),
	M12("Datecs","ppEscDatecs"),
	M13("Externo","ppExterno");
	
	
	private final String description;
	private final String modelo;

	
	private ModeloImpressoraAcbr(String description,String mod){
		this.description = description;
		this.modelo = mod;
	}
	
	@Override
	public String toString(){
		return this.description;
	}
	
	public String getModelo(){
		return this.modelo;
	}

}
