package br.com.nsym.application.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.time.LocalDate;
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

import br.com.nsym.application.component.Translator;
import br.com.nsym.application.component.table.AbstractDataModel;
import br.com.nsym.application.component.table.AbstractLazyModel;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.misc.AddressFinder;
import br.com.nsym.domain.misc.AddressFinder.StatusWebMania;
import br.com.nsym.domain.misc.ReceitaFinder;
import br.com.nsym.domain.misc.ValidadorCpf;
import br.com.nsym.domain.model.entity.cadastro.Cliente;
import br.com.nsym.domain.model.entity.cadastro.Contato;
import br.com.nsym.domain.model.entity.cadastro.Email;
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.EndComplemento;
import br.com.nsym.domain.model.entity.cadastro.Endereco;
import br.com.nsym.domain.model.entity.cadastro.Fone;
import br.com.nsym.domain.model.entity.cadastro.Pais;
import br.com.nsym.domain.model.entity.cadastro.TipoCadastro;
import br.com.nsym.domain.model.entity.financeiro.Credito;
import br.com.nsym.domain.model.entity.tools.Enquadramento;
import br.com.nsym.domain.model.entity.tools.PedidoStatus;
import br.com.nsym.domain.model.entity.tools.ReceitaFederalConsulta;
import br.com.nsym.domain.model.entity.tools.TipoCliente;
import br.com.nsym.domain.model.entity.tools.Uf;
import br.com.nsym.domain.model.entity.tools.UfSigla;
import br.com.nsym.domain.model.entity.venda.Pedido;
import br.com.nsym.domain.model.repository.cadastro.ClienteRepository;
import br.com.nsym.domain.model.repository.cadastro.ColaboradorRepository;
import br.com.nsym.domain.model.repository.cadastro.ContatoRepository;
import br.com.nsym.domain.model.repository.cadastro.EmailRepository;
import br.com.nsym.domain.model.repository.cadastro.EmpresaRepository;
import br.com.nsym.domain.model.repository.cadastro.EndComplementoRepository;
import br.com.nsym.domain.model.repository.cadastro.EnderecoRepository;
import br.com.nsym.domain.model.repository.cadastro.FilialRepository;
import br.com.nsym.domain.model.repository.cadastro.FoneRepository;
import br.com.nsym.domain.model.repository.cadastro.PaisRepository;
import br.com.nsym.domain.model.repository.financeiro.CreditoRepository;
import br.com.nsym.domain.model.repository.venda.PedidoRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped

public class ClienteBean extends AbstractBeanEmpDS<Cliente>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private boolean Estrangeiro = false;

	@Getter
	@Setter
	private TipoCliente validaCampo;

	@Getter
	@Setter
	private Cliente cliente = new Cliente();

	@Getter
	private ValidadorCpf validaCpf;

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
	private UfSigla ufConsulta;

	@Getter
	@Setter
	private String logra;

	@Getter
	@Setter
	private String munic;

	@Getter
	@Setter
	private Endereco endereco = new Endereco();

	@Getter
	@Setter
	private Pais pais;

	@Inject
	private PaisRepository paisDao;


	@Inject
	private EnderecoRepository enderecoDao;

	@Inject
	private EmpresaRepository empresaDao;
	
	@Inject
	private FilialRepository filialDao;

	@Inject
	@Getter
	private Translator tradutor;

	@Inject
	private ClienteRepository clienteDao;

	@Setter
	private List<Cliente> clientes = new ArrayList<>();

	@Setter
	private List<Empresa> empresas = new ArrayList<>();

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
	private boolean visivelPorIdCliente = false;

