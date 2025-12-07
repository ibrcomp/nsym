package br.com.nsym.domain.model.security;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Named;

import lombok.Getter;

/**
 * Mapeamento das permissoes individuais do sistema
 *
 * @author Ibrahim Yousef Quatani
 *
 * @version 1.0.0
 * @since 2.2.0, 22/10/2016
 */
@Named
@Dependent
public class Authorization {
	
	@Getter
	@AuthorizationGroup("authority.fiscal_naturezaOperacao")
	public final String NATUREZAOPERACAO_VIEW = "authority.fiscal_naturezaOperacao.acess";
	
	@Getter
	@AuthorizationGroup("authority.fiscal_naturezaOperacao")
	public final String NATUREZAOPERACAO_INSERT = "authority.fiscal_naturezaOperacao.add";
	
	@Getter
	@AuthorizationGroup("authority.fiscal_naturezaOperacao")
	public final String NATUREZAOPERACAO_DELETE = "authority.fiscal_naturezaOperacao.delete";
	
	@Getter
	@AuthorizationGroup("authority.estoque")
	public final String ESTOQUE_VIEW = "authority.estoque.acess";
	
	@Getter
	@AuthorizationGroup("authority.estoque")
	public final String ESTOQUE_INSERT = "authority.estoque.add";
	
	@Getter
	@AuthorizationGroup("authority.estoque_recebimento")
	public final String ESTOQUE_DELETE = "authority.estoque.delete";

	@Getter
	@AuthorizationGroup("authority.estoque_produto")
	public final String ESTOQUE_PRODUTO_VIEW = "authority.estoque_produto.acess";
	
	@Getter
	@AuthorizationGroup("authority.estoque_produto")
	public final String ESTOQUE_PRODUTO_INSERT = "authority.estoque_produto.add";
	
	@Getter
	@AuthorizationGroup("authority.estoque_produto")
	public final String ESTOQUE_PRODUTO_DELETE = "authority.estoque_produto.delete";
	
	@Getter
	@AuthorizationGroup("authority.estoque_produto")
	public final String ESTOQUE_PRODUTO_REPORT = "authority.estoque_produto.report";
	
	@Getter
	@AuthorizationGroup("authority.estoque_recebimento")
	public final String ESTOQUE_RECEBIMENTO_VIEW = "authority.estoque_recebimento.acess";
	
	@Getter
	@AuthorizationGroup("authority.estoque_recebimento")
	public final String ESTOQUE_RECEBIMENTO_INSERT = "authority.estoque_recebimento.add";
	
	@Getter
	@AuthorizationGroup("authority.estoque")
	public final String ESTOQUE_RECEBIMENTO_DELETE = "authority.estoque_recebimento.delete";
	
	
	
	@Getter
	@AuthorizationGroup("authority.fiscal_sat")
	public final String SAT_VIEW = "authority.fiscal_sat.acess";
	
	@Getter
	@AuthorizationGroup("authority.fiscal_sat")
	public final String SAT_INSERT = "authority.fiscal_sat.add";
	
	@Getter
	@AuthorizationGroup("authority.fiscal_sat")
	public final String SAT_DELETE = "authority.fiscal_sat.delete";
	
//	----------------------------------------------------------
	@Getter
	@AuthorizationGroup("authority.fiscal_nfe")
	public final String NFE_VIEW = "authority.fiscal_nfe.acess";
	
	@Getter
	@AuthorizationGroup("authority.fiscal_nfe")
	public final String NFE_INSERT = "authority.fiscal_nfe.add";
	
	@Getter
	@AuthorizationGroup("authority.fiscal_nfe")
	public final String NFE_DELETE = "authority.fiscal_nfe.delete";
	
//  ----------------------------------------------------------	
	
	@Getter
	@AuthorizationGroup("authority.financa")
	public final String FINANCEIRO_VIEW = "authority.financa.view";
	
