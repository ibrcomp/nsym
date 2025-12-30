package br.com.nsym.domain.model.entity.fiscal.Cfe;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.financeiro.FormaDePagamento;
import br.com.nsym.domain.model.entity.financeiro.RecebimentoParcial;
import br.com.nsym.domain.model.entity.financeiro.tools.ParcelasNfe;
import br.com.nsym.domain.model.entity.fiscal.tools.StatusNfe;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="cfe",uniqueConstraints = {@UniqueConstraint(columnNames={"numeroNota","id_empresa","id_filial"})})
public class CFe extends PersistentEntity {
	
	
	
	/**
	 *
	 */
	private static final long serialVersionUID = -3520063063617822407L;

	@Transient
	private MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);

	@Getter
	@Setter
	@OneToOne(cascade = CascadeType.ALL)
	private EmitenteCFe emitente;
	
	@Getter
	@Setter
	private String numeroNota;
	
	@Getter
	@Setter
	@OneToOne(cascade = CascadeType.ALL)
	private DestinatarioCFe destinatario;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="cfe",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ItemCFe> listaItem = new ArrayList<>();
	
	@Getter
	@Setter
	private LocalDate dataEmissao = LocalDate.now();
	
//	@Getter
//	@Setter
//	@ManyToOne
//	@JoinColumn(name="Pedido")
//	private PedidoVenda pedido;
	
	// verificar se vai estar sendo utilizado!
	@Getter
	@Setter
	private boolean emitido;
	
	@Getter
	@Setter
	private StatusNfe statusEmissao;
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal BaseIcms = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal ValorIcms = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal baseIcmsSubstituicao = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorIcmsSubstituicao = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorTotalProdutos = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorFrete = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorSeguro = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal desconto = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorTotalPis = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorTotalCofins = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal outrasDespesas = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorTotalIpi = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorTotalNota = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorTotalTributos = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal vFCP = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal vFCPST = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal vFCPSTRet = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal vIPIDevol = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal vCFe = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorIcmsDesonerado = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal vFCPUFDest = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal vICMSUFDest = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal vICMSUFRemet = new BigDecimal("0");
	 
	@Getter
	@Setter
	private String vCFeLei12741;
	
	@Getter
	@Setter
	private String vAcresSubtot;
	
	@Getter
	@Setter
	private String vDescSubtot;
	
	@Getter
	@Setter
	private String vTroco;
	
	@Getter
	@Setter
	private String cMP;
	
	@Getter
	@Setter
	private String vMP;
	
	// preenchido quando cupom emitido em modo avulso
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Pagamento_ID")
	private FormaDePagamento formaPagamento;
	
	// Preenchido quando o cupom é emitido pelo caixa
	@Getter
	@Setter
	@OneToMany(fetch = FetchType.LAZY)
	private List<RecebimentoParcial> listaRecebimentosAgrupados = new ArrayList<RecebimentoParcial>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="cfe",cascade = CascadeType.ALL)
	private List<ParcelasNfe> listaParcelas = new ArrayList<>();
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorTotalPisST = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorTotalCofinsSt = new BigDecimal("0");
	
	@Getter
	@Setter
	private String caminho;
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal valorCSLL= new BigDecimal("0");
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal aliqCSLL= new BigDecimal("0");
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal valorIRRF= new BigDecimal("0");
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal aliqIRRF= new BigDecimal("0");
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal vTrfIcms= new BigDecimal("0");
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal vFundoAmpara= new BigDecimal("0");
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal valorOutro= new BigDecimal("0");

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(BaseIcms, ValorIcms, baseIcmsSubstituicao, cMP, caminho, dataEmissao,
				desconto, destinatario, emitente, emitido, formaPagamento, listaItem, listaParcelas,
				listaRecebimentosAgrupados,
				mc, numeroNota, outrasDespesas, statusEmissao, vAcresSubtot, vCFe,
				vCFeLei12741, vDescSubtot, vFCP, vFCPST, vFCPSTRet, vFCPUFDest, vICMSUFDest, vICMSUFRemet, vIPIDevol,
				vMP, vTroco, valorFrete, valorIcmsDesonerado, valorIcmsSubstituicao, valorSeguro, valorTotalCofins,
				valorTotalCofinsSt, valorTotalIpi, valorTotalNota, valorTotalPis, valorTotalPisST, valorTotalProdutos,
				valorTotalTributos);
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
		CFe other = (CFe) obj;
		return Objects.equals(BaseIcms, other.BaseIcms) && Objects.equals(ValorIcms, other.ValorIcms)
				&& Objects.equals(baseIcmsSubstituicao, other.baseIcmsSubstituicao) && Objects.equals(cMP, other.cMP)
				&& Objects.equals(caminho, other.caminho) && Objects.equals(dataEmissao, other.dataEmissao)
				&& Objects.equals(desconto, other.desconto) && Objects.equals(destinatario, other.destinatario)
				&& Objects.equals(emitente, other.emitente) && emitido == other.emitido
				&& Objects.equals(formaPagamento, other.formaPagamento) && Objects.equals(listaItem, other.listaItem)
				&& Objects.equals(listaParcelas, other.listaParcelas)
				&& Objects.equals(listaRecebimentosAgrupados, other.listaRecebimentosAgrupados)
				&& Objects.equals(mc, other.mc) && Objects.equals(numeroNota, other.numeroNota)
				&& Objects.equals(outrasDespesas, other.outrasDespesas) && statusEmissao == other.statusEmissao
				&& Objects.equals(vAcresSubtot, other.vAcresSubtot) && Objects.equals(vCFe, other.vCFe)
				&& Objects.equals(vCFeLei12741, other.vCFeLei12741) && Objects.equals(vDescSubtot, other.vDescSubtot)
				&& Objects.equals(vFCP, other.vFCP) && Objects.equals(vFCPST, other.vFCPST)
				&& Objects.equals(vFCPSTRet, other.vFCPSTRet) && Objects.equals(vFCPUFDest, other.vFCPUFDest)
				&& Objects.equals(vICMSUFDest, other.vICMSUFDest) && Objects.equals(vICMSUFRemet, other.vICMSUFRemet)
				&& Objects.equals(vIPIDevol, other.vIPIDevol) && Objects.equals(vMP, other.vMP)
				&& Objects.equals(vTroco, other.vTroco) && Objects.equals(valorFrete, other.valorFrete)
				&& Objects.equals(valorIcmsDesonerado, other.valorIcmsDesonerado)
				&& Objects.equals(valorIcmsSubstituicao, other.valorIcmsSubstituicao)
				&& Objects.equals(valorSeguro, other.valorSeguro)
				&& Objects.equals(valorTotalCofins, other.valorTotalCofins)
				&& Objects.equals(valorTotalCofinsSt, other.valorTotalCofinsSt)
				&& Objects.equals(valorTotalIpi, other.valorTotalIpi)
				&& Objects.equals(valorTotalNota, other.valorTotalNota)
				&& Objects.equals(valorTotalPis, other.valorTotalPis)
				&& Objects.equals(valorTotalPisST, other.valorTotalPisST)
				&& Objects.equals(valorTotalProdutos, other.valorTotalProdutos)
				&& Objects.equals(valorTotalTributos, other.valorTotalTributos);
	}



	@Override
	public String toString() {
		return "CFe [emitente=" + emitente + ", numeroNota=" + numeroNota + ", destinatario=" + destinatario
				+ ", BaseIcms=" + BaseIcms + ", ValorIcms=" + ValorIcms + ", baseIcmsSubstituicao="
				+ baseIcmsSubstituicao + ", valorIcmsSubstituicao=" + valorIcmsSubstituicao + ", valorTotalProdutos="
				+ valorTotalProdutos + ", valorTotalPis=" + valorTotalPis + ", valorTotalCofins=" + valorTotalCofins
				+ ", valorTotalNota=" + valorTotalNota + ", vCFe=" + vCFe + ", formaPagamento=" + formaPagamento
				+ ", valorTotalPisST=" + valorTotalPisST + ", valorTotalCofinsSt=" + valorTotalCofinsSt + ", caminho="
				+ caminho + ", getId()=" + getId() + ", getIdEmpresa()=" + getIdEmpresa() + ", getIdFilial()="
				+ getIdFilial() + "]";
	}

	
}
