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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.estoque.EntradaEstoque;
import br.com.nsym.domain.model.entity.financeiro.Credito;
import br.com.nsym.domain.model.entity.fiscal.nfe.Destinatario;
import br.com.nsym.domain.model.entity.tools.Enquadramento;
import br.com.nsym.domain.model.entity.tools.TipoCliente;
import br.com.nsym.domain.model.entity.tools.Uf;
import br.com.nsym.domain.model.entity.venda.DestinatarioPedido;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "fornecedor", uniqueConstraints = { @UniqueConstraint(columnNames = { "cnpj", "id_empresa" }),
		@UniqueConstraint(columnNames = { "cpf", "id_empresa" }) })
public class Fornecedor extends PersistentEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
//	@NotNull(message = "{empresa.cnpj}")
	@CNPJ
	@Column(name = "cnpj", nullable = true)
	private String cnpj;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoCliente tipoCliente;

	@Getter
	@Setter
	private boolean temCnpj;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "pais_ID")
	private Pais pais;

	@Getter
	@Setter
	@Column(name = "insc_estadual")
	private String inscEstadual;

	@Getter
	@Setter
//	@NotNull(message = "{cliente.cpf}")
	@CPF
	@Column(name = "cpf")
	private String cpf;

	@Getter
	@Setter
	@NotNull(message = "{empresa.razaoSocial}")
	@Column(nullable = true, length = 100)
	private String razaoSocial;

	@Setter
	@Getter
	@Enumerated(EnumType.STRING)
	private Uf estado;

	@Getter
	@Setter
	@Column(length = 100)
	private String nomeFantasia;

	@Getter
	@Setter
	@OneToOne(mappedBy = "fornecedor")
	private EndComplemento endereco;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private Enquadramento enquadramento;

	@Getter
	@Setter
	@OneToOne(mappedBy = "fornecedor", cascade = CascadeType.ALL)
	private Email emailNFE;

	@Getter
	@Setter
	@OneToMany(mappedBy = "fornecedor")
	private List<Contato> contato = new ArrayList<Contato>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy = "fornecedor")
	private List<EntradaEstoque> listaRecebimentosProdutos = new ArrayList<EntradaEstoque>();

	@Getter
	@Setter
	private String suframa;

	@Getter
	@Setter
	@OneToMany(mappedBy = "fornecedor")
	private List<Fone> fones = new ArrayList<Fone>();

	@Getter
	@ManyToMany(mappedBy = "fornecedores")
	private List<Produto> produtos = new ArrayList<>();

	@Getter
	@Setter
	@OneToMany(mappedBy = "fornecedor")
	private List<DestinatarioPedido> listaPedidos = new ArrayList<>();

	@Getter
	@Setter
	@OneToMany(mappedBy = "fornecedor")
	private List<Destinatario> destinatarios = new ArrayList<>();

	@Getter
	@Setter
	@Column(precision = 19, scale = 5)
	private BigDecimal perRedBaseCalculo = new BigDecimal("0");

	@Getter
	@Setter
	private boolean permiteReducao;

	@Getter
	@Setter
	@OneToMany(mappedBy = "fornecedor",cascade = CascadeType.ALL)
	private List<Credito> ListaCredito = new ArrayList<>();
	
	@Getter
	@Setter
	@Column(name="passaporte")
	private String idEstrangeiro;


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(cnpj, cpf, inscEstadual, nomeFantasia, razaoSocial, suframa);
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
		Fornecedor other = (Fornecedor) obj;
		return Objects.equals(cnpj, other.cnpj) && Objects.equals(cpf, other.cpf)
				&& Objects.equals(inscEstadual, other.inscEstadual) && Objects.equals(nomeFantasia, other.nomeFantasia)
				&& Objects.equals(razaoSocial, other.razaoSocial) && Objects.equals(suframa, other.suframa);
	}


	@Override
	public String toString() {
		return "Fornecedor [cnpj=" + cnpj + ", tipoCliente=" + tipoCliente + ", temCnpj=" + temCnpj + ", pais=" + pais
				+ ", inscEstadual=" + inscEstadual + ", cpf=" + cpf + ", razaoSocial=" + razaoSocial + ", estado="
				+ estado + ", nomeFantasia=" + nomeFantasia + ", endereco=" + endereco + ", enquadramento="
				+ enquadramento + ", suframa=" + suframa + ", idEstrangeiro=" + idEstrangeiro + "]";
	}


//	@Override
//	public String toString() {
//		return "Fornecedor [cnpj=" + cnpj + ", tipoCliente=" + tipoCliente + ", temCnpj=" + temCnpj + ", pais=" + pais
//				+ ", inscEstadual=" + inscEstadual + ", cpf=" + cpf + ", razaoSocial=" + razaoSocial + ", estado="
//				+ estado + ", nomeFantasia=" + nomeFantasia + ", endereco=" + endereco + ", enquadramento="
//				+ enquadramento + ", emailNFE=" + emailNFE + ", contato=" + contato + ", suframa=" + suframa
//				+ ", fones=" + fones + ", produtos=" + produtos + ", listaPedidos=" + listaPedidos + ", destinatarios="
//				+ destinatarios + ", perRedBaseCalculo=" + perRedBaseCalculo + ", permiteReducao=" + permiteReducao + "]";
//	}

}
