package br.com.nsym.domain.model.entity.financeiro.produto;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import br.com.nsym.domain.model.entity.PersistentEntityCustom;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
public abstract class Custo extends PersistentEntityCustom{

		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		@Getter
		@Setter
		@Column(precision = 19 , scale = 5)
		private BigDecimal custo;

		@Getter
		@Setter
		@Column(precision = 19 , scale = 5)
		private BigDecimal custoLiquido;
		
		@Getter
		@Setter
		@Column(precision = 19 , scale = 5)
		private BigDecimal preco1;
		
		@Getter
		@Setter
		@Column(precision = 19 , scale = 5)
		private BigDecimal preco2;
		
		@Getter
		@Setter
		@Column(precision = 19 , scale = 5)
		private BigDecimal preco3;
		
		@Getter
		@Setter
		private BigDecimal preco4;
		
		@Getter
		@Setter
		@Column(precision = 19 , scale = 5)
		private BigDecimal preco5;

}
