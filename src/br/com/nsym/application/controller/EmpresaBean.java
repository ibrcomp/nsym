package br.com.nsym.application.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.SortOrder;

import br.com.nsym.application.component.Translator;
import br.com.nsym.application.component.table.AbstractLazyModel;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.misc.ImportaCliente;
import br.com.nsym.domain.misc.ImportaCliente.ClienteTemp;
import br.com.nsym.domain.misc.ReceitaFinder;
import br.com.nsym.domain.model.entity.cadastro.Cliente;
import br.com.nsym.domain.model.entity.cadastro.Contato;
import br.com.nsym.domain.model.entity.cadastro.Email;
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.EndComplemento;
import br.com.nsym.domain.model.entity.cadastro.Endereco;
import br.com.nsym.domain.model.entity.cadastro.Filial;
import br.com.nsym.domain.model.entity.cadastro.Fone;
import br.com.nsym.domain.model.entity.cadastro.Pais;
import br.com.nsym.domain.model.entity.cadastro.TipoCadastro;
import br.com.nsym.domain.model.entity.cadastro.tools.VersaoSat;
import br.com.nsym.domain.model.entity.fiscal.Tributos;
import br.com.nsym.domain.model.entity.tools.Enquadramento;
import br.com.nsym.domain.model.entity.tools.ReceitaFederalConsulta;
import br.com.nsym.domain.model.entity.tools.UfSigla;
import br.com.nsym.domain.model.repository.cadastro.ContatoRepository;
import br.com.nsym.domain.model.repository.cadastro.EmailRepository;
import br.com.nsym.domain.model.repository.cadastro.EmpresaRepository;
import br.com.nsym.domain.model.repository.cadastro.EndComplementoRepository;
import br.com.nsym.domain.model.repository.cadastro.EnderecoRepository;
import br.com.nsym.domain.model.repository.cadastro.FoneRepository;
import br.com.nsym.domain.model.repository.cadastro.PaisRepository;
import br.com.nsym.domain.model.repository.fiscal.TributosRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped

public class EmpresaBean extends AbstractBeanEmpDS<Empresa>{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private Empresa empresa = new Empresa();
	
	@Getter
	@Setter
	private Long idEmpresaSelecionada;

	@Getter
	@Setter
	private String cnpj;

	@Getter
	@Setter
	private ReceitaFederalConsulta receita = new ReceitaFederalConsulta();

	@Getter
	@Setter
	private Empresa empresaReceita = new Empresa();

	@Getter
	@Setter
	private EndComplemento endComplemento = new EndComplemento();

	@Inject
	private EndComplementoRepository endComplementoDao;

	@Inject
	private FoneRepository foneDao;

	@Getter
	@Setter
	private String cep;

	@Getter
	@Setter
	private String estado;

	@Getter
	@Setter
	private String logra;

	@Getter
	@Setter
	private String munic;

	@Getter
	@Setter
	private Endereco endereco = new Endereco();


	@Inject
	private EnderecoRepository enderecoDao;

	@Inject
	@Getter
	private Translator tradutor;

	@Inject
	private EmpresaRepository empresaDAO;

	@Setter
	private List<Empresa> empresas = new ArrayList<>();

	@Getter
	@Setter
	private Boolean consultaPorCepVisivel = true;

	@Setter
	private List<Fone> fonesEmpresa = new ArrayList<>();

	@Getter
	@Setter
	private boolean listaPreenchida = false;

	@Getter
	@Setter
	private boolean visivelPorIdCliente = false;

	@Getter
	@Setter
	private boolean consultaReceitaOk= false;

	@Getter
	@Setter
	private Long idEndereco;

	@Getter
	private List<Endereco> listaEncontrada =new ArrayList<>(); 

	@Getter
	private AbstractLazyModel<Empresa> empresaModel;

	@Getter
	private AbstractLazyModel<Contato> contatoModel;

	@Getter
	@Setter
	private Fone fone = new Fone();

	@Setter
	private List<Fone> listaFone;

	@Getter
	@Setter
	private Contato contato = new Contato();

	@Getter
	@Setter
	private String nomeContato;

