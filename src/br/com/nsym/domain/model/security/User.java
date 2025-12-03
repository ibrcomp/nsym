package br.com.nsym.domain.model.security;

import static org.picketlink.idm.model.annotation.IdentityStereotype.Stereotype.USER;
import static org.picketlink.idm.model.annotation.StereotypeProperty.Property.IDENTITY_USER_NAME;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.picketlink.idm.model.AbstractIdentityType;
import org.picketlink.idm.model.Account;
import org.picketlink.idm.model.annotation.AttributeProperty;
import org.picketlink.idm.model.annotation.IdentityStereotype;
import org.picketlink.idm.model.annotation.StereotypeProperty;
import org.picketlink.idm.model.annotation.Unique;
import org.picketlink.idm.query.QueryParameter;

import br.com.nsym.domain.model.entity.tools.Configuration;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Ibrahim Yousef Quatani
 *
 * @version 1.0.0
 * @since 2.2.0, 22/10/2016
 */
@IdentityStereotype(USER)
public class User extends AbstractIdentityType implements Account {
  
	@Getter
    @Setter
    @NotNull(message = "{user.name}")
    @Column(name = "name", length = 90, nullable = false)
    private String name;
    @Getter
    @Setter
    @Email(message = "{user.email}")
    @NotNull(message = "{user.email}")
    @Column(name = "email", length = 90, nullable = false)
    private String email;
    @Setter
    @Getter
    @Unique
    @StereotypeProperty(IDENTITY_USER_NAME)
    @NotNull(message = "{user.username}")
    @Column(name = "username", length = 45, nullable = false)
    private String username;
    
    @Getter
    @Setter
    @AttributeProperty
    private String theme;
    @Getter
    @Setter
    @AttributeProperty
    private String menuLayout;

    @Getter
    @Setter
    @Transient
    private GroupMembership groupMembership;
    
    @Getter
    @Setter
    @Transient
    private boolean selected;
    @Getter
    @Setter
    @Transient
    private String password;
    @Getter
    @Setter
    @Transient
    private String passwordConfirmation;
    
    @Getter
    @Setter
    @AttributeProperty
    private Long idEmpresa;
    
    @Getter
    @Setter
    @AttributeProperty
    private Long idFilial;
    
    @Getter
    @Setter
    @AttributeProperty
    private boolean logado = false;
    
    @Getter
    @Setter
    @AttributeProperty	
    @MapsId("id")
    @OneToOne(cascade = {CascadeType.PERSIST,CascadeType.REMOVE},fetch = FetchType.EAGER)
    private Configuration config;
    
   
    public static final QueryParameter NAME = QUERY_ATTRIBUTE.byName("name");
    public static final QueryParameter EMAIL = QUERY_ATTRIBUTE.byName("email");
    public static final QueryParameter USER_NAME = QUERY_ATTRIBUTE.byName("username");
    public static final QueryParameter LOGADO = QUERY_ATTRIBUTE.byName("logado");
    public static final QueryParameter EMPRESA = QUERY_ATTRIBUTE.byName("idEmpresa");
//    public static final QueryParameter CONFIGURACAO = QUERY_ATTRIBUTE.byName("config");

    /**
     *
     */
    public User() {
        this.theme = "skin-black";
        this.groupMembership = new GroupMembership(null, this);
    }

    /**
     *
     * @param username 
     */
    public User(String username) {
        this();
        this.username = username;
    }
    
    /**
     * @return 
     */
    public String getGroupName() {
        return this.groupMembership.getGroup().getName();
    }
    
    /**
     * @return 
     */
    public boolean isBlocked() {
        return !this.isEnabled();
    }
    
    /**
     * 
     * @param blocked 
     */
    public void setBlocked(boolean blocked) {
        this.setEnabled(!blocked);
    }
    
    /**
     * @return 
     */
    public boolean isSmallMenu() {
        return this.menuLayout != null && !this.menuLayout.isEmpty();
    }
    
    /**
     * @param smallMenu 
     */
    public void setSmallMenu(boolean smallMenu) {
        this.menuLayout = smallMenu ? "sidebar-collapse" : "";
    }
    
}
