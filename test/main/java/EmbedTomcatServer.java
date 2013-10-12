
import java.io.File;

import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.startup.Tomcat;

import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * @author xiangqh
 *
 */
public class EmbedTomcatServer {

	/**
	 * @param args
	 * @throws ServletException
	 */
	public static void main(String[] args) throws Exception {
		String application  = "qstruts2";
		int httpPort = 8081;
		if(args.length == 2) {
			httpPort = Integer.valueOf(args[1]);
		}

		Tomcat tomcat = new Tomcat();
		tomcat.setPort(httpPort);

		 // Add AprLifecycleListener
        AprLifecycleListener aprLifecycleListener = new AprLifecycleListener();
        StandardServer server = (StandardServer) tomcat.getServer();
        server.addLifecycleListener(aprLifecycleListener);
        tomcat.addWebapp("/" + application, new File("web").getAbsolutePath());
        tomcat.start();
        server.await();

	}

}
