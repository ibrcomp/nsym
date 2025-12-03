package br.com.nsym.domain.model.entity.security;

import java.io.Serializable;
import java.util.Date;
import org.picketlink.idm.credential.storage.EncodedPasswordStorage;
import org.picketlink.idm.jpa.annotations.CredentialClass;
import org.picketlink.idm.jpa.annotations.CredentialProperty;
import org.picketlink.idm.jpa.annotations.EffectiveDate;
import org.picketlink.idm.jpa.annotations.ExpiryDate;
import org.picketlink.idm.jpa.annotations.OwnerReference;
import org.picketlink.idm.jpa.annotations.entity.ManagedCredential;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
@Table(name = "passwords")
@ManagedCredential(EncodedPasswordStorage.class)
public class PasswordTypeEntity implements Serializable {

    private static final long serialVersionUID = 558269707967464249L;
	@Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;
    @Getter
    @Setter
    @CredentialClass
    @Column(name = "type_name")
    private String typeName;
    @Getter
    @Setter
    @EffectiveDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "effective_date")
    private Date effectiveDate;
    @Getter
    @Setter
    @ExpiryDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expiry_date")
    private Date expiryDate;
    @Getter
    @Setter
    @CredentialProperty(name = "encodedHash")
    @Column(name = "encoded_hash")
    private String encodedHash;
    @Getter
    @Setter
    @CredentialProperty(name = "salt")
    @Column(name = "salt")
    private String salt;
    
    @Getter
    @Setter
    @ManyToOne
    @OwnerReference
    @JoinColumn(name = "id_owner")
    private UserTypeEntity owner;
}