package br.com.nsym.domain.model.repository.venda;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityGraph;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.transform.ResultTransformer;

import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.Filial;
import br.com.nsym.domain.model.entity.cadastro.dto.RelVendasFabricanteDTO;
import br.com.nsym.domain.model.entity.financeiro.AgPedido;
import br.com.nsym.domain.model.entity.financeiro.Caixa;
import br.com.nsym.domain.model.entity.tools.PedidoStatus;
import br.com.nsym.domain.model.entity.tools.PedidoTipo;
import br.com.nsym.domain.model.entity.venda.DestinatarioTransferencia;
import br.com.nsym.domain.model.entity.venda.Pedido;
import br.com.nsym.domain.model.entity.venda.RelComissaoColaboradoresDTO;
import br.com.nsym.domain.model.entity.venda.RelatorioVendasDTO;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class PedidoRepository extends GenericRepositoryEmpDS<Pedido, Long> {

	
//	public static Connection getConnection() throws HibernateException, SQLException{
//		return new AnnotationConfiguration().configure().buildSettings().getConnectionProvider()
//		.getConnection();
//		}

	/**
	 *
	 */
	private static final long serialVersionUID = -5784045903414486795L;

	public Page<Pedido> listaPedidosEmitidosPorIntervaloData(LocalDate dataIni, LocalDate dataFim, Long pegaIdEmpresa, Long pegaIdFilial,PageRequest pageRequest,String filtro , String pesquisa) {
		
		EntityGraph<Pedido> entityGraph = this.getEntityManager().createEntityGraph(Pedido.class);
		entityGraph.addAttributeNodes("controle");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Pedido> criteria = builder.createQuery(Pedido.class);

		Root<Pedido> formPedido = criteria.from(Pedido.class);

		List<Predicate> conditions = new ArrayList<>();

		Predicate itemPedido = builder.between(formPedido.get("dataEmissao"),dataIni,dataFim);

		if (filtro != null){
			if (!filtro.isEmpty() ) {
				conditions.add(builder.like(builder.upper(formPedido.get(filtro)),"%"+pesquisa.toUpperCase()+"%")); // garantindo que tudo estja em maiuscula para localizar o termo
			}

		}
		conditions.add(builder.equal(formPedido.get("pedidoTipo"),PedidoTipo.PVE));
		conditions.add(itemPedido);
		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.isNull(formPedido.get("idFilial")));
			}else{
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.equal(formPedido.get("idFilial"),pegaIdFilial));
			}
		}

		CriteriaQuery<Long> cq = builder.createQuery(Long.class);
		cq.select(builder.count(cq.from(Pedido.class)));
		this.getEntityManager().createQuery(cq);
		cq.where(conditions.toArray(new Predicate[0]));

		final Long totalRows = this.getEntityManager().createQuery(cq).getSingleResult();

//		criteria.select(formPedido.alias("cf"));
		criteria.where(conditions.toArray(new Predicate[]{}));

		if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
			criteria.orderBy(builder.asc(formPedido.get(pageRequest.getSortField())));

		} else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
			criteria.orderBy(builder.desc(formPedido.get(pageRequest.getSortField())));

		}

		criteria.distinct(true);
		TypedQuery<Pedido> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);

		// paginamos
		typedQuery.setFirstResult(pageRequest.getFirstResult());
		typedQuery.setMaxResults(pageRequest.getPageSize());
		return new Page<>(typedQuery.getResultList(),totalRows);

	}

	public Page<Pedido> listaPedidosEmitidosPorIntervaloDataEmAberto(LocalDate dataIni, LocalDate dataFim, Long pegaIdEmpresa, Long pegaIdFilial,PageRequest pageRequest,String filtro , String pesquisa) {
		EntityGraph<Pedido> entityGraph = this.getEntityManager().createEntityGraph(Pedido.class);
		entityGraph.addAttributeNodes("controle","destino","listaItensPedido","pagamento");
		
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Pedido> criteria = builder.createQuery(Pedido.class);

		Root<Pedido> formPedido = criteria.from(Pedido.class);

		List<Predicate> conditions = new ArrayList<>();

		Predicate itemPedido = builder.between(formPedido.get("dataEmissao"),dataIni,dataFim);

		if (filtro != null){
			if (!filtro.isEmpty() ) {
				conditions.add(builder.like(builder.upper(formPedido.get(filtro)), pesquisa.toUpperCase())); // garantindo que tudo estja em maiuscula para localizar o termo
			}

		}
		conditions.add(builder.equal(formPedido.get("pedidoStatus"), PedidoStatus.AgR));
		conditions.add(builder.equal(formPedido.get("pedidoTipo"),PedidoTipo.PVE));
		conditions.add(itemPedido);
		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.isNull(formPedido.get("idFilial")));
			}else{
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.equal(formPedido.get("idFilial"),pegaIdFilial));
			}
		}

		CriteriaQuery<Long> cq = builder.createQuery(Long.class);
		cq.select(builder.count(cq.from(Pedido.class)));
		this.getEntityManager().createQuery(cq);
		cq.where(conditions.toArray(new Predicate[0]));

		final Long totalRows = this.getEntityManager().createQuery(cq).getSingleResult();

