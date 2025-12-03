package br.com.nsym.domain.model.entity.fiscal.nfe;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.com.nsym.domain.model.entity.tools.AbstractEmitente;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="emitente")
public class Emitente extends AbstractEmitente {
		
				
		/**
	 *
	 */
	private static final long serialVersionUID = 6394678914359127956L;

		@Getter
		@Setter
		@OneToOne(mappedBy="emitente",cascade=CascadeType.ALL)
		private Nfe nfe;
		
		@Getter
		@Setter
		@OneToOne(mappedBy = "emitente",cascade = CascadeType.ALL)
		private NfeRecebida nfRecebida;
}
