package br.com.nsym.domain.model.entity.fiscal.reforma;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "reforma_ccredpres",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_ccredpres_emp_fil",
        columnNames = { "cod_ccredpres", "id_empresa", "id_filial" }
    )
)
public class CCredPres extends PersistentEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Código do crédito presumido (cCredPres – tabela oficial IBS/CBS).
     * Exemplos: "1", "2", ..., "13".
     */
    @Getter
    @Setter
    @Column(name = "cod_ccredpres", length = 5, nullable = false)
    private String codigo;

    /**
     * Descrição textual da hipótese de crédito presumido.
     * (Se sua planilha tiver coluna "Descrição", você mapeia aqui.)
     */
    @Getter
    @Setter
    @Column(name = "descricao", length = 2000)
    private String descricao;

    /**
     * Coluna "LC 214/2025" da planilha – dispositivo legal.
     */
    @Getter
    @Setter
    @Column(name = "lc214", length = 50)
    private String lc214;

    /**
     * Coluna "pAliq" da planilha.
     * Percentual (em %) da alíquota do crédito presumido (quando vier preenchido).
     */
    @Getter
    @Setter
    @Column(name = "p_aliq", precision = 10, scale = 4)
    private BigDecimal pAliq;

    /**
     * Coluna "vBC_CredPres" – orientação de base de cálculo do crédito presumido.
     */
    @Getter
    @Setter
    @Column(name = "vbc_cred_pres", precision = 18, scale = 4)
    private BigDecimal vBcCredPres;

    /**
     * Coluna "vCred Pres" – orientação de valor do crédito presumido.
     */
    @Getter
    @Setter
    @Column(name = "v_cred_pres", precision = 18, scale = 4)
    private BigDecimal vCredPres;

    /**
     * Coluna "Impedimento de CredPres" – texto explicando impedimentos de uso.
     */
    @Getter
    @Setter
    @Column(name = "impedimento_cred_pres", length = 2000)
    private String impedimentoCredPres;

    /**
     * Se a versão da planilha que você está usando trouxer datas de vigência
     * (ex: dIniVig / dFimVig), você pode mapear aqui.
     * Se não vier, esses campos simplesmente ficarão nulos.
     */
    @Getter
    @Setter
    @Column(name = "d_ini_vig")
    private LocalDate dataInicioVigencia;

    @Getter
    @Setter
    @Column(name = "d_fim_vig")
    private LocalDate dataFimVigencia;
}
