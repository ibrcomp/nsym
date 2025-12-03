package br.com.nsym.application.channels;

import java.io.Serializable;

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

    public DadosDeConexaoSocket(String nameHost, int porta) {
        this.nameHost = nameHost;
        this.porta = porta;
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
