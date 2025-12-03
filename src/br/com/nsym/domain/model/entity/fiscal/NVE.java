package br.com.nsym.domain.model.entity.fiscal;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="NVE",uniqueConstraints= {@UniqueConstraint(columnNames = {"nVE","Ncm_ID","id_empresa"})})
public class NVE extends PersistentEntity{

	/**
	 *
	 */
	private static final long serialVersionUID = -7746238161719015129L;

	@Getter
	@Setter
	private String nVE;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Ncm_ID")
	private Ncm ncm;

	@Override
	public String toString() {
		return "NVE [nVE=" + nVE + ", ncm=" + ncm + "]";
	}
	
	
	
}
