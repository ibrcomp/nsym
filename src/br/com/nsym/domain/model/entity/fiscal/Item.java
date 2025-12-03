package br.com.nsym.domain.model.entity.fiscal;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.BarrasEstoque;
import br.com.nsym.domain.model.entity.cadastro.Produto;
import br.com.nsym.domain.model.entity.fiscal.nfe.II;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
public abstract class Item extends PersistentEntity   {

	/**
	 *
	 */
	private static final long serialVersionUID = -8286764619371277767L;

	@Transient
	private MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);
	
	@Getter
	@Setter
	private String ref;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="produto_ID")
	private Produto produto;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Barras_ID")
	private BarrasEstoque barras;

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
	private BigDecimal aliqCofins= new BigDecimal("0",mc);
	
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
    @Column(name = "vbc_ibs", precision = 15, scale = 2) 
	private BigDecimal vbcIbs;
	
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(aliqCofins, aliqIPI, aliqIcms, aliqIcmsSt, aliqPis, barras, baseICMS,
				baseICMSSt, cEnq, cest, cfopItem, cst, cstCofins, cstIpi, cstPis, desconto, ii, isPorcentagem, itemST,
				mc, modBC, mvaSt, obsItem, origem, pCbs, pFCP, pFCPUFDest, pICMSInter, pICMSInterPart, pICMSUFDest,
				pIbs, pIs, produto, quantidade, ref, tributo, unidade, vBCUFDest, vCbs, vFCP, vFCPUFDest, vICMSUFDest,
				vICMSUFRemet, vIbs, vIs, valorCofins, valorDespesas, valorFrete, valorIPI, valorIcms, valorIcmsSt,
				valorPis, valorSeguro, valorTotal, valorTotalBruto, valorTotalTributoItem, valorUnitario, vbcCbs,
				vbcIbs, vbcIs);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof Item))
			return false;
		Item other = (Item) obj;
		return Objects.equals(aliqCofins, other.aliqCofins) && Objects.equals(aliqIPI, other.aliqIPI)
				&& Objects.equals(aliqIcms, other.aliqIcms) && Objects.equals(aliqIcmsSt, other.aliqIcmsSt)
				&& Objects.equals(aliqPis, other.aliqPis) && Objects.equals(barras, other.barras)
				&& Objects.equals(baseICMS, other.baseICMS) && Objects.equals(baseICMSSt, other.baseICMSSt)
				&& Objects.equals(cEnq, other.cEnq) && Objects.equals(cest, other.cest)
				&& Objects.equals(cfopItem, other.cfopItem) && Objects.equals(cst, other.cst)
				&& cstCofins == other.cstCofins && Objects.equals(cstIpi, other.cstIpi) && cstPis == other.cstPis
				&& Objects.equals(desconto, other.desconto) && Objects.equals(ii, other.ii)
				&& isPorcentagem == other.isPorcentagem && itemST == other.itemST && Objects.equals(mc, other.mc)
				&& Objects.equals(modBC, other.modBC) && Objects.equals(mvaSt, other.mvaSt)
				&& Objects.equals(obsItem, other.obsItem) && origem == other.origem && Objects.equals(pCbs, other.pCbs)
				&& Objects.equals(pFCP, other.pFCP) && Objects.equals(pFCPUFDest, other.pFCPUFDest)
				&& Objects.equals(pICMSInter, other.pICMSInter) && Objects.equals(pICMSInterPart, other.pICMSInterPart)
				&& Objects.equals(pICMSUFDest, other.pICMSUFDest) && Objects.equals(pIbs, other.pIbs)
				&& Objects.equals(pIs, other.pIs) && Objects.equals(produto, other.produto)
				&& Objects.equals(quantidade, other.quantidade) && Objects.equals(ref, other.ref)
				&& Objects.equals(tributo, other.tributo) && Objects.equals(unidade, other.unidade)
				&& Objects.equals(vBCUFDest, other.vBCUFDest) && Objects.equals(vCbs, other.vCbs)
				&& Objects.equals(vFCP, other.vFCP) && Objects.equals(vFCPUFDest, other.vFCPUFDest)
				&& Objects.equals(vICMSUFDest, other.vICMSUFDest) && Objects.equals(vICMSUFRemet, other.vICMSUFRemet)
				&& Objects.equals(vIbs, other.vIbs) && Objects.equals(vIs, other.vIs)
				&& Objects.equals(valorCofins, other.valorCofins) && Objects.equals(valorDespesas, other.valorDespesas)
				&& Objects.equals(valorFrete, other.valorFrete) && Objects.equals(valorIPI, other.valorIPI)
				&& Objects.equals(valorIcms, other.valorIcms) && Objects.equals(valorIcmsSt, other.valorIcmsSt)
				&& Objects.equals(valorPis, other.valorPis) && Objects.equals(valorSeguro, other.valorSeguro)
				&& Objects.equals(valorTotal, other.valorTotal)
				&& Objects.equals(valorTotalBruto, other.valorTotalBruto)
				&& Objects.equals(valorTotalTributoItem, other.valorTotalTributoItem)
				&& Objects.equals(valorUnitario, other.valorUnitario) && Objects.equals(vbcCbs, other.vbcCbs)
				&& Objects.equals(vbcIbs, other.vbcIbs) && Objects.equals(vbcIs, other.vbcIs);
	}

}
