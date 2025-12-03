package br.com.nsym.domain.model.entity.tools;

import java.util.Objects;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.Cliente;
import br.com.nsym.domain.model.entity.cadastro.Colaborador;
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.Filial;
import br.com.nsym.domain.model.entity.cadastro.Fornecedor;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
public abstract class AbstractDestino extends PersistentEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1752868363847122349L;

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
	@JoinColumn(name="Colaborador_id")
	private Colaborador colaborador;
	
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(cliente, colaborador, empresa, filial, fornecedor);
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
		AbstractDestino other = (AbstractDestino) obj;
		return Objects.equals(cliente, other.cliente) && Objects.equals(colaborador, other.colaborador)
				&& Objects.equals(empresa, other.empresa) && Objects.equals(filial, other.filial)
				&& Objects.equals(fornecedor, other.fornecedor);
	}

	@Override
	public String toString() {
		return "Destino [cliente=" + cliente + ", fornecedor=" + fornecedor + ", colaborador=" + colaborador
				+ ", empresa=" + empresa + ", filial=" + filial + "]";
	}
	
	
}
