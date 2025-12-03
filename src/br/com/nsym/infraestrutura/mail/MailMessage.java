package br.com.nsym.infraestrutura.mail;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;

/**
 * Iterface que define uma mensagem de email
 *
 * @author Ibrahim Yousef Quatani
 *
 * @version 2.0.0
 * @since 1.1.0, 19/10/2016
 */
public interface MailMessage {

    /**
     * @return o titulo da mensagem
     */
    public String getTitle();
    
    /**
     * @return o conteudo da mensagem
     */
    public String getContent();
    
    /**
     * @return os emissores
     */
    public Address getFrom();
    
    /**
     * @return os emails usados para caso o cliente quiser responder o email
     */
    public Address getReplyTo();
    
    /**
     * @return os destinatarios
     */
    public InternetAddress[] getAddressees();
    
    /**
     * @return os destinatarios em copia
     */
    public InternetAddress[] getCcs();
}