package br.com.nsym.infraestrutura.configuration;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.picketlink.annotations.PicketLink;
import org.picketlink.config.SecurityConfigurationBuilder;
import org.picketlink.event.SecurityConfigurationEvent;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.config.IdentityConfigurationBuilder;
import org.picketlink.idm.credential.encoder.BCryptPasswordEncoder;
import org.picketlink.idm.credential.handler.PasswordCredentialHandler;
import org.picketlink.internal.EntityManagerContextInitializer;

import br.com.nsym.domain.model.entity.security.GrantTypeEntity;
import br.com.nsym.domain.model.entity.security.GroupMembershipTypeEntity;
import br.com.nsym.domain.model.entity.security.GroupTypeEntity;
import br.com.nsym.domain.model.entity.security.PartitionTypeEntity;
import br.com.nsym.domain.model.entity.security.PasswordTypeEntity;
import br.com.nsym.domain.model.entity.security.RelationshipIdentityTypeEntity;
import br.com.nsym.domain.model.entity.security.RelationshipTypeEntity;
import br.com.nsym.domain.model.entity.security.RoleTypeEntity;
import br.com.nsym.domain.model.entity.security.UserTypeEntity;
import br.com.nsym.domain.model.security.Authorization;
import br.com.nsym.domain.model.security.Grant;
import br.com.nsym.domain.model.security.Group;
import br.com.nsym.domain.model.security.GroupMembership;
import br.com.nsym.domain.model.security.Partition;
import br.com.nsym.domain.model.security.Role;
import br.com.nsym.domain.model.security.User;
import br.com.nsym.infraestrutura.picketlink.CustomPartitionManager;

/**
 * Configura toda infra de seguranca do sistema atraves do PicketLink
 *
 * @author Ibrahim Yousef Quatani
 *
 * @version 2.0.0
 * @since 1.1.0, 19/10/2016
 */
public class SecurityConfiguration {

    @Inject
    @Default
    private Authorization authorization;
    @Inject
    @Default
    private EntityManagerContextInitializer contextInitializer;
    

    @Produces
    @PicketLink
    public PartitionManager configureInternal() {

        final IdentityConfigurationBuilder builder = new IdentityConfigurationBuilder();
        
        
        builder.named("jpa.config")
                .stores()
                .jpa()
                .supportType(
                        User.class,
                        Role.class,
                        Group.class,
                        Partition.class)
                .supportGlobalRelationship(  
                        Grant.class,  
                        GroupMembership.class)  
                .supportCredentials(true)
                .mappedEntity(
                        RoleTypeEntity.class,
                        UserTypeEntity.class,
                        GrantTypeEntity.class,
                        GroupTypeEntity.class,
                        PasswordTypeEntity.class,
                        PartitionTypeEntity.class,
                        RelationshipTypeEntity.class,
                        GroupMembershipTypeEntity.class,
                        RelationshipIdentityTypeEntity.class)
                .addContextInitializer(this.contextInitializer)
                .setCredentialHandlerProperty(
                        PasswordCredentialHandler.PASSWORD_ENCODER, 
                        new BCryptPasswordEncoder(10));
        
        return new CustomPartitionManager(builder.build());
    }
    
