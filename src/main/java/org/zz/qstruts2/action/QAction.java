package org.zz.qstruts2.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.zz.qstruts2.upload.UploadBean;

/**
 * @author xiangqh
 * 
 */
public class QAction {

	private static final long serialVersionUID = -187235989739919273L;

	public HttpServletRequest getHttpServletRequest() {
		return ServletActionContext.getRequest();
	}

	public HttpServletResponse getHttpServletResponse() {
		return ServletActionContext.getResponse();
	}

	public String getHttpParameter(String param) {
		HttpServletRequest req = getHttpServletRequest();
		if (req != null) {
			return req.getParameter(param);
		}
		return null;
	}

	public String[] getHttpParameters(String param) {
		HttpServletRequest req = getHttpServletRequest();
		if (req != null) {
			return req.getParameterValues(param);
		}
		return null;
	}

	public void setHttpAttribute(String key, Object value) {
		HttpServletRequest req = getHttpServletRequest();
		if (req != null) {
			req.setAttribute(key, value);
		}
	}

	public Long getParameterLong(String key) {
		String temp = getHttpParameter(key);
		if (temp == null) {
			return null;
		}
		try {
			return Long.parseLong(temp);
		} catch (Exception e) {

		}
		return null;
	}

	public int getParameterInt(String key) {
		String temp = getHttpParameter(key);
		if (temp == null) {
			return 0;
		}
		try {
			return Integer.parseInt(temp);
		} catch (Exception e) {

		}
		return 0;
	}

	public Integer getParameterInteger(String key) {
		String temp = getHttpParameter(key);
		if (temp == null) {
			return null;
		}
		try {
			return Integer.parseInt(temp);
		} catch (Exception e) {

		}
		return null;
	}

	public BigDecimal getParameterPrice(String key) {
		String temp = getHttpParameter(key);
		if (temp == null) {
			return null;
		}
		try {
			return new BigDecimal(temp);
		} catch (Exception e) {

		}
		return null;
	}

	public Double getParameterDouble(String key) {
		String temp = getHttpParameter(key);
		if (temp == null) {
			return null;
		}
		try {
			return Double.parseDouble(temp);
		} catch (Exception e) {

		}
		return null;
	}

	public List<UploadBean> getUpLoadFiles() {
		List<UploadBean> list = new ArrayList<UploadBean>();
		HttpServletRequest requset = getHttpServletRequest();
		if (requset instanceof MultiPartRequestWrapper) {
			MultiPartRequestWrapper request = (MultiPartRequestWrapper) getHttpServletRequest();
			Enumeration<String> fileNames = request.getFileParameterNames();
			while (fileNames.hasMoreElements()) {
				String fieldName = fileNames.nextElement();
				UploadBean uploadBean = new UploadBean();
				uploadBean.setFiles(request.getFiles(fieldName));
				uploadBean.setFileNames(request.getFileNames(fieldName));
				uploadBean.setContentTypes(request.getContentTypes(fieldName));
				list.add(uploadBean);
			}
		}
		return list;
	}

}
