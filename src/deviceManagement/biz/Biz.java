package deviceManagement.biz;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import deviceManagement.bean.Attachment;
import deviceManagement.bean.Battery;
import deviceManagement.bean.DeviceResume;

import deviceManagement.bean.Notice;
import deviceManagement.bean.Result;
import deviceManagement.bean.Scrap;
import deviceManagement.bean.User;
import deviceManagement.bean.Service;
import deviceManagement.bean.Check;
import deviceManagement.dao.Dao;
import deviceManagement.dao.IDao;
import deviceManagement.util.I;
import deviceManagement.util.PropertiesUtils;
import jpush.PushExample;

public class Biz implements IBiz {
	IDao dao;

	public Biz() {
		dao = new Dao();
	}

	@Override
	public Result<String> getYujing() {
		Result<String> result = new Result<>();
		String yujing = dao.getYujing();
		if (yujing != null) {
			result.setRetData(yujing);
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
			;
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}
		return result;

	}

	@Override
	public Result<ArrayList<String[]>> getServiceCount(String unit) {
		Result<ArrayList<String[]>> result = new Result<>();
		ArrayList<String[]> tongji = dao.getServiceCount(unit);

		if (tongji != null) {
			result.setRetData(tongji);
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}
		return result;

	}

	@Override
	public Result<ArrayList<String[]>> getTotalCount(String unit, String year, String type) {
		Result<ArrayList<String[]>> result = new Result<>();
		ArrayList<String[]> count = null; 
		System.out.println(type);
		if (type.equals("报废")) {

			count = dao.getScrapCount(unit, year);
		} else {
			count = dao.getTotalCount(unit, year);
		}
		if (count != null) {
			result.setRetData(count);
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}
		return result;

	}

	@Override
	public Result<String[]> deviceDetail(String id) {
		Result<String[]> result = new Result<String[]>();
		String[] data = dao.deviceDetail(id);
		if (data != null) {
			result.setRetData(data);
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
			;
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}
		return result;
	}

	@Override
	public Result<String> control(String control, String Did) {
		Result<String> result = new Result<>();
		if (dao.control(control, Did)) {
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
			result.setRetData(control);
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}

		return result;
	}

	@Override
	public Result<String> inactive(String Did, String position) {
		Result<String> result = new Result<>();
		if (dao.inactive(Did, position)) {
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
			result.setRetData("待用");
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}

		return result;
	}

	@Override
	public Result<User> Login(String name, String passwd) {
		Result<User> result = new Result<>();
		User user = dao.Login(name, passwd);
		if (user != null) {
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
			result.setRetData(user);
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}
		return result;
	}

	@Override
	public Result<String> LogOut(String name) {
		Result<String> result = new Result<>();
		System.out.println(name);
		if (dao.LogOut(name)) {
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
			result.setRetData("登出成功");
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}
		return result;
	}

	@Override
	public Result<Service[]> downloadService(String Did, int page, int size) {
		Result<Service[]> result = new Result<>();
		Service[] weixius = dao.getService(Did, page, size);
		if (weixius != null) {
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
			result.setRetData(weixius);
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}
		return result;
	}

	@Override
	public Result<Check[]> downloadCheck(String Did, int page, int size) {
		Result<Check[]> result = new Result<>();
		Check[] checks = dao.getCheck(Did, page, size);
		if (checks != null) {
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
			result.setRetData(checks);
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}
		return result;
	}

	@Override
	public Result<String> check(String name, String Did, String DStatus, String remark, String unit) {
		Result<String> result = new Result<>();
		boolean isOK = false;
		if (DStatus.equals("良好")) {
			isOK = true;
		}
		String status = isOK ? "备用" : "待修";
		if (dao.check(name, Did, DStatus, remark, unit)) {
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
			result.setRetData(status);
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}
		return result;
	}

	@Override
	public Result<String> repair(String name, String Did, boolean translate, String wxtype, String remark) {
		Result<String> result = new Result<>();
		if (dao.repair(name, Did, translate, wxtype, remark)) {
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
			result.setRetData("备用");
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}
		return result;
	}

	@Override
	public Result<String> scrap(String name, String Dname, String Did, String remark, String unit, String type) {
		Result<String> result = new Result<>();
		if (dao.scrap(name, Dname, Did, remark, unit, type)) {
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
			result.setRetData("报废");
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}
		return result;
	}

	@Override
	public Result<Scrap[]> downScrap(int page, int size, int dName) {
		Result<Scrap[]> result = new Result<>();
		Scrap[] scraps = dao.downScrap(page, size, dName);
		if (scraps != null) {
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
			result.setRetData(scraps);
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}
		return result;
	}

