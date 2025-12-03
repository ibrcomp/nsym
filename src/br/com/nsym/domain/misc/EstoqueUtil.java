package br.com.nsym.domain.misc;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.persistence.Transient;
import javax.transaction.Transactional;

import org.hibernate.HibernateException;

import br.com.ibrcomp.exception.EstoqueException;
import br.com.ibrcomp.exception.EstoqueRuntimeException;
import br.com.ibrcomp.interceptor.RollbackOn;
import br.com.nsym.application.component.Translator;
import br.com.nsym.domain.model.entity.cadastro.BarrasEstoque;
import br.com.nsym.domain.model.entity.cadastro.Produto;
import br.com.nsym.domain.model.entity.estoque.Estoque;
import br.com.nsym.domain.model.entity.financeiro.produto.ProdutoCusto;
import br.com.nsym.domain.model.entity.fiscal.Item;
import br.com.nsym.domain.model.entity.fiscal.NcmEstoque;
import br.com.nsym.domain.model.entity.fiscal.nfe.ItemNfe;
import br.com.nsym.domain.model.entity.venda.ItemPedido;
import br.com.nsym.domain.model.repository.cadastro.BarrasEstoqueRepository;
import br.com.nsym.domain.model.repository.financeiro.CustoProdutoRepository;
import br.com.nsym.domain.model.repository.fiscal.NcmEstoqueRepository;

@RequestScoped
public class EstoqueUtil {
	
	// Estoque Geral
		@Inject
		private BarrasEstoqueRepository barrasDao;
		
		@Inject
		private CustoProdutoRepository custoDao;
		
		//Estoque fiscal
		@Inject
		private NcmEstoqueRepository ncmDao;
		
		@Inject
		@Default
		private Translator translator;
		
		@Transient
		private MathContext mc = new MathContext(20, RoundingMode.HALF_EVEN);
	
	/**
	 * Metodo que calcula o estoque total independente de tamanho ou cor
	 * @return Bigdecimal com a somatoria
	 */
	public BigDecimal calculaEstoqueTotal(List<BarrasEstoque> lista){
		BigDecimal resultado = new BigDecimal("0");
		if (!lista.isEmpty()){
			for (BarrasEstoque estoque : lista) { //testar mudanï¿½a
				if(estoque.getTotalEstoque() != null){
					resultado  = resultado.add(estoque.getTotalEstoque());
				}
			}
		}
		return resultado;
	}
	/**
	 * MÃ©todo para o calculo do estoqueAnterir do produto
	 * @param lista - BarrasEstoque 
	 * @return Total estoque anterior - BigDecimal
	 */
	public BigDecimal calculaEstoqueAnterior(List<BarrasEstoque> lista){
		BigDecimal resultado = new BigDecimal("0");
		if (lista != null) {
			if (!lista.isEmpty()){
				for (BarrasEstoque estoque : lista) { //testar mudanï¿½a
					if(estoque.getEstoqueAnterior() != null){
						resultado  = resultado.add(estoque.getEstoqueAnterior());
					}
				}
			}
		}
		return resultado;
	}
	/**
	 *  CÃ¡lcula o estoque total anterir a Ãºltima alteraÃ§Ã£o, por produto!
	 * @param produto
	 * @param idEmpresa
	 * @param idFilial
	 * @return BigDecimal
	 */
	public BigDecimal estoqueTotalAnteriorPorProduto(Produto produto,Long idEmpresa,Long idFilial) {
		return  calculaEstoqueAnterior(this.barrasDao.listaBarrasPorProduto(produto, idEmpresa , idFilial));
	}
	
	/**
	 *  CÃ¡lcula o estoque total anterir a Ãºltima alteraÃ§Ã£o, por produto!
	 * @param produto
	 * @param idEmpresa
	 * @param idFilial
	 * @return BigDecimal
	 */
	public BigDecimal pegaEstoqueTotal(Produto produto,Long idEmpresa,Long idFilial) {
		return  calculaEstoqueTotal(this.barrasDao.listaBarrasPorProduto(produto, idEmpresa , idFilial));
	}

	/**
	 * Metodo que calcula o total jï¿½ comprado do produto
	 * @return Bigdecimal com o resultado
	 */

	public BigDecimal calculaTotalComprado(List<BarrasEstoque> lista){
		BigDecimal resultado = new BigDecimal("0");
		if (!lista.isEmpty()){
			for (BarrasEstoque estoque : lista) {
				if (estoque.getTotalComprado() != null){
					resultado  = resultado.add(estoque.getTotalComprado());
				}
			}
		}
		return resultado;
	}

