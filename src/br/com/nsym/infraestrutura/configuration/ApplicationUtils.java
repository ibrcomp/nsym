package br.com.nsym.infraestrutura.configuration;

import java.util.ResourceBundle;

import javax.faces.application.ProjectStage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Classe utilitaria para uso em alguns pontos da aplicacao sem a necessidade de
 * ficar repetindo alguns codigos tais como pegar configuracoes ou a URL base da
 * aplicacaao para uso em alguma template
 *
 * @author Ibrahim Yousef Quatani
 *
 * @version 1.0.0
 * @since 2.0.0, 10/10/2016
 */
public class ApplicationUtils {

    /**
     * Busca no bundle de configuracoes da aplicacao uma determinada chave para
     * uma configuracao
     *
     * @param configurationKey a chave da qual queremos a configuracao
     * @return a configuracao para a chave informada
     */
    public static String getConfiguration(String configurationKey) {

        final ResourceBundle bundle = ResourceBundle.getBundle("nsymErpV2");

        return bundle.getString(configurationKey);
    }

    /**
     * Constroi a URL base da aplicacao
     *
     * @return a URL base da aplicaco + contexto
     */
    public static String buildBaseURL() {

        final FacesContext facesContext = FacesContext.getCurrentInstance();

        final HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();

        final StringBuilder builder = new StringBuilder();

        String actualPath = request.getRequestURL().toString();

        builder.append(actualPath.replace(request.getRequestURI(), ""));
        builder.append(request.getContextPath());

        return builder.toString();
    }

    /**
     * Checa em que estagio do projeto estamos, as opcoes sao as definidas no
     * enum {@link ProjectStage}
     *
     * @param projectStage o estagio do projeto
     * @return se estamos usando ele ou nao
     */
    public static boolean isStageRunning(ProjectStage projectStage) {
        return FacesContext.getCurrentInstance()
                .isProjectStage(projectStage);
    }

    /**
     * Gera um codigo aleatorio baseado em marcas do tempo
     *
     * @param size o tamanho
     * @param onlyNumbers se deve ou nao usar somente numeros
     * @return o codigo
     */
    public static String createRamdomCode(int size, boolean onlyNumbers) {

        final String digits;
        
        if (onlyNumbers) {
            digits = "0123456789";
        } else {
            digits = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        }
        
        long decimalNumber = System.nanoTime();

        String generated = "";

        int mod;
        int authCodeLength = 0;

        while (decimalNumber != 0 && authCodeLength < size) {
            mod = (int) (decimalNumber % digits.length());
            generated = digits.substring(mod, mod + 1) + generated;
            decimalNumber = decimalNumber / digits.length();
            authCodeLength++;
        }
        
        return generated;
    }
}
