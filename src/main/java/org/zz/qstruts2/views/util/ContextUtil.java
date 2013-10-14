package org.zz.qstruts2.views.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.util.StrutsUtil;
import org.apache.struts2.views.jsp.ui.OgnlTool;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Container;

/**
 * @author xiangqh
 *
 */
public class ContextUtil {

	public static final String REQUEST = "request";
    public static final String REQUEST2 = "request";
    public static final String RESPONSE = "response";
    public static final String RESPONSE2 = "response";
    public static final String SESSION = "session";
    public static final String BASE = "base";
    public static final String STACK = "stack";
    public static final String OGNL = "ognl";
    public static final String STRUTS = "struts";
    public static final String ACTION = "action";

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map getStandardContext(ActionContext ac, HttpServletRequest req, HttpServletResponse res) {
        HashMap map = new HashMap();
        map.put(REQUEST, req);
        map.put(REQUEST2, req);
        map.put(RESPONSE, res);
        map.put(RESPONSE2, res);
        map.put(SESSION, req.getSession(false));
        map.put(BASE, req.getContextPath());
        map.put(STACK, ac.getValueStack());
        map.put(OGNL, ac.getInstance(OgnlTool.class));
        map.put(STRUTS, new StrutsUtil(ac.getValueStack(), req, res));

        ActionInvocation invocation = (ActionInvocation) ac.get(ActionContext.ACTION_INVOCATION);
        if (invocation != null) {
            map.put(ACTION, invocation.getAction());
        }
        return map;
    }

    /**
     * Return true if either Configuration's altSyntax is on or the stack context's useAltSyntax is on
     * @param context stack's context
     * @return boolean
     */
    @SuppressWarnings("rawtypes")
	public static boolean isUseAltSyntax(Map context) {
        // We didn't make altSyntax static cause, if so, struts.configuration.xml.reload will not work
        // plus the Configuration implementation should cache the properties, which the framework's
        // configuration implementation does
        return "true".equals(((Container)context.get(ActionContext.CONTAINER)).getInstance(String.class, StrutsConstants.STRUTS_TAG_ALTSYNTAX)) ||(
                (context.containsKey("useAltSyntax") &&
                        context.get("useAltSyntax") != null &&
                        "true".equals(context.get("useAltSyntax").toString())));
    }

    /**
     * Returns a String for overriding the default templateSuffix if templateSuffix is on the stack
     * @param context stack's context
     * @return String
     */
    @SuppressWarnings("rawtypes")
	public static String getTemplateSuffix(Map context) {
        return context.containsKey("templateSuffix") ? (String) context.get("templateSuffix") : null;
    }

}
