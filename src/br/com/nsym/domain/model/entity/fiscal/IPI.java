package br.com.nsym.domain.model.entity.fiscal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.fiscal.tools.CSTIPI;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoCalculo;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="ipi")
public class IPI extends PersistentEntity{


	/**
	 *
	 */
	private static final long serialVersionUID = 7172575049505355009L;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private CSTIPI cst;
	
	@Getter
	@Setter
	private int codigoEnquadramento;
	
	@Getter
	@Setter
	private int classeEnquadramento;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoCalculo	calculo;
	
	@Getter
	@Setter
	private BigDecimal valor;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="ipi")
	private List<Tributos> listaTributos = new ArrayList<>();

	
}
