package br.com.nsym.infraestrutura.configuration;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.query.IdentityQuery;
import org.picketlink.idm.query.IdentityQueryBuilder;

import br.com.nsym.domain.model.security.Authorization;
import br.com.nsym.domain.model.security.Grant;
import br.com.nsym.domain.model.security.Group;
import br.com.nsym.domain.model.security.GroupMembership;
import br.com.nsym.domain.model.security.Partition;
import br.com.nsym.domain.model.security.Role;
import br.com.nsym.domain.model.security.User;

/**
 * Classe de inicializacao do modelo de seguranca do sistema, por ela toda o
 * mecanismo de seguranca sera inicializado para uso no sistema
 *
 * @author Ibrahim Yousef Quatani
 *
 * @version 2.0.0
 * @since 1.1.0, 19/10/2016
 */
@Startup
@Singleton
public class SecurityInitializer {

    private IdentityManager identityManager;

    @Inject
    private Authorization authorization;
    @Inject
    private PartitionManager partitionManager;

    private static final String DEFAULT_ADMIN_USER = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "9714";
    private static final String DEFAULT_ADMIN_GROUP = "Administradores";

    /**
     * Carga inicial do modelo de seguranca da aplicacao
     */
    @PostConstruct
    protected void initialize() {

        // cria ou recupera do banco a particao
        final Partition partition = this.checkPartition();

        // cria o gestor de identidades
        this.identityManager = this.partitionManager
                .createIdentityManager(partition);

        // checamos se todas as roles estao dentro do sistema
        for (String role : this.authorization.listAuthorizations()) {
            if (!this.hasRole(role)) {
                this.identityManager.add(new Role(role));
            }
        }

        // checamos se existe o grupo default
        if (!this.hasGroup(DEFAULT_ADMIN_GROUP)) {
            this.identityManager.add(new Group(DEFAULT_ADMIN_GROUP));
        }

        // checa se existe o usuario admin
        if (!this.hasUser(DEFAULT_ADMIN_USER)) {

            final User user = new User(DEFAULT_ADMIN_USER);

            user.setName("Administrador");
            user.setCreatedDate(new Date());
            user.setEnabled(true);
            user.setExpirationDate(null);
            user.setEmail("ibrahim@ibrcomp.com.br");
            
            this.identityManager.add(user);

            this.identityManager.updateCredential(
                    user, new Password(DEFAULT_ADMIN_PASSWORD));

            // setamos agora as permissoes no grupo
            final Group group = this.getGroup(DEFAULT_ADMIN_GROUP);

            // criamos um gerenciador de relacionamentos
            final RelationshipManager relationshipManager
                    = this.partitionManager.createRelationshipManager();

            // adicionamos no grupo, todas as roles do sistema
            for (Role role : this.getRoles()) {
                relationshipManager.add(new Grant(role, group));
            }

            // garantimos ao admin que ele faz parte do grupo administradores
            relationshipManager.add(new GroupMembership(group, user));
        }
    }

    /**
     * Checamos se a particao de default de seguranca foi criada, se nao criamos
     */
    private Partition checkPartition() {

        Partition partition = this.partitionManager.getPartition(
                Partition.class, Partition.DEFAULT);

        if (partition == null) {
            partition = new Partition(Partition.DEFAULT);
            this.partitionManager.add(partition);
        }

        return partition;
    }

    /**
     * Verifica se o usuario informado existe ou nao
     *
     * @param user o usuario a ser buscado
     * @return se o usuario existe ou nao
     */
    private boolean hasUser(String user) {

        final IdentityQueryBuilder queryBuilder = this.identityManager.getQueryBuilder();
        final IdentityQuery<User> query = queryBuilder.createIdentityQuery(User.class);

        query.where(queryBuilder.equal(User.USER_NAME, user));

        return !query.getResultList().isEmpty();
    }
    
    /**
     * Verifica se o usuario informado já esta logado no sistema
     * @param user o usuario a ser buscado
     * @return se o usuario está ou não logado
     */
    
    public boolean hasUserLogon(String user) {

        final IdentityQueryBuilder queryBuilder = this.identityManager.getQueryBuilder();
        final IdentityQuery<User> query = queryBuilder.createIdentityQuery(User.class);

        query.where(queryBuilder.equal(User.LOGADO, user));

        return !query.getResultList().isEmpty();
    }

    /**
     * Verifica se o grupo informado existe ou nao
     *
     * @param group o grupo a ser verificado
     * @return se o grupo existe ou nao
     */
    private boolean hasGroup(String group) {

        final IdentityQueryBuilder queryBuilder = this.identityManager.getQueryBuilder();
        final IdentityQuery<Group> query = queryBuilder.createIdentityQuery(Group.class);

        query.where(queryBuilder.equal(Group.NAME, group));

        return !query.getResultList().isEmpty();
    }

    /**
     * Verifica se a role existe ou nao
     *
     * @param role a role a ser verificada
     * @return se a role existe ou nao
     */
    private boolean hasRole(String role) {

        final IdentityQueryBuilder queryBuilder = this.identityManager.getQueryBuilder();
        final IdentityQuery<Role> query = queryBuilder.createIdentityQuery(Role.class);

        query.where(queryBuilder.equal(Role.AUTHORIZATION, role));

        return !query.getResultList().isEmpty();
    }

    /**
     * Busca um grupo em especifico
     *
     * @param group o grupo a ser buscado
     * @return o grupo encontrado
     */
    private Group getGroup(String group) {

        final IdentityQueryBuilder queryBuilder = this.identityManager.getQueryBuilder();
        final IdentityQuery<Group> query = queryBuilder.createIdentityQuery(Group.class);

        query.where(queryBuilder.equal(Group.NAME, group));

        return query.getResultList().get(0);
    }

    /**
     * @return todas as roles do sistema
     */
    private List<Role> getRoles() {

        final IdentityQueryBuilder queryBuilder = this.identityManager.getQueryBuilder();
        final IdentityQuery<Role> query = queryBuilder.createIdentityQuery(Role.class);

        return query.getResultList();
    }
}
