package br.com.nsym.domain.model.entity.venda;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import br.com.nsym.domain.model.entity.tools.AbstractDestino;
import lombok.Getter;
import lombok.Setter;

@Entity
public class DestinatarioPedido extends AbstractDestino {

	
	/**
	 *
	 */
	private static final long serialVersionUID = -7813907290501270349L;
	@Getter
	@Setter
	@OneToOne(mappedBy="destino",cascade=CascadeType.ALL)
	private Pedido pedido;
	
	public String nome(){
		String resposta = "";
		if (getCliente() != null) {
			resposta = getCliente().getRazaoSocial();
		}else if (getFornecedor() != null) {
			resposta = getFornecedor().getRazaoSocial();
		}else if (getColaborador() != null) {
			resposta = getColaborador().getNome();
		}else {
			resposta = "Não Informado";
		}
		return resposta;
	}
	
	/**
	 * 
	 * @return o ID da Classe cadastrada
	 */
	public Long idRegistro(){
		Long resposta = 0l;
		if (getCliente() != null) {
			resposta = getCliente().getId();
		}else if (getFornecedor() != null) {
			resposta = getFornecedor().getId();
		}else if (getColaborador() != null) {
			resposta = getColaborador().getId();
		}
		return resposta;
	}
	
	public Object classePreenchida() {
		if (getCliente() != null) {
			return getCliente();
		}else if (getFornecedor() != null) {
			return getFornecedor();
		}else if (getColaborador() != null) {
			return getColaborador();
		}else {
			return null;
		}
	}
	
	public String informaClassePreenchida() {
		if (getCliente() != null) {
			return "cliente";
		}else if (getFornecedor() != null) {
			return "fornecedor";
		}else if (getColaborador() != null) {
			return "colaborador";
		}else {
			return "null";
		}
	}



}
