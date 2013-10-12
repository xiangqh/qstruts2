package org.zz.qstruts2.dispatcher.ng.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.ng.listener.ListenerHostConfig;
import org.zz.qstruts2.dispatcher.Dispatcher;
import org.zz.qstruts2.dispatcher.ng.InitOperations;
import org.zz.qstruts2.dispatcher.ng.PrepareOperations;

/**
 * Servlet listener for Struts.  The preferred way to use Struts is as a filter via the
 * {@link org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter} and its variants.
 * This might be useful if Struts config information is needed from other servlet listeners, like
 * Sitemesh or OSGi 
 */
public class StrutsListener implements ServletContextListener {
    private PrepareOperations prepare;

    public void contextInitialized(ServletContextEvent sce) {
       InitOperations init = new InitOperations();
        try {
            ListenerHostConfig config = new ListenerHostConfig(sce.getServletContext());
            init.initLogging(config);
            Dispatcher dispatcher = init.initDispatcher(config);
            init.initStaticContentLoader(config, dispatcher);

            prepare = new PrepareOperations(config.getServletContext(), dispatcher);
            sce.getServletContext().setAttribute(StrutsStatics.SERVLET_DISPATCHER, dispatcher);
        } finally {
            init.cleanup();
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        prepare.cleanupDispatcher();
    }
}