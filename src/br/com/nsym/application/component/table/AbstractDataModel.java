package br.com.nsym.application.component.table;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import br.com.nsym.domain.model.entity.IPersistentEntity;

public class AbstractDataModel <T extends IPersistentEntity>extends ListDataModel<T> implements SelectableDataModel<T> {

	
	public AbstractDataModel(){
		
	}
	
	 public AbstractDataModel(List<T> data) {
		// TODO Auto-generated constructor stub
		 super(data);
	}


	@Override
	    public Object getRowKey(T object) {
	        return object.getId();
	    }


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
