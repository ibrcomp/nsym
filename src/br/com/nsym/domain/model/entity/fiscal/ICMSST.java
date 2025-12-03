package br.com.nsym.domain.model.entity.fiscal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.fiscal.tools.CSTNormal;
import br.com.nsym.domain.model.entity.fiscal.tools.CSTSimples;
import br.com.nsym.domain.model.entity.fiscal.tools.ModalidadeICMS;
import br.com.nsym.domain.model.entity.fiscal.tools.ModalidadeICMSST;
import br.com.nsym.domain.model.entity.fiscal.tools.Origem;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="icmsst")
public class ICMSST extends PersistentEntity {


	/**
	 *
	 */
	private static final long serialVersionUID = -6864290858284531274L;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private CSTSimples cstVendaSimples;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private CSTNormal cstVendaNormal;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private CSTNormal cstVendaNormalSimples;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private CSTSimples cstVendaSimplesNormal;
	
	@Getter
	@Setter
	private String cstConsumidor;
	
	@Getter
	@Setter
	private String descricao;
	
	@Getter
	@Setter
	private Origem origem;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private ModalidadeICMS modICMS;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private ModalidadeICMSST modICMSST;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="icmsSt")
	private List<Tributos> listaTributos = new ArrayList<>();

	
	
}
