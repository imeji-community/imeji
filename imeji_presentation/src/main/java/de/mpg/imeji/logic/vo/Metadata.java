/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo;

import java.net.URI;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import de.mpg.imeji.logic.vo.predefinedMetadata.ConePerson;
import de.mpg.imeji.logic.vo.predefinedMetadata.Date;
import de.mpg.imeji.logic.vo.predefinedMetadata.Geolocation;
import de.mpg.imeji.logic.vo.predefinedMetadata.License;
import de.mpg.imeji.logic.vo.predefinedMetadata.Link;
import de.mpg.imeji.logic.vo.predefinedMetadata.Number;
import de.mpg.imeji.logic.vo.predefinedMetadata.Publication;
import de.mpg.imeji.logic.vo.predefinedMetadata.Text;
import de.mpg.j2j.annotations.j2jDataType;
import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jLiteral;
import de.mpg.j2j.annotations.j2jResource;

@j2jResource("http://imeji.org/terms/metadata")
@j2jId(getMethod = "getId", setMethod = "setId")
@XmlType(name = "metadata")
public abstract class Metadata
{
    private URI id = URI.create("http://imeji.org/terms/metadata/" + UUID.randomUUID());
    private int pos = 0;

    public enum Types
    {
        TEXT(Text.class), NUMBER(Number.class), CONE_PERSON(ConePerson.class), DATE(Date.class), GEOLOCATION(
                Geolocation.class), LICENSE(License.class), LINK(Link.class), PUBLICATION(Publication.class);
        private Class<? extends Metadata> clazz = null;

        private Types(Class<? extends Metadata> clazz)
        {
            this.clazz = clazz;
        }

        public Class<? extends Metadata> getClazz()
        {
            return clazz;
        }

        public String getClazzNamespace()
        {
            return clazz.getAnnotation(j2jDataType.class).value();
        }
    }

    public Metadata()
    {
    }

    public String getTypeNamespace()
    {
        return this.getClass().getAnnotation(j2jDataType.class).value();
    }

    public int compareTo(Metadata imd)
    {
        if (imd.getPos() > this.pos)
            return -1;
        else if (imd.getPos() == this.pos)
            return 0;
        else
            return 1;
    }

    public abstract void copy(Metadata metadata);

    public abstract URI getStatement();

    public abstract void setStatement(URI namespace);

    public abstract String asFulltext();

    protected void copyMetadata(Metadata metadata)
    {
        this.id = metadata.getId();
    }

    public URI getId()
    {
        return id;
    }

    public void setId(URI id)
    {
        this.id = id;
    }

    public int getPos()
    {
        return pos;
    }

    public void setPos(int pos)
    {
        this.pos = pos;
    }
}
