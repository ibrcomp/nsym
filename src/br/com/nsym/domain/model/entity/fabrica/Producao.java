package br.com.nsym.domain.model.entity.fabrica;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.Produto;
import br.com.nsym.domain.model.entity.fabrica.util.EtapaProducao;
import br.com.nsym.domain.model.entity.fabrica.util.GradeProducao;
import br.com.nsym.domain.model.entity.fabrica.util.LinhaProducao;
import br.com.nsym.domain.model.entity.fabrica.util.StatusAndamento;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="producao")
public class Producao extends PersistentEntity{

	/**
	 *
	 */
	private static final long serialVersionUID = 3728673468061836307L;
	
	@Transient
	private MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Modelo_ID")
	private Modelo modelo;
	
	@Getter
	@Setter
	private String ref;
	
	@Getter
	@Setter
	private String descricao;
	
	@Getter
	@Setter
	@OneToMany(cascade = CascadeType.ALL )
	@JoinTable(name="Tabela_Grade_Producao",joinColumns= @JoinColumn(name="OP_Id"),
	inverseJoinColumns= @JoinColumn(name="GradeProd_Id"))
	private List<GradeProducao> listaGrade = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(cascade=CascadeType.ALL)
	@JoinTable(name="Tabela_FichaTecnica",joinColumns= @JoinColumn(name="OP_Id"),
	inverseJoinColumns= @JoinColumn(name="FichaTecnica_Id"))
	private List<FichaTecnica> listaDeFichasTecnicas = new ArrayList<>();
	
	
	@Getter
	@Setter
	@OneToMany(cascade=CascadeType.ALL)
	@JoinTable(name="Tabela_OP_Materiais",joinColumns= @JoinColumn(name="OP_Id"),
	inverseJoinColumns= @JoinColumn(name="MaterialModelo_Id"))
	private List<MaterialModelo> listaDeMateriais = new ArrayList<>();
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Etapa_ID")
	private EtapaProducao etapa;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private StatusAndamento andamento;

	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal estimativa = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@OneToOne
	@JoinColumn(name="Sequencia_ID")
	private LinhaProducao sequenciaProducao ;
	
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="Produto_ID")
	private Produto produto;
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal quantDispProduzir = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal quantTotalProduzido = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal quantDefeito = new BigDecimal("0",mc);
	
	
	@Getter
	@Setter
	@OneToMany(cascade=CascadeType.ALL)
	@JoinTable(name="Tabela_OS_OP",joinColumns= @JoinColumn(name="OP_Id"),
	inverseJoinColumns= @JoinColumn(name="OS_Id"))
	private List<OrdemServico> listaDeServicos = new ArrayList<>();

	@Override
	public String toString() {
		return "Producao [etapa=" + etapa + ", andamento=" + andamento + ", estimativa=" + estimativa + ", getId()="
				+ getId() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(andamento, estimativa, etapa, listaDeFichasTecnicas, listaDeMateriais,
				listaGrade, mc, modelo, sequenciaProducao);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Producao other = (Producao) obj;
		return andamento == other.andamento && Objects.equals(estimativa, other.estimativa)
				&& Objects.equals(etapa, other.etapa)
				&& Objects.equals(listaDeFichasTecnicas, other.listaDeFichasTecnicas)
				&& Objects.equals(listaDeMateriais, other.listaDeMateriais)
				&& Objects.equals(listaGrade, other.listaGrade) && Objects.equals(mc, other.mc)
				&& Objects.equals(modelo, other.modelo) && Objects.equals(sequenciaProducao, other.sequenciaProducao);
	}

}
