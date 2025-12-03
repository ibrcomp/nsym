package br.com.nsym.domain.model.entity.venda;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

public class RelatorioEncomendaPedidos  {
	
	
	public RelatorioEncomendaPedidos() {
		super();
		//TODO Auto-generated constructor stub
	}

	public RelatorioEncomendaPedidos(Long id, String controle, String nome, String previsto, String emissao,
			BigDecimal total) {
		super();
		this.id = id;
		this.controle = controle;
		this.nome = nome;
		this.previsto = previsto;
		this.emissao = emissao;
		this.total = total;
		
	}

	@Getter
	@Setter
	private Long id;
	
	@Getter
	@Setter
	private String controle;
	
	@Getter
	@Setter
	private String nome;
	
	@Getter
	@Setter
	private String previsto;
	
	@Getter
	@Setter
	private String emissao;
	
	@Getter
	@Setter
	private BigDecimal total= new BigDecimal("0");

	@Override
	public int hashCode() {
		return Objects.hash(controle, emissao, id, nome, previsto, total);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RelatorioEncomendaPedidos other = (RelatorioEncomendaPedidos) obj;
		return Objects.equals(controle, other.controle) && Objects.equals(emissao, other.emissao)
				&& Objects.equals(id, other.id) && Objects.equals(nome, other.nome)
				&& Objects.equals(previsto, other.previsto) && Objects.equals(total, other.total);
	}

	@Override
	public String toString() {
		return "RelatorioEncomendaPedidos [id=" + id + ", controle=" + controle + ", nome=" + nome + ", previsto="
				+ previsto + ", emissao=" + emissao + ", total=" + total + "]";
	}
	




	
}
