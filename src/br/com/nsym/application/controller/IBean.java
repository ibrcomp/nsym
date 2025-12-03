package br.com.nsym.application.controller;

/**
 * Interface que define os metodos minimos que uma Beam precisa para manipular os dados do repository corretamente
 *
 * @param <T> qualquer coisa que seja serializavel
 *
 * @author Ibrahim Yousef Quatani
 *
 * @version 1.0.0
 * @since 1.0.0, 02/01/2017
 */
public interface IBean <T>{

	/**
	 * 
	 *  setter para definir a empresa que o dado pertence
	 * 
	 * @param entity
	 * @return
	 */
		public T setIdEmpresa(Long idEmpresa);
		
	/**
	 * 
	 * setter para definir a filial que o dado pertence
	 * 
	 * @param entity
	 * @return
	 */
		
		public T setIdFilial(Long idFilial);
		
	/**
	 * Inicialização da pagina em modo listagem
	 */
		public void initializeListing();
		
	/**
	 * Inicialização da página em modo de Nova ou Edição
	 * @param id
	 */
		public void initializeForm(Long id);
		
}
