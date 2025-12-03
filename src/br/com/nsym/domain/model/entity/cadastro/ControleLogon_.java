package br.com.nsym.domain.model.entity.cadastro;

import br.com.nsym.domain.model.entity.PersistentEntityLogon_;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2022-07-28T04:05:43.947-0300")
@StaticMetamodel(ControleLogon.class)
public class ControleLogon_ extends PersistentEntityLogon_ {
	public static volatile SingularAttribute<ControleLogon, String> idReferencia;
	public static volatile SingularAttribute<ControleLogon, String> nome;
	public static volatile SingularAttribute<ControleLogon, Boolean> logado;
	public static volatile SingularAttribute<ControleLogon, Date> dataLogin;
	public static volatile SingularAttribute<ControleLogon, Date> expiraLogin;
}
