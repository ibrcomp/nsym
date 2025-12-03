package br.com.nsym.domain.model.entity.tools;

import java.util.Objects;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.Filial;
import br.com.nsym.domain.model.entity.cadastro.Fornecedor;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
public abstract class AbstractEmitente extends PersistentEntity {

	
	/**
	 *
	 */
	private static final long serialVersionUID = 8900892590284908275L;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(empresa, filial, fornecedor);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractEmitente other = (AbstractEmitente) obj;
		return Objects.equals(empresa, other.empresa) && Objects.equals(filial, other.filial)
				&& Objects.equals(fornecedor, other.fornecedor);
	}
	
	
	public String retornaRazao() {
		String resposta ="Não identificado";
		if(this.filial != null) {
			resposta =  this.filial.getRazaoSocial();
		}
		if (this.fornecedor != null) {
			resposta =  this.fornecedor.getRazaoSocial();
		}
		if (this.empresa != null) {
			resposta =  this.empresa.getRazaoSocial();
		}
		return resposta;
	}
	
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
