package br.com.nsym.application.api.reforma.importer.resteasy;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import br.com.nsym.domain.model.service.fiscal.reforma.ExcelCstCClassTribImporter;
import br.com.nsym.domain.model.service.fiscal.reforma.ExcelCstCClassTribImporter.Relatorio;

@RequestScoped
@Path("/api/reforma/importar-resteasy")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class ImportacaoReformaUploadResourceResteasy {

    @Inject
    private ExcelCstCClassTribImporter importer;

    @POST
    public Response upload(@MultipartForm UploadForm form) {

        if (form == null || form.getFile() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erro\":\"arquivo não enviado\"}")
                    .build();
        }

        try {
            Relatorio rel = importer.importar(form.getFile());
            return Response.ok(rel).build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("{\"erro\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}
