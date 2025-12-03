package br.com.nsym.domain.model.entity.tools;

import java.util.Comparator;

import org.primefaces.model.SortOrder;

public class LazySorter<T> implements Comparator<T> {

private String sortField;
    
    private SortOrder sortOrder;
    
    public LazySorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }

    @SuppressWarnings("unchecked")
	public int compare(T cliente1, T cliente2) {
        try {
			Object value1 = (T) Object.class.getField(this.sortField).get(cliente1);
            Object value2 = (T) Object.class.getField(this.sortField).get(cliente2);

            int value = ((Comparable<T>)value1).compareTo((T) value2);
            
            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        }
        catch(Exception e) {
            throw new RuntimeException();
        }
    }


}
