package br.com.nsym.infraestrutura.mail;

import java.util.ArrayList;
import java.util.List;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import lombok.Setter;

/**
 * Iterface que define uma mensagem de email
 *
 * @author Ibrahim Yousef Quatani
 *
 * @version 2.0.0
 * @since 1.1.0, 19/10/2016
 */
public class AbstractMailMessage implements MailMessage {

    @Setter
    private String title;
    @Setter
    private String content;
    @Setter
    private Address from;
    @Setter
    private Address replyTo;
    @Setter
    private List<InternetAddress> ccs;
    @Setter
    private List<InternetAddress> addressees;

    /**
     * 
     */
    public AbstractMailMessage() {
        this.ccs = new ArrayList<>();
        this.addressees = new ArrayList<>();
    }
    
    /**
     * 
     * @param cc 
     */
    public void addCc(InternetAddress cc) {
        this.ccs.add(cc);
    }
    
    /**
     * 
     * @param addressee 
     */
    public void addAddressees(InternetAddress addressee) {
        this.addressees.add(addressee);
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public String getTitle() {
        return this.title;
    }

    /**
     * 
     * @return 
     */
    @Override
    public String getContent() {
        return this.content;
    }

    /**
     * 
     * @return 
     */
    @Override
    public Address getFrom() {
        return this.from;
    }

    /**
     * 
     * @return 
     */
    @Override
    public Address getReplyTo() {
        return this.replyTo;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public InternetAddress[] getCcs() {
        return this.ccs.toArray(new InternetAddress[0]);
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public InternetAddress[] getAddressees() {
        return this.addressees.toArray(new InternetAddress[0]);
    }
}