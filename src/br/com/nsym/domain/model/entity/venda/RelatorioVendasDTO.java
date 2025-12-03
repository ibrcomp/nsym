package br.com.nsym.domain.model.entity.venda;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

public class RelatorioVendasDTO  {
	
	
	public RelatorioVendasDTO() {
		super();
		//TODO Auto-generated constructor stub
	}

	public RelatorioVendasDTO(String ref, String descricao, BigDecimal quant, BigDecimal valor_Un, BigDecimal vl_Med_Un,
			BigDecimal total, String tamanho, String cor, BigDecimal totalPeriodo, BigDecimal totalPecas,
			String matriz, String filial, BigInteger barras) {
		super();
		this.ref = ref;
		this.descricao = descricao;
		this.quant = quant;
		this.valor_Un = valor_Un;
		this.vl_Med_Un = vl_Med_Un;
		this.total = total;
		this.tamanho = tamanho;
		this.cor = cor;
		this.totalPeriodo = totalPeriodo;
		this.totalPecas = totalPecas;
		this.matriz = matriz;
		this.filial = filial;
		this.barras= barras;
	}

	@Getter
	@Setter
	private String ref;
	
	@Getter
	@Setter
	private String descricao;
	
	@Getter
	@Setter
	private String tamanho;
	
	@Getter
	@Setter
	private String cor;
	
	@Getter
	@Setter
	private BigDecimal valor_Un = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal quant= new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal vl_Med_Un= new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal total= new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal totalPeriodo= new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal totalPecas= new BigDecimal("0");
	
	@Getter
	@Setter
	private String matriz;
	
	@Getter
	@Setter
	private String filial;
	
	@Getter
	@Setter
	private BigInteger barras;

	@Override
	public int hashCode() {
		return Objects.hash(barras, cor, descricao, filial, matriz, quant, ref, tamanho, total, totalPecas,
				totalPeriodo, valor_Un, vl_Med_Un);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RelatorioVendasDTO other = (RelatorioVendasDTO) obj;
		return Objects.equals(barras, other.barras) && Objects.equals(cor, other.cor)
				&& Objects.equals(descricao, other.descricao) && Objects.equals(filial, other.filial)
				&& Objects.equals(matriz, other.matriz) && Objects.equals(quant, other.quant)
				&& Objects.equals(ref, other.ref) && Objects.equals(tamanho, other.tamanho)
				&& Objects.equals(total, other.total) && Objects.equals(totalPecas, other.totalPecas)
				&& Objects.equals(totalPeriodo, other.totalPeriodo) && Objects.equals(valor_Un, other.valor_Un)
				&& Objects.equals(vl_Med_Un, other.vl_Med_Un);
	}

	@Override
	public String toString() {
		return "RelatorioVendasDTO [ref=" + ref + ", descricao=" + descricao + ", tamanho=" + tamanho + ", cor=" + cor
				+ ", valor_Un=" + valor_Un + ", quant=" + quant + ", vl_Med_Un=" + vl_Med_Un + ", total=" + total
				+ ", totalPeriodo=" + totalPeriodo + ", totalPecas=" + totalPecas + ", matriz=" + matriz + ", filial="
				+ filial + ", barras=" + barras + "]";
	}



	
}
