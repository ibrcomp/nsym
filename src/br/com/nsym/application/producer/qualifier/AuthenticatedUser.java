package br.com.nsym.application.producer.qualifier;

import br.com.nsym.application.controller.UserSessionBean;
import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.inject.Qualifier;

/**
 * Qualificador CDI para identificar a producao/injecao de dependencias do 
 * usuario autenticado no sistema pelo produto {@link UserSessionBean}
 * 
 * @author Ibrahim Yousef quatani
 *
 * @version 1.1.0
 * @since 2.0.0, 25/10/2016
 */
@Qualifier
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER, TYPE})
public @interface AuthenticatedUser { }
