package br.com.nsym.domain.model.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Classe base para indicar que se trata de uma entidade, nela temos os atributos
 * basicos para que a classe possa ser persistente.
 *
 * @author Ibrahim Yousef Quatani
 *
 * @version 2.0.0
 * @since 1.0.0, 19/10/2015
 */
@MappedSuperclass
@ToString(of = "id")
@EqualsAndHashCode(of={"id","idEmpresa","idFilial"})
@EntityListeners(PersistentEntityListener.class)
public abstract class PersistentEntityIDManual implements IPersistentEntity<Long>, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @Getter
    @Setter
    @Column(name = "id", unique = true, updatable = true)
    private Long id;

    @Getter
    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "inclusion", nullable = false)
    private Date inclusion;
    @Getter
    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_edition")
    private Date lastEdition;
    
    @Getter
    @Setter
    @Column(name = "included_by", length = 45)
    private String includedBy;
    @Getter
    @Setter
    @Column(name = "edited_by", length = 45)
    private String editedBy;
    
    
    @Getter
    @Setter
    @Column(name="id_empresa")
    private Long idEmpresa;
    
    @Getter
    @Setter
    @Column(name="id_filial")
    private Long idFilial;
    
    @Getter
    @Setter
    private boolean isDeleted;

    /**
     * @return {@inheritDoc}
     */
    @Override
    public boolean isSaved() {
        return !(getId() == null || getId() == 0);
    }
    
    /**
     * @return a data de inclusao em formato string
     */
    public String getInclusionDateAsString() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(this.inclusion);
    }
    
    /**
     * @return a data de inclusao em localdate 
     */
    public LocalDate getInclusionAsLocalDate() {
        return this.inclusion.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
