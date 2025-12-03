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

public class AgendaDTOPdf  {
	


	
	public AgendaDTOPdf() {
		super();
		//TODO Auto-generated constructor stub
	}
	
	


	public AgendaDTOPdf(BigInteger idParc, BigInteger numParcela, BigDecimal valorParcela,
			String vencimento, String status,  String dataRecebimento, String descricao,
			String tipoLancamento, String tipoPagamento,
			BigDecimal valorOriginal, BigInteger qParcelas, BigInteger numeroNfe,long diasDeAtraso) {
		super();
		this.idParc = idParc;
		this.numParcela = numParcela;
		this.valorParcela = valorParcela;
		this.vencimento = vencimento;
		this.status = status;
		this.dataRecebimento = dataRecebimento;
		this.descricao = descricao;
		this.tipoLancamento = tipoLancamento;
		this.tipoPagamento = tipoPagamento;
		this.valorOriginal = valorOriginal;
		this.qParcelas = qParcelas;
		this.numeroNfe = numeroNfe;
		this.diasDeAtraso = diasDeAtraso;
	}

	@Setter
	private Long id;
		
	@Getter
	@Setter
	private BigInteger idParc = new BigInteger("0");
	@Getter
	@Setter
	private BigInteger numParcela= new BigInteger("0");
	@Getter
	@Setter
	private BigDecimal valorParcela= new BigDecimal("0");
	
	@Setter
	@Getter
	private String vencimento;
	@Getter
	@Setter
	private String status;
	
	@Setter
	@Getter
	private String dataRecebimento;
	@Getter
	@Setter
	private String descricao;
	@Getter
	@Setter
	private String tipoLancamento;
	
	@Getter
	@Setter
	private String tipoPagamento  ;
	
	@Getter
	@Setter
	private BigDecimal valorOriginal= new BigDecimal("0");
	
	@Getter
	@Setter
	private BigInteger qParcelas = new BigInteger("0");
	
	@Getter
	@Setter
	private BigInteger numeroNfe = new BigInteger("0");
	
	@Setter
	@Getter
	private long diasDeAtraso = 0l;
	

}
