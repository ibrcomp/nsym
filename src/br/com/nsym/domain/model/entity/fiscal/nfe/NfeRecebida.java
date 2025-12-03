package br.com.nsym.domain.model.entity.fiscal.nfe;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.Transportadora;
import br.com.nsym.domain.model.entity.financeiro.FormaDePagamento;
import br.com.nsym.domain.model.entity.financeiro.tools.ParcelasNfe;
import br.com.nsym.domain.model.entity.fiscal.Tributos;
import br.com.nsym.domain.model.entity.fiscal.tools.FinalidadeNfe;
import br.com.nsym.domain.model.entity.fiscal.tools.StatusNfe;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoPesquisa;
import br.com.nsym.domain.model.entity.tools.TipoFrete;
import br.com.nsym.domain.model.entity.tools.Uf;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="nfeRecebida",uniqueConstraints = {@UniqueConstraint(columnNames={"numeroNota","id_empresa","id_filial"})})
public class NfeRecebida extends PersistentEntity{

	/**
	 *
	 */
	private static final long serialVersionUID = -5114943696428615507L;

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
	@OneToMany(mappedBy="nfeRecebida")
	private List<ItemNfeRecebida> listaItemNfe = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToOne(cascade=CascadeType.ALL)
	private Transportador transportador;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Pagamento_ID")
	private FormaDePagamento formaPagamento;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="nfeRecebida",cascade=CascadeType.REMOVE)
	private List<ParcelasNfe> listaParcelas = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="nfeRecebida",cascade=CascadeType.REMOVE)
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
	private boolean estoqueAtualizado;
	
	@Getter
	@Setter
	@OneToOne(mappedBy="nfeRecebida")
	private NfeNaoConfirmada nfeNaoConfirmada;
	
	@Getter
	@Setter
	private boolean financeiroGerado = false;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(BaseIcms, ValorIcms, baseIcmsSubstituicao, caminhoCancelado, caminhoXml,
				chaveAcesso, clienteRetira, dadosAdicionais, dataEmissao, dataSaida, desconto, destino, di, emitente,
				estoqueAtualizado, finalidadeEmissao, formaPagamento, horaSaida, importacao, indFinal, inutilizada,
				listaChavesReferenciada, listaItemNfe, listaLacres, listaParcelas, mensagemEmitente, mensagemFisco,
				motivoCancelado, natOperacao, nfeNaoConfirmada, nome, notaDeFornecedor, numeroNota, outrasDespesas,
				placa, protCancelado, protocoloAutorizacao, respostaAcbr, respostaFinalAcbr, statusEmissao, tipoFrete,
				tipoOperacao, tipoPesquisa, transportador, transportadora, ufDestino, vFCP, vFCPST, vFCPSTRet,
				vFCPUFDest, vICMSUFDest, vICMSUFRemet, vIPIDevol, valorFrete, valorIcmsDesonerado,
				valorIcmsSubstituicao, valorSeguro, valorTotalCofins, valorTotalIpi, valorTotalNota, valorTotalPis,
				valorTotalProdutos, valorTotalTributos, veiculoUf);
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
		NfeRecebida other = (NfeRecebida) obj;
		return Objects.equals(BaseIcms, other.BaseIcms) && Objects.equals(ValorIcms, other.ValorIcms)
				&& Objects.equals(baseIcmsSubstituicao, other.baseIcmsSubstituicao)
				&& Objects.equals(caminhoCancelado, other.caminhoCancelado)
				&& Objects.equals(caminhoXml, other.caminhoXml) && Objects.equals(chaveAcesso, other.chaveAcesso)
				&& clienteRetira == other.clienteRetira && Objects.equals(dadosAdicionais, other.dadosAdicionais)
				&& Objects.equals(dataEmissao, other.dataEmissao) && Objects.equals(dataSaida, other.dataSaida)
				&& Objects.equals(desconto, other.desconto) && Objects.equals(destino, other.destino)
				&& Objects.equals(di, other.di) && Objects.equals(emitente, other.emitente)
				&& estoqueAtualizado == other.estoqueAtualizado && finalidadeEmissao == other.finalidadeEmissao
				&& Objects.equals(formaPagamento, other.formaPagamento) && Objects.equals(horaSaida, other.horaSaida)
				&& importacao == other.importacao && Objects.equals(indFinal, other.indFinal)
				&& Objects.equals(inutilizada, other.inutilizada)
				&& Objects.equals(listaChavesReferenciada, other.listaChavesReferenciada)
				&& Objects.equals(listaItemNfe, other.listaItemNfe) && Objects.equals(listaLacres, other.listaLacres)
				&& Objects.equals(listaParcelas, other.listaParcelas)
				&& Objects.equals(mensagemEmitente, other.mensagemEmitente)
				&& Objects.equals(mensagemFisco, other.mensagemFisco)
				&& Objects.equals(motivoCancelado, other.motivoCancelado)
				&& Objects.equals(natOperacao, other.natOperacao)
				&& Objects.equals(nfeNaoConfirmada, other.nfeNaoConfirmada) && Objects.equals(nome, other.nome)
				&& notaDeFornecedor == other.notaDeFornecedor && Objects.equals(numeroNota, other.numeroNota)
				&& Objects.equals(outrasDespesas, other.outrasDespesas) && Objects.equals(placa, other.placa)
				&& Objects.equals(protCancelado, other.protCancelado)
				&& Objects.equals(protocoloAutorizacao, other.protocoloAutorizacao)
				&& Objects.equals(respostaAcbr, other.respostaAcbr)
				&& Objects.equals(respostaFinalAcbr, other.respostaFinalAcbr) && statusEmissao == other.statusEmissao
				&& tipoFrete == other.tipoFrete && tipoOperacao == other.tipoOperacao
				&& tipoPesquisa == other.tipoPesquisa && Objects.equals(transportador, other.transportador)
				&& Objects.equals(transportadora, other.transportadora) && ufDestino == other.ufDestino
				&& Objects.equals(vFCP, other.vFCP) && Objects.equals(vFCPST, other.vFCPST)
				&& Objects.equals(vFCPSTRet, other.vFCPSTRet) && Objects.equals(vFCPUFDest, other.vFCPUFDest)
				&& Objects.equals(vICMSUFDest, other.vICMSUFDest) && Objects.equals(vICMSUFRemet, other.vICMSUFRemet)
				&& Objects.equals(vIPIDevol, other.vIPIDevol) && Objects.equals(valorFrete, other.valorFrete)
				&& Objects.equals(valorIcmsDesonerado, other.valorIcmsDesonerado)
				&& Objects.equals(valorIcmsSubstituicao, other.valorIcmsSubstituicao)
				&& Objects.equals(valorSeguro, other.valorSeguro)
				&& Objects.equals(valorTotalCofins, other.valorTotalCofins)
				&& Objects.equals(valorTotalIpi, other.valorTotalIpi)
				&& Objects.equals(valorTotalNota, other.valorTotalNota)
				&& Objects.equals(valorTotalPis, other.valorTotalPis)
				&& Objects.equals(valorTotalProdutos, other.valorTotalProdutos)
				&& Objects.equals(valorTotalTributos, other.valorTotalTributos) && veiculoUf == other.veiculoUf;
	}
	
	
}
