package br.com.nsym.domain.misc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class LocalizaRegex {
	

	/**
	 * Localiza no texto se existe a palavra a ser encontrada
	 * @param texto
	 * @param regex
	 * @return boolean
	 */
 public boolean localizaPalavra(String texto , String regex){
	Pattern pattern = Pattern.compile(regex);
	Matcher matcher = pattern.matcher(texto);
	boolean resultado = false;
	int mIdx = 0;
	if (matcher.find()) {
	    System.out.println("Full match: " + matcher.group(0));
	    for (int i = 0; i < matcher.groupCount()+1; i++) {
	        System.out.println("Group " + mIdx + ": " + matcher.group(i));
	    }
	    mIdx++;
	    resultado = true;
	}
	return resultado;
 }
 
 
 /**
  * Retorna o numero de vezes que a pesquisa encontra o resultado
  * @param texto
  * @param regex
  * @return int
  */
 public int count(String texto,String regex){
	 Pattern pattern = Pattern.compile(regex);
	Matcher matcher = pattern.matcher(texto);
	int tot = 0;
	while (matcher.find()){
		for( int groupIdx = 0; groupIdx < matcher.groupCount()+1; groupIdx++ ){
	        System.out.println( "[" + tot + "][" + groupIdx + "] = " + matcher.group(groupIdx));
	      }
		tot++;
	}
	return tot;
 }
 
 public Matcher grupo(String texto,String regex){
	 Pattern pattern = Pattern.compile(regex);
	Matcher matcher = pattern.matcher(texto);
	while (matcher.find()){
		matcher.group();
	}
	return matcher;
 }
 
 /**
  * Método que retorna uma lista contendo o conteúdo entre o Delimitador que é passado no padrão REGEX
  * 
  * 
  * @param texto
  * @param regex - inicio do campo
  * @param regexFinal  - fim do campo
  * @return List<String>
  */
 public List<String> listaString (String texto,String regex){
	 	Pattern re = Pattern.compile(regex,Pattern.MULTILINE);
		Matcher m = re.matcher(texto);
		List<String> resultado = new ArrayList<>();
		int inicio = 0 ;
		while (m.find()){
			for( int groupIdx = 0; groupIdx < m.groupCount()+1; groupIdx++ ){
				System.out.println("grupo: " + texto.substring(inicio, m.start()));
				resultado.add(texto.substring(inicio, m.start()));
				inicio = m.end();
			}
		}
	return resultado;
}
 
 /**
  * Método que retorna uma lista contendo o conteúdo entre o Delimitador que é passado no padrão REGEX
  * 
  * 
  * @param texto
  * @param regex - inicio do campo
  * @param regexFinal  - fim do campo
  * @return List<String>
  */
 public List<String> listaString (String texto,String regex,String regexFinal){
	 	Pattern re = Pattern.compile(regex,Pattern.MULTILINE);
	 	Pattern fi =Pattern.compile(regexFinal,Pattern.MULTILINE);
		Matcher m = re.matcher(texto);
		Matcher f = fi.matcher(texto);
		List<String> resultado = new ArrayList<>();
		int inicio = 0 ;
		int fim = 0;
		while (m.find()) {
			inicio = m.start();
			f.find();
			fim = f.start();
			for( int groupIdx = 0; groupIdx < m.groupCount()+1; groupIdx++ ){
				System.out.println("grupo: " + texto.substring(inicio, fim));
				resultado.add(texto.substring(inicio,fim));
			}
		}
	return resultado;
 }
 
public HashMap<BigDecimal, BigDecimal> listaIndice (String texto,String regex){
	 	Pattern re = Pattern.compile(regex);
		Matcher m = re.matcher(texto);
		HashMap<BigDecimal, BigDecimal>resultado = new HashMap<BigDecimal, BigDecimal>();
		int posicao = 0;
		while (m.find()){
			for( int groupIdx = 0; groupIdx < m.groupCount()+1; groupIdx++ ){
				
				System.out.println("passando pelo listaIndice localiza " + posicao + " indice: " + m.start());
				resultado.put(new BigDecimal(posicao),new BigDecimal(m.start()));
				posicao++;
			}
		}
		System.out.println("Lista de inidices Tamanho: " +resultado.size());
	return resultado;
}
 
 public List<Map.Entry<BigDecimal, BigDecimal>> geraListaIndice(String arquivo,String regex){ 
		Set<Map.Entry<BigDecimal, BigDecimal>> formaDePagamentoSet = this.listaIndice(arquivo, regex).entrySet();
		return new ArrayList<Map.Entry<BigDecimal,BigDecimal>> (formaDePagamentoSet);
	}

}
