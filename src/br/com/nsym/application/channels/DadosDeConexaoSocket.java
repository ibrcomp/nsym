package br.com.nsym.application.channels;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Ibrahim Yousef
 * 
 */
public class DadosDeConexaoSocket implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String nameHost;
    private int porta;
    @Getter @Setter
    private String csc;
    @Getter @Setter
    private String idToken;
    

    public DadosDeConexaoSocket(String nameHost, int porta) {
        this.nameHost = nameHost;
        this.porta = porta;
    }
    
    public DadosDeConexaoSocket(String nameHost, int porta,String csc,String idToken) {
        this.nameHost = nameHost;
        this.porta = porta;
        this.csc = csc;
        this.idToken = idToken;
    }

    public String getNameHost() {
        return nameHost;
    }

    public void setNameHost(String nameHost) {
        this.nameHost = nameHost;
    }

    public int getPorta() {
        return porta;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }
}
