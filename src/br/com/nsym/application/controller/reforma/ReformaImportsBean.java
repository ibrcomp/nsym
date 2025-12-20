package br.com.nsym.application.controller.reforma;

import java.io.IOException;
import java.io.InputStream;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import javax.servlet.http.Part;


import br.com.nsym.application.controller.AbstractBeanEmpDS;
import br.com.nsym.domain.model.service.fiscal.reforma.ExcelCstCClassTribImporter;
import br.com.nsym.domain.model.service.fiscal.reforma.ExcelCstCClassTribImporter.Relatorio;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class ReformaImportsBean extends AbstractBeanEmpDS<Object> {


    /**
	 *
	 */
	private static final long serialVersionUID = -149850796997882845L;

	@Getter @Setter
	private Part  arquivo;   // Excel com abas "CST" e "cClassTrib"

    @Getter
    private Relatorio relatorio;    // resultado da importação

    @Inject
    private ExcelCstCClassTribImporter importer;

    public void importar() {
        if (arquivo == null || arquivo.getSize() <= 0) {
            addMensagem(FacesMessage.SEVERITY_WARN,
                    "Selecione o arquivo Excel antes de importar.");
            return;
        }

        try (InputStream in = arquivo.getInputStream()) {
            relatorio = importer.importar(in);
            addMensagem(FacesMessage.SEVERITY_INFO,
                    "Importação concluída. CST: " + relatorio.getQtdeCst()
                    + ", cClassTrib: " + relatorio.getQtdeCClassTrib());
        } catch (IOException e) {
            addMensagem(FacesMessage.SEVERITY_ERROR,
                    "Erro ao ler o arquivo: " + e.getMessage());
        } catch (Exception e) {
            addMensagem(FacesMessage.SEVERITY_ERROR,
                    "Erro ao importar arquivo: " + e.getMessage());
        }
    }

    // ---------- helpers para o painel ----------

    public boolean isTemRelatorio() {
        return relatorio != null;
    }

    public int getTotalRegistrosImportados() {
        if (relatorio == null) return 0;
        return relatorio.getQtdeCst() + relatorio.getQtdeCClassTrib()+ relatorio.getQtdeCCredPres();
    }

    public String getResumoImportacao() {
        if (relatorio == null) return "";
        return "CST: " + relatorio.getQtdeCst()
             + " | cClassTrib: " + relatorio.getQtdeCClassTrib()
             + " | cCredPres: " + relatorio.getQtdeCCredPres()
             + " | Total: " + getTotalRegistrosImportados();
    }

    private void addMensagem(FacesMessage.Severity severidade, String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severidade, msg, null));
    }

	@Override
	public Object setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initializeListing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initializeForm(Long id) {
		// TODO Auto-generated method stub
		
	}
}

