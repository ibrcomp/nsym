package br.com.nsym.domain.model.entity.fabrica;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.Fornecedor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="Servico")
public class Servico extends PersistentEntity{

	/**
	 *
	 */
	private static final long serialVersionUID = -3975964817344896609L;
	
	@Getter
	@Setter
	private String descricao;
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorSugerido = new BigDecimal("0");
	
	@Getter
	@Setter
	private boolean porPeca = false;
	
	@Getter
	@Setter
	private boolean integraCusto = true;
	
	@Getter
	@Setter
	private String obs;
	
	@Getter
	@Setter
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name="Tabela_OS_Servico",joinColumns= @JoinColumn(name="Servico_Id"),
	inverseJoinColumns= @JoinColumn(name="OS_Id"))
	private List<OrdemServico> ListaOS = new ArrayList<>();
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Prestador_ID")
	private Fornecedor prestador;

	
}
