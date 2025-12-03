package br.com.nsym.application.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
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

import br.com.nsym.application.component.table.AbstractLazyModel;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.misc.ReceitaFinder;
import br.com.nsym.domain.model.entity.cadastro.Cliente;
import br.com.nsym.domain.model.entity.cadastro.Contato;
import br.com.nsym.domain.model.entity.cadastro.Email;
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.EndComplemento;
import br.com.nsym.domain.model.entity.cadastro.Endereco;
import br.com.nsym.domain.model.entity.cadastro.Fone;
import br.com.nsym.domain.model.entity.cadastro.TipoCadastro;
import br.com.nsym.domain.model.entity.cadastro.Transportadora;
import br.com.nsym.domain.model.entity.tools.Enquadramento;
import br.com.nsym.domain.model.entity.tools.ReceitaFederalConsulta;
import br.com.nsym.domain.model.entity.tools.UfSigla;
import br.com.nsym.domain.model.repository.cadastro.ContatoRepository;
import br.com.nsym.domain.model.repository.cadastro.EmailRepository;
import br.com.nsym.domain.model.repository.cadastro.EmpresaRepository;
import br.com.nsym.domain.model.repository.cadastro.EndComplementoRepository;
import br.com.nsym.domain.model.repository.cadastro.EnderecoRepository;
import br.com.nsym.domain.model.repository.cadastro.FoneRepository;
import br.com.nsym.domain.model.repository.cadastro.TransportadoraRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class TransportadoraBean extends AbstractBeanEmpDS<Transportadora> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7157383733679978680L;

	@Getter
	@Setter
	private Transportadora transportadora = new Transportadora();

	@Inject
	private TransportadoraRepository transportadoraDao;
	

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

	@Setter
	private List<Transportadora> transportadoras = new ArrayList<>();

	@Getter
	@Setter
	private Boolean consultaPorCepVisivel = true;

	@Setter
	private List<Fone> fonesCliente = new ArrayList<>();

	@Getter
	@Setter
	private boolean listaPreenchida = false;


	@Getter
	@Setter
	private boolean visivelPorIdTransportadora = false;

	@Getter
	@Setter
	private Long idEndereco;

	@Getter
	private List<Endereco> listaEncontrada =new ArrayList<>(); 

	@Getter
	private AbstractLazyModel<Transportadora> transportadoraModel;

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

	@Getter
	@Setter
	private String emailStr;
	
	@Setter
	private List<Email> listaEmail;

	@Inject
	private EmailRepository emailDao;

	@Getter
	private List<Cliente> consultaFiliais;

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
	private UfSigla siglaSeleciona;

	@PostConstruct
	public void init(){
		transportadoraModel = getLazyTransportadora();
	}

	public AbstractLazyModel<Transportadora> getLazyTransportadora(){
		this.transportadoraModel = new AbstractLazyModel<Transportadora>() {
			/**
			 *
			 */
			private static final long serialVersionUID = -802026140245182442L;

			@Override
			public List<Transportadora> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Transportadora");
				final PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				Page<Transportadora> page = transportadoraDao.listaLazyComFiltro(isDeleted, null, pegaIdEmpresa(), null, pageRequest, null, null, false);

				this.setRowCount(page.getTotalPagesInt());
				if (filters != null){
					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
						try{
							String filterProperty = it.next();
							Object filterValue = filters.get(filterProperty);
							page = transportadoraDao.listaLazyComFiltro(isDeleted(),null,pegaIdEmpresa(), null, pageRequest, filterProperty, filterValue.toString().toUpperCase(),false);
							this.setRowCount(page.getTotalPagesInt());
						} catch(Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}

				return page.getContent();
			}
		};
		return transportadoraModel;
	}

	public void telaPesquisaEndereco() {
		this.openDialog("dialogEndereco");
	}

	public void telaTelefone(){
		this.openDialog("dialogContatoTransportadora");
	}

	/**
	 * 
	 * @param idTransportadora
	 */
	@Transactional
	public void initializeForm(Long idTransportadora) {
		if (idTransportadora == null) {
			this.viewState = ViewState.ADDING;
			this.transportadora = new Transportadora();
		} else {
			this.viewState = ViewState.EDITING;
			setVisivelPorIdTransportadora(true);
			this.transportadora = this.transportadoraDao.findById(idTransportadora, false);
			if (this.transportadora.getEndereco() != null){
				this.endereco = enderecoDao.listCep(transportadora.getEndereco().getEndereco().getCep());
				this.endComplemento = endComplementoDao.pegaEndComplementoPorTransportadora(transportadora);
				if (this.endComplemento.getLogradouro() == null || this.endComplemento.getLogradouro().isEmpty()) {
					this.endComplemento.setLogradouro(this.endereco.getLogra());
					this.endComplemento.setBairro(this.endereco.getBairro());
				}else {
					this.endereco.setBairro(this.endComplemento.getBairro());
					this.endereco.setLogra(this.endComplemento.getLogradouro());
				}
			}
			if ( this.transportadora.getEmailNFE() != null){
				this.email = emailDao.pegaEmailNfe(null,null,null,null,transportadora,pegaIdEmpresa());
				this.emailStr = this.email.getEmail();
			}
		}
	}

	/**
	 * 
	 */
	public void initializeListing() {
		this.viewState = ViewState.LISTING;
	}
	@Transactional
	public void doSalvar() {
		try{
			System.out.println("estou dentro do DOSALVAR");
			boolean ie = verificaIe(); //checa se a Inscri��o estadual � v�lida  (obs. Inscri��o Estadual em branco � V�lidado com True)
			if (this.transportadora.getId() == null){
				System.out.println("Estou no Transportadora id = vazio");
				if ( !this.transportadora.isTemCnpj() ){
					System.out.println("Estou dentro do tipo Consumidor Final CPF");
					final boolean cpfCadastrado = transportadoraDao.procuraCPF(this.transportadora.getCpf(),pegaIdEmpresa());
					if(cpfCadastrado == false){
						this.transportadora.setDeleted(false);
						this.transportadora.setEstado(this.endereco.getUf());
						this.transportadora = this.transportadoraDao.save(this.transportadora);
						setVisivelPorIdTransportadora(true);
					}else{
						this.addWarning(true, "cpf.exist");
						return;
					}
				}else{
					System.out.println("Estou no OUTROS tipos de clientes");
					final boolean cnpjExistente = transportadoraDao.procuraCnpj(transportadora.getCnpj(),pegaIdEmpresa());
					System.out.println("1- estou dentro do filialID = null " + " cnpjExistente = " + cnpjExistente + "inscri��o v�lido = "+ verificaIe());
					if (cnpjExistente == false && ie){
						this.transportadora.setDeleted(false);
						this.transportadora.setEstado(this.endereco.getUf());
						this.transportadora = this.transportadoraDao.save(this.transportadora);
						System.out.println("1-1 "+ this.transportadora.getId() + " id da Transpordora dentro do salvar Novo CNPJ id vazio");
						setVisivelPorIdTransportadora(true);

					}else{
						if (cnpjExistente== true){
							this.addError(true, "cnpj.exist");
							return;
						}else{
							this.addError(true, "ie.error");
							return;
						}
					}
				}
			if (this.transportadora.getId() != null && this.endereco.getLogra() != null){
				System.out.println("2- estou dentro do transporadoraID diferente de null salvando o endComplemento "+ transportadora.getId());
				Endereco endProvisorio = enderecoDao.listCep(this.endereco.getCep());
//				if (endProvisorio.getLogra().equalsIgnoreCase(this.endereco.getLogra())){
//					this.endereco = endProvisorio;
//				}else{
//					endProvisorio.setLogra(this.endereco.getLogra());
//					endProvisorio.setBairro(this.endereco.getBairro());
//					this.endereco = endProvisorio;
//				}
				System.out.println("Estou gravando o endComplemnto clienteID: " + this.transportadora.getId());
				this.endComplemento.setDeleted(false);
				this.endComplemento.setTransportadora(this.transportadora);
				this.endComplemento.setEndereco(endProvisorio);
				this.endComplemento.setLogradouro(this.endereco.getLogra());
				this.endComplemento.setBairro(this.endereco.getBairro());
				this.email.setTransportadora(this.transportadora);
				this.email.setEmail(emailStr);
				this.email.setDeleted(false);
				this.email = this.emailDao.save(this.email);
				this.endComplemento =  this.endComplementoDao.save(this.endComplemento);
				this.email = emailDao.pegaEmailNfe(null,null,null,null ,this.transportadora,pegaIdEmpresa());
			}
			if (this.endComplemento.getId() != null){
				System.out.println("3- Estou dentro do endComplementoID diferente de null atualizando a transportadora com o ID do endComplemento " + endComplemento.getId() );
				this.transportadora.setEndereco(this.endComplemento);
				this.transportadora = this.transportadoraDao.save(this.transportadora);
//				alteraEnderecoCep();
			}
			this.addInfo(true, "save.sucess");
		}else { // la�o do ATUALIZA 
			if (ie){
				this.email.setTransportadora(this.transportadora);
				this.email.setEmail(emailStr);
				this.email.setDeleted(false);
//				this.email = this.emailDao.save(this.email);
				this.transportadora.setEmailNFE(this.email);
				this.transportadora = this.transportadoraDao.save(this.transportadora);
//				alteraEnderecoCep();
				this.endComplemento.setTransportadora(this.transportadora);
				this.endComplemento.setEndereco(this.endereco);
				this.endComplemento.setLogradouro(this.endereco.getLogra());
				this.endComplemento.setBairro(this.endereco.getBairro());
				this.endComplemento = this.endComplementoDao.save(this.endComplemento);
				this.addInfo(true,"save.sucess");
			}
		}
	}catch (IllegalArgumentException e) {
		System.out.println("grava��o cancelada " + e);
	}
}
public boolean verificaIe(){
	try {
		boolean valida;
		System.out.println(endereco.getUf().name());
		if (endereco.getUf() != null){
			valida = validaIE(transportadora.getInscEstadual(),endereco.getUf().name());
			System.out.println(" ie do estado " + endereco.getUf() + " foi validado com " + valida);
			if (valida == false && transportadora.getInscEstadual() == null ){
				return true;
			}else {
				return valida;
			}
		}else  {
			return true;
		}
	} catch (Exception e) {
		if (transportadora.getInscEstadual() == null){
			return true;
		}else{
			return false;
		}
	}
}

