package br.com.nsym.application.controller.nfe.tools;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.com.ibrcomp.exception.TotaisCFeException;
import br.com.ibrcomp.exception.TributosException;
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.Filial;
import br.com.nsym.domain.model.entity.cadastro.Produto;
import br.com.nsym.domain.model.entity.fiscal.ParamReforma2026;
import br.com.nsym.domain.model.entity.fiscal.TabFcpEstado;
import br.com.nsym.domain.model.entity.fiscal.TabIVAEstado;
import br.com.nsym.domain.model.entity.fiscal.Tributos;
import br.com.nsym.domain.model.entity.fiscal.Cfe.CFe;
import br.com.nsym.domain.model.entity.fiscal.Cfe.ItemCFe;
import br.com.nsym.domain.model.entity.fiscal.Cfe.Nfce;
import br.com.nsym.domain.model.entity.fiscal.Cfe.NfceItem;
import br.com.nsym.domain.model.entity.fiscal.nfe.ItemNfe;
import br.com.nsym.domain.model.entity.fiscal.nfe.Nfe;
import br.com.nsym.domain.model.entity.fiscal.reforma.CstIbsCbs;
import br.com.nsym.domain.model.entity.fiscal.tools.CSTCOFINS;
import br.com.nsym.domain.model.entity.fiscal.tools.CSTPIS;
import br.com.nsym.domain.model.entity.fiscal.tools.CSTSimples;
import br.com.nsym.domain.model.entity.fiscal.tools.FinalidadeNfe;
import br.com.nsym.domain.model.entity.fiscal.tools.OrigemXDestino;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoCalculo;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoPesquisa;
import br.com.nsym.domain.model.entity.tools.Enquadramento;
import br.com.nsym.domain.model.entity.tools.TipoCliente;
import br.com.nsym.domain.model.entity.tools.Uf;
import br.com.nsym.domain.model.repository.cadastro.EmpresaRepository;
import br.com.nsym.domain.model.repository.cadastro.FilialRepository;
import br.com.nsym.domain.model.repository.fiscal.ParamReforma2026Repository;
import br.com.nsym.domain.model.repository.fiscal.TabFcpEstadoRepository;
import br.com.nsym.domain.model.repository.fiscal.TabIVAEstadoRepository;
import br.com.nsym.domain.model.service.fiscal.reforma.ParametrizacaoReforma2026Service;
import br.com.nsym.domain.model.service.fiscal.reforma.ParametrizacaoReforma2026Service.Aliquotas;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@RequestScoped
public class CalculaTributos {

	@Getter
	@Setter
	private Tributos tributos = new Tributos();

	@Inject
	private FilialRepository filialDao;
	
//	@Inject
//    private ParamReforma2026Repository paramReforma2026Repository;
	@Inject
	private ParametrizacaoReforma2026Service paramService;

	@Getter
	@Setter
	private Filial filial = new Filial();

	@Getter
	@Setter
	private Empresa empresa = new Empresa();

	@Inject
	private EmpresaRepository empresaDao;
	
	@Getter
	@Setter
	private ItemNfe itemCalculado = new ItemNfe();

	@Getter
	@Setter
	private OrigemXDestino aliquota = new OrigemXDestino();

	@Inject
	private FormulasDosImpostos calcula;

	@Inject
	private TabIVAEstadoRepository ivaDao;

	@Getter
	@Setter
	private TabIVAEstado ivaEstado = new TabIVAEstado();

	@Getter
	@Setter
	private TabFcpEstado fcpEstado = new TabFcpEstado();

	@Inject
	private TabFcpEstadoRepository fcpDao; 



	public Emp retornaEmpresa(Long idFilial, Long idEmpresa,boolean st){
		Emp emp = new Emp();
		if (idFilial == null){
			this.empresa = this.empresaDao.findById(idEmpresa, false);
			emp.setRegime(this.empresa.getEnquadramento());		
			emp.setUf(this.empresa.getEndereco().getEndereco().getUf());
			emp.setExporta(this.empresa.isExporta());
			emp.setAliqAproveita(this.empresa.getAliqArpoveitaIcms());
			emp.setReducao(this.empresa.getReduzBaseIcms());
			if (st) {
				if (this.empresa.getTributoST() != null) {
					emp.setTributo(this.empresa.getTributoST());
				}
			}else {
				if (this.empresa.getTributo() != null) {
					emp.setTributo(this.empresa.getTributo());
				}
			}
		}else{
			this.filial = this.filialDao.findById(idFilial, false);
			emp.setRegime(this.filial.getEnquadramento());
			emp.setUf(this.filial.getEndereco().getEndereco().getUf());
			emp.setExporta(this.filial.isExporta());
			emp.setAliqAproveita(this.filial.getAliqArpoveitaIcms());
			emp.setReducao(this.filial.getReduzBaseIcms());
			if (st) {
				if (this.filial.getTributoST() != null) {
				emp.setTributo(this.filial.getTributoST());
				}
			}else {
				if (this.filial.getTributo() != null) {
					emp.setTributo(this.filial.getTributo());
				}
			}
		}
		return emp;
	}

	/**
	 *  Mï¿½todo que preenche o itemNfe com os impostos correspondentes seguindo os paramentros definido
	 *  na Tabela Tributos levando em conta o destino da mercadoria e o regime do c
	 * @param preencher - Item da nfe a ter o imposto preenchido
	 * @param nfe - tipo de destinatario Cliente/Fornecedor/Empresa/Filial/Colaborador (TipoPesquisa.class)
	 * @param idEmpresa - a Matriz caso seja ela que esteja emitindo a nfe
	 * @param idFilial - a Filial caso seja ela que esteja emitindo a nfe
	 * @return ItemNfe preenchido com os devidos impostos.
	 */

