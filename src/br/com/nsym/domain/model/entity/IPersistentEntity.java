package br.com.nsym.domain.model.entity;

import java.io.Serializable;

/**
 * Interface que define os metodos minimos que uma entidade deva possuir para
 * ser considerada uma entitdade valida na regra de negocios deste sistema
 *
 * @param <T> qualquer coisa que seja serializavel
 *
 * @author Ibrahim Yousef Quatani
 *
 * @version 1.0.0
 * @since 1.0.0, 19/10/2016
 */
public interface IPersistentEntity<T extends Serializable> {

    /**
     * Getter para o ID da entidade
     *
     * @return o id da entidade
     */
    public T getId();

    /**
     * Metodo que indica se uma entidade ja foi ou nao persistida (salva)
     *
     * @return se a entidade ja foi persistida, retorna <code>true</code>
     * indicando
     * que a mesma ja foi salva se nao retorna <code>false</code>
     */
    public boolean isSaved();
    
}
