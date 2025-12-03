package br.com.nsym.domain.model.entity.estoque;

import java.time.LocalDateTime;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.Fornecedor;
import br.com.nsym.domain.model.entity.fiscal.nfe.NfeRecebida;
import lombok.Getter;
import lombok.Setter;

public class Recebimento extends PersistentEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="fornecedor_Id")
	private Fornecedor fornecedor;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="NfeFornecedor_Id")
	private NfeRecebida nfeFornecedor;
	
	@Getter
	@Setter
	private LocalDateTime dataRecebimento = LocalDateTime.now();
	
	@Getter
	@Setter
	private boolean estoqueAtualizado;
	
	@Getter
	@Setter
	private boolean financeiroCriado;

	@Getter
	@Setter
	private boolean nota;
	
	// criar modulo para pedidos de compra e atrelar 
//	private PedidoCompra pedidoCompra;
	

}
