package de.mpg.escidoc.faces.metastore;

import java.util.HashMap;

public class QueryFor
{
    private static String queryString;
    
    public static String faceItemRDFAll(String[] ids)
    {
        queryString = "CONSTRUCT { ?s ?p ?o } ";
        queryString += "{ { ?s ?p ?o } UNION ";
        queryString += "{ GRAPH ?g { ?s ?p ?o } } ";
        if (ids.length > 1)
        {
            queryString += "FILTER (";
            for (int f = 0; f < ids.length; f++)
            {
                if (f < (ids.length - 1))
                {
                    queryString += " regex(str(?s), \"" + ids[f] + "\") ||";
                }
                else
                {
                    queryString += " regex(str(?s), \"" + ids[f] + "\") ";
                }
            }
            queryString += ") }";
        }
        else
        {
            queryString += "FILTER regex(str(?s), \"" + ids[0] + "\") }";
        }
        return queryString;
    }
    
    public static String faceItemRDFProperties(String[] ids)
    {
        queryString = "CONSTRUCT { ?s ?p ?o } ";
        queryString += "{ ?s ?p ?o . ";
        if (ids.length > 1)
        {
            queryString += "FILTER (";
            for (int f = 0; f < ids.length; f++)
            {
                if (f < (ids.length - 1))
                {
                    queryString += " regex(str(?s), \"" + ids[f] + "\") ||";
                }
                else
                {
                    queryString += " regex(str(?s), \"" + ids[f] + "\") ";
                }
            }
            queryString += ") }";
        }
        else
        {
            queryString += "FILTER regex(str(?s), \"" + ids[0] + "\") }";
        }
        return queryString;
    }
    
    public static String faceItemRDFMetadata(String[] ids)
    {
        queryString = "CONSTRUCT { ?s ?p ?o } ";
        queryString += "{ GRAPH ?g { ?s ?p ?o } ";
        if (ids.length > 1)
        {
            queryString += "FILTER (";
            for (int f = 0; f < ids.length; f++)
            {
                if (f < (ids.length - 1))
                {
                    queryString += " regex(str(?s), \"" + ids[f] + "\") ||";
                }
                else
                {
                    queryString += " regex(str(?s), \"" + ids[f] + "\") ";
                }
            }
            queryString += ") }";
        }
        else
        {
            queryString += "FILTER regex(str(?s), \"" + ids[0] + "\") }";
        }
        return queryString;
    }
    
    public static String faceItemValuesAll(String id)
    {
        queryString = "SELECT ?p ?o ";
        queryString += "{ { ?s ?p ?o } UNION ";
        queryString += "{ GRAPH ?g { ?s ?p ?o } } ";
        queryString += "FILTER regex(str(?s), \"" + id + "\") }";
        return queryString;
    }
    
    public static String faceItemValuesProperties(String id)
    {
        queryString = "SELECT ?p ?o ";
        queryString += "{ ?s ?p ?o . ";
        queryString += "FILTER regex(str(?s), \"" + id + "\") }";
        return queryString;
    }
    
    public static String faceItemValuesMetadata(String id)
    {
        queryString = "SELECT ?p ?o ";
        queryString += "{ GRAPH ?g { ?s ?p ?o } ";
        queryString += "FILTER regex(str(?s), \"" + id + "\") }";
        return queryString;
    }
}