    /**
     * Configuracao das regras de navegacao HTTP do sistema atraves do evento
     * de configuracado do picketlink
     * 
     * @param event o evento de configuracao
     */
    public void configureHttpSecurity(@Observes SecurityConfigurationEvent event) {
        
        final SecurityConfigurationBuilder builder = event.getBuilder();

        builder.http()
                .allPaths()
                    .authenticateWith()
                    .form()
                        .loginPage("/index.xhtml")
                        .errorPage("/index.xhtml?failure=true")
                .forPath("/logout")
                    .logout()
                    .redirectTo("/index.xhtml?faces-redirect=true")
                .forPath("/javax.faces.resource/*")
                    .unprotected()
                .forPath("/favicon.ico*")
                    .unprotected()
                .forPath("/main/entries/card/*")
                    .authorizeWith()
                        .role(this.authorization.CARD_VIEW)
                .forPath("/main/entries/contact/*")
                    .authorizeWith()
                        .role(this.authorization.CONTACT_VIEW)
                .forPath("/main/entries/costCenter/*")
                    .authorizeWith()
                        .role(this.authorization.COST_CENTER_VIEW)
                .forPath("/main/entries/wallet/*")
                    .authorizeWith()
                        .role(this.authorization.WALLET_VIEW)
                .forPath("/main/entries/movementClass/*")
                    .authorizeWith()
                        .role(this.authorization.MOVEMENT_CLASS_VIEW)
                .forPath("/main/financial/movement/*")
                    .authorizeWith()
                        .role(
                            this.authorization.MOVEMENT_VIEW, 
                            this.authorization.FIXED_MOVEMENT_VIEW)
                .forPath("/main/financial/cardInvoice/*")
                    .authorizeWith()
                        .role(
                            this.authorization.CARD_INVOICE_VIEW, 
                            this.authorization.CARD_INVOICE_HISTORIC)
                .forPath("/main/financial/transference/*")
                    .authorizeWith()
                        .role(this.authorization.BALANCE_TRANSFERENCE_VIEW)
                .forPath("/main/miscellany/closing/*")
                    .authorizeWith()
                        .role(this.authorization.CLOSING_VIEW)
                .forPath("/main/financial/formasDePagamento/*")
                    .authorizeWith()
                        .role(this.authorization.FINANCIAL_PERIOD_VIEW)
                .forPath("/main/financial/finaneiro/*")
                    .authorizeWith()
                        .role(this.authorization.FINANCEIRO_VIEW)
                .forPath("/main/fabrica/*")
                    .authorizeWith()
                        .role(this.authorization.FABRICA_VIEW)
                .forPath("/main/fabrica/cadastro/*")
                    .authorizeWith()
                        .role(this.authorization.MODELO_VIEW)
                .forPath("/main/fabrica/cadastro/*")
                    .authorizeWith()
                        .role(this.authorization.CORTADOR_VIEW)
                .forPath("/main/fabrica/cadastro/*")
                    .authorizeWith()
                        .role(this.authorization.LISTASERVICO_VIEW)
                .forPath("/main/fabrica/cadastro/*")
                    .authorizeWith()
                        .role(this.authorization.RISCO_VIEW)
                .forPath("/main/fabrica/Producao/*")
                    .authorizeWith()
                        .role(this.authorization.PRODUCAO_VIEW)
                .forPath("/main/tools/user/*")
                    .authorizeWith()
                        .role(this.authorization.USER_VIEW)
                .forPath("/main/tools/group/*")
                    .authorizeWith()
                        .role(this.authorization.GROUP_VIEW)
                .forPath("/main/tools/configuration/*")
                    .authorizeWith()
                        .role(this.authorization.CONFIGURATION_VIEW)
                .forPath("/main/cadastro/Cliente/*")
                    .authorizeWith()
                        .role(this.authorization.CLIENT_VIEW)
                .forPath("/main/cadastro/Colaborador/*")
                    .authorizeWith()
                        .role(this.authorization.COLABORADOR_VIEW)
                .forPath("/main/cadastro/Empresa/*")
                    .authorizeWith()
                        .role(this.authorization.SUPORT_VIEW) 
                 .forPath("/main/cadastro/Filial/*")
                       .authorizeWith()
                          .role(this.authorization.SUPORT_VIEW)
                 .forPath("/main/tools/cadastro/Cargo/*")
                       .authorizeWith()
                          .role(this.authorization.CARGO_VIEW)
                 .forPath("/main/tools/cadastro/Departamento/*")
                       .authorizeWith()
                          .role(this.authorization.DEPARTAMENTO_VIEW)
                 .forPath("/main/tools/cadastro/Secao/*")
                       .authorizeWith()
                          .role(this.authorization.SECAO_VIEW)      
                 .forPath("/main/tools/cadastro/SubSecao/*")
                       .authorizeWith()
                          .role(this.authorization.SUBSECAO_VIEW)
                 .forPath("/main/tools/cadastro/Tamanho/*")
                       .authorizeWith()
                          .role(this.authorization.TAMANHO_VIEW)
                 .forPath("/main/tools/cadastro/Cores/*")
                       .authorizeWith()
                          .role(this.authorization.CORES_VIEW)
                 .forPath("/main/tools/cadastro/Grades/*")
                       .authorizeWith()
                          .role(this.authorization.GRADES_VIEW)
                 .forPath("/main/cadastro/Transportadora/*")
                       .authorizeWith()
                          .role(this.authorization.TRANSPORTADORA_VIEW)
                 .forPath("/main/tributos/Tributos/*")
                       .authorizeWith()
                          .role(this.authorization.TRIBUTOS_VIEW)
                 .forPath("/main/cadastro/Fornecedor/*")
                       .authorizeWith()
                          .role(this.authorization.FORNECEDOR_VIEW)
                 .forPath("/main/cadastro/Produto/*")
                      .authorizeWith()
                         .role(this.authorization.PRODUTO_VIEW)
                 .forPath("/main/tools/cadastro/Fabricante/*")
                      .authorizeWith()
                         .role(this.authorization.FABRICANTE_VIEW)
                 .forPath("/main/fiscal/*")
                      .authorizeWith()
                         .role(this.authorization.NFE_VIEW)
                 .forPath("/main/tributos/Reforma/*")
                      .authorizeWith()
                         .role(this.authorization.REFORMATRIB_VIEW)
                 .forPath("/main/fiscal/sat/*")
                      .authorizeWith()
                         .role(this.authorization.SAT_VIEW)
                 .forPath("/main/fiscal/tools/*")
                      .authorizeWith()
                         .role(this.authorization.NATUREZAOPERACAO_VIEW)
                 .forPath("/main/estoque/*")
                      .authorizeWith()
                         .role(this.authorization.ESTOQUE_VIEW)
                .forPath("/main/tools/message/sent/*")
                    .authorizeWith()
                        .role(this.authorization.MESSAGE_SEND)
                .forPath("/main/Vendas/pdv/*")
                     .authorizeWith()
                        .role(this.authorization.VENDA_VIEW)
                .forPath("/main/Admin/Config/*")
                     .authorizeWith()
                        .role(this.authorization.ADM_VIEW) 
                .forPath("/main/financial/caixa/*")
                     .authorizeWith()
                        .role(this.authorization.CAIXA_VIEW)
                .forPath("/main/gerencia/*")
                     .authorizeWith()
                        .role(this.authorization.GERENCIA_VIEW)
                .forPath("/main/pdv/TiposDeVenda/*")
                     .authorizeWith()
                        .role(this.authorization.TRANSACTION_VIEW);
    }
}
