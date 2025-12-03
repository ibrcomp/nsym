package br.com.nsym.domain.model.entity.financeiro;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.financeiro.tools.RecebimentoParcialAbstract;

@Entity
@Table(name="RecebimentoParcial",uniqueConstraints = {@UniqueConstraint(columnNames={"id","id_empresa","id_filial"})})
public class RecebimentoParcial extends RecebimentoParcialAbstract {

	
	/**
	 *
	 */
	private static final long serialVersionUID = -575527313881238329L;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RecebimentoParcial [getId()=" + getId() + "]";
	}


}
