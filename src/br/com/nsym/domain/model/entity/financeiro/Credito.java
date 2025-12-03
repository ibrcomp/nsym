package br.com.nsym.domain.model.entity.financeiro;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.Cliente;
import br.com.nsym.domain.model.entity.cadastro.Colaborador;
import br.com.nsym.domain.model.entity.cadastro.Fornecedor;
import br.com.nsym.domain.model.entity.venda.Pedido;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="Credito",uniqueConstraints = {@UniqueConstraint(columnNames = {"cliente","id_empresa","id_filial"}),
											@UniqueConstraint(columnNames = {"fornecedor","id_empresa","id_filial"}),
											@UniqueConstraint(columnNames = {"colaborador","id_empresa","id_filial"})})
public class Credito extends PersistentEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cliente")
	private Cliente cliente;
	
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="fornecedor")
	private Fornecedor fornecedor;
	
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="colaborador")
	private Colaborador colaborador; 
	
	@Getter
	@Setter
	@OneToMany(fetch = FetchType.LAZY)
	private List<Pedido> listaCompras = new ArrayList<Pedido>();
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal saldoCreditoDevolucao = new BigDecimal("0") ;
	
	public Object classePreenchida() {
		if (this.cliente != null) {
			return this.cliente.getClass();
		}else {
			if (this.fornecedor != null) {
				return this.fornecedor.getClass();
			}else {
				if (this.colaborador != null) {
					return this.fornecedor.getClass();
				}else {
					return null;
				}
			}
		}
	}
	
	public Long idPreenchido() {
		if (this.cliente != null) {
			return this.cliente.getId();
		}else {
			if (this.fornecedor != null) {
				return this.fornecedor.getId();
			}else {
				if (this.colaborador != null) {
					return this.fornecedor.getId();
				}else {
					return 0L;
				}
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(cliente, colaborador, fornecedor, listaCompras, saldoCreditoDevolucao);
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
		Credito other = (Credito) obj;
		return Objects.equals(cliente, other.cliente) && Objects.equals(colaborador, other.colaborador)
				&& Objects.equals(fornecedor, other.fornecedor) && Objects.equals(listaCompras, other.listaCompras)
				&& Objects.equals(saldoCreditoDevolucao, other.saldoCreditoDevolucao);
	}

	@Override
	public String toString() {
		return "Credito [cliente=" + cliente + ", fornecedor=" + fornecedor + ", colaborador=" + colaborador
				+ ", saldoCreditoDevolucao=" + saldoCreditoDevolucao + "]";
	}
	
	

}
