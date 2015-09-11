package org.gumtree.service.httpclient;

import java.net.URI;

import org.gumtree.core.object.IDisposable;

public interface IHttpClient extends IDisposable {

	public void performGet(URI uri, IHttpClientCallback callback);
	
	public void performGet(final URI uri, final IHttpClientCallback callback, final String username, final String passwd);
	
	public void setProxy(String hostName, int port);
}
