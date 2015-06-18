package de.mpg.imeji.rest.process;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EasyEditHelper {
	public static void update(String key) throws JsonParseException, JsonMappingException, IOException
	{  
		JsonFactory factory = new JsonFactory(); 
	    ObjectMapper mapper = new ObjectMapper(factory); 
	    File from = new File("c:\\test.json"); 
	    TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};

	    HashMap<String,Object> o = mapper.readValue(from, typeRef); 
		
	}

}
