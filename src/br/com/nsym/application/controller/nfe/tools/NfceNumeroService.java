package br.com.nsym.application.controller.nfe.tools;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.Filial;
import br.com.nsym.domain.model.repository.cadastro.EmpresaRepository;
import br.com.nsym.domain.model.repository.cadastro.FilialRepository;

@RequestScoped
public class NfceNumeroService {

	@Inject
	private FilialRepository filialRepository;

	@Inject
	private EmpresaRepository empresaRepository;

	@Transactional
	public Long proximoNumero(Long idEmpresa, Long idFilial) {
		if (idFilial != null) {
			Filial filial = filialRepository.lockById(idFilial);
			Long atual = Optional.ofNullable(filial.getNumeroNfce()).orElse(0L);
			filial.setNumeroNfce(atual + 1);
			filialRepository.save(filial);
			return filial.getNumeroNfce();
		}

		Empresa empresa = empresaRepository.lockById(idEmpresa);
		Long atual = Optional.ofNullable(empresa.getNumeroNfce()).orElse(0L);
		empresa.setNumeroNfce(atual + 1);
		empresaRepository.save(empresa);
		return empresa.getNumeroNfce();
	}

	public Integer resolveSerie(Long idEmpresa, Long idFilial) {
		if (idFilial != null) {
			Filial filial = filialRepository.findById(idFilial, false);
			return serieNumerica(filial != null ? filial.getSerie() : null);
		}
		Empresa empresa = empresaRepository.findById(idEmpresa, false);
		return serieNumerica(empresa != null ? empresa.getSerie() : null);
	}

	private Integer serieNumerica(String serie) {
		try {
			return serie != null ? Integer.parseInt(serie) : 1;
		} catch (NumberFormatException e) {
			return 1;
		}
	}
}