//		criteria.select(formPedido.alias("cf"));
		criteria.where(conditions.toArray(new Predicate[]{}));

		if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
			criteria.orderBy(builder.asc(formPedido.get(pageRequest.getSortField())));

		} else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
			criteria.orderBy(builder.desc(formPedido.get(pageRequest.getSortField())));

		}

		criteria.distinct(true);
		TypedQuery<Pedido> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);

		// paginamos
		typedQuery.setFirstResult(pageRequest.getFirstResult());
		typedQuery.setMaxResults(pageRequest.getPageSize());
		//		return (BarrasEstoque)typedQuery.getSingleResult();
		return new Page<>(typedQuery.getResultList(),totalRows);

	}
	public Pedido pegaTransferenciaPorId(Long id) {
		EntityGraph<Pedido> entityGraph = this.getEntityManager().createEntityGraph(Pedido.class);
		entityGraph.addAttributeNodes("controle","emitente","transacao","destinoTransferencia","listaItensPedido","pagamento");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Pedido> criteria = builder.createQuery(Pedido.class);

		Root<Pedido> formPedido = criteria.from(Pedido.class);

		List<Predicate> conditions = new ArrayList<>();

		conditions.add(builder.equal(formPedido.get("id"),id));
		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Pedido> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		
		return typedQuery.getSingleResult();

	}
	public Pedido pegaPedidoPorId(Long id) {
		EntityGraph<Pedido> entityGraph = this.getEntityManager().createEntityGraph(Pedido.class);
		entityGraph.addAttributeNodes("controle","emitente","transacao","destino","listaItensPedido","pagamento");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Pedido> criteria = builder.createQuery(Pedido.class);

		Root<Pedido> formPedido = criteria.from(Pedido.class);

		List<Predicate> conditions = new ArrayList<>();

		conditions.add(builder.equal(formPedido.get("id"),id));

		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Pedido> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		
		return typedQuery.getSingleResult();

	}

	public BigDecimal totalPedidosPeriodo(LocalDate dataIni, LocalDate dataFim, Long pegaIdEmpresa, Long pegaIdFilial) {
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<BigDecimal> criteria = builder.createQuery(BigDecimal.class);

		Root<Pedido> formPedido = criteria.from(Pedido.class);

		List<Predicate> conditions = new ArrayList<>();

		Predicate itemPedido = builder.between(formPedido.get("dataEmissao"),dataIni,dataFim);

		conditions.add(itemPedido);
		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.isNull(formPedido.get("idFilial")));
			}else{
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.equal(formPedido.get("idFilial"),pegaIdFilial));
			}
		}


		criteria.select(builder.sum(formPedido.get("valorTotalPedido").as(BigDecimal.class)));
