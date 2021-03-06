package au.gov.ansto.bragg.quokka.msw.composites;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.wb.swt.SWTResourceManager;
import org.gumtree.msw.elements.IElementListListener;
import org.gumtree.msw.ui.IModelValueConverter;
import org.gumtree.msw.ui.ModelBinder;
import org.gumtree.msw.ui.Resources;
import org.gumtree.msw.ui.ktable.ButtonInfo;
import org.gumtree.msw.ui.ktable.CheckableCellRenderer;
import org.gumtree.msw.ui.ktable.ElementTableModel;
import org.gumtree.msw.ui.ktable.ElementTableModel.ColumnDefinition;
import org.gumtree.msw.ui.ktable.IButtonListener;
import org.gumtree.msw.ui.ktable.NameCellRenderer;
import org.gumtree.msw.ui.observable.IProxyElementListener;
import org.gumtree.msw.ui.observable.ProxyElement;

import au.gov.ansto.bragg.quokka.msw.Configuration;
import au.gov.ansto.bragg.quokka.msw.ConfigurationList;
import au.gov.ansto.bragg.quokka.msw.Measurement;
import au.gov.ansto.bragg.quokka.msw.ModelProvider;
import au.gov.ansto.bragg.quokka.msw.converters.CountValueConverter;
import au.gov.ansto.bragg.quokka.msw.converters.IndexValueConverter;
import au.gov.ansto.bragg.quokka.msw.converters.TimeValueConverter;
import au.gov.ansto.bragg.quokka.msw.schedule.CustomInstrumentAction;
import au.gov.ansto.bragg.quokka.msw.util.ConfigurationCatalogDialog;
import org.gumtree.msw.ui.ktable.KTable;
import org.gumtree.msw.ui.ktable.KTableCellEditor;
import org.gumtree.msw.ui.ktable.SWTX;
import org.gumtree.msw.ui.ktable.editors.KTableCellEditorCheckbox;
import org.gumtree.msw.ui.ktable.editors.KTableCellEditorText2;
import org.gumtree.msw.ui.ktable.renderers.DefaultCellRenderer;
import org.gumtree.msw.ui.ktable.renderers.TextCellRenderer;

public class ConfigurationsComposite extends Composite {
	// fields
	private final KTable tblConfigurations;
	private final Menu menu;
	private ConfigurationList configurationList;
	private Text txtName;
	private Text txtInitializeScript;
	private Text txtPretransmissionScript;
	private Text txtPrescatteringScript;
	private Button btnInitializeScriptApply;
	private Button btnInitializeScriptTestDrive;
	private Button btnPretransmissionScriptApply;
	private Button btnPretransmissionScriptTestDrive;
	private Button btnPrescatteringScriptApply;
	private Button btnPrescatteringScriptTestDrive;

	private Composite cmpTransmission;
	private Combo cmbTransmissionAttAlgo;
	private Combo cmbTransmissionAttAngle;
	private Label lblTransmissionMaxTime;
	private Text txtTransmissionMaxTime;
	private Label lblTransmissionMaxTimeUnit;
	
	private Composite cmpScattering;
	private Combo cmbScatteringAttAlgo;
	private Combo cmbScatteringAttAngle;
	private Label lblScatteringMaxTime;
	private Text txtScatteringMaxTime;
	private Label lblScatteringMaxTimeUnit;
	
	private Composite cmpExpand;
	private Button btnExpand;
	private Label lblExpand;
	
