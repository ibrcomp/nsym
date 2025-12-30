package br.com.nsym.application.controller.nfe.tools;

import br.com.nsym.domain.model.entity.fiscal.Cfe.Nfce;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NfceEmissaoResultado {

	private boolean valido;
	private String motivo;
	private String codigoRetorno;

	private String chaveAcesso;
	private String protocolo;
	private String caminhoXml;

	private Integer numero;
	private Integer serie;

	private Nfce nfce;

	public static NfceEmissaoResultado ok(Nfce nfce, NfceRespostaAcbr resp) {
		NfceEmissaoResultado r = new NfceEmissaoResultado();
		r.setValido(true);
		r.setNfce(nfce);
		r.setMotivo(resp.getMotivo());
		r.setCodigoRetorno(resp.getCodigoRetorno());
		r.setChaveAcesso(resp.getChaveAcesso());
		r.setProtocolo(resp.getProtocolo());
		r.setCaminhoXml(resp.getCaminhoXml());
		r.setNumero(resp.getNumero());
		r.setSerie(resp.getSerie());
		return r;
	}

	public static NfceEmissaoResultado erro(Nfce nfce, NfceRespostaAcbr resp) {
		NfceEmissaoResultado r = new NfceEmissaoResultado();
		r.setValido(false);
		r.setNfce(nfce);
		r.setMotivo(resp.getMotivo());
		r.setCodigoRetorno(resp.getCodigoRetorno());
		r.setChaveAcesso(resp.getChaveAcesso());
		r.setProtocolo(resp.getProtocolo());
		r.setCaminhoXml(resp.getCaminhoXml());
		r.setNumero(resp.getNumero());
		r.setSerie(resp.getSerie());
		return r;
	}
}
