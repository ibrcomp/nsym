package br.com.nsym.domain.misc;

import java.io.IOException;
import java.util.Scanner;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import br.com.caelum.stella.format.CNPJFormatter;
import br.com.caelum.stella.format.Formatter;
import br.com.nsym.application.channels.AcbrComunica;
import br.com.nsym.application.channels.DadosDeConexaoSocket;
import br.com.nsym.domain.model.entity.tools.ReceitaFederalConsulta;
import br.com.nsym.domain.model.entity.tools.UfSigla;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ibrahim
 *
 */
@RequestScoped
public class ReceitaFinder {

	@Getter
	@Setter
	private ReceitaFederalConsulta receita = new ReceitaFederalConsulta();

//	@Getter
//	private WebClient webClient = new WebClient(BrowserVersion.getDefault());

	private Formatter formatado = new CNPJFormatter();

	@Inject
	private AcbrComunica acbr;

	@Inject
	private LocalizaRegex localiza;

	@Inject
	protected Logger LOG;

//	public ReceitaFederalConsulta procuraReceita(String numeroCNPJ,String uf) throws IOException, InterruptedException{
//		webClient = new WebClient(BrowserVersion.CHROME);
//		webClient.getCache().clear();
//		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
//		webClient.getOptions().setThrowExceptionOnScriptError(false);
//		webClient.getOptions().setJavaScriptEnabled(true);
//		webClient.getOptions().setUseInsecureSSL(true); 
//		webClient.getOptions().setRedirectEnabled(false);
//		webClient.setCssErrorHandler(new SilentCssErrorHandler());
//		webClient.getOptions().setTimeout(40000);
//		webClient.setRefreshHandler(new ThreadedRefreshHandler());
//
//		HtmlPage reCaptchaFrame;
//		HtmlPage cccPage = webClient.getPage("https://www.sefaz.rs.gov.br/NFE/NFE-CCC.aspx");
//
//		webClient.getJavaScriptEngine();
//		webClient.waitForBackgroundJavaScript(200);
//		JavaScriptEngine engine = (JavaScriptEngine) webClient.getJavaScriptEngine();
//		System.out.println("passei pelo engine");
//		engine.holdPosponedActions();
//		System.out.println("engine.holdPosponedActions();");
//		List<FrameWindow> frames = cccPage.getFrames();
//		System.out.println("pegei os frames");
//		System.out.println(frames.isEmpty());
//		System.out.println(frames.size());
//
//		reCaptchaFrame = (HtmlPage) frames.get(0).getEnclosedPage();
//		// initiating to enter the reCaptcha
//		System.out.println("passei pelo recaptchaframe");
////		System.out.println("3 - " +reCaptchaFrame.asText());
//
//		HtmlSpan reCaptchaAnchor = reCaptchaFrame.getFirstByXPath("//*[@id='recaptcha-anchor']"); //*[@id="divRecaptcha"]/fieldset/div/table/tbody/tr[1]/td/div/div/div/iframe
//		if (reCaptchaAnchor == null){
//			System.out.println("reCaptcha = null");
//			throw new NullPointerException("Captcha not found");
//		}
////		System.out.println(reCaptchaAnchor.asText());
//		try{
//			HtmlPage pageCaptcha = reCaptchaAnchor.click();
//		} catch(WrappedException e){
//			System.out.println("Exite uma exception" + e.details());
//		}
//
//		System.out.println(cccPage.getTitleText() + " - Acessei a pagina");
//		HtmlForm form = cccPage.getFormByName("aspnetForm");
//		HtmlSelect select = cccPage.getFirstByXPath("//*[@id='ctl00_cphConteudo_cmbUF']");//*[@id="ctl00_cphConteudo_cmbUF"]
////		System.out.println("1-" + select.asText());
//		HtmlOption option = select.getOptionByValue(uf);
//		select.setSelectedAttribute(option, true);
////		System.out.println("2- "+select.asText());
//		HtmlTextInput  cnpj = form.getInputByName("tbxCNPJContrib");
//		String cnpjSemFormatacao = formatado.unformat(numeroCNPJ); // retira a pontua��o do cnpj
//		System.out.println(cnpjSemFormatacao);
//		cnpj.setText(cnpjSemFormatacao);
//
//
//
//		//*[@id="recaptcha-anchor"]
//		HtmlButtonInput button = form.getInputByValue("Pesquisar por CNPJ");
//		HtmlPage cccPageResultado = button.click();
//		Thread.sleep(2000);
//		String cnpjComFormatacao = formatado.format(numeroCNPJ);
//		System.out.println(cnpjComFormatacao + "acessando os dados pagina 2" );
//		HtmlAnchor link =  cccPageResultado.getAnchorByText(cnpjComFormatacao);
////		System.out.println(link.asText() + "   link de acesso ao resultado");
//		HtmlPage cccPage2 = link.click();
//
//		System.out.println(cccPage2.getTitleText() + "exibindo o titlo do resultado retornado");
//		HtmlPage table = cccPage2.getPage();
////		System.out.println("  exibindo titulo do resultado 2  " + table.asText());
//
//		HtmlSpan receitaInscricao = table.getFirstByXPath("//*[@id='ctl00_cphConteudo_txIE']");
//		//		HtmlSpan receitaInscricao = table.getFirstByXPath("//*[@id='Estab']/fieldset[2]/div/table/tbody/tr[4]/td[2]/span[1]");
//		//*[@id="Estab"]/fieldset[2]/div/table/tbody/tr[4]/td[2]
//		//*[@id="ctl00_cphConteudo_txIE"]
//		receita.setReceitaIE(receitaInscricao.getTextContent());
//		System.out.println("Localizei os dados inscri�ao: " + receitaInscricao.getTextContent());
//		System.out.println(receita);
//
//		HtmlSpan receitaRazao = table.getFirstByXPath("//*[@id='Estab']/fieldset[2]/div/table/tbody/tr[1]/td[2]/span[1]");
//		//*[@id="Estab"]/fieldset[2]/div/table/tbody/tr[1]/td[2]
//		receita.setReceitaRazao(receitaRazao.getTextContent());
//
//		HtmlSpan receitaSituacao= table.getFirstByXPath("//*[@id='Estab']/fieldset[2]/div/table/tbody/tr[4]/td[4]/span[1]");
//		//*[@id="Estab"]/fieldset[2]/div/table/tbody/tr[4]/td[4]
//		receita.setReceitaSituacaoCadastral(receitaSituacao.getTextContent());
//
//		HtmlSpan receitaFantasia= table.getFirstByXPath("//*[@id='Estab']/fieldset[3]/div/table/tbody/tr[1]/td[2]/span[1]");
//		//*[@id="Estab"]/fieldset[3]/div/table/tbody/tr[1]/td[2]
//		receita.setReceitaFantasia(receitaFantasia.getTextContent());
//
//		HtmlSpan receitaRegime= table.getFirstByXPath("//*[@id='Estab']/fieldset[3]/div/table/tbody/tr[3]/td[2]/span[1]");
//		//*[@id="Estab"]/fieldset[3]/div/table/tbody/tr[3]/td[2]
//		receita.setReceitaRegime(receitaRegime.getTextContent());
//
//		HtmlSpan receitaCnaePrincipal= table.getFirstByXPath("//*[@id='Estab']/fieldset[3]/div/table/tbody/tr[6]/td[2]/span[1]");
//		//*[@id="Estab"]/fieldset[3]/div/table/tbody/tr[6]/td[2]
//		receita.setReceitaCnaePrincipal(receitaCnaePrincipal.getTextContent());
//
//		HtmlSpan receitaInicioAtividade= table.getFirstByXPath("//*[@id='Estab']/fieldset[3]/div/table/tbody/tr[2]/td[2]/span[1]");
//		//*[@id="Estab"]/fieldset[3]/div/table/tbody/tr[2]/td[2]
//		receita.setReceitaDataAbertura(receitaInicioAtividade.getTextContent());
//		System.out.println("Estou antes de criar a mascara para o Cep" + receita.getReceitaCep());
//
//		HtmlSpan receitaCep= table.getFirstByXPath("//*[@id='Estab']/fieldset[4]/div/table/tbody/tr[4]/td[2]/span[1]"); 
//		//*[@id="Estab"]/fieldset[4]/div/table/tbody/tr[4]/td[2]
//		int tamanhoCep = receitaCep.getTextContent().length();
//		String cepSemMascara = receitaCep.getTextContent();
//		for ( int i = tamanhoCep ;  i < 8; i++) {
//			cepSemMascara = "0"+cepSemMascara;
//		}
//		String cepComMascara = cepSemMascara.substring(0, 5)+"-"+ cepSemMascara.substring(5);
//
//		System.out.println("Criei a mascara" + cepComMascara);
//
//		receita.setReceitaCep(cepComMascara);
//
//		HtmlSpan receitaLogradouro= table.getFirstByXPath("//*[@id='Estab']/fieldset[4]/div/table/tbody/tr[2]/td[2]/span[1]");
//		//*[@id="Estab"]/fieldset[4]/div/table/tbody/tr[2]/td[2]
//		receita.setReceitaLogradouro(receitaLogradouro.getTextContent());
//
//		HtmlSpan receitaNumero= table.getFirstByXPath("//*[@id='Estab']/fieldset[4]/div/table/tbody/tr[2]/td[4]/span[1]");
//		//*[@id="Estab"]/fieldset[4]/div/table/tbody/tr[2]/td[4]
//		receita.setReceitaNumero(receitaNumero.getTextContent());
//
//		HtmlSpan receitaComplemento= table.getFirstByXPath("//*[@id='Estab']/fieldset[4]/div/table/tbody/tr[3]/td[2]/span[1]");
//		//*[@id="Estab"]/fieldset[4]/div/table/tbody/tr[3]/td[2]
//		receita.setReceitaComplemento(receitaComplemento.getTextContent());
//
//		HtmlSpan receitaCnpj= table.getFirstByXPath("//*[@id='Estab']/fieldset[2]/div/table/tbody/tr[3]/td[2]/span[1]");
//		receita.setReceitaCNPJ(receitaCnpj.getTextContent());
//		//*[@id="Estab"]/fieldset[2]/div/table/tbody/tr[3]/td[2]
//
//
//		return receita;
//	}