	/**
	 * Metodo que retorna a data da ultima compra do produto
	 * @return LocalDate da ultima compra
	 */

	public LocalDate retornaUltimaCompra(List<BarrasEstoque> lista){
		LocalDate data = LocalDate.now().minusMonths(6);
		if (!lista.isEmpty()){
			for (BarrasEstoque barrasEstoque : lista) {
				if (barrasEstoque.getUltimaCompra() != null) {
					data = barrasEstoque.getUltimaCompra();
				}
			}
			for (BarrasEstoque barrasEstoque : lista) {
				if (barrasEstoque.getUltimaCompra() != null){
					if (barrasEstoque.getUltimaCompra().isAfter(data)){
						data = barrasEstoque.getUltimaCompra();
					}
				}
			}
		}
		return data;
	}

	/**
	 * Metodo que retorna a data da primeira compra do produto
	 * @return LocalDate da ultima compra
	 */

	public LocalDate retornaPrimeiraCompra(List<BarrasEstoque> lista){
		LocalDate data = LocalDate.now();
		if (!lista.isEmpty()){
			for (BarrasEstoque barrasEstoque : lista) {
				if (barrasEstoque.getUltimaCompra() != null) {
					data = barrasEstoque.getUltimaCompra();
				}
			}
			for (BarrasEstoque barrasEstoque : lista) {
				if (barrasEstoque.getUltimaCompra() != null){
					if (barrasEstoque.getUltimaCompra().isBefore(data)){
						data = barrasEstoque.getUltimaCompra();
					}
				}
			}
		}
		return data;
	}

	/**
	 * Metodo que retorna a quantidade Total da ultima compra do produto
	 * @return LocalDate da ultima compra
	 */

