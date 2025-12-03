package br.com.nsym.domain.model.entity.financeiro.tools;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.financeiro.ContaCorrente;
import lombok.Getter;
import lombok.Setter;

@Entity
public class WsBanco extends PersistentEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -3390116573247445060L;

	@Getter
	@Setter
	private String clienteID;
	
	@Getter
	@Setter
	private String clienteSecret;
	
	@Getter
	@Setter
	private String keyUser;
	
	@Getter
	@Setter
	private String scope;
	
	@Getter
	@Setter
	private String arquivoKey;
	
	@Getter
	@Setter
	private String arquivoCrt;
	
	@Getter
	@Setter
	private boolean indicadorPIX = false;
	
	@Getter
	@Setter
	private boolean ambiente = false;
	
	@Getter
	@Setter
	@OneToOne
	@JoinColumn(name = "conta_id")
	private ContaCorrente conta;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(ambiente, arquivoCrt, arquivoKey, clienteID, clienteSecret, conta,
				indicadorPIX, keyUser, scope);
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
		WsBanco other = (WsBanco) obj;
		return ambiente == other.ambiente && Objects.equals(arquivoCrt, other.arquivoCrt)
				&& Objects.equals(arquivoKey, other.arquivoKey) && Objects.equals(clienteID, other.clienteID)
				&& Objects.equals(clienteSecret, other.clienteSecret) && Objects.equals(conta, other.conta)
				&& indicadorPIX == other.indicadorPIX && Objects.equals(keyUser, other.keyUser)
				&& Objects.equals(scope, other.scope);
	}

	@Override
	public String toString() {
		return "WsBanco [clienteID=" + clienteID + ", clienteSecret=" + clienteSecret + ", keyUser=" + keyUser
				+ ", scope=" + scope + ", arquivoKey=" + arquivoKey + ", arquivoCrt=" + arquivoCrt + ", indicadorPIX="
				+ indicadorPIX + ", ambiente=" + ambiente + ", conta=" + conta + "]";
	}
	
	
}