	/** 
	 * M�todo que retorna a consulta cadastral junto a receita pesquisando por CNPJ atraves do ACBR
	 * @throws IOException 
	 * 
	 */
	public String consultaCadSintegra(DadosDeConexaoSocket infConexao,String cnpj, UfSigla estado) throws IOException{
		return acbr.enviaComandoACBr(infConexao,"NFE.ConsultaCadastro(\""+ estado.toString() + "\","+cnpj+")" );

	}
	/**
	 * M�todo que recebe a resposta do ACBR e converte para a classe ReceitaFederalConsulta
	 * @param respAcbr
	 * @return ReceitaFederalConsulta
	 */
	public ReceitaFederalConsulta retornoConsultaSintegra(String respAcbr){
		String respMaiuscula;
		String resultado;
		int ini = 0;
		int fim = 0;
		this.receita = new ReceitaFederalConsulta();
		respMaiuscula = respAcbr.toUpperCase();
		boolean achei = localiza.localizaPalavra(respMaiuscula,"INFCAD001" );
		if (achei){
			ini = respMaiuscula.indexOf("[INFCAD001]");

			resultado = respMaiuscula.substring(ini).toUpperCase();
			
			Scanner in = new Scanner(resultado);
			while (in.hasNextLine()) {
			    String line = in.nextLine();
			    System.out.println("ibrahim " + line);
			    if (localiza.localizaPalavra(line, "CNPJ=")) {
			    	this.receita.setReceitaCNPJ(line.substring(5, line.length()).trim());
			    }
			    if (localiza.localizaPalavra(line, "IE=")) {
			    	this.receita.setReceitaIE(line.substring(3, line.length()).trim()); 
			    }
			    if (localiza.localizaPalavra(line, "CEP=")) {
			    	String cepSemMascara =line.substring(4, line.length()).trim();
					int tamanhoCep = cepSemMascara.length();
					for ( int i = tamanhoCep ;  i < 8; i++) {
						cepSemMascara = "0"+cepSemMascara;
					}
					String cepComMascara = cepSemMascara.substring(0, 5)+"-"+ cepSemMascara.substring(5);

					this.receita.setReceitaCep(cepComMascara);
			    }
			    if (localiza.localizaPalavra(line, "CNAE=")) {
			    	this.receita.setReceitaCnaePrincipal(line.substring(5, line.length()).trim());
			    }
			    if (localiza.localizaPalavra(line, "DINIATIV=")) {
			    	this.receita.setReceitaDataAbertura(line.substring(9, line.length()).trim());
			    }
			    if (localiza.localizaPalavra(line, "XBAIRRO=")) {
			    	this.receita.setReceitaBairro(line.substring(8, line.length()).trim());
			    }
			    if (localiza.localizaPalavra(line, "XLGR=")) {
			    	this.receita.setReceitaLogradouro(line.substring(5, line.length()).trim());
			    }
			    if (localiza.localizaPalavra(line, "XMUN=")) {
			    	this.receita.setReceitaMunicipio(line.substring(5, line.length()).trim());
			    }
			    if (localiza.localizaPalavra(line, "XNOME=")) {
			    	this.receita.setReceitaRazao(line.substring(6, line.length()).trim());
			    }
			    if (localiza.localizaPalavra(line, "XFANT=")) {
			    	this.receita.setReceitaFantasia(line.substring(6, line.length()).trim());
			    }
			    if (localiza.localizaPalavra(line, "NRO=")) {
			    	this.receita.setReceitaNumero(line.substring(4, line.length()).trim());
			    }
			    if (localiza.localizaPalavra(line, "XCPL=")) {
			    	this.receita.setReceitaComplemento(line.substring(5, line.length()).trim());
			    }
			    if (localiza.localizaPalavra(line, "XREGAPUR=")) {
			    	this.receita.setReceitaRegime(line.substring(9, line.length()).trim());
			    }
			    if (localiza.localizaPalavra(line, "UF=")) {
			    	this.receita.setReceitaUF(line.substring(3, line.length()).trim());
			    }
			    if (localiza.localizaPalavra(line, "CSIT")) {
			    	String csit = line.substring(5, line.length()).trim();
					if (csit.trim().equalsIgnoreCase("1")){
						this.receita.setReceitaSituacaoCadastral("HABILITADO");
						System.out.println("ibrahim scaner: " + csit + "linha: " + line);
					}else{
						this.receita.setReceitaSituacaoCadastral("N�O HABILITADO");
						System.out.println("ibrahim: " + csit + "linha: " + line);
					}
			    }
			}
			in.close();
			System.out.println(receita.getReceitaUF());
		}else{
			return null;
		}

		return this.receita;
	}

}
