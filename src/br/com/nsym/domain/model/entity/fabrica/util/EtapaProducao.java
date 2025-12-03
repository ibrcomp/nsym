package br.com.nsym.domain.model.entity.fabrica.util;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="EtapaProducao",uniqueConstraints={@UniqueConstraint(columnNames={"descricao","id_empresa"})})
public class EtapaProducao extends PersistentEntity{
	
	/**
	 *
	 */
	private static final long serialVersionUID = 6564371880348586987L;
	
	@Getter
	@Setter
	private String descricao;
	
	@Getter
	@Setter
	private boolean usarOS = true;
	
	@Getter
	@Setter
	private boolean usarFichaTec = false;

	@Override
	public String toString() {
		return "EtapaProducao [descricao=" + descricao + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(descricao);
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
		EtapaProducao other = (EtapaProducao) obj;
		return Objects.equals(descricao, other.descricao);
	}
	
	
	
}
