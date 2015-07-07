package de.mpg.imeji.rest.to;

import static de.mpg.imeji.logic.util.StringHelper.isNullOrEmptyTrim;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@XmlRootElement
@XmlType(propOrder = {"id", "createdBy", "modifiedBy", "createdDate", "modifiedDate",
    "versionDate", "status", "visibility", "version", "discardComment", "collectionId", "filename",
    "mimetype", "checksumMd5", "webResolutionUrlUrl", "thumbnailUrl", "fileUrl", "metadata"})
@JsonInclude(Include.NON_NULL)
public class ItemTO extends PropertiesTO implements Serializable {

  private static final long serialVersionUID = 8408059450327059926L;

  private String visibility;

  private String collectionId;

  private String filename;


  private String mimetype;

  private String checksumMd5;

  private URI webResolutionUrlUrl;

  private URI thumbnailUrl;

  private URI fileUrl;

  protected List<MetadataSetTO> metadata = new ArrayList<MetadataSetTO>();

  private long fileSize;

  public static enum SYNTAX {
    DEFAULT, RAW;
    public static SYNTAX guessType(String type) {
      // if null return DefaultItemTO
      if (isNullOrEmptyTrim(type) || type.trim().equalsIgnoreCase(DEFAULT.toString()))
        return DEFAULT;
      else if (type.trim().equalsIgnoreCase(RAW.toString()))
        return RAW;
      else
        return null;
    }
  };


  public long getFileSize() {
    return fileSize;
  }


  public void setFileSize(long fileSize) {
    this.fileSize = fileSize;
  }


  public String getVisibility() {
    return visibility;
  }


  public void setVisibility(String visibility) {
    this.visibility = visibility;
  }


  public String getCollectionId() {
    return collectionId;
  }


  public void setCollectionId(String collectionId) {
    this.collectionId = collectionId;
  }


  public String getFilename() {
    return filename;
  }


  public void setFilename(String filename) {
    this.filename = filename;
  }


  public String getMimetype() {
    return mimetype;
  }


  public void setMimetype(String mimetype) {
    this.mimetype = mimetype;
  }



  public String getChecksumMd5() {
    return checksumMd5;
  }


  public void setChecksumMd5(String checksumMd5) {
    this.checksumMd5 = checksumMd5;
  }


  public URI getWebResolutionUrlUrl() {
    return webResolutionUrlUrl;
  }


  public void setWebResolutionUrlUrl(URI webResolutionUrlUrl) {
    this.webResolutionUrlUrl = webResolutionUrlUrl;
  }


  public URI getThumbnailUrl() {
    return thumbnailUrl;
  }


  public void setThumbnailUrl(URI thumbnailUrl) {
    this.thumbnailUrl = thumbnailUrl;
  }


  public URI getFileUrl() {
    return fileUrl;
  }


  public void setFileUrl(URI fileUrl) {
    this.fileUrl = fileUrl;
  }


  public List<MetadataSetTO> getMetadata() {
    return metadata;
  }

  public List<MetadataSetTO> filterMetadataByTypeURI(final URI type) {
    return Lists.newArrayList(Collections2.filter(Lists.newArrayList(this.metadata),
        new Predicate<MetadataSetTO>() {
          @Override
          public boolean apply(MetadataSetTO md) {
            return type.equals(md.getTypeUri());
          }
        }));
  }

  public MetadataSetTO findMetadata(final URI statement, final URI type) {
    return Iterables.find(this.metadata, new Predicate<MetadataSetTO>() {
      @Override
      public boolean apply(MetadataSetTO md) {
        return md.getTypeUri().equals(type) && md.getStatementUri().equals(statement);
      }
    }, null);
  }

  public void setMetadata(List<MetadataSetTO> metadata) {
    this.metadata = metadata;
  }


}
