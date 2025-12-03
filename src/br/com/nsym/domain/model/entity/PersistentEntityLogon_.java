package br.com.nsym.domain.model.entity;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2022-07-28T04:05:43.963-0300")
@StaticMetamodel(PersistentEntityLogon.class)
public class PersistentEntityLogon_ {
	public static volatile SingularAttribute<PersistentEntityLogon, Long> id;
	public static volatile SingularAttribute<PersistentEntityLogon, Date> inclusion;
	public static volatile SingularAttribute<PersistentEntityLogon, Date> lastEdition;
	public static volatile SingularAttribute<PersistentEntityLogon, String> includedBy;
	public static volatile SingularAttribute<PersistentEntityLogon, String> editedBy;
	public static volatile SingularAttribute<PersistentEntityLogon, Long> idEmpresa;
	public static volatile SingularAttribute<PersistentEntityLogon, Long> idFilial;
	public static volatile SingularAttribute<PersistentEntityLogon, Boolean> isDeleted;
}
