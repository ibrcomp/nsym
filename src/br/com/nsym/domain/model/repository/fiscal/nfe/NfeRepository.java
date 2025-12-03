package br.com.nsym.domain.model.repository.fiscal.nfe;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityGraph;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;

import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.model.entity.financeiro.Banco;
import br.com.nsym.domain.model.entity.fiscal.Tributos;
import br.com.nsym.domain.model.entity.fiscal.dto.RelNatOperacaoDTO;
import br.com.nsym.domain.model.entity.fiscal.nfe.Nfe;
import br.com.nsym.domain.model.entity.fiscal.tools.StatusNfe;
import br.com.nsym.domain.model.entity.tools.PedidoStatus;
import br.com.nsym.domain.model.entity.tools.PedidoTipo;
import br.com.nsym.domain.model.entity.venda.RelatorioVendasDTO;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class NfeRepository extends GenericRepositoryEmpDS<Nfe, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public List<Nfe> listNfeAtivo(Long idEmpresa , Long idFilial){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Nfe> criteria = builder.createQuery(Nfe.class);
		

		Root<Nfe> formPedido = criteria.from(Nfe.class);

		List<Predicate> conditions = new ArrayList<>();

		Predicate itemPedido = builder.equal(formPedido.get("isDeleted"),false);

		conditions.add(itemPedido);
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


	//	criteria.select(builder.sum(formPedido.get("valorTotalPedido").as(BigDecimal.class)));
		//criteria.select(formPedido.alias("cf"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Nfe> typedQuery = this.getEntityManager().createQuery(criteria);
		return typedQuery.getResultList();
	}
	/**
	 * Lista em modo Lazy 
	 * 
	 * @param isDeleted ( exibe conteudo deletado ( false = exibe apenas os NÃO deletados / TRUE = exibe apenas os deletados )
	 * @param isBloked
	 * @param idEmpresa
	 * @param idFilial
	 * @param pageRequest ( informações uteis para a paginação da DATATABLE)
	 * @param filtro ( Campo que sera pesquisado )
	 * @param pesquisa ( String com o conteudo a ser pesquisando na lista
	 * @param porFilial  (True = pesquisa feita fazendo separação entre filal e Matriz / False =
	 *  pesquisa feita exibindo TODAS as informações SEM separação entre filial e matriz)
	 * @return
	 */
	public Page<Nfe> listaPeriodoLazyComFiltro(Boolean isDeleted, Boolean isBloked,LocalDateTime dataIni,LocalDateTime dataFim, Long idEmpresa, Long idFilial, PageRequest pageRequest,String filtro , String pesquisa,Boolean porFilial ){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Nfe> criteria = builder.createQuery(getPersistentClass());

		Root<Nfe> fromProdutos = criteria.from(getPersistentClass());


		List<Predicate> conditions = new ArrayList<>();
		Predicate periodo = builder.between(fromProdutos.get("dataEmissao"),dataIni,dataFim);
		Predicate filial = builder.equal(fromProdutos.get("idFilial"), idFilial);
		Predicate filialNull = builder.isNull(fromProdutos.get("idFilial"));
		Predicate empresa = builder.equal(fromProdutos.get("idEmpresa"), idEmpresa);
		Predicate empresaNull = builder.isNull(fromProdutos.get("idEmpresa"));

		conditions.add(builder.equal(fromProdutos.get("isDeleted"), isDeleted));
		conditions.add(periodo);
		
		if (idFilial == null){
			if (idEmpresa == null){
				conditions.add(empresaNull);
			}else{
				if (porFilial) {
					conditions.add(empresa);
					conditions.add(filialNull);
				}else {
					conditions.add(empresa);
				}
			}
		}else{
			if (porFilial) {
				conditions.add(empresa);
				conditions.add(filial);
			}else {
				conditions.add(empresa);
				}
		}
		
		if (filtro != null){
			if (!filtro.isEmpty()){
				conditions.add(builder.like(builder.lower(fromProdutos.<String>get(filtro)),"%"+pesquisa.toLowerCase()+"%"));
			}
		}

		// projetamos para pegar o total de paginas possiveis

		CriteriaQuery<Long> cq = builder.createQuery(Long.class);
		cq.select(builder.count(cq.from(getPersistentClass())));
		this.getEntityManager().createQuery(cq);
		cq.where(conditions.toArray(new Predicate[0]));

		final Long totalRows = this.getEntityManager().createQuery(cq).getSingleResult();

		criteria.select(fromProdutos.alias("p"));
		criteria.where(conditions.toArray(new Predicate[0]));


		if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
			criteria.orderBy(builder.asc(fromProdutos.<String>get(pageRequest.getSortField())));

		} else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
			criteria.orderBy(builder.desc(fromProdutos.<String>get(pageRequest.getSortField())));

		}
		criteria.distinct(true);
		TypedQuery<Nfe> typedQuery = this.getEntityManager().createQuery(criteria);

		// paginamos
		typedQuery.setFirstResult(pageRequest.getFirstResult());
		typedQuery.setMaxResults(pageRequest.getPageSize());

		// montamos o resultado paginado
		return new Page<>(typedQuery.getResultList(), totalRows);
	}
	
	
	public boolean jaExiste(String nome){
		final Criteria criteria = this.createCriteria();
		
		criteria.add(Restrictions.ilike("cfop", nome));
		if (criteria.list().isEmpty() == true){
			return false;
		}else {
			return true;
		}
	}
	
