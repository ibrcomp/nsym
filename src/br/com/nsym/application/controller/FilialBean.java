package br.com.nsym.application.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.ArrayList;
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
import br.com.nsym.domain.misc.ReceitaFinder;
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
import br.com.nsym.domain.model.repository.cadastro.FilialRepository;
import br.com.nsym.domain.model.repository.cadastro.FoneRepository;
import br.com.nsym.domain.model.repository.cadastro.PaisRepository;
import br.com.nsym.domain.model.repository.fiscal.TributosRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped

public class FilialBean extends AbstractBeanEmpDS<Filial>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter

	private Filial filial = new Filial();
	
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
	private EmpresaRepository empresaDao;

	@Inject
	@Getter
	private Translator tradutor;

	@Inject
	private FilialRepository filialDao;

	@Setter
	private List<Filial> filiais = new ArrayList<>();
	
	@Setter
	private List<Empresa> empresas = new ArrayList<>();

	@Getter
	@Setter
	private Boolean consultaPorCepVisivel = true;

	@Setter
	private List<Fone> fonesfilial = new ArrayList<>();

	@Getter
	@Setter
	private boolean listaPreenchida = false;

	@Getter
	@Setter
	private boolean visivelPorIdCliente = false;

	@Getter
	@Setter
	private Long idEndereco;

	@Getter
	private List<Endereco> listaEncontrada =new ArrayList<>(); 

	@Getter
	private AbstractLazyModel<Filial> filiaisModel;

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
	private List<Filial> consultaFiliais;

	@Getter
	@Setter
	private boolean isDeleted = false;

	@Inject
	private ReceitaFinder pesquisaReceita;	
	
	@Getter
	@Setter
	private ReceitaFederalConsulta receita = new ReceitaFederalConsulta();
	
	@Getter
	@Setter
	private boolean consultaReceitaOk= false;
	
	@Getter
	@Setter
	private String valorCnpj;
	
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
		filiaisModel = getLazyfilial();
	}
	
