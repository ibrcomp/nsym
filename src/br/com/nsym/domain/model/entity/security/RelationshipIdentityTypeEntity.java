package br.com.nsym.domain.model.entity.security;

import org.picketlink.idm.jpa.annotations.OwnerReference;
import org.picketlink.idm.jpa.annotations.RelationshipDescriptor;
import org.picketlink.idm.jpa.annotations.RelationshipMember;
import org.picketlink.idm.jpa.annotations.entity.IdentityManaged;
import org.picketlink.idm.model.Relationship;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 *
  * @author Ibrahim Yousef Quatani
 *
 * @version 2.0.0
 * @since 1.1.0, 19/10/2016
 */
@Entity
@IdentityManaged(Relationship.class)
@Table(name = "identity_relationships")
public class RelationshipIdentityTypeEntity implements Serializable {

    private static final long serialVersionUID = -2480755862368222575L;
	@Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;
    @Getter
    @Setter
    @RelationshipDescriptor
    @Column(name = "descriptor")
    private String descriptor;
    @Getter
    @Setter
    @RelationshipMember
    @Column(name = "identity_type")
    private String identityType;
    
    @Getter
    @Setter
    @ManyToOne
    @OwnerReference
    @JoinColumn(name = "id_owner")
    private RelationshipTypeEntity owner;
}
