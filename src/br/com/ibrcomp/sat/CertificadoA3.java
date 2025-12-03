package br.com.ibrcomp.sat;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;

public class CertificadoA3 {
	private PrivateKey privateKey;
	private X509Certificate certificate;

	public CertificadoA3(String file, String password) {
		try {
			Provider p = Security.getProvider("SunPKCS11");
			Security.addProvider(p);  
            char[] pin = password.toCharArray();  
            System.clearProperty("java.security.KeyStore");
            System.clearProperty("java.security.PrivateKey");
            System.clearProperty("java.security.Provider");
            KeyStore ks = KeyStore.getInstance("pkcs11", p);  
            ks.load(null, pin);  

			String alias = "";
			Enumeration<String> aliasesEnum = ks.aliases();
			while (aliasesEnum.hasMoreElements()) {
				alias = aliasesEnum.nextElement();
				if (ks.isKeyEntry(alias)) {
					break;
				}
			}

			this.certificate = (X509Certificate) ks.getCertificate(alias);
			this.privateKey = (PrivateKey) ks.getKey(alias, password.toCharArray());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (KeyStoreException e1) {
			e1.printStackTrace();
			System.out.println("Keystore com problemas");
		} catch (IOException e) {
			throw new RuntimeException("Senha do Certificado Digital esta incorreta ou Certificado inv�lido.");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		}

	}

	public PrivateKey getPrivateKey() {
		return this.privateKey;
	}

	public X509Certificate getCertificate() {
		return this.certificate;
	}
	
	public Date getValidade(){
		return this.certificate.getNotAfter();
	}

	public Date getEmissao(){
		return certificate.getNotBefore();
	}

}
