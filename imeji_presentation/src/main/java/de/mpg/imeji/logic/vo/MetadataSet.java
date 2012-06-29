/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;

import de.mpg.imeji.logic.vo.predefinedMetadata.ConePerson;
import de.mpg.imeji.logic.vo.predefinedMetadata.Date;
import de.mpg.imeji.logic.vo.predefinedMetadata.Geolocation;
import de.mpg.imeji.logic.vo.predefinedMetadata.License;
import de.mpg.imeji.logic.vo.predefinedMetadata.Link;
import de.mpg.imeji.logic.vo.predefinedMetadata.Publication;
import de.mpg.imeji.logic.vo.predefinedMetadata.Text;
import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jList;
import de.mpg.j2j.annotations.j2jResource;

@j2jResource("http://imeji.org/terms/metadataSet")
@j2jId(getMethod = "getId", setMethod = "setId")
public class MetadataSet
{
    @j2jList("http://imeji.org/terms/metadata")
    private Collection<Metadata> metadata = new LinkedList<Metadata>();
    @j2jResource("http://imeji.org/terms/mdprofile")
    private URI profile;
    private URI id;
//    @j2jList("http://imeji.org/terms/metadata#text")
//    private Collection<Text> texts = null;
//    @j2jList("http://imeji.org/terms/metadata#conePerson")
//    private Collection<ConePerson> conePersons = null;
//    @j2jList("http://imeji.org/terms/metadata#date")
//    private Collection<Date> dates = null;
//    @j2jList("http://imeji.org/terms/metadata#geolocation")
//    private Collection<Geolocation> geolocations = null;
//    @j2jList("http://imeji.org/terms/metadata#license")
//    private Collection<License> licenses = null;
//    @j2jList("http://imeji.org/terms/metadata#numbers")
//    private Collection<de.mpg.imeji.logic.vo.predefinedMetadata.Number> numbers = null;
//    @j2jList("http://imeji.org/terms/metadata#publication")
//    private Collection<Publication> publications = null;
//    @j2jList("http://imeji.org/terms/metadata#link")
//    private Collection<Link> links = null;

    public MetadataSet()
    {
        initLists();
    }

    public void initLists()
    {
//        texts = new LinkedList<Text>();
//        conePersons = new LinkedList<ConePerson>();
//        dates = new LinkedList<Date>();
//        geolocations = new LinkedList<Geolocation>();
//        licenses = new LinkedList<License>();
//        numbers = new LinkedList<de.mpg.imeji.logic.vo.predefinedMetadata.Number>();
//        publications = new LinkedList<Publication>();
//        links = new LinkedList<Link>();
    }

    private void setAllMetadata()
    {
//        metadata.addAll(texts);
//        metadata.addAll(conePersons);
//        metadata.addAll(dates);
//        metadata.addAll(geolocations);
//        metadata.addAll(licenses);
//        metadata.addAll(numbers);
//        metadata.addAll(publications);
//        metadata.addAll(links);
        initLists();
    }

    public Collection<Metadata> getMetadata()
    {
        setAllMetadata();
        return metadata;
    }

    public void setMetadata(Collection<Metadata> metadata)
    {
        this.metadata = metadata;
    }

    public URI getProfile()
    {
        return profile;
    }

    public void setProfile(URI profile)
    {
        this.profile = profile;
    }

    public void setId(URI id)
    {
        this.id = id;
    }

    public URI getId()
    {
        return id;
    }
}
