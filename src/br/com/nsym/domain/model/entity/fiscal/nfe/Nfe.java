package br.com.nsym.domain.model.entity.fiscal.nfe;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.Transportadora;
import br.com.nsym.domain.model.entity.financeiro.FormaDePagamento;
import br.com.nsym.domain.model.entity.financeiro.tools.ParcelasNfe;
import br.com.nsym.domain.model.entity.fiscal.CFOP;
import br.com.nsym.domain.model.entity.fiscal.Tributos;
import br.com.nsym.domain.model.entity.fiscal.tools.FinalidadeNfe;
import br.com.nsym.domain.model.entity.fiscal.tools.StatusNfe;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoPesquisa;
import br.com.nsym.domain.model.entity.tools.TipoFrete;
import br.com.nsym.domain.model.entity.tools.Uf;
import br.com.nsym.domain.model.entity.venda.Pedido;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="nfe",uniqueConstraints = {@UniqueConstraint(columnNames={"numeroNota","id_empresa","id_filial"})})
@SqlResultSetMapping(
		name= "RelNatOperaMapping",
		classes = {
			@ConstructorResult(targetClass = br.com.nsym.domain.model.entity.fiscal.dto.RelNatOperacaoDTO.class,
				columns = {@ColumnResult(name = "numeroNFE",type = BigInteger.class),
						@ColumnResult(name = "razaoSocial",type = String.class),
						@ColumnResult(name = "dataSaida",type = Date.class),
						@ColumnResult(name = "natOpera",type = String.class),
						@ColumnResult(name = "totalNota",type = BigDecimal.class),
						@ColumnResult(name = "status",type = String.class),
						@ColumnResult(name = "matriz",type = String.class),
						@ColumnResult(name = "filial",type = String.class),
						})	
			}
		)