	public BigDecimal retornaTotalUltimaCompra(List<BarrasEstoque> lista){
		LocalDate data = LocalDate.now().minusMonths(6);
		BigDecimal resultado = new BigDecimal("0");
		if (!lista.isEmpty()){
			for (BarrasEstoque barrasEstoque : lista) {
				if (barrasEstoque.getUltimaCompra() != null) {
					data = barrasEstoque.getUltimaCompra();
				}
			}
			for (BarrasEstoque barrasEstoque : lista) {
				if (barrasEstoque.getUltimaCompra() != null){
					if (barrasEstoque.getUltimaCompra().isAfter(data)){
						data = barrasEstoque.getUltimaCompra();
					}
				}
			}
			for (BarrasEstoque barrasEstoque : lista) {
				if (barrasEstoque.getUltimaCompra() != null){
					if (barrasEstoque.getUltimaCompra().isEqual(data)){
						resultado = resultado.add(barrasEstoque.getQuantidadeAcrescentada());
					}
				}
			}
		}
		return resultado;
	}
	/**
	 *  MÃ©todo que retorna o estoque preenchido do item informado
	 * @param item - generico utilizado tanto por NFE / SAT / Pedido
	 * @param empresa
	 * @param filial
	 * @return Classe Estoque
	 * @throws EstoqueException 
	 */
	public Estoque preencheEstoqueItem(Item item,Long empresa, Long filial) throws EstoqueException  {
		
		Estoque estoqueTemp = new Estoque();
		estoqueTemp.setNcmEstoque(ncmDao.pegaNcmComEstoque(item.getProduto().getNcm(), empresa, filial));
		// pegando a lista de barrasEstoque do produto
		List<BarrasEstoque> listaBarras = barrasDao.listaBarrasPorProduto(item.getProduto(), empresa, filial);
		if (listaBarras != null) {
//			if (item.getBarras() != null || !item.getBarras().getBarras().isEmpty()) {
			for (BarrasEstoque barrasEstoque : listaBarras) {
				if (barrasEstoque.getBarras() == item.getRef()) {
					item.setBarras(barrasEstoque);
				}
			}
			if (item.getBarras() != null) {
				if (item.getBarras().getBarras() != null ) {
					for (BarrasEstoque barrasEstoque : listaBarras) {
						if (barrasEstoque.getBarras().equalsIgnoreCase(item.getBarras().getBarras())) {
							estoqueTemp.setBarrasEstoque(barrasDao.encontraBarrasPorEmpresaEFilialEProduto(barrasEstoque.getBarras(),barrasEstoque.getProdutoBase(), empresa, filial));
						}
					}
				}else { // produto nao tem codigo de barras
					if (listaBarras.size() == 1) {
						estoqueTemp.setBarrasEstoque(barrasDao.pegaEstoque(listaBarras.get(0).getProdutoBase().getId(), empresa, filial));
					}else  {
						estoqueTemp.setBarrasEstoque(barrasDao.findById(item.getBarras().getId(), false));
						if (estoqueTemp.getBarrasEstoque() == null) {
							
							throw new EstoqueException(translator.translate("estoqueException.barras.muitosEncontrados") + item.getBarras().getId());
						}
					}
				}
			}else {
				if (listaBarras.size() == 1) {
					// localizando barrasEstoque do produto.
					BarrasEstoque barrasLocalizado = new BarrasEstoque();
					barrasLocalizado = barrasDao.pegaEstoque(item.getProduto().getId(), empresa, filial);
					if (barrasLocalizado == null) { // caso nao tenha encontrado estoque para o produto cria um.
						BarrasEstoque novoBarras = new BarrasEstoque();
						estoqueTemp.setBarrasEstoque(novoBarras);
					}else { // localizado o estoque do produto
						estoqueTemp.setBarrasEstoque(barrasLocalizado);
					}
				}else  {
					BarrasEstoque barrasLocalizado = new BarrasEstoque();
					barrasLocalizado = barrasDao.encontraBarrasPorEmpresa(item.getRef(),empresa, filial);
//					barrasLocalizado = barrasDao.encontraBarrasPorEmpresaEFilialEProduto(item.getRef(),item.getProduto(), empresa, filial);
					if (barrasLocalizado != null) {
						estoqueTemp.setBarrasEstoque(barrasLocalizado);
					}else {
						throw new EstoqueException(translator.translate("estoqueException.barras.muitosEncontrados"));
					}
				}
			}
		}
//		estoqueTemp.setBarrasEstoque(barrasDao.pegaEstoque(item.getProduto().getId(), empresa, filial));
		if (estoqueTemp.getBarrasEstoque() == null && estoqueTemp.getNcmEstoque() == null) {
			estoqueTemp.setBarrasEstoque(new BarrasEstoque());
			estoqueTemp.setNcmEstoque(new NcmEstoque());
			estoqueTemp.getBarrasEstoque().setProdutoBase(item.getProduto());
			estoqueTemp.getNcmEstoque().setNcm(item.getProduto().getNcm());
		}else {
			if (estoqueTemp.getBarrasEstoque() == null) {
				estoqueTemp.setBarrasEstoque(new BarrasEstoque());
				estoqueTemp.getBarrasEstoque().setProdutoBase(item.getProduto());
			}else if (estoqueTemp.getNcmEstoque() == null) {
				estoqueTemp.setNcmEstoque(new NcmEstoque());
				estoqueTemp.getNcmEstoque().setNcm(item.getProduto().getNcm());
			}
		}if (estoqueTemp.getBarrasEstoque() == null && estoqueTemp.getNcmEstoque() == null) {
			estoqueTemp.setBarrasEstoque(new BarrasEstoque());
			estoqueTemp.getBarrasEstoque().setProdutoBase(item.getProduto());
			estoqueTemp.setNcmEstoque(new NcmEstoque());
			estoqueTemp.getNcmEstoque().setNcm(item.getProduto().getNcm());
		}else {
			if (estoqueTemp.getBarrasEstoque() == null) {
				estoqueTemp.setBarrasEstoque(new BarrasEstoque());
				estoqueTemp.getBarrasEstoque().setProdutoBase(item.getProduto());
			}else if (estoqueTemp.getNcmEstoque() == null) {
				estoqueTemp.setNcmEstoque(new NcmEstoque());
				estoqueTemp.getNcmEstoque().setNcm(item.getProduto().getNcm());
			}
		}
		return estoqueTemp;
	}
	
