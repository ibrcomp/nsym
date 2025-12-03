package br.com.nsym.infraestrutura.picketlink;

import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.config.IdentityConfiguration;
import org.picketlink.idm.internal.DefaultPartitionManager;

/**
 * Uma implementacao customizada do partition manager para que o sistema possa
 * produzir uma versao customizada do gerenciador de relacionamentos
 * 
 * @author Ibrahim Yousef Quatani
 *
 * @version 2.0.0
 * @since 1.1.0, 19/10/2016
 */
public class CustomPartitionManager extends DefaultPartitionManager {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     * @param configuration 
     */
    public CustomPartitionManager(IdentityConfiguration configuration) {
        super(configuration);
    }

    /**
     * 
     * @return 
     */
    @Override
    public RelationshipManager createRelationshipManager() {
        return new CustomRelationshipManager(this);
    }
}
