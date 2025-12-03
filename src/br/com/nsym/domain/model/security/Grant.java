package br.com.nsym.domain.model.security;

import lombok.Getter;
import lombok.Setter;
import org.picketlink.idm.model.AbstractAttributedType;
import org.picketlink.idm.model.IdentityType;
import org.picketlink.idm.model.Relationship;
import org.picketlink.idm.model.annotation.InheritsPrivileges;
import org.picketlink.idm.model.annotation.RelationshipStereotype;
import org.picketlink.idm.model.annotation.StereotypeProperty;
import org.picketlink.idm.query.RelationshipQueryParameter;
import static org.picketlink.idm.model.annotation.RelationshipStereotype.Stereotype.GRANT;
import static org.picketlink.idm.model.annotation.StereotypeProperty.Property.RELATIONSHIP_GRANT_ASSIGNEE;
import static org.picketlink.idm.model.annotation.StereotypeProperty.Property.RELATIONSHIP_GRANT_ROLE;

/**
 *
 * @author Ibrahim Yousef Quatani
 *
 * @version 1.0.0
 * @since 2.2.0, 22/10/2016
 */
@RelationshipStereotype(GRANT)
public class Grant extends AbstractAttributedType implements Relationship {

    @Getter
    @Setter
    @InheritsPrivileges("role")
    @StereotypeProperty(RELATIONSHIP_GRANT_ASSIGNEE)
    private IdentityType assignee;
    @Getter
    @Setter
    @StereotypeProperty(RELATIONSHIP_GRANT_ROLE)
    private Role role;
    
    public static final RelationshipQueryParameter ROLE = RELATIONSHIP_QUERY_ATTRIBUTE.byName("role");
    public static final RelationshipQueryParameter ASSIGNEE = RELATIONSHIP_QUERY_ATTRIBUTE.byName("assignee");

    /**
     * 
     */
    public Grant() { }

    /**
     * 
     * @param assignee
     * @param role 
     */
    public Grant(Role role, IdentityType assignee) {
        this.role = role;
        this.assignee = assignee;
    }
    
    /**
     * 
     * @return 
     */
    public String getGrantAuthorization() {
        return role.getAuthorization();
    }
    
    /**
     * 
     * @return 
     */
    public String getAuthorization() {
        return this.role.getAuthorization();
    }
}