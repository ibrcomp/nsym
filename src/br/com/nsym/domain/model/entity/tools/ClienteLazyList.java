package br.com.nsym.domain.model.entity.tools;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.primefaces.model.SortOrder;

import br.com.nsym.application.component.table.AbstractLazyModel;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.domain.model.entity.cadastro.Cliente;

public class ClienteLazyList extends AbstractLazyModel<Cliente> {

	/**
	 *
	 */
	private static final long serialVersionUID = -2716458810686592661L;

	private List<Cliente> data;
	
	@SuppressWarnings("unused")
	private Page<Cliente> data1;



	public ClienteLazyList(List<Cliente> data) {
		this.data = data;
	}

	public ClienteLazyList(Page<Cliente> listGlobal) {
		this.data1 = listGlobal;
	}

	@Override  
	public Cliente getRowData(String rowKey) {  
		//In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  


		for(Cliente clienteNovo : data) {  
			if(clienteNovo.getRazaoSocial().equals(rowKey)){
				System.out.println("Igualdade OK");
				return clienteNovo; 
			}
		}  

		return null ;  
	}  

	@Override  
	public Object getRowKey(Cliente cliente) {  
		return cliente.getRazaoSocial();  
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Cliente> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, Object> filters) {

		//filter
		for(Cliente cliente : data) {
			boolean match = true;

			for(Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
				try {
					String filterProperty = it.next();
					String filterValue =  (String) filters.get(filterProperty);
					Object fieldValue = String.valueOf(cliente.getClass().getField(filterProperty).get(cliente));

					if(filterValue == null ||  ((String) fieldValue).startsWith(filterValue.toString())) {
						match = true;
					}
					else {
						match = false;
						break;
					}
				} catch(Exception e) {
					match = false;
				} 
			}

			if(match) {
				data.add(cliente);
			}
		}

		//sort
		if(sortField != null) {
			Collections.sort(data, new LazySorter(sortField, sortOrder));
		}

		//rowCount
		int dataSize = data.size();
		this.setRowCount(dataSize);

		//paginate
		if(dataSize > pageSize) {
			try {
				return data.subList(first, first + pageSize);
			}
			catch(IndexOutOfBoundsException e) {
				return data.subList(first, first + (dataSize % pageSize));
			}
		}
		else {
			return data;
		}
	}

//	@OVERRIDE
//	PUBLIC LIST<CLIENTE> LOAD(INT POSICAOPRIMEIRALINHA,
//			INT MAXIMOPORPAGINA,                             STRING ORDERNARPELOCAMPO,
//			SORTORDER ORDERNARASCOUDESC,
//			MAP<STRING, STRING> FILTROS) {
//
//		STRING ORDERNACAO = ORDERNARASCOUDESC.TOSTRING();
//
//		IF(SORTORDER.UNSORTED.EQUALS(ORDERNARASCOUDESC)){
//			ORDERNACAO = SORTORDER.ASCENDING.TOSTRING();
//		}
//
//
//		CLIENTES = GETDAO().BUSCAPORPAGINACAO(POSICAOPRIMEIRALINHA,
//				MAXIMOPORPAGINA,
//				ORDERNARPELOCAMPO,
//				ORDERNACAO, FILTROS);
//
//		// TOTAL ENCONTRADO NO BANCO DE DADOS, CASO O FILTRO ESTEJA PREENCHIDO DISPARA A CONSULTA NOVAMENTE
//		IF (GETROWCOUNT() <= 0 || (FILTROS != NULL && !FILTROS.ISEMPTY())) {
//			SETROWCOUNT(GETDAO().COUNTALL(FILTROS));
//		}
//
//		// QUANTIDADE A SER EXIBIDA EM CADA P�GINA
//		SETPAGESIZE(MAXIMOPORPAGINA);
//
//		RETURN CLIENTES;
//	}

//	private ClienteDao getDAO() {
//		return AbstractClienteDao.getClienteDao();
//	}


	@Override
	public void setRowIndex(int rowIndex) {
		// solu��o para evitar ArithmeticException
		if (rowIndex == -1 || getPageSize() == 0) {
			super.setRowIndex(-1);
		}
		else
			super.setRowIndex(rowIndex % getPageSize());
	}
}

