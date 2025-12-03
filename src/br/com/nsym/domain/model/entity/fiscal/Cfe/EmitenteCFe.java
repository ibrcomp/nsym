package br.com.nsym.domain.model.entity.fiscal.Cfe;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.Filial;
import br.com.nsym.domain.model.entity.cadastro.Fornecedor;
import br.com.nsym.domain.model.entity.tools.Uf;
import lombok.Getter;
import lombok.Setter;

@Entity
public class EmitenteCFe extends PersistentEntity{

	

	/**
	 *
	 */
	private static final long serialVersionUID = -8995185071800721687L;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Filial")
	private Filial filial;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Empresa")
	private Empresa empresa;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Fornecedor")
	private Fornecedor fornecedor;
	
	@Getter
	@Setter
	@OneToOne(mappedBy="emitente",cascade = CascadeType.ALL)
	private CFe cfe;
	
	public Object retornaObjeto() {
		Object resposta = new Object();
		if(this.filial != null) {
			resposta=   this.filial;
		}
		if (this.fornecedor != null) {
			resposta=  this.fornecedor;
		}
		if (this.empresa != null) {
			resposta= this.empresa;
		}
		return resposta;
	}
	
	public Uf retornaUf() {
		Uf resposta = null;
		if(this.filial != null) {
			resposta=   this.filial.getEstado();
		}
		if (this.fornecedor != null) {
			resposta=  this.fornecedor.getEstado();
		}
		if (this.empresa != null) {
			resposta= this.empresa.getEstado();
		}
		return resposta;
	}
	
	public String retornaCnae() {
		String resposta = null;
		if(this.filial != null) {
			resposta =  this.filial.getCnaePrincipal();
		}
		if (this.fornecedor != null) {
			resposta =  null;
		}
		if (this.empresa != null) {
			resposta =  this.empresa.getCnaePrincipal();
		}
		return resposta;
	}

}
