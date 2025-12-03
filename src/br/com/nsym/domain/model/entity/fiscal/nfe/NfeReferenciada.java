package br.com.nsym.domain.model.entity.fiscal.nfe;

import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.tools.Uf;
import lombok.Getter;
import lombok.Setter;

@Entity
public class NfeReferenciada extends PersistentEntity {
	
		
	/**
	 *
	 */
	private static final long serialVersionUID = 5350964047933745920L;

	@Getter
	@Setter
	private String chaveReferenciada = "";
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private Uf cUF;

	@Getter
	@Setter
	private LocalDate aaMM = LocalDate.now();
	
	@Getter
	@Setter
	private String cnpj = "";
	
	@Getter
	@Setter
	private String modelo = "";
	
	@Getter
	@Setter
	private String serie ="";
	
	@Getter
	@Setter
	private String nNF = "";
	
	@Getter
	@Setter
	@ManyToOne(cascade=CascadeType.REMOVE)
	@JoinColumn(name="nfe_id")
	private Nfe nfe;


	
	
}
