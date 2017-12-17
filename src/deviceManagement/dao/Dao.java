package deviceManagement.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import deviceManagement.bean.Attachment;
import deviceManagement.bean.Battery;
import deviceManagement.bean.DeviceResume;
import deviceManagement.bean.Notice;
import deviceManagement.bean.Scrap;
import deviceManagement.bean.User;
import deviceManagement.bean.Service;
import deviceManagement.bean.Check;
import deviceManagement.util.DBUtils;
import deviceManagement.util.I;
import deviceManagement.util.PropertiesUtils;
import deviceManagement.util.Utils;

public class Dao implements IDao {
	public Dao() {

	}

	@Override
	public String getYujing() {
		String dir = PropertiesUtils.getValue("device_path", "path.properties");
		File file = new File(dir,"warning.txt");
		FileInputStream in = null;
		StringBuilder sb = null;
		try {
			in = new FileInputStream(file);
			InputStreamReader reader = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(reader);
			sb = new StringBuilder();
			String s;
			while ((s = br.readLine()) != null) {
				sb.append(s);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	@Override
	public ArrayList<String[]> getTotalCount(String unit, String year) {

		HashMap<Integer, String[]> list = new HashMap<>();
		int count = 0;

		if (Integer.parseInt(unit) == 0) {
			count = 20;
		} else {
			count = 1;
		}
		CountDownLatch countDown = new CountDownLatch(count);
		for (int i = 0; i < count; i++) {
			new MyThread(i, year, unit, list, countDown).start();
		}

		try {
			countDown.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ArrayList<String[]> list1 = new ArrayList<>();
		System.out.print("xingle" + list.size());
		Iterator<Entry<Integer, String[]>> it = list.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, String[]> entry = it.next();
			list1.add(entry.getValue());
		}

		return list1;
	}
	
	@Override
	public ArrayList<String[]> getServiceCount(String unit) {
		HashMap<Integer, String[]> list = new HashMap<>();
		int count = 0;

		if (Integer.parseInt(unit) == 0) {
			count = 4;
		} else {
			count = 1;
		}
		CountDownLatch countDown = new CountDownLatch(count);
		for (int i = 0; i < count; i++) {
			new ServiceThread(i, unit, list, countDown).start(); 
		}

		try {
			countDown.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ArrayList<String[]> list1 = new ArrayList<>();
		System.out.print("xingle" + list.size());
		Iterator<Entry<Integer, String[]>> it = list.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, String[]> entry = it.next();
			list1.add(entry.getValue());
		}
		return list1;
	}

	

	class ServiceThread extends Thread { 
		int i;
		String year, unit;
		HashMap<Integer, String[]> list;
		Connection conn;
		CountDownLatch countDown;

		public ServiceThread(int i, String unit, HashMap<Integer, String[]> list, CountDownLatch countDown) {
			this.i = i;
			this.unit = unit;
			this.list = list;
			this.countDown = countDown;
		}

		@Override
		public void run() {
			
			PreparedStatement ps = null;
			ResultSet rs = null;
			conn = DBUtils.getOConnection();
	
			StringBuilder status=new StringBuilder();
			status.append(" and ").append(I.DEVICE2.STATUS).append("=?");
			String[] title=new String[]{"","待修","维修","修竣"};
			list.put(i*5,title);
			for (int j = 1; j < 5; j++) {
				String[] tongji = new String[4];
				String station = null;
				String category = null;
				StringBuilder type = new StringBuilder();
				StringBuilder sql = new StringBuilder();
				category = Utils.getDname(j);
				
				if (!unit.equals("0")) {
					station = Utils.getStation(Integer.parseInt(unit));
					if (j == 2 || j == 3) {
						type.append(" and ").append(I.DEVICE2.TYPE).append("=").append(Utils.getType(j));
					} else {
						type.append("");
					}
				} else {
					station = Utils.getStation(i);
					if (j == 2 || j == 3) {
						type.append(" and ").append(I.DEVICE2.TYPE).append("=").append(Utils.getType(j));
					} else {
						type.append("");
					}

				}
			
				sql.append("select count(*) from ").append(I.DEVICE2.TABLE_NAME).append(" where ")
						.append(I.DEVICE2.STATION).append("='").append(station).append("' and ")
						.append(I.DEVICE2.CATEGROY).append("=").append(category).append(type).append(status);
				try {
					for(int m=1;m<4;m++){
						ps = conn.prepareStatement(sql.toString());
						ps.setString(1,Utils.getStatus(m+3) );
						rs = ps.executeQuery();
						int size = 0;
						if (rs.next()) {
							size = rs.getInt("count(*)");
						}
						tongji[m] = String.valueOf(size);
						ps.close();
						rs.close();
					}
					tongji[0]=Utils.getFormDname(j);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				System.out.println(i*5+j+":"+Arrays.toString(tongji));
				list.put(i*5+j, tongji);
			}
			
			countDown.countDown();
		}
	}
	
	
	class MyThread extends Thread {
		int i;
		String year, unit;
		HashMap<Integer, String[]> list;
		Connection conn;
		CountDownLatch countDown;

		public MyThread(int i, String year, String unit, HashMap<Integer, String[]> list, CountDownLatch countDown) {
			super();
			this.i = i;
			this.year = year;
			this.unit = unit;
			this.list = list;
			this.countDown = countDown;
		}

		@Override
		public void run() {
			String[] tongji = new String[4];
			PreparedStatement ps = null;
			ResultSet rs = null;
			conn = DBUtils.getOConnection();
			StringBuilder SQLYear = new StringBuilder();
			if (year.equals("all")) {
				SQLYear.append("");
			} else {
				SQLYear.append(" and ").append(I.DEVICE2.USE_DATE)
						.append(" between to_date ('" + year + "-01-01 00:00:00','yyyy-MM-dd HH24:MI:SS') and ")
						.append("to_date ('" + (Integer.parseInt(year) + 1))
						.append("-01-01 00:00:00','yyyy-MM-dd HH24:MI:SS')");
			}
			for (int j = 1; j < 5; j++) {

				String station = null;
				String category = null;
				StringBuilder type = new StringBuilder();
				StringBuilder sql = new StringBuilder();
				category = Utils.getDname(j);
				if (!unit.equals("0")) {
					station = Utils.getStation(Integer.parseInt(unit));
					if (j == 2 || j == 3) {
						type.append(" and ").append(I.DEVICE2.TYPE).append("=").append(Utils.getType(j));
					} else {
						type.append("");
					}
				} else {
					station = Utils.getStation(i);
					if (j == 2 || j == 3) {
						type.append(" and ").append(I.DEVICE2.TYPE).append("=").append(Utils.getType(j));
					} else {
						type.append("");
					}

				}
				sql.append("select count(*) from ").append(I.DEVICE2.TABLE_NAME).append(" where ")
						.append(I.DEVICE2.STATION).append("='").append(station).append("' and ")
						.append(I.DEVICE2.CATEGROY).append("=").append(category).append(type).append(SQLYear);
				try {
					ps = conn.prepareStatement(sql.toString());
					System.out.println(sql);
					rs = ps.executeQuery();
					int size = 0;
					if (rs.next()) {
						size = rs.getInt("count(*)");
					}
					tongji[j - 1] = String.valueOf(size);
					ps.close();
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			list.put(i, tongji);
			countDown.countDown();
		}
	}

	

	@Override
	public String[] deviceDetail(String id) {
		System.out.println("id:" + id);
		Connection conn = DBUtils.getOConnection();
		PreparedStatement ps = null;
		String sql = "select*from " + I.DEVICE2.TABLE_NAME + " where " + I.DEVICE2.DID + "=?";
		ResultSet rs = null;
		String[] data = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, id);

			System.out.println(ps.toString());
			rs = ps.executeQuery();

			if (rs.next()) {
				data = new String[30];
				for (int i = 0; i < 29; i++) {
					data[i] = rs.getString(i + 1);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return data;
	}

	@Override
	public boolean control(String cid, String Did) {
		Connection conn = DBUtils.getOConnection();
		PreparedStatement ps = null;
		String sql = "update " + I.DEVICE2.TABLE_NAME + " set " + I.DEVICE2.STATUS + "=?" + " where " + I.DEVICE2.DID
				+ "=?";
		try {
			if(cid.equals("维修")){
				if(!saveServiceDate(Did)){
					return false;
				}
			}
			ps = conn.prepareStatement(sql);
			ps.setString(1, cid);
			ps.setString(2, Did);
			return ps.executeUpdate() == 1;
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(conn, ps, null);
		}
		return false;
	}



	@Override
	public User Login(String name, String passwd) {
		Connection conn = DBUtils.getOConnection();
		PreparedStatement ps = null;
		String sql = "select * from MYUSER where ACCOUNTS=?";
		ResultSet rs = null;
		User user = null;
		System.out.println(name + ":" + passwd);
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			rs = ps.executeQuery();
			if (rs.next()) {

				String p = rs.getString("MYPASSWD");
				if (p.equals(passwd)) {
					user = new User();
					user.setUnit(rs.getInt(I.USER.UNIT));
					user.setGrade(rs.getInt(I.USER.GRADE));
					user.setAuthority(rs.getInt(I.USER.AUTHORITY));
					user.setName(rs.getString(I.USER.NAME));
					user.setAccounts(rs.getString(I.USER.ACCOUNTS));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return user;
	}

	@Override
	public boolean LogOut(String name) {
		System.out.println("logout");
		return true;
	}

	@Override
	public Service[] getService(String Did, int page, int size) {
		Connection conn = DBUtils.getOConnection();
		PreparedStatement ps = null;
		String sql = "select*from " + I.SERVICE.TABLENAME + " where DID=? and "
				+I.SERVICE.ID+">? and "+I.SERVICE.ID+"<?";
		ResultSet rs = null;

		int i = 0;
		int count = getWCount(I.SERVICE.TABLENAME, Did);
		int fin = 0;
		System.out.println("count" + count);
		if (count < page * size) {
			fin = count;
		} else {
			fin = page * size;
		}
		int length = fin - (page - 1) * size;
		if (length == 0) {
			return null;
		}
		Service[] services = new Service[length];
		System.out.println("lenght" + length);
		try {
			ps = conn.prepareStatement(sql);
			System.out.println(sql);
			ps.setString(1, Did);
			ps.setInt(2, (page - 1) * size);
			ps.setInt(3, size);
			rs = ps.executeQuery();
			while (rs.next()) {
				Service service = new Service();
				service.setUser(rs.getString(I.SERVICE.USER));
				service.setRemark(rs.getString(I.SERVICE.REMARK));
				service.setServiceDate(rs.getDate(I.SERVICE.SERVICE_DATE));
				service.setRepairDate(rs.getDate(I.SERVICE.REPAIR_DATE));
				service.setTranslate(Utils.int2boolean(rs.getInt(I.SERVICE.TRANSLATE)));
				services[i] = service;
				i++;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return services;
	}

	@Override
	public Check[] getCheck(String Did, int page, int size) {
		Connection conn = DBUtils.getOConnection();
		PreparedStatement ps = null;
		String sql = "select*from " + I.CHECK.TABLENAME + " where " + I.SERVICE.DID + "=? and "
				+I.SERVICE.ID+">? and "+I.SERVICE.ID+"<?";
		ResultSet rs = null;

		int i = 0;
		int count = getWCount(I.CHECK.TABLENAME, Did);
		System.out.println("count" + count);
		int fin = 0;
		if (count < page * size) {
			fin = count;
		} else {
			fin = page * size;
		}
		int length = fin - (page - 1) * size;
		if (length == 0) {
			return null;
		}
		Check[] checks = new Check[length];
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, Did);
			ps.setInt(2, (page - 1) * size);
			ps.setInt(3, size);
			rs = ps.executeQuery();
			while (rs.next()) {
				Check check = new Check();
				check.setUser(rs.getString(I.CHECK.USER));
				check.setCause(rs.getString(I.CHECK.REMARK));
				check.setStatus(rs.getString(I.CHECK.STATUS));
				check.setDate(rs.getDate(I.CHECK.DATE));
				checks[i] = check;
				i++;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return checks;
	}

	@Override
	public boolean check(String name, String Did, String DStatus, String remark,String unit) {
		Connection conn = DBUtils.getOConnection();
		PreparedStatement ps = null;
		String sql = "update " + I.DEVICE2.TABLE_NAME + " set " + I.DEVICE2.STATUS + "=? ,"+I.DEVICE2.SERVICE_STATION+"=? where " + I.DEVICE2.DID + "='"
				+ Did+"'";
		String sql2 = "insert into " + I.CHECK.TABLENAME + " values(?,?,?,?,?,?)";
		String sql3 = "update " + I.DEVICE2.TABLE_NAME + " set " + I.DEVICE2.DATE_IN_CHECK + "=?" + " where " + I.DEVICE2.DID
				+ "='" + Did+"'";
		String select="select count(*) from "+I.CHECK.TABLENAME+" where "+I.CHECK.DID+"=?";
		ResultSet rs=null;
		int count = 0;
		boolean isOK = false;
		if(DStatus.equals("良好")){
			isOK=true;
		}
		boolean flag = false;
		String date=Utils.Date2String(new Date(System.currentTimeMillis()));
		java.sql.Timestamp timestamp=java.sql.Timestamp.valueOf(date);
		
		try {
			ps=conn.prepareStatement(select);
			ps.setString(1, Did);
			rs=ps.executeQuery();
				int id=0;
			if(rs.next()){				
				id=rs.getInt(1);
			}
			ps.close();
			rs=null;
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql3);
			
			ps.setTimestamp(1, timestamp);
			count += ps.executeUpdate();
			ps.close();
			if (!isOK) {
				ps = conn.prepareStatement(sql);
				ps.setString(1,"待修");
				ps.setString(2,unit);
				count += ps.executeUpdate();
				ps.close();
//			
			}
				ps = conn.prepareStatement(sql2);
				ps.setString(1,Did);
				ps.setTimestamp(2, timestamp);
				
				System.out.println(Did);
				ps.setString(3,DStatus);
				ps.setString(4, name);
				ps.setString(5, remark);
				ps.setInt(6, id+1);
				count += ps.executeUpdate();
			System.out.println(count);
			if (isOK) {
			flag=count==1;
			} else {
			flag=count==3;
			}
			conn.commit();
			System.out.println();
			return flag;

		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			DBUtils.closeAll(conn, ps, null);
		}
		return false;
	}

	@Override
	public boolean repair( String name, String Did, boolean translate, String type,String remark) {
		Connection conn = DBUtils.getOConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		int id = 0;
		String sql2 = "update " + I.DEVICE2.TABLE_NAME + " set " + I.DEVICE2.STATUS + "=?" + " where " + I.DEVICE2.DID + "=?"
		;
		String sql3 = "select * from service where " + I.SERVICE.DID + "=?"  + " order by id DESC";
		String sql4 = "UPDATE " + I.SERVICE.TABLENAME + " SET " + I.SERVICE.TRANSLATE + "=?," + I.SERVICE.USER + "=?,"
				+ I.SERVICE.REMARK + "=?," + I.SERVICE.REPAIR_DATE + "=?,"+I.SERVICE.TYPE+"=?" + " WHERE " 
				+ I.SERVICE.ID + "=? and "
				+I.SERVICE.DID+"=?";
		int count = 0;
		try {
			conn.setAutoCommit(false);
				ps = conn.prepareStatement(sql2);
				ps.setString(1,"备用");
				ps.setString(2,Did);
				count += ps.executeUpdate();
				ps.close();

				ps = conn.prepareStatement(sql3);
				ps.setString(1, Did);
				rs = ps.executeQuery();
			
				if (rs.next()) {
					id = rs.getInt(I.SERVICE.ID);
					System.out.println(id);
				}
				
				ps.close();
				System.out.println(sql4);
					ps = conn.prepareStatement(sql4);
					ps.setString(2, name);
					ps.setInt(1, Utils.boolean2int(translate));
					ps.setString(3, remark);
					String date=Utils.Date2String(new Date(System.currentTimeMillis()));
					java.sql.Timestamp timestamp=java.sql.Timestamp.valueOf(date);
					ps.setTimestamp(4, timestamp);
					ps.setString(5,type);
					ps.setInt(6, id);
					ps.setString(7, Did);
					count += ps.executeUpdate();
					System.out.println(count);
			conn.commit();
			return count==2;
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				System.out.println("进入rallback");
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return false;
	}

	@Override
	public boolean saveServiceDate(String Did) {
		Connection conn = DBUtils.getOConnection();
		PreparedStatement ps = null;
		String sql = "insert into " + I.SERVICE.TABLENAME + "(" 
		+ I.SERVICE.SERVICE_DATE + "," + I.SERVICE.DID +","+
		I.SERVICE.ID+ ")"+ " values(?,?,?)";
		String select="select count(*) from "+I.SERVICE.TABLENAME+" where "
		+I.SERVICE.DID+"=?";
		ResultSet rs=null;
		try {
			int size=0;
			ps=conn.prepareStatement(select);
			ps.setString(1, Did);
			rs=ps.executeQuery();
			if(rs.next()){
				size=rs.getInt(1)+1;
			}
			ps.close();
			ps = conn.prepareStatement(sql);
			String date=Utils.Date2String(new Date(System.currentTimeMillis()));
			java.sql.Timestamp timestamp=java.sql.Timestamp.valueOf(date);
			ps.setTimestamp(1, timestamp);
			ps.setString(2, Did);
			ps.setInt(3, size);
			int count = ps.executeUpdate();
			return count == 1;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(conn, ps, null);
		}
		return false;

	}

	@Override
	public boolean scrap(String user, String Dname, String Did, String remark,String unit,String type) {
		Connection conn = DBUtils.getOConnection();
		PreparedStatement ps = null;
		String sql2 = "insert into " + I.SCRAP.TABLENAME + " values(?,?,?,?,?,?,?)";
		String sql= "update "+I.DEVICE2.TABLE_NAME+" set "+I.DEVICE2.STATUS+"=? where "+I.DEVICE2.DID+"=?";
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			ps.setString(1, "报废");
			ps.setString(2, Did);
			int count = ps.executeUpdate();
			ps.close();

			ps = conn.prepareStatement(sql2);
			ps.setString(1, Did);
			ps.setString(2, Dname);
			String date=Utils.Date2String(new Date(System.currentTimeMillis()));
			java.sql.Timestamp timestamp=java.sql.Timestamp.valueOf(date);
			ps.setTimestamp(3,timestamp );
			ps.setString(4, user);
			ps.setString(5, remark);
			ps.setString(6, unit);
			ps.setString(7, type);
			count += ps.executeUpdate();
			conn.commit();
			System.out.println(count);
			return count == 2;

		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			DBUtils.closeAll(conn, ps, null);
		}
		return false;
	}

	@Override
	public int getStatus(String Did) {
		String sql = "select " + I.DEVICE2.STATUS + " from " + I.DEVICE2.TABLE_NAME + " where " + I.DEVICE2.DID + "=" + Did;
		Connection conn = DBUtils.getOConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		int status = 0;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				status = rs.getInt(I.DEVICE2.STATUS);
			} 
			return status;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}

		return 0;
	}

	@Override
	public Scrap[] downScrap(int page, int size, int Dname) {
		Connection conn = DBUtils.getOConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select * from " + I.SCRAP.TABLENAME + " where " + I.DEVICE2.CATEGROY + "=" + Dname + " limit ?,?";
		int count = getDCount( Dname, 0);
		int length = 0;
		if (count > page * size) {
			length = size;
		} else {
			length = count - (page - 1) * size;
		}
		if (length <= 0) {
			return null;
		}
		Scrap[] scraps = new Scrap[length];
		int i = 0;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, (page - 1) * size);
			ps.setInt(2, page * size);
			rs = ps.executeQuery();

			while (rs.next()) {
				Scrap s = new Scrap();
				s.setDid(rs.getInt(I.SCRAP.DID));
				s.setDname(rs.getInt(I.SCRAP.DNAME));
				s.setRemark(rs.getString(I.SCRAP.REMARK));
				s.setUser(rs.getString(I.SCRAP.USER));
				s.setScrapDate(rs.getDate(I.SCRAP.DATE));
				scraps[i] = s;
				i++;
				if (i == length) {
					return scraps;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}
		return scraps;
	}



	@Override
	public int getDCount( int dName, int Dstatus) {
		Connection conn = DBUtils.getOConnection();
		PreparedStatement ps = null;
		String sql = "";
		if (Dstatus == 0) {
			sql = "select count(*) from " + I.SCRAP.TABLENAME + " where " + I.SCRAP.DNAME + "=" + dName;
		} else {
			sql = "select count(*) from " + I.SCRAP.TABLENAME + " where " + I.SCRAP.DNAME + "=" + dName + " and "
					+ I.DEVICE2.STATUS + "=" + Dstatus;
		}

		ResultSet rs = null;
		int count = 0;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				count = rs.getInt("count(*)");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}

		return count;
	}

	private int getWCount(String tableName, String did) {
		Connection conn = DBUtils.getOConnection();
		PreparedStatement ps = null;
		String sql = "select count(*) from " + tableName + " where " + I.SERVICE.DID + " =? " ;
		ResultSet rs = null;
		int count = 0;
		try {
			ps = conn.prepareStatement(sql);
			System.out.println(did);
			System.out.println(sql);
			ps.setString(1, did);
			rs = ps.executeQuery();
			if (rs.next()) {
				count = rs.getInt("COUNT(*)");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(conn, ps, rs);
		}

		return count;
	}

	@Override
	public boolean batInactive(String Did) {
		Connection conn=DBUtils.getOConnection();
		ResultSet rs=null;
		PreparedStatement ps=null;
		StringBuilder sql1=new StringBuilder();
		sql1.append("select ").append(I.BATTERY.START_TIME).append(",")
		.append(I.BATTERY.USED_DURATION)
		.append(" from ")
		.append(I.BATTERY.TABLENAME).append(" where ").append(I.BATTERY.DID).append(" =?");
		StringBuilder sql2=new StringBuilder();
		sql2.append("update ").append(I.BATTERY.TABLENAME).append(" set ")
		.append(I.BATTERY.USED_DURATION).append("=? ,").append(I.BATTERY.STATUS)
		.append("=?").append(" where ").append(I.BATTERY.DID).append("=?");
		try {
			long start = 0;
			long afterUsed=0;
			ps=conn.prepareStatement(sql1.toString());
			ps.setString(1, Did);
			rs=ps.executeQuery();
			if(rs.next()){
				start=rs.getLong(I.BATTERY.START_TIME);
				afterUsed=rs.getLong(I.BATTERY.USED_DURATION);
			}
			ps.close();
			
			ps=conn.prepareStatement(sql2.toString());
			long used=System.currentTimeMillis()-start+afterUsed;
			ps.setLong(1, used);
			ps.setString(2,"待用");
			ps.setString(3,Did);
			return ps.executeUpdate()==1;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBUtils.closeAll(conn, ps, rs);
		}
		return false;
	}

	@Override
	public boolean using(String Did) {
		Connection conn=DBUtils.getOConnection();
		ResultSet rs=null;
		PreparedStatement ps=null;
		int exit=0;
		StringBuilder sql1=new StringBuilder();
		sql1.append("select count(*) from ").append(I.BATTERY.TABLENAME)
		.append(" where ").append(I.BATTERY.DID).append("=?");
	
		String sql2=new StringBuilder().append("insert into ").append(I.BATTERY.TABLENAME)
				.append(" values(?,?,?,?,?,?,?)").toString();
		String sql3=new StringBuilder().append("update ").append(I.BATTERY.TABLENAME)
				.append(" set ").append(I.BATTERY.START_TIME).append("=?,")
				.append(I.BATTERY.STATUS).append("=?").append(" where ")
				.append(I.BATTERY.DID).append("=?").toString();
		String sql4=new StringBuilder().append("select ").append(I.DEVICE2.USE_DURATION)
				.append(",").append(I.DEVICE2.UNIT_ID).append(",").append(I.DEVICE2.CATEGROY)
				.append(" from ").append(I.DEVICE2.TABLE_NAME).append(" where ")
				.append(I.DEVICE2.DID).append("=?").toString();
		try {
			ps=conn.prepareStatement(sql1.toString());
			ps.setString(1,Did);
			rs=ps.executeQuery();
			if(rs.next()){
				exit=rs.getInt("count(*)");
			}
			ps.close();
			rs.close();
			if(exit==0){
				int use=0;
				String unit=null;
				String name=null;
				ps=conn.prepareStatement(sql4);
				ps.setString(1, Did);
				rs=ps.executeQuery();
				if(rs.next()){
					use=rs.getInt(1);
					unit=rs.getString(2);
					name=rs.getString(3);
				}
				ps.close();
				
				ps=conn.prepareStatement(sql2);
				ps.setString(1, Did);
				ps.setLong(2,System.currentTimeMillis());
				ps.setLong(3,0);
				ps.setLong(4,use*60*60*1000);
				ps.setString(5,"待用");
				ps.setString(6,unit);
				ps.setString(7, name);
				return ps.executeUpdate()==1;
			}else{
				ps=conn.prepareStatement(sql3);
				ps.setLong(1, System.currentTimeMillis());
				ps.setString(2,"使用");
				ps.setString(3,Did);
				return ps.executeUpdate()==1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBUtils.closeAll(conn, ps, rs);
		}
		
		return false;
	}

	@Override
	public boolean charging(String Did) {
		Connection conn=DBUtils.getOConnection();
		PreparedStatement ps=null;
		StringBuilder sql1=new StringBuilder();
		sql1.append("update ").append(I.BATTERY.TABLENAME).append(" set ")
		.append(I.BATTERY.STATUS).append("=? ,")
		.append(I.BATTERY.START_TIME).append("=?,")
		.append(I.BATTERY.USED_DURATION).append("=?")
		.append(" where ").append(I.BATTERY.DID).append("=?");
		
		try {
			ps=conn.prepareStatement(sql1.toString());
			ps.setString(1, "待用");
			ps.setLong(2,0);
			ps.setLong(3,0);
			ps.setString(4,Did);
			System.out.println(sql1.toString());
			return ps.executeUpdate()==1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public ArrayList<String[]> getStatusCount(String unit, String year, String memory) {
		HashMap<Integer, String[]> list = new HashMap<>();
		int count = 0;

		if (Integer.parseInt(unit) == 0) {
			count = 4;
		} else {
			count = 1;
		}
		CountDownLatch countDown = new CountDownLatch(count);
		for (int i = 0; i < count; i++) {
			new statusThread(i, year, unit, list, countDown).start(); 
		}

		try {
			countDown.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ArrayList<String[]> list1 = new ArrayList<>();
		System.out.print("xingle" + list.size());
		Iterator<Entry<Integer, String[]>> it = list.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, String[]> entry = it.next();
			list1.add(entry.getValue());
		}
		return list1;
	}
	
	class statusThread extends Thread {
		int i;
		String year, unit;
		HashMap<Integer, String[]> list;
		Connection conn;
		CountDownLatch countDown;

		public statusThread(int i, String year, String unit, HashMap<Integer, String[]> list, CountDownLatch countDown) {
			super();
			this.i = i;
			this.year = year;
			this.unit = unit;
			this.list = list;
			this.countDown = countDown;
		}

		@Override
		public void run() {
			
			PreparedStatement ps = null;
			ResultSet rs = null;
			conn = DBUtils.getOConnection();
			StringBuilder SQLYear = new StringBuilder();
			if (year.equals("all")) {
				SQLYear.append("");
			} else {
				SQLYear.append(" and ").append(I.DEVICE2.USE_DATE)
						.append(" between to_date ('" + year + "-01-01 00:00:00','yyyy-MM-dd HH24:MI:SS') and ")
						.append("to_date ('" + (Integer.parseInt(year) + 1))
						.append("-01-01 00:00:00','yyyy-MM-dd HH24:MI:SS')");
			}
			StringBuilder status=new StringBuilder();
			status.append(" and ").append(I.DEVICE2.STATUS).append("=?");
			String[] title=new String[]{"","备用","待用","运行","待修","维修","修竣"};
			list.put(i*5,title);
			for (int j = 1; j < 5; j++) {
				String[] tongji = new String[7];
				String station = null;
				String category = null;
				StringBuilder type = new StringBuilder();
				StringBuilder sql = new StringBuilder();
				category = Utils.getDname(j);
				
				if (!unit.equals("0")) {
					station = Utils.getStation(Integer.parseInt(unit));
					if (j == 2 || j == 3) {
						type.append(" and ").append(I.DEVICE2.TYPE).append("=").append(Utils.getType(j));
					} else {
						type.append("");
					}
				} else {
					station = Utils.getStation(i);
					if (j == 2 || j == 3) {
						type.append(" and ").append(I.DEVICE2.TYPE).append("=").append(Utils.getType(j));
					} else {
						type.append("");
					}

				}
			
				sql.append("select count(*) from ").append(I.DEVICE2.TABLE_NAME).append(" where ")
						.append(I.DEVICE2.STATION).append("='").append(station).append("' and ")
						.append(I.DEVICE2.CATEGROY).append("=").append(category).append(type).append(SQLYear).append(status);
				try {
					for(int m=1;m<7;m++){
						ps = conn.prepareStatement(sql.toString());
						ps.setString(1,Utils.getStatus(m) );
//						System.out.println(Utils.getStatus(m));
//						System.out.println(sql);
						rs = ps.executeQuery();
						int size = 0;
						if (rs.next()) {
							size = rs.getInt("count(*)");
						}
						tongji[m] = String.valueOf(size);
						ps.close();
						rs.close();
					}
					tongji[0]=Utils.getFormDname(j);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				System.out.println(i*5+j+":"+Arrays.toString(tongji));
				list.put(i*5+j, tongji);
			}
			
			countDown.countDown();
		}
	}

	@Override
	public ArrayList<String[]> getScrapCount(String unit, String year) {
		System.out.println("execute bafoe");
		HashMap<Integer, String[]> list = new HashMap<>();
		int count = 0;

		if (Integer.parseInt(unit) == 0) {
			count = 20;
		} else {
			count = 1;
		}
		CountDownLatch countDown = new CountDownLatch(count);
		for (int i = 0; i < count; i++) {
			new MyBfThread(i, year, unit, list, countDown).start();
		}

		try {
			countDown.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ArrayList<String[]> list1 = new ArrayList<>();
		System.out.print("xingle" + list.size());
		Iterator<Entry<Integer, String[]>> it = list.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, String[]> entry = it.next();
			list1.add(entry.getValue());
		}

		return list1;

	}
	class MyBfThread extends Thread {
		int i;
		String year, unit;
		HashMap<Integer, String[]> list;
		Connection conn;
		CountDownLatch countDown;

		public MyBfThread(int i, String year, String unit, HashMap<Integer, String[]> list, CountDownLatch countDown) {
			super();
			this.i = i;
			this.year = year;
			this.unit = unit;
			this.list = list;
			this.countDown = countDown;
		}

		@Override
		public void run() {
			String[] tongji = new String[4];
			PreparedStatement ps = null;
			ResultSet rs = null;
			conn = DBUtils.getOConnection();
			StringBuilder SQLYear = new StringBuilder();
			if (year.equals("all")) {
				SQLYear.append("");
			} else {
				SQLYear.append(" and ").append(I.SCRAP.DATE)
						.append(" between to_date ('" + year + "-01-01 00:00:00','yyyy-MM-dd HH24:MI:SS') and ")
						.append("to_date ('" + (Integer.parseInt(year) + 1))
						.append("-01-01 00:00:00','yyyy-MM-dd HH24:MI:SS')");
			}
		
			for (int j = 1; j < 5; j++) {

				String station = null;
				String category = null;
				StringBuilder type = new StringBuilder();
				StringBuilder sql = new StringBuilder();
				category = Utils.getDname(j);
				if (!unit.equals("0")) {
					station = Utils.getStation(Integer.parseInt(unit));
					if (j == 2 || j == 3) {
						type.append(" and ").append(I.SCRAP.TYPE).append("=").append(Utils.getType(j));
					} else {
						type.append("");
					}
				} else {
					station = Utils.getStation(i);
					if (j == 2 || j == 3) {
						type.append(" and ").append(I.SCRAP.TYPE).append("=").append(Utils.getType(j));
					} else {
						type.append("");
					}

				}
				
				sql.append("select count(*) from ").append(I.SCRAP.TABLENAME).append(" where ")
						.append(I.SCRAP.STATION).append("='").append(station).append("' and ")
						.append(I.SCRAP.DNAME).append("=").append(category).append(type).append(SQLYear);
				try {
					ps = conn.prepareStatement(sql.toString());
					System.out.println(sql);
					rs = ps.executeQuery();
					int size = 0;
					if (rs.next()) {
						size = rs.getInt("count(*)");
					}
					tongji[j - 1] = String.valueOf(size);
					ps.close();
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			list.put(i, tongji);
			countDown.countDown();
		}
	}

	@Override
	public ArrayList<Notice> getNotices(int memory) {
		Connection conn=DBUtils.getOConnection();
		PreparedStatement ps=null;
		ResultSet rs=null;
	
		
		String sql="SELECT * FROM (select ROWNUM r,"+I.NOTICE.TABLENAME+
				".* from "+I.NOTICE.TABLENAME+") WHERE r BETWEEN ? AND ?";
		ArrayList<Notice> list=null;
		try {
			list=new ArrayList<>();
			System.out.println(sql);
			ps=conn.prepareStatement(sql);
			ps.setInt(1, memory);
			ps.setInt(2, memory+9);
			rs=ps.executeQuery();
			while(rs.next()){
				Notice notice=new Notice();
				notice.setCommon(rs.getString(I.NOTICE.COMMON));
				notice.setDate(rs.getDate(I.NOTICE.DATE));
				notice.setNid((rs.getLong(I.NOTICE.NID)));
				notice.setTitle(rs.getString(I.NOTICE.TITLE));
				list.add(notice);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBUtils.closeAll(conn, ps, rs);
		}
		return list;
	}

	@Override
	public boolean addNotice(Notice notice) {
		Connection conn=DBUtils.getOConnection();
		PreparedStatement ps=null;
		String sql="insert into "+I.NOTICE.TABLENAME+" values(?,?,?,?)";
	
		try {
	
			ps=conn.prepareStatement(sql);
			ps.setLong(1,notice.getNid());
			ps.setString(2, notice.getTitle());
			java.sql.Date date=new Date(notice.getDate().getTime());
			ps.setDate(3, date);
			ps.setString(4,notice.getCommon());
		
			return ps.executeUpdate()==1;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBUtils.closeAll(conn, ps, null);
		}
		return false;
	}

	@Override
	public boolean deleteNotice(String Nid) {
		Connection conn=DBUtils.getOConnection();
		PreparedStatement ps=null;
		String sql="delete from "+I.NOTICE.TABLENAME+" where "+I.NOTICE.NID+"=?";
		try {
			ps=conn.prepareStatement(sql);
			ps.setLong(1, Long.parseLong(Nid));
			return ps.executeUpdate()==1;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBUtils.closeAll(conn, ps,null);
		}
		return false;
	}

	@Override
	public ArrayList<Attachment> getAttachment(String Nid) {
		Connection conn=DBUtils.getOConnection();
		PreparedStatement ps=null;
		ResultSet rs=null;
		String sql="select * from "+I.ATTACHMENT.TABLENAME+" where "+I.ATTACHMENT.NID+"=?";
		ArrayList<Attachment> list=null;
		try {
			list=new ArrayList<>();
			ps=conn.prepareStatement(sql);
			ps.setString(1, Nid);
			rs=ps.executeQuery();
			while(rs.next()){
				Attachment attach=new Attachment();
				attach.setAid(rs.getLong(I.ATTACHMENT.AID));
				attach.setNid(rs.getLong(I.ATTACHMENT.NID));
				attach.setDate(rs.getDate(I.ATTACHMENT.DATE));
				attach.setName(rs.getString(I.ATTACHMENT.NAME));
				list.add(attach);
			
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBUtils.closeAll(conn, ps, rs);
		}
		return list;
	}

	@Override
	public boolean addAttachment(Attachment a) {
		Connection conn=DBUtils.getOConnection();
		PreparedStatement ps=null;
		String sql="insert into "+I.ATTACHMENT.TABLENAME+" values(?,?,?,?)";
	
		try {
		
			ps=conn.prepareStatement(sql);
			ps.setLong(1,a.getAid());
			ps.setLong(2, a.getNid());
			java.sql.Date date=new Date(a.getDate().getTime());
			ps.setDate(3, date);
			ps.setString(4, a.getName());
	
			return ps.executeUpdate()==1;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBUtils.closeAll(conn, ps, null);
		}
		return false;
	}

	@Override
	public boolean deleteAttachment(String name) {
		Connection conn=DBUtils.getOConnection();
		PreparedStatement ps=null;
		String sql="delete from "+I.ATTACHMENT.TABLENAME+" where "+I.ATTACHMENT.NAME+"=?";
		try {
			System.out.println(sql+name);
			ps=conn.prepareStatement(sql);
			ps.setString(1,name);
			return ps.executeUpdate()==1;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBUtils.closeAll(conn, ps,null);
		}
		return false;
	}

	@Override
	public Long updateAttachment(String Aid, String name) {
		Connection conn=DBUtils.getOConnection();
		PreparedStatement ps=null;
		String sql="update "+I.ATTACHMENT.TABLENAME+" set "+I.ATTACHMENT.NAME+"=?,"
				+I.ATTACHMENT.AID+"=? "+" where "+I.ATTACHMENT.AID+"=?";
		System.out.println(Aid+":"+name);
		long time=System.currentTimeMillis();
		try {
			ps=conn.prepareStatement(sql);
			ps.setString(1,name);
			ps.setLong(2,time );
			ps.setLong(3, Long.parseLong(Aid));
			
			if(ps.executeUpdate()==1){
				return time;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBUtils.closeAll(conn, ps, null);
		}
		return 0L;
	}

	@Override
	public ArrayList<DeviceResume> getDeviceResume(int sType,String unit, String category, String type, String status, int page) {
		Connection conn=DBUtils.getOConnection();
		PreparedStatement ps=null;
		ResultSet rs=null;
		String typesql="";
		if(type!=null){
			typesql=I.DEVICE2.TYPE+"='"+type+"' and ";
		}
		String key=null;
		if(sType==0){
			key=I.DEVICE2.UNIT_ID;
		}else{
			key=I.DEVICE2.SERVICE_STATION;
		}
		StringBuilder sb=new StringBuilder();
		sb.append("SELECT * FROM(").append("select ROWNUM r,").append(I.DEVICE2.TABLE_NAME+".* from ")
		.append(I.DEVICE2.TABLE_NAME).append(" where ")
		.append(I.DEVICE2.CATEGROY).append("=? and ").append(key).append("=? and ")
		.append(typesql).append(I.DEVICE2.STATUS).append("=?)")
		.append(" WHERE r BETWEEN ? AND ?");
		ArrayList<DeviceResume> list=new ArrayList<>();
		try {
			ps=conn.prepareStatement(sb.toString());
			System.out.println(unit);
			String station;
			if(sType==1){
				station=Utils.getServiceStation(Integer.parseInt(unit));
			}else{
				station=Utils.getStation(Integer.parseInt(unit));
			}
			ps.setString(1, category);
			ps.setString(2,station );
			ps.setString(3, status);
			ps.setInt(4, page*10);
			ps.setInt(5,(page*10)+10 );
			System.out.println(sb.toString());
			rs=ps.executeQuery();
			while(rs.next()){
				DeviceResume deviceResume=new DeviceResume();
				deviceResume.setCheck_date((rs.getDate(I.DEVICE2.DATE_IN_CHECK)));
				deviceResume.setDid(rs.getString(I.DEVICE2.DID));
				deviceResume.setRemark(rs.getString(I.DEVICE2.REMARK));
				deviceResume.setScrap_date(rs.getDate(I.DEVICE2.OUT_DATE));
				deviceResume.setUse_date(rs.getDate(I.DEVICE2.USE_DATE));
				deviceResume.setUse_local(rs.getString(I.DEVICE2.USE_POSITION));
				deviceResume.setUse_unit(rs.getString(I.DEVICE2.STATION));
				list.add(deviceResume);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBUtils.closeAll(conn, ps, rs);
		}
		
		return list;
	}

	@Override
	public ArrayList<Battery> checkTimeOut(String unit) {
		Connection conn=DBUtils.getOConnection();
		ResultSet rs=null;
		PreparedStatement ps=null;
		String sql="select * from "+I.BATTERY.TABLENAME+" where "+I.BATTERY.UNIT_ID
				+" =? ";
		ArrayList<Battery> list=new ArrayList<>();
		try {
			
			ps=conn.prepareStatement(sql);
			ps.setString(1, unit);
			rs=ps.executeQuery();
			while(rs.next()){
				Battery b=new Battery();
				b.setDid(rs.getString(I.BATTERY.DID));
				b.setStart_time(rs.getLong(I.BATTERY.START_TIME));
				b.setStatus(rs.getString(I.BATTERY.STATUS));
				b.setTheory_duration(rs.getLong(I.BATTERY.THEORY_DURATION));
				b.setUnit_id(unit);
				b.setUsed_duration(rs.getLong(I.BATTERY.USED_DURATION));
				b.setType(rs.getString(I.BATTERY.TYPE));
				long usedtime=0;
				if(b.getStatus().equals("待用")){
					usedtime=b.getUsed_duration();
				}else{
					 usedtime=System.currentTimeMillis()-b.getStart_time()+b.getUsed_duration();
				}
				if(usedtime>b.getTheory_duration()){
					list.add(b);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public boolean inactive(String Did, String position) {
		Connection conn=DBUtils.getOConnection();
		PreparedStatement ps=null;
		String sql="update "+I.DEVICE2.TABLE_NAME+" set "+I.DEVICE2.STATUS+"=?,"
				+I.DEVICE2.USE_POSITION+"=? where "+I.DEVICE2.DID+"=?";
		try {
			ps=conn.prepareStatement(sql);
			ps.setString(1, "待用");
			ps.setString(2, position);
			ps.setString(3, Did);
			return ps.executeUpdate()==1;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBUtils.closeAll(conn, ps, null);
		}
		return false;
	}

	@Override
	public boolean updateNotice(String Nid, Notice notice) {
		Connection conn=DBUtils.getOConnection();
		PreparedStatement ps=null;
		String sql="delete from "+I.NOTICE.TABLENAME+" where "+I.NOTICE.NID+"=?";
		String sql2="insert into "+I.NOTICE.TABLENAME+" values(?,?,?,?)";
		String sql3="update "+I.ATTACHMENT.TABLENAME+" set "+I.ATTACHMENT.NID+"=? where "
				+I.ATTACHMENT.NID+"=?";
		int count=0;
		try {
			conn.setAutoCommit(false);
			ps=conn.prepareStatement(sql);
			ps.setLong(1, Long.parseLong(Nid));
			count+= ps.executeUpdate();
			ps.close();
			
			ps=conn.prepareStatement(sql2);
			ps.setLong(1,notice.getNid());
			ps.setString(2, notice.getTitle());
			java.sql.Date date=new Date(notice.getDate().getTime());
			ps.setDate(3, date);
			ps.setString(4,notice.getCommon());
			count+= ps.executeUpdate();
			ps.close();
			
			ps=conn.prepareStatement(sql3);
			ps.setLong(1, notice.getNid());
			ps.setLong(2, Long.parseLong(Nid));
			count+=ps.executeUpdate();
			conn.commit();
			return count==3;
	}catch (SQLException e) {
		e.printStackTrace();
		try {
			conn.rollback();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}finally{
		DBUtils.closeAll(conn, ps, null);
	}
		
		return false;
	}

	@Override
	public String getNoticeName(long nid) {
		PreparedStatement ps=null;
		Connection conn=DBUtils.getOConnection();
		ResultSet rs=null;
		String name=null;
		String sql="select "+I.NOTICE.TITLE+" from "+I.NOTICE.TABLENAME+" where "+I.NOTICE.NID+"=?";
		try {
			ps=conn.prepareStatement(sql);
			ps.setLong(1, nid);
			rs=ps.executeQuery();
			if(rs.next()){
				name=rs.getString(I.NOTICE.TITLE);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBUtils.closeAll(conn, ps, rs);
		}
		return name;
	}

	@Override
	public Notice getNoticeFromTitle(String title) {
		PreparedStatement ps=null;
		Connection conn=DBUtils.getOConnection();
		ResultSet rs=null;
		Notice notice=null;
		String sql="select * from "+I.NOTICE.TABLENAME+" where "+I.NOTICE.TITLE+"=?";
		try {
			ps=conn.prepareStatement(sql);
			ps.setString(1, title);
			rs=ps.executeQuery();
			if(rs.next()){
				notice=new Notice();
				notice.setCommon(rs.getString(I.NOTICE.COMMON));
				notice.setDate(rs.getDate(I.NOTICE.DATE));
				notice.setNid((rs.getLong(I.NOTICE.NID)));
				notice.setTitle(rs.getString(I.NOTICE.TITLE));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBUtils.closeAll(conn, ps, rs);
		}
		return notice;
	}

	

	@Override
	public Notice getNoticeFromId(long id) {
		PreparedStatement ps=null;
		Connection conn=DBUtils.getOConnection();
		ResultSet rs=null;
		Notice notice=null;
		String sql="select * from "+I.NOTICE.TABLENAME+" where "+I.NOTICE.NID+"=?";
		try {
			ps=conn.prepareStatement(sql);
			ps.setLong(1, id);
			rs=ps.executeQuery();
			if(rs.next()){
				notice=new Notice();
				notice.setCommon(rs.getString(I.NOTICE.COMMON));
				notice.setDate(rs.getDate(I.NOTICE.DATE));
				notice.setNid((rs.getLong(I.NOTICE.NID)));
				notice.setTitle(rs.getString(I.NOTICE.TITLE));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBUtils.closeAll(conn, ps, rs);
		}
		return notice;
	}

}

