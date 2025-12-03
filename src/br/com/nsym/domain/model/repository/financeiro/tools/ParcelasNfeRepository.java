package br.com.nsym.domain.model.repository.financeiro.tools;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityGraph;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.transform.ResultTransformer;

import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.model.entity.financeiro.AgPedido;
import br.com.nsym.domain.model.entity.financeiro.AgTitulo;
import br.com.nsym.domain.model.entity.financeiro.ParcelaStatus;
import br.com.nsym.domain.model.entity.financeiro.Enum.TipoLancamento;
import br.com.nsym.domain.model.entity.financeiro.tools.AgendaDTO;
import br.com.nsym.domain.model.entity.financeiro.tools.ParcelasNfe;
import br.com.nsym.domain.model.entity.financeiro.tools.TotalizadorFinanceiro;
import br.com.nsym.domain.model.entity.fiscal.Cfe.CFe;
import br.com.nsym.domain.model.entity.fiscal.nfe.Nfe;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoPesquisa;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class ParcelasNfeRepository extends GenericRepositoryEmpDS<ParcelasNfe, Long> {

	/**
	 *
	 */
	private static final long serialVersionUID = 2497572091611073054L;

	public List<ParcelasNfe> listaParcelasPorNfe(Nfe nfe,Long idEmpresa,Long idFilial){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ParcelasNfe> criteria = builder.createQuery(ParcelasNfe.class);
		

		Root<ParcelasNfe> formPedido = criteria.from(ParcelasNfe.class);
		
		List<Predicate> conditions = new ArrayList<>();

		Predicate itemPedido = builder.equal(formPedido.get("nfe"),nfe);

		conditions.add(itemPedido);
		conditions.add(builder.equal(formPedido.get("isDeleted"),false));
		if (idEmpresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
			if (idFilial == null){
				conditions.add(builder.equal(formPedido.get("idEmpresa"),idEmpresa));
				conditions.add(builder.isNull(formPedido.get("idFilial")));
			}else{
				conditions.add(builder.equal(formPedido.get("idEmpresa"),idEmpresa));
				conditions.add(builder.equal(formPedido.get("idFilial"),idFilial));
			}
		}


		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<ParcelasNfe> typedQuery = this.getEntityManager().createQuery(criteria);
		return typedQuery.getResultList();
	}
	/**
	 * Lista as parcelas por agPedido
	 * @param agpedido
	 * @return lista das parcelas
	 */
	public List<ParcelasNfe> listaParcelasPorAgPedido(AgPedido agpedido){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ParcelasNfe> criteria = builder.createQuery(ParcelasNfe.class);
		

		Root<ParcelasNfe> formPedido = criteria.from(ParcelasNfe.class);

		List<Predicate> conditions = new ArrayList<>();

		Predicate itemPedido = builder.equal(formPedido.get("agPedido"),agpedido);

		conditions.add(itemPedido);
		conditions.add(builder.equal(formPedido.get("isDeleted"),false));

		//criteria.select(formPedido.alias("cf"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<ParcelasNfe> typedQuery = this.getEntityManager().createQuery(criteria);
		return typedQuery.getResultList();
	}
	
	public List<ParcelasNfe> listaParcelasPorCFe(CFe cfe,Long idEmpresa,Long idFilial){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ParcelasNfe> criteria = builder.createQuery(ParcelasNfe.class);
		

		Root<ParcelasNfe> formPedido = criteria.from(ParcelasNfe.class);

		List<Predicate> conditions = new ArrayList<>();

		Predicate itemPedido = builder.equal(formPedido.get("cfe"),cfe);

		conditions.add(itemPedido);
		conditions.add(builder.equal(formPedido.get("isDeleted"),true));
		if (idEmpresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
			if (idFilial == null){
				conditions.add(builder.equal(formPedido.get("idEmpresa"),idEmpresa));
				conditions.add(builder.isNull(formPedido.get("idFilial")));
			}else{
				conditions.add(builder.equal(formPedido.get("idEmpresa"),idEmpresa));
				conditions.add(builder.equal(formPedido.get("idFilial"),idFilial));
			}
		}

		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<ParcelasNfe> typedQuery = this.getEntityManager().createQuery(criteria);
		return typedQuery.getResultList();
	}
	/**
	 * Lista de de titulos (conta a pagar e receber)
	 * 
	 * @param isDeleted - Caso True - Exibe títulos removidos
	 * @param dataIni - Período inicial da consulta
	 * @param dataFim - Perídodo final da consulta	
	 * @param idEmpresa - id da matriz
	 * @param idFilial - id da filial 
	 * @param id - Identificação do titulo
	 * @param pageRequest
	 * @param filtro
	 * @param pesquisa
	 * @param porFilial - Caso False (Exibe Global)
	 * 					- Caso True (exibe as informações da empresa solicitante)
	 * @param tipoResultado - Caso False ( Exibe apenas titulos a RECEBER) 
	 * 						- Caso True ( Exibe apenas titulos a pagar)
	 * 						- Caso NULL ( EXIBE ambos)
	 * @param lancamento - Informar 
	 * 							-Todos (para exibir tanto A Pagar quanto A Receber) 
	 * 							-Credito (Exibi apenas títulos A Receber)
	 * 							-Débito (Exibi apenas títulos A pagar)
	 * 								
	 * @return
	 */
	public Page<ParcelasNfe> listaParcelasLazyComFiltro(Boolean isDeleted,LocalDate dataIni,LocalDate dataFim, Long idEmpresa, Long idFilial, PageRequest pageRequest,String filtro , String pesquisa,Boolean porFilial,Boolean tipoResultado,TipoLancamento lancamento ){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ParcelasNfe> criteria = builder.createQuery(ParcelasNfe.class);

		Root<ParcelasNfe> fromParcelas = criteria.from(ParcelasNfe.class);


		List<Predicate> conditions = new ArrayList<>();
		Predicate filialNull = builder.isNull(fromParcelas.get("idFilial"));
		Predicate empresaNull = builder.isNull(fromParcelas.get("idEmpresa"));

		conditions.add(builder.equal(fromParcelas.get("isDeleted"), isDeleted));
		conditions.add(builder.notEqual(fromParcelas.get("status"),ParcelaStatus.NAO));
//		conditions.add(builder.isNotNull(fromParcelas.get("status")));
		Predicate itemPedido = builder.between(fromParcelas.get("vencimento"),dataIni,dataFim);
		conditions.add(itemPedido);
		if (lancamento != TipoLancamento.tpAll) {
			conditions.add(builder.equal(fromParcelas.get("tipoLancamento"), lancamento));
		}else {
			conditions.add(builder.isNotNull(fromParcelas.get("tipoLancamento")));
		}
			
		if (idFilial == null){
			if (idEmpresa == null){
				conditions.add(empresaNull);
			}else{
				if (porFilial) {
					conditions.add(builder.equal(fromParcelas.get("idEmpresa"), idEmpresa));
					conditions.add(filialNull);
				}else {
					conditions.add(builder.equal(fromParcelas.get("idEmpresa"), idEmpresa));
				}
			}
		}else{
			if (porFilial) {
				conditions.add(builder.equal(fromParcelas.get("idEmpresa"), idEmpresa));
				conditions.add(builder.equal(fromParcelas.get("idFilial"), idFilial));
			}else {
				conditions.add(builder.equal(fromParcelas.get("idEmpresa"), idEmpresa));
				}
		}
		
		if (filtro != null){
			if (!filtro.isEmpty()){
				conditions.add(builder.like(builder.lower(fromParcelas.get(filtro)),"%"+pesquisa.toLowerCase()+"%"));
			}
		}

		// projetamos para pegar o total de paginas possiveis

		CriteriaQuery<Long> cq = builder.createQuery(Long.class);
		cq.select(builder.count(cq.from(getPersistentClass())));
//		this.getEntityManager().createQuery(cq);
		cq.where(conditions.toArray(new Predicate[0]));

		final Long totalRows = this.getEntityManager().createQuery(cq).getSingleResult();

		criteria.select(fromParcelas.alias("parc"));
		criteria.where(conditions.toArray(new Predicate[]{}));


		if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
			criteria.orderBy(builder.asc(fromParcelas.<String>get(pageRequest.getSortField())));

		} else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
			criteria.orderBy(builder.desc(fromParcelas.<String>get(pageRequest.getSortField())));

		}
		criteria.distinct(true);
		TypedQuery<ParcelasNfe> typedQuery = this.getEntityManager().createQuery(criteria);
