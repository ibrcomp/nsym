package br.com.nsym.domain.model.security;

import lombok.Getter;
import lombok.Setter;
import org.picketlink.idm.model.AbstractIdentityType;
import org.picketlink.idm.model.annotation.AttributeProperty;
import org.picketlink.idm.model.annotation.IdentityStereotype;
import org.picketlink.idm.model.annotation.StereotypeProperty;
import org.picketlink.idm.model.annotation.Unique;
import org.picketlink.idm.query.QueryParameter;
import static org.picketlink.idm.model.annotation.IdentityStereotype.Stereotype.ROLE;
import static org.picketlink.idm.model.annotation.StereotypeProperty.Property.IDENTITY_ROLE_NAME;

/**
 *
 * @author Ibrahim Yousef Quatani
 *
 * @version 1.0.0
 * @since 2.2.0, 22/10/2016
 */
@IdentityStereotype(ROLE)
public class Role extends AbstractIdentityType {

    @Getter
    @Setter
    @Unique
    @AttributeProperty
    @StereotypeProperty(IDENTITY_ROLE_NAME)
    private String authorization;
    
    public static final QueryParameter AUTHORIZATION = QUERY_ATTRIBUTE.byName("authorization");

    /**
     * 
     */
    public Role() { }

    /**
     * 
     * @param authorization 
     */
    public Role(String authorization) {
        this.authorization = authorization;
    }
}
