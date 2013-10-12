package org.zz.qstruts2.dispatcher.ng;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.RequestUtils;
import org.apache.struts2.dispatcher.StaticContentLoader;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import org.zz.qstruts2.dispatcher.Dispatcher;

/**
 * Contains execution operations for filters
 */
public class ExecuteOperations {
    private ServletContext servletContext;
    private Dispatcher dispatcher;

    public ExecuteOperations(ServletContext servletContext, Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.servletContext = servletContext;
    }

    /**
     * Tries to execute a request for a static resource
     * @return True if it was handled, false if the filter should fall through
     * @throws IOException
     * @throws ServletException
     */
    public boolean executeStaticResourceRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // there is no action in this request, should we look for a static resource?
        String resourcePath = RequestUtils.getServletPath(request);

        if ("".equals(resourcePath) && null != request.getPathInfo()) {
            resourcePath = request.getPathInfo();
        }

        StaticContentLoader staticResourceLoader = dispatcher.getContainer().getInstance(StaticContentLoader.class);
        if (staticResourceLoader.canHandle(resourcePath)) {
            staticResourceLoader.findStaticResource(resourcePath, request, response);
            // The framework did its job here
            return true;

        } else {
            // this is a normal request, let it pass through
            return false;
        }
    }

    /**
     * Executes an action
     * @throws ServletException
     */
    public void executeAction(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ServletException {
        dispatcher.serviceAction(request, response, servletContext, mapping);
    }
}