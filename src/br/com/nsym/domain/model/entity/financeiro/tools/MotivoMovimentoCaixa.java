package br.com.nsym.domain.model.entity.financeiro.tools;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoMovimento;
import lombok.Getter;
import lombok.Setter;
/**
 * Classe que armazena os motivos possíveis para movimentação do caixa
 * 
 * @author ibrcomp
 * 
 */
@Entity
@Table(name="MotivoCaixa",uniqueConstraints = {@UniqueConstraint(columnNames = {"id","id_empresa"})})
public class MotivoMovimentoCaixa extends PersistentEntity  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6872946978876811146L;
	
	@Getter
	@Setter
	private String motivo;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoMovimento tipoMovimento;

}
