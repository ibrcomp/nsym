package br.com.nsym.domain.model.entity.cadastro;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="grade")
public class Grade extends PersistentEntity{
	
	/**
	 * Classe que controla a GRADE disponível para venda 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private String grade;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="grade")
	private List<Tamanho> tamanhos = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="grade")
	private List<Cor> cores= new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="grade")
	private List<Produto> produtos;

}
