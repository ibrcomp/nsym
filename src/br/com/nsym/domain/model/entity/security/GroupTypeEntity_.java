package br.com.nsym.domain.model.entity.security;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2022-07-28T04:05:43.979-0300")
@StaticMetamodel(GroupTypeEntity.class)
public class GroupTypeEntity_ extends AbstractIdentityTypeEntity_ {
	public static volatile SingularAttribute<GroupTypeEntity, String> name;
	public static volatile SingularAttribute<GroupTypeEntity, GroupTypeEntity> parent;
	public static volatile SingularAttribute<GroupTypeEntity, PartitionTypeEntity> partition;
}
