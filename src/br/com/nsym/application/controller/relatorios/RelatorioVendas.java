package br.com.nsym.application.controller.relatorios;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrinterName;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import br.com.ibrcomp.exception.RelatoriosException;
import br.com.nsym.application.component.Translator;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

@RequestScoped
public class RelatorioVendas {
	
	@Inject
	private Translator tradutor = new Translator();
	
	private FacesContext facesContext = FacesContext.getCurrentInstance();
	
	
	
	public void visualizaPDF(String path,Map<String, Object> parametros,String nomeArquivo,JRBeanCollectionDataSource data ) throws JRException, IOException, RelatoriosException { //throws RelatoriosException   {
	//	try {
			for (Iterator<Object> it = parametros.values().iterator(); it.hasNext();){

				System.out.println(it.next());
			}
			
//			JRBeanCollectionDataSource data = new JRBeanCollectionDataSource(dataSource,false);
			String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(path);
			parametros.put("CollectionBeanParam", data);
			JasperReport report = JasperCompileManager.compileReport(reportPath);
			JasperPrint print  = JasperFillManager.fillReport(report, parametros, new JREmptyDataSource());
			HttpServletResponse response = (HttpServletResponse)facesContext.getExternalContext().getResponse();
			response.reset();
			response.setContentType("application/pdf"); // inline - para abrir o PDF no lugar do attachment / attachment - para DOWNLOAD do arquivo
			response.setHeader("Content-Disposition", "inline;filename=\""+nomeArquivo.trim()+".pdf\""); 
			ServletOutputStream servletOutputStream = response.getOutputStream();
			if (print != null) {
				JRPdfExporter  exporter = new JRPdfExporter();
				exporter.setExporterInput(new SimpleExporterInput(print));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(servletOutputStream));
				exporter.exportReport();
//				JasperExportManager.exportReportToPdfStream(print, servletOutputStream);
			}else {
				facesContext.responseComplete();
				throw new  RelatoriosException(tradutor.translate("relatorio.error.empty"));
			}
			FacesContext.getCurrentInstance().responseComplete();
		//}catch (Exception exc) {
			
			//throw new RelatoriosException(tradutor.translate("relatorio.error.unknown")+ " Mais Vendidos", exc.getCause());
		//}
	}
	
	public void visualizaXLS(String path,Map<String, Object> parametros,String nomeArquivo,JRBeanCollectionDataSource data ) throws JRException, IOException, RelatoriosException { //throws RelatoriosException   {
		//	try {
				for (Iterator<Object> it = parametros.values().iterator(); it.hasNext();){

					System.out.println(it.next());
				}
				
//				JRBeanCollectionDataSource data = new JRBeanCollectionDataSource(dataSource,false);
				String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(path);
				parametros.put("CollectionBeanParam", data);
				JasperReport report = JasperCompileManager.compileReport(reportPath);
				JasperPrint print  = JasperFillManager.fillReport(report, parametros, new JREmptyDataSource());
				
				SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
				configuration.setOnePagePerSheet(true);
				configuration.setIgnoreGraphics(false);
				HttpServletResponse response = (HttpServletResponse)facesContext.getExternalContext().getResponse();
				response.reset();
				response.setContentType("application/excel"); // inline - para abrir o PDF no lugar do attachment / attachment - para DOWNLOAD do arquivo
				response.setHeader("Content-Disposition", "attachment;filename=\""+nomeArquivo.trim()+".xlsx\""); 
				ServletOutputStream servletOutputStream = response.getOutputStream();
//				File outputFile = new File(nomeArquivo.trim()+".xlsx");
//				try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//					     OutputStream fileOutputStream = new FileOutputStream(outputFile)) {
//					Exporter exporter = new JRXlsxExporter();
//					    exporter.setExporterInput(new SimpleExporterInput(print));
//					    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
//					    exporter.setConfiguration(configuration);
//					    exporter.exportReport();
//					    byteArrayOutputStream.writeTo(fileOutputStream);
//					}catch (JRException je) {
//						facesContext.responseComplete();
//						throw new  RelatoriosException(tradutor.translate("relatorio.error.empty"));
//					}
				if (print != null) {
					JRXlsxExporter  exporter = new JRXlsxExporter();
					exporter.setExporterInput(new SimpleExporterInput(print));
					exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(servletOutputStream));
					exporter.setConfiguration(configuration);
					exporter.exportReport();
//					JasperExportManager.exportReportToPdfStream(print, servletOutputStream);
				}else {
					facesContext.responseComplete();
					throw new  RelatoriosException(tradutor.translate("relatorio.error.empty"));
				}
				FacesContext.getCurrentInstance().responseComplete();
			//}catch (Exception exc) {
				
				//throw new RelatoriosException(tradutor.translate("relatorio.error.unknown")+ " Mais Vendidos", exc.getCause());
			//}
		}
	
	public void visualizaPDFTeste(String path,Map<String, Object> parametros,String nomeArquivo,JRBeanCollectionDataSource data ) throws JRException, IOException, RelatoriosException { //throws RelatoriosException   {
		//	try {
				for (Iterator<Object> it = parametros.values().iterator(); it.hasNext();){

					System.out.println(it.next());
				}
				
//				JRBeanCollectionDataSource data = new JRBeanCollectionDataSource(dataSource,false);
				String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(path);
				parametros.put("CollectionBeanParam", data);
				JasperReport report = JasperCompileManager.compileReport(reportPath);
				JasperPrint print  = JasperFillManager.fillReport(report, parametros, new JREmptyDataSource());
//				HttpServletResponse response = (HttpServletResponse)facesContext.getExternalContext().getResponse();
//				response.reset();
//				response.setContentType("application/pdf"); // inline - para abrir o PDF no lugar do attachment / attachment - para DOWNLOAD do arquivo
//				response.setHeader("Content-Disposition", "inline;filename=\""+nomeArquivo.trim()+".pdf\""); 
//				ServletOutputStream servletOutputStream = response.getOutputStream();
//				if (print != null) {
//					JRPdfExporter  exporter = new JRPdfExporter();
//					exporter.setExporterInput(new SimpleExporterInput(print));
//					exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(servletOutputStream));
//					exporter.exportReport();
////					JasperExportManager.exportReportToPdfStream(print, servletOutputStream);
//				}else {
//					facesContext.responseComplete();
//					throw new  RelatoriosException(tradutor.translate("relatorio.error.empty"));
//				}
				printReportToPrinter(print,"");
				FacesContext.getCurrentInstance().responseComplete();
			//}catch (Exception exc) {
				
				//throw new RelatoriosException(tradutor.translate("relatorio.error.unknown")+ " Mais Vendidos", exc.getCause());
			//}
		}
	
	private void printReportToPrinter(JasperPrint jasperPrint,String impressora) throws JRException {

		//Get the printers names
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);

		//Lets set the printer name based on the registered printers driver name (you can see the printer names in the services variable at debugging) 
		String selectedPrinter = impressora;   
