package br.com.nsym.domain.model.entity.fiscal;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoMovimento;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="cfop",uniqueConstraints ={@UniqueConstraint(columnNames={"cfop","id_empresa"})})
public class CFOP extends PersistentEntity {
	
	/**
	 *
	 */
	private static final long serialVersionUID = 3544133075968717345L;

	@Getter
	@Setter
	@Column(name="cfop",nullable = false)
	private String cfop;
	
	@Getter
	@Setter
	private String descricao;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoMovimento tipoNota;
	
	@Getter
	@Setter
	@ManyToMany(mappedBy="listaCfop")
	private List<Ncm> ncm = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="cfopDentro")
	private List<Tributos> listaTributosDentro = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="cfopFora")
	private List<Tributos> listaTributosFora = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="cfopExterior")
	private List<Tributos> listaTributosExterior = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="cfopConsumidor")
	private List<Tributos> listaTributosConsumidor = new ArrayList<>();

	
}
