package br.com.nsym.domain.misc;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.HibernateException;

import br.com.nsym.application.controller.AbstractBean;
import br.com.nsym.application.controller.nfe.tools.CalculaTributos;
import br.com.nsym.domain.model.entity.financeiro.FormaDePagamento;
import br.com.nsym.domain.model.entity.financeiro.TipoPagamento;
import br.com.nsym.domain.model.entity.financeiro.tools.ParcelasNfe;
import javax.enterprise.context.Dependent;
/**
 * FinanceiroTools é uma classe padrao para auxiliar nos calculos e parcelamentos
 * 
 * @author Ibrahim Yousef Quatani
 *
 */

@Dependent
public class FinanceiroTools extends AbstractBean {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	public CalculaTributos calculaTributos; 

	/**
	 * Método que gera uma lista de parcelas, calculando prazos conforme definidos na classe FormaDePagamento
	 * 
	 * @param pagamento - Forma De Pagamento
	 * @param valor - Valor Total a ser parcelada
	 * @param controle - Numero identificador da transação
	 * @return Lista com as parcelas 
	 */
	public List<ParcelasNfe> preencheParcelamento(FormaDePagamento pagamento,BigDecimal valor,Long controle){
		try{
			List<ParcelasNfe> listaParcelas  = new ArrayList<>();
			LocalDate hoje = LocalDate.now();
			MathContext precisao = new MathContext(20, RoundingMode.HALF_EVEN);
			Long numParcela = 1L;
			BigDecimal somaDasParcelas = new BigDecimal("0");
			BigDecimal resultado = new BigDecimal("0");
			BigDecimal valorCadaParcela = new BigDecimal("0");
			ParcelasNfe parcelaTemporaria = new ParcelasNfe();
			String parc= ""+pagamento.getParcelas();
			System.out.println("quantidade parcelas " + pagamento.getParcelas());
			// só irá fazer os cálculos caso  valor Total da nota for maior que 0 e numero de parcelas tambem maior que zero
			if ((valor.compareTo(new BigDecimal("0")) > 0  && pagamento.getParcelas() > 0) || (pagamento.getTipoPagamento().equals(TipoPagamento.Spg))){
				if (pagamento.getTipoPagamento().equals(TipoPagamento.Spg)){
					parcelaTemporaria.setNumParcela(1L);
					parcelaTemporaria.setControle(controle);
					parcelaTemporaria.setValorParcela(valor);
					parcelaTemporaria.setVencimento(hoje);
					parcelaTemporaria.setFormaPag(pagamento);
					listaParcelas.add(parcelaTemporaria);
				}else{
					valorCadaParcela = valor.divide(new BigDecimal(parc),precisao).setScale(2, RoundingMode.HALF_EVEN);;
					System.out.println("valor de cada parcela com arredondamento para baixo 2 casas decimais" + valorCadaParcela);
					somaDasParcelas = valorCadaParcela.multiply(new BigDecimal(pagamento.getParcelas()));
					resultado = valor.subtract(somaDasParcelas);
					System.out.println("valor da nota - soma das parcelas = " + resultado);

					for (int i = 0 ; i < pagamento.getParcelas() ; i++){
						parcelaTemporaria.setFormaPag(pagamento);
						parcelaTemporaria.setValorParcela(valorCadaParcela);
						if ( i == 0 ){
							System.out.println("Estou no parcela temporaria i = 0 ");
							parcelaTemporaria.setVencimento(hoje.plusDays(pagamento.getIntervalo()+pagamento.getCarencia()));
						}else{
							System.out.println("estou no parcela Temporaria onde i = " + i);
							parcelaTemporaria.setVencimento(hoje.plusDays((pagamento.getIntervalo()* (i+1))+pagamento.getCarencia()));
						}
						parcelaTemporaria.setNumParcela(numParcela);
						parcelaTemporaria.setControle(controle);
						listaParcelas.add(parcelaTemporaria);
						parcelaTemporaria = new ParcelasNfe();
						numParcela++;
					}
					if (valor.compareTo(somaDasParcelas) > 0 ){
						listaParcelas.get(0).setValorParcela(valorCadaParcela.add(resultado));
						System.out.println("Valor da primeira parcela " + listaParcelas.get(0).getValorParcela().toString());
					}
					for (ParcelasNfe parcelasNfe : listaParcelas) {
						System.out.println("Estou dentro do foreach listaParcelass");
						System.out.println(parcelasNfe.getVencimento());
						System.out.println(parcelasNfe.getValorParcela());
						System.out.println(parcelasNfe.getFormaPag().getTipoPagamento().name());
					}
				}
			}
			return listaParcelas;
		}catch (HibernateException h){
			this.addError(true, "Não foi possivel apagar as parcelas", h.getCause());
			return null;
		}catch (Exception e) {
			this.addError(true, "Não sei qual foi a causa do erro", e.getCause());
			return null;
		}
	}
	
}
