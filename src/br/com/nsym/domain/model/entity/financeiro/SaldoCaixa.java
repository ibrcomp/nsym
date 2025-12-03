package br.com.nsym.domain.model.entity.financeiro;

import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="SaldoCaixa",uniqueConstraints = {@UniqueConstraint(columnNames = {"Caixa_ID","forma"})})
public class SaldoCaixa extends PersistentEntity {
	
	/**
	 *
	 */
	private static final long serialVersionUID = -7647215089767026900L;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Caixa_ID")
	private Caixa caixa;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoPagamentoSimples forma;
	
	@Getter
	@Setter
	private BigDecimal valor;


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(caixa, forma, valor);
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
		SaldoCaixa other = (SaldoCaixa) obj;
		return Objects.equals(caixa, other.caixa) && forma == other.forma && Objects.equals(valor, other.valor);
	}


	@Override
	public String toString() {
		return "SaldoCaixa [caixa=" + caixa + ", forma=" + forma + ", valor=" + valor + "]";
	}
	
	

}
