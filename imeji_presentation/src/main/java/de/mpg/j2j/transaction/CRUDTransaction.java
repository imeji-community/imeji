package de.mpg.j2j.transaction;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;

import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.j2j.controler.ResourceController;

public class CRUDTransaction extends Transaction
{
    private List<Object> objects = new ArrayList<Object>();
    private OperationsType type;
    private boolean lazy = false;

    public CRUDTransaction(List<Object> objects, OperationsType type, String modelURI, boolean lazy)
    {
        super(modelURI);
        this.objects = objects;
        this.type = type;
        this.lazy = lazy;
    }

    protected void execute(Dataset ds) throws Exception
    {
        ResourceController rc = new ResourceController(getModel(ds), lazy);
        for (Object o : objects)
        {
            invokeResourceController(rc, o);
        }
    }

    private void invokeResourceController(ResourceController rc, Object o) throws Exception
    {
        switch (type)
        {
            case CREATE:
                logger.info("Create " + this.getId());
                rc.create(o);
                logger.info("Created " + this.getId());
                break;
            case READ:
                o = rc.read(o);
                break;
            case UPDATE:
                logger.info("Update " + this.getId());
                rc.update(o);
                logger.info("Updated " + this.getId());
                break;
            case DELETE:
                rc.delete(o);
                break;
        }
    }

    @Override
    protected ReadWrite getLockType()
    {
        switch (type)
        {
            case READ:
                return ReadWrite.READ;
            default:
                return ReadWrite.WRITE;
        }
    }
}
