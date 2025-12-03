package br.com.nsym.domain.model.repository.tools;

import javax.enterprise.context.Dependent;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.model.entity.tools.Message;
import br.com.nsym.domain.model.repository.GenericRepository;
import br.com.nsym.domain.model.security.User;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 2.2.0, 24/02/2016
 */
@Dependent
public class MessageRepository extends GenericRepository<Message, Long> implements IMessageRepository {

    /**
     * 
     * @param sender
     * @param filter
     * @param pageRequest
     * @return 
     */
    @Override
    public Page<Message> listSent(User sender, String filter, PageRequest pageRequest) {

        final Criteria criteria = this.createCriteria();

        if (filter != null) {
            criteria.add(Restrictions.or(
                    Restrictions.ilike("title", "%" + filter + "%"),
                    Restrictions.ilike("content", "%" + filter + "%")
            ));
        }
        
        criteria.add(Restrictions.eq("deleted", false));
        criteria.add(Restrictions.eq("sender", sender));
        
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
    
    
}
