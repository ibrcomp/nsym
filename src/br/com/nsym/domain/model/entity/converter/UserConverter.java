package br.com.nsym.domain.model.entity.converter;

import javax.persistence.AttributeConverter;

import br.com.nsym.domain.model.security.User;

/**
 *
 * @author Ibrahim Yousef Quatani
 *
 * @version 1.0.0
 * @since 2.2.0, 19/10/2016
 */
public class UserConverter implements AttributeConverter<User, String> {

    /**
     *
     * @param attribute
     * @return
     */
    @Override
    public String convertToDatabaseColumn(User attribute) {
        return attribute.getId();
    }

    /**
     *
     * @param dbData
     * @return
     */
    @Override
    public User convertToEntityAttribute(String dbData) {
        final User user = new User();
        user.setId(dbData);
        return user;
    }
}
