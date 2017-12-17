package deviceManagement.util;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class UtilDateSerializer implements JsonSerializer<Date> {


	@Override
	public JsonElement serialize(Date arg0, Type arg1, JsonSerializationContext arg2) {
		// TODO Auto-generated method stub
		return new JsonPrimitive(arg0.getTime());
	}
}