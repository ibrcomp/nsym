package br.com.nsym.domain.model.entity.fiscal.Cfe;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import br.com.nsym.domain.model.entity.fiscal.Item;
import lombok.Getter;
import lombok.Setter;

@Entity
public class ItemCFe extends Item {

	
//	@Transient
//	private MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);
	
	/**
	 *
	 */
	private static final long serialVersionUID = 1608331447314545917L;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="CFe_id",referencedColumnName = "id")
	private CFe cfe;
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal vRatDesc= new BigDecimal("0"); 
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal vBasePis= new BigDecimal("0"); 
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal vBaseCofins= new BigDecimal("0"); 
	
	@Getter
	@Setter
	@Column(precision = 5 , scale = 2)
	private BigDecimal aliqIcmsSat= new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal vRatAcr= new BigDecimal("0");

	

	@Override
	public String toString() {
		return "ItemCFe [cfe=" + cfe + ", vRatDesc=" + vRatDesc + ", vBasePis=" + vBasePis + ", vBaseCofins="
				+ vBaseCofins + ", aliqIcmsSat=" + aliqIcmsSat + ", vRatAcr=" + vRatAcr + ", getProduto()="
				+ getProduto() + "]";
	}

	
}
