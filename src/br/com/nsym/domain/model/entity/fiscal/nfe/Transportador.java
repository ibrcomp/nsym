package br.com.nsym.domain.model.entity.fiscal.nfe;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.Transportadora;
import br.com.nsym.domain.model.entity.tools.TipoFrete;
import br.com.nsym.domain.model.entity.tools.Uf;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Transportador extends PersistentEntity{

	
	/**
	 *
	 */
	private static final long serialVersionUID = -247017184534658965L;


	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="transportadora_ID")
	private Transportadora transportadora;
	
	
	@Getter
	@Setter
	@OneToOne(mappedBy="transportador",cascade=CascadeType.ALL)
	private Nfe nfe;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoFrete tipoFrete;
	
	@Getter
	@Setter
	private String retiraNome;
	
	@Getter
	@Setter
	private String retiraEnd;
	
	@Getter
	@Setter
	private String retiraMunicipio;
	
	@Getter
	@Setter
	private String retiraUf;
	
	@Getter
	@Setter
	private String retiraDoc;
	
	@Getter
	@Setter
	private String retiraInsc;
	
	@Getter
	@Setter
	private String codigoAnnt;
	
	@Getter
	@Setter
	private String Placa;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private Uf ufPlaca;
	
	@Getter
	@Setter
	private Long quantidade;
	
	@Getter
	@Setter
	private String especie;
	
	@Getter
	@Setter
	private String marca;
	
	@Getter
	@Setter
	private String numeracao;
	
	@Getter
	@Setter
	@Column(precision = 17 , scale = 5)
	private BigDecimal pesoBruto;
	
	@Getter
	@Setter
	@Column(precision = 17 , scale = 5)
	private BigDecimal pesoLiquido;
	
	@Getter
	@Setter
	private String vagao;
	
	@Getter
	@Setter
	private String balsa;


	
}
