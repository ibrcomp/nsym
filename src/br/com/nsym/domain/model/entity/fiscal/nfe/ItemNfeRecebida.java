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
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.BarrasEstoque;
import br.com.nsym.domain.model.entity.cadastro.Produto;
import lombok.Getter;
import lombok.Setter;


@Entity
public class ItemNfeRecebida extends PersistentEntity {


	/**
	 *
	 */
	private static final long serialVersionUID = -5347278615741760775L;

	@Transient
	private MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);
	
	@Getter
	@Setter
	private String xProduto;
	@Getter
	@Setter
	private String cProd;
	@Getter
	@Setter
	private String ean;
	@Getter
	@Setter
	private String ncm;
	@Getter
	@Setter
	private String cest;
	@Getter
	@Setter
	private String cfop;
	@Getter
	@Setter
	private String uCom;
	@Getter
	@Setter
	private String vUnCom;
	@Getter
	@Setter
	private String qCom;
	@Getter
	@Setter
	private String vProd;
	@Getter
	@Setter
	private String vFrete;

	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="NFERecebida_id")
	private NfeRecebida nfeRecebida;

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
	private boolean estoqueAtualizado;
	
	@Transient
	@Getter
	@Setter
	private Produto produtoTemp;
	
	@Transient
	@Getter
	@Setter
	private BarrasEstoque barraEstoqueTemp;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(aliqCofins, aliqIPI, aliqIcms, aliqIcmsSt, aliqPis, barraEstoqueTemp,
				baseICMS, baseICMSSt, cEnq, cProd, cest, cfop, cst, cstCofins, cstIpi, cstPis, desconto, ean,
				estoqueAtualizado, ii, isPorcentagem, itemST, mc, modBC, mvaSt, ncm, nfeRecebida, obsItem, origem, pFCP,
				pFCPUFDest, pICMSInter, pICMSInterPart, pICMSUFDest, produtoTemp, qCom, quantidade, uCom, unidade,
				vBCUFDest, vFCP, vFCPUFDest, vFrete, vICMSUFDest, vICMSUFRemet, vProd, vUnCom, valorCofins,
				valorDespesas, valorFrete, valorIPI, valorIcms, valorIcmsSt, valorPis, valorSeguro, valorTotal,
				valorTotalBruto, valorTotalTributoItem, valorUnitario, xProduto);
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
		ItemNfeRecebida other = (ItemNfeRecebida) obj;
		return Objects.equals(aliqCofins, other.aliqCofins) && Objects.equals(aliqIPI, other.aliqIPI)
				&& Objects.equals(aliqIcms, other.aliqIcms) && Objects.equals(aliqIcmsSt, other.aliqIcmsSt)
				&& Objects.equals(aliqPis, other.aliqPis) && Objects.equals(barraEstoqueTemp, other.barraEstoqueTemp)
				&& Objects.equals(baseICMS, other.baseICMS) && Objects.equals(baseICMSSt, other.baseICMSSt)
				&& Objects.equals(cEnq, other.cEnq) && Objects.equals(cProd, other.cProd)
				&& Objects.equals(cest, other.cest) && Objects.equals(cfop, other.cfop)
				&& Objects.equals(cst, other.cst) && cstCofins == other.cstCofins
				&& Objects.equals(cstIpi, other.cstIpi) && cstPis == other.cstPis
				&& Objects.equals(desconto, other.desconto) && Objects.equals(ean, other.ean)
				&& estoqueAtualizado == other.estoqueAtualizado && Objects.equals(ii, other.ii)
				&& isPorcentagem == other.isPorcentagem && itemST == other.itemST && Objects.equals(mc, other.mc)
				&& Objects.equals(modBC, other.modBC) && Objects.equals(mvaSt, other.mvaSt)
				&& Objects.equals(ncm, other.ncm) && Objects.equals(nfeRecebida, other.nfeRecebida)
				&& Objects.equals(obsItem, other.obsItem) && origem == other.origem && Objects.equals(pFCP, other.pFCP)
				&& Objects.equals(pFCPUFDest, other.pFCPUFDest) && Objects.equals(pICMSInter, other.pICMSInter)
				&& Objects.equals(pICMSInterPart, other.pICMSInterPart)
				&& Objects.equals(pICMSUFDest, other.pICMSUFDest) && Objects.equals(produtoTemp, other.produtoTemp)
				&& Objects.equals(qCom, other.qCom) && Objects.equals(quantidade, other.quantidade)
				&& Objects.equals(uCom, other.uCom) && Objects.equals(unidade, other.unidade)
				&& Objects.equals(vBCUFDest, other.vBCUFDest) && Objects.equals(vFCP, other.vFCP)
				&& Objects.equals(vFCPUFDest, other.vFCPUFDest) && Objects.equals(vFrete, other.vFrete)
				&& Objects.equals(vICMSUFDest, other.vICMSUFDest) && Objects.equals(vICMSUFRemet, other.vICMSUFRemet)
				&& Objects.equals(vProd, other.vProd) && Objects.equals(vUnCom, other.vUnCom)
				&& Objects.equals(valorCofins, other.valorCofins) && Objects.equals(valorDespesas, other.valorDespesas)
				&& Objects.equals(valorFrete, other.valorFrete) && Objects.equals(valorIPI, other.valorIPI)
				&& Objects.equals(valorIcms, other.valorIcms) && Objects.equals(valorIcmsSt, other.valorIcmsSt)
				&& Objects.equals(valorPis, other.valorPis) && Objects.equals(valorSeguro, other.valorSeguro)
				&& Objects.equals(valorTotal, other.valorTotal)
				&& Objects.equals(valorTotalBruto, other.valorTotalBruto)
				&& Objects.equals(valorTotalTributoItem, other.valorTotalTributoItem)
				&& Objects.equals(valorUnitario, other.valorUnitario) && Objects.equals(xProduto, other.xProduto);
	}

}
