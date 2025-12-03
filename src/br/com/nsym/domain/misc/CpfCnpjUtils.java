package br.com.nsym.domain.misc;

public class CpfCnpjUtils {
    private static final int[] pesoCPF = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] pesoCNPJ = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    public static boolean isValid(String cpfCnpj) {
        return (isValidCPF(cpfCnpj) || isValidCNPJ(cpfCnpj));
    }
    
    public static String acrescentaZeros(String str,String pessoa) {
    	String resultado= str;
    	int tamanho = 0;
    	int falta = 0;
    	tamanho = str.trim().length();
    	if (pessoa.contentEquals("F")) {
    		falta = 11 - tamanho;
    		if (falta == 0 ) {
    			resultado = str;
    		}else {
    			for (int i = 0;falta > i ; i++ ) {
    				resultado = "0" + resultado;
    			}
    		}
    	}else {
    		if (pessoa.contentEquals("J")) {
    			falta = 14 - tamanho;
    			if (falta == 0 ) {
    				resultado = str;
    			}else {
    				for (int i = 0;falta > i ; i++ ) {
    					resultado = "0" + resultado;
    				}
    			}
    		}
    	}
    	return resultado;
    }

    private static int calcularDigito(String str, int[] peso) {
        int soma = 0;
        for (int indice=str.length()-1, digito; indice >= 0; indice-- ) {
            digito = Integer.parseInt(str.substring(indice,indice+1));
            soma += digito*peso[peso.length-str.length()+indice];
        }
        soma = 11 - soma % 11;
        return soma > 9 ? 0 : soma;
    }

    private static String padLeft(String text, char character) {
        return String.format("%11s", text).replace(' ', character);
    }

    private static boolean isValidCPF(String cpf) {
        cpf = cpf.trim().replace(".", "").replace("-", "");
        if ((cpf==null) || (cpf.length()!=11)) return false;

        for (int j = 0; j < 10; j++)
            if (padLeft(Integer.toString(j), Character.forDigit(j, 10)).equals(cpf))
                return false;

        Integer digito1 = calcularDigito(cpf.substring(0,9), pesoCPF);
        Integer digito2 = calcularDigito(cpf.substring(0,9) + digito1, pesoCPF);
        return cpf.equals(cpf.substring(0,9) + digito1.toString() + digito2.toString());
    }

    private static boolean isValidCNPJ(String cnpj) {
        cnpj = cnpj.trim().replace(".", "").replace("-", "").replace("/", "");
        if ((cnpj==null)||(cnpj.length()!=14)) return false;

        Integer digito1 = calcularDigito(cnpj.substring(0,12), pesoCNPJ);
        Integer digito2 = calcularDigito(cnpj.substring(0,12) + digito1, pesoCNPJ);
        return cnpj.equals(cnpj.substring(0,12) + digito1.toString() + digito2.toString());
    }
    
    /*
     * Método que retorna 1 para CNPJ  2 para CPF e 0 para inválido
     */
    public static int isCpfOrCnpjOrNull(String doc) {
    	doc = doc.trim().replace(".", "").replace("-", "").replace("/", "");
    	if (doc.length()== 14 ){
    		return 1;
    	}else if (doc.length() == 11) {
    		return 2;
    	}else {
    		return 0;
    	}
    }
    
    /*
     * Método que retira os caracteres Especias  . - / de uma String 
     */
    public static String retiraCaracteresEspeciais(String doc) {
    	doc = doc.trim().replace(".", "").replace("-", "").replace("/", "");
    		return doc;
    }
    
    /*
     * Método que adiciona os caracteres Especiais no CNPJ ou CPF
     */
    
    public static String adcionaCaracteresEspeciais(String doc) {
		StringBuilder stringBuilder = new StringBuilder(doc.trim());
    	if (stringBuilder.length()== 14 ){
			stringBuilder.insert(doc.length() - 2 , "-");
			stringBuilder.insert(doc.length() - 6 , "/");
			stringBuilder.insert(doc.length() - 9 , ".");
			stringBuilder.insert(doc.length() - 12 , ".");
    	}else if (stringBuilder.length() == 11) {
			stringBuilder.insert(doc.length() - 2 , "-");
			stringBuilder.insert(doc.length() - 5 , ".");
			stringBuilder.insert(doc.length() - 8 , ".");
    	}
		return stringBuilder.toString();
    }
    
}