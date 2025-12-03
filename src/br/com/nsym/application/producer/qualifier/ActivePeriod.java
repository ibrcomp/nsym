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

/**
 * Qualificador CDI para marcar que desejamos uma instancia do periodo ativo
 *
 * @author Ibrahim Yousef Quatani
 * @version 1.0.0
 * @since 2.2.0, 08/02/2016
 */
@Qualifier
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER, TYPE})
public @interface ActivePeriod { }