@Transactional
public String doExcluir() {
	try {
		this.transportadora.setDeleted(true);
		transportadoraDao.save(this.transportadora);
		return toListTransportadora();
	} catch (IllegalArgumentException e) {
		System.out.println("N�o foi possivel excluir o registro " + e);
		return null;
	}
}

public String newTransportadora() {
	return "formCadTransportadora.xhtml?faces-redirect=true";
}

public String toListTransportadora() {
	return "formListTransportadora.xhtml?faces-redirect=true";
}
/*
 * redireciona para a pagina com o ID da filial a ser editada
 * 
 * @param filialID
 * 
 * @return
 */


public String changeToEdit(Long transportadoraID) {
	System.out.println();
	return "formCadTransportadora.xhtml?faces-redirect=true&transportadoraID=" + transportadoraID+"&tipoCadastro="+TipoCadastro.TRANSP.name();
}

/**
 * @return a lista de tipos validos para Enquadramento
 */
public Enquadramento[] getEnquadramentoType() {
	return Enquadramento.values();
}

public List<Transportadora> getTransportadoras() {
	if (this.transportadoras.isEmpty() == true){
		return this.transportadoras = transportadoraDao.listTransportadoraAtiva();
	}else{
		return this.transportadoras;
	}
}


public String getIe() {
	return transportadora.getInscEstadual();
}

