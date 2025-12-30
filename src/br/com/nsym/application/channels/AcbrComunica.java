package br.com.nsym.application.channels;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.hibernate.HibernateException;

import br.com.ibrcomp.exception.NfeException;
import br.com.nsym.application.controller.nfe.tools.AcbrComunicaReformaHelper;
import br.com.nsym.application.controller.nfe.tools.CupomFiscalCaixa;
import br.com.nsym.application.controller.nfe.tools.FormulasDosImpostos;
import br.com.nsym.application.producer.qualifier.AuthenticatedUser;
import br.com.nsym.domain.misc.CpfCnpjUtils;
import br.com.nsym.domain.misc.ImportaCliente.ClienteTemp;
import br.com.nsym.domain.misc.LocalizaRegex;
import br.com.nsym.domain.model.entity.cadastro.Contato;
import br.com.nsym.domain.model.entity.cadastro.Email;
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.Fone;
import br.com.nsym.domain.model.entity.financeiro.RecebimentoParcial;
import br.com.nsym.domain.model.entity.financeiro.TipoPagamento;
import br.com.nsym.domain.model.entity.financeiro.tools.Parcelas;
import br.com.nsym.domain.model.entity.financeiro.tools.ParcelasNfe;
import br.com.nsym.domain.model.entity.fiscal.NVE;
import br.com.nsym.domain.model.entity.fiscal.Cfe.CFe;
import br.com.nsym.domain.model.entity.fiscal.Cfe.EmitenteCFe;
import br.com.nsym.domain.model.entity.fiscal.Cfe.ItemCFe;
import br.com.nsym.domain.model.entity.fiscal.Cfe.Nfce;
import br.com.nsym.domain.model.entity.fiscal.nfe.CartaCorrecao;
import br.com.nsym.domain.model.entity.fiscal.nfe.Emitente;
import br.com.nsym.domain.model.entity.fiscal.nfe.ItemNfe;
import br.com.nsym.domain.model.entity.fiscal.nfe.Nfe;
import br.com.nsym.domain.model.entity.fiscal.tools.FinalidadeNfe;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoCalculo;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoImpressaoDFe;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoPesquisa;
import br.com.nsym.domain.model.entity.tools.Enquadramento;
import br.com.nsym.domain.model.entity.tools.MensagensSistema;
import br.com.nsym.domain.model.entity.tools.TipoCliente;
import br.com.nsym.domain.model.entity.tools.Uf;
import br.com.nsym.domain.model.entity.venda.EmitenteVenda;
import br.com.nsym.domain.model.repository.cadastro.ContatoRepository;
import br.com.nsym.domain.model.repository.cadastro.EmailRepository;
import br.com.nsym.domain.model.repository.cadastro.EmpresaRepository;
import br.com.nsym.domain.model.repository.cadastro.FilialRepository;
import br.com.nsym.domain.model.repository.cadastro.FoneRepository;
import br.com.nsym.domain.model.security.User;
import br.com.nsym.infraestrutura.configuration.ApplicationUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@RequestScoped
public class AcbrComunica {

	@Getter
	@Setter
	private String retorno;
	
	@Inject
	private FormulasDosImpostos calcula;

	@Getter
	@Setter
	private Socket telnetAcbr;

	@Getter
	@Setter
	private PrintWriter out;

	@Getter
	@Setter
	private BufferedReader in ;

	@Getter
	@Setter
	private String leitura;

	@Getter
	@Setter
	private String enter = "\r"+"\n";

	@Getter
	@Setter
	private short b = -1;

	@Getter
	@Setter
	private String comandoEnviar;

	@Getter
	@Setter
	private boolean primeiroAcesso = true;

	@Getter
	@Setter
	private String conexaoOk = "N";

	@Getter
	@Inject
	@AuthenticatedUser
	private User usuarioAutenticado;

	@Inject
	private EmpresaRepository empDao;

	@Inject
	private FilialRepository filialDao;

	@Inject
	private FoneRepository telefoneDao;

	@Inject
	private EmailRepository emailDao; 

	@Getter
	@Setter
	private Fone telefone;

	@Inject
	private ContatoRepository contatoDao;

	@Getter
	@Setter
	private Emissor emissor = new Emissor();
	
	@Getter
	@Setter
	private Destino destino = new Destino();

	@Getter
	@Setter
	private String produtoPreenchido ="";

	@Getter
	@Setter
	private String notaReferencia = "";

	@Getter
	@Setter
	private String pagamentoPreenchido = "";

	@Getter
	@Setter
	private String infFisco = new String();

	@Getter
	@Setter
	private String infEmitente = new String();

	@Getter
	private MensagensSistema menInt = new MensagensSistema();
	@Getter
	@Setter
	private boolean is101 = false;
	@Getter
	@Setter
	private boolean is102 = false;
	@Getter
	@Setter
	private boolean isSimples = false;
	
	@Getter
	@Setter
	private boolean is50 = false;

	@Inject
	private LocalizaRegex localiza;

	@Getter
	private LocalDateTime dataHoraHoje = LocalDateTime.now();

	@Getter
	private DateTimeFormatter formatador = DateTimeFormatter
	.ofLocalizedDateTime(FormatStyle.MEDIUM)
	.withLocale(new Locale("pt", "br"));
	
	@Getter
	private DateTimeFormatter formatoSimples = DateTimeFormatter
	.ofLocalizedDateTime(FormatStyle.SHORT)
	.withLocale(new Locale("pt", "br"));
	
	@Getter
	private DateTimeFormatter formatoSimplesDate = DateTimeFormatter
	.ofLocalizedDate(FormatStyle.SHORT)
	.withLocale(new Locale("pt", "br"));


//	@Transactional
	public void criaConexao(DadosDeConexaoSocket dados){
		try{
			this.telnetAcbr = new Socket(InetAddress.getByName(dados.getNameHost()), dados.getPorta());
		} 
		catch (java.net.UnknownHostException u) {
			this.retorno = "Erro: Host desconhecido! Verifique as configurações de rede: " + "\n\n" + u.getMessage();
		}
		catch (java.io.IOException io) {
			this.retorno = "Erro: Falha de comunicação com o ACBrMonitor, contate o suporte técnico: " + "\n\n" + io.getMessage();
		}
		catch (Exception e) {
			this.retorno = "Erro: tentando conectar com o ACBrMonitor. Contate o suporte técnico: " + "\n\n" + e.getMessage();
		}
	}

//	@Transactional
	public String comandoAcbr(String comando) throws IOException{
		try{
			this.out = new PrintWriter(telnetAcbr.getOutputStream(),true);
			this.in = new BufferedReader(new InputStreamReader(telnetAcbr.getInputStream(),Charset.forName("UTF-8")));
			if (isPrimeiroAcesso()){
				//loop para limpar o cabeçalho da conexao com o acbr
				this.leitura = "";
				while(this.b != 3)
				{
					this.b = (short) in.read();
					if (this.b != 3)
						this.leitura += (char)(b);
				}
				System.out.println("Leitura: " + leitura);
				setPrimeiroAcesso(false);
			}
			this.out.println(comando.trim()+"\r"+"\n"+"."+"\r"+"\n");
			System.out.println(comando+enter);
			this.out.flush();
			// faz a leitura do retorno
			this.retorno = "";
			this.b = -1;
			while(this.b != 3)
			{
				this.b = (short) in.read();
				if (this.b != 3)
					this.retorno += (char)(b);
			}
			System.out.println("Resposta: " + retorno);
			//fecha conexao
			this.out.close();
			this.in.close();
//			fechaTudo();
			setPrimeiroAcesso(true);
		}catch (java.net.UnknownHostException u) {
			fechaTudo();
			this.retorno = "Erro: Host desconhecido! Verifique as configurações de rede: " + "\n\n" + u.getMessage();
		}
		catch (java.io.IOException io) {
			fechaTudo();
			this.retorno = "Erro: Falha de comunicação com o ACBrMonitor, contate o suporte técnico: " + "\n\n" + io.getMessage();
		}
		catch (Exception e) {
			fechaTudo();
			this.retorno = "Erro: tentando conectar com o ACBrMonitor. Contate o suporte técnico: " + "\n\n" + e.getMessage();
		}
		System.out.println("Retorno fim: " +this.retorno);
		return this.retorno;
	}
	
	public void fechaTudo() throws IOException {
		if (this.in != null) {
			this.in.close();
		}
		if (this.out != null) {
			this.out.close();
		}
		if (this.telnetAcbr != null) {
			this.telnetAcbr.close();
		}
	}
	
	public void fechaTelnet() throws IOException {
		if(this.telnetAcbr != null) {
			this.telnetAcbr.close();
		}
	}

	public String consultaStatusNFE(DadosDeConexaoSocket dados) throws IOException{
		return comandoAcbr("NFE.StatusServico");
	}



//	@Transactional
	public String enviaComandoACBr(DadosDeConexaoSocket dados,String comando) throws IOException{
		try{
			
			this.retorno = "";
			setPrimeiroAcesso(true);
			this.b = -1;

			criaConexao(dados);
			String mensagem = comandoAcbr(comando);
			
//			this.telnetAcbr.close();
			fechaTudo();
			return mensagem;
		}
		catch (Exception e) {
			fechaTudo();
			this.retorno = "Erro: tentando conectar com o ACBrMonitor. Contate o suporte técnico: " + "\n\n" + e.getMessage();
			return this.retorno;
		}		

	}
	/**
	 *  método que exibe todas as NFes recebidas pelo emitente
	 * @param dados - conexao Acbr - DadosDeConexaoSocket
	 * @param uf - sigla Estado
	 * @param cnpj - CNPJ emitente
	 * @param nsu - Sequencia Unica - opcional (caso não for informar enviar NULL)
	 * @return
	 * @throws IOException 
	 */
//	@Transactional
	public String distribuicaoDFe(DadosDeConexaoSocket dados,String uf, String cnpj, String nsu) throws IOException{
		String comando;
		try {
			if ((dados != null && uf != null) || (dados != null && uf.isEmpty()==false)){
				if ( nsu == null || nsu.isEmpty()){
					comando = "NFe.DistribuicaoDFe(\""+uf+"\",\""+cnpj+"\")";
				}else{
					comando = "NFe.DistribuicaoDFe(\""+uf+"\",\""+cnpj+"\","+nsu+")";
				}
				return enviaComandoACBr(dados,comando);
			}else{
				this.retorno = "Erro: tentando conectar com o ACBrMonitor. Contate o suporte técnico: " + "\n\n";
				return this.retorno;
			}
		}catch (Exception e ){
			fechaTudo();
			this.retorno = "Erro: tentando conectar com o ACBrMonitor. Contate o suporte técnico: " + "\n\n" + e.getMessage();
			return this.retorno;
		}
	}
	
	
	/**
	 * Método para localizar a ultima NSU
	 * @param dados
	 * @param uf
	 * @param cnpj
	 * @param nsu
	 * @return
	 * @throws IOException 
	 */
//	@Transactional
	public String achaNSU(DadosDeConexaoSocket dados,String uf, String cnpj, String nsu) throws IOException{
		String comando;
		try {
			if ((dados != null && uf != null) || (dados != null && uf.isEmpty()==false)){
				if ( nsu == null || nsu.isEmpty()){
					comando = "NFe.DistribuicaoDFePorUltNSU(\""+uf+"\",\""+cnpj+"\")";
				}else{
					comando = "NFe.DistribuicaoDFePorUltNSU(\""+uf+"\",\""+cnpj+"\","+nsu+")";
				}
				return enviaComandoACBr(dados,comando);
			}else{
				this.retorno = "Erro: tentando conectar com o ACBrMonitor. Contate o suporte técnico: " + "\n\n";
				return this.retorno;
			}
		}catch (Exception e ){
			fechaTudo();
			this.retorno = "Erro: tentando conectar com o ACBrMonitor. Contate o suporte técnico: " + "\n\n" + e.getMessage();
			return this.retorno;
		}
	}
	
	/**
	 *  método que recebe o XML da NFe emitida pelo fornecedor, desde que a NFe tenha sido manifetada
	 *  com Confirmação de operação
	 * @param dados - conexao Acbr - DadosDeConexaoSocket - OBRIGATÓRIO
	 * @param uf - codigo numérico do Estado - IBGE ou 91 - ambiente nacional - OBRIGATÓRIO
	 * @param cnpj - CNPJ do Destinatário da NFE - OBRIGATÓRIO
	 * @param nsu - NSU da NFE - OBRIGATÓRIO
	 * @return STRING - Retorno ACBR
	 * @throws IOException 
	 */
//	@Transactional
	public String distribuicaoDFePorNSU(DadosDeConexaoSocket dados,String uf, String cnpj, String nsu) throws IOException{
		String comando;
		try{
			if (nsu.equalsIgnoreCase("") || nsu == null || nsu.isEmpty() || uf == null || uf.isEmpty()|| cnpj == null || cnpj.isEmpty()){
				this.retorno = "Erro: tentando conectar com o ACBrMonitor. Contate o suporte técnico: " + "\n\n";
				return this.retorno;
			}else{
				comando = "NFe.DistribuicaoDFePorNSU(\""+uf+"\",\""+cnpj+"\","+nsu+")";
				return enviaComandoACBr(dados,comando);
			}
		}catch (Exception e){
			fechaTudo();
			this.retorno = "Erro: tentando conectar com o ACBrMonitor. Contate o suporte técnico: " + "\n\n" + e.getMessage();
			return this.retorno;
		}
	} 

	/**
	 *  método que recebe o XML da NFe emitida pelo fornecedor, desde que a NFe tenha sido manifetada
	 *  com Confirmação de operação
	 * @param dados - conexao Acbr - DadosDeConexaoSocket - OBRIGATÓRIO
	 * @param uf - codigo numérico do Estado - IBGE ou 91 - ambiente nacional - OBRIGATÓRIO
	 * @param cnpj - CNPJ do Destinatário da NFE - OBRIGATÓRIO
	 * @param chave - Chave da NFE - OBRIGATÓRIO
	 * @return STRING - XML NFE
	 * @throws IOException 
	 */
//	@Transactional
	public String distribuicaoDFePorChaveNfe(DadosDeConexaoSocket dados,String uf, String cnpj, String chave) throws IOException{
		String comando;
		try{
			if (chave.equalsIgnoreCase("") || chave == null || chave.isEmpty() || uf == null || uf.isEmpty()|| cnpj == null || cnpj.isEmpty()){
				this.retorno = "Erro: tentando conectar com o ACBrMonitor. Contate o suporte técnico: " + "\n\n";
				return this.retorno;
			}else{
				comando = "NFe.DistribuicaoDFePorChaveNFe(\""+uf+"\",\""+cnpj+"\","+chave+")";
				return enviaComandoACBr(dados,comando);
			}
		}catch (Exception e){
			fechaTudo();
			this.retorno = "Erro: tentando conectar com o ACBrMonitor. Contate o suporte técnico: " + "\n\n" + e.getMessage();
			return this.retorno;
		}
	} 

	/**
	 * Método que le uma XML para importar para o sistema
	 * @param dados
	 * @param nomeArquivo
	 * @return
	 * @throws IOException 
	 */
	public String lerXML(DadosDeConexaoSocket dados,String nomeArquivo) throws IOException{
		try {
		return enviaComandoACBr(dados,"NFE.LoadfromFile(\"C:\\ibrcomp\\NFeEntrada\\"+nomeArquivo+"-nfe.xml\")");
		}catch (Exception e) {
			fechaTudo();
			this.retorno = "Erro: tentando conectar com o ACBrMonitor. Contate o suporte técnico: " + "\n\n" + e.getMessage();
			return this.retorno;
		}
	}

	/**
	 * Método que verifica se existe arquivo na maquina
	 * @param dados
	 * @param nomeArquivo
	 * @return true se existir e falso caso contrario
	 * @throws IOException 
	 */
	public boolean arquvioExiste(DadosDeConexaoSocket dados,String nomeArquivo) throws IOException{
		String resposta = enviaComandoACBr(dados,"NFe.FileExists(\"C:\\ibrcomp\\NFeEntrada\\"+nomeArquivo+"-nfe.xml\")");
		boolean achei = localiza.localizaPalavra(resposta.toUpperCase(), "ERRO");
		if (achei){
			return false;
		}else{
			return true;
		}
	}

	/**
	 * Método de confirmacao de operacao com NFE - O evento de “Confirmação da Operação”
	 * pelo destinatário confirma a operação e o recebimento da mercadoria (para as operações com circulação de mercadoria).
	 *	Se ocorrer a devolução total ou parcial das mercadorias, além do procedimento atual de geração da Nota Fiscal de devolução, 
	 * também poderá ser comandado o evento da “Confirmação da Operação”.
	 * @param dados - conexao Acbr - DadosDeConexaoSocket - OBRIGATÓRIO
	 * @param uf - codigo numérico do Estado - IBGE ou 91 - ambiente nacional - OBRIGATÓRIO
	 * @param cnpj - CNPJ do Destinatário da NFE - OBRIGATÓRIO
	 * @param chave - Chave da NFE - OBRIGATÓRIO
	 * @param nomeArquivo - nome para o arquivo .ini que será gerado para trasnmitir pelo ACBR
	 * @return
	 * @throws IOException 
	 */
//	@Transactional
	public String confirmaOperacao(DadosDeConexaoSocket dados,String uf, String cnpj, String chave, String nomeArquivo) throws IOException{
		try{
			// criando o arquivo .ini
			if (chave.equalsIgnoreCase("") || chave == null || chave.isEmpty() || uf == null || uf.isEmpty()|| cnpj == null || cnpj.isEmpty()){
				this.retorno = "Erro: tentando conectar com o ACBrMonitor. Contate o suporte técnico: " + "\n\n" ;
				return this.retorno;
			}else{
				String confOper =
						"[EVENTO]" + "\n"+
								"idLote=1" +"\n"+
								"[EVENTO001]"+"\n"+
								"cOrgao="+uf.trim()+"\n"+
								"CNPJ="+cnpj.trim()+"\n"+
								"chNFe="+chave.trim()+"\n"+
								"dhEvento="+dataHoraHoje.format(formatoSimples) +"\n"+
								"tpEvento=210200"+"\n"+
								"nSeqEvento=1"+"\n"+
								"versaoEvento=1.00";
				this.retorno =  enviaComandoACBr(dados, "ACBr.SaveToFile(\"C:\\ibrcomp\\tmp\\"+"cop"+nomeArquivo+".ini\",\""+confOper+"\",5)");
				//			return this.retorno;
				return enviaComandoACBr(dados, "NFe.EnviarEvento(\""+"C:\\ibrcomp\\tmp\\cop"+nomeArquivo+".ini\")");
			}
		}catch (Exception e){
			fechaTudo();
			this.retorno = "Erro: tentando conectar com o ACBrMonitor. Contate o suporte técnico: " + "\n\n" + e.getMessage();
			return this.retorno;
		}
	}
	
	/**
	 * Método que DESCONHECE  operacao com NFE - O evento de “Desconhecimento da Operação”
	 * pelo destinatário .
	 * @param dados - conexao Acbr - DadosDeConexaoSocket - OBRIGATÓRIO
	 * @param uf - codigo numérico do Estado - IBGE ou 91 - ambiente nacional - OBRIGATÓRIO
	 * @param cnpj - CNPJ do Destinatário da NFE - OBRIGATÓRIO
	 * @param chave - Chave da NFE - OBRIGATÓRIO
	 * @param nomeArquivo - nome para o arquivo .ini que será gerado para trasnmitir pelo ACBR
	 * @return
	 * @throws IOException 
	 */
	
//	@Transactional
	public String desconheceOperacao(DadosDeConexaoSocket dados,String uf, String cnpj, String chave, String nomeArquivo) throws IOException{
		try{
			// criando o arquivo .ini
			if (chave.equalsIgnoreCase("") || chave == null || chave.isEmpty() || uf == null || uf.isEmpty()|| cnpj == null || cnpj.isEmpty()){
				this.retorno = "Erro: tentando conectar com o ACBrMonitor. Contate o suporte técnico: " + "\n\n" ;
				return this.retorno;
			}else{
				String confOper =
						"[EVENTO]" + "\n"+
								"idLote=1" +"\n"+
								"[EVENTO001]"+"\n"+
								"cOrgao="+uf.trim()+"\n"+
								"CNPJ="+cnpj.trim()+"\n"+
								"chNFe="+chave.trim()+"\n"+
								"dhEvento="+dataHoraHoje.format(formatoSimples) +"\n"+
								"tpEvento=210220"+"\n"+
								"nSeqEvento=1"+"\n"+
								"versaoEvento=1.00";
				this.retorno =  enviaComandoACBr(dados, "ACBr.SaveToFile(\"C:\\ibrcomp\\tmp\\"+"des"+nomeArquivo+".ini\",\""+confOper+"\",5)");
				//			return this.retorno;
				return enviaComandoACBr(dados, "NFe.EnviarEvento(\""+"C:\\ibrcomp\\tmp\\des"+nomeArquivo+".ini\")");
			}
		}catch (Exception e){
			fechaTudo();
			this.retorno = "Erro: tentando conectar com o ACBrMonitor. Contate o suporte técnico: " + "\n\n" + e.getMessage();
			return this.retorno;
		}
	}
	
	/**
	 * Método Operação Não Realizada  - O evento de “Operação não realizada”
	 * pelo destinatário .
	 * @param dados - conexao Acbr - DadosDeConexaoSocket - OBRIGATÓRIO
	 * @param uf - codigo numérico do Estado - IBGE ou 91 - ambiente nacional - OBRIGATÓRIO
	 * @param cnpj - CNPJ do Destinatário da NFE - OBRIGATÓRIO
	 * @param chave - Chave da NFE - OBRIGATÓRIO
	 * @param nomeArquivo - nome para o arquivo .ini que será gerado para trasnmitir pelo ACBR
	 * @return
	 * @throws IOException 
	 */
	
//	@Transactional
	public String operacaoNaoRealizada(DadosDeConexaoSocket dados,String uf, String cnpj, String chave, String nomeArquivo, String justificativa) throws IOException{
		try{
			// criando o arquivo .ini
			if (chave.equalsIgnoreCase("") || chave == null || chave.isEmpty() || uf == null || uf.isEmpty()|| cnpj == null || cnpj.isEmpty()){
				this.retorno = "Erro: tentando conectar com o ACBrMonitor. Contate o suporte técnico: " + "\n\n" ;
				return this.retorno;
			}else{
				String confOper =
						"[EVENTO]" + "\n"+
								"idLote=1" +"\n"+
								"[EVENTO001]"+"\n"+
								"cOrgao="+uf.trim()+"\n"+
								"CNPJ="+cnpj.trim()+"\n"+
								"chNFe="+chave.trim()+"\n"+
								"dhEvento="+dataHoraHoje.format(formatoSimples) +"\n"+
								"tpEvento=210240"+"\n"+
								"nSeqEvento=1"+"\n"+
								"versaoEvento=1.00"+
								"xJust="+justificativa;
				this.retorno =  enviaComandoACBr(dados, "ACBr.SaveToFile(\"C:\\ibrcomp\\tmp\\"+"onr"+nomeArquivo+".ini\",\""+confOper+"\",5)");
				//			return this.retorno;
				return enviaComandoACBr(dados, "NFe.EnviarEvento(\""+"C:\\ibrcomp\\tmp\\onr"+nomeArquivo+".ini\")");
			}
		}catch (Exception e){
			fechaTudo();
			this.retorno = "Erro: tentando conectar com o ACBrMonitor. Contate o suporte técnico: " + "\n\n" + e.getMessage();
			return this.retorno;
		}
	}
	
	/**
	 * Ativa o SAT
	 * 
	 * @param dados
	 * @param cnpj
	 * @param uf
	 * @return
	 * @throws IOException 
	 */
	public String satAtiva (DadosDeConexaoSocket dados,String cnpj, Uf uf) throws IOException {
		return enviaComandoACBr(dados, "SAT.Ativar(\""+cnpj+","+uf.getCod()+"\")");
	}
	
	/**
	 * Inicializa o SAT
	 * 
	 * @param dados
	 * @return Resposta do SAT
	 * @throws IOException 
	 */
	public String satInicializa(DadosDeConexaoSocket dados) throws IOException {
		return enviaComandoACBr(dados, "SAT.Inicializar");
	}
	
	/**
	 * Desinicializa o SAT
	 * 
	 * @param dados
	 * @return Resposta do SAT
	 * @throws IOException 
	 */
	public String satDesinicializa(DadosDeConexaoSocket dados) throws IOException {
		return enviaComandoACBr(dados, "SAT.Desinicializar");
	}
	
	/**
	 * Comando que retorna o status do SAT
	 * 
	 * @param dados
	 * @return
	 * @throws IOException 
	 */
	public String satConsultaStatus(DadosDeConexaoSocket dados) throws IOException {
		satInicializa(dados);
		String resposta =  enviaComandoACBr(dados,"SAT.ConsultarStatusOperacional");
		satDesinicializa(dados);
		return resposta;
	}
	
	/**
	 * Comando para Criar e Enviar a CFe para o SAT
	 * 
	 * @param dados
	 * @param arquivo
	 * @return Resposta do SAT 
	 * @throws IOException 
	 */
	public String satCriarEnviarCFe(DadosDeConexaoSocket dados, String arquivo) throws IOException {
		satInicializa(dados);
		String resposta = enviaComandoACBr(dados, "SAT.CriarEnviarCFe(\""+"C:\\ibrcomp\\tmp\\"+arquivo+".ini\")");
		satDesinicializa(dados);
		return resposta;
	}
	
	/**
	 * Comando para imprimir o CFe
	 * 
	 * @param dados - conexao ao ACBR
	 * @param caminho - patch com o local do arquivo  + nome do arquivo XML
	 * @return
	 * @throws IOException 
	 */
	public String satImprimiExtratoVenda(DadosDeConexaoSocket dados, String caminho) throws IOException {
//		satInicializa(dados);
		String resposta = enviaComandoACBr(dados,"SAT.ImprimirExtratoVenda(\""+caminho.trim()+"\")");
//		satDesinicializa(dados);
		return resposta;
	}
	
	/**
	 * Envia email com o arquivo PDF do CFe emitido.
	 * @param dados - conexão ACBR
	 * @param destino - Email que irá receber o PDF
	 * @param xmlVenda - patch com o caminho e o XML do CFe 
	 * @param chave - chave da CFE
	 * @return 
	 * @throws IOException 
	 */
	public String satEnviarEmailCFe(DadosDeConexaoSocket dados, String destino, String xmlVenda,String chave) throws IOException {
//		satInicializa(dados);
		String resultado =  enviaComandoACBr(dados,"SAT.EnviarEmailCFe(\""+destino.trim()+"\",\""+ xmlVenda.trim()+"\",,,,\"C:\\ibrcomp\\pdfSat\\"+chave.trim()+".pdf\")");
//		satDesinicializa(dados);
		return resultado;
	}
	
	/**
	 * Envia uma NF-e/NFC-e por e-mail (comando do ACBrMonitor: NFe.EnviarEmail).
	 *
	 * Assunto e corpo da mensagem são configurados no ACBrMonitor/ACBrNFeMonitor.
	 *
	 * Sintaxe (ACBr): NFe.EnviarEmail(cEmailDestino,cArqXML,cEnviaPDF,[cAssunto],[cEmailsCopias],[cAnexos],[cReplayTo])
	 */
	public String nfeEnviarEmail(DadosDeConexaoSocket dados,
			String emailDestino,
			String caminhoXml,
			boolean enviaPdf,
			String assunto,
			String emailsCopias,
			String anexos,
			String replyTo) throws IOException {

		String cmd = "NFe.EnviarEmail(\"" + safeStr(emailDestino).trim() + "\","
				+ "\"" + safeStr(caminhoXml).trim() + "\","
				+ (enviaPdf ? "1" : "0");

		if (!isBlank(assunto) || !isBlank(emailsCopias) || !isBlank(anexos) || !isBlank(replyTo)) {
			cmd += ",\"" + nz(assunto) + "\"";
			cmd += ",\"" + nz(emailsCopias) + "\"";
			cmd += ",\"" + nz(anexos) + "\"";
			cmd += ",\"" + nz(replyTo) + "\"";
		}

		cmd += ")";
		return enviaComandoACBr(dados, cmd);
	}

	public String nfeEnviarEmail(DadosDeConexaoSocket dados, String emailDestino, String caminhoXml, boolean enviaPdf) throws IOException {
		return nfeEnviarEmail(dados, emailDestino, caminhoXml, enviaPdf, null, null, null, null);
	}

	
	/**
	 * cria o PDF do CFe 
	 * 
	 * @param dados - Conexão do ACBR
	 * @param arqVenda - XML do CFE a ser criada a PDF
	 * @param pdf - Caminho com o nome do arquivo que sera gerado o PDF
	 * @return retorna o caminho onde foi salvo o PDF
	 * @throws IOException 
	 */
	
	public String geraPDFExtratoVenda(DadosDeConexaoSocket dados, String arqVenda, String pdf) throws IOException {
//		satInicializa(dados);
		String resultado = enviaComandoACBr(dados, "SAT.GerarPDFExtratoVenda(\""+arqVenda.trim()+"\",\"C:\\ibrcomp\\pdfSat\\"+pdf.trim()+"\")");
//		satDesinicializa(dados);
		return resultado;
	}
	
	/**
	 * Método que cria o arquivo XML na máquina
	 * @param dados
	 * @param xml
	 * @param nomeArquivo
	 * @return
	 * @throws IOException 
	 */
	public String criaArqXMLNfeCompras(DadosDeConexaoSocket dados,String xml,String nomeArquivo) throws IOException{
		return enviaComandoACBr(dados, "ACBr.SaveToFile(\"C:\\ibrcomp\\NfeEntrada\\"+nomeArquivo+"-nfe.xml\",\""+xml+"\",5)");
	}

	/**
	 * Módulo que gera o arquivo ini na maquina do cliente para emissão de NFe
	 * @param dados
	 * @param nomeArquivo
	 * @param nota
	 * @param finalidade
	 * @return String
	 * @throws IOException 
	 */
	
