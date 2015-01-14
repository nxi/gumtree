package au.gov.ansto.bragg.taipan.ui.align;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.gumtree.data.ui.viewers.PlotViewer;
import org.gumtree.ui.scripting.viewer.CommandLineViewer;
import org.gumtree.ui.scripting.viewer.ICommandLineViewer;

import au.gov.ansto.bragg.nbi.ui.scripting.ScriptPageRegister;
import au.gov.ansto.bragg.nbi.ui.scripting.parts.ScriptControlViewer;
import au.gov.ansto.bragg.nbi.ui.scripting.parts.ScriptDataSourceViewer;
import au.gov.ansto.bragg.nbi.ui.scripting.parts.ScriptInfoViewer;

public class TaipanCalibrationViewer extends Composite {

	private static final String CALIBRATION_SCRIPT_NAME = "gumtree.taipan.calibrationScript";
	
	private ScriptControlViewer controlViewer;
	private CommandLineViewer consoleViewer;
	private ScriptDataSourceViewer dataSourceViewer;
	private ScriptInfoViewer infoViewer;
	
	public TaipanCalibrationViewer(Composite parent, int style) {
		super(parent, style);
		GridLayoutFactory.fillDefaults().applyTo(this);
		ScriptPageRegister register = new ScriptPageRegister();
		
		SashForm level1Form = new SashForm(this, SWT.HORIZONTAL);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(level1Form);
		
		SashForm level2Left = new SashForm(level1Form, SWT.VERTICAL);
		SashForm level2Right = new SashForm(level1Form, SWT.VERTICAL);
		level1Form.setWeights(new int[]{5, 5});

		createControlArea(level2Left);
		level2Left.setWeights(new int[]{7, 4});
		
		SashForm level3Form = new SashForm(level2Right, SWT.HORIZONTAL);
		dataSourceViewer = new ScriptDataSourceViewer(level3Form, SWT.NONE);
		SashForm level4Form = new SashForm(level3Form, SWT.VERTICAL);
		PlotViewer plot1Viewer = new PlotViewer(level4Form, SWT.NONE);
//		PlotViewer plot2Viewer = new PlotViewer(level4Form, SWT.NONE);
		infoViewer = new ScriptInfoViewer(level4Form, SWT.NONE);
		level4Form.setWeights(new int[]{3, 2});
		level3Form.setWeights(new int[]{3, 7});
		PlotViewer plot2Viewer = new PlotViewer(level2Right, SWT.NONE);
		level2Right.setWeights(new int[]{7, 4});
		
		ScriptPageRegister.registPage(controlViewer.getScriptRegisterID(), register);
		register.setControlViewer(controlViewer);
		register.setConsoleViewer(consoleViewer);
		register.setDataSourceViewer(dataSourceViewer);
		register.setInfoViewer(infoViewer);
		register.registerObject("Plot1", plot1Viewer);
		register.registerObject("Plot2", plot2Viewer);
//		register.registerObject("Plot3", plot3Viewer);
		controlViewer.runNativeInitScript();
		controlViewer.loadScript(ScriptControlViewer.getFullScriptPath(System.getProperty(CALIBRATION_SCRIPT_NAME)));
	}

	private void createControlArea(SashForm level2Left) {
		// TODO Auto-generated method stub
		CommandHandler commandHandler = new CommandHandler();
		consoleViewer = new CommandLineViewer();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		consoleViewer.setScriptExecutor(commandHandler.getScriptExecutor());
		controlViewer = new ScriptControlViewer(level2Left, SWT.NONE);
		controlViewer.setScriptExecutor(commandHandler.getScriptExecutor());
		Color whiteColor = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
		controlViewer.setBackground(whiteColor);
		Label titleLabel = new Label(controlViewer, SWT.NONE);
		titleLabel.setBackground(whiteColor);
		FontData[] fD = titleLabel.getFont().getFontData();
		fD[0].setHeight(18);
		titleLabel.setFont(new Font(titleLabel.getDisplay(), fD));
		titleLabel.setText(" Taipan Calibration");
		controlViewer.getScrollArea().moveAbove(controlViewer.getStaticComposite());
		titleLabel.moveAbove(controlViewer.getScrollArea());
		controlViewer.getStaticComposite().setVisible(false);
		controlViewer.getStaticComposite().dispose();
		GridLayoutFactory.fillDefaults().applyTo(controlViewer);
//		GridDataFactory.fillDefaults().grab(true, true).applyTo(controlViewer);
		
		Composite consoleComposite = new Composite(level2Left, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(consoleComposite);
//		GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 180).applyTo(consoleComposite);
		consoleViewer.createPartControl(consoleComposite, ICommandLineViewer.NO_UTIL_AREA);
	}

	
}