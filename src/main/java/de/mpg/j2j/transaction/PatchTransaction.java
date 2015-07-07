package de.mpg.j2j.transaction;

import java.util.List;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.ImejiTriple;
import de.mpg.j2j.persistence.Java2Jena;

/**
 * Transaction to make patch method
 * 
 * @author saquet
 *
 */
public class PatchTransaction extends Transaction {
  private List<ImejiTriple> triples;

  public PatchTransaction(List<ImejiTriple> triples, String modelURI) {
    super(modelURI);
    this.triples = triples;
  }

  @Override
  protected void execute(Dataset ds) throws ImejiException {
    Java2Jena writer = new Java2Jena(getModel(ds), false);
    for (ImejiTriple t : triples) {
      writer.update(t.getUri(), t.getProperty(), t.getValue());
    }
  }

  @Override
  protected ReadWrite getLockType() {
    return ReadWrite.WRITE;
  }

}