//		criteria.select(formPedido.alias("cf"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<BigDecimal> typedQuery = this.getEntityManager().createQuery(criteria);
		return typedQuery.getSingleResult();
	}

	public List<Pedido> pegaPedidosPorAgPedido(AgPedido ag){
		EntityGraph<Pedido> entityGraph = this.getEntityManager().createEntityGraph(Pedido.class);
		entityGraph.addAttributeNodes("controle","destino","listaItensPedido","pagamento");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Pedido> criteria = builder.createQuery(Pedido.class);

		Root<Pedido> formPedido = criteria.from(Pedido.class);

		List<Predicate> conditions = new ArrayList<>();

		conditions.add(builder.equal(formPedido.get("agrupado"),ag));

		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Pedido> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		return typedQuery.getResultList();

	}

	public List<Pedido> pegaPedidosPorCaixa(Caixa cx){
		EntityGraph<Pedido> entityGraph = this.getEntityManager().createEntityGraph(Pedido.class);
		entityGraph.addAttributeNodes("controle","agrupado");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Pedido> criteria = builder.createQuery(Pedido.class);

		Root<Pedido> formPedido = criteria.from(Pedido.class);

		List<Predicate> conditions = new ArrayList<>();

		conditions.add(builder.equal(formPedido.get("caixa"),cx));

//		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Pedido> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		return typedQuery.getResultList();

	}

	public List<Pedido> produtosVendidosPorPeriodo(LocalDate dataInicial, LocalDate dataFinal, Long pegaIdEmpresa,
			Long pegaIdFilial) {
		
		EntityGraph<Pedido> entityGraph = this.getEntityManager().createEntityGraph(Pedido.class);
		entityGraph.addAttributeNodes("controle","destino","destinoTransferencia");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Pedido> criteria = builder.createQuery(Pedido.class);

		Root<Pedido> formPedido = criteria.from(Pedido.class);
		
		
		List<Predicate> conditions = new ArrayList<>();
		
		Predicate itemPedido = builder.between(formPedido.get("dataEmissao"),dataInicial,dataFinal);
		
		conditions.add(itemPedido);
		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.isNull(formPedido.get("idFilial")));
			}else{
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.equal(formPedido.get("idFilial"),pegaIdFilial));
			}
		}
		
//		CriteriaQuery<Long> cq = builder.createQuery(Long.class);
//		cq.select(builder.count(cq.from(Pedido.class)));
//		this.getEntityManager().createQuery(cq);
//		cq.where(conditions.toArray(new Predicate[0]));
//		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);

		TypedQuery<Pedido> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		return typedQuery.getResultList();
	}
	
	public List<Pedido> pedidosVendidosPorPeriodoPorCliente(LocalDate dataInicial, LocalDate dataFinal, Long pegaIdEmpresa,
			Long pegaIdFilial,Long nomeCliente) {
		
		EntityGraph<Pedido> entityGraph = this.getEntityManager().createEntityGraph(Pedido.class);
		entityGraph.addAttributeNodes("controle","destino");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Pedido> criteria = builder.createQuery(Pedido.class);

		Root<Pedido> formPedido = criteria.from(Pedido.class);
		
		
		List<Predicate> conditions = new ArrayList<>();
		
		Predicate itemPedido = builder.between(formPedido.get("dataEmissao"),dataInicial,dataFinal);
		
		conditions.add(itemPedido);
		conditions.add(builder.equal(formPedido.get("destino").get("cliente"), nomeCliente));
		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.isNull(formPedido.get("idFilial")));
			}else{
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.equal(formPedido.get("idFilial"),pegaIdFilial));
			}
		}
		
//		CriteriaQuery<Long> cq = builder.createQuery(Long.class);
//		cq.select(builder.count(cq.from(Pedido.class)));
//		this.getEntityManager().createQuery(cq);
//		cq.where(conditions.toArray(new Predicate[0]));
//		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);

		TypedQuery<Pedido> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		return typedQuery.getResultList();
	}
	
	/**
	 *  Consulta o banco de dados a procura de Solicitaçoes de transferencias para modulo Estoque
	 * @param dataIni
	 * @param dataFim
	 * @param pegaIdEmpresa = id da empresa
	 * @param pegaIdFilial = id da filial
	 * @param tipo = Tipo do Pedido (classe PedidoTipo)
	 * @param status = Status Pedido (classe PedidoStatus)
	 * @param transferenciaConcluida (True = para apenas ja finalizada  / FALSE = para transferencias ainda nao confirmadas)
	 * @param matriz (True = Matriz solicitando a listagem ou FALSE = Filial solicitando a listagem)
	 * @return List<Pedido> lista com as transferencias
	 */
	
	public List<Pedido> listaTransferenciaPorTipoStatusDestino(LocalDate dataIni,LocalDate dataFim, Empresa pegaIdEmpresa, Filial pegaIdFilial, PedidoTipo tipo,PedidoStatus status,boolean transferenciaConcluida,boolean matriz){
		EntityGraph<Pedido> entityGraph = this.getEntityManager().createEntityGraph(Pedido.class);
		if (tipo == PedidoTipo.TRA) {
			entityGraph.addAttributeNodes("controle","emitente","destinoTransferencia");
		}else {
			entityGraph.addAttributeNodes("controle","destino");
		}
		List<Predicate> conditions = new ArrayList<>();
		
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Pedido> criteria =  builder.createQuery(getPersistentClass());
		Root<Pedido> formPedido = criteria.from(Pedido.class);
		if (tipo == PedidoTipo.TRA) {
			Join<DestinatarioTransferencia, Pedido> formDestino = formPedido.join("destinoTransferencia");
			if (matriz) {
				conditions.add(builder.equal(formDestino.get("empresa"),pegaIdEmpresa));
			}else {
				conditions.add(builder.equal(formDestino.get("filial"),pegaIdFilial));
			}
		}
		
		

		Predicate itemPedido = builder.between(formPedido.get("dataEmissao"),dataIni,dataFim);
		conditions.add(builder.equal(formPedido.get("isDeleted"),false));
		conditions.add(builder.equal(formPedido.get("transferenciaConcluida"),transferenciaConcluida));
		conditions.add(itemPedido);
		
		if (tipo != null) {
			conditions.add(builder.equal(formPedido.get("pedidoTipo"),tipo));
		}
		if (status != null) {
			conditions.add(builder.equal(formPedido.get("pedidoStatus"),status));
		}
		
		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa.getId()));
		}
		
