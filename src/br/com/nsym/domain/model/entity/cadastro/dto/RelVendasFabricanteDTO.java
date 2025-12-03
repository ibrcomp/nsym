package br.com.nsym.domain.model.entity.cadastro.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

public class RelVendasFabricanteDTO {
	
	public RelVendasFabricanteDTO(String fabricante,String departamento,BigDecimal quantidade, 
			BigDecimal totalPecas,BigDecimal part,BigDecimal valorMedio,BigDecimal totalValor,BigDecimal totalVendido,
			BigDecimal partVenda,String matriz,String filial,BigInteger idFab, BigInteger idDep) {
		super();
		this.fabricante = fabricante;
		this.departamento = departamento;
		this.quantidade = quantidade;
		this.totalPecas = totalPecas;
		this.part = part;
		this.valorMedio = valorMedio;
		this.totalValor = totalValor;
		this.totalVendido = totalVendido;
		this.partVenda= partVenda;
		this.matriz = matriz;
		this.filial = filial;
		this.idFab = idFab;
		this.idDep = idDep;
	}

	@Getter
	@Setter
	private String fabricante;
	
	@Getter
	@Setter
	private String departamento;
	
	@Getter
	@Setter
	private BigDecimal quantidade = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal totalPecas = new BigDecimal("0");

	@Getter
	@Setter
	private BigDecimal part = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal valorMedio = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal totalValor = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal totalVendido = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal partVenda = new BigDecimal("0");
	
	@Getter
	@Setter
	private String matriz;
	
	@Getter
	@Setter
	private String filial;
	
	@Getter
	@Setter
	private BigInteger idFab = new BigInteger("0");
	
	@Getter
	@Setter
	private BigInteger idDep =new BigInteger("0");

	@Override
	public int hashCode() {
		return Objects.hash(departamento, fabricante, filial, matriz, part, partVenda, quantidade, totalPecas,
				totalValor, totalVendido, valorMedio);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RelVendasFabricanteDTO other = (RelVendasFabricanteDTO) obj;
		return Objects.equals(departamento, other.departamento) && Objects.equals(fabricante, other.fabricante)
				&& Objects.equals(filial, other.filial) && Objects.equals(matriz, other.matriz)
				&& Objects.equals(part, other.part) && Objects.equals(partVenda, other.partVenda)
				&& Objects.equals(quantidade, other.quantidade) && Objects.equals(totalPecas, other.totalPecas)
				&& Objects.equals(totalValor, other.totalValor) && Objects.equals(totalVendido, other.totalVendido)
				&& Objects.equals(valorMedio, other.valorMedio);
	}

	@Override
	public String toString() {
		return "RelVendasFabricanteDTO [fabricante=" + fabricante + ", departamento=" + departamento + ", quantidade="
				+ quantidade + ", totalPecas=" + totalPecas + ", part=" + part + ", valorMedio=" + valorMedio
				+ ", totalValor=" + totalValor + ", totalVendido=" + totalVendido + ", partVenda=" + partVenda
				+ ", matriz=" + matriz + ", filial=" + filial + "]";
	}
	

	
	
	
}
