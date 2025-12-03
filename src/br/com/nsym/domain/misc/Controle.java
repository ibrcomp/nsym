package br.com.nsym.domain.misc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.HibernateException;

import br.com.nsym.domain.model.entity.tools.ControlePedido;
import br.com.nsym.domain.model.repository.venda.ControlePedidoRepository;
import lombok.Getter;
import lombok.Setter;
/**
 * Classe criada para criar uma sequencia de controle que se reinicia diariamente para o Long 1
 * 
 * @author ibrahim yousef 
 *
 */
@RequestScoped
public class Controle {
	
		
	@Getter
	@Setter
	private ControlePedido control =  new ControlePedido();
	
	@Inject
	private ControlePedidoRepository controlDao;
	
	/**
	 * 	Retorna o numero de controle disponivel no dia
	 * @param empresa
	 * @param filial
	 * @return
	 */
	public ControlePedido retornaNumeroControleDisponivel(Long empresa, Long filial) {
		List<ControlePedido> listaTemp = new ArrayList<>();
		listaTemp = this.controlDao.pegaListaControlePedido(empresa, filial);
		Long numero = 0L;
		ControlePedido controlTemp = new ControlePedido();
		if (listaTemp != null) {
			for (ControlePedido controlePedido : listaTemp) {
				if (controlePedido.isDisponivel()) {
					if (controlTemp.getControle() == null) {
						controlTemp = controlePedido;
					}else {
						if (controlTemp.getControle() > controlePedido.getControle()) {
							controlTemp = controlePedido;
						}
					}
				}else {
					if (controlePedido.getControle() > numero) {
						numero = controlePedido.getControle();
					}
				}
			}
			if (controlTemp.getControle() == null) {
				controlTemp.setControle(numero + 1L);
			}
		}else {
			controlTemp.setControle(1L);
		}
		return controlTemp; 
	}
			
//			if (this.control.getControle() == null) {
//				this.control = controlePedido;
//			}else if (this.control.getControle() > controlePedido.getControle()) {
//				this.control = controlePedido;
//			}
//		}else {
//			if (controlTemp.getControle() == null) {
//				controlTemp = controlePedido;
//			}else {
//				if (controlTemp.getControle()> controlePedido.getControle()) {
//					controlTemp.setControle(controlePedido.getControle());
//				}
//			}
//		}
//	}
//	if (this.control.getControle() == null && controlTemp.getControle() == null) {
//			this.control.setControle(1L);
//	}else { 
//		if (controlTemp.getControle() == null){
//			if (this.control.isDisponivel() == false) {
//				this.control.setControle(this.control.getControle()+1L);
//			}
//		}else {
//			this.control = controlTemp;
//		}
//	}		
	
	@Transactional
	public ControlePedido saveControle(ControlePedido controle) throws HibernateException,Exception {
		controle.setDisponivel(false);
		controle.setDeleted(false);
		controle.setHorario(LocalTime.now());
		controle.setHoje(LocalDate.now());
		return this.controlDao.save(controle);
	}
	
	@Transactional
	public ControlePedido liberaControle(ControlePedido controle) {
		controle.setDisponivel(true);
		controle.setDeleted(false);
		return this.controlDao.save(controle);
	}
	
	@Transactional
	public void excluiControle(ControlePedido controle) {
		
		this.controlDao.delete(controle);
	}

}
