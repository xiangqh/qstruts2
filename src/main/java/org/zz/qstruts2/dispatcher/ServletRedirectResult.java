package org.zz.qstruts2.dispatcher;

import static javax.servlet.http.HttpServletResponse.SC_FOUND;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.StrutsResultSupport;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.views.util.UrlHelper;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionException;
import com.opensymphony.xwork2.util.reflection.ReflectionExceptionHandler;

/**
 * <!-- START SNIPPET: description -->
 * 
 * Calls the {@link HttpServletResponse#sendRedirect(String) sendRedirect}
 * method to the location specified. The response is told to redirect the
 * browser to the specified location (a new request from the client). The
 * consequence of doing this means that the action (action instance, action
 * errors, field errors, etc) that was just executed is lost and no longer
 * available. This is because actions are built on a single-thread model. The
 * only way to pass data is through the session or with web parameters
 * (url?name=value) which can be OGNL expressions.
 * 
 * <!-- END SNIPPET: description -->
 * <p/>
 * <b>This result type takes the following parameters:</b>
 * 
 * <!-- START SNIPPET: params -->
 * 
 * <ul>
 * 
 * <li><b>location (default)</b> - the location to go to after execution.</li>
 * 
 * <li><b>parse</b> - true by default. If set to false, the location param will
 * not be parsed for Ognl expressions.</li>
 * 
 * <li><b>anchor</b> - Optional.  Also known as "fragment" or colloquially as 
 * "hash".  You can specify an anchor for a result.</li>
 * </ul>
 * 
 * <p>
 * This result follows the same rules from {@link StrutsResultSupport}.
 * </p>
 * 
 * <!-- END SNIPPET: params -->
 * 
 * <b>Example:</b>
 * 
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;!--
 *   The redirect URL generated will be:
 *   /foo.jsp#FRAGMENT
 * --&gt;
 * &lt;result name="success" type="redirect"&gt;
 *   &lt;param name="location"&gt;foo.jsp&lt;/param&gt;
 *   &lt;param name="parse"&gt;false&lt;/param&gt;
 *   &lt;param name="anchor"&gt;FRAGMENT&lt;/param&gt;
 * &lt;/result&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 * 
 */
public class ServletRedirectResult extends StrutsResultSupport implements ReflectionExceptionHandler {

    private static final long serialVersionUID = 6316947346435301270L;

    private static final Logger LOG = LoggerFactory.getLogger(ServletRedirectResult.class);

    protected boolean prependServletContext = true;
    protected ActionMapper actionMapper;
    protected int statusCode = SC_FOUND;
    protected boolean suppressEmptyParameters = false;
    protected Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
    protected String anchor;

    private UrlHelper urlHelper;

    public ServletRedirectResult() {
        super();
    }

    public ServletRedirectResult(String location) {
        this(location, null);
    }

    public ServletRedirectResult(String location, String anchor) {
        super(location);
        this.anchor = anchor;
    }

    @Inject
    public void setActionMapper(ActionMapper mapper) {
        this.actionMapper = mapper;
    }

    @Inject
    public void setUrlHelper(UrlHelper urlHelper) {
        this.urlHelper = urlHelper;
    }

    public void setStatusCode(int code) {
        this.statusCode = code;
    }

    /**
     * Set the optional anchor value.
     * 
     * @param anchor
     */
    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    /**
     * Sets whether or not to prepend the servlet context path to the redirected
     * URL.
     * 
     * @param prependServletContext <tt>true</tt> to prepend the location with the servlet context path, <tt>false</tt> otherwise.
     */
    public void setPrependServletContext(boolean prependServletContext) {
        this.prependServletContext = prependServletContext;
    }

    public void execute(ActionInvocation invocation) throws Exception {
        if (anchor != null) {
            anchor = conditionalParse(anchor, invocation);
        }
        super.execute(invocation);
    }

