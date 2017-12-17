package deviceManagement.util;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class UtilDateDeserializer implements JsonDeserializer<java.util.Date> {



	@Override
	public Date deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) 
			throws JsonParseException {
		
		return new  java.util.Date(arg0.getAsJsonPrimitive().getAsLong());
	}

}