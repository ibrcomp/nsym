package br.com.nsym.infraestrutura.primefaces;

import java.io.IOException;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.inputnumber.InputNumber;
import org.primefaces.component.inputnumber.InputNumberRenderer;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.util.HTML;

/**
 * Customizacao da renderer do inputNumber para acertar sua classe CSS no 
 * layout da aplicacao
 * 
 * @author Ibrahim Yousef Quatani
 *
 * @version 2.0.0
 * @since 1.1.0, 19/10/2016
 */
public class CustomInputNumberRenderer extends InputNumberRenderer {

    private static final String CUSTOM_CLASSES = "form-control";
    
    /**
     * GAMBIARRA NECESSARIA! Zeus tenha piedade de mim e do programador que fez
     * a merda do inputnumber, o class deve ser escrito no input de saida e nao
     * na span que envolve o componente. Alias, pq tem um span la? :P
     * 
     * @param context
     * @param inputNumber
     * @param clientId
     * @throws IOException 
     */
//    @Override
    protected void encodeOutput(FacesContext context, InputNumber inputNumber, String clientId) throws IOException {
        
        final ResponseWriter writer = context.getResponseWriter();
        
        final String inputId = clientId + "_input";

        // como o PF nao sabe colocar a class na input, coloca apenas 
        // na span que envolve o elemento, fiz esse bypass para que quando
        // renderizar o elemento a classe do form-control seja adicionada
        String defaultClass = InputText.STYLE_CLASS + " pe-inputNumber " + CUSTOM_CLASSES;
        
        defaultClass = inputNumber.isValid() ? defaultClass : defaultClass + " ui-state-error";
        defaultClass = !inputNumber.isDisabled() ? defaultClass : defaultClass + " ui-state-disabled";

        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", inputNumber.getType(), null);

        renderPassThruAttributes(context, inputNumber, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, inputNumber, HTML.INPUT_TEXT_EVENTS);

        if (inputNumber.isReadonly()) {
            writer.writeAttribute("readonly", "readonly", "readonly");
        }
        if (inputNumber.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", "disabled");
        }

        writer.writeAttribute("class", defaultClass, "");

        writer.endElement("input");
    }
}
