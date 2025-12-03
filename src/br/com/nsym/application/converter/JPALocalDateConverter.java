package br.com.nsym.application.converter;

import java.sql.Date;
import java.time.LocalDate;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Converter customizado para dar suporte ao uso de LocalDate no JPA
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.2.0, 09/02/2015
 */
@Converter(autoApply = true)
public class JPALocalDateConverter implements AttributeConverter<LocalDate, Date> {

    /**
     * Converte o valor da entity para a base de dados
     * 
     * @param attribute
     * @return 
     */
    @Override
    public Date convertToDatabaseColumn(LocalDate attribute) {
        return attribute != null ? Date.valueOf(attribute) : null;
    }

    /**
     * Converte o valor da base para entidade
     * 
     * @param dbData
     * @return 
     */
    @Override
    public LocalDate convertToEntityAttribute(Date dbData) {
        return dbData != null ? dbData.toLocalDate() : null;
    }
}
