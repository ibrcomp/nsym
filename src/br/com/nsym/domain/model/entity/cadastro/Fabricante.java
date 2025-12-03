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
@Table(name="fabricante",uniqueConstraints={@UniqueConstraint(columnNames={"marca","id_empresa"})})
public class Fabricante extends PersistentEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Column(name="marca")
	private String marca;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="fabricante")
	private List<Produto> produto = new ArrayList<>();
}