	@Setter
	private List<Contato> listaContato ;

	@Inject
	private ContatoRepository contatoDao;

	@Getter
	@Setter
	private Email email = new Email();

	@Setter
	private List<Email> listaEmail;

	@Inject
	private EmailRepository emailDao;

	@Getter
	private List<Empresa> consultaEmpresas;

	@Getter
	@Setter
	private boolean isDeleted = false;

	@Getter
	@Setter
	private String valorCaptcha;

	@Getter
	@Setter
	private String valorCnpj;

	@Getter
	@Setter
	private HtmlImage image;

//	@Getter
//	WebClient webClient = new WebClient(BrowserVersion.getDefault());

	@Getter
	@Setter
	private HtmlPage paginaInicial;

	@Getter
	@Setter
	private File captchaImg;

	@Getter
	@Setter
	private boolean exibeImagem = false ;

	@Inject
	ReceitaFinder pesquisaReceita;
	
	@Getter
	@Setter
	private BigDecimal reducaoBaseIcms;
	
	@Getter
	@Setter
	private BigDecimal aliqAprovIcms;
	
	@Getter
	@Setter
	private String emailStr;
	
	@Getter
	@Setter
	private Email emailNfe= new Email();

	@Getter
	@Setter
	private Pais pais;

	@Inject
	private PaisRepository paisDao;
	
	@Inject
	private ImportaCliente importa;
	
	@Getter
	@Setter
	private String nomeArquivo;
	
	@Getter
	@Setter
	private transient List<ClienteTemp> listaClienteImp = new ArrayList<>();
	
	@Getter
	@Setter
	private Cliente clienteImp = new Cliente();
	
	@Inject
	private TributosRepository tributoDao;
	
	@Getter
	private List<Tributos> listaTributosAtivos = new ArrayList<>();
	@Getter
	@Setter
	private String respostaAcbrLocal;
	
	@Getter
	@Setter
	private UfSigla ufConsulta;

	@PostConstruct
	public void init(){
		empresaModel = getLazyEmpresa();
	}

	public AbstractLazyModel<Empresa> getLazyEmpresa(){
		this.empresaModel = new AbstractLazyModel<Empresa>() {
			/**
			 *
			 */
			private static final long serialVersionUID = -5372717092176446171L;

			@Override
			public List<Empresa> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no empresaModel");
				final PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				final Page<Empresa> page = empresaDAO.listByStatus(isDeleted, null, null,pageRequest);

				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return empresaModel;
	}

	public void telaPesquisaEndereco() {
		this.openDialog("dialogEndereco");
	}

	public void telaTelefone(){
		this.openDialog("dialogTelefone");
	}

	public void telaReceitaFederal(){
		this.updateAndOpenDialog("receitaFederalDialog","dialogReceitaFederal");
	}

	public void telaResultadoReceitaFederal() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
//		consultaCnpjCCC();
		//		resultadoReceita();
		this.updateAndOpenDialog("resultadoReceitaFederalDialog","dialogResultadoReceitaFederal");
	}

	/**
	 * 
	 * @param idEmpresa
	 */
	@Transactional
	public void initializeForm(Long idEmpresa) {
		if ((idEmpresa == null) && (idEmpresa == null)) {
			this.viewState = ViewState.ADDING;
			this.empresa = new Empresa();
			this.reducaoBaseIcms = new BigDecimal("0");
			this.aliqAprovIcms = new BigDecimal("0");
			this.emailNfe= new Email();
			this.listaTributosAtivos = new ArrayList<Tributos>();
		} else {
			this.viewState = ViewState.EDITING;
			setVisivelPorIdCliente(true);
			this.empresa = this.empresaDAO.findById(idEmpresa, false);
			this.listaTributosAtivos = this.tributoDao.listaTodosTributosAtivos(this.empresa.getId(), null);
			this.aliqAprovIcms = this.empresa.getAliqArpoveitaIcms();
			this.reducaoBaseIcms = this.empresa.getReduzBaseIcms();
			if (this.empresa.getEndereco() != null){
				this.endereco = enderecoDao.listCep(empresa.getEndereco().getEndereco().getCep());
				this.endComplemento = endComplementoDao.pegaEndComplementoPorEmpresa(empresa);
				if (this.endComplemento.getLogradouro() == null || this.endComplemento.getLogradouro().isEmpty()) {
					this.endComplemento.setLogradouro(this.endereco.getLogra());
					this.endComplemento.setBairro(this.endereco.getBairro());
				}else {
					this.endereco.setBairro(this.endComplemento.getBairro());
					this.endereco.setLogra(this.endComplemento.getLogradouro());
				}
			}
			if (this.empresa.getEmailNFE() != null){
				this.emailNfe = this.emailDao.pegaEmailNfe(null, null, this.empresa,null ,null,null);
				this.emailStr = this.emailNfe.getEmail();
			}
		}
	}

