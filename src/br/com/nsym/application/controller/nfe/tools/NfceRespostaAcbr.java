package br.com.nsym.application.controller.nfe.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;

/**
 * Parser tolerante para retornos do ACBrMonitor/ACBrLib em comandos de NFC-e.
 * A ideia é funcionar com diferentes formatos (texto, pipes, etc).
 */
@Getter
@Setter
public class NfceRespostaAcbr {

	private boolean valido;
	private String motivo = "";
	private String codigoRetorno = "";

	private String chaveAcesso = "";
	private String protocolo = "";
	private String caminhoXml = "";

	private Integer numero;
	private Integer serie;

	public static NfceRespostaAcbr parse(String retorno) {
		NfceRespostaAcbr r = new NfceRespostaAcbr();
		String raw = retorno == null ? "" : retorno.trim();
		String up = raw.toUpperCase();

		// Defaults
		r.setValido(false);

		// Tentativa 1: formato "OK|..."
		if (raw.contains("|")) {
			String[] parts = raw.split("\\|");
			String first = parts.length > 0 ? parts[0].trim().toUpperCase() : "";
			if (first.startsWith("OK")) {
				r.setValido(true);
			}
			// pega caminho xml, chave, protocolo, motivo se existirem
			for (String p : parts) {
				String s = p == null ? "" : p.trim();
				String sup = s.toUpperCase();
				if (r.getCaminhoXml().isEmpty() && (sup.endsWith(".XML") || sup.contains(".XML"))) {
					r.setCaminhoXml(s);
				}
				if (r.getChaveAcesso().isEmpty()) {
					Matcher mk = Pattern.compile("(\\d{44})").matcher(s);
					if (mk.find()) {
						r.setChaveAcesso(mk.group(1));
					}
				}
				if (r.getProtocolo().isEmpty()) {
					Matcher mp = Pattern.compile("(?:NPROT|PROTOCOLO|PROT)\\s*[:=]?\\s*(\\d{8,20})").matcher(sup);
					if (mp.find()) {
						r.setProtocolo(mp.group(1));
					}
				}
				if (!r.isValido() && (sup.contains("AUTORIZADO") || sup.contains("100") && sup.contains("USO DA NF"))) {
					r.setValido(true);
				}
			}
		}

		// Tentativa 2: texto livre (ACBr costuma retornar "Autorizado o uso da NF-e" etc)
		if (!r.isValido()) {
			boolean autorizado = up.contains("AUTORIZADO") || (up.contains("100") && up.contains("USO DA NF"));
			boolean ok = up.startsWith("OK") || up.contains("OK:");
			boolean rejeicao = up.contains("REJEI") || up.contains("ERRO") || up.contains("FALHA");

			if ((autorizado || ok) && !rejeicao) {
				r.setValido(true);
			}
		}

		// chave
		if (r.getChaveAcesso().isEmpty()) {
			Matcher mk = Pattern.compile("(\\d{44})").matcher(raw);
			if (mk.find()) {
				r.setChaveAcesso(mk.group(1));
			}
		}

		// caminho XML
		if (r.getCaminhoXml().isEmpty()) {
			Matcher mx = Pattern.compile("([A-Za-z]:\\\\[^\n\r]*?\\.xml)", Pattern.CASE_INSENSITIVE).matcher(raw);
			if (mx.find()) {
				r.setCaminhoXml(mx.group(1));
			}
		}

		// protocolo (heurística)
		if (r.getProtocolo().isEmpty()) {
			Matcher mp = Pattern.compile("(?:nProt|NPROT|PROTOCOLO|PROT)\\s*[:=]?\\s*(\\d{8,20})").matcher(raw);
			if (mp.find()) {
				r.setProtocolo(mp.group(1));
			}
		}

		// motivo
		String motivo = extrairMotivo(raw);
		r.setMotivo(motivo);

		// código retorno (quando existir algo como "cStat=100" ou "cStat: 100")
		Matcher mc = Pattern.compile("(?:cStat|CSTAT)\\s*[:=]\\s*(\\d{1,4})").matcher(raw);
		if (mc.find()) {
			r.setCodigoRetorno(mc.group(1));
		} else {
			// se não achou, tenta pegar um número típico 100/204/302 etc na presença de 'cstat'
			Matcher m2 = Pattern.compile("\\b(100|101|102|103|104|105|106|107|108|109|110|204|302|303|304|305|306|539|563|999)\\b").matcher(up);
			if (m2.find()) {
				r.setCodigoRetorno(m2.group(1));
			}
		}

		return r;
	}

	private static String extrairMotivo(String raw) {
		if (raw == null) return "";
		String up = raw.toUpperCase();

		// tenta achar XMotivo= ou xMotivo:
		Matcher mm = Pattern.compile("(?:xMotivo|XMOTIVO)\\s*[:=]\\s*([^\n\r|]+)").matcher(raw);
		if (mm.find()) {
			return mm.group(1).trim();
		}

		// fallback: pega primeira linha relevante
		String[] linhas = raw.split("\\r?\\n");
		for (String l : linhas) {
			String s = l == null ? "" : l.trim();
			String sup = s.toUpperCase();
			if (sup.contains("REJEI") || sup.contains("AUTORIZADO") || sup.contains("DENEG") || sup.contains("ERRO")) {
				return s;
			}
		}
		return raw.length() > 250 ? raw.substring(0, 250) : raw;
	}
}
