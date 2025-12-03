package br.com.nsym.domain.model.entity.financeiro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.financeiro.tools.ParcelasNfe;
import br.com.nsym.domain.model.entity.tools.PedidoStatus;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="AgTitulo",uniqueConstraints = {@UniqueConstraint(columnNames={"id","id_empresa","id_filial"})})
public class AgTitulo extends PersistentEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -259591861090935259L;
	
	@Getter
	@Setter
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name="TabelaListaTitulosAgrupados",joinColumns= @JoinColumn(name="AgTitulo_ID"),
	inverseJoinColumns= @JoinColumn(name="Titulo_ID"))
	private List<ParcelasNfe> listaTitulosAgrupados = new ArrayList<ParcelasNfe>();
	
	@Getter
	@Setter
	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name="TabelaListaRecebimentoParcialAgTitulo",joinColumns= @JoinColumn(name="AgTitulo_ID"),
	inverseJoinColumns= @JoinColumn(name="RecParcial_id"))
	private List<RecebimentoParcial> listRecebimentoParcial = new ArrayList<RecebimentoParcial>();
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Caixa_id")
	private Caixa caixa;
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorTotal = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorBruto = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal desconto = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal acrescimo = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorRecebido = new BigDecimal("0");
	
	@Getter
	@Setter
	private LocalDate dataRec;
	
	@Getter
	@Setter
	private LocalTime horaRec;
	
	@Getter
	@Setter
	private LocalDate dataCriacao;
	
	@Getter
	@Setter
	private LocalTime horaCriacao;
	
	@Getter
	@Setter
	private String obs;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private PedidoStatus status;
	

}
