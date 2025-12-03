package br.com.nsym.domain.misc;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.ProjectStage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import br.com.nsym.application.channels.AcbrComunica;
import br.com.nsym.application.channels.DadosDeConexaoSocket;
import br.com.nsym.application.producer.qualifier.AuthenticatedUser;
import br.com.nsym.domain.model.entity.cadastro.Cliente;
import br.com.nsym.domain.model.entity.cadastro.Contato;
import br.com.nsym.domain.model.entity.cadastro.Email;
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.EndComplemento;
import br.com.nsym.domain.model.entity.cadastro.Endereco;
import br.com.nsym.domain.model.entity.cadastro.Filial;
import br.com.nsym.domain.model.entity.cadastro.Fone;
import br.com.nsym.domain.model.entity.tools.Enquadramento;
import br.com.nsym.domain.model.entity.tools.TipoCliente;
import br.com.nsym.domain.model.entity.tools.Uf;
import br.com.nsym.domain.model.repository.cadastro.ClienteRepository;
import br.com.nsym.domain.model.repository.cadastro.ContatoRepository;
import br.com.nsym.domain.model.repository.cadastro.EmpresaRepository;
import br.com.nsym.domain.model.repository.cadastro.EndComplementoRepository;
import br.com.nsym.domain.model.repository.cadastro.EnderecoRepository;
import br.com.nsym.domain.model.repository.cadastro.FilialRepository;
import br.com.nsym.domain.model.repository.cadastro.FoneRepository;
import br.com.nsym.domain.model.repository.cadastro.PaisRepository;
import br.com.nsym.domain.model.security.User;
import br.com.nsym.infraestrutura.configuration.ApplicationUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@RequestScoped
public class ImportaCliente {

	@Getter
	@Inject
	@AuthenticatedUser
	private User usuarioAutenticado;

	//	@Inject
	//	private EmpresaRepository empresaDao;

	//	@Inject
	//	private FilialRepository filialDao;

	//	private Scanner scanner;

	private Scanner scannerLinha;

	//	private Cliente cliente = new Cliente();

	@Inject
	private ClienteRepository clienteDao;

	//	private Fone fone = new Fone();

	//	private Endereco end = new Endereco();

	@Inject
	private ContatoRepository contatoDao;

	@Inject
	private FoneRepository foneDao;

	//	@Inject
	//	private EmailRepository emailDao;

	@Inject
	private PaisRepository paisDao;

	@Inject
	private EnderecoRepository endDao;

	//	private EndComplemento endComplemento = new EndComplemento();

	@Inject
	private EndComplementoRepository endComplementoDao;

	@Getter
	@Setter
	private List<Cliente> listaCliente = new ArrayList<>();
	
	@Getter
	@Setter
	private List<ClienteTemp> listaInvalido = new ArrayList<>();

	@Inject
	private AcbrComunica acbr;

	private String arquivo;
	
	@Inject
	private EmpresaRepository empresaDao;
	
	@Inject
	private FilialRepository filialDao;

	public  DadosDeConexaoSocket pegaConexao(Empresa emp){
		DadosDeConexaoSocket conexao; 
		conexao = new DadosDeConexaoSocket(emp.getIpAcbr().trim(),emp.getPortaAcbr());
		System.out.println(emp.getIpAcbr().trim());
		System.out.println(emp.getPortaAcbr());
		return conexao;
	}
	
	/**
	 * Pega o IP de internet do usuario (WAN) e disponibiliza para o sistema
	 */
	public String meuIP() {
		 HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();  
		    String ip = null;
		    
		    ip = request.getHeader("x-forwarded-for");
		    if (ip == null) {
		    	ip = request.getHeader("X_FORWARDED_FOR");
		        if (ip == null){
		        	ip = request.getRemoteAddr();
		        }
		    }  
		    
		    return ip;
	}
	
