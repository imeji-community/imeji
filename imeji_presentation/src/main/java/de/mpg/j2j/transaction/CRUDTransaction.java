package de.mpg.j2j.transaction;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;

import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.j2j.controler.ResourceController;
import de.mpg.j2j.helper.J2JHelper;

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
        //logger.info("Start: " + this.getId() + " " + getLockType() + " " + J2JHelper.getId(o));
        switch (type)
        {
            case CREATE:
                rc.create(o);
                break;
            case READ:
                o = rc.read(o);
                break;
            case UPDATE:
                rc.update(o);
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