	/**
	 * 
	 */
	public void initializeListing() {
		this.viewState = ViewState.LISTING;
	}
	
	public void ativaEmpresa(){
    	if (this.idEmpresaSelecionada != null){
    	this.getUsuarioAutenticado().setIdEmpresa(this.idEmpresaSelecionada);
    	}else{
    		System.out.println("erro empresaADM nulo");
    	}
    }

	public Long pegaIdEmpresa() {
		final Long id = getUsuarioAutenticado().getIdEmpresa();
		if (id == null) {
			this.addError(true, "user.logon.withoutcompany");
		}
		return id;
	}

	public List<Filial> filiais() {
		return empresaDAO.listFiliaisPorEmpresa(pegaIdEmpresa());
	}

	@Override
	public Empresa setIdEmpresa(Long idEmpresa) {
		return null;
	}

	@Override
	public Empresa setIdFilial(Long idFilial) {
		return null;
	}

	@Transactional
	public void doSalvar() {
		try{
			boolean ie = verificaIe();
			if (this.empresa.getId() == null){
				boolean cnpjInvalido = empresaDAO.procuraCnpj(empresa.getCnpj(),getUsuarioAutenticado().getIdEmpresa());

				System.out.println("1- estou dentro do empresaID = null" + " cnpjInvalido = " + cnpjInvalido + "inscri��o v�lido = "+ verificaIe());
				if (cnpjInvalido == false && ie){
					this.empresa.setDeleted(false);
					this.empresa.setEstado(endereco.getUf());
					this.empresa.setAliqArpoveitaIcms(this.aliqAprovIcms);
					this.empresa.setReduzBaseIcms(this.reducaoBaseIcms);
					this.pais = this.paisDao.listaPaises("BRASIL");
					this.empresa.setPais(this.pais);
					this.empresaDAO.save(this.empresa);
					this.empresa = empresaDAO.localizaPorCnpj(this.empresa.getCnpj(),getUsuarioAutenticado().getIdEmpresa());
					System.out.println("1-1 "+ empresa.getId() + "id da empresa dentro do salvar Novo CNPJ id vazio");
					setVisivelPorIdCliente(true);

				}else{
					if (cnpjInvalido== true){
						this.addError(true, "cnpj.error");
						return;
					}else{
						this.addError(true, "ie.error");
						return;
					}
				}
				if (this.empresa.getId() != null && this.endereco.getLogra() != null){
					System.out.println("2- estou dentro do empresaid diferente de null salvando o endComplemento "+ this.empresa.getId());
					this.endereco = enderecoDao.listCep(this.endereco.getCep());
					this.endComplemento.setDeleted(false);
					this.endComplemento.setLogradouro(this.endereco.getLogra());
					this.endComplemento.setBairro(this.endereco.getBairro());
					this.endComplemento.setEmpresa(empresa);
					this.endComplemento.setEndereco(endereco);
					this.emailNfe.setEmail(this.emailStr);
					this.emailNfe.setEmpresa(this.empresa);
					this.emailNfe.setDeleted(false);
					this.emailDao.save(this.emailNfe);
					this.endComplemento = this.endComplementoDao.save(endComplemento);
					this.emailNfe = emailDao.pegaEmailNfe(null,null,this.empresa,null, null,pegaIdEmpresa());
					System.out.println("passei pelo save endComplemento");
					//					this.endComplemento = endComplementoDao.pegaEndComplementoPorEmpresa(this.endComplemento.getEmpresa());
				}
				if (this.endComplemento.getId() != null){
					System.out.println("3- Estou dentro do endComplementoID diferente de null atualizando a empresa com o ID do endComplemento " + endComplemento.getId() );
					this.endComplemento.setLogradouro(this.endereco.getLogra());
					this.endComplemento.setBairro(this.endereco.getBairro());
					this.empresa.setEndereco(this.endComplemento);
					this.empresaDAO.save(this.empresa);
//					alteraEnderecoCep();
				}
				this.addInfo(true, "save.sucess");
			}else { // la�o do ATUALIZA 
				if (ie){
					this.emailNfe.setEmail(this.emailStr);
					this.emailNfe.setEmpresa(this.empresa);
					this.empresa.setEmailNFE(this.emailNfe);
					this.empresa.setAliqArpoveitaIcms(this.aliqAprovIcms);
					this.empresa.setReduzBaseIcms(this.reducaoBaseIcms);
					this.empresaDAO.update(this.empresa, getUsuarioAutenticado().getName(), new Date());
//					alteraEnderecoCep();
					this.endComplemento.setLogradouro(this.endereco.getLogra());
					this.endComplemento.setBairro(this.endereco.getBairro());
					this.endComplemento.setEmpresa(this.empresa);
					this.endComplemento.setEndereco(this.endereco);
					this.endComplemento = this.endComplementoDao.save(this.endComplemento);
					this.addInfo(true, "save.update");
				}
			}
		} catch (IllegalArgumentException e) {
			System.out.println("grava��o cancelada " + e);
		}
	}


