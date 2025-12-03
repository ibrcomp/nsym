package br.com.nsym.domain.model.entity.security;

import org.picketlink.idm.jpa.annotations.entity.IdentityManaged;

import br.com.nsym.domain.model.security.Grant;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
*
* @author Ibrahim Yousef Quatani
*
* @version 1.0.0
* @since 2.0.0, 19/10/2016
*/
@Entity
@Table(name = "grants")
@IdentityManaged(Grant.class)
public class GrantTypeEntity extends RelationshipTypeEntity {

	private static final long serialVersionUID = -7490263259770198054L; }
