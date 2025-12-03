package br.com.nsym.domain.model.entity.fiscal.tools;

public enum CSTIPI {
	C00("00 - Entrada com recuperação de crédito","00"),        
	C01("01 - Entrada tributada com alíquota zero","01"),        
	C02("02 - Entrada isenta","02"),                             
	C03("03 - Entrada não-tributada","03"),                      
	C04("04 - Entrada imune","04"),                              
	C05("05 - Entrada com suspensão","05"),                     
	C49("49 - Outras entradas","49"),                            
	C50("50 - Saída tributada","50"),                           
	C51("51 - Saída tributada com alíquota zero","51"),          
	C52("52 - Saída isenta","52"),                               
	C53("53 - Saída não-tributada","53"),                        
	C54("54 - Saída imune","54"),                                
	C55("55 - Saída com suspensão","55"),                        
	C99("99 - Outras Saídas","99");   

	private final String description;
	private final String cst;

	private CSTIPI(String description,String cst){
		this.description = description;
		this.cst=cst;
	}

	@Override
	public String toString(){
		return this.description;
	}

	public String getCst() {
		return cst;
	}
}
