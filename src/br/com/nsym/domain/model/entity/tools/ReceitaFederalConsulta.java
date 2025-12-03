package br.com.nsym.domain.model.entity.tools;

import lombok.Getter;
import lombok.Setter;
/*
 * Classe que representa os dados recebidos do site da receita apos a consulta do CNPJ
 * 
 * @version 2.0.0
 * @since 1.1.0, 06/06/2017
 */
public class ReceitaFederalConsulta {

	@Getter
	@Setter
	String  receitaCNPJ ;
	
	@Getter
	@Setter
	String receitaIE;
	
	@Getter
	@Setter
	String  receitaRazao ;
	
	@Getter
	@Setter
	String  receitaFantasia ;
	
	@Getter
	@Setter
	String  receitaLogradouro;
	
	@Getter
	@Setter
	String  receitaNumero;
	
	@Getter
	@Setter
	String  receitaComplemento;
	
	@Getter
	@Setter
	String  receitaCep;
	
	@Getter
	@Setter
	String  receitaBairro ;
	
	@Getter
	@Setter
	String  receitaMunicipio ;
	
	@Getter
	@Setter
	String  receitaUF;
	
	@Getter
	@Setter
	String  receitaSituacaoCadastral ;
	
	@Getter
	@Setter
	String  receitaCnaePrincipal ;
	
	@Getter
	@Setter
	String  receitaCnaeSecundario ;
	
	@Getter
	@Setter
	String  receitaDataAbertura ;
	
	@Getter
	@Setter
	String receitaRegime;

	@Override
	public String toString() {
		return String.format(
				"ReceitaFederalConsulta [receitaCNPJ=%s, receitaIE=%s, receitaRazao=%s, receitaFantasia=%s, receitaLogradouro=%s, receitaNumero=%s, receitaComplemento=%s, receitaCep=%s, receitaBairro=%s, receitaMunicipio=%s, receitaUF=%s, receitaSituacaoCadastral=%s, receitaCnaePrincipal=%s, receitaCnaeSecundario=%s, receitaDataAbertura=%s, receitaRegime=%s]",
				receitaCNPJ, receitaIE, receitaRazao, receitaFantasia, receitaLogradouro, receitaNumero,
				receitaComplemento, receitaCep, receitaBairro, receitaMunicipio, receitaUF, receitaSituacaoCadastral,
				receitaCnaePrincipal, receitaCnaeSecundario, receitaDataAbertura, receitaRegime);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((receitaBairro == null) ? 0 : receitaBairro.hashCode());
		result = prime * result + ((receitaCNPJ == null) ? 0 : receitaCNPJ.hashCode());
		result = prime * result + ((receitaCep == null) ? 0 : receitaCep.hashCode());
		result = prime * result + ((receitaCnaePrincipal == null) ? 0 : receitaCnaePrincipal.hashCode());
		result = prime * result + ((receitaCnaeSecundario == null) ? 0 : receitaCnaeSecundario.hashCode());
		result = prime * result + ((receitaComplemento == null) ? 0 : receitaComplemento.hashCode());
		result = prime * result + ((receitaDataAbertura == null) ? 0 : receitaDataAbertura.hashCode());
		result = prime * result + ((receitaFantasia == null) ? 0 : receitaFantasia.hashCode());
		result = prime * result + ((receitaIE == null) ? 0 : receitaIE.hashCode());
		result = prime * result + ((receitaLogradouro == null) ? 0 : receitaLogradouro.hashCode());
		result = prime * result + ((receitaMunicipio == null) ? 0 : receitaMunicipio.hashCode());
		result = prime * result + ((receitaNumero == null) ? 0 : receitaNumero.hashCode());
		result = prime * result + ((receitaRazao == null) ? 0 : receitaRazao.hashCode());
		result = prime * result + ((receitaRegime == null) ? 0 : receitaRegime.hashCode());
		result = prime * result + ((receitaSituacaoCadastral == null) ? 0 : receitaSituacaoCadastral.hashCode());
		result = prime * result + ((receitaUF == null) ? 0 : receitaUF.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ReceitaFederalConsulta other = (ReceitaFederalConsulta) obj;
		if (receitaBairro == null) {
			if (other.receitaBairro != null) {
				return false;
			}
		} else if (!receitaBairro.equals(other.receitaBairro)) {
			return false;
		}
		if (receitaCNPJ == null) {
			if (other.receitaCNPJ != null) {
				return false;
			}
		} else if (!receitaCNPJ.equals(other.receitaCNPJ)) {
			return false;
		}
		if (receitaCep == null) {
			if (other.receitaCep != null) {
				return false;
			}
		} else if (!receitaCep.equals(other.receitaCep)) {
			return false;
		}
		if (receitaCnaePrincipal == null) {
			if (other.receitaCnaePrincipal != null) {
				return false;
			}
		} else if (!receitaCnaePrincipal.equals(other.receitaCnaePrincipal)) {
			return false;
		}
		if (receitaCnaeSecundario == null) {
			if (other.receitaCnaeSecundario != null) {
				return false;
			}
		} else if (!receitaCnaeSecundario.equals(other.receitaCnaeSecundario)) {
			return false;
		}
		if (receitaComplemento == null) {
			if (other.receitaComplemento != null) {
				return false;
			}
		} else if (!receitaComplemento.equals(other.receitaComplemento)) {
			return false;
		}
		if (receitaDataAbertura == null) {
			if (other.receitaDataAbertura != null) {
				return false;
			}
		} else if (!receitaDataAbertura.equals(other.receitaDataAbertura)) {
			return false;
		}
		if (receitaFantasia == null) {
			if (other.receitaFantasia != null) {
				return false;
			}
		} else if (!receitaFantasia.equals(other.receitaFantasia)) {
			return false;
		}
		if (receitaIE == null) {
			if (other.receitaIE != null) {
				return false;
			}
		} else if (!receitaIE.equals(other.receitaIE)) {
			return false;
		}
		if (receitaLogradouro == null) {
			if (other.receitaLogradouro != null) {
				return false;
			}
		} else if (!receitaLogradouro.equals(other.receitaLogradouro)) {
			return false;
		}
		if (receitaMunicipio == null) {
			if (other.receitaMunicipio != null) {
				return false;
			}
		} else if (!receitaMunicipio.equals(other.receitaMunicipio)) {
			return false;
		}
		if (receitaNumero == null) {
			if (other.receitaNumero != null) {
				return false;
			}
		} else if (!receitaNumero.equals(other.receitaNumero)) {
			return false;
		}
		if (receitaRazao == null) {
			if (other.receitaRazao != null) {
				return false;
			}
		} else if (!receitaRazao.equals(other.receitaRazao)) {
			return false;
		}
		if (receitaRegime == null) {
			if (other.receitaRegime != null) {
				return false;
			}
		} else if (!receitaRegime.equals(other.receitaRegime)) {
			return false;
		}
		if (receitaSituacaoCadastral == null) {
			if (other.receitaSituacaoCadastral != null) {
				return false;
			}
		} else if (!receitaSituacaoCadastral.equals(other.receitaSituacaoCadastral)) {
			return false;
		}
		if (receitaUF == null) {
			if (other.receitaUF != null) {
				return false;
			}
		} else if (!receitaUF.equals(other.receitaUF)) {
			return false;
		}
		return true;
	}


}