	@Getter
	@AuthorizationGroup("authority.financa")
	public final String FINANCEIRO_DELETE = "authority.financa.delete";
	
	@Getter
	@AuthorizationGroup("authority.financa")
	public final String FINANCEIRO_INSERT = "authority.financa.insert";
	
	@Getter
	@AuthorizationGroup("authority.financa")
	public final String FINANCEIRO_EDIT = "authority.financa.edit";
	
	@Getter
	@AuthorizationGroup("authority.financa")
	public final String FINANCEIRO_PAY = "authority.financa.pay";
	
	@Getter
	@AuthorizationGroup("authority.financa")
	public final String FINANCEIRO_REPORT = "authority.financa.report";
	
//	--------------------------------------------------------------
	
	@Getter
	@AuthorizationGroup("authority.venda_allinone")
	public final String VENDA_VIEW = "authority.venda_allinone.acess";
	
	@Getter
	@AuthorizationGroup("authority.venda_allinone")
	public final String VENDA_INSERT = "authority.venda_allinone.add";
	
	@Getter
	@AuthorizationGroup("authority.venda_allinone")
	public final String VENDA_DELETE = "authority.venda_allinone.delete";
	
	@Getter
	@AuthorizationGroup("authority.venda_allinone")
	public final String VENDA_EDIT = "authority.venda_allinone.edit";
	
	@Getter
	@AuthorizationGroup("authority.venda_allinone")
	public final String VENDA_REPORT = "authority.venda_allinone.report";
	
	@Getter
	@AuthorizationGroup("authority.venda_allinone")
	public final String VENDA_LIST = "authority.venda_allinone.list";
	
//  ----------------------------------------------------------	
	
	@Getter
	@AuthorizationGroup("authority.caixa_recebimento")
	public final String CAIXA_VIEW = "authority.caixa_recebimento.acess";
	
	@Getter
	@AuthorizationGroup("authority.caixa_recebimento")
	public final String CAIXA_INSERT = "authority.caixa_recebimento.add";
	
	@Getter
	@AuthorizationGroup("authority.caixa_recebimento")
	public final String CAIXA_DELETE = "authority.caixa_recebimento.delete";
	
	@Getter
	@AuthorizationGroup("authority.caixa_recebimento")
	public final String CAIXA_EDIT = "authority.caixa_recebimento.edit";
	
//  ----------------------------------------------------------	
	
	@Getter
	@AuthorizationGroup("authority.gerencia")
	public final String GERENCIA_VIEW = "authority.gerencia.acess";
					
	@Getter
	@AuthorizationGroup("authority.gerencia")
	public final String GERENCIA_INSERT = "authority.gerencia.add";
					
	@Getter
	@AuthorizationGroup("authority.gerencia")
	public final String GERENCIA_DELETE = "authority.gerencia.delete";

	@Getter
	@AuthorizationGroup("authority.gerencia")
	public final String GERENCIA_EDIT = "authority.gerencia.edit";

	@Getter
	@AuthorizationGroup("authority.gerencia")
	public final String GERENCIA_REPORT = "authority.gerencia.report";
	
//  -------------------------------------------------------------
	
	@Getter
	@AuthorizationGroup("authority.venda_transaction")
	public final String TRANSACTION_VIEW = "authority.venda_transaction.acess";
	
	@Getter
	@AuthorizationGroup("authority.venda_transaction")
	public final String TRANSACTION_INSERT = "authority.venda_transaction.add";
	
	@Getter
	@AuthorizationGroup("authority.venda_transaction")
	public final String TRANSACTION_DELETE = "authority.venda_transaction.delete";
//  ----------------------------------------------------------	
	
	@Getter
	@AuthorizationGroup("authority.fiscal_cfop")
	public final String CFOP_VIEW = "authority.fiscal_cfop.acess";
	
	@Getter
	@AuthorizationGroup("authority.fiscal_cfop")
	public final String CFOP_INSERT = "authority.fiscal_cfop.add";
	
