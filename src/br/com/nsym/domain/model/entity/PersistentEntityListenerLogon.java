package br.com.nsym.domain.model.entity;

import java.util.Date;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.omnifaces.util.BeansLocal;
import org.picketlink.Identity;

import br.com.nsym.domain.model.security.User;

/**
 * Listener de edicao e persistencia dos dados, com ele preenchemos os valores
 * default de nossas entidades exclusivo para controle de logons
 *
 * @author Ibrahim Yousef Quatani
 *
 * @version 1.0.0
 * @since 2.2.0, 30/08/2017
  */
public class PersistentEntityListenerLogon {

    @Inject
    @Default
    private BeanManager beanManager;

    /**
     * Listerner de pre-persistencia do dados
     * 
     * @param entity a entidade a ser afetada pelo evento
     */
    @PrePersist
    public void prePersist(PersistentEntityLogon entity) {
    	if (entity.getId() == null){
        entity.setInclusion(new Date());
        entity.setDeleted(false);
    	}else {
    		entity.setLastEdition(new Date());
    	}
        
    }
    
    /**
     * Listerner de pre-atualizacao do dados
     * 
     * @param entity a entidade a ser afetada pelo evento
     */
    @PreUpdate
    public void preUpdate(PersistentEntityLogon entity) {
        entity.setLastEdition(new Date());
        entity.setEditedBy(this.getAuthenticated().getUsername());
    }
    
    /**
     * @return o usuario autenticado
     */
    private User getAuthenticated() {

        final Identity identity = BeansLocal.getInstance(
                this.beanManager, Identity.class);
        
        return (User) identity.getAccount();
    }
}

