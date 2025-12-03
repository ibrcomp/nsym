package br.com.nsym.domain.model.entity.venda;

import java.math.BigDecimal;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

public class CupomVendaDTO {
	
	
	public CupomVendaDTO(int item, String ref, String desc, String tamanho, String cor, BigDecimal quantidade,
			String unidade, BigDecimal vlUnitario, BigDecimal valorTotal) {
		super();
		this.item = item;
		this.ref = ref;
		this.desc = desc;
		this.tamanho = tamanho;
		this.cor = cor;
		this.quantidade = quantidade;
		this.unidade = unidade;
		this.vlUnitario = vlUnitario;
		this.valorTotal = valorTotal;
	}
	
	

	public CupomVendaDTO() {
		super();
		//TODO Auto-generated constructor stub
	}



	@Getter
	@Setter
	private int item;
	
	@Getter
	@Setter
	private String ref;
	
	@Getter
	@Setter
	private String desc;
	
	@Getter
	@Setter
	private String tamanho;
	
	@Getter
	@Setter
	private String cor;
	
	@Getter
	@Setter
	private BigDecimal quantidade;
	
	@Getter
	@Setter
	private String unidade;
	
	@Getter
	@Setter
	private BigDecimal vlUnitario;
	
	@Getter
	@Setter
	private BigDecimal valorTotal;

	@Override
	public int hashCode() {
		return Objects.hash(cor, desc, item, quantidade, ref, tamanho, unidade, valorTotal, vlUnitario);
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CupomVendaDTO other = (CupomVendaDTO) obj;
		return Objects.equals(cor, other.cor) && Objects.equals(desc, other.desc) && item == other.item
				&& Objects.equals(quantidade, other.quantidade) && Objects.equals(ref, other.ref)
				&& Objects.equals(tamanho, other.tamanho) && Objects.equals(unidade, other.unidade)
				&& Objects.equals(valorTotal, other.valorTotal) && Objects.equals(vlUnitario, other.vlUnitario);
	}

}