	@Getter
	@AuthorizationGroup("authority.fiscal_cfop")
	public final String CFOP_DELETE = "authority.fiscal_cfop.delete";
	
	@Getter
	@AuthorizationGroup("authority.fiscal_ncm")
	public final String NCM_VIEW = "authority.fiscal_ncm.acess";
	
	@Getter
	@AuthorizationGroup("authority.fiscal_ncm")
	public final String NCM_INSERT = "authority.fiscal_ncm.add";
	
	@Getter
	@AuthorizationGroup("authority.fiscal_ncm")
	public final String NCM_DELETE = "authority.fiscal_ncm.delete";
	
	@Getter
	@AuthorizationGroup("authority.fiscal_tributos")
	public final String TRIBUTOS_VIEW = "authority.fiscal_tributos.acess";
	
	@Getter
	@AuthorizationGroup("authority.fiscal_tributos")
	public final String TRIBUTOS_INSERT = "authority.fiscal_tributos.add";
	
	@Getter
	@AuthorizationGroup("authority.fiscal_tributos")
	public final String TRIBUTOS_DELETE = "authority.fiscal_tributos.delete";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_fabricante")
	public final String FABRICANTE_VIEW = "authority.cadastro_fabricante.acess";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_fabricante")
	public final String FABRICANTE_INSERT = "authority.cadastro_fabricante.add";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_fabricante")
	public final String FABRICANTE_DELETE = "authority.cadastro_fabricante.delete";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_departamento")
	public final String DEPARTAMENTO_VIEW = "authority.cadastro_departamento.acess";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_departamento")
	public final String DEPARTAMENTO_INSERT = "authority.cadastro_departamento.add";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_departamento")
	public final String DEPARTAMENTO_DELETE = "authority.cadastro_departamento.delete";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_secao")
	public final String SECAO_VIEW = "authority.cadastro_secao.acess";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_secao")
	public final String SECAO_INSERT = "authority.cadastro_secao.add";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_secao")
	public final String SECAO_DELETE = "authority.cadastro_secao.delete";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_subsecao")
	public final String SUBSECAO_INSERT = "authority.cadastro_subsecao.add";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_subsecao")
	public final String SUBSECAO_DELETE = "authority.cadastro_subsecao.delete";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_subsecao")
	public final String SUBSECAO_VIEW = "authority.cadastro_subsecao.acess";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_tamanho")
	public final String TAMANHO_INSERT = "authority.cadastro_tamanho.add";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_tamanho")
	public final String TAMANHO_DELETE = "authority.cadastro_tamanho.delete";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_tamanho")
	public final String TAMANHO_VIEW = "authority.cadastro_tamanho.acess";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_cores")
	public final String CORES_INSERT = "authority.cadastro_cores.add";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_cores")
	public final String CORES_DELETE = "authority.cadastro_cores.delete";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_cores")
	public final String CORES_VIEW = "authority.cadastro_cores.acess";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_grades")
	public final String GRADES_INSERT = "authority.cadastro_grades.add";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_grades")
	public final String GRADES_DELETE = "authority.cadastro_grades.delete";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_grades")
	public final String GRADES_VIEW = "authority.cadastro_grades.acess";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_produto")
	public final String PRODUTO_VIEW = "authority.cadastro_produto.acess";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_produto")
	public final String PRODUTO_INSERT = "authority.cadastro_produto.add";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_produto")
	public final String PRODUTO_DELETE = "authority.cadastro_produto.delete";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_di")
	public final String DI_VIEW = "authority.cadastro_di.acess";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_di")
	public final String DI_INSERT = "authority.cadastro_di.add";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_di")
	public final String DI_DELETE = "authority.cadastro_di.delete";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_fornecedor")
	public final String FORNECEDOR_VIEW = "authority.cadastro_fornecedor.acess";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_fornecedor")
	public final String FORNECEDOR_INSERT = "authority.cadastro_fornecedor.add";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_fornecedor")
	public final String FORNECEDOR_DELETE = "authority.cadastro_fornecedor.delete";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_empresa")
	public final String EMPRESA_VIEW = "authority.cadastro_empresa.acess";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_empresa")
	public final String EMPRESA_INSERT = "authority.cadastro_empresa.add";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_empresa")
	public final String EMPRESA_DELETE = "authority.cadastro_empresa.delete";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_transportadora")
	public final String TRANSPORTADORA_VIEW = "authority.cadastro_transportadora.acess";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_transportadora")
	public final String TRANSPORTADORA_INSERT = "authority.cadastro_transportadora.add";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_transportadora")
	public final String TRANSPORTADORA_DELETE = "authority.cadastro_transportadora.delete";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_colaborador")
	public final String COLABORADOR_VIEW = "authority.cadastro_colaborador.acess";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_colaborador")
	public final String COLABORADOR_INSERT = "authority.cadastro_colaborador.add";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_colaborador")
	public final String COLABORADOR_DELETE = "authority.cadastro_colaborador.delete";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_cargo")
	public final String CARGO_VIEW = "authority.cadastro_cargo.acess";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_cargo")
	public final String CARGO_INSERT = "authority.cadastro_cargo.add";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_cargo")
	public final String CARGO_DELETE = "authority.cadastro_cargo.delete";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_endereco")
	public final String ENDERECO_VIEW ="authority.cadastro_endereco.acess";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_endereco")
	public final String ENDERECO_INSERT = "authority.cadastro_endereco.add";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_endereco")
	public final String ENDERECO_DELETE = "authority.cadastro_endereco.delete";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_cliente")
	public final String CLIENT_VIEW = "authority.cadastro_cliente.acess";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_cliente")
	public final String CLIENT_UPDATE = "authority.cadastro_cliente.update";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_cliente")
	public final String CLIENT_INSERT = "authority.cadastro_cliente.add";
	