//		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Pedido> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		
//		List<Pedido> pedidosPorDestinatario = new ArrayList<>();
//		for (Pedido pedido : typedQuery.getResultList()) {
//			if (matriz) {
//				if (pedido.getDestinoTransferencia().getEmpresa() != null) {
//					pedidosPorDestinatario.add(pedido);
//				}
//			}else {
//				if (pedido.getDestinoTransferencia().getFilial() != null) {
//					if (pedido.getDestinoTransferencia().getFilial() == pegaIdFilial) {
//						pedidosPorDestinatario.add(pedido);
//					}
//				}
//			}
//		}
		return typedQuery.getResultList();
	}
	
	/**
	 *  Consulta o banco de dados a procura de Solicitaçoes de transferencias para modulo CAIXA!!!!
	 * @param dataIni
	 * @param dataFim
	 * @param pegaIdEmpresa = id da empresa
	 * @param pegaIdFilial = id da filial
	 * @param tipo = Tipo do Pedido (classe PedidoTipo)
	 * @param status = Status Pedido (classe PedidoStatus)
	 * @param transferenciaConcluida (True = para apenas ja finalizada  / FALSE = para transferencias ainda nao confirmadas)
	 * @param matriz (True = Matriz solicitando a listagem ou FALSE = Filial solicitando a listagem)
	 * @return List<Pedido> lista com as transferencias
	 */
	
	public List<Pedido> listaTransferenciaPorTipoStatusDestinoCaixa(LocalDate dataIni,LocalDate dataFim, Empresa pegaIdEmpresa, Filial pegaIdFilial, PedidoTipo tipo,PedidoStatus status,boolean transferenciaConcluida,boolean matriz){
		EntityGraph<Pedido> entityGraph = this.getEntityManager().createEntityGraph(Pedido.class);
		if (tipo == PedidoTipo.TRA) {
			entityGraph.addAttributeNodes("controle","emitente","destinoTransferencia");
		}else {
			entityGraph.addAttributeNodes("controle","destino");
		}
		List<Predicate> conditions = new ArrayList<>();
		
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Pedido> criteria =  builder.createQuery(getPersistentClass());
		Root<Pedido> formPedido = criteria.from(Pedido.class);
//		if (tipo == PedidoTipo.TRA) {
//			Join<DestinatarioTransferencia, Pedido> formDestino = formPedido.join("destinoTransferencia");
//			if (matriz) {
//				conditions.add(builder.equal(formDestino.get("empresa"),pegaIdEmpresa));
//			}else {
//				conditions.add(builder.equal(formDestino.get("filial"),pegaIdFilial));
//			}
//		}
		
		

		Predicate itemPedido = builder.between(formPedido.get("dataEmissao"),dataIni,dataFim);
		conditions.add(builder.equal(formPedido.get("isDeleted"), false));
		conditions.add(builder.equal(formPedido.get("transferenciaConcluida"),transferenciaConcluida));
		conditions.add(itemPedido);
		
		if (tipo != null) {
			conditions.add(builder.equal(formPedido.get("pedidoTipo"),tipo));
		}
		if (status != null) {
			conditions.add(builder.equal(formPedido.get("pedidoStatus"),status));
		}
		
		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa.getId()));
				conditions.add(builder.isNull(formPedido.get("idFilial")));
			}else{
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa.getId()));
				conditions.add(builder.equal(formPedido.get("idFilial"),pegaIdFilial.getId()));
			}
		}
		
