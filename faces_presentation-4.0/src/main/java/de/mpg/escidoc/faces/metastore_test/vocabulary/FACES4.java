package de.mpg.escidoc.faces.metastore_test.vocabulary;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;

public class FACES4
{
    protected static final String uri = "http://purl.org/escidoc/metadata/terms/0.1/";

    public static String getURI()
    {
        return uri;
    }

    private static Model m = ModelFactory.createDefaultModel();
    public static final Property emotion = m.createProperty(uri, "emotion");
    public static final Property picture_group = m.createProperty(uri, "picture-group");
    public static final Property age = m.createProperty(uri, "age");
    public static final Property age_group = m.createProperty(uri, "age-group");
    public static final Property gender = m.createProperty(uri, "gender");
    public static final Property complete_name = m.createProperty(uri, "complete-name");
    public static final Property family_name = m.createProperty(uri, "family-name");
    public static final Property given_name = m.createProperty(uri, "given-name");
    public static final Property alternative_name = m.createProperty(uri, "alternative-name");
    public static final Property person_title = m.createProperty(uri, "person-title");
    public static final Property pseudonym = m.createProperty(uri, "pseudonym");
    public static final Property address = m.createProperty(uri, "address");


    public static final class Emotion
    {
        protected static final String uri = "http://purl.org/escidoc/metadata/ves/emotions/";

        public static String getURI()
        {
            return uri;
        }
        public static final Resource happiness = ResourceFactory.createProperty(uri, "happiness");
        public static final Resource anger = ResourceFactory.createProperty(uri, "anger");
        public static final Resource neutrality = ResourceFactory.createProperty(uri, "neutrality");
        public static final Resource sadness = ResourceFactory.createProperty(uri, "sadness");
        public static final Resource fear = ResourceFactory.createProperty(uri, "fear");
        public static final Resource disgust = ResourceFactory.createProperty(uri, "disgust");
    }
    
    public static final class PictureGroup
    {
        protected static final String uri = "http://purl.org/escidoc/metadata/ves/picture-group/";

        public static String getURI()
        {
            return uri;
        }
        public static final Resource a = ResourceFactory.createProperty(uri, "a");
        public static final Resource b = ResourceFactory.createProperty(uri, "b");
    }
    
    public static final class AgeGroup
    {
        protected static final String uri = "http://purl.org/escidoc/metadata/ves/age-group/";

        public static String getURI()
        {
            return uri;
        }
        public static final Resource young = ResourceFactory.createProperty(uri, "young");
        public static final Resource middle = ResourceFactory.createProperty(uri, "middle");
        public static final Resource old = ResourceFactory.createProperty(uri, "old");
    }
    
    public static final class Gender
    {
        protected static final String uri = "http://purl.org/escidoc/metadata/ves/gender/";

        public static String getURI()
        {
            return uri;
        }
        public static final Resource female = ResourceFactory.createProperty(uri, "female");
        public static final Resource male = ResourceFactory.createProperty(uri, "male");
    }
}
