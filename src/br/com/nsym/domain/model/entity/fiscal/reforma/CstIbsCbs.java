package br.com.nsym.domain.model.entity.fiscal.reforma;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoTributo;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
	    name = "reforma_cst_ibscbs",
	    uniqueConstraints = {
	        @UniqueConstraint(
	            name = "uk_cst_cbs_emp_fil",
	            columnNames = { "cst_cbs", "id_empresa", "id_filial" }
	        ),
	        @UniqueConstraint(
	            name = "uk_cst_ibs_emp_fil",
	            columnNames = { "cst_ibs", "id_empresa", "id_filial" }
	        ),
	        @UniqueConstraint(
	            name = "uk_cst_is_emp_fil",
	            columnNames = { "cst_is", "id_empresa", "id_filial" }
	        		),
	        @UniqueConstraint(
	            name = "uk_cstIbsCbs_is_emp_fil",
	            columnNames = { "cst_ibscbs", "id_empresa", "id_filial" }
	        )
	    }
	)
public class CstIbsCbs extends PersistentEntity {

    /**
	 *
	 */
	private static final long serialVersionUID = -3671230131270956401L;
	
	@Getter @Setter
    @Column(name = "cst_ibscbs", nullable = false, length = 20)
    private String cstIbsCbs;
	
	 /**
     * CST específico de CBS (NT IBS/CBS/IS).
     * Ex.: "01", "06", etc.
     */
    @Getter
    @Setter
    @Column(name = "cst_cbs", length = 4)
    private String cstCbs;

    /**
     * CST específico de IBS.
     */
    @Getter
    @Setter
    @Column(name = "cst_ibs", length = 4)
    private String cstIbs;

    /**
     * CST específico de IS.
     * (Útil caso no futuro surjam CST próprios para IS.)
     */
    @Getter
    @Setter
    @Column(name = "cst_is", length = 4)
    private String cstIs;

    @Getter @Setter
    @Column(name = "descricao", length = 500)
    private String descricaoCstIbsCbs;

    @Getter @Setter
    @Column(name = "ind_gibscbs")
    private Boolean indGibscbs;

    @Getter @Setter
    @Column(name = "ind_vigente")
    private Boolean indVigente;

    @Getter @Setter
    @Column(name = "tipo_nfe_nfce", length = 50)
    private String tipoNfeNfce;

    @Getter @Setter
    @Column(name = "natureza_receita", length = 200)
    private String naturezaDaReceita;

    @Getter @Setter
    @Column(name = "notas", length = 2000)
    private String notas;
    
    @Getter @Setter
    @Column(name = "ind_gIBSCBSMono")
    private Boolean indgIBSCBSMono
;
    @Getter @Setter
    @Column(name = "ind_gRed")
    private Boolean indgRed;
    
    @Getter @Setter
    @Column(name = "ind_gDif")
    private Boolean indgDif;
    
    @Getter @Setter
    @Column(name = "ind_gTransfCred")
    private Boolean indgTransfCred;
    
    @Getter @Setter
    @Column(name = "ind_gCredPresIBSZFM")
    private Boolean indgCredPresIBSZFM;
    
    @Getter @Setter
    @Column(name = "ind_gAjusteCompet")
    private Boolean indgAjusteCompet;
    
    @Getter @Setter
    @Column(name = "ind_RedutorBC")
    private Boolean indRedutorBC;

    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "cclasstrib_id")
    private CClassTrib cClassTrib;

    // NOVO: identifica se este CST se aplica a IBS, CBS ou IS
    @Getter @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_tributo", length = 10)
    private TipoTributo tipoTributo; // valores recomendados: "IBS", "CBS", "IS"
    
    
}
