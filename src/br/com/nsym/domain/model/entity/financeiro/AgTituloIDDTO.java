package br.com.nsym.domain.model.entity.financeiro;

import java.math.BigInteger;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

public class AgTituloIDDTO {
	@Getter
	@Setter
	
	private BigInteger id;

	public AgTituloIDDTO(BigInteger id) {
		super();
		this.id = id;
	}

	public AgTituloIDDTO() {
		super();
		//TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "AgTituloIDDTO [id=" + id + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AgTituloIDDTO other = (AgTituloIDDTO) obj;
		return Objects.equals(id, other.id);
	}

}
