package br.com.nsym.domain.model.entity.cadastro;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.produto.ProdutoEstoque;
import br.com.nsym.domain.model.entity.fabrica.Modelo;
import br.com.nsym.domain.model.entity.fabrica.Producao;
import br.com.nsym.domain.model.entity.financeiro.produto.ProdutoCusto;
import br.com.nsym.domain.model.entity.fiscal.Ncm;
import br.com.nsym.domain.model.entity.fiscal.Tributos;
import br.com.nsym.domain.model.entity.fiscal.nfe.ItemNfe;
import br.com.nsym.domain.model.entity.tools.Finalidade;
import br.com.nsym.domain.model.entity.tools.Medida;
import br.com.nsym.domain.model.entity.tools.TipoControleEstoque;
import br.com.nsym.domain.model.entity.tools.TipoMedida;
import br.com.nsym.domain.model.entity.tools.Uf;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="produto",uniqueConstraints = {@UniqueConstraint(columnNames={"referencia","id_empresa"})})
//@UniqueConstraint(columnNames={"refFornecedor","Fornecedor_ID","id_empresa"})
public class Produto extends PersistentEntity {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	private String referencia;
	

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="NCM_Id")
	private Ncm ncm;

	@Getter
	@Setter
	private boolean comercializavel = true;

	@Getter
	@Setter
	@OneToOne
	@JoinColumn(name="medida_ID")
	private Medida medida;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoMedida tipoMedida;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private Finalidade finalidade;

	@Getter
	@Setter
	private boolean fabricado = false;

	@Getter
	@Setter
	private String descricao;

	@Getter
	@Setter
	@OneToMany(mappedBy="produtoBase",cascade=CascadeType.ALL,fetch=FetchType.LAZY)
	private List<BarrasEstoque> listaBarras = new ArrayList<>();
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Departamento_id")
	private Departamento departamento;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Secao_id")
	private Secao secao;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Sub_Secao_Id")
	private SubSecao subSecao;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Fabricante_id")
	private Fabricante fabricante;

	@Getter
	@Setter
	@ManyToMany()
	@JoinTable(name="Tabela_Fornecedores_Produto",joinColumns= @JoinColumn(name="Produto_Id"),
	inverseJoinColumns= @JoinColumn(name="Fornecedor_Id"))
	private List<Fornecedor> fornecedores = new ArrayList<>(); 
	
	// Campos necess�rios para importa��o de nfe de compra
//	@Getter
//	@Setter
//	@ManyToOne
//	@JoinColumn(name="Fornecedor_ID")
//	private Fornecedor fornecedor = new Fornecedor();
	
	@Getter
	@Setter
	private String descricaoFornecedor;
	
	@Getter
	@Setter
	private String refFornecedor;
	
	// Fim dos campos para nfe de compra
	
	@Getter
	@Setter
	@OneToMany(mappedBy="produto",fetch=FetchType.LAZY)
	private List<Imagens> imagens = new ArrayList<>();

	@Getter
	@Setter
	@ManyToMany
	@JoinTable(name="Tabela_Tamanho_Produto",joinColumns= @JoinColumn(name="Produto_Id"),
	inverseJoinColumns= @JoinColumn(name="Tamanho_Id"))
	private List<Tamanho> tamanhos = new ArrayList<>();
	
	@Getter
	@Setter
	@ManyToMany
	@JoinTable(name="Tabela_Cor_Produto",joinColumns= @JoinColumn(name="Produto_Id"),
	inverseJoinColumns= @JoinColumn(name="Cor_id"))
	private List<Cor> cores = new ArrayList<>();

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="Grade_Id")
	private Grade grade;

	@Getter
	@Setter
	@OneToMany(mappedBy="produto",cascade=CascadeType.MERGE,fetch=FetchType.EAGER)
	private List<ProdutoCusto> listaCustoProduto = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="produto")
	private List<ProdutoEstoque> listaEstoqueGeral = new ArrayList<>();

	@Getter
	@Setter
	@OneToMany(mappedBy="produto")
	private List<ItemNfe> itensNfe = new ArrayList<>();

	@Getter
	@Setter
	@OneToMany(mappedBy="produto")
	private List<InfAdcionais> mensagem = new ArrayList<>();

	@Getter
	@Setter
	private boolean tributacaoEspecial = false;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoControleEstoque tipoEstoque;
	
	//somente utilizado para conversao no momento de importa��o de uma danfe
	@Getter
	@Setter
	private String barras;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="tributo_ID")
	private Tributos tributoEspecial;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="tributo_ID_DV")
	private Tributos tributoEspecialDevolucao;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private Uf ufOrigem;

	@Getter
	@Setter
	private BigDecimal mvaFornecedor = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal rendimento = new BigDecimal("1");
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Modelo_ID")
	private Modelo modelo;
	
	@Getter
	@Setter
	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name="Tabela_OP_Produto",joinColumns= @JoinColumn(name="Produto_Id"),
	inverseJoinColumns= @JoinColumn(name="OP_id"))
	private List<Producao> listaOP = new ArrayList<>();
	
	@Getter
	@Setter
	private boolean bemMovelUsado = false;
	
	
	
	public BigDecimal estoqueTotalBarras(){
		BigDecimal total = new BigDecimal("0");
		for (BarrasEstoque barras : this.listaBarras) {
			total = total.add(barras.getTotalEstoque());
		}
		return total;
	}

	@Override
	public String toString() {
		return "Produto [referencia=" + referencia + ", descricao=" + descricao + "id=" +getId() + "]";
	}
	


//	public String toFilter(String filterProperty) {
//		String result = "";
//		if (filterProperty.equalsIgnoreCase("referencia")){
//			result = this.referencia;
//		}
//		if (filterProperty.equalsIgnoreCase("descricao")){
//			result = this.descricao;
//		}
//		if (filterProperty.equalsIgnoreCase("fabricante")){
//			result= this.fabricante.getMarca();
//		}
//		return result;
//	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ Objects.hash(departamento, descricao, fornecedores, medida, ncm, referencia, secao, subSecao);
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
		Produto other = (Produto) obj;
		return Objects.equals(departamento, other.departamento) && Objects.equals(descricao, other.descricao)
				&& Objects.equals(fornecedores, other.fornecedores) && Objects.equals(medida, other.medida)
				&& Objects.equals(ncm, other.ncm) && Objects.equals(referencia, other.referencia)
				&& Objects.equals(secao, other.secao) && Objects.equals(subSecao, other.subSecao);
	}

	

	

}
