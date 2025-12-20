package br.com.nsym.domain.model.repository;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.application.producer.qualifier.EmpDS;
import br.com.nsym.domain.model.entity.IPersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.Email;
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.Fone;

/**
 * A implementacao padrao do repositorio generico, com esta classe habilitamos o
 * suporte as funcionalidades basicas de um repositorio de dados no banco
 *
 * @param <T> a classe persistente para este repositorio
 * @param <ID> o tipo de nossos ID
 *
 * @author Ibrahim Yousef quatani
 *
 * @version 2.0.0
 * @since 1.0.0, 25/10/2016
 */
@SuppressWarnings("rawtypes")
public abstract class GenericRepositoryEmpDS<T extends IPersistentEntity, ID extends Serializable> 
	implements IGenericRepository<T, ID>, Serializable {

	private static final long serialVersionUID = 6843264201483551929L;

	@PersistenceContext(unitName= "empDS")
	@EmpDS
    private EntityManager entityManager;

    private final Class<T> persistentClass;
    

//    /**
//     * Inicia o repositorio identificando qual e a classe de nossa entidade, seu
//     * tipo {@link Class<?>}
//     */
//    @SuppressWarnings({"unchecked", "unsafe"})
//    public GenericRepositoryEmpDS() {
//        this.persistentClass = (Class< T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//    }
    
    public GenericRepositoryEmpDS() {
        this.persistentClass = resolveEntityClass();
    }

    @SuppressWarnings("unchecked")
    private Class<T> resolveEntityClass() {
        // Começa na classe real (ou proxy) e vai subindo até achar o GenericRepositoryEmpDS parametrizado
        Class<?> clazz = getClass();
        while (clazz != null) {
            Type generic = clazz.getGenericSuperclass();

            if (generic instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) generic;
                Type[] args = pt.getActualTypeArguments();
                if (args != null && args.length > 0 && args[0] instanceof Class) {
                    return (Class<T>) args[0];
                }
            }

            clazz = clazz.getSuperclass();
        }

        // fallback (não deveria cair aqui em condições normais)
        throw new IllegalStateException("Não foi possível resolver o tipo genérico de " + getClass());
    }

    /**
     * @return nosso entityManager, inicializado e configurado
     */
    @EmpDS
    protected EntityManager getEntityManager() {
        if (this.entityManager == null) {
            throw new IllegalStateException("The entityManager is not initialized");
        }
        return this.entityManager;
    }

    /**
     * @param string 
     * @param class1 
     * @return a {@link Criteria} do hibernate setada para a classe do
     * repositorio
     */
    protected Criteria createCriteria() {
        return this.getSession().createCriteria(this.getPersistentClass(),"t");
    }
    
    /**
     * Criado para poder setar a classe manualmente do criteria
     */
    protected Criteria createCriteriaFone(Class<Fone> class1 , String apelido){
    	return this.getSession().createCriteria(class1,apelido);
    }

    protected CriteriaBuilder novaCriteria(){
    	CriteriaBuilder builder = this.getSession().getCriteriaBuilder();
//    	CriteriaQuery<T> criteria = builder.createQuery(this.getPersistentClass());
    	return builder;
    }
    
    /**
     * Criado para poder setar a classe manualmente do criteria
     */
    protected Criteria createCriteriaEmail(Class<Email> class1 , String apelido){
    	return this.getSession().createCriteria(class1,apelido);
    }
    
    /**
     * @return a {@link Session} do Hibernate para que possamos usar nossa
     * {@link Criteria} para buscas
     */
    protected Session getSession() {
        return (Session) this.getEntityManager().getDelegate();
    }
    

    /**
     * @return a classe de nossa entidade persistente
     */
    public Class<T> getPersistentClass() {
        return this.persistentClass;
    }

    /**
     * {@inheritDoc}
     *
     * @param id
     * @param lock
     * @return
     */
    @Override
    public T findById(ID id, boolean lock) {

        final T entity;

        if (lock) {
            entity = (T) this.getEntityManager().find(
                    this.getPersistentClass(), id, LockModeType.OPTIMISTIC);
        } else {
            entity = (T) this.getEntityManager().find(
                    this.getPersistentClass(), id);
        }
        return entity;
    }
    public T pegaReferencia(ID id) {

        return  (T) this.getEntityManager().getReference(this.getPersistentClass(), id);
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public List<T> listAll() {

        final EntityManager manager = this.getEntityManager();

        final CriteriaQuery<T> query = manager.getCriteriaBuilder()
                .createQuery(this.getPersistentClass());
        final TypedQuery<T> selectAll
                = manager.createQuery(query.select(
                        query.from(this.getPersistentClass())));

        return selectAll.getResultList();
    }
    /*
     * Retorna uma lista por empresa
     */
    @SuppressWarnings("unchecked")
	public List<T> listaPorEmpresa(Empresa id){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("empresa", id),
				Restrictions.eq("isDeleted", false)));
		criteria.setResultTransformer(Criteria.ROOT_ENTITY);
		return criteria.list();
	}
    /*
     * Retorna uma lista por Filial/Empresa
     */
    @SuppressWarnings("unchecked")
	public List<T> listaPorFilial(Long idEmpresa, Long idFilial){
		final Criteria criteria = this.createCriteria();
		
		if (idEmpresa != null && idFilial !=null){
		criteria.add(Restrictions.and(
				Restrictions.eq("idEmpresa", idEmpresa),
				Restrictions.eq("idFilial", idFilial),
				Restrictions.eq("isDeleted", false)));
		}
		if (idEmpresa != null && idFilial == null){
			criteria.add(Restrictions.and(
					Restrictions.eq("idEmpresa", idEmpresa),
//					Restrictions.isNull("idFilial"),
					Restrictions.eq("isDeleted", false)));
			
		}
		if (idEmpresa == null){
			criteria.add(Restrictions.and(
					Restrictions.isNull("idEmpresa"),
					Restrictions.isNull("idFilial"),
					Restrictions.eq("isDeleted", false)));
		}
		return criteria.list();
	}
    /**
     *  	Métdodo que retorna uma lista, caso queira que o retorno contenha as informaçoes empresa e filial informe global = true
     *  caso seja alguma LISTA para o Developer idMatriz = NULL
     * @param idEmpresa
     * @param idFilial
     * @param global - Se TRUE retorna todos os dados sem diferenciar entre filial e matriz
     * 				 - Se FALSE retorna os dados diferenciando se pertencem a matriz / filial
     * @param deleted (TRUE- exibe somente Excluidos  FALSE - Exibe os nï¿½o excluidos)
     * @return Lista<T>
     */
    public List<T> listaCriteriaPorFilial (Long idEmpresa , Long idFilial,boolean global, boolean deleted){
    	CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> criteria =  builder.createQuery(getPersistentClass());
		
		Root<T> formPedido = criteria.from(getPersistentClass());
		List<Predicate> conditions = new ArrayList<>();
		
		
		conditions.add(builder.equal(formPedido.get("isDeleted"), deleted));
		
		if (idEmpresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
			if (idFilial == null){
				conditions.add(builder.equal(formPedido.get("idEmpresa"),idEmpresa));
				if (global == false) {
					conditions.add(builder.isNull(formPedido.get("idFilial")));
				}
			}else{
				if (global == false) {
					conditions.add(builder.equal(formPedido.get("idEmpresa"),idEmpresa));
					conditions.add(builder.equal(formPedido.get("idFilial"),idFilial));
				}else {
					conditions.add(builder.equal(formPedido.get("idEmpresa"),idEmpresa));
				}
			}
		}
		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<T> typedQuery = this.getEntityManager().createQuery(criteria);
		
		return typedQuery.getResultList();
    }
    /**
     * {@inheritDoc}
     * 
     * @return 
     */
    @Override
    public Long count() {
        
        final EntityManager manager = this.getEntityManager();

        final CriteriaBuilder builder = manager.getCriteriaBuilder();
        final CriteriaQuery<Long> query = builder.createQuery(Long.class);
        
        query.select(builder.count(query.from(this.getPersistentClass())));
        
        return manager.createQuery(query).getSingleResult();
    }

    /**
     * {@inheritDoc}
     *
     * @param entity
     * @return
     */
    @Override
    public T save(T entity) {
        return this.getEntityManager().merge(entity);
    }
    

    /**
     * {@inheritDoc}
     *
     * @param entity
     */
    @Override
    public void delete(T entity) {
        final T persistentEntity = this.getEntityManager().getReference(
                this.getPersistentClass(), entity.getId());
        this.getEntityManager().remove(persistentEntity);
    }
    
    @SuppressWarnings("unchecked")
	public Page<T> listByStatus(Boolean isDeleted,  Boolean isBlocked, Long emp ,PageRequest pageRequest) {

		final Criteria criteria = this.createCriteria();

		if (isBlocked != null) {
			criteria.add(Restrictions.eq("blocked", isBlocked));
		}
		if (isDeleted !=null){
			criteria.add(Restrictions.eq("isDeleted", isDeleted));
		}
		if (emp != null){
			criteria.add(Restrictions.eq("idEmpresa", emp));
		}
		if (emp == null){
			criteria.add(Restrictions.isNull("idEmpresa"));
		}
		
		// projetamos para pegar o total de paginas possiveis
		criteria.setProjection(Projections.count("id"));

		final Long totalRows = (Long) criteria.uniqueResult();

		// limpamos a projection para que a criteria seja reusada
		criteria.setProjection(null);
		criteria.setResultTransformer(Criteria.ROOT_ENTITY);

		// paginamos
		criteria.setFirstResult(pageRequest.getFirstResult());
		criteria.setMaxResults(pageRequest.getPageSize());

		if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
			criteria.addOrder(Order.asc(pageRequest.getSortField()));
		} else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
			criteria.addOrder(Order.desc(pageRequest.getSortField()));
		}

		// montamos o resultado paginado
		return new Page<>(criteria.list(), totalRows);
	}
    
    @SuppressWarnings("unchecked")
	public Page<T> listByFilter(Boolean isDeleted,  Boolean isBlocked, Long emp ,PageRequest pageRequest,String filtro , String pesquisa) {

		final Criteria criteria = this.createCriteria();

		if (isBlocked != null) {
			criteria.add(Restrictions.eq("blocked", isBlocked));
		}
		if (isDeleted !=null){
			criteria.add(Restrictions.eq("isDeleted", isDeleted));
		}
		if (emp != null){
			criteria.add(Restrictions.eq("idEmpresa", emp));
		}
		if (emp == null){
			criteria.add(Restrictions.isNull("idEmpresa"));
		}
		if (!filtro.isEmpty()){
			criteria.add(Restrictions.ilike(filtro, pesquisa, MatchMode.ANYWHERE));
		}
		
		// projetamos para pegar o total de paginas possiveis
		criteria.setProjection(Projections.count("id"));

		final Long totalRows = (Long) criteria.uniqueResult();

		// limpamos a projection para que a criteria seja reusada
		criteria.setProjection(null);
		criteria.setResultTransformer(Criteria.ROOT_ENTITY);

		// paginamos
		criteria.setFirstResult(pageRequest.getFirstResult());
		criteria.setMaxResults(pageRequest.getPageSize());

		if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
			criteria.addOrder(Order.asc(pageRequest.getSortField()));
		} else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
			criteria.addOrder(Order.desc(pageRequest.getSortField()));
		}

		// montamos o resultado paginado
		return new Page<>(criteria.list(), totalRows);
	}
    
    @SuppressWarnings("unchecked")
	public Page<T> listByFilterFilial(Boolean isDeleted,  Boolean isBlocked, Long emp ,Long filial,PageRequest pageRequest,String filtro , String pesquisa) {

		final Criteria criteria = this.createCriteria();

		if (isBlocked != null) {
			criteria.add(Restrictions.eq("blocked", isBlocked));
		}
		if (isDeleted !=null){
			criteria.add(Restrictions.eq("isDeleted", isDeleted));
		}
		if (emp != null){
			criteria.add(Restrictions.eq("idEmpresa", emp));
		}
		if (emp == null){
			criteria.add(Restrictions.isNull("idEmpresa"));
		}
		
		if (filial != null){
			criteria.add(Restrictions.eq("idFilial", emp));
		}
		if (filial == null){
			criteria.add(Restrictions.isNull("idFilial"));
		}
		if (!filtro.isEmpty()){
			criteria.add(Restrictions.ilike(filtro, pesquisa, MatchMode.ANYWHERE));
		}
		
		// projetamos para pegar o total de paginas possiveis
		criteria.setProjection(Projections.count("id"));

		final Long totalRows = (Long) criteria.uniqueResult();

		// limpamos a projection para que a criteria seja reusada
		criteria.setProjection(null);
		criteria.setResultTransformer(Criteria.ROOT_ENTITY);

		// paginamos
		criteria.setFirstResult(pageRequest.getFirstResult());
		criteria.setMaxResults(pageRequest.getPageSize());

		if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
			criteria.addOrder(Order.asc(pageRequest.getSortField()));
		} else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
			criteria.addOrder(Order.desc(pageRequest.getSortField()));
		}

		// montamos o resultado paginado
		return new Page<>(criteria.list(), totalRows);
	}
    
    @SuppressWarnings("unchecked")
	public Page<T> listByStatusFilial(Boolean isDeleted,  Boolean isBlocked, Long emp , Long filial,Boolean notaFornecedor,PageRequest pageRequest) {

		final Criteria criteria = this.createCriteria();

		if (isBlocked != null) {
			criteria.add(Restrictions.eq("blocked", isBlocked));
		}
		if (isDeleted !=null){
			criteria.add(Restrictions.eq("isDeleted", isDeleted));
		}
		if (emp != null){
			criteria.add(Restrictions.eq("idEmpresa", emp));
			if (filial != null){
				criteria.add(Restrictions.eq("idFilial", filial));
			}
			if (filial == null){
				criteria.add(Restrictions.isNull("idFilial"));
			}
		}
		if (notaFornecedor != null){
			if (notaFornecedor == false){
				criteria.add(Restrictions.eqOrIsNull("notaDeFornecedor",notaFornecedor));
			}else{
				criteria.add(Restrictions.eq("notaDeFornecedor",notaFornecedor));
			}
		}
		if (emp == null){
			criteria.add(Restrictions.and(
					Restrictions.isNull("idEmpresa"),
					Restrictions.isNull("idFilial")));
			
		}
		// projetamos para pegar o total de paginas possiveis
		criteria.setProjection(Projections.count("id"));

		final Long totalRows = (Long) criteria.uniqueResult();

		// limpamos a projection para que a criteria seja reusada
		criteria.setProjection(null);
		criteria.setResultTransformer(Criteria.ROOT_ENTITY);

		// paginamos
		criteria.setFirstResult(pageRequest.getFirstResult());
		criteria.setMaxResults(pageRequest.getPageSize());

		if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
			criteria.addOrder(Order.asc(pageRequest.getSortField()));
		} else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
			criteria.addOrder(Order.desc(pageRequest.getSortField()));
		}

		// montamos o resultado paginado
		return new Page<>(criteria.list(), totalRows);
	}
    
    /*
	 * Procura se existe o cnpj no banco da empresa.
	 * @param String cnpj 
	 * @param Long id 
	 * @return boolean
	 */
	public boolean procuraCnpj(String cnpj, Long empresa){
		final Criteria criteria = this.createCriteria();
		if (empresa != null){
			criteria.add(Restrictions.and(
				Restrictions.eq("cnpj",cnpj),
				Restrictions.eq("idEmpresa", empresa)));
		}else{
			criteria.add(Restrictions.and(
					Restrictions.eq("cnpj",cnpj),
					Restrictions.isNull("idEmpresa")));
		}
		System.out.println("ESTOU NO PROCURA CNPJ");
		if (criteria.uniqueResult() == null){
			return false;
		}else {
			return true;
		}
	}
	@SuppressWarnings("unchecked")
	public T localizaPorCnpj(String cnpj, Long empresa){
		final Criteria criteria = this.createCriteria();
		if (empresa != null){
		criteria.add(Restrictions.and(
				Restrictions.eq("cnpj",cnpj),
				Restrictions.eq("idEmpresa", empresa)));
		}else {
			criteria.add(Restrictions.and(
					Restrictions.eq("cnpj",cnpj),
					Restrictions.isNull("idEmpresa")));
		}
		return (T) criteria.uniqueResult();
	}
	@SuppressWarnings("unchecked")
	public T localizaPorCpf(String cpf, Long empresa){
		final Criteria criteria = this.createCriteria();
		if (empresa != null){
		criteria.add(Restrictions.and(
				Restrictions.eq("cpf",cpf),
				Restrictions.eq("idEmpresa", empresa)));
		}else {
			criteria.add(Restrictions.and(
					Restrictions.eq("cpf",cpf),
					Restrictions.isNull("idEmpresa")));
		}
		return (T) criteria.uniqueResult();
	}
	
	public boolean procuraCPF(String cpf, Long empresa) {
		final Criteria criteria = this.createCriteria();
		if (empresa != null){
			criteria.add(Restrictions.and(
				Restrictions.eq("cpf",cpf),
				Restrictions.eq("idEmpresa", empresa)));
		}else{
			criteria.add(Restrictions.and(
					Restrictions.eq("cpf",cpf),
					Restrictions.isNull("idEmpresa")));
		}
		System.out.println("ESTOU NO PROCURA cpf");
		if (criteria.uniqueResult() == null){
			return false;
		}else {
			return true;
		}
	}
	/**
	 * Lista em modo Lazy 
	 * 
	 * @param isDeleted ( exibe conteudo deletado ( false = exibe apenas os NÃƒO deletados / TRUE = exibe apenas os deletados )
	 * @param isBloked
	 * @param idEmpresa
	 * @param idFilial
	 * @param pageRequest ( informaÃ§Ãµes uteis para a paginaÃ§Ã£o da DATATABLE)
	 * @param filtro ( Campo que sera pesquisado )
	 * @param pesquisa ( String com o conteudo a ser pesquisando na lista
	 * @param porFilial  (True = pesquisa feita fazendo separaÃ§Ã£o entre filal e Matriz / False =
	 *  pesquisa feita exibindo TODAS as informaÃ§Ãµes SEM separaÃ§Ã£o entre filial e matriz)
	 * @return
	 */
	public Page<T> listaLazyComFiltro(Boolean isDeleted, Boolean isBloked, Long idEmpresa, Long idFilial, PageRequest pageRequest,String filtro , String pesquisa,Boolean porFilial ){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(getPersistentClass());

		Root<T> fromProdutos = criteria.from(getPersistentClass());


		List<Predicate> conditions = new ArrayList<>();

		Predicate filial = builder.equal(fromProdutos.get("idFilial"), idFilial);
		Predicate empresa = builder.equal(fromProdutos.get("idEmpresa"), idEmpresa);
		Predicate empresaNull = builder.isNull(fromProdutos.get("idEmpresa"));

		conditions.add(builder.equal(fromProdutos.get("isDeleted"), isDeleted));
		if (porFilial == true) {
			if (idFilial == null) {
				if (idEmpresa == null){
					conditions.add(empresaNull);					
				}else {
					conditions.add(empresa);
					conditions.add(builder.isNull(fromProdutos.get("idFilial")));
				}
			}else {
				if (idEmpresa == null){
					conditions.add(empresaNull);					
				}else {
					conditions.add(empresa);
					conditions.add(filial);
				}
			}
		}else {
			if (idEmpresa == null){
				conditions.add(empresaNull);
			}else{
				conditions.add(empresa);
			}
		}
		
		if (filtro != null){
			if (!filtro.isEmpty()){
				if (filtro.equalsIgnoreCase("id")) {
					conditions.add(builder.equal(fromProdutos.get(filtro), Long.valueOf(pesquisa) ));
				}else {
				conditions.add(builder.like(builder.lower(fromProdutos.<String>get(filtro)),"%"+pesquisa.toLowerCase()+"%"));
				}
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
		TypedQuery<T> typedQuery = this.getEntityManager().createQuery(criteria);

		// paginamos
		typedQuery.setFirstResult(pageRequest.getFirstResult());
		typedQuery.setMaxResults(pageRequest.getPageSize());

		// montamos o resultado paginado
		return new Page<T>(typedQuery.getResultList(), totalRows);
	}
	
}
