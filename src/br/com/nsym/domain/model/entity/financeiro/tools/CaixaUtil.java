package br.com.nsym.domain.model.entity.financeiro.tools;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import br.com.ibrcomp.exception.CaixaException;
import br.com.nsym.application.component.Translator;
import br.com.nsym.application.producer.qualifier.AuthenticatedUser;
import br.com.nsym.domain.model.entity.financeiro.Caixa;
import br.com.nsym.domain.model.entity.financeiro.MovimentoCaixa;
import br.com.nsym.domain.model.entity.financeiro.RecebimentoParcial;
import br.com.nsym.domain.model.entity.financeiro.SaldoCaixa;
import br.com.nsym.domain.model.entity.financeiro.TipoPagamento;
import br.com.nsym.domain.model.entity.financeiro.TipoPagamentoSimples;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoMovimento;
import br.com.nsym.domain.model.entity.tools.CaixaFinalidade;
import br.com.nsym.domain.model.repository.financeiro.CaixaRepository;
import br.com.nsym.domain.model.security.User;
import lombok.Getter;

@RequestScoped
public class CaixaUtil {

	@Getter
	@Inject
	@AuthenticatedUser
	private User usuarioAutenticado;

	@Inject
	private CaixaRepository caixaDao;


	@Inject
	@Default
	private Translator translator;
	
	@Getter
	private MathContext mc = new MathContext(20,RoundingMode.HALF_EVEN);



	/**
	 * Traduz uma mensagem pelo bundle da aplicacao
	 * 
	 * @param message a chave da mensagem original
	 * @return o texto
	 */
	public String translate(String message) {
		return this.translator.translate(message);
	}
	/**
	 * 
	 * @param finalidade 
	 * ( rece = tras o saldo de caixa em modo lazy
	 * 	 fech = tras a lista de recebimento em modo lazy
	 * @return
	 * @throws CaixaException
	 */
	
	public Caixa retornaCaixa(CaixaFinalidade finalidade) throws CaixaException {
		Caixa caixaAbertoHoje = new Caixa();
		List<Caixa> listaCaixaTemp = new ArrayList<Caixa>();
		LocalDate maiorData = LocalDate.now().minusDays(10);
		listaCaixaTemp = this.caixaDao.pegaCaixasEmAberto(getUsuarioAutenticado().getName(),maiorData, StatusCaixa.Abe, getUsuarioAutenticado().getIdEmpresa()	, getUsuarioAutenticado().getIdFilial());
		if (listaCaixaTemp.size() == 0 || listaCaixaTemp == null || listaCaixaTemp.isEmpty()) {
			throw new CaixaException(this.translate("caixa.needOpen"));
		}else {
			for (Caixa caixa : listaCaixaTemp) {
				if (caixa.getDataAbertura().isAfter(maiorData)) {
					maiorData = caixa.getDataAbertura();
				}
			}
			System.out.println("data: " + maiorData);
			caixaAbertoHoje = this.caixaDao.pegaCaixaAbertoUsuario(getUsuarioAutenticado().getName(), maiorData,StatusCaixa.Abe, getUsuarioAutenticado().getIdEmpresa()	, getUsuarioAutenticado().getIdFilial(),null,finalidade);
		}
		return caixaAbertoHoje;
	}
	
