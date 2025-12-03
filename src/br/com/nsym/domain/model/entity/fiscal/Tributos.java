package br.com.nsym.domain.model.entity.fiscal;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.Filial;
import br.com.nsym.domain.model.entity.cadastro.Produto;
import br.com.nsym.domain.model.entity.fiscal.nfe.Nfe;
import br.com.nsym.domain.model.entity.fiscal.tools.FinalidadeNfe;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoMovimento;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoNFCredito;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoNFDebito;
import br.com.nsym.domain.model.entity.tools.Uf;
import br.com.nsym.domain.model.entity.venda.Transacao;
import lombok.Getter;
import lombok.Setter;

// classe que agrupa todos os tributos como IPI ICMS PIS COFINS para gerar um modelo padrao para uma determinada CFOP + NCM + UF em conjunto
// caso uf = nulo entender que � padrao!

@Entity
@Table(name="tributos",uniqueConstraints = {@UniqueConstraint(columnNames = {"CFOP_Dentro_ID","estado","id_empresa"}),
											@UniqueConstraint(columnNames = {"CFOP_Dentro_ID","estado","id_filial"}),
											@UniqueConstraint(columnNames = {"CFOP_Fora_ID","estado","id_filial"}),
											@UniqueConstraint(columnNames = {"CFOP_Fora_ID","estado","id_empresa"}),
											@UniqueConstraint(columnNames = {"CFOP_Exterior_ID","estado","id_empresa"}),
											@UniqueConstraint(columnNames = {"CFOP_Exterior_ID","estado","id_filial"})})
public class Tributos extends PersistentEntity{
	

	/**
	 *
	 */
	private static final long serialVersionUID = 8591811663397606221L;

	@Getter
	@Setter
	@OneToMany(mappedBy="tributo")
	private List<Ncm> listaNcm = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="tributo")
	private List<Empresa> listaEmpTributos = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="tributoST")
	private List<Empresa> listaEmpTributosST = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="tributo")
	private List<Filial> listaFilialTributos = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="tributoST")
	private List<Filial> listaFilialTributosST = new ArrayList<>();
	
//	@Getter
//	@Setter
//	@OneToMany(mappedBy="tributoFilial")
//	private List<Ncm> listaNcmFilial = new ArrayList<>();
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoMovimento tipoNF;
	
	@Getter
	@Setter
	@Column(name="sub_tributaria")
	private boolean st;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="CFOP_Dentro_ID")
	private CFOP cfopDentro;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="CFOP_DentroFabricado_ID")
	private CFOP cfopDentroFabricado;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="CFOP_Fora_ID")
	private CFOP cfopFora;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="CFOP_ForaFabricado_ID")
	private CFOP cfopForaFabricado;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="CFOP_Exterior_ID")
	private CFOP cfopExterior;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="CFOP_Importacao_ID")
	private CFOP cfopImportacao;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="CFOP_ExteriorFabricado_ID")
	private CFOP cfopExteriorFabricado;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="CFOP_Consumidor_ID")
	private CFOP cfopConsumidor;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="CFOP_ConsumidorFabricado_ID")
	private CFOP cfopConsumidorFabricado;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private Uf estado;
	
	// nome para essa regra de tributos que ser� utilizada por um determinado NCM
	@Getter
	@Setter
	private String descricao;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="ipi_id")
	private IPI ipi;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="icms_id")
	private ICMS icms;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="pis_id")
	private PIS pis;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="cofins_id")
	private COFINS cofins;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="icmsSt_id")
	private ICMSST icmsSt;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="pisSt_id")
	private PISST pisSt;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="cofinsSt_id")
	private COFINSST cofinsSt;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="natOperacao")
	private List<Nfe> listaNfes = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="tributoEspecial")
	private List<Produto> produto;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="tributoPadrao")
	private List<Transacao> transacao;
	
	@Getter
	@Setter
	private boolean devVenda = false;
	
	@Enumerated(EnumType.STRING)
	@Getter
	@Setter
	private TipoNFCredito tpNFCredito;
	
	@Enumerated(EnumType.STRING)
	@Getter
	@Setter
	private TipoNFDebito tpNFDebito;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private FinalidadeNfe finalidadeEmissao;
}
