package br.com.nsym.domain.model.entity.fabrica;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.Produto;
import br.com.nsym.domain.model.entity.fabrica.util.ItemProducao;
import br.com.nsym.domain.model.entity.fabrica.util.StatusAndamento;
import br.com.nsym.domain.model.entity.fabrica.util.TipoEnfesto;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="fichaTecnica",uniqueConstraints= {@UniqueConstraint(columnNames = {"id","id_empresa","id_filial"})})
public class FichaTecnica extends PersistentEntity{

	/**
	 *
	 */
	private static final long serialVersionUID = -8245500359287211000L;
	
	@Transient
	private MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Produca_ID")
	private Producao producao;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Material_ID")
	private Produto materiaPrima;
	
	@Getter
	@Setter
	private BigDecimal largura = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal totalCorte = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal Comprimento = new BigDecimal("0");

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoEnfesto enfesto;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private StatusAndamento andamento;
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal numFolhas = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	private boolean temSobra = false;
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal quantSobra = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@OneToMany(cascade = CascadeType.ALL )
	@JoinTable(name="Tabela_Tecido_Utilizado",joinColumns= @JoinColumn(name="Barras_Id"),
	inverseJoinColumns= @JoinColumn(name="FichaTecnica_Id"))
	private List<ItemProducao> listaDeTecidosUtilizado = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JoinTable(name="TabelaItensFicha",joinColumns= @JoinColumn(name="Ficha_Id"),
	inverseJoinColumns= @JoinColumn(name="ItemFicha_Id"))
	private List<ItemFichaTecnica> itens = new ArrayList<>();
	

}
