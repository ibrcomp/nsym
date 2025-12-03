package br.com.nsym.domain.model.security;

import org.picketlink.idm.model.AbstractPartition;
import org.picketlink.idm.model.annotation.IdentityPartition;

/**
 *
 * @author Ibrahim Yousef Quatani
 *
 * @version 1.0.0
 * @since 2.2.0, 22/10/2016
 */
@IdentityPartition(supportedTypes = {User.class, Role.class, Group.class})
public class Partition extends AbstractPartition {

    public static final String DEFAULT = "default";

    /**
     * 
     */
    private Partition() { 
        super(null); 
    }

    /**
     * 
     * @param name 
     */
    public Partition(String name) {
        super(name);
    }
}
