package br.com.nsym.domain.model.security;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.picketlink.idm.model.AbstractAttributedType;
import org.picketlink.idm.model.Account;
import org.picketlink.idm.model.Relationship;
import org.picketlink.idm.model.annotation.InheritsPrivileges;
import org.picketlink.idm.model.annotation.RelationshipStereotype;
import org.picketlink.idm.model.annotation.StereotypeProperty;
import org.picketlink.idm.query.RelationshipQueryParameter;
import static org.picketlink.idm.model.annotation.RelationshipStereotype.Stereotype.GROUP_MEMBERSHIP;
import static org.picketlink.idm.model.annotation.StereotypeProperty.Property.RELATIONSHIP_GROUP_MEMBERSHIP_GROUP;
import static org.picketlink.idm.model.annotation.StereotypeProperty.Property.RELATIONSHIP_GROUP_MEMBERSHIP_MEMBER;

/**
 *
  * @author Ibrahim Yousef Quatani
 *
 * @version 1.0.0
 * @since 2.2.0, 22/10/2016
 */
@RelationshipStereotype(GROUP_MEMBERSHIP)
public class GroupMembership extends AbstractAttributedType implements Relationship {

    @Getter
    @Setter
    @NotNull(message = "{user.no-group}")
    @StereotypeProperty(RELATIONSHIP_GROUP_MEMBERSHIP_GROUP)
    private Group group;
    @Getter
    @Setter
    @InheritsPrivileges("group")
    @StereotypeProperty(RELATIONSHIP_GROUP_MEMBERSHIP_MEMBER)
    private Account member;

    public static final RelationshipQueryParameter GROUP = RELATIONSHIP_QUERY_ATTRIBUTE.byName("group");
    public static final RelationshipQueryParameter MEMBER = RELATIONSHIP_QUERY_ATTRIBUTE.byName("member");
    
    /**
     * 
     */
    public GroupMembership() { }

    /**
     * 
     * @param member
     * @param group 
     */
    public GroupMembership(Group group, Account member) {
        this.group = group;
        this.member = member;
    }
}
