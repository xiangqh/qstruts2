package org.zz.qstruts2.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author xiangqh
 *
 */
public class ReflectUtils {

	private static Log log = LogFactory.getLog(ReflectUtils.class);

	@SuppressWarnings("rawtypes")
	public static Set<Class> getInterfaces(Class<?> clazz) {
		Set<Class> iterfaces = new LinkedHashSet<Class>();
		iterfaces.addAll(Arrays.asList(clazz.getInterfaces()));
		for (Class superClass : getSuperClasses(clazz)) {
			iterfaces.addAll(Arrays.asList(superClass.getInterfaces()));
		}
		return iterfaces;
	}

	@SuppressWarnings("rawtypes")
	public static Set<Class> getSuperClasses(Class clazz) {
		Set<Class> superClasses = new LinkedHashSet<Class>();
		while (clazz.getSuperclass() != null && !Object.class.equals(clazz.getSuperclass())) {
			superClasses.add(clazz.getSuperclass());
			clazz = clazz.getSuperclass();
		}
		return superClasses;
	}

	@SuppressWarnings("rawtypes")
	public static Method getMethod(String methodName, Class clazz) {
		if (StringUtils.isBlank(methodName))
			return null;
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			if (methodName.equalsIgnoreCase(method.getName())) {
				return method;
			}
		}
		return null;
	}

	public static Object invokeMethod(Method method, Object bean, Object... parameters) {
		boolean accessible = method.isAccessible();
		try {
			if (!accessible) {
				method.setAccessible(true);
			}
			return method.invoke(bean, parameters);
		} catch (IllegalAccessException e) {
			String message = "Invoke method [" + method + "] failed";
			if (log.isDebugEnabled()) {
				log.debug(message, e);
			}
			throw new RuntimeException(message, e);
		} catch (InvocationTargetException e) {
			String message = "Invoke method [" + method + "] failed";
			if (log.isDebugEnabled()) {
				log.debug(message, e.getTargetException());
			}
			throw new RuntimeException(message, e.getTargetException());
		} finally {
			method.setAccessible(accessible);
		}
	}
}
