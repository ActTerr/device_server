package deviceManagement.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {
	public static String getValue(String key,String proName){
		String value = null;
		Properties p = new Properties();
//		String path = PropertiesUtils.class.getResource("/").getPath();
		try {
			InputStream in = PropertiesUtils.class.getClassLoader().getResourceAsStream("config/"+proName) ;
			p.load(in
//					new FileInputStream(new File(path+"/"+proName))
					);
			value = p.getProperty(key);
			return value;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}
}
