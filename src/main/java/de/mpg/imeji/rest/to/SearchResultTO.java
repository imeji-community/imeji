package de.mpg.imeji.rest.to;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
/**
 * A TO to wrap Search results
 * 
 * @author bastiens
 *
 */
public class SearchResultTO implements Serializable {

  private static final long serialVersionUID = -9046921435794271874L;
  private int totalNumberOfRecords;
  private int numberOfRecords;
  private int size;
  private int offset;
  private String q;
  private List<?> results = new ArrayList<>();

  /**
   * Builder for {@link SearchResultTO}
   * 
   * @author bastiens
   *
   */
  public static class Builder {
    private int totalNumberOfRecords;
    private int numberOfRecords;
    private int size;
    private int offset;
    private String q;
    private List<?> results = new ArrayList<>();

    public SearchResultTO build() {
      return new SearchResultTO(this);
    }

    public Builder totalNumberOfRecords(int i) {
      this.totalNumberOfRecords = i;
      return this;
    }

    public Builder numberOfRecords(int i) {
      this.numberOfRecords = i;
      return this;
    }

    public Builder size(int i) {
      this.size = i;
      return this;
    }

    public Builder offset(int i) {
      this.offset = i;
      return this;
    }

    public Builder query(String s) {
      this.q = s;
      return this;
    }

    public Builder results(List<?> l) {
      this.results = l;
      return this;
    }

  }

  public SearchResultTO(Builder builder) {
    this.q = builder.q;
    this.numberOfRecords = builder.numberOfRecords;
    this.offset = builder.offset;
    this.results = builder.results;
    this.size = builder.size;
    this.totalNumberOfRecords = builder.totalNumberOfRecords;
  }

  public int getTotalNumberOfRecords() {
    return totalNumberOfRecords;
  }

  public void setTotalNumberOfRecords(int totalNumberOfRecords) {
    this.totalNumberOfRecords = totalNumberOfRecords;
  }

  /**
   * @return the results
   */
  public List<?> getResults() {
    return results;
  }

  /**
   * @param results the results to set
   */
  public void setResults(List<?> results) {
    this.results = results;
  }

  /**
   * @return the size
   */
  public int getSize() {
    return size;
  }

  /**
   * @param size the size to set
   */
  public void setSize(int size) {
    this.size = size;
  }

  /**
   * @return the offset
   */
  public int getOffset() {
    return offset;
  }

  /**
   * @param offset the offset to set
   */
  public void setOffset(int offset) {
    this.offset = offset;
  }

  /**
   * @return the q
   */
  public String getQ() {
    return q;
  }

  /**
   * @param q the q to set
   */
  public void setQ(String q) {
    this.q = q;
  }

  /**
   * @return the numberOfRecords
   */
  public int getNumberOfRecords() {
    return numberOfRecords;
  }

  /**
   * @param numberOfRecords the numberOfRecords to set
   */
  public void setNumberOfRecords(int numberOfRecords) {
    this.numberOfRecords = numberOfRecords;
  }


}
