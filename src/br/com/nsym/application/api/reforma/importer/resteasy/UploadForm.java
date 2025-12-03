package br.com.nsym.application.api.reforma.importer.resteasy;

import java.io.InputStream;

import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class UploadForm {

    private InputStream file;

    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream getFile() {
        return file;
    }

    public void setFile(InputStream file) {
        this.file = file;
    }
}
