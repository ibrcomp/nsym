package br.com.nsym.application.producer;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

/**
 * Produtor de contextos do sistema
 *
 * @author Ibrahim Yousef Quatani
 *
 * @version 1.0.0
 * @since 2.0.0, 29/10/2016
 */
public class FacesContextProducer {

    /**
     * Produz um contexto valido do {@link RequestContext}
     *
     * @return um {@link RequestContext} valido
     */
    @Produces
    @RequestScoped
    RequestContext produceRequestContext() {
        return RequestContext.getCurrentInstance();
    }

    /**
     * Produz um contexto valido do {@link FacesContext}
     *
     * @return um {@link FacesContext} valido
     */
    @Produces
    @RequestScoped
    FacesContext produceFacesContext() {
        return FacesContext.getCurrentInstance();
    }
}
