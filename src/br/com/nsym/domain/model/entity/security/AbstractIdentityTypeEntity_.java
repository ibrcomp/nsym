package br.com.nsym.domain.model.entity.security;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2022-07-28T04:05:43.970-0300")
@StaticMetamodel(AbstractIdentityTypeEntity.class)
public class AbstractIdentityTypeEntity_ {
	public static volatile SingularAttribute<AbstractIdentityTypeEntity, String> id;
	public static volatile SingularAttribute<AbstractIdentityTypeEntity, String> typeName;
	public static volatile SingularAttribute<AbstractIdentityTypeEntity, Date> createdDate;
	public static volatile SingularAttribute<AbstractIdentityTypeEntity, Date> expirationDate;
	public static volatile SingularAttribute<AbstractIdentityTypeEntity, Boolean> enabled;
	public static volatile SingularAttribute<AbstractIdentityTypeEntity, Integer> numeroConexao;
}
