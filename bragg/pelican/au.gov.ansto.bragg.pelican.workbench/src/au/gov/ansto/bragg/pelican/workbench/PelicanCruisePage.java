package au.gov.ansto.bragg.pelican.workbench;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.gumtree.ui.cruise.ICruisePanelPage;

public class PelicanCruisePage implements ICruisePanelPage {

	@Override
	public String getName() {
		return "Pelican";
	}

	@Override
	public Composite createNormalWidget(Composite parent) {
		return new PelicanCruisePageWidget(parent, SWT.NONE);
	}

	@Override
	public Composite createFullWidget(Composite parent) {
		return new PelicanCruisePageWidget(parent, SWT.NONE);
	}

}