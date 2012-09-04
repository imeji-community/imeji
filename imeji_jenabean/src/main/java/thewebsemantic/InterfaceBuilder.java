package thewebsemantic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

public class InterfaceBuilder {
	OntModel m;
	String pckg = "beans";
	private static String indent = "   ";
	private static String setter = "\n   public void set{2}({0} {1}) '{'\n		this.{1} = {1};\n   '}'";
	private static String getter = "\n   public {0} get{2}() '{'\n		return this.{1};\n   '}'";
	
	public void create(String uri, String namespace, String type) throws IOException {
	
		m = ModelFactory.createOntologyModel();
		m.read(uri);
		m.read("http://files.cimtool.org/CPSM2007-CIMTool-Profile.owl");
		File dir = new File(pckg);
		if (!dir.exists())
			dir.mkdir();
		
		ExtendedIterator<OntClass> it = m.listNamedClasses();
		List<OntClass> clsses = it.toList();
		it.close();
		for (OntClass cls : clsses) {
			if (cls.isProperty())
				continue;
			
			HashMap<OntProperty, Integer> restrictions = new HashMap<OntProperty, Integer>();			
			List<OntClass> superclss = cls.listSuperClasses(true).toList();
			String superclsname=null;
			if (superclss.size()>0)
				superclsname = superclss.iterator().next().getLabel(null);
			if (superclsname==null)
				superclsname = "Object";
			for (OntClass supercls : superclss) {
				if (supercls.isRestriction() && supercls.asRestriction().isMaxCardinalityRestriction()) {
					int i = supercls.asRestriction().asMaxCardinalityRestriction().getMaxCardinality();
					try {
						if (i==1)
							restrictions.put(supercls.asRestriction().asMaxCardinalityRestriction().getOnProperty(),1);
						System.out.print("restriction " + i);
					} catch (Exception e) {}
				} 
			}

			StringBuilder buffer = new StringBuilder();
			StringBuilder setgetBuffer = new StringBuilder();
			if (cls.getLabel(null) == null)
				continue;
			//System.out.println(cls.getLabel(null));
			File f = new File(dir, cls.getLabel(null) + ".java");

			StmtIterator props = m.listStatements(null, RDFS.domain, cls);
			List<Statement> propList = props.toList();
			props.close();
			buffer.append("package " + pckg + ";\n\nimport thewebsemantic.*;\nimport java.util.*;\n\n");
			buffer.append("@Namespace(\"" + namespace  + "\")\n");
			buffer.append("@RdfType(\"" + cls.getLocalName()  + "\")\n");
			buffer.append("public class " + cls.getLabel(null) + " extends " + superclsname + " {\n");
			buffer.append("   @Id\n");
			buffer.append("   private java.net.URI id;\n");
			for(Statement stmt : propList) {
				
				//System.out.println(".");
				OntProperty prop = stmt.getSubject().as(OntProperty.class);
					
				if (prop.getLabel(null) == null || prop.getRange() == null)
					continue;
				String fieldName = prop.getLabel(null);
				fieldName.replace('.', '_');

				OntResource range = prop.getRange();
				String typeName = null;
				if (prop.isDatatypeProperty()) {System.out.println(range.getURI());
				    Class clz =TypeMapper.getInstance().getSafeTypeByName(range.getURI()).getJavaClass();
				    if (clz == null)
				    	typeName = "Long";
				    else
				    	typeName = clz.getName();
				} else {
					typeName = range.getLocalName();
				}
				if (prop.isFunctionalProperty() || prop.isInverseFunctionalProperty() || restrictions.containsKey(prop)) {
										
				} else {
					typeName = "Collection<" + typeName + ">";
				}
				buffer.append("   @RdfProperty(\"" + prop.getURI() + "\")\n");
				buffer.append("   private " + typeName + " " + fieldName + ";\n");

				setgetBuffer.append(
					MessageFormat.format(setter, new Object[] {typeName, fieldName, Util.toProperCase(fieldName) }  )
				);
				setgetBuffer.append(
					MessageFormat.format(getter, new Object[] {typeName, fieldName, Util.toProperCase(fieldName) }  )
				);			
			}

			setgetBuffer.append(
				MessageFormat.format(setter, new Object[] {"java.net.URI", "id", "Id" }  )
			);
			setgetBuffer.append(
				MessageFormat.format(getter, new Object[] {"java.net.URI", "id", "Id" }  )
			);
				
			FileWriter writer = new FileWriter(f);
			writer.write(buffer.toString());
			writer.write(setgetBuffer.toString());
			writer.write("}\n");
			writer.close();
		}		
	}


	public static void main(String[] args) throws IOException {
		new InterfaceBuilder().create("file:cim2003.owl",
				"http://iec.ch/TC57/2003/CIM-schema-cim10#", "CIM");
	}
}