	@Getter
	@AuthorizationGroup("authority.cadastro_cliente")
	public final String CLIENT_DELETE = "authority.cadastro_cliente.delete";
	
	@Getter
	@AuthorizationGroup("authority.suport")
	public final String SUPORT_VIEW= "authority.suport.acess";
	@Getter
	@AuthorizationGroup("authority.suport")
	public final String SUPORT_INSERT = "authority.suport.add";
	@Getter
	@AuthorizationGroup("authority.suport")
	public final String SUPORT_DELETE = "authority.suport.delete";

    @Getter
    @AuthorizationGroup("authority.configuration")
    public final String CONFIGURATION_VIEW = "authority.configuration.access";
    @Getter
    @AuthorizationGroup("authority.configuration")
    public final String CONFIGURATION_INSERT = "authority.configuration.add";

    @Getter
    @AuthorizationGroup("authority.card")
    public final String CARD_VIEW = "authority.card.access";
    @Getter
    @AuthorizationGroup("authority.card")
    public final String CARD_STATISTICS = "authority.card.statistics";
    @Getter
    @AuthorizationGroup("authority.card")
    public final String CARD_INSERT = "authority.card.add";
    @Getter
    @AuthorizationGroup("authority.card")
    public final String CARD_UPDATE = "authority.card.edit";
    @Getter
    @AuthorizationGroup("authority.card")
    public final String CARD_DELETE = "authority.card.delete";

    @Getter
    @AuthorizationGroup("authority.contact")
    public final String CONTACT_VIEW = "authority.contact.access";
    @Getter
    @AuthorizationGroup("authority.contact")
    public final String CONTACT_INSERT = "authority.contact.add";
    @Getter
    @AuthorizationGroup("authority.contact")
    public final String CONTACT_UPDATE = "authority.contact.edit";
    @Getter
    @AuthorizationGroup("authority.contact")
    public final String CONTACT_DELETE = "authority.contact.delete";

