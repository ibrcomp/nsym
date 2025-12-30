package br.com.nsym.domain.model.entity.fiscal.Cfe;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.financeiro.RecebimentoParcial;
import br.com.nsym.domain.model.entity.fiscal.tools.StatusNfe;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nfce")
public class Nfce extends PersistentEntity{

    private static final long serialVersionUID = -9170467673312652561L;
    
	@Getter
	@Setter
	@OneToOne(cascade = CascadeType.ALL)
	private EmitenteCFe emitente;
	
	@Getter
	@Setter
	@OneToOne(cascade = CascadeType.ALL)
	private DestinatarioCFe destinatario;
    
    @Getter
    @Setter
	@Column(name = "numero")
	private Long numero;
	
    @Getter
    @Setter
    @Column(name = "serie")
    private Integer serie;
    
    @Getter
    @Setter
    @Column(name = "modelo")
    private Integer modelo = 65;
    
    @Getter
    @Setter
    @Column(name = "chave_acesso")
    private String chaveAcesso;
    
    @Getter
    @Setter
    @Column(name = "data_emissao")
    private LocalDate dataEmissao;
    
    @Getter
    @Setter
    @Column(name = "v_prod", precision = 15, scale = 2)
    private BigDecimal vProd;
    
    @Getter
    @Setter
    @Column(name = "v_nf",   precision = 15, scale = 2)
    private BigDecimal vNf;
    
    @Getter
    @Setter
    @Column(name = "tot_v_cbs", precision = 15, scale = 2)
    private BigDecimal totVCbs;
    
    @Getter
    @Setter
    @Column(name = "tot_v_ibs", precision = 15, scale = 2)
    private BigDecimal totVIbs;
    
    @Getter
    @Setter
    @Column(name = "tot_v_is",  precision = 15, scale = 2)
    private BigDecimal totVIs;
    
    @Getter @Setter
    @Column(name="protocolo_autorizacao")
    private String protocoloAutorizacao;

    @Getter @Setter
    @Column(name="caminho_xml")
    private String caminhoXml;

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    @Column(name="status_emissao")
    private StatusNfe statusEmissao;

    @Getter @Setter
    @Column(name="emitido")
    private boolean emitido;

    @Getter @Setter
    @Column(name="motivo_retorno")
    private String motivoRetorno;
    
//    @Getter
//	@Setter
//	@OneToMany(mappedBy="nfce",cascade = CascadeType.ALL)
//    private List<ParcelasNfe> listaParcelas = new ArrayList<>();
    
    @Getter
   	@Setter
   	@OneToMany(mappedBy="nfce",cascade = CascadeType.ALL)
    private List<RecebimentoParcial> listaRecebimentosAgrupados = new ArrayList<>();

    
    @Getter
    @Setter
    @OneToMany(mappedBy = "nfce", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<NfceItem> itens = new ArrayList<>();

    public void addItem(NfceItem it) { 
    	it.setNfce(this); 
    	itens.add(it);
    }
    
    public void addRecebimentoParcial(RecebimentoParcial rec) {
    	rec.setNfce(this);
    	listaRecebimentosAgrupados.add(rec);
    }

}
