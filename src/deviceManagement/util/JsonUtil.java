package deviceManagement.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import deviceManagement.bean.Result;




public class JsonUtil {
//	static JsonUtil
//	public static void getInstance(){
//		return Json
//	}
	private static final ObjectMapper mapper = new ObjectMapper();
//	 Class<T> mClazz;
//	  public JsonUtil<T> targetClass(Class<T> clazz) {
//	        mClazz = clazz;
//	        return this;
//	    }
	  
    
	public static void writeJsonToClient(Result<?> result , HttpServletResponse response){
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			mapper.writeValue(pw, result);
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(pw!=null){
				pw.close();
			}
		}
	}

}