    @Getter
    @AuthorizationGroup("authority.wallet")
    public final String WALLET_VIEW = "authority.wallet.access";
    @Getter
    @AuthorizationGroup("authority.wallet")
    public final String WALLET_INSERT = "authority.wallet.add";
    @Getter
    @AuthorizationGroup("authority.wallet")
    public final String WALLET_UPDATE = "authority.wallet.edit";
    @Getter
    @AuthorizationGroup("authority.wallet")
    public final String WALLET_DELETE = "authority.wallet.delete";
    @Getter
    @AuthorizationGroup("authority.wallet")
    public final String WALLET_ADJUST_BALANCE = "authority.wallet.adjust-balance";

    @Getter
    @AuthorizationGroup("authority.cost-center")
    public final String COST_CENTER_VIEW = "authority.cost-center.access";
    @Getter
    @AuthorizationGroup("authority.cost-center")
    public final String COST_CENTER_INSERT = "authority.cost-center.add";
    @Getter
    @AuthorizationGroup("authority.cost-center")
    public final String COST_CENTER_UPDATE = "authority.cost-center.edit";
    @Getter
    @AuthorizationGroup("authority.cost-center")
    public final String COST_CENTER_DELETE = "authority.cost-center.delete";

    @Getter
    @AuthorizationGroup("authority.movement-class")
    public final String MOVEMENT_CLASS_VIEW = "authority.movement-class.access";
    @Getter
    @AuthorizationGroup("authority.movement-class")
    public final String MOVEMENT_CLASS_INSERT = "authority.movement-class.add";
    @Getter
    @AuthorizationGroup("authority.movement-class")
    public final String MOVEMENT_CLASS_UPDATE = "authority.movement-class.edit";
    @Getter
    @AuthorizationGroup("authority.movement-class")
    public final String MOVEMENT_CLASS_DELETE = "authority.movement-class.delete";

    @Getter
    @AuthorizationGroup("authority.movement")
    public final String MOVEMENT_VIEW = "authority.movement.access";
    @Getter
    @AuthorizationGroup("authority.movement")
    public final String MOVEMENT_INSERT = "authority.movement.add";
    @Getter
    @AuthorizationGroup("authority.movement")
    public final String MOVEMENT_UPDATE = "authority.movement.edit";
    @Getter
    @AuthorizationGroup("authority.movement")
    public final String MOVEMENT_PAY = "authority.movement.pay";
    @Getter
    @AuthorizationGroup("authority.movement")
    public final String MOVEMENT_DELETE = "authority.movement.delete";
    
    @Getter
    @AuthorizationGroup("authority.fixed-movement")
    public final String FIXED_MOVEMENT_VIEW = "authority.fixed-movement.access";
    @Getter
    @AuthorizationGroup("authority.fixed-movement")
    public final String FIXED_MOVEMENT_INSERT = "authority.fixed-movement.add";
    @Getter
    @AuthorizationGroup("authority.fixed-movement")
    public final String FIXED_MOVEMENT_UPDATE = "authority.fixed-movement.edit";
    @Getter
    @AuthorizationGroup("authority.fixed-movement")
    public final String FIXED_MOVEMENT_DELETE = "authority.fixed-movement.delete";
    @Getter
    @AuthorizationGroup("authority.fixed-movement")
    public final String FIXED_MOVEMENT_LAUNCH = "authority.fixed-movement.launch";
    @Getter
    @AuthorizationGroup("authority.fixed-movement")
    public final String FIXED_MOVEMENT_LAUNCHES = "authority.fixed-movement.launches";

    @Getter
    @AuthorizationGroup("authority.card-invoice")
    public final String CARD_INVOICE_PAY = "authority.card-invoice.pay";
    @Getter
    @AuthorizationGroup("authority.card-invoice")
    public final String CARD_INVOICE_VIEW = "authority.card-invoice.access";
    @Getter
    @AuthorizationGroup("authority.card-invoice")
    public final String CARD_INVOICE_PROCESS = "authority.card-invoice.process";
    @Getter
    @AuthorizationGroup("authority.card-invoice")
    public final String CARD_INVOICE_HISTORIC = "authority.card-invoice.historic";

