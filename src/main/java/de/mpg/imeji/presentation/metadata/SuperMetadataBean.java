package de.mpg.imeji.presentation.metadata;

import java.net.URI;

import de.mpg.imeji.logic.util.DateFormatter;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.MetadataSet;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.metadata.util.MetadataHelper;

/**
 * Bean for all Metadata types. This bean should have all variable that have been defined in all metadata types.
 * 
 * @author saquet
 */
public class SuperMetadataBean
{
    /**
     * The {@link Metadata} defined within thie {@link SuperMetadataBean}
     */
    private Metadata metadata;
    /**
     * The position of the {@link Metadata} in the {@link MetadataSet}
     */
    private int pos = 0;
    /**
     * The parent {@link SuperMetadataBean} (i.e {@link Metadata}), according to what is defined in the
     * {@link MetadataProfile}
     */
    private SuperMetadataBean parent = null;
    /**
     * Define how many parents this {@link Metadata} has until the highest parent
     */
    private int hierarchyLevel = 0;
    /**
     * The {@link Statement} of this {@link Metadata}
     */
    private Statement statement;
    /**
     * True if the {@link Metadata} has no value defined
     */
    private boolean empty = false;
    private boolean preview = true;
    // All possible fields defined for a metadata:
    private String text;
    private Person person;
    private URI coneId;
    private URI uri;
    private String label;
    private String date;
    private long time;
    private double longitude = Double.NaN;
    private double latitude = Double.NaN;
    private String name;
    private String exportFormat;
    private String citation;
    private double number = Double.NaN;
    private String license = null;
    private URI externalUri;

    /**
     * Bean for all Metadata types. This bean should have all variable that have been defined in all metadata types.
     * 
     * @param metadata
     */
    public SuperMetadataBean(Metadata metadata, Statement statement)
    {
        this.metadata = metadata;
        this.empty = MetadataHelper.isEmpty(metadata);
        this.statement = statement;
        ObjectHelper.copyAllFields(metadata, this);
    }

    /**
     * Get {@link SuperMetadataBean} as {@link Metadata}
     * 
     * @return
     */
    public Metadata asMetadata()
    {
        ObjectHelper.copyAllFields(this, metadata);
        MetadataHelper.setConeID(metadata);
        return metadata;
    }

    // /**
    // * getter for the {@link Statement} defining this {@link Metadata}
    // *
    // * @return
    // */
    // public URI getStatement()
    // {
    // return metadata.getStatement();
    // }
    /**
     * Retun the id (last part of the {@link URI}) of the {@link Statement}. Used for GUI representation
     * 
     * @return
     */
    public String getStatementId()
    {
        return ObjectHelper.getId(getStatement().getId());
    }

    /**
     * getter for the namespace defining the type of the {@link Metadata}
     * 
     * @return
     */
    public String getTypeNamespace()
    {
        return metadata.getTypeNamespace();
    }

    /**
     * getter
     * 
     * @return
     */
    public String getText()
    {
        return text;
    }

    /**
     * setter
     * 
     * @param text
     */
    public void setText(String text)
    {
        this.text = text;
    }

    /**
     * getter
     * 
     * @return
     */
    public Person getPerson()
    {
        return person;
    }

    /**
     * setter
     * 
     * @param person
     */
    public void setPerson(Person person)
    {
        this.person = person;
    }

    /**
     * getter
     * 
     * @return
     */
    public URI getConeId()
    {
        return coneId;
    }

    /**
     * setter
     * 
     * @param coneId
     */
    public void setConeId(URI coneId)
    {
        this.coneId = coneId;
    }

    /**
     * getter
     * 
     * @return
     */
    public URI getUri()
    {
        return uri;
    }

    /**
     * setter
     * 
     * @param uri
     */
    public void setUri(URI uri)
    {
        this.uri = uri;
    }

    /**
     * getter
     * 
     * @return
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * setter
     * 
     * @param label
     */
    public void setLabel(String label)
    {
        this.label = label;
    }

    /**
     * getter
     * 
     * @return
     */
    public String getDate()
    {
        return date;
    }

    /**
     * setter
     * 
     * @param date
     */
    public void setDate(String date)
    {
        if (date != null && !"".equals(date))
        {
            time = DateFormatter.getTime(date);
            this.date = date;
        }
        this.date = date;
    }

    /**
     * getter
     * 
     * @return
     */
    public double getLongitude()
    {
        return longitude;
    }

    /**
     * setter
     * 
     * @param longitude
     */
    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    /**
     * getter
     * 
     * @return
     */
    public double getLatitude()
    {
        return latitude;
    }

    /**
     * setter
     * 
     * @param latitude
     */
    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    /**
     * getter
     * 
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * setter
     * 
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * getter
     * 
     * @return
     */
    public String getExportFormat()
    {
        return exportFormat;
    }

    /**
     * setter
     * 
     * @param exportFormat
     */
    public void setExportFormat(String exportFormat)
    {
        this.exportFormat = exportFormat;
    }

    /**
     * getter
     * 
     * @return
     */
    public String getCitation()
    {
        return citation;
    }

    /**
     * setter
     * 
     * @param citation
     */
    public void setCitation(String citation)
    {
        this.citation = citation;
    }

    /**
     * getter
     * 
     * @return
     */
    public double getNumber()
    {
        return number;
    }

    /**
     * setter
     * 
     * @param number
     */
    public void setNumber(double number)
    {
        this.number = number;
    }

    /**
     * getter
     * 
     * @return
     */
    public String getLicense()
    {
        return license;
    }

    /**
     * setter
     * 
     * @param license
     */
    public void setLicense(String license)
    {
        this.license = license;
    }

    /**
     * getter
     * 
     * @return
     */
    public int getPos()
    {
        return pos;
    }

    /**
     * setter
     * 
     * @param pos
     */
    public void setPos(int pos)
    {
        this.pos = pos;
    }

    /**
     * @return the externalUri
     */
    public URI getExternalUri()
    {
        return externalUri;
    }

    /**
     * @param externalUri the externalUri to set
     */
    public void setExternalUri(URI externalUri)
    {
        this.externalUri = externalUri;
    }

    /**
     * setter
     * 
     * @param parent the parent to set
     */
    public void setParent(SuperMetadataBean parent)
    {
        this.parent = parent;
        if (parent != null)
            this.hierarchyLevel = parent.getHierarchyLevel() + 1;
    }

    /**
     * getter
     * 
     * @return the parent
     */
    public SuperMetadataBean getParent()
    {
        return parent;
    }

    /**
     * getter
     * 
     * @return the hierarchyLevel
     */
    public int getHierarchyLevel()
    {
        return hierarchyLevel;
    }

    /**
     * setter
     * 
     * @param hierarchyLevel the hierarchyLevel to set
     */
    public void setHierarchyLevel(int hierarchyLevel)
    {
        this.hierarchyLevel = hierarchyLevel;
    }

    /**
     * getter
     * 
     * @return the empty
     */
    public boolean isEmpty()
    {
        return empty;
    }

    /**
     * getter
     * 
     * @return the preview
     */
    public boolean isPreview()
    {
        return preview;
    }

    /**
     * setter
     * 
     * @param preview the preview to set
     */
    public void setPreview(boolean preview)
    {
        this.preview = preview;
    }

    /**
     * getter
     * 
     * @return the statement
     */
    public Statement getStatement()
    {
        return statement;
    }

    /**
     * setter
     * 
     * @param statement the statement to set
     */
    public void setStatement(Statement statement)
    {
        this.statement = statement;
    }
}
