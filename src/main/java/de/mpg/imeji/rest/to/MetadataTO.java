package de.mpg.imeji.rest.to;

import de.mpg.imeji.rest.to.predefinedMetadataTO.*;
import de.mpg.j2j.helper.J2JHelper;

import java.net.URI;


public class MetadataTO {


    public enum Types {
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

}
