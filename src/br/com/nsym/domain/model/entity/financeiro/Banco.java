package br.com.nsym.domain.model.entity.financeiro;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="Banco", uniqueConstraints = {@UniqueConstraint(columnNames = {"numeroBanco", "id_empresa"}),
											@UniqueConstraint(columnNames = {"nomeBanco","id_empresa"})})
public class Banco extends PersistentEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -9063873782035957896L;
	
	@Getter
	@Setter
	private String numeroBanco;
	
	@Getter
	@Setter
	private String nomeBanco;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="banco",fetch = FetchType.LAZY)
	private List<ContaCorrente> listaContas = new ArrayList<ContaCorrente>();


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(nomeBanco, numeroBanco);
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
		Banco other = (Banco) obj;
		return Objects.equals(nomeBanco, other.nomeBanco) && Objects.equals(numeroBanco, other.numeroBanco);
	}


	@Override
	public String toString() {
		return "Banco [numeroBanco=" + numeroBanco + ", nomeBanco=" + nomeBanco + ", getId()=" + getId() + "]";
	}

	
}
