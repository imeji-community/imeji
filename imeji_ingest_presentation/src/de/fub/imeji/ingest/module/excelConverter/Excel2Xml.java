package de.fub.imeji.ingest.module.excelConverter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import de.fub.imeji.ingest.core.beans.metadata.terms.DCTerms;
import de.fub.imeji.ingest.core.beans.metadata.terms.IMEJITerms;
import de.fub.imeji.ingest.core.beans.metadata.terms.RDFTerms;
import de.fub.imeji.ingest.core.beans.metadata.terms.Terms;
import de.fub.imeji.ingest.core.zuse.metadata.terms.ZuseDCTerms;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * 
 * @author hnguyen
 *
 */
public class Excel2Xml {
	
	private static String workSheet = "profile";
	
	private static String outputFilename = "excel2xml.xml";
	
	public enum ExcelHeader {
		DMA_NAME(0), IMEJI_NAME_DE(1), IMEJI_NAME_EN(2), TYPE(3), MULTIPLE(4), PARENT(5);
		
		private int code;
		
		private ExcelHeader(int c) { setCode(c); }
		
		public void setCode(int c) { code = c; }
		public int getCode() { return code; }
	}

	/**
	 * 
	 * @param filename
	 */
	public Excel2Xml(String filename) {
		this(new File(filename), new File(outputFilename));
	}
	
	/**
	 * 
	 * @param filenameIn
	 * @param filenameOut
	 */
	public Excel2Xml(String filenameIn, String filenameOut) {
		this(new File(filenameIn), new File(filenameOut));
	}
	
	/**
	 * 
	 * @param fin
	 * @param fout
	 */
	public Excel2Xml(File fin, File fout) {
		export2Xml(fin, fout);
	}

	/**
	 * 
	 * @param inFile
	 * @param outFile
	 */
	private void export2Xml(File inFile, File outFile) {
		Workbook workbook = null;		
		try {
			workbook = Workbook.getWorkbook(inFile);			
			
			exportSheet2Xml(workbook.getSheet(Excel2Xml.workSheet),outFile);
			
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void exportSheet2Xml(Sheet sheet, File outFile) throws JDOMException, IOException {

		Element root = new Element("Zuse",RDFTerms.RDF_NAMESPACE);
		root.addNamespaceDeclaration(IMEJITerms.IMEJI_NAMESPACE);
		root.addNamespaceDeclaration(DCTerms.DCTERMS_NAMESPACE);
		root.addNamespaceDeclaration(DCTerms.DCTERMS_ELEMENT_NAMESPACE);
		root.addNamespaceDeclaration(ZuseDCTerms.DCTERMS_ZUSE);
		root.addNamespaceDeclaration(Terms.XSI_NAMESPACE);
		root.addNamespaceDeclaration(RDFTerms.RDFS_NAMESPACE);
		root = this.createProfile(root,sheet,
				"Konrad Zuse Internet Archive",
				"This archive offers chronological and subject-based access to the documents spanning the work of Zuse.");

		Document doc = new Document(root);				
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());		
		FileOutputStream fos = new FileOutputStream(outFile);
		serializer.output(doc, fos);
		fos.close();
	}

	private Element createProfile(Element root, Sheet sheet, String profileTitleName, String profileDescriptionName) {
		
		XmlProfile profile = new XmlProfile();
		
		profile.setTitle(profileTitleName);
		profile.setDescription(profileDescriptionName);

		
		int id_counter = 1;
		ArrayList<XmlStatement> xmlsts = new ArrayList<XmlStatement>();
		ArrayList<XmlStatement> xmlstsHasParent = new ArrayList<XmlStatement>();
		
		for (int i = 1; i < sheet.getRows(); i++) {
			
			String type = sheet.getCell(ExcelHeader.TYPE.getCode(), i).getContents();
			
			if(type.isEmpty()) continue;			
			
			XmlStatement statement = new XmlStatement(id_counter++, RDFTerms.RDF_NAMESPACE);
			
			if(type.equalsIgnoreCase("text")) {
				statement.setType("http://imeji.org/terms/metadata#text");
			}
			
			String signature_original = sheet.getCell(ExcelHeader.DMA_NAME.getCode(), i).getContents();
			String signature_de = sheet.getCell(ExcelHeader.IMEJI_NAME_DE.getCode(), i).getContents();
			String signature_en = sheet.getCell(ExcelHeader.IMEJI_NAME_EN.getCode(), i).getContents();
			
			if(signature_original.isEmpty() || signature_original.equalsIgnoreCase("-")) {
				if(signature_de.isEmpty() || signature_de.equalsIgnoreCase("-"))
					signature_original = signature_en;
				else {
					signature_original = signature_de;
				}
			}
			
			statement.setOriginLabelName(signature_original, RDFTerms.RDFS_NAMESPACE);
			
			if(!signature_de.isEmpty()) {
				statement.addLabel("de", signature_de);
			}
			
			if(!signature_en.isEmpty()) {
				statement.addLabel("en", signature_en);
			}
			
			statement.setDescription(true, RDFTerms.RDF_BOOLEAN_NAMESPACE);
			
			String muliple = sheet.getCell(ExcelHeader.MULTIPLE.getCode(), i).getContents();
			
			statement.setMinOccurs("0",RDFTerms.RDF_STRING_NAMESPACE);
			
			if(muliple.isEmpty()) {
				statement.setMaxOccurs("1",RDFTerms.RDF_STRING_NAMESPACE);
			} else {
				statement.setMaxOccurs("unbounded",RDFTerms.RDF_STRING_NAMESPACE);
			}
			
			String parent = sheet.getCell(ExcelHeader.PARENT.getCode(), i).getContents();
			
			if(!parent.isEmpty()) {
				statement.setParentName(parent);
			}
			
			xmlsts.add(statement);
		}	
		
				
		for (XmlStatement statement : xmlsts) {
			if(!statement.getParentName().isEmpty()) {
				for (XmlStatement stm : xmlsts) {
					String oriName = stm.getOriginLabelName();
					if(statement.getParentName().equalsIgnoreCase(oriName)) {
						statement.setParentNode(stm);
						break;
					}
				}
			}
		}
		
		
		
		
		profile.addContent(xmlsts);
		
		return root.addContent(profile);
	}
}
