/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.hp.hpl.jena.ontology.Profile;

import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jList;
import de.mpg.j2j.annotations.j2jResource;

/**
 * Container for a {@link List} of {@link Metadata} defined for one {@link Profile}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@j2jResource("http://imeji.org/terms/metadataSet")
@j2jId(getMethod = "getId", setMethod = "setId")
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "metadataSet", namespace = "http://imeji.org/terms")
public class MetadataSet
{
    @j2jList("http://imeji.org/terms/metadata")
    private Collection<Metadata> metadata = new ArrayList<Metadata>();
    @j2jResource("http://imeji.org/terms/mdprofile")
    private URI profile;
    private URI id;

    public MetadataSet()
    {
    }

    @XmlElement(name = "metadata", namespace = "http://imeji.org/terms")
    public Collection<Metadata> getMetadata()
    {
        sortMetadata();
        return metadata;
    }

    public void setMetadata(Collection<Metadata> metadata)
    {
         this.metadata = metadata;
    }

    @XmlElement(name = "profile", namespace = "http://imeji.org/terms")
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

    @XmlAttribute(name = "id")
    public URI getId()
    {
        return id;
    }

    /**
     * sort the {@link Metadata} according to their position
     */
    public void sortMetadata()
    {
        Collections.sort((List<Metadata>)metadata);
    }

    public Object getValueFromMethod(String methodName)
    {
        Method method;
        Object ret = null;
        try
        {
            method = this.getClass().getMethod(methodName);
            ret = method.invoke(this);
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
}
