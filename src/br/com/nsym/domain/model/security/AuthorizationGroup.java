package br.com.nsym.domain.model.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation utlizada para realizar o agrupamento das authorities via
 * reflection
 *
 * @author Ibrahim Yousef Quatani
 *
 * @version 1.0.0
 * @since 2.2.0, 22/10/2016
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthorizationGroup {

    /**
     * @return o agrupador para a authority anotada
     */
    String value() default "";
}
