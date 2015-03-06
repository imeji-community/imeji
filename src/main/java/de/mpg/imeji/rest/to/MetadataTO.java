package de.mpg.imeji.rest.to;

import java.io.Serializable;
import java.net.URI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.imeji.rest.to.predefinedMetadataTO.ConePersonTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.DateTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.GeolocationTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.LicenseTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.LinkTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.NumberTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.PublicationTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.TextTO;
import de.mpg.j2j.annotations.j2jDataType;
import de.mpg.j2j.helper.J2JHelper;
@JsonInclude(Include.NON_NULL)
public abstract class MetadataTO implements Serializable {
	private static final long serialVersionUID = -6164935834371913175L;

    public static enum Types {
        TEXT(TextTO.class), NUMBER(NumberTO.class), CONE_PERSON(ConePersonTO.class), DATE(
                DateTO.class), GEOLOCATION(GeolocationTO.class), LICENSE(
                LicenseTO.class), LINK(LinkTO.class), PUBLICATION(PublicationTO.class);
        private Class<? extends MetadataTO> clazz = null;

        private Types(Class<? extends MetadataTO> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends MetadataTO> getClazz() {
            return clazz;
        }


        public static Class<MetadataTO> getClassOfType(URI typeUri) throws IllegalAccessException, InstantiationException {
            if (typeUri == null)
                return null;
            String type = typeUri.toString();
            for (Types typezz: Types.values()) {
                Class clazz = typezz.getClazz();
                if (type.equals(J2JHelper.getType(clazz.newInstance()))) {
                    return clazz;
                }
            }
            return null;
        }

    }
    
    public static URI getTypeURI(Class<? extends MetadataTO> clazz) {
        return URI.create(clazz.getAnnotation(j2jDataType.class).value());
    }


}
