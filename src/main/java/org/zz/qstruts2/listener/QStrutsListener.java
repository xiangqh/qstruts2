package org.zz.qstruts2.listener;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.zz.qstruts2.annotations.ActionController;
import org.zz.qstruts2.config.RuntimeProvider;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * @author xiangqh
 *
 */
public class QStrutsListener implements ServletContextListener {

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(QStrutsListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {

		ServletContext sc = sce.getServletContext();

		String actionPackage = sc.getInitParameter("actionPackage");
		if (actionPackage == null) {
			actionPackage = "";
		}
		String[] packageNames = actionPackage.split(",");
		for (String packageName : packageNames) {
			String packageDirName = packageName.replace('.', '/');
			URL url = QStrutsListener.class.getClassLoader().getResource(packageDirName);
			if (url == null)
				return;
			String protocol = url.getProtocol();
			if ("file".equals(protocol)) {
				parseFile(url, packageName);
			} else if ("jar".equals(protocol)) {
				parseJar(url, packageDirName);
			}
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

	private void parseFile(URL url, final String packageName) {
		File file = null;
		try {
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		file.listFiles(new FileFilter() {
			public boolean accept(File file) {
				String fileName = file.getName();
				if (fileName.endsWith(".class") && !fileName.contains("$")) {
					parseClass(packageName + "." + fileName.substring(0, file.getName().length() - 6));
				} else if (file.isDirectory()) {
					try {
						parseFile(file.toURI().toURL(), packageName + "." + fileName);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
				return false;
			}
		});
	}

	private void parseJar(URL url, String packageName) {
		Enumeration<JarEntry> entries = null;
		try {
			entries = ((JarURLConnection) url.openConnection()).getJarFile().entries();

		} catch (IOException e) {
			e.printStackTrace();
		}
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String fileName = entry.getName();
			if (!fileName.endsWith(".class") || fileName.contains("$") || !fileName.startsWith(packageName + "/"))
				continue;
			parseClass(fileName.substring(0, fileName.length() - 6).replace("/", "."));
		}

	}

	private void parseClass(String className) {
		Class<?> clazz = null;
		try {
			clazz = QStrutsListener.class.getClassLoader().loadClass(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		if (clazz != null && clazz.isAnnotationPresent(ActionController.class)) {
			ActionController controller = clazz.getAnnotation(ActionController.class);
			String actionName = controller.value();
			RuntimeProvider.addPackageConfig(clazz, actionName, className);
		}

	}

}