	public boolean verificaIe(){
		try {
			boolean valida;
			System.out.println(endereco.getUf().name());
			if (endereco.getUf() != null){
				valida = validaIE(empresa.getInscEstadual(),endereco.getUf().name());
				System.out.println(" ie do estado " + endereco.getUf() + " foi validado com " + valida);
				if (valida == false && empresa.getInscEstadual() == null ){
					return true;
				}else {
					return valida;
				}
			}else  {
				return true;
			}
		} catch (Exception e) {
			if (empresa.getInscEstadual() == null){
				return true;
			}else{
				return false;
			}
		}
	}

	@Transactional
	public String doExcluir() {
		try {
			this.empresa.setDeleted(true);
			empresaDAO.save(this.empresa);
			return toListEmpresa();
		} catch (IllegalArgumentException e) {
			System.out.println("N�o foi possivel excluir o registro " + e);
			return null;
		}
	}

	public String newEmpresa() {
		return "formCadEmpresa.xhtml?faces-redirect=true";
	}

	public String toListEmpresa() {
		return "formListEmpresa.xhtml?faces-redirect=true";
	}
	/*
	 * redireciona para a pagina com o ID da empresa a ser editada
	 * 
	 * @param empresaID
	 * 
	 * @return
	 */

	public String changeToEdit(Long empresaID) {
		System.out.println();
		return "formCadEmpresa.xhtml?faces-redirect=true&empresaID=" + empresaID+"&tipoCadastro="+TipoCadastro.EMP.name();
	}

//	public String changeToEditCliente(Long clienteID) {
//		return "formCadCliente.xhtml?faces-redirect=true&clienteID="+clienteID;
//	}

	
	/**
	 * @return a lista de tipos validos para Enquadramento
	 */
	public Enquadramento[] getEnquadramentoType() {
		return Enquadramento.values();
	}
	
	/**
	 * @return a lista de tipos validos para Versões de layout disponives para SAT
	 */
	public VersaoSat[] getVersaoSatType() {
		return VersaoSat.values();
	}
	
	public List<Empresa> getEmpresas() {
		if (this.empresas.isEmpty() == true){
			return this.empresas = empresaDAO.listEmpresaAtiva();
		}else{
			return this.empresas;
		}
	}


	public String getIe() {
		return empresa.getInscEstadual();
	}


