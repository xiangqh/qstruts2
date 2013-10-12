package org.zz.qstruts2.views.velocity.directive;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.struts2.ServletActionContext;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.zz.qstruts2.utils.ReflectUtils;

/**
 * @author xiangqh
 *
 */
public abstract class ModuleDirective extends Directive {

	public abstract Object getAction(String moduleName);

	public abstract String getMethodName(String moduleName);

	@Override
	public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException,
			ResourceNotFoundException, ParseErrorException, MethodInvocationException {
		Map<String, String> params = getParameters(context, node);

		VelocityContext vContext = new VelocityContext();
		try {

			Object parameters = params.get("parameters");
			if (parameters != null) {
				setParem(params);
			}

			Object action = getAction(params.get("moduleName"));
			if (action != null) {
				Method method = ReflectUtils.getMethod(getMethodName(params.get("moduleName")), action.getClass());
				ReflectUtils.invokeMethod(method, action, new Object[] { context, params, vContext });
			}

			String path = params.get("path").toString();
			context.pushCurrentTemplateName(path);
			Properties properties = new Properties();
			String basePath = ServletActionContext.getServletContext().getRealPath("/");
			properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, basePath);
			properties.setProperty(Velocity.ENCODING_DEFAULT, "utf-8");
			properties.setProperty(Velocity.INPUT_ENCODING, "utf-8");
			properties.setProperty(Velocity.OUTPUT_ENCODING, "utf-8");
			VelocityEngine engine = new VelocityEngine();
			engine.init(properties);
			Template template = engine.getTemplate(context.getCurrentTemplateName());
			StringWriter wri = new StringWriter();
			template.merge(vContext, wri);
			writer.write(wri.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	protected void setParem(Map<String, String> paraMap) throws ParseErrorException, MethodInvocationException {

		String parameters = paraMap.get("parameters").trim();
		if (!(parameters.startsWith("{") && parameters.endsWith("}"))) {
			throw new IllegalStateException("parameters illegal");
		}
		String[] params = parameters.substring(1, parameters.length() - 1).split(",");
		for (String param : params) {
			paraMap.put(param.split(":")[0].trim(), param.split(":")[1].trim());
		}
	}

	@SuppressWarnings("unchecked")
	protected Map<String, String> getParameters(InternalContextAdapter contextAdapter, Node node)
			throws ParseErrorException, MethodInvocationException {

		Map<String, String> paremMap = null;
		Node fChild = null;
		Object fValue = null;
		int paNum = node.jjtGetNumChildren();
		if (getType() == BLOCK) {
			paNum--;
		}
		if (paNum == 1 && null != (fChild = node.jjtGetChild(0)) && null != (fValue = fChild.value(contextAdapter))
				&& fValue instanceof Map) {
			paremMap = (Map<String, String>) fValue;
		} else {
			paremMap = new HashMap<String, String>();
			for (int index = 0, length = paNum; index < length; index++) {
				setParameter(paremMap, contextAdapter, node.jjtGetChild(index));
			}
		}

		return paremMap;
	}

	protected void setParameter(Map<String, String> propertyMap, InternalContextAdapter contextAdapter, Node node)
			throws ParseErrorException, MethodInvocationException {
		String param = node.value(contextAdapter).toString();
		int index = param.indexOf("=");
		if (index != -1) {
			String key = param.substring(0, index);
			String value = param.substring(index + 1);
			propertyMap.put(key, value);
		}
	}

}