	/**
	 * Pega as informaï¿½ï¿½es da empresa / filial para conexao com acbr
	 * @return a conexao preenchida
	 */
	public  DadosDeConexaoSocket pegaConexao(){
		DadosDeConexaoSocket conexao; 
		Empresa emp = new Empresa();
		Filial fil = new Filial();
		if (this.getUsuarioAutenticado().getIdFilial() == null){
			if (this.getUsuarioAutenticado().getIdEmpresa() == null) {
				if (ApplicationUtils.isStageRunning(ProjectStage.Production)){
					System.out.println("Stagio ProduÃ§Ã£o");
					conexao = new DadosDeConexaoSocket("ibrcomp.no-ip.org",3434);
				}else {
					System.out.println("Stagio Teste");
					conexao = new DadosDeConexaoSocket("127.0.0.1",3434);
				}
			}else {
				emp = this.empresaDao.findById(this.getUsuarioAutenticado().getIdEmpresa(), false);
			}
			if (emp.getIpAcbr() == null) {
				conexao = new DadosDeConexaoSocket(meuIP().trim(),emp.getPortaAcbr());
			}else {
				conexao = new DadosDeConexaoSocket(emp.getIpAcbr().trim(),emp.getPortaAcbr());
			}
		}else{
			fil = this.filialDao.findById(this.getUsuarioAutenticado().getIdFilial(), false);
			if (fil.getIpAcbr() == null) {
				conexao = new DadosDeConexaoSocket(meuIP().trim(),fil.getPortaAcbr());
			}else {
				conexao = new DadosDeConexaoSocket(fil.getIpAcbr().trim(),fil.getPortaAcbr());
			}
		}
		return conexao;
	}
	
