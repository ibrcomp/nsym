package br.com.nsym.domain.model.entity.cadastro;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
@Table(name="tamanho",uniqueConstraints= {@UniqueConstraint(columnNames = {"tamanho", "id_empresa"})})
public class Tamanho extends PersistentEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private String tamanho;
	
//	@Getter
//	@Setter
//	@ManyToMany
//	private List<Produto> produtos = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="tamanho",cascade=CascadeType.ALL,fetch=FetchType.LAZY)
	private List<BarrasEstoque> listaBarras = new ArrayList<>();
	
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="Grade_Id")
	private Grade grade;




	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(grade, listaBarras, tamanho);
		return result;
	}




	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tamanho other = (Tamanho) obj;
		return Objects.equals(grade, other.grade) && Objects.equals(listaBarras, other.listaBarras)
				&& Objects.equals(tamanho, other.tamanho);
	}




	@Override
	public String toString() {
		return String.format(
				"Tamanho [tamanho=%s, listaBarras=%s, grade=%s, getId()=%s, getIdEmpresa()=%s, getIdFilial()=%s]",
				tamanho, listaBarras, grade, getId(), getIdEmpresa(), getIdFilial());
	}


	

	
}