public class Nfe extends PersistentEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = 1468993961867331146L;

	@Getter
	@Setter
	@OneToOne(cascade=CascadeType.ALL)
	private Emitente emitente;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="nfe",cascade=CascadeType.REMOVE)
	private List<NfeReferenciada> listaChavesReferenciada;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="NatOperacao_ID")
	private Tributos natOperacao;
	
	@Getter
	@Setter
	@OneToOne(cascade=CascadeType.ALL)
	private Destinatario destino;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="nfe")
	private List<ItemNfe> listaItemNfe = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToOne(cascade=CascadeType.ALL)
	private Transportador transportador;
	
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="Pagamento_ID")
	private FormaDePagamento formaPagamento;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="nfe",cascade=CascadeType.REMOVE)
	private List<ParcelasNfe> listaParcelas = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="nfe",cascade=CascadeType.REMOVE)
	private List<Lacre> listaLacres = new ArrayList<>();
	
	@Getter
	@Setter
	@ManyToOne(cascade=CascadeType.REMOVE)
	@JoinColumn(name="inutilizada_ID")
	private Inutilizacao inutilizada;
	
	@Getter
	@Setter
	@OneToOne(cascade=CascadeType.ALL)
	private DI di;
	
	
	@Getter
	@Setter
	@Column(name="numeroNota")
	private Long numeroNota;
	
	
	@Getter
	@Setter
	private String nome;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private Uf ufDestino;
	
	@Getter
	@Setter
	private String chaveAcesso;
	
	
	@Getter
	@Setter
	private String caminhoXml;
	
	@Getter
	@Setter
	private String protocoloAutorizacao;
	
	@Getter
	@Setter
	@Lob
	private String respostaAcbr;
	
	@Getter
	@Setter
	@Lob 
	private String respostaFinalAcbr;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoPesquisa tipoPesquisa;
	
	@Getter
	@Setter
	private LocalDateTime dataEmissao = LocalDateTime.now();
	
	@Getter
	@Setter
	private LocalDateTime dataSaida = LocalDateTime.now();
	
	@Getter
	@Setter
	private LocalTime horaSaida;
	
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
	private BigDecimal valorFrete = new BigDecimal("0.0");
	
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
	private BigDecimal valorTotalII = new BigDecimal("0");
	
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
	@Lob
	private String dadosAdicionais;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private StatusNfe statusEmissao;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private FinalidadeNfe finalidadeEmissao;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoFrete tipoFrete;
	
	@Getter
	@Setter
	private Transportadora transportadora;
	
	@Getter
	@Setter
	private String placa;
	
	@Getter
	@Setter
	private String indFinal;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private Uf veiculoUf;
	
	@Getter
	@Setter
	private boolean tipoOperacao = true;
	
	@Getter
	@Setter
	private boolean importacao = false;
	
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
	private String mensagemFisco;
	
	@Getter
	@Setter
	private String mensagemEmitente;
	
	@Getter
	@Setter
	private String caminhoCancelado;
	
	@Getter
	@Setter
	private String protCancelado;
	
	@Getter
	@Setter
	private String motivoCancelado;
	
	@Getter
	@Setter
	private boolean clienteRetira = false;
	
	
	@Getter
	@Setter
	private boolean notaDeFornecedor;
	
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="Pedido_ID")
	private Pedido pedido;
	
	@Getter
	@Setter
	private boolean origemPedido = false;
	
	@Getter
	@Setter
	private boolean financeiroGerado = false;
	
	/**
	 * novos campos criados para contemplar a reforma tributária de 20225 
	 */
	
	@Getter
	@Setter
	@Column(name = "tot_v_cbs", precision = 15, scale = 2)
	private BigDecimal totVCbs;
	
	@Getter
	@Setter
	@Column(name = "tot_v_ibs", precision = 15, scale = 2)
	private BigDecimal totVIbs;

	@Getter
	@Setter
	@Column(name = "tot_v_is", precision = 15, scale = 2)
	private BigDecimal totVIs;

	public void addItemNfe(ItemNfe item) {
		item.setNfe(this);
		listaItemNfe.add(item);
	}



	@Override
	public String toString() {
		return String.format(
				"Nfe [emitente=%s, listaChavesReferenciada=%s, natOperacao=%s, destino=%s, listaItemNfe=%s, transportador=%s, formaPagamento=%s, listaParcelas=%s, listaLacres=%s, inutilizada=%s, di=%s, numeroNota=%s, nome=%s, ufDestino=%s, chaveAcesso=%s, caminhoXml=%s, protocoloAutorizacao=%s, respostaAcbr=%s, respostaFinalAcbr=%s, tipoPesquisa=%s, dataEmissao=%s, dataSaida=%s, horaSaida=%s, BaseIcms=%s, ValorIcms=%s, baseIcmsSubstituicao=%s, valorIcmsSubstituicao=%s, valorTotalProdutos=%s, valorFrete=%s, valorSeguro=%s, desconto=%s, valorTotalPis=%s, valorTotalCofins=%s, outrasDespesas=%s, valorTotalIpi=%s, valorTotalNota=%s, valorTotalTributos=%s, vFCP=%s, vFCPST=%s, vFCPSTRet=%s, vIPIDevol=%s, dadosAdicionais=%s, statusEmissao=%s, finalidadeEmissao=%s, tipoFrete=%s, transportadora=%s, placa=%s, indFinal=%s, veiculoUf=%s, tipoOperacao=%s, importacao=%s, valorIcmsDesonerado=%s, vFCPUFDest=%s, vICMSUFDest=%s, vICMSUFRemet=%s, mensagemFisco=%s, mensagemEmitente=%s, caminhoCancelado=%s, protCancelado=%s, motivoCancelado=%s, clienteRetira=%s, notaDeFornecedor=%s]",
				emitente, listaChavesReferenciada, natOperacao, destino, listaItemNfe, transportador, formaPagamento,
				listaParcelas, listaLacres, inutilizada, di, numeroNota, nome, ufDestino, chaveAcesso, caminhoXml,
				protocoloAutorizacao, respostaAcbr, respostaFinalAcbr, tipoPesquisa, dataEmissao, dataSaida, horaSaida,
				BaseIcms, ValorIcms, baseIcmsSubstituicao, valorIcmsSubstituicao, valorTotalProdutos, valorFrete,
				valorSeguro, desconto, valorTotalPis, valorTotalCofins, outrasDespesas, valorTotalIpi, valorTotalNota,
				valorTotalTributos, vFCP, vFCPST, vFCPSTRet, vIPIDevol, dadosAdicionais, statusEmissao,
				finalidadeEmissao, tipoFrete, transportadora, placa, indFinal, veiculoUf, tipoOperacao, importacao,
				valorIcmsDesonerado, vFCPUFDest, vICMSUFDest, vICMSUFRemet, mensagemFisco, mensagemEmitente,
				caminhoCancelado, protCancelado, motivoCancelado, clienteRetira, notaDeFornecedor);
	}

	
}
