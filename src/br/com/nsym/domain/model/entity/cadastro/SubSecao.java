package br.com.nsym.domain.model.entity.cadastro;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="subsecao_produto",uniqueConstraints={@UniqueConstraint(columnNames={"subSecao","id_empresa"})})
public class SubSecao extends PersistentEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Column(name="subsecao")
	private String subSecao;

	@Getter
	@Setter
	@OneToMany(mappedBy="subSecao")
	private List<Produto> produto = new ArrayList<>();
}
