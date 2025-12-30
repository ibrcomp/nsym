package br.com.nsym.application.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.Optional;
import java.util.function.Function;

import javax.enterprise.inject.Default;
import javax.faces.application.ProjectStage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.omnifaces.util.Messages;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;

import br.com.caelum.stella.validation.ie.IEAcreValidator;
import br.com.caelum.stella.validation.ie.IEAlagoasValidator;
import br.com.caelum.stella.validation.ie.IEAmapaValidator;
import br.com.caelum.stella.validation.ie.IEAmazonasValidator;
import br.com.caelum.stella.validation.ie.IEBahiaValidator;
import br.com.caelum.stella.validation.ie.IECearaValidator;
import br.com.caelum.stella.validation.ie.IEDistritoFederalValidator;
import br.com.caelum.stella.validation.ie.IEEspiritoSantoValidator;
import br.com.caelum.stella.validation.ie.IEGoiasValidator;
import br.com.caelum.stella.validation.ie.IEMaranhaoValidator;
import br.com.caelum.stella.validation.ie.IEMatoGrossoDoSulValidator;
import br.com.caelum.stella.validation.ie.IEMatoGrossoValidator;
import br.com.caelum.stella.validation.ie.IEMinasGeraisValidator;
import br.com.caelum.stella.validation.ie.IEParaValidator;
import br.com.caelum.stella.validation.ie.IEParaibaValidator;
import br.com.caelum.stella.validation.ie.IEParanaValidator;
import br.com.caelum.stella.validation.ie.IEPernambucoValidator;
import br.com.caelum.stella.validation.ie.IEPiauiValidator;
import br.com.caelum.stella.validation.ie.IERioDeJaneiroValidator;
import br.com.caelum.stella.validation.ie.IERioGrandeDoNorteValidator;
import br.com.caelum.stella.validation.ie.IERioGrandeDoSulValidator;
import br.com.caelum.stella.validation.ie.IERondoniaValidator;
import br.com.caelum.stella.validation.ie.IERoraimaValidator;
import br.com.caelum.stella.validation.ie.IESantaCatarinaValidator;
import br.com.caelum.stella.validation.ie.IESaoPauloValidator;
import br.com.caelum.stella.validation.ie.IESergipeValidator;
import br.com.caelum.stella.validation.ie.IETocantinsValidator;
import br.com.nsym.application.channels.DadosDeConexaoSocket;
import br.com.nsym.application.component.Translator;
import br.com.nsym.application.component.chart.AbstractChartModel;
import br.com.nsym.application.producer.qualifier.AuthenticatedUser;
import br.com.nsym.domain.model.entity.cadastro.Cliente;
import br.com.nsym.domain.model.entity.cadastro.Colaborador;
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.Endereco;
import br.com.nsym.domain.model.entity.cadastro.Filial;
import br.com.nsym.domain.model.entity.cadastro.Fornecedor;
import br.com.nsym.domain.model.entity.cadastro.Transportadora;
import br.com.nsym.domain.model.entity.tools.TipoCliente;
import br.com.nsym.domain.model.entity.tools.Uf;
import br.com.nsym.domain.model.entity.tools.UfSigla;
import br.com.nsym.domain.model.repository.cadastro.EmpresaRepository;
import br.com.nsym.domain.model.repository.cadastro.FilialRepository;
import br.com.nsym.domain.model.security.User;
import br.com.nsym.infraestrutura.configuration.ApplicationUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Bean utilizado como base para todos os outros beans da aplicacao. Nele esta
 * algumas funcionalidades base para que a pagina seja manipulada com mais faci-
 * lidade
 *
 * @author Ibrahim Yousef
 *
 * @version 1.2.0
 * @since 1.0.0, 18/01/2015
 */