//		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
//		typedQuery.setHint("jakarta.persistence.fetchgraph", entityGraph);
		
		// paginamos
		typedQuery.setFirstResult(pageRequest.getFirstResult());
		typedQuery.setMaxResults(pageRequest.getPageSize());
		
		// montamos o resultado paginado
		return new Page<>(typedQuery.getResultList(), totalRows);
	}
	/**
	 * 
	 * @param isDeleted
	 * @param dataIni
	 * @param dataFim
	 * @param idEmpresa
	 * @param idFilial
	 * @param pageRequest
	 * @param tipoPesquisa
	 * @param cadastro
	 * @param lancamento
	 * @param parcStatus 
	 * 			null = todos 
	 * 			false = baixados
	 * 			true = aguardando baixa
	 * @param porFilial
	 * @return
	 */
	public Page<ParcelasNfe> listaParcelasPorCliente(Boolean isDeleted,LocalDate dataIni,LocalDate dataFim,Long idEmpresa, Long idFilial,PageRequest pageRequest,TipoPesquisa tipoPesquisa,Long cadastro,TipoLancamento lancamento,Boolean parcStatus,boolean porFilial){
		EntityGraph<ParcelasNfe> entityGraph = this.getEntityManager().createEntityGraph(ParcelasNfe.class); //TabelaListaPedidosRecebidos
		entityGraph.addSubgraph("agPedido",AgPedido.class).addAttributeNodes("listaParcelas");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ParcelasNfe> criteria = builder.createQuery(ParcelasNfe.class);

		Root<ParcelasNfe> fromParcelas = criteria.from(ParcelasNfe.class);
		From<?,?> agPedidoJoin = fromParcelas.join("agPedido",JoinType.INNER);
		From<?,?> destinatarioJoin = agPedidoJoin.join("destinatario",JoinType.INNER);

		List<Predicate> conditions = new ArrayList<>();
		Predicate filialNull = builder.isNull(fromParcelas.get("idFilial"));
		Predicate empresaNull = builder.isNull(fromParcelas.get("idEmpresa"));

		conditions.add(builder.equal(fromParcelas.get("isDeleted"), isDeleted));
		conditions.add(builder.isNotNull(fromParcelas.get("tipoLancamento")));
		conditions.add(builder.isNotNull(fromParcelas.get("status")));
		if (tipoPesquisa == TipoPesquisa.CLI) {
			conditions.add(builder.equal(destinatarioJoin.get("cliente"), cadastro));
		}
		if (tipoPesquisa == TipoPesquisa.COL) {
			conditions.add(builder.equal(destinatarioJoin.get("colaborador"), cadastro));
		}
		if (tipoPesquisa == TipoPesquisa.FOR) {
			conditions.add(builder.equal(destinatarioJoin.get("fornecedor"), cadastro));
		}
		if (lancamento != TipoLancamento.tpAll) {
			conditions.add(builder.equal(fromParcelas.get("tipoLancamento"), lancamento));
		}
		if (parcStatus != null) {
			if (parcStatus == false) {
				conditions.add(builder.equal(fromParcelas.get("status"), ParcelaStatus.REC));
			}else {
				conditions.add(builder.notEqual(fromParcelas.get("status"), ParcelaStatus.REC));
				conditions.add(builder.notEqual(fromParcelas.get("status"),ParcelaStatus.NAO));
				conditions.add(builder.notEqual(fromParcelas.get("status"),ParcelaStatus.AGR));
			}
		}
		Predicate itemPedido = builder.between(fromParcelas.get("vencimento"),dataIni,dataFim);
		conditions.add(itemPedido);

		if (idFilial == null){
			if (idEmpresa == null){
				conditions.add(empresaNull);
			}else{
				if (porFilial) {
					conditions.add(builder.equal(fromParcelas.get("idEmpresa"), idEmpresa));
					conditions.add(filialNull);
				}else {
					conditions.add(builder.equal(fromParcelas.get("idEmpresa"), idEmpresa));
				}
			}
		}else{
			if (porFilial) {
				conditions.add(builder.equal(fromParcelas.get("idEmpresa"), idEmpresa));
				conditions.add(builder.equal(fromParcelas.get("idFilial"), idFilial));
			}else {
				conditions.add(builder.equal(fromParcelas.get("idEmpresa"), idEmpresa));
			}
		}

		criteria.select(fromParcelas.alias("parc"));
		criteria.where(conditions.toArray(new Predicate[]{}));


		if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
			criteria.orderBy(builder.asc(fromParcelas.<String>get(pageRequest.getSortField())));

		} else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
			criteria.orderBy(builder.desc(fromParcelas.<String>get(pageRequest.getSortField())));

		}
		criteria.distinct(true);
		TypedQuery<ParcelasNfe> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("jakarta.persistence.fetchgraph", entityGraph);

		// paginamos
		typedQuery.setFirstResult(pageRequest.getFirstResult());
		typedQuery.setMaxResults(pageRequest.getPageSize());

		final Long totalRows = new BigDecimal(typedQuery.getResultList().size()).longValue();
		// montamos o resultado paginado
		return new Page<>(typedQuery.getResultList(), totalRows);
	}
	
	public ParcelasNfe pegaParcela(Long idEmpresa,Long idFilial, Long idParcela){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ParcelasNfe> criteria = builder.createQuery(ParcelasNfe.class);
		

		Root<ParcelasNfe> formPedido = criteria.from(ParcelasNfe.class);

		List<Predicate> conditions = new ArrayList<>();

		Predicate itemPedido = builder.equal(formPedido.get("id"),idParcela);

		conditions.add(itemPedido);
		conditions.add(builder.equal(formPedido.get("isDeleted"),false));
		if (idEmpresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
			if (idFilial == null){
				conditions.add(builder.equal(formPedido.get("idEmpresa"),idEmpresa));
				conditions.add(builder.isNull(formPedido.get("idFilial")));
			}else{
				conditions.add(builder.equal(formPedido.get("idEmpresa"),idEmpresa));
				conditions.add(builder.equal(formPedido.get("idFilial"),idFilial));
			}
		}
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<ParcelasNfe> typedQuery = this.getEntityManager().createQuery(criteria);
		return typedQuery.getSingleResult();
		
	}
	
	public TotalizadorFinanceiro geraTotaisAgenda(Boolean isDeleted,LocalDate dataIni,LocalDate dataFim, Long idEmpresa, Long idFilial,Boolean porFilial,TipoLancamento lancamento) {
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ParcelasNfe> criteria = builder.createQuery(ParcelasNfe.class);

		Root<ParcelasNfe> fromParcelas = criteria.from(ParcelasNfe.class);


		List<Predicate> conditions = new ArrayList<>();
		Predicate filialNull = builder.isNull(fromParcelas.get("idFilial"));
		Predicate empresaNull = builder.isNull(fromParcelas.get("idEmpresa"));
		Predicate lancNotNull = builder.isNotNull(fromParcelas.get("tipoLancamento"));
		
		conditions.add(builder.equal(fromParcelas.get("isDeleted"), isDeleted));
		conditions.add(builder.notEqual(fromParcelas.get("status"),ParcelaStatus.NAO));
		conditions.add(builder.isNotNull(fromParcelas.get("status")));
		Predicate itemPedido = builder.between(fromParcelas.get("vencimento"),dataIni,dataFim);
		conditions.add(itemPedido);
		
		if (lancamento != TipoLancamento.tpAll) {
			conditions.add(builder.equal(fromParcelas.get("tipoLancamento"), lancamento));
		}
			
		if (idFilial == null){
			if (idEmpresa == null){
				conditions.add(empresaNull);
			}else{
				if (porFilial) {
					conditions.add(builder.equal(fromParcelas.get("idEmpresa"), idEmpresa));
					conditions.add(filialNull);
				}else {
					conditions.add(builder.equal(fromParcelas.get("idEmpresa"), idEmpresa));
				}
			}
		}else{
			if (porFilial) {
				conditions.add(builder.equal(fromParcelas.get("idEmpresa"), idEmpresa));
				conditions.add(builder.equal(fromParcelas.get("idFilial"), idFilial));
			}else {
				conditions.add(builder.equal(fromParcelas.get("idEmpresa"), idEmpresa));
				}
		}
		
		conditions.add(lancNotNull);

		criteria.select(fromParcelas.alias("p"));
		criteria.where(conditions.toArray(new Predicate[0]));
		
		criteria.distinct(true);
		TypedQuery<ParcelasNfe> typedQuery = this.getEntityManager().createQuery(criteria);
		
		List<ParcelasNfe> listaTemp = typedQuery.getResultList();
		TotalizadorFinanceiro tot = new TotalizadorFinanceiro();
		System.out.println("Tamanho da lista " + listaTemp.size());
		for (ParcelasNfe parc : listaTemp) {
			System.out.println("Valor da parcela " + parc.getValorParcela());
			if (parc.getTipoLancamento().equals(TipoLancamento.tpCredito)) {
				if (parc.getValorOriginal() != null) {
					if (parc.getStatus().equals(ParcelaStatus.REC) || parc.getStatus().equals(ParcelaStatus.PAR)) {
						if (parc.getValorRecebido().compareTo(new BigDecimal("0"))>0) {
							tot.setTotalCreditoRec(tot.getTotalCreditoRec().add(parc.getValorRecebido()));
						}else {
							tot.setTotalCreditoRec(tot.getTotalCreditoRec().add(parc.getValorParcela()));
						}
					}
					if (parc.getValorOriginal().compareTo(new BigDecimal("0"))>0) {
						tot.setTotalCredito(tot.getTotalCredito().add(parc.getValorOriginal()));
					}else {
						tot.setTotalCredito(tot.getTotalCredito().add(parc.getValorParcela()));
					}
				}else {
					tot.setTotalCredito(tot.getTotalCredito().add(parc.getValorParcela()));
				}
			}else {
				if (parc.getValorOriginal() != null) { 
					if (parc.getStatus().equals(ParcelaStatus.REC) || parc.getStatus().equals(ParcelaStatus.PAR)) {
						if (parc.getValorOriginal().compareTo(new BigDecimal("0"))>0) {
							tot.setTotalDebitoPag(tot.getTotalDebitoPag().add(parc.getValorRecebido()));
						}else {
							tot.setTotalDebitoPag(tot.getTotalDebitoPag().add(parc.getValorParcela()));
						}
					}
					if (parc.getValorOriginal().compareTo(new BigDecimal("0"))>0) {
						tot.setTotalDebito(tot.getTotalDebito().add(parc.getValorOriginal()));
					}else {
						tot.setTotalDebito(tot.getTotalDebito().add(parc.getValorParcela()));
					}
				}else {
					tot.setTotalDebito(tot.getTotalDebito().add(parc.getValorParcela()));
				}
			}
		}
		tot.setTotalCreditoLiq(tot.getTotalCredito().subtract(tot.getTotalCreditoRec()));
		tot.setTotalDebitoLiq(tot.getTotalDebito().subtract(tot.getTotalDebitoPag()));
		return tot;
	}
	
	/**
	 * Lista de de titulos (conta a pagar e receber)
	 * 
	 * @param isDeleted - Caso True - Exibe títulos removidos
	 * @param dataIni - Período inicial da consulta
	 * @param dataFim - Perídodo final da consulta	
	 * @param idEmpresa - id da matriz
	 * @param idFilial - id da filial 
	 * @param id - Identificação do titulo
	 * @param pageRequest
	 * @param filtro
	 * @param pesquisa
	 * @param porFilial - Caso False (Exibe Global)
	 * 					- Caso True (exibe as informações da empresa solicitante)
	 * @param tipoResultado - Caso False ( Exibe apenas titulos a RECEBER) 
	 * 						- Caso True ( Exibe apenas titulos a pagar)
	 * 						- Caso NULL ( EXIBE ambos)
	 * @param lancamento - Informar 
	 * 							-Todos (para exibir tanto A Pagar quanto A Receber) 
	 * 							-Credito (Exibi apenas títulos A Receber)
	 * 							-Débito (Exibi apenas títulos A pagar)
	 * 								
	 * @return
	 */
	public Page<ParcelasNfe> listaParcelasLazyComFiltroGerir(Boolean isDeleted,LocalDate dataIni,LocalDate dataFim, Long idEmpresa, Long idFilial, Long id, PageRequest pageRequest,String filtro , String pesquisa,Boolean porFilial,Boolean tipoResultado,TipoLancamento lancamento ){
		EntityGraph<ParcelasNfe> entityGraph = this.getEntityManager().createEntityGraph(ParcelasNfe.class); //TabelaListaPedidosRecebidos
		entityGraph.addSubgraph("listaAgTitulo",AgTitulo.class).addAttributeNodes("listaTitulosAgrupados");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ParcelasNfe> criteria = builder.createQuery(ParcelasNfe.class);

		Root<ParcelasNfe> fromParcelas = criteria.from(ParcelasNfe.class);


		List<Predicate> conditions = new ArrayList<>();
		Predicate filialNull = builder.isNull(fromParcelas.get("idFilial"));
		Predicate empresaNull = builder.isNull(fromParcelas.get("idEmpresa"));

		conditions.add(builder.equal(fromParcelas.get("isDeleted"), isDeleted));
		conditions.add(builder.isNotNull(fromParcelas.get("tipoLancamento")));
		conditions.add(builder.notEqual(fromParcelas.get("status"), ParcelaStatus.REC));
		conditions.add(builder.notEqual(fromParcelas.get("status"),ParcelaStatus.NAO));
		conditions.add(builder.notEqual(fromParcelas.get("status"),ParcelaStatus.AGR));
		conditions.add(builder.isNotNull(fromParcelas.get("status")));
		Predicate itemPedido = builder.between(fromParcelas.get("vencimento"),dataIni,dataFim);
		conditions.add(itemPedido);
		if (lancamento != TipoLancamento.tpAll) {
			conditions.add(builder.equal(fromParcelas.get("tipoLancamento"), lancamento));
		}
			
		if (idFilial == null){
			if (idEmpresa == null){
				conditions.add(empresaNull);
			}else{
				if (porFilial) {
					conditions.add(builder.equal(fromParcelas.get("idEmpresa"), idEmpresa));
					conditions.add(filialNull);
				}else {
					conditions.add(builder.equal(fromParcelas.get("idEmpresa"), idEmpresa));
				}
			}
		}else{
			if (porFilial) {
				conditions.add(builder.equal(fromParcelas.get("idEmpresa"), idEmpresa));
				conditions.add(builder.equal(fromParcelas.get("idFilial"), idFilial));
			}else {
				conditions.add(builder.equal(fromParcelas.get("idEmpresa"), idEmpresa));
				}
		}
		
		if (filtro != null){
			if (!filtro.isEmpty()){
				conditions.add(builder.like(builder.lower(fromParcelas.get(filtro)),"%"+pesquisa.toLowerCase()+"%"));
			}
		}

		// projetamos para pegar o total de paginas possiveis

		CriteriaQuery<Long> cq = builder.createQuery(Long.class);
		cq.select(builder.count(cq.from(getPersistentClass())));
		this.getEntityManager().createQuery(cq);
		cq.where(conditions.toArray(new Predicate[0]));

		final Long totalRows = this.getEntityManager().createQuery(cq).getSingleResult();

		criteria.select(fromParcelas.alias("parc"));
		criteria.where(conditions.toArray(new Predicate[]{}));


		if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
			criteria.orderBy(builder.asc(fromParcelas.<String>get(pageRequest.getSortField())));

		} else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
			criteria.orderBy(builder.desc(fromParcelas.<String>get(pageRequest.getSortField())));

		}
		criteria.distinct(true);
		TypedQuery<ParcelasNfe> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("jakarta.persistence.fetchgraph", entityGraph);
		
		// paginamos
		typedQuery.setFirstResult(pageRequest.getFirstResult());
		typedQuery.setMaxResults(pageRequest.getPageSize());

		// montamos o resultado paginado
		return new Page<>(typedQuery.getResultList(), totalRows);
	}
	
	public List<ParcelasNfe> listaParcelasComFiltro(Boolean isDeleted,LocalDate dataIni,LocalDate dataFim, Long idEmpresa, Long idFilial, Long id, PageRequest pageRequest,String filtro , String pesquisa,Boolean porFilial,Boolean tipoResultado,TipoLancamento lancamento ){
		EntityGraph<ParcelasNfe> entityGraph = this.getEntityManager().createEntityGraph(ParcelasNfe.class); //TabelaListaPedidosRecebidos
		entityGraph.addSubgraph("agPedido",AgPedido.class).addAttributeNodes("listaParcelas");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ParcelasNfe> criteria = builder.createQuery(ParcelasNfe.class);

		Root<ParcelasNfe> fromParcelas = criteria.from(ParcelasNfe.class);


		List<Predicate> conditions = new ArrayList<>();
		Predicate filialNull = builder.isNull(fromParcelas.get("idFilial"));
		Predicate empresaNull = builder.isNull(fromParcelas.get("idEmpresa"));

		conditions.add(builder.equal(fromParcelas.get("isDeleted"), isDeleted));
		conditions.add(builder.isNotNull(fromParcelas.get("tipoLancamento")));
		conditions.add(builder.notEqual(fromParcelas.get("status"),ParcelaStatus.NAO));
		conditions.add(builder.isNotNull(fromParcelas.get("status")));
		Predicate itemPedido = builder.between(fromParcelas.get("vencimento"),dataIni,dataFim);
		conditions.add(itemPedido);
		if (lancamento != TipoLancamento.tpAll) {
			conditions.add(builder.equal(fromParcelas.get("tipoLancamento"), lancamento));
		}
			
		if (idFilial == null){
			if (idEmpresa == null){
				conditions.add(empresaNull);
			}else{
				if (porFilial) {
					conditions.add(builder.equal(fromParcelas.get("idEmpresa"), idEmpresa));
					conditions.add(filialNull);
				}else {
					conditions.add(builder.equal(fromParcelas.get("idEmpresa"), idEmpresa));
				}
			}
		}else{
			if (porFilial) {
				conditions.add(builder.equal(fromParcelas.get("idEmpresa"), idEmpresa));
				conditions.add(builder.equal(fromParcelas.get("idFilial"), idFilial));
			}else {
				conditions.add(builder.equal(fromParcelas.get("idEmpresa"), idEmpresa));
				}
		}
		
		if (filtro != null){
			if (!filtro.isEmpty()){
				conditions.add(builder.like(builder.lower(fromParcelas.get(filtro)),"%"+pesquisa.toLowerCase()+"%"));
			}
		}

		criteria.select(fromParcelas.alias("parc"));
		criteria.where(conditions.toArray(new Predicate[]{}));

		criteria.distinct(true);
		TypedQuery<ParcelasNfe> typedQuery = this.getEntityManager().createQuery(criteria);
//		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		typedQuery.setHint("jakarta.persistence.fetchgraph", entityGraph);

		// montamos o resultado paginado
		return typedQuery.getResultList();
	}
	
	
	public List<AgendaDTO> parcelasLazyComFiltro(Boolean isDeleted,LocalDate dataIni,LocalDate dataFim, Long idEmpresa, Long idFilial, Long id, PageRequest pageRequest,String filtro , String pesquisa,Boolean porFilial,Boolean tipoResultado,TipoLancamento lancamento,ParcelaStatus parcelaStatus,String procuraOrigem,boolean dpFiltro ){
		String sql = "select parc.id as idParc ,parc.controle, parc.numParcela,parc.valorParcela,parc.vencimento,parc.status,parc.valorRecebido, "
				+ " parc.dataRecebimento,parc.tipoLancamento,parc.tipoPagamento,parc.qRecorrencia,parc.valorCobrado,parc.valorOriginal,"
				+ " if (parc.CaixaGT_ID is null, if (parc.id_NFE is null,if (parc.NFeRec_id is null,if (parc.qRecorrencia = 0,1,parc.qRecorrencia),(select count(*) from parcelasNfe parNfe where parc.NFeRec_id = parNfe.NFeRec_id )),(select count(*) from parcelasNfe pNfe where parc.id_NFE = pNfe.id_NFE )) , "
				+ " (select count(*) from TabelaListaParcelamento ag where ag.AgPedido_ID = parc.CaixaGT_ID )) as qParcelas, "
				+ " if (parc.descricao is null,if (parc.id_NFE is null, if(parc.CaixaGT_ID is null,if(parc.NfeRec_id is null ,parc.descricao,if(emi.Filial is null,if(emi.Empresa is null,if(emi.Fornecedor is null,\"Não Localizado\",forn.razaoSocial),emp.razaoSocial) ,fil.razaoSocial)), "
				+ " if (des.Cliente_Id is null, if (des.Fornecedor_id is null,if (des.Colaborador_id is null,'Não Localizado',col.nome),forn.razaoSocial),cli.razaoSocial)),if (desNFE.Cliente_Id is null, if (desNFE.Fornecedor_id is null,if (desNFE.Colaborador_id is null,'Não Localizado',colNFE.nome),fornNfe.razaoSocial),concat(cliNFE.razaoSocial,' NFE: ',(select nota.numeroNota from nfe nota where nota.id = parc.id_nfe)) )),parc.descricao) as razao, "
				+ " n.id as nfe,ag.id as agPedido,nfRec.id as nfRec, n.numeroNota as numeroNfe, nfRec.numeroNota as numeroNFeRec from parcelasNfe parc "
				+ " left join AgPedido ag on ag.id = parc.CaixaGT_ID "
				+ " left join nfe n on n.id = parc.id_NFE "
				+ " left join DestinatarioPedido des on des.id = ag.Destinatario_ID"
				+ " left join cliente cli on cli.id = des.Cliente_Id "
				+ " left join fornecedor forn on forn.id =des.Fornecedor_id "
				+ " left join colaborador col on col.id = des.Colaborador_id "
				+ " left join nfeRecebida nfRec on nfRec.id = parc.NfeRec_ID "
				+ " left join emitente emi on emi.id = nfRec.emitente_id "
				+ " left join Filial fil on fil.id = emi.Filial "
				+ " left join Empresa emp on emp.id = emi.Empresa "
				+ " left join fornecedor forne on forne.id = emi.Fornecedor "
				+ " left join Destinatario desNFE on desNFE.id = n.id "
				+ " left join fornecedor fornNfe on fornNfe.id = desNFE.Fornecedor_id "
				+ " left join cliente cliNFE on cliNFE.id = desNFE.Cliente_Id "
				+ " left join colaborador colNFE on colNFE.id = desNFE.Colaborador_id "				
				+ " where ";
		if (idEmpresa == null) {
			sql = sql + " parc.id_empresa is null '";
		}else {
			sql = sql	+ " parc.id_empresa = '" + idEmpresa+"' ";
		}
		if (idFilial == null) {
			sql = sql + " and parc.id_filial is null ";
		}else {
			sql = sql + " and parc.id_filial = '"+idFilial+"' ";
		}
		sql = sql + " and parc.vencimento between '"+dataIni+"' and '"+dataFim+"' " ;
		if (parcelaStatus == ParcelaStatus.ABE) {
			sql = sql + " and (parc.status = 'ABE' or parc.status = 'PAR') ";
		}else {
			if (parcelaStatus != ParcelaStatus.ALL) {
				sql = sql + " and parc.status = '"+parcelaStatus.getCod()+ "' ";
			}else {
				sql = sql+ " and parc.status != '"+ParcelaStatus.NAO.getCod()+"' ";
			}
		}
		if (lancamento != TipoLancamento.tpAll) {
			sql = sql + " and parc.tipoLancamento = '"+lancamento.name()+"' ";
		}
		sql = sql + " and parc.dpEspecial = " + dpFiltro ;
		if (filtro != null){
			if (!filtro.isEmpty()){
				sql = sql + " and upper(parc."+filtro+") = '"+pesquisa.toUpperCase()+"' ";
			}
		}
		if (procuraOrigem != null) {
			sql = sql + " and upper(parc.descricao) like '%"+procuraOrigem.toUpperCase()+"%' ";
		}
//		if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
			sql = sql + " order by date_format(parc.vencimento,'%Y-%m-%d') asc";

//		} else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
//			sql = sql + " order by date_format(parc.vencimento,'%Y-%m-%d') desc";
//
//		}
		@SuppressWarnings("deprecation")
		Query resultado  =  this.getEntityManager()
		.createNativeQuery(sql)
			.unwrap(org.hibernate.query.Query.class).setResultTransformer(new ResultTransformer(){

				/**
				 *
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public Object transformTuple(Object[] tuple, String[] aliases) {
					if (tuple[9] == null) {
						tuple[9]= (int)0;
					};
					if (tuple[10] == null) {
						tuple[10] = (int)0;
					}
					// TODO Auto-generated method stub
					AgendaDTO relDto = new AgendaDTO(
								(BigInteger)tuple[0],
								(BigInteger)tuple[1],
								(BigInteger)tuple[2],
								(BigDecimal)tuple[3],
								(Date)tuple[4],
								(String)tuple[5],
								(BigDecimal)tuple[6],
								(Date)tuple[7],
								(String)tuple[8],
								(int)tuple[9],
								(int)tuple[10],
								(BigDecimal)tuple[11],
								(BigDecimal)tuple[12],
								(BigInteger)tuple[13],
								(String)tuple[14],
								(BigInteger)tuple[15],
								(BigInteger)tuple[16],
								(BigInteger)tuple[17],
								(BigInteger)tuple[18],
								(BigInteger)tuple[19])
									;
						return relDto;
				}

				@SuppressWarnings("rawtypes")
				@Override
				public List transformList(List collection) {
					// TODO Auto-generated method stub
					return collection;
				}
				
			});
		@SuppressWarnings("unchecked")
		List<AgendaDTO> dto =  resultado.getResultList();
		TotalizadorFinanceiro tot = new TotalizadorFinanceiro();
		if (dto.size() > 0) {
			for (AgendaDTO parc : dto) {
				System.out.println("Valor da parcela " + parc.getValorParcela());
				tot = geraTotalizador(parc,tot);
			}
		}
		tot.setTotalCreditoLiq(tot.getTotalCredito().subtract(tot.getTotalCreditoRec()));
		tot.setTotalDebitoLiq(tot.getTotalDebito().subtract(tot.getTotalDebitoPag()));
		
		if (dto.size() > 0) {
			for (AgendaDTO agendaDTO : dto) {
				agendaDTO.setTotalizador(tot);
			}
		}
		return dto;
	}
	
	private TotalizadorFinanceiro geraTotalizador(AgendaDTO agenda,TotalizadorFinanceiro tot) {
		if (agenda.getTipoLancamento().equals(TipoLancamento.tpCredito.name())) {
			if (agenda.getValorOriginal() != null) {
				if (agenda.getStatus().equals(ParcelaStatus.REC.name()) || agenda.getStatus().equals(ParcelaStatus.PAR.name())) {
					if (agenda.getValorRecebido().compareTo(new BigDecimal("0"))>0) {
						tot.setTotalCreditoRec(tot.getTotalCreditoRec().add(agenda.getValorRecebido()));
					}else {
						tot.setTotalCreditoRec(tot.getTotalCreditoRec().add(agenda.getValorParcela()));
					}
				}
				if (agenda.getValorOriginal().compareTo(new BigDecimal("0"))>0) {
					tot.setTotalCredito(tot.getTotalCredito().add(agenda.getValorOriginal()));
				}else {
					tot.setTotalCredito(tot.getTotalCredito().add(agenda.getValorParcela()));
				}
			}else {
				tot.setTotalCredito(tot.getTotalCredito().add(agenda.getValorParcela()));
			}
		}else {
			if (agenda.getValorOriginal() != null) { 
				if (agenda.getStatus().equals(ParcelaStatus.REC.name()) || agenda.getStatus().equals(ParcelaStatus.PAR.name())) {
					if (agenda.getValorOriginal().compareTo(new BigDecimal("0"))>0) {
						tot.setTotalDebitoPag(tot.getTotalDebitoPag().add(agenda.getValorRecebido()));
					}else {
						tot.setTotalDebitoPag(tot.getTotalDebitoPag().add(agenda.getValorParcela()));
					}
				}
				if (agenda.getValorOriginal().compareTo(new BigDecimal("0"))>0) {
					tot.setTotalDebito(tot.getTotalDebito().add(agenda.getValorOriginal()));
				}else {
					tot.setTotalDebito(tot.getTotalDebito().add(agenda.getValorParcela()));
				}
			}else {
				tot.setTotalDebito(tot.getTotalDebito().add(agenda.getValorParcela()));
			}
		}
		return tot;
	}
}
