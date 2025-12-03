package br.com.nsym.domain.misc;

public class ValidadorCpf {
	
	
	public boolean isCpfValido(String numero){
		int cpf[] = new int[11], dv1=0, dv2=0;

		numero = numero.replace(".", "");
		numero = numero.replace("-", "");

		if (numero.length()!=11)
			return false;

		for(int i=0;i<11;i++)
			cpf[i] = Integer.parseInt(numero.substring(i, i+1));
		for(int i=0;i<9;i++)
	           dv1 += cpf[i] * (i+1);
	    cpf[9] = dv1 = dv1 % 11;
	    for(int i=0;i<10;i++)
	           dv2 += cpf[i] * i;
	    cpf[10] = dv2 = dv2 % 11;
	    if(dv1>9) cpf[9]=0;		if(dv2>9) cpf[10]=0;

	    if(Integer.parseInt(numero.substring(9,10))!= cpf[9]||
	    		Integer.parseInt(numero.substring(10,11))!=cpf[10])
	    	return false;
	    else
	    	return true;
	}

}
