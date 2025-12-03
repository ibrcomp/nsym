package br.com.ibrcomp.sat.mb;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.apache.commons.codec.binary.Base64;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.com.ibrcomp.sat.CertificadoA3;
import br.com.nsym.application.controller.AbstractBean;
import br.com.samuelweb.certificado.Certificado;
import br.com.samuelweb.nfe.exception.NfeException;
import br.com.samuelweb.nfe.util.CertificadoUtil;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class AssinaA3Bean extends AbstractBean {

	/**
	 *
	 */
	private static final long serialVersionUID = -1483965114920732515L;
	
	
	@Getter
	private String smartCfgFile = "c:\\ibrcomp\\certificado\\SmartCard.cfg";
	@Getter
	@Setter
	private String senha ;
	@Getter
	@Setter
	private  String cnpjSoftwareHouse = "09571009000127";
	@Getter
	@Setter
	private  String cnpjCliente ;
	@Getter
	@Setter
	private  String assinatura;
	@Getter
	@Setter
	private String tributo;
	@Getter
	@Setter
	private String mensagemCupom;
	
	@Getter
	@Setter
	private Certificado certificadoTeste;
	
	//CERTIFICADO A3
	private CertificadoA3 certificado;
	private String conteudo;

	private StreamedContent arq;

	public void geraChave() {
		File diretorio = new File("c:\\ibrcomp\\tmp");
		diretorio.mkdir();
		File chave = new File(diretorio, "chave.dat");

		try{
			chave.createNewFile();
			FileWriter fileWriter = new FileWriter(chave,false);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			String a = "0";
			for (int i = 1; i <= 329; i++ ){
				a ="0"+ a ;
			}
			printWriter.println("09571009000127"+a);
			//	    printWriter.println("assinatura com 344 caracteres");
			printWriter.println(this.assinatura);
			for (int i = tributo.length(); i <= 4  ; i++ ){
				tributo = "0"+tributo;
			}
			printWriter.println(tributo);
			printWriter.println(mensagemCupom);
			printWriter.flush();
			printWriter.close();

		}catch (IOException e){
			e.printStackTrace();
		}

	}
	public StreamedContent getArq() {
		try{
			InputStream stream = new FileInputStream("c:\\ibrcomp\\tmp\\chave.dat");
			arq = new DefaultStreamedContent(stream, "application/dat", "chave.dat");
		}catch (IOException e){
			e.printStackTrace();
		}
		return arq;
	}


	public String juntaCnpj(){
		this.conteudo = cnpjSoftwareHouse+cnpjCliente;
		return conteudo;
	}

	public void assina()throws NoSuchAlgorithmException, InvalidKeyException, SignatureException , IOException{
		if ((cnpjCliente == null || cnpjCliente.isEmpty())&&(cnpjSoftwareHouse== null || cnpjSoftwareHouse.isEmpty())){
			FacesMessage msg = new FacesMessage("Todos os campos s�o de preenchimento obrigatorio "+ cnpjCliente + cnpjSoftwareHouse);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}else {
			getCertificado();
			juntaCnpj();
			Signature signature = Signature.getInstance("SHA256withRSA"); //Criando signature
			signature.initSign(certificado.getPrivateKey()); //setando privatekey do certificado
			signature.update(conteudo.getBytes()); //joga o conteudo como bytes
			byte[] signatureBytes = signature.sign(); //assina
			this.assinatura = Base64.encodeBase64String(signatureBytes);//transforma os bytes da assinatura em base64
			geraChave();
		}
	}

	public CertificadoA3 getCertificado() {
		return this.certificado = new CertificadoA3(smartCfgFile, senha); 
	}
	
	public List<Certificado> listaCertificados(){
		try {
			List<Certificado> certificados = CertificadoUtil.listaCertificadosWindows();
			for (Certificado certificado : certificados) {
				System.out.println("Nome Do Certificado:" + certificado.getNome());
				System.out.println("Dias Restantes para o Vencimento:" + certificado.getDiasRestantes());
				System.out.println("Vencimento:" + certificado.getVencimento());
			}
			return certificados;
		} catch (NfeException e) {
			System.out.println("Erro:" + e.getMessage());;
			return null;
		}
	}

}
