package br.com.nsym.domain.model.entity.fiscal.Cfe;

import javax.persistence.*;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
	private Integer numero;
	
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
    private LocalDateTime dataEmissao;
    
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
    
    @Getter
    @Setter
    @OneToMany(mappedBy = "nfce", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<NfceItem> itens = new ArrayList<>();

    public void addItem(NfceItem it) { 
    	it.setNfce(this); 
    	itens.add(it);
    }

}
