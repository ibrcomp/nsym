package br.com.nsym.application.producer;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

import org.picketlink.annotations.PicketLink;

import br.com.nsym.application.producer.qualifier.EmpDS;
import br.com.nsym.domain.model.security.Partition;

/**
 * Um producer para os recursos necessarios ao Picketlink:
 * 
 * {@link EntityManager} para que o PL possa gerenciar nosso modelo de seguranca
 * baseado em JPA
 * {@link Partition} para que nossa producao do gerenciador de identidades ja 
 * saia configurada de fabrica
 * 
 * Recursos do PL sao identificados pela anotacao {@link PicketLink}
 * 
 * @author Ibrahim Yousef Quatani
 *
 * @version 1.0.0
 * @since 2.0.0, 29/10/2016
 */
@ApplicationScoped
public class EntityManagerProducer {

	@PersistenceContext(unitName= "nsymDS")
	private EntityManager entityManager;

	// nao sera mais injetado, mas criado manualmente
//	@PersistenceContext(unitName= "empDS")
	private EntityManagerFactory empFactory;
	
	
	@Produces
	@PicketLink
	@Default
	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	@Produces
	@EmpDS
	public EntityManager getEmpEntityManager() {
		return getEmpEntityManagerFactory().createEntityManager();
	}
	private EntityManagerFactory getEmpEntityManagerFactory() {
		if (empFactory == null) {
			// criar uma rotina que pesquise na base de dados da ibrcomp o banco de dados do cliente e passe para os campos abaixo.
			String url = "jdbc:mysql://localhost:3306/empDS";
			String username ="root";
			String password = "9714";

			Map<String, String> configs = new HashMap<>();
			configs.put("javax.persistence.jdbc.url", url);
			configs.put("javax.persistence.jdbc.user", username);
			configs.put("javax.persistence.jdbc.password", password);

//			empFactory = Persistence.createEntityManagerFactory("empDS",configs);
			
			empFactory = Persistence.createEntityManagerFactory("empDS");
		}

		return empFactory;
	}

	public void close(@Disposes @EmpDS EntityManager em) {
		em.close();
	}

}


