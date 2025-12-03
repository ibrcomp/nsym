package br.com.nsym.domain.model.entity.cadastro;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import br.com.nsym.domain.model.entity.PersistentEntityLogon;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="log")
public class ControleLogon extends PersistentEntityLogon{
	

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Column(name = "idReferencia", unique = true , nullable = true)
	private String idReferencia;
	
	@Getter
	@Setter
	private String nome;

	@Getter
	@Setter
	private boolean logado = false;
	
	@Getter
	@Setter
	private Date dataLogin;
	
	@Getter
	@Setter
	private Date expiraLogin;

	
}
