//package de.mpg.imeji.util;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.xmlbeans.impl.xb.xsdschema.ComplexType;
//import org.apache.xmlbeans.impl.xb.xsdschema.Element;
//import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
//
//
//public class VocabularyHelper
//{
////    public static List<StatementVO> getEtermsVocabulary()
////    {
////        SchemaDocument eTermsSchema = readSchema("http://metadata.mpdl.mpg.de/escidoc/metadata/schemas/0.1/escidoctypes.xsd");
////        return parseVocabulary(eTermsSchema, "eTerms");
////    }
////
////    public static List<StatementVO> getDcTermsVocabulary()
////    {
////        SchemaDocument eTermsSchema = readSchema("http://metadata.mpdl.mpg.de/escidoc/metadata/schemas/0.1/dcterms.xsd");
////        return parseVocabulary(eTermsSchema, "dcTerms");
////    }
//
//    /**
//     * Read and Parse a schema from its location.
//     * 
//     * @param path
//     * @return
//     */
//    public static SchemaDocument readSchema(String path)
//    {
//        SchemaDocument schema;
//        String schemaXml = "";
//        try
//        {
//            URL url = new URL(path);
//            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
//            String inputLine;
//            while ((inputLine = in.readLine()) != null)
//            {
//                schemaXml += inputLine;
//            }
//            in.close();
//            System.out.println(schemaXml);
//            schema = SchemaDocument.Factory.parse(schemaXml);
//        }
//        catch (Exception e)
//        {
//            throw new RuntimeException("Error parsing" + path + ": " + e);
//        }
//        return schema;
//    }
//
//    /**
//     * Parse a schema and returns the list of simple metadata defined <br>
//     * ComplexType, Group, reference, not supported so far!!!!
//     * 
//     * @param schema
//     * @return
//     */
//    public static List<StatementVO> parseVocabulary(SchemaDocument schema, String vocabularyName)
//    {
//        List<StatementVO> list = new ArrayList<StatementVO>();
//        List<String> complexTypes = new ArrayList<String>();
//        for (ComplexType ct : schema.getSchema().getComplexTypeArray())
//        {
//            complexTypes.add(ct.getName());
//        }
//        for (Element el : schema.getSchema().getElementArray())
//        {
//            if (el.getType() == null || !complexTypes.contains(el.getType().getLocalPart()))
//            {
//                String namespace = schema.getSchema().getTargetNamespace();
//                if (el.getType() != null && el.getType().getNamespaceURI() != null)
//                {
//                    namespace = el.getType().getNamespaceURI();
//                }
//                if (el.getSubstitutionGroup() != null)
//                {
//                    namespace = el.getSubstitutionGroup().getNamespaceURI();
//                }
//                StatementVO stVO = new StatementVO();
//                stVO.setName(el.getName());
//                stVO.setNamespace(namespace);
//                stVO.setLabel(vocabularyName + ":" + el.getName());
//                list.add(stVO);
//            }
//        }
//        return list;
//    }
//}
