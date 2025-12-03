package br.com.nsym.domain.model.entity.tools;

import br.com.nsym.domain.model.entity.PersistentEntity_;
import br.com.nsym.domain.model.security.User;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2022-07-30T20:31:46.548-0300")
@StaticMetamodel(UserMessage.class)
public class UserMessage_ extends PersistentEntity_ {
	public static volatile SingularAttribute<UserMessage, Boolean> read;
	public static volatile SingularAttribute<UserMessage, Boolean> deleted;
	public static volatile SingularAttribute<UserMessage, User> recipient;
	public static volatile SingularAttribute<UserMessage, Message> message;
}
