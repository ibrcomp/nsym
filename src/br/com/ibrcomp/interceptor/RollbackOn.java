package br.com.ibrcomp.interceptor;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RollbackOn {
    Class<? extends Exception>[] value();
}

