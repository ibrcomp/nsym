package br.com.nsym.application.controller.nfe.tools;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;

import javax.enterprise.context.RequestScoped;

import br.com.ibrcomp.exception.TributosException;
import br.com.nsym.domain.model.entity.fiscal.tools.OrigemXDestino;
import br.com.nsym.domain.model.entity.tools.Uf;
import lombok.Getter;
import lombok.Setter;

/**
 * Serviço de calculo de impostos
 * 
 * @author Ibrahim Yousef Quatani
 * @since 18/11/2017
 */

@RequestScoped
public class FormulasDosImpostos {

	private MathContext mc = new MathContext(20,RoundingMode.HALF_EVEN);
	
	@Getter
	@Setter
	private BigDecimal resultado = new BigDecimal("0",mc);
	
	/**
	 * Gera o Icms do produto IMPORTACAO
	 * 
	 * @param base
	 * @param valorIpi
	 * @param frete
	 * @param seguro
	 * @param despesas
	 * @param desconto
	 * @param aliquotaICMS
	 * @return
	 */
	public BigDecimal geraBaseIcmsImportacao(BigDecimal base,BigDecimal valorIpi,BigDecimal frete,BigDecimal seguro,BigDecimal despesas,BigDecimal desconto, BigDecimal aliquotaICMS){
		System.out.println("gera base icms importacao");
		BigDecimal baseIcmsImportacao = new BigDecimal("0");
		BigDecimal valorBase = new BigDecimal("0");
		BigDecimal pAliquota = new BigDecimal("0");
		
		valorBase = base.add(valorIpi.add(frete.add(seguro.add(despesas),mc),mc),mc).subtract(desconto,mc).setScale(2,RoundingMode.HALF_EVEN);
		System.out.println("GeraBaseIcmsImportacao : Valor Produto + adicionais : " + valorBase);
		if (valorBase.compareTo(new BigDecimal("0")) == 1 && aliquotaICMS.compareTo(new BigDecimal("0")) == 1){
			pAliquota = (new BigDecimal("100").subtract(aliquotaICMS)).divide(new BigDecimal("100"),mc);
			System.out.println("GeraBaseIcmsImportacao : Valor pAliquota " + pAliquota+ "aliquota ICMS " + aliquotaICMS);
			baseIcmsImportacao = 
					(valorBase.divide(pAliquota,mc)).setScale(2,RoundingMode.HALF_EVEN);
		}else {
			System.out.println("Não foi possivel fazer o calculo de GerarBaseIcmsImportacao");
			baseIcmsImportacao = valorBase;
		}

		return baseIcmsImportacao.setScale(2,RoundingMode.HALF_EVEN);
	}
	
	/**
	 * 	Gera o Icms do produto
	 * 
	 * @param baseIcms (valor total do produto)
	 * @param imposto (aliquota icms para o estado de destino)
	 * @return	o resultado 
	 */
	public BigDecimal geraIcms(BigDecimal baseIcms , BigDecimal imposto){
		if (imposto != null && baseIcms != null){
			this.resultado = baseIcms.multiply(imposto.divide(new BigDecimal("100"),mc));
		}else {
			System.out.println("Erro geraIcms");
			this.resultado = new BigDecimal("0");
		}
		return this.resultado.setScale(2,RoundingMode.HALF_EVEN); 
	}

	/**
	 *  Gera o IPI do produto
	 * 
	 * @param valorTotalProduto
	 * @param imposto (aliquota do imposto)
	 * @return o resultado
	 */
	public BigDecimal geraIpi(BigDecimal valorTotalProduto, BigDecimal imposto){
		if (valorTotalProduto != null && imposto != null){
			this.resultado= valorTotalProduto.multiply(imposto.divide(new BigDecimal("100"),mc));
		}else{
			System.out.println("Erro geraIpi");
			this.resultado = new BigDecimal("0");
		}
		return this.resultado.setScale(2,RoundingMode.HALF_EVEN);
	}

	/**
	 * Gera o valor de COFINS por aliquota
	 * 
	 * @param valorTotalProduto
	 * @param imposto (aliquota do imposto)
	 * @return o resultado
	 */
	public BigDecimal geraCofins(BigDecimal valorTotalProduto, BigDecimal imposto){
		if (valorTotalProduto != null && imposto != null){
			this.resultado = valorTotalProduto.multiply(imposto.divide(new BigDecimal("100"),mc));
		}else{
			System.out.println("Erro geraCofins");
			this.resultado = new BigDecimal("0");
		}
		return this.resultado.setScale(2,RoundingMode.HALF_EVEN);
	}
	
