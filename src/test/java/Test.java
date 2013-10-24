import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.zz.qstruts2.annotations.ActionController;
import org.zz.qstruts2.annotations.RequestMapping;



/**
 * @author xiangqh
 *
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String packageName = "example";
		String packageDirName = packageName.replace('.', '/');
		URL url = Test.class.getClassLoader().getResource(packageDirName);
		if(url == null) return;
		String protocol = url.getProtocol();
		if ("file".equals(protocol)) {
			parseFile(url, packageName);
		} else if ("jar".equals(protocol)) {
			parseJar(url, packageName);
		}
	}

	private static void parseFile(URL url, final String packageName) {
		File file = null;
		try {
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		file.listFiles(new FileFilter() {
			public boolean accept(File file) {
				String fileName = file.getName();
				if(fileName.endsWith(".class") && !fileName.contains("$")) {
					parseClass(packageName+"."+fileName.substring(0,file.getName().length() - 6));

				}else if(file.isDirectory()) {
					try {
						parseFile(file.toURI().toURL(),packageName+"."+fileName);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
				return false;
			}
		});
	}

	private static void parseJar(URL url, String packageDirName) {

	}

	private static void parseClass(String className) {
		Class<?> clazz = null;
		try {
			clazz = Test.class.getClassLoader().loadClass(className);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(clazz!=null && clazz.isAnnotationPresent(ActionController.class)) {
			Method[] methods = clazz.getDeclaredMethods();
			for(Method method : methods) {
				if(method.isAnnotationPresent(RequestMapping.class)) {
					RequestMapping mapping = method.getAnnotation(RequestMapping.class);
					System.out.println(className +":" +mapping.value());
				}
			}

		}

	}

}