    @Getter
    @AuthorizationGroup("authority.balance-transference")
    public final String BALANCE_TRANSFERENCE_VIEW = "authority.balance-transference.access";
    @Getter
    @AuthorizationGroup("authority.balance-transference")
    public final String BALANCE_TRANSFERENCE_MAKE = "authority.balance-transference.make";
    
    // FABRICA
    @Getter
	@AuthorizationGroup("authority.fabrica")
	public final String FABRICA_VIEW = "authority.fabrica.acess";
					
	@Getter
	@AuthorizationGroup("authority.fabrica")
	public final String FABRICA_INSERT = "authority.fabrica.add";
					
	@Getter
	@AuthorizationGroup("authority.fabrica")
	public final String FABRICA_DELETE = "authority.fabrica.delete";

	@Getter
	@AuthorizationGroup("authority.fabrica")
	public final String FABRICA_EDIT = "authority.fabrica.edit";

	@Getter
	@AuthorizationGroup("authority.fabrica")
	public final String FABRICA_REPORT = "authority.fabrica.report";
	
	// MODELO
	 // FABRICA
    @Getter
	@AuthorizationGroup("authority.modelo")
	public final String MODELO_VIEW = "authority.modelo.acess";
					
	@Getter
	@AuthorizationGroup("authority.modelo")
	public final String MODELO_INSERT = "authority.modelo.add";
					
	@Getter
	@AuthorizationGroup("authority.modelo")
	public final String MODELO_DELETE = "authority.modelo.delete";

	@Getter
	@AuthorizationGroup("authority.modelo")
	public final String MODELO_EDIT = "authority.modelo.edit";

	@Getter
	@AuthorizationGroup("authority.modelo")
	public final String MODELO_REPORT = "authority.modelo.report";

	@Getter
	@AuthorizationGroup("authority.cortador")
	public final String CORTADOR_VIEW = "authority.cortador.acess";

	@Getter
	@AuthorizationGroup("authority.cortador")
	public final String CORTADOR_INSERT = "authority.cortador.add";

	@Getter
	@AuthorizationGroup("authority.cortador")
	public final String CORTADOR_DELETE = "authority.cortador.delete";

	@Getter
	@AuthorizationGroup("authority.cortador")
	public final String CORTADOR_EDIT = "authority.cortador.edit";
	
	@Getter
	@AuthorizationGroup("authority.cortador")
	public final String CORTADOR_REPORT = "authority.cortador.report";
	
	@Getter
	@AuthorizationGroup("authority.listaservico")
	public final String LISTASERVICO_VIEW = "authority.listaservico.acess";

	@Getter
	@AuthorizationGroup("authority.listaservico")
	public final String LISTASERVICO_REPORT = "authority.listaservico.report";
	
	@Getter
	@AuthorizationGroup("authority.listaservico")
	public final String LISTASERVICO_INSERT = "authority.listaservico.add";

	@Getter
	@AuthorizationGroup("authority.listaservico")
	public final String LISTASERVICO_DELETE = "authority.listaservico.delete";

	@Getter
	@AuthorizationGroup("authority.listaservico")
	public final String LISTASERVICO_EDIT = "authority.listaservico.edit";
	
	@Getter
	@AuthorizationGroup("authority.risco")
	public final String RISCO_VIEW = "authority.risco.acess";

	@Getter
	@AuthorizationGroup("authority.listaservico")
	public final String RISCO_REPORT = "authority.risco.report";
	
	@Getter
	@AuthorizationGroup("authority.listaservico")
	public final String RISCO_INSERT = "authority.risco.add";

	@Getter
	@AuthorizationGroup("authority.listaservico")
	public final String RISCO_DELETE = "authority.risco.delete";

