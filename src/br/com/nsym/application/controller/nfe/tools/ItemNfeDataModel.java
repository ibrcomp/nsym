package br.com.nsym.application.controller.nfe.tools;

import java.util.List;

import br.com.nsym.application.component.table.AbstractDataModel;
import br.com.nsym.domain.model.entity.fiscal.nfe.ItemNfe;

public class ItemNfeDataModel extends AbstractDataModel<ItemNfe> {
	
	public ItemNfeDataModel(){
		
	}
	
	public ItemNfeDataModel(List<ItemNfe> data){
		super(data);
	}
}
