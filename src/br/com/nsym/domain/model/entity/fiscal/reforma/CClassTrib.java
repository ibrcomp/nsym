package br.com.nsym.domain.model.entity.fiscal.reforma;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "reforma_cclass_trib",
    uniqueConstraints = @UniqueConstraint(
            name = "uk_cst_cclass",
            columnNames = { "cst_ibscbs", "c_class_trib","id_empresa","id_filial" }
    )
)
public class CClassTrib extends PersistentEntity {

	
	private static final long serialVersionUID = 4494121791115549520L;

	@Getter
	@Setter
	@Column(name = "cst_ibscbs", nullable = false, length = 20)
    private String cstIbsCbs;
	
	@Getter
	@Setter
    @Column(name = "c_class_trib", nullable = false, length = 40)
    private String cClassTrib;
	
	@Getter
	@Setter
    @Column(name = "nome_cclass_trib", length = 200)
    private String nomeCClassTrib;

	@Getter
	@Setter
    @Column(name = "tipo_aliquota", length = 50)
    private String tipoDeAliquota;

	@Getter
	@Setter
    @Column(name = "p_red_ibs", precision = 10, scale = 4)
    private BigDecimal pRedIbs;

	@Getter
	@Setter
    @Column(name = "p_red_cbs", precision = 10, scale = 4)
    private BigDecimal pRedCbs;

	@Getter
	@Setter
    @Column(name = "ind_ibs_recuperavel")
    private Boolean indIbsRecuperavel;

	@Getter
	@Setter
    @Column(name = "ind_cbs_recuperavel")
    private Boolean indCbsRecuperavel;

	@Getter
	@Setter
    @Column(name = "ind_exig_mus")
    private Boolean indExigMus;

	@Getter
	@Setter
    @Column(name = "ind_is_seletivo")
    private Boolean indIsSeletivo;

	@Getter
	@Setter
    @Column(name = "ind_outra_camada")
    private Boolean indOutraCamada;

	@Getter
	@Setter
    @Column(name = "ind_monofasico")
    private Boolean indMonofasico;

	@Getter
	@Setter
    @Column(name = "ind_cbs_por_dentro")
    private Boolean indCbsPorDentro;

	@Getter
	@Setter
    @Column(name = "ind_ibs_por_dentro")
    private Boolean indIbsPorDentro;

	@Getter
	@Setter
    @Column(name = "tipo_is", length = 50)
    private String tipoIs;

	@Getter
	@Setter
    @Column(name = "anexo", length = 50)
    private String anexo;

	@Getter
	@Setter
    @Column(name = "link", length = 500)
    private String link;

	@Getter
	@Setter
    @Column(name = "notas", length = 2000)
    private String notas;

	@Getter
	@Setter
    @Column(name = "ind_cst_ajustada")
    private Boolean indCstAjustada;

	@Getter
	@Setter
    @Column(name = "ind_apenas_pf")
    private Boolean indApenasPessoaFisica;

	@Getter
	@Setter
    @Column(name = "ind_apenas_contrib")
    private Boolean indApenasContribuinte;

	@Getter
	@Setter
    @Column(name = "ind_cest_obrigatorio")
    private Boolean indCestObrigatorio;

	@Getter
	@Setter
    @Column(name = "ind_pf_nfce_60b")
    private Boolean indPfNfce60B;

	@Getter
	@Setter
    @Column(name = "ind_nfse_via")
    private Boolean indNfseVia;

	@Getter
	@Setter
    @Column(name = "ind_outros")
    private Boolean indOutros;
	
	@Getter
	@Setter
    @Column(name = "ind_gTribRegular")
    private Boolean indgTribRegular;
	
	@Getter
	@Setter
    @Column(name = "ind_gCredPresOper")
    private Boolean indgCredPresOper;
	@Getter
	@Setter
    @Column(name = "ind_gMonoPadrao")
    private Boolean indgMonoPadrao;
	@Getter
	@Setter
    @Column(name = "ind_gMonoReten")
    private Boolean indgMonoReten;
	@Getter
	@Setter
    @Column(name = "ind_gMonoRet")
    private Boolean indgMonoRet;
	@Getter
	@Setter
    @Column(name = "ind_gMonoDif")
    private Boolean indgMonoDif;
	@Getter
	@Setter
    @Column(name = "ind_gEstornoCred")
    private Boolean indgEstornoCred;
	
	
	@Getter @Setter
    @Column(name = "dIniVig")
    private LocalDate dIniVig;
	
	@Getter @Setter
    @Column(name = "dFimVig")
    private LocalDate dFimVig;
    
    @Getter @Setter
    @Column(name = "dAtualizado")
    private LocalDate dAtualizado;
    @Getter
    
	@Setter
    @Column(name = "indNFeABI")
    private Boolean indNFeABI;
    @Setter
    @Column(name = "indNFe")
    private Boolean indNFe;
    @Setter
    @Column(name = "indNFCe")
    private Boolean indNFCe;
    @Setter
    @Column(name = "indCTe")
    private Boolean indCTe;
    @Setter
    @Column(name = "indCTeOS")
    private Boolean indCTeOS;
    @Setter
    @Column(name = "indBPe")
    private Boolean indBPe;
    @Setter
    @Column(name = "indBPeTA")
    private Boolean indBPeTA;
    @Setter
    @Column(name = "indBPeTM")
    private Boolean indBPeTM;
    @Setter
    @Column(name = "indNF3e")
    private Boolean indNF3e;
    @Setter
    @Column(name = "indNFSe")
    private Boolean indNFSe;
    @Setter
    @Column(name = "indNFCom")
    private Boolean indNFCom;
    @Setter
    @Column(name = "indNFAg")
    private Boolean indNFAg;
    @Setter
    @Column(name = "indNFGas")
    private Boolean indNFGas;
    @Setter
    @Column(name = "indDERE")
    private Boolean indDERE;
    
 // Indica se esta classificação permite crédito presumido para operações na ZFM (Zona Franca de Manaus)
    @Getter
    @Setter
    @Column(name = "ind_gCredPresIBSZFM")
    private Boolean indgCredPresIBSZFM;
}