	@Getter
	@AuthorizationGroup("authority.listaservico")
	public final String RISCO_EDIT = "authority.risco.edit";

	@Getter
	@AuthorizationGroup("authority.producao")
	public final String PRODUCAO_VIEW = "authority.producao.acess";

	@Getter
	@AuthorizationGroup("authority.producao")
	public final String PRODUCAO_INSERT = "authority.producao.add";

	@Getter
	@AuthorizationGroup("authority.producao")
	public final String PRODUCAO_DELETE = "authority.producao.delete";

	@Getter
	@AuthorizationGroup("authority.producao")
	public final String PRODUCAO_EDIT = "authority.producao.edit";

	@Getter
	@AuthorizationGroup("authority.producao")
	public final String PRODUCAO_REPORT = "authority.producao.report";
	
	// OS
	@Getter
	@AuthorizationGroup("authority.os")
	public final String OS_VIEW = "authority.os.acess";

	@Getter
	@AuthorizationGroup("authority.os")
	public final String OS_INSERT = "authority.os.add";

	@Getter
	@AuthorizationGroup("authority.os")
	public final String OS_DELETE = "authority.os.delete";

	@Getter
	@AuthorizationGroup("authority.os")
	public final String OS_EDIT = "authority.os.edit";

	@Getter
	@AuthorizationGroup("authority.os")
	public final String OS_REPORT = "authority.os.report";
	
    // Financeiro
    @Getter
    @AuthorizationGroup("authority.financial-period")
    public final String FINANCIAL_PERIOD_VIEW = "authority.financial-period.access";
    @Getter
    @AuthorizationGroup("authority.financial-period")
    public final String FINANCIAL_PERIOD_INSERT = "authority.financial-period.add";
    @Getter
    @AuthorizationGroup("authority.financial-period")
    public final String FINANCIAL_PERIOD_DELETE = "authority.financial-period.delete";
    @Getter
    @AuthorizationGroup("authority.financial-period")
    public final String FINANCIAL_PERIOD_DETAILS = "authority.financial-period.details";
    
    @Getter
	@AuthorizationGroup("authority.reformatrib")
	public final String REFORMATRIB_VIEW = "authority.reformatrib.acess";
    
	@Getter
	@AuthorizationGroup("authority.reformatrib")
	public final String REFORMATRIB_INSERT = "authority.reformatrib.add";
					
	@Getter
	@AuthorizationGroup("authority.reformatrib")
	public final String REFORMATRIB_DELETE = "authority.reformatrib.delete";

	@Getter
	@AuthorizationGroup("authority.reformatrib")
	public final String REFORMATRIB_EDIT = "authority.reformatrib.edit";
	
	@Getter
	@AuthorizationGroup("authority.reformatrib")
	public final String REFORMATRIB_IMPORT = "authority.reformatrib.import";
	
    @Getter
    @AuthorizationGroup("authority.closing")
    public final String CLOSING_VIEW = "authority.closing.access";
    @Getter
    @AuthorizationGroup("authority.closing")
    public final String CLOSING_CLOSE = "authority.closing.close";
    @Getter
    @AuthorizationGroup("authority.closing")
    public final String CLOSING_PROCESS = "authority.closing.process";

    @Getter
    @AuthorizationGroup("authority.user")
    public final String USER_VIEW = "authority.user.access";
    @Getter
    @AuthorizationGroup("authority.user")
    public final String USER_INSERT = "authority.user.add";
    @Getter
    @AuthorizationGroup("authority.user")
    public final String USER_UPDATE = "authority.user.edit";
    @Getter
    @AuthorizationGroup("authority.user")
    public final String USER_DELETE = "authority.user.delete";
    
    @Getter
    @AuthorizationGroup("authority.adm")
    public final String ADM_VIEW = "authority.adm.access";
    @Getter
    @AuthorizationGroup("authority.adm")
    public final String ADM_INSERT = "authority.adm.add";
    @Getter
    @AuthorizationGroup("authority.adm")
    public final String ADM_UPDATE = "authority.adm.edit";
    @Getter
    @AuthorizationGroup("authority.adm")
    public final String ADM_DELETE = "authority.adm.delete";
    
