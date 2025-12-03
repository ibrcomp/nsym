package br.com.nsym.domain.model.entity.tools;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.venda.Pedido;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="controlePedido",uniqueConstraints = {@UniqueConstraint(columnNames={"controle","hoje","id_empresa","id_filial"})})
public class ControlePedido extends PersistentEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5229404560186172462L;
	
	@Getter
	@Setter
	private Long controle;
	
	@Getter
	@Setter
	private LocalDate hoje ;
	
	@Getter
	@Setter
	private LocalTime horario;
	
	@Getter
	@Setter
	private boolean disponivel = true;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="controle",fetch=FetchType.LAZY)
	private List<Pedido> listaPedidos = new ArrayList<Pedido>();

}
