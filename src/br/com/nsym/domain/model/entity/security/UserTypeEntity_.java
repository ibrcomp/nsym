package br.com.nsym.domain.model.entity.security;

import br.com.nsym.domain.model.entity.tools.Configuration;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2022-03-23T19:22:15.391-0300")
@StaticMetamodel(UserTypeEntity.class)
public class UserTypeEntity_ extends AbstractIdentityTypeEntity_ {
	public static volatile SingularAttribute<UserTypeEntity, String> username;
	public static volatile SingularAttribute<UserTypeEntity, String> name;
	public static volatile SingularAttribute<UserTypeEntity, String> email;
	public static volatile SingularAttribute<UserTypeEntity, String> theme;
	public static volatile SingularAttribute<UserTypeEntity, String> menuLayout;
	public static volatile SingularAttribute<UserTypeEntity, PartitionTypeEntity> partition;
	public static volatile SingularAttribute<UserTypeEntity, Long> idEmpresa;
	public static volatile SingularAttribute<UserTypeEntity, Long> idFilial;
	public static volatile SingularAttribute<UserTypeEntity, Configuration> config;
}
