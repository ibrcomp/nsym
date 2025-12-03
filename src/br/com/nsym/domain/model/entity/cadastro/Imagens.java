package br.com.nsym.domain.model.entity.cadastro;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="imagens")
public class Imagens extends PersistentEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "cliente_ID")
	private Cliente cliente;
	
	@Getter
	@Setter
	@Lob
	@Column(name = "imagem", columnDefinition = "LONGBLOB")
	private byte[] imagem;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Produto_Id")
	private Produto produto;
	
}