//	public List<Nfe> pesquisaTexto(String dep,Long idEmpresa, Long idFilial){
//		final Criteria criteria = this.createCriteria();
//		
//		criteria.add(Restrictions.and(
//						Restrictions.ilike("nfe", "%"+dep+"%"),
//						Restrictions.eq("isDeleted", false),
//						Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
//						Restrictions.eqOrIsNull("idFilial", idFilial)));
//		return criteria.list();
//	}
	
//	public Nfe pegaNfe(Long id, Long idEmpresa){
//		final Criteria criteria = this.createCriteria();
//
//		criteria.add(Restrictions.and(
//				Restrictions.eq("id", id),
//				Restrictions.eq("isDeleted", false),
//				Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
//		criteria.setFetchMode("listaItemNfe", FetchMode.JOIN);
//
//
//		return (Nfe) criteria.uniqueResult();
//	}
	
	public Nfe pegaNfe(Long id, Long idEmpresa,Long idFilial) {
		EntityGraph<Nfe> entityGraph = this.getEntityManager().createEntityGraph(Nfe.class);
		entityGraph.addAttributeNodes("destino","listaItemNfe","transportador","formaPagamento");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Nfe> criteria = builder.createQuery(Nfe.class);
		

		Root<Nfe> formPedido = criteria.from(Nfe.class);

		List<Predicate> conditions = new ArrayList<>();

		Predicate itemPedido = builder.equal(formPedido.get("id"),id);

		conditions.add(itemPedido);
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


	//	criteria.select(builder.sum(formPedido.get("valorTotalPedido").as(BigDecimal.class)));
		criteria.select(formPedido.alias("cf"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Nfe> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.loadgraph", entityGraph);
		return typedQuery.getSingleResult();
	}
	/**
	 *  M�todo que retorna a NFE com a lista de parcelas preenchida
	 * @param id da nfe
	 * @param idEmpresa 
	 * @param idFilial
	 * @return Nfe
	 */
	public Nfe pegaNfeComParcelas(Long id, Long idEmpresa,Long idFilial) {
		EntityGraph<Nfe> entityGraph = this.getEntityManager().createEntityGraph(Nfe.class);
		entityGraph.addAttributeNodes("listaParcelas");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Nfe> criteria = builder.createQuery(Nfe.class);
		

		Root<Nfe> formPedido = criteria.from(Nfe.class);

		List<Predicate> conditions = new ArrayList<>();

		Predicate itemPedido = builder.equal(formPedido.get("id"),id);

		conditions.add(itemPedido);
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


	//	criteria.select(builder.sum(formPedido.get("valorTotalPedido").as(BigDecimal.class)));
		criteria.select(formPedido.alias("cf"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Nfe> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.loadgraph", entityGraph);
		return typedQuery.getSingleResult();
	}
	
	public List<RelNatOperacaoDTO> relNatOperacao(LocalDate dataInicial, LocalDate dataFinal, Long pegaIdEmpresa,
			Long pegaIdFilial,Tributos trib,StatusNfe status) {
		String sql = "select nfe.numeroNota as numeroNFE, nfe.nome as razaoSocial, date(nfe.dataSaida) as dataSaida ,trib.descricao as natOpera, sum(item.valorTotal) as totalNota,"
				+ "if (nfe.statusEmissao = 'EN','NORMAL','CANCELADA') as status,"
				+ "emp.razaoSocial as matriz,"
				+ " if ("+pegaIdFilial +" is null,'Vazio',fil.razaoSocial) as filial"
				+ " from ItemNfe as item "
				+ " inner join nfe on nfe.id = item.NFE_id "
				+ " inner join tributos trib on trib.id = nfe.NatOperacao_ID "
				+ " inner join cfop on cfop.id = item.Cfop "
				+ " inner join Empresa emp on nfe.id_empresa = emp.id"
				+ " left join Filial fil on fil.id = nfe.id_filial "
				+ " where nfe.id_empresa = '"+pegaIdEmpresa+"'"
				+ " and date(nfe.dataSaida) between '"+dataInicial+"' and '"+dataFinal+"'  and nfe.NatOperacao_ID = '"+trib.getId()+"' and"
				+ " nfe.statusEmissao = '"+status.getSigla()+"' and ";
		if (pegaIdFilial == null) {
			sql = sql + " nfe.id_filial is null group by nfe.id,nfe.NatOperacao_ID order by nfe.id desc";
		}else {
			sql = sql + "nfe.id_filial = '"+pegaIdFilial+"' group by nfe.id,nfe.NatOperacao_ID order by nfe.id desc";
		}
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
					// TODO Auto-generated method stub
					RelNatOperacaoDTO relDto = new RelNatOperacaoDTO(
									(BigInteger)tuple[0],
									(String)tuple[1], 
									(Date)tuple[2],
									(String)tuple[3],
									(BigDecimal)tuple[4],
									(String)tuple[5],
									(String)tuple[6],
									(String)tuple[7]) 
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
		List<RelNatOperacaoDTO> dto =  resultado.getResultList();
		
		
		return dto;
	}	

}
