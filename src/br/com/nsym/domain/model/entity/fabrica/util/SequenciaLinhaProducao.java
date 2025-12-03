package br.com.nsym.domain.model.entity.fabrica.util;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="SequenciaLinhaProducao",uniqueConstraints = {@UniqueConstraint(columnNames={"indice","Linha_Id","id_empresa"})})
public class SequenciaLinhaProducao extends PersistentEntity{

	/**
	 *
	 */
	private static final long serialVersionUID = -7355826323967888873L;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Etapa_Id")
	private EtapaProducao etapa;
	
	@Getter
	@Setter
	private Long indice;
	
	@Getter
	@Setter
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="Linha_Id")
	private LinhaProducao linha;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(etapa, indice, linha);
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
		SequenciaLinhaProducao other = (SequenciaLinhaProducao) obj;
		return Objects.equals(etapa, other.etapa) && Objects.equals(indice, other.indice)
				&& Objects.equals(linha, other.linha);
	}

	@Override
	public String toString() {
		return "SequenciaLinhaProducao [etapa=" + etapa.getDescricao() + "]";
	}
	
	

}