	/**
	 * Gera o valor de COFINS por valor
	 * 
	 * @param valorTotalProduto
	 * @param imposto (aliquota do imposto)
	 * @return o resultado
	 */
	public BigDecimal geraCofinsPorValor(BigDecimal valorTotalProduto, BigDecimal imposto){
		if (valorTotalProduto != null && imposto != null){
			this.resultado = valorTotalProduto.multiply(imposto,mc);
		}else{
			System.out.println("Erro geraCofins");
			this.resultado = new BigDecimal("0");
		}
		return this.resultado.setScale(2,RoundingMode.HALF_EVEN);
	}

	/** 
	 * Gera o valor de PIS por aliquota
	 * 	
	 * @param valorTotalProduto
	 * @param imposto (aliquota do pis)
	 * @return o resultado
	 * @throws TributosException 
	 */
	public BigDecimal geraPis(BigDecimal valorTotalProduto, BigDecimal imposto) throws TributosException{
		if (valorTotalProduto != null && imposto != null){
			this.resultado = valorTotalProduto.multiply(imposto.divide(new BigDecimal("100"),mc));
		}else{
			System.out.println("Erro geraPis");
			this.resultado = new BigDecimal("0");
			throw new TributosException("Erro: PIS - Informações incorretas ou ausentes");
		}
		return this.resultado.setScale(2,RoundingMode.HALF_EVEN);
	}
	
	/** 
	 * Gera o valor de PIS por valor
	 * 	
	 * @param valorTotalProduto
	 * @param imposto (Valor em REAIS do pis)
	 * @return o resultado
	 * @throws TributosException 
	 */
	public BigDecimal geraPisPorValor(BigDecimal valorTotalProduto, BigDecimal imposto) throws TributosException{
		if (valorTotalProduto != null && imposto != null){
			this.resultado = valorTotalProduto.multiply(imposto,mc);
		}else{
			System.out.println("Erro geraPis");
			this.resultado = new BigDecimal("0");
			throw new TributosException("Erro: PIS - Informações incorretas ou ausentes");
		}
		return this.resultado.setScale(2,RoundingMode.HALF_EVEN);
	}
	/**
	 * Gera o valor de ICMS-ST
	 * 
	 * @param baseIcmsSt Valor do produto ( produto + Frete + seguro + outras Despesas - Desconto)
	 * @param impostoInterno (aliquota icms praticado dentro do estado de destino)
	 * @return o valor de ICMS-ST
	 */
	public BigDecimal geraIcmsSt(BigDecimal baseIcmsSt,BigDecimal valorIcms, BigDecimal impostoInterno){

		if (baseIcmsSt != null && valorIcms != null && impostoInterno != null){
			this.resultado = (baseIcmsSt.multiply(impostoInterno.divide(new BigDecimal("100"),mc))).subtract(valorIcms); 
		}else{
			System.out.println("Erro geraIcmsSt");
			this.resultado = new BigDecimal("0");
		}
		return this.resultado.setScale(2,RoundingMode.HALF_EVEN);
	}

	/**
	 * 	Gera o MVA Ajustado para o estado de destino
	 * 
	 * @param mva ( mva original)
	 * @param imposto (Icms entre os estados origem X destino)
	 * @param impostoInterno ( Aliquota de icms interna no destino)
	 * @return o mva ajustado.
	 */
	public BigDecimal geraMvaAjustada(BigDecimal mva, BigDecimal imposto, BigDecimal impostoInterno){
		BigDecimal result = new BigDecimal("0",this.mc);
		BigDecimal resultParc = new BigDecimal("0",this.mc);
		BigDecimal mvaOriginal = new BigDecimal("0",this.mc);
		BigDecimal	aliqRemet = new BigDecimal("0",this.mc);
		BigDecimal aliqDestino = new BigDecimal("0",this.mc);
		if (mva != null && imposto != null && impostoInterno != null){
			mvaOriginal = new BigDecimal("1").add(mva.divide(new BigDecimal("100"),mc));
			aliqRemet =	new BigDecimal("1").subtract(imposto.divide(new BigDecimal("100"),mc));
			aliqDestino = new BigDecimal("1").subtract(impostoInterno.divide(new BigDecimal("100"),mc));
			resultParc = mvaOriginal.multiply(aliqRemet).divide(aliqDestino,mc);
			result = resultParc.subtract(new BigDecimal("1")).multiply(new BigDecimal("100"),mc);
		}else{
			System.out.println("Erro geraMvaAjustada");
			result = new BigDecimal("0");
		}

		return result.setScale(2,RoundingMode.HALF_EVEN);
	}

