package de.mpg.escidoc.faces.metastore.vocabulary;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;

public class ESCIDOC
{
    protected static final String uri = "http://escidoc.de/core/01/properties/";

    public static String getURI()
    {
        return uri;
    }

    public static final Property locatorurl = ResourceFactory.createProperty(uri, "locator-url");
    public static final Property name = ResourceFactory.createProperty(uri, "name");
    public static final Property description = ResourceFactory.createProperty(uri, "description");
    public static final Property loginname = ResourceFactory.createProperty(uri, "login-name");
    public static final Property email = ResourceFactory.createProperty(uri, "email");
    public static final Property creationdate = ResourceFactory.createProperty(uri, "creation-date");
    public static final Property publicstatus = ResourceFactory.createProperty(uri, "public-status");
    public static final Property publicstatuscomment = ResourceFactory.createProperty(uri, "public-status-comment");
    public static final Property active = ResourceFactory.createProperty(uri, "active");
    public static final Property visibility = ResourceFactory.createProperty(uri, "visibility");
    public static final Property validstatus = ResourceFactory.createProperty(uri, "valid-status");
    public static final Property type = ResourceFactory.createProperty(uri, "type");
    public static final Property haschildren = ResourceFactory.createProperty(uri, "has-children");
    public static final Property lockdate = ResourceFactory.createProperty(uri, "lock-date");
    public static final Property lockstatus = ResourceFactory.createProperty(uri, "lock-status");
    public static final Property contentcategory = ResourceFactory.createProperty(uri, "content-category");
    public static final Property filename = ResourceFactory.createProperty(uri, "file-name");
    public static final Property filesize = ResourceFactory.createProperty(uri, "file-size");
    public static final Property mimetype = ResourceFactory.createProperty(uri, "mime-type");
    public static final Property checksum = ResourceFactory.createProperty(uri, "checksum");
    public static final Property checksumalgorithm = ResourceFactory.createProperty(uri, "checksum-algorithm");
    public static final Property pid = ResourceFactory.createProperty(uri, "pid");
    public static final Property contentmodelspecific = ResourceFactory.createProperty(uri, "content-model-specific");
    public static final Property version = ResourceFactory.createProperty(uri, "version");
    public static final Property latestversion = ResourceFactory.createProperty(uri, "latest-version");
    public static final Property latestrelease = ResourceFactory.createProperty(uri, "latest-release");
    public static final Property contexttitle = ResourceFactory.createProperty(uri, "context-title");
    public static final Property contentmodeltitle = ResourceFactory.createProperty(uri, "conten-tmodel-title");
    public static final Property createdbytitle = ResourceFactory.createProperty(uri, "created-by-title");
    public static final Property modifiedbytitle = ResourceFactory.createProperty(uri, "modified-by-title");
    public static final Property grantremark = ResourceFactory.createProperty(uri, "grant-remark");
    public static final Property revocationremark = ResourceFactory.createProperty(uri, "revocation-remark");
    public static final Property revocationdate = ResourceFactory.createProperty(uri, "revocation-date");
    public static final Property externalids = ResourceFactory.createProperty(uri, "external-ids");
    public static final Property organizationalunits = ResourceFactory.createProperty(uri, "organizational-units");
    public static final Property affiliations = ResourceFactory.createProperty(uri, "affiliations");

    public static final class Version
    {
        protected static final String uri = "http://escidoc.de/core/01/properties/version/";

        public static String getURI()
        {
            return uri;
        }
        private static Model m = ModelFactory.createDefaultModel();

        public static final Property number = m.createProperty(uri, "number");
        public static final Property date = m.createProperty(uri, "date");
        public static final Property status = m.createProperty(uri, "status");
        public static final Property modifiedby = ESCIDOC.Relation.modifiedby;
        public static final Property comment = m.createProperty(uri, "comment");
        public static final Property pid = m.createProperty(uri, "pid");
    }

    public static final class Release
    {
        protected static final String uri = "http://escidoc.de/core/01/properties/release/";

        public static String getURI()
        {
            return uri;
        }

        public static final Property number = ResourceFactory.createProperty(uri, "number");
        public static final Property date = ResourceFactory.createProperty(uri, "date");
        public static final Property pid = ResourceFactory.createProperty(uri, "pid");
    }

    public static final class Relation
    {
        protected static final String uri = "http://escidoc.de/core/01/structural-relations/";

        public static String getURI()
        {
            return uri;
        }

        public static final Property createdby = ResourceFactory.createProperty(uri, "created-by");
        public static final Property modifiedby = ResourceFactory.createProperty(uri, "modified-by");
        public static final Property revokedby = ResourceFactory.createProperty(uri, "revoked-by");
        public static final Property grantedto = ResourceFactory.createProperty(uri, "granted-to");
        public static final Property context = ResourceFactory.createProperty(uri, "context");
        public static final Property contentmodel = ResourceFactory.createProperty(uri, "content-model");
        public static final Property lockowner = ResourceFactory.createProperty(uri, "lock-owner");
        public static final Property organizationalunit = ResourceFactory.createProperty(uri, "organizational-unit");
        public static final Property affiliation = ResourceFactory.createProperty(uri, "affiliation");
        public static final Property person = ResourceFactory.createProperty(uri, "person");
        public static final Property role = ResourceFactory.createProperty(uri, "role");
        public static final Property assignedon = ResourceFactory.createProperty(uri, "assigned-on");
        public static final Property parent = ResourceFactory.createProperty(uri, "parent");
        public static final Property child = ResourceFactory.createProperty(uri, "child");
        public static final Property predecessor = ResourceFactory.createProperty(uri, "predecessor");
        public static final Property member = ResourceFactory.createProperty(uri, "member");
        public static final Property item = ResourceFactory.createProperty(uri, "item");
        public static final Property container = ResourceFactory.createProperty(uri, "container");
        public static final Property component = ResourceFactory.createProperty(uri, "component");
    }
}
