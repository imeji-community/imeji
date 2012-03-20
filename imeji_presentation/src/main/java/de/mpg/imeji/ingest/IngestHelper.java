/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.imeji.ingest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import thewebsemantic.LocalizedString;

import de.mpg.jena.util.MetadataFactory;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.complextypes.Text;

/**
 * Class helper to realize the ingest procedures
 * @author hnguyen
 *
 */
public class IngestHelper
{
	/**
	 * XML handling object.
	 */
	private SAXBuilder builder;
	
	/**
	 * Meta data objects in an array list
	 */
	private ArrayList<XmlMDBean> mdObjs;
	
	/**
	 * Hash table of profile names.
	 */
	private Hashtable<String,Integer> profileNames;
	
	/**
	 * Standard constructor
	 */
	public IngestHelper() {
		this.builder = new SAXBuilder();
		this.mdObjs = new ArrayList<XmlMDBean>();
		this.profileNames = new Hashtable<String,Integer>();
	}
	
	
	/**
	 * Returns array list of xml meta data objects.
	 * @return the mdObjs
	 */
	public ArrayList<XmlMDBean> getMdObjs() {
		return mdObjs;
	}

	/**
	 * Returns a hashmap specified various profiles 
	 * position in array list of the meta data object.
	 * @return the profileNames
	 */
	public Hashtable<String, Integer> getProfileNames() {
		return profileNames;
	}
	
	/**
	 * This method gets the meta data from the xml file, 
	 * converted to xml meta data object (XmlMDOject).
	 * @param xmlFilename
	 * @return List of xml meta data objects 
	 */
	public ArrayList<XmlMDBean> extractXmlMDObjects(File xmlFile) {
    	try{
        	
			Document document = (Document) this.builder.build(xmlFile);			
			
			/*
			 * XML conform format for imeji
			 * <CollectionName>
			 * 	<Object-Profile1>
			 * 		<Metadata1>
			 * 			value1 ...
			 * 		</Metadata1>
			 * 		<Metadata2>
			 * 			value2 ...
			 * 		</Metadata2>
			 * 		...
			 * 	</Object-Profile1>
			 * 	<Object-Profile1>
			 * 		<Metadata1>
			 * 			value1 ...
			 * 		</Metadata1>
			 * 		<Metadata2>
			 * 			value2 ...
			 * 		</Metadata2>
			 * 		...
			 * 	</Object-Profile1>
			 *  <Object-Profile2>
			 * 		<Metadata1>
			 * 			value1 ...
			 * 		</Metadata1>
			 * 		<Metadata2>
			 * 			value2 ...
			 * 		</Metadata2>
			 * 		...
			 * 	</Object-Profile2>
			 * 	<Object-Profile2>
			 * 		<Metadata1>
			 * 			value1 ...
			 * 		</Metadata1>
			 * 		<Metadata2>
			 * 			value2 ...
			 * 		</Metadata2>
			 * 		...
			 * 	</Object-Profile2>
			 * 	...
			 * </CollectionName>
			 */
			
			// gets the collection name
			Element rootNode = document.getRootElement();
			
			// gets meta data structure
			@SuppressWarnings("unchecked")
			List<Element> list = rootNode.getChildren();	
			
			// traverses the xml tree (using algorithm deepth first search starting on one site
			for (int i=0; i< list.size(); i++) {
				// gets the meta data
				Element elem = list.get(i);
				XmlMDBean mdo = new XmlMDBean(rootNode.getName(),elem.getName());				
				if(elem.getChildren().isEmpty()) {
					mdo.add(elem.getName(),elem.getValue());
				} else {
					dfs(elem,mdo,"");
				}
				
				// add multiple profile if file contains more than one profile
				if(!this.profileNames.containsKey(elem.getName())) {
					this.profileNames.put(elem.getName(), new Integer(i));
				}
				this.mdObjs.add(mdo);
           }
 
			
    	 }catch(IOException io){
    		System.out.println(io.getMessage());
    	 }catch(JDOMException jdomex){
    		System.out.println(jdomex.getMessage());
    	}
    	 
    	 return this.mdObjs;
	}
	
