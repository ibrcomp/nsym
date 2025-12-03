package br.com.nsym.application.controller.nfe.tools;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="numeroNfeSemUso",uniqueConstraints = {@UniqueConstraint(columnNames={"numeroLivre","id_empresa","id_filial"})})
public class NumeroSemUtilizacaoNFe extends PersistentEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private Long numeroLivre;
	
	@Getter
	@Setter
	private boolean bloqueado;
}
