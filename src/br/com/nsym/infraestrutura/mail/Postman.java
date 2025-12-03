package br.com.nsym.infraestrutura.mail;

import java.util.Date;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

/**
 * O nosso carteiro, ele escuta por qualquer evento relacionado ao envio de
 * mensagens, e caso alguem dispare um, ele se encarrega de encaminhar a
 * mensagem atraves da sessao ativa de email disponibilizada como recurso
 *
 * @author Ibrahim Yousef Quatani
 *
 * @version 2.0.0
 * @since 1.1.0, 19/10/2016
 */
@ApplicationScoped
public class Postman {

    @Resource(name = "java:/mail/nsymErpV2")
    private Session mailSession;
    
    /**
     * Escuta por eventos de envio de e-mail
     * 
     * @param mailMessage a mensagem a ser enviada
     * @throws Exception caso haja problemas, dispara exception
     */
    @Asynchronous
    public void send(@Observes MailMessage mailMessage) throws Exception {
       
        final MimeMessage message = new MimeMessage(this.mailSession);

        // header da mensagem
        message.setFrom(mailMessage.getFrom());
        message.setSubject(mailMessage.getTitle());
        message.setRecipients(Message.RecipientType.TO, mailMessage.getAddressees());
        message.setRecipients(Message.RecipientType.CC, mailMessage.getCcs());
        
        // a mensagem
        message.setText(mailMessage.getContent(), "UTF-8", "html");
        message.setSentDate(new Date());

        // envia
        Transport.send(message);
    }
}