	public String criarArqIniMaqRemota(DadosDeConexaoSocket dados, String nomeArquivo, Nfe nota, FinalidadeNfe finalidade, boolean isNFCEAtivo) throws IOException,NfeException {
	    try {
	        System.out.println("Iniciando a montagem da nfe para acbr");

	        // Reseta buffers/flags usadas em outros pontos da classe
	        this.notaReferencia     = nz(this.notaReferencia);
	        this.pagamentoPreenchido= nz(this.pagamentoPreenchido);
	        this.produtoPreenchido  = nz(this.produtoPreenchido);
	        this.infFisco           = nz(this.infFisco);
	        this.infEmitente        = nz(this.infEmitente);
	        this.isSimples          = false;
	        this.is101              = false;
	        this.is50               = false;

	        System.out.println("inicio de conversao Emissor");
	        this.emissor = preencheEmitente(nota.getEmitente());
	        System.out.println("Fim do emissor inicio da conversao do destino");
	        this.destino = preencheDestino(nota);
	        System.out.println("Fim do destino");

	        final DateTimeFormatter formataAAMM      = DateTimeFormatter.ofPattern("yy/MM");
	        final DateTimeFormatter formataAaaaMmDd  = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	        // 1) [infNFE]
	        String infNfe = new StringBuilder()
	                .append("[infNFE]\n")
	                .append("versao=").append(ApplicationUtils.getConfiguration("versao.nfe")).append('\n')
	                .toString();

	        // 2) [Identificacao]
	        String identificacao = buildIdentificacao(nota,isNFCEAtivo);

	        // 2.1) [NFRefXXX] via Streams
	        if (!nota.getFinalidadeEmissao().equals(FinalidadeNfe.NO) && nota.getListaChavesReferenciada() != null && !nota.getFinalidadeEmissao().equals(FinalidadeNfe.ND )) {
	            final java.util.concurrent.atomic.AtomicInteger seq = new java.util.concurrent.atomic.AtomicInteger(1);

	            final String referStr = nota.getListaChavesReferenciada().stream()
	                .map(r -> {
	                    int idx = seq.getAndIncrement();
	                    return section("NFRef", idx)
	                            + kv("Tipo", "NFe")
	                            + kv("refNFe", r.getChaveReferenciada())
	                            + kv("cUF", this.destino.getCUF())
	                            + kv("AAMM", r.getAaMM().format(formataAAMM))
	                            + kv("CNPJ", this.destino.getCNPJCPF())
	                            + kv("mod", "01")
	                            + kv("Serie", r.getSerie())
	                            + kv("nNF", r.getNNF());
	                })
	                .collect(java.util.stream.Collectors.joining());
	            this.notaReferencia = this.notaReferencia + referStr;
	        }
	        // 2.2 [gPagAntecipado01] via Streams
//	        if (!nota.getFinalidadeEmissao().equals(FinalidadeNfe.NO) && nota.getListaChavesReferenciada() != null) {
	        if (nota.getFinalidadeEmissao().equals(FinalidadeNfe.ND) && nota.getListaChavesReferenciada() != null) {

	            final String referStr = nota.getListaChavesReferenciada().stream()
	                .map(r -> {
	                    return new StringBuilder()
	        	                .append("[gPagAntecipado01]\n")
	                            + kv("refNFe", r.getChaveReferenciada());
	                            
	                })
	                .collect(java.util.stream.Collectors.joining());
	            this.notaReferencia = this.notaReferencia + referStr;
	        }

	        // 3) [Emitente]
	        String emitente = buildEmitente();

	        // 4) [Destinatario]
	        String destinatario = buildDestinatario(nota);

	        // 5) [autXML1] (se houver CNPJ/CPF do destinatário)
	        String autXml = "";
	        if (this.destino.getCNPJCPF() != null) {
	            autXml = section("autXML", 1) ;
//	            autXml = section("autXML", 1) + kv("CNPJCPF", this.destino.getCNPJCPF());
	        }

	        // 6) [Total]
	        String total = buildTotais(nota, isNFCEAtivo);
	        
	        
	        // 6-1 novos totalizadores para referma tributaria
	        System.out.println("Incicio appendBlocosTotaisReforma");
	        LocalDate dataImplata = LocalDate.of(2026, 1, 1);
	        StringBuilder novosTotais = new StringBuilder();
	        if (!LocalDate.now().isBefore(dataImplata)) {
	        	AcbrComunicaReformaHelper.appendBlocosTotaisReforma(novosTotais, nota);
	        }
	        System.out.println("Fim appendBlocosTotaisReforma");

	        // 7) [Transportador] + [Volume001]
	        String transportador;
	        String volume = "";
	        if (isNFCEAtivo) {
	            // NFC-e (modelo 65): não informar dados de transporte/frete.
	            // modFrete deve ser 9 (sem frete) e não deve existir grupo de volumes/lacres.
	            transportador = new StringBuilder()
	                    .append("[Transportador]\n")
	                    .append(kv("modFrete", "9"))
	                    .toString();
	        } else {
	            transportador = buildTransportador(nota);
	            volume = new StringBuilder()
	                    .append("[Volume001]\n")
	                    .append(kv("qVol", nota.getTransportador().getQuantidade()))
	                    .append(kv("esp", nota.getTransportador().getEspecie()))
	                    .append(kvIfNotNull("Marca", nota.getTransportador().getMarca()))
	                    .append(kvIfNotNull("nVol", nota.getTransportador().getNumeracao()))
	                    .append(kv("pesoL", nota.getTransportador().getPesoLiquido()))
	                    .append(kv("pesoB", nota.getTransportador().getPesoBruto()))
	                    .toString();
	        }


	        // 7.1) [Lacre001XYZ] via Streams
	        String lacre = "";
	        if (!isNFCEAtivo && nota.getListaLacres() != null && !nota.getListaLacres().isEmpty()) {
	            lacre = java.util.stream.IntStream.range(0, nota.getListaLacres().size())
	                    .mapToObj(i -> "[Lacre001" + pad3(i + 1) + "]\n" + kv("nLacre", nota.getListaLacres().get(i).getLacre()))
	                    .collect(java.util.stream.Collectors.joining());
	        }

	        // Fatura (apenas se NÃO for importação)
	        String fatura = "";
	        if (!nota.isImportacao()) {
	            fatura = new StringBuilder()
	                    .append("[Fatura]\n")
	                    .append(kv("nFat", nota.getNumeroNota()))
	                    .append(kv("vOrig", nota.getValorTotalNota().add(nota.getDesconto())))
	                    .append(kv("vDesc", nota.getDesconto()))
	                    .append(kv("vLiq",  nota.getValorTotalNota()))
	                    .toString();
	        }

	        // 8) Pagamento (Streams: agrupamento por TipoPagamento)
	        if (nota.getListaParcelas() != null && !nota.getListaParcelas().isEmpty()) {
	            final java.util.Map<TipoPagamento, java.util.List<Parcelas>> agrupado =
	                    nota.getListaParcelas().stream()
	                            .filter(p -> p.getFormaPag() != null && p.getFormaPag().getTipoPagamento() != null)
	                            .collect(java.util.stream.Collectors.groupingBy(p -> p.getFormaPag().getTipoPagamento()));

	            final java.util.concurrent.atomic.AtomicInteger seqPag = new java.util.concurrent.atomic.AtomicInteger(1);
	            
	            boolean faltaDescOutros = nota.getListaParcelas().stream()
	            	    .filter(p -> p.getFormaPag() != null && p.getFormaPag().getTipoPagamento() != null)
	            	    .filter(p -> "99".equals(p.getFormaPag().getTipoPagamento().getCod()))
	            	    .anyMatch(p -> p.getFormaPag().getDescOutros() == null || p.getFormaPag().getDescOutros().trim().isEmpty());

	            	if (faltaDescOutros) {
	            	    // unchecked: compila dentro e fora de streams
	            	    throw new IllegalStateException("Tipo Pagamento definido como 'Outros (99)'. É obrigatório preencher a descrição em Formas de Pagamento.");
	            	}

	            // Ordena por código do tipo (quando possível) para manter previsibilidade
	            final String pagamentos = agrupado.entrySet().stream()
	                    .sorted((e1, e2) -> safeStr(e1.getKey().getCod()).compareTo(safeStr(e2.getKey().getCod())))
	                    .map(entry -> {
	                        final TipoPagamento tipo = entry.getKey();
	                        final java.util.List<Parcelas> parcelas = entry.getValue();

	                        final java.math.BigDecimal totalparc = parcelas.stream()
	                                .map(Parcelas::getValorParcela)
	                                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

	                        StringBuilder sb = new StringBuilder();
	                        sb.append(section("PAG", seqPag.getAndIncrement()))
	                          .append(kv("tpag", tipo.getCod()));

	                        if ("99".equals(tipo.getCod())) { // Outros
	                            String desc = (parcelas.get(0).getFormaPag() != null) ? parcelas.get(0).getFormaPag().getDescOutros() : null;
	                            sb.append(kv("xPag", desc));
	                        }

	                        if ("90".equals(tipo.getCod())) { // Sem pagamento
	                            sb.append(kv("vPag", "0.00"));
	                        } else {
	                            sb.append(kv("vPag", totalparc.setScale(2, java.math.RoundingMode.HALF_EVEN)));
	                        }

	                        if ("01".equals(tipo.getCod())) { // Dinheiro → vTroco
	                            sb.append(kv("vTroco", "0.00"));
	                        }

	                        if ("03".equals(tipo.getCod()) || "04".equals(tipo.getCod())) { // Cartões
	                            sb.append(kv("tpIntegra", ApplicationUtils.getConfiguration("integra.car")));
	                        }

	                        return sb.toString();
	                    })
	                    .collect(java.util.stream.Collectors.joining());
	            
	            this.pagamentoPreenchido += pagamentos;

	            // Duplicatas (apenas tipos 14/15)
	            final java.util.List<ParcelasNfe> parcelas = nota.getListaParcelas();
	            final java.util.concurrent.atomic.AtomicInteger seqDup = new java.util.concurrent.atomic.AtomicInteger(1);

	            final String duplicatas = parcelas.stream()
	                    .filter(p -> {
	                        String cod = (p.getFormaPag() != null && p.getFormaPag().getTipoPagamento() != null)
	                                ? p.getFormaPag().getTipoPagamento().getCod()
	                                : null;
	                        return "14".equals(cod) || "15".equals(cod);
	                    })
	                    .map(p -> {
	                        int idx = seqDup.getAndIncrement();
	                        StringBuilder sb = new StringBuilder();
	                        sb.append(section("Duplicata", idx));
	                        sb.append(kv("nDup", pad3(p.getNumParcela().intValue())));
	                        sb.append(kv("dVenc", p.getVencimento().format(formataAaaaMmDd)));
	                        sb.append(kv("vDup", p.getValorParcela()));
	                        return sb.toString();
	                    })
	                    .collect(java.util.stream.Collectors.joining());

	            this.pagamentoPreenchido += duplicatas;
	        }

	        // 9) Itens (mantido em loop clássico por complexidade/efeitos colaterais)
	        {
	        	final AtomicInteger contadorPrincipal = new AtomicInteger(1);
	            for (ItemNfe item : nota.getListaItemNfe()) {
	                final boolean regimeNormal = EmissorEnquadradoNormal(this.emissor);
	                final int contador = contadorPrincipal.getAndIncrement();
	                // Produto
	                StringBuilder prod = new StringBuilder();
                    prod.append(section("Produto", contador))
                        .append(kv("cProd", item.getProduto().getReferencia()))
                        .append(kv("cEAN", "SEM GTIN"))
                        .append(kv("xProd", montarDescricaoProduto(item)))
                        .append(kv("NCM",  item.getProduto().getNcm().getNcm()));
                    if (item.getProduto().getNcm().getListaNVE() != null
                            && !item.getProduto().getNcm().getListaNVE().isEmpty()) {
                    final AtomicInteger seqNVE = new AtomicInteger(1);
                    
                    List<NVE> listaNVE = item.getProduto().getNcm().getListaNVE();
    	            final String referStr =listaNVE.stream()
    	                .map((NVE r) -> {
    	                    int idx = seqNVE.getAndIncrement();
    	                    return section("NVE" + pad3(contador),idx)
    	                            + kv("NVE", r.getNVE());
    	                }).collect(java.util.stream.Collectors.joining());
    	            	prod.append(referStr);
                    }
                    if (item.getProduto().getNcm().getExTipi() != null) {
                        prod.append(kv("EXTIPI", item.getProduto().getNcm().getExTipi()));
                    }

                    prod.append(kv("CFOP", item.getCfopItem().getCfop()))
                        .append(kv("uCom", item.getProduto().getTipoMedida() == null ? "UN" : item.getProduto().getTipoMedida().getSigla()))
                        .append(kv("qCom", item.getQuantidade()))
                        .append(kv("vUnCom", item.getValorUnitario()))
                        .append(kv("vProd", item.getValorTotalBruto()))
                        .append(kv("cEANTrib", "SEM GTIN"))
                        .append(kv("uTrib", item.getProduto().getTipoMedida() == null ? "UN" : item.getProduto().getTipoMedida().getSigla()))
                        .append(kv("qTrib", item.getQuantidade()))
                        .append(kv("vUnTrib", item.getValorUnitario()))
                        .append(kv("vFrete", (isNFCEAtivo ? java.math.BigDecimal.ZERO : item.getValorFrete())))
                        .append(kv("vSeg", (isNFCEAtivo ? java.math.BigDecimal.ZERO : item.getValorSeguro())))
                        .append(kv("vDesc", item.getDesconto()))
                        .append(kv("vOutro", item.getValorDespesas()))
                        .append(kv("indTot", "1"))
                        .append(kv("vTotTrib", item.getValorTotalTributoItem()))
                        .append(kv("vItem", item.getValorTotal()));

                    if (item.getProduto().getNcm().getTpCredPressIBSZFM() != null) {
                    	prod.append(kv("tpCredPresIBSZFM",item.getProduto().getNcm().getTpCredPressIBSZFM().getCodigo()));
                    }			
                    if (item.getProduto().isBemMovelUsado()) {
                    	prod.append(kv("indBemMovelUsado","1"));
                    }
                    if (item.getObsItem() != null) {
                        prod.append(kv("infAdProd", item.getObsItem()));
                    }
                    
	                if (regimeNormal) {
	                    
	                    // Importação (DI/LADI/II) quando houver
	                    if (nota.isImportacao() && nota.getDi() != null) {
	                        prod.append(section("DI" + pad3(contador), 1)) // "[DI00X001]"
	                            .append(kv("nDi",        nota.getDi().getNnDi()))
	                            .append(kv("dDi",        nota.getDi().getDDi().format(this.formatoSimplesDate)))
	                            .append(kv("xLocDesemb", nota.getDi().getXLocDesemb()))
	                            .append(kv("UFDesemb",   nota.getDi().getUFDesemb()))
	                            .append(kv("dDesemb",    nota.getDi().getDdDesemb().format(this.formatoSimplesDate)))
	                            .append(kv("tpViaTransp",nota.getDi().getTpViaTransp().getCodigo()))
	                            .append(kv("vAFRMM",     nota.getDi().getVAFRMM()))
	                            .append(kv("tpIntermedio", nota.getDi().getTpIntermedio().getCodigo()))
	                            .append(kv("CNPJ",       nota.getDi().getCnpjAdq()))
	                            .append(kv("UFTerceiro", nota.getDi().getUFTerceiro()))
	                            .append(kv("cExportador",nota.getDi().getCExportador()));

	                        // Adição (LADI)
	                        prod.append("[LADI").append(pad3(contador)).append("001001]\n")
	                            .append(kv("nAdicao",      nota.getDi().getListaAdicao().get(contador - 1).getNAdicao()))
	                            .append(kv("nSeqAdi",      nota.getDi().getListaAdicao().get(contador - 1).getNSeqAdic()))
	                            .append(kv("cFabricante",  nota.getDi().getListaAdicao().get(contador - 1).getCFabricante()))
	                            .append(kv("vDescDI",      nota.getDi().getListaAdicao().get(contador - 1).getVDescDI()))
	                            .append(kv("nDraw",        nota.getDi().getListaAdicao().get(contador - 1).getNDraw()));

	                        // Imposto II
	                        prod.append(section("II", contador))
	                            .append(kv("vBC",       item.getIi().getVBC()))
	                            .append(kv("vDespAdu",  item.getIi().getVDespAdu()))
	                            .append(kv("vII",       item.getIi().getVII()))
	                            .append(kv("vIOF",      item.getIi().getVIOF()));
	                    }

	                    // ICMS
	                    prod.append(section("ICMS", contador))
	                        .append(kv("orig", item.getOrigem()))
	                        .append(kv("CST",  item.getCst()));
	                    prod.append(buildICMSNormalBloco(nota, item));

	                    // DIFAL (ICMSUFDest) quando aplicável
	                    if (EmissorEnquadradoNormal(this.emissor)
	                            && "9".equals(this.destino.getIndIEDest())
	                            && !nota.isImportacao()) {
	                        prod.append(section("ICMSUFDest", contador))
	                            .append(kv("vBCUFDest",     item.getBaseICMS()))
	                            .append(kv("vBCFCPUFDest",  item.getVBCUFDest()))
	                            .append(kv("pFCPUFDest",    item.getPFCPUFDest()))
	                            .append(kv("pICMSUFDest",   item.getPICMSUFDest()))
	                            .append(kv("pICMSInter",    item.getPICMSInter()))
	                            .append(kv("pICMSInterPart",item.getPICMSInterPart()))
	                            .append(kv("vFCPUFDest",    item.getVFCPUFDest()))
	                            .append(kv("vICMSUFDest",   item.getVICMSUFDest()))
	                            .append(kv("vICMSUFRemet",  item.getVICMSUFRemet()));
	                    }

	                    // IPI
	                    prod.append(buildIPI(contador, item));

	                    // PIS/COFINS
	                    prod.append(buildPIS(contador, item));
	                    prod.append(buildCOFINS(contador, item));
	                    
//	                    //IS
//	                    prod.append(section("IS",contador))
//	                    	.append(kv("CSTIS","000"))
//	                    	.append(kv("cClassTribIS","000001"));
//	                    	.append(kv("vBCIS",""))
//	                    	.append(kv("pIS",""))
//	                    	.append(kv("pISEspec",""))
//	                    	.append(kv("uTrib",""))
//	                    	.append(kv("qTrib",""))
//	                    	.append(kv("vIS",""));
	                    
	                    // IBS CBS
//	                    prod.append(section("IBSCBS",contador))
//	                    	.append(kv("CST",item.getCclassTrib().getCstIbsCbs()))
//	                    	.append(kv("cClassTrib",item.getCclassTrib().getCClassTrib()))
//	                    	.append(kv("indDoacao",item.getCclassTrib().getCstIbsCbs()))
//	                    	;
//	                    prod.append(section("gIBSCBS",contador))
//                    	.append(kv("vBC",item.getVbcCbs()))
//                    	.append(kv("vIBS",item.getCclassTrib().getCstIbsCbs()));
//	                    
//	                    prod.append(section("gIBSUF",contador))
//                    	.append(kv("pIBSUF",item.getPIbs()))
//                    	.append(kv("vIBSUF",item.getVIbs()));
//                    	.append(kv("pDif",item.getp))
//                    	.append(kv("vDif",item.getCclassTrib().getCstIbsCbs()))
//                    	.append(kv("vDevTrib",item.getCclassTrib().getCstIbsCbs()))
//                    	.append(kv("pRedAliq",item.getCclassTrib().getCstIbsCbs()))
//                    	.append(kv("pAliqEfet",item.getCclassTrib().getCstIbsCbs()));
	                    System.out.println("Incicio appendBlocosReformaItem");
	        	        if (!LocalDate.now().isBefore(dataImplata)) {
	                    	AcbrComunicaReformaHelper.appendBlocosReformaItem(prod, item, contador);
	                    }
		                System.out.println("Fim appendBlocosReformaItem");

	                    this.produtoPreenchido += prod.toString();
	                } else {
	                    // SIMPLES NACIONAL
	                    this.isSimples = true;


	                    if (item.getObsItem() != null) {
	                        prod.append(kv("infAdProd", item.getObsItem()));
	                    }

	                    // ICMS/CSOSN (Simples)
	                    prod.append(section("ICMS", contador))
	                        .append(kv("orig",  item.getOrigem()))
	                        .append(kv("CSOSN", item.getCst()));

	                    // Tratatativas por CSOSN
	                    final String csosn = item.getCst();
	                    if ("101".equals(csosn)) {
	                        this.is101 = true;
	                        prod.append(kv("pCredSN", emissor.getBaseReducao()))
	                            .append(kv("vCredICMSSN", item.getValorIcms()));
	                    } else if ("201".equals(csosn)) {
	                        this.is101 = true;
	                        prod.append(kv("modBCST", item.getTributo().getIcmsSt().getModICMSST().getCod()))
	                            .append(kv("pMVAST", item.getMvaSt()))
	                            .append(kv("vBCST",  item.getBaseICMSSt()))
	                            .append(kv("pICMSST",item.getAliqIcmsSt()))
	                            .append(kv("vICMSST",item.getValorIcmsSt()))
	                            .append(kv("pCredSN", emissor.getBaseReducao()))
	                            .append(kv("vCredICMSSN", item.getValorIcms()));
	                        if (!"2".equals(destino.getIndIEDest())) {
	                            if (item.getPFCP().compareTo(java.math.BigDecimal.ZERO) == 0) {
	                                prod.append(kv("vBCFCPST", "0"))
	                                    .append(kv("pFCPST", item.getPFCP()))
	                                    .append(kv("vFCPST", item.getVFCP()))
	                                    .append(kv("infAdProd", "vBCFCP=" + item.getBaseICMSSt() + "pFCP=" + item.getPFCP() + "vFCP=" + item.getVFCP()));
	                            } else {
	                                prod.append(kv("vBCFCPST", item.getBaseICMSSt()))
	                                    .append(kv("pFCPST", item.getPFCP()))
	                                    .append(kv("vFCPST", item.getVFCP()))
	                                    .append(kv("infAdProd", "vBCFCP=" + item.getBaseICMSSt() + "pFCP=" + item.getPFCP() + "vFCP=" + item.getVFCP()));
	                            }
	                        }
	                    } else if ("202".equals(csosn) || "203".equals(csosn)) {
	                        prod.append(kv("modBCST", item.getTributo().getIcmsSt().getModICMSST().getCod()))
	                            .append(kv("pMVAST", item.getMvaSt()))
	                            .append(kv("vBCST",  item.getBaseICMSSt()))
	                            .append(kv("pICMSST",item.getAliqIcmsSt()))
	                            .append(kv("vICMSST",item.getValorIcmsSt()));
	                        if (!"2".equals(destino.getIndIEDest())) {
	                            if (item.getPFCP().compareTo(java.math.BigDecimal.ZERO) == 0) {
	                                prod.append(kv("vBCFCPST", "0"))
	                                    .append(kv("pFCPST", item.getPFCP()))
	                                    .append(kv("vFCPST", item.getVFCP()))
	                                    .append(kv("infAdProd", "vBCFCP=" + item.getBaseICMSSt() + "pFCP=" + item.getPFCP() + "vFCP=" + item.getVFCP()));
	                            } else {
	                                prod.append(kv("vBCFCPST", item.getBaseICMSSt()))
	                                    .append(kv("pFCPST", item.getPFCP()))
	                                    .append(kv("vFCPST", item.getVFCP()))
	                                    .append(kv("infAdProd", "vBCFCP=" + item.getBaseICMSSt() + "pFCP=" + item.getPFCP() + "vFCP=" + item.getVFCP()));
	                            }
	                        }
	                    } else if ("500".equals(csosn)) {
	                        prod.append(kv("pRedBCST", "0"))
	                            .append(kv("pST", "0"))
	                            .append(kv("vICMSSTRet", "0"))
	                            .append(kv("vBCFCPSTRet", "0"))
	                            .append(kv("pFCPSTRet", "0"))
	                            .append(kv("vFCPSTRet", "0"));
	                    } else if ("900".equals(csosn)) {
	                        if (this.destino.getRegime().equals(Enquadramento.Normal) && nota.getFinalidadeEmissao().equals(FinalidadeNfe.DV)) {
	                            this.is101 = true;
	                            prod.append(kv("modBC", item.getTributo().getIcms().getModICMSNormal().getCod()))
	                                .append(kv("pRedBC", destino.getReducaoBase()))
	                                .append(kv("vBC", item.getBaseICMS()))
	                                .append(kv("pICMS", item.getAliqIcms()))
	                                .append(kv("vICMS", item.getValorIcms()));

	                            if (item.isItemST()) {
	                                prod.append(kv("modBCST", item.getTributo().getIcmsSt().getModICMSST().getCod()))
	                                    .append(kv("pMVAST", item.getMvaSt()))
	                                    .append(kv("vBCST",  item.getBaseICMSSt()))
	                                    .append(kv("pICMSST",item.getAliqIcmsSt()))
	                                    .append(kv("vICMSST",item.getValorIcmsSt()));
	                            }
	                        } else if (this.destino.getRegime().equals(Enquadramento.Normal)) {
	                            this.is101 = true;
	                            prod.append(kv("modBC", item.getTributo().getIcms().getModICMSNormal().getCod()))
	                                .append(kv("pRedBC", emissor.getBaseReducao()))
	                                .append(kv("vBC", item.getBaseICMS()))
	                                .append(kv("pICMS", item.getAliqIcms()))
	                                .append(kv("vICMS", item.getValorIcms()))
	                                .append(kv("pCredSN", emissor.getBaseReducao()))
	                                .append(kv("vCredICMSSN", item.getValorIcms()));
	                            if (item.isItemST()) {
	                                prod.append(kv("modBCST", item.getTributo().getIcmsSt().getModICMSST().getCod()))
	                                    .append(kv("pMVAST", item.getMvaSt()))
	                                    .append(kv("vBCST",  item.getBaseICMSSt()))
	                                    .append(kv("pICMSST",item.getAliqIcmsSt()))
	                                    .append(kv("vICMSST",item.getValorIcmsSt()))
	                                    .append(kv("pCredSN", emissor.getBaseReducao()))
	                                    .append(kv("vCredICMSSN", item.getValorIcms()));
	                            }
	                        } else {
	                            this.is101 = false;
	                            prod.append(kv("pCredSN", emissor.getBaseReducao()))
	                                .append(kv("vCredICMSSN", item.getValorIcms()));
	                            if (item.isItemST()) {
	                                prod.append(kv("modBCST", item.getTributo().getIcmsSt().getModICMSST().getCod()))
	                                    .append(kv("pMVAST", item.getMvaSt()))
	                                    .append(kv("vBCST",  item.getBaseICMSSt()))
	                                    .append(kv("pICMSST",item.getAliqIcmsSt()))
	                                    .append(kv("vICMSST",item.getValorIcmsSt()));
	                            }
	                        }
	                    }

	                    // IPI
	                    prod.append(buildIPI(contador, item));

	                    // PIS/COFINS
	                    prod.append(buildPIS(contador, item));
	                    prod.append(buildCOFINS(contador, item));
	                    
//	                    liberar somente em 2027!!!!
	                 // gera o ini dos campos ibs/cbs/is e seus subgrupos dos itens 
//		            	System.out.println("Incicio appendBlocosReformaItem");
//		                AcbrComunicaReformaHelper.appendBlocosReformaItem(prod, item, contador);
//		                System.out.println("Fim appendBlocosReformaItem");
	                    this.produtoPreenchido += prod.toString();
	                }
//	                contador++;
	            }
	        }
	        // Mensagens adicionais
	        if (nota.getDadosAdicionais() != null) {
	            this.infEmitente = this.infEmitente + nota.getDadosAdicionais();
	        }
	        if (this.isSimples && this.is101 && !nota.getFinalidadeEmissao().equals(FinalidadeNfe.DV)) {
	            this.infFisco = this.menInt.getSimplesNacional()
	                    + this.menInt.getMensagem101(calcula.geraIcms(nota.getValorTotalNota(), new java.math.BigDecimal(emissor.getBaseReducao())).toString(), emissor.getBaseReducao())
	                    + this.infFisco;
	        }
	        if (this.isSimples && this.is101 && nota.getFinalidadeEmissao().equals(FinalidadeNfe.DV)) {
	            this.infFisco = this.menInt.getSimplesNacional();
	        }
	        if (this.isSimples && !this.is101 && !nota.getFinalidadeEmissao().equals(FinalidadeNfe.DV)) {
	            this.infFisco = this.menInt.getSimplesNacional() + this.menInt.getMensagem102() + this.infFisco;
	        }
	        if (this.isSimples && !this.is101 && nota.getFinalidadeEmissao().equals(FinalidadeNfe.DV)) {
	            this.infFisco = this.menInt.getSimplesNacional();
	        }
	        if (!this.isSimples && this.destino.isPermiteReducao()) {
	            this.infFisco = this.menInt.getReducaoBaseICMS();
	        }
	        if (!this.isSimples && this.is50) {
	            this.infFisco = this.menInt.getCst50();
	        }
	        if (!nota.isImportacao()) {
	            this.infEmitente = this.menInt.getTotalTributos(nota.getValorTotalTributos().toString()) + " | " + this.infEmitente;
	        }

	        String adicionais = "[DadosAdicionais]\n";
	        if (!nz(this.infFisco).isEmpty()) {
	            adicionais += kv("infAdFisco", removerAcentos(this.infFisco));
	        }
	        if (this.infEmitente != null) {
	            adicionais += kv("infCpl", removerAcentos(this.infEmitente));
	        }

	        // Montagem final
	        String infArquivo;
	        if (!nota.getFinalidadeEmissao().equals(FinalidadeNfe.NO)) {
	            infArquivo = infNfe + identificacao + emitente + destinatario + this.notaReferencia + autXml + this.produtoPreenchido + total +novosTotais +transportador + volume + lacre + this.pagamentoPreenchido + adicionais;
	        } else {
	            infArquivo = infNfe + identificacao + emitente + destinatario + autXml + this.produtoPreenchido + total +novosTotais +transportador + volume + lacre + fatura + this.pagamentoPreenchido + adicionais;
	        }

	        System.out.println("finalizado a criação do arquivo na pasta c:\\ibrcomp\\tmp\\" + nomeArquivo);
	        criaConexao(dados);
	        return comandoAcbr("ACBr.SaveToFile(\"C:\\ibrcomp\\tmp\\" + nomeArquivo + ".ini\",\"" + infArquivo + "\",5)");

	    } catch (HibernateException h) {
	        fechaTudo();
	        System.out.println("Erro consulta hibernate: " + h.getMessage() + " motivo: " + h.toString());
	        return h.getMessage();
	    } catch (Exception e) {
	        fechaTudo();
	        System.out.println("Erro: tentando conectar com o ACBrMonitor. Contate o suporte técnico: \n\n" + e.getMessage() + " campo: " + e.toString());
	        return e.getMessage();
	    }
	}

	/* ======================= HELPERS (Java 8) ======================= */

	private static String nz(String s) { return s == null ? "" : s; }
	private static String safeStr(String s) { return s == null ? "" : s; }

	private static java.math.BigDecimal nzbd(java.math.BigDecimal v) {
		return (v != null ? v : java.math.BigDecimal.ZERO);
	}

	private static java.math.BigDecimal scale2(java.math.BigDecimal v) {
		return nzbd(v).setScale(2, java.math.RoundingMode.HALF_EVEN);
	}

	private static java.math.BigDecimal scale2Nullable(java.math.BigDecimal v) {
		return (v != null ? v.setScale(2, java.math.RoundingMode.HALF_EVEN) : null);
	}