	@Override
	public void downPic(String Dname, int id, HttpServletResponse resp) {
		// 设置响应类型
		resp.setContentType("image/jpeg");
		FileInputStream in = null;
		OutputStream os = null;
		File file = new File(PropertiesUtils.getValue("device_path", "path.properties") +"/pic/"+Dname,
				 id + "." + I.PIC.AVATAR_SUFFIX_JPG);
		System.out.println(file.getName());
		try {
			in = new FileInputStream(file);
			os = resp.getOutputStream();
			byte[] buffer = new byte[1024 * 4];
			int b = 0;
			int count = 0;
			while ((b = in.read(buffer)) != -1) {
				os.write(buffer, 0, b);
				count += b;
			}
			System.out.println(count);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public Result<Integer> getPicCount(String Dname) {
		File file = new File(PropertiesUtils.getValue("device_path", "path.properties"),"pic/"+Dname);
		System.out.println(file.getAbsolutePath());
		File[] files=file.listFiles();
		int count=0;
		for(File f:files){
			if(!f.getName().substring(0,1).equals(".")){
				count++;
			}
		}
		Result<Integer> result = new Result<>();
		if (count!=0) {
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
			result.setRetData(count);
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}
		System.out.println("count:"+count);
		return result;
	}

	@Override
	public Result<String> uploadUnCaught(String path, String name, HttpServletRequest req, HttpServletResponse resp) {
		FileOutputStream out = null;
		Result<String> result = new Result<>();
		String fileName = name.substring(name.lastIndexOf("/") + 1);
		try {
			ServletInputStream in = req.getInputStream();
			File file = new File(path, fileName);
			out = new FileOutputStream(file);
			int len;
			byte[] buffer = new byte[1024 * 4];
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetData("上传成功");
			result.setRetMsg(I.RESULT.SUC);
		} catch (IOException e) {
			e.printStackTrace();
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}
		return result;
	}

	@Override
	public Result<String> controlBat(String Did, String req) {
		Result<String> result = new Result<>();
		boolean flag = false;
		String status = null;
		switch (req) {

		case I.CONTROL_BAT.BAT_INACTIVE:
			flag = dao.batInactive(Did);
			status = "待用";
			break;
		case I.CONTROL_BAT.USING:
			flag = dao.using(Did);
			status = "使用";
			break;
		case I.CONTROL_BAT.CHARGE:
			flag = dao.charging(Did);
			status = "待用";
			break;
		}
		if (flag) {
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
			result.setRetData(status);
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}
		return result;
	}

	@Override
	public Result<ArrayList<String[]>> getStatusCount(String unit, String year, String size) {
		Result<ArrayList<String[]>> result = new Result<>();

		ArrayList<String[]> tongji = dao.getStatusCount(unit, year, size);
		if (tongji != null) {
			result.setRetData(tongji);
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}
		return result;
	}

	@Override
	public Result<ArrayList<Notice>> getNotices(int memory) {
		Result<ArrayList<Notice>> result = new Result<>();
		ArrayList<Notice> list = dao.getNotices(memory);
		if (list != null) {
			result.setRetData(list);
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}
		return result;
	}

	@Override
	public Result<String> deleteNotice(String Nid) {
		Result<String> result = new Result<>();
		if (dao.deleteNotice(Nid)) {
			result.setRetData("删除成功");
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}
		return result;
	}
	

	@Override
	public Result<String> updateNotice(String Nid, Notice notice) {
		Result<String> result = new Result<>();
//		if (dao.deleteNotice(Nid)) {
//			if (dao.addNotice(notice)) {
			if(dao.updateNotice(Nid,notice)){	
				result.setRetData("更新成功");
				result.setRetCode(I.RESULT.SUCCESS);
				result.setRetMsg(I.RESULT.SUC);
				PushExample.testSendPushWithCustomConfig("您有一条新公告!");
			} else {
				result.setRetMsg(I.RESULT.DEF);
				result.setRetCode(I.RESULT.DEFEAT);
			}

		return result;
	}

	@Override
	public Result<String> addNotice(Notice notice) {
		Result<String> result = new Result<>();
		if (dao.addNotice(notice)) {
			result.setRetData("删除成功");
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
			PushExample.testSendPushWithCustomConfig("您有一条新公告!");
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}
		return result;
	}

	@Override
	public Result<ArrayList<Attachment>> getAttachment(String Nid) {
		Result<ArrayList<Attachment>> result = new Result<>();
		ArrayList<Attachment> list = new ArrayList<>();
		list = dao.getAttachment(Nid);
		if (list != null) {
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
			result.setRetData(list);
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}
		return result;
	}

	@Override
	public Result<String> deleteAttachment(String name) {
		Result<String> result = new Result<>();
		if (dao.deleteAttachment(name)) {
			System.out.println("db att delete suc");
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
			result.setRetData("删除");
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}
		return result;
	}

	@Override
	public Result<Long> updateAttachment(String Aid, String attachment) {
		Result<Long> result = new Result<>();
		long time=dao.updateAttachment(Aid, attachment);
		if (time!=0L) {
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
			result.setRetData(time);
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}

		return result;
	}

	@Override
	public Result<Attachment> addAttachment(Attachment attachment) {
		Result<Attachment> result = new Result<>();
		if (dao.addAttachment(attachment)) {
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
			result.setRetData(attachment);
			String name=dao.getNoticeName(attachment.getNid());
			PushExample.testSendPushWithCustomConfig("名为"+name+"的公告有一个新附件!");
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}
		return result;
	}

	@Override
	public boolean uploadFile(String name, long length, HttpServletRequest req) {

		DiskFileItemFactory factory = new DiskFileItemFactory();

		
		// 设置文件上传路径
		String upload = PropertiesUtils.getValue("device_path", "path.properties") +"attachment/"+name;
		// 获取系统默认的临时文件保存路径，该路径为Tomcat根目录下的temp文件夹
		String temp = System.getProperty("java.io.tmpdir");
		// 设置缓冲区大小为 5M
		factory.setSizeThreshold(1024 * 1024 * 5);
		// 设置临时文件夹为temp
		factory.setRepository(new File(temp));
		// 用工厂实例化上传组件,ServletFileUpload 用来解析文件上传请求
		ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
		try {
			List<FileItem> list = servletFileUpload.parseRequest(req);

			for (FileItem item : list) {
				InputStream is = item.getInputStream();
				String field = item.getFieldName();
				System.out.println("执行");
				System.out.println(field);
				if (field.contains("content")) {
					System.out.println(inputStream2String(is));
				} else if (field.contains("file")) {
					try {
						System.out.println("transferring");
						inputStream2File(is, upload,length);
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
			}

			System.out.println("success");
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} 
		catch (FileUploadException e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	// 流转化成字符串
	public static String inputStream2String(InputStream is) throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = -1;
		while ((i = is.read()) != -1) {
			baos.write(i);
		}
		return baos.toString();
	}

	// 流转化成文件
	public static boolean inputStream2File(InputStream is, String savePath,long length) throws Exception {
		System.out.println("文件保存路径为:" + savePath);
		InputStream inputSteam = is;
		BufferedInputStream fis = new BufferedInputStream(inputSteam);
		RandomAccessFile file;
		file = new RandomAccessFile(savePath, "rwd");
		System.out.println("文件移动至:" + length);
		file.seek(length);
		byte[] buffer = new byte[1024 * 8];
		int len = 0;
		while ((len = fis.read(buffer)) != -1) {
			System.out.println(len);
			file.write(buffer, 0, len);
		}
		System.out.println("完成上传");
		fis.close();
		file.close();
		inputSteam.close();
		return true;
	}

	@Override
	public void downloadFile(String FileName, long completesize, HttpServletResponse resp) {
		OutputStream out;
		RandomAccessFile file;

		try {
			file = new RandomAccessFile(PropertiesUtils.getValue("device_path", "path.properties") +"attachment/"+ FileName, "r");
			file.seek(completesize);
			System.out.println("seek to "+completesize);
			resp.setContentLengthLong(file.length());
			out = resp.getOutputStream();
			byte[] buffer = new byte[1024 * 8];
			int len = 0;
			while ((len = file.read(buffer)) != -1) {
				out.write(buffer, 0, len);
				System.out.println(FileName);
			}
	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public boolean deleteFile(String name) {
		File file = new File(PropertiesUtils.getValue("device_path", "path.properties") +"attachment/"+ name);
		boolean flag = false;
		if (file.exists()) {
			flag = file.delete();
		}
		return flag;
	}

	public boolean renameFile(String name, String newName) {
		boolean flag = false;
		File file = new File(PropertiesUtils.getValue("device_path", "path.properties") + "attachment/"+name);
		File newFile = new File(PropertiesUtils.getValue("device_path", "path.properties") +"attachment/"+ newName);
		flag = file.renameTo(newFile);
		return flag;
	}

	@Override
	public Result<ArrayList<DeviceResume>> getDeviceResume(int grade, String unit, String category, String type,
			String status, int page) {
		Result<ArrayList<DeviceResume>> result = new Result<>();
		ArrayList<DeviceResume> list = dao.getDeviceResume(grade, unit, category, type, status, page);
		if (list.size() != 0) {
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
			result.setRetData(list);
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}

		return result;
	}

	@Override
	public Result<ArrayList<Battery>> getTimeOut(String unit) {
		Result<ArrayList<Battery>> result = new Result<>();
		ArrayList<Battery> list = dao.checkTimeOut(unit);
		if (list.size() != 0) {
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
			result.setRetData(list);
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}
		return result;
	}

	@Override
	public Result<Notice> getNotice(String title,long id) {
		Result<Notice> result = new Result<>();
		Notice notice=null;
		if(title!=null){
			notice=dao.getNoticeFromTitle(title);
		}else{
			notice=dao.getNoticeFromId(id);
		}
		
		
		if (notice != null) {
			result.setRetCode(I.RESULT.SUCCESS);
			result.setRetMsg(I.RESULT.SUC);
			result.setRetData(notice);
		} else {
			result.setRetMsg(I.RESULT.DEF);
			result.setRetCode(I.RESULT.DEFEAT);
		}
		return result;
	}




	

}
