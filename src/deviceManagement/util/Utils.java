package deviceManagement.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;



public class Utils {
	public static boolean int2boolean(int i) {
		if(i>0){
			return true;
		}
		return false;
	}
	
	public static int boolean2int(boolean i) {
		if(i){
			return 1;
		}
		return 0;
	}
	
	public static Date String2Date(String s){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			java.util.Date da=sdf.parse(s);
			java.sql.Date sd=new java.sql.Date(da.getTime());
			return sd;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
//	public static Date String2Date(Long s){
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
//		try {
////			java.util.Date da=sdf.parse(s);
////			java.sql.Date sd=new java.sql.Date(da.getTime());
//			return sd;
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	 public static String Date2String(Date date) {
	        if (date!=null){
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	            return sdf.format(date);
	        }
	        return "";
	    }

	public static String getStation(int unit){
		switch(unit+1){
		case 1:
			return "00D11";
		case 2:
			return "00D12";
		case 3:
			return "00D13";
		case 4:
			return "00D14";
		case 5:
			return "000D2";
		case 6:
			return "000D5";
		case 7:
			return "00D20";
		case 8:
			return "00D19";
		case 9:
			return "000D1";
		case 10:
			return "000D8";
		case 11:
			return "000D6";
		case 12:
			return "00D17";
		case 13:
			return "000D4";
		case 14:
			return "00D18";
		case 15:
			return "00D10";
		case 16:
			return "000D9";
		case 17:
			return "000D7";
		case 18:
			return "00D16";
		case 19:
			return "000D3";
		case 20:
			return "00D15";
		}
		return null;
	}
	
	public static String getServiceStation(int i){
		switch(i+1){
		case 1:
			return "wxhrb";
		case 2:
			return "wxhrbd";
		case 3:
			return "wxhrbn";
		case 4:
			return "wxsh";
		case 5:
			return "wxqqhr";
		case 6:
			return "wxsjf";
		case 7:
			return "wxdq";
		case 8:
			return "wxjgdq";
		case 9:
			return "wxmdj";
		case 10:
			return "wxjx";
		case 11:
			return "wxjms";
		case 12:
			return "wxhlr";
		case 13:
			return "wxytlh";
		case 14:
			return "wxmzl";
		}
		
		return null;
	}
	
	   public static String getDname(int Dname) {
	        switch (Dname) {
	            case 1:
	                return "'手持台'";
	            case 2:
	            case 3:
	                return "'机控器'";
	            case 4:
	                return "'区控器'";
	        }
	        return null;
	    }
	   
	   public static String getFormDname(int i){
		   switch(i){
		   case 1:
			   return "手持台";
		   case 2:
			   return "固定机控器";
		   case 3:
			   return "移动机控器";
		   case 4:
			   return "区控器";
		   }
		   return null;
	   }
	   
	   public static String getType(int i){
		   if(i==2){
			   return "'数字固定'";
			   
		   }else{
			   return "'数字移动'";
		   }
	   }
	   
	   public static String getStatus(int i){
		   switch(i){
		
		   case 1:
			   return "备用";
		   case 2:
			   return "待用";
		   case 3:
			   return "运行";
		   case 4:
			   return "待修";
		   case 5:
			   return "维修";
		   case 6:
			   return "修竣";
		   }
		   return null;
	   }
	
	   public static int getTypeId(String name){
		   switch(name){
		   case "手持台":
			   return 1;
		   case "机控器":
			   return 2;
		   case "区控器":
			   return 3;
		   }
		   return 0;
	   }
}
