package deviceManagement.bean;


import java.io.Serializable;

import deviceManagement.util.I;



public class Result<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int retCode = -1;
	public String retMsg;
	public T retData;
	public Result() {
	}
	public boolean isSuccess() {
		return retCode== I.RESULT.SUCCESS;
	}

	public int getRetCode() {
		return retCode;
	}

	public void setRetCode(int retCode) {
		this.retCode = retCode;
	}

	public String getRetMsg() {
		return retMsg;
	}

	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}

	public T getRetData() {
		return retData;
	}

	public void setRetData(T retData) {
		this.retData = retData;
	}

	@Override
	public String toString() {
		return "Result [retCode=" + retCode + ", retMsg=" + retMsg + ", retData=" + retData + "]";
	}
}