	public ItemNfe preencheImpostos(ItemNfe preencher,Nfe nfe,Long idEmpresa,Long idFilial){
		try{
			Emp emp = new Emp();
			Dest destino = new Dest();
			boolean st;
			
			final TipoPesquisa pesquisa = nfe.getTipoPesquisa();
			System.out.println("Pesquisa:"+ pesquisa.toString());
			// Seta os impostos diferenciando ST / MVA 
			// MVA = 0  define produto como sem ST
			this.setItemCalculado(preencher);
			// pega as informaï¿½oes necessï¿½rias e que sï¿½o comuns tanto para filial como para empresa

			System.out.println("Estou inciando o processo de definir a empresa");

			emp = retornaEmpresa(idFilial, idEmpresa, preencher.getProduto().getNcm().isSt());

			System.out.println("Finalizado o processo de definiï¿½ï¿½o de empresa!");

			System.out.println("estou no inicio do preenche Impostos");
			
			System.out.println("iniciado o processo de definiï¿½ï¿½o de tributos");
			System.out.println("antes do if");
			if (this.itemCalculado.getProduto().getNcm().isSt()){
				System.out.println("dento is st antes do especial");
				if (this.itemCalculado.getProduto().isTributacaoEspecial()){
					System.out.println("linha 103 - Pegando os tributos pelo produto , quando ï¿½ ST - produto Especial");
					if (nfe.getFinalidadeEmissao().equals(FinalidadeNfe.DV)) {
						this.itemCalculado.setTributo(preencher.getProduto().getTributoEspecialDevolucao());
					}else {
						this.itemCalculado.setTributo(preencher.getProduto().getTributoEspecial());
					}
					if (itemCalculado.getTributo().isSt()){
						this.ivaEstado = this.ivaDao.pegarIvaPorNcmEstado(this.itemCalculado.getProduto().getNcm(), nfe.getUfDestino(), idEmpresa);
						if (this.ivaEstado.getPIVA().equals(new BigDecimal("0"))){
							st = false;
							this.itemCalculado.setTributo(nfe.getNatOperacao());
						}else{
							this.itemCalculado.setCest(this.itemCalculado.getProduto().getNcm().getCest());
							this.itemCalculado.setMvaSt(this.ivaEstado.getPIVA());
							st = true;
						}
					}else{
						st = false;
					}
				}else{
					System.out.println("Linha 118 - pegando os tributos pelo ncm - quando e ST");					
					// verifica se Tributos definido no momento de emissao da nota e um tributo ST caso contrario ira
					// pegar o tributoST padrao para Matriz ou Filial
					if (nfe.getNatOperacao().isSt()) { 
						this.itemCalculado.setTributo(nfe.getNatOperacao());
					}else {
						this.itemCalculado.setTributo(emp.getTributo());
					}
					this.ivaEstado = this.ivaDao.pegarIvaPorNcmEstado(this.itemCalculado.getProduto().getNcm(), nfe.getUfDestino(), idEmpresa);
					if (this.ivaEstado.getPIVA().equals(new BigDecimal("0"))){
						st = false;
						this.itemCalculado.setTributo(nfe.getNatOperacao());
					}else{
						this.itemCalculado.setCest(this.itemCalculado.getProduto().getNcm().getCest());
						this.itemCalculado.setMvaSt(this.ivaEstado.getPIVA());
						st = true;
					}

				}
				this.itemCalculado.setItemST(st);
			}else{
				System.out.println("nao e st antes do if especial");
				if (this.itemCalculado.getProduto().isTributacaoEspecial()){
					System.out.println("linha 133 - pegando os tributos pelo produto");
					if (nfe.getFinalidadeEmissao().equals(FinalidadeNfe.DV)) {
						this.itemCalculado.setTributo(preencher.getProduto().getTributoEspecialDevolucao());
					}else {
						this.itemCalculado.setTributo(preencher.getProduto().getTributoEspecial());
					}
					if (itemCalculado.getTributo().isSt()){
						this.ivaEstado = this.ivaDao.pegarIvaPorNcmEstado(this.itemCalculado.getProduto().getNcm(), nfe.getUfDestino(), idEmpresa);
						if (this.ivaEstado.getPIVA().equals(new BigDecimal("0"))){
							st = false;
							this.itemCalculado.setTributo(nfe.getNatOperacao());
						}else{
							this.itemCalculado.setCest(this.itemCalculado.getProduto().getNcm().getCest());
							this.itemCalculado.setMvaSt(this.ivaEstado.getPIVA());
							st = true;
						}
					}else{
						st = false;
					}
				}else{
					System.out.println("linha 148  - pegando os tributos pela nfe");
					this.itemCalculado.setTributo(nfe.getNatOperacao());
					st = false;
				}
				this.itemCalculado.setItemST(st);
			}
			System.out.println("o produto ï¿½ st = "+ st );
			System.out.println("o tributo ï¿½ = " + this.itemCalculado.getTributo().getDescricao());

			System.out.println("Finalizado o processo de definiï¿½ï¿½o de tributos");


			// Define o destino. Como podemos pegar o destinatario tanto da base clientes, fornecedores, filiais e Empresas reduzindo 
			// assim a quantidade de ifs para setarmos os impostos.
			System.out.println("inicio do processo que define o destino");
			if (pesquisa.equals(TipoPesquisa.CLI)){
				destino.setRegime(nfe.getDestino().getCliente().getEnquadramento());
				destino.setInscricao(nfe.getDestino().getCliente().getInscEstadual());
				destino.setTipoCliente(nfe.getDestino().getCliente().getTipoCliente());
				destino.setUf(nfe.getDestino().getCliente().getEndereco().getEndereco().getUf());
				destino.setReducao(nfe.getDestino().getCliente().isPermiteReducao());

			}else if (pesquisa.equals(TipoPesquisa.FIL)){
				destino.setRegime(nfe.getDestino().getFilial().getEnquadramento());
				destino.setInscricao(nfe.getDestino().getFilial().getInscEstadual());
				destino.setTipoCliente(TipoCliente.Rev);
				destino.setUf(nfe.getDestino().getFilial().getEndereco().getEndereco().getUf());
			}else if(pesquisa.equals(TipoPesquisa.FOR)){
				if (nfe.getDestino().getFornecedor().getEnquadramento() == null) {
					destino.setRegime(Enquadramento.Normal);
				}else {
					destino.setRegime(nfe.getDestino().getFornecedor().getEnquadramento());
				}	
				destino.setInscricao(nfe.getDestino().getFornecedor().getInscEstadual());
				destino.setTipoCliente(nfe.getDestino().getFornecedor().getTipoCliente());
				destino.setUf(nfe.getDestino().getFornecedor().getEndereco().getEndereco().getUf());
				destino.setReducao(nfe.getDestino().getFornecedor().isPermiteReducao());
			}else {
				destino.setRegime(nfe.getDestino().getEmpresa().getEnquadramento());
				destino.setInscricao(nfe.getDestino().getEmpresa().getInscEstadual());
				destino.setTipoCliente(TipoCliente.Rev);
				destino.setUf(nfe.getDestino().getEmpresa().getEndereco().getEndereco().getUf());
			}
			System.out.println("Fim do processo de destino - 171");

			System.out.println("inicio processo que define aliquotas");
			System.out.println(aliquota.pegaAliquota(emp.getUf(), destino.getUf()).toString());
			System.out.println(aliquota.pegaAliquota(destino.getUf(), destino.getUf()).toString());
			BigDecimal aliquotaDestino = new BigDecimal("0");
			aliquotaDestino = new BigDecimal(aliquota.pegaAliquota(emp.getUf(), destino.getUf()).toString());
			System.out.println("aliquota Destino" + aliquotaDestino);
			BigDecimal aliquotaInterna = new BigDecimal("0");
			aliquotaInterna = new BigDecimal(aliquota.pegaAliquota(destino.getUf(), destino.getUf()).toString());
			BigDecimal aliquotaDevolucao = new BigDecimal("0");
			if (nfe.getNatOperacao().isDevVenda()) {
				aliquotaDevolucao = aliquotaDestino;
			}else {
				aliquotaDevolucao = new BigDecimal(aliquota.pegaAliquota(destino.getUf(), emp.getUf()).toString());
			}
			if (nfe.isImportacao()) { // setando a aliquota de icms para padrao do estado no caso de importacao
				aliquotaDestino = new BigDecimal(aliquota.pegaAliquota(emp.getUf(), emp.getUf()).toString());
			}
			System.out.println("aliquota Interna" + aliquotaInterna);
			System.out.println("Fim do processo de aliquotas - 182");
			System.out.println("aliquota Devoluï¿½ï¿½o" + aliquotaDevolucao);
			System.out.println("aliquota Devolu" + aliquotaDevolucao);
			final BigDecimal valorProduto = new BigDecimal(preencher.getValorTotal().toString());
			System.out.println("Valor Produto :" + valorProduto);
			System.out.println("estou antes de entrar o if ST = ");
			// verificar se nao irï¿½ gerar erro nesse ponto quando nao tiver valor ou for nulo o resultado
			System.out.println("verificando o fcpEstado - linha 187");
			System.out.println(nfe.getUfDestino());
			this.fcpEstado = this.fcpDao.pegarFcpPorNcmEstado(this.itemCalculado.getProduto().getNcm(), nfe.getUfDestino(), idEmpresa);
			System.out.println("verifiquei com sucesso! - linha 189");

			// define a aliquota do icms e CFOP do item
			BigDecimal aliquotaICMS = new BigDecimal("0");
			System.out.println("Iniciando a definï¿½ï¿½o de aliquota de icms e CFOP - linha 193");
			if (emp.getUf() == destino.getUf()){
				if (this.itemCalculado.getProduto().isFabricado()){
					this.itemCalculado.setCfopItem(this.itemCalculado.getTributo().getCfopDentroFabricado());
				}else{
					this.itemCalculado.setCfopItem(this.itemCalculado.getTributo().getCfopDentro());
				}
				if (emp.getRegime().equals(Enquadramento.SimplesNacional) || emp.getRegime().equals(Enquadramento.SimplesNacionalMei)|| emp.getRegime().equals(Enquadramento.SimplesNacionalExcecao)){
					aliquotaICMS = emp.aliqAproveita;
				}else{
					aliquotaICMS = aliquotaInterna;
				}
			}else{
				if (emp.isExporta()){
					// alteracao 13-08-2018
					if (this.itemCalculado.isItemST()){
						if (this.itemCalculado.getTributo().getIcmsSt().getOrigem().getCst() == 1 || this.itemCalculado.getTributo().getIcmsSt().getOrigem().getCst() == 2){
							if (destino.getUf() != emp.getUf()){
								aliquotaICMS = new BigDecimal(aliquota.pegaAliquota(emp.getUf(), Uf.EX).toString());
							}else{
								aliquotaICMS = aliquotaDestino;
							}
						}else{
							aliquotaICMS = aliquotaDestino;
						}
					}else{
						if (this.itemCalculado.getTributo().getIcms().getOrigem().getCst() == 1){
							if (destino.getUf() != emp.getUf()){
								aliquotaICMS = new BigDecimal(aliquota.pegaAliquota(emp.getUf(), Uf.EX).toString());
							}else{
								aliquotaICMS = aliquotaDestino;
							}
						}else{
							aliquotaICMS = aliquotaDestino;
						}
					}
					//				aliquotaICMS = aliquotaDestino; antes da alteracao
					if (this.itemCalculado.getProduto().isFabricado()){
						this.itemCalculado.setCfopItem(this.itemCalculado.getTributo().getCfopExteriorFabricado());
					}else{
						this.itemCalculado.setCfopItem(this.itemCalculado.getTributo().getCfopExterior());
					}
				}else{
					if (this.itemCalculado.getProduto().isFabricado()){
						this.itemCalculado.setCfopItem(this.itemCalculado.getTributo().getCfopForaFabricado());
					}else{
						this.itemCalculado.setCfopItem(this.itemCalculado.getTributo().getCfopFora());
					}
					if (emp.getRegime().equals(Enquadramento.SimplesNacional) || emp.getRegime().equals(Enquadramento.SimplesNacionalMei)|| emp.getRegime().equals(Enquadramento.SimplesNacionalExcecao)){
						aliquotaICMS = emp.aliqAproveita;
					}else{
						// alteracao para produtos com origem 1 ou 2 para vendas para fora do estado de origem aplicar
						// aliquota de 4%
						if (this.itemCalculado.isItemST()){
							if (this.itemCalculado.getTributo().getIcmsSt().getOrigem().getCst() == 1 || this.itemCalculado.getTributo().getIcmsSt().getOrigem().getCst() == 2){
								if (destino.getUf() != emp.getUf()){
									aliquotaICMS = new BigDecimal(aliquota.pegaAliquota(emp.getUf(), Uf.EX).toString());
								}else{
									aliquotaICMS = aliquotaDestino;
								}
							}else{
								aliquotaICMS = aliquotaDestino;
							}
						}else{
							if (this.itemCalculado.getTributo().getIcms().getOrigem().getCst() == 1 || this.itemCalculado.getTributo().getIcms().getOrigem().getCst() == 2){
								if (destino.getUf() != emp.getUf()){
									aliquotaICMS = new BigDecimal(aliquota.pegaAliquota(emp.getUf(), Uf.EX).toString());
								}else{
									aliquotaICMS = aliquotaDestino;
								}
							}else{
								aliquotaICMS = aliquotaDestino;
							}
						}

						// aliquotaICMS = aliquotaDestino; antes da alteracao
					}
					if (destino.tipoCliente.equals(TipoCliente.CfC) || destino.tipoCliente.equals(TipoCliente.Cfi) || (destino.tipoCliente.equals(TipoCliente.Est) && emp.isExporta() == false)){ //consumidor final
						if (this.itemCalculado.getProduto().isFabricado()){
							this.itemCalculado.setCfopItem(this.itemCalculado.getTributo().getCfopConsumidorFabricado());
						}else{
							this.itemCalculado.setCfopItem(this.itemCalculado.getTributo().getCfopConsumidor());
						}
						aliquotaICMS = aliquotaDestino; // aliquota interna sera usada somente no momento do calculo de partilha
					}
				}
			}
			System.out.println("Definido a aliquota de icms e CFOP! linha 218");

			if (st == false){
				System.out.println("estou dentro do st == false");
				this.itemCalculado.setValorTotalTributoItem(calcula.calculaTotalTributositem(this.itemCalculado.getProduto().getNcm().getValorTotalTributos(), this.itemCalculado.getValorTotal()));
				if (nfe.getFinalidadeEmissao().equals(FinalidadeNfe.DV)){
					this.itemCalculado.setAliqIcms(aliquotaDevolucao);
				}else {
					this.itemCalculado.setAliqIcms(aliquotaICMS);
				}
				// se tem Inscricao estadual
				//			if (destino.getInscricao() != null && destino.getTipoCliente().equals(TipoCliente.Rev)){ 
				//				System.out.println("estou dentro do tipoCliente = Rev");
				//				System.out.println("St = falso / destino = revenda");
				// preenche o compo Cst da nota de acordo com o regime emissor X destino 
				if (emp.getRegime().equals(Enquadramento.Normal)){
					System.out.println("linha 316 - verificando regime");
					if (destino.getTipoCliente().equals(TipoCliente.CfC) || destino.tipoCliente.equals(TipoCliente.Cfi)){
						this.itemCalculado.setCst(this.itemCalculado.getTributo().getIcms().getCstVendaNormal().getCst());
						System.out.println("icms CST: " + this.itemCalculado.getCst());
					}else{
						if (destino.getRegime().equals(Enquadramento.Normal)){
							this.itemCalculado.setCst(this.itemCalculado.getTributo().getIcms().getCstVendaNormal().getCst());
							System.out.println("icms CST: " + this.itemCalculado.getCst());
						}else{
							this.itemCalculado.setCst(this.itemCalculado.getTributo().getIcms().getCstVendaNormalSimples().getCst());
							System.out.println("icms CST: " + this.itemCalculado.getCst());
						}
					}
					System.out.println("linha 324 - Setado cst!");
					this.itemCalculado.setCstIpi(this.itemCalculado.getTributo().getIpi().getCst().getCst());
					System.out.println("ipi cst: " + this.itemCalculado.getCstIpi());
					this.itemCalculado.setCstPis(this.itemCalculado.getTributo().getPis().getCstPis().getPis().intValue());
					System.out.println("pis cst: " + this.itemCalculado.getCstPis());
					this.itemCalculado.setCstCofins(this.itemCalculado.getTributo().getCofins().getCstCofins().getCofins());
					System.out.println("cofins cst: " + this.itemCalculado.getCstCofins());
					this.itemCalculado.setOrigem(this.itemCalculado.getTributo().getIcms().getOrigem().getCst());
					System.out.println("Origem  :" + this.itemCalculado.getOrigem());
					//-----------------------------------------------------
					//  valida o cst conforme o enquadramento da empresa X destinatario
						this.itemCalculado = defineCstIcms(this.itemCalculado, destino, emp,nfe);
					//--------------------------------------------------------
					System.out.println("depois do defineCstIcms : " + this.itemCalculado.getCst());

					// gera o calculo do icms
					if (nfe.isImportacao()) {
						if (this.itemCalculado.getCst().equals("00") || this.itemCalculado.getCst().equals("90")){
							System.out.println("Aliq Icms importacao fim:  "  + this.itemCalculado.getAliqIcms() + "valor total " +preencher.getValorTotal() );
							this.itemCalculado.setBaseICMS(calcula.geraBaseIcmsImportacao(preencher.getValorTotal(),this.itemCalculado.getValorIPI(), this.itemCalculado.getValorFrete(),
									this.itemCalculado.getValorSeguro(),this.itemCalculado.getValorDespesas(),this.itemCalculado.getDesconto(),aliquotaICMS));
							System.out.println("base icms importacao fim" + this.itemCalculado.getBaseICMS() + " antes do metodo calcula ");
							this.itemCalculado.setValorIcms(calcula.geraIcms(this.itemCalculado.getBaseICMS(), this.itemCalculado.getAliqIcms()).setScale(5,RoundingMode.HALF_EVEN));

							// calcula fpc
							System.out.println("linha 352 - Calcula FPC cst 00");
							if (this.fcpEstado.getPFcp().compareTo(new BigDecimal("0")) == 1 ){
								System.out.println("dentro do if");
								this.itemCalculado.setPFCP(this.fcpEstado.getPFcp());
								System.out.println("setei o valor de Pfc");
								this.itemCalculado.setVFCP(calcula.calculaValorFCP(this.itemCalculado.getBaseICMS(), this.fcpEstado.getPFcp()));
							}
							System.out.println("linha 357 - fim do FPC");
						}
					}else {
						if (this.itemCalculado.getCst().equals("00") || this.itemCalculado.getCst().equals("90")){
							System.out.println("Aliq Icms:  "  + this.itemCalculado.getAliqIcms());
							this.itemCalculado.setBaseICMS(preencher.getValorTotal().add(this.itemCalculado.getValorFrete()));
							System.out.println("base icms " + this.itemCalculado.getBaseICMS() + " antes do metodo calcula ");
							this.itemCalculado.setValorIcms(calcula.geraIcms(this.itemCalculado.getBaseICMS(), this.itemCalculado.getAliqIcms()).setScale(5,RoundingMode.HALF_EVEN));

							// calcula fpc
							System.out.println("linha 352 - Calcula FPC cst 00");
							if (this.fcpEstado.getPFcp().compareTo(new BigDecimal("0")) == 1 ){
								System.out.println("dentro do if");
								this.itemCalculado.setPFCP(this.fcpEstado.getPFcp());
								System.out.println("setei o valor de Pfc");
								this.itemCalculado.setVFCP(calcula.calculaValorFCP(this.itemCalculado.getBaseICMS(), this.fcpEstado.getPFcp()));
							}
							System.out.println("linha 357 - fim do FPC");
						}
					}
					if (this.itemCalculado.getCst().equals("20") ){
						System.out.println("CalculaTributos - linha 365 ");
						System.out.println("valor total = ");
						System.out.println(preencher.getValorTotal());
						System.out.println("Pecentual de reduï¿½ï¿½o: ");
						System.out.println(emp.getReducao());

							if (nfe.getFinalidadeEmissao().equals(FinalidadeNfe.DV)){
								// calcula credito icms
								if (nfe.getDestino().getFornecedor().getPerRedBaseCalculo().compareTo(new BigDecimal("0")) == 1){
									this.itemCalculado.setBaseICMS(calcula.geraBaseIcmsReducao(preencher.getValorTotal().add(this.itemCalculado.getValorFrete()), nfe.getDestino().getFornecedor().getPerRedBaseCalculo()));
									if (this.itemCalculado.getTributo().getIcms().getOrigem().getCst() == 1 || this.itemCalculado.getTributo().getIcms().getOrigem().getCst() == 2){
										if (destino.getUf() != emp.getUf()){
											this.itemCalculado.setAliqIcms( new BigDecimal(aliquota.pegaAliquota(emp.getUf(), Uf.EX).toString()));
										}else{
											this.itemCalculado.setAliqIcms(aliquotaDevolucao);
										}
									}else{
										this.itemCalculado.setAliqIcms(aliquotaDevolucao);
									}
									this.itemCalculado.setValorIcms(calcula.geraIcms(this.itemCalculado.getBaseICMS(), this.itemCalculado.getAliqIcms()));
								}else{
									this.itemCalculado.setBaseICMS(preencher.getValorTotal().add(this.itemCalculado.getValorFrete()));
									if (this.itemCalculado.getTributo().getIcms().getOrigem().getCst() == 1 || this.itemCalculado.getTributo().getIcms().getOrigem().getCst() == 2){
										if (destino.getUf() != emp.getUf()){
											this.itemCalculado.setAliqIcms( new BigDecimal(aliquota.pegaAliquota(emp.getUf(), Uf.EX).toString()));
										}else{
											this.itemCalculado.setAliqIcms(aliquotaDevolucao);
										}
									}else{
										this.itemCalculado.setAliqIcms(aliquotaDevolucao);
									}
									this.itemCalculado.setValorIcms(calcula.geraIcms(this.itemCalculado.getBaseICMS(), this.itemCalculado.getAliqIcms()));
								}
							}else{
								// calcula credito icms
//								this.itemCalculado.setBaseICMS(preencher.getValorTotal().add(this.itemCalculado.getValorFrete()));
//								this.itemCalculado.setValorIcms(calcula.geraIcms(this.itemCalculado.getBaseICMS(), this.itemCalculado.getAliqIcms()));
								this.itemCalculado.setBaseICMS(calcula.geraBaseIcmsReducao(preencher.getValorTotal().add(this.itemCalculado.getValorFrete()), emp.getReducao()));
								System.out.println(this.itemCalculado.getBaseICMS());
								this.itemCalculado.setValorIcms(calcula.geraIcms(this.itemCalculado.getBaseICMS(), this.itemCalculado.getAliqIcms()).setScale(5,RoundingMode.HALF_EVEN));
							}
// calculo de reducao base
						// calcula fpc
						System.out.println("linha 365 - Calcula FPC cst 20");
						if (this.fcpEstado.getPFcp().compareTo(new BigDecimal("0")) == 1  ){
							this.itemCalculado.setPFCP(this.fcpEstado.getPFcp());
							this.itemCalculado.setVFCP(calcula.calculaValorFCP(this.itemCalculado.getBaseICMS(), this.fcpEstado.getPFcp()));
						}
						System.out.println("linha 370 - fim FPC cst 20");
					}
					if (this.itemCalculado.getCst().equals("51")){
						System.out.println("Aliq Icms:  "  + this.itemCalculado.getAliqIcms());
						this.itemCalculado.setBaseICMS(preencher.getValorTotal().add(this.itemCalculado.getValorFrete()));
						System.out.println("base icms " + this.itemCalculado.getBaseICMS() + " antes do metodo calcula ");
						this.itemCalculado.setValorIcms(calcula.geraIcms(this.itemCalculado.getBaseICMS(), this.itemCalculado.getAliqIcms()).setScale(5,RoundingMode.HALF_EVEN));

						// calcula fpc
						System.out.println("linha 379 - Calcula FPC cst 51");
						if (this.fcpEstado.getPFcp().compareTo(new BigDecimal("0")) == 1  ){
							this.itemCalculado.setPFCP(this.fcpEstado.getPFcp());
							this.itemCalculado.setVFCP(calcula.calculaValorFCP(this.itemCalculado.getBaseICMS(), this.fcpEstado.getPFcp()));
						}
						System.out.println("linha 384 - Fim FPC cst 51");
					}

					System.out.println("Valor icms : " + this.itemCalculado.getValorIcms());

					if (!destino.getTipoCliente().equals(TipoCliente.Est) && !destino.getTipoCliente().equals(TipoCliente.Rev)){
						if (destino.getInscricao() == null){
							// recalcula o icms acrescentado o ipi a base de calculo
							System.out.println("Aliq Icms:  "  + this.itemCalculado.getAliqIcms());
							this.itemCalculado.setBaseICMS(preencher.getValorTotal().add(this.itemCalculado.getValorIPI()).add(this.itemCalculado.getValorFrete()));
							System.out.println("base icms difal " + this.itemCalculado.getBaseICMS() + " antes do metodo calcula ");
							this.itemCalculado.setValorIcms(calcula.geraIcms(this.itemCalculado.getBaseICMS(), aliquotaDestino).setScale(5,RoundingMode.HALF_EVEN));
							// criar aqui a rotina do difal!!
							if ( !destino.getUf().equals(emp.getUf()) &&  emp.regime.equals(Enquadramento.Normal) ){ // se nï¿½o ï¿½ presencial
								// calcula o difal
								System.out.println("estou dentro do calcula difal");
								this.itemCalculado.setVBCUFDest(this.itemCalculado.getBaseICMS().setScale(5,RoundingMode.HALF_EVEN));
								this.itemCalculado.setPFCPUFDest(this.fcpEstado.getPFcp().setScale(5,RoundingMode.HALF_EVEN));
								this.itemCalculado.setPICMSUFDest(aliquotaInterna.setScale(5,RoundingMode.HALF_EVEN));
								this.itemCalculado.setPICMSInter(aliquotaDestino.setScale(5,RoundingMode.HALF_EVEN));
								this.itemCalculado.setPICMSInterPart(new BigDecimal("100.00")); // era 80
								this.itemCalculado.setVFCPUFDest(calcula.calculaValorFCP(this.itemCalculado.getBaseICMS(), this.itemCalculado.getPFCP()).setScale(5,RoundingMode.HALF_EVEN));
								this.itemCalculado.setVICMSUFDest(calcula.calculaPartilhaDestino(this.itemCalculado.getBaseICMS(), aliquotaInterna, aliquotaDestino).setScale(5,RoundingMode.HALF_EVEN));
								this.itemCalculado.setVICMSUFRemet(calcula.calculaPartilhaOrigem(this.itemCalculado.getBaseICMS(), aliquotaInterna, aliquotaDestino).setScale(5,RoundingMode.HALF_EVEN));
									
								System.out.println("Venda nao presencial");
								System.out.println(this.itemCalculado.getVBCUFDest());
								System.out.println(this.itemCalculado.getPFCPUFDest());
								System.out.println(this.itemCalculado.getPICMSUFDest());
								System.out.println(this.itemCalculado.getPICMSInter());
								System.out.println(this.itemCalculado.getPICMSInterPart());
								System.out.println(this.itemCalculado.getVFCPUFDest());
								System.out.println(this.itemCalculado.getVICMSUFRemet());
								System.out.println(this.itemCalculado.getVICMSUFDest());


							}
						}

					}

				}
				if (emp.getRegime().equals(Enquadramento.SimplesNacional)|| emp.getRegime().equals(Enquadramento.SimplesNacionalMei)|| emp.getRegime().equals(Enquadramento.SimplesNacionalExcecao)){ 
					System.out.println("Empresa = Simples Nacional ST = False  Linha 385");
					this.itemCalculado.setCstIpi(this.itemCalculado.getTributo().getIpi().getCst().getCst());
					System.out.println("ipi cst: " + this.itemCalculado.getCstIpi());
					this.itemCalculado.setCstPis(this.itemCalculado.getTributo().getPis().getCstPis().getPis().intValue());
					System.out.println("pis cst: " + this.itemCalculado.getCstPis());
					this.itemCalculado.setCstCofins(this.itemCalculado.getTributo().getCofins().getCstCofins().getCofins());
					System.out.println("cofins cst: " + this.itemCalculado.getCstCofins());

					if (destino.getTipoCliente() == TipoCliente.CfC || destino.getTipoCliente() == TipoCliente.Cfi){
						destino.setRegime(Enquadramento.SimplesNacional);
					}

					if (destino.getRegime().equals(Enquadramento.Normal)){
						this.itemCalculado.setCst(this.itemCalculado.getTributo().getIcms().getCstVendaSimplesNormal().getCst());
						System.out.println("icms CST : " + this.itemCalculado.getCst());
						this.itemCalculado.setOrigem(this.itemCalculado.getTributo().getIcms().getOrigem().getCst());
						System.out.println("origem : " + this.itemCalculado.getOrigem());
						//-----------------------------------------------------
						//  valida o cst conforme o enquadramento da empresa X destinatario
							this.itemCalculado = defineCstIcms(this.itemCalculado, destino, emp,nfe);
						//--------------------------------------------------------
						if (this.itemCalculado.getCst().equals("101") || this.itemCalculado.getCst().equals("900")){
							if (nfe.getFinalidadeEmissao().equals(FinalidadeNfe.DV)){
								// calcula credito icms
								if (nfe.getDestino().getFornecedor().getPerRedBaseCalculo().compareTo(new BigDecimal("0")) == 1){
									this.itemCalculado.setBaseICMS(calcula.geraBaseIcmsReducao(preencher.getValorTotal().add(this.itemCalculado.getValorFrete()), nfe.getDestino().getFornecedor().getPerRedBaseCalculo()));
									if (this.itemCalculado.getTributo().getIcms().getOrigem().getCst() == 1 || this.itemCalculado.getTributo().getIcms().getOrigem().getCst() == 2){
										if (destino.getUf() != emp.getUf()){
											this.itemCalculado.setAliqIcms( new BigDecimal(aliquota.pegaAliquota(emp.getUf(), Uf.EX).toString()));
										}else{
											this.itemCalculado.setAliqIcms(aliquotaDevolucao);
										}
									}else{
										this.itemCalculado.setAliqIcms(aliquotaDevolucao);
									}
									this.itemCalculado.setValorIcms(calcula.geraIcms(this.itemCalculado.getBaseICMS(), this.itemCalculado.getAliqIcms()));
								}else{
									this.itemCalculado.setBaseICMS(preencher.getValorTotal().add(this.itemCalculado.getValorFrete()));
									if (this.itemCalculado.getTributo().getIcms().getOrigem().getCst() == 1 || this.itemCalculado.getTributo().getIcms().getOrigem().getCst() == 2){
										if (destino.getUf() != emp.getUf()){
											this.itemCalculado.setAliqIcms( new BigDecimal(aliquota.pegaAliquota(emp.getUf(), Uf.EX).toString()));
										}else{
											this.itemCalculado.setAliqIcms(aliquotaDevolucao);
										}
									}else{
										this.itemCalculado.setAliqIcms(aliquotaDevolucao);
									}
									this.itemCalculado.setValorIcms(calcula.geraIcms(this.itemCalculado.getBaseICMS(), this.itemCalculado.getAliqIcms()));
								}
							}else{
								// calcula credito icms
								this.itemCalculado.setBaseICMS(preencher.getValorTotal().add(this.itemCalculado.getValorFrete()));
								this.itemCalculado.setValorIcms(calcula.geraIcms(this.itemCalculado.getBaseICMS(), this.itemCalculado.getAliqIcms()));
							}

						}
					}else {
						this.itemCalculado.setCst(this.itemCalculado.getTributo().getIcms().getCstVendaSimples().getCst());
						this.itemCalculado.setOrigem(this.itemCalculado.getTributo().getIcms().getOrigem().getCst());
						//-----------------------------------------------------
						//  valida o cst conforme o enquadramento da empresa X destinatario
						this.itemCalculado = defineCstIcms(this.itemCalculado, destino, emp,nfe);
						//--------------------------------------------------------
					}
				}

				//gera o calculo IPI
				if (this.itemCalculado.getCstIpi() == "00" ||this.itemCalculado.getCstIpi() == "50" ){
					this.itemCalculado.setAliqIPI(this.itemCalculado.getTributo().getIpi().getValor());
					System.out.println("IPI: " + this.itemCalculado.getAliqIPI());
					this.itemCalculado.setValorIPI(calcula.geraIpi(valorProduto, this.itemCalculado.getAliqIPI()));
					System.out.println("Valor Ipi: " + this.itemCalculado.getValorIPI());
					this.itemCalculado.setBaseIPI(valorProduto);

				}
				if (nfe.isImportacao()) {
					// Gera calculo Cofins st
					if (this.itemCalculado.getCstCofins() != 7 || this.itemCalculado.getCstCofins() != 8 || this.itemCalculado.getCstCofins() != 9 || this.itemCalculado.getCstCofins() != 5){
						this.itemCalculado.setAliqCofins(this.itemCalculado.getTributo().getCofins().getValor());
						if (this.itemCalculado.getIi() != null) {
							this.itemCalculado.setBaseCofins(this.itemCalculado.getIi().getVBC());
						}else {
							this.itemCalculado.setBaseCofins((this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete())).subtract(this.itemCalculado.getValorIcms()));
						}
						System.out.println("Aliq Cofins: " + this.itemCalculado.getAliqCofins());
//						this.itemCalculado.setValorCofins(calcula.geraCofins(this.itemCalculado.getBaseICMS(), this.itemCalculado.getAliqCofins()));
//						this.itemCalculado.setValorCofins(calcula.geraCofins(this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete()), this.itemCalculado.getAliqCofins()));
						this.itemCalculado.setValorCofins(calcula.geraCofins(this.itemCalculado.getBaseCofins(), this.itemCalculado.getAliqCofins()));
						System.out.println("Valor Cofins : " + this.itemCalculado.getValorCofins());
					}
					// gera calculo Pis st
					if (this.itemCalculado.getCstPis() != 7 || this.itemCalculado.getCstPis() != 8 || this.itemCalculado.getCstPis() != 9 || this.itemCalculado.getCstPis() != 5){
						this.itemCalculado.setAliqPis(this.itemCalculado.getTributo().getPis().getValor());
						if (this.itemCalculado.getIi() != null) {
							this.itemCalculado.setBasePis(this.itemCalculado.getIi().getVBC());
						}else {
							this.itemCalculado.setBasePis((this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete())).subtract(this.itemCalculado.getValorIcms()));
						}
//						this.itemCalculado.setBasePis(this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete()));
						System.out.println("Aliq Pis : " + this.itemCalculado.getAliqPis());
//						this.itemCalculado.setValorPis(calcula.geraPis(this.itemCalculado.getBaseICMS(), this.itemCalculado.getAliqPis()));
//						this.itemCalculado.setValorPis(calcula.geraPis(this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete()), this.itemCalculado.getAliqPis()));
						this.itemCalculado.setValorPis(calcula.geraPis(this.itemCalculado.getBasePis(), this.itemCalculado.getAliqPis()));
						System.out.println("valor Pis : " + this.itemCalculado.getValorPis());
					}
				}else {
					// Gera calculo Cofins
					if (this.itemCalculado.getCstCofins() != 7 || this.itemCalculado.getCstCofins() != 8 || this.itemCalculado.getCstCofins() != 9 || this.itemCalculado.getCstCofins() != 5){
						this.itemCalculado.setAliqCofins(this.itemCalculado.getTributo().getCofins().getValor());
						this.itemCalculado.setBaseCofins((this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete())).subtract(this.itemCalculado.getValorIcms()));
						System.out.println("Aliq Cofins: " + this.itemCalculado.getAliqCofins());
						//					this.itemCalculado.setValorCofins(calcula.geraCofins(this.itemCalculado.getBaseICMS(), this.itemCalculado.getAliqCofins()));
						//					this.itemCalculado.setValorCofins(calcula.geraCofins(this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete()), this.itemCalculado.getAliqCofins()));
						this.itemCalculado.setValorCofins(calcula.geraCofins(this.itemCalculado.getBaseCofins(), this.itemCalculado.getAliqCofins()));
						System.out.println("Valor Cofins : " + this.itemCalculado.getValorCofins());
					}
					// gera calculo Pis
					if (this.itemCalculado.getCstPis() != 7 || this.itemCalculado.getCstPis() != 8 || this.itemCalculado.getCstPis() != 9 || this.itemCalculado.getCstPis() != 5){
						this.itemCalculado.setAliqPis(this.itemCalculado.getTributo().getPis().getValor());
						this.itemCalculado.setBasePis((this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete())).subtract(this.itemCalculado.getValorIcms()));
						//					this.itemCalculado.setBasePis(this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete()));
						System.out.println("Aliq Pis : " + this.itemCalculado.getAliqPis());
						//					this.itemCalculado.setValorPis(calcula.geraPis(this.itemCalculado.getBaseICMS(), this.itemCalculado.getAliqPis()));
						//					this.itemCalculado.setValorPis(calcula.geraPis(this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete()), this.itemCalculado.getAliqPis()));
						this.itemCalculado.setValorPis(calcula.geraPis(this.itemCalculado.getBasePis(), this.itemCalculado.getAliqPis()));
						System.out.println("valor Pis : " + this.itemCalculado.getValorPis());
					}
				}
				System.out.println("Resumo Tributação do item :" + itemCalculado.getProduto().getId());
				System.out.println("Enquadramento Destino : " + destino.getRegime());
				System.out.println("Enquadramento Emitente : " + emp.getRegime());
				System.out.println("Produto ï¿½ ST :" + st);
				System.out.println("CST ICMS ou ICMS-ST : " + itemCalculado.getCst());
				System.out.println("CST IPI :" + itemCalculado.getCstIpi());
				System.out.println("CST PIS / PIS-ST : " + itemCalculado.getCstPis());
				System.out.println("CST COFINS / COFINS-ST : " + itemCalculado.getCstCofins());
				return this.itemCalculado;
			}else{ // se produto for st

				System.out.println("estou dentro do st == true");
				this.itemCalculado.setValorTotalTributoItem(calcula.calculaTotalTributositem(this.itemCalculado.getProduto().getNcm().getValorTotalTributos(), this.itemCalculado.getValorTotal()));
				this.itemCalculado.setAliqIcms(aliquotaICMS);
				// se tem Inscricao estadual
				//			if (destino.getInscricao() != null && destino.getTipoCliente().equals(TipoCliente.Rev)){ 
				//				System.out.println("estou dentro do tipoCliente = Rev");
				//				System.out.println("St = falso / destino = revenda");
				// preenche o compo Cst da nota de acordo com o regime emissor X destino 
				if (emp.getRegime().equals(Enquadramento.Normal)){

					if (destino.getRegime().equals(Enquadramento.Normal)){
						this.itemCalculado.setCst(this.itemCalculado.getTributo().getIcmsSt().getCstVendaNormal().getCst());
						System.out.println("icms CST: " + this.itemCalculado.getCst());
					}else{
						this.itemCalculado.setCst(this.itemCalculado.getTributo().getIcmsSt().getCstVendaNormalSimples().getCst());
						System.out.println("icms CST: " + this.itemCalculado.getCst());
					}
					this.itemCalculado.setCstIpi(this.itemCalculado.getTributo().getIpi().getCst().getCst());
					System.out.println("ipi cst: " + this.itemCalculado.getCstIpi());
					this.itemCalculado.setCstPis(this.itemCalculado.getTributo().getPisSt().getCstPisSt().getPis().intValue());
					System.out.println("pis cst: " + this.itemCalculado.getCstPis());
					this.itemCalculado.setCstCofins(this.itemCalculado.getTributo().getCofinsSt().getCstCofinsSt().getCofins());
					System.out.println("cofins cst: " + this.itemCalculado.getCstCofins());
					this.itemCalculado.setOrigem(this.itemCalculado.getTributo().getIcmsSt().getOrigem().getCst());
					System.out.println("Origem  :" + this.itemCalculado.getOrigem());


					//gera o calculo IPI
					if (this.itemCalculado.getCstIpi() == "00" ||this.itemCalculado.getCstIpi() == "50"){
						System.out.println("entrei o ipi");
						this.itemCalculado.setAliqIPI(this.itemCalculado.getTributo().getIpi().getValor());
						System.out.println("IPI: " + this.itemCalculado.getAliqIPI());
						this.itemCalculado.setValorIPI(calcula.geraIpi(this.itemCalculado.getBaseICMSSt(), this.itemCalculado.getAliqIPI()));
						System.out.println("Valor Ipi: " + this.itemCalculado.getValorIPI());
						this.itemCalculado.setBaseIPI(this.itemCalculado.getBaseICMSSt());

					}
					System.out.println("Passei pelo IPI");
					// Gera calculo Pis
					if (this.itemCalculado.getCstPis() != 7 || this.itemCalculado.getCstPis() != 8 || this.itemCalculado.getCstPis() != 9 || this.itemCalculado.getCstPis() != 5){
						System.out.println("entrei no pis");
						this.itemCalculado.setAliqPis(this.itemCalculado.getTributo().getPisSt().getValor());
						System.out.println("Aliq Pis : " + this.itemCalculado.getAliqPis());
//						this.itemCalculado.setBasePis(this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete()));
//						this.itemCalculado.setValorPis(calcula.geraPis(this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete()), this.itemCalculado.getAliqPis()));
						this.itemCalculado.setBasePis((this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete())).subtract(this.itemCalculado.getValorIcms()).add(this.itemCalculado.getValorDespesas()));
						this.itemCalculado.setValorPis(calcula.geraPis(this.itemCalculado.getBasePis(), this.itemCalculado.getAliqPis()));
						System.out.println("valor Pis : " + this.itemCalculado.getValorPis());
					}
					// alteracao devido IMPORTACAO
					System.out.println("Passei pelo Pis");
					// gera calculo Cofins
					if (this.itemCalculado.getCstCofins() != 7 || this.itemCalculado.getCstCofins() != 8 || this.itemCalculado.getCstCofins() != 9 || this.itemCalculado.getCstCofins() != 5){
						System.out.println("entrei no cofins");
						this.itemCalculado.setAliqCofins(this.itemCalculado.getTributo().getCofinsSt().getValor());
						System.out.println("Aliq Cofins: " + this.itemCalculado.getAliqCofins());
//						this.itemCalculado.setBaseCofins(this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete()));
//						this.itemCalculado.setValorCofins(calcula.geraCofins(this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete()), this.itemCalculado.getAliqCofins()));
						this.itemCalculado.setBaseCofins((this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete())).subtract(this.itemCalculado.getValorIcms()).add(this.itemCalculado.getValorDespesas()));
						this.itemCalculado.setValorCofins(calcula.geraCofins(this.itemCalculado.getBaseCofins(), this.itemCalculado.getAliqCofins()));
						System.out.println("Valor Cofins : " + this.itemCalculado.getValorCofins());
					}
					System.out.println("CalculaTributos linha 548");
					//-----------------------------------------------------
					//  valida o cst conforme o enquadramento da empresa X destinatario
					this.itemCalculado = defineCstIcmsSt(this.itemCalculado, destino, emp);
					//--------------------------------------------------------
					System.out.println("CalculaTributos Linha 551 - inicio de calculos impostos ST");
					// gera o calculo do icms ST
					if (this.itemCalculado.getCst().equals("10") ){
						// base icms
						System.out.println("Aliq Icms:  "  + this.itemCalculado.getAliqIcms());
						this.itemCalculado.setBaseICMSSt(preencher.getValorTotal().add(this.itemCalculado.getValorFrete()));
						System.out.println("base icms " + this.itemCalculado.getBaseICMS() + " antes do metodo calcula ");
						this.itemCalculado.setValorIcmsSt(calcula.geraIcms(this.itemCalculado.getBaseICMSSt(), this.itemCalculado.getAliqIcms()).setScale(5,RoundingMode.HALF_EVEN));
						System.out.println("Valor icms : " + this.itemCalculado.getValorIcms());
						// base icms ST
						this.itemCalculado.setAliqIcmsSt(aliquotaInterna.setScale(5,RoundingMode.HALF_EVEN));
						System.out.println("Valor Aliquota IcmsSt : " + this.itemCalculado.getAliqIcmsSt());
						if (emp.getUf() == destino.getUf()){
							System.out.println("Estou dentro do uf = uf linha 571");
							this.itemCalculado.setBaseICMSSt(calcula.geraBaseIcmsSt(preencher.getValorTotal(), itemCalculado.getValorIPI(),itemCalculado.getValorFrete(),itemCalculado.getValorSeguro(), itemCalculado.getValorDespesas(),itemCalculado.getDesconto(), this.ivaEstado.getPIVA()).setScale(5,RoundingMode.HALF_EVEN));
							System.out.println("Base IcmsSt : " + this.itemCalculado.getBaseICMSSt());
							this.itemCalculado.setValorIcmsSt(calcula.geraIcmsSt(this.itemCalculado.getBaseICMSSt(), this.itemCalculado.getValorIcms(),aliquotaInterna));
							System.out.println("Valor do IcmsSt : " + this.itemCalculado.getValorIcmsSt());
						}else{ // calcula com mva Ajustada!!!
							System.out.println("uf != uf linha 577");
							this.itemCalculado.setBaseICMSSt(calcula.geraBaseIcmsStAjustada(preencher.getValorTotal().add(this.itemCalculado.getValorFrete()), aliquotaDestino, aliquotaInterna, itemCalculado.getValorIPI(), this.ivaEstado.getPIVA()).setScale(5,RoundingMode.HALF_EVEN));
							this.itemCalculado.setMvaSt(calcula.geraMvaAjustada(this.ivaEstado.getPIVA(), aliquotaDestino, aliquotaInterna));
							System.out.println("passei pelo baseICMSST linha 579");
							System.out.println("Base IcmsSt : " + this.itemCalculado.getBaseICMSSt().setScale(5,RoundingMode.HALF_EVEN));
							this.itemCalculado.setValorIcmsSt(calcula.geraIcmsStAjustado(this.itemCalculado.getBaseICMSSt(), this.itemCalculado.getValorIcms(),aliquotaInterna));
							System.out.println("Valor do IcmsSt : " + this.itemCalculado.getValorIcmsSt().setScale(5,RoundingMode.HALF_EVEN));
						}

						// calcula FCP
						// calcula fpc
						if (this.fcpEstado.getPFcp() == null || this.fcpEstado.getPFcp() == new BigDecimal("0") ){
							this.itemCalculado.setPFCP(this.fcpEstado.getPFcp());
							this.itemCalculado.setVFCP(calcula.calculaValorFCP(this.itemCalculado.getBaseICMSSt(), this.fcpEstado.getPFcp()));
						}

					}
					if (this.itemCalculado.getCst().equals("30")){
						// nao possue base icms
						// apenas base icms ST
						this.itemCalculado.setAliqIcmsSt(aliquotaInterna);
						System.out.println("Valor Aliquota IcmsSt : " + this.itemCalculado.getAliqIcmsSt());
						this.itemCalculado.setBaseICMSSt(calcula.geraBaseIcmsSt(preencher.getValorTotal(), itemCalculado.getValorIPI(),itemCalculado.getValorFrete(),itemCalculado.getValorSeguro(), itemCalculado.getValorDespesas(),itemCalculado.getDesconto(), this.ivaEstado.getPIVA()));
						System.out.println("Base IcmsSt : " + this.itemCalculado.getBaseICMSSt());
						this.itemCalculado.setValorIcmsSt(calcula.geraIcmsSt(this.itemCalculado.getBaseICMSSt(), this.itemCalculado.getValorIcms(),aliquotaInterna));
						System.out.println("Valor do IcmsSt : " + this.itemCalculado.getValorIcmsSt());	

						// calcula fcp
						if (this.fcpEstado.getPFcp() == null || this.fcpEstado.getPFcp() == new BigDecimal("0") ){
							this.itemCalculado.setPFCP(this.fcpEstado.getPFcp());
							this.itemCalculado.setVFCP(calcula.calculaValorFCP(this.itemCalculado.getBaseICMSSt(), this.fcpEstado.getPFcp()));
						}

					}
					if (this.itemCalculado.getCst().equals("60")){
						// nao possue base icms e nao gera
						// base icms ST  caso seja necessï¿½rio informar base icms st retido 

					}
					if(this.itemCalculado.getCst().equals("70")){
						// base icms e reducao base de calculo 
						this.itemCalculado.setAliqIcms(aliquotaDestino);
						System.out.println("Aliq Icms:  "  + this.itemCalculado.getAliqIcms());
						this.itemCalculado.setBaseICMS(calcula.geraBaseIcmsReducao(preencher.getValorTotal().add(this.itemCalculado.getValorFrete()), emp.getReducao()));
						System.out.println("base icms " + this.itemCalculado.getBaseICMS() + " antes do metodo calcula ");
						this.itemCalculado.setValorIcms(calcula.geraIcms(this.itemCalculado.getBaseICMS(), aliquotaDestino).setScale(5,RoundingMode.HALF_EVEN));
						System.out.println("Valor icms : " + this.itemCalculado.getValorIcms());


						// base icms ST
						this.itemCalculado.setAliqIcmsSt(aliquotaInterna);
						System.out.println("Valor Aliquota IcmsSt : " + this.itemCalculado.getAliqIcmsSt());
						this.itemCalculado.setBaseICMSSt(calcula.geraBaseIcmsSt(preencher.getValorTotal(), itemCalculado.getValorIPI(),itemCalculado.getValorFrete(),itemCalculado.getValorSeguro(), itemCalculado.getValorDespesas(),itemCalculado.getDesconto(), this.ivaEstado.getPIVA()));
						System.out.println("Base IcmsSt : " + this.itemCalculado.getBaseICMSSt());
						this.itemCalculado.setValorIcmsSt(calcula.geraIcmsSt(this.itemCalculado.getBaseICMSSt(), this.itemCalculado.getValorIcms(),aliquotaInterna));
						System.out.println("Valor do IcmsSt : " + this.itemCalculado.getValorIcmsSt());

						// calcula FCP
						if (this.fcpEstado.getPFcp() == null || this.fcpEstado.getPFcp() == new BigDecimal("0") ){
							this.itemCalculado.setPFCP(this.fcpEstado.getPFcp());
							this.itemCalculado.setVFCP(calcula.calculaValorFCP(this.itemCalculado.getBaseICMSSt(), this.fcpEstado.getPFcp()));
						}
					}
					if (this.itemCalculado.getCst().equals("90")){
						System.out.println("Aliq Icms:  "  + this.itemCalculado.getAliqIcms());
						this.itemCalculado.setBaseICMS(preencher.getValorTotal().add(this.itemCalculado.getValorFrete()));
						System.out.println("base icms " + this.itemCalculado.getBaseICMS() + " antes do metodo calcula ");
						this.itemCalculado.setValorIcms(calcula.geraIcms(this.itemCalculado.getBaseICMS(), aliquotaDestino).setScale(5,RoundingMode.HALF_EVEN));

						// base icms ST
						this.itemCalculado.setAliqIcmsSt(aliquotaInterna);
						System.out.println("Valor Aliquota IcmsSt : " + this.itemCalculado.getAliqIcmsSt());
						this.itemCalculado.setBaseICMSSt(calcula.geraBaseIcmsSt(preencher.getValorTotal(), itemCalculado.getValorIPI(),itemCalculado.getValorFrete(),itemCalculado.getValorSeguro(), itemCalculado.getValorDespesas(),itemCalculado.getDesconto(), this.ivaEstado.getPIVA()));
						System.out.println("Base IcmsSt : " + this.itemCalculado.getBaseICMSSt());
						this.itemCalculado.setValorIcmsSt(calcula.geraIcmsSt(this.itemCalculado.getBaseICMSSt(), this.itemCalculado.getValorIcms(),aliquotaInterna));
						System.out.println("Valor do IcmsSt : " + this.itemCalculado.getValorIcmsSt());

						// calcula fpc
						if (this.fcpEstado.getPFcp() == null || this.fcpEstado.getPFcp() == new BigDecimal("0") ){
							this.itemCalculado.setPFCP(this.fcpEstado.getPFcp());
							this.itemCalculado.setVFCP(calcula.calculaValorFCP(this.itemCalculado.getBaseICMSSt(), this.fcpEstado.getPFcp()));
						}
					}

					System.out.println("Valor icms : " + this.itemCalculado.getValorIcms());

				}
				if (emp.getRegime().equals(Enquadramento.SimplesNacional) || emp.getRegime().equals(Enquadramento.SimplesNacionalMei)|| emp.getRegime().equals(Enquadramento.SimplesNacionalExcecao)){ 
					System.out.println("Inicio Emitente = Simples definindo cï¿½digos CST para ICMS PIS COFINS IPI linha 528");
					if (destino.getRegime().equals(Enquadramento.Normal)){
						this.itemCalculado.setCst(this.itemCalculado.getTributo().getIcmsSt().getCstVendaSimplesNormal().getCst());
						System.out.println("icms CST: " + this.itemCalculado.getCst());
					}else{
						this.itemCalculado.setCst(this.itemCalculado.getTributo().getIcmsSt().getCstVendaSimples().getCst());
						System.out.println("icms CST: " + this.itemCalculado.getCst());
					}

					this.itemCalculado.setCstIpi(this.itemCalculado.getTributo().getIpi().getCst().getCst());
					System.out.println("ipi cst: " + this.itemCalculado.getCstIpi());
					this.itemCalculado.setCstPis(this.itemCalculado.getTributo().getPisSt().getCstPisSt().getPis().intValue());
					System.out.println("pis cst: " + this.itemCalculado.getCstPis());
					this.itemCalculado.setCstCofins(this.itemCalculado.getTributo().getCofinsSt().getCstCofinsSt().getCofins());
					System.out.println("cofins cst: " + this.itemCalculado.getCstCofins());
					this.itemCalculado.setOrigem(this.itemCalculado.getTributo().getIcmsSt().getOrigem().getCst());
					System.out.println("Origem  :" + this.itemCalculado.getOrigem());

					//-----------------------------------------------------
					//  valida o cst conforme o enquadramento da empresa X destinatario
					System.out.println("Validando o CST  - linha 548");
					this.itemCalculado = defineCstIcmsSt(this.itemCalculado, destino, emp);
					System.out.println("Fim da validaï¿½ï¿½o - linha 550");
					//--------------------------------------------------------
					//gera o calculo IPI
					if (this.itemCalculado.getCstIpi().equals("00") |this.itemCalculado.getCstIpi().equals("50") ){
						System.out.println("enstou dentro do if IPI");
						this.itemCalculado.setAliqIPI(this.itemCalculado.getTributo().getIpi().getValor());
						System.out.println("IPI: " + this.itemCalculado.getAliqIPI());
						this.itemCalculado.setValorIPI(calcula.geraIpi(valorProduto, this.itemCalculado.getAliqIPI()));
						this.itemCalculado.setBaseIPI(valorProduto);
						System.out.println("Valor Ipi: " + this.itemCalculado.getValorIPI());
						System.out.println("Conclui If IPI");
					}
					// ST
					if (nfe.isImportacao()) {
						// Gera calculo Cofins st
						if (this.itemCalculado.getCstCofins() != 7 || this.itemCalculado.getCstCofins() != 8 || this.itemCalculado.getCstCofins() != 9 || this.itemCalculado.getCstCofins() != 5){
							this.itemCalculado.setAliqCofins(this.itemCalculado.getTributo().getCofins().getValor());
							if (this.itemCalculado.getIi() != null) {
								this.itemCalculado.setBaseCofins(this.itemCalculado.getIi().getVBC());
							}else {
								this.itemCalculado.setBaseCofins((this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete())).subtract(this.itemCalculado.getValorIcms()));
							}
							System.out.println("Aliq Cofins: " + this.itemCalculado.getAliqCofins());
//							this.itemCalculado.setValorCofins(calcula.geraCofins(this.itemCalculado.getBaseICMS(), this.itemCalculado.getAliqCofins()));
//							this.itemCalculado.setValorCofins(calcula.geraCofins(this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete()), this.itemCalculado.getAliqCofins()));
							this.itemCalculado.setValorCofins(calcula.geraCofins(this.itemCalculado.getBaseCofins(), this.itemCalculado.getAliqCofins()));
							System.out.println("Valor Cofins : " + this.itemCalculado.getValorCofins());
						}
						// gera calculo Pis st
						if (this.itemCalculado.getCstPis() != 7 || this.itemCalculado.getCstPis() != 8 || this.itemCalculado.getCstPis() != 9 || this.itemCalculado.getCstPis() != 5){
							this.itemCalculado.setAliqPis(this.itemCalculado.getTributo().getPis().getValor());
							if (this.itemCalculado.getIi() != null) {
								this.itemCalculado.setBasePis(this.itemCalculado.getIi().getVBC());
							}else {
								this.itemCalculado.setBasePis((this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete())).subtract(this.itemCalculado.getValorIcms()));
							}
//							this.itemCalculado.setBasePis(this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete()));
							System.out.println("Aliq Pis : " + this.itemCalculado.getAliqPis());
//							this.itemCalculado.setValorPis(calcula.geraPis(this.itemCalculado.getBaseICMS(), this.itemCalculado.getAliqPis()));
//							this.itemCalculado.setValorPis(calcula.geraPis(this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete()), this.itemCalculado.getAliqPis()));
							this.itemCalculado.setValorPis(calcula.geraPis(this.itemCalculado.getBasePis(), this.itemCalculado.getAliqPis()));
							System.out.println("valor Pis : " + this.itemCalculado.getValorPis());
						}
					}else {
						// Gera calculo Cofins st
						if (this.itemCalculado.getCstCofins() != 7 || this.itemCalculado.getCstCofins() != 8 || this.itemCalculado.getCstCofins() != 9 || this.itemCalculado.getCstCofins() != 5){
							this.itemCalculado.setAliqCofins(this.itemCalculado.getTributo().getCofins().getValor());
							this.itemCalculado.setBaseCofins((this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete())).subtract(this.itemCalculado.getValorIcms()));
							System.out.println("Aliq Cofins: " + this.itemCalculado.getAliqCofins());
							//					this.itemCalculado.setValorCofins(calcula.geraCofins(this.itemCalculado.getBaseICMS(), this.itemCalculado.getAliqCofins()));
							//					this.itemCalculado.setValorCofins(calcula.geraCofins(this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete()), this.itemCalculado.getAliqCofins()));
							this.itemCalculado.setValorCofins(calcula.geraCofins(this.itemCalculado.getBaseCofins(), this.itemCalculado.getAliqCofins()));
							System.out.println("Valor Cofins : " + this.itemCalculado.getValorCofins());
						}
						// gera calculo Pis st
						if (this.itemCalculado.getCstPis() != 7 || this.itemCalculado.getCstPis() != 8 || this.itemCalculado.getCstPis() != 9 || this.itemCalculado.getCstPis() != 5){
							this.itemCalculado.setAliqPis(this.itemCalculado.getTributo().getPis().getValor());
							this.itemCalculado.setBasePis((this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete())).subtract(this.itemCalculado.getValorIcms()));
							//					this.itemCalculado.setBasePis(this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete()));
							System.out.println("Aliq Pis : " + this.itemCalculado.getAliqPis());
							//					this.itemCalculado.setValorPis(calcula.geraPis(this.itemCalculado.getBaseICMS(), this.itemCalculado.getAliqPis()));
							//					this.itemCalculado.setValorPis(calcula.geraPis(this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete()), this.itemCalculado.getAliqPis()));
							this.itemCalculado.setValorPis(calcula.geraPis(this.itemCalculado.getBasePis(), this.itemCalculado.getAliqPis()));
							System.out.println("valor Pis : " + this.itemCalculado.getValorPis());
						}
					}
					System.out.println("Fim Emitente = Simples definindo cï¿½digos CST para ICMS PIS COFINS IPI - linha 544");
					if (destino.getRegime().equals(Enquadramento.Normal)){
						System.out.println("Inicio calculos Emitente = Simples Destino = Regime Normal produto ST");
						//					this.itemCalculado.setCst(this.itemCalculado.getTributo().getIcms().getCstVendaSimplesNormal().getCst());
						//					this.itemCalculado.setOrigem(this.itemCalculado.getTributo().getIcms().getOrigem().getCst());

						//-----------------------------------------------------
						//  valida o cst conforme o enquadramento da empresa X destinatario
						//					this.itemCalculado = defineCstIcmsSt(this.itemCalculado, destino, emp);
						//--------------------------------------------------------


						if (this.itemCalculado.getCst().equals("201")){
							this.itemCalculado.setBaseICMS(preencher.getValorTotal().add(this.itemCalculado.getValorFrete()));
							this.itemCalculado.setValorIcms(calcula.geraIcms(this.itemCalculado.getBaseICMS(), aliquotaDestino));

							// base icms ST
							this.itemCalculado.setAliqIcmsSt(aliquotaInterna);
							System.out.println("Valor Aliquota IcmsSt : " + this.itemCalculado.getAliqIcmsSt());
							System.out.println(valorProduto + " - linha 595");
							System.out.println(itemCalculado.getValorIPI());
							System.out.println(itemCalculado.getValorFrete());
							System.out.println(itemCalculado.getValorSeguro());
							System.out.println(itemCalculado.getValorDespesas());
							System.out.println(itemCalculado.getDesconto());
							if (this.ivaEstado.getPIVA() != new BigDecimal("0") ){
								System.out.println("estou dentro do PIVA != 0");
								System.out.println(this.ivaEstado.getPIVA());
							}
							System.out.println("Valor Aliquota IcmsSt : " + this.itemCalculado.getAliqIcmsSt());
							this.itemCalculado.setBaseICMSSt(calcula.geraBaseIcmsSt(preencher.getValorTotal(), itemCalculado.getValorIPI(),itemCalculado.getValorFrete(),itemCalculado.getValorSeguro(), itemCalculado.getValorDespesas(),itemCalculado.getDesconto(), this.ivaEstado.getPIVA()));
							System.out.println("Base IcmsSt : " + this.itemCalculado.getBaseICMSSt());
							this.itemCalculado.setValorIcmsSt(calcula.geraIcmsSt(this.itemCalculado.getBaseICMSSt(), this.itemCalculado.getValorIcms(),aliquotaInterna));
							System.out.println("Valor do IcmsSt : " + this.itemCalculado.getValorIcmsSt());	
						}
						if (this.itemCalculado.getCst().equals("500")){

						}
						if (this.itemCalculado.getCst().equals("203")){
							this.itemCalculado.setBaseICMS(preencher.getValorTotal().add(this.itemCalculado.getValorFrete()));
							this.itemCalculado.setValorIcms(calcula.geraIcms(this.itemCalculado.getBaseICMS(), aliquotaDestino));
							// base icms ST
							this.itemCalculado.setAliqIcmsSt(aliquotaInterna);
							if (this.ivaEstado.getPIVA() != new BigDecimal("0") ){
								System.out.println("estou dentro do PIVA != 0");
								System.out.println(this.ivaEstado.getPIVA());
							}
							System.out.println("Valor Aliquota IcmsSt : " + this.itemCalculado.getAliqIcmsSt());
							this.itemCalculado.setBaseICMSSt(calcula.geraBaseIcmsSt(valorProduto, itemCalculado.getValorIPI(),itemCalculado.getValorFrete(),itemCalculado.getValorSeguro(), itemCalculado.getValorDespesas(),itemCalculado.getDesconto(), this.ivaEstado.getPIVA()));
							System.out.println("Base IcmsSt : " + this.itemCalculado.getBaseICMSSt());
							this.itemCalculado.setValorIcmsSt(calcula.geraIcmsSt(this.itemCalculado.getBaseICMSSt(), this.itemCalculado.getValorIcms(),aliquotaInterna));
							System.out.println("Valor do IcmsSt : " + this.itemCalculado.getValorIcmsSt());	
						}
						System.out.println("Fim dos calculos para Emitente = Simples Destino = Regime Normal Produto = ST");
					}else {
						System.out.println("Inicio dos calculos para Emitente = Simples Destino = Simples Produto = ST");
						if (this.itemCalculado.getCst().equals("202")){
							this.itemCalculado.setBaseICMS(preencher.getValorTotal().add(this.itemCalculado.getValorFrete()));
							this.itemCalculado.setValorIcms(calcula.geraIcms(this.itemCalculado.getBaseICMS(), aliquotaDestino));
							// base icms ST
							this.itemCalculado.setAliqIcmsSt(aliquotaInterna);
							System.out.println("Valor Aliquota IcmsSt : " + this.itemCalculado.getAliqIcmsSt());
							System.out.println(valorProduto);
							System.out.println(itemCalculado.getValorIPI());
							System.out.println(itemCalculado.getValorFrete());
							System.out.println(itemCalculado.getValorSeguro());
							System.out.println(itemCalculado.getValorDespesas());
							System.out.println(itemCalculado.getDesconto());
							if (this.ivaEstado.getPIVA() != new BigDecimal("0") ){
								System.out.println("estou dentro do PIVA != 0");
								System.out.println(this.ivaEstado.getPIVA());
							}
							this.itemCalculado.setBaseICMSSt(calcula.geraBaseIcmsSt(valorProduto, itemCalculado.getValorIPI(),itemCalculado.getValorFrete(),itemCalculado.getValorSeguro(), itemCalculado.getValorDespesas(),itemCalculado.getDesconto(), this.ivaEstado.getPIVA()));
							System.out.println("Base IcmsSt : " + this.itemCalculado.getBaseICMSSt());
							this.itemCalculado.setValorIcmsSt(calcula.geraIcmsSt(this.itemCalculado.getBaseICMSSt(), this.itemCalculado.getValorIcms(),aliquotaInterna));
							System.out.println("Valor do IcmsSt : " + this.itemCalculado.getValorIcmsSt());	

						}
						if (this.itemCalculado.getCst().equals("500") ){

						}
						if (this.itemCalculado.getCst().equals("203")){
							this.itemCalculado.setBaseICMS(preencher.getValorTotal().add(this.itemCalculado.getValorFrete()));
							this.itemCalculado.setValorIcms(calcula.geraIcms(this.itemCalculado.getBaseICMS(), aliquotaDestino));
							// base icms ST
							this.itemCalculado.setAliqIcmsSt(aliquotaInterna);
							System.out.println("Valor Aliquota IcmsSt : " + this.itemCalculado.getAliqIcmsSt());
							this.itemCalculado.setBaseICMSSt(calcula.geraBaseIcmsSt(valorProduto, itemCalculado.getValorIPI(),itemCalculado.getValorFrete(),itemCalculado.getValorSeguro(), itemCalculado.getValorDespesas(),itemCalculado.getDesconto(), this.ivaEstado.getPIVA()));
							System.out.println("Base IcmsSt : " + this.itemCalculado.getBaseICMSSt());
							this.itemCalculado.setValorIcmsSt(calcula.geraIcmsSt(this.itemCalculado.getBaseICMSSt(), this.itemCalculado.getValorIcms(),aliquotaInterna));
							System.out.println("Valor do IcmsSt : " + this.itemCalculado.getValorIcmsSt());	
						}

						// simples nacional nao gera DIFAL para venda consumidor final de outros estados
						if (!destino.getTipoCliente().equals(TipoCliente.Est) && !destino.getTipoCliente().equals(TipoCliente.Rev) ){
							if (destino.getInscricao() == null){
								// recalcula o icms acrescentado o ipi a base de calculo
								System.out.println("Aliq Icms:  "  + this.itemCalculado.getAliqIcms());
								this.itemCalculado.setBaseICMS(preencher.getValorTotal().add(this.itemCalculado.getValorIPI()).add(this.itemCalculado.getValorFrete()));
								System.out.println("base icms difal simples " + this.itemCalculado.getBaseICMS() + " antes do metodo calcula ");
								this.itemCalculado.setValorIcms(calcula.geraIcms(this.itemCalculado.getBaseICMS(), aliquotaInterna).setScale(5,RoundingMode.HALF_EVEN));
								// criar aqui a rotina do difal!!
								if ( !destino.getUf().equals(emp.getUf()) && emp.regime.equals(Enquadramento.Normal)){ // se ï¿½ presencial
									// calcula o difal
									this.itemCalculado.setVBCUFDest(this.itemCalculado.getBaseICMS().setScale(5,RoundingMode.HALF_EVEN));
									this.itemCalculado.setPFCPUFDest(this.fcpEstado.getPFcp().setScale(5,RoundingMode.HALF_EVEN));
									this.itemCalculado.setPICMSUFDest(aliquotaInterna.setScale(2,RoundingMode.HALF_EVEN));
									this.itemCalculado.setPICMSInter(aliquotaDestino.setScale(2,RoundingMode.HALF_EVEN));
									this.itemCalculado.setPICMSInterPart(new BigDecimal("100.00")); //era 80
									this.itemCalculado.setVFCPUFDest(calcula.calculaValorFCP(this.itemCalculado.getBaseICMS(), this.itemCalculado.getPFCP()).setScale(5,RoundingMode.HALF_EVEN));
									this.itemCalculado.setVICMSUFDest(calcula.calculaPartilhaDestino(this.itemCalculado.getBaseICMS(), aliquotaInterna, aliquotaDestino).setScale(5,RoundingMode.HALF_EVEN));
									this.itemCalculado.setVICMSUFRemet(calcula.calculaPartilhaOrigem(this.itemCalculado.getBaseICMS(), aliquotaInterna, aliquotaDestino).setScale(5,RoundingMode.HALF_EVEN));
									
									System.out.println("Venda presencial");
									System.out.println(this.itemCalculado.getVBCUFDest());
									System.out.println(this.itemCalculado.getPFCPUFDest());
									System.out.println(this.itemCalculado.getPICMSUFDest());
									System.out.println(this.itemCalculado.getPICMSInter());
									System.out.println(this.itemCalculado.getPICMSInterPart());
									System.out.println(this.itemCalculado.getVFCPUFDest());
									System.out.println(this.itemCalculado.getVICMSUFRemet());
									System.out.println(this.itemCalculado.getVICMSUFDest());
								}
							}

						}
						System.out.println("Fim dos calculos para Emitente = Simples Destino = Simples Produto = ST");
					}

				}
				// Caso NFE Importacao refazendo os calculos de ICMS
