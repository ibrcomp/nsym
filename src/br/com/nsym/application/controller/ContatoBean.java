package br.com.nsym.application.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.SortOrder;

import br.com.nsym.application.component.table.AbstractLazyModel;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.model.entity.cadastro.Contato;
import br.com.nsym.domain.model.entity.cadastro.Email;
import br.com.nsym.domain.model.entity.cadastro.Fone;
import br.com.nsym.domain.model.entity.cadastro.TipoCadastro;
import br.com.nsym.domain.model.entity.tools.Operadora;
import br.com.nsym.domain.model.repository.cadastro.ContatoRepository;
import br.com.nsym.domain.model.repository.cadastro.EmailRepository;
import br.com.nsym.domain.model.repository.cadastro.FoneRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class ContatoBean extends AbstractBeanEmpDS<Contato> {


	/**
	 *
	 */
	private static final long serialVersionUID = -4601822592417774287L;

	@Getter
	@Setter
	private Contato contatoSelecionado = new Contato();

	@Getter
	@Setter
	private  Fone fone;

	@Getter
	@Setter
	private  Email email;

	@Inject
	private FoneRepository foneDao;

	@Inject
	private ContatoRepository contatoDao;

	@Inject
	private EmailRepository emailDao;

	@Getter
	private AbstractLazyModel<Contato> contatoModel;

	@Getter
	@Setter
	private boolean contatoDisponivel = false;

	@Getter
	@Setter
	private boolean alteraEmail = false;
	
	@Getter
	@Setter
	private TipoCadastro tipoCad;

//	@PostConstruct
//	public void init(){
//		contatoModel = getLazyContatosPorEmpresa();
//	}

	public void onRowSelect(SelectEvent event)throws IOException{
		FacesMessage msg = new FacesMessage("Contato "+((Contato) event.getObject()).getNome() +" selecionado");  
		FacesContext.getCurrentInstance().addMessage(null, msg);
		this.contatoSelecionado = ((Contato) event.getObject());
		setContatoDisponivel(true);
		setAlteraEmail(true);
		System.out.println("Estou o RowSelect");
		this.viewState = ViewState.EDITING;
		this.fone = new Fone();
		this.email = new Email();
	}
	public void onRowUnselect(UnselectEvent event) {  
		FacesMessage msg = new FacesMessage("Contato "+((Contato) event.getObject()).getNome() +" foi cancelada a seleção");  
		FacesContext.getCurrentInstance().addMessage(null, msg);
		System.out.println("UnselectedEvent - Contato");
		this.contatoSelecionado = new Contato();
		limpaCampos();

	}  

	public void editaFoneContato(Fone id){
		System.out.println(id.getId() +" - "+ id.getFone() );
		setAlteraEmail(false);
		this.fone = new Fone();
		this.fone = id;
		this.contatoSelecionado = id.getContato();
		setContatoDisponivel(true);
	}

	public void editaEmailContato(Email id){
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Vôce está adcionando um email para "+ id.getContato().getNome(), null);
		FacesContext.getCurrentInstance().addMessage(null, msg);
		setAlteraEmail(true);
		setContatoDisponivel(false);
		this.contatoSelecionado = id.getContato();
		System.out.println("contao selecionado é " + id.getContato().getNome());
		this.fone = new Fone();
		this.email = new Email();
		this.email = id;

	}

	public List<Fone> getListaFone(){
		return foneDao.listaFonePorContato(contatoSelecionado);
	}

	@Transactional
	public void doSalva(){
		try{
			FacesMessage msg = new FacesMessage();
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"estou no DoSalva", null);
			System.out.println("estou no DoSalva");
			if (this.contatoSelecionado.getId() != null){
				msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"estou no contatoSelecionado diferente de null", null);
				if ((this.fone.getId() == null) && (this.fone.getFone() != 0 )){
					this.fone.setContato(contatoSelecionado);
					this.fone.setDeleted(false);
					foneDao.save(this.fone);
					msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"O Cadastro do Telefone " + fone.getFone() + " foi concluído! ", null);
				}else if ((this.fone.getId() != null) && (this.fone.getFone() != 0)){
					msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"A Alteração do Telefone " + fone.getFone() + " foi concluído! ", null);
					foneDao.save(this.fone);
				}
				if ((alteraEmail == true) ){
					System.out.println(this.email.getEmail()+ "contato " + this.contatoSelecionado.getNome());
					if(this.email.getEmail().isEmpty() == false && this.email.getId() == null){
						msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"O Cadastro do Email " + email.getEmail() + " foi concluído! ", null);
						this.email.setContato(contatoSelecionado);
						this.email.setDeleted(false);
						emailDao.save(this.email);
					}else if (this.email.getId() != null){
						emailDao.save(this.email);
					}
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, msg);
			limpaCampos();
		}catch (Exception e){

		}
	}
	public void doCancela(){
		limpaCampos();
	}

	public Operadora[] getOperadoraType(){
		return Operadora.values();
	}
	@Override
	public Contato setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Contato setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

	public void limpaCampos(){
		this.contatoSelecionado = new Contato();
		this.email = new Email();
		this.fone = new Fone();
		setContatoDisponivel(false);
		setAlteraEmail(false);

	}

	/**
	 * 
	 * @param id - id da classe  a ser pesquisada
	 * @param idEmpresa -  id da Empresa
	 * @param idFilial - id da Filial
	 * @param tipo - Tipo de Cadastro
	 * @return os contatos em modo Lazy
	 */
	public AbstractLazyModel<Contato> getLazyContatosPorEmpresa(Long id,Long idEmpresa,Long idFilial, TipoCadastro tipo){

		this.contatoModel = new AbstractLazyModel<Contato>() {
			/**
			 *
			 */
			private static final long serialVersionUID = -2274325506136912707L;

			@Override
			public List<Contato> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no contatoModel");
				PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());
				Page<Contato> page = new Page<Contato>();
				
				page = contatoDao.listaContatoLazyComFiltro(false, null, idEmpresa, idFilial, id, tipo, pageRequest, sortField, sortField, false);

				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return contatoModel;
	}

	@Override
	public void initializeListing() {
		// TODO Auto-generated method stub
		
	}
	public void initializeFormT(Long id,String tipo) {
		if (id != null) {
			tipoCad = TipoCadastro.valueOf(tipo);
			this.contatoModel = getLazyContatosPorEmpresa(id,pegaIdEmpresa(),pegaIdFilial(),tipoCad);
		}
	}
	public TipoCadastro[] listaTipoCadastro() {
		return TipoCadastro.values();
	}
	@Override
	public void initializeForm(Long id) {
		// TODO Auto-generated method stub
		
	}

}
