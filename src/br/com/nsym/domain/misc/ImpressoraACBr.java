package br.com.nsym.domain.misc;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.com.nsym.application.channels.AcbrComunica;
import br.com.nsym.application.channels.AcbrComunica.Destino;
import br.com.nsym.application.channels.AcbrComunica.Emissor;
import br.com.nsym.application.channels.DadosDeConexaoSocket;
import br.com.nsym.domain.model.entity.cadastro.BarrasEstoque;
import br.com.nsym.domain.model.entity.financeiro.Caixa;
import br.com.nsym.domain.model.entity.financeiro.RecebimentoParcial;
import br.com.nsym.domain.model.entity.tools.Configuration;
import br.com.nsym.domain.model.entity.tools.PedidoTipo;
import br.com.nsym.domain.model.entity.tools.TipoMedida;
import br.com.nsym.domain.model.entity.venda.ItemPedido;
import br.com.nsym.domain.model.entity.venda.Pedido;
import br.com.nsym.domain.model.repository.cadastro.BarrasEstoqueRepository;
import lombok.Getter;
import lombok.Setter;

@RequestScoped
public class ImpressoraACBr {

	@Inject
	private AcbrComunica acbr;

	@Inject
	private LocalizaRegex localiza;
	
	@Getter
	@Setter
	private Emissor emissor = new Emissor();
	
	@Getter
	@Setter
	private Destino destino = new Destino();
	
	@Inject
	private BarrasEstoqueRepository estoqueDao;
	
	@Getter
	@Setter
	private BarrasEstoque estoque = new BarrasEstoque();
	
