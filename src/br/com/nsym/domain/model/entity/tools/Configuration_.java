package br.com.nsym.domain.model.entity.tools;

import br.com.nsym.domain.misc.ModeloImpressoraAcbr;
import br.com.nsym.domain.model.entity.PersistentEntity_;
import br.com.nsym.domain.model.entity.security.UserTypeEntity;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2025-01-05T12:51:01.742-0300")
@StaticMetamodel(Configuration.class)
public class Configuration_ extends PersistentEntity_ {
	public static volatile SingularAttribute<Configuration, UserTypeEntity> user;
	public static volatile SingularAttribute<Configuration, Boolean> alteraPrecoNFeAvulso;
	public static volatile SingularAttribute<Configuration, Boolean> alteraPrecoSatAvulso;
	public static volatile SingularAttribute<Configuration, Boolean> alteraPrecoVenda;
	public static volatile SingularAttribute<Configuration, Boolean> alteraPrecoTransferencia;
	public static volatile SingularAttribute<Configuration, Boolean> resumoCupomPdv;
	public static volatile SingularAttribute<Configuration, Boolean> cabecalhoPDV;
	public static volatile SingularAttribute<Configuration, String> portaUsbVendaPdv;
	public static volatile SingularAttribute<Configuration, BigDecimal> quantidadePadraoPDV;
	public static volatile SingularAttribute<Configuration, BigDecimal> quantViaVenda;
	public static volatile SingularAttribute<Configuration, ModeloImpressoraAcbr> impressoraPdv;
	public static volatile SingularAttribute<Configuration, String> portaACBR;
	public static volatile SingularAttribute<Configuration, String> ipACBR;
	public static volatile SingularAttribute<Configuration, Long> transacaoPadrao;
	public static volatile SingularAttribute<Configuration, Boolean> desconto;
	public static volatile SingularAttribute<Configuration, BigDecimal> porcentagemDesconto;
	public static volatile SingularAttribute<Configuration, Boolean> descontoPDV;
	public static volatile SingularAttribute<Configuration, String> mensPDV;
	public static volatile SingularAttribute<Configuration, Boolean> fantasia;
	public static volatile SingularAttribute<Configuration, Boolean> vendaCaixa;
	public static volatile SingularAttribute<Configuration, Boolean> cupomPDF;
	public static volatile SingularAttribute<Configuration, Boolean> alteraPrecoServico;
}
