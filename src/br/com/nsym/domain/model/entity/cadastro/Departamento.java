package br.com.nsym.domain.model.entity.cadastro;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="departamento",uniqueConstraints={@UniqueConstraint(columnNames={"departamento","id_empresa"})})

public class Departamento extends PersistentEntity {
		
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		@Getter
		@Setter
		@Column(name="departamento")
		private String departamento;
		
		@Getter
		@Setter
		@OneToMany(mappedBy="departamento", fetch=FetchType.LAZY)
		private List<Produto> produtos = new ArrayList<>();

		
}

