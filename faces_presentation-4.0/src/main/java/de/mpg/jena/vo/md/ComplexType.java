package de.mpg.jena.vo.md;

public abstract class ComplexType
{
    public enum AllowedTypes
    {
        CONE_AUTHOR(ConePerson.class, "Cone Author"), TEXT(Text.class, "Text"), NUMBER(Number.class, "Number"), DATE(
                Date.class, "Date"), LICENCE(License.class, "Licence"), GEOLOCALIZATION(Geolocalization.class,
                "GeoLocalization");
        private Class type;
        private String label;

        private AllowedTypes(Class type, String label)
        {
            this.type = type;
            this.label = label;
        }

        public Class getType()
        {
            return type;
        }

        public String getLabel()
        {
            return label;
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
