package br.com.nsym.domain.model.entity.financeiro;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.financeiro.tools.WsBanco;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="ContaCorrente", uniqueConstraints = {@UniqueConstraint(columnNames = {"contaCorrente","banco_ID" ,"id_empresa","id_filial"})})
public class ContaCorrente extends PersistentEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -4300897332820008868L;
	
	@Getter
	@Setter
	private String agencia;
	
	@Getter
	@Setter
	private String digitoAgencia;
	
	@Getter
	@Setter
	private boolean agenciaTemDigito = false;
	
	@Getter
	@Setter
	private String contaCorrente;
	
	@Getter
	@Setter
	private boolean contaTemDigito = false;
	
	@Getter
	@Setter
	private String digitoContaCorrente;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="banco_ID")
	private Banco banco;
	
	@Getter
	@Setter
	@OneToOne(mappedBy = "conta", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
	private InfBoleto infBoleto;
	
	@Getter
	@Setter
	@OneToOne(mappedBy = "conta", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
	private WsBanco wsBanco;
	
	@Getter
	@Setter
	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name="TabelaListaPagamentosXContaCorrente",joinColumns= @JoinColumn(name="ContaCorrente_ID"),
	inverseJoinColumns= @JoinColumn(name="FormaPagamento_ID"))
	private List<FormaDePagamento> listaDePagamentos = new ArrayList<FormaDePagamento>(); 


	@Override
	public String toString() {
		return "ContaCorrente [agencia=" + agencia + ", digitoAgencia=" + digitoAgencia + ", contaCorrente=" + contaCorrente 
			+ digitoContaCorrente + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(agencia, banco, contaCorrente, digitoAgencia, digitoContaCorrente);
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
		ContaCorrente other = (ContaCorrente) obj;
		return Objects.equals(agencia, other.agencia) && Objects.equals(banco, other.banco)
				&& Objects.equals(contaCorrente, other.contaCorrente)
				&& Objects.equals(digitoAgencia, other.digitoAgencia)
				&& Objects.equals(digitoContaCorrente, other.digitoContaCorrente);
	}


	
	
}
