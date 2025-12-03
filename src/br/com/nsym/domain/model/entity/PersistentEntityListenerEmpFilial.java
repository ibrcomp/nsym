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
 * default de nossas entidades
 *
 * @author Ibrahim Yousef Quatani
 *
 * @version 1.0.0
 * @since 2.2.0, 19/10/2016
  */
public class PersistentEntityListenerEmpFilial {

    @Inject
    @Default
    private BeanManager beanManager;

    /**
     * Listerner de pre-persistencia do dados
     * 
     * @param entity a entidade a ser afetada pelo evento
     */
    @PrePersist
    public void prePersist(PersistentEntitySemEmpFilial entity) {
    	if (entity.getId() == null){
    		entity.setInclusion(new Date());
    		entity.setIncludedBy(this.getAuthenticated().getUsername());
    		if (entity.getIdEmpresa() == null ) {
    			entity.setIdEmpresa(this.getAuthenticated().getIdEmpresa());
    		}
    		if (entity.getIdFilial() == null) {
    			entity.setIdFilial(this.getAuthenticated().getIdFilial());
    		}
    	}else {
    		entity.setEditedBy(this.getAuthenticated().getUsername());
    		entity.setLastEdition(new Date());
    	}

    }
    
    /**
     * Listerner de pre-atualizacao do dados
     * 
     * @param entity a entidade a ser afetada pelo evento
     */
    @PreUpdate
    public void preUpdate(PersistentEntitySemEmpFilial entity) {
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

