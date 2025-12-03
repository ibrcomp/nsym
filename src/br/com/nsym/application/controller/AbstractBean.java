package br.com.nsym.application.controller;

import java.io.Serializable;

import javax.enterprise.inject.Default;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.omnifaces.util.Messages;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;

import br.com.nsym.application.component.Translator;
import br.com.nsym.application.component.chart.AbstractChartModel;
import br.com.nsym.application.producer.qualifier.AuthenticatedUser;
import br.com.nsym.domain.model.security.User;
import lombok.Getter;

/**
 * Bean utilizado como base para todos os outros beans da aplicacao. Nele esta
 * algumas funcionalidades base para que a pagina seja manipulada com mais faci-
 * lidade
 *
 * @author Ibrahim Yousef
 *
 * @version 1.0.0
 * @since 1.0.0, 15/10/2016
 */
public abstract class AbstractBean implements Serializable {

	@Getter
	protected ViewState viewState;

	@Inject
	protected Logger logger;

	@Inject
	@Default
	private Translator translator;

	@Inject
	private FacesContext facesContext;
	@Inject
	private RequestContext requestContext;
	
	@Getter
	@Inject
	@AuthenticatedUser
	private User usuarioAutenticado;


	/**
	 * @return o nome do componente default de mensagens da view
	 */
	public String getDefaultMessagesComponentId() {
		return "messages";
	}

	/**
	 * Caso o nome do componente default de mensagens tenha sido setado, este
	 * metodo invocado apos adicionar mensagens faz com que ele seja atualizado
	 * automaticamente
	 */
	private void updateDefaultMessages() {
		if (this.getDefaultMessagesComponentId() != null 
				&& !this.getDefaultMessagesComponentId().isEmpty()) {
			this.temporizeHiding(this.getDefaultMessagesComponentId());
		}
	}

	/**
	 * Traduz uma mensagem pelo bundle da aplicacao
	 * 
	 * @param message a chave da mensagem original
	 * @return o texto
	 */
	public String translate(String message) {
		return this.translator.translate(message);
	}

	/**
	 * Atualiza um componente pelo seu id no contexto atual
	 *
	 * @param componentId o id do componente
	 */
	protected void updateComponent(String componentId) {
		this.requestContext.update(componentId);
	}

	/**
	 * Executa um JavaScript na pagina pelo FacesContext atual
	 *
	 * @param script o script a ser executado
	 */
	protected void executeScript(String script) {
		this.requestContext.execute(script);
	}

	/**
	 * Apenas abre uma dialog pelo seu widgetvar
	 * 
	 * @param widgetVar o widgetvar para abri-la
	 */
	protected void openDialog(String widgetVar) {
		this.executeScript("PF('" + widgetVar + "').show()");
	}

	/**
	 * Dado o id de um dialog, atualiza a mesma e depois abre pelo widgetvar
	 * 
	 * @param id o id da dialog para atualiza-la
	 * @param widgetVar o widgetvar para abri-la
	 */
	protected void updateAndOpenDialog(String id, String widgetVar) {
		this.updateComponent(id);
		this.executeScript("PF('" + widgetVar + "').show()");
	}
	
	/**
	 * Fecha uma dialog aberta previamente
	 *
	 * @param widgetVar o widgetvar da dialog
	 */
	protected void closeDialog(String widgetVar) {
		this.executeScript("PF('" + widgetVar + "').hide()");
	}

	/**
	 * Dado um componente, atualiza o mesmo e depois temporiza o seu fechamento
	 * 
	 * @param componentId o id do componente
	 */
	protected void temporizeHiding(String componentId) {
		this.updateComponent(componentId);
		this.executeScript("setTimeout(\"$(\'#" + componentId + "\').slideUp(300)\", 8000)");
	}

	/**
	 * Redireciona o usuario para um determinada URL, caso haja um erro, loga 
	 * 
	 * @param url a url para o cara ser redirecionado
	 */
	protected void redirectTo(String url) {
		try {
			this.facesContext.getExternalContext().redirect(url);
		} catch (Exception ex) {
			throw new RuntimeException(
					String.format("Can't redirect to url [%s]", url));
		}
	}
	

	/**
	 * Metodo para desempacotar a pilha de excessoes a fim de que possamos
	 * tratar de forma mais elegante exceptions do tipo constraints violadas
	 *
	 * @param exception a exception que buscamos
	 * @param stack a stack
	 * @return se ela existe ou nao nao nesta stack
	 */
	public boolean containsException(Class<? extends Exception> exception, Throwable stack) {

		// se nao tem stack nao ha o que fazer!
		if (stack == null) return false;

		// navegamos recursivamente na stack
		if (stack.getClass().isAssignableFrom(exception)) {
			return true;
		} else {
			return this.containsException(exception, stack.getCause());
		}
	}

	/**
	 * Adiciona uma mensagem de informacao na tela
	 * 
	 * @param message a mensagem
	 * @param parameters os parametros da mensagem
	 * @param updateDefault se devemos ou nao atualizar o componente default
	 */
	public void addInfo(boolean updateDefault, String message, Object... parameters) {
		Messages.addInfo(null, this.translate(message), parameters);
		if (updateDefault) this.updateDefaultMessages();
	}

	/**
	 * Adiciona uma mensagem de erro na tela
	 * 
	 * @param message a mensagem
	 * @param parameters os parametros da mensagem
	 * @param updateDefault se devemos ou nao atualizar o componente default
	 */
	public void addError(boolean updateDefault, String message, Object... parameters) {
		Messages.addError(null, this.translate(message), parameters);
		if (updateDefault) this.updateDefaultMessages();
	}

	/**
	 * Adiciona uma mensagem de aviso na tela
	 * 
	 * @param message a mensagem
	 * @param parameters os parametros da mensagem
	 * @param updateDefault se devemos ou nao atualizar o componente default
	 */
	public void addWarning(boolean updateDefault, String message, Object... parameters) {
		Messages.addWarn(null, this.translate(message), parameters);
		if (updateDefault) this.updateDefaultMessages();
	}

	/**
	 * 
	 * @param canvas
	 * @param model 
	 */
	public void drawDonutChart(String canvas, AbstractChartModel model) {
		this.executeScript("drawDonutChart(" + model.toJson() + ", '"+ canvas + "')");
	}

	/**
	 * 
	 * @param canvas
	 * @param model 
	 */
	public void drawLineChart(String canvas, AbstractChartModel model) {
		this.executeScript("drawLineChart(" + model.toJson() + ", '"+ canvas + "')");
	}

	
	/**
	 * Pega o IP de internet do usuario (WAN) e disponibiliza para o sistema
	 */
	public String meuIP() {
		 HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();  
		    String ip = null;
		    
		    ip = request.getHeader("x-forwarded-for");
		    if (ip == null) {
		    	ip = request.getHeader("X_FORWARDED_FOR");
		        if (ip == null){
		        	ip = request.getRemoteAddr();
		        }
		    }  
		    
		    return ip;
	}

	

	/**
	 * Enum para controle do estado de execucao da tela
	 */
	protected enum ViewState {
		ADDING,
		LISTING,
		INSERTING,
		EDITING,
		DELETING,
		DETAILING;
	}
}