	// construction
	public ConfigurationsComposite(Composite parent, ModelProvider provider) {
		super(parent, SWT.BORDER);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 10;
		gridLayout.marginHeight = 10;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));

		Composite cmpContent = new Composite(this, SWT.NONE);
		cmpContent.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true, 1, 1));
		cmpContent.setLayout(new GridLayout(2, false));
		cmpContent.setBackground(getBackground());

		Composite cmpLeft = new Composite(cmpContent, SWT.NONE);
		cmpLeft.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
		GridLayout gl_cmpLeft = new GridLayout(1, false);
		gl_cmpLeft.verticalSpacing = 0;
		gl_cmpLeft.horizontalSpacing = 0;
		gl_cmpLeft.marginWidth = 0;
		gl_cmpLeft.marginHeight = 0;
		cmpLeft.setLayout(gl_cmpLeft);
		cmpLeft.setBackground(getBackground());
		
		
		tblConfigurations = new KTable(cmpLeft, SWTX.EDIT_ON_KEY | SWT.V_SCROLL | SWT.H_SCROLL);
		tblConfigurations.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		tblConfigurations.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

		
		Composite cmpRight = new Composite(cmpContent, SWT.NONE);
		GridData gd_cmpRight = new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1);
		gd_cmpRight.widthHint = 560;
		cmpRight.setLayoutData(gd_cmpRight);
		GridLayout gl_cmpRight = new GridLayout(2, true);
		gl_cmpRight.marginHeight = 0;
		gl_cmpRight.marginWidth = 0;
		cmpRight.setLayout(gl_cmpRight);
		cmpRight.setBackground(getBackground());
		
		Group grpConfiguration = new Group(cmpRight, SWT.NONE);
		grpConfiguration.setLayout(new GridLayout(1, false));
		grpConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		grpConfiguration.setText("Configuration");
		
		Composite cmpConfiguration = new Composite(grpConfiguration, SWT.NONE);
		cmpConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl_cmpConfiguration = new GridLayout(5, false);
		gl_cmpConfiguration.marginWidth = 0;
		gl_cmpConfiguration.marginHeight = 0;
		cmpConfiguration.setLayout(gl_cmpConfiguration);
		cmpConfiguration.setBackground(getBackground());
		

		Label lblName = new Label(cmpConfiguration, SWT.NONE);
		lblName.setText("Name:");

		txtName = new Text(cmpConfiguration, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Label lblGroup = new Label(cmpConfiguration, SWT.NONE);
		lblGroup.setText("Group:");

		Combo cmbGroup = new Combo(cmpConfiguration, SWT.BORDER);
		cmbGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite cmpButtons = new Composite(cmpConfiguration, SWT.DROP_DOWN);
		cmpButtons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		GridLayout gl_cmpButtons = new GridLayout(2, true);
		gl_cmpButtons.marginWidth = 0;
		gl_cmpButtons.marginHeight = 0;
		cmpButtons.setLayout(gl_cmpButtons);
		cmpButtons.setBackground(getBackground());

		Button btnReset = new Button(cmpButtons, SWT.NONE);
		btnReset.setToolTipText("reset this configuration to its original state");
		GridData gd_btnReset = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_btnReset.heightHint = 21;
		btnReset.setLayoutData(gd_btnReset);
		btnReset.setImage(Resources.IMAGE_RESET);
		btnReset.setText("Reset");

		Button btnSave = new Button(cmpButtons, SWT.NONE);
		btnSave.setToolTipText("save this configuration in specified group");
		GridData gd_btnSave = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_btnSave.heightHint = 21;
		btnSave.setLayoutData(gd_btnSave);
		btnSave.setImage(Resources.IMAGE_DISK);
		btnSave.setText("Save");
		
		
		Group grpScripts = new Group(cmpRight, SWT.NONE);
		grpScripts.setLayout(new GridLayout(1, false));
		GridData gd_grpScripts = new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1);
		gd_grpScripts.minimumHeight = 200;
		grpScripts.setLayoutData(gd_grpScripts);
		grpScripts.setText("Scripts");
		
		Composite cmpScripts = new Composite(grpScripts, SWT.NONE);
		cmpScripts.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl_cmpScripts = new GridLayout(1, false);
		gl_cmpScripts.marginWidth = 0;
		gl_cmpScripts.marginHeight = 0;
		cmpScripts.setLayout(gl_cmpScripts);
		cmpScripts.setBackground(getBackground());

		TabFolder tabScripts = new TabFolder(cmpScripts, SWT.NONE);
		tabScripts.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabItem tbtInitialize = new TabItem(tabScripts, SWT.NONE);
		tbtInitialize.setText("Initialize");
		
		Composite tbtInitializeComposite = new Composite(tabScripts, SWT.NONE);
		tbtInitialize.setControl(tbtInitializeComposite);
		GridLayout gl_tbtInitializeComposite = new GridLayout(2, false);
		gl_tbtInitializeComposite.verticalSpacing = 2;
		gl_tbtInitializeComposite.marginTop = 1;
		gl_tbtInitializeComposite.marginRight = 1;
		gl_tbtInitializeComposite.marginWidth = 0;
		gl_tbtInitializeComposite.marginHeight = 0;
		tbtInitializeComposite.setLayout(gl_tbtInitializeComposite);
		tbtInitializeComposite.setBackground(getBackground());
		
		txtInitializeScript = new Text(tbtInitializeComposite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		txtInitializeScript.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		btnInitializeScriptApply = new Button(tbtInitializeComposite, SWT.NONE);
		GridData gd_btnInitializeScriptApply = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnInitializeScriptApply.heightHint = 21;
		gd_btnInitializeScriptApply.widthHint = 90;
		btnInitializeScriptApply.setLayoutData(gd_btnInitializeScriptApply);
		btnInitializeScriptApply.setImage(Resources.IMAGE_TICK);
		btnInitializeScriptApply.setText("Apply");
		
		btnInitializeScriptTestDrive = new Button(tbtInitializeComposite, SWT.NONE);
		btnInitializeScriptTestDrive.setToolTipText("execute specified script");
		GridData gd_btnInitializeScriptTestDrive = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnInitializeScriptTestDrive.heightHint = 21;
		gd_btnInitializeScriptTestDrive.widthHint = 90;
		btnInitializeScriptTestDrive.setLayoutData(gd_btnInitializeScriptTestDrive);
		btnInitializeScriptTestDrive.setImage(Resources.IMAGE_PLAY);
		btnInitializeScriptTestDrive.setText("Test Drive");
		
		TabItem tbtPretransmission = new TabItem(tabScripts, SWT.NONE);
		tbtPretransmission.setText("Pre-Transmission");

		Composite tbtPretransmissionComposite = new Composite(tabScripts, SWT.NONE);
		tbtPretransmission.setControl(tbtPretransmissionComposite);
		GridLayout gl_tbtPretransmissionComposite = new GridLayout(2, false);
		gl_tbtPretransmissionComposite.verticalSpacing = 2;
		gl_tbtPretransmissionComposite.marginTop = 1;
		gl_tbtPretransmissionComposite.marginRight = 1;
		gl_tbtPretransmissionComposite.marginWidth = 0;
		gl_tbtPretransmissionComposite.marginHeight = 0;
		tbtPretransmissionComposite.setLayout(gl_tbtPretransmissionComposite);
		tbtPretransmissionComposite.setBackground(getBackground());
		
		txtPretransmissionScript = new Text(tbtPretransmissionComposite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		txtPretransmissionScript.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		btnPretransmissionScriptApply = new Button(tbtPretransmissionComposite, SWT.NONE);
		GridData gd_btnPretransmissionScriptApply = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnPretransmissionScriptApply.heightHint = 21;
		gd_btnPretransmissionScriptApply.widthHint = 90;
		btnPretransmissionScriptApply.setLayoutData(gd_btnPretransmissionScriptApply);
		btnPretransmissionScriptApply.setImage(Resources.IMAGE_TICK);
		btnPretransmissionScriptApply.setText("Apply");
		
		btnPretransmissionScriptTestDrive = new Button(tbtPretransmissionComposite, SWT.NONE);
		btnPretransmissionScriptTestDrive.setToolTipText("execute specified script");
		GridData gd_btnPretransmissionScriptTestDrive = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnPretransmissionScriptTestDrive.heightHint = 21;
		gd_btnPretransmissionScriptTestDrive.widthHint = 90;
		btnPretransmissionScriptTestDrive.setLayoutData(gd_btnPretransmissionScriptTestDrive);
		btnPretransmissionScriptTestDrive.setImage(Resources.IMAGE_PLAY);
		btnPretransmissionScriptTestDrive.setText("Test Drive");
		
		
		TabItem tbtPrescattering = new TabItem(tabScripts, SWT.NONE);
		tbtPrescattering.setText("Pre-Scattering");

		Composite tbtPrescatteringComposite = new Composite(tabScripts, SWT.NONE);
		tbtPrescattering.setControl(tbtPrescatteringComposite);
		GridLayout gl_tbtPrescatteringComposite = new GridLayout(2, false);
		gl_tbtPrescatteringComposite.verticalSpacing = 2;
		gl_tbtPrescatteringComposite.marginTop = 1;
		gl_tbtPrescatteringComposite.marginRight = 1;
		gl_tbtPrescatteringComposite.marginWidth = 0;
		gl_tbtPrescatteringComposite.marginHeight = 0;
		tbtPrescatteringComposite.setLayout(gl_tbtPrescatteringComposite);
		tbtPrescatteringComposite.setBackground(getBackground());
		
		txtPrescatteringScript = new Text(tbtPrescatteringComposite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		txtPrescatteringScript.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		btnPrescatteringScriptApply = new Button(tbtPrescatteringComposite, SWT.NONE);
		GridData gd_btnPrescatteringScriptApply = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnPrescatteringScriptApply.heightHint = 21;
		gd_btnPrescatteringScriptApply.widthHint = 90;
		btnPrescatteringScriptApply.setLayoutData(gd_btnPrescatteringScriptApply);
		btnPrescatteringScriptApply.setImage(Resources.IMAGE_TICK);
		btnPrescatteringScriptApply.setText("Apply");
		
		btnPrescatteringScriptTestDrive = new Button(tbtPrescatteringComposite, SWT.NONE);
		btnPrescatteringScriptTestDrive.setToolTipText("execute specified script");
		GridData gd_btnPrescatteringScriptTestDrive = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnPrescatteringScriptTestDrive.heightHint = 21;
		gd_btnPrescatteringScriptTestDrive.widthHint = 90;
		btnPrescatteringScriptTestDrive.setLayoutData(gd_btnPrescatteringScriptTestDrive);
		btnPrescatteringScriptTestDrive.setImage(Resources.IMAGE_PLAY);
		btnPrescatteringScriptTestDrive.setText("Test Drive");
		
		// transmission
		Group grpTransmission = new Group(cmpRight, SWT.NONE);
		grpTransmission.setLayout(new GridLayout(1, false));
		grpTransmission.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpTransmission.setText(Measurement.TRANSMISSION);

		cmpTransmission = new Composite(grpTransmission, SWT.NONE);
		cmpTransmission.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_cmpTransmission = new GridLayout(3, false);
		gl_cmpTransmission.marginHeight = 0;
		gl_cmpTransmission.marginWidth = 0;
		cmpTransmission.setLayout(gl_cmpTransmission);
		cmpTransmission.setBackground(getBackground());

		cmbTransmissionAttAlgo = new Combo(cmpTransmission, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData gd_cmbTransmissionAlgoType = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_cmbTransmissionAlgoType.widthHint = 110;
		cmbTransmissionAttAlgo.setLayoutData(gd_cmbTransmissionAlgoType);
		cmbTransmissionAttAlgo.setItems(new String[] {"fixed attenuation"});
		cmbTransmissionAttAlgo.setText("fixed attenuation");
		cmbTransmissionAttAngle = new Combo(cmpTransmission, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.RIGHT);
		cmbTransmissionAttAngle.setItems(new String[] {"330�", "300�", "270�", "240�", "210�", "180�", "150�", "120�", "90�", "60�", "30�", "0�"});
		cmbTransmissionAttAngle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		cmbTransmissionAttAngle.setText("150�");
		new Label(cmpTransmission, SWT.NONE);

		lblTransmissionMaxTime = new Label(cmpTransmission, SWT.NONE);
		GridData gd_lblTransmissionMaxTime = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblTransmissionMaxTime.horizontalIndent = 4;
		lblTransmissionMaxTime.setLayoutData(gd_lblTransmissionMaxTime);
		lblTransmissionMaxTime.setText("Acquisition Time:");
		txtTransmissionMaxTime = new Text(cmpTransmission, SWT.BORDER | SWT.RIGHT);
		txtTransmissionMaxTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblTransmissionMaxTimeUnit = new Label(cmpTransmission, SWT.NONE);
		lblTransmissionMaxTimeUnit.setText("sec");

		// scattering
		Group grpScattering = new Group(cmpRight, SWT.NONE);
		grpScattering.setLayout(new GridLayout(1, false));
		grpScattering.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpScattering.setText(Measurement.SCATTERING);

		cmpScattering = new Composite(grpScattering, SWT.NONE);
		cmpScattering.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_cmpScattering = new GridLayout(3, false);
		gl_cmpScattering.marginWidth = 0;
		gl_cmpScattering.marginHeight = 0;
		cmpScattering.setLayout(gl_cmpScattering);
		cmpScattering.setBackground(getBackground());

		cmbScatteringAttAlgo = new Combo(cmpScattering, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData gd_cmbScatteringAlgoType = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_cmbScatteringAlgoType.widthHint = 110;
		cmbScatteringAttAlgo.setLayoutData(gd_cmbScatteringAlgoType);
		cmbScatteringAttAlgo.setItems(new String[] {"fixed attenuation", "iterative attenuation", "smart attenuation"});
		cmbScatteringAttAlgo.setText("iterative attenuation");
		cmbScatteringAttAngle = new Combo(cmpScattering, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.RIGHT);
		cmbScatteringAttAngle.setItems(new String[] {"330�", "300�", "270�", "240�", "210�", "180�", "150�", "120�", "90�", "60�", "30�", "0�"});
		cmbScatteringAttAngle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		cmbScatteringAttAngle.setText("90�");
		new Label(cmpScattering, SWT.NONE);

		lblScatteringMaxTime = new Label(cmpScattering, SWT.NONE);
		GridData gd_lblScatteringMaxTime = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblScatteringMaxTime.horizontalIndent = 4;
		lblScatteringMaxTime.setLayoutData(gd_lblScatteringMaxTime);
		lblScatteringMaxTime.setText("Acquisition Time:");
		txtScatteringMaxTime = new Text(cmpScattering, SWT.BORDER | SWT.RIGHT);
		txtScatteringMaxTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblScatteringMaxTimeUnit = new Label(cmpScattering, SWT.NONE);
		lblScatteringMaxTimeUnit.setText("sec");
		
		// expand button
		cmpExpand = new Composite(cmpRight, SWT.NONE);
		cmpExpand.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 2, 1));
		GridLayout gl_cmpExpand = new GridLayout(2, false);
		gl_cmpExpand.marginHeight = 0;
		cmpExpand.setLayout(gl_cmpExpand);
		cmpExpand.setBackground(getBackground());
		
		btnExpand = new Button(cmpExpand, SWT.NONE);
		GridData gd_btnScatteringExpand = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnScatteringExpand.heightHint = 15;
		gd_btnScatteringExpand.widthHint = 15;
		gd_btnScatteringExpand.verticalIndent = 2;
		btnExpand.setLayoutData(gd_btnScatteringExpand);
		btnExpand.setImage(Resources.IMAGE_DOWN);
		
		lblExpand = new Label(cmpExpand, SWT.NONE);
		GridData gd_lblScatteringExpand = new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1);
		gd_lblScatteringExpand.widthHint = 100;
		gd_lblScatteringExpand.horizontalIndent = -3;
		gd_lblScatteringExpand.verticalIndent = 2;
		lblExpand.setLayoutData(gd_lblScatteringExpand);
		lblExpand.setText("Advanced Options");


		// menu
	    menu = new Menu(this);
	    MenuItem menuItem;

	    // add new/saved
	    menuItem = new MenuItem(menu, SWT.NONE);
	    menuItem.setText("Add New");
	    menuItem.setImage(Resources.IMAGE_PLUS);
	    menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				configurationList.addConfiguration();
			}
		});

	    menuItem = new MenuItem(menu, SWT.NONE);
	    menuItem.setText("Add Saved...");
	    menuItem.setImage(Resources.IMAGE_IMPORT_FILE);
	    menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ConfigurationCatalogDialog dialog = new ConfigurationCatalogDialog(getShell());
				dialog.create();
				if (dialog.open() == Window.OK)
					configurationList.addConfigurations(dialog.getConfigurations());
			}
		});

	    menuItem = new MenuItem(menu, SWT.NONE);
	    menuItem.setText("Remove All");
	    menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				configurationList.clear();
			}
		});
	    
	    // enable/disable
	    new MenuItem(menu, SWT.SEPARATOR);
	    menuItem = new MenuItem(menu, SWT.NONE);
	    menuItem.setText("Enable All");
	    menuItem.setImage(Resources.IMAGE_BOX_CHECKED);
	    menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				configurationList.enableAll();
			}
		});
	    
	    menuItem = new MenuItem(menu, SWT.NONE);
	    menuItem.setText("Disable All");
	    menuItem.setImage(Resources.IMAGE_BOX_UNCHECKED);
	    menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				configurationList.disableAll();
			}
		});
	    
	    // import/export
	    new MenuItem(menu, SWT.SEPARATOR);
	    menuItem = new MenuItem(menu, SWT.NONE);
	    menuItem.setText("XML Import");
	    menuItem.setImage(Resources.IMAGE_IMPORT);
	    menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);

				fileDialog.setFilterNames(new String[] { "Extensible Markup Language (*.xml)", "All Files (*.*)" });
				fileDialog.setFilterExtensions(new String[] { "*.xml", "*.*" });

				String filename = fileDialog.open();
				if ((filename != null) && (filename.length() > 0)) {
					boolean succeeded = false;
					try (InputStream stream = new FileInputStream(filename)) {
						succeeded = configurationList.replaceConfigurations(stream);
					}
					catch (Exception e2) {
					}
					if (!succeeded) {
						MessageBox dialog = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK);
						dialog.setText("Warning");
						dialog.setMessage("Unable to import from selected xml file.");
						dialog.open();
					}
				}
			}
		});
	    menuItem = new MenuItem(menu, SWT.NONE);
	    menuItem.setText("XML Export");
	    menuItem.setImage(Resources.IMAGE_EXPORT);
	    menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);

				fileDialog.setFilterNames(new String[] { "Extensible Markup Language (*.xml)", "All Files (*.*)" });
				fileDialog.setFilterExtensions(new String[] { "*.xml", "*.*" });

				String filename = fileDialog.open();
				if ((filename != null) && (filename.length() > 0)) {
					boolean succeeded = false;
					try (OutputStream stream = new FileOutputStream(filename)) {
						succeeded = configurationList.saveTo(stream);
					}
					catch (Exception e2) {
					}
					if (!succeeded) {
						MessageBox dialog = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK);
						dialog.setText("Warning");
						dialog.setMessage("Unable to export to selected xml file.");
						dialog.open();
					}
				}
			}
		});

	    initDataBindings(provider);
	}
	
	// methods
	private void initDataBindings(final ModelProvider provider) {
		if (provider == null)
			return;
		
		configurationList = provider.getConfigurationList();
		
		// setup table
		ElementTableModel<ConfigurationList, Configuration> tableModel = createTableModel(tblConfigurations, configurationList, menu);

		// selection
		final ProxyElement<Configuration> selectedConfiguration = tableModel.getSelectedElement();
		final ProxyElement<Measurement> selectedTransmissionMeasurement = new ProxyElement<Measurement>();
		final ProxyElement<Measurement> selectedScatteringMeasurement = new ProxyElement<Measurement>();
		selectedConfiguration.addListener(new IProxyElementListener<Configuration>() {
			// fields
			private final IElementListListener<Measurement> listener = new IElementListListener<Measurement>() {
				@Override
				public void onAddedListElement(Measurement element) {
					switch (element.getIndex()) {
					case 0:
						selectedTransmissionMeasurement.setTarget(element);
						break;
					case 1:
						selectedScatteringMeasurement.setTarget(element);
						break;
					}
				}
				@Override
				public void onDeletedListElement(Measurement element) {
					if (selectedTransmissionMeasurement.getTarget() == element)
						selectedTransmissionMeasurement.setTarget(null);
					else if (selectedScatteringMeasurement.getTarget() == element)
						selectedScatteringMeasurement.setTarget(null);
				}
			};
			
			// methods
			@Override
			public void onTargetChange(Configuration oldTarget, Configuration newTarget) {
				if (oldTarget != null)
					oldTarget.removeListListener(listener);
				
				selectedTransmissionMeasurement.setTarget(null);
				selectedScatteringMeasurement.setTarget(null);
				
				if (newTarget != null)
					newTarget.addListListener(listener);
			}
		});

		// converters
		final IModelValueConverter<Long, String> timeValueConverter = new TimeValueConverter();
		final IModelValueConverter<Long, String> countValueConverter = new CountValueConverter();

		// data binding
		final DataBindingContext bindingContext = new DataBindingContext();
		
		ModelBinder.createTextBinding(
				bindingContext,
				txtName,
				selectedConfiguration,
				Configuration.NAME);
		
		// Scripts
		ModelBinder.createMultiLineBinding(
				bindingContext,
				txtInitializeScript,
				selectedConfiguration,
				Configuration.SETUP_SCRIPT);
		ModelBinder.createMultiLineBinding(
				bindingContext,
				txtPretransmissionScript,
				selectedTransmissionMeasurement,
				Measurement.SETUP_SCRIPT);
		ModelBinder.createMultiLineBinding(
				bindingContext,
				txtPrescatteringScript,
				selectedScatteringMeasurement,
				Measurement.SETUP_SCRIPT);
		
		// Apply / Test Drive
		ModelBinder.createEnabledBinding(
				btnInitializeScriptApply,
				selectedConfiguration);
		ModelBinder.createEnabledBinding(
				btnInitializeScriptTestDrive,
				selectedConfiguration);
		ModelBinder.createEnabledBinding(
				btnPretransmissionScriptApply,
				selectedTransmissionMeasurement);
		ModelBinder.createEnabledBinding(
				btnPretransmissionScriptTestDrive,
				selectedTransmissionMeasurement);
		ModelBinder.createEnabledBinding(
				btnPrescatteringScriptApply,
				selectedScatteringMeasurement);
		ModelBinder.createEnabledBinding(
				btnPrescatteringScriptTestDrive,
				selectedScatteringMeasurement);
		
		// Transmission - Attenuation
		ModelBinder.createComboBinding(
				bindingContext,
				cmbTransmissionAttAlgo,
				selectedTransmissionMeasurement,
				Measurement.ATTENUATION_ALGORITHM);
		ModelBinder.createComboBinding(
				bindingContext,
				cmbTransmissionAttAngle,
				selectedTransmissionMeasurement,
				Measurement.ATTENUATION_ANGLE);

		// Transmission - MaxTime
		ModelBinder.createEnabledBinding(
				bindingContext,
				txtTransmissionMaxTime,
				selectedTransmissionMeasurement,
				Measurement.MAX_TIME_ENABLED);
		ModelBinder.createTextBinding(
				bindingContext,
				txtTransmissionMaxTime,
				selectedTransmissionMeasurement,
				Measurement.MAX_TIME,
				timeValueConverter);

		// Scattering - Attenuation
		ModelBinder.createComboBinding(
				bindingContext,
				cmbScatteringAttAlgo,
				selectedScatteringMeasurement,
				Measurement.ATTENUATION_ALGORITHM);
		ModelBinder.createComboBinding(
				bindingContext,
				cmbScatteringAttAngle,
				selectedScatteringMeasurement,
				Measurement.ATTENUATION_ANGLE);

		// Scattering - MaxTime
		ModelBinder.createEnabledBinding(
				bindingContext,
				txtScatteringMaxTime,
				selectedScatteringMeasurement,
				Measurement.MAX_TIME_ENABLED);
		ModelBinder.createTextBinding(
				bindingContext,
				txtScatteringMaxTime,
				selectedScatteringMeasurement,
				Measurement.MAX_TIME,
				timeValueConverter);

		// buttons
		final SelectionListener expandClick = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmpExpand.dispose();

		        final ToolTip toolTip = new ToolTip(getShell(), SWT.ICON_INFORMATION);
		        toolTip.setText("Information");
		        toolTip.setMessage(
		        		// "\"Min Time\" and \"Max Time\" provide a time window for the acquisition.\r\n" +
						"After the specified minimal time the acquisition is stopped when either the " +
						"desired Detector Counts or Monitor Counts have been collected but at latest " +
						"at the specified maximal time.");
		        toolTip.setAutoHide(false);
				
		        MouseTrackListener listener = new MouseTrackAdapter() {
		        	// finals
				    final int TOOLTIP_HIDE_DELAY = 300; // ms
				    final int MOUSE_OFFSET = 15;
				    final int BOTTOM_OFFSET = 150;
				    
		        	// methods
					@Override
					public void mouseHover(MouseEvent e) {
						Display display = getDisplay();
						
						Point p = display.getCursorLocation();
						p.x += MOUSE_OFFSET;
						p.y += MOUSE_OFFSET;

						Rectangle bounds = display.getBounds();
						if (p.y > bounds.height - BOTTOM_OFFSET)
							p.y = bounds.height - BOTTOM_OFFSET;
						
						toolTip.setLocation(p.x, p.y);
						toolTip.setVisible(true);
					}
					@Override
					public void mouseExit(MouseEvent e) {
		                getDisplay().timerExec(TOOLTIP_HIDE_DELAY, new Runnable() {
		                    public void run() {
		                        toolTip.setVisible(false);
		                    }
		                });
					}
				};
		        
				cmpTransmission.addMouseTrackListener(listener);
				
				Button chkTransmissionMaxTime = new Button(cmpTransmission, SWT.CHECK);
				chkTransmissionMaxTime.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
				chkTransmissionMaxTime.setText("Max Time:");
				chkTransmissionMaxTime.setSelection(true);
				chkTransmissionMaxTime.addMouseTrackListener(listener);
				txtTransmissionMaxTime.addMouseTrackListener(listener);
				lblTransmissionMaxTimeUnit.addMouseTrackListener(listener);
				
				chkTransmissionMaxTime.moveAbove(lblTransmissionMaxTime);
				lblTransmissionMaxTime.dispose();
				
				Button chkTransmissionMinTime = new Button(cmpTransmission, SWT.CHECK);
				chkTransmissionMinTime.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
				chkTransmissionMinTime.setText("Min Time:");
				chkTransmissionMinTime.addMouseTrackListener(listener);
				Text txtTransmissionMinTime = new Text(cmpTransmission, SWT.BORDER | SWT.RIGHT);
				txtTransmissionMinTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
				txtTransmissionMinTime.addMouseTrackListener(listener);
				Label lblTransmissionMinTimeUnit = new Label(cmpTransmission, SWT.NONE);
				lblTransmissionMinTimeUnit.setText("sec");
				lblTransmissionMinTimeUnit.addMouseTrackListener(listener);

				chkTransmissionMinTime.moveAbove(chkTransmissionMaxTime);
				txtTransmissionMinTime.moveAbove(chkTransmissionMaxTime);
				lblTransmissionMinTimeUnit.moveAbove(chkTransmissionMaxTime);

				Button chkTransmissionMonitorCounts = new Button(cmpTransmission, SWT.CHECK);
				chkTransmissionMonitorCounts.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
				chkTransmissionMonitorCounts.setText("Monitor Counts:");
				chkTransmissionMonitorCounts.addMouseTrackListener(listener);
				Text txtTransmissionMonitorCounts = new Text(cmpTransmission, SWT.BORDER | SWT.RIGHT);
				txtTransmissionMonitorCounts.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
				txtTransmissionMonitorCounts.addMouseTrackListener(listener);
				Label blank1 = new Label(cmpTransmission, SWT.NONE);

				chkTransmissionMonitorCounts.moveAbove(chkTransmissionMaxTime);
				txtTransmissionMonitorCounts.moveAbove(chkTransmissionMaxTime);
				blank1.moveAbove(chkTransmissionMaxTime);

				Button chkTransmissionDetectorCounts = new Button(cmpTransmission, SWT.CHECK);
				chkTransmissionDetectorCounts.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
				chkTransmissionDetectorCounts.setText("Detector Counts:");
				chkTransmissionDetectorCounts.addMouseTrackListener(listener);
				Text txtTransmissionDetectorCounts = new Text(cmpTransmission, SWT.BORDER | SWT.RIGHT);
				txtTransmissionDetectorCounts.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
				txtTransmissionDetectorCounts.addMouseTrackListener(listener);
				Label blank2 = new Label(cmpTransmission, SWT.NONE);

				chkTransmissionDetectorCounts.moveAbove(chkTransmissionMaxTime);
				txtTransmissionDetectorCounts.moveAbove(chkTransmissionMaxTime);
				blank2.moveAbove(chkTransmissionMaxTime);
				

				cmpScattering.addMouseTrackListener(listener);
				
				Button chkScatteringMaxTime = new Button(cmpScattering, SWT.CHECK);
				chkScatteringMaxTime.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
				chkScatteringMaxTime.setText("Max Time:");
				chkScatteringMaxTime.setSelection(true);
				chkScatteringMaxTime.addMouseTrackListener(listener);
				txtScatteringMaxTime.addMouseTrackListener(listener);
				lblScatteringMaxTimeUnit.addMouseTrackListener(listener);

				chkScatteringMaxTime.moveAbove(lblScatteringMaxTime);
				lblScatteringMaxTime.dispose();
				
				Button chkScatteringMinTime = new Button(cmpScattering, SWT.CHECK);
				chkScatteringMinTime.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
				chkScatteringMinTime.setText("Min Time:");
				chkScatteringMinTime.addMouseTrackListener(listener);
				Text txtScatteringMinTime = new Text(cmpScattering, SWT.BORDER | SWT.RIGHT);
				txtScatteringMinTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
				txtScatteringMinTime.addMouseTrackListener(listener);
				Label lblScatteringMinTimeUnit = new Label(cmpScattering, SWT.NONE);
				lblScatteringMinTimeUnit.setText("sec");
				lblScatteringMinTimeUnit.addMouseTrackListener(listener);

				chkScatteringMinTime.moveAbove(chkScatteringMaxTime);
				txtScatteringMinTime.moveAbove(chkScatteringMaxTime);
				lblScatteringMinTimeUnit.moveAbove(chkScatteringMaxTime);
				
				Button chkScatteringMonitorCounts = new Button(cmpScattering, SWT.CHECK);
				chkScatteringMonitorCounts.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
				chkScatteringMonitorCounts.setText("Monitor Counts:");
				chkScatteringMonitorCounts.addMouseTrackListener(listener);
				Text txtScatteringMonitorCounts = new Text(cmpScattering, SWT.BORDER | SWT.RIGHT);
				txtScatteringMonitorCounts.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
				txtScatteringMonitorCounts.addMouseTrackListener(listener);
				Label blank3 = new Label(cmpScattering, SWT.NONE);

				chkScatteringMonitorCounts.moveAbove(chkScatteringMaxTime);
				txtScatteringMonitorCounts.moveAbove(chkScatteringMaxTime);
				blank3.moveAbove(chkScatteringMaxTime);
				
				Button chkScatteringDetectorCounts = new Button(cmpScattering, SWT.CHECK);
				chkScatteringDetectorCounts.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
				chkScatteringDetectorCounts.setText("Detector Counts:");
				chkScatteringDetectorCounts.addMouseTrackListener(listener);
				Text txtScatteringDetectorCounts = new Text(cmpScattering, SWT.BORDER | SWT.RIGHT);
				txtScatteringDetectorCounts.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
				txtScatteringDetectorCounts.addMouseTrackListener(listener);
				Label blank4 = new Label(cmpScattering, SWT.NONE);

				chkScatteringDetectorCounts.moveAbove(chkScatteringMaxTime);
				txtScatteringDetectorCounts.moveAbove(chkScatteringMaxTime);
				blank4.moveAbove(chkScatteringMaxTime);
				
				// Transmission - MaxTime
				ModelBinder.createCheckedBinding(
						bindingContext,
						chkTransmissionMaxTime,
						selectedTransmissionMeasurement,
						Measurement.MAX_TIME_ENABLED);

				// Transmission - MinTime
				ModelBinder.createCheckedBinding(
						bindingContext,
						chkTransmissionMinTime,
						selectedTransmissionMeasurement,
						Measurement.MIN_TIME_ENABLED);
				ModelBinder.createEnabledBinding(
						bindingContext,
						txtTransmissionMinTime,
						selectedTransmissionMeasurement,
						Measurement.MIN_TIME_ENABLED);
				ModelBinder.createTextBinding(
						bindingContext,
						txtTransmissionMinTime,
						selectedTransmissionMeasurement,
						Measurement.MIN_TIME,
						timeValueConverter);

				// Transmission - MonitorCounts
				ModelBinder.createCheckedBinding(
						bindingContext,
						chkTransmissionMonitorCounts,
						selectedTransmissionMeasurement,
						Measurement.TARGET_MONITOR_COUNTS_ENABLED);
				ModelBinder.createEnabledBinding(
						bindingContext,
						txtTransmissionMonitorCounts,
						selectedTransmissionMeasurement,
						Measurement.TARGET_MONITOR_COUNTS_ENABLED);
				ModelBinder.createTextBinding(
						bindingContext,
						txtTransmissionMonitorCounts,
						selectedTransmissionMeasurement,
						Measurement.TARGET_MONITOR_COUNTS,
						countValueConverter);

				// Transmission - DetectorCounts
				ModelBinder.createCheckedBinding(
						bindingContext,
						chkTransmissionDetectorCounts,
						selectedTransmissionMeasurement,
						Measurement.TARGET_DETECTOR_COUNTS_ENABLED);
				ModelBinder.createEnabledBinding(
						bindingContext,
						txtTransmissionDetectorCounts,
						selectedTransmissionMeasurement,
						Measurement.TARGET_DETECTOR_COUNTS_ENABLED);
				ModelBinder.createTextBinding(
						bindingContext,
						txtTransmissionDetectorCounts,
						selectedTransmissionMeasurement,
						Measurement.TARGET_DETECTOR_COUNTS,
						countValueConverter);
				
				// Scattering - MaxTime
				ModelBinder.createCheckedBinding(
						bindingContext,
						chkScatteringMaxTime,
						selectedScatteringMeasurement,
						Measurement.MAX_TIME_ENABLED);
				
				// Scattering - MinTime
				ModelBinder.createCheckedBinding(
						bindingContext,
						chkScatteringMinTime,
						selectedScatteringMeasurement,
						Measurement.MIN_TIME_ENABLED);
				ModelBinder.createEnabledBinding(
						bindingContext,
						txtScatteringMinTime,
						selectedScatteringMeasurement,
						Measurement.MIN_TIME_ENABLED);
				ModelBinder.createTextBinding(
						bindingContext,
						txtScatteringMinTime,
						selectedScatteringMeasurement,
						Measurement.MIN_TIME,
						timeValueConverter);
				
				// Scattering - MonitorCounts
				ModelBinder.createCheckedBinding(
						bindingContext,
						chkScatteringMonitorCounts,
						selectedScatteringMeasurement,
						Measurement.TARGET_MONITOR_COUNTS_ENABLED);
				ModelBinder.createEnabledBinding(
						bindingContext,
						txtScatteringMonitorCounts,
						selectedScatteringMeasurement,
						Measurement.TARGET_MONITOR_COUNTS_ENABLED);
				ModelBinder.createTextBinding(
						bindingContext,
						txtScatteringMonitorCounts,
						selectedScatteringMeasurement,
						Measurement.TARGET_MONITOR_COUNTS,
						countValueConverter);
				
				// Scattering - DetectorCounts
				ModelBinder.createCheckedBinding(
						bindingContext,
						chkScatteringDetectorCounts,
						selectedScatteringMeasurement,
						Measurement.TARGET_DETECTOR_COUNTS_ENABLED);
				ModelBinder.createEnabledBinding(
						bindingContext,
						txtScatteringDetectorCounts,
						selectedScatteringMeasurement,
						Measurement.TARGET_DETECTOR_COUNTS_ENABLED);
				ModelBinder.createTextBinding(
						bindingContext,
						txtScatteringDetectorCounts,
						selectedScatteringMeasurement,
						Measurement.TARGET_DETECTOR_COUNTS,
						countValueConverter);

				layout(true, true);
			}
		};
		
		btnExpand.addSelectionListener(expandClick);
		lblExpand.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				expandClick.widgetSelected(null);
			}
		});
		

		btnInitializeScriptTestDrive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CustomInstrumentAction customAction = provider.getCustomInstrumentAction();

				String script = selectedConfiguration.getTarget().getSetupScript();
				if (!customAction.testDrive(script)) {
					MessageBox dialog = new MessageBox(getShell(), SWT.ICON_INFORMATION | SWT.OK);
					dialog.setText("Information");
					dialog.setMessage("busy");
					dialog.open();
				}
			}
		});

		btnPretransmissionScriptTestDrive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CustomInstrumentAction customAction = provider.getCustomInstrumentAction();

				String script = selectedTransmissionMeasurement.getTarget().getSetupScript();
				if (!customAction.testDrive(script)) {
					MessageBox dialog = new MessageBox(getShell(), SWT.ICON_INFORMATION | SWT.OK);
					dialog.setText("Information");
					dialog.setMessage("busy");
					dialog.open();
				}
			}
		});

		btnPrescatteringScriptTestDrive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CustomInstrumentAction customAction = provider.getCustomInstrumentAction();

				String script = selectedScatteringMeasurement.getTarget().getSetupScript();
				if (!customAction.testDrive(script)) {
					MessageBox dialog = new MessageBox(getShell(), SWT.ICON_INFORMATION | SWT.OK);
					dialog.setText("Information");
					dialog.setMessage("busy");
					dialog.open();
				}
			}
		});
	}
	private static ElementTableModel<ConfigurationList, Configuration> createTableModel(KTable table, final ConfigurationList configurationList, Menu menu) {
		// cell rendering
		DefaultCellRenderer indexRenderer = new TextCellRenderer(TextCellRenderer.INDICATION_FOCUS);
    	DefaultCellRenderer checkableRenderer = new CheckableCellRenderer(CheckableCellRenderer.INDICATION_FOCUS | CheckableCellRenderer.INDICATION_COPYABLE);
    	DefaultCellRenderer nameRenderer = new NameCellRenderer(TextCellRenderer.INDICATION_FOCUS | TextCellRenderer.INDICATION_COPYABLE); 

    	indexRenderer.setAlignment(SWTX.ALIGN_HORIZONTAL_RIGHT | SWTX.ALIGN_VERTICAL_CENTER);
    	
    	// cell editing
    	KTableCellEditor indexEditor = new KTableCellEditorText2(SWT.RIGHT);
    	KTableCellEditor checkableEditor = new KTableCellEditorCheckbox();
    	KTableCellEditor nameEditor = new KTableCellEditorText2();
    	
    	// buttons
    	IButtonListener<Configuration> addButtonListener = new IButtonListener<Configuration>() {
			@Override
			public void onClicked(int col, int row, Configuration configuration) {
				configurationList.addConfiguration(configuration.getIndex());
			}
		};
    	IButtonListener<Configuration> duplicateButtonListener = new IButtonListener<Configuration>() {
			@Override
			public void onClicked(int col, int row, Configuration configuration) {
				configuration.duplicate();
			}
		};
    	IButtonListener<Configuration> deleteButtonListener = new IButtonListener<Configuration>() {
			@Override
			public void onClicked(int col, int row, Configuration configuration) {
				configuration.delete();
			}
		};
    	
    	// construction
    	ElementTableModel<ConfigurationList, Configuration> model = new ElementTableModel<ConfigurationList, Configuration>(
    			table,
    			configurationList,
    			menu,
		    	"add, duplicate or delete configuration",
		    	Arrays.asList(
		    			new ButtonInfo<Configuration>(Resources.IMAGE_PLUS_SMALL_GRAY, Resources.IMAGE_PLUS_SMALL, addButtonListener),
		    			new ButtonInfo<Configuration>(Resources.IMAGE_COPY_SMALL_GRAY, Resources.IMAGE_COPY_SMALL, duplicateButtonListener),
		    			new ButtonInfo<Configuration>(Resources.IMAGE_MINUS_SMALL_GRAY, Resources.IMAGE_MINUS_SMALL, deleteButtonListener)),
		    	Arrays.asList(
		    			new ColumnDefinition(Configuration.ENABLED, "", 30, checkableRenderer, checkableEditor),
		    			new ColumnDefinition(Configuration.INDEX, "", 30, indexRenderer, indexEditor, new IndexValueConverter()),
		    			new ColumnDefinition(Configuration.NAME, "Name", 200, nameRenderer, nameEditor)));
    	
    	table.setModel(model);
    	return model;
	}
}