public String recuperaEndereco(){
	this.endereco = pegaEnderecoNaSessao();
	return "PF('dialogEndereco').hide()";
}
public void onRowSelect(SelectEvent event)throws IOException{
	FacesMessage msg = new FacesMessage("Transportadora "+((Transportadora) event.getObject()).getRazaoSocial()+" selecionada");  
	FacesContext.getCurrentInstance().addMessage(null, msg);
	this.transportadora = (Transportadora) event.getObject();
	setVisivelPorIdTransportadora(true);
	System.out.println("Estou o RowSelect");
	this.viewState = ViewState.EDITING;
	changeToEdit(transportadora.getId());

}
public void limpaFormulario(){
	this.transportadora = new Transportadora();
	this.endereco = new Endereco();
	this.cep = "";
}

public List<Endereco> localizaEndereco(){
	try{
		return	this.listaEncontrada = enderecoDao.procuraCepWeb(estado,munic,logra);
	}catch (Exception e) {
		this.addError(true, "cep.pesquisa.erro", e.getMessage());
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
			this.addError(true, "cep.pesquisa.error", e.getMessage());
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
	Endereco endTemp = new Endereco();
	endTemp = this.enderecoDao.procuraCepBase(cep);
	if (endTemp == null) {
		saveEnd(this.endereco);
	}else {
		this.endereco = endTemp;
	}
}

@Transactional
public void doSalvaContato(){
	try{
		System.out.println("estou dentro do try" + this.transportadora.getId());
		if (this.transportadora.getId() != null){
			System.out.println("estou entro do if filial antes do contato getID"+ contato.getId());
			if (contato.getId() == null){

				System.out.println("estou no if contato.getID antes do setNome");
				this.contato.setNome(nomeContato);
				this.contato.setTransportadora(transportadora);
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

public List<Transportadora> setConsultafilials(){
	return getTransportadoraModel().getModelSource();
}

public List<Transportadora> pegaListaCliente(){
	return transportadoraDao.listTransportadoraAtiva();
}

public void criaExcel(){
	postProcessXLS(pegaListaCliente());
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

public List<Empresa> getEmpresas(){
	return empresaDao.listEmpresaAtiva();
}

@Transactional
public void alteraEnderecoCep(){
	try{
		Endereco enderecoTemporario = new Endereco();
		enderecoTemporario = enderecoDao.listCep(endereco.getCep());
		enderecoTemporario.setBairro(this.endereco.getBairro());
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
public void consultaCnpjCCC() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
	try{

		this.receita = pesquisaReceita.retornoConsultaSintegra(pesquisaReceita.consultaCadSintegra(pegaConexao(),this.valorCnpj, this.siglaSeleciona));
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

public void refresh() {
	FacesContext.getCurrentInstance().getPartialViewContext().release();
	//        Application application = context.getApplication();
	//        ViewHandler viewHandler = application.getViewHandler();
	//        UIViewRoot viewRoot = viewHandler.createView(context, context.getViewRoot().getViewId());
	//        context.setViewRoot(viewRoot);
	//        context.renderResponse();
}

@Transactional
public void transfereConsultaReceita(){
	transportadora.setCnpj(receita.getReceitaCNPJ());
	transportadora.setRazaoSocial(receita.getReceitaRazao());
	transportadora.setNomeFantasia(receita.getReceitaFantasia());
	transportadora.setInscEstadual(receita.getReceitaIE());
	if (!receita.getReceitaCep().equalsIgnoreCase("00000-000")){
		endereco = enderecoDao.listCep(receita.getReceitaCep());
		endComplemento.setComplemento(receita.getReceitaComplemento());
		endComplemento.setNumero(receita.getReceitaNumero());
		System.out.println(receita.getReceitaRegime().toUpperCase()+ " tamanho do campo: " + receita.getReceitaRegime().length());
	}
	if (receita.getReceitaRegime().equalsIgnoreCase("NORMAL - REGIME PERIÓDICO DE APURAÇÃO") || receita.getReceitaRegime().equalsIgnoreCase("NORMAL")){
		transportadora.setEnquadramento(Enquadramento.Normal);
	}else {
		transportadora.setEnquadramento(Enquadramento.SimplesNacional);
		System.out.println(transportadora.getEnquadramento().toString());
	}

}

public UfSigla[] getUfSigla(){
	return UfSigla.values();
}


@Override
public Transportadora setIdEmpresa(Long idEmpresa) {
	// TODO Auto-generated method stub
	return null;
}

@Override
public Transportadora setIdFilial(Long idFilial) {
	// TODO Auto-generated method stub
	return null;
}

}

