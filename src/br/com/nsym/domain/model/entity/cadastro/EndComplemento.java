package br.com.nsym.domain.model.entity.cadastro;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="end_complemento")
public class EndComplemento extends PersistentEntity {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private String numero;
	
	@Getter
	@Setter
	private String obs;
	
	@Getter
	@Setter
	private String complemento;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="cliente_id")
	private Cliente cliente;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="endereco_ID")
	private Endereco endereco  ;
	
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
	
	@Getter
    @Setter
//    @NotNull(message = "{cep.logradouro}")
    @Column(name = "logradouro", nullable = false, length = 100)
	private String logradouro;
	
	@Getter
	@Setter
    private String bairro;
}