//		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Pedido> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		
//		List<Pedido> pedidosPorDestinatario = new ArrayList<>();
//		for (Pedido pedido : typedQuery.getResultList()) {
//			if (matriz) {
//				if (pedido.getDestinoTransferencia().getEmpresa() != null) {
//					pedidosPorDestinatario.add(pedido);
//				}
//			}else {
//				if (pedido.getDestinoTransferencia().getFilial() != null) {
//					if (pedido.getDestinoTransferencia().getFilial() == pegaIdFilial) {
//						pedidosPorDestinatario.add(pedido);
//					}
//				}
//			}
//		}
		return typedQuery.getResultList();
	}
	
	public List<Pedido> listaPedidoPorTipoEStatus(LocalDate dataIni,LocalDate dataFim, Long pegaIdEmpresa, Long pegaIdFilial, PedidoTipo tipo,PedidoStatus status){
		EntityGraph<Pedido> entityGraph = this.getEntityManager().createEntityGraph(Pedido.class);
		if (tipo == PedidoTipo.TRA) {
			entityGraph.addAttributeNodes("controle","destinoTransferencia");
		}else {
			entityGraph.addAttributeNodes("controle","destino");
		}
		
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Pedido> criteria =  builder.createQuery(getPersistentClass());

		Root<Pedido> formPedido = criteria.from(Pedido.class);
		
		List<Predicate> conditions = new ArrayList<>();

		Predicate itemPedido = builder.between(formPedido.get("dataEmissao"),dataIni,dataFim);
		conditions.add(builder.equal(formPedido.get("isDeleted"), false));
		conditions.add(itemPedido);
		
		if (tipo != null) {
			conditions.add(builder.equal(formPedido.get("pedidoTipo"),tipo));
		}
		if (status != null) {
			conditions.add(builder.equal(formPedido.get("pedidoStatus"),status));
		}
		
		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.isNull(formPedido.get("idFilial")));
			}else{
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.equal(formPedido.get("idFilial"),pegaIdFilial));
			}
		}
		
		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Pedido> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		return typedQuery.getResultList();
	}
	/**
	 * Pesquisa para relatorio de pedidos com programação de entrega
	 * @param dataIni
	 * @param dataFim
	 * @param pegaIdEmpresa
	 * @param pegaIdFilial
	 * @param tipo
	 * @param status
	 * @return
	 */
	public List<Pedido>listaPedidoProgramados(LocalDate dataIni,LocalDate dataFim, Long pegaIdEmpresa, Long pegaIdFilial, PedidoTipo tipo,PedidoStatus status){
		EntityGraph<Pedido> entityGraph = this.getEntityManager().createEntityGraph(Pedido.class);
		if (tipo == PedidoTipo.TRA) {
			entityGraph.addAttributeNodes("controle","destinoTransferencia");
		}else {
			entityGraph.addAttributeNodes("controle","destino");
		}
		
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Pedido> criteria =  builder.createQuery(getPersistentClass());

		Root<Pedido> formPedido = criteria.from(Pedido.class);
		
		List<Predicate> conditions = new ArrayList<>();

		Predicate itemPedido = builder.between(formPedido.get("previsaoEntrega"),dataIni,dataFim);
		conditions.add(itemPedido);
		conditions.add(builder.equal(formPedido.get("isDeleted"), false));
		conditions.add(builder.equal(formPedido.get("ativaEncomenda"),true));
		
		if (tipo != null) {
			conditions.add(builder.equal(formPedido.get("pedidoTipo"),tipo));
		}
		if (status != null) {
			conditions.add(builder.equal(formPedido.get("pedidoStatus"),status));
		}
		
		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.isNull(formPedido.get("idFilial")));
			}else{
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.equal(formPedido.get("idFilial"),pegaIdFilial));
			}
		}
		
		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Pedido> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		
		
		return typedQuery.getResultList();
	}
	
	
	/**
	 *  Localiza o pedido atravÃ©s do controle e data, caso encontrado mais de um resultado ou nÃ£o encontrado 
	 *  correspondÃªncia, retorna NULL.
	 * 
	 * @param data
	 * @param controle
	 * @return
	 */
	public Pedido localizaPedidoPorControle(Long controle,Long pegaIdEmpresa, Long pegaIdFilial, PedidoTipo tipo,PedidoStatus status) {
		
		EntityGraph<Pedido> entityGraph = this.getEntityManager().createEntityGraph(Pedido.class);
		entityGraph.addAttributeNodes("controle","emitente","transacao","destino","listaItensPedido","pagamento");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Pedido> criteria = builder.createQuery(Pedido.class);
		

		Root<Pedido> formPedido = criteria.from(Pedido.class);

		List<Predicate> conditions = new ArrayList<>();

		conditions.add(builder.equal(formPedido.get("controle"),controle));
		
		if (tipo != null) {
			conditions.add(builder.equal(formPedido.get("pedidoTipo"),tipo));
		}
		if (status != null) {
			conditions.add(builder.equal(formPedido.get("pedidoStatus"),status));
		}
		
		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.isNull(formPedido.get("idFilial")));
			}else{
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.equal(formPedido.get("idFilial"),pegaIdFilial));
			}
		}

		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Pedido> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		
		List<Pedido> listaResultadoTemp = new ArrayList<>();
		listaResultadoTemp = typedQuery.getResultList();
		
		if (listaResultadoTemp.size()== 1 ) {
			return typedQuery.getSingleResult();
		}else {
			return null;
		}
	}
	/**
	 * Relatorio de vendas agrupado por fabricante e departamento e ordenado por Departamento.
	 * @param dataInicial
	 * @param dataFinal
	 * @param pegaIdEmpresa
	 * @param pegaIdFilial
	 * @param tipo
	 * @param status
	 * @return lista RelVendasFabricabte
	 */
	public List<RelVendasFabricanteDTO> maisVendidosPorFabricante(LocalDate dataInicial, LocalDate dataFinal, Long pegaIdEmpresa,
			Long pegaIdFilial,PedidoTipo tipo,PedidoStatus status) {
		String sql = "select fab.marca as fabricante,dep.departamento as departamento, sum(item.quantidade) as quantidade,"
				+ "(select sum(quantidade)  from  ItemPedido  i1 inner join pedido p2 on  i1.Pedido_ID  = p2.id  where p2.id_empresa = '"+pegaIdEmpresa+"' and p2.dataRecebimento between '"+dataInicial+"' and '"+dataFinal+"'  and p2.pedidoStatus = '"+status.getSigla()+"' and p2.pedidoTipo = '"+tipo.getSigla()+"' and ";
				if (pegaIdFilial == null) {
					sql = sql + "p2.id_filial is null) as totaPecas,";
				}else {
					sql = sql + "p2.id_filial = "+pegaIdFilial+") as totaPecas,";
				}
				sql = sql  
				+ "( (sum(item.quantidade)/(select sum(quantidade)  from  ItemPedido  i1 inner join pedido p2 on  i1.Pedido_ID  = p2.id  where p2.id_empresa = '"+pegaIdEmpresa+"' and p2.dataRecebimento between '"+dataInicial+"' and '"+dataFinal+"'  and p2.pedidoStatus = '"+status.getSigla()+"' and p2.pedidoTipo = '"+tipo.getSigla()+"' and ";
				if (pegaIdFilial == null) {
					sql = sql + "p2.id_filial is null))*100 ) as part,";
				}else {
					sql = sql + "p2.id_filial = "+pegaIdFilial+"))*100 ) as part,";
				}
						sql = sql
								+ "sum(item.valorTotal)/sum(item.quantidade) as valorMedio,"
								+ "sum(item.valorTotal) as totalValor,"
								+ "(select sum(valorTotalPedido) from pedido p1 where p1.id_empresa = '"+pegaIdEmpresa+"' and p1.dataRecebimento between '"+dataInicial+"' and '"+dataFinal+"'  and p1.pedidoStatus = '"+status.getSigla()+"' and p1.pedidoTipo = '"+tipo.getSigla()+"' and ";
				if (pegaIdFilial == null) {
					sql = sql + "p1.id_filial is null  ) as totalVendido,";
				}else {
					sql = sql + "p1.id_filial = "+pegaIdFilial+" ) as totalVendido,";
				}
				sql = sql  
						+ "( (sum(item.valorTotal)/(select sum(valorTotalPedido) from pedido p2 where p2.id_empresa = '"+pegaIdEmpresa+"' and p2.dataRecebimento between '"+dataInicial+"' and '"+dataFinal+"'  and p2.pedidoStatus = '"+status.getSigla()+"' and p2.pedidoTipo = '"+tipo.getSigla()+"' and ";
						if (pegaIdFilial == null) {
							sql = sql + "p2.id_filial is null))*100 ) as partVenda,emp.razaoSocial as matriz,";
						}else {
							sql = sql + "p2.id_filial = "+pegaIdFilial+"))*100 ) as partVenda,emp.razaoSocial as matriz,";
						}
				sql = sql			
				+ " if ("+pegaIdFilial +" is null,'Vazio',fil.razaoSocial) as filial,fab.id as idFab,dep.id as idDep "
				+ " from pedido ped "
				+ " inner join ItemPedido item on ped.id = item.Pedido_ID "
				+ " inner join produto prod on prod.id = item.produto_ID "
				+ " left join fabricante fab on fab.id = prod.Fabricante_id "
				+ " left join departamento dep on dep.id = prod.Departamento_id"
				+ " inner join Empresa emp on ped.id_empresa = emp.id"
				+ " left join Filial fil on fil.id = ped.id_filial "
				+ " where ped.id_empresa = '"+pegaIdEmpresa+"'"
				+ " and ped.dataRecebimento between '"+dataInicial+"' and '"+dataFinal+"'  and ped.pedidoStatus = '"+status.getSigla()+"' and ped.pedidoTipo = '"+tipo.getSigla()+"' and ";
		if (pegaIdFilial == null) {
			sql = sql + " ped.id_filial is null group by fab.id,dep.id order by dep.id";
		}else {
			sql = sql + "ped.id_filial = '"+pegaIdFilial+"' group by fab.id,dep.id order by dep.id";
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
					RelVendasFabricanteDTO relDto = new RelVendasFabricanteDTO(
									(String)tuple[0],
									(String)tuple[1], 
									(BigDecimal)tuple[2],
									(BigDecimal)tuple[3],
									(BigDecimal)tuple[4],
									(BigDecimal)tuple[5],
									(BigDecimal)tuple[6],
									(BigDecimal)tuple[7],
									(BigDecimal)tuple[8],
									(String)tuple[9], 
									(String)tuple[10],
									(BigInteger)tuple[11],
									(BigInteger)tuple[12]) 
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
		List<RelVendasFabricanteDTO> dto =  resultado.getResultList();
		
		
		return dto;
	}	
	
	
	public List<RelatorioVendasDTO> maisVendidos(LocalDate dataInicial, LocalDate dataFinal, Long pegaIdEmpresa,
			Long pegaIdFilial,PedidoTipo tipo,PedidoStatus status) {
		String sql = "select prod.referencia as ref,prod.descricao as descricao, sum(item.quantidade) as quant, item.valorUnitario as valor_Un,sum(item.valorTotal)/sum(item.quantidade) as vl_Med_Un,sum(item.valorTotal) as total, IF (tam.tamanho is null,'Vazio',tam.tamanho) as tamanho, IF (cor.nome is null,'Vazio',cor.nome) as cor,"
				+ "(select sum(valorTotalPedido) from pedido p1 where p1.id_empresa = '"+pegaIdEmpresa+"' and p1.dataRecebimento between '"+dataInicial+"' and '"+dataFinal+"'  and p1.pedidoStatus = '"+status.getSigla()+"' and p1.pedidoTipo = '"+tipo.getSigla()+"' and ";
				if (pegaIdFilial == null) {
					sql = sql + "p1.id_filial is null  ) as totalPeriodo,";
				}else {
					sql = sql + "p1.id_filial = "+pegaIdFilial+" ) as totalPeriodo,";
				}
				sql = sql 
				+ "(select sum(quantidade)  from  ItemPedido  i1 inner join pedido p2 on  i1.Pedido_ID  = p2.id  where p2.id_empresa = '"+pegaIdEmpresa+"' and p2.dataRecebimento between '"+dataInicial+"' and '"+dataFinal+"'  and p2.pedidoStatus = '"+status.getSigla()+"' and p2.pedidoTipo = '"+tipo.getSigla()+"' and ";
				if (pegaIdFilial == null) {
					sql = sql + "p2.id_filial is null) as totaPecas, emp.razaoSocial as matriz,";
				}else {
					sql = sql + "p2.id_filial = "+pegaIdFilial+") as totaPecas, emp.razaoSocial as matriz,";
				}
				sql = sql  
				+ " if ("+pegaIdFilial +" is null,'Vazio',fil.razaoSocial) as filial, "
				+ " barras.id as barras"
				+ " from pedido ped "
				+ " inner join ItemPedido item on ped.id = item.Pedido_ID "
				+ " inner join produto prod on prod.id = item.produto_ID "
				+ " inner Join barras barras on barras.id = item.Barras_ID"
				+ " left join tamanho tam on tam.id = barras.tamanho_Id"
				+ " left join cor cor on cor.id = barras.cor_Id"
				+ " inner join Empresa emp on ped.id_empresa = emp.id"
				+ " left join Filial fil on fil.id = ped.id_filial "
				+ " where ped.id_empresa = '"+pegaIdEmpresa+"'"
				+ " and ped.dataRecebimento between '"+dataInicial+"' and '"+dataFinal+"'  and ped.pedidoStatus = '"+status.getSigla()+"' and ped.pedidoTipo = '"+tipo.getSigla()+"' and ";
		if (pegaIdFilial == null) {
			sql = sql + " ped.id_filial is null group by barras.id order by quant desc";
		}else {
			sql = sql + "ped.id_filial = '"+pegaIdFilial+"' group by barras.id order by quant desc";
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
						RelatorioVendasDTO relDto = new RelatorioVendasDTO(
									(String)tuple[0],
									(String)tuple[1], 
									(BigDecimal)tuple[2],
									(BigDecimal)tuple[3],
									(BigDecimal)tuple[4],
									(BigDecimal)tuple[5],
									(String)tuple[6], 
									(String)tuple[7], 
									(BigDecimal)tuple[8],
									(BigDecimal)tuple[9],
									(String)tuple[10],
									(String)tuple[11],
									(BigInteger)tuple[12])
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
		List<RelatorioVendasDTO> dto =  resultado.getResultList();
		
		
		return dto;
	}	
	
	public List<RelComissaoColaboradoresDTO> comissaoColaboradores(LocalDate dataInicial, LocalDate dataFinal, Long pegaIdEmpresa,
			Long pegaIdFilial,PedidoTipo tipo,PedidoStatus status,String filtroNome) {
		String sql = "select col.nome as colaborador, sum(ped.valorTotalPedido) as total,"
				+ " emp.RazaoSocial as matriz,"
				+ " if ("+pegaIdFilial +" is null,'Vazio',fil.razaoSocial) as filial,col.id as id"
				+ " from pedido ped"
				+ " inner join colaborador col on col.id = ped.Colaborador_id"
				+ " inner join Empresa emp on emp.id = ped.id_empresa"
				+ " left join Filial fil on fil.id = ped.id_filial"
				+ " where (ped.dataRecebimento between '"+dataInicial+"' and '"+dataFinal+"')"
				+ " and ped.id_empresa = '"+pegaIdEmpresa+"'";
				if (filtroNome != null) {
					sql = sql + " and col.nome like '%" + filtroNome + "%' ";
				}
				if (pegaIdFilial == null) {
					sql = sql + " and ped.id_filial is null "
					+ " and ped.pedidoStatus = '"+status.getSigla()+"'"
					+ " and ped.pedidoTipo = '"+tipo.getSigla()+"'"
					+ " group by col.nome";
				}else {
					sql = sql + " and ped.id_filial = '"+pegaIdFilial+"'"
					+ " and ped.pedidoStatus = '"+status.getSigla()+"'"
					+ " and ped.pedidoTipo = '"+tipo.getSigla()+"'"
					+ " group by col.nome";
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
					RelComissaoColaboradoresDTO relDto = new RelComissaoColaboradoresDTO(
									(String)tuple[0],
									(BigDecimal)tuple[1],
									(String)tuple[2],
									(String)tuple[3],
									(BigInteger)tuple[4])
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
		List<RelComissaoColaboradoresDTO> dto =  resultado.getResultList();
		return dto;
	}	
	
}