	private static boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}

	private static String firstNonBlank(String... values) {
		if (values == null) return null;
		for (String v : values) {
			if (!isBlank(v)) return v.trim();
		}
		return null;
	}

	private static Object tryInvoke(Object target, String methodName) {
		if (target == null || isBlank(methodName)) return null;
		try {
			java.lang.reflect.Method m = target.getClass().getMethod(methodName);
			m.setAccessible(true);
			return m.invoke(target);
		} catch (Exception e) {
			return null;
		}
	}

	private static String tryInvokeStr(Object target, String methodName) {
		Object o = tryInvoke(target, methodName);
		return (o != null ? String.valueOf(o) : null);
	}

	private static String pad3(int n) {
	    if (n < 10) return "00" + n;
	    if (n < 100) return "0" + n;
	    return String.valueOf(n);
	}

	private static String section(String name, int idx) {
		// 1) section("PAG", 1)  -> [PAG001]
				// 2) section("PAG%03d", 1) -> [PAG001]
				String secName;
				if (name != null && name.contains("%")) {
					try {
						secName = String.format(name, idx);
					} catch (Exception e) {
						secName = name + pad3(idx);
					}
				} else {
					secName = name + pad3(idx);
				}
				return "[" + secName + "]\n";

	}
	
	private static String section(String name) {
	    return "[" + name + "]\n";
	}

	private static String kv(String k, Object v) {
	    return String.valueOf(k) + "=" + (v == null ? "" : String.valueOf(v)) + "\n";
	}

	private static String kvIfNotNull(String k, Object v) {
	    return (v == null ? "" : kv(k, v));
	}

	private boolean EmissorEnquadradoNormal(Emissor em) {
	    return em.getRegime().equals(Enquadramento.Normal);
	}

	private String montarDescricaoProduto(ItemNfe item) {
	    String base = removerAcentos(item.getProduto().getDescricao());
	    if (item.getBarras() != null) {
	        if (item.getBarras().getCor() != null) {
	            return base + " tam: " + item.getBarras().getTamanho().getTamanho() + " cor: " + item.getBarras().getCor().getNome();
	        } else if (item.getBarras().getTamanho() != null) {
	            return base + " tam: " + item.getBarras().getTamanho().getTamanho();
	        }
	    }
	    return base;
	}
	

	private String buildIdentificacao(Nfe nota,boolean isNFCE) {
	    StringBuilder sb = new StringBuilder();
	    sb.append("[Identificacao]\n")
	      .append(kv("cNF", nota.getNumeroNota() + "32"))
	      .append(kv("natOp", removerAcentos(nota.getNatOperacao().getDescricao())))
	      .append(kv("cUF",emissor.getUf().getIbgeUf()));
	    if (isNFCE) {
	    	sb.append(kv("mod", "65"));
	    }else {
	    	sb.append(kv("mod", "55"));
	    }

	    String serie = this.emissor.getSerie();
	    if (serie != null) {
	        if (serie.length() == 1)      sb.append(kv("serie", "00" + serie));
	        else if (serie.length() == 2) sb.append(kv("serie", "0" + serie));
	        else                          sb.append(kv("serie", serie));
	    }

	    sb.append(kv("nNF", nota.getNumeroNota()))
	    .append(kv("dhEmi", nota.getDataEmissao().format(this.formatoSimples)));
	    if (!isNFCE) {
	    	sb.append(kv("dhSaiEnt", nota.getDataSaida().format(this.formatoSimples)));
	    }
	    sb.append(kv("tpNF", nota.getNatOperacao().getTipoNF().getCodigo()))
	    .append(kv("idDest", defineIdDest(nota)))
	    .append(kv("tpImp", this.emissor.getTpImp()))
	    .append(kv("tpEmis", "1"))
	    .append(kv("finNFe", nota.getFinalidadeEmissao().getCodigo()))
	    .append(kv("indFinal", nota.getIndFinal()))
	    .append(kv("procEmi", "0"));
//	    .append(kv("finNFe", nota.getNatOperacao().getFinalidadeEmissao().getCodigo()))
	    //	      .append(kv("cMunFG", this.emissor.getCMunFG()))
	    if (nota.getNatOperacao().getTpNFCredito() != null ) {
	      sb.append(kv("tpNFCredito",nota.getNatOperacao().getTpNFCredito().getCod()));
	    }
	    if (nota.getNatOperacao().getTpNFDebito() != null ) {
	      sb.append(kv("tpNFDebito",nota.getNatOperacao().getTpNFDebito().getCod()));
	    }	  
	    sb.append(kv("verProc", ApplicationUtils.getConfiguration("application.version")));

	    if (nota.isTipoOperacao()){
	        sb.append(kv("indPres", "1")); // venda presencial
//	        if ("5".equals(nota.getIndpres()){  
//	        Criar campo getIndPres 
//	        0=Não se aplica (por exemplo, Nota Fiscal complementar ou de ajuste);
//	        1=Operação presencial;
//	        2=Operação não presencial, pela Internet;
//	        3=Operação não presencial, Teleatendimento;
//	        4=NFC-e em operação com entrega a domicílio;
//	        5=Operação presencial, fora do estabelecimento; (incluído NT 2016/002)
//	        9=Operação não presencial, outros. 
	        
//	        	sb.append(kv("cMunFGIBS", nota.getIbgeMunEmissao()));
	        
//	        Criar campo em nfe ibgeMunEmissao
//	        que armazena o codigo ibge do municipio em que foi emitido a nfe (venda presencial fora do estabelecimento)
//	        }
	    } else {
	        sb.append(kv("indPres", "2")); // consumidor final
	    }
	    return sb.toString();
	}
	
	private String buildEmitente() {
	    StringBuilder e = new StringBuilder();
	    e.append("[Emitente]\n")
	     .append(kv("CNPJCPF", this.emissor.getCnpjCpf()))
	     .append(kv("xNome", this.emissor.getXNome()));

	    if (this.emissor.getXFant() != null) e.append(kv("xFant", this.emissor.getXFant()));

	    e.append(kv("IE", this.emissor.getIe()));
	    if (this.emissor.getIeST() != null) e.append(kv("IEST", this.emissor.getIeST()));
	    if (this.emissor.getIMunicipal() != null) e.append(kv("IM", this.emissor.getIMunicipal()));

	    e.append(kv("CRT", this.emissor.getCrt()))
	     .append(kv("xLgr", this.emissor.getXLgr()))
	     .append(kv("nro", this.emissor.getNro()));

	    if (this.emissor.getXCpl() != null) e.append(kv("xCpl", this.emissor.getXCpl()));

	    e.append(kv("xBairro", this.emissor.getXBairro()))
	     .append(kv("cMun", this.emissor.getCMun()))
	     .append(kv("xMun", this.emissor.getXMun()))
	     .append(kv("UF", this.emissor.getUf().name()))
	     .append(kv("CEP", this.emissor.getCep()))
	     .append(kv("cPais", this.emissor.getCPais()))
	     .append(kv("xPais", this.emissor.getXPais()));

	    if (this.emissor.getFone() != null) e.append(kv("Fone", this.emissor.getFone()));
	    return e.toString();
	}

	private String buildDestinatario(Nfe nota) {
	    StringBuilder d = new StringBuilder();
	    d.append("[Destinatario]\n");

	    if (nota.isImportacao()) {
	        if (this.destino.getIdEstrangeiro() != null) {
	            d.append(kv("idEstrangeiro", this.destino.getIdEstrangeiro()));
	        } else {
	            d.append(kv("idEstrangeiro", "0"));
	        }
	    } else {
	        if (this.destino.getIdEstrangeiro() != null) {
	            d.append(kv("idEstrangeiro", this.destino.getIdEstrangeiro()));
	        } else {
	            d.append(kv("CNPJCPF", this.destino.getCNPJCPF()));
	        }
	    }

	    d.append(kv("xNome", this.destino.getXNome()))
	     .append(kv("indIEDest", this.destino.getIndIEDest()));

	    if (this.destino.getIE() != null)   d.append(kv("IE",   this.destino.getIE()));
	    if (this.destino.getISUF() != null) d.append(kv("ISUF", this.destino.getISUF()));
	    if (this.destino.getEmail() != null)d.append(kv("Email",this.destino.getEmail()));

	    d.append(kv("xLgr", this.destino.getXLgr()))
	     .append(kv("nro",  this.destino.getNro()));
	    if (this.destino.getXCpl() != null) d.append(kv("xCpl", this.destino.getXCpl()));

	    d.append(kv("xBairro", this.destino.getXBairro()))
	     .append(kv("cMun",    this.destino.getCMun()))
	     .append(kv("xMun",    this.destino.getXMun()))
	     .append(kv("UF",      this.destino.getUf()))
	     .append(kv("CEP",     this.destino.getCep()))
	     .append(kv("cPais",   this.destino.getCPais()))
	     .append(kv("xPais",   this.destino.getXPais()));

	    if (this.destino.getFone() != null) d.append(kv("Fone", this.destino.getFone()));
	    return d.toString();
	}

	private String buildTotais(Nfe nota, boolean isNFCE) {
	    StringBuilder t = new StringBuilder();
	    t.append("[Total]\n");

	    if ((emissor.getRegime().equals(Enquadramento.SimplesNacional) || emissor.getRegime().equals(Enquadramento.SimplesNacionalMei))
	        && !nota.getFinalidadeEmissao().equals(FinalidadeNfe.DV)) {
	        t.append(kv("vBC", "0.00")).append(kv("vICMS", "0.00"));
	    } else if (nota.getFinalidadeEmissao().equals(FinalidadeNfe.DV) || emissor.getRegime().equals(Enquadramento.Normal)) {
	        t.append(kv("vBC",  nota.getBaseIcms().setScale(2, java.math.RoundingMode.HALF_EVEN)))
	         .append(kv("vICMS",nota.getValorIcms().setScale(2, java.math.RoundingMode.HALF_EVEN)));
	    }

	    t.append(kv("vICMSDeson", nota.getValorIcmsDesonerado().setScale(2, java.math.RoundingMode.HALF_EVEN)))
	     .append(kv("vBCST",      nota.getBaseIcmsSubstituicao().setScale(2, java.math.RoundingMode.HALF_EVEN)))
	     .append(kv("vST",        nota.getValorIcmsSubstituicao().setScale(2, java.math.RoundingMode.HALF_EVEN)))
	     .append(kv("vProd",      nota.getValorTotalProdutos().setScale(2, java.math.RoundingMode.HALF_EVEN)))
	     .append(kv("vFrete",     (isNFCE ? java.math.BigDecimal.ZERO : nota.getValorFrete()).setScale(2, java.math.RoundingMode.HALF_EVEN)))
	     .append(kv("vSeg",       (isNFCE ? java.math.BigDecimal.ZERO : nota.getValorSeguro()).setScale(2, java.math.RoundingMode.HALF_EVEN)))
	     .append(kv("vDesc",      nota.getDesconto().setScale(2, java.math.RoundingMode.HALF_EVEN)));

	    if (nota.isImportacao()) {
	        t.append(kv("vII", nota.getValorTotalII()));
	    }

	    t.append(kv("vIPI",    nota.getValorTotalIpi().setScale(2, java.math.RoundingMode.HALF_EVEN)))
	     .append(kv("vPIS",    nota.getValorTotalPis().setScale(2, java.math.RoundingMode.HALF_EVEN)))
	     .append(kv("vCOFINS", nota.getValorTotalCofins().setScale(2, java.math.RoundingMode.HALF_EVEN)))
	     .append(kv("vOutro",  nota.getOutrasDespesas().setScale(2, java.math.RoundingMode.HALF_EVEN)))
	     .append(kv("vNF",     nota.getValorTotalNota().setScale(2, java.math.RoundingMode.HALF_EVEN)))
	     .append(kv("vNFTot", nota.getVNFTo().setScale(2, java.math.RoundingMode.HALF_EVEN)))
	     .append(kv("vTotTrib",nota.getValorTotalTributos().setScale(2, java.math.RoundingMode.HALF_EVEN)));

	    if ("9".equals(destino.getIndIEDest())) {
	        t.append(kv("vFCPUFDest",  nota.getVFCPUFDest().setScale(2, java.math.RoundingMode.HALF_EVEN)))
	         .append(kv("vICMSUFDest", nota.getVICMSUFDest().setScale(2, java.math.RoundingMode.HALF_EVEN)))
	         .append(kv("vICMSUFRemet",nota.getVICMSUFRemet().setScale(2, java.math.RoundingMode.HALF_EVEN)));
	    }

	    if (nota.getVFCP().compareTo(new java.math.BigDecimal("0")) > 0) {
	        t.append(kv("vFCP",      nota.getVFCP().setScale(2, java.math.RoundingMode.HALF_EVEN)))
	         .append(kv("vFCPST",    nota.getVFCPST().setScale(2, java.math.RoundingMode.HALF_EVEN)))
	         .append(kv("vFCPSTRet", nota.getVFCPSTRet().setScale(2, java.math.RoundingMode.HALF_EVEN)));

	        this.infFisco = this.infFisco + "vFCP=" + nota.getVFCP().setScale(2, java.math.RoundingMode.HALF_EVEN)
	                + "vFCPST=" + nota.getVFCPST().setScale(2, java.math.RoundingMode.HALF_EVEN)
	                + "vFCPSTRet=" + nota.getVFCPSTRet().setScale(2, java.math.RoundingMode.HALF_EVEN);
	    }
	    return t.toString();
	}

	private String buildTransportador(Nfe nota) {
	    StringBuilder tr = new StringBuilder();
	    tr.append("[Transportador]\n")
	      .append(kv("modFrete", nota.getTipoFrete().getCodigo()));

	    if (!nota.isClienteRetira()) {
	        if (nota.getTransportador().getTransportadora().getCnpj() == null) {
	            tr.append(kv("CNPJCPF", nota.getTransportador().getTransportadora().getCpf()));
	        } else {
	            tr.append(kv("CNPJCPF", nota.getTransportador().getTransportadora().getCnpj()));
	        }

	        tr.append(kv("xNome", removerAcentos(nota.getTransportador().getTransportadora().getRazaoSocial())))
	          .append(kv("IE", nota.getTransportador().getTransportadora().getInscEstadual()));

	        String logradouro = nota.getTransportador().getTransportadora().getEndereco().getLogradouro();
	        if (logradouro == null || logradouro.isEmpty()) {
	            tr.append(kv("xEnder", removerAcentos(nota.getTransportador().getTransportadora().getEndereco().getEndereco().getLogra())));
	        } else {
	            tr.append(kv("xEnder", removerAcentos(logradouro)));
	        }

	        tr.append(kv("xMun", removerAcentos(nota.getTransportador().getTransportadora().getEndereco().getEndereco().getLocalidade())))
	          .append(kv("UF", nota.getTransportador().getTransportadora().getEndereco().getEndereco().getUf().name()));
	    } else {
	        tr.append(kv("CNPJCPF", nota.getTransportador().getRetiraDoc()))
	          .append(kv("xNome",   removerAcentos(nota.getTransportador().getRetiraNome())))
	          .append(kv("IE",      nota.getTransportador().getRetiraInsc()))
	          .append(kv("xEnder",  removerAcentos(nota.getTransportador().getRetiraEnd())))
	          .append(kv("xMun",    removerAcentos(nota.getTransportador().getRetiraMunicipio())))
	          .append(kv("UF",      nota.getTransportador().getRetiraUf()));
	    }

	    if (nota.getTransportador().getPlaca() != null) {
	        tr.append(kv("Placa",   nota.getTransportador().getPlaca().toUpperCase()))
	          .append(kv("UFPlaca", nota.getTransportador().getUfPlaca().name()));
	    }
	    if (nota.getTransportador().getCodigoAnnt() != null) tr.append(kv("RNTC", nota.getTransportador().getCodigoAnnt()));
	    if (nota.getTransportador().getVagao() != null)      tr.append(kv("vagao", nota.getTransportador().getVagao()));
	    if (nota.getTransportador().getBalsa() != null)      tr.append(kv("balsa", nota.getTransportador().getBalsa()));
	    return tr.toString();
	}
	
