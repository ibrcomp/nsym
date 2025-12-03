package br.com.nsym.application.controller.tools;

import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.picketlink.Identity;

import br.com.nsym.application.controller.AbstractBean;
import br.com.nsym.domain.misc.ex.InternalServiceError;
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.Filial;
import br.com.nsym.domain.model.entity.security.UserTypeEntity;
import br.com.nsym.domain.model.entity.tools.Configuration;
import br.com.nsym.domain.model.repository.cadastro.EmpresaRepository;
import br.com.nsym.domain.model.repository.cadastro.FilialRepository;
import br.com.nsym.domain.model.repository.tools.ConfigurationRepository;
import br.com.nsym.domain.model.security.Group;
import br.com.nsym.domain.model.security.User;
import br.com.nsym.domain.model.service.AccountService;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Ibrahim Yousef quatani
 *
 * @version 2.0.0
 * @since 1.0.0, 25/10/2016
 */
@Named
@ViewScoped
public class UserBean extends AbstractBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4889021078844783286L;

	@Getter
	private User user;

	@Getter
	@Setter
	private Empresa empresa = new Empresa();


	@Getter
	@Setter
	private Filial filial = new Filial();


	@Inject
	private EmpresaRepository empresaDao;

	@Inject
	private FilialRepository filialDao;

	@Getter
	private List<User> users;
	@Getter
	private List<Group> groups;

	@Inject
	private Identity identity;

	@Inject
	private AccountService accountService;
	
	@Getter
	@Setter
	private Configuration config ;
	
	@Inject
	private ConfigurationRepository configDao;

	/**
	 *
	 */
	 public void initializeListing() {
		 this.viewState = ViewState.LISTING;
		 this.users = this.accountService.listUsers(null);
	 }

	 /**
	  * Inicializa a parte do profile do usuario
	  */
	 public void initializeProfile() {

		 // setamos no usuario o usuario autenticado e seu perfil
		 this.user = (User)this.identity.getAccount();
		 if (this.user.getConfig() != null) {
			 this.config = this.configDao.findById(this.user.getConfig().getId(), false);
		 }else {
			 this.config = new Configuration();
		 }
		 if (this.user.getIdEmpresa() != null){
			 this.empresa = empresaDao.findById(user.getIdEmpresa(), false);
		 }
		 if(this.user.getIdFilial() != null){
			 this.filial = filialDao.findById(user.getIdFilial(), false);
		 }
	 }

	 /**
	  * @param userId
	  */
	 public void initializeForm(String userId) {

		 this.groups = this.accountService.listGroups(null);

		 if (userId.isEmpty()) {
			 this.viewState = ViewState.ADDING;
			 this.user = new User();
			 this.config = new Configuration();
		 } else {
			 this.viewState = ViewState.EDITING;
			 this.user = this.accountService.findUserById(userId);
			 if (this.user.getConfig() != null) {
				 this.config = this.configDao.findById(this.user.getConfig().getId(), false);
			 }else {
				 this.config = new Configuration();
			 }
			 if (this.user.getIdEmpresa() != null){
				 this.empresa = empresaDao.findById(user.getIdEmpresa(), false);
			 }
			 if(this.user.getIdFilial() != null){
				 this.filial = filialDao.findById(user.getIdFilial(), false);
			 }
		 }
	 }

	 /**
	  * @return o form de inclusao
	  */
	 public String changeToAdd() {
		 return "formUser.xhtml?faces-redirect=true";
	 }

	 /**
	  * @param userId
	  * @return
	  */
	 public String changeToEdit(String userId) {
		 return "formUser.xhtml?faces-redirect=true&userId=" + userId;
	 }

	 /**
	  * @param userId
	  */
	 public void changeToDelete(String userId) {
		 this.user = this.accountService.findUserById(userId);
		 this.updateAndOpenDialog("deleteUserDialog", "dialogDeleteUser");
	 }

	 /**
	  *
	  */
	 @Transactional
	 public void doSave() {

		 try {
			 if(this.empresa != null){
				 this.user.setIdEmpresa(this.empresa.getId());
				 if(this.filial != null){
					 this.user.setIdFilial(this.filial.getId());
				 }else {
					 this.user.setIdFilial(null);
				 }
			 }
			 if (this.empresa == null && this.filial != null){

				 this.addError(true, "save.error.empresaNotEmpty");
				 return;
			 }
			 if (this.empresa == null && this.filial == null){
				 this.user.setIdEmpresa(null);
				 this.user.setIdFilial(null);
			 }
			 this.user.setConfig(this.config);
			 this.accountService.save(this.user);
			 this.user = new User();
			 this.addInfo(true, "user.saved");
		 } catch (InternalServiceError ex) {
			 this.addError(true, ex.getMessage(), ex.getParameters());
		 } catch (Exception ex) {
			 this.addError(true, "error.undefined-error", ex.getMessage());
		 }
	 }


	 /**
	  *
	  */
	 @Transactional
	 public void doUpdate() {

		 try {
			 if (this.empresa != null){
				 this.user.setIdEmpresa(this.empresa.getId());
				 if (this.filial != null){
					 this.user.setIdFilial(this.filial.getId());
				 }else {
					 this.user.setIdFilial(null);
				 }
			 }

			 if (this.empresa == null && this.filial != null){

				 this.addError(true, "save.error.empresaNotEmpty");
				 return;
			 }
			 if (this.empresa == null && this.filial == null){
				 this.user.setIdEmpresa(null);
				 this.user.setIdFilial(null);
			 }
			 
			 if (this.config.getId() == null) {
				 this.config = this.configDao.save(this.config);
			 }else {
				 this.config = (this.configDao.findById(this.user.getConfig().getId(), false));
			 }
			 this.user.setConfig(this.config);
			 this.accountService.update(this.user);

			 this.addInfo(true, "user.updated");
		 } catch (InternalServiceError ex) {
			 this.addError(true, ex.getMessage(), ex.getParameters());
		 } catch (Exception ex) {
			 this.addError(true, "error.undefined-error", ex.getMessage());
		 }
	 }

	 /**
	  *
	  */
	 @Transactional
	 public void doDelete() {

		 try {
			 this.accountService.delete(this.user);
			 this.users = this.accountService.listUsers(null);

			 this.addInfo(true, "user.deleted");
		 } catch (InternalServiceError ex) {
			 this.addError(true, ex.getMessage(), ex.getParameters());
		 } catch (Exception ex) {
			 this.logger.error(ex.getMessage(), ex);
			 this.addError(true, "error.undefined-error", ex.getMessage());
		 }finally {
			 this.updateComponent("usersList");
			 this.closeDialog("dialogDeleteUser");
		 }
	 }

	 /**
	  *
	  */
	 @Transactional
	 public void doProfileUpdate() {

		 try {
			 if (this.empresa != null){
				 this.user.setIdEmpresa(this.empresa.getId());
				 if (this.filial != null){
					 this.user.setIdFilial(this.filial.getId());
				 }
			 }
			 if (this.config.getId() == null) {
				 this.config = this.configDao.save(this.config);
			 }else {
				 this.user.setConfig(this.configDao.findById(((User) this.identity.getAccount()).getConfig().getId(), false));
			 }
			 System.out.println("User: " + this.user.getId() + " user-Config: " + this.user.getConfig().getId() );
			 this.accountService.updateProfile(this.user);
			 this.addInfo(true, "user.profile-updated");
		 } catch (InternalServiceError ex) {
			 this.addError(true, ex.getMessage(), ex.getParameters());
		 } catch (Exception ex) {
			 this.logger.error(ex.getMessage(), ex);
			 this.addError(true, "error.undefined-error", ex.getMessage());
		 }
	 }

	 /**
	  * Invoca a troca do tema selecionado pelo usuario
	  * 
	  * @param theme o tema que sera usado
	  */
	 public void changeTheme(String theme) {

		 // remove o tema atual
		 this.executeScript("$(\"body\").removeClass('"
				 + this.user.getTheme() + "')");

		 // coloca o novo
		 this.executeScript("$(\"body\").addClass('" + theme + "')");

		 // seta no usuario para quando for salvo
		 this.user.setTheme(theme);
	 }

	 /**
	  * @return
	  */
	 public String doCancel() {
		 return "listUsers.xhtml?faces-redirect=true";
	 }


	 /**
	  * @return
	  */
	 public String toDashboard() {
		 return "/main/dashboard.xhtml?faces-redirect=true";
	 }
}