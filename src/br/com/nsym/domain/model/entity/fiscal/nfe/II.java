package br.com.nsym.domain.model.entity.fiscal.nfe;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
public class II extends PersistentEntity{

	/**
	 *
	 */
	private static final long serialVersionUID = 3778639112000616863L;

	@Transient
	private MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal vBC= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal vDespAdu= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal vII= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal vIOF= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@OneToOne(mappedBy="ii", cascade=CascadeType.ALL)
	private ItemNfe itemNfe;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(itemNfe, mc, vBC, vDespAdu, vII, vIOF);
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
		II other = (II) obj;
		return Objects.equals(itemNfe, other.itemNfe) && Objects.equals(mc, other.mc) && Objects.equals(vBC, other.vBC)
				&& Objects.equals(vDespAdu, other.vDespAdu) && Objects.equals(vII, other.vII)
				&& Objects.equals(vIOF, other.vIOF);
	}

	
}
