package br.com.nsym.domain.model.entity.security;

import java.io.Serializable;
import javax.persistence.Column;
import org.picketlink.idm.jpa.annotations.AttributeValue;
import org.picketlink.idm.jpa.annotations.Identifier;
import org.picketlink.idm.jpa.annotations.PartitionClass;
import org.picketlink.idm.jpa.annotations.entity.ConfigurationName;
import org.picketlink.idm.jpa.annotations.entity.IdentityManaged;
import org.picketlink.idm.model.Partition;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode
@Table(name = "partitions")
@IdentityManaged(Partition.class)
@Inheritance(strategy = InheritanceType.JOINED)
public class PartitionTypeEntity implements Serializable {

    private static final long serialVersionUID = -8866717893808962829L;
	@Id
    @Identifier
    @Column(name = "id", unique = true)
    private String id;
    @Getter
    @Setter
    @AttributeValue
    @Column(name = "name")
    private String name;
    @Getter
    @Setter
    @PartitionClass
    @Column(name = "type_name")
    private String typeName;
    @Getter
    @Setter
    @ConfigurationName
    @Column(name = "configuration")
    private String configuration;
}
