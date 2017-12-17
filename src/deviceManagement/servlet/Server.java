package deviceManagement.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import deviceManagement.bean.Attachment;
import deviceManagement.bean.Battery;
import deviceManagement.bean.DeviceResume;

import deviceManagement.bean.Notice;
import deviceManagement.bean.Result;
import deviceManagement.bean.Scrap;
import deviceManagement.bean.User;
import deviceManagement.bean.Service;
import deviceManagement.bean.Check;
import deviceManagement.biz.Biz;
import deviceManagement.biz.IBiz;
import deviceManagement.util.I;
import deviceManagement.util.JsonUtil;
import deviceManagement.util.UtilGsonBuilder;


@WebServlet("/Server")
public class Server extends HttpServlet{
	private static final long serialVersionUID = 1L;

	  private IBiz biz;
	  public Server(){
		  biz=new Biz();
	  }
	 @Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setContentType("text/html;charset=utf-8");
			System.out.println("post请求");
		    doGet(req, resp);
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println(req.toString());
		resp.setContentType("text/html;charset=utf-8");
		String strReq = req.getParameter("request");
		System.out.println("strReq:"+strReq);
		if(strReq==null){
			return;
		}
		switch(strReq){
		case I.REQUEST.FIND:
			getDetail(req,resp);
			break;
		case I.REQUEST.CONTROL:
			control(req,resp);
			break;
		case I.REQUEST.GET_TOTAL_COUNT:
			getTotalCount(req,resp);
			break; 
		case I.REQUEST.WARING:
			getWarning(resp);
			break; 
		case I.REQUEST.DOWN_SERVICE:
			downService(req,resp);
			break; 
		case I.REQUEST.DOWN_CHECK:
			downCheck(req,resp); 
			break;
		case I.REQUEST.LOGOUT:
			logOut(req,resp);
			break;
		case I.REQUEST.LOGIN:
			login(req,resp);
			break;
		case I.REQUEST.REPAIR:
			repair(req,resp);
			break; 
		case I.REQUEST.CHECK:
			check(req,resp);
			break;
		case I.REQUEST.SCRAP:
			baofei(req,resp);
			break;
		case I.REQUEST.DOWN_SCRAP:
			downScrap(req,resp);
			break;
		case I.REQUEST.DOWN_PIC:
			System.out.println("下载图片");
			downPic(req,resp);
			break;
		case I.REQUEST.GET_PIC_COUNT:
			getPicCount(req,resp);
			break;
		case I.REQUEST.UPLOAD_UNCAUGHT:
			uploadCrash(req,resp);
			break;
		case I.REQUEST.CONTROL_BAT:
			controlD(req,resp);
			break;
		case I.REQUEST.GET_STATUS_COUNT:
			getStatusCount(req,resp);
			break;
		case I.REQUEST.GET_SCRAP_COUNT:
			getTotalCount(req,resp);
			break;
		case I.REQUEST.GET_NOTICES:
			getNotices(req,resp);
			break;
		case I.REQUEST.DELETE_NOTICE:
			deleteNotice(req,resp);
			break;
		case I.REQUEST.UPDATE_NOTICE:
			updateNotice(req,resp);
			break;
		case I.REQUEST.GET_ATTACHMENT:
			getAttachment(req,resp);
			break;
		case I.REQUEST.DELETE_ATTACHMENT:
			deleteAttachment(req,resp);
			break;
		case I.REQUEST.ADD_ATTACHMENT:
			addAttachment(req,resp);
			break;
		case I.REQUEST.UPDATE_ATTACHMENT:
			updateAttachment(req,resp);
			break;
		case I.REQUEST.DOWNLOAD_FILE:
			downloadFile(req,resp);
			break;
		case I.REQUEST.GET_SERVICE_COUNT:
			downServiceCount(req,resp);
			break;
		case I.REQUEST.GET_DEVICE_RESUME:
			getDeviceResume(req,resp);
			break;
		case I.REQUEST.CHECK_BATTERY:
			checkBattery(req,resp);
			break;
		case I.REQUEST.INACTIVE:
			inactive(req,resp);
			break; 
		case I.REQUEST.DELETE_FILE:
			deleteFile(req,resp);
			break;
		case I.REQUEST.GET_NOTICE:
			getNotice(req,resp);
			break;
	
		}
	}
	



	
	private void getNotice(HttpServletRequest req, HttpServletResponse resp) {
		String title=req.getParameter(I.NOTICE.TITLE);
		long id=Long.parseLong(req.getParameter(I.NOTICE.NID));
		Result<Notice> result=biz.getNotice(title,id);
		System.out.println(result.isSuccess());
		JsonUtil.writeJsonToClient(result, resp);
	}
	private void deleteFile(HttpServletRequest req, HttpServletResponse resp) {
		String FileName=req.getParameter(I.FILE.FILENAME);
		Result<String> result=new Result<>();
		if(biz.deleteFile(FileName)){
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
			result.setRetData("成功");
		}else{
			result.setRetCode(I.RESULT.DEFEAT);
			result.setRetMsg(I.RESULT.DEF);
		}
		JsonUtil.writeJsonToClient(result, resp);
	}
	private void inactive(HttpServletRequest req, HttpServletResponse resp) {
		String usePostion=req.getParameter(I.DEVICE2.USE_POSITION);
		String Did=req.getParameter(I.DEVICE2.DID);
		Result<String> result=biz.inactive(Did, usePostion);
		JsonUtil.writeJsonToClient(result, resp);
	}
	
	private void checkBattery(HttpServletRequest req, HttpServletResponse resp) {
		String unit=req.getParameter(I.BATTERY.UNIT_ID);
		Result<ArrayList<Battery>> result=biz.getTimeOut(unit);
		JsonUtil.writeJsonToClient(result, resp);
	}
	private void getDeviceResume(HttpServletRequest req, HttpServletResponse resp) {
		String unit=req.getParameter(I.DEVICE2.UNIT_ID);
		String category=req.getParameter(I.DEVICE2.CATEGROY);
		String status=req.getParameter(I.DEVICE2.STATUS);
		String type=req.getParameter(I.DEVICE2.TYPE);
		int page=Integer.parseInt(req.getParameter(I.PAGE));
		int sType=Integer.parseInt(req.getParameter(I.STATION_TYPE));
		Result<ArrayList<DeviceResume>> result=biz.getDeviceResume(sType,unit, category, type, status, page);
		JsonUtil.writeJsonToClient(result, resp);
	}
	private void downServiceCount(HttpServletRequest req, HttpServletResponse resp) {
		String unit=req.getParameter(I.UNIT);
		Result<ArrayList<String[]>> result=biz.getServiceCount(unit);
		JsonUtil.writeJsonToClient(result, resp);
	}
	
	
	private void updateAttachment(HttpServletRequest req, HttpServletResponse resp) {
		String Aid=req.getParameter(I.ATTACHMENT.AID);
		String name=req.getParameter(I.ATTACHMENT.NAME);
		System.out.println(name);
		String newName=req.getParameter(I.ATTACHMENT.NEW_NAME);
		System.out.println(newName);
			if(biz.renameFile(name, newName)){
				System.out.println("重命名 suc");
				Result<Long> result=biz.updateAttachment(Aid, newName);
				JsonUtil.writeJsonToClient(result, resp);
			}
	}
	
	
	private void addAttachment(HttpServletRequest req, HttpServletResponse resp) {
		long completed=Long.parseLong(req.getParameter(I.FILE.COMPLETED_SIZE));
		String name=req.getParameter(I.FILE.FILENAME);
		long Nid=Long.parseLong(req.getParameter(I.ATTACHMENT.NID));
		if(biz.uploadFile(name, completed, req)){
			Attachment attachment=new Attachment();
			attachment.setAid(System.currentTimeMillis());
			attachment.setDate(new Date(System.currentTimeMillis()));
			attachment.setName(name);
			attachment.setNid(Nid);
			Result<Attachment> result=biz.addAttachment(attachment);
			JsonUtil.writeJsonToClient(result, resp);
		}	
		
	}
	private void downloadFile(HttpServletRequest req, HttpServletResponse resp) {
		String filename=req.getParameter(I.FILE.FILENAME);
		long total=Long.parseLong(req.getParameter(I.FILE.COMPLETED_SIZE));
		biz.downloadFile(filename, total,resp);
	}
	
	//使用事务
	private void deleteAttachment(HttpServletRequest req, HttpServletResponse resp) {
		String fileName=req.getParameter(I.ATTACHMENT.NAME);
		System.out.println(fileName);
		if(biz.deleteFile(fileName)){
			System.out.println("删除文件成功");
			Result<String> result=biz.deleteAttachment(fileName);
			JsonUtil.writeJsonToClient(result, resp);
		}
		
	}
	
	
	
	private void getAttachment(HttpServletRequest req, HttpServletResponse resp) {
		String Nid=req.getParameter(I.ATTACHMENT.NID);
		Result<ArrayList<Attachment>> result=biz.getAttachment(Nid);
		JsonUtil.writeJsonToClient(result, resp);	
	}
	
	private void updateNotice(HttpServletRequest req, HttpServletResponse resp) {
		String Nid=req.getParameter(I.NOTICE.NID);
		Gson gson=UtilGsonBuilder.create();
		String json=req.getParameter("bean");
		Result<String> result=null;
		Notice notice=gson.fromJson(json, Notice.class);
		if(!Nid.equals("0")){
			result=biz.updateNotice(Nid, notice);
		}else{
			result=biz.addNotice(notice);
		}
		JsonUtil.writeJsonToClient(result, resp);	
		
	}
	private void deleteNotice(HttpServletRequest req, HttpServletResponse resp) {
		String Nid=req.getParameter(I.NOTICE.NID);
		Result<String> result=biz.deleteNotice(Nid);
		JsonUtil.writeJsonToClient(result, resp);	
	}
	private void getNotices(HttpServletRequest req, HttpServletResponse resp) {
		int memory=Integer.parseInt(req.getParameter(I.MEMORY));
		Result<ArrayList<Notice>> result=biz.getNotices(memory);
		JsonUtil.writeJsonToClient(result, resp);	
	}
	
	
	private void getStatusCount(HttpServletRequest req, HttpServletResponse resp) {
		String unit=req.getParameter(I.UNIT);
		String year=req.getParameter(I.YEAR);
		String size=req.getParameter(I.MEMORY);
		Result<ArrayList<String[]>> result=biz.getStatusCount(unit,year,size);
		JsonUtil.writeJsonToClient(result, resp);		
	}
	private void controlD(HttpServletRequest req, HttpServletResponse resp) {
		String Did=req.getParameter(I.DEVICE2.DID);
		String strReq = req.getParameter(I.CONTROL_BAT.CONTROL_TYPE);
		Result<String> result=biz.controlBat(Did, strReq);
		JsonUtil.writeJsonToClient(result, resp);
	}
	private void uploadCrash(HttpServletRequest req, HttpServletResponse resp) {
		String fileName=req.getParameter(I.UNCAUGHT.FILE_NAME);
		String path=req.getParameter(I.UNCAUGHT.PATH);
		Result<String> result=biz.uploadUnCaught(path, fileName, req, resp);
		JsonUtil.writeJsonToClient(result, resp); 
	}
	private void getPicCount(HttpServletRequest req, HttpServletResponse resp) {
		String Dname=req.getParameter(I.DEVICE2.CATEGROY);
		Result<Integer> result=biz.getPicCount(Dname);
		JsonUtil.writeJsonToClient(result, resp);
	}
	private void downPic(HttpServletRequest req, HttpServletResponse resp) {
		String Dname=req.getParameter(I.DEVICE2.CATEGROY);
		System.out.println(Dname);
		int id=Integer.parseInt(req.getParameter(I.PIC.PID));
		System.out.println(id);
		biz.downPic(Dname,id,resp);
	}
	private void downScrap(HttpServletRequest req, HttpServletResponse resp) {
		int page=Integer.parseInt(req.getParameter(I.DOWNLOAD.PAGE));
		int size=Integer.parseInt(req.getParameter(I.DOWNLOAD.SIZE));
		int dName=Integer.parseInt(req.getParameter(I.SCRAP.DNAME));
		Result<Scrap[]> result=biz.downScrap(page, size,dName);
		JsonUtil.writeJsonToClient(result, resp);
	}

	private void baofei(HttpServletRequest req, HttpServletResponse resp) {
		String Did=req.getParameter(I.DEVICE2.DID);
		String User=req.getParameter(I.SCRAP.USER);
		String remark=req.getParameter(I.SCRAP.REMARK);
		String Dname=req.getParameter(I.SCRAP.DNAME);
		String unit=req.getParameter(I.SCRAP.STATION);
		String type=req.getParameter(I.SCRAP.TYPE);
		Result<String> result=biz.scrap(User,Dname, Did,remark,unit,type);
		JsonUtil.writeJsonToClient(result, resp);
	}
	private void check(HttpServletRequest req, HttpServletResponse resp) {
		String userName=req.getParameter(I.CHECK.USER);
		String Did=req.getParameter(I.DEVICE2.DID);
		String DStatus=req.getParameter(I.CHECK.STATUS);
		String remark=req.getParameter(I.CHECK.REMARK);
		String unit=req.getParameter(I.USER.UNIT);
		Result<String> result=biz.check(userName, Did, DStatus, remark,unit);
		JsonUtil.writeJsonToClient(result, resp);
	}
	private void repair(HttpServletRequest req, HttpServletResponse resp) {
		String userName=req.getParameter(I.SERVICE.USER);
		String Did=req.getParameter(I.DEVICE2.DID);
		boolean translate=Boolean.valueOf(req.getParameter(I.SERVICE.TRANSLATE));
		String remark=req.getParameter(I.SERVICE.REMARK);
		String serviceType=req.getParameter(I.SERVICE.TYPE);
		Result<String> result=biz.repair(userName, Did, translate, serviceType,remark);
		JsonUtil.writeJsonToClient(result, resp);
	}
	private void login(HttpServletRequest req, HttpServletResponse resp) {
		String userName=req.getParameter(I.USER.ACCOUNTS);
		String passwd=req.getParameter(I.USER.PASSWD);
		Result<User> result=biz.Login(userName, passwd);
		JsonUtil.writeJsonToClient(result, resp);
	}
	private void logOut(HttpServletRequest req, HttpServletResponse resp) {
		String userName=req.getParameter(I.USER.NAME);
		Result<String> result=biz.LogOut(userName);
		JsonUtil.writeJsonToClient(result, resp);
	}

	private void downCheck(HttpServletRequest req, HttpServletResponse resp) {
		String Did=req.getParameter(I.CHECK.DID);
		int page=Integer.parseInt(req.getParameter(I.DOWNLOAD.PAGE));
		int size=Integer.parseInt(req.getParameter(I.DOWNLOAD.SIZE));
		Result<Check[]> checks=biz.downloadCheck(Did,page,size);
		JsonUtil.writeJsonToClient(checks, resp);
	}
	private void downService(HttpServletRequest req, HttpServletResponse resp) {
		String Did=req.getParameter(I.SERVICE.DID);
		int page=Integer.parseInt(req.getParameter(I.DOWNLOAD.PAGE));
		int size=Integer.parseInt(req.getParameter(I.DOWNLOAD.SIZE));
		Result<Service[]> result=biz.downloadService(Did,page,size);
		JsonUtil.writeJsonToClient(result, resp);
		
	}

	private void control(HttpServletRequest req, HttpServletResponse resp) {
		String Did=req.getParameter(I.DEVICE2.DID);
		String cid=req.getParameter(I.DEVICE2.STATUS);
		Result<String> result=biz.control(cid, Did);
		JsonUtil.writeJsonToClient(result, resp);
		
	}
	private void getDetail(HttpServletRequest req, HttpServletResponse resp) {
		String id=req.getParameter(I.DEVICE2.DID);
		Result<String[]> result=biz.deviceDetail(id);
		JsonUtil.writeJsonToClient(result, resp);
	}
	private void getWarning(HttpServletResponse resp) {
		Result<String> result=biz.getYujing();
		JsonUtil.writeJsonToClient(result, resp);
		
	}
	private void getTotalCount(HttpServletRequest req,HttpServletResponse resp) {
		String unit=req.getParameter(I.UNIT);
		String year=req.getParameter(I.YEAR);
		String type=req.getParameter(I.TYPE);
		Result<ArrayList<String[]>> result=biz.getTotalCount(unit,year,type);
		JsonUtil.writeJsonToClient(result, resp);
	}
	
	

	
	
}
