package br.com.nsym.application.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Named
@RequestScoped
public class ImagemBean {


	//	 private static StreamedContent defaultFileContent;
	private StreamedContent fileContent;


	public StreamedContent getFileContent() throws IOException
	{


		BufferedInputStream in=new BufferedInputStream(new FileInputStream("C:\\ibrcomp\\captcha\\captcha.jpg"));
		byte[] bytes=new byte[in.available()];
		in.read(bytes);
		in.close();			   

		fileContent = new DefaultStreamedContent(new ByteArrayInputStream(bytes), "image/jpeg");

		return fileContent;
	}


}
