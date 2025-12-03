package br.com.nsym.domain.model.security;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.picketlink.idm.model.AbstractIdentityType;
import org.picketlink.idm.model.annotation.AttributeProperty;
import org.picketlink.idm.model.annotation.IdentityStereotype;
import org.picketlink.idm.model.annotation.InheritsPrivileges;
import org.picketlink.idm.model.annotation.StereotypeProperty;
import org.picketlink.idm.model.annotation.Unique;
import org.picketlink.idm.query.QueryParameter;

import br.com.nsym.domain.model.service.AccountService;

import static org.picketlink.idm.model.annotation.IdentityStereotype.Stereotype.GROUP;
import static org.picketlink.idm.model.annotation.StereotypeProperty.Property.IDENTITY_GROUP_NAME;

/**
 *
 * @author Ibrahim Yousef Quatani
 *
 * @version 1.0.0
 * @since 2.2.0, 22/10/2016
 */
@IdentityStereotype(GROUP)
public class Group extends AbstractIdentityType {

    @Getter
    @Setter
    @Unique
    @AttributeProperty
    @NotEmpty(message = "{group.name}")
    @StereotypeProperty(IDENTITY_GROUP_NAME)
    private String name;
    @Getter
    @Setter
    @AttributeProperty
    @InheritsPrivileges
    private Group parent;
    
    /**
     * Cache dos grants deste grupo preenchido pelo metodo 
     * {@link AccountService#listUserGroupsAndGrants(User user)}
     */
    @Setter
    private List<Grant> grants;
    
    public static final QueryParameter NAME = QUERY_ATTRIBUTE.byName("name");
    public static final QueryParameter PARENT = QUERY_ATTRIBUTE.byName("parent");

    /**
     * 
     */
    public Group() { }

    /**
     * 
     * @param name 
     */
    public Group(String name) {
        this.name = name;
    }

    /**
     * 
     * @param name
     * @param parent 
     */
    public Group(String name, Group parent) {
        this.name = name;
        this.parent = parent;
    }
    
    /**
     * @return 
     */
    public boolean isBlocked() {
        return !this.isEnabled();
    }
    
    /**
     * @param blocked 
     */
    public void setBlocked(boolean blocked) {
        this.setEnabled(!blocked);
    }

    /**
     * @return uma lista nao modificavel dos grants
     */
    public List<Grant> getGrants() {
        return Collections.unmodifiableList(this.grants);
    }
}
