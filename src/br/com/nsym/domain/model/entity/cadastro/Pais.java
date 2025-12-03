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
@Table(name="bacen")
public class Pais extends PersistentEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Getter
	@Setter
	private Long codigo;
	@Getter
	@Setter
	private String nome;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="pais")
	private List<Cliente> listaClientes= new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="pais")
	private List<Colaborador> listaColaboradores = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="pais")
	private List<Fornecedor> listaFornecedores= new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="pais")
	private List<Empresa> listaEmpresas= new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="pais")
	private List<Filial> listaFiliais = new ArrayList<>();

}
