package br.com.nsym.domain.model.entity.fabrica;

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
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.fabrica.util.GradeProducao;
import br.com.nsym.domain.model.entity.fabrica.util.TipoEnfesto;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="Risco", uniqueConstraints = {@UniqueConstraint(columnNames = {"codigo", "id_empresa","id_filial"})})
public class Risco extends PersistentEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -4075649859105675170L;
	
	@Getter
	@Setter
	private String codigo;
	
	@Getter
	@Setter
	private String descricao;
	
	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name="Modelo_id")
	private Modelo modelo;
	
	@Getter
	@Setter
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name="Tabela_Grade_Risco",joinColumns= @JoinColumn(name="Risco_ID"),
	inverseJoinColumns= @JoinColumn(name="RiscoProd_Id"))
	private List<GradeProducao> grade = new ArrayList<>();
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal largura = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal comprimento = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal custoRisco = new BigDecimal("0");
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoEnfesto tipoEnfesto;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(codigo, comprimento, custoRisco, grade, largura, modelo);
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
		Risco other = (Risco) obj;
		return Objects.equals(codigo, other.codigo) && Objects.equals(comprimento, other.comprimento)
				&& Objects.equals(custoRisco, other.custoRisco) && Objects.equals(grade, other.grade)
				&& Objects.equals(largura, other.largura) && Objects.equals(modelo, other.modelo);
	}

	@Override
	public String toString() {
		return "Risco [codigo=" + codigo + ", modelo=" + modelo + ", grade=" + grade + ", largura=" + largura
				+ ", comprimento=" + comprimento + ", custoRisco=" + custoRisco + "]";
	}
	
	

}
