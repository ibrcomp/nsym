package br.com.nsym.domain.model.entity.tools;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="mensagens",uniqueConstraints= {@UniqueConstraint(columnNames = {"tipoMensagem","id_empresa","id_filial"})})
public class MensagensInternas extends PersistentEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	private String mensagem;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoMensagem tipoMensagem;
}
