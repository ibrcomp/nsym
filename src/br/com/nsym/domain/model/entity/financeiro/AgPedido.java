package br.com.nsym.domain.model.entity.financeiro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.financeiro.tools.ParcelasNfe;
import br.com.nsym.domain.model.entity.fiscal.Cfe.CFe;
import br.com.nsym.domain.model.entity.tools.PedidoStatus;
import br.com.nsym.domain.model.entity.venda.DestinatarioPedido;
import br.com.nsym.domain.model.entity.venda.ItemPedido;
import br.com.nsym.domain.model.entity.venda.Pedido;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "AgPedido", uniqueConstraints = { @UniqueConstraint(columnNames = { "id", "id_empresa", "id_filial" }) })
public class AgPedido extends PersistentEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = 1080434598546704467L;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "Destinatario_ID")
	private DestinatarioPedido destinatario;

	@Getter
	@Setter
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "TabelaListaPedidosRecebidos", joinColumns = @JoinColumn(name = "AgPedido_ID"), inverseJoinColumns = @JoinColumn(name = "Pedido_ID"))
	private List<Pedido> listaPedidosRecebidos = new ArrayList<Pedido>();

	@Getter
	@Setter
	@OneToMany(fetch = FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "TabelaListaItensAgrupados", joinColumns = @JoinColumn(name = "AgPedido_ID"), inverseJoinColumns = @JoinColumn(name = "Item_ID"))
	private List<ItemPedido> listaItensAgrupados = new ArrayList<ItemPedido>();

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "CFe_ID")
	private CFe cfe;

	@Getter
	@Setter
	@OneToMany(fetch = FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "TabelaListaParcelamento", joinColumns = @JoinColumn(name = "AgPedido_ID"), inverseJoinColumns = @JoinColumn(name = "Parcela_id"))
	private List<ParcelasNfe> listaParcelas = new ArrayList<ParcelasNfe>();

	@Getter
	@Setter
	@OneToMany(fetch = FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "TabelaListaRecebimentoParcialAgPedido", joinColumns = @JoinColumn(name = "AgPedido_ID"), inverseJoinColumns = @JoinColumn(name = "RecParcial_id"))
	private List<RecebimentoParcial> listRecebimentoParcial = new ArrayList<RecebimentoParcial>();

	@Getter
	@Setter
	@Column(precision = 19, scale = 5)
	private BigDecimal valorTotal = new BigDecimal("0");

	@Getter
	@Setter
	@Column(precision = 19, scale = 5)
	private BigDecimal valorBruto = new BigDecimal("0");

	@Getter
	@Setter
	@Column(precision = 19, scale = 5)
	private BigDecimal desconto = new BigDecimal("0");

	@Getter
	@Setter
	@Column(precision = 19, scale = 5)
	private BigDecimal frete = new BigDecimal("0");

	@Getter
	@Setter
	@Column(precision = 19, scale = 5)
	private BigDecimal acrescimo = new BigDecimal("0");

	@Getter
	@Setter
	@Column(precision = 19, scale = 5)
	private BigDecimal valorRecebido = new BigDecimal("0");

	@Getter
	@Setter
	private LocalDate dataCriacao;

	@Getter
	@Setter
	private LocalTime horaCriacao;

	@Getter
	@Setter
	private LocalDate dataRec;

	@Getter
	@Setter
	private LocalTime horaRec;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private PedidoStatus status;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "Caixa_id")
	private Caixa caixa;

	@Override
	public String toString() {
		return "AgPedido [listaPedidosRecebidos=" + listaPedidosRecebidos + ", listaItensAgrupados="
				+ listaItensAgrupados + ", listaParcelas=" + listaParcelas + ", valorTotal=" + valorTotal
				+ ", desconto=" + desconto + ", frete=" + frete + ", acrescimo=" + acrescimo + ", valorRecebido="
				+ valorRecebido + ", dataCriacao=" + dataCriacao + ", horaCriacao=" + horaCriacao + ", dataRec="
				+ dataRec + ", horaRec=" + horaRec + ", status=" + status + "]";
	}

}
