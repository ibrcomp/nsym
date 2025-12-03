package br.com.nsym.domain.model.entity.tools;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="mensagemsistema",uniqueConstraints = {@UniqueConstraint(columnNames={"codigo","id_empresa","id_filial"})})
public class MensagensSistema extends PersistentEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long codigo;
	
	@Getter
	@Setter
	private String mensagem;
	
	
	public String getMensagem101(String valor , String aliq){
		return "Permite o aproveitamento do crédito de ICMS "
				+ "no valor de R$ "+ valor + " , correspondente à aliquota "
				+ "de "	+ aliq +" %, nos termos do ART. 23 da LC 123/2006.";
	}
	
	public String getSimplesNacional(){
		return "Documento emitido por ME ou EPP optante "
				+ "pelo simples nacional.";
	}
	
	public String getMensagem102(){
		return "Não gera direito a crédito fiscal "
				+ "de ICMS, de ISS e de IPI.";
	}
	public String getTotalTributos(String valor){
		return "Tributos aproximado de R$ "+valor+ " Fonte:IBPT"; 
	}
	
	public String getReducaoBaseICMS() {
		return  "Base de cálculo do ICMS reduzida em 33,33% conforme decreto "
				+ "45.490/00 e decreto 62.560/2017 ";
	}
	
	public String getCst50() {
		return  "ICMS suspenso ART.402 do RICMS decreto 45.490/2000."
				+ "IPI suspenso nos termos do ART.42, inciso VI dp RIPI decreto 45.44/2002";
	}
}
