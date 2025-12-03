package br.com.nsym.application.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import br.com.nsym.domain.model.entity.cadastro.Fornecedor;
import br.com.nsym.domain.model.entity.cadastro.Pais;
import br.com.nsym.domain.model.entity.cadastro.TipoCadastro;
import br.com.nsym.domain.model.entity.financeiro.Credito;
import br.com.nsym.domain.model.entity.tools.Enquadramento;
import br.com.nsym.domain.model.entity.tools.ReceitaFederalConsulta;
import br.com.nsym.domain.model.entity.tools.TipoCliente;
import br.com.nsym.domain.model.entity.tools.Uf;
import br.com.nsym.domain.model.entity.tools.UfSigla;
import br.com.nsym.domain.model.repository.cadastro.ContatoRepository;
import br.com.nsym.domain.model.repository.cadastro.EmailRepository;
import br.com.nsym.domain.model.repository.cadastro.EmpresaRepository;
import br.com.nsym.domain.model.repository.cadastro.EndComplementoRepository;
import br.com.nsym.domain.model.repository.cadastro.EnderecoRepository;
import br.com.nsym.domain.model.repository.cadastro.FoneRepository;
import br.com.nsym.domain.model.repository.cadastro.FornecedorRepository;
import br.com.nsym.domain.model.repository.cadastro.PaisRepository;
import br.com.nsym.domain.model.repository.financeiro.CreditoRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class FornecedorBean extends AbstractBeanEmpDS<Fornecedor> {

	
	/**
	 *
	 */
	private static final long serialVersionUID = 2177897686306534171L;

	@Getter
	@Setter
	private Fornecedor fornecedor = new Fornecedor();

	@Inject
	private FornecedorRepository fornecedorDao;
	
	@Getter
	@Setter
	private TipoCliente validaCampo;
	
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
	private List<Fornecedor> fornecedores = new ArrayList<>();

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
	private boolean visivelPorIdFornecedor = false;

	@Getter
	@Setter
	private Long idEndereco;

	@Getter
	private List<Endereco> listaEncontrada =new ArrayList<>(); 

	@Getter
	private AbstractLazyModel<Fornecedor> fornecedorModel;

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
	
	@Getter
	@Setter
	private Email emailNfe= new Email();
	
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
	private String ufReceita;
	
	@Getter
	@Setter
	private UfSigla ufSelecionada;
	
	@Getter
	@Setter
	private Pais pais;
	
	@Inject
	private PaisRepository paisDao;
	
	@Getter
	@Setter
	private Credito credito;
	
	@Inject
	private CreditoRepository creditoDao;
	


	public AbstractLazyModel<Fornecedor> getLazyFornecedor(){
		this.fornecedorModel = new AbstractLazyModel<Fornecedor>() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1700847794143571367L;

			@Override
			public List<Fornecedor> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Fornecedor");
				final PageRequest pageRequest = new PageRequest();
				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				Page<Fornecedor> page = fornecedorDao.listaLazyComFiltro(isDeleted, null, pegaIdEmpresa(), null, pageRequest, null, null, false);
				this.setRowCount(page.getTotalPagesInt());
				
				if (filters != null){
					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
							String filterProperty = it.next();
							Object filterValue = filters.get(filterProperty);
							page = fornecedorDao.listaLazyComFiltro(isDeleted(),null,pegaIdEmpresa(), null, pageRequest, filterProperty, filterValue.toString().toUpperCase(),false);
							this.setRowCount(page.getTotalPagesInt());
					}
				}

				return page.getContent();
			}
		};
		return fornecedorModel;
	}

	public void telaPesquisaEndereco() {
		this.openDialog("dialogEndereco");
	}

	public void telaTelefone(){
		this.openDialog("dialogContatoFornecedor");
	}

	/**
	 * 
	 * @param idFornecedor
	 */
	@Transactional
	public void initializeForm(Long idFornecedor) {
		if (idFornecedor == null) {
			this.viewState = ViewState.ADDING;
			this.fornecedor = new Fornecedor();
			this.emailNfe = new Email();
			this.credito = new Credito();
		} else {
			this.viewState = ViewState.EDITING;
			setVisivelPorIdFornecedor(true);
//			this.contatoModel = contatoUtil.getLazyContatosPorEmpresa(idFornecedor,pegaIdEmpresa(),pegaIdFilial(),TipoCadastro.FOR);
			this.fornecedor = this.fornecedorDao.findById(idFornecedor, false);
			this.fornecedor.setListaCredito(this.creditoDao.retornaListaCredito(null, this.fornecedor, null, pegaIdEmpresa(), pegaIdFilial()));
			this.validaCampo = this.fornecedor.getTipoCliente();
			if (this.fornecedor.getEndereco() != null){
				this.endereco = enderecoDao.listCep(fornecedor.getEndereco().getEndereco().getCep());
				this.endComplemento = endComplementoDao.pegaEndComplementoPorFornecedor(fornecedor);
				if (this.endComplemento.getLogradouro() == null || this.endComplemento.getLogradouro().isEmpty()) {
					this.endComplemento.setLogradouro(this.endereco.getLogra());
					this.endComplemento.setBairro(this.endereco.getBairro());
				}else {
					this.endereco.setBairro(this.endComplemento.getBairro());
					this.endereco.setLogra(this.endComplemento.getLogradouro());
				}
			}
			if ( this.fornecedor.getEmailNFE() != null){
				this.emailNfe = this.emailDao.pegaEmailNfe(null,fornecedor,null,null,null,pegaIdEmpresa());
				this.emailStr = this.emailNfe.getEmail();
			}
		}
	}

	/**
	 * 
	 */
	public void initializeListing() {
		this.viewState = ViewState.LISTING;
		this.fornecedorModel = getLazyFornecedor();
	}
	@Transactional
	public void doSalvar() {
		try{
			System.out.println("estou dentro do DOSALVAR");
			boolean ie = verificaIe(); //checa se a Inscri��o estadual � v�lida  (obs. Inscri��o Estadual em branco � V�lidado com True)
			if (this.fornecedor.getId() == null){
				this.pais = this.paisDao.listaPaises("BRASIL");
				this.credito.setFornecedor(this.fornecedor);
				this.fornecedor.getListaCredito().add(this.credito);
				if (this.fornecedor.getPais() == null) {
					this.fornecedor.setPais(this.pais);
				}
				this.fornecedor.setTipoCliente(validaCampo);
				System.out.println("Estou no fornecedor id = vazio");
				if ( this.fornecedor.getTipoCliente().equals(TipoCliente.CfC) ){
					System.out.println("Estou dentro do tipo Consumidor Final CPF");
					final boolean cpfCadastrado = fornecedorDao.procuraCPF(this.fornecedor.getCpf(),pegaIdEmpresa());
					if(cpfCadastrado == false){
						this.fornecedor.setDeleted(false);
						this.fornecedor.setEstado(this.endereco.getUf());
						this.fornecedor = this.fornecedorDao.save(this.fornecedor);
						setVisivelPorIdFornecedor(true);
					}else{
						this.addWarning(true, "cpf.exist");
						return;
					}
				}else{
					if ( this.fornecedor.getTipoCliente().equals(TipoCliente.Est) ){
						this.fornecedor.setEnquadramento(Enquadramento.SimplesNacional);
					}
					System.out.println("Estou no OUTROS tipos de clientes");
					final boolean cnpjExistente = fornecedorDao.procuraCnpj(fornecedor.getCnpj(),pegaIdEmpresa());
					System.out.println("1- estou dentro do filialID = null " + " cnpjExistente = " + cnpjExistente + "inscri��o v�lido = "+ verificaIe());
					if (cnpjExistente == false && ie){
						this.fornecedor.setDeleted(false);
						this.fornecedor.setEstado(this.endereco.getUf());
						this.fornecedor.setTipoCliente(validaCampo);
						this.fornecedor = this.fornecedorDao.save(this.fornecedor);
						System.out.println("1-1 "+ this.fornecedor.getId() + " id do Fornecedor dentro do salvar Novo CNPJ id vazio");
						setVisivelPorIdFornecedor(true);

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
			if (this.fornecedor.getId() != null && this.endereco.getLogra() != null){
				System.out.println("2- estou dentro do transporadoraID diferente de null salvando o endComplemento "+ fornecedor.getId());
				Endereco endProvisorio = enderecoDao.listCep(this.endereco.getCep());
//				if (endProvisorio.getLogra().equalsIgnoreCase(this.endereco.getLogra())){
//					this.endereco = endProvisorio;
//				}else{
//					endProvisorio.setLogra(this.endereco.getLogra());
//					endProvisorio.setBairro(this.endereco.getBairro());
//					this.endereco = endProvisorio;
//				}
				System.out.println("Estou gravando o endComplemnto clienteID: " + this.fornecedor.getId());
				this.endComplemento.setDeleted(false);
				this.endComplemento.setFornecedor(this.fornecedor);
				this.endComplemento.setLogradouro(this.endereco.getLogra());
				this.endComplemento.setBairro(this.endereco.getBairro());
				this.endComplemento.setEndereco(endProvisorio);
				this.emailNfe.setEmail(this.emailStr);
				this.emailNfe.setFornecedor(this.fornecedor);
				this.emailNfe.setDeleted(false);
				this.emailNfe = this.emailDao.save(this.emailNfe);
				this.endComplemento = this.endComplementoDao.save(this.endComplemento);
//				this.emailNfe = emailDao.pegaEmailNfe(null,this.fornecedor,null,null ,null,pegaIdEmpresa());
			}
			if (this.endComplemento.getId() != null){
				System.out.println("3- Estou dentro do endComplementoID diferente de null atualizando a fornecedor com o ID do endComplemento " + endComplemento.getId() );
				this.fornecedor.setEndereco(this.endComplemento);
				this.endComplemento.setLogradouro(this.endereco.getLogra());
				this.endComplemento.setBairro(this.endereco.getBairro());
				this.fornecedor.setTipoCliente(validaCampo);
				this.fornecedor = this.fornecedorDao.save(this.fornecedor);
//				alteraEnderecoCep();
			}
//			insereFornecedorNaSessao();
//			limpaFormulario();
			this.addInfo(true, "save.sucess");
		}else { // la�o do ATUALIZA 
			if (this.fornecedor.getListaCredito().size() == 0 ) {
				this.credito = new Credito();
				this.credito.setFornecedor(this.fornecedor);
				this.fornecedor.getListaCredito().add(this.credito);
			}
			if (ie){
				this.emailNfe.setEmail(this.emailStr);
				this.emailNfe.setFornecedor(this.fornecedor);
				this.fornecedor.setEmailNFE(this.emailNfe);
				this.fornecedor.setTipoCliente(validaCampo);
				if ( this.fornecedor.getTipoCliente().equals(TipoCliente.Est) ){
					this.fornecedor.setEnquadramento(Enquadramento.SimplesNacional);
				}
				this.fornecedor = this.fornecedorDao.save(this.fornecedor);
//				alteraEnderecoCep();
				this.endComplemento.setLogradouro(this.endereco.getLogra());
				this.endComplemento.setBairro(this.endereco.getBairro());
				this.endComplemento.setFornecedor(this.fornecedor);
				this.endComplemento.setEndereco(this.endereco);
				this.endComplementoDao.save(this.endComplemento);
//				limpaFormulario();
			}
			this.addInfo(true,"save.update");
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
			if (new BigDecimal(Uf.GO.getCod()).compareTo(new BigDecimal(endereco.getUf().getCod()))==0) {
				System.out.println("estou dentro do validaGoias");
				valida = validaGoias(fornecedor.getInscEstadual());
			}else {
				if (new BigDecimal(Uf.RS.getCod()).compareTo(new BigDecimal(endereco.getUf().getCod()))==0) {
					System.out.println("ie = " + fornecedor.getInscEstadual());
					valida = validaRS(fornecedor.getInscEstadual());
				}else {
					System.out.println("Dentro do valida Stella");
					valida = validaIE(fornecedor.getInscEstadual(),endereco.getUf().name());
				}
				
			}
			System.out.println(" ie do estado " + endereco.getUf() + " foi validado com " + valida);
			if (valida == false && fornecedor.getInscEstadual() == null ){
				return true;
			}else {
				return valida;
			}
		}else  {
			return true;
		}
	} catch (Exception e) {
		if (fornecedor.getInscEstadual() == null){
			return true;
		}else{
			return false;
		}
	}
}

//@Transactional
//public void alteraEnderecoCep(){
//	try{
//		Endereco enderecoTemporario = new Endereco();
//		enderecoTemporario = enderecoDao.listCep(endereco.getCep());
//		enderecoTemporario.setLogra(endereco.getLogra());
//		enderecoTemporario.setBairro(this.endereco.getBairro());
//		enderecoDao.save(enderecoTemporario);
//	}catch (IllegalArgumentException e){
//		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"N�o foi poss�vel alterar o endere�o.", null);
//		FacesContext.getCurrentInstance().addMessage(null, msg);
//	}
//
//}

@Transactional
public String doExcluir() {
	try {
		this.fornecedor.setDeleted(true);
		fornecedorDao.save(this.fornecedor);
		return toListFornecedor();
	} catch (IllegalArgumentException e) {
		System.out.println("N�o foi possivel excluir o registro " + e);
		return null;
	}
}

public String newFornecedor() {
	return "formCadFornecedor.xhtml?faces-redirect=true";
}

public String toListFornecedor() {
	return "formListFornecedor.xhtml?faces-redirect=true";
}
/*
 * redireciona para a pagina com o ID da filial a ser editada
 * 
 * @param filialID
 * 
 * @return
 */


public String changeToEdit(Long fornecedorID) {
	System.out.println();
	return "formCadFornecedor.xhtml?faces-redirect=true&fornecedorID=" + fornecedorID +"&tipoCadastro="+TipoCadastro.FOR.name();
}

/**
 * @return a lista de tipos validos para Enquadramento
 */
public Enquadramento[] getEnquadramentoType() {
	return Enquadramento.values();
}

public List<Fornecedor> getFornecedores() {
	if (this.fornecedores.isEmpty() == true){
		return this.fornecedores = fornecedorDao.listFornecedorAtiva();
	}else{
		return this.fornecedores;
	}
}


public String getIe() {
	return fornecedor.getInscEstadual();
}

public String recuperaEndereco(){
	this.endereco = pegaEnderecoNaSessao();
	return "PF('dialogEndereco').hide()";
}
public void onRowSelect(SelectEvent event)throws IOException{
	FacesMessage msg = new FacesMessage("fornecedor "+((Fornecedor) event.getObject()).getRazaoSocial()+" selecionada");  
	FacesContext.getCurrentInstance().addMessage(null, msg);
	this.fornecedor = (Fornecedor) event.getObject();
	setVisivelPorIdFornecedor(true);
	System.out.println("Estou o RowSelect");
	this.viewState = ViewState.EDITING;
	changeToEdit(fornecedor.getId());

}
public void limpaFormulario(){
	this.fornecedor = new Fornecedor();
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
		System.out.println("estou dentro do try" + this.fornecedor.getId());
		if (this.fornecedor.getId() != null){
			System.out.println("estou entro do if filial antes do contato getID"+ contato.getId());
			if (contato.getId() == null){

				System.out.println("estou no if contato.getID antes do setNome");
				this.contato.setNome(nomeContato);
				this.contato.setFornecedor(fornecedor);
				this.contato = this.contatoDao.save(this.contato);
			}else{
				System.out.println("estou no else do filial getID");
				if (getFone() != null){
					System.out.println("estou no getFone != null no else do contato.getID");
					this.fone.setContato(getContato());
					this.fone = this.foneDao.save(this.fone);
				}
				if (getEmail() != null){
					this.email.setContato(getContato());
					this.email = this.emailDao.save(this.email);
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

public List<Fornecedor> setConsultafilials(){
	return getFornecedorModel().getModelSource();
}

public List<Fornecedor> pegaListaCliente(){
	return fornecedorDao.listFornecedorAtiva();
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


public UfSigla[] getUfSigla(){
	return UfSigla.values();
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
		this.receita = pesquisaReceita.retornoConsultaSintegra(pesquisaReceita.consultaCadSintegra(pegaConexao(),valorCnpj,this.ufSelecionada));
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
	fornecedor.setCnpj(receita.getReceitaCNPJ());
	fornecedor.setRazaoSocial(receita.getReceitaRazao());
	fornecedor.setNomeFantasia(receita.getReceitaFantasia());
	fornecedor.setInscEstadual(receita.getReceitaIE());
	if (!receita.getReceitaCep().equalsIgnoreCase("00000-000")){
		endereco = enderecoDao.listCep(receita.getReceitaCep());
		endComplemento.setComplemento(receita.getReceitaComplemento());
		endComplemento.setNumero(receita.getReceitaNumero());
		System.out.println(receita.getReceitaRegime().toUpperCase()+ " tamanho do campo: " + receita.getReceitaRegime().length());
	}
	if (receita.getReceitaRegime().equalsIgnoreCase("NORMAL - REGIME PERIÓDICO DE APURAÇÃO") || receita.getReceitaRegime().equalsIgnoreCase("NORMAL")){
		fornecedor.setEnquadramento(Enquadramento.Normal);
	}else {
		fornecedor.setEnquadramento(Enquadramento.SimplesNacional);
		System.out.println(fornecedor.getEnquadramento().toString());
	}
	
}

@Override
public Fornecedor setIdEmpresa(Long idEmpresa) {
	// TODO Auto-generated method stub
	return null;
}

@Override
public Fornecedor setIdFilial(Long idFilial) {
	// TODO Auto-generated method stub
	return null;
}

}

