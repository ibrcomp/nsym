package br.com.nsym.domain.misc;

import java.util.List;

import javax.enterprise.context.RequestScoped;

import br.com.nsym.domain.model.entity.cadastro.Endereco;
import br.com.nsym.infraestrutura.configuration.ApplicationUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Servico de busca de enderecos para o cadastro de contatos do sistema
 * 
 * @author Ibrahim Yousef Quatani	
 *
 * @version 2.0.0
 * @since 1.2.0, 29/10/2016
 */
@RequestScoped
public class AddressFinder {
	

    /**
     * Busca os dados referentes a um endereco partindo do CEP como referencia
     * 
     * @param zipcode o cep
     * @return o endereco
     */
    public AddressMania findAddressByZipcode(String zipcode) {
    		
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ApplicationUtils.getConfiguration("ws.cep"))
                .build();
        
        final ZipcodeService zipcodeService 
                = restAdapter.create(ZipcodeService.class);
        
        return zipcodeService.findAddress(zipcode);
    }
    
    /**
     * Busca os dados referentes a um endereco partindo do CEP como referencia
     * 
     * @param zipcode o cep
     * @return o endereco
     */
    public Address findAddressByZipcodeVia(String zipcode) {
    		
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ApplicationUtils.getConfiguration("ws.via"))
                .build();
        
        final ZipcodeService zipcodeService 
                = restAdapter.create(ZipcodeService.class);
        
        return zipcodeService.findAddressVia(zipcode);
    }
    /**
     * Retorna o status da assinatura junto a webmania 
     * @return
     */
    
    public StatusWebMania statusAssinatura() {
		
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ApplicationUtils.getConfiguration("ws.status"))
                .build();
        
        final ZipcodeService zipcodeService 
                = restAdapter.create(ZipcodeService.class);
        
        return zipcodeService.statusMania();
    }
    
    /**
     * Busca cep utilizando logradouro,localidade e uf como referencia
     */
    public  List<Address> findAddressByLogradouro(String uf, String localidade, String logradouro){
    		
    	 String pesquisa = uf+"/"+localidade+"/"+logradouro;
    	 final RestAdapter restAdapter = new RestAdapter.Builder()
                 .setEndpoint(ApplicationUtils.getConfiguration("ws.via"))
                 .build();
         final ZipcodeService zipcodeService 
                 = restAdapter.create(ZipcodeService.class);
         
         List<Address> resulto = zipcodeService.findListAddress(pesquisa);
		return  resulto;
    }
    /**
     * Definição do servico de busca do CEP para o retrofit
     */
    public interface ZipcodeService {
        
        /**
         * @param zipcode
         * @return 
         */
        @GET("/ws/{zipcode}/?app_key=d88VnDY9RSOqF2k4nS4BHkIYdaGHzFvB&app_secret=sGmx9Qeu1dD5TBWRM4lWPH6exVyukU35wkLfOESQGLWpBLkk")
        AddressMania findAddress(@Path("zipcode") String zipcode);
        @GET("/ws/?app_key=d88VnDY9RSOqF2k4nS4BHkIYdaGHzFvB&app_secret=sGmx9Qeu1dD5TBWRM4lWPH6exVyukU35wkLfOESQGLWpBLkk")
        StatusWebMania statusMania();
        @GET("/ws/{zipcode}/json")
        List<Address> findListAddress(@Path("zipcode")String zipcode);
        @GET("/ws/{zipcode}/json")
        Address findAddressVia(@Path("zipcode")String zipcode);
    }
        
    /**
     * A representacao concreta do endereco ViaCep
     */
    @ToString
    @EqualsAndHashCode
    public static class Address {

        @Getter
        @Setter
        private String cep;
        @Getter
        @Setter
        private String logradouro;
        @Getter
        @Setter
        private String complemento;
        @Getter
        @Setter
        private String bairro;
        @Getter
        @Setter
        private String localidade;
        @Getter
        @Setter
        private String uf;
        @Getter
        @Setter
        private String ibge;
        
        
        
        /**
         * @return o nome completo do estado referente a unidade federativa
         */
        public String getFullUfName() {
            
            switch (this.uf) {
                case "AC": return "Acre";
                case "AL": return "Alagoas";
                case "AP": return "Amap�";
                case "AM": return "Amazonas";
                case "BA": return "Bahia";
                case "CE": return "Ceará";
                case "DF": return "Distrito Federal";
                case "ES": return "Espírito Santo";
                case "GO": return "Goiás";
                case "MA": return "Maranhão";
                case "MT": return "Mato Grosso";
                case "MS": return "Mato Grosso do Sul";
                case "MG": return "Minas Gerais";
                case "PA": return "Pará";
                case "PB": return "Paraíba";
                case "PR": return "Paraná";
                case "PE": return "Pernambuco";
                case "PI": return "Piauí";
                case "RJ": return "Rio de Janeiro";
                case "RN": return "Rio Grande do Norte";
                case "RS": return "Rio Grande do Sul";
                case "RO": return "Rondônia";
                case "RR": return "Roraima";
                case "SC": return "Santa Catarina";
                case "SP": return "São Paulo";
                case "SE": return "Sergipe";
                case "TO": return "Tocantins";
                default: return "Desconhecido";
            }
        }
    }
    @ToString
    @EqualsAndHashCode
    public static class StatusWebMania{
    	
    	@Getter
    	@Setter
    	private String total;
    	@Getter
    	@Setter
    	private String limit;
    	@Getter
    	@Setter
    	private String expires_in;
    	@Getter
    	@Setter
    	private String plan;
    	
    	
    }
    /**
     * A representacao concreta do endereco Mania
     */
    @ToString
    @EqualsAndHashCode
    public static class AddressMania {

        @Getter
        @Setter
        private String cep;
        @Getter
        @Setter
        private String endereco;
        @Getter
        @Setter
        private String bairro;
        @Getter
        @Setter
        private String cidade;
        @Getter
        @Setter
        private String uf;
        @Getter
        @Setter
        private String ibge;
        
        
        
        
        /**
         * @return o nome completo do estado referente a unidade federativa
         */
        public String getFullUfName() {
            
            switch (this.uf) {
                case "AC": return "Acre";
                case "AL": return "Alagoas";
                case "AP": return "Amap�";
                case "AM": return "Amazonas";
                case "BA": return "Bahia";
                case "CE": return "Ceará";
                case "DF": return "Distrito Federal";
                case "ES": return "Espírito Santo";
                case "GO": return "Goiás";
                case "MA": return "Maranhão";
                case "MT": return "Mato Grosso";
                case "MS": return "Mato Grosso do Sul";
                case "MG": return "Minas Gerais";
                case "PA": return "Pará";
                case "PB": return "Paraíba";
                case "PR": return "Paraná";
                case "PE": return "Pernambuco";
                case "PI": return "Piauí";
                case "RJ": return "Rio de Janeiro";
                case "RN": return "Rio Grande do Norte";
                case "RS": return "Rio Grande do Sul";
                case "RO": return "Rondônia";
                case "RR": return "Roraima";
                case "SC": return "Santa Catarina";
                case "SP": return "São Paulo";
                case "SE": return "Sergipe";
                case "TO": return "Tocantins";
                default: return "Desconhecido";
            }
        }
    }
}