	public String recuperaEndereco(){
		this.endereco = pegaEnderecoNaSessao();
		return "PF('dialogEndereco').hide()";
	}
	public void onRowSelect(SelectEvent event)throws IOException{
		FacesMessage msg = new FacesMessage("Empresa "+((Empresa) event.getObject()).getRazaoSocial()+" selecionada");  
		FacesContext.getCurrentInstance().addMessage(null, msg);
		this.empresa = (Empresa) event.getObject();
		//		this.endereco =enderecoDao.findById(empresa.getEndereco().getEndereco().getId(),false);
		//	this.endereco = enderecoDao.listCep(empresa.getEndereco().getEndereco().getCep());
		//	this.endComplemento = endComplementoDao.pegaEndComplementoPorEmpresa(empresa);
		setVisivelPorIdCliente(true);
		System.out.println("Estou o RowSelect");
		this.viewState = ViewState.EDITING;
		changeToEdit(empresa.getId());
	}
	public void limpaFormulario(){
		this.empresa = new Empresa();
		this.endereco = new Endereco();
		this.cep = "";
	}

	public List<Endereco> localizaEndereco(){
		try{
			return	this.listaEncontrada = enderecoDao.procuraCepWeb(estado,munic,logra);
		}catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	@Transactional
	public void localizaCep(){
		try {
			if (cep.isEmpty() != true){
				System.out.println("estou indo para o DAO fazer a pesquisa de CEP no nosso Banco!");
				this.endereco = enderecoDao.listCep(cep);
				System.out.println("a pesquisa retornou "+ endereco.getLogra());
				insereEnderecoNaSessao(this.endereco);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	@Transactional
	public void saveEnd(Endereco end) {
		Endereco resultado = new Endereco();
		resultado.setBairro(end.getBairro());
		resultado.setLogra(end.getLogra());
		resultado.setCep(end.getCep());
		resultado.setUf(end.getUf());
		resultado.setIbge(end.getIbge());
		resultado.setLocalidade(end.getLocalidade());
		System.out.println(resultado.getLogra());
		this.endereco = enderecoDao.save(resultado);
	}
	
	@Transactional
	public void onRowEnderecoSelect(SelectEvent event)throws IOException{
		this.endereco = ((Endereco) event.getObject());
		cep = this.endereco.getCep();
		System.out.println(this.endereco.toString());
		saveEnd(this.endereco);
	}

	@Transactional
	public void doSalvaContato(){
		try{
			System.out.println("estou dentro do try" + this.empresa.getId());
			if (this.empresa.getId() != null){
				System.out.println("estou entro do if empresa antes do contato getID"+ contato.getId());
				if (contato.getId() == null){

					System.out.println("estou no if contato.getID antes do setNome");
					this.contato.setNome(nomeContato);
					this.contato.setEmpresa(getEmpresa());
					contatoDao.save(contato);
				}else{
					System.out.println("estou no else do empresa getID");
					if (getFone() != null){
						System.out.println("estou no getFone != null no else do contato.getID");
						this.fone.setContato(getContato());
						foneDao.save(fone);
					}
					if (getEmail() != null){
						this.email.setContato(getContato());
						emailDao.save(email);
					}
				}
				this.addInfo(true, "save.sucess");
			}else{
				this.addError(true, "save.error");
				return;
			}

		}catch (Exception e){
			this.addError(true, "save.error", e);
		}
	}

	public List<Empresa> setConsultaEmpresas(){
		return getEmpresaModel().getModelSource();
	}

	public List<Empresa> pegaListaEmpresa(){
		return empresaDAO.listEmpresaAtiva();
	}

	public void criaExcel(){
		postProcessXLS(pegaListaEmpresa());
	}

	/**
	 * Criar um documento em excel personalizado.
	 *  @param document
	 */
	public void postProcessXLS(Object document) {
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow header = sheet.getRow(0);
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setFillForegroundColor(HSSFColorPredefined.GREEN.getIndex());
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		for(int i=0; i < header.getPhysicalNumberOfCells();i++) {
			HSSFCell cell = header.getCell(i);
			cell.setCellStyle(cellStyle);
		}
	}
	/**
	 * Faz a consulta do cnpj junto o governo e retorna os dados
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws FailingHttpStatusCodeException 
	 */
	public void getResultadoCnpj() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException{
		String resultado;		
		System.out.println("sigla" + ufConsulta);
		resultado = pesquisaReceita.consultaCadSintegra(pegaConexao(),this.valorCnpj, getUfConsulta());
		this.respostaAcbrLocal = resultado;
		consultaCnpjCCC();
		telaResultadoReceitaFederal();
		
	}
//	public void resultadoReceita() throws FailingHttpStatusCodeException, IOException, IOException{
//
//		HtmlForm form = paginaInicial.getFormByName("frmConsulta");
//		HtmlTextInput  cnpj = form.getInputByName("cnpj");
//		HtmlTextInput captcha = form.getInputByName("txtTexto_captcha_serpro_gov_br");
//
//		cnpj.setText(valorCnpj);
//		System.out.println("inseri o valor do cnpj na pagina " + cnpj.getText());
//		captcha.setText(valorCaptcha);
//		System.out.println("inseri o valor do Captcha " + captcha.getText());
//		HtmlSubmitInput button = form.getInputByName("submit1");
//		System.out.println("Estou antes do htmlPage page2");
//		HtmlPage page2 = button.click();
//
//		// Metodo para caputar o resultado e transformar em uma classe 
//		HtmlPage table = page2.getPage(); 
//		HtmlBold  receitaCNPJ = table.getFirstByXPath("//*[@id='principal']/table[2]/tbody/tr/td/table[2]/tbody/tr/td[1]/font[2]/b[1]"); //xpath do cnpj
//		if (receitaCNPJ != null){
//			setConsultaReceitaOk(true);	
//		}
//		receita.setReceitaCNPJ(receitaCNPJ.getTextContent());
//		System.out.println(receita.getReceitaCNPJ()+ " Estou aqui!!!" + consultaReceitaOk);
//
//		HtmlBold  receitaRazao = table.getFirstByXPath("//*[@id='principal']/table[2]/tbody/tr/td/table[3]/tbody/tr/td/font[2]/b"); // xpath Razao Social
//		receita.setReceitaRazao(receitaRazao.getTextContent());
//		System.out.println(receita.getReceitaRazao() + " Estou aqui!!!");
//
//		HtmlBold  receitaFantasia = table.getFirstByXPath("//*[@id='principal']/table[2]/tbody/tr/td/table[4]/tbody/tr/td/font[2]/b"); //xpath nome Fantasia
//		receita.setReceitaFantasia(receitaFantasia.getTextContent());
//		System.out.println(receitaFantasia.getTextContent() + " Estou aqui!!!");
//
//		HtmlBold  receitaLogradouro= table.getFirstByXPath("//*[@id='principal']/table[2]/tbody/tr/td/table[8]/tbody/tr/td[1]/font[2]/b"); // logradouro
//		receita.setReceitaLogradouro(receitaLogradouro.getTextContent());
//		System.out.println(receitaLogradouro.getTextContent() + " Estou aqui!!!");
//
//		HtmlBold  receitaNumero = table.getFirstByXPath("//*[@id='principal']/table[2]/tbody/tr/td/table[8]/tbody/tr/td[3]/font[2]/b"); // numero
//		receita.setReceitaNumero(receitaNumero.getTextContent());
//		System.out.println(receitaNumero.getTextContent() + " Estou aqui!!!");
//
//		HtmlBold  receitaComplemento = table.getFirstByXPath("//*[@id='principal']/table[2]/tbody/tr/td/table[8]/tbody/tr/td[5]/font[2]/b"); // complemento
//		receita.setReceitaComplemento(receitaComplemento.getTextContent());
//		System.out.println(receitaComplemento.getTextContent() + " Estou aqui!!!");
//
//		HtmlBold  receitaCep = table.getFirstByXPath("//*[@id='principal']/table[2]/tbody/tr/td/table[9]/tbody/tr/td[1]/font[2]/b");// cep
//		receita.setReceitaCep(receitaCep.getTextContent());
//		System.out.println(receitaCep.getTextContent() + " Estou aqui!!!");
//
//		HtmlBold  receitaBairro = table.getFirstByXPath("//*[@id='principal']/table[2]/tbody/tr/td/table[9]/tbody/tr/td[3]/font[2]/b"); // bairro
//		receita.setReceitaBairro(receitaBairro.getTextContent());
//		System.out.println(receitaBairro.getTextContent() + " Estou aqui!!!");
//
//		HtmlBold  receitaMunicipio = table.getFirstByXPath("//*[@id='principal']/table[2]/tbody/tr/td/table[9]/tbody/tr/td[5]/font[2]/b"); // municipio
//		receita.setReceitaMunicipio(receitaMunicipio.getTextContent());
//		System.out.println(receitaMunicipio.getTextContent() + " Estou aqui!!!");
//
//		HtmlBold  receitaUF = table.getFirstByXPath("//*[@id='principal']/table[2]/tbody/tr/td/table[9]/tbody/tr/td[7]/font[2]/b"); // uf
//		receita.setReceitaUF(receitaUF.getTextContent());
//		System.out.println(receitaUF.getTextContent() + " Estou aqui!!!");
//
//		HtmlBold  receitaSituacaoCadastral = table.getFirstByXPath("//*[@id='principal']/table[2]/tbody/tr/td/table[12]/tbody/tr/td[1]/font[2]/b"); // situacao Cadastral
//		receita.setReceitaSituacaoCadastral(receitaSituacaoCadastral.getTextContent());
//		System.out.println(receitaSituacaoCadastral.getTextContent() + " Estou aqui!!!");
//
//		HtmlBold  receitaCnaePrincipal = table.getFirstByXPath("//*[@id='principal']/table[2]/tbody/tr/td/table[5]/tbody/tr/td/font[2]/b"); //xpath Cnae Principal
//		receita.setReceitaCnaePrincipal(receitaCnaePrincipal.getTextContent());
//		System.out.println(receitaCnaePrincipal.getTextContent() + " Estou aqui!!!");
//
//		HtmlBold  receitaCnaeSecundario = table.getFirstByXPath("//*[@id='principal']/table[2]/tbody/tr/td/table[6]/tbody/tr/td/font[2]/b"); //xpath Cnae Secund�rio
//		receita.setReceitaCnaeSecundario(receitaCnaeSecundario.getTextContent());
//		System.out.println(receitaCnaeSecundario.getTextContent() + " Estou aqui!!!");
//
//		HtmlBold  receitaDataAbertura = table.getFirstByXPath("//*[@id='principal']/table[2]/tbody/tr/td/table[2]/tbody/tr/td[3]/font[2]/b"); // xpath data Abertura
//		receita.setReceitaDataAbertura(receitaDataAbertura.getTextContent());
//		System.out.println(receitaDataAbertura.getTextContent() + " Estou aqui!!!");
//
//		webClient.close();
//	}

	@Transactional
	public void consultaCnpjCCC() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		try{

//			this.receita = pesquisaReceita.procuraReceita(valorCnpj);
			this.receita = pesquisaReceita.retornoConsultaSintegra(respostaAcbrLocal);
			System.out.println(this.receita.getReceitaCNPJ()+ " resultado de RECEITA com o cnpj preenchido");
			if (receita.getReceitaCNPJ() != null){
				setConsultaReceitaOk(true);

				System.out.println(receita.getReceitaIE()+" 1");
				System.out.println(receita.getReceitaRazao()+" 2");
				System.out.println(receita.getReceitaCep()+" 3");

				System.out.println(receita.getReceitaFantasia()+" 4");
				System.out.println(receita.getReceitaCnaePrincipal()+" 5");
				System.out.println(receita.getReceitaDataAbertura()+" 6");

				System.out.println(receita.getReceitaLogradouro()+" 7");
				System.out.println(receita.getReceitaRegime()+" 8");
				System.out.println(receita.getReceitaComplemento()+" 9");

				System.out.println(receita.getReceitaNumero()+" 10");

				System.out.println(receita.getReceitaSituacaoCadastral()+" 11");
			}
		}catch (Exception e ){
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Cnpj n�o informado!", null);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	/**
	 * 	Pesquisa o site da receita federal que contem um captcha exibe para o usuario que digita os valores e continua a pesquisa!
	 * @throws IOException
	 * @throws InterruptedException
	 */

//	@Transactional
//	public void receitaFederal() throws IOException, InterruptedException{
//
//		try{
//			webClient = new WebClient(BrowserVersion.getDefault());
//			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
//			webClient.getOptions().setThrowExceptionOnScriptError(false);
//			webClient.getOptions().setJavaScriptEnabled(true);
//			webClient.getOptions().setUseInsecureSSL(true); 
//
//			webClient.getOptions().setTimeout(8000);
//
//			webClient.setRefreshHandler(new ThreadedRefreshHandler());        
//			HtmlPage startPage = webClient.getPage("https://www.receita.fazenda.gov.br/pessoajuridica/cnpj/cnpjreva/cnpjreva_solicitacao2.asp");
//			webClient.waitForBackgroundJavaScript(5000);
//			String text = startPage.getTextContent();
//			System.out.println("Page Content: "+ text);
//
//			List<?> cells = startPage.getElementsByIdAndOrName("imgCaptcha");
//
//
//			image = (HtmlImage)cells.get(0);        
//
//			File file = new File("C:\\ibrcomp\\captcha");
//			file.mkdir();
//			captchaImg = new File(file, "captcha.jpg");
//			image.saveAs(captchaImg);
//
//			Thread.sleep(2000);
//
//			paginaInicial = startPage.getPage();
//
//			setExibeImagem(true);
//			telaReceitaFederal();
//		}catch (Exception e){
//			this.addError(true, "receita.error.off", e);
//		}
//
//	}

	/**
	 * Converte a consulta realizada para uma entity do sistema.
	 */

	@Transactional
	public void transfereConsultaReceita(){
		empresa.setCnpj(receita.getReceitaCNPJ());
		empresa.setRazaoSocial(receita.getReceitaRazao());
		empresa.setNomeFantasia(receita.getReceitaFantasia());
		empresa.setInscEstadual(receita.getReceitaIE());
		if (!receita.getReceitaCep().equalsIgnoreCase("00000-000")){
			endereco = enderecoDao.listCep(receita.getReceitaCep());
			endComplemento.setComplemento(receita.getReceitaComplemento());
			endComplemento.setNumero(receita.getReceitaNumero());
			System.out.println(receita.getReceitaRegime().toUpperCase()+ " tamanho do campo: " + receita.getReceitaRegime().length());
		}
		if (receita.getReceitaRegime().equalsIgnoreCase("NORMAL - REGIME PERIÓDICO DE APURAÇÃO") || receita.getReceitaRegime().equalsIgnoreCase("NORMAL")){
			empresa.setEnquadramento(Enquadramento.Normal);
		}else {
			empresa.setEnquadramento(Enquadramento.SimplesNacional);
			System.out.println(empresa.getEnquadramento().toString());
		}

	}
	@Transactional
	public void alteraEnderecoCep(){
		try{
			Endereco enderecoTemporario = new Endereco();
			enderecoTemporario = enderecoDao.listCep(endereco.getCep());
			enderecoTemporario.setLogra(endereco.getLogra());
			enderecoDao.save(enderecoTemporario);
		}catch (IllegalArgumentException e){
			this.addError(true, "save.error",e);		}

	}
	
	public void onRowSelectImpCliente(SelectEvent event)throws IOException{
		this.addInfo(true, "Cliente Selecionado");
		this.clienteImp = (Cliente) event.getObject();
		setVisivelPorIdCliente(true);
		this.viewState = ViewState.EDITING;
	}
	
	@Transactional
	public void importaDados() throws FileNotFoundException  {
			System.out.println("Estou antes do leArquivo");
			System.out.println(this.nomeArquivo);
			this.listaClienteImp = importa.leArquivo(this.nomeArquivo,this.empresa);
	}
	
	@Transactional
	public void armazenaDados() {
		System.out.println("Gravando os dados");
		importa.gravaListaCliente(this.listaClienteImp, getUsuarioAutenticado().getIdEmpresa(), null);
	}
	
	public void filtraCep() {
		System.out.println("Filtrando ceps");
		importa.filtraCepEGrava(this.listaClienteImp);
	}
}   
