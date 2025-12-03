package br.com.nsym.domain.model.entity.financeiro;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.financeiro.tools.RecebimentoParcialAbstract;

@Entity
@Table(name="titulo",uniqueConstraints = {@UniqueConstraint(columnNames={"id","id_empresa","id_filial"})})
public class Titulo extends RecebimentoParcialAbstract{

	/**
	 *
	 */
	private static final long serialVersionUID = 451521455917458558L;
	
//	private LocalDate dataPagamento;
//	
//	private LocalDate lancamento;
//	
//	private LocalDate vencimento;
}