    /**
     * Redirects to the location specified by calling
     * {@link HttpServletResponse#sendRedirect(String)}.
     * 
     * @param finalLocation the location to redirect to.
     * @param invocation an encapsulation of the action execution state.
     * @throws Exception if an error occurs when redirecting.
     */
    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        ActionContext ctx = invocation.getInvocationContext();
        HttpServletRequest request = (HttpServletRequest) ctx.get(ServletActionContext.HTTP_REQUEST);
        HttpServletResponse response = (HttpServletResponse) ctx.get(ServletActionContext.HTTP_RESPONSE);

        if (isPathUrl(finalLocation)) {
            if (!finalLocation.startsWith("/")) {
                ActionMapping mapping = actionMapper.getMapping(request, Dispatcher.getInstance().getConfigurationManager());
                String namespace = null;
                if (mapping != null) {
                    namespace = mapping.getNamespace();
                }

                if ((namespace != null) && (namespace.length() > 0) && (!"/".equals(namespace))) {
                    finalLocation = namespace + "/" + finalLocation;
                } else {
                    finalLocation = "/" + finalLocation;
                }
            }

            // if the URL's are relative to the servlet context, append the servlet context path
            if (prependServletContext && (request.getContextPath() != null) && (request.getContextPath().length() > 0)) {
                finalLocation = request.getContextPath() + finalLocation;
            }

            ResultConfig resultConfig = invocation.getProxy().getConfig().getResults().get(invocation.getResultCode());
            if (resultConfig != null) {
                Map<String, String> resultConfigParams = resultConfig.getParams();

                for (Map.Entry<String, String> e : resultConfigParams.entrySet()) {
                    if (!getProhibitedResultParams().contains(e.getKey())) {
                        String potentialValue = e.getValue() == null ? "" : conditionalParse(e.getValue(), invocation);
                        if (!suppressEmptyParameters || ((potentialValue != null) && (potentialValue.length() > 0))) {
                            requestParameters.put(e.getKey(), potentialValue);
                        }
                    }
                }
            }

            StringBuilder tmpLocation = new StringBuilder(finalLocation);
            urlHelper.buildParametersString(requestParameters, tmpLocation, "&");

            // add the anchor
            if (anchor != null) {
                tmpLocation.append('#').append(anchor);
            }

            finalLocation = response.encodeRedirectURL(tmpLocation.toString());
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Redirecting to finalLocation " + finalLocation);
        }

        sendRedirect(response, finalLocation);
    }

    protected List<String> getProhibitedResultParams() {
        return Arrays.asList(DEFAULT_PARAM, "namespace", "method", "encode", "parse", "location", "prependServletContext", "suppressEmptyParameters", "anchor");
    }

    /**
     * Sends the redirection. Can be overridden to customize how the redirect is
     * handled (i.e. to use a different status code)
     * 
     * @param response The response
     * @param finalLocation The location URI
     * @throws IOException
     */
    protected void sendRedirect(HttpServletResponse response, String finalLocation) throws IOException {
        if (SC_FOUND == statusCode) {
            response.sendRedirect(finalLocation);
        } else {
            response.setStatus(statusCode);
            response.setHeader("Location", finalLocation);
            response.getWriter().write(finalLocation);
            response.getWriter().close();
        }

    }

    private boolean isPathUrl(String url) {
        // filter out "http:", "https:", "mailto:", "file:", "ftp:"
        return !url.startsWith("http:")
                && !url.startsWith("https:")
                && !url.startsWith("mailto:")
                && !url.startsWith("file:")
                && !url.startsWith("ftp:");
    }

    /**
     * Sets the suppressEmptyParameters option
     * 
     * @param suppressEmptyParameters The new value for this option
     */
    public void setSuppressEmptyParameters(boolean suppressEmptyParameters) {
        this.suppressEmptyParameters = suppressEmptyParameters;
    }

    /**
     * Adds a request parameter to be added to the redirect url
     * 
     * @param key The parameter name
     * @param value The parameter value
     */
    public ServletRedirectResult addParameter(String key, Object value) {
        requestParameters.put(key, String.valueOf(value));
        return this;
    }

    public void handle(ReflectionException ex) {
        // Only log as debug as they are probably parameters to be appended to the url
        if (LOG.isDebugEnabled()) {
            LOG.debug(ex.getMessage(), ex);
        }
    }

}