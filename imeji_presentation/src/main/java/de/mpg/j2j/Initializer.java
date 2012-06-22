package de.mpg.j2j;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;

/**
 * Initialize Jena
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class Initializer
{
    public static void init()
    {
        Dataset imejiDataset = TDBFactory.createDataset(Imeji.TDB_PATH);
        Dataset itemDataset = TDBFactory.createDataset(Imeji.TDB_PATH);
        imejiDataset.addNamedModel(Imeji.ITEM_MODEL_URI, itemDataset.getDefaultModel());
        Imeji.DATASET = imejiDataset;
    }

    public static void initModel(String name)
    {
    }

    public static void reset(Model model)
    {
        model.removeAll();
    }

    public static void reset()
    {
        Imeji.DATASET.getNamedModel(Imeji.ITEM_MODEL_NAME).removeAll();
    }
}
