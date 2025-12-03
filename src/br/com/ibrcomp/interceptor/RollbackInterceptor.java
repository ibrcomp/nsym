package br.com.ibrcomp.interceptor;


import java.util.Arrays;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.inject.Default;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.TransactionSynchronizationRegistry;

import br.com.nsym.application.component.Translator;

@SuppressWarnings("cdi-missing-interceptor-binding")
@Interceptor
public class RollbackInterceptor {

    private static final Logger logger = Logger.getLogger(RollbackInterceptor.class.getName());

    @Resource
    private TransactionSynchronizationRegistry tsr;
    
    @Inject
	@Default
	private Translator translator;

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        RollbackOn rollbackOn = ctx.getMethod().getAnnotation(RollbackOn.class);

        try {
            return ctx.proceed();
        } catch (Exception e) {
            if (rollbackOn != null && deveForcarRollback(e, rollbackOn.value())) {
                tsr.setRollbackOnly();
                logger.warning("Rollback forçado para exceção: " + e.getClass().getSimpleName() +
                               " no método: " + ctx.getMethod().getName());
                
                String chave = gerarChaveErro(e);
                adicionarMensagemFaces(translator.translate(chave)+ " - " + e.getMessage());
            }
            throw e;
        }
    }

    private boolean deveForcarRollback(Exception e, Class<? extends Exception>[] excecoes) {
        return Arrays.stream(excecoes).anyMatch(ex -> ex.isAssignableFrom(e.getClass()));
    }
    
    private String gerarChaveErro(Exception e) {
        // Exemplo: EstoqueException → erro.estoqueexception
        return "erro." + e.getClass().getSimpleName().toLowerCase();
    }

    private void adicionarMensagemFaces(String mensagem) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, null));
        }
    }

    

}
