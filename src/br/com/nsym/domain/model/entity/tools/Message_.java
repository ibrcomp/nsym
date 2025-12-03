package br.com.nsym.domain.model.entity.tools;

import br.com.nsym.domain.model.entity.PersistentEntity_;
import br.com.nsym.domain.model.security.User;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2022-07-30T20:31:46.515-0300")
@StaticMetamodel(Message.class)
public class Message_ extends PersistentEntity_ {
	public static volatile SingularAttribute<Message, String> title;
	public static volatile SingularAttribute<Message, String> content;
	public static volatile SingularAttribute<Message, Boolean> deleted;
	public static volatile SingularAttribute<Message, MessagePriorityType> priorityType;
	public static volatile SingularAttribute<Message, User> sender;
}
