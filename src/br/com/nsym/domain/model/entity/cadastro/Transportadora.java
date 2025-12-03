package br.com.nsym.domain.model.entity.cadastro;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.fiscal.nfe.Transportador;
import br.com.nsym.domain.model.entity.tools.Enquadramento;
import br.com.nsym.domain.model.entity.tools.Uf;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transportadora", uniqueConstraints = { @UniqueConstraint(columnNames = { "cnpj", "id_empresa" }),
		@UniqueConstraint(columnNames = { "cpf", "id_empresa" }) })
public class Transportadora extends PersistentEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2209483501243259344L;

	@Getter
	@Setter
	@CNPJ
//	@NotNull(message = "{empresa.cnpj}")
	@Column(name = "cnpj", nullable = true)
	private String cnpj;

	@Getter
	@Setter
	private boolean temCnpj;

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
	@OneToOne(mappedBy = "transportadora")
	private EndComplemento endereco;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private Enquadramento enquadramento;

	@Getter
	@Setter
	@OneToMany(mappedBy = "transportadora")
	private List<Contato> contato = new ArrayList<Contato>();

	@Getter
	@Setter
	@OneToOne(mappedBy = "transportadora", cascade = CascadeType.ALL)
	private Email emailNFE;

	@Getter
	@Setter
	@OneToMany(mappedBy = "transportadora")
	private List<Cliente> clientes = new ArrayList<>();

	@Getter
	@Setter
	@OneToMany(mappedBy = "transportadora")
	private List<Fone> fones = new ArrayList<Fone>();

	@Getter
	@Setter
	@OneToMany(mappedBy = "transportadora")
	private List<Transportador> listaTransportador = new ArrayList<>();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(clientes, cnpj, contato, cpf, emailNFE, endereco, enquadramento, estado,
				fones, inscEstadual, listaTransportador, nomeFantasia, razaoSocial, temCnpj);
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
		Transportadora other = (Transportadora) obj;
		return Objects.equals(clientes, other.clientes) && Objects.equals(cnpj, other.cnpj)
				&& Objects.equals(contato, other.contato) && Objects.equals(cpf, other.cpf)
				&& Objects.equals(emailNFE, other.emailNFE) && Objects.equals(endereco, other.endereco)
				&& enquadramento == other.enquadramento && estado == other.estado && Objects.equals(fones, other.fones)
				&& Objects.equals(inscEstadual, other.inscEstadual)
				&& Objects.equals(listaTransportador, other.listaTransportador)
				&& Objects.equals(nomeFantasia, other.nomeFantasia) && Objects.equals(razaoSocial, other.razaoSocial)
				&& temCnpj == other.temCnpj;
	}
	
	

}
