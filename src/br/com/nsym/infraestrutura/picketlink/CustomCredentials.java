package br.com.nsym.infraestrutura.picketlink;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.picketlink.idm.credential.AbstractBaseCredentials;
import org.picketlink.idm.credential.Password;

/**
 * Customizacao das credenciais de login para possibilitar a validacao do form
 *
 * @author Ibrahim Yousef Quatani
 *
 * @version 2.0.0
 * @since 1.1.0, 19/10/2016
 */
@Named
@RequestScoped
public class CustomCredentials extends AbstractBaseCredentials {

    @Getter
    @Setter
    @NotEmpty(message = "{authentication.username}")
    private String username;
    private Object secret;

    /**
     * 
     * @return 
     */
    @NotNull(message = "{authentication.password}")
    public String getPassword() {
        if (this.secret instanceof Password) {
            Password password = (Password) this.secret;
            return new String(password.getValue());
        }
        return null;
    }

    /**
     * 
     * @param password 
     */
    public void setPassword(final String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password can not be null.");
        }
        this.secret = new Password(password.toCharArray());
    }

    /**
     * 
     */
    @Override
    public void invalidate() {
        this.secret = null;
        this.username = null;
    }

    /**
     * 
     * @return 
     */
    @Override
    public String toString() {
        return "DefaultLoginCredentials[" + (this.username != null ? this.username : "unknown") + "]";
    }
}
