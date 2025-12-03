package br.com.nsym.application.producer;

import java.util.Properties;
import javax.enterprise.inject.Produces;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.NumberTool;

/**
 * Producer de instancias do motor de template do velocity
 * 
 * @author Ibrahim Yousef Quatani	
 *
 * @version 1.0.0
 * @since 2.0.0, 29/10/2016
 */
public class VelocityProducer {

    /**
     * @return produz uma instancia do velocity template para uso no sistema
     */
    @Produces
    VelocityEngine produceEngine() {

        final VelocityEngine engine = new VelocityEngine();

        // manda carregar os recursos dos resources
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "class");
        engine.setProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());

        final Properties properties = new Properties();

        // joga os logs do velocity no log do server
        properties.put("runtime.log.logsystem.class",
                "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        properties.put("runtime.log.logsystem.log4j.category", "velocity");
        properties.put("runtime.log.logsystem.log4j.logger", "velocity");

        engine.init(properties);

        return engine;
    }

    /**
     * @return produz um contexto para velocity engine
     */
    @Produces
    VelocityContext produceContext() {

        final VelocityContext context = new VelocityContext();

        // tools para trabalhar com data e numeros
        context.put("date", new DateTool());
        context.put("number", new NumberTool());

        return context;
    }
}
