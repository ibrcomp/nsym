package br.com.nsym.domain.model.entity.security;

import org.picketlink.idm.jpa.annotations.entity.IdentityManaged;

import br.com.nsym.domain.model.security.GroupMembership;

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
@Table(name = "group_memberships")
@IdentityManaged(GroupMembership.class)
public class GroupMembershipTypeEntity extends RelationshipTypeEntity {

	private static final long serialVersionUID = -5005521635479551442L; }