//				if (nfe.isImportacao()) {
//					if (this.itemCalculado.getCst().equals("00") || this.itemCalculado.getCst().equals("90")){
//						System.out.println("Aliq Icms importacao fim:  "  + this.itemCalculado.getAliqIcms());
//						this.itemCalculado.setBaseICMS(calcula.geraBaseIcmsImportacao(preencher.getValorTotal(),this.itemCalculado.getValorIPI(), this.itemCalculado.getValorFrete(),
//								this.itemCalculado.getValorSeguro(),this.itemCalculado.getValorDespesas(),this.itemCalculado.getDesconto(),aliquotaICMS));
//						System.out.println("base icms importacao fim" + this.itemCalculado.getBaseICMS() + " antes do metodo calcula ");
//						this.itemCalculado.setValorIcms(calcula.geraIcms(this.itemCalculado.getBaseICMS(), this.itemCalculado.getAliqIcms()).setScale(5,RoundingMode.HALF_EVEN));
//
//						// calcula fpc
//						System.out.println("linha 352 - Calcula FPC cst 00");
//						if (this.fcpEstado.getPFcp().compareTo(new BigDecimal("0")) == 1 ){
//							System.out.println("dentro do if");
//							this.itemCalculado.setPFCP(this.fcpEstado.getPFcp());
//							System.out.println("setei o valor de Pfc");
//							this.itemCalculado.setVFCP(calcula.calculaValorFCP(this.itemCalculado.getBaseICMS(), this.fcpEstado.getPFcp()));
//						}
//						System.out.println("linha 357 - fim do FPC");
//					}
//				}
				
				System.out.println("Resumo Tributaï¿½ï¿½o do item :" + itemCalculado.getProduto().getId());
				System.out.println("Enquadramento Destino : " + destino.getRegime());
				System.out.println("Enquadramento Emitente : " + emp.getRegime());
				System.out.println("Produto ï¿½ ST :" + st);
				System.out.println("CST ICMS ou ICMS-ST : " + itemCalculado.getCst());
				System.out.println("CST IPI :" + itemCalculado.getCstIpi());
				System.out.println("CST PIS / PIS-ST : " + itemCalculado.getCstPis());
				System.out.println("CST COFINS / COFINS-ST : " + itemCalculado.getCstCofins());
				
				//* calculando reforma tributaria
				aplicarReformaTributariaNfe(this.itemCalculado,nfe,idEmpresa,idFilial);

				return this.itemCalculado;

			}
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
			return null;
		}

	}

	public ItemNfe defineCstIcms(ItemNfe item, Dest destino , Emp empresa, Nfe nfe ){
		if (empresa.regime.equals(Enquadramento.Normal)){
//			if (destino.regime.equals(Enquadramento.Normal) && destino.getUf().equals(empresa.getUf())){ SOMENTE QUANDO DESTINO FOR REGIME NORMAL*
			if (destino.getUf().equals(empresa.getUf())){ // SEGUNDO LH REDUï¿½ï¿½O ï¿½ PARA TODOS OS CLIENTES DO ESTADO DE SAO PAULO INDEPENDENTE DO ENQUADRAMENTO.
				if (empresa.getUf().equals(Uf.SP)){
					if (item.getProduto().getNcm().isPermiteReducao() && destino.reducao){
						if (item.getProduto().isFabricado() == true && item.getCst().equals("00")){
							item.setCst("20");
						}
						if (item.getProduto().isFabricado() == false && item.getCst().equals("20")){
							item.setCst("00");
						}
					}else{
						if(item.getCst().equals("20")){
							item.setCst("00");
						}

					}
				}
			}else {
				if (destino.regime.equals(Enquadramento.Normal) && !destino.getUf().equals(empresa.getUf())){
					if (nfe.getFinalidadeEmissao().equals(FinalidadeNfe.NO)) {
						if (item.getCst().equals("60")){
							item.setCst("10");
						}
						if (item.getCst().equals("20")){
							item.setCst("00");
						}
					}
				}
				if (!destino.tipoCliente.equals(TipoCliente.Est) && !destino.tipoCliente.equals(TipoCliente.Rev)){
					if (item.getCst().equals("10") || item.getCst().equals("30") || item.getCst().equals("70") || item.getCst().equals("60")){
						item.setCst("00");
					}
				}
			}
//			if (destino.regime.equals(Enquadramento.SimplesNacional)){
//				if (item.getCst().equals("20")){
//					item.setCst("00");
//				}
//			}
		}else{ // empresa = Simples nacional
			if (destino.regime.equals(Enquadramento.Normal)){
				if (item.getCst().equals("102")){
					item.setCst("101");
				}
				if (!destino.getUf().equals(empresa.getUf())){
					if (item.getCst().equals("500")){
						item.setCst("201");
					}
				}
			}else{
				if (item.getCst().equals("101")){
					item.setCst("102");
				}
			}

		}
		return item;
	}

	public ItemNfe defineCstIcmsSt(ItemNfe item, Dest destino , Emp empresa){
		if (empresa.regime.equals(Enquadramento.Normal)){
			
			if (destino.getUf().equals(empresa.getUf())){
				if (item.getProduto().getNcm().isPermiteReducao()){
					if (item.getProduto().isFabricado() && item.getCst().equals("10")){
						item.setCst("70");
					}
					if (!item.getProduto().isFabricado() && item.getCst().equals("70")){
						item.setCst("10");
					}
				}else{
					if(item.getCst()=="70"){
						item.setCst("10");
					}

				}
			}else {
				if (!destino.getUf().equals(empresa.getUf())){
					if (item.getCst().equals("60")){
						item.setCst("10");
					}
					if (item.getCst().equals("70")){
						item.setCst("10");
					}
				}
			}

		}else{ // empresa = Simples nacional
			if (destino.regime.equals(Enquadramento.Normal)){
				if (item.getCst().equals("202")){
					item.setCst("201");
				}
				if (!destino.getUf().equals(empresa.getUf())){
					if (item.getCst().equals("500")){
						item.setCst("201");
					}
				}
			}else{
				if (item.getCst().equals("201")){
					item.setCst("202");
				}
			}

		}
		return item;
	}

	@ToString
	@EqualsAndHashCode
	public static class Emp {

		@Getter
		@Setter
		private Enquadramento regime;

		@Getter
		@Setter
		private Uf uf;

		@Getter
		@Setter
		private boolean exporta;

		@Getter
		@Setter
		private BigDecimal aliqAproveita;

		@Getter
		@Setter
		private BigDecimal reducao;
		
		@Getter
		@Setter
		private Tributos tributo;
		
		@Getter
		@Setter
		private boolean isST;

	}

	@ToString
	@EqualsAndHashCode
	public static class Dest {

		@Getter
		@Setter
		private Enquadramento regime;
		@Getter
		@Setter
		private Uf uf;
		@Getter
		@Setter
		private TipoCliente tipoCliente;
		@Getter
		@Setter
		private String inscricao;
		@Getter
		@Setter
		private boolean reducao;
		

	}

	public void validaTributo (Produto produto) throws TributosException {
		if (produto.getNcm() == null){
			throw new TributosException(produto);
		}else if (produto.getNcm().getTributo() == null){
			throw new TributosException(produto.getNcm());
		}
	}

	/**
	 * Mï¿½todo que preenche os tributos do itemCFE
	 * @param preencher - ItemCFe que serï¿½ preenchido os impostos
	 * @param cfe	- CFe que origina o Item
	 * @param idEmpresa - Id da Empresa
	 * @param idFilial - Id da Filial
	 * @return
	 * @throws TributosException
	 */
	public ItemCFe preencheImpostoSat(ItemCFe preencher,CFe cfe,Long idEmpresa,Long idFilial) throws TributosException{
		ItemCFe itemTemp = new ItemCFe();
		//		validaTributo(preencher.getProduto());
		itemTemp = preencher;
		Emp emp = retornaEmpresa(idFilial, idEmpresa,preencher.getProduto().getNcm().isSt());
		Tributos tributo = new Tributos();  
		itemTemp.setUnidade(itemTemp.getProduto().getTipoMedida().getSigla());
		if (itemTemp.getProduto().isTributacaoEspecial()){
			if (itemTemp.getProduto().getTributoEspecial() == null) {
				throw new TributosException("tributosException.tributoEspecial");
			}else {
				tributo = itemTemp.getProduto().getTributoEspecial();
				itemTemp.setTributo(tributo);
			}
		}else {
			if (itemTemp.getProduto().getNcm() == null) {
				throw new TributosException(itemTemp.getProduto());
			}else {
					if (idEmpresa == null) {
						throw new TributosException("tributosException.empresaNULL");
					}else {
							if (emp.getTributo() == null) {
								throw new TributosException("tributosException.tributo");
							}else {
								tributo = emp.getTributo();
							}
					}
			}
			itemTemp.setTributo(tributo);
		}
		if (itemTemp.getProduto().isFabricado()){
			itemTemp.setCfopItem(tributo.getCfopDentroFabricado());
		}else{
			itemTemp.setCfopItem(tributo.getCfopDentro());
		}
		// Definindo a aliquota ICMS para o produto.
		BigDecimal aliquotaICMS = new BigDecimal("0");
		if (itemTemp.getProduto().getNcm().getAliqIcmsSat()==null) {
			System.out.println("estou definindo aliquota icms - 1132");
			aliquotaICMS = new BigDecimal(this.aliquota.pegaAliquota(emp.getUf(), emp.getUf()).toString());
			System.out.println("AliquotaICMS: " + aliquotaICMS + " - 1134");
			itemTemp.setAliqIcmsSat(aliquotaICMS);
		}else {
			if (itemTemp.getProduto().getNcm().getAliqIcmsSat().compareTo(new BigDecimal("0"))==1) {
				System.out.println("estou definindo aliquota icms - 1132");
				aliquotaICMS = new BigDecimal(this.aliquota.pegaAliquota(emp.getUf(), emp.getUf()).toString());
				System.out.println("AliquotaICMS: " + aliquotaICMS + " - 1134");
				itemTemp.setAliqIcmsSat(aliquotaICMS);
			}else {
				aliquotaICMS = itemTemp.getProduto().getNcm().getAliqIcmsSat();
				itemTemp.setAliqIcmsSat(aliquotaICMS);
			}
		}
		if (emp.regime.equals(Enquadramento.Normal)){ //Regime normal
			System.out.println("estou no enquadramento normal - 1141");
			if (tributo.isSt()){ // Caso substituiï¿½ï¿½o tributaria
				System.out.println("estou na tributaï¿½ï¿½o ST - 1143");
				itemTemp.setOrigem(tributo.getIcmsSt().getOrigem().getCst());
				itemTemp.setCst(tributo.getIcmsSt().getCstVendaNormal().getCst());
			}else{// caso Nï¿½O ï¿½ substituiï¿½ï¿½o tributaria
				System.out.println("estou na tributaï¿½ï¿½o sem ST - 1147");
				itemTemp.setOrigem(tributo.getIcms().getOrigem().getCst());
				itemTemp.setCst(tributo.getIcms().getCstVendaNormal().getCst());
				System.out.println("AliquotaICMS: " + aliquotaICMS + " - 1150");
				itemTemp.setAliqIcmsSat(aliquotaICMS);
				itemTemp.setBaseICMS(itemTemp.getValorTotal());
				itemTemp.setValorIcms(calcula.geraIcms(itemTemp.getValorTotal(), aliquotaICMS));
			}
		}else{ //simples nacional
			if (tributo.isSt()) {
				if (!tributo.getIcmsSt().getCstVendaSimples().getCst().equals(CSTSimples.C900.getCst())){ // Caso CST for diferente de 900
					System.out.println("Simples Nacional - CST <> 900 - 1157");
					itemTemp.setOrigem(tributo.getIcmsSt().getOrigem().getCst());
					itemTemp.setCst(tributo.getIcmsSt().getCstVendaSimples().getCst());
				}else{// caso cst for igual a 900
					System.out.println("Simples Nacional - CST = 900 - 1161");
					itemTemp.setOrigem(tributo.getIcmsSt().getOrigem().getCst());
					itemTemp.setCst(tributo.getIcmsSt().getCstVendaSimples().getCst());
					itemTemp.setAliqIcmsSat(aliquotaICMS);
					itemTemp.setBaseICMS(itemTemp.getValorTotal());
					itemTemp.setValorIcms(calcula.geraIcms(itemTemp.getValorTotal(), itemTemp.getAliqIcmsSat()));
				}
			}else {
				if (!tributo.getIcms().getCstVendaSimples().getCst().equals(CSTSimples.C900.getCst())){ // Caso CST for diferente de 900
					itemTemp.setOrigem(tributo.getIcms().getOrigem().getCst());
					itemTemp.setCst(tributo.getIcms().getCstVendaSimples().getCst());
				}else{// caso cst for igual a 900
					itemTemp.setOrigem(tributo.getIcms().getOrigem().getCst());
					itemTemp.setCst(tributo.getIcms().getCstVendaSimples().getCst());
					itemTemp.setAliqIcmsSat(aliquotaICMS);
					itemTemp.setValorIcms(calcula.geraIcms(itemTemp.getValorTotal(), itemTemp.getAliqIcmsSat()));
				}
			}
		}
		// Preenche valorTotalTributos 
		if (itemTemp.getProduto().getNcm().getValorTotalTributos().compareTo(new BigDecimal("0"))>0) {
			itemTemp.setValorTotalTributoItem(calcula.calculaTotalTributositem(itemTemp.getProduto().getNcm().getValorTotalTributos(), itemTemp.getValorTotal()));
		}
		// preenchendo o PIS
		if (emp.regime.equals(Enquadramento.SimplesNacional) || emp.getRegime().equals(Enquadramento.SimplesNacionalMei)|| emp.getRegime().equals(Enquadramento.SimplesNacionalExcecao)) {
			itemTemp.setCstPis(CSTPIS.P49.getPis().intValue());
		}else {
			if (!tributo.isSt()){
				itemTemp.setCstPis(tributo.getPis().getCstPis().getPis().intValue());
				if (tributo.getPis().getCstPis().equals(CSTPIS.P01) || tributo.getPis().getCstPis().equals(CSTPIS.P02) || tributo.getPis().getCstPis().equals(CSTPIS.P05)){
					itemTemp.setVBasePis(this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete()));		
					itemTemp.setAliqPis(tributo.getPis().getValor());
				}
				if (tributo.getPis().getCstPis().equals(CSTPIS.P03)){
					itemTemp.setAliqPis(tributo.getPis().getValor());
					itemTemp.setValorPis(calcula.geraPisPorValor(this.itemCalculado.getBaseICMS(), tributo.getPis().getValor()));
//					itemTemp.setValorPis(calcula.geraPisPorValor(this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete()), tributo.getPis().getValor()));
				}
				if (tributo.getPis().getCstPis().equals(CSTPIS.P04) 
						|| tributo.getPis().getCstPis().equals(CSTPIS.P06)
						|| tributo.getPis().getCstPis().equals(CSTPIS.P07)
						|| tributo.getPis().getCstPis().equals(CSTPIS.P08)
						|| tributo.getPis().getCstPis().equals(CSTPIS.P09)){

				}
				if (tributo.getPis().getCstPis().equals(CSTPIS.P49)){
					// nao ï¿½ necessario preencher nada neste if.
				}
				if (tributo.getPis().getCstPis().equals(CSTPIS.P99)){
					itemTemp.setVBasePis(this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete()));				
					if (tributo.getPis().getCalculo().equals(TipoCalculo.TP)){
						itemTemp.setAliqPis(tributo.getPis().getValor());
						itemTemp.setValorPis(calcula.geraPis(this.itemCalculado.getBaseICMS(), tributo.getPis().getValor()));
					}else{
						itemTemp.setAliqPis(tributo.getPis().getValor());
						itemTemp.setValorPis(calcula.geraPisPorValor(this.itemCalculado.getBaseICMS(), tributo.getPis().getValor()));
					}

				}
			}else{ // Item ï¿½ ST (tributo.getPisST)
				itemTemp.setCstPis(tributo.getPisSt().getCstPisSt().getPis().intValue());
				if (tributo.getPisSt().getCstPisSt().equals(CSTPIS.P01) || tributo.getPisSt().getCstPisSt().equals(CSTPIS.P02) || tributo.getPisSt().getCstPisSt().equals(CSTPIS.P05)){
					itemTemp.setVBasePis(this.itemCalculado.getBaseICMSSt());		
					itemTemp.setAliqPis(tributo.getPisSt().getValor());
					itemTemp.setValorPis(calcula.geraPis(this.itemCalculado.getBaseICMSSt(), tributo.getPisSt().getValor()));
				}
				if (tributo.getPis().getCstPis().equals(CSTPIS.P03)){
					itemTemp.setAliqPis(tributo.getPisSt().getValor());
					itemTemp.setValorPis(calcula.geraPisPorValor(this.itemCalculado.getBaseICMSSt(), tributo.getPisSt().getValor()));
				}
				if (tributo.getPis().getCstPis().equals(CSTPIS.P04) 
						|| tributo.getPisSt().getCstPisSt().equals(CSTPIS.P06)
						|| tributo.getPisSt().getCstPisSt().equals(CSTPIS.P07)
						|| tributo.getPisSt().getCstPisSt().equals(CSTPIS.P08)
						|| tributo.getPisSt().getCstPisSt().equals(CSTPIS.P09)){
					// nada nesse campo (opï¿½ao isento)
				}
				if (tributo.getPisSt().getCstPisSt().equals(CSTPIS.P49)){
					// nï¿½o ï¿½ necessario preencher nada neste if.
				}
				if (tributo.getPisSt().getCstPisSt().equals(CSTPIS.P99)){
					itemTemp.setVBasePis(this.itemCalculado.getBaseICMSSt());				
					if (tributo.getPis().getCalculo().equals(TipoCalculo.TP)){
						itemTemp.setAliqPis(tributo.getPisSt().getValor());
						itemTemp.setValorPis(calcula.geraPis(this.itemCalculado.getBaseICMSSt(), tributo.getPisSt().getValor()));
					}else{
						itemTemp.setAliqPis(tributo.getPisSt().getValor());
						itemTemp.setValorPis(calcula.geraPisPorValor(this.itemCalculado.getBaseICMSSt(), tributo.getPisSt().getValor()));
					}

				}
			}

		}
		// preenchendo o Cofins
				if (emp.regime.equals(Enquadramento.SimplesNacional) || emp.getRegime().equals(Enquadramento.SimplesNacionalMei)|| emp.getRegime().equals(Enquadramento.SimplesNacionalExcecao)) {
					itemTemp.setCstCofins(CSTCOFINS.CO49.getCofins());
				}else {
					if (!tributo.isSt()){
						itemTemp.setCstCofins(tributo.getCofins().getCstCofins().getCofins());
						if (tributo.getCofins().getCstCofins().equals(CSTCOFINS.CO01) || tributo.getCofins().getCstCofins().equals(CSTCOFINS.CO02) || tributo.getCofins().getCstCofins().equals(CSTCOFINS.CO05)){
							itemTemp.setVBaseCofins(this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete()));
							itemTemp.setAliqCofins(tributo.getCofins().getValor());
							itemTemp.setValorCofins(calcula.geraCofins(this.itemCalculado.getBaseICMS(), tributo.getCofins().getValor()));
//							itemTemp.setValorCofins(calcula.geraCofins(itemTemp.getVBaseCofins(), tributo.getCofins().getValor()));
							
						}
						if(tributo.getCofins().getCstCofins().equals(CSTCOFINS.CO03)){
							itemTemp.setAliqCofins(tributo.getCofins().getValor());
							itemTemp.setValorCofins(calcula.geraCofinsPorValor(this.itemCalculado.getBaseICMS(), tributo.getCofins().getValor()));
//							itemTemp.setValorCofins(calcula.geraCofinsPorValor(this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete()), tributo.getCofins().getValor()));

							
						}
						if (tributo.getCofins().getCstCofins().equals(CSTCOFINS.CO04) 
								|| tributo.getCofins().getCstCofins().equals(CSTCOFINS.CO06)
								|| tributo.getCofins().getCstCofins().equals(CSTCOFINS.CO07)
								|| tributo.getCofins().getCstCofins().equals(CSTCOFINS.CO08)
								|| tributo.getCofins().getCstCofins().equals(CSTCOFINS.CO09)){

						}
						if (tributo.getCofins().getCstCofins().equals(CSTCOFINS.CO49)){
							// nao ï¿½ necessario preencher nada neste if.
						}
						if (tributo.getCofins().getCstCofins().equals(CSTCOFINS.CO99)){
							itemTemp.setVBaseCofins(this.itemCalculado.getValorTotal().add(this.itemCalculado.getValorFrete()));			
							if (tributo.getCofinsSt().getCalculo().equals(TipoCalculo.TP)){
								itemTemp.setAliqCofins(tributo.getCofinsSt().getValor());
								itemTemp.setValorCofins(calcula.geraCofins(this.itemCalculado.getBaseICMS(), tributo.getCofins().getValor()));
							}else{
								itemTemp.setAliqCofins(tributo.getCofins().getValor());
								itemTemp.setValorCofins(calcula.geraCofinsPorValor(this.itemCalculado.getBaseICMS(), tributo.getCofinsSt().getValor()));
							}
						}
					}else{ // Item ï¿½ ST (tributo.getPisST)
						itemTemp.setCstCofins(tributo.getCofinsSt().getCstCofinsSt().getCofins());
						if (tributo.getCofinsSt().getCstCofinsSt().equals(CSTCOFINS.CO01) || tributo.getCofinsSt().getCstCofinsSt().equals(CSTCOFINS.CO02) || tributo.getCofinsSt().getCstCofinsSt().equals(CSTCOFINS.CO05)){
							itemTemp.setVBaseCofins(this.itemCalculado.getBaseICMSSt());
							itemTemp.setAliqCofins(tributo.getCofinsSt().getValor());
							itemTemp.setValorCofins(calcula.geraCofins(this.itemCalculado.getBaseICMSSt(), tributo.getCofinsSt().getValor()));
						}
						if (tributo.getCofinsSt().getCstCofinsSt().equals(CSTCOFINS.CO03)){
							itemTemp.setAliqCofins(tributo.getCofinsSt().getValor());
							itemTemp.setValorCofins(calcula.geraCofinsPorValor(this.itemCalculado.getBaseICMSSt(), tributo.getCofinsSt().getValor()));
						}
						if (tributo.getCofinsSt().getCstCofinsSt().equals(CSTCOFINS.CO04) 
								|| tributo.getCofinsSt().getCstCofinsSt().equals(CSTCOFINS.CO06)
								|| tributo.getCofinsSt().getCstCofinsSt().equals(CSTCOFINS.CO07)
								|| tributo.getCofinsSt().getCstCofinsSt().equals(CSTCOFINS.CO08)
								|| tributo.getCofinsSt().getCstCofinsSt().equals(CSTCOFINS.CO09)){
							// nada nesse campo (opï¿½ao isento)
						}
						if (tributo.getCofinsSt().getCstCofinsSt().equals(CSTCOFINS.CO49)){
							// nï¿½o ï¿½ necessario preencher nada neste if.
						}
						if (tributo.getCofinsSt().getCstCofinsSt().equals(CSTCOFINS.CO99)){
							itemTemp.setVBaseCofins(this.itemCalculado.getBaseICMSSt());			
							if (tributo.getCofinsSt().getCalculo().equals(TipoCalculo.TP)){
								itemTemp.setAliqCofins(tributo.getCofinsSt().getValor());
								itemTemp.setValorCofins(calcula.geraCofins(this.itemCalculado.getBaseICMSSt(), tributo.getCofins().getValor()));
							}else{
								itemTemp.setAliqCofins(tributo.getCofins().getValor());
								itemTemp.setValorCofins(calcula.geraCofinsPorValor(this.itemCalculado.getBaseICMSSt(), tributo.getCofinsSt().getValor()));
							}

						}
					}

				}
		return itemTemp;
	}
	/**
	 * Rotina que recebe uma lista de itens CFe onde percorre cada item preenchendo a tributaï¿½ï¿½o
	 * @param lista - lista de itensCFe que serï¿½ percorrida 
	 * @param cfe - CFe que origina a lista
	 * @param idEmpresa - Id da Empresa 
	 * @param idFilial - Id da Filial
	 * @return
	 * @throws TributosException
	 */
	public List<ItemCFe> preencheListaDeItensCfe(List<ItemCFe> lista,CFe cfe,Long idEmpresa,Long idFilial
			) throws TributosException{
		int i = 0 ;
		for(ItemCFe item : lista) {
			preencheImpostoSat(item, cfe, idEmpresa, idFilial);
			System.out.println("Passei pela lista "+ i++ +" veze(s) - preencheListaDeItensCfe - 1347" );
		}
		return lista;
	}

	/**
	 * Mï¿½todo que ao receber um CFe preenche os Totais da CFe 
	 * @param cfe - Cfe com a lista de itens para calcular os totais
	 * @return
	 */
	public CFe calculaTotaisCFe(CFe cfe) throws TotaisCFeException {
		for (ItemCFe item : cfe.getListaItem()) {
			if (item.getBaseICMS().compareTo(new BigDecimal(0))==1 ) {
				cfe.setBaseIcms(cfe.getBaseIcms().add(item.getBaseICMS()));
			}
			cfe.setValorTotalProdutos(cfe.getValorTotalProdutos().add(item.getValorTotalBruto()));// talvez tenha que colocar o valorBruto aqui!
			cfe.setValorTotalNota(cfe.getValorTotalNota().add(item.getValorTotal()));
			if (item.getValorPis().compareTo(new BigDecimal("0"))==1) {
				cfe.setValorTotalPis(cfe.getValorTotalPis().add(item.getValorPis()));
			}
			if (item.getValorCofins().compareTo(new BigDecimal("0"))==1) {
				cfe.setValorTotalCofins(cfe.getValorTotalCofins().add(item.getValorCofins()));
			}
			cfe.setOutrasDespesas(cfe.getOutrasDespesas().add(item.getValorDespesas()));
			cfe.setDesconto(cfe.getDesconto().add(item.getDesconto()));
			cfe.setVCFe(cfe.getVCFe().add(item.getValorTotal()));
			if (item.getValorIcms().compareTo(new BigDecimal("0"))==1) {
				cfe.setValorIcms(cfe.getValorIcms().add(item.getValorIcms()));
			}
			if (item.getTributo().isSt()) {
				cfe.setValorTotalPisST(cfe.getValorTotalPisST().add(item.getValorPis()));
				cfe.setValorTotalCofinsSt(cfe.getValorTotalCofinsSt().add(item.getValorCofins()));
			}
		}
		return cfe;
	}
	
	/** Reforma 2026 (NFe): cálculo parametrizado por item */
    private void aplicarReformaTributariaNfe(ItemNfe item, Nfe nota,Long idEmpresa, Long idFilial) {

        // 1) Resolver a parametrização aplicável para esse item  
    	ParamReforma2026 param = paramService.aliquotaItemNfeRef(item, nota.getEmitente().retornaUf().toString(), nota.getDestino().getUfDestino().toString(), nota.getEmitente().retornaCnae(),
                nota.getDestino().getTipoCliente(), nota.getDestino().getConsumidorFinal(), nota.getDestino().getUfDestino().getIbgeUf().toString(), nota.getDataEmissao().toLocalDate(), idEmpresa, idFilial );
        if (param == null || Boolean.FALSE.equals(param.getAtivo())) {
            new TributosException("Não existe nenhuma regra ativa/ atribuida para este produto REF: " + item.getProduto().getReferencia());
        }
        // propaga a flag da parametrização para o ItemNfe
        item.setIndSemIbsm(Boolean.TRUE.equals(param.getIndSemIbsm()));

        // 2) Resolver CST IBS/CBS a partir da parametrização
        CstIbsCbs cstIbsCbs = param.getCstIbsCbs();
        if (cstIbsCbs != null) {
            item.setCstCbs(cstIbsCbs.getCstCbs());
            item.setCstIbs(cstIbsCbs.getCstIbs());
            item.setCstIs(cstIbsCbs.getCstIs());

            // Definindo o CclasTrib utilizado
            if (cstIbsCbs.getCClassTrib() != null) {
                 item.setCclassTrib(cstIbsCbs.getCClassTrib());
            }
        }

        // 3) Definir base de cálculo usada para CBS/IBS/IS
        // AJUSTE ESTA LINHA para a base correta (por ex.: vProd + frete - desconto etc.)
        BigDecimal baseCalculo = item.getValorTotal();
        if (baseCalculo == null) {
            new TributosException("Erro de base de cálculo no produto "+ item.getProduto().getReferencia());
        }

        // 4) CBS
        BigDecimal aliquotaCbs = param.getPCbs();
        if (aliquotaCbs != null) {
            BigDecimal valorCbs = baseCalculo
                    .multiply(aliquotaCbs)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            // AJUSTE ESTES CAMPOS para os nomes reais que você tem para CBS:
            item.setVbcCbs(baseCalculo);
            item.setPCbs(aliquotaCbs);
            item.setVCbs(valorCbs);
        }

        // 5) IBS
        BigDecimal aliquotaIbs = param.getPIbs();
        if (aliquotaIbs != null) {
            BigDecimal valorIbs = baseCalculo
                    .multiply(aliquotaIbs)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            // AJUSTE ESTES CAMPOS para os nomes reais que você tem para IBS:
            item.setVbcIbs(baseCalculo);
            item.setPIbs(aliquotaIbs);
            item.setVIbs(valorIbs);
        }

        // 6) IS – respeitando isenção do NCM
        BigDecimal aliquotaIs = param.getPIs();

        boolean isentoIs = false;
        try {
            // AJUSTE o caminho conforme seu modelo:
            // getProduto().getNcm().getExcluirSeIsento()
            isentoIs = item.getProduto() != null
                    && item.getProduto().getNcm() != null
                    && Boolean.TRUE.equals(item.getProduto().getNcm().isExcluirSeIsento());
        } catch (Exception e) {
            // se der NullPointer em algum ponto, tratamos como não isento
            isentoIs = false;
        }

        if (aliquotaIs != null && !isentoIs) {
            BigDecimal valorIs = baseCalculo
                    .multiply(aliquotaIs)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            // AJUSTE ESTES CAMPOS para os nomes reais que você tem para IS:
            item.setVbcIs(baseCalculo);
            item.setPIs(aliquotaIs);
            item.setVIs(valorIs);
        } else {
            // Se isento, zera base/valor de IS (se fizer sentido no seu layout)
            // e mantém CST IS que você definir para isento
            // AJUSTE ESTES CAMPOS:
            item.setVbcIs(BigDecimal.ZERO);
            item.setPIs(BigDecimal.ZERO);
            item.setVIs(BigDecimal.ZERO);
        }
    }


    /** Geração de INI (ACBr) */
//    public File gerarIniNFeReforma2026oli(Nfe nfe, List<ItemNfe> itens, Path destino) throws IOException {
//        return AcbrIniReforma2026.gerarIniNFe(nfe, itens, destino);
//    }
//    public File gerarIniNFCeReforma2026(Nfce nfce, List<NfceItem> itens, Path destino) throws IOException {
//        return AcbrIniReforma2026.gerarIniNFCe(nfce, itens, destino);
//    }
}
