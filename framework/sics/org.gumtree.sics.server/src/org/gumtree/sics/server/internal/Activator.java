package org.gumtree.sics.server.internal;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.gumtree.sics.core.ISicsManager;
import org.gumtree.sics.io.ISicsProxy.ProxyState;
import org.gumtree.util.eclipse.EclipseUtils;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;
	
	private static Activator instance;

	private IEclipseContext eclipseContext;
	
	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		instance = this;
		try {
			SicsStarter starter = ContextInjectionFactory.make(SicsStarter.class,
					getEclipseContext());
			ISicsManager sicsManager = starter.getSicsManager();
			connect(sicsManager);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void connect(final ISicsManager sicsManager) {
		Thread connectionMonitor = new Thread(new Runnable() {
			public void run() {
				while (sicsManager.getProxy().getProxyState().equals(ProxyState.DISCONNECTED)) {
					try {
						sicsManager.getServerController().getServerStatus();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			}
		});
		connectionMonitor.start();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		if (eclipseContext != null) {
			eclipseContext.dispose();
			eclipseContext = null;
		}
		instance = null;
		Activator.context = null;
	}

	public IEclipseContext getEclipseContext() {
		if (eclipseContext == null) {
			eclipseContext = EclipseUtils.createEclipseContext(context.getBundle());
		}
		return eclipseContext;
	}
	
	public static Activator getDefault() {
		return instance;
	}
	
}
