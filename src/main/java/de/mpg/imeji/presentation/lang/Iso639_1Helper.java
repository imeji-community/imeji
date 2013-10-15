/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.lang;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.mpg.imeji.presentation.util.PropertyReader;
import de.mpg.imeji.presentation.util.ProxyHelper;

/**
 * Utility class for Iso638_1 languages vocabulary
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class Iso639_1Helper
{
    private static Logger logger = Logger.getLogger(Iso639_1Helper.class);
    private List<SelectItem> list = null;

    /**
     * Default constructor
     */
    public Iso639_1Helper()
    {
        list = new ArrayList<SelectItem>();
        parseVocabularyString(getVocabularyString());
    }

    /**
     * Get the Iso638_1 languages vocabulary from CoNe with the options format
     * 
     * @return
     */
    private String getVocabularyString()
    {
        try
        {
            System.out.println("get VocabularyString");
           
            HttpClient client = new HttpClient();
            GetMethod getMethod = new GetMethod(PropertyReader.getProperty("escidoc.cone.isos639_1.all")
                    + "?format=options");
            //client.executeMethod(getMethod);
            ProxyHelper.executeMethod(client, getMethod);
            return getMethod.getResponseBodyAsString();
        }
        catch (Exception e)
        {
            logger.error("Couldn't read ISO639_1 vocabulary, will use default one! Error: " + e);
            return null;
        }
    }

    /**
     * Parse the Result as defined in Cone
     * 
     * @param v
     */
    private void parseVocabularyString(String v)
    {
        try
        {
            for (String l : v.split("\n"))
            {
                String[] s = l.split("\\|");
                list.add(new SelectItem(s[0], s[1]));
            }
        }
        catch (Exception e)
        {
            list = new ArrayList<SelectItem>();
            list.add(new SelectItem("en", "en - English"));
            list.add(new SelectItem("de", "de - German"));
        }
    }

    /**
     * getter
     * 
     * @return
     */
    public List<SelectItem> getList()
    {
        return list;
    }

    /**
     * setter
     * 
     * @param list
     */
    public void setList(List<SelectItem> list)
    {
        this.list = list;
    }
}
