package br.com.nsym.domain.model.entity.fiscal.nfe;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Adicao extends PersistentEntity {

	
	/**
	 *
	 */
	private static final long serialVersionUID = -2936018001578434162L;

	@Transient
	private MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);
	
	@Getter
	@Setter
	private String nAdicao;
	@Getter
	@Setter
	private String nSeqAdic;
	@Getter
	@Setter
	private String cFabricante; // informar o codigo de exportador do fornecedor ou o codigo interno do fornecedor
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal vDescDI= new BigDecimal("0",mc);
	@Getter
	@Setter
	private String nDraw;
	@Getter
	@Setter
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="DI_ID")
	private DI di;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(cFabricante, di, mc, nAdicao, nDraw, nSeqAdic, vDescDI);
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
		Adicao other = (Adicao) obj;
		return Objects.equals(cFabricante, other.cFabricante) && Objects.equals(di, other.di)
				&& Objects.equals(mc, other.mc) && Objects.equals(nAdicao, other.nAdicao)
				&& Objects.equals(nDraw, other.nDraw) && Objects.equals(nSeqAdic, other.nSeqAdic)
				&& Objects.equals(vDescDI, other.vDescDI);
	}
	

}
