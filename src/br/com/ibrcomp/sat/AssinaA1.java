package br.com.ibrcomp.sat;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;

import org.apache.commons.codec.binary.Base64;
public class AssinaA1 {


	private static final String certA1 = "E:\\certificado.pfx";
	private static final String senha = "senha";

	CertificadoA1 certificado = new CertificadoA1(certA1, senha);

	String cnpjSoftwahouse = "11111111111111"; //cnpj da softwarehouse
	String cnpjCliente     = "00000000000000"; //cnpj do proprietario do sta
	String conteudo = cnpjSoftwahouse+cnpjCliente;  

	public String assina() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException{
	Signature signature = Signature.getInstance("SHA256withRSA"); //Criando signature
	signature.initSign(certificado.getPrivateKey()); //setando privatekey do certificado
	signature.update(conteudo.getBytes()); //joga o conteudo como bytes
	byte[] signatureBytes = signature.sign(); //assina
	String assinatura = Base64.encodeBase64String(signatureBytes);//transforma os bytes da assinatura em base64
	return(assinatura);
	}

}
