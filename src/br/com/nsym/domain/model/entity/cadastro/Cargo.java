package br.com.nsym.domain.model.entity.cadastro;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="cargo")
public class Cargo extends PersistentEntity{
	
	/**
	 *
	 */
	private static final long serialVersionUID = -3975480518443304125L;

	@Getter
	@Setter
	@Column(name="cargo",unique = true, nullable = true)
	private String cargo;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="cargo")
	private List<Colaborador> colaboradores = new ArrayList<>();
	
	
}
