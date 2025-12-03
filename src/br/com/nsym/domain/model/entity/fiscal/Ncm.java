package br.com.nsym.domain.model.entity.fiscal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.Produto;
import br.com.nsym.domain.model.entity.fiscal.Cfe.NfceItem;
import br.com.nsym.domain.model.entity.fiscal.tools.TpCredPressIBSZFM;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="ncm",uniqueConstraints= {@UniqueConstraint(columnNames = {"ncm", "id_empresa"})})
public class Ncm extends PersistentEntity{

	
	/**
	 *
	 */
	private static final long serialVersionUID = 3794804649767216324L;

	@Getter
	@Setter
	@Column(name="ncm",nullable = false)
	private String ncm;
	
	@Getter
	@Setter
	private String descricao;
	
	// IVA  ou Margem de valor agregado do produto
	@Getter
	@Setter
	private BigDecimal iva;
	
	@Getter
	@Setter
	@ManyToMany
	@JoinTable(name="T_NCM_CFOP", joinColumns=@JoinColumn(name="NCM_ID"),inverseJoinColumns=@JoinColumn(name="CFOP_ID"))
	private List<CFOP> listaCfop = new ArrayList<>();
	
	@Getter
	@Setter
	private boolean st;
	
	@Getter
	@Setter
	private String cest;
	
	@Getter
	@Setter
	@Column(precision = 5 , scale = 2)
	private BigDecimal aliqIcmsSat = new BigDecimal("0");
	
	@Getter
	@Setter
	private String exTipi;
	
	@Getter
	@Setter
	private BigDecimal valorTotalTributos;
	
	@Getter
	@Setter
	private boolean permiteReducao;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Tributo_id")
	private Tributos tributo;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="TributoFilial_id")
	private Tributos tributoFilial;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="ncm",cascade=CascadeType.MERGE)
	private List<TabIVAEstado> listaIVAEstado = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="ncm",cascade=CascadeType.MERGE)
	private List<TabFcpEstado> listaFCPEstado = new ArrayList<>();
		
	@Getter
	@Setter
	@OneToMany(mappedBy="ncm")
	private List<Produto> listaProdutos = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="ncm",cascade=CascadeType.MERGE)
	private List<NcmEstoque> listaEstoque = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="ncm",cascade=CascadeType.ALL)
	private List<NVE> listaNVE = new ArrayList<>();
	
	/*
	 * Reforma tributaria 2025, campo criado para validar se ncm é isento para cobrança de IS
	 */
	@Getter
	@Setter
	@Column(name = "excluir_se_isento")
    private boolean  excluirSeIsento = true;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TpCredPressIBSZFM tpCredPressIBSZFM;
	
	/*
	 * Adiciona o nve na lista setando o ncm
	 */
	public void addNVE(NVE it) { 
    	it.setNcm(this); 
    	listaNVE.add(it);
    }
	
	
	// * EX TIPI  EXCEï¿½ï¿½O DE IPI A SER VERIFICADA NA TABELA TIPI
	// * codigo CEST VERIFICAR EXISTENCIA DA TABELA DO CONFAZ
	// CASO POSSUI CEST ï¿½ UM PRODUTO COM SUBSTITUIï¿½ï¿½O TRIBUTARIA TENDO QUE SER INFORMADO OS IMPOSTOS ESPECIAIS
	// OU SEJA VERIFICAR EM CFOP ST PARA CALCULAR OS IMPOSTOS.
	
	// Lista de CFOPs para este NCM onde temos que cadastrar um modelo genï¿½rico 
	
	// Caso CEST preenchido exibir ICMS-ST PIS-ST COFINS-ST
	
	

}