	/**
	 * Gera o IcmsSt Ajustado
	 * 
	 * @param baseIcms Valor do produto ( produto + Frete + seguro + outras Despesas - Desconto)
	 * @param imposto (aliquota icms do emitente para destino)
	 * @param impostoInterno (aliquota icms interna no destino)
	 * @param valorIpi
	 * @param mva (margem de valor agregado)
	 * @return o resultado
	 */
	public BigDecimal geraIcmsStAjustado(BigDecimal baseIcmsStAjustada,BigDecimal valorIcms ,BigDecimal impostoInterno){
		if (baseIcmsStAjustada != null && valorIcms != null && impostoInterno != null){
			this.resultado = geraIcmsSt(baseIcmsStAjustada, valorIcms, impostoInterno);
		}else{
			System.out.println("Erro geraIcmsStAjustado");
			this.resultado = new BigDecimal("0");
		}
		return this.resultado.setScale(2,RoundingMode.HALF_EVEN);
	}

	public BigDecimal geraBaseIcmsSt(BigDecimal valorProduto,BigDecimal valorIpi,BigDecimal frete,BigDecimal seguro,BigDecimal despesas,BigDecimal desconto, BigDecimal mva){
		System.out.println("gera base icmst");
		BigDecimal baseIcmsSt = new BigDecimal("0");
		BigDecimal valorProdutoTotal = new BigDecimal("0");
		
		valorProdutoTotal = valorProduto.add(valorIpi.add(frete.add(seguro.add(despesas),mc),mc),mc).subtract(desconto,mc).setScale(2,RoundingMode.HALF_EVEN);
		System.out.println("GeraBaseIcmsSt : Valor Produto + adicionais : " + valorProduto);
		if (valorProdutoTotal.compareTo(new BigDecimal("0")) == 1 && mva.compareTo(new BigDecimal("0")) == 1){
			baseIcmsSt = valorProdutoTotal.multiply(new BigDecimal("1").add(mva.divide(new BigDecimal("100"),mc),mc),mc).setScale(2,RoundingMode.HALF_EVEN);
		}else {
			System.out.println("Não foi possivel fazer o calculo de GeraBaseIcmsSt");
			baseIcmsSt = valorProdutoTotal;
		}

		return baseIcmsSt.setScale(2,RoundingMode.HALF_EVEN);
	}
	/**
	 * 
	 * @param baseIcms (Valor do produto + Frete + outras despesas acessorias - descontos + IPI)
	 * @param aliqDestino 
	 * @param aliqInterna Alíquota praticada dentro do estado de destino
	 * @param valorIpi Alíquota praticada para vendas do estado do emitente para o estado de destino
	 * @param mva MVA(iva) do produto no estado de destino
	 * @return
	 */

	public BigDecimal geraBaseIcmsStAjustada(BigDecimal baseIcms, BigDecimal aliqDestino, BigDecimal aliqInterna,BigDecimal valorIpi , BigDecimal mva){
		BigDecimal mvaAjustado = new BigDecimal("0",this.mc);
		BigDecimal baseIcmsStAjustado = new BigDecimal("0",this.mc);
		if (mva != null && aliqDestino !=null && aliqInterna != null){
			mvaAjustado = geraMvaAjustada(mva, aliqDestino, aliqInterna).setScale(2,RoundingMode.HALF_EVEN);
		}else{
			System.out.println("Não foi possível gerar mvaAjustado dentro do metodo GeraBaseIcmsStAjustada ");
		}
		if (baseIcms != null){
			baseIcms = baseIcms.add(valorIpi,mc).setScale(2,RoundingMode.HALF_EVEN);
			baseIcmsStAjustado = baseIcms.multiply(new BigDecimal("1",mc).add(mvaAjustado.divide(new BigDecimal("100"),mc),mc),mc).setScale(2,RoundingMode.HALF_EVEN);
		}else{
			System.out.println("Erro geraBaseIcmsStAjustada");
		}
		return baseIcmsStAjustado.setScale(2,RoundingMode.HALF_EVEN);
	}
	
	public BigDecimal geraBaseIcmsReducao(BigDecimal valorTotal, BigDecimal reducao){
		BigDecimal resultado = new BigDecimal("0");
		BigDecimal converteReducao = new BigDecimal("0");
		
		converteReducao = new BigDecimal("100",mc).subtract(reducao,mc).setScale(2,RoundingMode.HALF_EVEN);
		resultado = valorTotal.multiply(converteReducao.divide(new BigDecimal("100"),mc),mc).setScale(2,RoundingMode.HALF_EVEN);
//		resultado = valorTotal.multiply(reducao.divide(new BigDecimal("100"),mc)).setScale(2,RoundingMode.HALF_EVEN);
		return resultado; 
	}
	
	public BigDecimal calculaTotalTributositem(BigDecimal valorTributos, BigDecimal totalProduto){
		BigDecimal resultado = new BigDecimal("0",mc);
		resultado = totalProduto.multiply((valorTributos.divide(new BigDecimal("100"))),mc);
		return resultado.setScale(2,RoundingMode.HALF_EVEN);
	}
	
