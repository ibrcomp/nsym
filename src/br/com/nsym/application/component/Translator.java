package br.com.nsym.application.component;

import br.com.nsym.application.producer.qualifier.I18n;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 * Factory que constroi e mantem em sessao uma instancia para uso do bundle de 
 * i18n da aplicacao
 *
* @author Ibrahim Yousef quatani
 *
 * @version 2.0.0
 * @since 1.0.0, 25/10/2016
 */
@ApplicationScoped
public class Translator {

    @Inject
    @Default
    private Logger logger;
    
    @I18n
    @Inject
    private ResourceBundle resourceBundle;
    
    /**
     * Prove as mensagens para os beans da aplicacao, por este metodo conseguimos
     * pegar as mensagens do contexto no bundle e usa-las nos managedbeans
     * 
     * Caso nao encontre a chave ou ela esteja em branco, retorna a propria chave
     * 
     * @param message a chave para pegar a traducao
     * @return a traducao/texto para a chave indicada
     */
    public String translate(String message) {
        
        try {
            final String text = this.resourceBundle.getString(message);
            return (text == null || text.isEmpty()) ? message : text;
        } catch (MissingResourceException ex) {
            this.logger.error("Can't find message for {}", message);
            return message;
        }
    } 
}
