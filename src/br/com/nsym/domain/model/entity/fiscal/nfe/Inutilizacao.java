package br.com.nsym.domain.model.entity.fiscal.nfe;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="inutilizacao")
public class Inutilizacao extends PersistentEntity{
	
	/**
	 *
	 */
	private static final long serialVersionUID = -2107154735313001495L;

	@Getter
	@Setter
	private String cJustificativa;
	
	@Getter
	@Setter
	private LocalDate nAno;
	
	@Getter
	@Setter 
	private String nModelo;
	
	@Getter
	@Setter 
	private String nSerie;
	
	@Getter
	@Setter 
	private String nNumInicial;
	
	@Getter
	@Setter 
	private String nNumFinal;
	
	@Getter
	@Setter
	@Lob
	private String respostaAcbrEvento;
	
	@Getter
	@Setter
	private String caminhoEvento;
	
	@Getter
	@Setter
	private String protocolo;
	
	@Getter
	@Setter
	private String xMotivo;
	
	@Getter
	@Setter
	private String cStat;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="inutilizada",cascade=CascadeType.REMOVE)
	private List<Nfe> listaInutilizada = new ArrayList<>();

	
}
