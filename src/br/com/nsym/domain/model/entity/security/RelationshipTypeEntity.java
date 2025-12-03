package br.com.nsym.domain.model.entity.security;

import java.io.Serializable;
import javax.persistence.Column;
import org.picketlink.idm.jpa.annotations.Identifier;
import org.picketlink.idm.jpa.annotations.RelationshipClass;
import org.picketlink.idm.jpa.annotations.entity.IdentityManaged;
import org.picketlink.idm.model.Relationship;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
@Table(name = "relationships")
@IdentityManaged(Relationship.class)
@Inheritance(strategy = InheritanceType.JOINED)
public class RelationshipTypeEntity implements Serializable {

    private static final long serialVersionUID = -7799057802268592093L;
	@Id
    @Getter
    @Setter
    @Identifier
    @Column(name = "id", unique = true)
    private String id;
    @Getter
    @Setter
    @RelationshipClass
    @Column(name = "type_name")
    private String typeName;
}