    @Getter
    @AuthorizationGroup("authority.group")
    public final String GROUP_VIEW = "authority.group.access";
    @Getter
    @AuthorizationGroup("authority.group")
    public final String GROUP_INSERT = "authority.group.add";
    @Getter
    @AuthorizationGroup("authority.group")
    public final String GROUP_UPDATE = "authority.group.edit";
    @Getter
    @AuthorizationGroup("authority.group")
    public final String GROUP_DELETE = "authority.group.delete";

    @Getter
    @AuthorizationGroup("authority.message")
    public final String MESSAGE_SEND = "authority.message.send";
    
    @Getter
    @AuthorizationGroup("authority.vehicle")
    public final String VEHICLE_VIEW = "authority.vehicle.access";
    @Getter
    @AuthorizationGroup("authority.vehicle")
    public final String VEHICLE_INSERT = "authority.vehicle.add";
    @Getter
    @AuthorizationGroup("authority.vehicle")
    public final String VEHICLE_UPDATE = "authority.vehicle.edit";
    @Getter
    @AuthorizationGroup("authority.vehicle")
    public final String VEHICLE_DELETE = "authority.vehicle.delete";
    
    @Getter
    @AuthorizationGroup("authority.entries")
    public final String ENTRIES_VIEW = "authority.entries.access";
    @Getter
    @AuthorizationGroup("authority.entries")
    public final String ENTRIES_INSERT = "authority.entries.add";
    @Getter
    @AuthorizationGroup("authority.entries")
    public final String ENTRIES_UPDATE = "authority.entries.edit";
    @Getter
    @AuthorizationGroup("authority.entries")
    public final String ENTRIES_DELETE = "authority.entries.delete";
    
    @Getter
    @AuthorizationGroup("authority.refueling")
    public final String REFUELING_VIEW = "authority.refueling.access";
    @Getter
    @AuthorizationGroup("authority.refueling")
    public final String REFUELING_INSERT = "authority.refueling.add";
    @Getter
    @AuthorizationGroup("authority.refueling")
    public final String REFUELING_DELETE = "authority.refueling.delete";

    /**
     * Lista todas as authorities disponiveis para uso, este metodo e utilzado
     * para criar o admin no bootstrap da aplicacao
     *
     * @return um set com todas as authorities disponiveis
     */
    public List<String> listAuthorizations() {

        final List<String> authorities = new ArrayList<>();

        for (Field field : this.getClass().getDeclaredFields()) {

            field.setAccessible(true);

            // verifica se a permissao tem grupo de permisao
            if (field.isAnnotationPresent(AuthorizationGroup.class)) {

                // adiciona as permissoes especificas
                try {
                    authorities.add((String) field.get(Authorization.this));
                } catch (IllegalAccessException ex) { }
            }
        }
        return authorities;
    }

    /**
     * Lista todas as authorities agrupadas pelo grupo de cada uma
     *
     * @return hashmap com os valores: grupo e itens do grupo
     */
    public HashMap<String, List<String>> listGroupedAuthorizations() {

        final HashMap<String, List<String>> authorities = new HashMap<>();
        final List<String> authorizations = this.listAuthorizations();

        for (Field field : this.getClass().getDeclaredFields()) {

            field.setAccessible(true);

            if (field.isAnnotationPresent(AuthorizationGroup.class)) {

                final String group = field.getAnnotation(AuthorizationGroup.class).value();

                if (!authorities.containsKey(group)) {

                    final List<String> grouped = authorizations.stream()
                            .filter(authorization -> authorization.contains(group + "."))
                            .collect(Collectors.toList());
                    
                    authorities.put(group, grouped);
                }
            }
        }
        
        return authorities;
    }
}
