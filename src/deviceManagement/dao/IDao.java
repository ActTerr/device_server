package deviceManagement.dao;

import java.util.ArrayList;

import deviceManagement.bean.Attachment;
import deviceManagement.bean.Battery;
import deviceManagement.bean.DeviceResume;
import deviceManagement.bean.Notice;
import deviceManagement.bean.Scrap;
import deviceManagement.bean.User;
import deviceManagement.bean.Service;
import deviceManagement.bean.Check;

public interface IDao {
	String getYujing();
	ArrayList<String[]> getTotalCount(String unit,String year);
	ArrayList<String[]> getScrapCount(String unit,String year);
	ArrayList<String[]> getStatusCount(String unit,String year,String size);
	ArrayList<String[]> getServiceCount(String unit);
	
	String[] deviceDetail(String id);

	User Login(String name,String passwd);
	boolean LogOut(String name);
	Service[] getService(String Did,int page,int size);
	Check[] getCheck(String Did,int page,int size);

	ArrayList<DeviceResume> getDeviceResume(int grade,String unit,String category,String type,String status,int page);
	
	boolean control(String vid,String Did); 
	boolean check(String name,String Did,String DStatus,String remark ,String unit);
	boolean repair( String name, String Did, boolean translate,String type, String remark);
	boolean saveServiceDate(String Did);
	boolean scrap(String name,String Dname,String Did,String remark,String unit,String type);
	boolean inactive(String Did,String position);
	
	int getStatus(String id);
	Scrap[] downScrap(int page,int size,int dName);
	int getDCount(int dName,int dStatus);
	
	boolean batInactive(String Did);
	boolean using(String Did);
	boolean charging(String Did);
	
	ArrayList<Notice> getNotices(int memory);
	Notice getNoticeFromTitle(String title);
	Notice getNoticeFromId(long id);
	String getNoticeName(long nid);
	boolean addNotice(Notice notice);
	boolean deleteNotice(String Nid);
	boolean updateNotice(String Nid, Notice notice);
	
	ArrayList<Attachment> getAttachment(String Nid);
	boolean addAttachment(Attachment a);
	boolean deleteAttachment(String Aid);
	Long updateAttachment(String Aid,String name);
	
	
	ArrayList<Battery> checkTimeOut(String unit);

}