	/**
	 *  Mï¿½todo que retorna o estoque preenchido do item informado
	 * @param item - generico utilizado tanto por NFE / SAT / Pedido
	 * @param empresa
	 * @param filial
	 * @return Classe Estoque
	 */
	public Estoque preencheEstoqueItemNfe(ItemNfe item,Long empresa, Long filial) {
		
		Estoque estoqueTemp = new Estoque();
		estoqueTemp.setNcmEstoque(ncmDao.pegaNcmComEstoque(item.getProduto().getNcm(), empresa, filial));
		estoqueTemp.setBarrasEstoque(barrasDao.pegaEstoque(item.getProduto().getId(), empresa, filial));
		if (estoqueTemp.getBarrasEstoque() == null && estoqueTemp.getNcmEstoque() == null) {
			estoqueTemp.setBarrasEstoque(new BarrasEstoque());
			estoqueTemp.setNcmEstoque(new NcmEstoque());
			estoqueTemp.getBarrasEstoque().setProdutoBase(item.getProduto());
			estoqueTemp.getNcmEstoque().setNcm(item.getProduto().getNcm());
		}else {
			if (estoqueTemp.getBarrasEstoque() == null) {
				estoqueTemp.setBarrasEstoque(new BarrasEstoque());
				estoqueTemp.getBarrasEstoque().setProdutoBase(item.getProduto());
			}else if (estoqueTemp.getNcmEstoque() == null) {
				estoqueTemp.setNcmEstoque(new NcmEstoque());
				estoqueTemp.getNcmEstoque().setNcm(item.getProduto().getNcm());
			}
		}if (estoqueTemp.getBarrasEstoque() == null && estoqueTemp.getNcmEstoque() == null) {
			estoqueTemp.setBarrasEstoque(new BarrasEstoque());
			estoqueTemp.getBarrasEstoque().setProdutoBase(item.getProduto());
			estoqueTemp.setNcmEstoque(new NcmEstoque());
			estoqueTemp.getNcmEstoque().setNcm(item.getProduto().getNcm());
		}else {
			if (estoqueTemp.getBarrasEstoque() == null) {
				estoqueTemp.setBarrasEstoque(new BarrasEstoque());
				estoqueTemp.getBarrasEstoque().setProdutoBase(item.getProduto());
			}else if (estoqueTemp.getNcmEstoque() == null) {
				estoqueTemp.setNcmEstoque(new NcmEstoque());
				estoqueTemp.getNcmEstoque().setNcm(item.getProduto().getNcm());
			}
		}
		return estoqueTemp;
	}
	
	/**
	 * Mï¿½todo para Diminuir a quantidade informada no estoque  
	 * 
	 * @param estoqueProduto
	 * @param quantidade
	 * @param empresa
	 * @param Filial
	 * @param fiscal (TRUE) atualiza estoque fiscal (FALSE) nï¿½o atualiza estoque fiscal
	 * @param geral (TRUE) atualiza estoque geral (FALSE) nï¿½o ataliza estoque geral
	 * @return classe Estoque
	 * @throws EstoqueException
	 * @throws HibernateException
	 */
	public Estoque subtraiEstoque(Estoque estoqueProduto,BigDecimal quantidade,Long empresa, Long filial, boolean fiscal, boolean geral) throws EstoqueException,HibernateException {
		if (quantidade.compareTo(new BigDecimal("0"))>0) {
			if (estoqueProduto.getNcmEstoque() != null && fiscal && geral && estoqueProduto.getBarrasEstoque() != null) {
				estoqueProduto.getNcmEstoque().setEstoque(estoqueProduto.getNcmEstoque().getEstoque().subtract(quantidade));		
				estoqueProduto.getBarrasEstoque().setTotalEstoque(estoqueProduto.getBarrasEstoque().getTotalEstoque().subtract(quantidade));
			}else 
			if  (estoqueProduto.getNcmEstoque() != null && fiscal && geral == false) {
				estoqueProduto.getNcmEstoque().setEstoque(estoqueProduto.getNcmEstoque().getEstoque().subtract(quantidade));	
			}else
			if ( fiscal == false && geral && estoqueProduto.getBarrasEstoque() != null ) {
					estoqueProduto.getBarrasEstoque().setTotalEstoque(estoqueProduto.getBarrasEstoque().getTotalEstoque().subtract(quantidade));
			}else {
				throw new EstoqueException("estoqueException.geral.estoqueNULL");
			}
			return estoqueProduto;
		}else {
			throw new EstoqueException("estoqueException.geral.quantidadeNegativa");
		}
	}
	
