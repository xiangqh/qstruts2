package org.zz.qstruts2.action;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

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

	public Integer getParameterInt(String key) {
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
}
