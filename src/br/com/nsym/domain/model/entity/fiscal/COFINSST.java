package br.com.nsym.domain.model.entity.fiscal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.fiscal.tools.CSTCOFINS;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoCalculo;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="cofinsst")
public class COFINSST extends PersistentEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -4686348749665982524L;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private CSTCOFINS cstCofinsSt;

	@Getter
	@Setter
	private TipoCalculo calculo;
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal valor = new BigDecimal("0");
	
	@Getter
	@Setter
	@OneToMany(mappedBy="cofinsSt")
	private List<Tributos> listaTributos = new ArrayList<>();
	
	
}
