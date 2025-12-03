package br.com.nsym.domain.model.entity.fiscal.nfe;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="cce")
public class CartaCorrecao extends PersistentEntity{


	/**
	 *
	 */
	private static final long serialVersionUID = -2767104561828912342L;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Nfe_id")
	private Nfe nfe;
	
	@Getter
	@Setter
	private String motivoCorrecao;
	
	@Getter
	@Setter
	private LocalDateTime dhEvento;
	
	@Getter
	@Setter 
	private String seqEvento;
	
	@Getter
	@Setter
	@Lob
	private String respostaAcbrEvento;
	
	@Getter
	@Setter
	private String caminhoEvento;
	
	@Getter
	@Setter
	private String protocolo;
	
	@Getter
	@Setter
	private String xMotivo;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(caminhoEvento, dhEvento, motivoCorrecao, nfe, protocolo,
				respostaAcbrEvento, seqEvento, xMotivo);
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
		CartaCorrecao other = (CartaCorrecao) obj;
		return Objects.equals(caminhoEvento, other.caminhoEvento) && Objects.equals(dhEvento, other.dhEvento)
				&& Objects.equals(motivoCorrecao, other.motivoCorrecao) && Objects.equals(nfe, other.nfe)
				&& Objects.equals(protocolo, other.protocolo)
				&& Objects.equals(respostaAcbrEvento, other.respostaAcbrEvento)
				&& Objects.equals(seqEvento, other.seqEvento) && Objects.equals(xMotivo, other.xMotivo);
	}
	
}
