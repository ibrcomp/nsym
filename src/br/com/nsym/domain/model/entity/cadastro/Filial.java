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
public class Filial extends PersistentEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@NotNull(message = "{empresa.cnpj}")
	@CNPJ
	@Column(name= "cnpj", unique = true , nullable = true  )
	private String cnpj;
	
	@Getter
	@Setter
	@Column(name= "insc_estadual")
	private String inscEstadual;
	
	@Getter
	@Setter
	@Lob
	private String assinaturaSat;
	
	@Getter
	@Setter
	private boolean emissorSatMatriz = false;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="pais_ID")
	private Pais pais;
	
	@Getter
	@Setter
	@NotNull(message="{empresa.razaoSocial}")
	@Column(nullable = true, length= 100)
	private String razaoSocial;
	
	@Getter
	@Setter
	@Column(length = 100)
	private String nomeFantasia;
	
	@Getter
	@Setter
	@OneToOne(mappedBy="filial")
	private EndComplemento endereco;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private Enquadramento enquadramento;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="filial")
	private List<Contato> contato = new ArrayList<Contato>();
	
	@Getter
	@Setter
	@OneToOne(mappedBy="filial",cascade=CascadeType.ALL)
	private Email emailNFE;
	
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="empresa_ID")
	private Empresa empresa;
	
	@Getter
	@Setter
	@Transient
	private List<User> usuarios;
	
	@Setter
	@Getter
	@Enumerated(EnumType.STRING)
	private Uf estado;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="filial")
	private List<Destinatario> destinatarios = new ArrayList<>();
	
	@Getter
	@Setter
	private boolean exporta;
	
	@Getter
	@Setter
	private String suframa;
	
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
	@OneToMany(mappedBy="filial")
	private List<Fone> listaFone;
	
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
	private String maxNSU;
	
	//�ltimo NSU pesquisado no Ambiente Nacional.
	@Getter
	@Setter
	private String ultNSU;
	
	@Getter
	@Setter
	private String nsuCapturado;
	
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
	
	@Getter
	@Setter
	private Long serieNfc;
	
	@Getter
	@Setter
	private Long numeroNfce;
	
	@Getter
	@Setter
	private String csc;
	
	@Getter
	@Setter
	private String idToken;
	
	@Getter
	@Setter
	private Boolean modoProducao = false;
	
	
}
