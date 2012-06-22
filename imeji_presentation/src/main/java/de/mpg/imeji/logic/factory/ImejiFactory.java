package de.mpg.imeji.logic.factory;

import de.mpg.imeji.logic.controller.ImejiController;
import de.mpg.imeji.presentation.util.PropertyReader;

public class ImejiFactory
{
    protected static String getUrl()
    {
        try
        {
            return PropertyReader.getProperty("escidoc.imeji.instance.url");
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error reading escidoc.imeji.instance.url , check your properties!", e);
        }
    }

    protected static int getCounter()
    {
        return ImejiController.getUniqueId();
    }
}
