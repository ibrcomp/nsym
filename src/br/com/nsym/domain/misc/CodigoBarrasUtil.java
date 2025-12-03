package br.com.nsym.domain.misc;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class CodigoBarrasUtil {

	/**
	 * Método que retorna o digito Verificador
	 * @return Digito tipo int 
	 */

	public int retornaDigito(String barras){
		if (!barras.matches("^[0-9]{12}$")) {
			throw new IllegalArgumentException("Código precisa ser númerico e ter 12 dígitos");

		}else{
			System.out.println("barras = " + barras);
			int[] numeros = barras.chars().map(Character::getNumericValue).toArray();
			for (int i : numeros) {
				System.out.println("array: " + i);
			}
			int somaPares = numeros[1] + numeros[3] + numeros[5] + numeros[7] + numeros[9] + numeros[11];
			System.out.println("soma pares : " + somaPares);
			int somaImpares = numeros[0] + numeros[2] + numeros[4] + numeros[6] + numeros[8] + numeros[10];
			System.out.println("soma impares: " + somaImpares);
			int resultado = somaImpares + (somaPares * 3);
			System.out.println("resultado impar + par * 3 = " + resultado);
			int resto = resultado % 10 ;
			int digitoVerificador = 0;
			if (resto != 0 ){
				digitoVerificador = 10 - resto;
			}
			System.out.println("digito Verificador : " + digitoVerificador);
			return digitoVerificador;
		}
	}
	/**
	 * Método que retorno o código de barras com o digito verificado correto
	 * @param String(Código de barras)
	 * @return String 
	 */
	public String retornaBarrasComDigitoValido(String barras){
		String numeroValido="";
		if (!barras.matches("^[0-9]{12,20}$")) {
			throw new IllegalArgumentException("Código precisa ser númerico e ter entre 12 dígitos e 20 dígitos");

		}else{
			if (barras.matches("^[0-9]{12,13}$")) {
				System.out.println("barras = " + barras);
				int[] numeros = barras.chars().map(Character::getNumericValue).toArray();
				for (int i : numeros) {
					System.out.println("array: " + i);
				}
				int somaPares = numeros[1] + numeros[3] + numeros[5] + numeros[7] + numeros[9] + numeros[11];
				System.out.println("soma pares : " + somaPares);
				int somaImpares = numeros[0] + numeros[2] + numeros[4] + numeros[6] + numeros[8] + numeros[10];
				System.out.println("soma impares: " + somaImpares);
				int resultado = somaImpares + (somaPares * 3);
				System.out.println("resultado impar + par * 3 = " + resultado);
				int resto = resultado % 10 ;
				int digitoVerificador = 0;
				if (resto != 0 ){
					digitoVerificador = 10 - resto;
				}
				System.out.println("digito Verificador : " + digitoVerificador);
				for (int b = 0; 13 > b ; b++){
					if (b == 12){
						numeroValido = numeroValido+ digitoVerificador;
					}else{
						numeroValido = numeroValido+ numeros[b];
					}
				}
				System.out.println("numero valido :" + numeroValido);
				return numeroValido;
			}else {
				return barras;
			}
		}
	}

	public boolean validaDigito(String barras){
		if (!barras.matches("^[0-9]{13}$")) {
			//			throw new IllegalArgumentException("Código precisa ser númerico e ter 13 dígitos");
			return false;
		}else{

			int[] numeros = barras.chars().map(Character::getNumericValue).toArray();
			int somaPares = numeros[1] + numeros[3] + numeros[5] + numeros[7] + numeros[9] + numeros[11];
			int somaImpares = numeros[0] + numeros[2] + numeros[4] + numeros[6] + numeros[8] + numeros[10];
			int resultado = somaImpares + (somaPares * 3);
			int resto = resultado % 10 ;
			int digitoVerificador = 0;
			if (resto != 0 ){
				digitoVerificador = 10 - resto;
			}
			return digitoVerificador == numeros[12];
		}
	}
	/**
	 * valida se a String passada possui 12 ou 13 digitos numéricos
	 * @param barras
	 * @return boolean
	 */
	public boolean validaCodigo(String barras){
		if (!barras.matches("^[0-9]{12,20}$")) {
			return false;
		}else{
			return true;
		}
	}
	/**
	 * valida se é numérico
	 * @param codigo
	 * @return boolean
	 */
	public boolean isNumerico(String codigo){
		if (codigo.matches("\\d+")){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * Verifica e Ean não é nulo e se é maior que 5 retornando true
	 * @param ean
	 * @return boolean
	 */
	public boolean barrasValido(String ean){
		boolean valido = false;
		if (ean != null){
			if (ean.length() > 5 ){
					return true;
			}
		}
		return valido;
	}
	/**
	 * Metodo que remove qualquer coisa que nao seja número
	 * @param codigo
	 * @return String
	 */
	public String somenteNumerico(String codigo){
		return codigo = codigo.replaceAll("[^0-9]+", "");
	}

	/**
	 * Metodo que gera um codigo Ean baseado em um numero fornecido
	 * @param codigo
	 * @return Ean valido
	 */
	public String geradorEan(String codigo){
		String ean="";
		String completa="";
		codigo = somenteNumerico(codigo);
		if (isNumerico(codigo)){
			if (codigo.length() == 12){
				ean = retornaBarrasComDigitoValido(codigo);
				return ean;
			}else if (codigo.length() > 12){
				throw new IllegalArgumentException("Não foi possível gerar automáticamente o Código de Barras");
			}else{
				int tamanho = codigo.length();
				int resta = 11-(tamanho);
				for (int i = 0; resta > i ; i++){
					completa = completa + "0";
				}
				ean = "7"+completa+codigo;
				ean = retornaBarrasComDigitoValido(ean);
				return ean;
			}
		}else{
			return null;
		}
	}

	/**
	 * Metodo que ao receber um codigo aplica uma serie de regras para validar o codigo retornando true
	 * (verifica se é apeneas numeros, tamanho minimo 12-13 dig. / digito / codigo)
	 * @param codigo
	 * @return booelan
	 */

	public boolean requisitosEan(String codigo){
		boolean resultado = false;
		resultado = barrasValido(codigo);
		if (resultado){
			if (validaCodigo(codigo) && validaDigito(codigo)){
				resultado = true;
			}else{
				resultado =  false;
			}
		}

		return resultado; 
	}

}
