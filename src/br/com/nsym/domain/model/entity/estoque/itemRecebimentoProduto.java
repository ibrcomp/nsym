package br.com.nsym.domain.model.entity.estoque;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.fiscal.Item;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="ItemRecebimentoProduto",uniqueConstraints = {@UniqueConstraint(columnNames={"id","id_empresa","id_filial"})})
public class itemRecebimentoProduto extends Item{

	/**
	 *
	 */
	private static final long serialVersionUID = -3492231853486303049L;
	
	@Getter
	@Setter
	@ManyToOne
	private EntradaEstoque entrada;

}