//	private String buildIS(Nfe nota, ItemNfe item) {
//		StringBuilder is = new StringBuilder();
//		is.append("[Transportador]\n")
//	    String cstIS = item.getCstIS();
//	}

	private String buildICMSNormalBloco(Nfe nota, ItemNfe item) {
	    StringBuilder sb = new StringBuilder();
	    String cst = item.getCst();

	    if (nota.isImportacao()) {
	        if ("00".equals(cst)) {
	            sb.append(kv("modBC", item.getTributo().getIcms().getModICMSNormal().getCod()))
	              .append(kv("vBC",   item.getBaseICMS()))
	              .append(kv("pICMS", item.getAliqIcms()))
	              .append(kv("vICMS", item.getValorIcms()));

	            if (item.getPFCP().compareTo(java.math.BigDecimal.ZERO) > 0) {
	                sb.append(kv("pFCP", item.getPFCP()))
	                  .append(kv("vFCP", item.getVFCP()))
	                  .append(kv("infAdProd", "pFCP=" + item.getPFCP() + "vFCP=" + item.getVFCP()));
	            }
	        }
	    } else {
	        if ("00".equals(cst)) {
	            sb.append(kv("modBC", item.getTributo().getIcms().getModICMSNormal().getCod()))
	              .append(kv("vBC",   item.getBaseICMS()))
	              .append(kv("pICMS", item.getAliqIcms()))
	              .append(kv("vICMS", item.getValorIcms()));

	            if (item.getPFCP().compareTo(java.math.BigDecimal.ZERO) > 0) {
	                sb.append(kv("pFCP", item.getPFCP()))
	                  .append(kv("vFCP", item.getVFCP()))
	                  .append(kv("infAdProd", "pFCP=" + item.getPFCP() + "vFCP=" + item.getVFCP()));
	            }
	        }
	    }

	    if ("10".equals(cst)) {
	        sb.append(kv("modBC",   item.getTributo().getIcmsSt().getModICMS().getCod()))
	          .append(kv("vBC",     item.getBaseICMS()))
	          .append(kv("pICMS",   item.getAliqIcms()))
	          .append(kv("vICMS",   item.getValorIcms()))
	          .append(kv("modBCST", item.getTributo().getIcmsSt().getModICMSST().getCod()))
	          .append(kv("pMVAST",  item.getMvaSt()))
	          .append(kv("vBCST",   item.getBaseICMSSt()))
	          .append(kv("pICMSST", item.getAliqIcmsSt()))
	          .append(kv("vICMSST", item.getValorIcmsSt()));

	        if (item.getPFCP().compareTo(java.math.BigDecimal.ZERO) == 0) {
	            sb.append(kv("vBCFCP", "0"))
	              .append(kv("pFCP", item.getPFCP()))
	              .append(kv("vFCP", item.getVFCP()))
	              .append(kv("infAdProd", "vBCFCP=" + item.getBaseICMSSt() + "pFCP=" + item.getPFCP() + "vFCP=" + item.getVFCP()));
	        } else {
	            sb.append(kv("vBCFCP", item.getBaseICMSSt()))
	              .append(kv("pFCP", item.getPFCP()))
	              .append(kv("vFCP", item.getVFCP()))
	              .append(kv("infAdProd", "vBCFCP=" + item.getBaseICMSSt() + "pFCP=" + item.getPFCP() + "vFCP=" + item.getVFCP()));
	        }
	    }

	    if ("20".equals(cst)) {
	        sb.append(kv("modBC", item.getTributo().getIcms().getModICMSNormal().getCod()))
	          .append(kv("vBC",    item.getBaseICMS()))
	          .append(kv("pICMS",  item.getAliqIcms()))
	          .append(kv("vICMS",  item.getValorIcms()));

	        if (item.getPFCP().compareTo(java.math.BigDecimal.ZERO) > 0) {
	            sb.append(kv("vBCFCP", item.getBaseICMSSt()))
	              .append(kv("pFCP", item.getPFCP()))
	              .append(kv("vFCP", item.getVFCP()))
	              .append(kv("infAdProd", "vBCFCP=" + item.getBaseICMSSt() + "pFCP=" + item.getPFCP() + "vFCP=" + item.getVFCP()));
	        }
	    }

	    if ("30".equals(cst)) {
	        sb.append(kv("modBCST", item.getTributo().getIcmsSt().getModICMSST().getCod()))
	          .append(kv("pMVAST",  item.getMvaSt()))
	          .append(kv("vBCST",   item.getBaseICMSSt()))
	          .append(kv("pICMSST", item.getAliqIcmsSt()))
	          .append(kv("vICMSST", item.getValorIcmsSt()));
	        if (!nz(destino.getISUF()).isEmpty()) {
	            // campos específicos desoneracao se aplicável
	        }
	        if (item.getPFCP().compareTo(java.math.BigDecimal.ZERO) > 0) {
	            sb.append(kv("vBCFCP", item.getBaseICMSSt()))
	              .append(kv("pFCP", item.getPFCP()))
	              .append(kv("vFCP", item.getVFCP()))
	              .append(kv("infAdProd", "vBCFCP=" + item.getBaseICMSSt() + "pFCP=" + item.getPFCP() + "vFCP=" + item.getVFCP()));
	        }
	    }

	    if ("40".equals(cst) || "41".equals(cst) || "50".equals(cst)) {
	        this.is50 = true;
	        if (!nz(this.destino.iSUF).isEmpty()) {
	            sb.append(kv("vICMSDeson", ""))
	              .append(kv("motDesICMS", ""));
	        }
	    }

	    if ("51".equals(cst)) {
	        sb.append(kv("modBC", item.getTributo().getIcms().getModICMSNormal().getCod()))
	          .append(kv("pRedBC", emissor.getBaseReducao()))
	          .append(kv("vBC", item.getBaseICMS()))
	          .append(kv("pICMS", item.getAliqIcms()))
	          .append(kv("vICMSOp", item.getValorIcms()))
	          .append(kv("vICMS", item.getValorIcms()));
	    }

	    if ("70".equals(cst)) {
	        sb.append(kv("modBC",   item.getTributo().getIcms().getModICMSNormal().getCod()))
	          .append(kv("pRedBC",  emissor.getBaseReducao()))
	          .append(kv("vBC",     item.getBaseICMS()))
	          .append(kv("pICMS",   item.getAliqIcms()))
	          .append(kv("vICMS",   item.getValorIcms()))
	          .append(kv("modBCST", item.getTributo().getIcmsSt().getModICMSST().getCod()))
	          .append(kv("pMVAST",  item.getMvaSt()))
	          .append(kv("vBCST",   item.getBaseICMSSt()))
	          .append(kv("pICMSST", item.getAliqIcmsSt()))
	          .append(kv("vICMSST", item.getValorIcmsSt()));

	        if (item.getPFCP().compareTo(java.math.BigDecimal.ZERO) > 0) {
	            sb.append(kv("vBCFCP", item.getBaseICMSSt()))
	              .append(kv("pFCP", item.getPFCP()))
	              .append(kv("vFCP", item.getVFCP()))
	              .append(kv("infAdProd", "vBCFCP=" + item.getBaseICMSSt() + "pFCP=" + item.getPFCP() + "vFCP=" + item.getVFCP()));
	        }

	        sb.append(kv("vICMSDeson", ""))
	          .append(kv("motDesICMS", ""));
	    }

	    if ("90".equals(cst)) {
	        sb.append(kv("modBC",  item.getTributo().getIcms().getModICMSNormal().getCod()))
	          .append(kv("pRedBC", emissor.getBaseReducao()))
	          .append(kv("vBC",    item.getBaseICMS()))
	          .append(kv("pICMS",  item.getAliqIcms()))
	          .append(kv("vICMS",  item.getValorIcms()))
	          .append(kv("vICMSDeson", ""))
	          .append(kv("motDesICMS", ""));
	        if (item.isItemST()) {
	            sb.append(kv("modBCST", item.getTributo().getIcmsSt().getModICMSST().getCod()))
	              .append(kv("pMVAST",  item.getMvaSt()))
	              .append(kv("vBCST",   item.getBaseICMSSt()))
	              .append(kv("pICMSST", item.getAliqIcmsSt()))
	              .append(kv("vICMSST", item.getValorIcmsSt()));
	        }
	        if (item.getPFCP().compareTo(java.math.BigDecimal.ZERO) > 0) {
	            sb.append(kv("vBCFCP", item.getBaseICMSSt()))
	              .append(kv("pFCP", item.getPFCP()))
	              .append(kv("vFCP", item.getVFCP()))
	              .append(kv("infAdProd", "vBCFCP=" + item.getBaseICMSSt() + "pFCP=" + item.getPFCP() + "vFCP=" + item.getVFCP()));
	        }
	    }

	    return sb.toString();
	}

	private String buildIPI(int contador, ItemNfe item) {
	    StringBuilder sb = new StringBuilder();
	    sb.append(section("IPI", contador))
	      .append(kv("CST",  item.getCstIpi()))
	      .append(kv("cEnq", item.getTributo().getIpi().getCodigoEnquadramento()));

	    if ("00".equals(item.getCstIpi()) || "49".equals(item.getCstIpi()) || "50".equals(item.getCstIpi()) || "99".equals(item.getCstIpi())) {
	        if (item.getTributo().getIpi().getCalculo().equals(TipoCalculo.TP)) {
	            sb.append(kv("vBC",  item.getBaseIPI()))
	              .append(kv("pIPI", item.getAliqIPI()));
	        } else {
	            sb.append(kv("qUnid", item.getQuantidade()))
	              .append(kv("vUnid", item.getValorUnitario()));
	        }
	        sb.append(kv("vIPI", item.getValorIPI()));
	    }
	    return sb.toString();
	}

	private String buildPIS(int contador, ItemNfe item) {
	    StringBuilder sb = new StringBuilder();
	    int cstPis = item.getCstPis();
	    sb.append(section("PIS", contador));

	    if (cstPis == 1 || cstPis == 2) {
	        sb.append(kv("CST", "0" + cstPis))
	          .append(kv("vBC",  item.getBasePis()))
	          .append(kv("pPIS", item.getAliqPis()))
	          .append(kv("vPIS", item.getValorPis()));
	    } else if (cstPis == 3) {
	        sb.append(kv("CST", "0" + cstPis))
	          .append(kv("vBC",     item.getBasePis()))
	          .append(kv("qBCProd", item.getQuantidade()))
	          .append(kv("vAliqProd", item.getTributo().getPis().getValor()))
	          .append(kv("vPIS",    item.getValorPis()));
	    } else if (cstPis == 4 || cstPis == 5 || cstPis == 6 || cstPis == 7 || cstPis == 8 || cstPis == 9) {
	        sb.append(kv("CST", "0" + cstPis));
	    } else {
	        if (item.getTributo().getPis().getCalculo() == TipoCalculo.TP) {
	            sb.append(kv("CST", item.getCstPis()))
	              .append(kv("vBC", item.getBasePis()))
	              .append(kv("pPIS", item.getAliqPis()))
	              .append(kv("vPIS", item.getValorPis()));
	        } else {
	            sb.append(kv("CST", item.getCstPis()))
	              .append(kv("qBCProd", item.getQuantidade()))
	              .append(kv("vAliqProd", item.getTributo().getPis().getValor()))
	              .append(kv("vPIS", item.getValorPis()));
	        }
	    }
	    return sb.toString();
	}

	private String buildCOFINS(int contador, ItemNfe item) {
	    StringBuilder sb = new StringBuilder();
	    int cstCof = item.getCstCofins();
	    sb.append(section("COFINS", contador));

	    if (cstCof == 1 || cstCof == 2) {
	        sb.append(kv("CST", "0" + cstCof))
	          .append(kv("vBC", item.getBasePis().setScale(2, java.math.RoundingMode.HALF_EVEN)))
	          .append(kv("pCOFINS", item.getAliqCofins()))
	          .append(kv("vCOFINS", item.getValorCofins()));
	    } else if (cstCof == 3) {
	        sb.append(kv("CST", "0" + cstCof))
	          .append(kv("vBC", item.getBaseCofins()))
	          .append(kv("qBCProd", item.getQuantidade()))
	          .append(kv("vAliqProd", item.getTributo().getCofins().getValor()))
	          .append(kv("vCOFINS", item.getValorCofins()));
	    } else if (cstCof == 4 || cstCof == 5 || cstCof == 6 || cstCof == 7 || cstCof == 8 || cstCof == 9) {
	        sb.append(kv("CST", "0" + cstCof));
	    } else {
	        if (item.getTributo().getCofins().getCalculo() == TipoCalculo.TP) {
	            sb.append(kv("CST", item.getCstCofins()))
	              .append(kv("vBC", item.getBaseCofins()))
	              .append(kv("pCOFINS", item.getAliqCofins()))
	              .append(kv("vCOFINS", item.getValorCofins()));
	        } else {
	            sb.append(kv("CST", item.getCstCofins()))
	              .append(kv("qBCProd", item.getQuantidade()))
	              .append(kv("vAliqProd", item.getTributo().getCofins().getValor()))
	              .append(kv("vCOFINS", item.getValorCofins()));
	        }
	    }
	    return sb.toString();
	}
	
	public String criaCCe(CartaCorrecao correcao,DadosDeConexaoSocket dados, String nomeArquivo) throws IOException{
		String cce;

		cce = "[EVENTO]"+"\n"+
				"idLote="+correcao.getSeqEvento()+"\n"
				+ "[EVENTO001]"
				+ "\n"
				+ "chNFe="+ correcao.getNfe().getChaveAcesso()+"\n"
				+ "cOrgao=35"+ "\n";
		if (correcao.getNfe().getEmitente().getFilial() == null){
			cce= cce
					+ "CNPJ="+correcao.getNfe().getEmitente().getEmpresa().getCnpj() + "\n";
		}else{
			cce=cce
					+ "CNPJ="+correcao.getNfe().getEmitente().getFilial().getCnpj() + "\n";
		}
		cce=cce
				+ "dhEvento="+ correcao.getDhEvento().format(formatador)+"\n"
				+ "tpEvento="+"110110"+"\n"
				+ "nSeqEvento="+correcao.getSeqEvento()+"\n"
				+ "versaoEvento="+ApplicationUtils.getConfiguration("versao.nfe")+"\n"
				+ "descEvento=Carta de Correcao"+"\n"
				+ "xCorrecao="+removerAcentos(correcao.getMotivoCorrecao())+"\n";

		criaConexao(dados);

		return comandoAcbr("ACBr.SaveToFile(\"C:\\ibrcomp\\tmp\\"+"cce"+nomeArquivo+".ini\",\""+cce+"\",5)");
	}
	
	public void geraListaImpInvalido(List<ClienteTemp> listaInvalidos,DadosDeConexaoSocket dados) throws IOException{
		String listagem="Lista de dados inválidos "+"\n";
		for (ClienteTemp clienteTemp : listaInvalidos) {
			listagem = listagem + "\n" + clienteTemp ;
		}
		criaConexao(dados);
		comandoAcbr("ACBr.SaveToFile(\"C:\\ibrcomp\\tmp\\"+"listaImportInvalidos.txt\",\""+listagem+"\",5)");
	}


	public String defineIdDest(Nfe nota){
		Uf ufEmissor;
		String resultado;
		Emissor emissor = new Emissor();
		emissor = preencheEmitente(nota.getEmitente());
		if(usuarioAutenticado.getIdFilial() != null){
			ufEmissor = this.filialDao.findById(usuarioAutenticado.getIdFilial(), false).getEndereco().getEndereco().getUf();
		}else{
			ufEmissor = this.empDao.findById(usuarioAutenticado.getIdEmpresa(), false).getEndereco().getEndereco().getUf();
		}

		if((nota.getUfDestino().equals(Uf.EX) && nota.getTipoPesquisa() == TipoPesquisa.FOR) || (nota.getUfDestino().equals(Uf.EX) && emissor.isExportador() )){
			resultado =  "3";
		}else if (nota.getUfDestino().equals(Uf.EX)){
			resultado =  "1";
		}else if (nota.getUfDestino().equals(ufEmissor)){
			resultado = "1";
		}else{
			resultado = "2";
		}
		return resultado;
	}

	@ToString
	@EqualsAndHashCode
	public static class Emissor{

		@Getter
		@Setter
		private String cnpjCpf;
		@Getter
		@Setter
		private String xNome;
		@Getter
		@Setter
		private String xFant;
		@Getter
		@Setter
		private String ie;
		@Getter
		@Setter
		private String ieST;
		@Getter
		@Setter
		private String iMunicipal;
		@Getter
		@Setter
		private String cNAE;
		@Getter
		@Setter
		private String crt;
		@Getter
		@Setter
		private String xLgr;
		@Getter
		@Setter
		private String nro;
		@Getter
		@Setter
		private String xCpl;
		@Getter
		@Setter
		private String xBairro;
		@Getter
		@Setter
		private String cMun;
		@Getter
		@Setter
		private String xMun;
		@Getter
		@Setter
		private Uf uf;
		@Getter
		@Setter
		private String cep;
		//		sat
		@Getter
		@Setter
		private String assinaturaSat;
		@Getter
		@Setter
		private String cPais;
		@Getter
		@Setter
		private String xPais;
		@Getter
		@Setter
		private String fone;
		@Getter
		@Setter
		private String cUF;
		@Getter
		@Setter
		private boolean exportador = false;
		@Getter
		@Setter
		private String cMunFG;
		@Getter
		@Setter
		private String mensagemFisco;
		@Getter
		@Setter
		private String mensagemEmitente;
		
		@Getter
		@Setter
		private String serie;

		@Getter
		@Setter
		@Enumerated(EnumType.STRING)
		private Enquadramento regime;
		
		@Getter
		@Setter
		@Enumerated(EnumType.STRING)
		private TipoImpressaoDFe tpImp;

		@Getter
		@Setter
		private String baseReducao;
	}
	public Emissor preencheEmissor(EmitenteCFe emitente){
		System.out.println("Estou no preenche Emitente");
		Emissor identificacao = new Emissor();
		List<Contato> listaContatos = new ArrayList<>();
		Contato contato = new Contato();
		String cep;

		if (emitente.getEmpresa() == null){
			if (emitente.getFilial().isEmissorSatMatriz() == false){
				System.out.println("Estou no preenche emitente - Filial");
				identificacao.setRegime(emitente.getFilial().getEnquadramento());
				System.out.println(identificacao.getRegime());
				identificacao.setBaseReducao(emitente.getFilial().getAliqArpoveitaIcms().toString());
				identificacao.setCnpjCpf(emitente.getFilial().getCnpj());
				identificacao.setExportador(emitente.getFilial().isExporta());
				System.out.println(identificacao.getCnpjCpf());
				identificacao.setXNome(removerAcentos(emitente.getFilial().getRazaoSocial()));
				System.out.println(identificacao.getXNome());
				if (emitente.getFilial().getNomeFantasia() != null){
					System.out.println("estou dento do if nome fantasia");
					System.out.println(identificacao.getXFant());
					identificacao.setXFant(removerAcentos(emitente.getFilial().getNomeFantasia()));
				}
				System.out.println("Fantasia: "+identificacao.getXFant());
				identificacao.setIe(emitente.getFilial().getInscEstadual());
				System.out.println(identificacao.getIe());
				if (emitente.getFilial().getAssinaturaSat() != null){ // arrumar essa logica
					identificacao.setAssinaturaSat(emitente.getFilial().getAssinaturaSat());

				}
				//emitente.setIeST();
				//emitente.setIMunicipal(iMunicipal);
				//emitente.setcnae();
				
				identificacao.setCrt(emitente.getFilial().getEnquadramento().getCod());
				if (emitente.getFilial().getEnquadramento() == Enquadramento.SimplesNacional){
					identificacao.setMensagemFisco(removerAcentos(this.menInt.getSimplesNacional()));
				}
				if (emitente.getFilial().getEndereco().getLogradouro() == "" || emitente.getFilial().getEndereco().getLogradouro().isEmpty()) {
					identificacao.setXLgr(removerAcentos(emitente.getFilial().getEndereco().getEndereco().getLogra()));
					identificacao.setXBairro(removerAcentos(emitente.getFilial().getEndereco().getEndereco().getBairro()));
				}else {
					identificacao.setXLgr(removerAcentos(emitente.getFilial().getEndereco().getLogradouro()));
					identificacao.setXBairro(removerAcentos(emitente.getFilial().getEndereco().getBairro()));
				}
				System.out.println(identificacao.getXLgr());
				identificacao.setNro(emitente.getFilial().getEndereco().getNumero());
				System.out.println(identificacao.getNro());
				identificacao.setXCpl(removerAcentos(emitente.getFilial().getEndereco().getComplemento()));
				System.out.println(identificacao.getXCpl());
				identificacao.setCMun(emitente.getFilial().getEndereco().getEndereco().getIbge());
				System.out.println(identificacao.getCMun());
				identificacao.setXMun(removerAcentos(emitente.getFilial().getEndereco().getEndereco().getLocalidade()));
				System.out.println(identificacao.getXMun());
				identificacao.setUf(emitente.getFilial().getEndereco().getEndereco().getUf());
				System.out.println(identificacao.getUf());
				cep = emitente.getFilial().getEndereco().getEndereco().getCep();
				identificacao.setCep(cep.replace("-", ""));
				//			emitente.setCep(emitente.getFilial().getEndereco().getEndereco().getCep());
				System.out.println(identificacao.getCep());
				identificacao.setCPais("1058");
				identificacao.setXPais("Brasil");
				System.out.println("vou iniciar a conversao do telefone");
				listaContatos = this.contatoDao.procuraContatoEmitente(null,emitente.getFilial());
				if (!listaContatos.isEmpty()){
					contato = listaContatos.get(0);
					contato.setFone(this.telefoneDao.listaFonePorContato(contato));
					if (!contato.getFone().isEmpty()){
						identificacao.setFone("(" + contato.getFone().get(0).getDdd()+")"+ contato.getFone().get(0).getFone());
						System.out.println("telefone: " + identificacao.getFone());
						listaContatos = new ArrayList<>();
					}
				}
				System.out.println("consegui!");
				//emitente.cUF;
				//emitente.cMunFG;
			}else{ // else onde é uma filial que esta emitindo um CFe mas o sat pertence a matriz
				Empresa empTemp = new Empresa();
				empTemp = this.empDao.findById(emitente.getFilial().getIdEmpresa(), false);
				System.out.println("Estou no preenche emitente - EMPRESA");
				identificacao.setRegime(empTemp.getEnquadramento());
				System.out.println(identificacao.getRegime());
				identificacao.setBaseReducao(empTemp.getAliqArpoveitaIcms().toString());
				identificacao.setCnpjCpf(empTemp.getCnpj());
				identificacao.setExportador(empTemp.isExporta());
				System.out.println(identificacao.getCnpjCpf());
				identificacao.setXNome(removerAcentos(empTemp.getRazaoSocial()));
				System.out.println(identificacao.getXNome());
				if (empTemp.getNomeFantasia() != null){
					System.out.println("estou dento do if nome fantasia");
					System.out.println(identificacao.getXFant());
					identificacao.setXFant(removerAcentos(empTemp.getNomeFantasia()));
				}
				if (empTemp.getAssinaturaSat() != null){
					identificacao.setAssinaturaSat(empTemp.getAssinaturaSat());
				}
				System.out.println("Fantasia: "+identificacao.getXFant());
				identificacao.setIe(empTemp.getInscEstadual());
				System.out.println(identificacao.getIe());
				//emitente.setIeST();
				//emitente.setIMunicipal(iMunicipal);
				//emitente.setcnae();
				identificacao.setCrt(empTemp.getEnquadramento().getCod());
				if (empTemp.getEndereco().getLogradouro() == "" || empTemp.getEndereco().getLogradouro().isEmpty() ) {
					identificacao.setXLgr(removerAcentos(empTemp.getEndereco().getEndereco().getLogra()));
					identificacao.setXBairro(removerAcentos(empTemp.getEndereco().getEndereco().getBairro()));
				}else {
					identificacao.setXLgr(removerAcentos(empTemp.getEndereco().getLogradouro()));
					identificacao.setXBairro(removerAcentos(empTemp.getEndereco().getBairro()));
				}
				System.out.println(identificacao.getXLgr());
				identificacao.setNro(empTemp.getEndereco().getNumero());
				System.out.println(identificacao.getNro());
				identificacao.setXCpl(removerAcentos(empTemp.getEndereco().getComplemento()));
				System.out.println(identificacao.getXCpl());
				identificacao.setCMun(empTemp.getEndereco().getEndereco().getIbge());
				System.out.println(identificacao.getCMun());
				identificacao.setXMun(removerAcentos(empTemp.getEndereco().getEndereco().getLocalidade()));
				System.out.println(identificacao.getXMun());
				identificacao.setUf(empTemp.getEndereco().getEndereco().getUf());
				System.out.println(identificacao.getUf());
				//		emitente.setCep(empTemp.getEndereco().getEndereco().getCep());
				cep =empTemp.getEndereco().getEndereco().getCep();
				identificacao.setCep(cep.replace("-", ""));
				System.out.println(identificacao.getCep());
				identificacao.setCPais("1058");
				identificacao.setXPais("Brasil");
				System.out.println("vou iniciar a conversao do telefone");
				listaContatos = this.contatoDao.procuraContatoEmitente(emitente.getEmpresa(), null);
				if (!listaContatos.isEmpty()){
					contato = listaContatos.get(0);
					identificacao.setFone("(" + contato.getFone().get(0).getDdd()+")"+ contato.getFone().get(0).getFone());
					System.out.println("telefone: " + identificacao.getFone());
					listaContatos = new ArrayList<>();
				}
				System.out.println("consegui!");

			}
		}else{ // quando quem está emitiendo a CFe é a MATRIZ
			System.out.println("Estou no preenche emitente - EMPRESA");
			identificacao.setRegime(emitente.getEmpresa().getEnquadramento());
			System.out.println(identificacao.getRegime());
			identificacao.setBaseReducao(emitente.getEmpresa().getAliqArpoveitaIcms().toString());
			identificacao.setCnpjCpf(emitente.getEmpresa().getCnpj());
			identificacao.setExportador(emitente.getEmpresa().isExporta());
			System.out.println(identificacao.getCnpjCpf());
			identificacao.setXNome(removerAcentos(emitente.getEmpresa().getRazaoSocial()));
			System.out.println(identificacao.getXNome());
			if (emitente.getEmpresa().getNomeFantasia() != null){
				System.out.println("estou dento do if nome fantasia");
				System.out.println(identificacao.getXFant());
				identificacao.setXFant(removerAcentos(emitente.getEmpresa().getNomeFantasia()));
			}
			if (emitente.getEmpresa().getAssinaturaSat() != null){
				identificacao.setAssinaturaSat(emitente.getEmpresa().getAssinaturaSat());
			}
			System.out.println("Fantasia: "+identificacao.getXFant());
			identificacao.setIe(emitente.getEmpresa().getInscEstadual());
			System.out.println(identificacao.getIe());
			//emitente.setIeST();
			//emitente.setIMunicipal(iMunicipal);
			//emitente.setcnae();
			identificacao.setCrt(emitente.getEmpresa().getEnquadramento().getCod());
			if (emitente.getEmpresa().getEndereco().getLogradouro() == "" || emitente.getEmpresa().getEndereco().getLogradouro().isEmpty()) {
				identificacao.setXLgr(removerAcentos(emitente.getEmpresa().getEndereco().getEndereco().getLogra()));
				identificacao.setXBairro(removerAcentos(emitente.getEmpresa().getEndereco().getEndereco().getBairro()));
			}else {
				identificacao.setXLgr(removerAcentos(emitente.getEmpresa().getEndereco().getLogradouro()));
				identificacao.setXBairro(removerAcentos(emitente.getEmpresa().getEndereco().getBairro()));
			}
				
			System.out.println(identificacao.getXLgr());
			identificacao.setNro(emitente.getEmpresa().getEndereco().getNumero());
			System.out.println(identificacao.getNro());
			identificacao.setXCpl(removerAcentos(emitente.getEmpresa().getEndereco().getComplemento()));
			System.out.println(identificacao.getXCpl());
			identificacao.setCMun(emitente.getEmpresa().getEndereco().getEndereco().getIbge());
			System.out.println(identificacao.getCMun());
			identificacao.setXMun(removerAcentos(emitente.getEmpresa().getEndereco().getEndereco().getLocalidade()));
			System.out.println(identificacao.getXMun());
			identificacao.setUf(emitente.getEmpresa().getEndereco().getEndereco().getUf());
			System.out.println(identificacao.getUf());
			//		emitente.setCep(emitente.getEmpresa().getEndereco().getEndereco().getCep());
			cep =emitente.getEmpresa().getEndereco().getEndereco().getCep();
			identificacao.setCep(cep.replace("-", ""));
			System.out.println(identificacao.getCep());
			identificacao.setCPais("1058");
			identificacao.setXPais("Brasil");
			System.out.println("vou iniciar a conversao do telefone");
			listaContatos = this.contatoDao.procuraContatoEmitente(emitente.getEmpresa(), null);
			if (!listaContatos.isEmpty()){
				contato = listaContatos.get(0);
				identificacao.setFone("(" + contato.getFone().get(0).getDdd()+")"+ contato.getFone().get(0).getFone());
				System.out.println("telefone: " + identificacao.getFone());
				listaContatos = new ArrayList<>();
			}
			System.out.println("consegui!");
			//emitente.cUF;
			//emitente.cMunFG;
		}
		return identificacao;
	}
	
	public Emissor preencheEmissorVenda(EmitenteVenda emitente){
		System.out.println("Estou no preenche Emitente");
		Emissor identificacao = new Emissor();
		List<Contato> listaContatos = new ArrayList<>();
		Contato contato = new Contato();
		String cep;

		if (emitente.getEmpresa() == null){
			if (emitente.getFilial().isEmissorSatMatriz() == false){
				System.out.println("Estou no preenche emitente - Filial");
				identificacao.setRegime(emitente.getFilial().getEnquadramento());
				System.out.println(identificacao.getRegime());
				identificacao.setBaseReducao(emitente.getFilial().getAliqArpoveitaIcms().toString());
				identificacao.setCnpjCpf(emitente.getFilial().getCnpj());
				identificacao.setExportador(emitente.getFilial().isExporta());
				System.out.println(identificacao.getCnpjCpf());
				identificacao.setXNome(removerAcentos(emitente.getFilial().getRazaoSocial()));
				System.out.println(identificacao.getXNome());
				if (emitente.getFilial().getNomeFantasia() != null){
					System.out.println("estou dento do if nome fantasia");
					System.out.println(identificacao.getXFant());
					identificacao.setXFant(removerAcentos(emitente.getFilial().getNomeFantasia()));
				}
				System.out.println("Fantasia: "+identificacao.getXFant());
				identificacao.setIe(emitente.getFilial().getInscEstadual());
				System.out.println(identificacao.getIe());
				if (emitente.getFilial().getAssinaturaSat() != null){ // arrumar essa logica
					identificacao.setAssinaturaSat(emitente.getFilial().getAssinaturaSat());

				}
				//emitente.setIeST();
				//emitente.setIMunicipal(iMunicipal);
				//emitente.setcnae();
				identificacao.setCrt(emitente.getFilial().getEnquadramento().getCod());
				if (emitente.getFilial().getEnquadramento() == Enquadramento.SimplesNacional){
					identificacao.setMensagemFisco(removerAcentos(this.menInt.getSimplesNacional()));
				}
				if (emitente.getFilial().getEndereco().getLogradouro( )== "" || emitente.getFilial().getEndereco().getLogradouro().isEmpty()) {
					identificacao.setXLgr(removerAcentos(emitente.getFilial().getEndereco().getEndereco().getLogra()));
					identificacao.setXBairro(removerAcentos(emitente.getFilial().getEndereco().getEndereco().getBairro()));
				}else {
					identificacao.setXLgr(removerAcentos(emitente.getFilial().getEndereco().getLogradouro()));
					identificacao.setXBairro(removerAcentos(emitente.getFilial().getEndereco().getBairro()));
				}
				System.out.println(identificacao.getXLgr());
				identificacao.setNro(emitente.getFilial().getEndereco().getNumero());
				System.out.println(identificacao.getNro());
				identificacao.setXCpl(removerAcentos(emitente.getFilial().getEndereco().getComplemento()));
				System.out.println(identificacao.getXCpl());
				identificacao.setCMun(emitente.getFilial().getEndereco().getEndereco().getIbge());
				System.out.println(identificacao.getCMun());
				identificacao.setXMun(removerAcentos(emitente.getFilial().getEndereco().getEndereco().getLocalidade()));
				System.out.println(identificacao.getXMun());
				identificacao.setUf(emitente.getFilial().getEndereco().getEndereco().getUf());
				System.out.println(identificacao.getUf());
				cep = emitente.getFilial().getEndereco().getEndereco().getCep();
				identificacao.setCep(cep.replace("-", ""));
				//			emitente.setCep(emitente.getFilial().getEndereco().getEndereco().getCep());
				System.out.println(identificacao.getCep());
				identificacao.setCPais("1058");
				identificacao.setXPais("Brasil");
				System.out.println("vou iniciar a conversao do telefone");
				listaContatos = this.contatoDao.procuraContatoEmitente(null,emitente.getFilial());
				if (!listaContatos.isEmpty()){
					contato = listaContatos.get(0);
					contato.setFone(this.telefoneDao.listaFonePorContato(contato));
					if (!contato.getFone().isEmpty()){
						identificacao.setFone("(" + contato.getFone().get(0).getDdd()+")"+ contato.getFone().get(0).getFone());
						System.out.println("telefone: " + identificacao.getFone());
						listaContatos = new ArrayList<>();
					}
				}
				System.out.println("consegui!");
				//emitente.cUF;
				//emitente.cMunFG;
			}else{ // else onde é uma filial que esta emitindo um CFe mas o sat pertence a matriz
				Empresa empTemp = new Empresa();
				empTemp = this.empDao.findById(emitente.getFilial().getIdEmpresa(), false);
				System.out.println("Estou no preenche emitente - EMPRESA");
				identificacao.setRegime(empTemp.getEnquadramento());
				System.out.println(identificacao.getRegime());
				identificacao.setBaseReducao(empTemp.getAliqArpoveitaIcms().toString());
				identificacao.setCnpjCpf(empTemp.getCnpj());
				identificacao.setExportador(empTemp.isExporta());
				System.out.println(identificacao.getCnpjCpf());
				identificacao.setXNome(removerAcentos(empTemp.getRazaoSocial()));
				System.out.println(identificacao.getXNome());
				if (empTemp.getNomeFantasia() != null){
					System.out.println("estou dento do if nome fantasia");
					System.out.println(identificacao.getXFant());
					identificacao.setXFant(removerAcentos(empTemp.getNomeFantasia()));
				}
				if (empTemp.getAssinaturaSat() != null){
					identificacao.setAssinaturaSat(empTemp.getAssinaturaSat());
				}
				System.out.println("Fantasia: "+identificacao.getXFant());
				identificacao.setIe(empTemp.getInscEstadual());
				System.out.println(identificacao.getIe());
				//emitente.setIeST();
				//emitente.setIMunicipal(iMunicipal);
				//emitente.setcnae();
				identificacao.setCrt(empTemp.getEnquadramento().getCod());
				if (empTemp.getEndereco().getLogradouro() == "" || empTemp.getEndereco().getLogradouro().isEmpty()) {
					identificacao.setXLgr(removerAcentos(empTemp.getEndereco().getEndereco().getLogra()));
					identificacao.setXBairro(removerAcentos(empTemp.getEndereco().getEndereco().getBairro()));
				}else {
					identificacao.setXLgr(removerAcentos(empTemp.getEndereco().getLogradouro()));
					identificacao.setXBairro(removerAcentos(empTemp.getEndereco().getBairro()));
				}
				System.out.println(identificacao.getXLgr());
				identificacao.setNro(empTemp.getEndereco().getNumero());
				System.out.println(identificacao.getNro());
				identificacao.setXCpl(removerAcentos(empTemp.getEndereco().getComplemento()));
				System.out.println(identificacao.getXCpl());
				identificacao.setCMun(empTemp.getEndereco().getEndereco().getIbge());
				System.out.println(identificacao.getCMun());
				identificacao.setXMun(removerAcentos(empTemp.getEndereco().getEndereco().getLocalidade()));
				System.out.println(identificacao.getXMun());
				identificacao.setUf(empTemp.getEndereco().getEndereco().getUf());
				System.out.println(identificacao.getUf());
				//		emitente.setCep(empTemp.getEndereco().getEndereco().getCep());
				cep =empTemp.getEndereco().getEndereco().getCep();
				identificacao.setCep(cep.replace("-", ""));
				System.out.println(identificacao.getCep());
				identificacao.setCPais("1058");
				identificacao.setXPais("Brasil");
				System.out.println("vou iniciar a conversao do telefone");
				listaContatos = this.contatoDao.procuraContatoEmitente(emitente.getEmpresa(), null);
				if (!listaContatos.isEmpty()){
					contato = listaContatos.get(0);
					identificacao.setFone("(" + contato.getFone().get(0).getDdd()+")"+ contato.getFone().get(0).getFone());
					System.out.println("telefone: " + identificacao.getFone());
					listaContatos = new ArrayList<>();
				}
				System.out.println("consegui!");

			}
		}else{ // quando quem está emitiendo a CFe é a MATRIZ
			System.out.println("Estou no preenche emitente - EMPRESA");
			identificacao.setRegime(emitente.getEmpresa().getEnquadramento());
			System.out.println(identificacao.getRegime());
			identificacao.setBaseReducao(emitente.getEmpresa().getAliqArpoveitaIcms().toString());
			identificacao.setCnpjCpf(emitente.getEmpresa().getCnpj());
			identificacao.setExportador(emitente.getEmpresa().isExporta());
			System.out.println(identificacao.getCnpjCpf());
			identificacao.setXNome(removerAcentos(emitente.getEmpresa().getRazaoSocial()));
			System.out.println(identificacao.getXNome());
			if (emitente.getEmpresa().getNomeFantasia() != null){
				System.out.println("estou dento do if nome fantasia");
				System.out.println(identificacao.getXFant());
				identificacao.setXFant(removerAcentos(emitente.getEmpresa().getNomeFantasia()));
			}
			if (emitente.getEmpresa().getAssinaturaSat() != null){
				identificacao.setAssinaturaSat(emitente.getEmpresa().getAssinaturaSat());
			}
			System.out.println("Fantasia: "+identificacao.getXFant());
			identificacao.setIe(emitente.getEmpresa().getInscEstadual());
			System.out.println(identificacao.getIe());
			//emitente.setIeST();
			//emitente.setIMunicipal(iMunicipal);
			//emitente.setcnae();
			identificacao.setCrt(emitente.getEmpresa().getEnquadramento().getCod());
			if(emitente.getEmpresa().getEndereco().getLogradouro() == "" || emitente.getEmpresa().getEndereco().getLogradouro().isEmpty()) {
				identificacao.setXLgr(removerAcentos(emitente.getEmpresa().getEndereco().getEndereco().getLogra()));
				identificacao.setXBairro(removerAcentos(emitente.getEmpresa().getEndereco().getEndereco().getBairro()));
			}else {
				identificacao.setXLgr(removerAcentos(emitente.getEmpresa().getEndereco().getLogradouro()));
				identificacao.setXBairro(removerAcentos(emitente.getEmpresa().getEndereco().getBairro()));
			}
			System.out.println(identificacao.getXLgr());
			identificacao.setNro(emitente.getEmpresa().getEndereco().getNumero());
			System.out.println(identificacao.getNro());
			identificacao.setXCpl(removerAcentos(emitente.getEmpresa().getEndereco().getComplemento()));
			System.out.println(identificacao.getXCpl());
			identificacao.setCMun(emitente.getEmpresa().getEndereco().getEndereco().getIbge());
			System.out.println(identificacao.getCMun());
			identificacao.setXMun(removerAcentos(emitente.getEmpresa().getEndereco().getEndereco().getLocalidade()));
			System.out.println(identificacao.getXMun());
			identificacao.setUf(emitente.getEmpresa().getEndereco().getEndereco().getUf());
			System.out.println(identificacao.getUf());
			//		emitente.setCep(emitente.getEmpresa().getEndereco().getEndereco().getCep());
			cep =emitente.getEmpresa().getEndereco().getEndereco().getCep();
			identificacao.setCep(cep.replace("-", ""));
			System.out.println(identificacao.getCep());
			identificacao.setCPais("1058");
			identificacao.setXPais("Brasil");
			System.out.println("vou iniciar a conversao do telefone");
			listaContatos = this.contatoDao.procuraContatoEmitente(emitente.getEmpresa(), null);
			if (!listaContatos.isEmpty()){
				contato = listaContatos.get(0);
				identificacao.setFone("(" + contato.getFone().get(0).getDdd()+")"+ contato.getFone().get(0).getFone());
				System.out.println("telefone: " + identificacao.getFone());
				listaContatos = new ArrayList<>();
			}
			System.out.println("consegui!");
			//emitente.cUF;
			//emitente.cMunFG;
		}
		return identificacao;
	}

	public Emissor preencheEmitente(Emitente emit){
		System.out.println("Estou no preenche Emitente");
		Emissor emitente = new Emissor();
		List<Contato> listaContatos = new ArrayList<>();
		Contato contato = new Contato();
		String cep;

		if (emit.getEmpresa() == null){
			System.out.println("Estou no preenche emitente - Filial");
			emitente.setRegime(emit.getFilial().getEnquadramento());
			System.out.println(emitente.getRegime());
			emitente.setBaseReducao(emit.getFilial().getAliqArpoveitaIcms().toString());
			emitente.setCnpjCpf(emit.getFilial().getCnpj());
			emitente.setExportador(emit.getFilial().isExporta());
			System.out.println(emitente.getCnpjCpf());
			emitente.setXNome(removerAcentos(emit.getFilial().getRazaoSocial()));
			System.out.println(emitente.getXNome());
			if (emit.getFilial().getNomeFantasia() != null){
				System.out.println("estou dento do if nome fantasia");
				System.out.println(emitente.getXFant());
				emitente.setXFant(removerAcentos(emit.getFilial().getNomeFantasia()));
			}
			System.out.println("Fantasia: "+emitente.getXFant());
			emitente.setIe(emit.getFilial().getInscEstadual());
			System.out.println(emitente.getIe());
			//emitente.setIeST();
			//emitente.setIMunicipal(iMunicipal);
			//emitente.setcnae();
			emitente.setSerie(emit.getFilial().getSerie());
			emitente.setCrt(emit.getFilial().getEnquadramento().getCod());
			if (emit.getFilial().getEnquadramento() == Enquadramento.SimplesNacional){
				emitente.setMensagemFisco(removerAcentos(this.menInt.getSimplesNacional()));
			}
			if(emit.getFilial().getEndereco().getLogradouro() == "" || emit.getFilial().getEndereco().getLogradouro().isEmpty()) {
				emitente.setXLgr(removerAcentos(emit.getFilial().getEndereco().getEndereco().getLogra()));
				emitente.setXBairro(removerAcentos(emit.getFilial().getEndereco().getEndereco().getBairro()));
			}else {
				emitente.setXLgr(removerAcentos(emit.getFilial().getEndereco().getLogradouro()));
				emitente.setXBairro(removerAcentos(emit.getFilial().getEndereco().getBairro()));
			}
			System.out.println(emitente.getXLgr());
			emitente.setNro(emit.getFilial().getEndereco().getNumero());
			System.out.println(emitente.getNro());
			emitente.setXCpl(removerAcentos(emit.getFilial().getEndereco().getComplemento()));
			System.out.println(emitente.getXCpl());
			emitente.setCMun(emit.getFilial().getEndereco().getEndereco().getIbge());
			System.out.println(emitente.getCMun());
			emitente.setXMun(removerAcentos(emit.getFilial().getEndereco().getEndereco().getLocalidade()));
			System.out.println(emitente.getXMun());
			emitente.setUf(emit.getFilial().getEndereco().getEndereco().getUf());
			System.out.println(emitente.getUf());
			cep = emit.getFilial().getEndereco().getEndereco().getCep();
			emitente.setCep(cep.replace("-", ""));
			//			emitente.setCep(emit.getFilial().getEndereco().getEndereco().getCep());
			System.out.println(emitente.getCep());
			emitente.setCPais("1058");
			emitente.setXPais("Brasil");
			System.out.println("vou iniciar a conversao do telefone");
			listaContatos = this.contatoDao.procuraContatoEmitente(null,emit.getFilial());
			if (!listaContatos.isEmpty()){
				contato = listaContatos.get(0);
				contato.setFone(this.telefoneDao.listaFonePorContato(contato));
				if (!contato.getFone().isEmpty()){
					emitente.setFone("(" + contato.getFone().get(0).getDdd()+")"+ contato.getFone().get(0).getFone());
					System.out.println("telefone: " + emitente.getFone());
					listaContatos = new ArrayList<>();
				}
			}
			emitente.setTpImp(emit.getFilial().getTpImp());
			System.out.println("consegui!");
			//emitente.cUF;
			//emitente.cMunFG;
		}else{ System.out.println("Estou no preenche emitente - EMPRESA");
		emitente.setRegime(emit.getEmpresa().getEnquadramento());
		System.out.println(emitente.getRegime());
		emitente.setBaseReducao(emit.getEmpresa().getAliqArpoveitaIcms().toString());
		emitente.setCnpjCpf(emit.getEmpresa().getCnpj());
		emitente.setExportador(emit.getEmpresa().isExporta());
		System.out.println(emitente.getCnpjCpf());
		emitente.setXNome(removerAcentos(emit.getEmpresa().getRazaoSocial()));
		System.out.println(emitente.getXNome());
		if (emit.getEmpresa().getNomeFantasia() != null){
			System.out.println("estou dento do if nome fantasia");
			System.out.println(emitente.getXFant());
			emitente.setXFant(removerAcentos(emit.getEmpresa().getNomeFantasia()));
		}
		System.out.println("Fantasia: "+emitente.getXFant());
		emitente.setIe(emit.getEmpresa().getInscEstadual());
		System.out.println(emitente.getIe());
		//emitente.setIeST();
		//emitente.setIMunicipal(iMunicipal);
		//emitente.setcnae();
		emitente.setSerie(emit.getEmpresa().getSerie());
		System.out.println(emitente.getSerie());
		emitente.setCrt(emit.getEmpresa().getEnquadramento().getCod());
		if (emit.getEmpresa().getEndereco().getLogradouro() == "" || emit.getEmpresa().getEndereco().getLogradouro().isEmpty()) {
			emitente.setXLgr(removerAcentos(emit.getEmpresa().getEndereco().getEndereco().getLogra()));
			emitente.setXBairro(removerAcentos(emit.getEmpresa().getEndereco().getEndereco().getBairro()));
		}else {
			emitente.setXLgr(removerAcentos(emit.getEmpresa().getEndereco().getLogradouro()));
			emitente.setXBairro(removerAcentos(emit.getEmpresa().getEndereco().getBairro()));
		}
		System.out.println(emitente.getXLgr());
		emitente.setNro(emit.getEmpresa().getEndereco().getNumero());
		System.out.println(emitente.getNro());
		emitente.setXCpl(removerAcentos(emit.getEmpresa().getEndereco().getComplemento()));
		System.out.println(emitente.getXCpl());
		emitente.setCMun(emit.getEmpresa().getEndereco().getEndereco().getIbge());
		System.out.println(emitente.getCMun());
		emitente.setXMun(removerAcentos(emit.getEmpresa().getEndereco().getEndereco().getLocalidade()));
		System.out.println(emitente.getXMun());
		emitente.setUf(emit.getEmpresa().getEndereco().getEndereco().getUf());
		System.out.println(emitente.getUf());
		//		emitente.setCep(emit.getEmpresa().getEndereco().getEndereco().getCep());
		cep =emit.getEmpresa().getEndereco().getEndereco().getCep();
		emitente.setCep(cep.replace("-", ""));
		System.out.println(emitente.getCep());
		emitente.setCPais("1058");
		emitente.setXPais("Brasil");
		System.out.println("vou iniciar a conversao do telefone");
		listaContatos = this.contatoDao.procuraContatoEmitente(emit.getEmpresa(), null);
		if (!listaContatos.isEmpty()){
			contato = listaContatos.get(0);
			emitente.setFone("(" + contato.getFone().get(0).getDdd()+")"+ contato.getFone().get(0).getFone());
			System.out.println("telefone: " + emitente.getFone());
			listaContatos = new ArrayList<>();
		}
		System.out.println("consegui!");
		//emitente.cUF;
		//emitente.cMunFG;
		emitente.setTpImp(emit.getEmpresa().getTpImp());
		}
		return emitente;
	}

	@ToString
	@EqualsAndHashCode
	public static class Destino{
		@Getter
		@Setter
		private String idEstrangeiro;
		@Getter
		@Setter
		private String cNPJCPF;
		@Getter
		@Setter
		private String xNome;
		@Getter
		@Setter
		private String indIEDest;
		@Getter
		@Setter
		private String iE;
		@Getter
		@Setter
		private String iSUF;
		@Getter
		@Setter
		private String email;
		@Getter
		@Setter
		private String xLgr;
		@Getter
		@Setter
		private String nro;
		@Getter
		@Setter
		private String xCpl;
		@Getter
		@Setter
		private String xBairro;
		@Getter
		@Setter
		private String cMun;
		@Getter
		@Setter
		private String xMun;
		@Getter
		@Setter
		private String uf;
		@Getter
		@Setter
		private String cep;
		@Getter
		@Setter
		private String cPais;
		@Getter
		@Setter
		private String xPais;
		@Getter
		@Setter
		private String fone;
		@Getter
		@Setter
		private int cUF;

		@Getter
		@Setter
		private String reducaoBase;
		@Getter
		@Setter
		private boolean permiteReducao = false;
		@Getter
		@Setter
		@Enumerated(EnumType.STRING)
		private Enquadramento regime;


	}
	// preenche destino 
	public Destino preencheDestinoPdv(br.com.nsym.domain.model.entity.tools.AbstractDestino destino) {
		System.out.println("Estou no preenche Destino");
		Destino destinoLocal = new Destino();
		Email emailLocal = new Email();
		List<Contato> listaContatos = new ArrayList<>();
		Contato contato = new Contato();
		String cep;

		if (destino.getCliente() != null){
			System.out.println("Estou dentro de Destino = CLiente");
			if (destino.getCliente().getTipoCliente().equals(TipoCliente.Est)){
				destinoLocal.setIdEstrangeiro(destino.getCliente().getIdEstrangeiro());
				System.out.println(destinoLocal.getIdEstrangeiro());
			}
			System.out.println("vou verificar se cnpj = null");

			if (destino.getCliente().getCnpj() == null || destino.getCliente().getTipoCliente().equals(TipoCliente.Est) ){
				System.out.println("estou na linha 1859 - cnpj = vazio");
				destinoLocal.setCNPJCPF(destino.getCliente().getCpf());
				System.out.println(destinoLocal.getCNPJCPF());
				destinoLocal.setIndIEDest("9");
			}else{
				destinoLocal.setCNPJCPF(destino.getCliente().getCnpj());
				System.out.println(destinoLocal.getCNPJCPF());
				if (destino.getCliente().getInscEstadual() == null){
					if ((destino.getCliente().getEstado().equals(Uf.AM)) ||
							(destino.getCliente().getEstado().equals(Uf.BA)) ||
							(destino.getCliente().getEstado().equals(Uf.CE)) ||
							(destino.getCliente().getEstado().equals(Uf.GO)) ||
							(destino.getCliente().getEstado().equals(Uf.MG)) ||
							(destino.getCliente().getEstado().equals(Uf.MS)) ||
							(destino.getCliente().getEstado().equals(Uf.MT)) ||
							(destino.getCliente().getEstado().equals(Uf.PA)) ||
							(destino.getCliente().getEstado().equals(Uf.PE)) ||
							(destino.getCliente().getEstado().equals(Uf.RN)) ||
							(destino.getCliente().getEstado().equals(Uf.SE)) ||
							(destino.getCliente().getEstado().equals(Uf.SP))){

						destinoLocal.setIndIEDest("9");
					}else{
						destinoLocal.setIndIEDest("2");
					}
				}else{
					destinoLocal.setIndIEDest("1");
					destinoLocal.setIE(destino.getCliente().getInscEstadual());
					System.out.println(destinoLocal.getIE());
					destinoLocal.setRegime(destino.getCliente().getEnquadramento());
					if (destino.getCliente().getEnquadramento() == Enquadramento.SimplesNacional){
						emissor.setMensagemFisco(this.menInt.getMensagem102());
					}
					System.out.println(destinoLocal.getRegime());
				}
			}

			destinoLocal.setXNome(removerAcentos(destino.getCliente().getRazaoSocial()));
			System.out.println(destinoLocal.getXNome());
			System.out.println("Setando o suframa caso exista!");
			if ( destino.getCliente().getSuframa() != null){
				System.out.println("Estou no suframa dentro do if diferente de vazio!");
				destinoLocal.setISUF(destino.getCliente().getSuframa());
				System.out.println(destinoLocal.getISUF());
			}else{
				destinoLocal.setISUF("");
			}
			System.out.println("Setando o Email");
			if (destino.getCliente().getEmailNFE().getEmail() != null){
				emailLocal = this.emailDao.pegaEmailNfe(destino.getCliente(), null,null,null,null,destino.getCliente().getIdEmpresa());
				destinoLocal.setEmail(emailLocal.getEmail());
				System.out.println(destinoLocal.getEmail());
			}
			if (destino.getCliente().getEndereco().getLogradouro() == "" || destino.getCliente().getEndereco().getLogradouro().isEmpty()) {
				destinoLocal.setXLgr(removerAcentos(destino.getCliente().getEndereco().getEndereco().getLogra()));
				destinoLocal.setXBairro(removerAcentos(destino.getCliente().getEndereco().getEndereco().getBairro()));
			}else {
				destinoLocal.setXLgr(removerAcentos(destino.getCliente().getEndereco().getLogradouro()));
				destinoLocal.setXBairro(removerAcentos(destino.getCliente().getEndereco().getBairro()));
			}
			System.out.println(destinoLocal.getXLgr());
			destinoLocal.setNro(destino.getCliente().getEndereco().getNumero());
			System.out.println(destinoLocal.getNro());
			destinoLocal.setXCpl(removerAcentos(destino.getCliente().getEndereco().getComplemento()));
			System.out.println(destinoLocal.getXCpl());
			destinoLocal.setCMun(destino.getCliente().getEndereco().getEndereco().getIbge());
			System.out.println(destinoLocal.getCMun());
			destinoLocal.setXMun(removerAcentos(destino.getCliente().getEndereco().getEndereco().getLocalidade()));
			System.out.println(destinoLocal.getXMun());
			destinoLocal.setUf(destino.getCliente().getEndereco().getEndereco().getUf().name());
			destinoLocal.setCUF(destino.getCliente().getEndereco().getEndereco().getUf().getCod());
			System.out.println(destinoLocal.getUf());
			//			destinoLocal.setCep(destino.getCliente().getEndereco().getEndereco().getCep());
			cep = destino.getCliente().getEndereco().getEndereco().getCep();
			destinoLocal.setCep(cep.replace("-", ""));
			System.out.println(destinoLocal.getCep());
			destinoLocal.setCPais(destino.getCliente().getPais().getCodigo().toString());
			System.out.println(destinoLocal.getCPais());
			destinoLocal.setXPais(destino.getCliente().getPais().getNome());
			System.out.println(destinoLocal.getXPais());
			System.out.println("vou iniciar a conversao do telefone");
			listaContatos = this.contatoDao.procuraContatoDestino(destino.getCliente(),null,null,null, null);
			if (!listaContatos.isEmpty() ){
				System.out.println("estou dentro da lista != vazio");
				contato = listaContatos.get(0);
				contato.setFone(this.telefoneDao.listaFonePorContato(contato));
				System.out.println("verificando se contato tem telefone");
				if (!contato.getFone().isEmpty()){
					System.out.println("setando o telefone para o contato");
					destinoLocal.setFone("(" + contato.getFone().get(0).getDdd()+")"+ contato.getFone().get(0).getFone());
					System.out.println("telefone: " + destinoLocal.getFone());
					//					this.listaContatos = new ArrayList<>();
				}
			}
			
			destinoLocal.setPermiteReducao(destino.getCliente().isPermiteReducao());
			if (destinoLocal.isPermiteReducao() && destino.getCliente().getPerRedBaseCalculo() != null) {
				if (destino.getCliente().getPerRedBaseCalculo().compareTo(new BigDecimal("0")) == 1){
					destinoLocal.setReducaoBase(destino.getCliente().getPerRedBaseCalculo().toString());
				}
			}
			System.out.println("consegui!");

		}else if (destino.getFornecedor() != null){

			if (destino.getFornecedor().getCnpj() == null ){
				destinoLocal.setCNPJCPF(destino.getFornecedor().getCpf());
				destinoLocal.setIndIEDest("2");
			}else{
				destinoLocal.setCNPJCPF(destino.getFornecedor().getCnpj());
				if (destino.getFornecedor().getInscEstadual().isEmpty()){
					destinoLocal.setIndIEDest("2");
				}else{
					destinoLocal.setIndIEDest("1");
					destinoLocal.setIE(destino.getFornecedor().getInscEstadual());
					destinoLocal.setRegime(destino.getFornecedor().getEnquadramento());
				}
			}
			if (destino.getFornecedor().getPerRedBaseCalculo().compareTo(new BigDecimal("0")) == 1){
				destinoLocal.setReducaoBase(destino.getFornecedor().getPerRedBaseCalculo().toString());
			}
			destinoLocal.setXNome(removerAcentos(destino.getFornecedor().getRazaoSocial()));
			if (destino.getFornecedor().getSuframa() != null){
				destinoLocal.setISUF(destino.getFornecedor().getSuframa());
			}
			if (destino.getFornecedor().getEmailNFE().getEmail() != null){
				emailLocal = this.emailDao.pegaEmailNfe(null, destino.getFornecedor(),null,null,null,destino.getFornecedor().getIdEmpresa());
				destinoLocal.setEmail(emailLocal.getEmail());
				System.out.println(destinoLocal.getEmail());
			}
			//destino.setEmail(destino.getFornecedor().getEmailNFE().getEmail());
			if (destino.getFornecedor().getEndereco().getLogradouro() == "" || destino.getFornecedor().getEndereco().getLogradouro().isEmpty()) {
				destinoLocal.setXLgr(removerAcentos(destino.getFornecedor().getEndereco().getEndereco().getLogra()));
				destinoLocal.setXBairro(removerAcentos(destino.getFornecedor().getEndereco().getEndereco().getBairro()));
			}else {
				destinoLocal.setXLgr(removerAcentos(destino.getFornecedor().getEndereco().getLogradouro()));
				destinoLocal.setXBairro(removerAcentos(destino.getFornecedor().getEndereco().getBairro()));
			}
			System.out.println(destinoLocal.getXLgr());
			destinoLocal.setNro(destino.getFornecedor().getEndereco().getNumero());
			System.out.println(destinoLocal.getNro());
			destinoLocal.setXCpl(removerAcentos(destino.getFornecedor().getEndereco().getComplemento()));
			System.out.println(destinoLocal.getXCpl());
			destinoLocal.setCMun(destino.getFornecedor().getEndereco().getEndereco().getIbge());
			System.out.println(destinoLocal.getCMun());
			destinoLocal.setXMun(removerAcentos(destino.getFornecedor().getEndereco().getEndereco().getLocalidade()));
			System.out.println(destinoLocal.getXMun());
			destinoLocal.setUf(destino.getFornecedor().getEndereco().getEndereco().getUf().name());
			System.out.println(destinoLocal.getUf());
			destinoLocal.setCUF(destino.getFornecedor().getEndereco().getEndereco().getUf().getCod());
			//			destinoLocal.setCep(destino.getFornecedor().getEndereco().getEndereco().getCep());
			cep = destino.getFornecedor().getEndereco().getEndereco().getCep();
			destinoLocal.setCep(cep.replaceAll("-", ""));
			System.out.println(destinoLocal.getCep());
			destinoLocal.setCPais(destino.getFornecedor().getPais().getCodigo().toString());
			destinoLocal.setXPais(removerAcentos(destino.getFornecedor().getPais().getNome()));
			System.out.println(destinoLocal.getXPais());
			System.out.println("vou iniciar a conversao do telefone");
			listaContatos = this.contatoDao.procuraContatoDestino(null,destino.getFornecedor(),null,null, null);
			if (!listaContatos.isEmpty() ){
				System.out.println("estou dentro da lista != vazio");
				contato = listaContatos.get(0);
				contato.setFone(this.telefoneDao.listaFonePorContato(contato));
				System.out.println("verificando se contato tem telefone");
				if (!contato.getFone().isEmpty()){
					System.out.println("setando o telefone para o contato");
					destinoLocal.setFone("(" + contato.getFone().get(0).getDdd()+")"+ contato.getFone().get(0).getFone());
					System.out.println("telefone: " + destinoLocal.getFone());
					//					this.listaContatos = new ArrayList<>();
				}
			}
			System.out.println("consegui!");

		}else if(destino.getEmpresa() != null){

			if (destino.getEmpresa().getCnpj() != null){
				destinoLocal.setCNPJCPF(destino.getEmpresa().getCnpj());
				if (destino.getEmpresa().getInscEstadual() == null){
					destinoLocal.setIndIEDest("2");
				}else{
					destinoLocal.setIndIEDest("1");
					destinoLocal.setIE(destino.getEmpresa().getInscEstadual());
					destinoLocal.setRegime(destino.getEmpresa().getEnquadramento());
				}
			}
			destinoLocal.setXNome(removerAcentos(destino.getEmpresa().getRazaoSocial()));
			if ( destino.getEmpresa().getSuframa() != null ){
				destinoLocal.setISUF(destino.getEmpresa().getSuframa());
			}
			//destino.setEmail(destino.getEmpresa().getEmailNFE().getEmail());
			
			if (destino.getEmpresa().getEndereco().getLogradouro() == "" || destino.getEmpresa().getEndereco().getLogradouro().isEmpty()) {
				destinoLocal.setXLgr(removerAcentos(destino.getEmpresa().getEndereco().getEndereco().getLogra()));
				destinoLocal.setXBairro(removerAcentos(destino.getEmpresa().getEndereco().getEndereco().getBairro()));
			}else {
				destinoLocal.setXLgr(removerAcentos(destino.getEmpresa().getEndereco().getLogradouro()));
				destinoLocal.setXBairro(removerAcentos(destino.getEmpresa().getEndereco().getBairro()));
			}
			destinoLocal.setNro(destino.getEmpresa().getEndereco().getNumero());
			destinoLocal.setXCpl(removerAcentos(destino.getEmpresa().getEndereco().getComplemento()));
			destinoLocal.setCMun(destino.getEmpresa().getEndereco().getEndereco().getIbge());
			destinoLocal.setXMun(removerAcentos(destino.getEmpresa().getEndereco().getEndereco().getLocalidade()));
			destinoLocal.setUf(destino.getEmpresa().getEndereco().getEndereco().getUf().name().toString());
			cep = destino.getEmpresa().getEndereco().getEndereco().getCep();
			destinoLocal.setCep(cep.replace("-", ""));
			destinoLocal.setCPais(destino.getEmpresa().getPais().getCodigo().toString());
			destinoLocal.setXPais(removerAcentos(destino.getEmpresa().getPais().getNome()));
			System.out.println("Inicio conversao telefone");
			listaContatos = this.contatoDao.procuraContatoDestino(null,null,null,destino.getEmpresa(), null);
			if (!listaContatos.isEmpty()){
				contato = listaContatos.get(0);
				contato.setFone(this.telefoneDao.listaFonePorContato(contato));
				if (!contato.getFone().isEmpty()){
					destinoLocal.setFone("(" + contato.getFone().get(0).getDdd()+")"+ contato.getFone().get(0).getFone());
					System.out.println("telefone: " + destinoLocal.getFone());
					//					listaContatos = new ArrayList<>();
				}
			}
			System.out.println("consegui!");

		}else if(destino.getFilial() != null){

			if (destino.getFilial().getCnpj() != null){
				destinoLocal.setCNPJCPF(destino.getFilial().getCnpj());
				if (destino.getFilial().getInscEstadual() == null){
					destinoLocal.setIndIEDest("2");
				}else{
					destinoLocal.setIndIEDest("1");
					destinoLocal.setIE(destino.getFilial().getInscEstadual());
					destinoLocal.setRegime(destino.getFilial().getEnquadramento());
				}
			}
			destinoLocal.setXNome(removerAcentos(destino.getFilial().getRazaoSocial()));
			if (destino.getFilial().getSuframa()!= null){
				destinoLocal.setISUF(destino.getFilial().getSuframa());
			}
			//destino.setEmail(destino.getFilial().getEmailNFE().getEmail());
			
			if (destino.getFilial().getEndereco().getLogradouro() == "" || destino.getFilial().getEndereco().getLogradouro().isEmpty()) {
				destinoLocal.setXLgr(removerAcentos(destino.getFilial().getEndereco().getEndereco().getLogra()));
				destinoLocal.setXBairro(removerAcentos(destino.getFilial().getEndereco().getEndereco().getBairro()));
			}else {
				destinoLocal.setXLgr(removerAcentos(destino.getFilial().getEndereco().getLogradouro()));
				destinoLocal.setXBairro(removerAcentos(destino.getFilial().getEndereco().getBairro()));
			}
			destinoLocal.setNro(destino.getFilial().getEndereco().getNumero());
			destinoLocal.setXCpl(removerAcentos(destino.getFilial().getEndereco().getComplemento()));
			destinoLocal.setCMun(destino.getFilial().getEndereco().getEndereco().getIbge());
			destinoLocal.setXMun(removerAcentos(destino.getFilial().getEndereco().getEndereco().getLocalidade()));
			destinoLocal.setUf(destino.getFilial().getEndereco().getEndereco().getUf().name());
			cep = destino.getFilial().getEndereco().getEndereco().getCep();
			destinoLocal.setCep(cep.replace("-", ""));
			destinoLocal.setCPais(destino.getFilial().getPais().getCodigo().toString());
			destinoLocal.setXPais(removerAcentos(destino.getFilial().getPais().getNome()));
			listaContatos = this.contatoDao.procuraContatoDestino(null,null,null,null, destino.getFilial());
			if (!listaContatos.isEmpty()){
				contato = listaContatos.get(0);
				contato.setFone(this.telefoneDao.listaFonePorContato(contato));
				if (!contato.getFone().isEmpty()){
					destinoLocal.setFone("(" + contato.getFone().get(0).getDdd()+")"+ contato.getFone().get(0).getFone());
					System.out.println("telefone: " + destinoLocal.getFone());
					//					listaContatos = new ArrayList<>();
				}
			}
			System.out.println("consegui!");

		}else if(destino.getColaborador() != null){

			destinoLocal.setCNPJCPF(destino.getColaborador().getCpf());
			destinoLocal.setIndIEDest("2");
			destinoLocal.setXNome(removerAcentos(destino.getColaborador().getNome()));
			//destino.setEmail(destino.getColaborador().getEmailNFE().getEmail());
			
			if (destino.getColaborador().getEndereco().getLogradouro() == "" || destino.getColaborador().getEndereco().getLogradouro().isEmpty()) {
				destinoLocal.setXLgr(removerAcentos(destino.getColaborador().getEndereco().getEndereco().getLogra()));
				destinoLocal.setXBairro(removerAcentos(destino.getColaborador().getEndereco().getEndereco().getBairro()));
			}else {
				destinoLocal.setXLgr(removerAcentos(destino.getColaborador().getEndereco().getLogradouro()));
				destinoLocal.setXBairro(removerAcentos(destino.getColaborador().getEndereco().getBairro()));
			}
			destinoLocal.setNro(destino.getColaborador().getEndereco().getNumero());
			destinoLocal.setXCpl(removerAcentos(destino.getColaborador().getEndereco().getComplemento()));
			destinoLocal.setCMun(destino.getColaborador().getEndereco().getEndereco().getIbge());
			destinoLocal.setXMun(removerAcentos(destino.getColaborador().getEndereco().getEndereco().getLocalidade()));
			destinoLocal.setUf(destino.getColaborador().getEndereco().getEndereco().getUf().name());
			cep = destino.getColaborador().getEndereco().getEndereco().getCep();
			destinoLocal.setCep(cep.replace("-", ""));
			destinoLocal.setCPais(destino.getColaborador().getPais().getCodigo().toString());
			destinoLocal.setXPais(removerAcentos(destino.getColaborador().getPais().getNome()));
			listaContatos = this.contatoDao.procuraContatoDestino(null,null,destino.getColaborador(),null, null);
			if (!listaContatos.isEmpty()){
				contato = listaContatos.get(0);
				contato.setFone(this.telefoneDao.listaFonePorContato(contato));
				if (!contato.getFone().isEmpty()){
					destinoLocal.setFone("(" + contato.getFone().get(0).getDdd()+")"+ contato.getFone().get(0).getFone());
					System.out.println("telefone: " + destinoLocal.getFone());
					//					listaContatos = new ArrayList<>();
				}
			}
			System.out.println("consegui!");
		}
		return destinoLocal;
	}

	public Destino preencheDestino(Nfe nfe){
		System.out.println("Estou no preenche Destino");
		Destino destinoLocal = new Destino();
		Email emailLocal = new Email();
		List<Contato> listaContatos = new ArrayList<>();
		Contato contato = new Contato();
		String cep;

		if (nfe.getDestino().getCliente() != null){
			System.out.println("Estou dentro de Destino = CLiente");
			if (nfe.getDestino().getCliente().getTipoCliente().equals(TipoCliente.Est)){
				destinoLocal.setIdEstrangeiro(nfe.getDestino().getCliente().getIdEstrangeiro());
				System.out.println(destinoLocal.getIdEstrangeiro());
				destinoLocal.setIndIEDest("9");
			}
			System.out.println("vou verificar se cnpj = null");

			if (nfe.getDestino().getCliente().getCnpj() == null || nfe.getDestino().getCliente().getTipoCliente().equals(TipoCliente.Est) ){
				System.out.println("estou na linha 1859 - cnpj = vazio");
				destinoLocal.setCNPJCPF(nfe.getDestino().getCliente().getCpf());
				System.out.println(destinoLocal.getCNPJCPF());
				destinoLocal.setIndIEDest("9");
			}else{
				destinoLocal.setCNPJCPF(nfe.getDestino().getCliente().getCnpj());
				System.out.println(destinoLocal.getCNPJCPF());
				if (nfe.getDestino().getCliente().getInscEstadual() == null){
					if ((nfe.getDestino().getCliente().getEstado().equals(Uf.AM)) ||
							(nfe.getDestino().getCliente().getEstado().equals(Uf.BA)) ||
							(nfe.getDestino().getCliente().getEstado().equals(Uf.CE)) ||
							(nfe.getDestino().getCliente().getEstado().equals(Uf.GO)) ||
							(nfe.getDestino().getCliente().getEstado().equals(Uf.MG)) ||
							(nfe.getDestino().getCliente().getEstado().equals(Uf.MS)) ||
							(nfe.getDestino().getCliente().getEstado().equals(Uf.MT)) ||
							(nfe.getDestino().getCliente().getEstado().equals(Uf.PA)) ||
							(nfe.getDestino().getCliente().getEstado().equals(Uf.PE)) ||
							(nfe.getDestino().getCliente().getEstado().equals(Uf.RN)) ||
							(nfe.getDestino().getCliente().getEstado().equals(Uf.SE)) ||
							(nfe.getDestino().getCliente().getEstado().equals(Uf.SP))){

						destinoLocal.setIndIEDest("9");
					}else{
						destinoLocal.setIndIEDest("2");
					}
				}else{
					destinoLocal.setIndIEDest("1");
					destinoLocal.setIE(nfe.getDestino().getCliente().getInscEstadual());
					System.out.println(destinoLocal.getIE());
					destinoLocal.setRegime(nfe.getDestino().getCliente().getEnquadramento());
					if (nfe.getDestino().getCliente().getEnquadramento() == Enquadramento.SimplesNacional){
						emissor.setMensagemFisco(this.menInt.getMensagem102());
					}
					System.out.println(destinoLocal.getRegime());
				}
			}

			destinoLocal.setXNome(removerAcentos(nfe.getDestino().getCliente().getRazaoSocial()));
			System.out.println(destinoLocal.getXNome());
			System.out.println("Setando o suframa caso exista!");
			if ( nfe.getDestino().getCliente().getSuframa() != null){
				System.out.println("Estou no suframa dentro do if diferente de vazio!");
				destinoLocal.setISUF(nfe.getDestino().getCliente().getSuframa());
				System.out.println(destinoLocal.getISUF());
			}else{
				destinoLocal.setISUF("");
			}
			System.out.println("Setando o Email");
			if (nfe.getDestino().getCliente().getEmailNFE().getEmail() != null){
				emailLocal = this.emailDao.pegaEmailNfe(nfe.getDestino().getCliente(), null,null,null,null,nfe.getDestino().getCliente().getIdEmpresa());
				destinoLocal.setEmail(emailLocal.getEmail());
				System.out.println(destinoLocal.getEmail());
			}
			if (nfe.getDestino().getCliente().getEndereco().getLogradouro() == "" || nfe.getDestino().getCliente().getEndereco().getLogradouro().isEmpty()) {
				destinoLocal.setXLgr(removerAcentos(nfe.getDestino().getCliente().getEndereco().getEndereco().getLogra()));
				destinoLocal.setXBairro(removerAcentos(nfe.getDestino().getCliente().getEndereco().getEndereco().getBairro()));
			}else {
				destinoLocal.setXLgr(removerAcentos(nfe.getDestino().getCliente().getEndereco().getLogradouro()));
				destinoLocal.setXBairro(removerAcentos(nfe.getDestino().getCliente().getEndereco().getBairro()));
			}
			
			System.out.println(destinoLocal.getXLgr());
			destinoLocal.setNro(nfe.getDestino().getCliente().getEndereco().getNumero());
			System.out.println(destinoLocal.getNro());
			destinoLocal.setXCpl(removerAcentos(nfe.getDestino().getCliente().getEndereco().getComplemento()));
			System.out.println(destinoLocal.getXCpl());
			destinoLocal.setCMun(nfe.getDestino().getCliente().getEndereco().getEndereco().getIbge());
			System.out.println(destinoLocal.getCMun());
			destinoLocal.setXMun(removerAcentos(nfe.getDestino().getCliente().getEndereco().getEndereco().getLocalidade()));
			System.out.println(destinoLocal.getXMun());
			destinoLocal.setUf(nfe.getDestino().getCliente().getEndereco().getEndereco().getUf().name());
			destinoLocal.setCUF(nfe.getDestino().getCliente().getEndereco().getEndereco().getUf().getCod());
			System.out.println(destinoLocal.getUf());
			//			destinoLocal.setCep(nfe.getDestino().getCliente().getEndereco().getEndereco().getCep());
			cep = nfe.getDestino().getCliente().getEndereco().getEndereco().getCep();
			destinoLocal.setCep(cep.replace("-", ""));
			System.out.println(destinoLocal.getCep());
			destinoLocal.setCPais(nfe.getDestino().getCliente().getPais().getCodigo().toString());
			System.out.println(destinoLocal.getCPais());
			destinoLocal.setXPais(nfe.getDestino().getCliente().getPais().getNome());
			System.out.println(destinoLocal.getXPais());
			System.out.println("vou iniciar a conversao do telefone");
			listaContatos = this.contatoDao.procuraContatoDestino(nfe.getDestino().getCliente(),null,null,null, null);
			if (!listaContatos.isEmpty() ){
				System.out.println("estou dentro da lista != vazio");
				contato = listaContatos.get(0);
				contato.setFone(this.telefoneDao.listaFonePorContato(contato));
				System.out.println("verificando se contato tem telefone");
				if (!contato.getFone().isEmpty()){
					System.out.println("setando o telefone para o contato");
					destinoLocal.setFone("(" + contato.getFone().get(0).getDdd()+")"+ contato.getFone().get(0).getFone());
					System.out.println("telefone: " + destinoLocal.getFone());
					//					this.listaContatos = new ArrayList<>();
				}
			}
			destinoLocal.setPermiteReducao(nfe.getDestino().getCliente().isPermiteReducao());
			if (nfe.getDestino().getCliente().getPerRedBaseCalculo().compareTo(new BigDecimal("0")) == 1){
				destinoLocal.setReducaoBase(nfe.getDestino().getCliente().getPerRedBaseCalculo().toString());
			}
			System.out.println("consegui!");

		}else if (nfe.getDestino().getFornecedor() != null){

			if (nfe.getDestino().getFornecedor().getCnpj() == null ){
				if (nfe.isImportacao()) {
					if (nfe.getDestino().getFornecedor().getTipoCliente().equals(TipoCliente.Est)){
						if (nfe.getDestino().getFornecedor().getIdEstrangeiro() == null) {
							destinoLocal.setIdEstrangeiro(null);
							destinoLocal.setIndIEDest("9");
						}else {
							destinoLocal.setIdEstrangeiro(nfe.getDestino().getFornecedor().getIdEstrangeiro());
							System.out.println(destinoLocal.getIdEstrangeiro());
							destinoLocal.setIndIEDest("9");
						}
					}

				}else {
					destinoLocal.setCNPJCPF(nfe.getDestino().getFornecedor().getCpf());
					destinoLocal.setIndIEDest("2");
				}
			}else{
				destinoLocal.setCNPJCPF(nfe.getDestino().getFornecedor().getCnpj());
				if (nfe.getDestino().getFornecedor().getInscEstadual().isEmpty()){
					if ((nfe.getDestino().getFornecedor().getEstado().equals(Uf.AM)) ||
							(nfe.getDestino().getFornecedor().getEstado().equals(Uf.BA)) ||
							(nfe.getDestino().getFornecedor().getEstado().equals(Uf.CE)) ||
							(nfe.getDestino().getFornecedor().getEstado().equals(Uf.GO)) ||
							(nfe.getDestino().getFornecedor().getEstado().equals(Uf.MG)) ||
							(nfe.getDestino().getFornecedor().getEstado().equals(Uf.MS)) ||
							(nfe.getDestino().getFornecedor().getEstado().equals(Uf.MT)) ||
							(nfe.getDestino().getFornecedor().getEstado().equals(Uf.PA)) ||
							(nfe.getDestino().getFornecedor().getEstado().equals(Uf.PE)) ||
							(nfe.getDestino().getFornecedor().getEstado().equals(Uf.RN)) ||
							(nfe.getDestino().getFornecedor().getEstado().equals(Uf.SE)) ||
							(nfe.getDestino().getFornecedor().getEstado().equals(Uf.SP))){

						destinoLocal.setIndIEDest("9");
					}else{
						destinoLocal.setIndIEDest("2");
					}
				}else{
					destinoLocal.setIndIEDest("1");
					destinoLocal.setIE(nfe.getDestino().getFornecedor().getInscEstadual());
					destinoLocal.setRegime(nfe.getDestino().getFornecedor().getEnquadramento());
				}
			}
			destinoLocal.setPermiteReducao(nfe.getDestino().getFornecedor().isPermiteReducao());
			if (nfe.getDestino().getFornecedor().getPerRedBaseCalculo().compareTo(new BigDecimal("0")) == 1){
				destinoLocal.setReducaoBase(nfe.getDestino().getFornecedor().getPerRedBaseCalculo().toString());
			}
			destinoLocal.setXNome(removerAcentos(nfe.getDestino().getFornecedor().getRazaoSocial()));
			if (nfe.getDestino().getFornecedor().getSuframa() != null){
				destinoLocal.setISUF(nfe.getDestino().getFornecedor().getSuframa());
			}else{
				destinoLocal.setISUF("");
			}
			if (nfe.getDestino().getFornecedor().getEmailNFE().getEmail() != null){
				emailLocal = this.emailDao.pegaEmailNfe(null, nfe.getDestino().getFornecedor(),null,null,null,nfe.getDestino().getFornecedor().getIdEmpresa());
				destinoLocal.setEmail(emailLocal.getEmail());
				System.out.println(destinoLocal.getEmail());
			}
			//destino.setEmail(nfe.getDestino().getFornecedor().getEmailNFE().getEmail());
			
			if (nfe.getDestino().getFornecedor().getEndereco().getLogradouro() == "" || nfe.getDestino().getFornecedor().getEndereco().getLogradouro().isEmpty()) {
				destinoLocal.setXLgr(removerAcentos(nfe.getDestino().getFornecedor().getEndereco().getEndereco().getLogra()));
				destinoLocal.setXBairro(removerAcentos(nfe.getDestino().getFornecedor().getEndereco().getEndereco().getBairro()));
			}else {
				destinoLocal.setXLgr(removerAcentos(nfe.getDestino().getFornecedor().getEndereco().getLogradouro()));
				destinoLocal.setXBairro(removerAcentos(nfe.getDestino().getFornecedor().getEndereco().getBairro()));
			}
			System.out.println(destinoLocal.getXLgr());
			destinoLocal.setNro(nfe.getDestino().getFornecedor().getEndereco().getNumero());
			System.out.println(destinoLocal.getNro());
			destinoLocal.setXCpl(removerAcentos(nfe.getDestino().getFornecedor().getEndereco().getComplemento()));
			System.out.println(destinoLocal.getXCpl());
			destinoLocal.setCMun(nfe.getDestino().getFornecedor().getEndereco().getEndereco().getIbge());
			System.out.println(destinoLocal.getCMun());
			destinoLocal.setXMun(removerAcentos(nfe.getDestino().getFornecedor().getEndereco().getEndereco().getLocalidade()));
			System.out.println(destinoLocal.getXMun());
			destinoLocal.setUf(nfe.getDestino().getFornecedor().getEndereco().getEndereco().getUf().name());
			System.out.println(destinoLocal.getUf());
			destinoLocal.setCUF(nfe.getDestino().getFornecedor().getEndereco().getEndereco().getUf().getCod());
			//			destinoLocal.setCep(nfe.getDestino().getFornecedor().getEndereco().getEndereco().getCep());
			cep = nfe.getDestino().getFornecedor().getEndereco().getEndereco().getCep();
			destinoLocal.setCep(cep.replaceAll("-", ""));
			System.out.println(destinoLocal.getCep());
			destinoLocal.setCPais(nfe.getDestino().getFornecedor().getPais().getCodigo().toString());
			destinoLocal.setXPais(removerAcentos(nfe.getDestino().getFornecedor().getPais().getNome()));
			System.out.println(destinoLocal.getXPais());
			System.out.println("vou iniciar a conversao do telefone");
			listaContatos = this.contatoDao.procuraContatoDestino(null,nfe.getDestino().getFornecedor(),null,null, null);
			if (!listaContatos.isEmpty() ){
				System.out.println("estou dentro da lista != vazio");
				contato = listaContatos.get(0);
				contato.setFone(this.telefoneDao.listaFonePorContato(contato));
				System.out.println("verificando se contato tem telefone");
				if (!contato.getFone().isEmpty()){
					System.out.println("setando o telefone para o contato");
					destinoLocal.setFone("(" + contato.getFone().get(0).getDdd()+")"+ contato.getFone().get(0).getFone());
					System.out.println("telefone: " + destinoLocal.getFone());
					//					this.listaContatos = new ArrayList<>();
				}
			}
			System.out.println("consegui!");

		}else if(nfe.getDestino().getEmpresa() != null){

			if (nfe.getDestino().getEmpresa().getCnpj() != null){
				destinoLocal.setCNPJCPF(nfe.getDestino().getEmpresa().getCnpj());
				if (nfe.getDestino().getEmpresa().getInscEstadual() == null){
					destinoLocal.setIndIEDest("2");
				}else{
					destinoLocal.setIndIEDest("1");
					destinoLocal.setIE(nfe.getDestino().getEmpresa().getInscEstadual());
					destinoLocal.setRegime(nfe.getDestino().getEmpresa().getEnquadramento());
				}
			}
			destinoLocal.setXNome(removerAcentos(nfe.getDestino().getEmpresa().getRazaoSocial()));
			if (!nfe.getDestino().getEmpresa().getSuframa().isEmpty()){
				destinoLocal.setISUF(nfe.getDestino().getEmpresa().getSuframa());
			}
			//destino.setEmail(nfe.getDestino().getEmpresa().getEmailNFE().getEmail());
			
			if (nfe.getDestino().getEmpresa().getEndereco().getLogradouro() == "" || nfe.getDestino().getEmpresa().getEndereco().getLogradouro().isEmpty()) {
				destinoLocal.setXLgr(removerAcentos(nfe.getDestino().getEmpresa().getEndereco().getEndereco().getLogra()));
				destinoLocal.setXBairro(removerAcentos(nfe.getDestino().getEmpresa().getEndereco().getEndereco().getBairro()));
			}else {
				destinoLocal.setXLgr(removerAcentos(nfe.getDestino().getEmpresa().getEndereco().getLogradouro()));
				destinoLocal.setXBairro(removerAcentos(nfe.getDestino().getEmpresa().getEndereco().getBairro()));
			}
			destinoLocal.setNro(nfe.getDestino().getEmpresa().getEndereco().getNumero());
			destinoLocal.setXCpl(removerAcentos(nfe.getDestino().getEmpresa().getEndereco().getComplemento()));
			destinoLocal.setCMun(nfe.getDestino().getEmpresa().getEndereco().getEndereco().getIbge());
			destinoLocal.setXMun(removerAcentos(nfe.getDestino().getEmpresa().getEndereco().getEndereco().getLocalidade()));
			destinoLocal.setUf(nfe.getDestino().getEmpresa().getEndereco().getEndereco().getUf().name().toString());
			cep = nfe.getDestino().getEmpresa().getEndereco().getEndereco().getCep();
			destinoLocal.setCep(cep.replace("-", ""));
			destinoLocal.setCPais(nfe.getDestino().getEmpresa().getPais().getCodigo().toString());
			destinoLocal.setXPais(removerAcentos(nfe.getDestino().getEmpresa().getPais().getNome()));
			System.out.println("Inicio conversao telefone");
			listaContatos = this.contatoDao.procuraContatoDestino(null,null,null,nfe.getDestino().getEmpresa(), null);
			if (!listaContatos.isEmpty()){
				contato = listaContatos.get(0);
				contato.setFone(this.telefoneDao.listaFonePorContato(contato));
				if (!contato.getFone().isEmpty()){
					destinoLocal.setFone("(" + contato.getFone().get(0).getDdd()+")"+ contato.getFone().get(0).getFone());
					System.out.println("telefone: " + destinoLocal.getFone());
					//					listaContatos = new ArrayList<>();
				}
			}
			System.out.println("consegui!");

		}else if(nfe.getDestino().getFilial() != null){

			if (nfe.getDestino().getFilial().getCnpj() != null){
				destinoLocal.setCNPJCPF(nfe.getDestino().getFilial().getCnpj());
				if (nfe.getDestino().getFilial().getInscEstadual() == null){
					destinoLocal.setIndIEDest("2");
				}else{
					destinoLocal.setIndIEDest("1");
					destinoLocal.setIE(nfe.getDestino().getFilial().getInscEstadual());
					destinoLocal.setRegime(nfe.getDestino().getFilial().getEnquadramento());
				}
			}
			destinoLocal.setXNome(removerAcentos(nfe.getDestino().getFilial().getRazaoSocial()));
			if (nfe.getDestino().getFilial().getSuframa()!= null){
				destinoLocal.setISUF(nfe.getDestino().getFilial().getSuframa());
			}
			//destino.setEmail(nfe.getDestino().getFilial().getEmailNFE().getEmail());
			
			if (nfe.getDestino().getFilial().getEndereco().getLogradouro() == "" || nfe.getDestino().getFilial().getEndereco().getLogradouro().isEmpty()) {
				destinoLocal.setXLgr(removerAcentos(nfe.getDestino().getFilial().getEndereco().getEndereco().getLogra()));
				destinoLocal.setXBairro(removerAcentos(nfe.getDestino().getFilial().getEndereco().getEndereco().getBairro()));
			}else {
				destinoLocal.setXLgr(removerAcentos(nfe.getDestino().getFilial().getEndereco().getLogradouro()));
				destinoLocal.setXBairro(removerAcentos(nfe.getDestino().getFilial().getEndereco().getBairro()));
			}
			destinoLocal.setNro(nfe.getDestino().getFilial().getEndereco().getNumero());
			destinoLocal.setXCpl(removerAcentos(nfe.getDestino().getFilial().getEndereco().getComplemento()));
			destinoLocal.setCMun(nfe.getDestino().getFilial().getEndereco().getEndereco().getIbge());
			destinoLocal.setXMun(removerAcentos(nfe.getDestino().getFilial().getEndereco().getEndereco().getLocalidade()));
			destinoLocal.setUf(nfe.getDestino().getFilial().getEndereco().getEndereco().getUf().name());
			cep = nfe.getDestino().getFilial().getEndereco().getEndereco().getCep();
			destinoLocal.setCep(cep.replace("-", ""));
			destinoLocal.setCPais(nfe.getDestino().getFilial().getPais().getCodigo().toString());
			destinoLocal.setXPais(removerAcentos(nfe.getDestino().getFilial().getPais().getNome()));
			listaContatos = this.contatoDao.procuraContatoDestino(null,null,null,null, nfe.getDestino().getFilial());
			if (!listaContatos.isEmpty()){
				contato = listaContatos.get(0);
				contato.setFone(this.telefoneDao.listaFonePorContato(contato));
				if (!contato.getFone().isEmpty()){
					destinoLocal.setFone("(" + contato.getFone().get(0).getDdd()+")"+ contato.getFone().get(0).getFone());
					System.out.println("telefone: " + destinoLocal.getFone());
					//					listaContatos = new ArrayList<>();
				}
			}
			System.out.println("consegui!");

		}else if(nfe.getDestino().getColaborador() != null){

			destinoLocal.setCNPJCPF(nfe.getDestino().getColaborador().getCpf());
			destinoLocal.setIndIEDest("2");
			destinoLocal.setXNome(removerAcentos(nfe.getDestino().getColaborador().getNome()));
			//destino.setEmail(nfe.getDestino().getColaborador().getEmailNFE().getEmail());
			
			if (nfe.getDestino().getColaborador().getEndereco().getLogradouro() == "" || nfe.getDestino().getColaborador().getEndereco().getLogradouro().isEmpty()) {
				destinoLocal.setXLgr(removerAcentos(nfe.getDestino().getColaborador().getEndereco().getEndereco().getLogra()));
				destinoLocal.setXBairro(removerAcentos(nfe.getDestino().getColaborador().getEndereco().getEndereco().getBairro()));
			}else {
				destinoLocal.setXLgr(removerAcentos(nfe.getDestino().getColaborador().getEndereco().getLogradouro()));
				destinoLocal.setXBairro(removerAcentos(nfe.getDestino().getColaborador().getEndereco().getBairro()));
			}
			destinoLocal.setNro(nfe.getDestino().getColaborador().getEndereco().getNumero());
			destinoLocal.setXCpl(removerAcentos(nfe.getDestino().getColaborador().getEndereco().getComplemento()));
			destinoLocal.setCMun(nfe.getDestino().getColaborador().getEndereco().getEndereco().getIbge());
			destinoLocal.setXMun(removerAcentos(nfe.getDestino().getColaborador().getEndereco().getEndereco().getLocalidade()));
			destinoLocal.setUf(nfe.getDestino().getColaborador().getEndereco().getEndereco().getUf().name());
			cep = nfe.getDestino().getColaborador().getEndereco().getEndereco().getCep();
			destinoLocal.setCep(cep.replace("-", ""));
			destinoLocal.setCPais(nfe.getDestino().getColaborador().getPais().getCodigo().toString());
			destinoLocal.setXPais(removerAcentos(nfe.getDestino().getColaborador().getPais().getNome()));
			listaContatos = this.contatoDao.procuraContatoDestino(null,null,nfe.getDestino().getColaborador(),null, null);
			if (!listaContatos.isEmpty()){
				contato = listaContatos.get(0);
				contato.setFone(this.telefoneDao.listaFonePorContato(contato));
				if (!contato.getFone().isEmpty()){
					destinoLocal.setFone("(" + contato.getFone().get(0).getDdd()+")"+ contato.getFone().get(0).getFone());
					System.out.println("telefone: " + destinoLocal.getFone());
					//					listaContatos = new ArrayList<>();
				}
			}
			System.out.println("consegui!");
		}
		return destinoLocal;
	}


	public static String removerAcentos(String str) {
		if (str == null){
			return " ";
		}else{
			return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
		}
	}
	// Modulo Sat
	public String criarArqIniSatMaqRemota(DadosDeConexaoSocket dados,String nomeArquivo, CFe sat,String versao) throws IOException{
		try{
			System.out.println("Iniciando a montagem do arquivo.ini Sat para acbr");
			int contador = 001;
			int secunContador = 001;
			String infArquivo="";

			System.out.println("inicio de conversao Emissor");
			this.emissor = preencheEmissor(sat.getEmitente());
			System.out.println("Fim do emissor inicio da conversao do destino");
			//			this.destino = preencheDestino(nota);
			System.out.println("Fim do destino");

			String infCfe = "[infCFe]"+"\n";
//			infCfe = infCfe+"versao="+ApplicationUtils.getConfiguration("versao.sat")+"\n";
			infCfe = infCfe+"versao="+versao+"\n";

			String ide = "[Identificacao]" + "\n";
			ide = ide + "CNPJ=" + ApplicationUtils.getConfiguration("cnpj.software") +"\n" +
					"signAC=" + this.emissor.getAssinaturaSat() + "\n" +
					"numeroCaixa="+"1"+ "\n";
			System.out.println("Preenchendo Emitente 2718");
			String emitente = "[Emitente]"+"\n" ;
				if (ApplicationUtils.getConfiguration("sat.prod").equalsIgnoreCase("0")) {
					emitente = emitente +
					"CNPJ="+ "11111111111111" + "\n" +
					"IE=" + "111111111111" + "\n" ;
				}else {
					emitente = emitente + 
					"CNPJ="+ CpfCnpjUtils.retiraCaracteresEspeciais(this.emissor.getCnpjCpf()) + "\n" +
					"IE=" +  CpfCnpjUtils.retiraCaracteresEspeciais(this.emissor.getIe()) + "\n" ;
				}
				emitente = emitente +
					//					IM=111111 inscricao municipal
					"indRatISSQN=" + "N" + "\n" +
					"xNome=" + this.emissor.getXNome() + "\n" +
					"xFant=" + this.emissor.getXFant() + "\n" +
					"cRegTrib=" + this.emissor.getRegime().getCod() + "\n" + 
					//					cRegTribISSQN=
					"xLgr=" + this.emissor.getXLgr() + "\n" + 
					"nro=" + this.emissor.getNro() + "\n" +
					"xCpl=" + this.emissor.getXCpl() + "\n" + 
					"xBairro=" + this.emissor.getXBairro() + "\n" + 
					"xMun=" + this.emissor.getXMun() + "\n" +
					"CEP=" + this.emissor.getCep() + "\n" +
					"UF=" + this.emissor.getUf() + "\n";
			System.out.println("Fim do preenchimento 2735 Inicio preenche Destinatario");
			String destinatario = "[Destinatario]" + "\n";
			if (sat.getDestinatario().getCnpj() == null && sat.getDestinatario().getCpf()== null ) {
				destinatario = destinatario + 
						"CNPJCPF="  + "\n" ;
			}else {
				if (sat.getDestinatario().getCpf() == null) {
					destinatario = destinatario + 
							"CNPJCPF="  +sat.getDestinatario().getCnpj() + "\n" ;
				}else {
					destinatario = destinatario + 
							"CNPJCPF="  +sat.getDestinatario().getCpf() + "\n" ;
				}
			}
					if (sat.getDestinatario().getNome() != null) {
						destinatario = destinatario +
					"xNome=" + sat.getDestinatario().getNome() + "\n";
					}else {
						destinatario = destinatario +
								"xNome="+"\n";
					}
			System.out.println("Fim Destinatario");
			String entrega = "[Entrega]" + "\n" +
					"xLgr=" + /* pegar do pedido de venda recebido */ "\n" +
					"nro=" + /**/ "\n"+
					"xCpl=" + /**/ "\n" +
					"xBairro=" +/*Centro*/ "\n"+
					"xMun=" + /*Tatui*/"\n" +
					"UF=" + /*SP*/"\n";

			contador = 001;
			String todosOsProdutos = "";
			System.out.println("Inicio preenche produto 2762");
			// inicio do loop para preencher os itens do cupom fiscal
			for (ItemCFe item : sat.getListaItem()) {

				System.out.println("Inicio Item Sat ");
				if (contador >9 && contador <100 ){
					todosOsProdutos = "[Produto"+"0"+contador+"]"+"\n";
				}else if (contador <= 9){
					todosOsProdutos = "[Produto"+"00"+contador+"]"+"\n";
				}else{
					todosOsProdutos = "[Produto"+contador+"]"+"\n";
				}
				todosOsProdutos = todosOsProdutos +
						"cProd=" + item.getProduto().getReferencia() + "\n";
				//"infAdProd="
				//				if (!item.getProduto().getListaBarras().get(0).getBarras().isEmpty()){
				//					todosOsProdutos = todosOsProdutos +
				//							"cEAN=" + item.getProduto().getListaBarras().get(0).getBarras() + "\n" ;
				//				}
				todosOsProdutos = todosOsProdutos +
						"xProd=" + item.getProduto().getDescricao() + "\n" +
						"NCM=" + item.getProduto().getNcm().getNcm() + "\n";
				System.out.println("if CEST - 2784");
				if (item.getProduto().getNcm().isSt() && !item.getProduto().getNcm().getCest().isEmpty()){
					todosOsProdutos = todosOsProdutos +
							"CEST=" + item.getProduto().getNcm().getCest() + "\n";
				}
				System.out.println("preenchendo CFOP - 2789");
				todosOsProdutos = todosOsProdutos +
						"CFOP=" + item.getCfopItem().getCfop() + "\n" +
						"uCom=" + item.getUnidade() + "\n" +
						//					Combustivel=0
						"qCom=" + item.getQuantidade().toString() + "\n" +
						"vUnCom=" + item.getValorUnitario().toString() + "\n" +

								//					vProd=
								"indRegra=A" + "\n"+ //arredondamento
								"vDesc=" + item.getDesconto().toString() + "\n"+
								"vOutro=" + item.getValorDespesas().toString() + "\n"+
								"vItem12741=" + item.getValorTotalTributoItem().toString() + "\n" +
								//					vItem=
								"vRatDesc=" + item.getVRatDesc().toString() + "\n"+
								"vRatAcr=" + item.getVRatAcr().toString() + "\n";
				if (contador >9 && contador <100 ){
					todosOsProdutos = todosOsProdutos +"[ObsFiscoDet"+"0"+contador+ "0" + contador+"]"+"\n";
				}else if (contador <= 9){
					todosOsProdutos = todosOsProdutos +"[ObsFiscoDet"+"00"+contador+"00"+contador+"]"+"\n";
				}else{
					todosOsProdutos = todosOsProdutos +"[ObsFiscoDet"+contador+contador+"]"+"\n";
				}
				System.out.println("inicio OBSFISCO");
				todosOsProdutos = todosOsProdutos +
						"xCampoDet=" + "\n" +
						"xTextoDet=" + "\n";
				if (contador >9 && contador <100 ){
					todosOsProdutos = todosOsProdutos +"[ICMS"+"0"+contador+"]"+"\n";
				}else if (contador <= 9){
					todosOsProdutos = todosOsProdutos +"[ICMS"+"00"+contador+"]"+"\n";
				}else{
					todosOsProdutos = todosOsProdutos +"[ICMS"+contador+"]"+"\n";
				}
				System.out.println("Inicio ICMS");
				todosOsProdutos = todosOsProdutos +
						"Orig=" + item.getOrigem() + "\n" ;
				if (this.emissor.getRegime().equals(Enquadramento.Normal)){
					todosOsProdutos = todosOsProdutos +
							"CST=" + item.getCst() + "\n" ;
					if (item.getCst().equalsIgnoreCase("00") || item.getCst().equalsIgnoreCase("20") || item.getCst().equalsIgnoreCase("90")){
						todosOsProdutos = todosOsProdutos +
								"pICMS=" + item.getAliqIcmsSat().toString() + "\n";
					}
				}else{
					todosOsProdutos = todosOsProdutos +
							"CSOSN=" + item.getCst() + "\n" ;
					if (item.getCst().equalsIgnoreCase("900")){
						todosOsProdutos = todosOsProdutos +
								"pICMS=" + item.getAliqIcmsSat().toString() + "\n";
					}
				}

				//									vICMS=
				// PIS
				System.out.println("Inicio PIS");
				if (contador >9 && contador <100 ){
					todosOsProdutos = todosOsProdutos +"[PIS"+"0"+contador+"]"+"\n";
				}else if (contador <= 9){
					todosOsProdutos = todosOsProdutos +"[PIS"+"00"+contador+"]"+"\n";
				}else{
					todosOsProdutos = todosOsProdutos +"[PIS"+contador+"]"+"\n";
				}
				if ((new BigDecimal(item.getCstPis()).compareTo(new BigDecimal("01")) == 0) ||
						(new BigDecimal(item.getCstPis()).compareTo(new BigDecimal("02")) == 0) || 
						(new BigDecimal(item.getCstPis()).compareTo(new BigDecimal("05")) == 0)){
					todosOsProdutos = todosOsProdutos +
							"CST=" + item.getCstPis() + "\n" +
							"vBC=" + item.getBaseICMS().toString() + "\n"+
							"pPIS=" + item.getAliqPis().divide(new BigDecimal("100")).toString() + "\n" ;
				}else if ((new BigDecimal(item.getCstPis()).compareTo(new BigDecimal("03")) == 0)) {
					todosOsProdutos = todosOsProdutos +
							"CST=" + item.getCstPis() + "\n" +
							"qBCProd=" + item.getQuantidade().toString() + "\n" +
							"vAliqProd=" + item.getAliqPis().toString() + "\n" ;
					//									vPIS=
				}else if ((new BigDecimal(item.getCstPis()).compareTo(new BigDecimal("04")) == 0) ||
						(new BigDecimal(item.getCstPis()).compareTo(new BigDecimal("06")) == 0) || 
						(new BigDecimal(item.getCstPis()).compareTo(new BigDecimal("07")) == 0) ||
						(new BigDecimal(item.getCstPis()).compareTo(new BigDecimal("08")) == 0) ||
						(new BigDecimal(item.getCstPis()).compareTo(new BigDecimal("49")) == 0)||
						(new BigDecimal(item.getCstPis()).compareTo(new BigDecimal("09")) == 0)) {
					todosOsProdutos = todosOsProdutos +
							"CST=" + item.getCstPis() + "\n" ;
				}else if ((new BigDecimal(item.getCstPis()).compareTo(new BigDecimal("99")) == 0)) {
					todosOsProdutos = todosOsProdutos +
							"CST=" + item.getCstPis() + "\n" ;
					if (item.getTributo().getPis().getCalculo().equals(TipoCalculo.TP)){
						todosOsProdutos = todosOsProdutos +
								"vBC=" + item.getValorTotal().toString() + "\n"+
								"pPIS=" + item.getAliqPis().divide(new BigDecimal("100")).toString() + "\n";
					}else{
						todosOsProdutos = todosOsProdutos +
								"qBCProd=" + item.getQuantidade().toString() + "\n" +
								"vAliqProd=" + item.getAliqPis().toString() + "\n" ;
					}
				}
				// inicio PISST
				if ((new BigDecimal(item.getCstPis()).compareTo(new BigDecimal("05")) == 0)){
					if (contador >9 && contador <100 ){
						todosOsProdutos = todosOsProdutos +"[PISST"+"0"+contador+"]"+"\n";
					}else if (contador <= 9){
						todosOsProdutos = todosOsProdutos +"[PISST"+"00"+contador+"]"+"\n";
					}else{
						todosOsProdutos = todosOsProdutos +"[PISST"+contador+"]"+"\n";
					}
					System.out.println("Inicio PISST");
					todosOsProdutos = todosOsProdutos + "\n" ;
					if (item.getTributo().getPis().getCalculo().equals(TipoCalculo.TP)){
						todosOsProdutos = todosOsProdutos +
								"vBC=" + item.getValorTotal().toString() + "\n"+
								"pPIS=" + item.getAliqPis().divide(new BigDecimal("100")).toString() + "\n";
					}else{
						todosOsProdutos = todosOsProdutos +
								"qBCProd=" + item.getQuantidade().toString() + "\n" +
								"vAliqProd=" + item.getAliqPis().toString() + "\n" ;
					}

					//									vPIS=
				}
				// COFINS
				if (contador >9 && contador <100 ){
					todosOsProdutos = todosOsProdutos +"[COFINS"+"0"+contador+"]"+"\n";
				}else if (contador <= 9){
					todosOsProdutos = todosOsProdutos +"[COFINS"+"00"+contador+"]"+"\n";
				}else{
					todosOsProdutos = todosOsProdutos +"[COFINS"+contador+"]"+"\n";
				}
				System.out.println("Inicio COFINS");
				if ((new BigDecimal(item.getCstCofins()).compareTo(new BigDecimal("01")) == 0) ||
						(new BigDecimal(item.getCstCofins()).compareTo(new BigDecimal("02")) == 0) || 
						(new BigDecimal(item.getCstCofins()).compareTo(new BigDecimal("05")) == 0)){
					todosOsProdutos = todosOsProdutos +
							"CST=" + item.getCstCofins() + "\n" +
							"vBC=" + item.getValorTotal().toString() + "\n"+
							"pCOFINS=" + item.getAliqCofins().divide(new BigDecimal("100")).toString() + "\n" ;
				}else if ((new BigDecimal(item.getCstCofins()).compareTo(new BigDecimal("03")) == 0)) {
					todosOsProdutos = todosOsProdutos +
							"CST=" + item.getCstCofins() + "\n" +
							"qBCProd=" + item.getQuantidade().toString() + "\n" +
							"vAliqProd=" + item.getAliqCofins().toString() + "\n" ;
					//									vPIS=
				}else if ((new BigDecimal(item.getCstCofins()).compareTo(new BigDecimal("04")) == 0) ||
						(new BigDecimal(item.getCstCofins()).compareTo(new BigDecimal("06")) == 0) || 
						(new BigDecimal(item.getCstCofins()).compareTo(new BigDecimal("07")) == 0) ||
						(new BigDecimal(item.getCstCofins()).compareTo(new BigDecimal("08")) == 0) ||
						(new BigDecimal(item.getCstCofins()).compareTo(new BigDecimal("49")) == 0)||
						(new BigDecimal(item.getCstCofins()).compareTo(new BigDecimal("09")) == 0)) {
					todosOsProdutos = todosOsProdutos +
							"CST=" + item.getCstCofins() + "\n" ;
				}else if ((new BigDecimal(item.getCstCofins()).compareTo(new BigDecimal("99")) == 0)) {
					todosOsProdutos = todosOsProdutos +
							"CST=" + item.getCstCofins() + "\n" ;
					if (item.getTributo().getPis().getCalculo().equals(TipoCalculo.TP)){
						todosOsProdutos = todosOsProdutos +
								"vBC=" + item.getValorTotal().toString() + "\n"+
								"pCOFINS" + item.getAliqCofins().divide(new BigDecimal("100")).toString() + "\n";
					}else{
						todosOsProdutos = todosOsProdutos +
								"qBCProd=" + item.getQuantidade().toString() + "\n" +
								"vAliqProd=" + item.getAliqCofins().toString() + "\n" ;
					}
				}
				// inicio COFINSST
				if ((new BigDecimal(item.getCstCofins()).compareTo(new BigDecimal("05")) == 0)){
					if (contador >9 && contador <100 ){
						todosOsProdutos = todosOsProdutos +"[COFINSST"+"0"+contador+"]"+"\n";
					}else if (contador <= 9){
						todosOsProdutos = todosOsProdutos +"[COFINSST"+"00"+contador+"]"+"\n";
					}else{
						todosOsProdutos = todosOsProdutos +"[COFINSST"+contador+"]"+"\n";
					}
					System.out.println("Inicio COFINSST");
					todosOsProdutos = todosOsProdutos  + "\n" ;
					if (item.getTributo().getPis().getCalculo().equals(TipoCalculo.TP)){
						todosOsProdutos = todosOsProdutos +
								"vBC=" + item.getValorTotal().toString() + "\n"+
								"pCOFINS=" + item.getAliqCofins().divide(new BigDecimal("100")).toString() + "\n";
					}else{
						todosOsProdutos = todosOsProdutos +
								"qBCProd=" + item.getQuantidade().toString() + "\n" +
								"vAliqProd=" + item.getAliqCofins().toString() + "\n" ;
					}
				}
				this.produtoPreenchido = this.produtoPreenchido+todosOsProdutos;
				contador ++;
				System.out.println("8- Loop item CFe  " + contador);
			} // Fim do loop de itens
			// nao sera usado
			//									[ISSQN]
			//									vDeducISSQN=
			//									vBC=
			//									vAliq=
			//									vISSQN=
			//									cMunFG=
			//									cListServ=
			//									cServTribMun=
			//									cNatOp=
			//									indIncFisc=
			String total = "[Total]" + "\n" +
					"vICMS=" + sat.getValorIcms() + "\n"+
					"vProd=" + sat.getValorTotalProdutos() + "\n" +
					"vDesc=" + sat.getDesconto()+ "\n"+
					"vPIS=" + sat.getValorTotalPis() + "\n"+
					"vCOFINS=" + sat.getValorTotalCofins() + "\n"+
					"vPISST=" + sat.getValorTotalPisST() +"\n" +
					"vCOFINSST=" + sat.getValorTotalCofinsSt() + "\n"+
					"vOutro=" + sat.getOutrasDespesas()+"\n"+
					"vCFe=" + sat.getVCFe() + "\n" +
					"vCFeLei12741="+ sat.getValorTotalTributos() + "\n" +
					"vBC=" + sat.getBaseIcms()+"\n"+
					"vISS=" +  "\n"+
					"vAcresSubtot=" + sat.getVAcresSubtot() + "\n" +
					"vDescSubtot=" + sat.getVDescSubtot() + "\n" +
					"vTroco=" + sat.getVTroco() + "\n" ;

			//			"vPIS=" + sat.getValorTotalPis() + "\n" +
			//			"vCOFINS="+""+"\n"+
			//			"vPISST=" + "" + "\n" +
			//			"vCOFINSST=" + "" + "\n" +

			// QUANDO CRIAR O MODULO CAIXA, QUE IRA PERMITIR MAIS DE 1 MEIO DE PAGAMENTO,
			//HABILITAR ESTE TREXO DE CÓDIGO.

			//			System.out.println("inicio do Pagamento - linha 3003");
			//			String pag ="";
			////			String duplicata ="" ;
			//			contador = 001;
			//			if (!sat.getListaParcelas().isEmpty()){
			//				for (int i = 0 ; i < sat.getListaParcelas().size(); i++){   //ParcelasNfe parc : nota.getListaParcelas()) {
			//
			//					System.out.println("Linha 3010 - listaPagamentos SAT  - inicio");
			//					for (ParcelasNfe parcela : sat.getListaParcelas()) {
			//						System.out.println(parcela.getFormaPag().getTipoPagamento());
			//						System.out.println(parcela.getFormaPag().getTipoPagamento().getCod());
			//					}
			//					if (contador >9 && contador <100 ){
			//						pag = "[Pagto"+"0"+contador+"]"+"\n";
			//					}else if (contador <= 9){
			//						pag = "[Pagto"+"00"+contador+"]"+"\n";
			//					}else{
			//						pag = "[Pagto"+contador+"]"+"\n";
			//					}
			//
			//					pag = pag+
			//							"tpag="+sat.getListaParcelas().get(i).getFormaPag().getTipoPagamento().getCod()+"\n";
			//					if (sat.getListaParcelas().get(i).getFormaPag().getTipoPagamento().getCod() == "90"){
			//						pag = pag+
			//								"vPag="+"0.00"+"\n";
			//					}else{
			//						pag = pag+
			//								"vPag="+sat.getValorTotalNota()+"\n";
			//
			//					}
			//					if (sat.getListaParcelas().get(i).getFormaPag().getTipoPagamento().getCod() == "01"){
			//						pag = pag+
			//								"vTroco="+"0.00"+"\n";
			//					}
			//					if (sat.getListaParcelas().get(i).getFormaPag().getTipoPagamento().getCod() == "03" || sat.getListaParcelas().get(i).getFormaPag().getTipoPagamento().getCod() == "04"){
			//						pag = pag + "tpIntegra="+ApplicationUtils.getConfiguration("integra.car")+"\n";
			//						//											"CNPJ="+"\n"+
			//						//											"tBand="+"\n"+
			//						//											"cAut="+"\n";
			//					}
			//					this.pagamentoPreenchido = this.pagamentoPreenchido + pag;
			//					contador++;
			//				} // quando sem pagamento!
			//			}

			String pagamento = "[Pagto001]" + "\n" +
					"cMP=" + sat.getFormaPagamento().getTipoPagamento().getCod() + "\n" +
					"vMP=" + sat.getVCFe() + "\n" ; 
					if (versao == "0.09") {
						if (sat.getFormaPagamento().getTipoPagamento().getCod() == "03" || sat.getFormaPagamento().getTipoPagamento().getCod() == "04" ){
							pagamento = pagamento+
									"cAdmC=" + sat.getFormaPagamento().getOperadoraCartao().getCod()+ "\n" +  
									"cAut=999999999999999999" + "\n";
						}
					}

			String adicionais = "[DadosAdicionais]" + "\n" +
					"infCpl=" + "\n" ;

			String obsFisco = "[ObsFisco001]" + "\n" +
					"xCampo=" +  "\n" +
					"xTexto=" +  "\n";

			infArquivo = infCfe+ide+emitente+destinatario+entrega+this.produtoPreenchido+total+pagamento+adicionais+obsFisco;
			//			System.out.println("ACBr.SaveToFile(\"C:\\ibrcomp\\tmp\\"+nomeArquivo+".ini\",\""+infArquivo+"\")");
			System.out.println("finalizado a criação do arquivo na pasta c:\\ibrcomp\\temp\\"+ nomeArquivo);
			criaConexao(dados);
			return comandoAcbr("ACBr.SaveToFile(\"C:\\ibrcomp\\tmp\\"+nomeArquivo+".ini\",\""+infArquivo+"\",5)");
		}catch (HibernateException h) {
			fechaTudo();
			// TODO: handle exception
			System.out.println("Erro consulta hibernate: " + h.getMessage() + " motivo: " + h.toString());
			return null;
		}catch (Exception e) {
			fechaTudo();
			//			this.retorno = "Erro: tentando conectar com o ACBrMonitor. Contate o suporte técnico: " + "\n\n" + e.getMessage();
			System.out.println("Erro: tentando conectar com o ACBrMonitor. Contate o suporte técnico: " + "\n\n" + e.getMessage() + " campo: " + e.toString());
			return null;
		}
	}
	// Modulo Sat Caixa
	public String criarArqIniSatCaixaMaqRemota(DadosDeConexaoSocket dados,String nomeArquivo, CFe sat,String versao) throws IOException{
		try{
			System.out.println("Iniciando a montagem do arquivo.ini Sat para acbr");
			int contador = 001;
			int secunContador = 001;
			String infArquivo="";

			System.out.println("inicio de conversao Emissor");
			this.emissor = preencheEmissor(sat.getEmitente());
			System.out.println("Fim do emissor inicio da conversao do destino");
			//			this.destino = preencheDestino(nota);
			System.out.println("Fim do destino");

			String infCfe = "[infCFe]"+"\n";
//			infCfe = infCfe+"versao="+ApplicationUtils.getConfiguration("versao.sat")+"\n";
			infCfe = infCfe+"versao="+versao+"\n";

			String ide = "[Identificacao]" + "\n";
			ide = ide + "CNPJ=" + ApplicationUtils.getConfiguration("cnpj.software") +"\n" +
					"signAC=" + this.emissor.getAssinaturaSat() + "\n" +
					"numeroCaixa="+"1"+ "\n";
			System.out.println("Preenchendo Emitente 2718");
			String emitente = "[Emitente]"+"\n" ;
				if (ApplicationUtils.getConfiguration("sat.prod").equalsIgnoreCase("0")) {
					emitente = emitente +
					"CNPJ="+ "11111111111111" + "\n" +
					"IE=" + "111111111111" + "\n" ;
				}else {
					emitente = emitente + 
					"CNPJ="+ CpfCnpjUtils.retiraCaracteresEspeciais(this.emissor.getCnpjCpf()) + "\n" +
					"IE=" +  CpfCnpjUtils.retiraCaracteresEspeciais(this.emissor.getIe()) + "\n" ;
				}
				emitente = emitente +
					//					IM=111111 inscricao municipal
					"indRatISSQN=" + "N" + "\n" +
					"xNome=" + this.emissor.getXNome() + "\n" +
					"xFant=" + this.emissor.getXFant() + "\n" +
					"cRegTrib=" + this.emissor.getRegime().getCod() + "\n" + 
					//					cRegTribISSQN=
					"xLgr=" + this.emissor.getXLgr() + "\n" + 
					"nro=" + this.emissor.getNro() + "\n" +
					"xCpl=" + this.emissor.getXCpl() + "\n" + 
					"xBairro=" + this.emissor.getXBairro() + "\n" + 
					"xMun=" + this.emissor.getXMun() + "\n" +
					"CEP=" + this.emissor.getCep() + "\n" +
					"UF=" + this.emissor.getUf() + "\n";
			System.out.println("Fim do preenchimento 2735 Inicio preenche Destinatario");
			String destinatario = "[Destinatario]" + "\n";
			if (sat.getDestinatario().getCnpj() == null && sat.getDestinatario().getCpf()== null ) {
				destinatario = destinatario + 
						"CNPJCPF="  + "\n" ;
			}else {
				if (sat.getDestinatario().getCpf() == null) {
					destinatario = destinatario + 
							"CNPJCPF="  +sat.getDestinatario().getCnpj() + "\n" ;
				}else {
					destinatario = destinatario + 
							"CNPJCPF="  +sat.getDestinatario().getCpf() + "\n" ;
				}
			}
					if (sat.getDestinatario().getNome() != null) {
						destinatario = destinatario +
					"xNome=" + sat.getDestinatario().getNome() + "\n";
					}else {
						destinatario = destinatario +
								"xNome="+"\n";
					}
			System.out.println("Fim Destinatario");
			String entrega = "[Entrega]" + "\n" +
					"xLgr=" + /* pegar do pedido de venda recebido */ "\n" +
					"nro=" + /**/ "\n"+
					"xCpl=" + /**/ "\n" +
					"xBairro=" +/*Centro*/ "\n"+
					"xMun=" + /*Tatui*/"\n" +
					"UF=" + /*SP*/"\n";

			contador = 001;
			String todosOsProdutos = "";
			System.out.println("Inicio preenche produto 2762");
			// inicio do loop para preencher os itens do cupom fiscal
			for (ItemCFe item : sat.getListaItem()) {

				System.out.println("Inicio Item Sat ");
				if (contador >9 && contador <100 ){
					todosOsProdutos = "[Produto"+"0"+contador+"]"+"\n";
				}else if (contador <= 9){
					todosOsProdutos = "[Produto"+"00"+contador+"]"+"\n";
				}else{
					todosOsProdutos = "[Produto"+contador+"]"+"\n";
				}
				todosOsProdutos = todosOsProdutos +
						"cProd=" + item.getProduto().getReferencia() + "\n";
				//"infAdProd="
				//				if (!item.getProduto().getListaBarras().get(0).getBarras().isEmpty()){
				//					todosOsProdutos = todosOsProdutos +
				//							"cEAN=" + item.getProduto().getListaBarras().get(0).getBarras() + "\n" ;
				//				}
				todosOsProdutos = todosOsProdutos +
						"xProd=" + item.getProduto().getDescricao() + "\n" +
						"NCM=" + item.getProduto().getNcm().getNcm() + "\n";
				System.out.println("if CEST - 2784");
				if (item.getProduto().getNcm().isSt() && !item.getProduto().getNcm().getCest().isEmpty()){
					todosOsProdutos = todosOsProdutos +
							"CEST=" + item.getProduto().getNcm().getCest() + "\n";
				}
				System.out.println("preenchendo CFOP - 2789");
				todosOsProdutos = todosOsProdutos +
						"CFOP=" + item.getCfopItem().getCfop() + "\n" +
						"uCom=" + item.getUnidade() + "\n" +
						//					Combustivel=0
						"qCom=" + item.getQuantidade().toString() + "\n" +
						"vUnCom=" + item.getValorUnitario().toString() + "\n" +

								//					vProd=
								"indRegra=A" + "\n"+ //arredondamento
								"vDesc=" + item.getDesconto().toString() + "\n"+
								"vOutro=" + item.getValorDespesas().toString() + "\n"+
								"vItem12741=" + item.getValorTotalTributoItem().toString() + "\n" +
								//					vItem=
								"vRatDesc=" + item.getVRatDesc().toString() + "\n"+
								"vRatAcr=" + item.getVRatAcr().toString() + "\n";
				if (contador >9 && contador <100 ){
					todosOsProdutos = todosOsProdutos +"[ObsFiscoDet"+"0"+contador+ "0" + contador+"]"+"\n";
				}else if (contador <= 9){
					todosOsProdutos = todosOsProdutos +"[ObsFiscoDet"+"00"+contador+"00"+contador+"]"+"\n";
				}else{
					todosOsProdutos = todosOsProdutos +"[ObsFiscoDet"+contador+contador+"]"+"\n";
				}
				System.out.println("inicio OBSFISCO");
				todosOsProdutos = todosOsProdutos +
						"xCampoDet=" + "\n" +
						"xTextoDet=" + "\n";
				if (contador >9 && contador <100 ){
					todosOsProdutos = todosOsProdutos +"[ICMS"+"0"+contador+"]"+"\n";
				}else if (contador <= 9){
					todosOsProdutos = todosOsProdutos +"[ICMS"+"00"+contador+"]"+"\n";
				}else{
					todosOsProdutos = todosOsProdutos +"[ICMS"+contador+"]"+"\n";
				}
				System.out.println("Inicio ICMS");
				todosOsProdutos = todosOsProdutos +
						"Orig=" + item.getOrigem() + "\n" ;
				if (this.emissor.getRegime().equals(Enquadramento.Normal)){
					todosOsProdutos = todosOsProdutos +
							"CST=" + item.getCst() + "\n" ;
					if (item.getCst().equalsIgnoreCase("00") || item.getCst().equalsIgnoreCase("20") || item.getCst().equalsIgnoreCase("90")){
						todosOsProdutos = todosOsProdutos +
								"pICMS=" + item.getAliqIcmsSat().toString() + "\n";
					}
				}else{
					todosOsProdutos = todosOsProdutos +
							"CSOSN=" + item.getCst() + "\n" ;
					if (item.getCst().equalsIgnoreCase("900")){
						todosOsProdutos = todosOsProdutos +
								"pICMS=" + item.getAliqIcmsSat().toString() + "\n";
					}
				}

				//									vICMS=
				// PIS
				System.out.println("Inicio PIS");
				if (contador >9 && contador <100 ){
					todosOsProdutos = todosOsProdutos +"[PIS"+"0"+contador+"]"+"\n";
				}else if (contador <= 9){
					todosOsProdutos = todosOsProdutos +"[PIS"+"00"+contador+"]"+"\n";
				}else{
					todosOsProdutos = todosOsProdutos +"[PIS"+contador+"]"+"\n";
				}
				if ((new BigDecimal(item.getCstPis()).compareTo(new BigDecimal("01")) == 0) ||
						(new BigDecimal(item.getCstPis()).compareTo(new BigDecimal("02")) == 0) || 
						(new BigDecimal(item.getCstPis()).compareTo(new BigDecimal("05")) == 0)){
					todosOsProdutos = todosOsProdutos +
							"CST=" + item.getCstPis() + "\n" +
							"vBC=" + item.getBaseICMS().toString() + "\n"+
							"pPIS=" + item.getAliqPis().divide(new BigDecimal("100")).toString() + "\n" ;
				}else if ((new BigDecimal(item.getCstPis()).compareTo(new BigDecimal("03")) == 0)) {
					todosOsProdutos = todosOsProdutos +
							"CST=" + item.getCstPis() + "\n" +
							"qBCProd=" + item.getQuantidade().toString() + "\n" +
							"vAliqProd=" + item.getAliqPis().toString() + "\n" ;
					//									vPIS=
				}else if ((new BigDecimal(item.getCstPis()).compareTo(new BigDecimal("04")) == 0) ||
						(new BigDecimal(item.getCstPis()).compareTo(new BigDecimal("06")) == 0) || 
						(new BigDecimal(item.getCstPis()).compareTo(new BigDecimal("07")) == 0) ||
						(new BigDecimal(item.getCstPis()).compareTo(new BigDecimal("08")) == 0) ||
						(new BigDecimal(item.getCstPis()).compareTo(new BigDecimal("49")) == 0)||
						(new BigDecimal(item.getCstPis()).compareTo(new BigDecimal("09")) == 0)) {
					todosOsProdutos = todosOsProdutos +
							"CST=" + item.getCstPis() + "\n" ;
				}else if ((new BigDecimal(item.getCstPis()).compareTo(new BigDecimal("99")) == 0)) {
					todosOsProdutos = todosOsProdutos +
							"CST=" + item.getCstPis() + "\n" ;
					if (item.getTributo().getPis().getCalculo().equals(TipoCalculo.TP)){
						todosOsProdutos = todosOsProdutos +
								"vBC=" + item.getValorTotal().toString() + "\n"+
								"pPIS=" + item.getAliqPis().divide(new BigDecimal("100")).toString() + "\n";
					}else{
						todosOsProdutos = todosOsProdutos +
								"qBCProd=" + item.getQuantidade().toString() + "\n" +
								"vAliqProd=" + item.getAliqPis().toString() + "\n" ;
					}
				}
				// inicio PISST
				if ((new BigDecimal(item.getCstPis()).compareTo(new BigDecimal("05")) == 0)){
					if (contador >9 && contador <100 ){
						todosOsProdutos = todosOsProdutos +"[PISST"+"0"+contador+"]"+"\n";
					}else if (contador <= 9){
						todosOsProdutos = todosOsProdutos +"[PISST"+"00"+contador+"]"+"\n";
					}else{
						todosOsProdutos = todosOsProdutos +"[PISST"+contador+"]"+"\n";
					}
					System.out.println("Inicio PISST");
					todosOsProdutos = todosOsProdutos + "\n" ;
					if (item.getTributo().getPis().getCalculo().equals(TipoCalculo.TP)){
						todosOsProdutos = todosOsProdutos +
								"vBC=" + item.getValorTotal().toString() + "\n"+
								"pPIS=" + item.getAliqPis().divide(new BigDecimal("100")).toString() + "\n";
					}else{
						todosOsProdutos = todosOsProdutos +
								"qBCProd=" + item.getQuantidade().toString() + "\n" +
								"vAliqProd=" + item.getAliqPis().toString() + "\n" ;
					}

					//									vPIS=
				}
				// COFINS
				if (contador >9 && contador <100 ){
					todosOsProdutos = todosOsProdutos +"[COFINS"+"0"+contador+"]"+"\n";
				}else if (contador <= 9){
					todosOsProdutos = todosOsProdutos +"[COFINS"+"00"+contador+"]"+"\n";
				}else{
					todosOsProdutos = todosOsProdutos +"[COFINS"+contador+"]"+"\n";
				}
				System.out.println("Inicio COFINS");
				if ((new BigDecimal(item.getCstCofins()).compareTo(new BigDecimal("01")) == 0) ||
						(new BigDecimal(item.getCstCofins()).compareTo(new BigDecimal("02")) == 0) || 
						(new BigDecimal(item.getCstCofins()).compareTo(new BigDecimal("05")) == 0)){
					todosOsProdutos = todosOsProdutos +
							"CST=" + item.getCstCofins() + "\n" +
							"vBC=" + item.getValorTotal().toString() + "\n"+
							"pCOFINS=" + item.getAliqCofins().divide(new BigDecimal("100")).toString() + "\n" ;
				}else if ((new BigDecimal(item.getCstCofins()).compareTo(new BigDecimal("03")) == 0)) {
					todosOsProdutos = todosOsProdutos +
							"CST=" + item.getCstCofins() + "\n" +
							"qBCProd=" + item.getQuantidade().toString() + "\n" +
							"vAliqProd=" + item.getAliqCofins().toString() + "\n" ;
					//									vPIS=
				}else if ((new BigDecimal(item.getCstCofins()).compareTo(new BigDecimal("04")) == 0) ||
						(new BigDecimal(item.getCstCofins()).compareTo(new BigDecimal("06")) == 0) || 
						(new BigDecimal(item.getCstCofins()).compareTo(new BigDecimal("07")) == 0) ||
						(new BigDecimal(item.getCstCofins()).compareTo(new BigDecimal("08")) == 0) ||
						(new BigDecimal(item.getCstCofins()).compareTo(new BigDecimal("49")) == 0)||
						(new BigDecimal(item.getCstCofins()).compareTo(new BigDecimal("09")) == 0)) {
					todosOsProdutos = todosOsProdutos +
							"CST=" + item.getCstCofins() + "\n" ;
				}else if ((new BigDecimal(item.getCstCofins()).compareTo(new BigDecimal("99")) == 0)) {
					todosOsProdutos = todosOsProdutos +
							"CST=" + item.getCstCofins() + "\n" ;
					if (item.getTributo().getPis().getCalculo().equals(TipoCalculo.TP)){
						todosOsProdutos = todosOsProdutos +
								"vBC=" + item.getValorTotal().toString() + "\n"+
								"pCOFINS" + item.getAliqCofins().divide(new BigDecimal("100")).toString() + "\n";
					}else{
						todosOsProdutos = todosOsProdutos +
								"qBCProd=" + item.getQuantidade().toString() + "\n" +
								"vAliqProd=" + item.getAliqCofins().toString() + "\n" ;
					}
				}
				// inicio COFINSST
				if ((new BigDecimal(item.getCstCofins()).compareTo(new BigDecimal("05")) == 0)){
					if (contador >9 && contador <100 ){
						todosOsProdutos = todosOsProdutos +"[COFINSST"+"0"+contador+"]"+"\n";
					}else if (contador <= 9){
						todosOsProdutos = todosOsProdutos +"[COFINSST"+"00"+contador+"]"+"\n";
					}else{
						todosOsProdutos = todosOsProdutos +"[COFINSST"+contador+"]"+"\n";
					}
					System.out.println("Inicio COFINSST");
					todosOsProdutos = todosOsProdutos  + "\n" ;
					if (item.getTributo().getPis().getCalculo().equals(TipoCalculo.TP)){
						todosOsProdutos = todosOsProdutos +
								"vBC=" + item.getValorTotal().toString() + "\n"+
								"pCOFINS=" + item.getAliqCofins().divide(new BigDecimal("100")).toString() + "\n";
					}else{
						todosOsProdutos = todosOsProdutos +
								"qBCProd=" + item.getQuantidade().toString() + "\n" +
								"vAliqProd=" + item.getAliqCofins().toString() + "\n" ;
					}
				}
				this.produtoPreenchido = this.produtoPreenchido+todosOsProdutos;
				contador ++;
				System.out.println("8- Loop item CFe  " + contador);
			} // Fim do loop de itens
			// nao sera usado
			//									[ISSQN]
			//									vDeducISSQN=
			//									vBC=
			//									vAliq=
			//									vISSQN=
			//									cMunFG=
			//									cListServ=
			//									cServTribMun=
			//									cNatOp=
			//									indIncFisc=
			String total = "[Total]" + "\n" +
					"vICMS=" + sat.getValorIcms() + "\n"+
					"vProd=" + sat.getValorTotalProdutos() + "\n" +
					"vDesc=" + sat.getDesconto()+ "\n"+
					"vPIS=" + sat.getValorTotalPis() + "\n"+
					"vCOFINS=" + sat.getValorTotalCofins() + "\n"+
					"vPISST=" + sat.getValorTotalPisST() +"\n" +
					"vCOFINSST=" + sat.getValorTotalCofinsSt() + "\n"+
					"vOutro=" + sat.getOutrasDespesas()+"\n"+
					"vCFe=" + sat.getVCFe() + "\n" +
					"vCFeLei12741="+ sat.getValorTotalTributos() + "\n" +
					"vBC=" + sat.getBaseIcms()+"\n"+
					"vISS=" +  "\n"+
					"vAcresSubtot=" + sat.getVAcresSubtot() + "\n" +
					"vDescSubtot=" + sat.getVDescSubtot() + "\n" +
					"vTroco=" + sat.getVTroco() + "\n" ;

			//			"vPIS=" + sat.getValorTotalPis() + "\n" +
			//			"vCOFINS="+""+"\n"+
			//			"vPISST=" + "" + "\n" +
			//			"vCOFINSST=" + "" + "\n" +

			// QUANDO CRIAR O MODULO CAIXA, QUE IRA PERMITIR MAIS DE 1 MEIO DE PAGAMENTO,
			//HABILITAR ESTE TREXO DE CÓDIGO.

						System.out.println("inicio do Pagamento - linha 3003");
						String pag ="";
						contador = 001;
						if (!sat.getListaRecebimentosAgrupados().isEmpty()){
							for (int i = 0 ; i < sat.getListaRecebimentosAgrupados().size(); i++){   //ParcelasNfe parc : nota.getListaParcelas()) {
			
								System.out.println("Linha 3010 - listaPagamentos SAT  - inicio");
//								for (ParcelasNfe parcela : sat.getListaParcelas()) {
//									System.out.println(parcela.getFormaPag().getTipoPagamento());
//									System.out.println(parcela.getFormaPag().getTipoPagamento().getCod());
//								}
								if (contador >9 && contador <100 ){
									pag = "[Pagto"+"0"+contador+"]"+"\n";
								}else if (contador <= 9){
									pag = "[Pagto"+"00"+contador+"]"+"\n";
								}else{
									pag = "[Pagto"+contador+"]"+"\n";
								}
			
								pag = pag+
										"cMP="+sat.getListaRecebimentosAgrupados().get(i).getFormaPagamento().getTipoPagamento().getCod()+"\n";
								if (sat.getListaRecebimentosAgrupados().get(i).getFormaPagamento().getTipoPagamento().getCod() == "90"){
									pag = pag+
											"vMP="+"0.00"+"\n";
								}else{
									pag = pag+
											"vMP="+sat.getListaRecebimentosAgrupados().get(i).getValorRecebido()+"\n";
									if (versao == "0.09") {
										if (sat.getListaRecebimentosAgrupados().get(i).getFormaPagamento().getTipoPagamento().getCod() == "03" || sat.getListaRecebimentosAgrupados().get(i).getFormaPagamento().getTipoPagamento().getCod() == "04" ){
											pag = pag+
													"vMP="+sat.getListaRecebimentosAgrupados().get(i).getValorRecebido()+"\n" +
													"cAdmC=" + sat.getListaRecebimentosAgrupados().get(i).getFormaPagamento().getOperadoraCartao().getCod()+ "\n" +  
													"cAut=999999999999999999" + "\n";
										}
									}
								}
								this.pagamentoPreenchido = this.pagamentoPreenchido + pag;
								contador++;
							} // quando sem pagamento!
						}
//
//			String pagamento = "[Pagto001]" + "\n" +
//					"cMP=" + sat.getFormaPagamento().getTipoPagamento().getCod() + "\n" +
//					"vMP=" + sat.getVCFe() + "\n" + 
//					"cAdmC=" + "\n" ;

			String adicionais = "[DadosAdicionais]" + "\n" +
					"infCpl=" + "\n" ;

			String obsFisco = "[ObsFisco001]" + "\n" +
					"xCampo=" +  "\n" +
					"xTexto=" +  "\n";

			infArquivo = infCfe+ide+emitente+destinatario+entrega+this.produtoPreenchido+total+this.pagamentoPreenchido+adicionais+obsFisco;
			//			System.out.println("ACBr.SaveToFile(\"C:\\ibrcomp\\tmp\\"+nomeArquivo+".ini\",\""+infArquivo+"\")");
			System.out.println("finalizado a criação do arquivo na pasta c:\\ibrcomp\\temp\\"+ nomeArquivo);
			criaConexao(dados);
			return comandoAcbr("ACBr.SaveToFile(\"C:\\ibrcomp\\tmp\\"+nomeArquivo+".ini\",\""+infArquivo+"\",5)");
		}catch (HibernateException h) {
			// TODO: handle exception
			fechaTudo();
			System.out.println("Erro consulta hibernate: " + h.getMessage() + " motivo: " + h.toString());
			return null;
		}catch (Exception e) {
			fechaTudo();
			//			this.retorno = "Erro: tentando conectar com o ACBrMonitor. Contate o suporte técnico: " + "\n\n" + e.getMessage();
			System.out.println("Erro: tentando conectar com o ACBrMonitor. Contate o suporte técnico: " + "\n\n" + e.getMessage() + " campo: " + e.toString());
			return null;
		}
	}

	/* =============================================================
	 * NFC-e (Modelo 65)
	 * =============================================================
	 *
	 * O patch NFC-e utiliza reflection para chamar estes métodos.
	 * Para evitar dúvidas, disponibilizamos nomes "alias" também
	 * (ex.: criarArqIniNfceCaixaMaqRemota / nfeCriarEnviarNfce etc.).
	 *
	 * Observação: QRCode/CSC, ambiente e impressora são configurados
	 * diretamente no ACBrMonitor.
	 */

	/**
	 * Gera o INI da NFC-e (modelo 65) a partir de um DTO neutro do caixa (fluxo novo).
	 * @throws IOException 
	 */
	public String criarArqIniNfce(DadosDeConexaoSocket dados, String nomeArquivo, CupomFiscalCaixa venda, Nfce nfce,List<RecebimentoParcial> lista) throws IOException {
		try {
			if (venda == null) {
				throw new IllegalArgumentException("CupomFiscalCaixa não informado.");
			}
			if (venda.getEmitente() == null) {
				throw new IllegalArgumentException("Emitente da venda não informado.");
			}
			if (venda.getListaRecebimentosAgrupados() == null) {
				throw new IllegalArgumentException("Forma de pagamento não informado.");
			}

			// Emissor/endereços
			this.emissor = preencheEmissor(venda.getEmitente());

			// Reset acumuladores
			this.produtoPreenchido = "";
			this.pagamentoPreenchido = "";

			String infNfe = new StringBuilder()
					.append("[infNFE]\n")
					.append(kv("versao", ApplicationUtils.getConfiguration("versao.nfe")))
					.toString();

			String ide = buildIdentificacaoNfce(venda, nfce);
			String emitente = buildEmitenteNfce();
			String destinatario = buildDestinatarioNfce(venda);

			// Produtos + impostos
			AtomicInteger seq = new AtomicInteger(1);
			if (venda.getItens() != null) {
				for (ItemCFe item : venda.getItens()) {
					int idx = seq.getAndIncrement();
					this.produtoPreenchido += buildProdutoNfce(item, idx);
				}
			}

			String total = buildTotalNfce(venda);
			String transporte = buildTransporteNfceSemFrete();
			String pagamentos = buildPagamentosNfce(venda,lista);

			String adicionais = new StringBuilder()
					.append("[DadosAdicionais]\n")
					.append(kvIfNotNull("infCpl", ""))
					.toString();

			String infArquivo = infNfe + ide + emitente + destinatario + this.produtoPreenchido + total + transporte + pagamentos + adicionais;

			System.out.println("finalizado a criação do arquivo NFC-e na pasta c:\\ibrcomp\\tmp\\" + nomeArquivo);
			criaConexao(dados);
			return comandoAcbr("ACBr.SaveToFile(\"C:\\ibrcomp\\tmp\\" + nomeArquivo + ".ini\",\"" + infArquivo + "\",5)");
		} catch (Exception e) {
			fechaTudo();
			System.out.println("Erro criando INI NFC-e: " + e.getMessage());
			return "ERRO: " + e.getMessage();
		}
	}

	// Aliases (CupomFiscalCaixa)
	public String criarArqIniNfceCaixaMaqRemota(DadosDeConexaoSocket dados, String nomeArquivo, CupomFiscalCaixa venda, Nfce nfce,List<RecebimentoParcial> lista) throws IOException {
		return criarArqIniNfce(dados, nomeArquivo, venda, nfce,lista);
	}
	public String criarArqIniNfceMaqRemota(DadosDeConexaoSocket dados, String nomeArquivo, CupomFiscalCaixa venda, Nfce nfce,List<RecebimentoParcial> lista) throws IOException {
		return criarArqIniNfce(dados, nomeArquivo, venda, nfce,lista);
	}

	/**
	 * Gera o INI da NFC-e (modelo 65) a partir da venda do caixa (CFe)
	 * e do registro {@link Nfce} (número/série).
	 * @throws IOException 
	 */
	public String criarArqIniNfce(DadosDeConexaoSocket dados, String nomeArquivo, CFe venda, Nfce nfce) throws IOException {
		try {
			if (venda == null) {
				throw new IllegalArgumentException("Venda (CFe) não informada.");
			}
			if (venda.getEmitente() == null) {
				throw new IllegalArgumentException("Emitente da venda não informado.");
			}

			// Emissor/endereços
			this.emissor = preencheEmissor(venda.getEmitente());

			// Reset acumuladores
			this.produtoPreenchido = "";
			this.pagamentoPreenchido = "";

			String infNfe = new StringBuilder()
					.append("[infNFE]\n")
					.append(kv("versao", ApplicationUtils.getConfiguration("versao.nfe")))
					.toString();

			String ide = buildIdentificacaoNfce(venda, nfce);
			String emitente = buildEmitenteNfce();
			String destinatario = buildDestinatarioNfce(venda);

			// Produtos + impostos
			AtomicInteger seq = new AtomicInteger(1);
			if (venda.getListaItem() != null) {
				for (ItemCFe item : venda.getListaItem()) {
					int idx = seq.getAndIncrement();
					this.produtoPreenchido += buildProdutoNfce(item, idx);
				}
			}

			String total = buildTotalNfce(venda);
			String transporte = buildTransporteNfceSemFrete();
			String pagamentos = buildPagamentosNfce(venda);

			String adicionais = new StringBuilder()
					.append("[DadosAdicionais]\n")
					.append(kvIfNotNull("infCpl", ""))
					.toString();

			String infArquivo = infNfe + ide + emitente + destinatario + this.produtoPreenchido + total + transporte + pagamentos + adicionais;


			System.out.println("finalizado a criação do arquivo NFC-e na pasta c:\\ibrcomp\\tmp\\" + nomeArquivo);
			criaConexao(dados);
			return comandoAcbr("ACBr.SaveToFile(\"C:\\ibrcomp\\tmp\\" + nomeArquivo + ".ini\",\"" + infArquivo + "\",5)");
		} catch (Exception e) {
			fechaTudo();
			System.out.println("Erro criando INI NFC-e: " + e.getMessage());
			return "ERRO: " + e.getMessage();
		}
	}

	// Aliases (para compatibilidade com versões anteriores do patch)
	public String criarArqIniNfceCaixaMaqRemota(DadosDeConexaoSocket dados, String nomeArquivo, CFe venda, Nfce nfce) throws IOException {
		return criarArqIniNfce(dados, nomeArquivo, venda, nfce);
	}
	public String criarArqIniNfceMaqRemota(DadosDeConexaoSocket dados, String nomeArquivo, CFe venda, Nfce nfce) throws IOException {
		return criarArqIniNfce(dados, nomeArquivo, venda, nfce);
	}

	/** Envia a NFC-e via ACBrMonitor (NFe.CriarEnviarNFe usando o INI gerado). */
	public String nfceCriarEnviarNfce(DadosDeConexaoSocket dados, String nomeArquivo) throws IOException {
		String ini = "C:\\ibrcomp\\tmp\\" + nomeArquivo + ".ini";
		String[] comandos = new String[] {
				// Preferimos síncrono e SEM impressão aqui (impressão pode ser feita depois via NFe.ImprimirDANFENFCe)
		        "NFe.CriarEnviarNFe(\"" + ini + "\",1,0,1)",
		        "NFE.CriarEnviarNFe(\"" + ini + "\",1,0,1)",
		        // Alternativa: síncrono e já imprime (use se você NÃO chamar a impressão depois)
		        "NFe.CriarEnviarNFe(\"" + ini + "\",1,1,1)",
		        "NFE.CriarEnviarNFe(\"" + ini + "\",1,1,1)",
		        // Fallbacks (podem depender da versão do ACBrMonitor)
		        "NFe.CriarEnviarNFe(\"" + ini + "\")",
		        "NFE.CriarEnviarNFe(\"" + ini + "\")",
		        "NFe.CriarEnviarNFe(\"" + ini + "\",1,1)",
		        "NFE.CriarEnviarNFe(\"" + ini + "\",1,1)",
		        "NFe.CriarEnviarNFe(\"" + ini + "\",1)",
		        "NFE.CriarEnviarNFe(\"" + ini + "\",1)"
		};
		String ultimo = "";
		for (String cmd : comandos) {
			ultimo = enviaComandoACBr(dados, cmd);
			if (!isErroAcbr(ultimo)) {
				return ultimo;
			}
		}
		return ultimo;
	}
	public String nfeCriarEnviarNfce(DadosDeConexaoSocket dados, String nomeArquivo) throws IOException { return nfceCriarEnviarNfce(dados, nomeArquivo); }
	public String nfeCriarEnviarNFe(DadosDeConexaoSocket dados, String nomeArquivo) throws IOException { return nfceCriarEnviarNfce(dados, nomeArquivo); }
	public String nfeCriarEnviar(DadosDeConexaoSocket dados, String nomeArquivo) throws IOException { return nfceCriarEnviarNfce(dados, nomeArquivo); }

	/** Imprime DANFCE (se o ACBr estiver configurado). */
	public String nfceImprimiDanfceVenda(DadosDeConexaoSocket dados, String caminhoXmlOuChave) throws IOException {
		String alvo = nz(caminhoXmlOuChave).trim();
		if (alvo.isEmpty()) return "";
		String[] comandos = new String[] {
				"NFe.ImprimirDANFENFCe(\"" + alvo + "\")",
				"NFe.ImprimirDanfce(\"" + alvo + "\")",
				"NFe.ImprimirDanfe(\"" + alvo + "\")",
				"NFE.ImprimirDANFENFCe(\"" + alvo + "\")"
		};
		String ultimo = "";
		for (String cmd : comandos) {
			ultimo = enviaComandoACBr(dados, cmd);
			if (!isErroAcbr(ultimo)) return ultimo;
		}
		return ultimo;
	}
	public String nfeImprimirDanfce(DadosDeConexaoSocket dados, String caminhoXmlOuChave) throws IOException { return nfceImprimiDanfceVenda(dados, caminhoXmlOuChave); }
	public String nfeImprimirDanfceVenda(DadosDeConexaoSocket dados, String caminhoXmlOuChave) throws IOException { return nfceImprimiDanfceVenda(dados, caminhoXmlOuChave); }

	/** Gera PDF do DANFCE (se o ACBr estiver configurado). */
	public String geraPDFDanfceVenda(DadosDeConexaoSocket dados, String caminhoXmlOuChave, String nomePdf) throws IOException {
		String alvo = nz(caminhoXmlOuChave).trim();
		if (alvo.isEmpty()) return "";
		String pdf = "C:\\ibrcomp\\pdfNfce\\" + (nomePdf == null || nomePdf.trim().isEmpty() ? "nfce.pdf" : nomePdf.trim());
		String[] comandos = new String[] {
				"NFe.GerarPDFDanfeNFCe(\"" + alvo + "\",\"" + pdf + "\")",
				"NFe.GerarPDFDANFENFCe(\"" + alvo + "\",\"" + pdf + "\")",
				"NFe.GerarPDFDanfe(\"" + alvo + "\",\"" + pdf + "\")",
				"NFE.GerarPDFDanfe(\"" + alvo + "\",\"" + pdf + "\")"
		};
		String ultimo = "";
		for (String cmd : comandos) {
			ultimo = enviaComandoACBr(dados, cmd);
			if (!isErroAcbr(ultimo)) return ultimo;
		}
		return ultimo;
	}
	public String geraPDFDanfeNfceVenda(DadosDeConexaoSocket dados, String caminhoXmlOuChave, String nomePdf) throws IOException { return geraPDFDanfceVenda(dados, caminhoXmlOuChave, nomePdf); }
	public String geraPDFDanfeNfce(DadosDeConexaoSocket dados, String caminhoXmlOuChave, String nomePdf) throws IOException { return geraPDFDanfceVenda(dados, caminhoXmlOuChave, nomePdf); }
	public String geraPDFDanfe(DadosDeConexaoSocket dados, String caminhoXmlOuChave, String nomePdf) throws IOException { return geraPDFDanfceVenda(dados, caminhoXmlOuChave, nomePdf); }

	/* --------------------------- Helpers --------------------------- */
	private boolean isErroAcbr(String retorno) {
		if (retorno == null) return true;
		String up = retorno.toUpperCase();
		return up.contains("ERRO") || up.contains("EXCEPTION") || up.contains("FALHA") || up.contains("INVALID");
	}

	private String gerarCNF8() {
		int n = java.util.concurrent.ThreadLocalRandom.current().nextInt(0, 100000000);
		return String.format(java.util.Locale.ROOT, "%08d", n);
	}

	private String resolveCUF(String ufSigla) {
		if (ufSigla == null || ufSigla.trim().isEmpty()) return "";
		try {
			return String.valueOf(Uf.valueOf(ufSigla.trim().toUpperCase()).getIbgeUf());
		} catch (Exception e) {
			return "";
		}
	}

	private String buildIdentificacaoNfce(CFe venda, Nfce nfce) {
		Long numero = (nfce != null ? nfce.getNumero() : null);
		Integer serie = (nfce != null ? nfce.getSerie() : null);
		String cUF = resolveCUF(this.emissor != null ? this.emissor.getUf().name() : null);
		return new StringBuilder()
				.append("[Identificacao]\n")
				.append(kv("cUF", cUF))
				.append(kv("cNF", gerarCNF8()))
				.append(kv("natOp", "VENDA"))
				.append(kv("mod", "65"))
				.append(kv("serie", serie == null ? 1 : serie))
				.append(kv("nNF", numero == null ? "" : numero))
				.append(kv("dhEmi", LocalDateTime.now().format(this.formatoSimples)))
				.append(kv("tpNF", "1"))
				.append(kv("idDest", "1"))
				.append(kv("cMunFG", this.emissor != null ? this.emissor.getCMun() : ""))
				.append(kv("tpImp", "4"))
				.append(kv("tpEmis", "1"))
				.append(kv("finNFe", "1"))
				.append(kv("indFinal", "1"))
				.append(kv("indPres", "1"))
				.append(kv("procEmi", "0"))
				.append(kv("verProc", "nsym"))
				.toString();
	}
	
	private String buildTransporteNfceSemFrete() {
	    return new StringBuilder()
	            .append("[Transportador]\n")
	            .append(kv("modFrete", "9"))
	            .toString();
	}

	private String buildIdentificacaoNfce(CupomFiscalCaixa venda, Nfce nfce) {
		// mesma regra do CFe; o cupom não é usado aqui (mantido por compatibilidade)
		Long numero = (nfce != null ? nfce.getNumero() : null);
		Integer serie = (nfce != null ? nfce.getSerie() : null);
		String cUF = resolveCUF(this.emissor != null ? this.emissor.getUf().name() : null);
		return new StringBuilder()
				.append("[Identificacao]\n")
				.append(kv("cUF", cUF))
				.append(kv("cNF", gerarCNF8()))
				.append(kv("natOp", "VENDA"))
				.append(kv("mod", "65"))
				.append(kv("serie", serie == null ? 1 : serie))
				.append(kv("nNF", numero == null ? "" : numero))
				.append(kv("dhEmi", LocalDateTime.now().format(this.formatoSimples)))
				.append(kv("tpNF", "1"))
				.append(kv("idDest", "1"))
				.append(kv("cMunFG", this.emissor != null ? this.emissor.getCMun() : ""))
				.append(kv("tpImp", "4"))
				.append(kv("tpEmis", "1"))
				.append(kv("finNFe", "1"))
				.append(kv("indFinal", "1"))
				.append(kv("indPres", "1"))
				.append(kv("procEmi", "0"))
				.append(kv("verProc", "nsym"))
				.toString();
	}

	private String buildEmitenteNfce() {
		if (this.emissor == null) return "[Emitente]\n";
		return new StringBuilder()
				.append("[Emitente]\n")
				.append(kv("CNPJ", this.emissor.getCnpjCpf()))
				.append(kv("xNome", removerAcentos(this.emissor.getXNome())))
				.append(kvIfNotNull("xFant", removerAcentos(this.emissor.getXFant())))
				.append(kvIfNotNull("IE", this.emissor.getIe()))
				.append(kvIfNotNull("IM", this.emissor.getIMunicipal()))
				.append(kvIfNotNull("CNAE", this.emissor.getCNAE()))
				.append(kv("CRT", this.emissor.getCrt()))
				.append(kv("xLgr", removerAcentos(this.emissor.getXLgr())))
				.append(kv("nro", this.emissor.getNro()))
				.append(kvIfNotNull("xCpl", removerAcentos(this.emissor.getXCpl())))
				.append(kv("xBairro", removerAcentos(this.emissor.getXBairro())))
				.append(kv("cMun", this.emissor.getCMun()))
				.append(kv("xMun", removerAcentos(this.emissor.getXMun())))
				.append(kv("UF", this.emissor.getUf().name()))
				.append(kv("CEP", this.emissor.getCep()))
				.append(kv("cPais", this.emissor.getCPais()))
				.append(kv("xPais", removerAcentos(this.emissor.getXPais())))
				.append(kvIfNotNull("Fone", this.emissor.getFone()))
				.toString();
	}
	
	private String buildDestinatarioNfce(CFe venda) {
		StringBuilder d = new StringBuilder();
		d.append("[Destinatario]\n");
		if (venda != null && venda.getDestinatario() != null) {
			String cnpj = nz(venda.getDestinatario().getCnpj()).trim();
			String cpf = nz(venda.getDestinatario().getCpf()).trim();
			String doc = !cnpj.isEmpty() ? cnpj : cpf;
			d.append(kv("CNPJCPF", doc));
			d.append(kv("xNome", removerAcentos(nz(venda.getDestinatario().getNome()))));
		} else {
			d.append(kv("CNPJCPF", ""));
			d.append(kv("xNome", "CONSUMIDOR"));
		}
		// Consumidor final normalmente sem IE
		d.append(kv("indIEDest", "9"));
		return d.toString();
	}

	private String buildDestinatarioNfce(CupomFiscalCaixa venda) {
		StringBuilder d = new StringBuilder();
		d.append("[Destinatario]\n");
		if (venda != null && venda.getDestinatario() != null) {
			String cnpj = nz(venda.getDestinatario().getCnpj()).trim();
			String cpf = nz(venda.getDestinatario().getCpf()).trim();
			String doc = !cnpj.isEmpty() ? cnpj : cpf;
			d.append(kv("CNPJCPF", doc));
			d.append(kv("xNome", removerAcentos(nz(venda.getDestinatario().getNome()))));
		} else {
			d.append(kv("CNPJCPF", ""));
			d.append(kv("xNome", "CONSUMIDOR"));
		}
		// Consumidor final normalmente sem IE
		d.append(kv("indIEDest", "9"));
		return d.toString();
	}

	private String buildProdutoNfce(ItemCFe item, int idx) {
		if (item == null) return "";
		StringBuilder sb = new StringBuilder();

		// Produto
		sb.append(section("Produto", idx))
		  .append(kv("cProd", item.getProduto() != null ? item.getProduto().getReferencia() : ""))
		  .append(kv("xProd", item.getProduto() != null ? removerAcentos(item.getProduto().getDescricao()) : ""));
		if (item.getProduto() != null && item.getProduto().getNcm() != null) {
			sb.append(kv("NCM", item.getProduto().getNcm().getNcm()));
		}
		if (item.getProduto() != null && item.getProduto().getNcm().getCest() != null) {
			sb.append(kvIfNotNull("CEST", item.getProduto().getNcm().getCest()));
		}
		if (item.getCfopItem() != null) {
			sb.append(kv("CFOP", item.getCfopItem().getCfop()));
		}
		if (item.getUnidade() != null) {
			sb.append(kv("uCom", item.getUnidade()));
		}
		sb.append(kv("qCom", item.getQuantidade()))
		  .append(kv("vUnCom", item.getValorUnitario()))
//		  .append(kv("vProd", item.getValorTotalBruto()))
//		  .append(kvIfNotNull("vDesc", item.getDesconto()))
//		  .append(kvIfNotNull("vFrete", item.getValorFrete()))
//		  .append(kvIfNotNull("vSeg", item.getValorSeguro()))
//		  .append(kvIfNotNull("vOutro", item.getValorDespesas()))
		  .append(kv("vProd", scale2(item.getValorTotalBruto())))
		  .append(kvIfNotNull("vDesc", scale2Nullable(item.getDesconto())))
		  .append(kvIfNotNull("vOutro", scale2Nullable(item.getValorDespesas())))

		  .append(kv("indTot", "1"));
		// Lei 12.741/2012
		sb.append(kvIfNotNull("vTotTrib", item.getValorTotalTributoItem()));

		// ICMS
		sb.append(section("ICMS", idx))
		  .append(kv("orig", item.getOrigem()))
		  .append(kv(EmissorEnquadradoNormal(this.emissor) ? "CST" : "CSOSN", item.getCst()))
		  .append(kvIfNotNull("vBC", item.getBaseICMS()))
		  .append(kvIfNotNull("pICMS", item.getAliqIcmsSat()))
		  .append(kvIfNotNull("vICMS", item.getValorIcms()));
		if (item.isItemST()) {
			sb.append(kvIfNotNull("vBCST", item.getBaseICMSSt()))
			  .append(kvIfNotNull("pICMSST", item.getAliqIcmsSt()))
			  .append(kvIfNotNull("vICMSST", item.getValorIcmsSt()));
		}

		// IPI (se existir)
		if (item.getValorIPI() != null && item.getValorIPI().doubleValue() > 0.0) {
			sb.append(section("IPI", idx))
			  .append(kvIfNotNull("vIPI", item.getValorIPI()))
			  .append(kvIfNotNull("pIPI", item.getAliqIPI()));
		}

		// PIS
		sb.append(section("PIS", idx))
		  .append(kvIfNotNull("CST", item.getCstPis()))
		  .append(kvIfNotNull("vBC", item.getValorTotal()))
		  .append(kvIfNotNull("pPIS", item.getAliqPis()))
		  .append(kvIfNotNull("vPIS", item.getValorPis()));

		// COFINS
		sb.append(section("COFINS", idx))
		  .append(kvIfNotNull("CST", item.getCstCofins()))
		  .append(kvIfNotNull("vBC", item.getValorTotal()))
		  .append(kvIfNotNull("pCOFINS", item.getAliqCofins()))
		  .append(kvIfNotNull("vCOFINS", item.getValorCofins()));

		return sb.toString();
	}

	private String buildTotalNfce(CFe venda) {
		if (venda == null) {
			return section("Total");
		}

		java.math.BigDecimal vProd = java.math.BigDecimal.ZERO;
		java.math.BigDecimal vDesc = java.math.BigDecimal.ZERO;
		java.math.BigDecimal vOutro = java.math.BigDecimal.ZERO;
		java.math.BigDecimal vTotTribItens = java.math.BigDecimal.ZERO;

		if (venda.getListaItem() != null) {
			for (ItemCFe it : venda.getListaItem()) {
				if (it == null) continue;
				vProd = vProd.add(nzbd(it.getValorTotalBruto()));
				vDesc = vDesc.add(nzbd(it.getDesconto()));
				vOutro = vOutro.add(nzbd(it.getValorDespesas()));
				vTotTribItens = vTotTribItens.add(nzbd(it.getValorTotalTributoItem()));
			}
		}

		// NFC-e não pode ter frete/seguro (rejeição 753)
		java.math.BigDecimal vFrete = java.math.BigDecimal.ZERO;
		java.math.BigDecimal vSeg = java.math.BigDecimal.ZERO;

		java.math.BigDecimal vNF = vProd.subtract(vDesc).add(vOutro).add(vFrete).add(vSeg);

		java.math.BigDecimal vTotTrib = (venda.getValorTotalTributos() != null)
				? venda.getValorTotalTributos()
						: vTotTribItens;

		java.math.BigDecimal vDescOut = (vDesc.signum() != 0 ? scale2(vDesc) : null);
		java.math.BigDecimal vOutroOut = (vOutro.signum() != 0 ? scale2(vOutro) : null);
		java.math.BigDecimal vTotTribOut = (vTotTrib.signum() != 0 ? scale2(vTotTrib) : null);

		return new StringBuilder()
				.append(section("Total"))
				.append(kvIfNotNull("vBC", scale2Nullable(venda.getBaseIcms())))
				.append(kvIfNotNull("vICMS", scale2Nullable(venda.getValorIcms())))
				.append(kvIfNotNull("vBCST", scale2Nullable(venda.getBaseIcmsSubstituicao())))
				.append(kvIfNotNull("vST", scale2Nullable(venda.getValorIcmsSubstituicao())))
				.append(kv("vProd", scale2(vProd)))
				.append(kv("vFrete", scale2(vFrete)))
				.append(kv("vSeg", scale2(vSeg)))
				.append(kvIfNotNull("vDesc", vDescOut))
				.append(kvIfNotNull("vOutro", vOutroOut))
				.append(kv("vNF", scale2(vNF)))
				.append(kvIfNotNull("vTotTrib", vTotTribOut))
				.toString();

	}

	private String buildTotalNfce(CupomFiscalCaixa venda) {
		if (venda == null) {
			return section("Total");
		}

		java.math.BigDecimal vProd = java.math.BigDecimal.ZERO;
		java.math.BigDecimal vDesc = java.math.BigDecimal.ZERO;
		java.math.BigDecimal vOutro = java.math.BigDecimal.ZERO;
		java.math.BigDecimal vTotTribItens = java.math.BigDecimal.ZERO;

		if (venda != null && venda.getItens() != null) {
			for (ItemCFe it : venda.getItens()) {
				if (it == null) continue;
				vProd = vProd.add(nzbd(it.getValorTotalBruto()));
				vDesc = vDesc.add(nzbd(it.getDesconto()));
				vOutro = vOutro.add(nzbd(it.getValorDespesas()));
				vTotTribItens = vTotTribItens.add(nzbd(it.getValorTotalTributoItem()));
			}
		}

		// NFC-e não pode ter frete/seguro (rejeição 753)
		java.math.BigDecimal vFrete = java.math.BigDecimal.ZERO;
		java.math.BigDecimal vSeg = java.math.BigDecimal.ZERO;
		java.math.BigDecimal vNF = vProd.subtract(vDesc).add(vOutro).add(vFrete).add(vSeg);

		java.math.BigDecimal vTotTrib = (venda != null && venda.getValorTotalTributos() != null)
				? venda.getValorTotalTributos()
						: vTotTribItens;
		java.math.BigDecimal vDescOut = (vDesc.signum() != 0 ? scale2(vDesc) : null);
		java.math.BigDecimal vOutroOut = (vOutro.signum() != 0 ? scale2(vOutro) : null);
		java.math.BigDecimal vTotTribOut = (vTotTrib.signum() != 0 ? scale2(vTotTrib) : null);
		

		return new StringBuilder()
				.append(section("Total"))
				.append(kvIfNotNull("vBC", scale2Nullable(venda.getBaseIcms())))
				.append(kvIfNotNull("vICMS", scale2Nullable(venda.getValorIcms())))
				.append(kvIfNotNull("vBCST", scale2Nullable(venda.getBaseIcmsSt())))
				.append(kvIfNotNull("vST", scale2Nullable(venda.getValorIcmsSt())))
				.append(kv("vProd", scale2(vProd)))
				.append(kv("vFrete", scale2(vFrete)))
				.append(kv("vSeg", scale2(vSeg)))
				.append(kvIfNotNull("vDesc", vDescOut))
				.append(kvIfNotNull("vOutro", vOutroOut))
				.append(kv("vNF", scale2(vNF)))
				.append(kvIfNotNull("vTotTrib", vTotTribOut))

				.toString();
	}
	
	private String buildPagamentosNfce(CFe venda) {
		if (venda == null) {
			return section("Pag001");
		}

		// Total da NF-e calculado a partir dos itens (mantém consistência com [Total])
		java.math.BigDecimal vProd = java.math.BigDecimal.ZERO;
		java.math.BigDecimal vDesc = java.math.BigDecimal.ZERO;
		java.math.BigDecimal vOutro = java.math.BigDecimal.ZERO;
		if (venda.getListaItem() != null) {
			for (ItemCFe it : venda.getListaItem()) {
				if (it == null) continue;
				vProd = vProd.add(nzbd(it.getValorTotalBruto()));
				vDesc = vDesc.add(nzbd(it.getDesconto()));
				vOutro = vOutro.add(nzbd(it.getValorDespesas()));
			}
		}
		java.math.BigDecimal vNF = vProd.subtract(vDesc).add(vOutro);

		java.util.List<RecebimentoParcial> lista = venda.getListaRecebimentosAgrupados();
		if (lista == null || lista.isEmpty()) {
			lista = new java.util.ArrayList<>();
			if (venda.getListaParcelas() != null) {
				for (Parcelas p : venda.getListaParcelas()) {
					if (p == null || p.getFormaPag() == null || p.getFormaPag().getTipoPagamento() == null) continue;
					RecebimentoParcial r = new RecebimentoParcial();
					r.setTipoPagamento(p.getFormaPag().getTipoPagamento());
					r.setValorRecebido(p.getValorRecebido());
					r.setFormaPagamento(p.getFormaPag());
					lista.add(r);
				}
			}
		}

		// Soma de pagamentos (para calcular troco corretamente)
		java.math.BigDecimal totalPago = java.math.BigDecimal.ZERO;
		for (RecebimentoParcial r : lista) {
			if (r == null) continue;
			totalPago = totalPago.add(nzbd(r.getValorRecebido()));
		}
		final java.math.BigDecimal trocoCalc =
		        scale2(totalPago.subtract(vNF).max(java.math.BigDecimal.ZERO));

		java.util.Map<String, java.util.List<RecebimentoParcial>> recebimentosAgrupados = lista.stream()
				.collect(java.util.stream.Collectors.groupingBy(r -> r.getTipoPagamento().getCod()));

		java.util.concurrent.atomic.AtomicInteger seq = new java.util.concurrent.atomic.AtomicInteger(1);
		StringBuilder sb = new StringBuilder();

		recebimentosAgrupados.entrySet().stream()
				.sorted(java.util.Map.Entry.comparingByKey())
				.forEach(entry -> {
					String tPag = entry.getKey();
					java.util.List<RecebimentoParcial> parcelas = entry.getValue();
					if (parcelas == null || parcelas.isEmpty()) return;

					java.math.BigDecimal vPag = parcelas.stream()
							.filter(r -> r != null && r.getValorRecebido() != null)
							.map(RecebimentoParcial::getValorRecebido)
							.reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

					vPag = scale2(vPag);

					sb.append(section("PAG", seq.getAndIncrement()))
						.append(kv("tPag", tPag))
						.append(kv("vPag", vPag));

					if ("99".equals(tPag)) {
						String xPag = parcelas.get(0).getFormaPagamento().getDescOutros();
						sb.append(kv("xPag", xPag));
					}

					if ("03".equals(tPag) || "04".equals(tPag) || "17".equals(tPag)) {
						String tpIntegra = ApplicationUtils.getConfiguration("integra.car");
						if (isBlank(tpIntegra)) tpIntegra = "2";
						sb.append(kv("tpIntegra", tpIntegra));

						Object forma = parcelas.get(0).getFormaPagamento();
						Object oper = tryInvoke(forma, "getOperadoraCartao");

						String cnpj = firstNonBlank(
								tryInvokeStr(oper, "getCnpj"),
								tryInvokeStr(oper, "getCnpjOperadora"),
								tryInvokeStr(oper, "getCnpjCredenciadora"),
								ApplicationUtils.getConfiguration("cartao.cnpjCredenciadora"),
								ApplicationUtils.getConfiguration("cartao.cnpj"));

						String tBand = firstNonBlank(
								tryInvokeStr(forma, "getTBandeira"),
								tryInvokeStr(forma, "getTBandeiraNfe"),
								tryInvokeStr(forma, "gettBand"),
								ApplicationUtils.getConfiguration("cartao.tBand"));

						String cAut = firstNonBlank(
								tryInvokeStr(parcelas.get(0), "getCAut"),
								tryInvokeStr(parcelas.get(0), "getcAut"),
								ApplicationUtils.getConfiguration("cartao.cAut"),
								"999999");

						if (!isBlank(cnpj)) sb.append(kv("CNPJ", cnpj));
						if (!isBlank(tBand)) sb.append(kv("tBand", tBand));
						if (!isBlank(cAut)) sb.append(kv("cAut", cAut));
					}

					if ("01".equals(tPag) && trocoCalc.signum() > 0) {
						sb.append(kv("vTroco", trocoCalc));
					}
				});

		return sb.toString();
	}



	private String buildPagamentosNfce(CupomFiscalCaixa venda,List<RecebimentoParcial> lista) {
		System.out.println("estou preenchendo campo Pagxxx");
		if (venda == null) {
			return section("Pag001");
		}

		// Total da NF-e calculado a partir dos itens (mantém consistência com [Total])
		java.math.BigDecimal vProd = java.math.BigDecimal.ZERO;
		java.math.BigDecimal vDesc = java.math.BigDecimal.ZERO;
		java.math.BigDecimal vOutro = java.math.BigDecimal.ZERO;
		if (venda.getItens() != null) {
			System.out.println("Itens != null");
			for (ItemCFe it : venda.getItens()) {
				if (it == null) continue;
				vProd = vProd.add(nzbd(it.getValorTotalBruto()));
				vDesc = vDesc.add(nzbd(it.getDesconto()));
				vOutro = vOutro.add(nzbd(it.getValorDespesas()));
			}
		}
		java.math.BigDecimal vNF = vProd.subtract(vDesc).add(vOutro);

		// Soma de pagamentos (para calcular troco corretamente)
		java.math.BigDecimal totalPago = java.math.BigDecimal.ZERO;
		if (venda.getListaRecebimentosAgrupados() != null && !venda.getListaRecebimentosAgrupados().isEmpty() ) {
			System.out.println("ListaRecebimentosAgrupados != null tamanho da lista" + venda.getListaRecebimentosAgrupados().size());
			for (RecebimentoParcial r : venda.getListaRecebimentosAgrupados()) {
				if (r == null) continue;
				totalPago = totalPago.add(nzbd(r.getValorRecebido()));
			}
		}else {
			if (lista != null) {
				System.out.println("nfce.ListaRecebimentosAgrupados != null" + lista.size());
				for (RecebimentoParcial r : lista) {
					if (r == null) continue;
					totalPago = totalPago.add(nzbd(r.getValorRecebido()));
				}
			}
		}
		
		java.util.Map<String, java.util.List<RecebimentoParcial>> recebimentosAgrupados = new java.util.HashMap<>();
		final java.math.BigDecimal trocoCalc =
		        scale2(totalPago.subtract(vNF).max(java.math.BigDecimal.ZERO));
		if (venda.getListaRecebimentosAgrupados() == null && venda.getListaRecebimentosAgrupados().isEmpty() ) {
			 recebimentosAgrupados = (venda.getListaRecebimentosAgrupados() == null || venda.getListaRecebimentosAgrupados().isEmpty())
					? new java.util.HashMap<>()
							: venda.getListaRecebimentosAgrupados().stream()
							.collect(java.util.stream.Collectors.groupingBy(r -> r.getFormaPagamento().getTipoPagamento().getCod()));
		}else {
			 recebimentosAgrupados = (lista == null ||lista.isEmpty())
					? new java.util.HashMap<>()
							: lista.stream()
							.collect(java.util.stream.Collectors.groupingBy(r -> r.getFormaPagamento().getTipoPagamento().getCod()));
		}
		System.out.println("Passei pelo HasMap PegaFormaPagamento tipoPagamento cod");
		System.out.println("recebimentosAgrupados lista tamanho: "+ recebimentosAgrupados.size());
		java.util.concurrent.atomic.AtomicInteger seq = new java.util.concurrent.atomic.AtomicInteger(1);

		System.out.println("depois do atomic" );
		StringBuilder sb = new StringBuilder();
		recebimentosAgrupados.entrySet().stream()
				.sorted(java.util.Map.Entry.comparingByKey())
				.forEach(entry -> {
					String tPag = entry.getKey();
					java.util.List<RecebimentoParcial> parcelas = entry.getValue();
					if (parcelas == null || parcelas.isEmpty()) return;
					System.out.println("Passei pelo parcela nulo");
					java.math.BigDecimal vPag = parcelas.stream()
							.filter(r -> r != null && r.getValorRecebido() != null)
							.map(RecebimentoParcial::getValorRecebido)
							.reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

					vPag = scale2(vPag);
					
					System.out.println("Peguei a informacao tPag: " + tPag);
					System.out.println("Peguei a informacao vPag: " + vPag);
					sb.append(section("PAG", seq.getAndIncrement()))
						.append(kv("tPag", tPag))
						.append(kv("vPag", vPag));

					if ("99".equals(tPag)) {
						String xPag = parcelas.get(0).getFormaPagamento().getDescOutros();
						sb.append(kv("xPag", xPag));
					}

					if ("03".equals(tPag) || "04".equals(tPag) || "17".equals(tPag)) {
						String tpIntegra = ApplicationUtils.getConfiguration("integra.car");
						if (isBlank(tpIntegra)) tpIntegra = "2";
						sb.append(kv("tpIntegra", tpIntegra));

					}

					if ("01".equals(tPag) && trocoCalc.signum() > 0) {
						sb.append(kv("vTroco", trocoCalc));
					}
				});

		return sb.toString();
	}
	// Dados do cartão: tenta pegar do cadastro; se não existir, usa configurações
