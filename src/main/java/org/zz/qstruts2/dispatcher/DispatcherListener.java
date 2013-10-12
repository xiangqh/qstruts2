package org.zz.qstruts2.dispatcher;

/**
 * A interface to tag those that want to execute code on the init and
 * destory of a Dispatcher.
 */
public interface DispatcherListener {

    /**
     * Called when the dispatcher is initialized
     *
     * @param du The dispatcher instance
     */
    public void dispatcherInitialized(Dispatcher du);

    /**
     * Called when the dispatcher is destroyed
     *
     * @param du The dispatcher instance
     */
    public void dispatcherDestroyed(Dispatcher du);
}
