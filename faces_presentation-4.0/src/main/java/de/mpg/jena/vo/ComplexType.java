package de.mpg.jena.vo;

import java.lang.annotation.Annotation;

import thewebsemantic.Namespace;
import thewebsemantic.RdfType;
import de.mpg.jena.vo.complextypes.ConePerson;
import de.mpg.jena.vo.complextypes.Date;
import de.mpg.jena.vo.complextypes.Geolocation;
import de.mpg.jena.vo.complextypes.License;
import de.mpg.jena.vo.complextypes.Number;
import de.mpg.jena.vo.complextypes.Publication;
import de.mpg.jena.vo.complextypes.Text;
import de.mpg.jena.vo.complextypes.URI;

public abstract class ComplexType
{
    @Namespace("http://imeji.mpdl.mpg.de/")
    @RdfType("complexTypes")
    public enum ComplexTypes
    {
        CONE_AUTHOR(ConePerson.class, "Person"), TEXT(Text.class, "Text"), NUMBER(Number.class, "Number"), DATE(
                Date.class, "Date"), LICENCE(License.class, "Licence"), GEOLOCATION(Geolocation.class, "Geolocation"), URI(
                URI.class, "URI"), PUBLICATION(Publication.class, "PubMan Publication");
        private Class<? extends ComplexType> type;
        private String label;

        private ComplexTypes(Class<? extends ComplexType> type, String label)
        {
            this.type = type;
            this.label = label;
        }

        public Class<? extends ComplexType> getClassType()
        {
            return type;
        }

        public String getLabel()
        {
            return label;
        }

        /**
         * Read namespace annotation of java class of this type
         * 
         * @return
         */
        public String getNamespace()
        {
            Annotation namespaceAnn = this.getClassType().getAnnotation(thewebsemantic.Namespace.class);
            return namespaceAnn.toString().split("@thewebsemantic.Namespace\\(value=")[1].split("\\)")[0];
        }

        /**
         * Read rdfType annotation of java class of this type
         * 
         * @return
         */
        public String getRdfType()
        {
            Annotation rdfTypeAnn = this.getClassType().getAnnotation(thewebsemantic.RdfType.class);
            return rdfTypeAnn.toString().split("@thewebsemantic.RdfType\\(value=")[1].split("\\)")[0];
        }
    }

    private ComplexTypes type;
    private String label;

    public ComplexType(ComplexTypes type)
    {
        this.type = type;
        label = type.getLabel();
    }

    public ComplexTypes getEnumType()
    {
        return type;
    }

    public void setEnumType(ComplexTypes type)
    {
        this.type = type;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }
}
