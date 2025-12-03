package br.com.nsym.domain.model.entity.fiscal.nfe;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.Cliente;
import br.com.nsym.domain.model.entity.cadastro.Colaborador;
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.Filial;
import br.com.nsym.domain.model.entity.cadastro.Fornecedor;
import br.com.nsym.domain.model.entity.tools.TipoCliente;
import br.com.nsym.domain.model.entity.tools.Uf;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Destinatario extends PersistentEntity{


	/**
	 *
	 */
	private static final long serialVersionUID = 8155801779774519478L;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Cliente_Id")
	private Cliente cliente;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Fornecedor_id")
	private Fornecedor fornecedor;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Empresa_id")
	private Empresa empresa;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Filial_id")
	private Filial filial;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Colaborador_id")
	private Colaborador colaborador;

	@Getter
	@Setter
	@OneToOne(mappedBy="destino",cascade=CascadeType.ALL)
	private Nfe nfe;
	
	public String retornaTipoDestinatario() {
		String resultado= "null";
		if (this.cliente != null) {
			resultado="cliente";
		}
		if (this.fornecedor != null) {
			resultado="fornecedor";
		}
		if (this.empresa != null) {
			resultado="empresa";
		}
		if (this.filial != null) {
			resultado="filial";
		}
		if (this.colaborador != null) {
			resultado="colaborador";
		}
		return resultado;
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
	
	public Uf getUfDestino() {
		Uf resposta = null ;
	    if (this.cliente != null) {
	       resposta =  this.cliente.getEstado();
	    }
	    if (this.fornecedor != null) {
	    	resposta = this.fornecedor.getEstado();
	    }
	    if (this.empresa != null) {
	    	resposta = this.empresa.getEstado();
	    }
	    if (this.filial != null) {
	        resposta = this.filial.getEstado();
	    }
	    if (this.colaborador != null) {
	    	resposta = this.colaborador.getEndereco().getEndereco().getUf();
	    }
	    return resposta;
	}
	
	public String getTipoCliente() {
		String resposta = null ;
	    if (this.cliente != null) {
	       resposta =  this.cliente.getTipoCliente().toString();
	    }
	    if (this.fornecedor != null) {
	    	resposta = this.fornecedor.getTipoCliente().toString();
	    }
	    if (this.empresa != null) {
	    	resposta = TipoCliente.Rev.toString();
	    }
	    if (this.filial != null) {
	        resposta = TipoCliente.Rev.toString();
	    }
	    if (this.colaborador != null) {
	    	resposta = TipoCliente.CfC.toString();
	    }
	    return resposta;
	}

	public Boolean getConsumidorFinal() {

		if (this.empresa != null) return   Boolean.FALSE ;
		
		if (this.filial != null)return   Boolean.FALSE ;
		
		if (this.colaborador != null)return   Boolean.TRUE ;
		
		boolean ehConsumidorFinal = java.util.stream.Stream.of(
				this.cliente != null ? this.cliente.getTipoCliente() : null,
						this.fornecedor != null ? this.fornecedor.getTipoCliente() : null
				)
				.filter(java.util.Objects::nonNull)
				.anyMatch(tc -> tc == TipoCliente.CfC || tc == TipoCliente.Cfi);

		return ehConsumidorFinal ? Boolean.TRUE : Boolean.FALSE;
	}
}
