package org.zz.qstruts2.config;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.zz.qstruts2.annotations.RequestMapping;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * @author xiangqh
 *
 */
public class RuntimeProvider implements ConfigurationProvider {

	private static final Logger LOG = LoggerFactory.getLogger(RuntimeProvider.class);

	public static ArrayList<PackageConfig.Builder> list = new ArrayList<PackageConfig.Builder>();
	private Configuration configuration;

	@SuppressWarnings("rawtypes")
	public static void addPackageConfig(Class actionClazz, String actionName, String className) {
		PackageConfig.Builder cfg = new PackageConfig.Builder(className).namespace("/" + actionName).isAbstract(false)
				.location(null);

		Method[] methods = actionClazz.getDeclaredMethods();
		for (Method method : methods) {
			if (method.isAnnotationPresent(RequestMapping.class)) {
				RequestMapping mapping = method.getAnnotation(RequestMapping.class);
				String name = mapping.value();
				ActionConfig actionConfig = new ActionConfig.Builder(name, name, className)
						.methodName(method.getName()).addAllowedMethod(method.getName()).location(null).build();
				cfg.addActionConfig(name, actionConfig);
			}
		}
		list.add(cfg);
	}

	@Override
	public void destroy() {

	}

	@Override
	public void init(Configuration configuration) throws ConfigurationException {
		this.configuration = configuration;
	}

	@Override
	public boolean needsReload() {
		return false;
	}

	@Override
	public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {

	}

	@Override
	public void loadPackages() throws ConfigurationException {
		for (PackageConfig.Builder builder : list) {
			PackageConfig packageConfig = builder.build();
			if (configuration.getPackageConfig(packageConfig.getName()) == null) {
				configuration.addPackageConfig(packageConfig.getName(), packageConfig);
				LOG.info("load action package " + packageConfig.getNamespace() + " class " + packageConfig.getName()
						+ "......", "");
			}
		}
	}

}
