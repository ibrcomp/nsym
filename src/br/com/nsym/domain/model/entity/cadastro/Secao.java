package br.com.nsym.domain.model.entity.cadastro;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="secao_produto",uniqueConstraints={@UniqueConstraint(columnNames={"secao","id_empresa"})})
public class Secao extends PersistentEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private String secao;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="secao")
	private List<Produto> produtos = new ArrayList<>();
}
