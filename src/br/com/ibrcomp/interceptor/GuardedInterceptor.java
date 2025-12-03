package br.com.ibrcomp.interceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Priority;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import br.com.nsym.application.component.Translator;

@Interceptor
@Guarded("") 
@Priority(200)
public class GuardedInterceptor {

    private final Map<String, Boolean> estados = new ConcurrentHashMap<>();

    @Inject
    private Translator messageResolver;

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        Guarded annotation = ctx.getMethod().getAnnotation(Guarded.class);
        String chave = annotation.value();

        if (estados.putIfAbsent(chave, true) != null) {
            String msg = messageResolver.translate("guarded.action.in.progress");
            FacesContext facesContext = FacesContext.getCurrentInstance();
            if (facesContext != null) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, msg, null));
            }
            return null;
        }

        try {
            return ctx.proceed();
        } finally {
            estados.remove(chave);
        }
    }
}
