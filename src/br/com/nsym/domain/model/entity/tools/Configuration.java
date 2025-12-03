
package br.com.nsym.domain.model.entity.tools;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.com.nsym.domain.misc.ModeloImpressoraAcbr;
import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.security.UserTypeEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Defini��es de utiliza��o do sistema
 * @author ibrcomp
 *
 */

@Entity
@ToString(callSuper = true)
@Table(name = "configurations")
@EqualsAndHashCode(callSuper = true)
public class Configuration extends PersistentEntity {
	
	private static final long serialVersionUID = 758259805500976628L;
	
	@Getter
	@Setter
	@OneToOne(mappedBy = "config",cascade = {CascadeType.DETACH,CascadeType.PERSIST})
	private UserTypeEntity user;
	
	@Getter
	@Setter
	private boolean alteraPrecoNFeAvulso = false;
	
	@Setter
	@Getter
	private boolean alteraPrecoSatAvulso = false;
	
	@Getter
	@Setter
	private boolean alteraPrecoVenda = false;
	
	@Getter
	@Setter
	private boolean alteraPrecoTransferencia = false;
	
	@Getter
	@Setter
	private boolean resumoCupomPdv = false;
	
	@Getter
	@Setter
	private boolean cabecalhoPDV = true;
	
	@Getter
	@Setter
	private String portaUsbVendaPdv;
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal quantidadePadraoPDV = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal quantViaVenda = new BigDecimal("1");
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private ModeloImpressoraAcbr impressoraPdv;
	
	@Getter
	@Setter
	private String portaACBR;
	
	@Getter
	@Setter
	private String ipACBR;
	
	@Getter
	@Setter
	private Long transacaoPadrao ;
	
	@Getter
	@Setter
	private boolean desconto;
	
	@Getter
	@Setter
	private BigDecimal porcentagemDesconto;
	
	@Getter
	@Setter
	private boolean descontoPDV = false;
	
	@Getter
	@Setter
	private String mensPDV;
	
	@Getter
	@Setter
	private boolean fantasia = false;
	
	@Getter
	@Setter
	private boolean vendaCaixa = false;
	
	@Getter
	@Setter
	private boolean cupomPDF = false;
	
	@Getter
	@Setter
	private boolean alteraPrecoServico = false;
}
