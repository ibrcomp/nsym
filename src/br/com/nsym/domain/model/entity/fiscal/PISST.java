package br.com.nsym.domain.model.entity.fiscal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.fiscal.tools.CSTPIS;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoCalculo;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="pisst")
public class PISST extends PersistentEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private CSTPIS cstPisSt;

	@Getter
	@Setter
	private TipoCalculo calculo;
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal valor = new BigDecimal("0");

	@Getter
	@Setter
	@OneToMany(mappedBy="pisSt")
	private List<Tributos> listTributos = new ArrayList<>();
}
