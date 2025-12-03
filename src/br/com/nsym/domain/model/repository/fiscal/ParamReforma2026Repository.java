package br.com.nsym.domain.model.repository.fiscal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.nsym.domain.model.entity.fiscal.ParamReforma2026;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class ParamReforma2026Repository extends GenericRepositoryEmpDS<ParamReforma2026, Long>{
	
    private static final long serialVersionUID = -3705530691323177291L;

    public List<ParamReforma2026> listarAtivosOrdenados(Long pegaIdEmpresa, Long pegaIdFilial) {
    	CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ParamReforma2026> cq = cb.createQuery(ParamReforma2026.class);
        Root<ParamReforma2026> root = cq.from(ParamReforma2026.class);
        
        List<Predicate> preds = new ArrayList<>();
        
        if (pegaIdEmpresa == null){
        	preds.add(cb.isNull(root.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				preds.add(cb.equal(root.get("idEmpresa"),pegaIdEmpresa));
				preds.add(cb.isNull(root.get("idFilial")));
			}else{
				preds.add(cb.equal(root.get("idEmpresa"),pegaIdEmpresa));
				preds.add(cb.equal(root.get("idFilial"),pegaIdFilial));
			}
		}
        preds.add(cb.isTrue(root.get("ativo")));

        cq.select(root)
          .where((preds.toArray(new Predicate[0])))
          .orderBy(cb.desc(root.get("prioridade")), cb.desc(root.get("id")));
        
        TypedQuery<ParamReforma2026> typedQuery = this.getEntityManager().createQuery(cq);
        return typedQuery.getResultList();
    }

    public List<ParamReforma2026> buscarPorFiltros(
            String ncmPrefix, String cfop, String ufOrig, String ufDest, String cnae,
            Boolean ativo, LocalDate vigenciaEm,
            Integer offset, Integer limit, Boolean ordenarPorPrioridadeDesc,
            Long pegaIdEmpresa, Long pegaIdFilial) {

    	CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ParamReforma2026> cq = cb.createQuery(ParamReforma2026.class);
        Root<ParamReforma2026> root = cq.from(ParamReforma2026.class);

        List<Predicate> preds = new ArrayList<>();

        if (notBlank(ncmPrefix)) preds.add(cb.equal(root.get("ncmPrefix"), ncmPrefix));
        if (notBlank(cfop))      preds.add(cb.equal(root.get("cfop"), cfop));
        if (notBlank(ufOrig))    preds.add(cb.equal(root.get("ufOrig"), ufOrig));
        if (notBlank(ufDest))    preds.add(cb.equal(root.get("ufDest"), ufDest));
        if (notBlank(cnae))      preds.add(cb.equal(root.get("cnae"), cnae));

        if (ativo != null) {
            preds.add(ativo ? cb.isTrue(root.get("ativo")) : cb.isFalse(root.get("ativo")));
        }
        if (vigenciaEm != null) {
            Predicate iniOk = cb.or(cb.isNull(root.get("vigenciaIni")), cb.lessThanOrEqualTo(root.get("vigenciaIni"), vigenciaEm));
            Predicate fimOk = cb.or(cb.isNull(root.get("vigenciaFim")), cb.greaterThanOrEqualTo(root.get("vigenciaFim"), vigenciaEm));
            preds.add(cb.and(iniOk, fimOk));
        }
        if (pegaIdEmpresa == null){
        	preds.add(cb.isNull(root.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				preds.add(cb.equal(root.get("idEmpresa"),pegaIdEmpresa));
				preds.add(cb.isNull(root.get("idFilial")));
			}else{
				preds.add(cb.equal(root.get("idEmpresa"),pegaIdEmpresa));
				preds.add(cb.equal(root.get("idFilial"),pegaIdFilial));
			}
		}

        cq.select(root).where(preds.toArray(new Predicate[0]));
        if (Boolean.TRUE.equals(ordenarPorPrioridadeDesc))
            cq.orderBy(cb.desc(root.get("prioridade")), cb.desc(root.get("id")));
        else
            cq.orderBy(cb.asc(root.get("id")));

        TypedQuery<ParamReforma2026> q = this.getEntityManager().createQuery(cq);
        if (offset != null && offset > 0) q.setFirstResult(offset);
        if (limit != null && limit > 0) q.setMaxResults(limit);
        return q.getResultList();
    }

    public List<ParamReforma2026> preSelecionarPorContextoSimples(String ufOrig, String ufDest, LocalDate dataOperacao,Long pegaIdEmpresa, Long pegaIdFilial) {
    	CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ParamReforma2026> cq = cb.createQuery(ParamReforma2026.class);
        Root<ParamReforma2026> root = cq.from(ParamReforma2026.class);

        List<Predicate> preds = new ArrayList<>();
        preds.add(cb.isTrue(root.get("ativo")));
        if (notBlank(ufOrig)) preds.add(cb.or(cb.isNull(root.get("ufOrig")), cb.equal(root.get("ufOrig"), ufOrig)));
        if (notBlank(ufDest)) preds.add(cb.or(cb.isNull(root.get("ufDest")), cb.equal(root.get("ufDest"), ufDest)));
        if (dataOperacao != null) {
            Predicate iniOk = cb.or(cb.isNull(root.get("vigenciaIni")), cb.lessThanOrEqualTo(root.get("vigenciaIni"), dataOperacao));
            Predicate fimOk = cb.or(cb.isNull(root.get("vigenciaFim")), cb.greaterThanOrEqualTo(root.get("vigenciaFim"), dataOperacao));
            preds.add(cb.and(iniOk, fimOk));
        }
        if (pegaIdEmpresa == null){
        	preds.add(cb.isNull(root.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				preds.add(cb.equal(root.get("idEmpresa"),pegaIdEmpresa));
				preds.add(cb.isNull(root.get("idFilial")));
			}else{
				preds.add(cb.equal(root.get("idEmpresa"),pegaIdEmpresa));
				preds.add(cb.equal(root.get("idFilial"),pegaIdFilial));
			}
		}

        cq.select(root).where(preds.toArray(new Predicate[0]))
          .orderBy(cb.desc(root.get("prioridade")), cb.desc(root.get("id")));

        TypedQuery<ParamReforma2026> q = this.getEntityManager().createQuery(cq);
        
        return q.getResultList();
    }

    private boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

}
