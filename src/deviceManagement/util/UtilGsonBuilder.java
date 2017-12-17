package deviceManagement.util;

import java.text.DateFormat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UtilGsonBuilder {
    public static Gson create(){
		GsonBuilder gb=new GsonBuilder();
		gb.registerTypeAdapter(java.util.Date.class , new  UtilDateSerializer()).setDateFormat(DateFormat.LONG);
		gb.registerTypeAdapter(java.util.Date.class , new  UtilDateDeserializer()).setDateFormat(DateFormat.LONG);
		Gson gson=gb.create();
		return gson;
	}
}