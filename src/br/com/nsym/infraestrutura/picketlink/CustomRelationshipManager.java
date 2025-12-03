package br.com.nsym.infraestrutura.picketlink;

import br.com.nsym.application.controller.UserSessionBean;
import br.com.nsym.domain.model.security.Role;

import javax.enterprise.inject.spi.CDI;
import org.picketlink.idm.internal.ContextualRelationshipManager;
import org.picketlink.idm.internal.DefaultPartitionManager;
import org.picketlink.idm.model.IdentityType;

/**
 * Implementacao customizada da relationshipmanager para que o metodo de 
 * checagem da heranca entre os grants de roles para grupos seja invocado 
 * direcionando para um metodo customizado do dominio da aplicacao
 * 
 * @author Ibrahim Yousef Quatani
 *
 * @version 2.0.0
 * @since 1.1.0, 19/10/2016
 */
public class CustomRelationshipManager extends ContextualRelationshipManager {

    private UserSessionBean userSessionBean;
    
    /**
     * @see ContextualRelationshipManager(org.picketlink.idm.internal.DefaultPartitionManager) 
     * 
     * @param partitionManager 
     */
    public CustomRelationshipManager(DefaultPartitionManager partitionManager) {
        super(partitionManager);
    }

    /**
     * @see #inheritsPrivileges(org.picketlink.idm.model.IdentityType, org.picketlink.idm.model.IdentityType) 
     * 
     * @param identity
     * @param assignee
     * @return 
     */
    @Override
    public boolean inheritsPrivileges(IdentityType identity, IdentityType assignee) {
        if (assignee instanceof Role) {
            final Role role = (Role) assignee;
            return this.getUserSessionBean().hasRole(role.getAuthorization());
        }
        return false;
    }
    
    /**
     * @return a instancia do gerenciador de permissoes do usuario
     */
    private UserSessionBean getUserSessionBean() {
        if (this.userSessionBean == null) {
            this.userSessionBean = CDI
                    .current()
                    .select(UserSessionBean.class)
                    .get();
        } 
        return this.userSessionBean;
    }
}
