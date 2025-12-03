package br.com.nsym.domain.model.entity.financeiro.tools;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import br.com.nsym.domain.model.entity.financeiro.ParcelaStatus;
import br.com.nsym.domain.model.entity.financeiro.TipoPagamento;
import lombok.Getter;
import lombok.Setter;

public class AgendaDTO  {
	


	public AgendaDTO(BigInteger idParc,BigInteger controle,BigInteger numParcela,BigDecimal valorParcela,Date vencimento,String status,BigDecimal valorRecebido, 
			Date dataRecebimento,String tipoLancamento,int tipoPagamento,int qRecorrencia,BigDecimal valorCobrado,BigDecimal valorOriginal,
			BigInteger qParcelas,String razao,BigInteger nfe,BigInteger ag,BigInteger nfRec, BigInteger numeroNfe,BigInteger numeroNFeRec) {
		super();
		this.idParc = idParc;
		this.controle = controle;
		this.numParcela = numParcela;
		this.valorParcela = valorParcela;
		this.vencimento = vencimento;
		this.status = status;
		this.valorRecebido = valorRecebido;
		this.dataRecebimento = dataRecebimento;
		this.tipoLancamento = tipoLancamento;
		this.tipoPagamento = tipoPagamento;
		this.qRecorrencia = qRecorrencia;
		this.valorCobrado = valorCobrado;
		this.valorOriginal = valorOriginal;
		this.qParcelas = qParcelas;
		this.descricao = razao;
		this.nfe = nfe;
		this.ag = ag;
		this.nfRec = nfRec;
		this.numeroNfe = numeroNfe;
		this.numeroNFeRec = numeroNFeRec;
	}
	
	
	public AgendaDTO() {
		super();
		//TODO Auto-generated constructor stub
	}
	
	


	public AgendaDTO(BigInteger idParc, BigInteger controle, BigInteger numParcela, BigDecimal valorParcela,
			Date vencimento, String status, BigDecimal valorRecebido, Date dataRecebimento, String descricao,
			String tipoLancamento, int tipoPagamento, int qRecorrencia, BigDecimal valorCobrado,
			BigDecimal valorOriginal, BigInteger qParcelas, BigInteger nfe, BigInteger ag, BigInteger nfRec,
			BigInteger numeroNfe, BigInteger numeroNFeRec, TotalizadorFinanceiro totalizador, TipoPagamento tipo) {
		super();
		this.idParc = idParc;
		this.controle = controle;
		this.numParcela = numParcela;
		this.valorParcela = valorParcela;
		this.vencimento = vencimento;
		this.status = status;
		this.valorRecebido = valorRecebido;
		this.dataRecebimento = dataRecebimento;
		this.descricao = descricao;
		this.tipoLancamento = tipoLancamento;
		this.tipoPagamento = tipoPagamento;
		this.qRecorrencia = qRecorrencia;
		this.valorCobrado = valorCobrado;
		this.valorOriginal = valorOriginal;
		this.qParcelas = qParcelas;
		this.nfe = nfe;
		this.ag = ag;
		this.nfRec = nfRec;
		this.numeroNfe = numeroNfe;
		this.numeroNFeRec = numeroNFeRec;
		this.totalizador = totalizador;
		this.tipo = tipo;
	}

	@Setter
	private Long id;
		
	@Getter
	@Setter
	private BigInteger idParc = new BigInteger("0");
	@Getter
	@Setter
	private BigInteger controle= new BigInteger("0");
	@Getter
	@Setter
	private BigInteger numParcela= new BigInteger("0");
	@Getter
	@Setter
	private BigDecimal valorParcela= new BigDecimal("0");
	
	@Setter
	private Date vencimento;
	@Getter
	@Setter
	private String status;
	@Getter
	@Setter
	private BigDecimal valorRecebido= new BigDecimal("0");
	
	@Setter
	private Date dataRecebimento;
	@Getter
	@Setter
	private String descricao;
	@Getter
	@Setter
	private String tipoLancamento;
	