//		PrintServiceLookup.lookupDefaultPrintService()
		// String selectedPrinter = "\\\\S-BPPRINT\\HP Color LaserJet 4700"; // examlpe to network shared printer

		System.out.println("Number of print services: " + services.length);
		for (PrintService printService : services) {
			System.out.println("Impressora: " + printService.getName());
		}
		PrintService selectedService = PrintServiceLookup.lookupDefaultPrintService();

		//Set the printing settings
		PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
		printRequestAttributeSet.add(MediaSizeName.ISO_A4);
		printRequestAttributeSet.add(new Copies(1));
		if (jasperPrint.getOrientationValue() == net.sf.jasperreports.engine.type.OrientationEnum.LANDSCAPE) { 
		  printRequestAttributeSet.add(OrientationRequested.LANDSCAPE); 
		} else { 
		  printRequestAttributeSet.add(OrientationRequested.PORTRAIT); 
		} 
		PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
		printServiceAttributeSet.add(new PrinterName(selectedService.getName(), null));

		JRPrintServiceExporter exporter = new JRPrintServiceExporter();
		SimplePrintServiceExporterConfiguration configuration = new SimplePrintServiceExporterConfiguration();
		configuration.setPrintRequestAttributeSet(printRequestAttributeSet);
		configuration.setPrintServiceAttributeSet(printServiceAttributeSet);
		configuration.setDisplayPageDialog(false);
		configuration.setDisplayPrintDialog(false);

		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setConfiguration(configuration);

		//Iterate through available printer, and once matched with our <selectedPrinter>, go ahead and print!
		if(services != null && services.length != 0){
		  for(PrintService service : services){
		      String existingPrinter = service.getName();
		      if(existingPrinter.equals(selectedPrinter))
		      {
		          selectedService = service;
		          break;
		      }
		  }
		}
		if(selectedService != null)
		{   
		  try{
		      //Lets the printer do its magic!
		      exporter.exportReport();
		  }catch(Exception e){
		System.out.println("JasperReport Error: "+e.getMessage());
		  }
		}else{
		  System.out.println("JasperReport Error: Printer not found!");
		}
	}
}	
	

