package br.com.nsym.domain.model.entity.cadastro;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.tools.Operadora;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Fone extends PersistentEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -6746279235511675337L;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="contato_ID")
	private Contato contato;
	
	// diz qual � a operadora utilizada
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private Operadora operadora;
	
	// para definir se este telefone � whatsapp tambem.
	@Getter
	@Setter
	private boolean whatsapp; 
	
	@Getter
	@Setter
	@Column(name="ddi",nullable = true, length = 2)
	private int ddi;
	
	@Getter
	@Setter
	@Column(name="ddd",nullable = true, length = 2)
	private int ddd;
	
	@Getter
	@Setter
	@Column(name="fone",nullable = true, length = 9)
	private int fone;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="cliente_ID")
	private Cliente cliente;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="empresa_ID")
	private Empresa empresa;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="filial_ID")
	private Filial filial;
	
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