	/**
	 * This method gets the meta data from the xml file stream, 
	 * converted to xml meta data object (XmlMDOject).
	 * @param xmlFilename
	 * @return List of xml meta data objects 
	 */
	public ArrayList<XmlMDBean> extractXmlMDObjects4Zuse(InputStream xmlFileStream) {
    	try{
        	
			Document document = (Document) this.builder.build(xmlFileStream);			
			
			/*
			 * XML conform format for imeji
			 * <CollectionName>
			 * 	<Object-Profile1>
			 * 		<Metadata1>
			 * 			value1 ...
			 * 		</Metadata1>
			 * 		<Metadata2>
			 * 			value2 ...
			 * 		</Metadata2>
			 * 		...
			 * 	</Object-Profile1>
			 * 	<Object-Profile1>
			 * 		<Metadata1>
			 * 			value1 ...
			 * 		</Metadata1>
			 * 		<Metadata2>
			 * 			value2 ...
			 * 		</Metadata2>
			 * 		...
			 * 	</Object-Profile1>
			 *  <Object-Profile2>
			 * 		<Metadata1>
			 * 			value1 ...
			 * 		</Metadata1>
			 * 		<Metadata2>
			 * 			value2 ...
			 * 		</Metadata2>
			 * 		...
			 * 	</Object-Profile2>
			 * 	<Object-Profile2>
			 * 		<Metadata1>
			 * 			value1 ...
			 * 		</Metadata1>
			 * 		<Metadata2>
			 * 			value2 ...
			 * 		</Metadata2>
			 * 		...
			 * 	</Object-Profile2>
			 * 	...
			 * </CollectionName>
			 */
			
			// gets the collection name
			Element rootNode = document.getRootElement();
			
			// gets meta data structure
			@SuppressWarnings("unchecked")
			List<Element> list = rootNode.getChildren();	
			
			// traverses the xml tree (using algorithm deepth first search starting on one site
			for (int i=0; i< list.size(); i++) {
				// gets the meta data
				Element elem = list.get(i);
				XmlMDBean mdo = new XmlMDBean(rootNode.getName(),elem.getName());				
				if(elem.getChildren().isEmpty()) {
					if(elem.getName().equalsIgnoreCase("Signatur")) {						
						mdo.add(elem.getName(),elem.getValue().replace("/", "_"));
					} else {
						mdo.add(elem.getName(),elem.getValue());
					}
				} else {
					dfs4Zuse(elem,mdo,"");
				}
				
				// add multiple profile if file contains more than one profile
				if(!this.profileNames.containsKey(elem.getName())) {
					this.profileNames.put(elem.getName(), new Integer(i));
				}
				this.mdObjs.add(mdo);
           }
 
			
    	 }catch(IOException io){
    		System.out.println(io.getMessage());
    	 }catch(JDOMException jdomex){
    		System.out.println(jdomex.getMessage());
    	}
    	 
    	 return this.mdObjs;
	}
	
	/**
	 * This method gets the meta data from the xml file stream, 
	 * converted to xml meta data object (XmlMDOject).
	 * @param xmlFilename
	 * @return List of xml meta data objects 
	 */
	public ArrayList<XmlMDBean> extractXmlMDObjects(InputStream xmlFileStream) {
    	try{
        	
			Document document = (Document) this.builder.build(xmlFileStream);			
			
			/*
			 * XML conform format for imeji
			 * <CollectionName>
			 * 	<Object-Profile1>
			 * 		<Metadata1>
			 * 			value1 ...
			 * 		</Metadata1>
			 * 		<Metadata2>
			 * 			value2 ...
			 * 		</Metadata2>
			 * 		...
			 * 	</Object-Profile1>
			 * 	<Object-Profile1>
			 * 		<Metadata1>
			 * 			value1 ...
			 * 		</Metadata1>
			 * 		<Metadata2>
			 * 			value2 ...
			 * 		</Metadata2>
			 * 		...
			 * 	</Object-Profile1>
			 *  <Object-Profile2>
			 * 		<Metadata1>
			 * 			value1 ...
			 * 		</Metadata1>
			 * 		<Metadata2>
			 * 			value2 ...
			 * 		</Metadata2>
			 * 		...
			 * 	</Object-Profile2>
			 * 	<Object-Profile2>
			 * 		<Metadata1>
			 * 			value1 ...
			 * 		</Metadata1>
			 * 		<Metadata2>
			 * 			value2 ...
			 * 		</Metadata2>
			 * 		...
			 * 	</Object-Profile2>
			 * 	...
			 * </CollectionName>
			 */
			
			// gets the collection name
			Element rootNode = document.getRootElement();
			
			// gets meta data structure
			@SuppressWarnings("unchecked")
			List<Element> list = rootNode.getChildren();	
			
			// traverses the xml tree (using algorithm deepth first search starting on one site
			for (int i=0; i< list.size(); i++) {
				// gets the meta data
				Element elem = list.get(i);
				XmlMDBean mdo = new XmlMDBean(rootNode.getName(),elem.getName());				
				if(elem.getChildren().isEmpty()) {
					mdo.add(elem.getName(),elem.getValue());
				} else {
					dfs(elem,mdo,"");
				}
				
				// add multiple profile if file contains more than one profile
				if(!this.profileNames.containsKey(elem.getName())) {
					this.profileNames.put(elem.getName(), new Integer(i));
				}
				this.mdObjs.add(mdo);
           }
 
			
    	 }catch(IOException io){
    		System.out.println(io.getMessage());
    	 }catch(JDOMException jdomex){
    		System.out.println(jdomex.getMessage());
    	}
    	 
    	 return this.mdObjs;
	}

