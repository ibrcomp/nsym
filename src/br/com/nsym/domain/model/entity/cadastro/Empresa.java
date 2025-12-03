package br.com.nsym.domain.model.entity.cadastro;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.br.CNPJ;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.tools.VersaoSat;
import br.com.nsym.domain.model.entity.fiscal.Tributos;
import br.com.nsym.domain.model.entity.fiscal.nfe.Destinatario;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoImpressaoDFe;
import br.com.nsym.domain.model.entity.tools.Enquadramento;
import br.com.nsym.domain.model.entity.tools.Uf;
import br.com.nsym.domain.model.security.User;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Empresa extends PersistentEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@CNPJ
	@NotNull(message = "{empresa.cnpj}")
	@Column(name= "cnpj", unique = true , nullable = true  )
	private String cnpj;
	
	@Getter
	@Setter
	@Lob
	private String assinaturaSat;
	
	@Getter
	@Setter
	@Column(name= "insc_estadual")
	private String inscEstadual;
	
	@Getter
	@Setter
	private String suframa;
	
	@Getter
	@Setter
	@NotNull(message="{empresa.razaoSocial}")
	@Column(nullable = true, length= 100)
	private String razaoSocial;
	
	@Setter
	@Getter
	@Enumerated(EnumType.STRING)
	private Uf estado;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="pais_ID")
	private Pais pais;
	
	@Getter
	@Setter
	@Column(length = 100)
	private String nomeFantasia;
	
	@Getter
	@Setter
	@OneToOne(mappedBy="empresa")
	private EndComplemento endereco;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private Enquadramento enquadramento;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="empresa")
	private List<Contato> contato = new ArrayList<Contato>();
	
	@Getter
	@Setter
	@OneToOne(mappedBy="empresa",cascade=CascadeType.ALL)
	private Email emailNFE;
	
	@Getter
	@Setter
//	@NotNull(message="empresa.bancoDS")
	@Column(name="bancoDS",nullable = true)
	private String bancoDS;
	
	@Getter
	@Setter
//	@NotNull(message="empresa.userDS")
	@Column(name="userDS",nullable = true)
	private String userDS;
	
	@Getter
	@Setter
//	@NotNull(message="empresa.passwordDS")
	@Column(name="passwordDS",nullable = true)
	private String passwordDS;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="empresa")
	private List<Filial> filiais = new ArrayList<Filial>();
	
	@Getter
	@Setter
	@Transient
	private List<User> usuarios;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="empresa")
	private List<Fone> fones = new ArrayList<Fone>();
	
	@Getter
	@Setter
	private int limiteConexao;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="empresa")
	private List<Destinatario> destinatarios = new ArrayList<>();
	
	@Getter
	@Setter
	private boolean exporta;
	
	@Getter
	@Setter
	private BigDecimal aliqArpoveitaIcms;
	
	@Getter
	@Setter
	private BigDecimal reduzBaseIcms;
	
	@Getter
	@Setter
	private Long numeroNFe;
	
	@Getter
	@Setter
	private int portaAcbr;
	
	@Getter
	@Setter
	private String ipAcbr;
	
	
	//Maior NSU existente no Ambiente Nacional para o
		//CNPJ/CPF informado
	@Getter
	@Setter
	private String maxNSU = "0";
	
	//�ltimo NSU pesquisado no Ambiente Nacional.
	@Getter
	@Setter
	private String ultNSU = "0";
	
	@Getter
	@Setter
	private String nsuCapturado = "0";
	
	@Getter
	@Setter
	private boolean estoqueFiscalNegativo = false;
	
	@Setter
	@Getter
	private boolean estoqueNegativo = false;
	
	@Setter
	@Getter
	private boolean estoqueNegativoFab = false;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Tributo_id")
	private Tributos tributo;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="TributoST_id")
	private Tributos tributoST;
	
	@Getter
	@Setter
	private String serie;
	
	@Getter
	@Setter
	private boolean gerarCustoMedio = false;
	
	@Enumerated(EnumType.STRING)
	@Getter
	@Setter
	private VersaoSat versaoSat;
	
	@Getter
	@Setter
	private boolean baixaEstoqueGeral = false;
	
	@Getter
	@Setter
	private boolean integraReceber = false;
	
	@Getter
	@Setter
	private boolean tranferAutomatico = false;
	
	@Getter
	@Setter
	private boolean tranferPreco = false;
	
	@Getter
	@Setter
	private boolean previsaEntrega = false;
	
	@Getter
	@Setter
	private boolean nFCeAtivo = false;
	
	@Enumerated(EnumType.STRING)
	@Getter
	@Setter
	private TipoImpressaoDFe tpImp;
	
	@Getter
	@Setter
	private String cnaePrincipal;
	
}
