package br.com.nsym.domain.model.entity.fiscal.nfe;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.tools.ManifestacaoDestinatario;
import br.com.nsym.domain.model.entity.tools.SitNfe;
import br.com.nsym.domain.model.entity.tools.Uf;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="nfeNaoConfirmada",uniqueConstraints = {@UniqueConstraint(columnNames={"chNfe","id_empresa","id_filial"})})
public class NfeNaoConfirmada extends PersistentEntity	 {

	/**
	 *
	 */
	private static final long serialVersionUID = -3943831991950616509L;

	@Getter
	@Setter
	private String cnpjCpf;
	
	@Getter
	@Setter
	private String numero;
	
	@Getter
	@Setter
	private String xNome;
	@Getter
	@Setter
	private String ie;
	@Getter
	@Setter
	private String chNfe;
	@Getter
	@Setter
	private String dhEmissao;
	@Getter
	@Setter
	private String valorNf;
	@Getter
	@Setter
	private String nProtocolo;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private SitNfe situacao;
	
	@Getter
	@Setter
	private String nsu; 
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private Uf ufOrigem;
	
	@Getter
	@Setter
	private boolean confirmada;
	
	@Getter
	@Setter
	private boolean estoqueAtualizado;
	
	@Getter
	@Setter
	private LocalDateTime dataRecebimento = LocalDateTime.now();
	
	@Getter
	@Setter
	private boolean financeiroCriado = false;
	
	@Getter
	@Setter
	@OneToOne
	private NfeRecebida nfeRecebida;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private ManifestacaoDestinatario manifesto;

	@Override
	public String toString() {
		return "NfeNaoConfirmada [cnpjCpf=" + cnpjCpf + ", numero=" + numero + ", xNome=" + xNome + ", ie=" + ie
				+ ", chNfe=" + chNfe + ", dhEmissao=" + dhEmissao + ", valorNf=" + valorNf + ", nProtocolo="
				+ nProtocolo + ", situacao=" + situacao + ", nsu=" + nsu + ", ufOrigem=" + ufOrigem + ", confirmada="
				+ confirmada + ", estoqueAtualizado=" + estoqueAtualizado + ", dataRecebimento=" + dataRecebimento
				+ ", financeiroCriado=" + financeiroCriado + ", nfeRecebida=" + nfeRecebida + ", manifesto=" + manifesto
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(chNfe, cnpjCpf, confirmada, dataRecebimento, dhEmissao,
				estoqueAtualizado, financeiroCriado, ie, manifesto, nProtocolo, nfeRecebida, nsu, numero, situacao,
				ufOrigem, valorNf, xNome);
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
		NfeNaoConfirmada other = (NfeNaoConfirmada) obj;
		return Objects.equals(chNfe, other.chNfe) && Objects.equals(cnpjCpf, other.cnpjCpf)
				&& confirmada == other.confirmada && Objects.equals(dataRecebimento, other.dataRecebimento)
				&& Objects.equals(dhEmissao, other.dhEmissao) && estoqueAtualizado == other.estoqueAtualizado
				&& financeiroCriado == other.financeiroCriado && Objects.equals(ie, other.ie)
				&& manifesto == other.manifesto && Objects.equals(nProtocolo, other.nProtocolo)
				&& Objects.equals(nfeRecebida, other.nfeRecebida) && Objects.equals(nsu, other.nsu)
				&& Objects.equals(numero, other.numero) && situacao == other.situacao && ufOrigem == other.ufOrigem
				&& Objects.equals(valorNf, other.valorNf) && Objects.equals(xNome, other.xNome);
	}

	
	// criar modulo para pedidos de compra e atrelar 
//		private PedidoCompra pedidoCompra;

}
