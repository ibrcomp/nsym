package br.com.nsym.domain.model.entity.cadastro;

import lombok.Getter;
import lombok.Setter;

public class UsuarioControle {
	
	@Getter
	@Setter
	private String idUsuario;
	
	@Getter
	@Setter
	private boolean conectado = false;
	
	@Getter
	@Setter
	private String apelido;
	

}
