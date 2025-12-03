package br.com.nsym.application.controller;

import java.io.IOException;
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
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

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
import br.com.nsym.domain.model.entity.cadastro.Colaborador;
import br.com.nsym.domain.model.entity.cadastro.Contato;
import br.com.nsym.domain.model.entity.cadastro.Email;
import br.com.nsym.domain.model.entity.cadastro.EndComplemento;
import br.com.nsym.domain.model.entity.cadastro.Endereco;
import br.com.nsym.domain.model.entity.cadastro.Fone;
import br.com.nsym.domain.model.entity.cadastro.Pais;
import br.com.nsym.domain.model.entity.cadastro.TipoCadastro;
import br.com.nsym.domain.model.entity.financeiro.Credito;
import br.com.nsym.domain.model.entity.tools.TipoColaborador;
import br.com.nsym.domain.model.repository.cadastro.ColaboradorRepository;
import br.com.nsym.domain.model.repository.cadastro.ContatoRepository;
import br.com.nsym.domain.model.repository.cadastro.EmailRepository;
import br.com.nsym.domain.model.repository.cadastro.EndComplementoRepository;
import br.com.nsym.domain.model.repository.cadastro.EnderecoRepository;
import br.com.nsym.domain.model.repository.cadastro.FoneRepository;
import br.com.nsym.domain.model.repository.cadastro.PaisRepository;
import br.com.nsym.domain.model.repository.financeiro.CreditoRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class ColaboradorBean extends AbstractBeanEmpDS<Colaborador> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7790725847122618736L;

	@Getter
	@Setter
	private Colaborador colaborador = new Colaborador();

	@Inject
	private ColaboradorRepository colaboradorDao;

	@Getter
	@Setter
	private boolean isDeleted = false;
	
	@Getter
	@Setter
	private Pais pais;
	
	@Inject
	private PaisRepository paisDao;

	@Getter
	@Setter
	private TipoColaborador tipoColaborador;

	@Getter
	private AbstractLazyModel<Colaborador> colaboradorModel;

	@Getter
	@Setter
	private Endereco endereco = new Endereco();

	@Inject
	private EnderecoRepository enderecoDao;

	@Getter
	@Setter
	private EndComplemento endComplemento = new EndComplemento();

	@Inject
	private EndComplementoRepository endComplementoDao;

	@Getter
	private List<Endereco> listaEncontrada =new ArrayList<>();

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
	private boolean visivelPorIdColaborador = false;
	
	@Getter
	private AbstractLazyModel<Contato> contatoModel;

	@Getter
	@Setter
	private Fone fone = new Fone();

	@Setter
	private List<Fone> listaFone;
	
	@Inject
	private FoneRepository foneDao;

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
	@Setter
	private Boolean consultaPorCepVisivel = true;
	
	@Getter
	@Setter
	private Credito credito;
	
	@Inject
	private CreditoRepository creditoDao;
	
	@PostConstruct
	public void init(){
		this.colaboradorModel = getLazyColaborador();
	}

	public AbstractLazyModel<Colaborador> getLazyColaborador(){
		this.colaboradorModel = new AbstractLazyModel<Colaborador>() {
			/**
			 *
			 */
			private static final long serialVersionUID = 4478584902195892733L;

			@Override
			public List<Colaborador> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no filiaisModel");
				final PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());
				
				Page<Colaborador> page = colaboradorDao.listaLazyComFiltro(isDeleted, null, pegaIdEmpresa(), pegaIdFilial(), pageRequest, null, null, true);

				this.setRowCount(page.getTotalPagesInt());
				if (filters != null){
					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
						try{
							String filterProperty = it.next();
							Object filterValue = filters.get(filterProperty);
							page = colaboradorDao.listaLazyComFiltro(isDeleted(),null,pegaIdEmpresa(), pegaIdFilial(), pageRequest, filterProperty, filterValue.toString().toUpperCase(),true);
							this.setRowCount(page.getTotalPagesInt());
						} catch(Exception e) {
	                    	System.out.println(e.getMessage());
	                    }
					}
				}
				return page.getContent();
			}
		};
		return colaboradorModel;
	}

	/**
	 * 
	 * @param idColadorador
	 */
	@Transactional
	public void initializeForm(Long idColaborador) {
		if (idColaborador == null ) {
			this.viewState = ViewState.ADDING;
			this.colaborador = new Colaborador();
			this.credito = new Credito();
		} else {
			this.viewState = ViewState.EDITING;
			this.setVisivelPorIdColaborador(true);
			this.colaborador = this.colaboradorDao.findById(idColaborador, false);
			this.colaborador.setListaCredito(this.creditoDao.retornaListaCredito(null, null, this.colaborador, pegaIdEmpresa(), pegaIdFilial()));
			if (this.colaborador.getEndereco() != null){
				this.endereco = enderecoDao.listCep(colaborador.getEndereco().getEndereco().getCep());
				this.endComplemento = endComplementoDao.pegaEndComplementoPorColaborador(colaborador);
				if (this.endComplemento.getLogradouro() == null || this.endComplemento.getLogradouro().isEmpty()) {
					this.endComplemento.setLogradouro(this.endereco.getLogra());
					this.endComplemento.setBairro(this.endereco.getBairro());
				}else {
					this.endereco.setBairro(this.endComplemento.getBairro());
					this.endereco.setLogra(this.endComplemento.getLogradouro());
				}
			}
			if (this.colaborador.getEmail()!= null){
				this.email = this.emailDao.pegaEmail(this.colaborador, pegaIdEmpresa());
			}
		}
	}

	/**
	 * 
	 */
	public void initializeListing() {
		this.viewState = ViewState.LISTING;
	}
	/**
	 * 
	 * Redirecionamento das p�ginas 
	 * 
	 */

	public String newColaborador() {
		return "formCadColaborador.xhtml?faces-redirect=true";
	}

	public String toListColaborador() {
		return "formListColaborador.xhtml?faces-redirect=true";
	}

	/*
	 * redireciona para a pagina com o ID do Colaborador a ser editada
	 * 
	 * @param colaboradorID
	 * 
	 * @return
	 */

	public String changeToEdit(Long colaboradorID) {
		return "formCadColaborador.xhtml?faces-redirect=true&colabID=" + colaboradorID+"&tipoCadastro="+TipoCadastro.COLAB.name();
	}

	public String recuperaEndereco(){
		this.endereco = pegaEnderecoNaSessao();
		return "PF('dialogEndereco').hide()";
	}

	/**
	 *  M�todo para sele��o do colaborador utilizado no dataTable do primeFaces
	 * @param event
	 * @throws IOException
	 */
	public void onRowSelect(SelectEvent event)throws IOException{
		FacesMessage msg = new FacesMessage("Colaborador "+((Colaborador) event.getObject()).getNome() +" selecionado(a)");  
		FacesContext.getCurrentInstance().addMessage(null, msg);
		this.colaborador = (Colaborador) event.getObject();
		System.out.println("Estou o RowSelect");
		this.viewState = ViewState.EDITING;
		this.setVisivelPorIdColaborador(true);

	}

	/**
	 * M�todos para pesquisa de enderecos
	 * 
	 */
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
		Endereco endTemp = new Endereco();
		endTemp = this.enderecoDao.procuraCepBase(cep);
		if (endTemp == null) {
			saveEnd(this.endereco);
		}else {
			this.endereco = endTemp;
		}
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
	
	public void telaPesquisaEndereco() {
		this.openDialog("dialogEndereco");
	}

	public void telaTelefone(){
		this.openDialog("dialogContatoColaborador");
	}

	@Transactional
	public void doSalvar() {
		try{
			if (this.colaborador.getId()== null){
				this.pais = this.paisDao.listaPaises("BRASIL");
				this.colaborador.setPais(this.pais);
				this.colaborador.setDeleted(false);
				this.credito.setColaborador(this.colaborador);
				this.colaborador.getListaCredito().add(this.credito);
				this.colaborador = this.colaboradorDao.save(this.colaborador);
				
				
				if (this.colaborador.getId()!= null && this.endereco.getLogra() != null){
					this.endComplemento.setColaborador(this.colaborador);
					this.endComplemento.setEndereco(this.endereco);
					this.endComplemento.setLogradouro(this.endereco.getLogra());
					this.endComplemento.setBairro(this.endereco.getBairro());
					this.endComplemento = this.endComplementoDao.save(this.endComplemento);
				}
				if (this.endComplemento.getId() != null){
					this.endComplemento.setEndereco(this.endereco);
					this.endComplemento.setLogradouro(this.endereco.getLogra());
					this.endComplemento.setBairro(this.endereco.getBairro());
					this.colaborador.setEndereco(this.endComplemento);
					this.colaborador = this.colaboradorDao.save(this.colaborador);
//					alteraEnderecoCep();
				}
				if (this.email.getEmail() != null && this.colaborador.getId() != null){
					this.email.setColaborador(this.colaborador);
					this.email = this.emailDao.save(this.email);
					this.colaborador.setEmail(this.email);
					this.colaborador = this.colaboradorDao.save(this.colaborador);
				}
				this.setVisivelPorIdColaborador(true);
				this.addInfo(true, "save.sucess");
			}else { // la�o do ATUALIZAR (UPDATE)
				if (this.colaborador.getListaCredito().size() == 0) {
					this.credito = new Credito();
					this.credito.setColaborador(this.colaborador);
					this.colaborador.getListaCredito().add(this.credito);
				}
				this.pais = this.paisDao.listaPaises("BRASIL");
				this.colaborador.setPais(this.pais);
				this.colaborador.setEmail(this.email);
				this.email.setColaborador(this.colaborador);
				this.colaborador = this.colaboradorDao.save(this.colaborador);
				this.endComplemento.setLogradouro(this.endereco.getLogra());
				this.endComplemento.setBairro(this.endereco.getBairro());
				this.endComplemento.setEndereco(this.endereco);
				this.endComplemento.setColaborador(this.colaborador);
				this.endComplemento = this.endComplementoDao.save(this.endComplemento); 
				this.addInfo(true, "save.sucess");
			}
			
		}catch (Exception e ){
			this.addError(true, "save.error", e);			
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
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"N�o foi poss�vel alterar o endere�o.", null);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}

	}
	
	@Transactional
	public String doExcluir() {
		try {
			this.colaborador.setDeleted(true);
			this.colaboradorDao.save(this.colaborador);
			return toListColaborador();
		} catch (IllegalArgumentException e) {
			System.out.println("N�o foi possivel excluir o registro " + e);
			return null;
		}
	}
	
	@Transactional
	public void doSalvaContato(){
		try{
			System.out.println("estou dentro do try" + this.colaborador.getId());
			if (this.colaborador.getId() != null){
				System.out.println("estou entro do if filial antes do contato getID"+ contato.getId());
				if (contato.getId() == null){

					System.out.println("estou no if contato.getID antes do setNome");
					this.contato.setNome(nomeContato);
					this.contato.setColaborador(getColaborador());
					this.contatoDao.save(contato);
				}else{
					System.out.println("estou no else do filial getID");
					if (getFone() != null){
						System.out.println("estou no getFone != null no else do contato.getID");
						this.fone.setContato(getContato());
						this.foneDao.save(fone);
					}
					if (getEmail() != null){
						this.email.setContato(getContato());
						this.emailDao.save(email);
					}
				}
			}else{
				this.addError(true, "save.error");
				return;
			}

		}catch (Exception e){
			this.addError(true, "save.error", e);
		}
	}
	
	public TipoColaborador[] pegaListaTipoColaborador(){
		return TipoColaborador.values();
	}
	
	public List<Colaborador> getListaColaboradoresPorFilial(){
		return this.colaboradorDao.listaPorFilial(pegaIdEmpresa(), pegaIdFilial());
	}

	@Override
	public Colaborador setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Colaborador setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

}
