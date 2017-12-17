package deviceManagement.biz;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import deviceManagement.bean.Attachment;
import deviceManagement.bean.Battery;
import deviceManagement.bean.DeviceResume;
import deviceManagement.bean.Notice;
import deviceManagement.bean.Result;
import deviceManagement.bean.Scrap;
import deviceManagement.bean.User;
import deviceManagement.bean.Service;
import deviceManagement.bean.Check;

public interface IBiz {
	Result<String> getYujing();
	Result<ArrayList<String[]>> getTotalCount(String unit,String year,String type);
	Result<ArrayList<String[]>> getStatusCount(String unit,String year,String size);
	Result<ArrayList<String[]>> getServiceCount(String unit);
	
	Result<ArrayList<DeviceResume>> getDeviceResume(int grade,String unit,String category,
			String type,String status,int page);
	Result<String[]> deviceDetail(String Did);
	Result<User> Login(String name,String passwd);
	Result<String> LogOut(String name);
	Result<Service[]> downloadService(String Did,int page,int size);
	Result<Check[]> downloadCheck(String Did,int page,int size); 
	
	
	Result<String> control(String status,String Did); 
	Result<String> check(String name,String Did,String DStatus,String remark,String unit );
	Result<String> repair(String name,String Did,boolean translate,String Type,String remark);
	Result<String> scrap(String Uname,String Dname,String Did,String remark,String unit,String type);
	Result<String> inactive(String Did,String position);
	
	Result<Scrap[]> downScrap(int page,int size,int dName);
	void downPic(String Dname,int id,HttpServletResponse resp);
	Result<Integer> getPicCount(String Dname);
	
	Result<String> controlBat(String Did,String req);
	Result<String> uploadUnCaught(String path,String name,HttpServletRequest req,
			HttpServletResponse resp);
	
	Result<ArrayList<Notice>> getNotices(int memory);
	Result<Notice> getNotice(String title,long id);
	Result<String> deleteNotice(String Nid);
	Result<String> updateNotice(String Nid,Notice notice); 
	Result<String> addNotice(Notice notice);
	
	Result<ArrayList<Attachment>> getAttachment(String Nid);
	Result<String> deleteAttachment(String Aid);
	Result<Long> updateAttachment(String Aid,String name); 
	Result<Attachment> addAttachment(Attachment attachment);
	
	boolean uploadFile(String FileName,long length,HttpServletRequest req);
	void downloadFile(String FileName,long completeSize,HttpServletResponse resp);
	boolean deleteFile(String FileName);
	boolean renameFile(String name,String newName);
	
	Result<ArrayList<Battery>> getTimeOut(String unit);

	}
 