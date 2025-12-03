package br.com.nsym.domain.model.entity.security;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2021-10-01T17:09:20.357-0300")
@StaticMetamodel(RelationshipIdentityTypeEntity.class)
public class RelationshipIdentityTypeEntity_ {
	public static volatile SingularAttribute<RelationshipIdentityTypeEntity, Long> id;
	public static volatile SingularAttribute<RelationshipIdentityTypeEntity, String> descriptor;
	public static volatile SingularAttribute<RelationshipIdentityTypeEntity, String> identityType;
	public static volatile SingularAttribute<RelationshipIdentityTypeEntity, RelationshipTypeEntity> owner;
}
