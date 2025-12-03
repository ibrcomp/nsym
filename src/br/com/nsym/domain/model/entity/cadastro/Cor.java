package br.com.nsym.domain.model.entity.cadastro;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="cor",uniqueConstraints= {@UniqueConstraint(columnNames = {"nome", "id_empresa"})})
public class Cor extends PersistentEntity{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	private String nome;
	
//	@Getter
//	@Setter
//	@ManyToMany
//	private List<Produto> produtos = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="cor",cascade=CascadeType.ALL,fetch=FetchType.LAZY)
	private List<BarrasEstoque> listaBarras = new ArrayList<>();
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Grade_Id")
	private Grade grade;

	@Override
	public String toString() {
		return String.format("Cor [nome=%s, listaBarras=%s, grade=%s, getId()=%s, getIdEmpresa()=%s, getIdFilial()=%s]",
				nome, listaBarras, grade, getId(), getIdEmpresa(), getIdFilial());
	}


	

}