//	Object forma = parcelas.get(0).getFormaPagamento();
//	Object oper = tryInvoke(forma, "getOperadoraCartao");
//
//	String cnpj = firstNonBlank(
//			tryInvokeStr(oper, "getCnpj"),
//			tryInvokeStr(oper, "getCnpjOperadora"),
//			tryInvokeStr(oper, "getCnpjCredenciadora"),
//			ApplicationUtils.getConfiguration("cartao.cnpjCredenciadora"),
//			ApplicationUtils.getConfiguration("cartao.cnpj"));
//
//	String tBand = firstNonBlank(
//			tryInvokeStr(forma, "getTBandeira"),
//			tryInvokeStr(forma, "getTBandeiraNfe"),
//			tryInvokeStr(forma, "gettBand"),
//			ApplicationUtils.getConfiguration("cartao.tBand"));
//
//	String cAut = firstNonBlank(
//			tryInvokeStr(parcelas.get(0), "getCAut"),
//			tryInvokeStr(parcelas.get(0), "getcAut"),
//			ApplicationUtils.getConfiguration("cartao.cAut"),
//			"999999");
//
//	if (!isBlank(cnpj)) sb.append(kv("CNPJ", cnpj));
//	if (!isBlank(tBand)) sb.append(kv("tBand", tBand));
//	if (!isBlank(cAut)) sb.append(kv("cAut", cAut));

/* ============================================================= */

}