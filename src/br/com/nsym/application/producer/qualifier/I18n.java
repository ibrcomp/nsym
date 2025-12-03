package br.com.nsym.application.producer.qualifier;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.inject.Qualifier;

import br.com.nsym.application.producer.BundleProducer;

/**
 * Qualidficador CDI que identifica a producao/injecao de instancias do nosso
 * bundle de mensagens internacionalizados pelo produto {@link BundleProducer}
 *
 * @author Ibrahim Yousef Quatani	
 *
 * @version 1.0.0
 * @since 2.0.0, 25/10/2016
 */
@Qualifier
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER, TYPE})
public @interface I18n { }
