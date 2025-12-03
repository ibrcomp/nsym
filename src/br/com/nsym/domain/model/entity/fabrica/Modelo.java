package br.com.nsym.domain.model.entity.fabrica;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.Produto;
import br.com.nsym.domain.model.entity.cadastro.Secao;
import br.com.nsym.domain.model.entity.cadastro.Tamanho;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="Modelo",uniqueConstraints = {@UniqueConstraint(columnNames={"id","id_empresa"}),@UniqueConstraint(columnNames={"referencia","id_empresa","id_filial"})})
public class Modelo extends PersistentEntity{

	/**
	 *
	 */
	private static final long serialVersionUID = -8245500359287211000L;
	
	@Getter
	@Setter
	private String descricao;
	
	@Getter
	@Setter
	private String referencia;
	
	@Getter
	@Setter
	private String colecao;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "Secao_ID")
	private Secao secao;
	
	@Getter
	@Setter
	@OneToMany(cascade = CascadeType.ALL )
	@JoinTable(name="Tabela_Materias_Modelo",joinColumns= @JoinColumn(name="Modelo_Id"),
	inverseJoinColumns= @JoinColumn(name="Material_Id"))
	private List<MaterialModelo> listaDeMaterias = new ArrayList<>();
	
	@Getter
	@Setter
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name="Tabela_Tamanho_Modelo",joinColumns= @JoinColumn(name="Modelo_Id"),
	inverseJoinColumns= @JoinColumn(name="Tamanho_Id"))
	private List<Tamanho> tamanhosDisponiveis = new ArrayList<>();
	
	@Getter
	@Setter
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name="Tabela_Produto_Modelo",joinColumns= @JoinColumn(name="Modelo_Id"),
	inverseJoinColumns= @JoinColumn(name="Produto_Id"))
	private List<Produto> listaDeProdutos = new ArrayList<>();


	
}
