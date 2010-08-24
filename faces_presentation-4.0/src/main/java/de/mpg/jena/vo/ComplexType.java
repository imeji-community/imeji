package de.mpg.jena.vo;

import java.lang.annotation.Annotation;
import java.net.URI;

import org.apache.tools.ant.types.selectors.ExtendSelector;

import de.mpg.jena.vo.complextypes.ConePerson;
import de.mpg.jena.vo.complextypes.Date;
import de.mpg.jena.vo.complextypes.Geolocation;
import de.mpg.jena.vo.complextypes.License;
import de.mpg.jena.vo.complextypes.Number;
import de.mpg.jena.vo.complextypes.Text;

public abstract class ComplexType
{
    public enum AllowedTypes
    {
        CONE_AUTHOR(ConePerson.class, "Cone Author"), TEXT(Text.class, "Text"), NUMBER(Number.class, "Number"), DATE(
                Date.class, "Date"), LICENCE(License.class, "Licence"), GEOLOCATION(Geolocation.class,
                "Geolocation");
        private Class<? extends ComplexType> type;
        private String label;

        private AllowedTypes(Class<? extends ComplexType> type, String label)
        {
            this.type = type;
            this.label = label;
        }

        public Class<? extends ComplexType> getType()
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
            Annotation namespaceAnn = this.getType().getAnnotation(thewebsemantic.Namespace.class);
            return namespaceAnn.toString().split("@thewebsemantic.Namespace\\(value=")[1].split("\\)")[0];
        }

        /**
         * Read rdfType annotation of java class of this type
         * 
         * @return
         */
        public String getRdfType()
        {
            Annotation rdfTypeAnn = this.getType().getAnnotation(thewebsemantic.RdfType.class);
            return rdfTypeAnn.toString().split("@thewebsemantic.RdfType\\(value=")[1].split("\\)")[0];
        }
    }

    private AllowedTypes type;
    private String label;

    public ComplexType(AllowedTypes type)
    {
        this.type = type;
        label = type.getLabel();
    }

    public AllowedTypes getType()
    {
        return type;
    }

    public void setType(AllowedTypes type)
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
