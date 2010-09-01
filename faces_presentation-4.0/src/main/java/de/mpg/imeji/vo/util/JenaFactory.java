//package de.mpg.imeji.vo.util;
//
//import java.math.BigInteger;
//
//import org.dublincore.xml.dcDsp.x2008.x01.x14.DescriptionSetTemplateDocument;
//import org.dublincore.xml.dcDsp.x2008.x01.x14.LiteralConstraintType;
//import org.dublincore.xml.dcDsp.x2008.x01.x14.DescriptionSetTemplateDocument.DescriptionSetTemplate;
//import org.dublincore.xml.dcDsp.x2008.x01.x14.DescriptionSetTemplateDocument.DescriptionSetTemplate.DescriptionTemplate;
//import org.dublincore.xml.dcDsp.x2008.x01.x14.DescriptionSetTemplateDocument.DescriptionSetTemplate.DescriptionTemplate.StatementTemplate;
//
//import thewebsemantic.custom_datatypes.XmlLiteral;
//import de.mpg.imeji.vo.MdProfileVO;
//import de.mpg.imeji.vo.MdsContainerVO;
//import de.mpg.imeji.vo.StatementVO;
//import de.mpg.jena.vo.ContainerMetadata;
//
//public class JenaFactory
//{    
//    public static XmlLiteral newMdProfile(MdProfileVO vo)
//    {
//        DescriptionSetTemplateDocument dspDoc = DescriptionSetTemplateDocument.Factory.newInstance();
//        DescriptionSetTemplate dsp = dspDoc.addNewDescriptionSetTemplate();
//        DescriptionTemplate dt = dsp.addNewDescriptionTemplate();
//        dt.setID(vo.getName());
//        dt.addNewResourceClass()
//                .setStringValue("http://metadata.mpdl.mpg.de/escidoc/metadata/profiles/" + vo.getName());
//        for (StatementVO stVO : vo.getStatements())
//        {
//            StatementTemplate st = dt.addNewStatementTemplate();
//            st.setID(stVO.getName());
//            if (stVO.isRequired())
//            {
//                st.setMinOccurs(BigInteger.ONE);
//            }
//            else
//            {
//                st.setMinOccurs(BigInteger.ZERO);
//            }
//            if (stVO.isMultiple())
//            {
//                st.setMaxOccurs(BigInteger.valueOf(1000));
//            }
//            else
//            {
//                st.setMaxOccurs(BigInteger.ONE);
//            }
//            if (stVO.getVocabulary() != null)
//            {
//                st.addNewNonLiteralConstraint().addNewVocabularyEncodingSchemeURI()
//                        .setStringValue(stVO.getVocabulary());
//            }
//            if (stVO.getConstraints() != null && stVO.getConstraints().size() > 0)
//            {
//                LiteralConstraintType lc = st.addNewLiteralConstraint();
//                for (String constraint : stVO.getConstraints())
//                {
//                    lc.addNewLiteralOption().setStringValue(constraint);
//                }
//            }
//        }
//        return new XmlLiteral(dspDoc.xmlText());
//    }
//}
