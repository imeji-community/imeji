/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.util;

import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.predefinedMetadata.ConePerson;
import de.mpg.imeji.logic.vo.predefinedMetadata.Date;
import de.mpg.imeji.logic.vo.predefinedMetadata.Geolocation;
import de.mpg.imeji.logic.vo.predefinedMetadata.License;
import de.mpg.imeji.logic.vo.predefinedMetadata.Link;
import de.mpg.imeji.logic.vo.predefinedMetadata.Number;
import de.mpg.imeji.logic.vo.predefinedMetadata.Publication;
import de.mpg.imeji.logic.vo.predefinedMetadata.Text;
import de.mpg.imeji.logic.vo.predefinedMetadata.util.MetadataTypesHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.j2j.annotations.j2jDataType;

public class MetadataFactory
{
    public static Metadata createMetadata(String typeNamespace)
    {
        Metadata md = null;
        if ("http://imeji.org/terms/metadata#text".equals(typeNamespace))
        {
            md = new Text();
        }
        else if ("http://imeji.org/terms/metadata#number".equals(typeNamespace))
        {
            md = new Number();
        }
        else if ("http://imeji.org/terms/metadata#publication".equals(typeNamespace))
        {
            md = new Publication();
        }
        else if ("http://imeji.org/terms/metadata#conePerson".equals(typeNamespace))
        {
            md = new ConePerson();
            ((ConePerson)md).setPerson(ImejiFactory.newPerson());
        }
        else if ("http://imeji.org/terms/metadata#date".equals(typeNamespace))
        {
            md = new Date();
        }
        else if ("http://imeji.org/terms/metadata#geolocation".equals(typeNamespace))
        {
            md = new Geolocation();
        }
        else if ("http://imeji.org/terms/metadata#link".equals(typeNamespace))
        {
            md = new Link();
        }
        else if ("http://imeji.org/terms/metadata#license".equals(typeNamespace))
        {
            md = new License();
        }
        else
        {
            throw new RuntimeException("MetadataFactory: Error creating new Metadata. Unknown type: " + typeNamespace);
        }
        return md;
    }

    public static Metadata createMetadata(Metadata.Types type)
    {
        return createMetadata(type.getClazz().getAnnotation(j2jDataType.class).value());
    }

    public static Metadata createMetadata(Statement s)
    {
        Metadata md = createMetadata(MetadataTypesHelper.getTypesForNamespace(s.getType().toString()));
        md.setStatement(s.getId());
        return md;
    }

    //
    /**
     * Copy metadata to another, and perform some transformation (add search values, format dates, etc.)
     * 
     * @param metadata
     * @return
     */
    public static Metadata copyMetadata(Metadata metadata)
    {
        Metadata md = createMetadata(metadata.getTypeNamespace());
        md.copy(metadata);
        return md;
    }
}
