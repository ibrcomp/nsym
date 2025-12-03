package br.com.nsym.domain.model.entity.security;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2022-07-28T04:05:43.980-0300")
@StaticMetamodel(PasswordTypeEntity.class)
public class PasswordTypeEntity_ {
	public static volatile SingularAttribute<PasswordTypeEntity, Long> id;
	public static volatile SingularAttribute<PasswordTypeEntity, String> typeName;
	public static volatile SingularAttribute<PasswordTypeEntity, Date> effectiveDate;
	public static volatile SingularAttribute<PasswordTypeEntity, Date> expiryDate;
	public static volatile SingularAttribute<PasswordTypeEntity, String> encodedHash;
	public static volatile SingularAttribute<PasswordTypeEntity, String> salt;
	public static volatile SingularAttribute<PasswordTypeEntity, UserTypeEntity> owner;
}
