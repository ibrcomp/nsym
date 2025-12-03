package br.com.nsym.domain.model.entity;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2022-07-28T04:05:43.956-0300")
@StaticMetamodel(PersistentEntity.class)
public class PersistentEntity_ {
	public static volatile SingularAttribute<PersistentEntity, Long> id;
	public static volatile SingularAttribute<PersistentEntity, Date> inclusion;
	public static volatile SingularAttribute<PersistentEntity, Date> lastEdition;
	public static volatile SingularAttribute<PersistentEntity, String> includedBy;
	public static volatile SingularAttribute<PersistentEntity, String> editedBy;
	public static volatile SingularAttribute<PersistentEntity, Long> idEmpresa;
	public static volatile SingularAttribute<PersistentEntity, Long> idFilial;
	public static volatile SingularAttribute<PersistentEntity, Boolean> isDeleted;
}
