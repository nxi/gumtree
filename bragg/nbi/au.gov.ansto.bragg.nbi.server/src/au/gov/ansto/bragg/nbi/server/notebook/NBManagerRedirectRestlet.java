/**
 * 
 */
package au.gov.ansto.bragg.nbi.server.notebook;

import org.gumtree.core.object.IDisposable;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;

/**
 * @author nxi
 *
 */
public class NBManagerRedirectRestlet extends Restlet implements IDisposable {

	/**
	 * 
	 */
	public NBManagerRedirectRestlet() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 */
	public NBManagerRedirectRestlet(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void handle(Request request, Response response) {
		// TODO Auto-generated method stub
		response.redirectPermanent("../notebookAdmin.html");
	}
	
	/* (non-Javadoc)
	 * @see org.gumtree.core.object.IDisposable#disposeObject()
	 */
	@Override
	public void disposeObject() {
		// TODO Auto-generated method stub

	}

}
