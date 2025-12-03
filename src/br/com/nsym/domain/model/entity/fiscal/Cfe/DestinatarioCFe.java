package br.com.nsym.domain.model.entity.fiscal.Cfe;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
public class DestinatarioCFe extends PersistentEntity{
	
	/**
	 *
	 */
	private static final long serialVersionUID = -6138793970690115328L;
	@Getter
	@Setter
	private String cnpj;
	@Getter
	@Setter
	private String cpf;
	@Getter
	@Setter
	private String nome;
	
	@Getter
	@Setter
	@OneToOne(mappedBy="destinatario",cascade = CascadeType.ALL)
	private CFe cfe;
	

}