	@Transactional
	public List<ClienteTemp> leArquivo(String nomeArquivo, Empresa emp) throws FileNotFoundException  {
		try {
			String respostaOk;
			System.out.println("Inicio do le arquivo");		
			List<ClienteTemp> listaCliente = new ArrayList<>();
			System.out.println(nomeArquivo);
			String loadFile = "ACBr.LoadFromFile(\"C:\\ibrcomp\\import\\"+nomeArquivo+")";
			this.arquivo =acbr.enviaComandoACBr(pegaConexao(emp), loadFile);

			respostaOk = this.arquivo.substring(3, this.arquivo.length()).trim();
			respostaOk =  removerAcentos(respostaOk);
			System.out.println(respostaOk);
			scannerLinha = new Scanner(respostaOk).useDelimiter("\\;|\\n");
			while (scannerLinha.hasNext()) {
				ClienteTemp temp = new ClienteTemp();
				temp.doc = scannerLinha.next();
				System.out.println(temp.doc);
				temp.fantasia = scannerLinha.next();
				temp.nome = scannerLinha.next();
				temp.cep = scannerLinha.next();
				temp.logradouro =  scannerLinha.next();
				temp.numero = scannerLinha.next();
				temp.complemento = scannerLinha.next();
				temp.bairro =  scannerLinha.next();
				temp.localidade =  scannerLinha.next();
				temp.uf = scannerLinha.next();
				temp.emailnfe =  scannerLinha.next();
				temp.fone1 = scannerLinha.next();
				temp.fone2 = scannerLinha.next();
				temp.juridico= scannerLinha.next();
				temp.insc = scannerLinha.next();
				temp.contato =  scannerLinha.next();
				
				listaCliente.add(temp);
				//				}
			}
			scannerLinha.close();
			//		scanner.close();
			return listaCliente;
		} catch (NoSuchElementException n) {
			System.out.println (n.getStackTrace());
			return null;
		}catch (Exception o) {
			o.printStackTrace();
			System.out.println("Não foi possível importar os dados erro: " );
			return null;
		}

	}
	/**
	 * Filtra os Cep recebidos e nulos para persistir na base
	 * @param cliTemp
	 */
	@Transactional
	public void filtraCepEGrava(List<ClienteTemp> cliTemp) {
		try {
			List<ClienteTemp> listaUsar = new ArrayList<>();
			Endereco end = new Endereco();
			boolean adiciona = false;
			String cepCorrigido;
			// Criando uma lista apenas com cep valido e não repitido
			for (ClienteTemp cepTemp : cliTemp) {
				if (cepTemp.getCep().trim().length() == 7) {
					 cepCorrigido = "0"+cepTemp.getCep().trim();
				}else {
					if (cepTemp.getCep().trim().length() != 8 ) {
						cepCorrigido = cepTemp.getCep().trim();
						System.out.println("Cep invalido");
						cepTemp.setMotivoInvalido("Cep invalido");
					}
					cepCorrigido = cepTemp.getCep().trim();
				}
				if (cepCorrigido != null && cepCorrigido.trim().length() == 8) {
					System.out.println("entrei no foreach do clitemp ");
					if (listaUsar.size() == 0 ) {
						cepTemp.setCep(cepCorrigido);
						listaUsar.add(cepTemp);
					}
					for (ClienteTemp novaLista : listaUsar) {
						System.out.println("entou dentro da listaUSAR");
						if (cepCorrigido.equalsIgnoreCase(novaLista.getCep())) {
							adiciona = false;
							System.out.println("adiciona = false");
						}else {
							adiciona=true;
							System.out.println("adiciona = true");
						}
					}
				}
				if (adiciona) {
					if (cepCorrigido.equalsIgnoreCase("0") || cepCorrigido.trim().length() < 8) {
						System.out.println("cep ignorado" + cepTemp.getCep());
					}else {
						cepTemp.setCep(cepCorrigido);
						listaUsar.add(cepTemp);
						System.out.println("adicionei na lista usar" + cepTemp.getCep());
					}
				}
			}
			System.out.println("Quantidade de ceps é: " + listaUsar.size());

			// Salvando a lista de cep na base de dados

			for (ClienteTemp clienteTemp : listaUsar) {
				System.out.println(clienteTemp.getCep());
				StringBuilder stringBuilder = new StringBuilder(clienteTemp.getCep());
				stringBuilder.insert(clienteTemp.getCep().trim().length() - 3 , "-");
				System.out.println(stringBuilder.toString());
				end = this.endDao.listCep(stringBuilder.toString());
				if (end == null) {
					end = new Endereco();
					System.out.println(clienteTemp.getLogradouro());
					end.setLogra(clienteTemp.getLogradouro());
					System.out.println(clienteTemp.getBairro());
					end.setBairro(clienteTemp.getBairro());
					System.out.println(stringBuilder.toString());
					end.setCep(stringBuilder.toString().trim());
					System.out.println(clienteTemp.getLocalidade());
					end.setLocalidade(clienteTemp.getLocalidade());
					System.out.println(clienteTemp.getUf().trim());
					end.setUf(Uf.valueOf(clienteTemp.getUf().trim()));
					end = this.endDao.save(end);
				}

			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Recebe uma Lista de ClienteTemp e Persiste no banco de dados validando as informaçoes
	 * como se possuie CEP, se possui CNPJ ou CPF caso contrário será ignorado o registro.
	 * @param temp Lista de ClienteTemp
	 * @return Cliente-Nsym gravado no banco
	 */
	@Transactional
	public void gravaListaCliente(List<ClienteTemp> cliTemp, Long idEmpresa,Long idFilial) {

		try {
			//filtraCepEGrava(cliTemp);
			int i = 0 ;
			String cepCorrigido;
			for (ClienteTemp temp : cliTemp) {
				
				String docNew =  CpfCnpjUtils.acrescentaZeros(temp.getDoc().trim(),temp.getJuridico().trim());
				System.out.println("documento capturado: " + temp.getDoc().trim());
				System.out.println("documento corrigido :" + docNew);
				String docPontuacao = CpfCnpjUtils.adcionaCaracteresEspeciais(docNew);
				System.out.println("DOCPONTUACAO: " +docPontuacao);
				if (CpfCnpjUtils.isValid(docNew)) {
					System.out.println("documento valido!");
					if (clienteDao.clienteCadastrado(docPontuacao, idEmpresa) == false) {
						System.out.println("Cliente nao cadastrado!");
						System.out.println("CEp: " + temp.cep + "tamanho: " + temp.getCep().trim().length());
						if (temp.getCep().trim().length() == 7) {
							 cepCorrigido = "0"+temp.getCep().trim();
						}else {
							if (temp.getCep().trim().length() != 8 ) {
								cepCorrigido = temp.getCep().trim();
								System.out.println("Cep invalido");
								temp.setMotivoInvalido("Cep invalido");
								this.listaInvalido.add(temp);
							}
							cepCorrigido = temp.getCep().trim();
						}
						if (cepCorrigido.length() == 8) {
							System.out.println(" estou dentro do cep != vazio " + cepCorrigido);

							Cliente cli = new Cliente();
							Endereco end = new Endereco();
							Contato contato = new Contato();
							Email emailnfe = new Email();
							Fone fone = new Fone();
							Fone fone2 = new Fone();
							Fone fone3 = new Fone();

							EndComplemento endComplemento = new EndComplemento();
							if (CpfCnpjUtils.isCpfOrCnpjOrNull(docPontuacao) == 1) {
								cli.setCnpj(docPontuacao);
								System.out.println("CNPJ : " + docPontuacao);
								if (!temp.insc.equalsIgnoreCase("ISENTO")) {
									cli.setInscEstadual(apenasNumeros(temp.insc.trim()));
									cli.setEnquadramento(Enquadramento.SimplesNacional);
									cli.setTipoCliente(TipoCliente.Rev);
								}else {
									cli.setTipoCliente(TipoCliente.Cfi);								
								}
							}else {
								cli.setCpf(docPontuacao);
								System.out.println("CPF : " + docPontuacao);
								cli.setTipoCliente(TipoCliente.CfC);
							}
							cli.setRazaoSocial(temp.nome.trim());
							if (temp.fantasia != null) {
								cli.setNomeFantasia(temp.fantasia.trim());
							}
							StringBuilder stringBuilder = new StringBuilder(cepCorrigido);
							stringBuilder.insert(cepCorrigido.length() - 3 , "-");
							System.out.println(stringBuilder.toString());
							cli.setEstado(Uf.valueOf(temp.uf.trim().toUpperCase()));
							end = this.endDao.procuraCepBase(stringBuilder.toString());
							System.out.println("endNumero: " + temp.getNumero().trim());
							if (temp.getNumero().trim()!= "") {
								endComplemento.setNumero(temp.getNumero().trim());
							}else {
								endComplemento.setNumero("S/N");
							}

							if (end != null) {
//								if (end.getLogra() == null ) {
//									end.setLogra(temp.logradouro.trim());
//									end.setBairro(temp.getBairro().trim());
//									end.setLocalidade(temp.localidade.trim());
//									end.setUf(Uf.valueOf(temp.uf.trim()));
//									end = this.endDao.save(end);
//								}
							}else {
								end = new Endereco();
								end.setLogra(temp.logradouro.trim());
								end.setBairro(temp.bairro.trim());
								end.setCep(cepCorrigido);
								end.setLocalidade(temp.localidade.trim());
								end.setUf(Uf.valueOf(temp.uf.trim()));
								end = this.endDao.save(end);
							}
							if (temp.contato.isEmpty()) {							
								contato.setNome("Loja");														
							}else {
								contato.setNome(temp.getContato().trim());
							}

							if (!temp.ddd.isEmpty() && !temp.fone1.isEmpty()) {	
								if (temp.ddd.trim().length() >2){
									temp.ddd = temp.ddd.trim().substring(1,temp.ddd.trim().length());
								}
								if	(temp.ddd.trim().length() == 2 && temp.fone1.trim().length() >7){
									fone.setDdd(Integer.parseInt(apenasNumeros(temp.ddd.trim())));
									fone.setFone(Integer.parseInt(apenasNumeros(temp.fone1.trim())));
								}
							}else {
								if (!temp.fone1.isEmpty()) {
									if (temp.getFone1().length() > 9) {
										fone.setDdd(Integer.parseInt(apenasNumeros(temp.getFone1().trim().subSequence(0,2).toString().trim())));
										fone.setFone(Integer.parseInt(apenasNumeros(temp.getFone1().trim().subSequence(2, temp.getFone1().length()).toString())));
									}else {
										fone.setFone(Integer.parseInt(apenasNumeros(temp.getFone1().trim())));
									}
								}
							}
							if (!temp.ddd.isEmpty() && !temp.fone2.isEmpty()) {	
								if (temp.ddd.trim().length() >2){
									temp.ddd = temp.ddd.trim().substring(1,temp.ddd.trim().length());
								}
								if	(temp.ddd.trim().length() == 2 && temp.fone2.trim().length() >7){
									fone2.setDdd(Integer.parseInt(apenasNumeros(temp.ddd.trim())));
									fone2.setFone(Integer.parseInt(apenasNumeros(temp.fone2.trim())));	
								}
							}else {
								if (!temp.fone2.isEmpty()) {
									if (temp.getFone2().length() > 9) {
										fone2.setDdd(Integer.parseInt(apenasNumeros(temp.getFone2().trim().subSequence(0,2).toString())));
										fone2.setFone(Integer.parseInt(apenasNumeros(temp.getFone2().trim().subSequence(2, temp.getFone2().length()).toString())));
									}else {
										fone2.setFone(Integer.parseInt(apenasNumeros(temp.getFone2().trim())));
									}
								}
							}
							if (!temp.ddd2.isEmpty() && !temp.fax.isEmpty()) {	
								if (temp.fax.trim().length() >7) {
									if (temp.ddd2.trim().length() >2){
										temp.ddd2 = temp.ddd2.trim().substring(1,temp.ddd2.trim().length());
									}
									if (!temp.ddd2.trim().isEmpty()) {
										fone3.setDdd(Integer.parseInt(apenasNumeros(temp.ddd2.trim())));
									}else {
										if (temp.ddd.trim().length() >2){
											fone3.setDdd(Integer.parseInt(apenasNumeros(temp.ddd.trim())));
										}
									}
									fone3.setFone(Integer.parseInt(apenasNumeros(temp.fax.trim())));
								}
							}
							if (idFilial == null) {
								cli.setIdEmpresa(idEmpresa);
							}else {
								cli.setIdFilial(idFilial);
								cli.setIdEmpresa(idEmpresa);
							}
							cli.setPais(this.paisDao.listaPaises("BRASIL"));
							cli = clienteDao.save(cli);
							//						if (end.getCep() == null) {
							//							end.setCep(stringBuilder.toString());
							//						}
							if (!temp.emailnfe.isEmpty()){
								emailnfe.setEmail(temp.emailnfe.trim().toLowerCase());
								emailnfe.setCliente(cli);
							}
							endComplemento.setCliente(cli);
							endComplemento.setEndereco(end);
							endComplemento.setLogradouro(temp.logradouro.trim());
							endComplemento.setComplemento(temp.complemento.trim());
							if (end.getBairro() != null) {
								endComplemento.setBairro(end.getBairro().trim());
							}else {
								endComplemento.setBairro("vazio");
							}
							endComplemento = endComplementoDao.save(endComplemento);
							if (fone.getFone() >1 ){
								System.out.println("fone 1 : " + fone.getDdd() + " " + fone.getFone());
								contato.setCliente(cli);
								contato = contatoDao.save(contato);
								fone.setContato(contato);
								fone = foneDao.save(fone);
							}
							if (fone2.getFone()>1 ){
								System.out.println("fone 1 : " + fone2.getDdd() + " " + fone2.getFone());
								if (contato.getId() == null) {
									contato.setCliente(cli);
									contato = contatoDao.save(contato);
								}
								fone2.setContato(contato);
								fone2 = foneDao.save(fone2);
							}
							if (fone3.getFone()>1 ){
								System.out.println("fone 1 : " + fone2.getDdd() + " " + fone2.getFone());
								if (contato.getId() == null) {
									contato.setCliente(cli);
									contato = contatoDao.save(contato);
								}
								fone3.setContato(contato);
								fone3 = foneDao.save(fone3);
							}
							cli.setEmailNFE(emailnfe);
							cli.setEndereco(endComplemento);
							cli = clienteDao.save(cli);
							i++;
						}else {
						}
					}
				}else {
					System.out.println("Documento invalido");
					temp.setMotivoInvalido("CNPJ/CPF invalido");
					this.listaInvalido.add(temp);
				}
			}
			System.out.println("Quantidade de registros persistidos : " + i);
			System.out.println("quantidade de clientes lidos : " + cliTemp.size());
			acbr.geraListaImpInvalido(listaInvalido,pegaConexao());
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erro ao gravar!");
		}

	}

	/**
	 * A representacao concreta do Cliente
	 */
	@ToString
	@EqualsAndHashCode
	public static class ClienteTemp {

		@Getter
		@Setter
		private String doc="";
		@Getter
		@Setter
		private String logradouro="";
		@Getter
		@Setter
		private String complemento="";
		@Getter
		@Setter
		private String bairro="";
		@Getter
		@Setter
		private String localidade="";
		@Getter
		@Setter
		private String uf="";
		@Getter
		@Setter
		private String numero="";
		@Getter
		@Setter
		private String nome="";
		@Getter
		@Setter
		private String ddd="";
		@Getter
		@Setter
		private String fone1="";
		@Getter
		@Setter
		private String fone2="";
		@Getter
		@Setter
		private String emailnfe="";
		@Getter
		@Setter
		private String insc="";
		@Getter
		@Setter
		private String juridico="";
		@Getter
		@Setter
		private String ddd2="";
		@Getter
		@Setter
		private String cep="";
		@Getter
		@Setter
		private String fax="";
		@Getter
		@Setter
		private String contato="";
		@Getter
		@Setter
		private String conhecido="";
		@Getter
		@Setter
		private String codigoCliente="";
		@Getter
		@Setter
		private String suframa="";
		@Getter
		@Setter
		private String aliqAprov="";
		@Getter
		@Setter
		private String fantasia;
		@Getter
		@Setter
		private String motivoInvalido;
		
		@Override
		public String toString() {
			return "ClienteTemp [doc=" + doc + ", nome=" + nome +", tipoCadstro="+ juridico +", CEP="+cep+", motivoInvalido=" + motivoInvalido + "]";
		}

	}
	public static String removerAcentos(String str) {
		if (str == null){
			return "";
		}else{
			str = Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
			
			return str;
		}
	}

	public static String apenasNumeros(String str){
		if (str==null){
			return "";
		}else{
			return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^0-9]+", "");
		}
	}
	
	

}
