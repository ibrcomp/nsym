package br.com.nsym.application.producer;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Produto que gera os objetos de para fins de log do sistema, cada classe 
 * que necessitar de um logger pode requisitar a injecao via CDI
 *
 * @author Ibrahim Yousef Quatani	
 *
 * @version 1.0.0
 * @since 2.0.0, 29/10/2016
 */
public class LoggerProducer {

    /**
     * Produz um objeto de logger para quem solicitar via {@link Inject}
     * 
     * @param injectionPoint o ponto de injecao onde o logger sera inserido
     * @return o objeto org.slf4j.Logger para a classe solcitante
     */
    @Produces
    Logger produceLogger(InjectionPoint injectionPoint) {
        return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass());
    }
}
