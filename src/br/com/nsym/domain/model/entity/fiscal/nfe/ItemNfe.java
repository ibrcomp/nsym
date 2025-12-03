package br.com.nsym.domain.model.entity.fiscal.nfe;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.BarrasEstoque;
import br.com.nsym.domain.model.entity.cadastro.Produto;
import br.com.nsym.domain.model.entity.fiscal.CFOP;
import br.com.nsym.domain.model.entity.fiscal.Tributos;
import br.com.nsym.domain.model.entity.fiscal.reforma.CClassTrib;
import br.com.nsym.domain.model.entity.fiscal.reforma.CstIbsCbs;
import lombok.Getter;
import lombok.Setter;

@Entity
public class ItemNfe extends PersistentEntity{
	
	private static final long serialVersionUID = -1344486796185847101L;


	@Transient
	private MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);
	
	
	@Getter
	@Setter
	private int row;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="produto_ID")
	private Produto produto;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="NFE_id")
	private Nfe nfe;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Barras_ID")
	private BarrasEstoque barras;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="NFERecebida_id")
	private NfeRecebida nfeRecebida;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Tributo")
	private Tributos tributo;
	
	@Getter
	@Setter
	private boolean itemST = false;
	
	@Getter
	@Setter
	private String obsItem;

	@Getter
	@Setter
	private String unidade;

	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal valorUnitario= new BigDecimal("0",mc);

	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal quantidade= new BigDecimal("0",mc);

	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal valorTotal= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal valorTotalBruto= new BigDecimal("0",mc);

	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal baseICMS= new BigDecimal("0",mc);

	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal valorIcms= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal aliqIcms= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal baseICMSSt= new BigDecimal("0",mc);

	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal valorIcmsSt= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal mvaSt= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal aliqIcmsSt= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal valorIPI= new BigDecimal("0",mc);


	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal aliqIPI= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal baseIPI= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal aliqCofins= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal baseCofins= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal valorCofins= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal aliqPis= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal basePis= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal valorPis= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal valorFrete= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal valorSeguro= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal valorDespesas= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal desconto = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	private boolean isPorcentagem = true;

	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal pFCP = new BigDecimal("0",mc);

	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal vFCP = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal valorTotalTributoItem = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Cfop")
	private CFOP cfopItem;
	
	@Getter
	@Setter
	private String cst;
	
	@Getter
	@Setter
	private int cstPis;
	
	@Getter
	@Setter
	private int cstCofins;
	
	@Getter
	@Setter
	private String cstIpi;
	
	@Getter
	@Setter
	private int origem;
	
	@Getter
	@Setter
	private String cest;
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal vBCUFDest = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal pFCPUFDest = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal pICMSUFDest= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal pICMSInter= new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal pICMSInterPart= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal vFCPUFDest= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal vICMSUFDest= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal vICMSUFRemet= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@OneToOne(cascade=CascadeType.ALL)
	private II ii;
	
	@Transient
	@Getter
	@Setter
	private String cEnq;
	
	@Transient
	@Getter
	@Setter
	private String modBC;
	
	/**
	 * Novos campos criados para contemplar a nova reforma tributaria de 2025
	 */
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="cclassTrib")
	private CClassTrib cclassTrib;
	
	@Getter
	@Setter
	private String cstCbs;
	
	@Getter
	@Setter
	@Column(name = "vbc_cbs", precision = 15, scale = 2) 
	private BigDecimal vbcCbs;
	
	@Getter
	@Setter
	@Column(name = "p_cbs",   precision = 10, scale = 4)
	private BigDecimal pCbs;
	
	@Getter
	@Setter
	@Column(name = "v_cbs",   precision = 15, scale = 2)
	private BigDecimal vCbs;
	
	@Getter
	@Setter
	@Column(name = "vDif_Cbs", precision = 15, scale = 2)
	private BigDecimal vDifCbs;
	
	@Getter
	@Setter
	private String cstIbs;

	@Getter
	@Setter
	@Column(name = "vbc_ibs", precision = 15, scale = 2)
	private BigDecimal vbcIbs;
	
	@Getter
	@Setter
	@Column(name = "vDif_ibs", precision = 15, scale = 2)
	private BigDecimal vDifIbs;
	
	@Getter
	@Setter
	@Column(name = "p_ibs",   precision = 10, scale = 4)
	private BigDecimal pIbs;
	
	@Getter
	@Setter
	@Column(name = "v_ibs",   precision = 15, scale = 2)
	private BigDecimal vIbs;
	
	@Getter
	@Setter
	private String cstIs;
	
	@Getter
	@Setter
	@Column(name = "vbc_is",  precision = 15, scale = 2)
	private BigDecimal vbcIs;
	
	@Getter
	@Setter
	@Column(name = "p_is",    precision = 10, scale = 4)
	private BigDecimal pIs;
	
	@Getter
	@Setter
	@Column(name = "v_is",    precision = 15, scale = 2)
	private BigDecimal vIs;
	
	@Getter
    @Setter
    @Transient
    private Boolean indSemIbsm;


	@Override
	public String toString() {
		return String.format(
				"ItemNfe [row=%s, produto=%s, nfe=%s, tributo=%s, itemST=%s, obsItem=%s, unidade=%s, valorUnitario=%s, quantidade=%s, valorTotal=%s, valorTotalBruto=%s, baseICMS=%s, valorIcms=%s, aliqIcms=%s, baseICMSSt=%s, valorIcmsSt=%s, mvaSt=%s, aliqIcmsSt=%s, valorIPI=%s, aliqIPI=%s, aliqCofins=%s, valorCofins=%s, aliqPis=%s, valorPis=%s, valorFrete=%s, valorSeguro=%s, valorDespesas=%s, desconto=%s, isPorcentagem=%s, pFCP=%s, vFCP=%s, valorTotalTributoItem=%s, cfopItem=%s, cst=%s, cstPis=%s, cstCofins=%s, cstIpi=%s, origem=%s, cest=%s, vBCUFDest=%s, pFCPUFDest=%s, pICMSUFDest=%s, pICMSInter=%s, pICMSInterPart=%s, vFCPUFDest=%s, vICMSUFDest=%s, vICMSUFRemet=%s, ii=%s]",
				row, produto, nfe, tributo, itemST, obsItem, unidade, valorUnitario, quantidade, valorTotal,
				valorTotalBruto, baseICMS, valorIcms, aliqIcms, baseICMSSt, valorIcmsSt, mvaSt, aliqIcmsSt, valorIPI,
				aliqIPI, aliqCofins, valorCofins, aliqPis, valorPis, valorFrete, valorSeguro, valorDespesas, desconto,
				isPorcentagem, pFCP, vFCP, valorTotalTributoItem, cfopItem, cst, cstPis, cstCofins, cstIpi, origem,
				cest, vBCUFDest, pFCPUFDest, pICMSUFDest, pICMSInter, pICMSInterPart, vFCPUFDest, vICMSUFDest,
				vICMSUFRemet, ii);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((aliqCofins == null) ? 0 : aliqCofins.hashCode());
		result = prime * result + ((aliqIPI == null) ? 0 : aliqIPI.hashCode());
		result = prime * result + ((aliqIcms == null) ? 0 : aliqIcms.hashCode());
		result = prime * result + ((aliqIcmsSt == null) ? 0 : aliqIcmsSt.hashCode());
		result = prime * result + ((aliqPis == null) ? 0 : aliqPis.hashCode());
		result = prime * result + ((baseICMS == null) ? 0 : baseICMS.hashCode());
		result = prime * result + ((baseICMSSt == null) ? 0 : baseICMSSt.hashCode());
		result = prime * result + ((cest == null) ? 0 : cest.hashCode());
		result = prime * result + ((cfopItem == null) ? 0 : cfopItem.hashCode());
		result = prime * result + ((cst == null) ? 0 : cst.hashCode());
		result = prime * result + cstCofins;
		result = prime * result + ((cstIpi == null) ? 0 : cstIpi.hashCode());
		result = prime * result + cstPis;
		result = prime * result + ((desconto == null) ? 0 : desconto.hashCode());
		result = prime * result + ((ii == null) ? 0 : ii.hashCode());
		result = prime * result + (isPorcentagem ? 1231 : 1237);
		result = prime * result + (itemST ? 1231 : 1237);
		result = prime * result + ((mc == null) ? 0 : mc.hashCode());
		result = prime * result + ((mvaSt == null) ? 0 : mvaSt.hashCode());
		result = prime * result + ((nfe == null) ? 0 : nfe.hashCode());
		result = prime * result + ((obsItem == null) ? 0 : obsItem.hashCode());
		result = prime * result + origem;
		result = prime * result + ((pFCP == null) ? 0 : pFCP.hashCode());
		result = prime * result + ((pFCPUFDest == null) ? 0 : pFCPUFDest.hashCode());
		result = prime * result + ((pICMSInter == null) ? 0 : pICMSInter.hashCode());
		result = prime * result + ((pICMSInterPart == null) ? 0 : pICMSInterPart.hashCode());
		result = prime * result + ((pICMSUFDest == null) ? 0 : pICMSUFDest.hashCode());
		result = prime * result + ((produto == null) ? 0 : produto.hashCode());
		result = prime * result + ((quantidade == null) ? 0 : quantidade.hashCode());
		result = prime * result + row;
		result = prime * result + ((tributo == null) ? 0 : tributo.hashCode());
		result = prime * result + ((unidade == null) ? 0 : unidade.hashCode());
		result = prime * result + ((vBCUFDest == null) ? 0 : vBCUFDest.hashCode());
		result = prime * result + ((vFCP == null) ? 0 : vFCP.hashCode());
		result = prime * result + ((vFCPUFDest == null) ? 0 : vFCPUFDest.hashCode());
		result = prime * result + ((vICMSUFDest == null) ? 0 : vICMSUFDest.hashCode());
		result = prime * result + ((vICMSUFRemet == null) ? 0 : vICMSUFRemet.hashCode());
		result = prime * result + ((valorCofins == null) ? 0 : valorCofins.hashCode());
		result = prime * result + ((valorDespesas == null) ? 0 : valorDespesas.hashCode());
		result = prime * result + ((valorFrete == null) ? 0 : valorFrete.hashCode());
		result = prime * result + ((valorIPI == null) ? 0 : valorIPI.hashCode());
		result = prime * result + ((valorIcms == null) ? 0 : valorIcms.hashCode());
		result = prime * result + ((valorIcmsSt == null) ? 0 : valorIcmsSt.hashCode());
		result = prime * result + ((valorPis == null) ? 0 : valorPis.hashCode());
		result = prime * result + ((valorSeguro == null) ? 0 : valorSeguro.hashCode());
		result = prime * result + ((valorTotal == null) ? 0 : valorTotal.hashCode());
		result = prime * result + ((valorTotalBruto == null) ? 0 : valorTotalBruto.hashCode());
		result = prime * result + ((valorTotalTributoItem == null) ? 0 : valorTotalTributoItem.hashCode());
		result = prime * result + ((valorUnitario == null) ? 0 : valorUnitario.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ItemNfe other = (ItemNfe) obj;
		if (aliqCofins == null) {
			if (other.aliqCofins != null) {
				return false;
			}
		} else if (!aliqCofins.equals(other.aliqCofins)) {
			return false;
		}
		if (aliqIPI == null) {
			if (other.aliqIPI != null) {
				return false;
			}
		} else if (!aliqIPI.equals(other.aliqIPI)) {
			return false;
		}
		if (aliqIcms == null) {
			if (other.aliqIcms != null) {
				return false;
			}
		} else if (!aliqIcms.equals(other.aliqIcms)) {
			return false;
		}
		if (aliqIcmsSt == null) {
			if (other.aliqIcmsSt != null) {
				return false;
			}
		} else if (!aliqIcmsSt.equals(other.aliqIcmsSt)) {
			return false;
		}
		if (aliqPis == null) {
			if (other.aliqPis != null) {
				return false;
			}
		} else if (!aliqPis.equals(other.aliqPis)) {
			return false;
		}
		if (baseICMS == null) {
			if (other.baseICMS != null) {
				return false;
			}
		} else if (!baseICMS.equals(other.baseICMS)) {
			return false;
		}
		if (baseICMSSt == null) {
			if (other.baseICMSSt != null) {
				return false;
			}
		} else if (!baseICMSSt.equals(other.baseICMSSt)) {
			return false;
		}
		if (cest == null) {
			if (other.cest != null) {
				return false;
			}
		} else if (!cest.equals(other.cest)) {
			return false;
		}
		if (cfopItem == null) {
			if (other.cfopItem != null) {
				return false;
			}
		} else if (!cfopItem.equals(other.cfopItem)) {
			return false;
		}
		if (cst == null) {
			if (other.cst != null) {
				return false;
			}
		} else if (!cst.equals(other.cst)) {
			return false;
		}
		if (cstCofins != other.cstCofins) {
			return false;
		}
		if (cstIpi == null) {
			if (other.cstIpi != null) {
				return false;
			}
		} else if (!cstIpi.equals(other.cstIpi)) {
			return false;
		}
		if (cstPis != other.cstPis) {
			return false;
		}
		if (desconto == null) {
			if (other.desconto != null) {
				return false;
			}
		} else if (!desconto.equals(other.desconto)) {
			return false;
		}
		if (ii == null) {
			if (other.ii != null) {
				return false;
			}
		} else if (!ii.equals(other.ii)) {
			return false;
		}
		if (isPorcentagem != other.isPorcentagem) {
			return false;
		}
		if (itemST != other.itemST) {
			return false;
		}
		if (mc == null) {
			if (other.mc != null) {
				return false;
			}
		} else if (!mc.equals(other.mc)) {
			return false;
		}
		if (mvaSt == null) {
			if (other.mvaSt != null) {
				return false;
			}
		} else if (!mvaSt.equals(other.mvaSt)) {
			return false;
		}
		if (nfe == null) {
			if (other.nfe != null) {
				return false;
			}
		} else if (!nfe.equals(other.nfe)) {
			return false;
		}
		if (obsItem == null) {
			if (other.obsItem != null) {
				return false;
			}
		} else if (!obsItem.equals(other.obsItem)) {
			return false;
		}
		if (origem != other.origem) {
			return false;
		}
		if (pFCP == null) {
			if (other.pFCP != null) {
				return false;
			}
		} else if (!pFCP.equals(other.pFCP)) {
			return false;
		}
		if (pFCPUFDest == null) {
			if (other.pFCPUFDest != null) {
				return false;
			}
		} else if (!pFCPUFDest.equals(other.pFCPUFDest)) {
			return false;
		}
		if (pICMSInter == null) {
			if (other.pICMSInter != null) {
				return false;
			}
		} else if (!pICMSInter.equals(other.pICMSInter)) {
			return false;
		}
		if (pICMSInterPart == null) {
			if (other.pICMSInterPart != null) {
				return false;
			}
		} else if (!pICMSInterPart.equals(other.pICMSInterPart)) {
			return false;
		}
		if (pICMSUFDest == null) {
			if (other.pICMSUFDest != null) {
				return false;
			}
		} else if (!pICMSUFDest.equals(other.pICMSUFDest)) {
			return false;
		}
		if (produto == null) {
			if (other.produto != null) {
				return false;
			}
		} else if (!produto.equals(other.produto)) {
			return false;
		}
		if (quantidade == null) {
			if (other.quantidade != null) {
				return false;
			}
		} else if (!quantidade.equals(other.quantidade)) {
			return false;
		}
		if (row != other.row) {
			return false;
		}
		if (tributo == null) {
			if (other.tributo != null) {
				return false;
			}
		} else if (!tributo.equals(other.tributo)) {
			return false;
		}
		if (unidade == null) {
			if (other.unidade != null) {
				return false;
			}
		} else if (!unidade.equals(other.unidade)) {
			return false;
		}
		if (vBCUFDest == null) {
			if (other.vBCUFDest != null) {
				return false;
			}
		} else if (!vBCUFDest.equals(other.vBCUFDest)) {
			return false;
		}
		if (vFCP == null) {
			if (other.vFCP != null) {
				return false;
			}
		} else if (!vFCP.equals(other.vFCP)) {
			return false;
		}
		if (vFCPUFDest == null) {
			if (other.vFCPUFDest != null) {
				return false;
			}
		} else if (!vFCPUFDest.equals(other.vFCPUFDest)) {
			return false;
		}
		if (vICMSUFDest == null) {
			if (other.vICMSUFDest != null) {
				return false;
			}
		} else if (!vICMSUFDest.equals(other.vICMSUFDest)) {
			return false;
		}
		if (vICMSUFRemet == null) {
			if (other.vICMSUFRemet != null) {
				return false;
			}
		} else if (!vICMSUFRemet.equals(other.vICMSUFRemet)) {
			return false;
		}
		if (valorCofins == null) {
			if (other.valorCofins != null) {
				return false;
			}
		} else if (!valorCofins.equals(other.valorCofins)) {
			return false;
		}
		if (valorDespesas == null) {
			if (other.valorDespesas != null) {
				return false;
			}
		} else if (!valorDespesas.equals(other.valorDespesas)) {
			return false;
		}
		if (valorFrete == null) {
			if (other.valorFrete != null) {
				return false;
			}
		} else if (!valorFrete.equals(other.valorFrete)) {
			return false;
		}
		if (valorIPI == null) {
			if (other.valorIPI != null) {
				return false;
			}
		} else if (!valorIPI.equals(other.valorIPI)) {
			return false;
		}
		if (valorIcms == null) {
			if (other.valorIcms != null) {
				return false;
			}
		} else if (!valorIcms.equals(other.valorIcms)) {
			return false;
		}
		if (valorIcmsSt == null) {
			if (other.valorIcmsSt != null) {
				return false;
			}
		} else if (!valorIcmsSt.equals(other.valorIcmsSt)) {
			return false;
		}
		if (valorPis == null) {
			if (other.valorPis != null) {
				return false;
			}
		} else if (!valorPis.equals(other.valorPis)) {
			return false;
		}
		if (valorSeguro == null) {
			if (other.valorSeguro != null) {
				return false;
			}
		} else if (!valorSeguro.equals(other.valorSeguro)) {
			return false;
		}
		if (valorTotal == null) {
			if (other.valorTotal != null) {
				return false;
			}
		} else if (!valorTotal.equals(other.valorTotal)) {
			return false;
		}
		if (valorTotalBruto == null) {
			if (other.valorTotalBruto != null) {
				return false;
			}
		} else if (!valorTotalBruto.equals(other.valorTotalBruto)) {
			return false;
		}
		if (valorTotalTributoItem == null) {
			if (other.valorTotalTributoItem != null) {
				return false;
			}
		} else if (!valorTotalTributoItem.equals(other.valorTotalTributoItem)) {
			return false;
		}
		if (valorUnitario == null) {
			if (other.valorUnitario != null) {
				return false;
			}
		} else if (!valorUnitario.equals(other.valorUnitario)) {
			return false;
		}
		return true;
	}
	
	
//	
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = super.hashCode();
//		result = prime * result + Objects.hash(barras, produto, quantidade, row, valorTotal, valorUnitario);
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (!super.equals(obj))
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		ItemNfe other = (ItemNfe) obj;
//		return Objects.equals(barras, other.barras) && Objects.equals(produto, other.produto)
//				&& Objects.equals(quantidade, other.quantidade) && row == other.row
//				&& Objects.equals(valorTotal, other.valorTotal) && Objects.equals(valorUnitario, other.valorUnitario);
//	}


	
}
