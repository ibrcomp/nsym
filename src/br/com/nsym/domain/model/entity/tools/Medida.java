package br.com.nsym.domain.model.entity.tools;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.Produto;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="medidas",uniqueConstraints={@UniqueConstraint(columnNames={"sigla","id_empresa"})})
public class Medida extends PersistentEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	private String sigla;
	
	@Getter
	@Setter
	private String descricao;
	
	@Getter
	@Setter
	@OneToOne(mappedBy="medida")
	private Produto produto;
}
