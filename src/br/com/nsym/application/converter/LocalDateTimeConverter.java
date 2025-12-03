package br.com.nsym.application.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Converter para manter a compatibilidade dos componentes hora do primefaces
 * com o java.time.LocalTime do Java 8
 * 
 * @author Ibrahim Yousef
 *
 * @version 1.0.0
 * @since 1.2.0, 27/08/2014
 */
@FacesConverter("localDateTimeConverter")
public class LocalDateTimeConverter implements Converter {


	/**
	 * Manda o objeto para o bean convertido em LocalTime
	 * 
	 * @param context
	 * @param component
	 * @param value
	 * 
	 * @return 
	 */
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return value != null ? LocalDateTime.parse(value, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : null;
	}

	/**
	 * Manda o objeto para a view, em String
	 * 
	 * @param context
	 * @param component
	 * @param value
	 * 
	 * @return 
	 */
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {

		final LocalDateTime time = (LocalDateTime) value;

		return time.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
	}
}


