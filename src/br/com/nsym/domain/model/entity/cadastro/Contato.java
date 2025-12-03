package br.com.nsym.domain.model.entity.cadastro;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="contato")
public class Contato extends PersistentEntity {
	
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@OneToMany(mappedBy="contato")
	private List<Fone> fone = new ArrayList<Fone>();
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "cliente_ID")
	private Cliente cliente;
	
	@Getter
	@Setter
	private String nome;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="contato",fetch = FetchType.EAGER)
	private List<Email> email = new ArrayList<Email>();
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="empresa_ID")
	private Empresa empresa;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="filial_id")
	private Filial filial;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="colaborador_id")
	private Colaborador colaborador;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="transportadora_id")
	private Transportadora transportadora;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="fornecedor_id")
	private Fornecedor fornecedor;
}
