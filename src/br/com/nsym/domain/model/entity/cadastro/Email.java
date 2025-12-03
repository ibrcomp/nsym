package br.com.nsym.domain.model.entity.cadastro;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.tools.TipoEmail;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="email",uniqueConstraints = {@UniqueConstraint(columnNames = {"cliente_ID", "id_empresa"}),
										 @UniqueConstraint(columnNames = {"transportadora_id","id_empresa"})})

public class Email extends PersistentEntity	{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="cliente_ID")
	private Cliente cliente;
	
	@Getter
	@Setter
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="colaborador_id")
	private Colaborador colaborador;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="contato_ID")
	private Contato contato;
	
	@Getter
	@Setter
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="empresa_ID")
	private Empresa empresa;
	
	@Getter
	@Setter
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="filial_id")
	private Filial filial;
	
	@Getter
	@Setter
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="transportadora_id")
	private Transportadora transportadora;
	
	@Getter
	@Setter
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="fornecedor_id")
	private Fornecedor fornecedor;
	
	@Getter
	@Setter
	@org.hibernate.validator.constraints.Email(message="{email.validate}",regexp = "^[\\w!#$%’*+/=\\-?^_`{|}~]+(\\.[\\w!#$%’*+/=\\-?^_`{|}~]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$")
	@Column(name="email",nullable = true, length = 60)
	private String email;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoEmail tipo;
}
