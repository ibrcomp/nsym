package br.com.nsym.domain.model.entity.cadastro;

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
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.br.CPF;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.financeiro.Credito;
import br.com.nsym.domain.model.entity.fiscal.nfe.Destinatario;
import br.com.nsym.domain.model.entity.tools.TipoColaborador;
import br.com.nsym.domain.model.entity.tools.TipoPagamentoComissao;
import br.com.nsym.domain.model.entity.venda.DestinatarioPedido;
import br.com.nsym.domain.model.entity.venda.ItemPedido;
import br.com.nsym.domain.model.entity.venda.Pedido;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="colaborador",uniqueConstraints = {@UniqueConstraint(columnNames = {"cpf","id_empresa","id_filial"})})
public class Colaborador extends PersistentEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 * Tabela de colaboradores para controle de comiss�es 
	 *
	 * @author Ibrahim Yousef quatani
	 *
	 * @version 2.0.0
	 * @since 1.0.0, 25/07/2017
	 */
	@Getter
	@Setter
	@NotNull(message="{cliente.cpf}")
	@CPF
	@Column(name="cpf", nullable = true)
	private String cpf;
	
	@Getter
	@Setter
	private String nome;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="pais_ID")
	private Pais pais;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="cargo_id")
	private Cargo cargo;
	
	@Getter
	@Setter
	private String apelido;
	
	@Getter
	@Setter
	@OneToOne(mappedBy="colaborador",cascade = CascadeType.ALL)
	private Email email;

	@Getter
	@Setter
	@OneToOne(mappedBy="colaborador")
	private EndComplemento endereco;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoColaborador tipoColaborador;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="colaborador")
	private List<Contato> contatos = new ArrayList<Contato>();
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoPagamentoComissao tipoComissao;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="colaborador")
	private List<Cliente> clientes = new ArrayList<Cliente>();
	
	@Getter
	@Setter
	@OneToOne(mappedBy="colaborador")
	private Destinatario destino;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="colaborador")
	private List<DestinatarioPedido> listaPedidos = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy ="atendente")
	private List<Pedido> listaPedidosVendas = new ArrayList<>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy="vendedor")
	private List<ItemPedido> listaItemsPedido =  new ArrayList<ItemPedido>();
	
	@Getter
	@Setter
	@OneToMany(mappedBy = "colaborador",cascade = CascadeType.ALL)
	private List<Credito> ListaCredito = new ArrayList<>();



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(apelido, cpf, nome);
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
		Colaborador other = (Colaborador) obj;
		return Objects.equals(apelido, other.apelido) && Objects.equals(cpf, other.cpf)
				&& Objects.equals(nome, other.nome);
	}



	@Override
	public String toString() {
		return "Colaborador [cpf=" + cpf + ", nome=" + nome + ", apelido=" + apelido + ", getId()=" + getId() + "]";
	}

	

}
