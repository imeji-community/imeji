package de.mpg.imeji.metadata;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import de.mpg.jena.util.ComplexTypeHelper;
import de.mpg.jena.vo.ComplexType;
import de.mpg.jena.vo.ImageMetadata;

public class MetadataBean
{
    private ImageMetadata metadata;
    private List<MdField> fields;
    private MdField field;

    public class MdField
    {
        private String name;
        private String label;
        private String value;
        private ImageMetadata parent;

        public MdField(String name, String value, ImageMetadata parent)
        {
            this.name = name;
            this.value = value;
            this.parent = parent;
            label = parent.getType().getLabel() + " - " + name;
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
        
    }

    public MetadataBean(ImageMetadata metadata)
    {
        this.metadata = metadata;
        fields = getFields(metadata);
        if (fields.size() > 0)
            field = fields.get(0);
    }

    public List<MdField> getFields(ImageMetadata md)
    {
        List<Field> l;
        try
        {
            l = ComplexTypeHelper.getComplexTypeFields(md.getType().getEnumType().getClassType(), false);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        List<MdField> fs = new ArrayList<MdField>();
        for (Field f : l)
        {
            fs.add(new MdField(f.getName(), "", md));
        }
        return fs;
    }

    public void selectedFieldListener(ValueChangeEvent event)
    {
        if (event != null && event.getNewValue() != event.getOldValue())
        {
            for (MdField mdf : fields)
            {
                if (mdf.getLabel().equals(event.getNewValue().toString()))
                    field = mdf;
            }
        }
    }

    public ImageMetadata getMetadata()
    {
        return metadata;
    }

    public void setMetadata(ImageMetadata metadata)
    {
        this.metadata = metadata;
    }

    public List<MdField> getMdFields()
    {
        return fields;
    }

    public void setMdFields(List<MdField> fields)
    {
        this.fields = fields;
    }

    public MdField getField()
    {
        return field;
    }

    public void setField(MdField field)
    {
        this.field = field;
    }
}
