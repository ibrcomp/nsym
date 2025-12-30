package br.com.nsym.domain.model.entity.financeiro.tools;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.financeiro.AgPedido;
import br.com.nsym.domain.model.entity.financeiro.AgTitulo;
import br.com.nsym.domain.model.entity.financeiro.Caixa;
import br.com.nsym.domain.model.entity.financeiro.ContaCorrente;
import br.com.nsym.domain.model.entity.financeiro.FormaDePagamento;
import br.com.nsym.domain.model.entity.financeiro.MovimentoCaixa;
import br.com.nsym.domain.model.entity.financeiro.RecebimentoParcial;
import br.com.nsym.domain.model.entity.financeiro.TipoPagamento;
import br.com.nsym.domain.model.entity.fiscal.Cfe.Nfce;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
public abstract class RecebimentoParcialAbstract extends PersistentEntity {

	/**
	 *
	 */	
	private static final long serialVersionUID = -2846149612678296522L;

	@Getter
	@Setter
	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name="TabelaListParcelasPorRecebimento",joinColumns= @JoinColumn(name="RecParc_ID"),
	inverseJoinColumns= @JoinColumn(name="Parcela_id"))
	private List<ParcelasNfe> listaParcelasPorRecebimentoParcial = new ArrayList<ParcelasNfe>();
	
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="FormaPagamento_ID")
	private FormaDePagamento formaPagamento;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoPagamento tipoPagamento;
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorRecebido = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal troco = new BigDecimal("0");
	
	@Getter
	@Setter
	@OneToOne (cascade = CascadeType.ALL)
	@JoinColumn(name="Movimento_ID")
	private MovimentoCaixa movimento;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private MovimentoEnum livroCaixa;
	
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="Caixa_ID")
	private Caixa caixa;
	
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="AgPedido_ID",referencedColumnName = "id")
	private AgPedido agrupado;
	
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="AgTitulo_ID",referencedColumnName = "id")
	private AgTitulo agTitulo;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Conta_ID",referencedColumnName = "id")
	private ContaCorrente contaCorrente;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Nfce_ID",referencedColumnName = "id")
	private Nfce nfce;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(listaParcelasPorRecebimentoParcial, agrupado, caixa, formaPagamento,
				livroCaixa, movimento, tipoPagamento, troco, valorRecebido);
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
		RecebimentoParcial other = (RecebimentoParcial) obj;
		return Objects.equals(listaParcelasPorRecebimentoParcial, other.getListaParcelasPorRecebimentoParcial())
				&& Objects.equals(agrupado, other.getAgrupado()) && Objects.equals(caixa, other.getCaixa())
				&& Objects.equals(formaPagamento, other.getFormaPagamento()) && livroCaixa == other.getLivroCaixa()
				&& Objects.equals(movimento, other.getMovimento()) && tipoPagamento == other.getTipoPagamento()
				&& Objects.equals(troco, other.getTroco()) && Objects.equals(valorRecebido, other.getValorRecebido());
	}


	@Override
	public String toString() {
		return "RecebimentoParcial [ListaParcelasPorRecebimentoParcial=" + listaParcelasPorRecebimentoParcial
				+ ", formaPagamento=" + formaPagamento + ", tipoPagamento=" + tipoPagamento + ", valorRecebido="
				+ valorRecebido + ", troco=" + troco + ", movimento=" + movimento + ", livroCaixa=" + livroCaixa
				+ ", caixa=" + caixa + ", agrupado=" + agrupado + "]";
	}



}