//	private StellaIEValidator ieValidator;

	@Getter
	@Setter
	private Long idEndereco;

	@Getter
	private List<Endereco> listaEncontrada =new ArrayList<>(); 

	@Getter
	private AbstractLazyModel<Cliente> clientesModel;
	
	@Getter
	@Setter
	private List<Cliente> filteredClientes;
	
	@Inject
	private ColaboradorRepository colaboradorDao;

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
	private String emailStr;
	
	@Getter
	@Setter
	private Email emailNfe= new Email();
	
	@Getter
	@Setter
	private String respostaAcbrLocal;
	
	@Getter
	private List<Pais> listaPaises = new ArrayList<>();
	
	@Getter
	@Setter
	private LocalDate dataInicial = LocalDate.now().minusMonths(1);
	
	@Getter
	@Setter
	private LocalDate dataFinal = LocalDate.now();
	
	@Getter
	@Setter
	private AbstractDataModel<Pedido> listaPedidosPorPeriodo = new AbstractDataModel<Pedido>();
	
	@Inject
	private PedidoRepository pedidoDao;
	
	@Getter
	@Setter
	private Pedido pedido = new Pedido();
	
	@Getter
	@Setter
	private Credito credito;
	
	@Inject
	private CreditoRepository creditoDao;
	
	@PostConstruct
	public void init(){
		clientesModel = getLazyCliente();
		this.listaPaises = this.paisDao.listaPaisAtivo();
	}

	public AbstractLazyModel<Cliente> getLazyCliente(){
		this.clientesModel = new AbstractLazyModel<Cliente>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 8820792489824557497L;

			@Override
			public List<Cliente> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters){
				System.out.println("Estou no filiaisModel");
				 PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				 Page<Cliente> page = clienteDao.pageListaClienteLazy(isDeleted,pegaIdEmpresa(),null,pageRequest,null,null);

				this.setRowCount(page.getTotalPagesInt());
				if (filters != null){
					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
						try{
							String filterProperty = it.next();
							Object filterValue = filters.get(filterProperty);
							page = clienteDao.pageListaClienteLazy(isDeleted, pegaIdEmpresa(), null,pageRequest, filterProperty, filterValue.toString().toUpperCase());
							this.setRowCount(page.getTotalPagesInt());
						} catch(Exception e) {
	                    	System.out.println(e.getMessage());
	                    }
					}
				}
				return page.getContent();
			}
		};
		return clientesModel;
	}
	
	public void telaPesquisaEndereco() {
		this.openDialog("dialogEndereco");
	}

	public void telaTelefone(){
		this.openDialog("dialogContatoCliente");
	}

	/**
	 * 
	 * @param idfilial
	 */
	@Transactional
	public void initializeForm(Long idfilial) {
		if (idfilial == null) {
			this.viewState = ViewState.ADDING;
			this.cliente = new Cliente();
			this.endereco = new Endereco();
			this.endComplemento = new EndComplemento();
			this.emailNfe = new Email();
			this.credito = new Credito();
		} else {
			this.viewState = ViewState.EDITING;			
			setVisivelPorIdCliente(true);
			this.cliente = this.clienteDao.findById(idfilial, false);
			this.listaPedidosPorPeriodo = new AbstractDataModel<Pedido>(this.pedidoDao.pedidosVendidosPorPeriodoPorCliente(dataInicial,dataFinal,getUsuarioAutenticado().getIdEmpresa(),
					getUsuarioAutenticado().getIdFilial(),idfilial));
			this.validaCampo = this.cliente.getTipoCliente();
			this.cliente.setListaCredito(this.creditoDao.retornaListaCredito(this.cliente, null, null, pegaIdEmpresa(), pegaIdFilial()));
			if (this.cliente.getEndereco() != null){
				this.endereco = enderecoDao.listCep(cliente.getEndereco().getEndereco().getCep());
				this.endComplemento = endComplementoDao.pegaEndComplementoPorCliente(cliente);
				if (this.endComplemento.getLogradouro() == null || this.endComplemento.getLogradouro().isEmpty()) {
					this.endComplemento.setLogradouro(this.endereco.getLogra());
					this.endComplemento.setBairro(this.endereco.getBairro());
				}else {
					this.endereco.setBairro(this.endComplemento.getBairro());
					this.endereco.setLogra(this.endComplemento.getLogradouro());
				}
			}
			if ( this.cliente.getEmailNFE() != null){
				this.emailNfe = emailDao.pegaEmailNfe(cliente,null,null,null,null,pegaIdEmpresa());
				this.emailStr = this.emailNfe.getEmail();
			}
			if (this.cliente.getColaborador() != null ) {
				this.cliente.setColaborador(this.colaboradorDao.findById(this.cliente.getColaborador().getId(), false));
			}
		}
	}
	
	public void procuraPedidos() {
		this.listaPedidosPorPeriodo = new AbstractDataModel<Pedido>(this.pedidoDao.pedidosVendidosPorPeriodoPorCliente(dataInicial,dataFinal,getUsuarioAutenticado().getIdEmpresa(),
				getUsuarioAutenticado().getIdFilial(),this.cliente.getId()));
	}
	
	public void onRowSelectPedido(SelectEvent event)throws IOException{
		this.pedido = ((Pedido) event.getObject());
		if (this.pedido.getPedidoStatus() == PedidoStatus.CAN ) {
			this.addWarning(true, "pedidoPDV.status.cancelOrRec");
		}else {
			if (this.pedido.getPedidoStatus() == PedidoStatus.REC) {
				this.viewState = ViewState.PRINTING;
			}else {
				this.viewState = ViewState.EDITING;
			}
		}
	}

	/**
	 * 
	 */
	public void initializeListing() {
		this.viewState = ViewState.LISTING;
	}


	@Override
	public Cliente setIdFilial(Long idFilial) {
		return null;
	}

	@Transactional
	public void doSalvar() {
		try{
			boolean ie = verificaIe(); //checa se a Inscriï¿½ï¿½o estadual ï¿½ vï¿½lida  (obs. Inscriï¿½ï¿½o Estadual em branco ï¿½ Vï¿½lidado com True)
			this.viewState = ViewState.EDITING;
			if (this.cliente.getId() == null){
				this.cliente.setTipoCliente(validaCampo);
				this.credito.setCliente(this.cliente);
				this.cliente.getListaCredito().add(this.credito);
				if (this.cliente.getTipoCliente() == TipoCliente.Est ){
					if (this.clienteDao.passaporteCadastrado(this.cliente.getIdEstrangeiro(), getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial())){
						this.addError(true, "passaport.exist");
					}else{
						System.out.println("Entou dentro do tipo ESTRANGEIRO");
						this.cliente.setTipoCliente(validaCampo);
						this.cliente.setDeleted(false);		
						this.cliente.setEnquadramento(Enquadramento.SimplesNacional);
						this.cliente = this.clienteDao.save(this.cliente);
					}
				}else{
					this.pais = this.paisDao.listaPaises("BRASIL");
					this.cliente.setPais(this.pais);
					if (this.cliente.getTipoCliente() == TipoCliente.CfC ){
						System.out.println("Estou dentro do tipo Consumidor Final CPF");
						final boolean cpfCadastrado = clienteDao.procuraCPF(this.cliente.getCpf(),pegaIdEmpresa());
						if(cpfCadastrado == false){
							this.cliente.setTipoCliente(validaCampo);
							this.cliente.setDeleted(false);
							this.cliente.setEstado(this.endereco.getUf());
							this.cliente.setEnquadramento(Enquadramento.SimplesNacional);
							this.cliente = this.clienteDao.save(this.cliente);
							setVisivelPorIdCliente(true);
							this.cliente = this.clienteDao.localizaPorCpf(this.cliente.getCpf(),pegaIdEmpresa());
						}else{
							this.addWarning(true, "cpf.exist");
							return;
						}
					}else{
						System.out.println("Estou no OUTROS tipos de clientes");
						final boolean cnpjExistente = this.clienteDao.procuraCnpj(this.cliente.getCnpj(),pegaIdEmpresa());
						System.out.println("1- estou dentro do filialID = null " + " cnpjExistente = " + cnpjExistente + "inscriï¿½ï¿½o vï¿½lido = "+ verificaIe());
						if (cnpjExistente == false && ie){
							this.cliente.setTipoCliente(validaCampo);
							this.cliente.setDeleted(false);
							this.cliente.setEstado(this.endereco.getUf());
							
							this.cliente = this.clienteDao.save(this.cliente);
							this.cliente = clienteDao.localizaPorCnpj(this.cliente.getCnpj(),pegaIdEmpresa());
							System.out.println("1-1 "+ this.cliente.getId() + " id da filial dentro do salvar Novo CNPJ id vazio");
							setVisivelPorIdCliente(true);

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
				}
				if (cliente.getId() != null && endereco.getLogra() != null && !this.cliente.getTipoCliente().equals(TipoCliente.Est)){
					System.out.println("2- estou dentro do filialid diferente de null salvando o endComplemento "+ cliente.getId());
					Endereco endProvisorio = enderecoDao.listCep(this.endereco.getCep());
					
					System.out.println("Estou gravando o endComplemnto clienteID: " + this.cliente.getId());
					this.endComplemento.setDeleted(false);
					this.endComplemento.setLogradouro(this.endereco.getLogra());
					this.endComplemento.setBairro(this.endereco.getBairro());
					this.endComplemento.setCliente(this.cliente);
					this.endComplemento.setEndereco(endProvisorio);
					this.emailNfe.setEmail(this.emailStr);
					this.emailNfe.setCliente(this.cliente);
					this.emailNfe.setDeleted(false);
					this.emailNfe = this.emailDao.save(this.emailNfe);
					this.endComplemento=this.endComplementoDao.save(this.endComplemento);	
					this.emailNfe = emailDao.pegaEmailNfe(cliente,null,null,null ,null,pegaIdEmpresa());
				}else {
					if(this.cliente.getTipoCliente().equals(TipoCliente.Est)){ // regra especial para cliente do exterior
						Endereco endProvisorio = this.enderecoDao.listCep("99999-999");
						this.endComplemento.setDeleted(false);
						this.endComplemento.setLogradouro(this.endereco.getLogra());
						this.endComplemento.setBairro(this.endereco.getBairro());
						this.endComplemento.setCliente(this.cliente);
						this.endComplemento.setEndereco(endProvisorio);
						this.emailNfe.setEmail(this.emailStr);
						this.emailNfe.setCliente(this.cliente);
						this.emailNfe.setDeleted(false);
						this.emailNfe = this.emailDao.save(this.emailNfe);
						this.endComplemento=this.endComplementoDao.save(this.endComplemento);	
						this.emailNfe = emailDao.pegaEmailNfe(cliente,null,null,null,null,pegaIdEmpresa());
					}
				}
				if (endComplemento.getId() != null){
					System.out.println("3- Estou dentro do endComplementoID diferente de null atualizando a filial com o ID do endComplemento " + endComplemento.getId() );
					cliente.setEndereco(endComplemento);
					this.cliente.setTipoCliente(validaCampo);
					this.clienteDao.save(this.cliente);
				}
				initializeForm(this.cliente.getId());
				this.addInfo(true,"save.sucess",this.cliente.getRazaoSocial());
			}else { //  ATUALIZA 
				if (this.cliente.getListaCredito().size() == 0 ) {
					this.credito = new Credito();
					this.credito.setCliente(this.cliente);
					this.cliente.getListaCredito().add(this.credito);
				}
				if (ie){
					if (this.cliente.getTipoCliente().equals(TipoCliente.Est)){
						
						this.emailNfe.setEmail(this.emailStr);
						this.emailNfe.setCliente(this.cliente);
						this.cliente.setEmailNFE(this.emailNfe);
						this.cliente.setTipoCliente(validaCampo);
						this.cliente = this.clienteDao.save(this.cliente);
						
						this.endComplemento.setCliente(this.cliente);
						this.endComplemento.setEndereco(this.endereco);
						this.endComplemento.setBairro(this.endereco.getBairro());
						this.endComplemento.setLogradouro(this.endereco.getLogra());
						this.endComplemento = this.endComplementoDao.save(this.endComplemento);
					}else{
						this.emailNfe.setEmail(this.emailStr);
						this.emailNfe.setCliente(this.cliente);
						this.pais = this.paisDao.listaPaises("BRASIL");
						this.cliente.setEmailNFE(this.emailNfe);
						this.cliente.setPais(this.pais);
						this.cliente.setTipoCliente(validaCampo);
						this.cliente = this.clienteDao.save(this.cliente);
						
						this.endComplemento.setBairro(this.endereco.getBairro());
						this.endComplemento.setLogradouro(this.endereco.getLogra());
						this.endComplemento.setCliente(this.cliente);
						this.endComplemento.setEndereco(this.endereco);
						this.endComplementoDao.save(this.endComplemento);
					}
					this.addInfo(true,"save.sucess",this.cliente.getRazaoSocial());
				}else {
					this.addError(true, "ie.error");
				}
			}
		}catch (IllegalArgumentException e) {
			System.out.println("gravação cancelada " + e);
		}catch(Exception e){
			this.addError(true,"exception.error.fatal");
		}
	}
	public boolean verificaIe(){
		try {
			boolean valida;
			System.out.println(endereco.getUf().name());
			if (endereco.getUf() != null){
				if (new BigDecimal(Uf.GO.getCod()).compareTo(new BigDecimal(endereco.getUf().getCod()))==0) {
					System.out.println("estou dentro do validaGoias");
					valida = validaGoias(cliente.getInscEstadual());
				}else {
					if (new BigDecimal(Uf.RS.getCod()).compareTo(new BigDecimal(endereco.getUf().getCod()))==0) {
						System.out.println("ie = " + cliente.getInscEstadual());
						valida = validaRS(cliente.getInscEstadual());
					}else {
						System.out.println("Dentro do valida Stella");
						valida = validaIE(cliente.getInscEstadual(),endereco.getUf().name());
					}
					
				}
				System.out.println(" ie do estado " + endereco.getUf() + " foi validado com " + valida);
				if (valida == false && cliente.getInscEstadual() == null ){
					return true;
				}else {
					return valida;
				}
			}else  {
				return true;
			}
		} catch (Exception e) {
			if (cliente.getInscEstadual() == null){
				return true;
			}else{
				return false;
			}
		}
	}

	@Transactional
	public String doExcluir() {
		try {
			this.cliente.setDeleted(true);
			clienteDao.save(this.cliente);
			return toListCliente();
		} catch (IllegalArgumentException e) {
			System.out.println("Nï¿½o foi possivel excluir o registro " + e);
			return null;
		}
	}

	public String newCliente() {
		return "formCadCliente.xhtml?faces-redirect=true";
	}

	public String toListCliente() {
		return "formListCliente.xhtml?faces-redirect=true";
	}
	/*
	 * redireciona para a pagina com o ID da filial a ser editada
	 * 
	 * @param filialID
	 * 
	 * @return
	 */

	public String changeToEdit(Long clienteID) {
		System.out.println();
		return "formCadCliente.xhtml?faces-redirect=true&clienteID=" + clienteID +"&tipoCadastro="+TipoCadastro.CLI.name();
	}

	/**
	 * @return a lista de tipos validos para Enquadramento
	 */
	public Enquadramento[] getEnquadramentoType() {
		return Enquadramento.values();
	}

	public List<Cliente> getClientes() {
		if (this.clientes.isEmpty() == true){
			return this.clientes = clienteDao.listClienteAtiva();
		}else{
			return this.clientes;
		}
	}


	public String getIe() {
		return cliente.getInscEstadual();
	}


//	public void setIeValidator(StellaIEValidator ieValidator) {
//		this.ieValidator = ieValidator;
//	}

//	public StellaIEValidator getIeValidator(){
//		this.ieValidator.setEstado(cliente.getEndereco().getEndereco().getUf().name());
//		return this.ieValidator;
//	}

	public String recuperaEndereco(){
		this.endereco = pegaEnderecoNaSessao();
		return "PF('dialogEndereco').hide()";
	}
	public void onRowSelect(SelectEvent event)throws IOException{
		FacesMessage msg = new FacesMessage("Cliente "+((Cliente) event.getObject()).getRazaoSocial()+" selecionada");  
		FacesContext.getCurrentInstance().addMessage(null, msg);
		this.cliente = (Cliente) event.getObject();
		setVisivelPorIdCliente(true);
		System.out.println("Estou o RowSelect");
		this.viewState = ViewState.EDITING;
		changeToEdit(cliente.getId());
		this.validaCampo = this.cliente.getTipoCliente();

	}
	public void limpaFormulario(){
		this.cliente = new Cliente();
		this.endereco = new Endereco();
		this.endComplemento = new EndComplemento();
		this.listaContato = new ArrayList<>();
		this.emailStr = "";
		this.contato = new Contato();
		this.validaCampo = null;
		this.cep = "";
		
	}
	@Transactional
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
		Endereco endTemp = new Endereco();
		endTemp = this.enderecoDao.procuraCepBase(cep);
		if (endTemp == null) {
			saveEnd(this.endereco);
		}else {
			this.endereco = endTemp;
		}
	}
	
	public void statusWebManiaCep() {
		AddressFinder address = new AddressFinder();
		StatusWebMania status = address.statusAssinatura();
		System.out.println("Expira em: " +status.getExpires_in() + " Limite: " + status.getLimit() + "Plano: " + status.getPlan() + "Total: " + status.getTotal());
		this.addInfo(true, "Expira em: " +status.getExpires_in() + " Limite: " + status.getLimit() + " Plano: " + status.getPlan() + " Total: " + status.getTotal());
		
	}


	@Transactional
	public void doSalvaContato(){
		try{
			System.out.println("estou dentro do try" + this.cliente.getId());
			if (this.cliente.getId() != null){
				System.out.println("estou entro do if filial antes do contato getID"+ contato.getId());
				if (contato.getId() == null){

					System.out.println("estou no if contato.getID antes do setNome");
					this.contato.setNome(nomeContato);
					this.contato.setCliente(getCliente());
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
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"O Cadastro do contato " + contato.getNome() + " Nï¿½O pode ser concluï¿½do! id filial - null ", null);
				FacesContext.getCurrentInstance().addMessage(null, msg);
				return;
			}

		}catch (Exception e){
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"O Cadastro do contato " + nomeContato + " Nï¿½O pode ser concluï¿½do! ", null);
			FacesContext.getCurrentInstance().addMessage(null, msg);
			
		}
	}

	public List<Cliente> setConsultafilials(){
		return getClientesModel().getModelSource();
	}

	public List<Cliente> pegaListaCliente(){
		return clienteDao.listClienteAtiva();
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

	@Override
	public Cliente setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Empresa> getEmpresas(){
		return empresaDao.listEmpresaAtiva();
	}

//	@Transactional
//	public void alteraEnderecoCep(){
//		try{
//			Endereco enderecoTemporario = new Endereco();
//			enderecoTemporario = enderecoDao.listCep(endereco.getCep());
//			enderecoTemporario.setLogra(endereco.getLogra());
//			enderecoTemporario.setBairro(this.endereco.getBairro());
//			enderecoDao.save(enderecoTemporario);
//		}catch (IllegalArgumentException e){
//			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Nï¿½o foi possï¿½vel alterar o endereï¿½o.", null);
//			FacesContext.getCurrentInstance().addMessage(null, msg);
//		}
//
//	}


	/**
	 * Metodo de pesquisa de Cnpj na Receita federal
	 * 
	 */

	public void telaReceitaFederal(){
		this.updateAndOpenDialog("receitaFederalDialog","dialogReceitaFederal");
	}

	public void telaResultadoReceitaFederal() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		
		//		resultadoReceita();
		this.updateAndOpenDialog("resultadoReceitaFederalDialog","dialogResultadoReceitaFederal");
	}
	public void consultaCnpjCCC() {
//		try{

//			this.receita = pesquisaReceita.procuraReceita(valorCnpj);
			this.receita = pesquisaReceita.retornoConsultaSintegra(respostaAcbrLocal);
			System.out.println("Recebi o resultado da pesquisa ");
			System.out.println(this.receita.getReceitaRazao());
			if (this.receita.getReceitaCNPJ() != null){
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
//		}catch (Exception e ){
//			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Cnpj nï¿½o informado!", null);
//			FacesContext.getCurrentInstance().addMessage(null, msg);
//		}
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
		cliente.setCnpj(receita.getReceitaCNPJ());
		cliente.setRazaoSocial(receita.getReceitaRazao());
		cliente.setNomeFantasia(receita.getReceitaFantasia());
		cliente.setInscEstadual(receita.getReceitaIE());
		if (!receita.getReceitaCep().equalsIgnoreCase("00000-000")){
			endereco = enderecoDao.listCep(receita.getReceitaCep());
			endComplemento.setComplemento(receita.getReceitaComplemento());
			endComplemento.setNumero(receita.getReceitaNumero());
			System.out.println(receita.getReceitaRegime().toUpperCase()+ " tamanho do campo: " + receita.getReceitaRegime().length());
		}
		if (receita.getReceitaRegime().equalsIgnoreCase("NORMAL - REGIME PERIÃ“DICO DE APURAÃ‡ÃƒO") || receita.getReceitaRegime().equalsIgnoreCase("NORMAL")){
			cliente.setEnquadramento(Enquadramento.Normal);
		}else {
			cliente.setEnquadramento(Enquadramento.SimplesNacional);
			System.out.println(cliente.getEnquadramento().toString());
		}

	}

	/**
	 * 
	 * @return Lista de paises
	 */
	public List<Pais> setListaPaises(){
		return this.paisDao.listaPaisAtivo();
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

}   