	@Getter
	private DateTimeFormatter formatador = DateTimeFormatter
	.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT)
	.withLocale(new Locale("pt", "br"));
	
	@Getter
	private DateTimeFormatter formatadorData = DateTimeFormatter
	.ofLocalizedDate(FormatStyle.SHORT)
	.withLocale(new Locale("pt", "br"));
	
	
	@Getter
	private DateTimeFormatter formatadorTime = DateTimeFormatter
	.ofLocalizedTime(FormatStyle.SHORT)
	.withLocale(new Locale("pt", "br"));
	
	
	

	/**
	 * M�todo que lista todas as impressoras dispon�veis (Spooler de impress�o)
	 * @param conexao
	 * @return lista de impressoras
	 * @throws IOException 
	 */
	public List<String> listaImpressorasSpooler(DadosDeConexaoSocket conexao) throws IOException{

		String lista = acbr.enviaComandoACBr(conexao, "ESCPOS.AcharPortasRAW");
		List<String> resultado = new ArrayList<>();

		boolean achei = localiza.localizaPalavra(lista,"RAW" );
		if (achei){
			int ini = lista.indexOf("RAW");

			String 	listaLimpa = lista.substring(ini);

			resultado = localiza.listaString(listaLimpa, "\\|");
		}
		return resultado;
	}
	
	/**
	 * M�todo que lista todas as portas USBs disponiveis para impress�o
	 * @param conexao
	 * @return lista de portas USB
	 * @throws IOException 
	 */
	
	public List<String> listaPortasUsb(DadosDeConexaoSocket conexao) throws IOException{
		
		String lista = acbr.enviaComandoACBr(conexao, "ESCPOS.AcharPortasUSB");
		
		List<String> resultado = new ArrayList<>();
		
		boolean achei = localiza.localizaPalavra(lista,"USB" );
		if (achei){
			int ini = lista.indexOf("USB");

			String 	listaLimpa = lista.substring(ini);

			resultado = localiza.listaString(listaLimpa, "\\|");
		}
		return resultado; 
	}
	/**
	 * Define a porta de impressao para impressora
	 * @param conexao
	 * @param porta
	 * @return
	 * @throws IOException 
	 */
	public String  setaPorta(DadosDeConexaoSocket conexao, String porta) throws IOException {
		return acbr.enviaComandoACBr(conexao, "ESCPOS.setporta(\""	+ porta	+ "\")");
	}
	
	/**
	 * Define o modelo de impressora 
	 * @param conexao
	 * @param modelo
	 * @return
	 * @throws IOException 
	 */
	public String setaModeloImpressora(DadosDeConexaoSocket conexao, ModeloImpressoraAcbr modelo) throws IOException {
		return acbr.enviaComandoACBr(conexao, "ESCPOS.SetModelo(\""	+ modelo.getModelo()	+ "\")");
	}
	
	/**
	 * Ativa Impressora 
	 * @param conexao
	 * @return
	 * @throws IOException 
	 */
	public String ativaImpressora(DadosDeConexaoSocket conexao) throws IOException {
		return acbr.enviaComandoACBr(conexao, "ESCPOS.Ativar()");
	}
	
	/**
	 * Desativa Impressora
	 * @param conexao
	 * @return
	 * @throws IOException 
	 */
	public String desativaImpressora(DadosDeConexaoSocket conexao) throws IOException {
		return acbr.enviaComandoACBr(conexao, "ESCPOS.Desativar()");
	}
	
	/**
	 * Retorna True para impessora ativa ou FALSE caso desativada
	 * @param conexao
	 * @return
	 * @throws IOException 
	 */
	public boolean impressoraAtivada(DadosDeConexaoSocket conexao) throws IOException {
		String resultado =  acbr.enviaComandoACBr(conexao, "ESCPOS.Ativo()").toUpperCase();
		return localiza.localizaPalavra(resultado,"OK: TRUE" );
		
	}
	
	public String imprimir(DadosDeConexaoSocket conexao, String  itens) throws IOException {
			acbr.enviaComandoACBr(conexao, "ESCPOS.Imprimir(\"</zera></linha_dupla>TEXTO NORMAL</lf></ae>ALINHADO A ESQUERDA</lf>1 2 3 "
					+ "TESTANDO</lf><n>FONTE NEGRITO</N></lf><e>FONTE EXPANDIDA</e></lf><a>FONTE"
					+ "ALT.DUPLA</a></lf><c>FONTE CONDENSADA</e></lf><in>FONTE INVERTIDA</in></lf><S>FONTE"
					+ "SUBLINHADA</s></lf><i>FONTE ITALICO</i></lf></fn></ce>ALINHADO NO CENTRO</lf>1 2 3"
					+ "TESTANDO</lf><n>FONTE NEGRITO</N></lf><e>FONTE EXPANDIDA</e></lf><a>FONTE"
					+ "ALT.DUPLA</a></lf><c>FONTE CONDENSADA</e></lf><in>FONTE INVERTIDA</in></lf><S>FONTE"
					+ "SUBLINHADA</s></lf><i>FONTE ITALICO</i></lf></fn></ad>ALINHADO A DIREITA</lf>1 2 3"
					+ "TESTANDO</lf><n>FONTE NEGRITO</N></lf><e>FONTE EXPANDIDA</e></lf><a>FONTE ALT.DUPLA</a></lf><c>FONTE\")");
		
		return acbr.enviaComandoACBr(conexao, "ESCPOS.Imprimir(\"</zera></linha_dupla>FONTE NORMAL: 48 Colunas</lf>....+....1....+....2....+....3...."
				+ "+....4....+....5....+....6....+....7....+....8</lf><e>EXPANDIDO: 48 Colunas</lf>....+....1....+....2....+....3...."
				+ "+....4....+....5....+....6....+....7....+....8</lf></e><c>CONDENSADO: 48 Colunas</lf>....+....1....+....2...."
				+ "+....3....+....4....+....5....+....6....+....7....+....8</lf></c><n>FONTE NEGRITO</N></lf><in>FONTE"
				+ "INVERTIDA</in></lf><S>FONTE SUBLINHADA</s></lf><i>FONTE ITALICO</i>FONTE	NORMAL</lf></linha_simples></lf>"
				+ "<n>LIGA NEGRITO</lf><i>LIGA ITALICO</lf><S>LIGASUBLINHADA</lf><c>LIGA CONDENSADA</lf>"
				+ "<e>LIGA EXPANDIDA</lf></fn>FONTE	NORMAL</lf></linha_simples></lf><e><n>NEGRITO E EXPANDIDA</n></e></lf></fn>FONTE NORMAL</lf><in><c>"
				+ "INVERTIDA E CONDENSADA</c></in></lf></fn>FONTE NORMAL</lf></linha_simples><//lf></FB>FONTE TIPO B</lf>"
				+ "<n>FONTE NEGRITO</N></lf><e>FONTE EXPANDIDA</e>\")");
//						"ESCPOS.imprimir(\""	+ modelo.getModelo()	+ "\")");
	}
	
	public String imprimirModelo(DadosDeConexaoSocket conexao, String  pedido) throws IOException {
		
		String parametros = "</zera></lf>";
		parametros = parametros + "</linha_dupla></lf>";
		parametros = parametros + "</ce>ALINHADO NO CENTRO 1 2 3 TESTANDO</lf>";
		parametros = parametros + "</ce>Teste Ibrahim linha 1</lf>";
		parametros = parametros + "</ce>Teste Ibrahim linha 2</lf>";
		parametros = parametros + "</ce>Teste Ibrahim linha 3</lf>";
		parametros = parametros + "</ce>Teste Ibrahim linha 4</lf>";
		parametros = parametros + "</ce></linha_dupla></lf>";
		parametros = parametros + "</pular_linhas></lf>";
		parametros = parametros + "</corte_total></lf>";
		parametros = parametros + "</ce></linha_dupla></lf>";
		parametros = parametros + "</ce>Teste Ibrahim linha 5</lf>";
		parametros = parametros + "</ce>Teste Ibrahim linha 6</lf>";
		parametros = parametros + "</ce>Teste Ibrahim linha 7</lf>";
		parametros = parametros + "</ce>Teste Ibrahim linha 8</lf>";
		parametros = parametros + "</ce></linha_dupla></lf>";
		parametros = parametros + "</pular_linhas></lf>";
		parametros = parametros + "</corte_total>";
		
		return acbr.enviaComandoACBr(conexao,"ESCPOS.imprimirlinha(\""+parametros+"\")");
	
	}
	
	public String formataNumero(Long numero) {
		String resultado="";
		if (numero <10L) {
			resultado = "00"+numero;
		}else if (numero >9 && numero <100 ) {
			resultado = "0"+numero;
		}else {
			resultado = ""+numero;
		}
		return resultado;
	}
	/**
	 * M�todo que imprimi diretamente para impressora o romaneio de venda
	 * @param pedido
	 * @param cabecalho
	 * @param config
	 * @return Comando de impress�o no ACBR
	 * @throws IOException 
	 */
	public String imprimirCupomPdv(Pedido pedido, boolean cabecalho, Configuration config) throws IOException {
		this.emissor = acbr.preencheEmissorVenda(pedido.getEmitente());
		this.destino = acbr.preencheDestinoPdv(pedido.getDestino());
		DadosDeConexaoSocket conexao = new DadosDeConexaoSocket(config.getIpACBR(), new BigDecimal(config.getPortaACBR()).intValue());
		String parametros = "</zera>";
		parametros = parametros + "</linha_dupla></lf>";
		// parametros Emitente
		if ( cabecalho) {
			if (config.isFantasia()) {
				parametros = parametros + "</ce><n><c><a>"+ this.emissor.getXFant()+"</a></c></n></lf>";
			}else {
				parametros = parametros + "</ce><n><c><a>"+ this.emissor.getXNome()+"</a></c></n></lf>";
			}
			parametros = parametros + "</ce><c>"+ this.emissor.getXLgr()+" "+this.emissor.getNro()+" "+ this.emissor.getXCpl()+"</c></lf>";
			parametros = parametros + "</ce><n><s>"+ this.emissor.getFone()+"</s></n></lf>";	
			parametros = parametros + "</linha_dupla>";
			parametros = parametros + "</lf>";
		}
		if (pedido.getPedidoTipo().equals(PedidoTipo.TRA)) {
			parametros = parametros + "</ae><n>Origem: <a>"+ this.emissor.getXNome()+"</a></n></lf>";
		}
		// parametros cliente
		parametros = parametros + "</ae><n>Cliente: "+ this.destino.getXNome() +"</n></lf>"; 
		parametros = parametros + "</ae><c><n>CNPJ/CPF:</c></n> <c>"+this.destino.getCNPJCPF()+"</c></lf>";
		// parametros vendedor
		parametros = parametros + "</ae><n>Colaborador: "+pedido.getAtendente().getApelido()+"</n></fn></lf>";
		// transa��o
		parametros = parametros + "</ae><n>Transacao: "+pedido.getTransacao().getDescricao()+"</n></lf>";
		//Forma de pagamento
		if (pedido.getPedidoTipo().equals(PedidoTipo.PVE)) {
			parametros = parametros + "</ae><n>Pagamento: "+pedido.getPagamento().getDescricao()+"</n></lf></lf>";
		}
		// parametros cabe�alho cupom
		if (pedido.getPedidoTipo().equals(PedidoTipo.TRA)) {
			parametros = parametros + "</ce><c><a><n><s>"+ "Transferencia" + "</s></n></a></c></lf></lf>";
		}else {
			if (pedido.getPedidoTipo().equals(PedidoTipo.DEV)) {
				parametros = parametros + "</ce><c><a><n><s>"+ "Devolucao" + "</s></n></a></c></lf></lf>";
			}else {
				parametros = parametros + "</ce><c><a><n><s>"+ "Orcamento" + "</s></n></a></c></lf></lf>";
			}
		}
//		parametros = parametros + "</ae><c>"+"ID: <a><in> " + pedido.getControle().getId() +" </in></a></c></lf></lf>"; //pedido.getControle().getControle()
		parametros = parametros + "</ae><n>"+"Controle: <a><in> " + formataNumero(pedido.getControle().getControle()) +" - "+ pedido.getControle().getId() +" </in></a></n>"; //pedido.getControle().getControle()
		parametros = parametros + "</ad><c>          "+ pedido.getDataEmissao().format(formatadorData)+"</c></lf></lf>";
//		parametros = parametros + "</ce><c><n>123456789A123456789B123456789C123456789D123456789E123456</n></c></lf>";//quantidade de caracteres em um linha em CONDENSADO 56
		parametros = parametros + "</ce><c><n><s>ITEM</s> <s>REF.</s> <s>DESCRICAO</s>             <s>QUANT.</s> <s>VL.Un.</s>  <s>Vl.Total</s></n></c></lf>";
		int i = 000;
		int totalItens = 000;
		String ii = "00";
		if (pedido.getListaItensPedido().isEmpty() == false) {
			for (ItemPedido item : pedido.getListaItensPedido()) {
				i++;
				if (i >10 && i<100) {
					ii = "0";
				}else {
					if (i >99) {
						ii="";
					}
				}
				totalItens = totalItens + item.getQuantidade().intValue();
				parametros = parametros + "<c><ae><n>"+ ii+ i+"   </n>"+item.getProduto().getReferencia()+"  " +item.getProduto().getDescricao()+"</ae>";
				if (item.getProduto().getTipoMedida() == null) {
					item.getProduto().setTipoMedida(TipoMedida.UN);
				}
				if (item.getBarras() != null) {
					if (item.getBarras().getTamanho() != null && item.getBarras().getCor() != null) {
						parametros = parametros + "<ad>" +"Tm: "+ item.getBarras().getTamanho().getTamanho() +" - Cor:"+ item.getBarras().getCor().getNome()+"- "+ item.getQuantidade() + " "+item.getProduto().getTipoMedida().getSigla() +" X R$"
								+  item.getValorUnitario().setScale(2,RoundingMode.HALF_DOWN)+" = R$"+  item.getValorTotalBruto().setScale(2,RoundingMode.HALF_DOWN)+ "</ad></c></lf>";
					}else {
						if (item.getBarras().getTamanho() != null && item.getBarras().getCor() == null) {
							parametros = parametros + "<ad>" + "Tm: " +item.getBarras().getTamanho().getTamanho() + "- "+item.getQuantidade() + " "+item.getProduto().getTipoMedida().getSigla() +" X R$"
									+  item.getValorUnitario().setScale(2,RoundingMode.HALF_DOWN)+" = R$"+  item.getValorTotalBruto().setScale(2,RoundingMode.HALF_DOWN)+ "</ad></c></lf>";
						}else {
							if (item.getBarras().getTamanho() == null && item.getBarras().getCor() != null) {
								parametros = parametros + "<ad>" +"Cor: " +item.getBarras().getCor().getNome()+ "- "+item.getQuantidade() + " "+item.getProduto().getTipoMedida().getSigla() +" X R$"
										+  item.getValorUnitario().setScale(2,RoundingMode.HALF_DOWN)+" = R$"+  item.getValorTotalBruto().setScale(2,RoundingMode.HALF_DOWN)+ "</ad></c></lf>";
							}else {
								parametros = parametros + "<ad>" + item.getQuantidade() + " "+item.getProduto().getTipoMedida().getSigla() +" X R$"
										+  item.getValorUnitario().setScale(2,RoundingMode.HALF_DOWN)+" = R$"+  item.getValorTotalBruto().setScale(2,RoundingMode.HALF_DOWN)+ "</ad></c></lf>";
							}
						}
					}
				}else {
					parametros = parametros + "<ad>" + item.getQuantidade() + " "+item.getProduto().getTipoMedida().getSigla() +" X R$"
							+  item.getValorUnitario().setScale(2,RoundingMode.HALF_DOWN)+" = R$"+  item.getValorTotalBruto().setScale(2,RoundingMode.HALF_DOWN)+ "</ad></c></lf>";
				}
				
			}
		}
		parametros = parametros + "</ce></linha_dupla></lf>";
		if (pedido.getPedidoTipo().equals(PedidoTipo.TRA)) {
//			parametros = parametros + "</ae><n><s><c><a>"+ "Total: R$" + pedido.getValorTotalPedido().setScale(2, RoundingMode.HALF_UP)+ "</a></c></s>               </ad><c>Total Pecas: " + totalItens+ "</c></n></lf>";
			parametros = parametros + "</ae><n><a>"+ "Total: " + NumberFormat.getCurrencyInstance().format(pedido.getValorTotalPedido().setScale(2, RoundingMode.HALF_UP))+ "</a></lf></ae>Total Pecas: " + NumberFormat.getNumberInstance().format(totalItens)+ "</fn></n></lf>";
		}else {
			parametros = parametros + "</ae><c><n>Desconto: "+ NumberFormat.getCurrencyInstance().format(pedido.getDesconto().setScale(2,RoundingMode.HALF_DOWN)) +"</n></c></lf>";
//			parametros = parametros + "</ae><n><s><a>"+ "Total: R$ " + pedido.getValorTotalPedido().setScale(2, RoundingMode.HALF_UP)+ "</a></s></fn>               </ad><c>Total Pecas: " + totalItens+ "</c></n></lf>";
			parametros = parametros + "</ae><n><a>"+ "Total: " + NumberFormat.getCurrencyInstance().format(pedido.getValorTotalPedido().setScale(2, RoundingMode.HALF_UP))+ "</a></lf></ae>Total Pecas: " + NumberFormat.getNumberInstance().format(totalItens)+ "</fn></n></lf>";
		}
		parametros = parametros + "</ce></linha_dupla></lf>";
		if (pedido.getPedidoTipo().equals(PedidoTipo.PVE)) {
			parametros = parametros + "</ae><c><n>"+config.getMensPDV()+"</n></c></lf>";
			parametros = parametros + "</ce></linha_dupla></lf>";
		}
		parametros = parametros + "</pular_linhas></lf>";
		parametros = parametros + "</corte_total>";
		if (config.isResumoCupomPdv()) {
			parametros = parametros + imprimirResumoVendaPdv(pedido,totalItens);
		}
		if (impressoraAtivada(conexao) == false) {
			ativaImpressora(conexao);
		}
		setaModeloImpressora(conexao, config.getImpressoraPdv());
		setaPorta(conexao, config.getPortaUsbVendaPdv());
		return acbr.enviaComandoACBr(conexao,"ESCPOS.imprimirlinha(\""+parametros+"\")");
	
	}
	
	/**
	 * Metodo que retorna o resumo da venda 
	 * @param pedido , Total de itens
	 * @return String
	 */
	
	public String imprimirResumoVendaPdv(Pedido pedido,int totalItens) {
		
		String parametros = "</zera>";
		parametros = parametros + "</linha_dupla></lf>";
		parametros = parametros + "</ae><n>"+"Controle: <a><in> " + formataNumero(pedido.getControle().getControle()) +" - "+ pedido.getControle().getId() +" </in></a></n>"; //pedido.getControle().getControle()
		parametros = parametros + "</ad><c>          "+ pedido.getDataEmissao().format(formatadorData)+"</c></lf></lf>";
		if (pedido.getPedidoTipo().equals(PedidoTipo.TRA)) {
			parametros = parametros + "</ae><n><c>Origem: <a>"+ this.emissor.getXNome()+"</a></c></n></lf>";
		}
		parametros = parametros + "</ae></fn><n>Colaborador: "+pedido.getAtendente().getApelido()+"</n></lf>";
		parametros = parametros + "</ae><n>Cliente: </n> "+ this.destino.getXNome() +"</n></lf>"; 
		// transa��o
				parametros = parametros + "</ae><n>Transacao: "+pedido.getTransacao().getDescricao()+"</n></lf>";
		//Forma de pagamento
		if (pedido.getPedidoTipo().equals(PedidoTipo.PVE)) {
			parametros = parametros + "</ae><n>Pagamento: "+pedido.getPagamento().getDescricao()+"</n></lf></lf>";
		}
		parametros = parametros + "</ae><n><a>"+ "Total: " + NumberFormat.getCurrencyInstance().format(pedido.getValorTotalPedido().setScale(2, RoundingMode.HALF_UP))+ "</a></lf></ae></s>Total Pecas: " + NumberFormat.getNumberInstance().format(totalItens)+ "</s></fn></n></lf>";
		parametros = parametros + "</linha_dupla></lf>";
		parametros = parametros + "</pular_linhas></lf>";
		parametros = parametros + "</corte_total>";
		return parametros;
	}
	
	
	public String imprimiSaldoFechandoOperado(Caixa caixa, Configuration config) throws IOException {
		DadosDeConexaoSocket conexao = new DadosDeConexaoSocket(config.getIpACBR(), new BigDecimal(config.getPortaACBR()).intValue());
		BigDecimal totalCaixa = new BigDecimal("0"); 
		String parametros = "</zera>";
		parametros = parametros + "</linha_dupla></lf>";
		parametros = parametros + "</ae><c><n>Caixa Usuario:</c></n> <c>"+ caixa.getUsuario() + "    ID: "+ caixa.getId()+"   Turno: "+caixa.getNumeroTurno()+"</c></lf>"; 
		parametros = parametros + "</ae><c><n>"+"Abertura:</n> <in> " +caixa.getDataAbertura().format(formatadorData) +" </in>" +"</c>"; 
		parametros = parametros + "</ad><c>                   Hora:"+ caixa.getHoraAbertura().format(formatadorTime)+"</c></lf></lf>";
		parametros = parametros + "</ae><c><n>"+"Fechamento:</n> <in> " +caixa.getDataFechamento().format(formatadorData) +" </in>" +"</c>"; 
		parametros = parametros + "</ad><c>                Hora:"+ caixa.getHoraFechamento().format(formatadorTime)+"</c></lf></lf>";
		parametros = parametros + "</linha_dupla>";
		parametros = parametros + "</lf>";
		for (RecebimentoParcial recFecha : caixa.getListaFechamentoCaixa()) {
			parametros = parametros + "<c></ae><n>"+recFecha.getTipoPagamento()+ "       </n>  R$: "+recFecha.getValorRecebido().setScale(2,RoundingMode.HALF_DOWN)+"</ae></c></lf>";
			totalCaixa = totalCaixa.add(recFecha.getValorRecebido().setScale(2,RoundingMode.HALF_DOWN));
		}
//		parametros = parametros + "<c></ae><n>Fundo de Caixa   </n>  R$: "+caixa.getFundoCaixa().setScale(2,RoundingMode.HALF_DOWN)+"</ae></c></lf>";
		parametros = parametros + "</ae><n><a>"+ "Total do caixa: " + NumberFormat.getCurrencyInstance().format(totalCaixa) + "</a></lf>";
		parametros = parametros + "</pular_linhas></lf>";
		parametros = parametros + "</linha_dupla>";
		parametros = parametros + "</lf>";
		parametros = parametros + "</ae><c><n>Status Fechamento:</c></n> <c>"+ caixa.getConferencia() + "</c></lf>";
		parametros = parametros + "</linha_dupla>";
		parametros = parametros + "</pular_linhas></lf>";
		parametros = parametros + "</lf>";
		parametros = parametros + "</ae><c><n>Assinatura Usuario:</n> ___________________________________"+"</c></lf>";
		parametros = parametros + "</pular_linhas></lf>";
		parametros = parametros + "</pular_linhas></lf>";
		parametros = parametros + "</corte_total>";
		
		if (impressoraAtivada(conexao) == false) {
			ativaImpressora(conexao);
		}
		setaModeloImpressora(conexao, config.getImpressoraPdv());
		setaPorta(conexao, config.getPortaUsbVendaPdv());
		return acbr.enviaComandoACBr(conexao,"ESCPOS.imprimirlinha(\""+parametros+"\")");
	}
	
	/**
	 * M�todo utilizado para reimprimir um pedido de venda
	 * @param pedido
	 * @param cabecalho
	 * @param config
	 * @return
	 * @throws IOException 
	 */
	
	public String imprimirReimpressao(Pedido pedido, boolean cabecalho, Configuration config) throws IOException {
		return imprimirCupomPdv(pedido,cabecalho,config);
	
	}
	
	
}