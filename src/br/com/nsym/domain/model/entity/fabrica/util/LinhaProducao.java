package br.com.nsym.domain.model.entity.fabrica.util;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
public class LinhaProducao extends PersistentEntity{

	/**
	 *
	 */
	private static final long serialVersionUID = 3433972913208145277L;

	@Getter
	@Setter
	private String descricao;
	
	@Getter
	@Setter
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name="Tabela_SequenciaProducao",joinColumns= @JoinColumn(name="linhaProd_Id"),
	inverseJoinColumns= @JoinColumn(name="sequencia_Id"))
	private List<SequenciaLinhaProducao> sequenciaProducao = new ArrayList<>();
	
	
}
