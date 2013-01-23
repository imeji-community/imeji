/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.metadata.util;

import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
//import org.richfaces.json.JSONCollection;

import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;

public class SuggestBean
{
    private Map<URI, Suggest> suggests = null;

    public void init(MetadataProfile profile)
    {
        suggests = new HashMap<URI, Suggest>();
        for (Statement s : profile.getStatements())
        {
            suggests.put(s.getId(), new Suggest(s));
        }
    }

    public Map<URI, Suggest> getSuggests()
    {
        return suggests;
    }

    public void setSuggests(Map<URI, Suggest> suggests)
    {
        this.suggests = suggests;
    }

    public class Suggest
    {
        private Statement statement = null;

        public Suggest(Statement s)
        {
            statement = s;
        }

        public List<SelectItem> getRestrictedValues()
        {
            if (statement.getLiteralConstraints() != null && statement.getLiteralConstraints().size() > 0)
            {
                List<SelectItem> list = new ArrayList<SelectItem>();
                list.add(new SelectItem(null, "-"));
                for (String str : statement.getLiteralConstraints())
                {
                    list.add(new SelectItem(str, str));
                }
                return list;
            }
            return null;
        }

        public List<Object> autoComplete(Object suggest)
        {
//            if (statement.getVocabulary() != null)
//            {
//                if (suggest.toString().isEmpty())
//                {
//                    suggest = "a";
//                }
//                else if (!suggest.toString().isEmpty())
//                {
//                    try
//                    {
//                        HttpClient client = new HttpClient();
//                        GetMethod getMethod = new GetMethod(statement.getVocabulary().toString()
//                                + URLEncoder.encode(suggest.toString(), "UTF-8"));
//                        client.executeMethod(getMethod);
//                        String responseString = getMethod.getResponseBodyAsString().trim();
//                        JSONCollection jsc = new JSONCollection(formatResultString(responseString));
//                        return Arrays.asList(jsc.toArray());
//                    }
//                    catch (Exception e)
//                    {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
            return null;
        }

        private String formatResultString(String s)
        {
            if (s.contains("\"formatted_address\" :"))
            {
                //is https://developers.google.com/maps/documentation/geocoding
                try
                {
                    s = parseMapsGoogleApiGeo(s);
                }
                catch (Exception e)
                {
                    throw new RuntimeException("Error parsing google geo api results", e);
                }
            }
            if (s.contains("\"status\" : \"ZERO_RESULTS\""))
            {
                // google zero result
                s = "";
            }
            if (!s.startsWith("["))
            {
                s = "[ " + s;
            }
            if (!s.endsWith("]"))
            {
                s = s + "]";
            }
            return s;
        }

        private String parseMapsGoogleApiGeo(String str) throws Exception
        {
            String response = "";
            String address = "";
            String latitude = "";
            String longitude = "";
            StringReader reader = new StringReader(str);
            int c = 0;
            boolean readingAddress = false;
            boolean readingLongitude = false;
            boolean readingLatitude = false;
            boolean readingLocation = false;
            String buffer = "";
            while ((c = reader.read()) != -1)
            {
                buffer += (char)c;
                if (buffer.contains("\"formatted_address\" : \""))
                {
                    // start reading address
                    readingAddress = true;
                    buffer = "";
                }
                else if (readingAddress && c == '"')
                {
                    // stop reading address
                    readingAddress = false;
                    buffer = "";
                }
                else if (buffer.contains("\"location\" : "))
                {
                    // Enter in Location group
                    readingLocation = true;
                    buffer = "";
                }
                else if (readingLocation && c == '}')
                {
                    // Sort out Location group
                    readingLocation = false;
                    buffer = "";
                }
                else if (readingLocation && buffer.contains("\"lat\" : "))
                {
                    // start reading latitude
                    readingLatitude = true;
                    buffer = "";
                }
                else if (readingLatitude && c == ',')
                {
                    // stop reading latitude
                    readingLatitude = false;
                    buffer = "";
                }
                else if (readingLocation && buffer.contains("\"lng\" : "))
                {
                    // start reading longitude
                    readingLongitude = true;
                    buffer = "";
                }
                else if (readingLongitude && c == '\n')
                {
                    // stop reading longitude
                    readingLongitude = false;
                    buffer = "";
                }
                else if (readingAddress)
                {
                    address += (char)c;
                }
                else if (readingLongitude)
                {
                    longitude += (char)c;
                }
                else if (readingLatitude)
                {
                    latitude += (char)c;
                }
                else if (address != "" && latitude != "" && longitude != "" && !readingAddress && !readingLocation)
                {
                    // write results
                    response += "{\"address\" : \"" + address + "\" , \"longitude\" : \"" + longitude
                            + "\" , \"latitude\" : \"" + latitude + "\"}, ";
                    address = "";
                    latitude = "";
                    longitude = "";
                }
            }
            return "[" + response + "]";
        }

        public boolean getHasRestrictedValues()
        {
            if (statement != null)
            {
                if (statement.getLiteralConstraints() != null && statement.getLiteralConstraints().size() > 0)
                {
                    return true;
                }
            }
            return false;
        }

        public boolean getDoAutoComplete()
        {
            if (statement != null)
            {
                if (statement.getLiteralConstraints() != null && statement.getLiteralConstraints().size() > 0)
                {
                    return false;
                }
                if (statement.getVocabulary() != null)
                {
                    return true;
                }
            }
            return false;
        }
    }
}
