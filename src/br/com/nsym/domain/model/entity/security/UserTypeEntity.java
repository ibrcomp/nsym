package br.com.nsym.domain.model.entity.security;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.picketlink.idm.jpa.annotations.AttributeValue;
import org.picketlink.idm.jpa.annotations.OwnerReference;
import org.picketlink.idm.jpa.annotations.entity.IdentityManaged;

import br.com.nsym.domain.model.entity.IPersistentEntity;
import br.com.nsym.domain.model.entity.tools.Configuration;
import br.com.nsym.domain.model.security.User;
import lombok.Getter;
import lombok.Setter;

/**
 *  
 * @author ibrcomp
 *
 */
@Entity
@Table(name = "users")
@IdentityManaged(User.class)
public class UserTypeEntity extends AbstractIdentityTypeEntity implements IPersistentEntity<String> {

	
	private static final long serialVersionUID = -1550268153814204246L;
	@Getter
    @Setter
    @AttributeValue
    @Column(name = "username")
    private String username;
    @Getter
    @Setter
    @AttributeValue
    @Column(name = "name")
    private String name;
    @Getter
    @Setter
    @AttributeValue
    @Column(name = "email")
    private String email;
    
    @Getter
    @Setter
    @AttributeValue
    @Column(name = "theme", nullable = false)
    private String theme;
    @Getter
    @Setter
    @AttributeValue
    @Column(name = "menu_layout")
    private String menuLayout;
  
    @Getter
    @Setter
    @OwnerReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_partition")
    private PartitionTypeEntity partition;
    
    @Getter
    @Setter
    @AttributeValue
    @Column(name="id_empresa")
    private Long idEmpresa;
    
    @Getter
    @Setter
    @AttributeValue
    @Column(name="id_filial")
    private Long idFilial;
    
    @Getter
    @Setter
    @AttributeValue
    @OneToOne(cascade = {CascadeType.REMOVE,CascadeType.PERSIST},fetch = FetchType.EAGER)
    private Configuration config;
    
    

    /**
     * @return se esta entidade ja foi ou nao salva
     */
    @Override
    public boolean isSaved() {
        return this.getId() != null && !this.getId().isEmpty();
    }
}
