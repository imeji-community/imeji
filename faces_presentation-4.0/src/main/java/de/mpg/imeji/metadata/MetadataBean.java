package de.mpg.imeji.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONCollection;

import thewebsemantic.LocalizedString;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.vo.ComplexType;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Organization;
import de.mpg.jena.vo.Person;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.complextypes.ConePerson;

public class MetadataBean
{
    private ImageMetadata metadata;
    private String selectedStatementName;
    private String prettyLink;

    // private List<MdField> fields;
    // private MdField field;
    public class MdField
    {
        private String name;
        private String label;
        private String value;
        private ImageMetadata parent;
        private List<String> literalOptions;

        public MdField(String name, String value, ImageMetadata parent)
        {
            this.name = name;
            this.value = value;
            this.parent = parent;
            label = parent.getType().getLabel() + " - " + name;
            literalOptions = new ArrayList<String>();
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getValue()
        {
            return value;
        }

        public void setValue(String value)
        {
            this.value = value;
        }

        public ImageMetadata getParent()
        {
            return parent;
        }

        public void setParent(ImageMetadata parent)
        {
            this.parent = parent;
        }

        public String getLabel()
        {
            return label;
        }

        public void setLabel(String label)
        {
            this.label = label;
        }

        public List<String> getLiteralOptions()
        {
            return literalOptions;
        }

        public void setLiteralOptions(List<String> literalOptions)
        {
            this.literalOptions = literalOptions;
        }
    }

    public List<Object> autoComplete(Object suggest)
    {
        Statement statement = null;
        for (Statement s : profile.getStatements())
            if (s.getName().equals(getSelectedStatementName()))
                statement = s;
        if (statement.getLiteralConstraints() != null && statement.getLiteralConstraints().size() > 0)
        {
            List<Object> suggestions = new ArrayList<Object>();
            List<String> literals = new ArrayList<String>();
            for (LocalizedString str : statement.getLiteralConstraints())
                literals.add(str.toString());
            for (String str : literals)
                if (str.contains(suggest.toString()))
                    suggestions.add(str);
            return suggestions;
        }
        else if (statement.getVocabulary() != null)
        {
            if (!suggest.toString().isEmpty())
            {
                try
                {
                    HttpClient client = new HttpClient();
                    GetMethod getMethod = new GetMethod(statement.getVocabulary().toString()
                            + "?format=json&n=10&m=full&q=" + suggest);
                    client.executeMethod(getMethod);
                    JSONCollection jsc = new JSONCollection(getMethod.getResponseBodyAsString());
                    return Arrays.asList(jsc.toArray());
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    public String getPrettyLink()
    {
        return prettyLink;
    }

    public void setPrettyLink(String prettyLink)
    {
        this.prettyLink = prettyLink;
    }

    private MetadataProfile profile;

    public MetadataBean(MetadataProfile profile, Statement s)
    {
        this.profile = profile;
        changeStatement(s);
        /*
         * fields = getFields(metadata); if (fields.size() > 0) field = fields.get(0);
         */
    }

    public MetadataBean(MetadataProfile profile, Statement s, ImageMetadata metadata)
    {
        this.profile = profile;
        changeStatement(s);
        this.metadata = metadata;
    }

    public String changeType()
    {
        for (Statement s : profile.getStatements())
        {
            if (s.getName().equals(getSelectedStatementName()))
            {
                changeStatement(s);
                break;
            }
        }
        System.err.println(prettyLink);
        return prettyLink;
    }

    private void changeStatement(Statement s)
    {
        ComplexType ct = ImejiFactory.newComplexType(s.getType());
        if (s.getLabels() != null && s.getLabels().size() > 0)
        {
            ct.setLabel(s.getLabels().iterator().next().toString());
        }
        if (ct.getEnumType().equals(ComplexType.ComplexTypes.CONE_AUTHOR))
        {
            Person p = new Person();
            Organization o = new Organization();
            p.getOrganizations().add(o);
            ((ConePerson)ct).setPerson(p);
        }
        this.metadata = new ImageMetadata(s.getName(), ct);
        this.selectedStatementName = s.getName();
    }

    public void setMetadata(ImageMetadata metadata)
    {
        this.metadata = metadata;
    }

    public ImageMetadata getMetadata()
    {
        return metadata;
    }

    public void setSelectedStatementName(String selectedStatementName)
    {
        this.selectedStatementName = selectedStatementName;
    }

    public String getSelectedStatementName()
    {
        return selectedStatementName;
    }
    /*
     * public List<MdField> getFields(ImageMetadata md) { List<Field> l; try { l =
     * ComplexTypeHelper.getComplexTypeFields(md.getType().getEnumType().getClassType(), false); } catch (Exception e) {
     * throw new RuntimeException(e); } List<MdField> fs = new ArrayList<MdField>(); for (Field f : l) fs.add(new
     * MdField(f.getName(), "", md)); return fs; } public void selectedFieldListener(ValueChangeEvent event) { if (event
     * != null && event.getNewValue() != event.getOldValue()) { for (MdField mdf : fields) { if
     * (mdf.getLabel().equals(event.getNewValue().toString())) field = mdf; } } } public ImageMetadata getMetadata() {
     * return metadata; } public void setMetadata(ImageMetadata metadata) { this.metadata = metadata; } public
     * List<MdField> getMdFields() { return fields; } public void setMdFields(List<MdField> fields) { this.fields =
     * fields; } public MdField getField() { return field; } public void setField(MdField field) { this.field = field; }
     */
}
