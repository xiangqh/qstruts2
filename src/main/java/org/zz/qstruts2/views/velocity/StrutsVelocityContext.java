package org.zz.qstruts2.views.velocity;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;

/**
 * @author xiangqh
 *
 */
public class StrutsVelocityContext extends VelocityContext {

	private static final long serialVersionUID = 8497212428904436963L;
	HttpServletRequest stack;


    public StrutsVelocityContext(HttpServletRequest stack) {
        this.stack = stack;
    }


    public boolean internalContainsKey(Object key) {
        boolean contains = super.internalContainsKey(key);

        // first let's check to see if we contain the requested key
        if (contains) {
            return true;
        }

        // if not, let's search for the key in the ognl value stack
        if (stack != null) {
            Object o = stack.getAttribute(key.toString());

            if (o != null) {
                return true;
            }

        }

        // nope, i guess it's really not here
        return false;
    }

    public Object internalGet(String key) {
        // first, let's check to see if have the requested value
        if (super.internalContainsKey(key)) {
            return super.internalGet(key);
        }

        // still no luck?  let's look against the value stack
        if (stack != null) {
            Object object = stack.getAttribute(key);

            if (object != null) {
                return object;
            }

        }

        // nope, i guess it's really not here
        return null;
    }

}
