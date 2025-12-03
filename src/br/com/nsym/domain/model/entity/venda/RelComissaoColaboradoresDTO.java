package br.com.nsym.domain.model.entity.venda;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

public class RelComissaoColaboradoresDTO {
	
	
	public RelComissaoColaboradoresDTO(String colaborador, BigDecimal total, String matriz, String filial,BigInteger id) {
		super();
		this.colaborador = colaborador;
		this.total = total;
		this.matriz = matriz;
		this.filial = filial;
		this.id = id;
	}
	@Getter
	@Setter
	private String colaborador;
	@Getter
	@Setter
	private BigDecimal total;
	@Getter
	@Setter
	private String matriz;
	@Getter
	@Setter
	private String filial;
	
	@Getter
	@Setter
	private BigInteger id;
	
	@Override
	public String toString() {
		return "RelComissaoColaboradoresDTO [colaborador=" + colaborador + ", total=" + total + ", matriz=" + matriz
				+ ", filial=" + filial + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(colaborador, filial, matriz, total);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RelComissaoColaboradoresDTO other = (RelComissaoColaboradoresDTO) obj;
		return Objects.equals(colaborador, other.colaborador) && Objects.equals(filial, other.filial)
				&& Objects.equals(matriz, other.matriz) && Objects.equals(total, other.total);
	}
	
	

}