public abstract class AbstractBeanEmpDS<T> implements IBean<T>,Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -5636145025146338280L;

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
	
	@Inject
	private EmpresaRepository empresaDao;
	
	@Inject
	private FilialRepository filialDao;
	
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
		if (getDefaultMessagesComponentId() != null 
				&& !this.getDefaultMessagesComponentId().isEmpty()) {
			this.temporizeHiding(this.getDefaultMessagesComponentId());
		}
	}
	/**
	 * Caso o nome do componente default de mensagens tenha sido setado, este
	 * metodo invocado apos adicionar mensagens faz com que ele seja atualizado
	 * automaticamente
	 */
	private void updateMessages(String componentId) {
			this.temporizeHiding(componentId);
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
		this.executeScript("PF('" + widgetVar + "').show();");
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
		this.executeScript("PF('" + widgetVar + "').hide();");
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
	
	/** Remove caracteres especiais da string
	 * 
	 * @param str
	 * @return
	 */
	
	public static String removerAcentos(String str) {
		if (str == null){
			return " ";
		}else{
			return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
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
	 * Adiciona uma mensagem de informacao na tela
	 * 
	 * @param message a mensagem
	 * @param parameters os parametros da mensagem
	 * @param updateDefault se devemos ou nao atualizar o componente default
	 */
	public void addInfoNew(boolean updateDefault,String componente ,String message, Object... parameters) {
		Messages.addInfo(null, this.translate(message), parameters);
		if (updateDefault) this.updateMessages(componente);
	}
	
	/**
	 * Adiciona uma mensagem de erro na tela informando o componente de exibiï¿½ï¿½o
	 * 
	 * @param message a mensagem
	 * @param parameters os parametros da mensagem
	 * @param updateDefault se devemos ou nao atualizar o componente default
	 * @param componente id do componente
	 */
	public void addErrorNew(boolean updateDefault,String componente,String message, Object... parameters) {
		Messages.addError(null, this.translate(message), parameters);
		if (updateDefault) this.updateMessages(componente);
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
	 * Adiciona uma mensagem de aviso na tela
	 * 
	 * @param message a mensagem
	 * @param parameters os parametros da mensagem
	 * @param updateDefault se devemos ou nao atualizar o componente default
	 */
	public void addWarningNew(boolean updateDefault,String componente, String message, Object... parameters) {
		Messages.addWarn(null, this.translate(message), parameters);
		if (updateDefault)  this.updateMessages(componente);
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
	 * Executa uma  regra para saber a porcentagem de um valor
	 * sobre o outro
	 * 
	 * @param x o x da parada
	 * @param total o total que seria o 100%
	 * 
	 * @return a porcentagem
	 */
	protected int percentageOf(BigDecimal x, BigDecimal total) {

		// escala o X para nao haver erros de comparacao
		x = x.setScale(2, RoundingMode.HALF_UP);

		// se um dos dois valores for null retorna 0 de cara
		if (x == null || total == null) {
			return 0;
		}

		BigDecimal percentage;

		if (x.compareTo(total) >= 0) {
			return 100;
		} else {
			percentage = x.multiply(new BigDecimal(100))
					.divide(total, 2, RoundingMode.HALF_UP);
		}

		return percentage.intValue() > 100 ? 100 : percentage.intValue();
	}
	
	
	
	/**
	 * Pega o Endereï¿½o salvo no sessao
	 * @return Endereco
	 */
	public Endereco pegaEnderecoNaSessao() {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		if (session.getAttribute("ENDERECO") != null){
			return (Endereco) session.getAttribute("ENDERECO");
		}else {
			return null;
		}
	}
	/*
	 * Pega empresa na sessao
	 * @return Empresa
	 */
	public Empresa pegaIdEmpresaNaSessao(){
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		if (session.getAttribute("empresa") != null){
			return (Empresa) session.getAttribute("empresa");
		}else {
			return null;
		}
	}
	
	/*
	 * Pega Colaborador na sessao
	 * @return Colaborador
	 */
	public Colaborador pegaColaboradorNaSessao(){
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		if (session.getAttribute("colaborador") != null){
			return (Colaborador) session.getAttribute("colaborador");
		}else {
			return null;
		}
	}
	/*
	 * Pega filial na sessao
	 * @return Filial
	 */
	public Filial pegaFilialNaSessao(){
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		if (session.getAttribute("filial") != null){
			return (Filial) session.getAttribute("filial");
		}else {
			return null;
		}
	}
	
	/*
	 * Pega Cliente na sessao
	 * @return Filial
	 */
	public Cliente pegaClienteNaSessao(){
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		if (session.getAttribute("cliente") != null){
			return (Cliente) session.getAttribute("cliente");
		}else {
			return null;
		}
		
	}
	
	/*
	 * Pega Cliente na sessao
	 * @return Filial
	 */
	public Transportadora pegaTransportadoraNaSessao(){
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		if (session.getAttribute("transportadora") != null){
			return (Transportadora) session.getAttribute("transportadora");
		}else {
			return null;
		}
		
	}
	/*
	 * Pega Fornecedor na sessao
	 * @return Fornecedor
	 */
	
	public Fornecedor pegaFornecedorNaSessao(){
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		if (session.getAttribute("fornecedor") != null){
			return (Fornecedor) session.getAttribute("fornecedor");
		}else {
			return null;
		}
		
	}
	
	/**
	 * Limpa variaveis da sessao
	 * @param endereco
	 */
	public void limpaSessaoParcial(){
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		session.removeAttribute("cliente");
		session.removeAttribute("colaborador");
		session.removeAttribute("transportadora");
	}

	public void limpaSessao(){
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		session.removeAttribute("empresa");
		session.removeAttribute("filial");
		session.removeAttribute("cliente");
		session.removeAttribute("colaborador");
		session.removeAttribute("transportadora");
	}
	
	public void insereEnderecoNaSessao(Endereco endereco){
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		session.setAttribute("ENDERECO", endereco);

	}

	/**
	 * @return a lista de tipos validos para Enquadramento
	 */
	public UfSigla[] getUfSiglaType() {
		return UfSigla.values();
	}
	/**
	 * @return a lista de tipos validos para Tipo de Cliente
	 */
	public TipoCliente[] getListTipoCliente() {
		return TipoCliente.values();
	}
	
	/**
	 * @return a lista de Uf  
	 */
	public Uf[] getListaUf(){
		return Uf.values();
	}
	
	/**
	 * @return o ID da empresa do usuï¿½rio autenticado na sessï¿½o
	 * 
	 */
	public Long pegaIdEmpresa() {
		Long id = getUsuarioAutenticado().getIdEmpresa();
		return id;
	}
	
	/**
	 * @return o ID da filial do usuï¿½rio autenticado na sessï¿½o
	 * 
	 */
	public Long pegaIdFilial() {
		Long id = getUsuarioAutenticado().getIdFilial();
		return id;
	}
	
	/**
	 * Pega as informaï¿½ï¿½es da empresa / filial para conexao com acbr
	 * @return a conexao preenchida
	 */
	public  DadosDeConexaoSocket pegaConexao(){
		DadosDeConexaoSocket conexao; 
		Empresa emp = new Empresa();
		Filial fil = new Filial();
		if (this.getUsuarioAutenticado().getIdFilial() == null){
			if (this.getUsuarioAutenticado().getIdEmpresa() == null) {
				if (ApplicationUtils.isStageRunning(ProjectStage.Production)){
					System.out.println("Stagio ProduÃ§Ã£o");
					conexao = new DadosDeConexaoSocket("ibrcomp.no-ip.org",3434);
				}else {
					System.out.println("Stagio Teste");
					conexao = new DadosDeConexaoSocket("127.0.0.1",3434);
				}
			}else {
				emp = this.empresaDao.findById(this.getUsuarioAutenticado().getIdEmpresa(), false);
			}
			if (emp.getIpAcbr() == null) {
				conexao = new DadosDeConexaoSocket(meuIP().trim(),emp.getPortaAcbr());
			}else {
				conexao = new DadosDeConexaoSocket(emp.getIpAcbr().trim(),emp.getPortaAcbr());
			}
		}else{
			fil = this.filialDao.findById(this.getUsuarioAutenticado().getIdFilial(), false);
			if (fil.getIpAcbr() == null) {
				conexao = new DadosDeConexaoSocket(meuIP().trim(),fil.getPortaAcbr());
			}else {
				conexao = new DadosDeConexaoSocket(fil.getIpAcbr().trim(),fil.getPortaAcbr());
			}
		}
		return conexao;
	}
	
	public  DadosDeConexaoSocket pegaConexaoNFce(){
		DadosDeConexaoSocket conexao; 
		Empresa emp = new Empresa();
		Filial fil = new Filial();
		if (this.getUsuarioAutenticado().getIdFilial() == null){
			if (this.getUsuarioAutenticado().getIdEmpresa() == null) {
				if (ApplicationUtils.isStageRunning(ProjectStage.Production)){
					System.out.println("Stagio Producao");
					conexao = new DadosDeConexaoSocket("ibrcomp.no-ip.org",3434);
				}else {
					System.out.println("Stagio Teste");
					conexao = new DadosDeConexaoSocket("127.0.0.1",3434);
				}
			}else {
				emp = this.empresaDao.findById(this.getUsuarioAutenticado().getIdEmpresa(), false);
			}
			if (emp.getIpAcbr() == null) {
				conexao = new DadosDeConexaoSocket(meuIP().trim(),emp.getPortaAcbr(),emp.getCsc(),emp.getIdToken());
			}else {
				conexao = new DadosDeConexaoSocket(emp.getIpAcbr().trim(),emp.getPortaAcbr(),emp.getCsc(),emp.getIdToken());
			}
		}else{
			fil = this.filialDao.findById(this.getUsuarioAutenticado().getIdFilial(), false);
			if (fil.getIpAcbr() == null) {
				conexao = new DadosDeConexaoSocket(meuIP().trim(),fil.getPortaAcbr(),fil.getCsc(),fil.getIdToken());
			}else {
				conexao = new DadosDeConexaoSocket(fil.getIpAcbr().trim(),fil.getPortaAcbr(),fil.getCsc(),fil.getIdToken());
			}
		}
		return conexao;
	}
	
	public EmpUser configEmpUser() {
		EmpUser emp = new EmpUser();
		if (this.usuarioAutenticado.getIdFilial() != null) {
			emp.setFil(this.filialDao.findById(this.usuarioAutenticado.getIdFilial(), false));
			emp.setEmp(this.empresaDao.findById(this.usuarioAutenticado.getIdEmpresa(), false));
		}else {
			emp.setEmp(this.empresaDao.findById(this.usuarioAutenticado.getIdEmpresa(), false));
		}
		return emp;
	}
	
	@ToString
	@EqualsAndHashCode
	public static class EmpUser{
		
		@Getter
		@Setter
		private Empresa emp ;
		@Getter
		@Setter
		private Filial fil ;
		
	}
	
	/*
	 * Retorna a informaçao que desejar da Empresa ou Filial
	 */
	public static <T> T campoEmpUser(EmpUser u,
			Function<Filial, T> daFilial,
			Function<Empresa, T> daEmpresa) {
		return Optional.ofNullable(u.getFil()).map(daFilial)
				.orElseGet(() -> Optional.ofNullable(u.getEmp()).map(daEmpresa).orElse(null));
	}

	/*
	 * Converte nome completo do estado em sigla
	 * @param nome
	 */
	public String nomeEstadoPorSigla(String nome) {

		switch (nome) {
		case "Acre": return "AC";
		case "Alagoas" : return "AL";
		case "Amapá": return "AP";
		case "Amazonas": return "AM";
		case "Bahia" : return "BA";
		case "Ceará": return "CE" ;
		case "Distrito Federal" : return "DF";
		case "Espirito Santo" : return "ES";
		case "Goiás": return "GO";
		case "Maranhão": return "MA";
		case "Mato Grosso": return "MT";
		case "Mato Grosso do Sul": return "MS";
		case "Minas Gerais": return "MG";
		case "Pará": return "PA";
		case "Paraíba": return "PB";
		case "Paraná": return "PR";
		case "Pernambuco": return "PE";
		case "Piauí": return "PI";
		case "Rio de Janeiro": return "RJ";
		case "Rio Grande do Norte": return "RN";
		case "Rio Grande do Sul": return "RS";
		case "Rondônia": return "RO";
		case "Roraima": return "RR";
		case "Santa Catarina": return "SC";
		case "São Paulo": return "SP";
		case "Sergipe": return "SE";
		case "Tocantins": return "TO";
		default: return "nulo";
		}
	}
	
	
	/*
	 * Converte a sigla do estado para nome completo
	 * @param UF
	 */

	public String siglaEstadoPorNome(String uf){

		switch (uf) {
		case "AC": return "Acre";
		case "AL": return "Alagoas";
		case "AP": return "Amapá";
		case "AM": return "Amazonas";
		case "BA": return "Bahia";
		case "CE": return "Ceará¡";
		case "DF": return "Distrito Federal";
		case "ES": return "Espírito Santo";
		case "GO": return "Goiás";
		case "MA": return "Maranhão";
		case "MT": return "Mato Grosso";
		case "MS": return "Mato Grosso do Sul";
		case "MG": return "Minas Gerais";
		case "PA": return "Pará";
		case "PB": return "Paraíba";
		case "PR": return "ParanÃ¡";
		case "PE": return "Pernambuco";
		case "PI": return "Piauí";
		case "RJ": return "Rio de Janeiro";
		case "RN": return "Rio Grande do Norte";
		case "RS": return "Rio Grande do Sul";
		case "RO": return "Rondônia";
		case "RR": return "Roraima";
		case "SC": return "Santa Catarina";
		case "SP": return "São Paulo";
		case "SE": return "Sergipe";
		case "TO": return "Tocantins";
		default: return "Desconhecido";
		}
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
		PRINTING,
		DISABLED,
		DETAILING;
	}
	public  boolean validaIE(String ie, String uf) {
		try {
			if (ie.isEmpty() == false ){
				if (uf.equals("AC")) {
					new IEAcreValidator(false).assertValid(ie);
				}
				if (uf.equals("AL")) {
					new IEAlagoasValidator(false).assertValid(ie);
				}
				if (uf.equals("AP")) {
					new IEAmapaValidator(false).assertValid(ie);
				}
				if (uf.equals("AM")) {
					new IEAmazonasValidator(false).assertValid(ie);
				}
				if (uf.equals("BA")) {
					new IEBahiaValidator(false).assertValid(ie);
				}
				if (uf.equals("CE")) {
					new IECearaValidator(false).assertValid(ie);
				}
				if (uf.equals("DF")) {
					new IEDistritoFederalValidator(false).assertValid(ie);
				}
				if (uf.equals("ES")) {
					new IEEspiritoSantoValidator(false).assertValid(ie);
				}
				if (uf.equals("GO")) {
					new IEGoiasValidator(false).assertValid(ie);
				}
				if (uf.equals("MA")) {
					new IEMaranhaoValidator(false).assertValid(ie);
				}
				if (uf.equals("MS")) {
					new IEMatoGrossoDoSulValidator(false).assertValid(ie);
				}
				if (uf.equals("MT")) {
					new IEMatoGrossoValidator(false).assertValid(ie);
				}
				if (uf.equals("MG")) {
					new IEMinasGeraisValidator(false).assertValid(ie);
				}
				if (uf.equals("PA")) {
					new IEParaValidator(false).assertValid(ie);
				}
				if (uf.equals("PB")) {
					new IEParaibaValidator(false).assertValid(ie);
				}         
				if (uf.equals("PR")) {
					new IEParanaValidator(false).assertValid(ie);
				}
				if (uf.equals("PE")) {
					new IEPernambucoValidator(false).assertValid(ie);
				}
				if (uf.equals("PI")) {
					new IEPiauiValidator(false).assertValid(ie);
				}
				if (uf.equals("RJ")) {
					new IERioDeJaneiroValidator(false).assertValid(ie);
				}
				if (uf.equals("RN")) {
					new IERioGrandeDoNorteValidator(false).assertValid(ie);
				}
				if (uf.equals("RS")) {
					new IERioGrandeDoSulValidator(false).assertValid(ie);
				}
				if (uf.equals("RO")) {
					new IERondoniaValidator(false).assertValid(ie);
				}
				if (uf.equals("RR")) {
					new IERoraimaValidator(false).assertValid(ie);
				}
				if (uf.equals("SC")) {
					new IESantaCatarinaValidator(false).assertValid(ie);
				}
				if (uf.equals("SP")) {
					new IESaoPauloValidator(false).assertValid(ie);
				}
				if (uf.equals("SE")) {
					new IESergipeValidator(false).assertValid(ie);
				}
				if (uf.equals("TO")) {
					new IETocantinsValidator(false).assertValid(ie);
				}
				return true;
			}else {
				return true;
			}
		} catch (Exception  e) {
			if (ie.isEmpty() == true){
			return true;
			}else {
				addError(true,"ie.error",e.getClass());
				return false;
			}
		}
	}
	
	public boolean validaGoias (String ie) {
		boolean resultado = false;
		int digito = 0;
		if (ie == null) {
			resultado = true;
		}else {
			String digitoIE = ie.substring(ie.length()-1,ie.length());
			System.out.println("digito IE = " + digitoIE);
			if (ie.isEmpty() == false) {
				if (ie.length()== 9 ) {
					digito = calculaDigito(ie);
					System.out.println("DIGITO calculado = "  + digito);
					if (digito == 0 || digito == 1 ) {
						digito = 0;
					}else {
						digito = 11 - digito;
					}
					System.out.println("digito = " + digito);
					if(new BigDecimal(digitoIE).compareTo(new BigDecimal(digito)) == 0) {
						resultado  = true;
						System.out.println("digitoIE = " + digitoIE + "calculo digito = " + digito);
					}
				}else {
					addError(true,"ie.error");
				}
			}
		}
		return resultado;
	}
	
	public boolean validaRS (String ie) {
		boolean resultado = false;
		int digito = 0;
		if (ie == null) {
			resultado = true;
		}else {
			String digitoIE = ie.substring(ie.length()-1,ie.length());
			System.out.println("digito IE = " + digitoIE);
			if (ie.isEmpty() == false) {
				if (ie.length()== 10 ) {
					digito = calculaDigitoRS(ie);
					logger.info("DIGITO calculado = "  + digito);
					if (digito == 0 || digito == 1 ) {
						digito = 0;
					}else {
						digito = 11 - digito;
					}
					logger.info("digito = " + digito);
					if(new BigDecimal(digitoIE).compareTo(new BigDecimal(digito)) == 0) {
						resultado  = true;
						logger.info("digitoIE = " + digitoIE + "calculo digito = " + digito);
					}
				}else {
					addError(true,"ie.error"," digito calculado = " + digito);
				}
			}
		}
		return resultado;
	}

	public int calculaDigito(String ie) {
		String ieSemDigito = ie.substring(0, ie.length()-1);
		char[] caracteres = ieSemDigito.toCharArray();
		BigDecimal total = new BigDecimal("0");
		BigDecimal resto = new BigDecimal("0");
		
		int peso = 9;
		for (char c : caracteres) {
			int i  = Character.digit(c,10);
			total = (new BigDecimal(peso).multiply(new BigDecimal(i))).add(total);
			System.out.println(" caracter: " + i + "x peso: "+ peso + " = total: " + total);
			peso--;
		}
		resto = total.remainder(new BigDecimal("11"));
		System.out.println("TOTAL= " + total +" / 11  resto = " + resto );
		return resto.intValue();
	}
	
	public int calculaDigitoRS(String ie) {
		String ieSemDigito = ie.substring(0, ie.length()-1);
		char[] caracteres = ieSemDigito.toCharArray();
		BigDecimal total = new BigDecimal("0");
		BigDecimal resto = new BigDecimal("0");
		int peso = 9;
		int pesoIni = 2;
		int contagem=1;
		for (char c : caracteres) {
			int i  = Character.digit(c,10);
			if (contagem == 1) {
				total = (new BigDecimal(pesoIni).multiply(new BigDecimal(i))).add(total);
			}else {
				total = (new BigDecimal(peso).multiply(new BigDecimal(i))).add(total);
				peso--;
			}
			logger.info(" caracter: " + i + "x peso: "+ peso + " = total: " + total);
			contagem++;
		}
		resto = total.remainder(new BigDecimal("11"));
		
		logger.info("TOTAL= " + total +" / 11  resto = " + resto );
		return resto.intValue();
	}
}