	@Setter
	private int tipoPagamento = 0 ;
	@Getter
	@Setter
	private int qRecorrencia = 0 ;
	@Getter
	@Setter
	private BigDecimal valorCobrado= new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal valorOriginal= new BigDecimal("0");
	
	@Getter
	@Setter
	private BigInteger qParcelas = new BigInteger("0");
	
	@Getter
	@Setter
	private BigInteger nfe= new BigInteger("0");
	
	@Getter
	@Setter
	private BigInteger ag= new BigInteger("0");
	
	@Getter
	@Setter
	private BigInteger nfRec = new BigInteger("0");
	
	@Getter
	@Setter
	private BigInteger numeroNfe = new BigInteger("0");
	
	@Getter
	@Setter
	private BigInteger numeroNFeRec = new BigInteger("0");
	
	@Getter
	@Setter
	private TotalizadorFinanceiro totalizador;
	
	@Getter
	private TipoPagamento tipo;
	
	@Getter
	transient DateTimeFormatter formatadorData = DateTimeFormatter
	.ofLocalizedDate(FormatStyle.SHORT)
	.withLocale(new Locale("pt", "br"));
	
	
	public String getVencimento() {
		if( this.vencimento == null) {
			return "";
		}else {
        return this.vencimento.toLocalDate().format(formatadorData);
		}
    }
	
	public String getDataRecebimento() {
		if (this.dataRecebimento == null) {
			return "";
		}else {
        return this.dataRecebimento.toLocalDate().format(formatadorData);
		}
    }
	
	public String getTipoPagamento() {
			return TipoPagamento.pegaPorIndice(this.tipoPagamento);
	}
	
//	public String getOrigemParcela() {
//    	String resposta = "Não identificado";
//    	if (this.descricao == null || this.descricao == "" ) {
//    		if (this.nfe != null && this.agPedido == null ) {
//    			resposta = "NFE: "+ this.nfe.getNumeroNota()+ " - " + this.nfe.getNome();
//    		}else {
//    			if (this.agPedido != null) {
//    				if (this.agPedido.getListaPedidosRecebidos().size() >= 1) {
//    					resposta = this.agPedido.getDestinatario().nome() +" - Valor do PedidoGT R$ " + this.agPedido.getValorTotal().setScale(2,RoundingMode.HALF_DOWN) ;
//    					int i = 1;
//    					for (Pedido ped : this.agPedido.getListaPedidosRecebidos()) {
//    						resposta = resposta  + " - Pedido("+i+"):" + ped.getControle().getId();
//    						i++;
//    					}
//    				}
//    			}else {
//    				resposta = this.descricao;
//    			}
//    		}
//    	}else {
//    		resposta = this.descricao;
//    	}
//    	return resposta;
//    	
//    }
	
	 public BigDecimal exibeValorCobrado() {
	    	BigDecimal resultado = new BigDecimal("0");
	    	if (this.valorOriginal != null) {
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
	 
	 public LocalDate dateToLocalDate(Date data) {
		 return data.toInstant().atZone(ZoneId.systemDefault())
         .toLocalDate();
	 }
	 
	 /**
	     * 
	     * @return quantidade de dias em atraso
	     */
	    public long getDiasDeAtraso() {
	    	long dias = 0L ;
	    	if (this.status != null) {
	    		if (ParcelaStatus.valueOf(this.status).equals(ParcelaStatus.ABE)) {
	    			dias = ChronoUnit.DAYS.between(LocalDate.now(),this.vencimento.toLocalDate());
	    		}else {
	    			if (ParcelaStatus.valueOf(this.status).equals(ParcelaStatus.REC)) {
	    				if (this.dataRecebimento != null) {
	    					dias = ChronoUnit.DAYS.between(this.dataRecebimento.toLocalDate(),this.vencimento.toLocalDate());
	    				}else {
	    					dias = 0L;
	    				}
	    			}
	    		}
	    	}
	    	return dias;
	    }


//	@Override
//	public Long getId() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//	@Override
//	public boolean isSaved() {
//		// TODO Auto-generated method stub
//		return false;
//	}



}