	/**
	 * Using depth first search algorithm starting on the left side of the tree
	 */
	private void dfs(Element elem, XmlMDBean metadatas, String prefix) {
		@SuppressWarnings("unchecked")
		List<Element> list = (List<Element>) elem.getChildren();
		Element subelem;
		String tag, value;
		for (int i = 0; i < list.size(); i++) {
			subelem = list.get(i);
			tag = subelem.getName();			
			if(subelem.getChildren().isEmpty()) {
				value = subelem.getValue();
				if(prefix.isEmpty()) {
					// add meta data
					metadatas.add(tag,value);					
				} else {
					// adds sub meta data, %20 for space key
					metadatas.add(prefix + " - " + tag,value);
				}
			} else {
				// traverses the xml tree and adds sub meta data
				dfs(subelem,metadatas,tag);
			}
		}		
	}
	
	/**
	 * Using depth first search algorithm starting on the left side of the tree
	 */
	private void dfs4Zuse(Element elem, XmlMDBean metadatas, String prefix) {
		@SuppressWarnings("unchecked")
		List<Element> list = (List<Element>) elem.getChildren();
		Element subelem;
		String tag, value;
		for (int i = 0; i < list.size(); i++) {
			subelem = list.get(i);
			tag = subelem.getName();			
			if(subelem.getChildren().isEmpty()) {
				value = subelem.getValue();
				if(prefix.isEmpty()) {
					// add meta data
					if(tag.equalsIgnoreCase("Signatur")) {
						value = value.replace("/", "_");
					}
					metadatas.add(tag,value);					
				} else {
					// adds sub meta data, %20 for space key
					metadatas.add(prefix + " - " + tag,value);
				}
			} else {
				// traverses the xml tree and adds sub meta data
				dfs(subelem,metadatas,tag);
			}
		}		
	}
	
	/**
	 * Specific method only use for the Zuse archive use case!
	 * @param mdbs
	 * @param filename
	 * @return
	 */
	public XmlMDBean getMDBeanObject(ArrayList<XmlMDBean> mdbs, String filename) {				
		String fn = new String("DMA_NL_207_00620.jpg");
		StringBuffer fnsb = new StringBuffer();
		
		for (XmlMDBean xb : mdbs) {
			fn = "DMA_" 
				+ xb.getValueOfTag("Bestand")
				+ "_"
				+ xb.getValueOfTag("Signatur")
				+ ".jpg";
			fn = fn.replace(" ", "_");
//			System.out.println("in: "+fn+" - out:"+filename);
			if(fn.equalsIgnoreCase(filename))
				return xb;
		}		
		return null;
	}
	
	/**
	 * Maps a meta data object entry to image meta data entry
	 * @param mdb
	 * @param statements
	 * @return an image meta data entry as a list of image meta data.
	 */
	public List<ImageMetadata> mappingFromXmlMDObjectToImageMD(XmlMDBean mdb, ArrayList<Statement> statements) {
		List<ImageMetadata> cimd = new ArrayList<ImageMetadata>();
		
		for (Statement statement : statements) {
			ImageMetadata imd = MetadataFactory.newMetadata(statement);
			ArrayList<LocalizedString> labels = (ArrayList<LocalizedString>) statement.getLabels();
			String value = null;
			for (LocalizedString label : labels) {
				value = mdb.getValueOfTag(label.toString());
			}			
						
			((Text)imd).setText(value);
			cimd.add(imd);
		}
		
		return (List<ImageMetadata>)cimd;
	}
	
	/**
	 * Replace the space key value to '%20' for the right URL
	 * @param string
	 * @return
	 */
	public static String replaceSpace(String string) {		
		return string.replace(" ", "%20");
	}

}
