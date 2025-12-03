package br.com.ibrcomp.sat;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;

public class CertificadoA1 {
	private PrivateKey privateKey = null;
	private X509Certificate certificate = null;

	public CertificadoA1(String file, String password) {
		try {
			InputStream entrada = new FileInputStream(file);
			KeyStore ks = KeyStore.getInstance("pkcs12");
			ks.load(entrada, password.toCharArray());

			String alias = "";
			Enumeration<String> aliasesEnum = ks.aliases();
			while (aliasesEnum.hasMoreElements()) {
				alias = aliasesEnum.nextElement();
				if (ks.isKeyEntry(alias)) {
					break;
				}
			}

			certificate = (X509Certificate) ks.getCertificate(alias);
			privateKey = (PrivateKey) ks.getKey(alias, password.toCharArray());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (KeyStoreException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			throw new RuntimeException("Senha do Certificado Digital esta incorreta ou Certificado inválido.");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		}

	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public X509Certificate getCertificate() {
		return certificate;
	}
	
	public Date getValidade(){
		return certificate.getNotAfter();
	}

	public Date getEmissao(){
		return certificate.getNotBefore();
	}

}
