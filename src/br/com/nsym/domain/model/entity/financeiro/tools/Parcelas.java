package br.com.nsym.domain.model.entity.financeiro.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.financeiro.AgPedido;
import br.com.nsym.domain.model.entity.financeiro.ContaCorrente;
import br.com.nsym.domain.model.entity.financeiro.FormaDePagamento;
import br.com.nsym.domain.model.entity.financeiro.ParcelaStatus;
import br.com.nsym.domain.model.entity.financeiro.RecebimentoParcial;
import br.com.nsym.domain.model.entity.financeiro.TipoPagamento;
import br.com.nsym.domain.model.entity.financeiro.Enum.TipoLancamento;
import br.com.nsym.domain.model.entity.fiscal.Cfe.CFe;
import br.com.nsym.domain.model.entity.fiscal.Cfe.Nfce;
import br.com.nsym.domain.model.entity.fiscal.nfe.Nfe;
import br.com.nsym.domain.model.entity.fiscal.nfe.NfeRecebida;
import br.com.nsym.domain.model.entity.venda.Pedido;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
public abstract class Parcelas extends PersistentEntity{

	/**
	 *
	 */
	private static final long serialVersionUID = 6318694647406862491L;
	
	@Getter
	transient DateTimeFormatter formatadorData = DateTimeFormatter
	.ofLocalizedDate(FormatStyle.SHORT)
	.withLocale(new Locale("pt", "br"));
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="FormaPag_ID",referencedColumnName = "id")
	private FormaDePagamento formaPag;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="id_NFE",referencedColumnName = "id")
	private Nfe nfe;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="NfeRec_ID",referencedColumnName = "id")
	private NfeRecebida nfeRecebida;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="id_CFe",referencedColumnName = "id")
	private CFe cfe;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="id_Nfce",referencedColumnName = "id")
	private Nfce nfce;
	
	@Getter
	@Setter
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name="CaixaGT_ID",referencedColumnName = "id")
	private AgPedido agPedido;
	
	@Getter
	@Setter
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name="RecebimentoParcial_id",referencedColumnName = "id")
	private RecebimentoParcial recebimentoParcial;
	
	// deve ser preenchido com o numero da nfe
	@Getter
	@Setter
	private Long controle;

	// informar o numero da parcela neste campo
	@Getter
	@Setter
	private Long numParcela = 1L;
	
	@Getter
	@Setter
	private LocalDate vencimento = LocalDate.now();
	
	@Getter
	@Setter
	private LocalDate dataRecebimento ;
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 2)
	private BigDecimal valorParcela = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 2)
	private BigDecimal valorOriginal = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 2)
	private BigDecimal valorRecebido = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 2)
	private BigDecimal valorCobrado = new BigDecimal("0");
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private ParcelaStatus status ;
	
	@Getter
	@Setter
	private boolean financeiro = false;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoLancamento tipoLancamento= TipoLancamento.tpCredito;
	
	@Getter
	@Setter
	@ManyToOne()
	private ContaCorrente conta;
	
	@Getter
	@Setter
	private TipoPagamento tipoPagamento;
	
	@Getter
	@Setter
	private String descricao;
	
	@Getter
	@Setter
	private boolean pessoal = false;
	
	@Getter
	@Setter
	private boolean origemAgenda = false;
	
	@Getter
	@Setter
	private boolean recorrente = false;
	
	@Getter
	@Setter
	private int qRecorrencia = 0;
	
	@Getter
	@Setter
	private BigDecimal acrescimo = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal desconto = new BigDecimal("0");
	
	
	 /**
     * @return a data de recebimento em padrão BR
     */
    public String getDataRecebimentoBR() {
    	if (this.dataRecebimento != null) {
        return this.dataRecebimento.format(formatadorData);
    	}else {
    		return "Aguardando...";
    	}
    }
    
    /**
     * @return a data de vencimento em padrão BR
     */
    public String getVencimentoFormatoBR() {
        return this.vencimento.format(formatadorData);
    }
    
    /**Método que identifica a origem da parcela
     * 
     * @return String informando a origem que gerou a parcela
     */
    public String getOrigemParcela() {
    	String resposta = "Não identificado";
    	if (this.descricao == null || this.descricao == "" ) {
    		if (this.nfe != null && this.agPedido == null ) {
    			resposta = "NFE: "+ this.nfe.getNumeroNota()+ " - " + this.nfe.getNome();
    		}else {
    			if (this.agPedido != null) {
    				if (this.agPedido.getListaPedidosRecebidos().size() >= 1) {
    					resposta = this.agPedido.getDestinatario().nome() +" - Valor do PedidoGT R$ " + this.agPedido.getValorTotal().setScale(2,RoundingMode.HALF_DOWN) ;
    					int i = 1;
    					for (Pedido ped : this.agPedido.getListaPedidosRecebidos()) {
    						resposta = resposta  + " - Pedido("+i+"):" + ped.getControle().getId();
    						i++;
    					}
    				}
    			}else {
    				resposta = this.descricao;
    			}
    		}
    	}else {
    		resposta = this.descricao;
    	}
    	return resposta;
    	
    }
    
    public Long idCadastro() {
    	Long resposta = 0l;
    	if (this.nfe != null && this.agPedido == null ) {
    		resposta = this.nfe.getDestino().idRegistro();
    	}else {
    		if (this.agPedido != null) {
    			resposta = this.agPedido.getDestinatario().idRegistro();
    		}
    	}
    	return resposta;
    }
    
    /**
     * 
     * @return quantidade de dias em atraso
     */
    public long getDiasDeAtraso() {
    	long dias = 0L ;
    	if (this.status != null) {
    		if (this.status.equals(ParcelaStatus.ABE)) {
    			dias = ChronoUnit.DAYS.between(LocalDate.now(),this.vencimento);
    		}else {
    			if (this.status.equals(ParcelaStatus.REC)){
    				if (this.dataRecebimento != null) {
    					dias = ChronoUnit.DAYS.between(this.dataRecebimento,this.vencimento);
    				}else {
    					dias = 0L;
    				}
    			}
    		}
    	}
    	return dias;
    }
    
    public BigDecimal saldoEmAberto() {
		BigDecimal resultado = new BigDecimal("0");
		if (valorRecebido.compareTo(new BigDecimal("0"))>0) {
			if (valorCobrado != null) {
				resultado = valorCobrado.subtract(valorRecebido);
			}else {
				resultado = valorRecebido.subtract(valorParcela);
			}
		}else {
			resultado = valorParcela;
		}
		return resultado;
	}
    
    public BigDecimal exibeValorCobrado() {
    	BigDecimal resultado = new BigDecimal("0");
    	if (valorOriginal != null) {
    		if (valorOriginal.compareTo(new BigDecimal("0"))==0) {
    			resultado = valorParcela;
    		}else {
    			resultado = valorOriginal;
    		}
    	}else {
    		resultado = valorParcela;
    	}
    	return resultado;
    }
	
	@Override
	public String toString() {
		return "ParcelasNfe [formaPag=" + this.formaPag + ", nfe=" + nfe + ", cfe=" + cfe + ", agPedido=" + agPedido
				+ ", recebimentoParcial=" + recebimentoParcial + ", controle=" + controle + ", numParcela=" + numParcela
				+ ", vencimento=" + vencimento + ", valorParcela=" + valorParcela + ", valorRecebido=" + valorRecebido
				+ ", status=" + status + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(agPedido, cfe, controle, dataRecebimento, financeiro, formaPag, nfe,
				numParcela, recebimentoParcial, status, tipoLancamento, valorParcela, valorRecebido, vencimento);
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
		Parcelas other = (Parcelas) obj;
		return Objects.equals(agPedido, other.agPedido) && Objects.equals(cfe, other.cfe)
				&& Objects.equals(controle, other.controle) && Objects.equals(dataRecebimento, other.dataRecebimento)
				&& financeiro == other.financeiro && Objects.equals(formaPag, other.formaPag)
				&& Objects.equals(nfe, other.nfe) && Objects.equals(numParcela, other.numParcela)
				&& Objects.equals(recebimentoParcial, other.recebimentoParcial) && status == other.status
				&& tipoLancamento == other.tipoLancamento && Objects.equals(valorParcela, other.valorParcela)
				&& Objects.equals(valorRecebido, other.valorRecebido) && Objects.equals(vencimento, other.vencimento);
	}

}
