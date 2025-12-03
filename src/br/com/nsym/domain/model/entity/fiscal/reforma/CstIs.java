package br.com.nsym.domain.model.entity.fiscal.reforma;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "reforma_cst_is",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_cst_is",
        columnNames = { "cst_is","id_empresa", "id_filial" }
    )
)
public class CstIs extends PersistentEntity {

    private static final long serialVersionUID = 1L;

    @Getter @Setter
    @Column(name = "cst_is", nullable = false, length = 4)
    private String cstIs;

    @Getter @Setter
    @Column(name = "descricao", length = 500)
    private String descricao;

    @Getter @Setter
    @Column(name = "ind_vigente")
    private Boolean indVigente = Boolean.TRUE;

}