	/**
	 * Mï¿½todo que ADICIONA a quantidade informada ao estoque do produto
	 * 
	 * @param estoqueProduto
	 * @param quantidade
	 * @param empresa
	 * @param filial
	 * @param fiscal (TRUE) atualiza ambos os estoques (FALSE) apenas o estoque geral
	 * @return
	 * @throws EstoqueException
	 * @throws HibernateException
	 */
	public Estoque acrescentaEstoque(Estoque estoqueProduto,BigDecimal quantidade,Long empresa, Long filial, boolean fiscal, boolean geral) throws EstoqueException {
		if (quantidade.compareTo(new BigDecimal("0"))>0) {
			if (estoqueProduto.getNcmEstoque() != null && fiscal && geral && estoqueProduto.getBarrasEstoque() != null) {
				estoqueProduto.getNcmEstoque().setEstoque(estoqueProduto.getNcmEstoque().getEstoque().add(quantidade));	
				// acrescentado a quantidade ao estoque
				estoqueProduto.getBarrasEstoque().setTotalEstoque(estoqueProduto.getBarrasEstoque().getTotalEstoque().add(quantidade));
				// Acrescentando ao total jÃ¡ comprado
			}else 
			if  (estoqueProduto.getNcmEstoque() != null && fiscal && geral == false) {
				estoqueProduto.getNcmEstoque().setEstoque(estoqueProduto.getNcmEstoque().getEstoque().add(quantidade));	
			}else
			if ( fiscal == false && geral && estoqueProduto.getBarrasEstoque() != null ) {
					// acrescentando a quantidade ao estoque
					estoqueProduto.getBarrasEstoque().setTotalEstoque(estoqueProduto.getBarrasEstoque().getTotalEstoque().add(quantidade));
			}else {
				throw new EstoqueException("estoqueException.geral.estoqueNULL");
			}
			return estoqueProduto;
		}else {
			throw new EstoqueException("estoqueException.geral.quantidadeNegativa");
		}
	}
	
	/**
	 * 
	 * MÃ©todo que ADICIONA a quantidade informada ao estoque do produto, armazenando data do recebimento, 
	 * quantidade recebida, data da ultima compra e armazenando o estoque antigo para eventual auditoria.
	 *  UTILIZAR somente para RECEBIMENTO DE MATERIAL - MóDULO ESTOQUE 
	 * 
	 * @param estoqueProduto
	 * @param quantidade
	 * @param empresa
	 * @param filial
	 * @param fiscal (TRUE) atualiza ambos os estoques (FALSE) apenas o estoque geral
	 * @param geral
	 * @param totalComprado
	 * @return
	 * @throws EstoqueException
	 */
	
	public Estoque acrescentaEstoqueRecebimentoMaterial(
	        Estoque estoqueProduto,
	        BigDecimal quantidade,
	        Long empresa,
	        Long filial,
	        boolean fiscal,
	        boolean geral,
	        boolean totalComprado) throws EstoqueException {

	    validarQuantidade(quantidade);

	    if (fiscal && geral && estoqueProduto.getNcmEstoque() != null && estoqueProduto.getBarrasEstoque() != null) {
	        atualizarNcmEstoque(estoqueProduto, quantidade);
	        atualizarBarrasEstoque(estoqueProduto, quantidade, totalComprado);
	    } else if (fiscal && !geral && estoqueProduto.getNcmEstoque() != null) {
	        atualizarNcmEstoque(estoqueProduto, quantidade);
	    } else if (!fiscal && geral && estoqueProduto.getBarrasEstoque() != null) {
	        atualizarBarrasEstoque(estoqueProduto, quantidade, totalComprado);
	    } else {
	        throw new EstoqueException("estoqueException.geral.estoqueNULL");
	    }

	    return estoqueProduto;
	}
	
