package br.com.nsym.domain.model.entity.cadastro;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.tools.Uf;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Endereco extends PersistentEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
    @Setter
//    @NotNull(message = "{cep.name}")
    @Column(name = "cep", unique = true, nullable = false, length = 9)
	private String cep;
	
	@Getter
    @Setter
//    @NotNull(message = "{cep.logradouro}")
    @Column(name = "logradouro", nullable = false, length = 100)
	private String logra;
	
	@Getter
    @Setter
	@Column(name="complemento")
	private String complemento;
	
	@Getter
	@Setter
    private String bairro;
	
	@Getter
	@Setter
//	@NotNull(message = "{cep.localidade}")
    private String localidade;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "uf", nullable = false)
    private Uf uf;
	
	@Getter
	@Setter
    private String ibge;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="endereco")
	private List<EndComplemento> endComplemento = new ArrayList<EndComplemento>();

	@Override
	public String toString() {
		return "Endereco [cep=" + cep + ", logradouro=" + logra + ", complemento=" + complemento + ", bairro="
				+ bairro + ", localidade=" + localidade + ", uf=" + uf + ", ibge=" + ibge + ", endComplemento="
				+ endComplemento + ", getId()=" + getId() + "]";
	}


	
}
