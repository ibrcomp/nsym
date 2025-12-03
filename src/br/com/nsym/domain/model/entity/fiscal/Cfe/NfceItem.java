package br.com.nsym.domain.model.entity.fiscal.Cfe;

import javax.persistence.*;

import br.com.nsym.domain.model.entity.fiscal.Item;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "nfce_item")
public class NfceItem  extends Item{
	
    private static final long serialVersionUID = 5492916851574204859L;

    @Getter
	@Setter
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "nfce_id")
    private Nfce nfce;

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

	
}
