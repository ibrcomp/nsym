package br.com.nsym.domain.model.entity.fiscal;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.fiscal.reforma.CClassTrib;
import br.com.nsym.domain.model.entity.fiscal.reforma.CCredPres;
import br.com.nsym.domain.model.entity.fiscal.reforma.CstIbsCbs;
import br.com.nsym.domain.model.entity.fiscal.reforma.CstIs;
import br.com.nsym.domain.model.entity.tools.TipoCliente;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reforma_param_2026")
public class ParamReforma2026 extends PersistentEntity{
	
	
    private static final long serialVersionUID = 5856446886716058910L;
    
    @Getter
    @Setter
	@Column(name = "desc_regra", length = 100)
    private String descricaoRegra;
    
    @Getter
    @Setter
	@Column(name = "ncm_prefix", length = 10)
    private String ncmPrefix;
    
    @Getter
    @Setter
    @Column(name = "ncm_ini", length = 10)
    private String ncmIni;
    
    @Getter
    @Setter
    @Column(name = "ncm_fim", length = 10)
    private String ncmFim;
    
    @Getter
    @Setter
    @Column(name = "ncm_list", length = 1000)
    private String ncmList;

    @Getter
    @Setter
    @Column(name = "cfop", length = 20)
    private String cfop;
    
    @Getter
    @Setter
    @Column(name = "cfop_list", length = 1000)
    private String cfopList;
    
    @Getter
    @Setter
    @Column(name = "cfop_regex", length = 255)
    private String cfopRegex;

    @Getter
    @Setter
    @Column(name = "cest", length = 20)
    private String cest;
    
    @Getter
    @Setter
    @Column(name = "ex_tipi", length = 10)
    private String exTipi;

    @Getter
    @Setter
    @Column(name = "cst", length = 3)
    private String cst;
    
    @Getter
    @Setter
    @Column(name = "csosn", length = 3)
    private String csosn;

    @Getter
    @Setter
    @Column(name = "uf_orig", length = 2)
    private String ufOrig;
    
    @Getter
    @Setter
    @Column(name = "uf_dest", length = 2)
    private String ufDest;
    
    @Getter
    @Setter
    @Column(name = "ibge_mun_dest", length = 7)
    private String ibgeMunDest;
    
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cliente", length = 30)
    private TipoCliente tipoCliente;
    
    @Getter
    @Setter
    @Column(name = "consumidor_final")
    private Boolean consumidorFinal;
    
    @Getter
    @Setter
    @Column(name = "cnae", length = 20)
    private String cnae;

    @Getter
    @Setter
    @Column(name = "vigencia_ini")
    private LocalDate vigenciaIni;
    
    @Getter
    @Setter
    @Column(name = "vigencia_fim")
    private LocalDate vigenciaFim;

    @Getter
    @Setter
    @Column(name = "sku_incluir_list", length = 2000)
    private String skuIncluirList;
    
    @Getter
    @Setter
    @Column(name = "sku_excluir_list", length = 2000) 
    private String skuExcluirList;

    @Getter
    @Setter
    @Column(name = "p_cbs", precision = 10, scale = 4)
    private BigDecimal pCbs;
    
    @Getter
    @Setter
    @Column(name = "p_ibs", precision = 10, scale = 4)
    private BigDecimal pIbs;
    
    @Getter
    @Setter
    @Column(name = "p_is", precision = 10, scale = 4)
    private BigDecimal pIs;

    @Getter
    @Setter
    @Column(name = "ativo")
    private Boolean ativo = Boolean.TRUE;
    
    @Getter
    @Setter
    @Column(name = "prioridade")
    private Integer prioridade = 0;
    
    // ========================
    // NOVOS CAMPOS
    // ========================
    
    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "cst_is_id")
    private CstIs cstIs;
    
    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "cst_ibs_cbs_id")
    private CstIbsCbs cstIbsCbs;

    @Getter @Setter
    @Column(name = "cst_ibs", length = 4)
    private String cstIbs;

    @Getter @Setter
    @Column(name = "cst_cbs", length = 4)
    private String cstCbs;


    @Getter @Setter
    @Column(name = "p_dif_ibs", precision = 15, scale = 4)
    private BigDecimal pDifIbs;

    @Getter @Setter
    @Column(name = "p_dif_cbs", precision = 15, scale = 4)
    private BigDecimal pDifCbs;

    @Getter @Setter
    @Column(name = "p_dif_is", precision = 15, scale = 4)
    private BigDecimal pDifIs;

    @Getter @Setter  
    @Column(name = "ind_sem_ibsm")
    private Boolean indSemIbsm;
    
 // campo de configuração – JPA
    @Getter @Setter
    @Column(name = "ccredpres_codigo", length = 10)
    private String codigoCredPres;
    
    @Getter @Setter
    @Transient
    private CCredPres regraCredPres;

    @Getter @Setter
    @Transient
    private BigDecimal pCredPres;   // alíquota do crédito presumido (%)

    @Getter @Setter
    @Transient
    private BigDecimal vCredPres;   // valor “orientativo” do crédito
    
    @Getter
    @Setter
	@ManyToOne
	@JoinColumn(name="id_cClassTrib")
    private CClassTrib cClassTrib;



}