	private void validarQuantidade(BigDecimal quantidade) throws EstoqueException {
	    if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
	        throw new EstoqueException("estoqueException.geral.quantidadeNegativa");
	    }
	}

	private void atualizarNcmEstoque(Estoque estoqueProduto, BigDecimal quantidade) {
	    estoqueProduto.getNcmEstoque().setEstoque(
	        estoqueProduto.getNcmEstoque().getEstoque().add(quantidade));
	}

	private void atualizarBarrasEstoque(Estoque estoqueProduto, BigDecimal quantidade, boolean totalComprado) {
	    BarrasEstoque barras = estoqueProduto.getBarrasEstoque();

	    barras.setQuantidadeAcrescentada(quantidade);
	    barras.setEstoqueAnterior(barras.getTotalEstoque());

	    LocalDate hoje = LocalDate.now();

	    if (barras.getUltimaCompra() == null) {
	        barras.setUltimaCompra(hoje);
	        barras.setDataRecebimentoAnterior(hoje);
	        barras.setTotalUltimaCompra(quantidade);
	    } else {
	        barras.setDataRecebimentoAnterior(barras.getUltimaCompra());

	        if (barras.getUltimaCompra().isEqual(hoje)) {
	            barras.setTotalUltimaCompra(barras.getTotalUltimaCompra().add(quantidade));
	        } else {
	            barras.setTotalUltimaCompra(quantidade);
	        }

	        barras.setUltimaCompra(hoje);
	    }

	    barras.setTotalEstoque(barras.getTotalEstoque().add(quantidade));

	    if (totalComprado) {
	        barras.setTotalComprado(barras.getTotalComprado().add(quantidade));
	    }
	}

	/**
	 * Mï¿½todo para Zerar a quantidade no estoque  
	 * 
	 * @param estoqueProduto
	 * @param empresa
	 * @param Filial
	 * @param fiscal (TRUE) atualiza estoque fiscal (FALSE) nï¿½o atualiza estoque fiscal
	 * @param geral (TRUE) atualiza estoque geral (FALSE) nï¿½o ataliza estoque geral
	 * @return classe Estoque
	 * @throws EstoqueException
	 * @throws HibernateException
	 */
	public Estoque zeraEstoque(Estoque estoqueProduto,Long empresa, Long filial, boolean fiscal, boolean geral,boolean zeraTotalComprado) throws EstoqueException,HibernateException {
		if (estoqueProduto.getNcmEstoque() != null && fiscal && geral && estoqueProduto.getBarrasEstoque() != null) {
			estoqueProduto.getNcmEstoque().setEstoque(new BigDecimal("0"));		
			estoqueProduto.getBarrasEstoque().setTotalEstoque(new BigDecimal("0"));
			if (zeraTotalComprado) {
				estoqueProduto.getBarrasEstoque().setTotalComprado(new BigDecimal("0"));
			}
		}else 
			if  (estoqueProduto.getNcmEstoque() != null && fiscal && geral == false) {
				estoqueProduto.getNcmEstoque().setEstoque(new BigDecimal("0"));	
			}else
				if ( fiscal == false && geral && estoqueProduto.getBarrasEstoque() != null ) {
					estoqueProduto.getBarrasEstoque().setTotalEstoque(new BigDecimal("0"));
					if (zeraTotalComprado) {
						estoqueProduto.getBarrasEstoque().setTotalComprado(new BigDecimal("0"));
					}
				}else {
					throw new EstoqueException("estoqueException.geral.estoqueNULL");
				}
		return estoqueProduto;
	}
	
	/**
	 * Mï¿½todo para Define a quantidade no estoque  
	 * 
	 * @param estoqueProduto
	 * @param quantidade
	 * @param empresa
	 * @param Filial
	 * @param fiscal (TRUE) atualiza estoque fiscal (FALSE) nï¿½o atualiza estoque fiscal
	 * @param geral (TRUE) atualiza estoque geral (FALSE) nï¿½o ataliza estoque geral
	 * @return classe Estoque
	 * @throws EstoqueException
	 * @throws HibernateException
	 */
	@RollbackOn({EstoqueException.class,HibernateException.class})
	public Estoque insereQuantidadeExataDoEstoque(Estoque estoqueProduto,BigDecimal quantidade,Long empresa, Long filial, boolean fiscal, boolean geral) throws EstoqueException,HibernateException {
		if (quantidade.compareTo(new BigDecimal("0"))>-1) {
			if (estoqueProduto.getNcmEstoque() != null && fiscal && geral && estoqueProduto.getBarrasEstoque() != null) {
				estoqueProduto.getNcmEstoque().setEstoque(quantidade);		
				estoqueProduto.getBarrasEstoque().setQuantidadeInventarioAnterior(estoqueProduto.getBarrasEstoque().getTotalEstoque());
				estoqueProduto.getBarrasEstoque().setTotalEstoque(quantidade);				
				estoqueProduto.getBarrasEstoque().setDataInventario(LocalDate.now());
			}else 
			if  (estoqueProduto.getNcmEstoque() != null && fiscal && geral == false) {
				estoqueProduto.getNcmEstoque().setEstoque(quantidade);	
			}else
			if ( fiscal == false && geral && estoqueProduto.getBarrasEstoque() != null ) {
				estoqueProduto.getBarrasEstoque().setQuantidadeInventarioAnterior(estoqueProduto.getBarrasEstoque().getTotalEstoque());
				estoqueProduto.getBarrasEstoque().setDataInventario(LocalDate.now());
				estoqueProduto.getBarrasEstoque().setTotalEstoque(quantidade);
			}else {
				throw new EstoqueException("estoqueException.geral.estoqueNULL");
			}
			return estoqueProduto;
		}else {
			throw new EstoqueException("estoqueException.geral.quantidadeNegativa");
		}
	}
	
	@RollbackOn({EstoqueRuntimeException.class})
	public ProdutoCusto calculaCustoMedioTransferencia (
	        ItemPedido itemEstoque,
	        BigDecimal custoAtual,
	        ProdutoCusto custoItem,
	        Long idEmpresa,
	        Long idFilial,
	        boolean custoMedioEmp) throws EstoqueRuntimeException {

	    validarCustoAtual(itemEstoque.getProduto(),custoAtual);

	    ProdutoCusto custo = custoItem != null ? custoItem : new ProdutoCusto();

	    BigDecimal quantidadeEstoqueAntigo = estoqueTotalAnteriorPorProduto(
	        itemEstoque.getProduto(), idEmpresa, idFilial);

	    BigDecimal custoAntigo = determinarCustoAntigo(itemEstoque.getProduto(),custo, custoAtual, custoMedioEmp);
	    BigDecimal valorTotalEstoqueAnterior = custoAntigo.multiply(quantidadeEstoqueAntigo, mc)
	        .setScale(2, RoundingMode.HALF_EVEN);

	    BigDecimal quantidadeRecebidaHoje = itemEstoque.getQuantidade();
	    BigDecimal valorTotalRecebidoHoje = custoAtual.multiply(quantidadeRecebidaHoje, mc)
	        .setScale(2, RoundingMode.HALF_EVEN);

	    System.out.println("estoque Antigo: " +quantidadeEstoqueAntigo+ "estoque recebido " +quantidadeRecebidaHoje+ "Total antigo " +valorTotalEstoqueAnterior
	    + "Total recebido " +valorTotalRecebidoHoje+ "Custo atual: " + custoAtual);
	    
	    BigDecimal custoMedio = calcularCustoMedio(
	        valorTotalEstoqueAnterior, valorTotalRecebidoHoje,
	        quantidadeEstoqueAntigo, quantidadeRecebidaHoje, custoAtual);

	    custo.setCustoMedio(custoMedio);
	    custo.setCustoAnterior(custoAntigo);
	    custo.setCusto(custoAtual);

	    return custo;
	}
	
	@RollbackOn({EstoqueRuntimeException.class})
	private void validarCustoAtual(Produto produto,BigDecimal custoAtual)throws EstoqueRuntimeException {
	    if (custoAtual == null || custoAtual.compareTo(BigDecimal.ZERO) <= 0) {
	        throw new EstoqueRuntimeException(this.translator.translate("estoqueException.custo.zero")+" - Ref: " +produto.getReferencia());
	    }
	}
	
	@RollbackOn({EstoqueRuntimeException.class})
	public ProdutoCusto atualizaCustoComPreco(ProdutoCusto destino, ProdutoCusto origem,Produto produto)throws EstoqueRuntimeException {
		
		validaCustoOrigem(origem,produto);
		
	    if (destino == null) destino = new ProdutoCusto();

	    destino.setCusto(origem.getCusto());
	    destino.setPreco1(origem.getPreco1());
	    destino.setPreco2(origem.getPreco2());
	    destino.setPreco3(origem.getPreco3());
	    destino.setPreco4(origem.getPreco4());
	    destino.setPreco5(origem.getPreco5());

	    if (destino.getCustoAnterior() == null) destino.setCustoAnterior(origem.getCustoAnterior());
	    if (destino.getCustoLiquido() == null) destino.setCustoLiquido(origem.getCustoLiquido());
	    if (destino.getCustoMedio() == null) destino.setCustoMedio(origem.getCustoMedio());
	    if (destino.getProduto() == null) destino.setProduto(origem.getProduto());

	    return destino;
	}
	
	@RollbackOn({EstoqueRuntimeException.class})
	public ProdutoCusto atualizaCustoSemPreco(ProdutoCusto destino, ProdutoCusto origem,Produto produto)throws EstoqueRuntimeException{
		
		validaCustoOrigem(origem,produto);
	    if (destino == null) destino = new ProdutoCusto();

	    destino.setCusto(origem.getCusto());
	    if (isZero(destino.getPreco1())) destino.setPreco1(origem.getPreco1());
	    if (isZero(destino.getPreco2())) destino.setPreco2(origem.getPreco2());
	    if (isZero(destino.getPreco3())) destino.setPreco3(origem.getPreco3());
	    if (isZero(destino.getPreco4())) destino.setPreco4(origem.getPreco4());
	    if (isZero(destino.getPreco5())) destino.setPreco5(origem.getPreco5());
	    if (isZero(destino.getCustoAnterior())) destino.setCustoAnterior(origem.getCustoAnterior());
	    if (isZero(destino.getCustoLiquido())) destino.setCustoLiquido(origem.getCustoLiquido());
	    if (isZero(destino.getCustoMedio())) destino.setCustoMedio(origem.getCustoMedio());
	    if (destino.getProduto() == null) destino.setProduto(origem.getProduto());

	    return destino;
	}

	private boolean isZero(BigDecimal valor) {
	    return valor == null || valor.compareTo(BigDecimal.ZERO) <= 0;
	}
	
	@RollbackOn({EstoqueRuntimeException.class})
	private BigDecimal determinarCustoAntigo(Produto produto, ProdutoCusto custo, BigDecimal custoAtual, boolean custoMedioEmp) throws EstoqueRuntimeException {
			
	    if (custoMedioEmp) {
	    	
	    	if (custo.getCustoMedio().compareTo(BigDecimal.ZERO) > 0) {
		        return custo.getCustoMedio();
		    }

		    if (custo.getCustoAnterior().compareTo(BigDecimal.ZERO) > 0) {
		        return custo.getCustoAnterior();
		    }
	    	
	        if (custo.getCusto().compareTo(BigDecimal.ZERO) > 0) {
	            return custo.getCusto();
	        }
	    } else {
	    	if (custo.getCusto().compareTo(BigDecimal.ZERO) > 0) {
	            return custo.getCusto();
	    	}
	    }
	    throw new EstoqueRuntimeException(this.translator.translate("estoqueException.custo.zero")+ " - Ref: " + produto.getReferencia());
	}

	private BigDecimal calcularCustoMedio(
	        BigDecimal valorAntigo,
	        BigDecimal valorNovo,
	        BigDecimal qtdAntiga,
	        BigDecimal qtdNova,
	        BigDecimal custoAtual) {

	    if (qtdAntiga.compareTo(BigDecimal.ZERO) <= 0) {
	        return custoAtual;
	    }

	    BigDecimal totalValor = valorAntigo.add(valorNovo, mc).setScale(2, RoundingMode.HALF_EVEN);
	    BigDecimal totalQuantidade = qtdAntiga.add(qtdNova, mc);

	    return totalValor.divide(totalQuantidade, mc).setScale(2, RoundingMode.HALF_EVEN);
	}
	
	/**
	 * Método que pega o custo no destino, caso não possua, gera EstoqueRuntimeException para executar o rollback automático! obs. para o Origem, o custo não pode 
	 * retornar nulo! 
	 * Método para ser usado na Transferencia entre filiais.
	 * @param itemTemp
	 * @param idEmpresa
	 * @param idFilial
	 * @return
	 * @throws EstoqueRuntimeException
	 * @throws HibernateException
	 */
	
	@RollbackOn({HibernateException.class})
	public ProdutoCusto pegaCustoOrigemTransferencia(Produto itemTemp, Long idEmpresa, Long idFilial)throws HibernateException {
		ProdutoCusto custoOrigem  = this.custoDao.pegaCustoProdutoPorIdTransferencia(itemTemp, idEmpresa, idFilial);

        return custoOrigem;
        
	}
	
	@RollbackOn({EstoqueRuntimeException.class})
	public void validaCustoOrigem(ProdutoCusto origem,Produto produto)throws EstoqueRuntimeException {
		if (origem == null) {
			throw new EstoqueRuntimeException(this.translator.translate("estoqueException.custo.zero")+" - Ref: " +produto.getReferencia());
        }
	}

	/**
	 * Método que pega o custo no destino, caso não possua, retorna um custo zerado! obs. para o destino, o custo pode retornar nulo nesse caso
	 * Método para ser usado na Transferencia entre filiais.
	 * @param itemTemp
	 * @param idEmpresa
	 * @param idFilial
	 * @return
	 * @throws EstoqueRuntimeException
	 * @throws HibernateException
	 */
	@RollbackOn({HibernateException.class})
	public ProdutoCusto pegaCustoDestinoTransferencia(Produto itemTemp, Long idEmpresa, Long idFilial)throws HibernateException {
		ProdutoCusto custoOrigem  = this.custoDao.pegaCustoProdutoPorIdTransferencia(itemTemp, idEmpresa, idFilial);

        if (custoOrigem == null) {
        	custoOrigem = new ProdutoCusto();
        }
        return custoOrigem;
        
	}
}
