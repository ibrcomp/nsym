package br.com.nsym.domain.model.entity.cadastro;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.financeiro.Credito;
import br.com.nsym.domain.model.entity.fiscal.nfe.Destinatario;
import br.com.nsym.domain.model.entity.tools.Enquadramento;
import br.com.nsym.domain.model.entity.tools.TipoCliente;
import br.com.nsym.domain.model.entity.tools.Uf;
import br.com.nsym.domain.model.entity.venda.DestinatarioPedido;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="cliente", uniqueConstraints = {@UniqueConstraint(columnNames = {"cnpj", "id_empresa"}),
											@UniqueConstraint(columnNames = {"cpf","id_empresa"}),
											@UniqueConstraint(columnNames = {"passaporte","id_empresa"})})
public class Cliente extends PersistentEntity {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
//	@NotNull(message="{cliente.cnpj}")
	@CNPJ
	@Column(name="cnpj", nullable = true)
	private String cnpj;
	
	@Getter
	@Setter
	@Column(name="insc_estadual",length = 20)
	private String inscEstadual;
	
	@Getter
	@Setter
	@Column(name="passaporte")
	private String idEstrangeiro;
	
	@Getter
	@Setter
	@Column(name="cod_Suframa")
	private String suframa;
	
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
//	@NotNull(message="{cliente.cpf}")
	@CPF
	@Column(name="cpf",nullable = true)
	private String cpf;
	
	@Getter
	@Setter
	private boolean consumidorFinal;
	
	@Getter
	@Setter
	private String rg;
	
	@Getter
	@Setter
	private String razaoSocial;
	
	@Getter
	@Setter
	private String nomeFantasia;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="cliente",fetch = FetchType.LAZY)
	private List<Contato> contato = new ArrayList<Contato>();
	
	@Getter
	@Setter
	@OneToOne(mappedBy="cliente",cascade=CascadeType.ALL,fetch = FetchType.LAZY)
	private Email emailNFE;
	
	@Getter
	@Setter
	private boolean permiteReducao ;
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal perRedBaseCalculo = new BigDecimal("0");
	
	@Getter
	@Setter
	@OneToOne(mappedBy="cliente",fetch = FetchType.LAZY)
	private EndComplemento endereco ;
	
	@Getter
	@Setter
	private String observacoes;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="cliente",fetch = FetchType.LAZY)
	private List<Fone> fone = new ArrayList<Fone>();
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private Enquadramento enquadramento;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoCliente tipoCliente;
	
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="colaborador_id")
	private Colaborador colaborador;
	
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="transportadora_id")
	private Transportadora transportadora;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="cliente",fetch = FetchType.LAZY)
	private List<Destinatario> destinatarios = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="cliente", fetch = FetchType.LAZY)
	private List<DestinatarioPedido> listaPedidos = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy = "cliente",cascade = CascadeType.ALL)
	private List<Credito> ListaCredito = new ArrayList<>();



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(cnpj, cpf, inscEstadual, razaoSocial);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cliente other = (Cliente) obj;
		return Objects.equals(cnpj, other.cnpj) && Objects.equals(cpf, other.cpf)
				&& Objects.equals(inscEstadual, other.inscEstadual) && Objects.equals(razaoSocial, other.razaoSocial);
	}

	@Override
	public String toString() {
		return "Cliente [cnpj=" + cnpj + ", inscEstadual=" + inscEstadual + ", idEstrangeiro=" + idEstrangeiro
				+ ", suframa=" + suframa + ", estado=" + estado + ", pais=" + pais + ", cpf=" + cpf
				+ ", consumidorFinal=" + consumidorFinal + ", rg=" + rg + ", razaoSocial=" + razaoSocial
				+ ", nomeFantasia=" + nomeFantasia + ", contato=" + contato + ", emailNFE=" + emailNFE
				+ ", perRedBaseCalculo=" + perRedBaseCalculo + ", endereco=" + endereco + ", observacoes=" + observacoes
				+ ", fone=" + fone + ", enquadramento=" + enquadramento + ", tipoCliente=" + tipoCliente
				+ ", colaborador=" + colaborador + ", transportadora=" + transportadora + ", destinatarios="
				+ destinatarios + ", listaPedidos=" + listaPedidos + ", getId()=" + getId() + ", getIdEmpresa()="
				+ getIdEmpresa() + ", getIdFilial()=" + getIdFilial() + "]";
	}

	public String toFilter(String campo){
		String result="";
		if (campo.equals("cnpj")){
			result = getCnpj().replaceAll("[^0-9]", "");
		}
		if (campo.equals("inscEstadual")){
			result = getInscEstadual().replaceAll("[^0-9]", "");
		}
		if (campo.equals("idEstrangeiro")){
			result = getIdEstrangeiro().replaceAll("[^0-9]", "");
		}
		if (campo.equals("cpf")){
			result = getCpf().replaceAll("[^0-9]", "");
		}
		if (campo.equals("rg")){
			result = getRg().replaceAll("[^0-9]", "");
		}
		if (campo.equals("razaoSocial")){
			result = getRazaoSocial();
		}
		if (campo.equals("nomeFantasia")){
			result = getNomeFantasia();
		}
		if (campo.equals("emailNFE")){
			result = getEmailNFE().getEmail();
		}
		if (campo.equals("colaborador")){
			result = getColaborador().getNome();
		}
		return result;
	}
	
	
	

}