	/**
	 * método que localiza o caixa 
	 * @return CAIXA
	 */
	public Caixa retornaAberturaCaixa(CaixaFinalidade finalidade) throws CaixaException {
		Caixa caixaAbertoHoje = new Caixa();
		List<Caixa> listaCaixaTemp = new ArrayList<Caixa>();
		LocalDate maiorData = LocalDate.now().minusDays(10);
		listaCaixaTemp = this.caixaDao.pegaCaixasEmAberto(getUsuarioAutenticado().getName(),maiorData ,StatusCaixa.Abe,getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
		if (listaCaixaTemp.size() == 0 || listaCaixaTemp == null || listaCaixaTemp.isEmpty()) {
			caixaAbertoHoje = null;
		}else {
			for (Caixa caixa : listaCaixaTemp) {
				if (caixa.getDataAbertura().isAfter(maiorData)) {
					maiorData = caixa.getDataAbertura();
				}
			}
			System.out.println("data: " + maiorData);
			caixaAbertoHoje = this.caixaDao.pegaCaixaAbertoUsuario(getUsuarioAutenticado().getName(), maiorData,StatusCaixa.Abe, getUsuarioAutenticado().getIdEmpresa()	, getUsuarioAutenticado().getIdFilial(),null,finalidade);
		}
		return caixaAbertoHoje;
	}

	/**
	 * Gera um HashMap com TipoDePagamentoSimples <String,BigDecimal>
	 * @return HasMap com os TipodePagamentosSimples e totais zerados
	 */
	public HashMap<String, BigDecimal> hashTipoSimplesLimpo(){
		HashMap<String, BigDecimal> hashTemp = new HashMap<String, BigDecimal>();
		for (TipoPagamentoSimples tipo : TipoPagamentoSimples.values()) {
			hashTemp.put(tipo.toString(), new BigDecimal(0));
		}
		return hashTemp;		
	}
	
	public HashMap<String, BigDecimal> hashTipoSimplesZerado(){
		HashMap<String, BigDecimal> hashTemp = new HashMap<String, BigDecimal>();
		for (TipoPagamentoSimples tipo : TipoPagamentoSimples.values()) {
			hashTemp.put(tipo.name(), new BigDecimal(0));
		}
		return hashTemp;		
	}

	/**
	 * Método que gera o saldo disponivel no caixa por TipodePagamentoSImples
	 * @return HashMap com os valores preenchidos
	 */
	public HashMap<String, BigDecimal> saldoDisponivel(Caixa caixa){
		HashMap<String, BigDecimal> listaPagamentos = hashTipoSimplesLimpo();

		for (RecebimentoParcial recebimento : caixa.getListaRecebimentoCaixa()) {
			if (!(recebimento.getLivroCaixa().compareTo(MovimentoEnum.Ret) == 0) && !(recebimento.getLivroCaixa().compareTo(MovimentoEnum.Fech) ==0)  ) {
				if (recebimento.getFormaPagamento() != null) {				
					if (listaPagamentos.containsKey(recebimento.getFormaPagamento().getTipoPagamento().toString())) {
						listaPagamentos.replace(recebimento.getFormaPagamento().getTipoPagamento().toString(), listaPagamentos.get(recebimento.getFormaPagamento().getTipoPagamento().toString()).setScale(3,RoundingMode.HALF_EVEN).add(recebimento.getValorRecebido(),mc).setScale(3,RoundingMode.HALF_EVEN).subtract(recebimento.getTroco().setScale(3,RoundingMode.HALF_EVEN)).setScale(3,RoundingMode.HALF_EVEN)).setScale(3,RoundingMode.HALF_EVEN);
					}else {
						listaPagamentos.put(recebimento.getFormaPagamento().getTipoPagamento().toString(), listaPagamentos.get(recebimento.getFormaPagamento().getTipoPagamento().toString()).setScale(3,RoundingMode.HALF_EVEN).add(recebimento.getValorRecebido(),mc).setScale(3,RoundingMode.HALF_EVEN).subtract(recebimento.getTroco().setScale(3,RoundingMode.HALF_EVEN)).setScale(3,RoundingMode.HALF_EVEN)).setScale(3,RoundingMode.HALF_EVEN);
					}
				}else {
					if (listaPagamentos.containsKey(recebimento.getTipoPagamento().toString())) {
						listaPagamentos.replace(recebimento.getTipoPagamento().toString(), listaPagamentos.get(recebimento.getTipoPagamento().toString()).setScale(3,RoundingMode.HALF_EVEN).add(recebimento.getValorRecebido().setScale(3,RoundingMode.HALF_EVEN)).setScale(3,RoundingMode.HALF_EVEN).subtract(recebimento.getTroco().setScale(3,RoundingMode.HALF_EVEN)).setScale(3,RoundingMode.HALF_EVEN));
					}else {
						if (recebimento.getFormaPagamento() == null) {
							listaPagamentos.put(recebimento.getTipoPagamento().toString(), listaPagamentos.get(recebimento.getTipoPagamento().toString()).setScale(3,RoundingMode.HALF_EVEN).add(recebimento.getValorRecebido().setScale(3,RoundingMode.HALF_EVEN)).setScale(3,RoundingMode.HALF_EVEN).subtract(recebimento.getTroco().setScale(3,RoundingMode.HALF_EVEN)).setScale(3,RoundingMode.HALF_EVEN));
						}
						listaPagamentos.put(recebimento.getTipoPagamento().toString(), listaPagamentos.get(recebimento.getTipoPagamento().toString()).setScale(3,RoundingMode.HALF_EVEN).add(recebimento.getValorRecebido().setScale(3,RoundingMode.HALF_EVEN)).setScale(3,RoundingMode.HALF_EVEN).subtract(recebimento.getTroco().setScale(3,RoundingMode.HALF_EVEN)).setScale(3,RoundingMode.HALF_EVEN));
					}
				}
			}else {
				if (!(recebimento.getLivroCaixa().compareTo(MovimentoEnum.Fech) ==0)){
					if (recebimento.getFormaPagamento() != null) {				
						if (listaPagamentos.containsKey(recebimento.getFormaPagamento().getTipoPagamento().toString())) {
							listaPagamentos.replace(recebimento.getFormaPagamento().getTipoPagamento().toString(), listaPagamentos.get(recebimento.getFormaPagamento().getTipoPagamento().toString()).setScale(3,RoundingMode.HALF_EVEN).subtract(recebimento.getValorRecebido().setScale(3,RoundingMode.HALF_EVEN)).setScale(3,RoundingMode.HALF_EVEN));
						}else {
							listaPagamentos.put(recebimento.getFormaPagamento().getTipoPagamento().toString(), listaPagamentos.get(recebimento.getFormaPagamento().getTipoPagamento().toString()).setScale(3,RoundingMode.HALF_EVEN).subtract(recebimento.getValorRecebido().setScale(3,RoundingMode.HALF_EVEN)).setScale(3,RoundingMode.HALF_EVEN));
						}
					}else {
						if (listaPagamentos.containsKey(recebimento.getTipoPagamento().toString())) {
							listaPagamentos.replace(recebimento.getTipoPagamento().toString(), listaPagamentos.get(recebimento.getTipoPagamento().toString()).setScale(3,RoundingMode.HALF_EVEN).subtract(recebimento.getValorRecebido().setScale(3,RoundingMode.HALF_EVEN)).setScale(3,RoundingMode.HALF_EVEN));
						}else {
							listaPagamentos.put(recebimento.getTipoPagamento().toString(), listaPagamentos.get(recebimento.getTipoPagamento().toString()).setScale(3,RoundingMode.HALF_EVEN).subtract(recebimento.getValorRecebido().setScale(3,RoundingMode.HALF_EVEN)).setScale(3,RoundingMode.HALF_EVEN));
						}
					}
				}
			}
		}
		return listaPagamentos;
	}

	public boolean temSaldo(TipoPagamento pagamento,BigDecimal valor,Caixa caixa) throws CaixaException {
		boolean resultado = false;
		HashMap<String, BigDecimal> saldoDisponivel = saldoDisponivel(caixa);
		BigDecimal valorDisponivel = new BigDecimal("0",mc);

		if (saldoDisponivel.containsKey(pagamento.toString())) {
			valorDisponivel = saldoDisponivel.get(pagamento.toString());
		}else {
			throw new CaixaException(this.translate("caixa.tipoPagamento.notFound") + " temSaldo");
		}
		System.out.println("valorDisponivel = " +valorDisponivel.compareTo(valor) );
		System.out.println("Valor Disponivel = " + valorDisponivel);
		System.out.println("Valor Movimento = " + valor);
		if (valorDisponivel.compareTo(valor) >=0) {
			System.out.println("Valor Disponivel = " + valorDisponivel);
			System.out.println("Valor Movimento = " + valor);
			resultado = true;
		}

		return resultado;
	}

	/**
	 * Método para criar um Recebimento Parcial com a movimentação
	 * @param movimento
	 * @param valor
	 * @param caixa
	 * @return RecebimentoParcial Preenchido
	 * @throws CaixaException
	 */
	public RecebimentoParcial movimentacaoEntradaSaidaDeValor(MovimentoCaixa movimento, BigDecimal valor,Caixa caixa) throws CaixaException {
		HashMap<String, BigDecimal> saldoDisponivel = saldoDisponivel(caixa);
		RecebimentoParcial resultado = new RecebimentoParcial();

		if (valor.compareTo(new BigDecimal("0"))>=0) {
			if (movimento.getMotivo().getTipoMovimento().equals(TipoMovimento.EN)) {
				if (saldoDisponivel.containsKey(movimento.getPagamento().getTipoPagamento().toString())) {
					resultado.setTipoPagamento(movimento.getPagamento().getTipoPagamento());
					resultado.setFormaPagamento(movimento.getPagamento());
					resultado.setValorRecebido(valor.setScale(3,RoundingMode.HALF_EVEN));
					resultado.setMovimento(movimento);
					resultado.setCaixa(caixa);
					resultado.setLivroCaixa(MovimentoEnum.Ent);
				}else {
					throw new CaixaException(this.translate("caixa.tipoPagamento.notFound"));
				}
			}else {
				if (temSaldo(movimento.getPagamento().getTipoPagamento(), valor, caixa)) {
					if (saldoDisponivel.containsKey(movimento.getPagamento().getTipoPagamento().toString())) {
						resultado.setTipoPagamento(movimento.getPagamento().getTipoPagamento());
						resultado.setFormaPagamento(movimento.getPagamento());
						resultado.setValorRecebido(valor.setScale(3,RoundingMode.HALF_EVEN));
						resultado.setMovimento(movimento);
						resultado.setCaixa(caixa);
						resultado.setLivroCaixa(MovimentoEnum.Ret);
					}else {
						throw new CaixaException(this.translate("caixa.tipoPagamento.notFound"));
					}
				}else {
					throw new CaixaException(this.translate("caixa.insufficient.funds") + " - CaixaUtil L=159");
				}
			}
		}else {
			throw new CaixaException(this.translate("caixa.entered.value.equal.to.zero"));
		}
		return resultado;
	}

	/**
	 * Conversor de TipoPagamentosSimples para TipoPagamento
	 * @param simples
	 * @return TipoPagamento
	 */
	public TipoPagamento converteTipoSimples(TipoPagamentoSimples simples) {
		return TipoPagamento.valueOf(simples.name());
	}
	/**
	 * conversor de TipoPagamento para TipoPagamentoSimples
	 * @param pagamento
	 * @return TipoPagamentoSimples
	 */
	public TipoPagamentoSimples converteTipoSimples(TipoPagamento pagamento) {
		return TipoPagamentoSimples.valueOf(pagamento.name());
	}

	/**
	 * Metodo que retorna os totais por TipoDePagamentoSimples
	 * @param lista - lista dos RecebimentosParciais
	 * @param hasTipoPagSimples - HasMap com os pagamentos a ser acrecidos pelos valores contidos em lista.
	 * @return HasMap com os totais já somados
	 */
	public HashMap<String, BigDecimal> geraTotaisPorTipoPagamentoSimples(List<RecebimentoParcial> lista, HashMap<String,BigDecimal> hasTipoPagSimples) {
		HashMap<String, BigDecimal> hasFormaTemp = new HashMap<String, BigDecimal>();
		for (RecebimentoParcial recebimentoParcial : lista) {
			if (hasFormaTemp.containsKey(recebimentoParcial.getTipoPagamento().toString())){
				BigDecimal total = new BigDecimal("0").setScale(2);
				total = hasTipoPagSimples.get(recebimentoParcial.getTipoPagamento().toString()).setScale(3,RoundingMode.HALF_EVEN).add(recebimentoParcial.getValorRecebido()).setScale(3,RoundingMode.HALF_EVEN);
				System.out.println("A soma do pagamento: " + recebimentoParcial.getFormaPagamento() + " é: R$ " + total);
				hasFormaTemp.replace(recebimentoParcial.getTipoPagamento().toString(), total.setScale(3,RoundingMode.HALF_EVEN));
			}else {
				hasFormaTemp.put(recebimentoParcial.getTipoPagamento().toString(), recebimentoParcial.getValorRecebido().setScale(2));
			}
		}
		return hasFormaTemp;
	}

	/**
	 * Método que retorna o caixa com o saldoCaixa preenchido
	 * @param caixa
	 * @param tipo (MovimentoEnum)
	 * @return caixa
	 */
	public Caixa preencheSaldoCaixa(Caixa caixa,MovimentoEnum tipo,List<RecebimentoParcial> listaRecebimento) throws CaixaException {
		switch (tipo) {
		case ABre: // abertura de caixa
			List<RecebimentoParcial> listTempRece = new ArrayList<RecebimentoParcial>();
			for (RecebimentoParcial rec : caixa.getListaRecebimentoCaixa()) {
				if (rec.getLivroCaixa().equals(MovimentoEnum.ABre)) {
					if (rec.getValorRecebido().compareTo(new BigDecimal("0"))>0) {
						listTempRece.add(rec);
					}
				}
			}
			if (!caixa.getSaldoCaixa().isEmpty() && !listTempRece.isEmpty()) {
				for (int i = 0; i == caixa.getSaldoCaixa().size();i++) {
					for (RecebimentoParcial recPar : listTempRece) {
						if (caixa.getSaldoCaixa().get(i).getForma().equals(converteTipoSimples(recPar.getTipoPagamento()))) {
							caixa.getSaldoCaixa().get(i).setValor(caixa.getSaldoCaixa().get(i).getValor().setScale(3,RoundingMode.HALF_EVEN).add(recPar.getValorRecebido()).setScale(3,RoundingMode.HALF_EVEN));
						}
					}
				}
			}else {
				if (!listTempRece.isEmpty()) {
					for (RecebimentoParcial recebimentoParcial : listTempRece) {
						SaldoCaixa saldo = new SaldoCaixa();
						saldo.setCaixa(caixa);
						saldo.setForma(converteTipoSimples(recebimentoParcial.getTipoPagamento()));
						saldo.setValor(recebimentoParcial.getValorRecebido().setScale(3,RoundingMode.HALF_EVEN));
						caixa.getSaldoCaixa().add(saldo);
					}
				}else {
					throw new CaixaException(this.translate("caixa.saldo.error") + " Livro Caixa = Abertura ");
				}
			}

			break;
		case Ent: // Entrada no caixa
			if (!listaRecebimento.isEmpty() && listaRecebimento.size() != 0) {
				HashMap<String, BigDecimal> listaPagamentos = hashTipoSimplesLimpo();
				// Preenchendo a listaPagamentos com o saldo do caixa.
				for (SaldoCaixa saldo : caixa.getSaldoCaixa()){ 
					listaPagamentos.replace(saldo.getForma().toString(), saldo.getValor().setScale(3,RoundingMode.HALF_EVEN));
				}
				// Preenchendo a listaPagamento com os valores da lista de Recebimento
				for (RecebimentoParcial recebimentoParcial : listaRecebimento) {
					BigDecimal total = new BigDecimal("0");
					if (recebimentoParcial.getFormaPagamento().getTipoPagamento().equals(TipoPagamento.Din)) {
						total = listaPagamentos.get(recebimentoParcial.getTipoPagamento().toString()).setScale(3,RoundingMode.HALF_EVEN).add(recebimentoParcial.getValorRecebido()).setScale(3,RoundingMode.HALF_EVEN).subtract(recebimentoParcial.getTroco().setScale(3,RoundingMode.HALF_EVEN));
						listaPagamentos.replace(recebimentoParcial.getTipoPagamento().toString(),total);
					}else {
						total = listaPagamentos.get(recebimentoParcial.getTipoPagamento().toString()).setScale(3,RoundingMode.HALF_EVEN).add(recebimentoParcial.getValorRecebido().setScale(3,RoundingMode.HALF_EVEN));
						listaPagamentos.replace(recebimentoParcial.getTipoPagamento().toString(),total.setScale(3,RoundingMode.HALF_EVEN));
					}
				}
				//Preechendo a lista de saldoDecaixa
				List<SaldoCaixa> listaSaldoTemp = new ArrayList<SaldoCaixa>();
				for (TipoPagamentoSimples tipoPagamentoSimples : TipoPagamentoSimples.values()) {
					if (listaPagamentos.get(tipoPagamentoSimples.toString()).compareTo(new BigDecimal("0"))>0) {
						SaldoCaixa saldo = new SaldoCaixa();
						saldo.setCaixa(caixa);
						saldo.setForma(tipoPagamentoSimples);
						saldo.setValor(listaPagamentos.get(tipoPagamentoSimples.toString()).setScale(3,RoundingMode.HALF_EVEN));
						listaSaldoTemp.add(saldo);
					}
				}
				List<SaldoCaixa> caixaSaldoTemp = caixa.getSaldoCaixa();
				for (SaldoCaixa saldoCaixa : listaSaldoTemp) {
					int i = -1;
					for (int b = 0; b< caixaSaldoTemp.size();b++) {
						if (saldoCaixa.getForma().equals(caixaSaldoTemp.get(b).getForma())) {
							i=b;							
						}
					}
					if (i == -1) { //caso nao tenha a forma na lista de saldo de caixa,  adiciona uma
						SaldoCaixa saldo = new SaldoCaixa();
						saldo.setCaixa(caixa);
						saldo.setForma(saldoCaixa.getForma());
						saldo.setValor(saldoCaixa.getValor().setScale(3,RoundingMode.HALF_EVEN));
						caixa.getSaldoCaixa().add(saldo);
					}else {
						caixa.getSaldoCaixa().get(i).setValor(saldoCaixa.getValor().setScale(3,RoundingMode.HALF_EVEN));
					}
				}
			}else {
				throw new CaixaException(this.translate("caixa.recebimento.isEmpty") + " - SaldoCaixa(Ent)");
			}
			break;
		case Fech: // Fechamdno de caixa
			break;
		case Reab: // Reabertura de caixa
			break;
		case Rec: // Recebimento no caixa
			if (!listaRecebimento.isEmpty() && listaRecebimento.size() != 0) {
				HashMap<String, BigDecimal> listaPagamentos = hashTipoSimplesLimpo();
				// Preenchendo a listaPagamentos com o saldo do caixa.
				for (SaldoCaixa saldo : caixa.getSaldoCaixa()){ 
					listaPagamentos.replace(saldo.getForma().toString(), saldo.getValor().setScale(3,RoundingMode.HALF_EVEN));
				}
				// Preenchendo a listaPagamento com os valores da lista de Recebimento
				for (RecebimentoParcial recebimentoParcial : listaRecebimento) {
					BigDecimal total = new BigDecimal("0",mc);
					if (recebimentoParcial.getFormaPagamento() == null) {
						if (recebimentoParcial.getTipoPagamento() != null) {
							if (recebimentoParcial.getTipoPagamento().equals(TipoPagamento.Din)) {
								total = listaPagamentos.get(recebimentoParcial.getTipoPagamento().toString()).add(recebimentoParcial.getValorRecebido().setScale(3,RoundingMode.HALF_EVEN)).subtract(recebimentoParcial.getTroco().setScale(3,RoundingMode.HALF_EVEN));
								listaPagamentos.replace(recebimentoParcial.getTipoPagamento().toString(),total.setScale(3,RoundingMode.HALF_EVEN));
							}else {
								total = listaPagamentos.get(recebimentoParcial.getTipoPagamento().toString()).setScale(3,RoundingMode.HALF_EVEN).add(recebimentoParcial.getValorRecebido().setScale(3,RoundingMode.HALF_EVEN));
								listaPagamentos.replace(recebimentoParcial.getTipoPagamento().toString(),total.setScale(3,RoundingMode.HALF_EVEN));
							}
						}else {
							throw new CaixaException(this.translate("caixaException.tipoPag.notFound") + " CaixaUtil L395");
						}
					}else {
						if (recebimentoParcial.getFormaPagamento().getTipoPagamento().equals(TipoPagamento.Din)) {
							total = listaPagamentos.get(recebimentoParcial.getTipoPagamento().toString()).add(recebimentoParcial.getValorRecebido().setScale(3,RoundingMode.HALF_EVEN)).subtract(recebimentoParcial.getTroco().setScale(3,RoundingMode.HALF_EVEN));
							listaPagamentos.replace(recebimentoParcial.getTipoPagamento().toString(),total.setScale(3,RoundingMode.HALF_EVEN));
						}else {
							total = listaPagamentos.get(recebimentoParcial.getTipoPagamento().toString()).setScale(3,RoundingMode.HALF_EVEN).add(recebimentoParcial.getValorRecebido().setScale(3,RoundingMode.HALF_EVEN));
							listaPagamentos.replace(recebimentoParcial.getTipoPagamento().toString(),total.setScale(3,RoundingMode.HALF_EVEN));
						}
					}
				}
				//Preechendo a lista de saldoDecaixa
				List<SaldoCaixa> listaSaldoTemp = new ArrayList<SaldoCaixa>();
				for (TipoPagamentoSimples tipoPagamentoSimples : TipoPagamentoSimples.values()) {
					if (listaPagamentos.get(tipoPagamentoSimples.toString()).compareTo(new BigDecimal("0",mc))>0) {
						SaldoCaixa saldo = new SaldoCaixa();
						saldo.setCaixa(caixa);
						saldo.setForma(tipoPagamentoSimples);
						saldo.setValor(listaPagamentos.get(tipoPagamentoSimples.toString()).setScale(3,RoundingMode.HALF_EVEN));
						listaSaldoTemp.add(saldo);
					}
				}
				List<SaldoCaixa> caixaSaldoTemp = caixa.getSaldoCaixa();
				for (SaldoCaixa saldoCaixa : listaSaldoTemp) {
					int i = -1;
					for (int b = 0; b< caixaSaldoTemp.size();b++) {
						if (saldoCaixa.getForma().equals(caixaSaldoTemp.get(b).getForma())) {
							i=b;							
						}
					}
					if (i == -1) { //caso nao tenha a forma na lista de saldo de caixa,  adiciona uma
						SaldoCaixa saldo = new SaldoCaixa();
						saldo.setCaixa(caixa);
						saldo.setForma(saldoCaixa.getForma());
						saldo.setValor(saldoCaixa.getValor().round(mc));
						caixa.getSaldoCaixa().add(saldo);
					}else {
						caixa.getSaldoCaixa().get(i).setValor(saldoCaixa.getValor().setScale(3,RoundingMode.HALF_EVEN));
					}
				}
			}else {
				throw new CaixaException(this.translate("caixa.recebimento.isEmpty") + " - SaldoCaixa(Rec)");
			}
			break;
		case Ret: // Retirada do caixa
			if (!listaRecebimento.isEmpty() && listaRecebimento.size() != 0) {
				HashMap<String, BigDecimal> listaPagamentos = hashTipoSimplesLimpo();
				List<SaldoCaixa> listaSaldoTemp = new ArrayList<SaldoCaixa>();
				// Preenchendo a listaPagamentos com o saldo do caixa.
				for (SaldoCaixa saldo : caixa.getSaldoCaixa()){ 
					listaPagamentos.replace(saldo.getForma().toString(), saldo.getValor());
				}
				// Preenchendo a listaPagamento com os valores da lista de Recebimento
				for (RecebimentoParcial recebimentoParcial : listaRecebimento) {
					BigDecimal total = new BigDecimal("0");
					if (listaPagamentos.get(recebimentoParcial.getTipoPagamento().toString()).compareTo(recebimentoParcial.getValorRecebido())> -1) {
						total = listaPagamentos.get(recebimentoParcial.getTipoPagamento().toString()).subtract(recebimentoParcial.getValorRecebido().setScale(3,RoundingMode.HALF_EVEN));
						if (total.compareTo(new BigDecimal("0")) > -1 ) {
							listaPagamentos.replace(recebimentoParcial.getTipoPagamento().toString(),total.setScale(3,RoundingMode.HALF_EVEN));
							SaldoCaixa saldo = new SaldoCaixa();
							saldo.setCaixa(caixa);
							saldo.setForma(converteTipoSimples(recebimentoParcial.getTipoPagamento()));
							saldo.setValor(total.setScale(3,RoundingMode.HALF_EVEN));
							listaSaldoTemp.add(saldo);
						}else {
							throw new CaixaException(this.translate("caixa.insufficient.funds") + " total negativo");
						}
					}else {
						throw new CaixaException(this.translate("caixa.insufficient.funds" + " listaPagamento negativo"));
					}
					
				}
				List<SaldoCaixa> caixaSaldoTemp = caixa.getSaldoCaixa();
				for (SaldoCaixa saldoCaixa : listaSaldoTemp) {
					int i = -1;
					for (int b = 0; b< caixaSaldoTemp.size();b++) {
						if (saldoCaixa.getForma().equals(caixaSaldoTemp.get(b).getForma())) {
							i=b;							
						}
					}
					if (i == -1) { //caso nao tenha a forma na lista de saldo de caixa.  
						throw new CaixaException(this.translate("caixa.insufficient.funds") + " TipoPagamento não encontrado");
					}else {
						caixa.getSaldoCaixa().get(i).setValor(saldoCaixa.getValor());
					}
				}
			}else {
				throw new CaixaException(this.translate("caixa.recebimento.isEmpty") + " - SaldoCaixa(Ret)");
			}
			
			break;

		default:
			break;
		}
		return caixa;
	}
	 /**
	  * Calcula o desconto ou acréscimo e retorna o resultado
	  * @param total = BigDecimal com o valor total que sera aplicada o desconto ou acréscimo
	  * @param valor = valor do desconto ou acréscimo
	  * @param tipo (True - desconto  / False - acréscimo)
	  * @param porcentagem (True = por Porcentagem  False = por Valor) 
	  * @return BigDecimal = resultado em valor a ser adicionado ou subtraido do total (Arredondamento pra cima)
	  */
	public BigDecimal calculaDescontoAcrescimo(BigDecimal total, BigDecimal valor,boolean tipo, boolean porcentagem) {
		BigDecimal resultado = new BigDecimal("0",mc);
		BigDecimal porcento = valor.divide(new BigDecimal("100"));
		if (porcentagem) { // calculo por porcentagem 
			if (tipo) { // TRUE = DESCONTO
				resultado =	total.multiply(porcento,mc);
			}else {  // false = acréscimo
				resultado = total.multiply(porcento,mc);
			}
		}else { // calculo por valor
			if (tipo) { // TRUE = DESCONTO
				resultado = valor;
			}else {  // false = acréscimo
				resultado = valor;
			}
		}
		
		return resultado.setScale(2,RoundingMode.HALF_EVEN);
	}
	
	/**
	 *  retorna a porcentagem quando informado um valor em moeda para conceder desconto
	 * @param total - valor total do pedido
	 * @param desconto - valor em moeda corrente para abater do total do pedido
	 * @return
	 */
	public BigDecimal retornaPorcentagemDesconto (BigDecimal total,BigDecimal desconto) {
		return (new BigDecimal("1").subtract((total.subtract(desconto)).divide(total,mc))).multiply(new BigDecimal("100"),mc);
	}
}
