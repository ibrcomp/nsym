package br.com.nsym.application.component.table;

import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import br.com.nsym.domain.model.entity.IPersistentEntity;

/**
 * LazyDataModel generico para uso nas datatables do sistema. Como ele podemos
 * definir a carga de um datatable on-demand
 *
 * @param <T> o tipo deste model
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 2.1.0, 05/09/2015
 */
public class AbstractLazyModel<T extends IPersistentEntity> extends LazyDataModel<T> {

	private static final long serialVersionUID = -1583144244080649974L;

	private List<T> datasource;
	
	/**
     * @see LazyDataModel#load(int, int, java.util.List, java.util.Map)
     *
     * @param first
     * @param pageSize
     * @param multiSortMeta
     * @param filters
     * @return
     */
    @Override
    public List<T> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, Object> filters) {
        throw new IllegalStateException("Lazy loading not implemented");
    }

    /**
     * @see LazyDataModel#load(int, int, java.lang.String,
     * org.primefaces.model.SortOrder, java.util.Map)
     *
     * @param first
     * @param pageSize
     * @param sortField
     * @param sortOrder
     * @param filters
     * @return
     */
    @Override
    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        throw new IllegalStateException("Lazy loading not implemented");
    }
    /**
     * @see LazyDataModel#getRowKey(java.lang.Object)
     *
     * @param object
     * @return
     */
    @Override
    public Object getRowKey(T object) {
        return object.getId();
    }

    /**
     * @see LazyDataModel#getRowData(java.lang.String)
     *
     * @param rowKey
     * @return
     */
    @Override
    public T getRowData(String rowKey) {

        final Long key = Long.parseLong(rowKey);

        for (T t : this.getModelSource()) {
            if (t.getId().equals(key)) {
                return t;
            }
        }

        return null;
    }

    /**
     * @return a lista encapsulada por este model
     */
    @SuppressWarnings("unchecked")
	public List<T> getModelSource() {
        return (List<T>) this.getWrappedData();
    }

}