public AbstractLazyModel<Filial> getLazyfilial(){
		this.filiaisModel = new AbstractLazyModel<Filial>() {
			/**
			 *
			 */
			private static final long serialVersionUID = 2090969253321226394L;

			@Override
			public List<Filial> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no filiaisModel");
				final PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				final Page<Filial> page = filialDao.listByStatus(isDeleted, null,null, pageRequest);

				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return filiaisModel;
}

	public void telaPesquisaEndereco() {
		this.openDialog("dialogEndereco");
	}

	public void telaTelefone(){
		this.openDialog("dialogContatoFilial");
	}

	/**
	 * 
	 * @param idfilial
	 */
	@Transactional
	public void initializeForm(Long idfilial) {
		if ((idfilial == null) && (idfilial == null)) {
			this.viewState = ViewState.ADDING;
			this.filial = new Filial();
			this.emailNfe= new Email();
			this.reducaoBaseIcms = new BigDecimal("0");
			this.aliqAprovIcms = new BigDecimal("0");
			this.listaTributosAtivos = new ArrayList<Tributos>();
		} else {
			this.viewState = ViewState.EDITING;
			setVisivelPorIdCliente(true);
			this.filial = this.filialDao.findById(idfilial, false);
			this.listaTributosAtivos = this.tributoDao.listaTodosTributosAtivos(this.filial.getEmpresa().getId(),this.filial.getId() );
			this.aliqAprovIcms = this.filial.getAliqArpoveitaIcms();
			this.reducaoBaseIcms = this.filial.getReduzBaseIcms();
			if (this.filial.getEndereco() != null){
				this.endereco = enderecoDao.listCep(filial.getEndereco().getEndereco().getCep());
				this.endComplemento = endComplementoDao.pegaEndComplementoPorFilial(filial);
				if (this.endComplemento.getLogradouro() == null || this.endComplemento.getLogradouro().isEmpty()) {
					this.endComplemento.setLogradouro(this.endereco.getLogra());
					this.endComplemento.setBairro(this.endereco.getBairro());
				}else {
					this.endereco.setBairro(this.endComplemento.getBairro());
					this.endereco.setLogra(this.endComplemento.getLogradouro());
				}
			}
			if (this.filial.getEmailNFE() != null){
				this.emailNfe = this.emailDao.pegaEmailNfe(null, null, null,this.filial,null, null);
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

	public Long pegaIdEmpresa() {
		final Long id = getUsuarioAutenticado().getIdEmpresa();
		if (id == null) {
			FacesMessage msg = new FacesMessage("O Usu�rio " + getUsuarioAutenticado().getName()
					+ " Logado n�o possue filial vinculada, vincule uma filial primeiro!");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		return id;
	}



	@Override
	public Filial setIdFilial(Long idFilial) {
		return null;
	}

	@Transactional
	public void doSalvar() {
		try{
			final boolean ie = verificaIe();
			if (filial.getId() == null){
				final boolean cnpjInvalido = filialDao.procuraCnpj(filial.getCnpj(),getUsuarioAutenticado().getIdEmpresa());

				System.out.println("1- estou dentro do filialID = null" + " cnpjInvalido = " + cnpjInvalido + "inscri��o v�lido = "+ verificaIe());
				if (cnpjInvalido == false && ie){
					filial.setDeleted(false);
					filial.setEstado(endereco.getUf());
					this.filial.setAliqArpoveitaIcms(this.aliqAprovIcms);
					this.filial.setReduzBaseIcms(this.reducaoBaseIcms);
					this.pais = this.paisDao.listaPaises("BRASIL");
					this.filial.setPais(this.pais);
					filialDao.save(filial);
					this.filial = filialDao.localizaPorCnpj(filial.getCnpj(),getUsuarioAutenticado().getIdEmpresa());
					System.out.println("1-1 "+ filial.getId() + "id da filial dentro do salvar Novo CNPJ id vazio");
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
				if (filial.getId() != null && endereco.getLogra() != null){
					System.out.println("2- estou dentro do filialid diferente de null salvando o endComplemento "+ filial.getId());
					this.endereco = enderecoDao.listCep(this.endereco.getCep());
					endComplemento.setDeleted(false);
					this.endComplemento.setLogradouro(this.endereco.getLogra());
					this.endComplemento.setBairro(this.endereco.getBairro());
					endComplemento.setFilial(filial);
					endComplemento.setEndereco(endereco);
					this.emailNfe.setEmail(this.emailStr);
					this.emailNfe.setFilial(this.filial);
					this.emailNfe.setDeleted(false);
					this.emailDao.save(this.emailNfe);
					this.endComplemento= this.endComplementoDao.save(endComplemento);
					this.emailNfe = emailDao.pegaEmailNfe(null,null,null,this.filial,null,pegaIdEmpresa());
				}
				if (endComplemento.getId() != null){
					System.out.println("3- Estou dentro do endComplementoID diferente de null atualizando a filial com o ID do endComplemento " + endComplemento.getId() );
					this.endComplemento.setLogradouro(this.endereco.getLogra());
					this.endComplemento.setBairro(this.endereco.getBairro());
					this.filial.setEndereco(endComplemento);
					this.filialDao.save(filial);
//					alteraEnderecoCep();
				}
				this.addInfo(true, "save.sucess");
			}else { // la�o do ATUALIZA 
				if (ie){
					this.emailNfe.setEmail(this.emailStr);
					this.emailNfe.setFilial(this.filial);
					this.filial.setEmailNFE(this.emailNfe);
					this.filial.setAliqArpoveitaIcms(this.aliqAprovIcms);
					this.filial.setReduzBaseIcms(this.reducaoBaseIcms);
					filialDao.save(filial);
//					alteraEnderecoCep();
					this.endComplemento.setLogradouro(this.endereco.getLogra());
					this.endComplemento.setBairro(this.endereco.getBairro());
					endComplemento.setFilial(this.filial);
					endComplemento.setEndereco(endereco);
					this.endComplemento = this.endComplementoDao.save(endComplemento);
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
				valida = validaIE(filial.getInscEstadual(),endereco.getUf().name());
				System.out.println(" ie do estado " + endereco.getUf() + " foi validado com " + valida);
				if (valida == false && filial.getInscEstadual() == null ){
					return true;
				}else {
					return valida;
				}
			}else  {
				return true;
			}
		} catch (Exception e) {
			if (filial.getInscEstadual() == null){
				return true;
			}else{
				return false;
			}
		}
	}

	@Transactional
	public String doExcluir() {
		try {
			this.filial.setDeleted(true);
			filialDao.save(this.filial);
			return toListFilial();
		} catch (IllegalArgumentException e) {
			System.out.println("N�o foi possivel excluir o registro " + e);
			return null;
		}
	}

	public String newFilial() {
		return "formCadFilial.xhtml?faces-redirect=true";
	}

	public String toListFilial() {
		return "formListFilial.xhtml?faces-redirect=true";
	}
	/*
	 * redireciona para a pagina com o ID da filial a ser editada
	 * 
	 * @param filialID
	 * 
	 * @return
	 */

	public String changeToEdit(Long filialID) {
		System.out.println();
		return "formCadFilial.xhtml?faces-redirect=true&filialID=" + filialID+"&tipoCadastro="+TipoCadastro.FIL.name();
	}

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

	public List<Filial> getFiliais() {
		if (this.filiais.isEmpty() == true){
			return this.filiais = filialDao.listFilialAtiva();
		}else{
			return this.filiais;
		}
	}


	public String getIe() {
		return filial.getInscEstadual();
	}


		public String recuperaEndereco(){
		this.endereco = pegaEnderecoNaSessao();
		return "PF('dialogEndereco').hide()";
	}
	public void onRowSelect(SelectEvent event)throws IOException{
		FacesMessage msg = new FacesMessage("Filial "+((Filial) event.getObject()).getRazaoSocial()+" selecionada");  
		FacesContext.getCurrentInstance().addMessage(null, msg);
		this.filial = (Filial) event.getObject();
		setVisivelPorIdCliente(true);
		System.out.println("Estou o RowSelect");
		this.viewState = ViewState.EDITING;
		changeToEdit(filial.getId());
		
	}
	public void limpaFormulario(){
		this.filial = new Filial();
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
			System.out.println("estou dentro do try" + this.filial.getId());
			if (this.filial.getId() != null){
				System.out.println("estou entro do if filial antes do contato getID"+ contato.getId());
				if (contato.getId() == null){

					System.out.println("estou no if contato.getID antes do setNome");
					this.contato.setNome(nomeContato);
					this.contato.setFilial(getFilial());
					contatoDao.save(contato);
				}else{
					System.out.println("estou no else do filial getID");
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
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"O Cadastro do contato " + contato.getNome() + " N�O pode ser conclu�do! id filial - null ", null);
				FacesContext.getCurrentInstance().addMessage(null, msg);
				return;
			}

		}catch (Exception e){
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"O Cadastro do contato " + nomeContato + " N�O pode ser conclu�do! ", null);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}

	public List<Filial> setConsultafilials(){
		return getFiliaisModel().getModelSource();
	}
	
	public List<Filial> pegaListafilial(){
		return filialDao.listFilialAtiva();
	}
	
	public void criaExcel(){
		postProcessXLS(pegaListafilial());
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

	@Override
	public Filial setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<Empresa> getEmpresas(){
		return empresaDao.listEmpresaAtiva();
	}
	
	@Transactional
	public void alteraEnderecoCep(){
		try{
			Endereco enderecoTemporario = new Endereco();
			enderecoTemporario = enderecoDao.listCep(endereco.getCep());
			enderecoTemporario.setLogra(endereco.getLogra());
			enderecoDao.save(enderecoTemporario);
		}catch (IllegalArgumentException e){
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"N�o foi poss�vel alterar o endere�o.", null);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}

	}

	
	/**
	 * Metodo de pesquisa de Cnpj na Receita federal
	 * 
	 */
	
	public void telaReceitaFederal(){
		this.updateAndOpenDialog("receitaFederalDialog","dialogReceitaFederal");
	}

	public void telaResultadoReceitaFederal() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		consultaCnpjCCC();
		//		resultadoReceita();
		this.updateAndOpenDialog("resultadoReceitaFederalDialog","dialogResultadoReceitaFederal");
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
	public void consultaCnpjCCC() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		try{

//			this.receita = pesquisaReceita.procuraReceita(valorCnpj);
			this.receita = pesquisaReceita.retornoConsultaSintegra(respostaAcbrLocal);
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
	@Transactional
	public void transfereConsultaReceita(){
		filial.setCnpj(receita.getReceitaCNPJ());
		filial.setRazaoSocial(receita.getReceitaRazao());
		filial.setNomeFantasia(receita.getReceitaFantasia());
		filial.setInscEstadual(receita.getReceitaIE());
		if (!receita.getReceitaCep().equalsIgnoreCase("00000-000")){
			endereco = enderecoDao.listCep(receita.getReceitaCep());
			endComplemento.setComplemento(receita.getReceitaComplemento());
			endComplemento.setNumero(receita.getReceitaNumero());
			System.out.println(receita.getReceitaRegime().toUpperCase()+ " tamanho do campo: " + receita.getReceitaRegime().length());
		}
		if (receita.getReceitaRegime().equalsIgnoreCase("NORMAL - REGIME PERIÓDICO DE APURAÇÃO") || receita.getReceitaRegime().equalsIgnoreCase("NORMAL")){
			filial.setEnquadramento(Enquadramento.Normal);
		}else {
			filial.setEnquadramento(Enquadramento.SimplesNacional);
			System.out.println(filial.getEnquadramento().toString());
		}

	}

}   
