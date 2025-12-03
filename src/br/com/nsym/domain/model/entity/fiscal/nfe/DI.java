package br.com.nsym.domain.model.entity.fiscal.nfe;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.tools.UfSigla;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="dImportacao",uniqueConstraints = {@UniqueConstraint(columnNames={"nnDi","id_empresa"}),
												@UniqueConstraint(columnNames={"nnDi","id_filial"})})

public class DI extends PersistentEntity {

	
	/**
	 *
	 */
	private static final long serialVersionUID = -5957193203685006221L;
	@Transient
	private MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);
	@Getter
	@Setter
	private String nnDi;
	@Getter
	@Setter
	private LocalDate dDi = LocalDate.now();
	@Getter
	@Setter
	private String xLocDesemb;
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private UfSigla uFDesemb;
	@Getter
	@Setter
	private LocalDate ddDesemb = LocalDate.now();
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoViaTransporte tpViaTransp;
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal vAFRMM= new BigDecimal("0",mc);			
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoIntermedio tpIntermedio;
	
	@Getter
	@Setter
	private String cnpjAdq;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private UfSigla uFTerceiro;
	
	@Getter
	@Setter
	private String cExportador;
	
	@Getter
	@Setter
	@OneToOne(mappedBy="di",cascade=CascadeType.ALL)
	private Nfe nfe;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="di" ,cascade=CascadeType.ALL)
	private List<Adicao> listaAdicao=new ArrayList<Adicao>();




	@Override
	public String toString() {
		return String.format(
				"DI [mc=%s, nDI=%s, dDi=%s, xLocDesemb=%s, uFDesemb=%s, dDesemb=%s, tpViaTransp=%s, vAFRMM=%s, tpIntermedio=%s, cnpjAdq=%s, uFTerceiro=%s, cExportador=%s, nfe=%s, listaAdicao=%s]",
				mc, nnDi, dDi, xLocDesemb, uFDesemb, ddDesemb, tpViaTransp, vAFRMM, tpIntermedio, cnpjAdq, uFTerceiro,
				cExportador, nfe, listaAdicao);
	}
	
	

}