	/**
	 * 
	 * @param baseICMS (Valor do produto + Frete + outras despesas acessorias - descontos + IPI)
	 * @param aliqICMSIntra Alíquota praticada dentro do estado de destino
	 * @param aliqICMSInter Alíquota praticada para vendas do estado do emitente para o estado de destino
	 * @return Valor do ICMS que cabe para o DESTINO
	 */
	public BigDecimal calculaPartilhaDestino(BigDecimal baseICMS, BigDecimal aliqICMSIntra,BigDecimal aliqICMSInter ){
		BigDecimal resultado = new BigDecimal("0",mc);
		LocalDate ano = LocalDate.now();
		LocalDate vinteUm = LocalDate.of(2021, 1, 1);
		if (ano.getYear() >= vinteUm.getYear()){
//			resultado = baseICMS.multiply(aliqICMSInter.divide(new BigDecimal("100",mc),mc));
			resultado = baseICMS.multiply((aliqICMSIntra.subtract(aliqICMSInter,mc)).divide(new BigDecimal("100",mc),mc));
		}else {
			resultado = baseICMS.multiply((aliqICMSIntra.subtract(aliqICMSInter,mc)).divide(new BigDecimal("100",mc),mc));
			resultado = resultado.multiply(new BigDecimal("0.80",mc));		
		}
		return resultado.setScale(2,RoundingMode.HALF_EVEN);
	}
	
	/**
	 * 
	 * @param baseICMS (Valor do produto + Frete + outras despesas acessorias - descontos + IPI)
	 * @param aliqICMSIntra Alíquota praticada dentro do estado de destino
	 * @param aliqICMSInter Alíquota praticada para vendas do estado do emitente para o estado de destino
	 * @return Valor do ICMS que cabe a ORIGEM (UF do EMITENTE)
	 */
	public BigDecimal calculaPartilhaOrigem(BigDecimal baseICMS, BigDecimal aliqICMSIntra,BigDecimal aliqICMSInter ){
		BigDecimal resultado = new BigDecimal("0");
		LocalDate ano = LocalDate.now();
			if (ano.getYear() > 2018){
				resultado = new BigDecimal("0");
			}else{
				resultado = baseICMS.multiply((aliqICMSIntra.subtract(aliqICMSInter,mc)).divide(new BigDecimal("100",mc),mc),mc);
				resultado = resultado.multiply(new BigDecimal("0.20",mc),mc);		
			}
			
		return resultado.setScale(2,RoundingMode.HALF_EVEN);
	}
	
	public BigDecimal calculaValorFCP(BigDecimal baseFCP,BigDecimal aliqFCP){
		return baseFCP.multiply(aliqFCP.divide(new BigDecimal("100",mc),mc)).setScale(2,RoundingMode.HALF_EVEN);
	}
	
	/**
	 * 
	 * @param valorBase Valor do produto ( produto + Frete + seguro + outras Despesas - Desconto)
	 * @param estadoOrigemProduto
	 * @param estadoDestinoProduto
	 * @param mva - Informe o mva do produto (origem x destino)
	 * @return
	 */
	
	public BigDecimal geraSTRetido(BigDecimal valorBase, Uf estadoOrigemProduto , Uf estadoDestinoProduto, BigDecimal mva){
		BigDecimal valorIcms = new BigDecimal("0",mc);
		BigDecimal aliquota = new BigDecimal("0",mc);
		BigDecimal aliquotaInterna = new BigDecimal("0",mc);
		BigDecimal baseIcmsSt = new BigDecimal("0",mc);
		OrigemXDestino pegaAliq = new OrigemXDestino();
		aliquota = pegaAliq.pegaAliquota(estadoOrigemProduto, estadoDestinoProduto);
		aliquotaInterna = pegaAliq.pegaAliquota(estadoDestinoProduto, estadoDestinoProduto);
		
		if (valorBase.compareTo(new BigDecimal("0")) == 1 && mva.compareTo(new BigDecimal("0")) == 1){
			baseIcmsSt = valorBase.multiply(new BigDecimal("1",mc).add(mva.divide(new BigDecimal("100",mc),mc),mc),mc).setScale(2,RoundingMode.HALF_EVEN);
		}else {
			System.out.println("Não foi possivel fazer o calculo de GeraBaseIcmsSt");
			baseIcmsSt = valorBase;
		}
		valorIcms = valorBase.multiply(aliquota.divide(new BigDecimal("100")));				
		if (baseIcmsSt != null && valorIcms != null && aliquotaInterna != null){
			
			this.resultado = (baseIcmsSt.multiply(aliquotaInterna.divide(new BigDecimal("100",mc),mc),mc)).subtract(valorIcms,mc).setScale(2,RoundingMode.HALF_EVEN);
			
		}else{
			System.out.println("Erro geraIcmsSt");
			this.resultado = new BigDecimal("0",mc);
		}
		return this.resultado.setScale(2,RoundingMode.HALF_EVEN);
	}
	
}
