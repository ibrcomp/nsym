package br.com.nsym.application.controller.tools;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.SortOrder;

import br.com.nsym.application.component.table.AbstractLazyModel;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.application.controller.AbstractBean;
import br.com.nsym.application.producer.qualifier.AuthenticatedUser;
import br.com.nsym.domain.misc.ex.InternalServiceError;
import br.com.nsym.domain.model.entity.tools.Message;
import br.com.nsym.domain.model.entity.tools.MessagePriorityType;
import br.com.nsym.domain.model.security.User;
import br.com.nsym.domain.model.service.AccountService;
import br.com.nsym.domain.model.service.MessagingService;
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
public class MessageSentBean extends AbstractBean {

    @Getter
    @Setter
    private String filter;

    @Getter
    @Setter
    private Message message;

    @Getter
    private List<User> users;

    @Getter
    @Inject
    @AuthenticatedUser
    private User authenticatedUser;

    @Inject
    private AccountService accountService;
    @Inject
    private MessagingService messagingService;

    @Getter
    private final AbstractLazyModel<Message> messagesModel;

    /**
     *
     */
    public MessageSentBean() {
       
        this.messagesModel = new AbstractLazyModel<Message>() {
            @Override
            public List<Message> load(int first, int pageSize, String sortField,
                    SortOrder sortOrder, Map<String, Object> filters) {

                // constroi o filtro
                final PageRequest pageRequest = new PageRequest();

                pageRequest
                        .setFirstResult(first)
                        .withPageSize(pageSize)
                        .sortingBy(sortField, "inclusion")
                        .withDirection(sortOrder.name());

                final Page<Message> page = messagingService
                        .listSentMessages(filter, pageRequest);

                this.setRowCount(page.getTotalPagesInt());

                return page.getContent();
            }
        };
    }

    /**
     *
     */
    public void initializeList() {
        this.viewState = ViewState.LISTING;
    }

    /**
     *
     * @param messageId
     * @param viewState
     */
    public void initializeForm(long messageId, String viewState) {

        // capturamos o estado da tela 
        this.viewState = ViewState.valueOf(viewState);
        
        final List<User> allUsers = this.accountService.listUsers(Boolean.FALSE);

        // remove o usuario logado da lista de destinatarios
        this.users = allUsers.stream()
                .filter(user -> !user.getId().equals(this.authenticatedUser.getId()))
                .collect(Collectors.toList());
        
        // inicia a mensagem
        this.message = new Message(this.authenticatedUser);
    }
    
    /**
     * 
     * @param messageId
     * @param viewState 
     */
    public void initializeDetailing(long messageId, String viewState) {
        
        // capturamos o estado da tela 
        this.viewState = ViewState.valueOf(viewState);
        
        // inicia a mensagem
        this.message = this.messagingService.findMessageById(messageId, true);
    }

    /**
     * 
     */
    public void doSave() {
        try {
            this.messagingService.sendMessage(this.message);
            this.message = new Message(this.authenticatedUser);
            this.addInfo(true, "message.sent");
        } catch (InternalServiceError ex) {
            this.addError(true, ex.getMessage(), ex.getParameters());
        } catch (Exception ex) {
            this.logger.error(ex.getMessage(), ex);
            this.addError(true, "error.undefined-error", ex.getMessage());
        }
    }
    
    /**
     * 
     * @return 
     */
    public String doDelete() {
        try {
            this.messagingService.deleteMessage(this.message);
            return this.changeTolist();
        } catch (InternalServiceError ex) {
            this.addError(true, ex.getMessage(), ex.getParameters());
            return null;
        } catch (Exception ex) {
            this.logger.error(ex.getMessage(), ex);
            this.addError(true, "error.undefined-error", ex.getMessage());
            return null;
        }
    }
    
    /**
     * @return redireciona para a pagina de cadastro
     */
    public String changeToAdd() {
        return "formSentMessage.xhtml?faces-redirect=true&viewState="
                + ViewState.ADDING;
    }

    /**
     * Redireciona para pagina de detalhes do vendedor
     */
    public void changeToDetail() {
        this.redirectTo("detailSentMessage.xhtml?faces-redirect=true&id="
                + this.message.getId() + "&viewState=" + ViewState.DETAILING);
    }

    /**
     * @param sellerId
     * @return
     */
    public String changeToDelete(String sellerId) {
        return "detailSentMessage.xhtml?faces-redirect=true&id=" + sellerId
                + "&viewState=" + ViewState.DELETING;
    }

    /**
     * @return volta para a listagem
     */
    public String changeTolist() {
        return "listSentMessages.xhtml?faces-redirect=true";
    }

    /**
     * @return as prioridades
     */
    public MessagePriorityType[] getPriorities() {
        return MessagePriorityType.values();
    }
}
