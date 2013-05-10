package qos;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

public class MainFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	
	
	/*	+-----------------------------------------------------------+
	 * 	| +-------------------------------------------------------+ |
	 * 	| |														  | |
	 * 	| |				   		 GUI VARIABLES				 	  | |
	 * 	| |														  | |
	 * 	| +-------------------------------------------------------+ |
	 * 	+-----------------------------------------------------------+
	 */
	
	// Labels
	private JLabel lblWeightSumSigma;
	private JLabel lblWeightSum;
	private JLabel jLabelUtilityText;
	private JLabel jLabelFitnessBraceLeft;
	private JLabel jLabelFitnessCaseOne;
	private JLabel jLabelFitnessCaseOneDescription;
	private JLabel jLabelFitnessCaseTwo;
	private JLabel jLabelFitnessCaseTwoDescription;
	private JLabel jLabelElitismRatePercentage;
	private JLabel jLabelTerminationColon;
	private JLabel jLabelTerminationDegree;
	private JLabel jLabelTerminationDegreeClose;
	
	
	// Textfields
	private JTextField jTextFieldMaxCosts;
	private JTextField jTextFieldMaxResponseTime;
	private JTextField jTextFieldMinAvailability;
	private JTextField jTextFieldCostsWeight;
	private JTextField jTextFieldResponseTimeWeight;
	private JTextField jTextFieldAvailabilityWeight;
	private JTextField jTextFieldRelaxation;
	private JTextField jTextFieldPopulationSize;
	private JTextField jTextFieldElitismRate;
	private JTextField jTextFieldCrossoverRate;
	private JTextField jTextFieldMutationRate;
	private JTextField jTextFieldTerminationCriterion;
	private JTextField jTextFieldTerminationDegree;
	private JTextField txtAntVariant;
	private JTextField txtAntIterations;
	private JTextField txtAntAnts;
	private JTextField txtAntAlpha;
	private JTextField txtAntBeta;
	private JTextField txtAntDilution;
	private JTextField txtAntPi;
	
	// Checkboxes
	private JCheckBox jCheckBoxMaxCosts;
	private JCheckBox jCheckBoxMaxResponseTime;
	private JCheckBox jCheckBoxMinAvailability;
	private JCheckBox jCheckBoxRelaxation;
	private JCheckBox jCheckBoxBenchmarkMode;
	private JCheckBox jCheckboxGeneticAlgorithm;
	private JCheckBox jCheckBoxAntColonyOptimization;
	private JCheckBox jCheckBoxAnalyticAlgorithm;
	private JCheckBox jCheckBoxElitismRate;
	
	// Sliders, Comboboxes & Spinner
	private JSlider jSliderMaxCosts;
	private JSlider jSliderMaxResponseTime;
	private JSlider jSliderMinAvailability;
	private JSlider jSliderRelaxation;
	private JComboBox<String> jComboBoxCrossover;
	private JComboBox<String> jComboBoxSelection;
	private JComboBox<String> jComboBoxTerminationCriterion;
	private JSpinner jSpinnerNumberResultTiers;
	
	// Tables
	private ServiceSelectionTable jTableServiceClasses;
	private ServiceSelectionTable jTableWebServices;
	private ServiceSelectionTable jTableAnalyticAlgorithm;
	private ServiceSelectionTable jTableGeneralResults;
	
	// Progress Bars
	private JProgressBar jProgressBarGeneticAlgorithm;
	private JProgressBar jProgressBarAntAlgorithm;
	private JProgressBar jProgressBarAnalyticAlgorithm;
	
	// Buttons
	private JButton jButtonStart;
	private JButton jButtonSaveResults;
	private JButton jButtonVisualize;
	
	// Panels
	private JPanel contentPane;
	private JTabbedPane jTabbedPane;
	
	// Other
	private JTextArea textAreaLog;

	
	
	/*	+-----------------------------------------------------------+
	 * 	| +-------------------------------------------------------+ |
	 * 	| |														  | |
	 * 	| |				   		  VARIABLES				 		  | |
	 * 	| |														  | |
	 * 	| +-------------------------------------------------------+ |
	 * 	+-----------------------------------------------------------+
	 */

	// Class Objects
	private GeneticAlgorithm geneticAlgorithm;
	private AntAlgorithm antAlgorithm;
	private AnalyticAlgorithm analyticAlgorithm;
	private AlgorithmsVisualization algorithmVisualization;
	
	// Constants
	private static final double DEFAULT_RELAXATION = 0.5;
	private static final int DEFAULT_START_POPULATION_SIZE = 100;
	private static final int MAX_START_POPULATION_SIZE = 10000;
	private static final int DEFAULT_ELITISM_RATE = 1;
	private static final int DEFAULT_CROSSOVER_RATE = 70;
	private static final int DEFAULT_MUTATION_RATE = 10;
	private static final int DEFAULT_TERMINATION_CRITERION = 100;
	private static final int DEFAULT_DEGREE_OF_EQUALITY = 75;
	private static final int DEFAULT_VARIANT = 1;
	private static final int DEFAULT_ITERATIONS = 100;
	private static final int DEFAULT_ANTS = 10;
	private static final double DEFAULT_ALPHA = 1;
	private static final double DEFAULT_BETA = 1;
	private static final double DEFAULT_DILUTION = 0.01;
	private static final double DEFAULT_PIINIT = 1;
	private static final int NUMBER_OF_BENCHMARK_ITERATIONS = 10000;
	
	// Formats
	private static final DecimalFormat DECIMAL_FORMAT_TWO = 
		new DecimalFormat("###.##");
	private static final DecimalFormat DECIMAL_FORMAT_FOUR = 
		new DecimalFormat("###.####");
	private SimpleDateFormat dateFormatLog = 
		new SimpleDateFormat("HH:mm:ss: ");
	
	// Lists
	private List<ServiceClass> serviceClassesList = 
		new LinkedList<ServiceClass>();
	private List<ServiceCandidate> serviceCandidatesList = 
		new LinkedList<ServiceCandidate>();
	private List<String> saveResultList = new LinkedList<String>();
	
	// Boolean
	private boolean webServicesLoaded = false;
	private boolean correctWeights = true;
	private boolean benchmarkMode = false;
	private boolean algorithmInProgress;
	
	// Integer & Double
	private int maxCosts = 10000;
	private int maxResponseTime = 10000;
	private int maxAvailability = 100;
	private int minCosts = 0;
	private int minResponseTime = 0;
	private int minAvailability = 0;
	private double cumulatedRuntime;	

	
	
	/*	+-----------------------------------------------------------+
	 * 	| +-------------------------------------------------------+ |
	 * 	| |														  | |
	 * 	| |				   		  MAIN METHOD					  | |
	 * 	| |														  | |
	 * 	| +-------------------------------------------------------+ |
	 * 	+-----------------------------------------------------------+
	 */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new MainFrame().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	
	/*	+-----------------------------------------------------------+
	 * 	| +-------------------------------------------------------+ |
	 * 	| |														  | |
	 * 	| |				   GUI INITIALIZATION AREA				  | |
	 * 	| |														  | |
	 * 	| +-------------------------------------------------------+ |
	 * 	+-----------------------------------------------------------+
	 */
	public MainFrame() {
		// Initialization logging
		System.out.println(dateFormatLog.format(new Date()) + 
				"Initialize Main Content Panel - Started");
		boolean correctInitialization = true;
		try {
			initializeMainContentPanel();
			System.out.println(dateFormatLog.format(new Date()) + 
					"Initialize Main Content Panel - Completed");
		} catch (Exception e) {
			System.err.println(dateFormatLog.format(new Date()) + 
				"Initialize Main Content Panel - Failed");
			correctInitialization = false;
		}
		System.out.println(dateFormatLog.format(new Date()) + 
				"Initialize Menu Bar - Started");
		try {
			initializeMenuBar();
			System.out.println(dateFormatLog.format(new Date()) + 
					"Initialize Menu Bar - Completed");
		} catch (Exception e) {
			System.err.println(dateFormatLog.format(new Date()) + 
					"Initialize Menu Bar - Failed");
			correctInitialization = false;
		}
		System.out.println(dateFormatLog.format(new Date()) + 
				"Initialize General Settings Panel - Started");
		try {
			initializeGeneralSettingsPanel(contentPane);
			System.out.println(dateFormatLog.format(new Date()) + 
					"Initialize General Settings Panel - Completed");
		} catch (Exception e) {
			System.err.println(dateFormatLog.format(new Date()) + 
					"Initialize General Settings Panel - Failed");
			correctInitialization = false;
		}
		System.out.println(dateFormatLog.format(new Date()) + 
				"Initialize Center Area Panel - Started");
		try {
			initializeCenterArea(contentPane);
			System.out.println(dateFormatLog.format(new Date()) + 
					"Initialize Center Area Panel - Completed");
		} catch (Exception e) {
			System.err.println(dateFormatLog.format(new Date()) + 
					"Initialize Center Area Panel - Failed");
			correctInitialization = false;
		}
		System.out.println(dateFormatLog.format(new Date()) + 
				"Initialize Genetic Algorithm Settings Panel - Started");
		try {
			initializeGeneticAlgorithmPanel(contentPane);
			System.out.println(dateFormatLog.format(new Date()) + 
					"Initialize Genetic Algorithm Panel - Completed");
		} catch (Exception e) {
			System.err.println(dateFormatLog.format(new Date()) + 
					"Initialize Genetic Algorithm Panel - Failed");
			correctInitialization = false;
		}
		System.out.println(dateFormatLog.format(new Date()) + 
				"Initialize Ant Algorithm Panel - Started");
		try {
			initializeAntAlgorithmPanel(contentPane);
			System.out.println(dateFormatLog.format(new Date()) + 
					"Initialize Ant Algorithm Panel - Completed");
		} catch (Exception e) {
			System.err.println(dateFormatLog.format(new Date()) + 
					"Initialize Ant Algorithm Panel - Failed");
			correctInitialization = false;
		}
		System.out.println(dateFormatLog.format(new Date()) + 
				"Initialize Analytic Algorithm Panel - Started");
		try {
			initializeAnalyticAlgorithmPanel(contentPane);
			System.out.println(dateFormatLog.format(new Date()) + 
					"Initialize Analytic Algorithm Panel - Completed");
		} catch (Exception e) {
			System.err.println(dateFormatLog.format(new Date()) + 
					"Initialize Analytic Algorithm Panel - Failed");
			correctInitialization = false;
		}
		System.out.println(dateFormatLog.format(new Date()) + 
				"Initialize Tabbed Results Panel - Started");
		try {
			initializeTabbedResultsPanel(contentPane);
			System.out.println(dateFormatLog.format(new Date()) + 
					"Initialize Tabbed Results Panel - Completed");
		} catch (Exception e) {
			System.err.println(dateFormatLog.format(new Date()) + 
					"Initialize Tabbed Results Panel - Failed");
			correctInitialization = false;
		}
		System.out.println(dateFormatLog.format(new Date()) + 
				"Initialize General Results Panel - Started");
		try {
			initializeGeneralResultsPanel(contentPane);
			System.out.println(dateFormatLog.format(new Date()) + 
					"Initialize General Results Panel - Completed");
		} catch (Exception e) {
			System.err.println(dateFormatLog.format(new Date()) + 
					"Initialize General Results Panel - Failed");
			correctInitialization = false;
		}
		System.out.println(dateFormatLog.format(new Date()) + 
				"Initialize Log Panel - Started");
		try {
			initializeLogPanel(contentPane);
			System.out.println(dateFormatLog.format(new Date()) + 
					"Initialize Log Panel - Completed");
		} catch (Exception e) {
			System.err.println(dateFormatLog.format(new Date()) + 
					"Initialize Log Panel - Failed");
			correctInitialization = false;
		}
		if (!correctInitialization) {
			System.exit(1);
		}
	}
	
	private void initializeMainContentPanel() {
		setTitle("Service Selection");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 0, 1100, 850);

		contentPane = new JPanel();
		contentPane.setBorder(new LineBorder(Color.BLACK));
		setContentPane(contentPane);
		GridBagLayout gblContentPane = new GridBagLayout();
		gblContentPane.columnWeights = new double[]{0.7, 0.15, 0.15};
		gblContentPane.rowWeights = new double[]{
				0.025, 0.15, 0.05, 0.275, 0.01, 0.025, 0.415, 0.05};
		contentPane.setLayout(gblContentPane);
		Font generalFont = new Font(
				"generalSettingsFont", Font.BOLD, 16);

		JLabel jLabelSettings = new JLabel("Settings");
		jLabelSettings.setFont(generalFont);
		GridBagConstraints gbcJLabelSettings = new GridBagConstraints();
		gbcJLabelSettings.gridwidth = 2;
		gbcJLabelSettings.insets = new Insets(0, 0, 5, 0);
		gbcJLabelSettings.gridx = 0;
		gbcJLabelSettings.gridy = 0;
		contentPane.add(jLabelSettings, gbcJLabelSettings);

		JSeparator jSeparatorSettingsResults = new JSeparator();
		GridBagConstraints gbcJSeparatorSettingsResults = 
				new GridBagConstraints();
		gbcJSeparatorSettingsResults.gridwidth = 3;
		gbcJSeparatorSettingsResults.fill = GridBagConstraints.BOTH;
		gbcJSeparatorSettingsResults.insets = new Insets(10, 5, 10, 5);
		gbcJSeparatorSettingsResults.gridx = 0;
		gbcJSeparatorSettingsResults.gridy = 4;
		contentPane.add(jSeparatorSettingsResults, 
				gbcJSeparatorSettingsResults);

		JLabel jLabelResults = new JLabel("Results");
		jLabelResults.setFont(generalFont);
		GridBagConstraints gbcJLabelResults = new GridBagConstraints();
		gbcJLabelResults.gridwidth = 2;
		gbcJLabelResults.insets = new Insets(0, 0, 5, 5);
		gbcJLabelResults.gridx = 0;
		gbcJLabelResults.gridy = 5;
		contentPane.add(jLabelResults, gbcJLabelResults);
		
		jButtonSaveResults = new JButton("Save Results");
		jButtonSaveResults.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveResults();
			}
		});
		jButtonSaveResults.setEnabled(false);
		GridBagConstraints gbcJButtonSaveResults = new GridBagConstraints();
		gbcJButtonSaveResults.insets = new Insets(0, 0, 5, 0);
		gbcJButtonSaveResults.anchor = GridBagConstraints.WEST;
		gbcJButtonSaveResults.gridx = 2;
		gbcJButtonSaveResults.gridy = 5;
		contentPane.add(jButtonSaveResults, gbcJButtonSaveResults);

		jButtonVisualize = new JButton("Visualize Results");
		jButtonVisualize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showResultVisualization();
			}
		});
		jButtonVisualize.setEnabled(false);
		GridBagConstraints gbcJButtonVisualize = new GridBagConstraints();
		gbcJButtonVisualize.insets = new Insets(0, 0, 5, 6);
		gbcJButtonVisualize.anchor = GridBagConstraints.EAST;
		gbcJButtonVisualize.gridx = 2;
		gbcJButtonVisualize.gridy = 5;
		contentPane.add(jButtonVisualize, gbcJButtonVisualize);
	}

	private void initializeMenuBar() {
		JMenuBar jMenuBar = new JMenuBar();
		setJMenuBar(jMenuBar);

		JMenu jMenuFile = new JMenu("File");
		jMenuBar.add(jMenuFile);
		JMenuItem jMenuItemReset = new JMenuItem("Reset");
		jMenuItemReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				resetProgram();
			}
		});
		jMenuFile.add(jMenuItemReset);
		
		jMenuFile.addSeparator();
		
		JMenuItem jMenuItemLoad = new JMenuItem("Load Model Setup"); 
		jMenuItemLoad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				loadModelSetup();
			}
		});
		jMenuFile.add(jMenuItemLoad);
		
		JMenuItem jMenuItemSaveDataSet = new JMenuItem("Save Model Setup");
		jMenuItemSaveDataSet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveModelSetup();
			}
		});
		jMenuFile.add(jMenuItemSaveDataSet);
		
		JMenuItem jMenuItemLoadRandomSet = 
				new JMenuItem("Generate Model Setup");
		jMenuFile.add(jMenuItemLoadRandomSet);
		jMenuItemLoadRandomSet.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						generateModelSetup();
					}
				});

		jMenuFile.addSeparator();
		
		JMenuItem jMenuItemExit = new JMenuItem("Exit");
		jMenuItemExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		jMenuFile.add(jMenuItemExit);

		
		
		
		JMenu jMenuEdit = new JMenu("Algorithm");
		jMenuBar.add(jMenuEdit);
		
		JMenuItem jMenuItemLoadSettings = new JMenuItem("Load Settings");
		jMenuItemLoadSettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				loadAlgorithmSettings();
			}
		});
		jMenuEdit.add(jMenuItemLoadSettings);
		
		JMenuItem jMenuItemSaveSettings = new JMenuItem("Save Settings");
		jMenuItemSaveSettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveAlgorithmSettings();
			}
		});
		jMenuEdit.add(jMenuItemSaveSettings);

//		JMenuItem jMenuItemLoadDefaultConstraints = 
//			new JMenuItem("Use Default Constraints");
//		jMenuItemLoadDefaultConstraints.addActionListener(
//				new ActionListener() {
//					public void actionPerformed(ActionEvent e) {
//						setDefaultConstraints();
//					}
//				});
//		jMenuEdit.add(jMenuItemLoadDefaultConstraints);
		

			
		JMenu jMenuOther = new JMenu("?");
		jMenuBar.add(jMenuOther);
		// POTENTIAL EXTENSIONS 
//		JMenuItem jMenuItemHelp = new JMenuItem("Help");
//		jMenuOther.add(jMenuItemHelp);
//		JMenuItem jMenuItemSupport = new JMenuItem("Support");
//		jMenuOther.add(jMenuItemSupport);
		JMenuItem jMenuItemAbout = new JMenuItem("About");
		jMenuItemAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showAboutDialog();
			}
		});
		jMenuOther.add(jMenuItemAbout);
	}

	private void initializeGeneralSettingsPanel (JPanel contentPane) {
		JPanel jPanelGeneralSettings = new JPanel();
		GridBagConstraints gbcJPanelGeneralSettings = 
			new GridBagConstraints();
		GridBagLayout gblJPanelGeneralSettings = new GridBagLayout();
		gblJPanelGeneralSettings.columnWeights = new double[]{0.2, 0.2, 0.6};
		gblJPanelGeneralSettings.rowWeights = new double[]{1.0};
		jPanelGeneralSettings.setLayout(gblJPanelGeneralSettings);
		gbcJPanelGeneralSettings.gridheight = 1;
		gbcJPanelGeneralSettings.gridwidth = 3;
		gbcJPanelGeneralSettings.fill = GridBagConstraints.BOTH;
		gbcJPanelGeneralSettings.gridx = 0;
		gbcJPanelGeneralSettings.gridy = 1;
		initializeQosConstraintsPanel(jPanelGeneralSettings);
		initializeServiceClassesPanel(jPanelGeneralSettings);
		initializeWebServicesPanel(jPanelGeneralSettings);
		contentPane.add(jPanelGeneralSettings, gbcJPanelGeneralSettings);
	}

	private void initializeQosConstraintsPanel(JPanel contentPane) {
		JPanel jPanelQosConstraints = new JPanel();
		GridBagConstraints gbcJPanelQosConstraints = new GridBagConstraints();
		gbcJPanelQosConstraints.gridheight = 1;
		gbcJPanelQosConstraints.insets = new Insets(0, 5, 5, 5);
		gbcJPanelQosConstraints.fill = GridBagConstraints.BOTH;
		gbcJPanelQosConstraints.gridx = 0;
		gbcJPanelQosConstraints.gridy = 0;
		contentPane.add(jPanelQosConstraints, gbcJPanelQosConstraints);
		GridBagLayout gblJPanelQosConstraints = new GridBagLayout();
		gblJPanelQosConstraints.columnWeights = new double[]{
				0.15, 0.3, 0.4, 0.05, 0.05, 0.05};
		gblJPanelQosConstraints.rowWeights = new double[]{
				0.15, 0.15, 0.15, 0.15, 0.15, 0.1, 0.15};
		jPanelQosConstraints.setLayout(gblJPanelQosConstraints);

		JLabel jLabelQosConstraints = new JLabel("QoS Constraints:");
		GridBagConstraints gbcJLabelQosConstraints = new GridBagConstraints();
		gbcJLabelQosConstraints.gridwidth = 4;
		gbcJLabelQosConstraints.insets = new Insets(0, 0, 5, 5);
		gbcJLabelQosConstraints.gridx = 0;
		gbcJLabelQosConstraints.gridy = 0;
		jPanelQosConstraints.add(
				jLabelQosConstraints, gbcJLabelQosConstraints);

		

		JLabel lblWeight = new JLabel("Weight");
		GridBagConstraints gbc_lblWeight = new GridBagConstraints();
		gbc_lblWeight.insets = new Insets(0, 0, 5, 5);
		gbc_lblWeight.gridx = 4;
		gbc_lblWeight.gridy = 1;
		jPanelQosConstraints.add(lblWeight, gbc_lblWeight);

		jCheckBoxMaxCosts = new JCheckBox("Max. Costs");
		jCheckBoxMaxCosts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeConstraintCheckboxStatus(jCheckBoxMaxCosts, 
						jTextFieldCostsWeight, jSliderMaxCosts);
			}
		});
		jCheckBoxMaxCosts.setSelected(true);
		GridBagConstraints gbcJCheckBoxMaxCosts = new GridBagConstraints();
		gbcJCheckBoxMaxCosts.insets = new Insets(0, 0, 5, 5);
		gbcJCheckBoxMaxCosts.gridx = 0;
		gbcJCheckBoxMaxCosts.gridy = 2;
		gbcJCheckBoxMaxCosts.anchor = GridBagConstraints.WEST;
		jPanelQosConstraints.add(jCheckBoxMaxCosts, gbcJCheckBoxMaxCosts);

		jSliderMaxCosts = new JSlider();
		jSliderMaxCosts.setMaximum(maxCosts);
		jSliderMaxCosts.setMinimum(0);
		jSliderMaxCosts.setValue(maxCosts / 2);
		jSliderMaxCosts.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				useConstraintSlider(jTextFieldMaxCosts, jSliderMaxCosts);
			}
		});
		GridBagConstraints gbcJSliderMaxCosts = new GridBagConstraints();
		gbcJSliderMaxCosts.insets = new Insets(0, 0, 5, 5);
		gbcJSliderMaxCosts.gridx = 1;
		gbcJSliderMaxCosts.gridy = 2;
		gbcJSliderMaxCosts.fill = GridBagConstraints.BOTH;
		jPanelQosConstraints.add(jSliderMaxCosts, gbcJSliderMaxCosts);

		jTextFieldMaxCosts = 
			new JTextField(String.valueOf(jSliderMaxCosts.getValue()));
		jTextFieldMaxCosts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setConstraintValueManually(jSliderMaxCosts, 
						jTextFieldMaxCosts, jSliderMaxCosts.getMinimum(),
						jSliderMaxCosts.getMaximum());
			}
		});
		jTextFieldMaxCosts.setHorizontalAlignment(JTextField.RIGHT);
		GridBagConstraints gbcJTextFieldMaxCosts = new GridBagConstraints();
		gbcJTextFieldMaxCosts.insets = new Insets(0, 0, 5, 5);
		gbcJTextFieldMaxCosts.fill = GridBagConstraints.HORIZONTAL;
		gbcJTextFieldMaxCosts.gridx = 2;
		gbcJTextFieldMaxCosts.gridy = 2;
		jPanelQosConstraints.add(jTextFieldMaxCosts, gbcJTextFieldMaxCosts);	

		JLabel jLabelMaxCosts = new JLabel("\u20AC");
		GridBagConstraints gbcJLabelMaxCosts = new GridBagConstraints();
		gbcJLabelMaxCosts.anchor = GridBagConstraints.WEST;
		gbcJLabelMaxCosts.insets = new Insets(0, 0, 5, 5);
		gbcJLabelMaxCosts.gridx = 3;
		gbcJLabelMaxCosts.gridy = 2;
		jPanelQosConstraints.add(jLabelMaxCosts, gbcJLabelMaxCosts);

		jTextFieldCostsWeight = new JTextField("34");
		jTextFieldCostsWeight.setHorizontalAlignment(JTextField.RIGHT);
		jTextFieldCostsWeight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeWeight(jTextFieldCostsWeight);
			}
		});
		GridBagConstraints gbc_txtCostsWeight = new GridBagConstraints();
		gbc_txtCostsWeight.insets = new Insets(0, 0, 5, 5);
		gbc_txtCostsWeight.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtCostsWeight.gridx = 4;
		gbc_txtCostsWeight.gridy = 2;
		jPanelQosConstraints.add(jTextFieldCostsWeight, gbc_txtCostsWeight);

		JLabel lblPercentageCostsWeight = new JLabel("%");
		GridBagConstraints gbc_lblPercentageCostsWeight = 
			new GridBagConstraints();
		gbc_lblPercentageCostsWeight.insets = new Insets(0, 0, 5, 0);
		gbc_lblPercentageCostsWeight.gridx = 5;
		gbc_lblPercentageCostsWeight.gridy = 2;
		jPanelQosConstraints.add(
				lblPercentageCostsWeight, gbc_lblPercentageCostsWeight);



		jCheckBoxMaxResponseTime = new JCheckBox("Max. Response Time");
		jCheckBoxMaxResponseTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				changeConstraintCheckboxStatus(jCheckBoxMaxResponseTime, 
						jTextFieldResponseTimeWeight, jSliderMaxResponseTime);
			}
		});
		jCheckBoxMaxResponseTime.setSelected(true);
		GridBagConstraints gbcJCheckBoxMaxResponseTime = 
			new GridBagConstraints();
		gbcJCheckBoxMaxResponseTime.insets = new Insets(0, 0, 5, 5);
		gbcJCheckBoxMaxResponseTime.gridx = 0;
		gbcJCheckBoxMaxResponseTime.gridy = 3;
		gbcJCheckBoxMaxResponseTime.anchor = GridBagConstraints.WEST;
		jPanelQosConstraints.add(
				jCheckBoxMaxResponseTime, gbcJCheckBoxMaxResponseTime);

		jSliderMaxResponseTime = new JSlider();
		jSliderMaxResponseTime.setMaximum(maxResponseTime);
		jSliderMaxResponseTime.setMinimum(0);
		jSliderMaxResponseTime.setValue(maxResponseTime / 2);
		jSliderMaxResponseTime.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				useConstraintSlider(
						jTextFieldMaxResponseTime, jSliderMaxResponseTime);
			}
		});
		GridBagConstraints gbcJSliderMaxResponseTime = 
			new GridBagConstraints();
		gbcJSliderMaxResponseTime.insets = new Insets(0, 0, 5, 5);
		gbcJSliderMaxResponseTime.gridx = 1;
		gbcJSliderMaxResponseTime.gridy = 3;
		gbcJSliderMaxResponseTime.fill = GridBagConstraints.BOTH;
		jPanelQosConstraints.add(
				jSliderMaxResponseTime, gbcJSliderMaxResponseTime);

		jTextFieldMaxResponseTime = new JTextField(
				String.valueOf(jSliderMaxResponseTime.getValue()));
		jTextFieldMaxResponseTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setConstraintValueManually(jSliderMaxResponseTime, 
						jTextFieldMaxResponseTime, 
						jSliderMaxResponseTime.getMinimum(), 
						jSliderMaxResponseTime.getMaximum());
			}
		});
		jTextFieldMaxResponseTime.setHorizontalAlignment(JTextField.RIGHT);
		GridBagConstraints gbcJTextFieldMaxResponseTime = 
			new GridBagConstraints();
		gbcJTextFieldMaxResponseTime.insets = new Insets(0, 0, 5, 5);
		gbcJTextFieldMaxResponseTime.fill = GridBagConstraints.HORIZONTAL;
		gbcJTextFieldMaxResponseTime.gridx = 2;
		gbcJTextFieldMaxResponseTime.gridy = 3;
		jPanelQosConstraints.add(
				jTextFieldMaxResponseTime, gbcJTextFieldMaxResponseTime);

		JLabel jLabelMaxResponseTime = new JLabel("ms");
		GridBagConstraints gbcJLabelMaxResponseTime = 
			new GridBagConstraints();
		gbcJLabelMaxResponseTime.anchor = GridBagConstraints.WEST;
		gbcJLabelMaxResponseTime.insets = new Insets(0, 0, 5, 5);
		gbcJLabelMaxResponseTime.gridx = 3;
		gbcJLabelMaxResponseTime.gridy = 3;
		jPanelQosConstraints.add(
				jLabelMaxResponseTime, gbcJLabelMaxResponseTime);

		jTextFieldResponseTimeWeight = new JTextField("33");
		jTextFieldResponseTimeWeight.setHorizontalAlignment(JTextField.RIGHT);
		jTextFieldResponseTimeWeight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeWeight(jTextFieldResponseTimeWeight);
			}
		});
		GridBagConstraints gbc_txtResponseTimeWeight = 
			new GridBagConstraints();
		gbc_txtResponseTimeWeight.insets = new Insets(0, 0, 5, 5);
		gbc_txtResponseTimeWeight.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtResponseTimeWeight.gridx = 4;
		gbc_txtResponseTimeWeight.gridy = 3;
		jPanelQosConstraints.add(
				jTextFieldResponseTimeWeight, gbc_txtResponseTimeWeight);

		JLabel lblPercentageResponseTimeWeight = new JLabel("%");
		GridBagConstraints gbc_lblPercentageResponseTimeWeight = 
			new GridBagConstraints();
		gbc_lblPercentageResponseTimeWeight.insets = new Insets(0, 0, 5, 0);
		gbc_lblPercentageResponseTimeWeight.gridx = 5;
		gbc_lblPercentageResponseTimeWeight.gridy = 3;
		jPanelQosConstraints.add(
				lblPercentageResponseTimeWeight, 
				gbc_lblPercentageResponseTimeWeight);



		jCheckBoxMinAvailability = new JCheckBox("Min. Availability");
		jCheckBoxMinAvailability.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				changeConstraintCheckboxStatus(jCheckBoxMinAvailability, 
						jTextFieldAvailabilityWeight, jSliderMinAvailability);
			}
		});
		jCheckBoxMinAvailability.setSelected(true);
		GridBagConstraints gbcJCheckBoxMinAvailability = 
			new GridBagConstraints();
		gbcJCheckBoxMinAvailability.insets = new Insets(0, 0, 5, 5);
		gbcJCheckBoxMinAvailability.gridx = 0;
		gbcJCheckBoxMinAvailability.gridy = 4;
		gbcJCheckBoxMinAvailability.anchor = GridBagConstraints.WEST;
		jPanelQosConstraints.add(
				jCheckBoxMinAvailability, gbcJCheckBoxMinAvailability);

		jSliderMinAvailability = new JSlider();
		jSliderMinAvailability.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				useConstraintSlider(
						jTextFieldMinAvailability, jSliderMinAvailability);
			}
		});
		GridBagConstraints gbcJSliderMinAvailability = 
			new GridBagConstraints();
		gbcJSliderMinAvailability.insets = new Insets(0, 0, 5, 5);
		gbcJSliderMinAvailability.gridx = 1;
		gbcJSliderMinAvailability.gridy = 4;
		gbcJSliderMinAvailability.fill = GridBagConstraints.BOTH;
		jPanelQosConstraints.add(
				jSliderMinAvailability, gbcJSliderMinAvailability);

		jTextFieldMinAvailability = new JTextField(
				String.valueOf(jSliderMinAvailability.getValue()));
		jTextFieldMinAvailability.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setConstraintValueManually(jSliderMinAvailability, 
						jTextFieldMinAvailability, 
						jSliderMinAvailability.getMinimum(), 
						jSliderMinAvailability.getMaximum());
			}
		});
		jTextFieldMinAvailability.setHorizontalAlignment(JTextField.RIGHT);
		GridBagConstraints gbcJTextFieldMinAvailability = 
			new GridBagConstraints();
		gbcJTextFieldMinAvailability.insets = new Insets(0, 0, 5, 5);
		gbcJTextFieldMinAvailability.fill = GridBagConstraints.HORIZONTAL;
		gbcJTextFieldMinAvailability.gridx = 2;
		gbcJTextFieldMinAvailability.gridy = 4;
		jPanelQosConstraints.add(
				jTextFieldMinAvailability, gbcJTextFieldMinAvailability);

		JLabel jLabelMinAvailability = new JLabel("%");
		GridBagConstraints gbcJLabelMinAvailability = new GridBagConstraints();
		gbcJLabelMinAvailability.anchor = GridBagConstraints.WEST;
		gbcJLabelMinAvailability.insets = new Insets(0, 0, 5, 5);
		gbcJLabelMinAvailability.gridx = 3;
		gbcJLabelMinAvailability.gridy = 4;
		jPanelQosConstraints.add(
				jLabelMinAvailability, gbcJLabelMinAvailability);

		jTextFieldAvailabilityWeight = new JTextField("33");
		jTextFieldAvailabilityWeight.setHorizontalAlignment(JTextField.RIGHT);
		jTextFieldAvailabilityWeight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeWeight(jTextFieldAvailabilityWeight);
			}
		});
		GridBagConstraints gbc_txtAvailabilityWeight = 
			new GridBagConstraints();
		gbc_txtAvailabilityWeight.insets = new Insets(0, 0, 5, 5);
		gbc_txtAvailabilityWeight.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtAvailabilityWeight.gridx = 4;
		gbc_txtAvailabilityWeight.gridy = 4;
		jPanelQosConstraints.add(
				jTextFieldAvailabilityWeight, gbc_txtAvailabilityWeight);

		JLabel lblPercentageAvailabilityWeight = new JLabel("%");
		GridBagConstraints gbc_lblPercentageAvailabilityWeight = 
			new GridBagConstraints();
		gbc_lblPercentageAvailabilityWeight.insets = new Insets(0, 0, 5, 0);
		gbc_lblPercentageAvailabilityWeight.gridx = 5;
		gbc_lblPercentageAvailabilityWeight.gridy = 4;
		jPanelQosConstraints.add(
				lblPercentageAvailabilityWeight, 
				gbc_lblPercentageAvailabilityWeight);

		JSeparator separatorWeights = new JSeparator();
		GridBagConstraints gbc_separatorWeights = new GridBagConstraints();
		gbc_separatorWeights.insets = new Insets(0, 0, 0, 5);
		gbc_separatorWeights.fill = GridBagConstraints.HORIZONTAL;
		gbc_separatorWeights.anchor = GridBagConstraints.SOUTH;
		gbc_separatorWeights.gridwidth = 2;
		gbc_separatorWeights.gridx = 4;
		gbc_separatorWeights.gridy = 5;
		jPanelQosConstraints.add(separatorWeights, gbc_separatorWeights);
		
		jCheckBoxRelaxation = new JCheckBox("Constraint Relaxation");
		jCheckBoxRelaxation.setToolTipText("<html>Use this slider to set " +
				"all constraints automatically<br>" +
				"cons<sup>(i)</sup> = coeff<sub>relax</sub> * " +
				"(max<sup>(i)</sup> - min<sup>(i)</sup>) + " +
				"min<sup>(i)</sup></html>");
		jCheckBoxRelaxation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkRelaxationStatus();
			}
		});
		GridBagConstraints gbcJCheckBoxRelaxation = new GridBagConstraints();
		gbcJCheckBoxRelaxation.insets = new Insets(0, 0, 5, 5);
		gbcJCheckBoxRelaxation.gridx = 0;
		gbcJCheckBoxRelaxation.gridy = 5;
		gbcJCheckBoxRelaxation.anchor = GridBagConstraints.WEST;
		jPanelQosConstraints.add(jCheckBoxRelaxation, gbcJCheckBoxRelaxation);
		
		jSliderRelaxation = new JSlider();
		jSliderRelaxation.setMinimum(0);
		jSliderRelaxation.setMaximum(100);
		jSliderRelaxation.setValue((int) (DEFAULT_RELAXATION * 100));
		jSliderRelaxation.setEnabled(false);
		jSliderRelaxation.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				useRelaxationSlider();
			}
		});
		GridBagConstraints gbcJSliderRelaxation = new GridBagConstraints();
		gbcJSliderRelaxation.insets = new Insets(5, 0, 5, 5);
		gbcJSliderRelaxation.fill = GridBagConstraints.BOTH;
		gbcJSliderRelaxation.gridx = 1;
		gbcJSliderRelaxation.gridy = 5;
		jPanelQosConstraints.add(jSliderRelaxation, gbcJSliderRelaxation);
		
		jTextFieldRelaxation = new JTextField("-");
		jTextFieldRelaxation.setHorizontalAlignment(JTextField.RIGHT);
		jTextFieldRelaxation.setEditable(false);
		GridBagConstraints gbcJTextFieldRelaxation = new GridBagConstraints();
		gbcJTextFieldRelaxation.insets = new Insets(0, 0, 5, 5);
		gbcJTextFieldRelaxation.fill = GridBagConstraints.HORIZONTAL;
		gbcJTextFieldRelaxation.gridx = 2;
		gbcJTextFieldRelaxation.gridy = 5;
		jPanelQosConstraints.add(
				jTextFieldRelaxation, gbcJTextFieldRelaxation);

		
		jCheckBoxBenchmarkMode = new JCheckBox("Benchmark Mode");
		jCheckBoxBenchmarkMode.setToolTipText("<html>Select this checkbox " +
				"to disable all additional features<br>" +
				"(Needed for better runtime comparisons)</html>)");
		jCheckBoxBenchmarkMode.setSelected(false);
		GridBagConstraints gbcJCheckBoxBenchmarkMode = 
			new GridBagConstraints();
		gbcJCheckBoxBenchmarkMode.insets = new Insets(5, 0, 5, 5);
		gbcJCheckBoxBenchmarkMode.gridx = 0;
		gbcJCheckBoxBenchmarkMode.gridy = 6;
		gbcJCheckBoxBenchmarkMode.anchor = GridBagConstraints.WEST;
		jPanelQosConstraints.add(
				jCheckBoxBenchmarkMode, gbcJCheckBoxBenchmarkMode);
		
		JPanel jPanelWeightSum = new JPanel();
		GridBagLayout gblJPanelWeightSum = new GridBagLayout();
		gblJPanelWeightSum.columnWeights = new double[] {1.0, 1.0};
		gblJPanelWeightSum.rowWeights = new double[] {1.0};
		jPanelWeightSum.setLayout(gblJPanelWeightSum);
		GridBagConstraints gbcJPanelWeightSum = new GridBagConstraints();
		gbcJPanelWeightSum.anchor = GridBagConstraints.WEST;
		gbcJPanelWeightSum.fill = GridBagConstraints.HORIZONTAL;
		gbcJPanelWeightSum.insets = new Insets(0, 0, 0, 7);
		gbcJPanelWeightSum.gridx = 4;
		gbcJPanelWeightSum.gridy = 6;
		jPanelQosConstraints.add(
				jPanelWeightSum, gbcJPanelWeightSum);
		
		lblWeightSumSigma = new JLabel("\u03A3");
		lblWeightSumSigma.setForeground(Color.GREEN);
		GridBagConstraints gbc_lblWeightSumSigma = new GridBagConstraints();
		gbc_lblWeightSumSigma.gridx = 0;
		gbc_lblWeightSumSigma.gridy = 0;
		gbc_lblWeightSumSigma.anchor = GridBagConstraints.WEST;
		jPanelWeightSum.add(lblWeightSumSigma, gbc_lblWeightSumSigma);

		lblWeightSum = new JLabel("100");
		lblWeightSum.setForeground(Color.GREEN);
		GridBagConstraints gbc_lblWeightSum = new GridBagConstraints();
		gbc_lblWeightSum.gridx = 1;
		gbc_lblWeightSum.gridy = 0;
		gbc_lblWeightSum.anchor = GridBagConstraints.EAST;
		jPanelWeightSum.add(lblWeightSum, gbc_lblWeightSum);

		JLabel lblPercentageWeightSum = new JLabel("%");
		GridBagConstraints gbc_lblPercentageWeightSum = 
			new GridBagConstraints();
		gbc_lblPercentageWeightSum.insets = new Insets(0, 0, 0, 0);
		gbc_lblPercentageWeightSum.gridx = 5;
		gbc_lblPercentageWeightSum.gridy = 6;
		jPanelQosConstraints.add(
				lblPercentageWeightSum, gbc_lblPercentageWeightSum);
	}

	private void initializeServiceClassesPanel(JPanel contentPane) {
		JPanel jPanelServiceClasses = new JPanel();
		GridBagConstraints gbcJPanelServiceClasses = 
			new GridBagConstraints();
		gbcJPanelServiceClasses.gridheight = 1;
		gbcJPanelServiceClasses.insets = new Insets(0, 0, 5, 5);
		gbcJPanelServiceClasses.fill = GridBagConstraints.BOTH;
		gbcJPanelServiceClasses.gridx = 1;
		gbcJPanelServiceClasses.gridy = 0;
		contentPane.add(jPanelServiceClasses, gbcJPanelServiceClasses);
		GridBagLayout gblJPanelServiceClasses = new GridBagLayout();
		gblJPanelServiceClasses.columnWeights = new double[]{1.0};
		gblJPanelServiceClasses.rowWeights = new double[]{0.0, 1.0};
		jPanelServiceClasses.setLayout(gblJPanelServiceClasses);

		JLabel jLabelServiceFunctions = new JLabel("Service Classes:");
		GridBagConstraints gbcJLabelServiceFunctions = 
			new GridBagConstraints();
		gbcJLabelServiceFunctions.insets = new Insets(0, 0, 5, 0);
		gbcJLabelServiceFunctions.gridx = 0;
		gbcJLabelServiceFunctions.gridy = 0;
		jPanelServiceClasses.add(
				jLabelServiceFunctions, gbcJLabelServiceFunctions);

		JScrollPane jScrollPaneServiceClasses = new JScrollPane();
		GridBagConstraints gbcJScrollPaneServiceClasses = 
			new GridBagConstraints();
		gbcJScrollPaneServiceClasses.fill = GridBagConstraints.BOTH;
		gbcJScrollPaneServiceClasses.gridx = 0;
		gbcJScrollPaneServiceClasses.gridy = 1;
		jPanelServiceClasses.add(
				jScrollPaneServiceClasses, gbcJScrollPaneServiceClasses);

		jTableServiceClasses = new ServiceSelectionTable(0, 2, false);
		jTableServiceClasses.setEnabled(false);		
		jTableServiceClasses.getColumnModel().getColumn(0).setHeaderValue(
		"ID");
		jTableServiceClasses.getColumnModel().getColumn(1).setHeaderValue(
		"Name");

		jScrollPaneServiceClasses.setViewportView(jTableServiceClasses);
	}

	private void initializeWebServicesPanel(JPanel contentPane) {
		JPanel jPanelWebServices = new JPanel();
		GridBagConstraints gbcJPanelWebServices = new GridBagConstraints();
		gbcJPanelWebServices.gridheight = 1;
		gbcJPanelWebServices.insets = new Insets(0, 0, 5, 5);
		gbcJPanelWebServices.fill = GridBagConstraints.BOTH;
		gbcJPanelWebServices.gridx = 2;
		gbcJPanelWebServices.gridy = 0;
		contentPane.add(jPanelWebServices, gbcJPanelWebServices);
		GridBagLayout gblJPanelWebServices = new GridBagLayout();
		gblJPanelWebServices.columnWeights = new double[]{1.0};
		gblJPanelWebServices.rowWeights = new double[]{0.0, 1.0};
		jPanelWebServices.setLayout(gblJPanelWebServices);

		JLabel jLabelWebServices = new JLabel("Web Services:");
		GridBagConstraints gbcJLabelWebServices = new GridBagConstraints();
		gbcJLabelWebServices.insets = new Insets(0, 0, 5, 0);
		gbcJLabelWebServices.gridx = 0;
		gbcJLabelWebServices.gridy = 0;
		jPanelWebServices.add(jLabelWebServices, gbcJLabelWebServices);

		JScrollPane jScrollPaneWebServices = new JScrollPane();
		jScrollPaneWebServices.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbcJScrollPaneWebServices = 
			new GridBagConstraints();
		gbcJScrollPaneWebServices.fill = GridBagConstraints.BOTH;
		gbcJScrollPaneWebServices.gridx = 0;
		gbcJScrollPaneWebServices.gridy = 1;
		jPanelWebServices.add(
				jScrollPaneWebServices, gbcJScrollPaneWebServices);

		jTableWebServices = new ServiceSelectionTable(0, 6, false);
		jTableWebServices.setEnabled(false);
		jTableWebServices.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jTableWebServices.getColumnModel().getColumn(0).setHeaderValue(
				"ServiceClass");
		jTableWebServices.getColumnModel().getColumn(1).setHeaderValue(
		"ID");
		jTableWebServices.getColumnModel().getColumn(2).setHeaderValue(
		"Name");		
		jTableWebServices.getColumnModel().getColumn(3).setHeaderValue(
		"Costs");
		jTableWebServices.getColumnModel().getColumn(4).setHeaderValue(
		"ResponseTime");
		jTableWebServices.getColumnModel().getColumn(5).setHeaderValue(
		"Availability");		
		jScrollPaneWebServices.setViewportView(jTableWebServices);
	}

	private void initializeCenterArea(JPanel contentPane) {
		JPanel jPanelUtilityFunction = new JPanel();
		jPanelUtilityFunction.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbcJPanelUtilityFunction = 
			new GridBagConstraints();
		gbcJPanelUtilityFunction.gridwidth = 3;
		gbcJPanelUtilityFunction.insets = new Insets(0, 5, 5, 5);
		gbcJPanelUtilityFunction.fill = GridBagConstraints.BOTH;
		gbcJPanelUtilityFunction.gridx = 0;
		gbcJPanelUtilityFunction.gridy = 2;
		contentPane.add(jPanelUtilityFunction, gbcJPanelUtilityFunction);
		GridBagLayout gblJPanelUtilityFunction = new GridBagLayout();
		gblJPanelUtilityFunction.columnWeights = 
			new double[]{0.25, 0.5, 0.2, 0.05};
		gblJPanelUtilityFunction.rowWeights = new double[]{0.33, 0.33, 0.33};
		jPanelUtilityFunction.setLayout(gblJPanelUtilityFunction);

		Font fontUtilityFunction = 
			new Font("utilityFunction", Font.BOLD, 16);
		JLabel jLabelUtilityFunction = new JLabel("Utility Function:");
		jLabelUtilityFunction.setFont(fontUtilityFunction);
		GridBagConstraints gbc_lblUtilityFunction = new GridBagConstraints();
		gbc_lblUtilityFunction.gridheight = 3;
		gbc_lblUtilityFunction.fill = GridBagConstraints.BOTH;
		gbc_lblUtilityFunction.anchor = GridBagConstraints.CENTER;
		gbc_lblUtilityFunction.insets = new Insets(0, 25, 0, 5);
		gbc_lblUtilityFunction.gridx = 0;
		gbc_lblUtilityFunction.gridy = 0;
		jPanelUtilityFunction.add(
				jLabelUtilityFunction, gbc_lblUtilityFunction);
		
		
		Font fontUtilityText = 
				new Font("utilityText", Font.ITALIC, 14);
		jLabelUtilityText = new JLabel();
		getUtilityFunction();	
		jLabelUtilityText.setFont(fontUtilityText);
		GridBagConstraints gbc_lblUtilityText = new GridBagConstraints();
		gbc_lblUtilityText.gridheight = 1;
		gbc_lblUtilityText.gridwidth = 2;
		gbc_lblUtilityText.fill = GridBagConstraints.BOTH;
		gbc_lblUtilityText.anchor = GridBagConstraints.WEST;
		gbc_lblUtilityText.insets = new Insets(0, 25, 0, 5);
		gbc_lblUtilityText.gridx = 1;
		gbc_lblUtilityText.gridy = 1;
		jPanelUtilityFunction.add(
				jLabelUtilityText, gbc_lblUtilityText);
		 
		jButtonStart = new JButton("Start");
		jButtonStart.setEnabled(false);
		jButtonStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				pressStartButton();
			}
		});
		GridBagConstraints gbcJButtonStart = new GridBagConstraints();
		gbcJButtonStart.gridheight = 3;
		gbcJButtonStart.fill = GridBagConstraints.VERTICAL;
		gbcJButtonStart.anchor = GridBagConstraints.EAST;
		gbcJButtonStart.insets = new Insets(5, 0, 5, 5);
		gbcJButtonStart.gridx = 3;
		gbcJButtonStart.gridy = 0;
		jPanelUtilityFunction.add(jButtonStart, gbcJButtonStart);
	}

	private void initializeGeneticAlgorithmPanel(JPanel contentPane) {
		JPanel jPanelGeneticAlgorithm = new JPanel();
		GridBagLayout gblJPanelGeneticAlgorithm = new GridBagLayout();
		gblJPanelGeneticAlgorithm.columnWeights = new double[]{1.0};
		gblJPanelGeneticAlgorithm.rowWeights = 
			new double[]{0.1, 0.8, 0.1};
		jPanelGeneticAlgorithm.setLayout(gblJPanelGeneticAlgorithm);
		GridBagConstraints gbcGeneticAlgorithm = new GridBagConstraints();
		gbcGeneticAlgorithm.gridheight = 1;
		gbcGeneticAlgorithm.insets = new Insets(0, 5, 5, 5);
		gbcGeneticAlgorithm.fill = GridBagConstraints.BOTH;
		gbcGeneticAlgorithm.gridx = 0;
		gbcGeneticAlgorithm.gridy = 3;
		contentPane.add(jPanelGeneticAlgorithm, gbcGeneticAlgorithm);

		jCheckboxGeneticAlgorithm = new JCheckBox("Genetic Algorithm");
		jCheckboxGeneticAlgorithm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseAlgorithm("genAlg");
			}
		});
		jCheckboxGeneticAlgorithm.setSelected(true);
		GridBagConstraints gbcJCheckboxGeneticAlgorithm = 
			new GridBagConstraints();
		gbcJCheckboxGeneticAlgorithm.anchor = GridBagConstraints.NORTH;
		gbcJCheckboxGeneticAlgorithm.insets = new Insets(0, 0, 5, 0);
		gbcJCheckboxGeneticAlgorithm.gridx = 0;
		gbcJCheckboxGeneticAlgorithm.gridy = 0;
		jPanelGeneticAlgorithm.add(
				jCheckboxGeneticAlgorithm, gbcJCheckboxGeneticAlgorithm);

		JScrollPane jScrollPaneGeneticAlgorithm = new JScrollPane();
		GridBagConstraints gbcJScrollPaneGeneticAlgorithm = 
			new GridBagConstraints();
		gbcJScrollPaneGeneticAlgorithm.insets = new Insets(0, 0, 5, 0);
		gbcJScrollPaneGeneticAlgorithm.fill = GridBagConstraints.BOTH;
		gbcJScrollPaneGeneticAlgorithm.gridx = 0;
		gbcJScrollPaneGeneticAlgorithm.gridy = 1;
		jPanelGeneticAlgorithm.add(
				jScrollPaneGeneticAlgorithm, gbcJScrollPaneGeneticAlgorithm);

		JPanel jPanelGeneticAlgorithmSettings = new JPanel();
		jScrollPaneGeneticAlgorithm.setViewportView(
				jPanelGeneticAlgorithmSettings);
		GridBagLayout gblJPanelGeneticAlgorithmSettings = new GridBagLayout();
		gblJPanelGeneticAlgorithmSettings.columnWeights = 
			new double[]{0.3, 1.0, 1.0};
		gblJPanelGeneticAlgorithmSettings.rowWeights = 
			new double[]{0.2, 0.1, 0.2, 1.0, 1.0,
						 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
		jPanelGeneticAlgorithmSettings.setLayout(
				gblJPanelGeneticAlgorithmSettings);
		
		Font fontBraceLeft = new Font("braceLeft", Font.PLAIN, 40);
		Font fontFormula = new Font("formula", Font.ITALIC, 10);
		
		JPanel jPanelFitness = new JPanel();
		GridBagLayout gblJPanelFitness = new GridBagLayout();
		gblJPanelFitness.columnWeights = new double[] {1.0, 1.0, 1.0, 1.0};
		gblJPanelFitness.rowWeights = new double[] {0.5, 0.5};
		jPanelFitness.setLayout(gblJPanelFitness);
		GridBagConstraints gbcJPanelFitness = new GridBagConstraints();
		gbcJPanelFitness.anchor = GridBagConstraints.WEST;
		gbcJPanelFitness.insets = new Insets(5, 5, 5, 5);
		gbcJPanelFitness.gridwidth = 3;
		gbcJPanelFitness.gridx = 0;
		gbcJPanelFitness.gridy = 0;
		jPanelGeneticAlgorithmSettings.add(jPanelFitness, gbcJPanelFitness);
		
		JLabel jLabelFitnessFunction = new JLabel(
				"<html><i>Fitness = </i></html>");
		GridBagConstraints gbcJLabelFitnessFunction = new GridBagConstraints();
		gbcJLabelFitnessFunction.insets = new Insets(5, 0, 8, 0);
		gbcJLabelFitnessFunction.anchor = GridBagConstraints.WEST;
		gbcJLabelFitnessFunction.gridx = 0;
		gbcJLabelFitnessFunction.gridy = 0;
		gbcJLabelFitnessFunction.gridheight = 2;
		jPanelFitness.add(jLabelFitnessFunction, gbcJLabelFitnessFunction);
//		jLabelFitnessFunction.setFont(fontFormula);
		
		jLabelFitnessBraceLeft = new JLabel("{");
		GridBagConstraints gbcJLabelFitnessBraceLeft = 
				new GridBagConstraints();
		gbcJLabelFitnessBraceLeft.insets = new Insets(0, 0, 12, 5);
		gbcJLabelFitnessBraceLeft.anchor = GridBagConstraints.WEST;
		gbcJLabelFitnessBraceLeft.fill = GridBagConstraints.VERTICAL;
		gbcJLabelFitnessBraceLeft.gridx = 1;
		gbcJLabelFitnessBraceLeft.gridy = 0;
		gbcJLabelFitnessBraceLeft.gridheight = 2;
		jPanelFitness.add(jLabelFitnessBraceLeft, gbcJLabelFitnessBraceLeft);
		jLabelFitnessBraceLeft.setFont(fontBraceLeft);
		
		jLabelFitnessCaseOne = new JLabel("Utility + 1");
		GridBagConstraints gbcJLabelUtilityCaseOne = new GridBagConstraints();
		gbcJLabelUtilityCaseOne.insets = new Insets(10, 0, 0, 0);
		gbcJLabelUtilityCaseOne.anchor = GridBagConstraints.WEST;
		gbcJLabelUtilityCaseOne.gridx = 2;
		gbcJLabelUtilityCaseOne.gridy = 0;
		jPanelFitness.add(jLabelFitnessCaseOne, gbcJLabelUtilityCaseOne);
		jLabelFitnessCaseOne.setFont(fontFormula);
		
		jLabelFitnessCaseOneDescription = 
				new JLabel("if no constraints violated");
		GridBagConstraints gbcJLabelUtilityCaseOneDescription = 
				new GridBagConstraints();
		gbcJLabelUtilityCaseOneDescription.insets = new Insets(15, 5, 0, 0);
		gbcJLabelUtilityCaseOneDescription.anchor = GridBagConstraints.WEST;
		gbcJLabelUtilityCaseOneDescription.gridx = 3;
		gbcJLabelUtilityCaseOneDescription.gridy = 0;
		jPanelFitness.add(jLabelFitnessCaseOneDescription, 
				gbcJLabelUtilityCaseOneDescription);
		jLabelFitnessCaseOneDescription.setFont(fontFormula);

		jLabelFitnessCaseTwo = new JLabel();
		GridBagConstraints gbcJLabelUtilityCaseTwo = 
			new GridBagConstraints();
		gbcJLabelUtilityCaseTwo.anchor = GridBagConstraints.WEST;
		gbcJLabelUtilityCaseTwo.insets = new Insets(0, 0, 10, 0);
		gbcJLabelUtilityCaseTwo.gridx = 2;
		gbcJLabelUtilityCaseTwo.gridy = 1;
		jPanelFitness.add(jLabelFitnessCaseTwo, gbcJLabelUtilityCaseTwo);
		jLabelFitnessCaseTwo.setFont(fontFormula);
		
		jLabelFitnessCaseTwoDescription = new JLabel("otherwise");
		GridBagConstraints gbcJLabelUtilityCaseTwoDescription = 
				new GridBagConstraints();
		gbcJLabelUtilityCaseTwoDescription.insets = new Insets(0, 5, 15, 0);
		gbcJLabelUtilityCaseTwoDescription.anchor = GridBagConstraints.WEST;
		gbcJLabelUtilityCaseTwoDescription.gridx = 3;
		gbcJLabelUtilityCaseTwoDescription.gridy = 1;
		jPanelFitness.add(jLabelFitnessCaseTwoDescription, 
				gbcJLabelUtilityCaseTwoDescription);
		jLabelFitnessCaseTwoDescription.setFont(fontFormula);
		
		
		
		JLabel jLabelPopulationSize = new JLabel("Population Size:");
		GridBagConstraints gbcJLabelPopulationSize = 
			new GridBagConstraints();
		gbcJLabelPopulationSize.anchor = GridBagConstraints.WEST;
		gbcJLabelPopulationSize.insets = new Insets(5, 5, 5, 5);
		gbcJLabelPopulationSize.gridx = 0;
		gbcJLabelPopulationSize.gridy = 4;
		jPanelGeneticAlgorithmSettings.add(jLabelPopulationSize, 
				gbcJLabelPopulationSize);
		
		JPanel jPanelPopulationSize = new JPanel();
		GridBagLayout gblJPanelPopulationSize = new GridBagLayout();
		gblJPanelPopulationSize.columnWeights = new double[] {1.0, 1.0};
		gblJPanelPopulationSize.rowWeights = new double[] {1.0};
		jPanelPopulationSize.setLayout(gblJPanelPopulationSize);
		GridBagConstraints gbcJPanelPopulationSize = new GridBagConstraints();
		gbcJPanelPopulationSize.anchor = GridBagConstraints.WEST;
		gbcJPanelPopulationSize.gridwidth = 2;
		gbcJPanelPopulationSize.gridx = 1;
		gbcJPanelPopulationSize.gridy = 4;
		jPanelGeneticAlgorithmSettings.add(jPanelPopulationSize, 
				gbcJPanelPopulationSize);
		
		jTextFieldPopulationSize = new JTextField(
				String.valueOf(DEFAULT_START_POPULATION_SIZE));
		jTextFieldPopulationSize.setColumns(3);
		jTextFieldPopulationSize.setHorizontalAlignment(JTextField.RIGHT);
		jTextFieldPopulationSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkInputValue(jTextFieldPopulationSize, 
						MAX_START_POPULATION_SIZE, 1, 
						DEFAULT_START_POPULATION_SIZE);
			}
		});
		jTextFieldPopulationSize.setToolTipText("<html>Number of " +
				"compositions in a generation<br>" +
				"Typical value: 100</html>");
		GridBagConstraints gbcJTextFieldPopulationSize = 
				new GridBagConstraints();
		gbcJTextFieldPopulationSize.insets = new Insets(5, 5, 5, 5);
		gbcJTextFieldPopulationSize.anchor = GridBagConstraints.EAST;
		gbcJTextFieldPopulationSize.gridx = 0;
		gbcJTextFieldPopulationSize.gridy = 0;
		jPanelPopulationSize.add(jTextFieldPopulationSize, 
				gbcJTextFieldPopulationSize);

//		jLabelPopulationPercentage = new JLabel();
//		GridBagConstraints gbcJLabelStartPopulationPercentage = 
//			new GridBagConstraints();
//		gbcJLabelStartPopulationPercentage.insets = new Insets(5, 0, 5, 5);
//		gbcJLabelStartPopulationPercentage.anchor = GridBagConstraints.WEST;
//		gbcJLabelStartPopulationPercentage.gridx = 1;
//		gbcJLabelStartPopulationPercentage.gridy = 0;
//		jPanelPopulationSize.add(jLabelPopulationPercentage, 
//				gbcJLabelStartPopulationPercentage);
		
		JLabel jLabelSelection = new JLabel("Selection Method:");
		GridBagConstraints gbcJLabelSelection = new GridBagConstraints();
		gbcJLabelSelection.anchor = GridBagConstraints.WEST;
		gbcJLabelSelection.insets = new Insets(10, 5, 5, 5);
		gbcJLabelSelection.gridx = 0;
		gbcJLabelSelection.gridy = 5;
		jPanelGeneticAlgorithmSettings.add(
				jLabelSelection, gbcJLabelSelection);
		
		JPanel jPanelSelection = new JPanel();
		GridBagLayout gblJPanelSelection = new GridBagLayout();
		gblJPanelSelection.columnWeights = new double[] {1.0};
		gblJPanelSelection.rowWeights = new double[] {1.0};
		jPanelSelection.setLayout(gblJPanelSelection);
		GridBagConstraints gbcJPanelSelection = new GridBagConstraints();
		gbcJPanelSelection.anchor = GridBagConstraints.WEST;
		gbcJPanelSelection.gridwidth = 2;
		gbcJPanelSelection.gridx = 1;
		gbcJPanelSelection.gridy = 5;
		jPanelGeneticAlgorithmSettings.add(
				jPanelSelection, gbcJPanelSelection);
		
		jComboBoxSelection = new JComboBox<String>();
		jComboBoxSelection.addItem("Roulette Wheel Selection");
		jComboBoxSelection.addItem("Linear Ranking Selection");
		jComboBoxSelection.addItem("Tournament Selection");
		jComboBoxSelection.setToolTipText("<html>Method used to determine " +
				"the mating pool,<br>i.e. the compositions used for " +
				"crossover");
		GridBagConstraints gbcJComboBoxSelection = 
			new GridBagConstraints();
		gbcJComboBoxSelection.insets = new Insets(5, 5, 5, 5);
		gbcJComboBoxSelection.anchor = GridBagConstraints.EAST;
		gbcJComboBoxSelection.gridx = 0;
		gbcJComboBoxSelection.gridy = 0;
		jPanelSelection.add(jComboBoxSelection, 
				gbcJComboBoxSelection);		
		
		
		
		JPanel jPanelElitismRate = new JPanel();
		GridBagLayout gblJPanelElitismRate = new GridBagLayout();
		gblJPanelElitismRate.columnWeights = new double[] {1.0};
		gblJPanelElitismRate.rowWeights = new double[] {1.0};
		jPanelElitismRate.setLayout(gblJPanelElitismRate);
		GridBagConstraints gbcJPanelElitismRate = new GridBagConstraints();
		gbcJPanelElitismRate.anchor = GridBagConstraints.WEST;
		gbcJPanelElitismRate.gridwidth = 2;
		gbcJPanelElitismRate.gridx = 1;
		gbcJPanelElitismRate.gridy = 6;
		jPanelGeneticAlgorithmSettings.add(
				jPanelElitismRate, gbcJPanelElitismRate);
		
		jCheckBoxElitismRate = new JCheckBox("Elitism Rate:");
		jCheckBoxElitismRate.setSelected(true);
		jCheckBoxElitismRate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setElitismRateSelection();
			}
		});
		jCheckBoxElitismRate.setToolTipText("<html>If activated, the best " +
				"compositions will<br>be preserved for the next " +
				"generation</html>");
		GridBagConstraints gbcJCheckBoxElitismRate = new GridBagConstraints();
		gbcJCheckBoxElitismRate.anchor = GridBagConstraints.WEST;
		gbcJCheckBoxElitismRate.insets = new Insets(5, 5, 5, 0);
		gbcJCheckBoxElitismRate.gridx = 0;
		gbcJCheckBoxElitismRate.gridy = 0;
		jPanelElitismRate.add(
				jCheckBoxElitismRate, gbcJCheckBoxElitismRate);
		
		jTextFieldElitismRate = new JTextField(
				String.valueOf(DEFAULT_ELITISM_RATE));
		jTextFieldElitismRate.setColumns(2);
		jTextFieldElitismRate.setHorizontalAlignment(
				JTextField.RIGHT);
		jTextFieldElitismRate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				checkInputValue(jTextFieldElitismRate,
						100, 1, DEFAULT_ELITISM_RATE);
			}
		});
		jTextFieldElitismRate.setToolTipText("<html>Percentage of preserved " +
				"compositions (relative to the population size)<br>" +
				"Typical value: 1%</html>");
		GridBagConstraints gbcJTextFieldElitismRate = 
			new GridBagConstraints();
		gbcJTextFieldElitismRate.insets = new Insets(5, 5, 5, 0);
		gbcJTextFieldElitismRate.anchor = GridBagConstraints.WEST;
		gbcJTextFieldElitismRate.gridx = 1;
		gbcJTextFieldElitismRate.gridy = 0;
		jPanelElitismRate.add(jTextFieldElitismRate, 
				gbcJTextFieldElitismRate);
		
		jLabelElitismRatePercentage = new JLabel("%");
		GridBagConstraints gbcJLabelElitismRatePercentage = 
			new GridBagConstraints();
		gbcJLabelElitismRatePercentage.insets = new Insets(5, 5, 5, 5);
		gbcJLabelElitismRatePercentage.anchor = GridBagConstraints.WEST;
		gbcJLabelElitismRatePercentage.gridx = 2;
		gbcJLabelElitismRatePercentage.gridy = 0;
		jPanelElitismRate.add(jLabelElitismRatePercentage, 
				gbcJLabelElitismRatePercentage);

		
		
		JLabel jLabelCrossover = new JLabel("Crossover Method:");
		GridBagConstraints gbcJLabelCrossover = new GridBagConstraints();
		gbcJLabelCrossover.anchor = GridBagConstraints.NORTHWEST;
		gbcJLabelCrossover.insets = new Insets(10, 5, 5, 5);
		gbcJLabelCrossover.gridx = 0;
		gbcJLabelCrossover.gridy = 7;
		jPanelGeneticAlgorithmSettings.add(
				jLabelCrossover, gbcJLabelCrossover);
		
		JPanel jPanelCrossover = new JPanel();
		GridBagLayout gblJPanelCrossover = new GridBagLayout();
		jPanelCrossover.setLayout(gblJPanelCrossover);
		GridBagConstraints gbcJPanelCrossover = new GridBagConstraints();
		gbcJPanelCrossover.anchor = GridBagConstraints.WEST;
		gbcJPanelCrossover.gridwidth = 2;
		gbcJPanelCrossover.gridx = 1;
		gbcJPanelCrossover.gridy = 7;
		jPanelGeneticAlgorithmSettings.add(
				jPanelCrossover, gbcJPanelCrossover);
		
		jComboBoxCrossover = new JComboBox<String>();
		jComboBoxCrossover.addItem("One-Point Crossover");
		jComboBoxCrossover.addItem("Two-Point Crossover");
		jComboBoxCrossover.addItem("Uniform Crossover");
		jComboBoxCrossover.setPreferredSize(
				jComboBoxSelection.getPreferredSize());
		jComboBoxCrossover.setToolTipText("<html>Method used to create " +
				"two child-compositions<br>out of two parent-compositions " +
				"contained in<br>the mating pool</html>");
		GridBagConstraints gbcJComboBoxCrossover = new GridBagConstraints();
		gbcJComboBoxCrossover.insets = new Insets(5, 5, 5, 5);
		gbcJComboBoxCrossover.anchor = GridBagConstraints.WEST;
		gbcJComboBoxCrossover.gridwidth = 3;
		gbcJComboBoxCrossover.gridx = 0;
		gbcJComboBoxCrossover.gridy = 0;
		jPanelCrossover.add(jComboBoxCrossover, 
				gbcJComboBoxCrossover);
		
		JLabel jLabelCrossoverRate = new JLabel("Crossover Rate:");
		GridBagConstraints gbcJLabelCrossoverRate = new GridBagConstraints();
		gbcJLabelCrossoverRate.anchor = GridBagConstraints.WEST;
		gbcJLabelCrossoverRate.insets = new Insets(5, 5, 5, 5);
		gbcJLabelCrossoverRate.gridx = 0;
		gbcJLabelCrossoverRate.gridy = 1;
		jPanelCrossover.add(
				jLabelCrossoverRate, gbcJLabelCrossoverRate);
		
		jTextFieldCrossoverRate = new JTextField(
				String.valueOf(DEFAULT_CROSSOVER_RATE));
		jTextFieldCrossoverRate.setColumns(2);
		jTextFieldCrossoverRate.setHorizontalAlignment(
				JTextField.RIGHT);
		jTextFieldCrossoverRate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkInputValue(jTextFieldCrossoverRate, 
						100, 0, DEFAULT_CROSSOVER_RATE);
			}
		});
		jTextFieldCrossoverRate.setToolTipText("<html>Probability for the " +
				"crossover to actually happen " +
				"<br>Typical value: 70-100%</html>");
		GridBagConstraints gbcJTextFieldCrossoverRate = 
				new GridBagConstraints();
		gbcJTextFieldCrossoverRate.insets = new Insets(5, 5, 5, 5);
		gbcJTextFieldCrossoverRate.anchor = GridBagConstraints.WEST;
		gbcJTextFieldCrossoverRate.gridx = 1;
		gbcJTextFieldCrossoverRate.gridy = 1;
		jPanelCrossover.add(jTextFieldCrossoverRate, 
				gbcJTextFieldCrossoverRate);
		
		JLabel jLabelCrossoverPercentage = new JLabel("%");
		GridBagConstraints gbcJLabelCrossoverPercentage = 
				new GridBagConstraints();
		gbcJLabelCrossoverPercentage.anchor = GridBagConstraints.WEST;
		gbcJLabelCrossoverPercentage.insets = new Insets(5, 0, 5, 5);
		gbcJLabelCrossoverPercentage.gridx = 2;
		gbcJLabelCrossoverPercentage.gridy = 1;
		jPanelCrossover.add(
				jLabelCrossoverPercentage, gbcJLabelCrossoverPercentage);
		
		
		
		
		JLabel jLabelMutationRate = new JLabel(
				"Mutation Rate:");
		GridBagConstraints gbcJLabelMutationRate = 
				new GridBagConstraints();
		gbcJLabelMutationRate.anchor = GridBagConstraints.WEST;
		gbcJLabelMutationRate.insets = new Insets(5, 5, 5, 5);
		gbcJLabelMutationRate.gridx = 0;
		gbcJLabelMutationRate.gridy = 8;
		jPanelGeneticAlgorithmSettings.add(
				jLabelMutationRate, gbcJLabelMutationRate);
		
		JPanel jPanelMutation = new JPanel();
		GridBagLayout gblJPanelMutation = new GridBagLayout();
		gblJPanelMutation.columnWeights = new double[] {1.0, 1.0};
		gblJPanelMutation.rowWeights = new double[] {1.0};
		jPanelMutation.setLayout(gblJPanelMutation);
		GridBagConstraints gbcJPanelMutation = 
			new GridBagConstraints();
		gbcJPanelMutation.anchor = GridBagConstraints.WEST;
		gbcJPanelMutation.gridwidth = 2;
		gbcJPanelMutation.gridx = 1;
		gbcJPanelMutation.gridy = 8;
		jPanelGeneticAlgorithmSettings.add(
				jPanelMutation, gbcJPanelMutation);
		
		jTextFieldMutationRate = new JTextField(
				String.valueOf(DEFAULT_MUTATION_RATE));
		jTextFieldMutationRate.setColumns(2);
		jTextFieldMutationRate.setHorizontalAlignment(
				JTextField.RIGHT);
		jTextFieldMutationRate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkInputValue(jTextFieldMutationRate, 
						1000, 0, DEFAULT_MUTATION_RATE);
			}
		});
		jTextFieldMutationRate.setToolTipText("<html>Probability for the " +
				"mutation of a service candidate " +
				"<br>Typical value: 1-80\u2030</html>");
		GridBagConstraints gbcJTextFieldMutationRate = 
			new GridBagConstraints();
		gbcJTextFieldMutationRate.insets = new Insets(5, 5, 5, 0);
		gbcJTextFieldMutationRate.anchor = GridBagConstraints.WEST;
		gbcJTextFieldMutationRate.gridx = 0;
		gbcJTextFieldMutationRate.gridy = 0;
		jPanelMutation.add(jTextFieldMutationRate, 
				gbcJTextFieldMutationRate);
		
		JLabel jLabelMutationPromille = new JLabel("\u2030");
		GridBagConstraints gbcJLabelMutationPromille = 
				new GridBagConstraints();
		gbcJLabelMutationPromille.insets = new Insets(5, 5, 5, 5);
		gbcJLabelMutationPromille.anchor = GridBagConstraints.EAST;
		gbcJLabelMutationPromille.gridx = 1;
		gbcJLabelMutationPromille.gridy = 0;
		jPanelMutation.add(jLabelMutationPromille, gbcJLabelMutationPromille);
		
		
		
		
		JLabel jLabelTerminationCriterion = new JLabel(
				"Termination Criterion:");
		GridBagConstraints gbcJLabelTerminationCriterion = 
				new GridBagConstraints();
		gbcJLabelTerminationCriterion.anchor = GridBagConstraints.WEST;
		gbcJLabelTerminationCriterion.insets = new Insets(5, 5, 5, 5);
		gbcJLabelTerminationCriterion.gridx = 0;
		gbcJLabelTerminationCriterion.gridy = 9;
		jPanelGeneticAlgorithmSettings.add(
				jLabelTerminationCriterion, gbcJLabelTerminationCriterion);
		
		JPanel jPanelTerminationCriterion = new JPanel();
		GridBagLayout gblJPanelTerminationCriterion = new GridBagLayout();
		gblJPanelTerminationCriterion.columnWeights = 
				new double[] {1.0, 0.1, 1.0};
		gblJPanelTerminationCriterion.rowWeights = new double[] {1.0, 1.0};
		jPanelTerminationCriterion.setLayout(gblJPanelTerminationCriterion);
		GridBagConstraints gbcJPanelTerminationCriterion = 
				new GridBagConstraints();
		gbcJPanelTerminationCriterion.gridwidth = 2;
		gbcJPanelTerminationCriterion.anchor = GridBagConstraints.WEST;
		gbcJPanelTerminationCriterion.gridx = 1;
		gbcJPanelTerminationCriterion.gridy = 9;
		jPanelGeneticAlgorithmSettings.add(
				jPanelTerminationCriterion, gbcJPanelTerminationCriterion);
		
		jComboBoxTerminationCriterion = new JComboBox<String>();
		jComboBoxTerminationCriterion.addItem("Number of Iterations");
		jComboBoxTerminationCriterion.addItem("Consecutive Equal Generations");
		jComboBoxTerminationCriterion.addItem("Fitness Value Convergence");
		jComboBoxTerminationCriterion.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showExtendedTerminationCriterionSettings();
			}
		});
		jComboBoxTerminationCriterion.setToolTipText("<html>Criterion used " +
				"to determine when the<br>algorithm should terminate</html>");
		GridBagConstraints gbcJComboBoxTerminationCriterion = 
			new GridBagConstraints();
		gbcJComboBoxTerminationCriterion.insets = new Insets(5, 5, 5, 5);
		gbcJComboBoxTerminationCriterion.anchor = GridBagConstraints.EAST;
		gbcJComboBoxTerminationCriterion.gridx = 0;
		gbcJComboBoxTerminationCriterion.gridy = 0;
		jPanelTerminationCriterion.add(jComboBoxTerminationCriterion, 
				gbcJComboBoxTerminationCriterion);
		
		jLabelTerminationColon = new JLabel(":");
		GridBagConstraints gbcJLabelTerminationColon = 
				new GridBagConstraints();
		gbcJLabelTerminationColon.insets = new Insets(5, 0, 5, 0);
		gbcJLabelTerminationColon.anchor = GridBagConstraints.CENTER;
		gbcJLabelTerminationColon.gridx = 1;
		gbcJLabelTerminationColon.gridy = 0;
		jPanelTerminationCriterion.add(
				jLabelTerminationColon, gbcJLabelTerminationColon);
		
		jTextFieldTerminationCriterion = new JTextField(
				String.valueOf(DEFAULT_TERMINATION_CRITERION));
		jTextFieldTerminationCriterion.setColumns(3);
		jTextFieldTerminationCriterion.setHorizontalAlignment(
				JTextField.RIGHT);
		jTextFieldTerminationCriterion.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkInputValue(jTextFieldTerminationCriterion, 
						Integer.MAX_VALUE, 1, DEFAULT_TERMINATION_CRITERION);
			}
		});
		jTextFieldTerminationCriterion.setToolTipText("<html>Number of " +
				"generations created by the algorithm<br>" +
				"Typical value: 100-1000</html>");
		GridBagConstraints gbcJTextFieldTerminationCriterion = 
			new GridBagConstraints();
		gbcJTextFieldTerminationCriterion.insets = new Insets(5, 5, 5, 0);
		gbcJTextFieldTerminationCriterion.anchor = GridBagConstraints.EAST;
		gbcJTextFieldTerminationCriterion.gridx = 2;
		gbcJTextFieldTerminationCriterion.gridy = 0;
		jPanelTerminationCriterion.add(jTextFieldTerminationCriterion, 
				gbcJTextFieldTerminationCriterion);

		JPanel jPanelTerminationDegree = new JPanel();
		GridBagLayout gblJPanelTerminationDegree = new GridBagLayout();
		gblJPanelTerminationCriterion.columnWeights = 
				new double[] {1.0, 0.1, 1.0};
		gblJPanelTerminationCriterion.rowWeights = new double[] {1.0, 1.0};
		jPanelTerminationDegree.setLayout(gblJPanelTerminationDegree);
		GridBagConstraints gbcJPanelTerminationDegree = 
				new GridBagConstraints();
		gbcJPanelTerminationDegree.gridwidth = 2;
		gbcJPanelTerminationDegree.anchor = GridBagConstraints.WEST;
		gbcJPanelTerminationDegree.gridx = 1;
		gbcJPanelTerminationDegree.gridy = 10;
		jPanelGeneticAlgorithmSettings.add(jPanelTerminationDegree, 
				gbcJPanelTerminationDegree);
		
		jLabelTerminationDegree = new JLabel("(Degree of Equality:");
		jLabelTerminationDegree.setVisible(false);
		GridBagConstraints gbcJLabelTerminationDegree = 
				new GridBagConstraints();
		gbcJLabelTerminationDegree.insets = new Insets(5, 5, 5, 0);
		gbcJLabelTerminationDegree.anchor = GridBagConstraints.WEST;
		gbcJLabelTerminationDegree.gridx = 0;
		gbcJLabelTerminationDegree.gridy = 1;
		jPanelTerminationDegree.add(jLabelTerminationDegree, 
					gbcJLabelTerminationDegree);
		
		jTextFieldTerminationDegree = new JTextField(
				String.valueOf(DEFAULT_DEGREE_OF_EQUALITY));
		jTextFieldTerminationDegree.setColumns(2);
		jTextFieldTerminationDegree.setHorizontalAlignment(JTextField.RIGHT);
		jTextFieldTerminationDegree.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				checkInputValue(jTextFieldTerminationDegree, 100, 1, 
						DEFAULT_DEGREE_OF_EQUALITY);	
			}
		});
		// TODO: Find a typical value.
		jTextFieldTerminationDegree.setToolTipText("<html>Degree to " +
				"which the consecutive<br>generations have to be equal<br>" +
				"Typical value: 75-95%</html>");
		jTextFieldTerminationDegree.setVisible(false);
		GridBagConstraints gbcJTextFieldTerminationDegree = 
				new GridBagConstraints();
		gbcJTextFieldTerminationDegree.insets = new Insets(5, 5, 5, 0);
		gbcJTextFieldTerminationDegree.anchor = GridBagConstraints.WEST;
		gbcJTextFieldTerminationDegree.gridx = 1;
		gbcJTextFieldTerminationDegree.gridy = 1;
		jPanelTerminationDegree.add(jTextFieldTerminationDegree, 
				gbcJTextFieldTerminationDegree);
		
		jLabelTerminationDegreeClose = new JLabel("%)");
		jLabelTerminationDegreeClose.setVisible(false);
		GridBagConstraints gbcJLabelTerminationDegreeClose = 
				new GridBagConstraints();
		gbcJLabelTerminationDegreeClose.insets = new Insets(5, 5, 5, 0);
		gbcJLabelTerminationDegreeClose.anchor = GridBagConstraints.WEST;
		gbcJLabelTerminationDegreeClose.gridx = 2;
		gbcJLabelTerminationDegreeClose.gridy = 1;
		jPanelTerminationDegree.add(jLabelTerminationDegreeClose, 
					gbcJLabelTerminationDegreeClose);
		
		
		
		buildGeneticAlgorithmFitnessFunction();
		
		
		
		jProgressBarGeneticAlgorithm = new JProgressBar();
		jProgressBarGeneticAlgorithm.setStringPainted(true);
		GridBagConstraints gbcJProgressBarGeneticAlgorithm = 
			new GridBagConstraints();
		gbcJProgressBarGeneticAlgorithm.fill = GridBagConstraints.HORIZONTAL;
		gbcJProgressBarGeneticAlgorithm.anchor = GridBagConstraints.SOUTH;
		gbcJProgressBarGeneticAlgorithm.gridx = 0;
		gbcJProgressBarGeneticAlgorithm.gridy = 3;
		jPanelGeneticAlgorithm.add(
				jProgressBarGeneticAlgorithm, gbcJProgressBarGeneticAlgorithm);
	}

	private void initializeAntAlgorithmPanel(JPanel contentPane) {
		JPanel jPanelAntAlgorithm = new JPanel();
		GridBagConstraints gbcPanelAntAlgorithm = new GridBagConstraints();
		gbcPanelAntAlgorithm.gridheight = 1;
		gbcPanelAntAlgorithm.insets = new Insets(0, 5, 5, 5);
		gbcPanelAntAlgorithm.fill = GridBagConstraints.BOTH;
		gbcPanelAntAlgorithm.gridx = 1;
		gbcPanelAntAlgorithm.gridy = 3;
		contentPane.add(jPanelAntAlgorithm, gbcPanelAntAlgorithm);
		GridBagLayout gblJPanelAntAlgorithm = new GridBagLayout();
		gblJPanelAntAlgorithm.columnWeights = new double[]{1.0};
		gblJPanelAntAlgorithm.rowWeights = new double[]{0.1, 0.8, 0.1};
		jPanelAntAlgorithm.setLayout(gblJPanelAntAlgorithm);

		jCheckBoxAntColonyOptimization = 
			new JCheckBox("Ant Colony Optimization Algorithm");
		jCheckBoxAntColonyOptimization.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseAlgorithm("antAlg");
			}
		});
		jCheckBoxAntColonyOptimization.setSelected(true);
		GridBagConstraints gbcJCheckBoxAntAlgorithm = 
			new GridBagConstraints();
		gbcJCheckBoxAntAlgorithm.anchor = GridBagConstraints.NORTH;
		gbcJCheckBoxAntAlgorithm.insets = new Insets(0, 0, 5, 0);
		gbcJCheckBoxAntAlgorithm.gridx = 0;
		gbcJCheckBoxAntAlgorithm.gridy = 0;
		jPanelAntAlgorithm.add(jCheckBoxAntColonyOptimization, 
				gbcJCheckBoxAntAlgorithm);

		JScrollPane jScrollPaneAntAlgorithm = new JScrollPane();
		GridBagConstraints gbcJScrollPaneAntAlgorithm = 
			new GridBagConstraints();
		gbcJScrollPaneAntAlgorithm.insets = new Insets(0, 0, 5, 0);
		gbcJScrollPaneAntAlgorithm.fill = GridBagConstraints.BOTH;
		gbcJScrollPaneAntAlgorithm.gridx = 0;
		gbcJScrollPaneAntAlgorithm.gridy = 1;
		jPanelAntAlgorithm.add(
				jScrollPaneAntAlgorithm, gbcJScrollPaneAntAlgorithm);
			
		JPanel panelAntAlgorithmSettings = new JPanel();
		jScrollPaneAntAlgorithm.setViewportView(panelAntAlgorithmSettings);
		GridBagLayout gbl_panelAntAlgorithmSettings = new GridBagLayout();
		gbl_panelAntAlgorithmSettings.columnWeights = 
			new double[]{0.5, 0.5};
		gbl_panelAntAlgorithmSettings.rowWeights = 
			new double[]{0.16, 0.14, 0.14, 0.14, 0.14, 0.14, 0.14};
		panelAntAlgorithmSettings.setLayout(gbl_panelAntAlgorithmSettings);
		
		JLabel jLabelAntVariant = new JLabel("Variant:");		
		GridBagConstraints gbcJLabelAntVariant = new GridBagConstraints();
		gbcJLabelAntVariant.gridwidth = 1;
		gbcJLabelAntVariant.insets = new Insets(5, 0, 5, 25);
		gbcJLabelAntVariant.gridx = 0;
		gbcJLabelAntVariant.gridy = 0;
		gbcJLabelAntVariant.anchor = GridBagConstraints.EAST;
		panelAntAlgorithmSettings.add(
				jLabelAntVariant, gbcJLabelAntVariant);
		
		txtAntVariant = new JTextField(""+DEFAULT_VARIANT);
		txtAntVariant.setToolTipText("<html>Set Variant of the " +
				"Algorithm<br>Choose Variant from 1 to 5:" + 
				"<br>Ant System = 1" +
				"<br>Ant Colony System = 2" +
				"<br>MAX-MIN Ant System = 3" +				
				"<br>Convergent Variant = 4" +
				"<br>(Qiqing et al. 2009) = 5" +
				"<br>(Li und Yan-xiang 2011) = 6</html>");
		txtAntVariant.setColumns(5);
		txtAntVariant.setHorizontalAlignment(JTextField.RIGHT);		
		GridBagConstraints gbc_AntVariant = new GridBagConstraints();
		gbc_AntVariant.insets = new Insets(5, 0, 5, 50);
		gbc_AntVariant.fill = GridBagConstraints.NONE;
		gbc_AntVariant.gridx = 1;
		gbc_AntVariant.gridy = 0;
		panelAntAlgorithmSettings.add(txtAntVariant, gbc_AntVariant);
		
		JLabel jLabelAntIterations = new JLabel("Iterations:");		
		GridBagConstraints gbcJLabelAntIterations = new GridBagConstraints();
		gbcJLabelAntIterations.gridwidth = 1;
		gbcJLabelAntIterations.insets = new Insets(5, 0, 5, 25);
		gbcJLabelAntIterations.gridx = 0;
		gbcJLabelAntIterations.gridy = 1;
		gbcJLabelAntIterations.anchor = GridBagConstraints.EAST;
		panelAntAlgorithmSettings.add(
				jLabelAntIterations, gbcJLabelAntIterations);
		
		txtAntIterations = new JTextField(""+DEFAULT_ITERATIONS);
		txtAntIterations.setToolTipText("<html>Number of Iterations" +
				"<br>Usually between 50 and 200</html>");
		txtAntIterations.setColumns(5);
		txtAntIterations.setHorizontalAlignment(JTextField.RIGHT);		
		GridBagConstraints gbc_AntIterations = new GridBagConstraints();
		gbc_AntIterations.insets = new Insets(5, 0, 5, 50);
		gbc_AntIterations.fill = GridBagConstraints.NONE;
		gbc_AntIterations.gridx = 1;
		gbc_AntIterations.gridy = 1;
		panelAntAlgorithmSettings.add(txtAntIterations, gbc_AntIterations);
		
		JLabel jLabelAntAnts = new JLabel("Ants:");
		GridBagConstraints gbcJLabelAntAnts = new GridBagConstraints();
		gbcJLabelAntAnts.gridwidth = 1;
		gbcJLabelAntAnts.insets = new Insets(0, 0, 5, 25);
		gbcJLabelAntAnts.gridx = 0;
		gbcJLabelAntAnts.gridy = 2;
		gbcJLabelAntAnts.anchor = GridBagConstraints.EAST;
		panelAntAlgorithmSettings.add(
				jLabelAntAnts, gbcJLabelAntAnts);
		
		txtAntAnts = new JTextField(""+DEFAULT_ANTS);
		txtAntAnts.setToolTipText("<html>Number of Ants" +
				"<br>Usually between 10 and 30</html>");
		txtAntAnts.setColumns(5);
		txtAntAnts.setHorizontalAlignment(JTextField.RIGHT);		
		GridBagConstraints gbc_AntAnts = new GridBagConstraints();
		gbc_AntAnts.insets = new Insets(0, 0, 5, 50);
		gbc_AntAnts.fill = GridBagConstraints.NONE;
		gbc_AntAnts.gridx = 1;
		gbc_AntAnts.gridy = 2;
		panelAntAlgorithmSettings.add(txtAntAnts, gbc_AntAnts);
		
		JLabel jLabelAntAlpha = new JLabel("Alpha:");
		GridBagConstraints gbcJLabelAntAlpha = new GridBagConstraints();
		gbcJLabelAntAlpha.gridwidth = 1;
		gbcJLabelAntAlpha.insets = new Insets(0, 0, 5, 25);
		gbcJLabelAntAlpha.gridx = 0;
		gbcJLabelAntAlpha.gridy = 3;
		gbcJLabelAntAlpha.anchor = GridBagConstraints.EAST;
		panelAntAlgorithmSettings.add(
				jLabelAntAlpha, gbcJLabelAntAlpha);
		
		txtAntAlpha = new JTextField(""+DEFAULT_ALPHA);
		txtAntAlpha.setToolTipText("<html>Influence of the " +
				"Pheromome Trail<br>Usually between 0.3 and 2</html>");
		txtAntAlpha.setColumns(5);
		txtAntAlpha.setHorizontalAlignment(JTextField.RIGHT);		
		GridBagConstraints gbc_AntAlpha = new GridBagConstraints();
		gbc_AntAlpha.insets = new Insets(0, 0, 5, 50);
		gbc_AntAlpha.fill = GridBagConstraints.NONE;
		gbc_AntAlpha.gridx = 1;
		gbc_AntAlpha.gridy = 3;
		panelAntAlgorithmSettings.add(txtAntAlpha, gbc_AntAlpha);
		
		JLabel jLabelAntBeta = new JLabel("Beta:");
		GridBagConstraints gbcJLabelAntBeta = new GridBagConstraints();
		gbcJLabelAntBeta.gridwidth = 1;
		gbcJLabelAntBeta.insets = new Insets(0, 0, 5, 25);
		gbcJLabelAntBeta.gridx = 0;
		gbcJLabelAntBeta.gridy = 4;
		gbcJLabelAntBeta.anchor = GridBagConstraints.EAST;
		panelAntAlgorithmSettings.add(
				jLabelAntBeta, gbcJLabelAntBeta);
		
		txtAntBeta = new JTextField(""+DEFAULT_BETA);
		txtAntBeta.setToolTipText("<html>Influence of the " +
				"local Utility<br>Usually between 0.3 and 2</html>");
		txtAntBeta.setColumns(5);
		txtAntBeta.setHorizontalAlignment(JTextField.RIGHT);		
		GridBagConstraints gbc_AntBeta = new GridBagConstraints();
		gbc_AntBeta.insets = new Insets(0, 0, 5, 50);
		gbc_AntBeta.fill = GridBagConstraints.NONE;
		gbc_AntBeta.gridx = 1;
		gbc_AntBeta.gridy = 4;
		panelAntAlgorithmSettings.add(txtAntBeta, gbc_AntBeta);
		
		JLabel jLabelAntDilution = new JLabel("Dilution:");
		GridBagConstraints gbcJLabelAntDilution = new GridBagConstraints();
		gbcJLabelAntDilution.gridwidth = 1;
		gbcJLabelAntDilution.insets = new Insets(0, 0, 5, 25);
		gbcJLabelAntDilution.gridx = 0;
		gbcJLabelAntDilution.gridy = 5;
		gbcJLabelAntDilution.anchor = GridBagConstraints.EAST;
		panelAntAlgorithmSettings.add(
				jLabelAntDilution, gbcJLabelAntDilution);
		
		txtAntDilution = new JTextField(""+DEFAULT_DILUTION);
		txtAntDilution.setToolTipText("<html>Dilution of the " +
				"Pheromome Trail<br>Usually between 0.01 and 0.1</html>");
		txtAntDilution.setColumns(5);
		txtAntDilution.setHorizontalAlignment(JTextField.RIGHT);		
		GridBagConstraints gbc_AntDilution = new GridBagConstraints();
		gbc_AntDilution.insets = new Insets(0, 0, 5, 50);
		gbc_AntDilution.fill = GridBagConstraints.NONE;
		gbc_AntDilution.gridx = 1;
		gbc_AntDilution.gridy = 5;
		panelAntAlgorithmSettings.add(txtAntDilution, gbc_AntDilution);
		
		JLabel jLabelAntPi = new JLabel("Pi Init-Value:");
		GridBagConstraints gbcJLabelAntPi = new GridBagConstraints();
		gbcJLabelAntPi.gridwidth = 1;
		gbcJLabelAntPi.insets = new Insets(0, 0, 5, 25);
		gbcJLabelAntPi.gridx = 0;
		gbcJLabelAntPi.gridy = 6;
		gbcJLabelAntPi.anchor = GridBagConstraints.EAST;
		panelAntAlgorithmSettings.add(
				jLabelAntPi, gbcJLabelAntPi);
		
		txtAntPi = new JTextField(""+DEFAULT_PIINIT);
		txtAntPi.setToolTipText("<html>Initial Value of the " +
				"Pheromome Trail<br>Usually between 0.5 and 10</html>");
		txtAntPi.setColumns(5);
		txtAntPi.setHorizontalAlignment(JTextField.RIGHT);		
		GridBagConstraints gbc_AntPi = new GridBagConstraints();
		gbc_AntPi.insets = new Insets(0, 0, 5, 50);
		gbc_AntPi.fill = GridBagConstraints.NONE;
		gbc_AntPi.gridx = 1;
		gbc_AntPi.gridy = 6;
		panelAntAlgorithmSettings.add(txtAntPi, gbc_AntPi);

		jProgressBarAntAlgorithm = new JProgressBar();
		jProgressBarAntAlgorithm.setStringPainted(true);
		GridBagConstraints gbcJProgressBarAntAlgorithm = 
			new GridBagConstraints();
		gbcJProgressBarAntAlgorithm.fill = GridBagConstraints.HORIZONTAL;
		gbcJProgressBarAntAlgorithm.anchor = GridBagConstraints.SOUTH;
		gbcJProgressBarAntAlgorithm.gridx = 0;
		gbcJProgressBarAntAlgorithm.gridy = 2;
		jPanelAntAlgorithm.add(
				jProgressBarAntAlgorithm, gbcJProgressBarAntAlgorithm);
	}

	private void initializeAnalyticAlgorithmPanel(JPanel contentPane) {
		JPanel jPanelAnalyticAlgorithm = new JPanel();
		GridBagConstraints gbcJPanelAnalyticAlgorithm =
			new GridBagConstraints();
		gbcJPanelAnalyticAlgorithm.gridheight = 1;
		gbcJPanelAnalyticAlgorithm.insets = new Insets(0, 5, 5, 5);
		gbcJPanelAnalyticAlgorithm.fill = GridBagConstraints.BOTH;
		gbcJPanelAnalyticAlgorithm.gridx = 2;
		gbcJPanelAnalyticAlgorithm.gridy = 3;
		contentPane.add(jPanelAnalyticAlgorithm, gbcJPanelAnalyticAlgorithm);
		GridBagLayout gblJPanelAnalyticAlgorithm = new GridBagLayout();
		gblJPanelAnalyticAlgorithm.columnWeights = new double[]{1.0};
		gblJPanelAnalyticAlgorithm.rowWeights = 
				new double[]{0.1, 0.8, 0.1};
		jPanelAnalyticAlgorithm.setLayout(gblJPanelAnalyticAlgorithm);

		jCheckBoxAnalyticAlgorithm = 
			new JCheckBox("Analytic Algorithm");
		jCheckBoxAnalyticAlgorithm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chooseAlgorithm("analyticAlg");
			}
		});
		jCheckBoxAnalyticAlgorithm.setSelected(true);
		jCheckBoxAnalyticAlgorithm.setToolTipText("<html>Please note:<br>" +
				"Datasets with size <b>10x10</b> or bigger<br>" +
				"require a lot of time!<br>" +
				"It is strongly recommended to <i>deselect</i><br>" +
				"the analytic algorithm in such cases.</html>");
		GridBagConstraints gbcJCheckBoxAnalyticAlgorithm = 
			new GridBagConstraints();
		gbcJCheckBoxAnalyticAlgorithm.anchor = GridBagConstraints.NORTH;
		gbcJCheckBoxAnalyticAlgorithm.insets = new Insets(0, 0, 5, 0);
		gbcJCheckBoxAnalyticAlgorithm.gridx = 0;
		gbcJCheckBoxAnalyticAlgorithm.gridy = 0;
		jPanelAnalyticAlgorithm.add(
				jCheckBoxAnalyticAlgorithm, gbcJCheckBoxAnalyticAlgorithm);
		
		JPanel panelAnalyticBody = new JPanel();
		GridBagLayout gbl_panelAnalyticBody = new GridBagLayout();
		gbl_panelAnalyticBody.columnWeights = new double[] {1.0, 1.0};
		gbl_panelAnalyticBody.rowWeights = new double[] {0.8, 0.2};
		panelAnalyticBody.setLayout(gbl_panelAnalyticBody);
		GridBagConstraints gbc_panelAnalyticBody = new GridBagConstraints();
		gbc_panelAnalyticBody.fill = GridBagConstraints.BOTH;
		gbc_panelAnalyticBody.gridx = 0;
		gbc_panelAnalyticBody.gridy = 1;
		jPanelAnalyticAlgorithm.add(
				panelAnalyticBody, gbc_panelAnalyticBody);

		JScrollPane jScrollPaneAnalyticAlgorithm = new JScrollPane();
		GridBagConstraints gbcJScrollPaneAnalyticAlgorithm = 
			new GridBagConstraints();
		gbcJScrollPaneAnalyticAlgorithm.insets = new Insets(4, 0, 5, 0);
		gbcJScrollPaneAnalyticAlgorithm.fill = GridBagConstraints.BOTH;
		gbcJScrollPaneAnalyticAlgorithm.gridwidth = 2;
		gbcJScrollPaneAnalyticAlgorithm.gridx = 0;
		gbcJScrollPaneAnalyticAlgorithm.gridy = 0;
		panelAnalyticBody.add(
				jScrollPaneAnalyticAlgorithm, gbcJScrollPaneAnalyticAlgorithm);

		jTableAnalyticAlgorithm = new ServiceSelectionTable(1, 2, true);
		jTableAnalyticAlgorithm.setEnabled(false);
		// Look for a better solution for this listener 
		// if more analytic methods are implemented
//		jTableAnalyticAlgorithm.addMouseListener(new MouseAdapter() {
//			public void mouseClicked(MouseEvent e) {
//				if (jTableAnalyticAlgorithm.getSelectedRow() == 0 && 
//						jTableAnalyticAlgorithm.getSelectedColumn() == 0) {
//					if (jTableAnalyticAlgorithm.getValueAt(
//							0, 0).equals(true)) {
//						jTableAnalyticAlgorithm.setValueAt(false, 1, 0);
//					}
//					else {
//						jTableAnalyticAlgorithm.setValueAt(true, 1, 0);
//					}
//				}
//				else if (jTableAnalyticAlgorithm.getSelectedRow() == 1 && 
//						jTableAnalyticAlgorithm.getSelectedColumn() == 0) {
//					if (jTableAnalyticAlgorithm.getValueAt(
//							1, 0).equals(true)) {
//						jTableAnalyticAlgorithm.setValueAt(false, 0, 0);
//					}
//					else {
//						jTableAnalyticAlgorithm.setValueAt(true, 0, 0);
//					}
//				}
//			}
//		});
		jTableAnalyticAlgorithm.getColumnModel().getColumn(0).setHeaderValue(
		"Selection");
		jTableAnalyticAlgorithm.getColumnModel().getColumn(1).setHeaderValue(
		"Method");
		jTableAnalyticAlgorithm.setValueAt(true, 0, 0);
		jTableAnalyticAlgorithm.setValueAt(" Enumeration", 0, 1);
//		jTableAnalyticAlgorithm.setValueAt(false, 1, 0);
//		jTableAnalyticAlgorithm.setValueAt("Integer Programming", 1, 1);
		jTableAnalyticAlgorithm.setColumnWidthRelative(
				new double[] {0.2, 0.8});
		jScrollPaneAnalyticAlgorithm.setViewportView(jTableAnalyticAlgorithm);
		
		JLabel jLabelResultTiers = new JLabel("Number of Result Tiers:");
		jLabelResultTiers.setToolTipText("<html>Number of best solutions" +
				"<br>to be shown</html>");
		GridBagConstraints gbcJLabelResultTiers = new GridBagConstraints();
		gbcJLabelResultTiers.insets = new Insets(0, 5, 5, 5);
		gbcJLabelResultTiers.anchor = GridBagConstraints.CENTER;
		gbcJLabelResultTiers.gridx = 0;
		gbcJLabelResultTiers.gridy = 1;
		panelAnalyticBody.add(jLabelResultTiers, gbcJLabelResultTiers);

		jSpinnerNumberResultTiers = 
			new JSpinner(new SpinnerNumberModel(1, 1, 3, 1));
		((JSpinner.DefaultEditor) jSpinnerNumberResultTiers.getEditor()).
		getTextField().setEditable(false);
		jSpinnerNumberResultTiers.setPreferredSize(new Dimension(35, 25));
		GridBagConstraints gbcJSpinnerNumberResultTiers = 
			new GridBagConstraints();
		gbcJSpinnerNumberResultTiers.insets = new Insets(0, 0, 5, 5);
		gbcJSpinnerNumberResultTiers.anchor = GridBagConstraints.WEST;
		gbcJSpinnerNumberResultTiers.gridx = 1;
		gbcJSpinnerNumberResultTiers.gridy = 1;
		panelAnalyticBody.add(
				jSpinnerNumberResultTiers, gbcJSpinnerNumberResultTiers);

		jProgressBarAnalyticAlgorithm = new JProgressBar();
		jProgressBarAnalyticAlgorithm.setStringPainted(true);
		GridBagConstraints gbcJProgressBarAnalyticAlgorithm = 
			new GridBagConstraints();
		gbcJProgressBarAnalyticAlgorithm.fill = GridBagConstraints.HORIZONTAL;
		gbcJProgressBarAnalyticAlgorithm.anchor = GridBagConstraints.SOUTH;
		gbcJProgressBarAnalyticAlgorithm.gridwidth = 2;
		gbcJProgressBarAnalyticAlgorithm.gridx = 0;
		gbcJProgressBarAnalyticAlgorithm.gridy = 2;
		jPanelAnalyticAlgorithm.add(
				jProgressBarAnalyticAlgorithm, 
				gbcJProgressBarAnalyticAlgorithm);
	}

	private void initializeTabbedResultsPanel(JPanel contentPane) {
		jTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbcJTabbedPane = new GridBagConstraints();
		gbcJTabbedPane.gridheight = 1;
		gbcJTabbedPane.gridwidth = 2;
		gbcJTabbedPane.insets = new Insets(0, 5, 0, 5);
		gbcJTabbedPane.fill = GridBagConstraints.BOTH;
		gbcJTabbedPane.gridx = 0;
		gbcJTabbedPane.gridy = 6;
		contentPane.add(jTabbedPane, gbcJTabbedPane);
		jTabbedPane.setPreferredSize(new Dimension(0, 0));
	}

	private void initializeGeneralResultsPanel(JPanel contentPane) {
		JPanel jPanelGeneralResults = new JPanel();
		GridBagConstraints gbcJPanelGeneralResults = new GridBagConstraints();
		gbcJPanelGeneralResults.gridheight = 1;
		gbcJPanelGeneralResults.fill = GridBagConstraints.BOTH;
		gbcJPanelGeneralResults.gridx = 2;
		gbcJPanelGeneralResults.gridy = 6;
		contentPane.add(jPanelGeneralResults, gbcJPanelGeneralResults);
		GridBagLayout gblJPanelGeneralResults = new GridBagLayout();
		gblJPanelGeneralResults.columnWeights = new double[]{1.0};
		gblJPanelGeneralResults.rowWeights = new double[]{1.0};
		jPanelGeneralResults.setLayout(gblJPanelGeneralResults);

		String[] generalResultsData = {
				"Runtime:", 
				"     Genetic Algorithm",
				"     Ant Algorithm", 
				"     Analytic Algorithm",
				" \u0394 Genetic Algorithm", 
				" \u0394 Ant Algorithm"
		};
		JScrollPane jScrollPaneResults = new JScrollPane();
		jScrollPaneResults.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbcJScrollPaneResults = new GridBagConstraints();
		gbcJScrollPaneResults.insets = new Insets(0, 0, 0, 5);
		gbcJScrollPaneResults.fill = GridBagConstraints.BOTH;
		gbcJScrollPaneResults.gridx = 0;
		gbcJScrollPaneResults.gridy = 0;
		jPanelGeneralResults.add(jScrollPaneResults, gbcJScrollPaneResults);
		jTableGeneralResults = new ServiceSelectionTable(6,2,false);
		jTableGeneralResults.getColumnModel().getColumn(0).
		setHeaderValue("Variable");
		jTableGeneralResults.getColumnModel().getColumn(1).
		setHeaderValue("Value");
		for (int i = 0; i < generalResultsData.length; i++) {
			jTableGeneralResults.setValueAt(generalResultsData[i], i, 0);
		}
		jTableGeneralResults.setColumnWidthRelative(new double[] {0.6, 0.4});
		jTableGeneralResults.setColumnTextAlignment(
				1, DefaultTableCellRenderer.RIGHT);
		jTableGeneralResults.setEnabled(false);
		jScrollPaneResults.setViewportView(jTableGeneralResults);
	}

	private void initializeLogPanel(JPanel contentPane) {
		JScrollPane jScrollPaneLog = new JScrollPane();
		jScrollPaneLog.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbcJScrollPaneResults = new GridBagConstraints();
		gbcJScrollPaneResults.insets = new Insets(0, 5, 0, 5);
		gbcJScrollPaneResults.fill = GridBagConstraints.BOTH;
		gbcJScrollPaneResults.gridwidth = 3;
		gbcJScrollPaneResults.gridx = 0;
		gbcJScrollPaneResults.gridy = 7;
		contentPane.add(jScrollPaneLog, gbcJScrollPaneResults);
		textAreaLog = new JTextArea();
		textAreaLog.setEditable(false);
		textAreaLog.setForeground(Color.RED);
		jScrollPaneLog.setViewportView(textAreaLog);
	}
	
	
	
	/*	+-----------------------------------------------------------+
	 * 	| +-------------------------------------------------------+ |
	 * 	| |														  | |
	 * 	| |				   		MENU BAR METHODS				  | |
	 * 	| |														  | |
	 * 	| +-------------------------------------------------------+ |
	 * 	+-----------------------------------------------------------+
	 */
	
	private void resetProgram() {
		this.dispose();
		new MainFrame().setVisible(true);
	}
	
	// Load web services from a CSV file.
	private void loadModelSetup() {
		// Delete previously loaded web services.
		serviceCandidatesList.removeAll(serviceCandidatesList);
		serviceClassesList.removeAll(serviceClassesList);
		
		ServiceSelectionFileChooser fileChooser = 
				new ServiceSelectionFileChooser("");
		if (fileChooser.showOpenDialog(MainFrame.this) != 
				JFileChooser.APPROVE_OPTION) {
			return;
		}
		File file = fileChooser.getSelectedFile();
		if (file == null || !file.canExecute()) {
			writeErrorLogEntry("File does not exist");
			return;
		}
		else if (!file.getName().endsWith(".csv")) {
			writeErrorLogEntry("Chosen file has the wrong format");
			return;
		}
		
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			
			// Load Constraints
			// Constraints-Header will never be used
			bufferedReader.readLine().split(";");
			String[] constraintsValues = bufferedReader.readLine().split(";");
			String[] constraintsWeights = bufferedReader.readLine().split(";");
			
			// skip header and empty line
			bufferedReader.readLine();
			bufferedReader.readLine();

			// Load web services data.
			String[] serviceCandidateArray;
			while (bufferedReader.ready()) {
				serviceCandidateArray = bufferedReader.readLine().split(";");
				// Create and save service candidates.
				ServiceCandidate serviceCandidate = new ServiceCandidate(
						Integer.parseInt(serviceCandidateArray[0]), 
						Integer.parseInt(serviceCandidateArray[2]), 
						serviceCandidateArray[3], 
						new QosVector(
								Double.parseDouble(serviceCandidateArray[4]), 
								Double.parseDouble(serviceCandidateArray[5]), 
								Double.parseDouble(serviceCandidateArray[6])));
				serviceCandidatesList.add(serviceCandidate);

				// Create and save service classes. Assign service candidates 
				// to service classes.
				boolean serviceClassAlreadyCreated = false;
				for (ServiceClass serviceClass : serviceClassesList) {
					if (serviceClass.getServiceClassId() == Integer.parseInt(
							serviceCandidateArray[0])) {
						serviceClassAlreadyCreated = true;
						serviceClass.getServiceCandidateList().add(
								serviceCandidate);
						break;
					}
				}
				if (! serviceClassAlreadyCreated) {
					ServiceClass serviceClass = new ServiceClass(
							Integer.parseInt(serviceCandidateArray[0]), 
							serviceCandidateArray[1], 
							new LinkedList<ServiceCandidate>());
					serviceClassesList.add(serviceClass);
					serviceClass.getServiceCandidateList().add(
							serviceCandidate);
				}
			}
			
			loadServiceData(false);
			
			// NOW SET LOADED CONSTRAINTS
			jSliderMaxCosts.setValue((int) Math.ceil(
					Double.parseDouble(constraintsValues[0])));
			jSliderMaxResponseTime.setValue((int) Math.ceil(
					Double.parseDouble(constraintsValues[1])));
			jSliderMinAvailability.setValue((int) Math.ceil(
					Double.parseDouble(constraintsValues[2])*100));
			jTextFieldMaxCosts.setText(constraintsValues[0]);
			jTextFieldMaxResponseTime.setText(constraintsValues[1]);
			jTextFieldMinAvailability.setText(String.valueOf(
					Double.parseDouble(constraintsValues[2]) * 100));
			jTextFieldCostsWeight.setText(String.valueOf(
					(int) Math.ceil(
							Double.parseDouble(constraintsWeights[0]))));
			jTextFieldResponseTimeWeight.setText(String.valueOf(
					(int) Math.ceil(
							Double.parseDouble(constraintsWeights[1]))));
			jTextFieldAvailabilityWeight.setText(String.valueOf(
					(int) Math.ceil(
							Double.parseDouble(constraintsWeights[2]))));	
			disableRelaxationSlider();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			writeErrorLogEntry("Chosen file has the wrong (internal) format");
		}
		finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void generateModelSetup() {
		JSpinner spinnerNumberOfServiceClasses = new JSpinner(
				new SpinnerNumberModel(1, 1, 1000, 1));		
		((JSpinner.DefaultEditor) spinnerNumberOfServiceClasses.getEditor()).
		getTextField().setHorizontalAlignment(JTextField.CENTER);
		JSpinner spinnerNumberOfWebServices = new JSpinner(
				new SpinnerNumberModel(1, 1, 1000, 1));
		((JSpinner.DefaultEditor) spinnerNumberOfWebServices.getEditor()).
		getTextField().setEditable(true);
		((JSpinner.DefaultEditor) spinnerNumberOfWebServices.getEditor()).
		getTextField().setHorizontalAlignment(JTextField.CENTER);
		JComponent[] dialogComponents = new JComponent[] {
				new JLabel("Number of Service Classes:"),
				spinnerNumberOfServiceClasses,
				new JLabel("Number of Web Services (per Class):"),
				spinnerNumberOfWebServices
		};
		if (JOptionPane.showConfirmDialog(null, dialogComponents, 
				"Random Set Properties", 
				JOptionPane.OK_CANCEL_OPTION, 
				JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
			return;
		}
		int numberOfServiceClasses = 
			(Integer) spinnerNumberOfServiceClasses.getValue();
		int numberOfWebServices = 
			(Integer) spinnerNumberOfWebServices.getValue();
		
		// Delete previously loaded web services.
		serviceCandidatesList.removeAll(serviceCandidatesList);
		serviceClassesList.removeAll(serviceClassesList);
		
		// Use RandomSetGenerator to create web services data.
		serviceClassesList = new RandomSetGenerator().generateSet(
				numberOfServiceClasses, numberOfWebServices);
		
		loadServiceData(true);
		jCheckBoxRelaxation.setSelected(true);
		jSliderRelaxation.setEnabled(true);
		jSliderRelaxation.setValue((int) (DEFAULT_RELAXATION * 100));
		jTextFieldRelaxation.setText(String.valueOf(DEFAULT_RELAXATION));
	}
	
	private void saveModelSetup() {
		ServiceSelectionFileChooser fileChooser = 
				new ServiceSelectionFileChooser("DataSet.csv");
		if (fileChooser.showSaveDialog(MainFrame.this) != 
				JFileChooser.APPROVE_OPTION) {
			return;
		}								
		File file = fileChooser.getSelectedFile();
		checkOverwrite(file, "Model Setup has");
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(file));
			
			Map<String, Constraint> constraintsMap = getChosenConstraints();
			Constraint costs = constraintsMap.get(Constraint.COSTS);
			Constraint responseTime = constraintsMap.get(
					Constraint.RESPONSE_TIME);
			Constraint availability = constraintsMap.get(
					Constraint.AVAILABILITY);			
			String constraintsHeader = "" + Constraint.COSTS + 
					";" + Constraint.RESPONSE_TIME + 
					";" + Constraint.AVAILABILITY;			
			String values = "" + costs.getValue() + ";" + 
					responseTime.getValue() + ";" + availability.getValue();
			String weights = "" + costs.getWeight() + ";" + 
					responseTime.getWeight() + ";" + availability.getWeight();
			bufferedWriter.write(constraintsHeader);
			bufferedWriter.newLine();
			bufferedWriter.write(values);
			bufferedWriter.newLine();
			bufferedWriter.write(weights);
			bufferedWriter.newLine();
			bufferedWriter.newLine();
			
			String header = "serviceClassId;serviceClassName;ID;Name;" +
					"Costs;Response Time;Availability";
			bufferedWriter.write(header);			
			for (ServiceCandidate sc : serviceCandidatesList) {
				String line = sc.getServiceClassId() + ";ServiceClass" + 
					sc.getServiceClassId() + ";" + sc.getServiceCandidateId() + 
					";" + sc.getName() + ";" + sc.getQosVector().getCosts() + 
					";" + sc.getQosVector().getResponseTime() + ";" + 
					sc.getQosVector().getAvailability();
				bufferedWriter.newLine();
				bufferedWriter.write(line);
			}
			bufferedWriter.close();
		} catch (IOException e1) {	
			e1.printStackTrace();
		}
	}
	
	private void loadAlgorithmSettings() {
		ServiceSelectionFileChooser fileChooser =
				new ServiceSelectionFileChooser("");
		if (fileChooser.showOpenDialog(MainFrame.this) != 
				JFileChooser.APPROVE_OPTION) {
			return;
		}
		File file = fileChooser.getSelectedFile();
		if (file == null || !file.canExecute()) {
			writeErrorLogEntry("File does not exist");
			return;
		}
		else if (!file.getName().endsWith(".csv")) {
			writeErrorLogEntry("Chosen file has the wrong format");
			return;
		}
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));			
			// skip headers
			bufferedReader.readLine().split(";");
			String[] values = bufferedReader.readLine().split(";");
			txtAntVariant.setText(values[0]);
			txtAntIterations.setText(values[1]);
			txtAntAnts.setText(values[2]);
			txtAntAlpha.setText(values[3]);
			txtAntBeta.setText(values[4]);
			txtAntDilution.setText(values[5]);
			txtAntPi.setText(values[6]);
			jTextFieldPopulationSize.setText(values[7]);
			jComboBoxSelection.setSelectedItem(values[8]);
			jTextFieldElitismRate.setText(values[9]);
			jComboBoxCrossover.setSelectedItem(values[10]);
			jTextFieldCrossoverRate.setText(values[11]);
			jTextFieldMutationRate.setText(values[12]);
			jComboBoxTerminationCriterion.setSelectedItem(values[13]);
			jTextFieldTerminationCriterion.setText(values[14]);
			jTextFieldTerminationDegree.setText(values[15]);
			bufferedReader.close();
		} catch (IOException e1) {			
			e1.printStackTrace();
		} catch (NullPointerException e) {
			writeErrorLogEntry("Chosen file has the wrong (internal) format");
		}	
	}
	
	private void saveAlgorithmSettings() {
		ServiceSelectionFileChooser fileChooser =
				new ServiceSelectionFileChooser("AlgorithmSettings.csv");
		if (fileChooser.showSaveDialog(MainFrame.this) != 
				JFileChooser.APPROVE_OPTION) {
			return;
		}                
		File file = fileChooser.getSelectedFile();
		checkOverwrite(file, "Algorithm Settings have");
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(file));				
			String header = "txtAntVariant;txtAntIterations;txtAntAnts;" +
					"txtAntAlpha;txtAntBeta;txtAntDilution;txtAntPi";
			header += ";Population Size;Selection Method;" + 
					"Elitism Rate;Crossover Method;" +
					"Crossover Rate;Mutation Rate;" +
					"Termination Criterion;Termination Value;" +
					"Termination Degree of Equality";			
			String values = ""+txtAntVariant.getText();
			values += ";" + txtAntIterations.getText();
			values += ";" + txtAntAnts.getText();
			values += ";" + txtAntAlpha.getText();
			values += ";" + txtAntBeta.getText();
			values += ";" + txtAntDilution.getText();
			values += ";" + txtAntPi.getText();
			values += ";" + jTextFieldPopulationSize.getText();
			values += ";" + jComboBoxSelection.getSelectedItem();
			values += ";" + jTextFieldElitismRate.getText();
			values += ";" + jComboBoxCrossover.getSelectedItem();
			values += ";" + jTextFieldCrossoverRate.getText();
			values += ";" + jTextFieldMutationRate.getText();
			values += ";" + jComboBoxTerminationCriterion.getSelectedItem();
			values += ";" + jTextFieldTerminationCriterion.getText();
			values += ";" + jTextFieldTerminationDegree.getText();
			bufferedWriter.write(header);
			bufferedWriter.newLine();			
			bufferedWriter.write(values);			
			bufferedWriter.close();
		} catch (IOException e1) {			
			e1.printStackTrace();
		}
	}
	
//	private void setDefaultConstraints() {
//		jSliderMaxCosts.setValue((maxCosts + minCosts) / 2);
//		jSliderMaxResponseTime.setValue(
//				(maxResponseTime + minResponseTime) / 2);
//		jSliderMinAvailability.setValue(
//				(maxAvailability + minAvailability) / 2);
//	}
	
	// Implement method which shows a message dialog
	//		 -> dialog should contain basic information for using 
	//			the program correctly
//	private void showHelpDialog() {
//		
//	}
	
	// Implement method which shows an input dialog
	//		 -> input message should be sent to an admin
	//		 -> check if web access is available
	//		 -> local solution: create txt-file, with date etc.
//	private void showSupportDialog() {
//		
//	}
	
	// TODO: Adjust Dialog (if necessary)
	private void showAboutDialog() {
		JOptionPane.showMessageDialog(null, 
				"<html><h1>Service Selection Tool</h1><br>" +
				"<h2><i>Version 1.0 (10.05.2013)</i></h2><br><br>" +
				"<h3>Developed by:</h3>" +
				"<ul style=\"list-style-type: none;\">" +
				"<li>Christian Deml</li>" +
				"<li>Christian Richthammer</li>" +
				"<li>Michael Mayer</li>" +
				"<li>Gerit Wagner</li></ul></html>", 
				"About", JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void loadServiceData(boolean generationMode) {
		// Write service classes headers.
		jTableServiceClasses.setModel(new BasicTableModel(
			serviceClassesList.size(), 2, false));
		jTableServiceClasses.setColumnWidthRelative(
				new double[] {0.3, 0.7});
		TableColumnModel serviceClassesColumnModel = 
			jTableServiceClasses.getColumnModel();
		serviceClassesColumnModel.getColumn(0).setHeaderValue("ID");
		serviceClassesColumnModel.getColumn(1).setHeaderValue("Name");
		jTableServiceClasses.setColumnTextAlignment(
				0, DefaultTableCellRenderer.CENTER);

		// Write service classes data.
		for (int i = 0 ; i < serviceClassesList.size() ; i++) {
			ServiceClass serviceClass = serviceClassesList.get(i);
			jTableServiceClasses.setValueAt(
					serviceClass.getServiceClassId(), i, 0);
			jTableServiceClasses.setValueAt(serviceClass.getName(), i, 1);
			
			if (generationMode) {
				for (ServiceCandidate serviceCandidate : 
					serviceClass.getServiceCandidateList()) {
					serviceCandidatesList.add(serviceCandidate);
				}
			}
		}
		// Write service candidates headers.
		jTableWebServices.setModel(new BasicTableModel(
				serviceCandidatesList.size(), 6, false));
		TableColumnModel webServicesColumnModel = 
			jTableWebServices.getColumnModel();
		String[] headerArray = new String[] {"Service Class ", "ID", 
			"Name", "Costs", "ResponseTime", "Availability"};
		for (int k = 0; k < 6; k++) {
			webServicesColumnModel.getColumn(k).setHeaderValue(
					headerArray[k]);
		}
		jTableWebServices.setColumnTextAlignment(
				0, DefaultTableCellRenderer.CENTER);
		jTableWebServices.setColumnTextAlignment(
				1, DefaultTableCellRenderer.CENTER);
		for (int i = 3; i < 6; i++) {
			jTableWebServices.setColumnTextAlignment(
					i, DefaultTableCellRenderer.RIGHT);
		}
		// Write service candidates data.
		for (int k = 0 ; k < serviceCandidatesList.size() ; k++) {
			ServiceCandidate serviceCandidate = 
				serviceCandidatesList.get(k);
			QosVector qosVector = serviceCandidate.getQosVector();
			jTableWebServices.setValueAt(
					serviceCandidate.getServiceClassId(), k, 0);
			jTableWebServices.setValueAt(
					serviceCandidate.getServiceCandidateId(), k, 1);
			jTableWebServices.setValueAt(serviceCandidate.getName(), k, 2);
			jTableWebServices.setValueAt(qosVector.getCosts(), k, 3);
			jTableWebServices.setValueAt(
					qosVector.getResponseTime(), k, 4);
			jTableWebServices.setValueAt(qosVector.getAvailability(), k, 5);
		}
		webServicesLoaded = true;
		checkEnableStartButton();
		setSliderExtremeValues();
		if (generationMode) {
			jSliderMaxCosts.setValue((int) (
					DEFAULT_RELAXATION * (maxCosts + minCosts)));
			jSliderMaxResponseTime.setValue((int) (
					DEFAULT_RELAXATION * (maxResponseTime + minResponseTime)));
			jSliderMinAvailability.setValue((int) (
					DEFAULT_RELAXATION * (maxAvailability + minAvailability)));
		}
		checkInputValue(jTextFieldPopulationSize, 
				MAX_START_POPULATION_SIZE, 1, 
				DEFAULT_START_POPULATION_SIZE);
	}
	
	
	
	/*	+-----------------------------------------------------------+
	 * 	| +-------------------------------------------------------+ |
	 * 	| |														  | |
	 * 	| |				     CONSTRAINT METHODS					  | |
	 * 	| |														  | |
	 * 	| +-------------------------------------------------------+ |
	 * 	+-----------------------------------------------------------+
	 */

	private void changeConstraintCheckboxStatus(JCheckBox checkBox, 
			JTextField textFieldWeight, JSlider slider) {
		int lblWeights;
		slider.setEnabled(checkBox.isSelected());
		textFieldWeight.setEditable(checkBox.isSelected());
		lblWeights = Integer.parseInt(lblWeightSum.getText());
		lblWeights -= Integer.parseInt(textFieldWeight.getText());
		if (checkBox.isSelected()) {
			textFieldWeight.setText(String.valueOf(
					Math.max(0, 100 - lblWeights)));
			lblWeightSum.setText(String.valueOf(
					lblWeights + Math.max(0, 100 - lblWeights)));
		}
		else {
			lblWeightSum.setText(String.valueOf(lblWeights));
			textFieldWeight.setText("0");
		}
		changeWeight(textFieldWeight);
		getUtilityFunction();
		buildGeneticAlgorithmFitnessFunction();
		if (checkBox.isSelected()) {
			disableRelaxationSlider();
		}
	}
	
	private void setConstraintValueManually(
			JSlider slider, JTextField textField, int minValue, int maxValue) {
		int average = (minValue + maxValue) / 2;
		try {
			Integer.parseInt(textField.getText());
		} catch (NumberFormatException e) {
			textField.setText(String.valueOf(average));
			slider.setValue(average);
			writeErrorLogEntry("Value has to be from the type Integer!");
		}
		if (Integer.parseInt(textField.getText()) < minValue || 
				Integer.parseInt(textField.getText()) > maxValue) {
			textField.setText(String.valueOf(average));
			slider.setValue(average);
			writeErrorLogEntry("Value has to be between " + 
					minValue + " and " + maxValue);
		}
		else {
			slider.setValue(Integer.parseInt(textField.getText()));
		}
		getUtilityFunction();
		disableRelaxationSlider();
	}
	
	private void useConstraintSlider(JTextField textfield, JSlider slider) {
		textfield.setText(String.valueOf(slider.getValue()));
		getUtilityFunction();
		if (!jSliderRelaxation.getValueIsAdjusting() && (jSliderMaxCosts.
				getValue() != (int) (Math.round(jSliderRelaxation.getValue() / 
						100.0 * (maxCosts - minCosts)) + minCosts) || 
						jSliderMaxResponseTime.getValue() != (int) (Math.round(
								jSliderRelaxation.getValue() / 100.0 * (
										maxResponseTime - minResponseTime)) + 
										minResponseTime) || 
										jSliderMinAvailability.
										getValue() != (int) (Math.round(
												jSliderRelaxation.
												getValue() / 100.0 * (
														minAvailability - 
														maxAvailability)) + 
														maxAvailability))) {
			disableRelaxationSlider();
		}
	}
	
	private void useRelaxationSlider() {
		jTextFieldRelaxation.setText(
				String.valueOf(jSliderRelaxation.getValue() / 100.0));
		if (jCheckBoxMaxCosts.isSelected()) {
			jSliderMaxCosts.setValue(
					(int) (Math.round(jSliderRelaxation.getValue() / 100.0 * (
							maxCosts - minCosts)) + minCosts));
		}
		if (jCheckBoxMaxResponseTime.isSelected()) {
			jSliderMaxResponseTime.setValue(
					(int) (Math.round(jSliderRelaxation.getValue() / 100.0 * (
							maxResponseTime - minResponseTime)) + 
							minResponseTime));
		}
		if (jCheckBoxMinAvailability.isSelected()) {
			jSliderMinAvailability.setValue(
					(int) (Math.round(jSliderRelaxation.getValue() / 100.0 * (
							minAvailability - maxAvailability)) + 
							maxAvailability));
		}
	}
	
	private void checkRelaxationStatus() {
		if (jCheckBoxRelaxation.isSelected()) {
			useRelaxationSlider();
			jTextFieldRelaxation.setText(String.valueOf(
					jSliderRelaxation.getValue() / 100.0));
			jSliderRelaxation.setEnabled(true);
		}
		else {
			jSliderRelaxation.setEnabled(false);
			jTextFieldRelaxation.setText("-");
		}
	}
	
	private void disableRelaxationSlider() {
		jCheckBoxRelaxation.setSelected(false);
		jSliderRelaxation.setEnabled(false);
		jTextFieldRelaxation.setText("-");
	}

	// Set the extrem values of the constraint sliders. The values  
	// are computed according to the approach of Gao et al., which
	// can be found under "4. Simulation Analysis" in their paper 
	// "Qos-aware Service Composition based on Tree-Coded Genetic 
	// Algorithm".
	private void setSliderExtremeValues() {
		QosVector qosMaxComposition = determineQosMaxComposition(
				serviceClassesList);
		QosVector qosMinComposition = determineQosMinComposition(
				serviceClassesList);
		maxCosts = (int) Math.ceil(qosMaxComposition.getCosts());
		minCosts = (int) Math.floor(qosMinComposition.getCosts());
		maxResponseTime = (int) Math.ceil(qosMaxComposition.getResponseTime());
		minResponseTime = (int) Math.floor(
				qosMinComposition.getResponseTime());
		maxAvailability = (int) Math.ceil(
				qosMaxComposition.getAvailability() * 100);
		minAvailability = (int) Math.floor(
				qosMinComposition.getAvailability() * 100);

		jSliderMaxCosts.setMaximum(maxCosts);
		jSliderMaxCosts.setMinimum(minCosts);
		jTextFieldMaxCosts.setToolTipText("<html>Max. Costs<br>" +
				"Margin: " + minCosts + " - " + maxCosts + "</html>");
		jSliderMaxResponseTime.setMaximum(maxResponseTime);
		jSliderMaxResponseTime.setMinimum(minResponseTime);
		jTextFieldMaxResponseTime.setToolTipText(
				"<html>Max. Response Time<br>" +"Margin: " + minResponseTime + 
				" - " + maxResponseTime + "</html>");
		jSliderMinAvailability.setMaximum(maxAvailability);
		jSliderMinAvailability.setMinimum(minAvailability);
		jTextFieldMinAvailability.setToolTipText(
				"<html>Min. Availability<br>" +"Margin: " + minAvailability + 
				" - " + maxAvailability + "</html>");
		
		// Set the values of the constraints according  
		// to the given constraints relaxation.
		useRelaxationSlider();
	}
	
	private void changeWeight(JTextField textField) {
		try {
			Integer.parseInt(textField.getText());
		} catch (NumberFormatException e) {
			textField.setText("0");
		}
		int cumulatedPercentage = 0;
		if (jCheckBoxMaxCosts.isSelected()) {
			cumulatedPercentage += Integer.parseInt(
					jTextFieldCostsWeight.getText());
		}
		if (jCheckBoxMaxResponseTime.isSelected()) {
			cumulatedPercentage += Integer.parseInt(
					jTextFieldResponseTimeWeight.getText());
		}
		if (jCheckBoxMinAvailability.isSelected()) {
			cumulatedPercentage += Integer.parseInt(
					jTextFieldAvailabilityWeight.getText());
		}
		
		lblWeightSum.setText(String.valueOf(cumulatedPercentage));
		if (cumulatedPercentage != 100) {
			lblWeightSum.setForeground(Color.RED);
			lblWeightSumSigma.setForeground(Color.RED);
			correctWeights = false;
			writeErrorLogEntry(
					"Sum of active constraint weights has to be 100%");
		}
		else {
			lblWeightSumSigma.setForeground(Color.GREEN);
			lblWeightSum.setForeground(Color.GREEN);
			correctWeights = true;
			getUtilityFunction();
		}		
		checkEnableStartButton();
	}

	
	
	/*	+-----------------------------------------------------------+
	 * 	| +-------------------------------------------------------+ |
	 * 	| |														  | |
	 * 	| |				 EXECUTING ALGORITHM METHODS			  | |
	 * 	| |														  | |
	 * 	| +-------------------------------------------------------+ |
	 * 	+-----------------------------------------------------------+
	 */
	
	private void pressStartButton() {
		final Map<String, Constraint> constraintsMap = getChosenConstraints();
		if (jCheckBoxBenchmarkMode.isSelected()) {
			benchmarkMode = true;
		}
		else {
			benchmarkMode = false;
		}
		cumulatedRuntime = 0;
		algorithmInProgress = true;

		// Calculate the utility value for all service candidates.
		QosVector qosMaxServiceCandidate = determineQosMaxServiceCandidate(
				serviceCandidatesList);
		QosVector qosMinServiceCandidate = determineQosMinServiceCandidate(
				serviceCandidatesList);
		for (ServiceCandidate serviceCandidate : serviceCandidatesList) {
			serviceCandidate.determineUtilityValue(constraintsMap, 
					qosMaxServiceCandidate, qosMinServiceCandidate);
		}
		jProgressBarGeneticAlgorithm.setValue(0);
		jProgressBarAnalyticAlgorithm.setValue(0);

		if (jCheckboxGeneticAlgorithm.isSelected()) {
			if (!jCheckBoxElitismRate.isSelected()) {
				jTextFieldElitismRate.setText("0");
			}
			if (!jTextFieldTerminationDegree.isVisible()) {
				jTextFieldTerminationDegree.setText("0");
			}
			geneticAlgorithm = new GeneticAlgorithm(
					serviceClassesList, constraintsMap, 
					Integer.parseInt(jTextFieldPopulationSize.getText()), 
					Integer.parseInt(jTextFieldTerminationCriterion.getText()),
					(String) jComboBoxSelection.getSelectedItem(),
					Integer.parseInt(jTextFieldElitismRate.getText()),
					((String) jComboBoxCrossover.getSelectedItem()),
					Integer.parseInt(jTextFieldCrossoverRate.getText()),
					Integer.parseInt(jTextFieldMutationRate.getText()),
					((String) jComboBoxTerminationCriterion.getSelectedItem()),
					Integer.parseInt(jTextFieldTerminationDegree.getText()));
		}
		if (jCheckBoxAntColonyOptimization.isSelected()) {
			int variant;
			int iterations;
			int ants;
			double alpha;
			double beta;
			double dilution;
			double piInit;
			try {
				variant = Integer.parseInt(txtAntVariant.getText());
				iterations = Integer.parseInt(txtAntIterations.getText());
				ants = Integer.parseInt(txtAntAnts.getText());
				alpha = Double.parseDouble(txtAntAlpha.getText());
				beta = Double.parseDouble(txtAntBeta.getText());
				dilution = Double.parseDouble(txtAntDilution.getText());
				piInit = Double.parseDouble(txtAntPi.getText());
			} catch (Exception e) {
				variant = DEFAULT_VARIANT;
				iterations = DEFAULT_ITERATIONS;
				ants = DEFAULT_ANTS;
				alpha = DEFAULT_ALPHA;
				beta = DEFAULT_BETA;
				dilution = DEFAULT_DILUTION;
				piInit = DEFAULT_PIINIT;
			}
			antAlgorithm = new AntAlgorithm(
					serviceClassesList, serviceCandidatesList, constraintsMap,
					variant, iterations, ants, alpha, beta,
					dilution, piInit);
		}	
		if (jCheckBoxAnalyticAlgorithm.isSelected()) {
			analyticAlgorithm = new AnalyticAlgorithm(
					serviceClassesList, constraintsMap, 
					(Integer) jSpinnerNumberResultTiers.getValue());
		}


		if (!benchmarkMode && 
				(jCheckboxGeneticAlgorithm.isSelected() || 
						jCheckBoxAnalyticAlgorithm.isSelected() || 
						jCheckBoxAntColonyOptimization.isSelected())) {
			new Thread() {
				@Override
				public void run() {
					while(algorithmInProgress) {
						if (jCheckboxGeneticAlgorithm.isSelected()) {
							jProgressBarGeneticAlgorithm.setValue(
									geneticAlgorithm.getWorkPercentage());
						}						
						if (jCheckBoxAntColonyOptimization.isSelected()) {
							jProgressBarAntAlgorithm.setValue(
									antAlgorithm.getWorkPercentage());
						}
						if (jCheckBoxAnalyticAlgorithm.isSelected()) {
							jProgressBarAnalyticAlgorithm.setValue(
									analyticAlgorithm.getWorkPercentage());
						}
						try {
							sleep(100);
						} catch (InterruptedException e) {
						}
					}
				}
			}.start();
		}
		
		// BENCHMARK MODE
		if (benchmarkMode) {
			if (jCheckboxGeneticAlgorithm.isSelected()) {
				String[][] iterationValueArray = 
						new String[NUMBER_OF_BENCHMARK_ITERATIONS][3];
				for (int i = 0; i < NUMBER_OF_BENCHMARK_ITERATIONS; i++) {
					geneticAlgorithm.startInBenchmarkMode();
					iterationValueArray[i][0] = String.valueOf(i + 1);
					iterationValueArray[i][1] = String.valueOf(
							geneticAlgorithm.getRuntime());
					if (geneticAlgorithm.getAlgorithmSolutionTiers().
							size() > 0) {
						iterationValueArray[i][2] = String.valueOf(
								geneticAlgorithm.getAlgorithmSolutionTiers().
								get(0).getServiceCompositionList().
								get(0).getUtility());
					}
					else {
						iterationValueArray[i][2] = "No Solution";
					}
				}
				
				File file = new File("benchmark_genetic.csv");
				checkOverwrite(file, "Benchmark has");
				BufferedWriter bufferedWriter = null;
				try {
					bufferedWriter = new BufferedWriter(new FileWriter(file));
					bufferedWriter.write("Iteration;Runtime;Utility");			
					for (int i = 0; i < NUMBER_OF_BENCHMARK_ITERATIONS; i++) {				
						bufferedWriter.newLine();
						bufferedWriter.write(iterationValueArray[i][0] + ";" + 
								iterationValueArray[i][1] + ";" + 
								iterationValueArray[i][2]);
					}
					bufferedWriter.close();
					writeErrorLogEntry("File benchmark_genetic has been " +
							"created successfully");
				} catch (IOException e1) {			
					writeErrorLogEntry("File benchmark_genetic has not been " +
							"created successfully");
				}
			}
			if (jCheckBoxAnalyticAlgorithm.isSelected()) {
				String[][] iterationValueArray = 
						new String[NUMBER_OF_BENCHMARK_ITERATIONS][3];
				for (int i = 0; i < NUMBER_OF_BENCHMARK_ITERATIONS; i++) {
					analyticAlgorithm.startInBenchmarkMode();
					iterationValueArray[i][0] = String.valueOf(i + 1);
					iterationValueArray[i][1] = String.valueOf(
							analyticAlgorithm.getRuntime());
					if (analyticAlgorithm.getAlgorithmSolutionTiers().
							size() > 0) {
						iterationValueArray[i][2] = String.valueOf(
								analyticAlgorithm.getAlgorithmSolutionTiers().
								get(0).getServiceCompositionList().
								get(0).getUtility());
					}
					else {
						iterationValueArray[i][2] = "No Solution";
					}
				}
				
				File file = new File("benchmark_analytic.csv");
				checkOverwrite(file, "Benchmark has");
				BufferedWriter bufferedWriter = null;
				try {
					bufferedWriter = new BufferedWriter(new FileWriter(file));
					bufferedWriter.write("Iteration;Runtime;Utility");			
					for (int i = 0; i < NUMBER_OF_BENCHMARK_ITERATIONS; i++) {				
						bufferedWriter.newLine();
						bufferedWriter.write(iterationValueArray[i][0] + ";" + 
								iterationValueArray[i][1] + ";" + 
								iterationValueArray[i][2]);
					}
					bufferedWriter.close();
					writeErrorLogEntry("File benchmark_analytic has " +
							"been created successfully");
				} catch (IOException e1) {			
					writeErrorLogEntry("File benchmark_analytic has not " +
							"been created successfully");
				}
			}
		}
		// STANDARD MODE
		else {
			// Calculation and Results Display Thread
			new Thread() {
				@Override
				public void run() {
					setEnabled(false);
					jButtonStart.setEnabled(false);
					if (jCheckboxGeneticAlgorithm.isSelected()) {
						doGeneticAlgorithm();
					}
					if (jCheckBoxAntColonyOptimization.isSelected()) {
						doAntAlgorithm();					
					}
					if (jCheckBoxAnalyticAlgorithm.isSelected()) {
						doEnumeration();
					}						
					algorithmInProgress = false;
					if (jCheckboxGeneticAlgorithm.isSelected()) {
						jProgressBarGeneticAlgorithm.setValue(100);
					}
					if (jCheckBoxAntColonyOptimization.isSelected()) {
						jProgressBarAntAlgorithm.setValue(100);
					}
					if (jCheckBoxAnalyticAlgorithm.isSelected()) {
						jProgressBarAnalyticAlgorithm.setValue(100);
					}
					if (cumulatedRuntime > (120000.0 * 1000000.0)) {
						jTableGeneralResults.setValueAt(DECIMAL_FORMAT_TWO.
								format(cumulatedRuntime / 
										(60000.0 * 1000000.0)) + " min", 0, 1);
					}
					else if (cumulatedRuntime > (1000.0 * 1000000.0)) {
						jTableGeneralResults.setValueAt(DECIMAL_FORMAT_TWO.
								format(cumulatedRuntime / 
										(1000.0 * 1000000.0)) + " s", 0, 1);
					}
					else if (cumulatedRuntime > 1000000.0) {
						jTableGeneralResults.setValueAt(DECIMAL_FORMAT_TWO.
								format(cumulatedRuntime / 1000000.0) + 
								" ms", 0, 1);
					}
					else {
						jTableGeneralResults.setValueAt(DECIMAL_FORMAT_TWO.
								format(cumulatedRuntime) + " ns", 0, 1);
					}
					// TODO: If ant algorithm has no solution, 
					//		 handle output visualization!
					if (jCheckBoxAnalyticAlgorithm.isSelected()) {
						double optimalUtility = 0.0;
						if (analyticAlgorithm.getAlgorithmSolutionTiers().
								size() > 0) {
							optimalUtility = analyticAlgorithm.
									getAlgorithmSolutionTiers().								
									get(0).getServiceCompositionList().
									get(0).getUtility();
						}
						if (jCheckboxGeneticAlgorithm.isSelected()) {
							if (geneticAlgorithm.getAlgorithmSolutionTiers().
									size() > 0) {
								double geneticDelta = optimalUtility - 
										geneticAlgorithm.
										getAlgorithmSolutionTiers().
										get(0).getServiceCompositionList().
										get(0).getUtility();
								jTableGeneralResults.setValueAt(
										DECIMAL_FORMAT_FOUR.format(
												geneticDelta) + " (" + 
												DECIMAL_FORMAT_TWO.format(
														Math.abs(geneticDelta / 
																optimalUtility 
																* 100)) + 
																"%)" , 4, 1);
							}
							else {
								jTableGeneralResults.setValueAt(
										"<html><b color=red>No Solution" +
												"</b></html>", 4, 1);
							}
						}
						if (jCheckBoxAntColonyOptimization.isSelected()) {
							double antDelta = optimalUtility - antAlgorithm.
									getAlgorithmSolutionTiers().get(0).
									getServiceCompositionList().get(0).
									getUtility();
							jTableGeneralResults.setValueAt(
									DECIMAL_FORMAT_FOUR.format(antDelta) + 
									" (" + DECIMAL_FORMAT_TWO.format(Math.abs(
											antDelta / optimalUtility * 100)) + 
											"%)" , 5, 1);
						}
					}
					buildResultTable();
					if (benchmarkMode) {
						jButtonVisualize.setEnabled(false);
					}
					else {
						jButtonVisualize.setEnabled(true);
					}
					jButtonSaveResults.setEnabled(true);
					jButtonStart.setEnabled(true);
					setEnabled(true);				
				}
			}.start();
		}

		
	}			
	
	private void doGeneticAlgorithm() {
		geneticAlgorithm.start();
		double runtime = geneticAlgorithm.getRuntime();
		cumulatedRuntime += runtime;
		if (runtime > (120000.0 * 1000000.0)) {
			jTableGeneralResults.setValueAt(DECIMAL_FORMAT_TWO.format(
					runtime / (60000.0 * 1000000.0)) + " min", 1, 1);
		}
		else if (runtime > (1000.0 * 1000000.0)) {
			jTableGeneralResults.setValueAt(DECIMAL_FORMAT_TWO.format(
					runtime / (1000.0 * 1000000.0)) + " s", 1, 1);
		}
		else if (runtime > 1000000.0) {
			jTableGeneralResults.setValueAt(DECIMAL_FORMAT_TWO.format(
					runtime / 1000000.0) + " ms", 1, 1);
		}
		else {
			jTableGeneralResults.setValueAt(DECIMAL_FORMAT_TWO.format(
					runtime) + " ns", 1, 1);
		}
	}
	
	private void doAntAlgorithm() {			
		antAlgorithm.start();
		double runtime = antAlgorithm.getRuntime();
		cumulatedRuntime += runtime;
		if (runtime > (120000.0 * 1000000.0)) {
			jTableGeneralResults.setValueAt(DECIMAL_FORMAT_TWO.format(
					runtime / (60000.0 * 1000000.0)) + " min", 2, 1);
		}
		else if (runtime > (1000.0 * 1000000.0)) {
			jTableGeneralResults.setValueAt(DECIMAL_FORMAT_TWO.format(
					runtime / (1000.0 * 1000000.0)) + " s", 2, 1);
		}
		else if (runtime > 1000000.0) {
			jTableGeneralResults.setValueAt(DECIMAL_FORMAT_TWO.format(
					runtime / 1000000.0) + " ms", 2, 1);
		}
		else {
			jTableGeneralResults.setValueAt(DECIMAL_FORMAT_TWO.format(
					runtime) + " ns", 2, 1);
		}
	}
	
	private void doEnumeration() {
		analyticAlgorithm.start();
		double runtime = analyticAlgorithm.getRuntime();
		cumulatedRuntime += runtime;
		if (runtime > (120000.0 * 1000000.0)) {
			jTableGeneralResults.setValueAt(DECIMAL_FORMAT_TWO.format(
					runtime / (60000.0 * 1000000.0)) + " min", 3, 1);
		}
		else if (runtime > (1000.0 * 1000000.0)) {
			jTableGeneralResults.setValueAt(DECIMAL_FORMAT_TWO.format(
					runtime / (1000.0 * 1000000.0)) + " s", 3, 1);
		}
		else if (runtime > 1000000.0) {
			jTableGeneralResults.setValueAt(DECIMAL_FORMAT_TWO.format(
					runtime / 1000000.0) + " ms", 3, 1);
		}
		else {
			jTableGeneralResults.setValueAt(DECIMAL_FORMAT_TWO.format(
					runtime) + " ns", 3, 1);
		}
	}
	
	private void buildGeneticAlgorithmFitnessFunction() {
		int i = 1;
		String formula = "<html>Utility * (1 - (";
		if (jCheckBoxMaxCosts.isSelected()) {
			formula += "w<sub>" + i + "</sub> * " +
					"\u03B4<sub>costs</sub>";
			i++;
		}
		if (jCheckBoxMaxResponseTime.isSelected()) {
			if (formula.equals("<html>Utility * (1 - (")) {
				formula += "w<sub>" + i + "</sub> * " +
						"\u03B4<sub>response time</sub>";
			}
			else {
				formula += " + w<sub>" + i + "</sub> * " +
						"\u03B4<sub>response time</sub>";
			}
			i++;
		}
		if (jCheckBoxMinAvailability.isSelected()) {
			if (formula.equals("<html>Utility * (1 - (")) {
				formula += "w<sub>" + i + "</sub> * " +
						"\u03B4<sub>availability</sub>";
			}
			else {
				formula += " + w<sub>" + i + "</sub> * " +
						"\u03B4<sub>availability</sub>";
			}
			i++;
		}
		if (formula.equals("<html>Utility * (1 - (")) {
			jLabelFitnessBraceLeft.setVisible(false);
			jLabelFitnessCaseTwo.setVisible(false);
			jLabelFitnessCaseTwoDescription.setVisible(false);
			jLabelFitnessCaseOneDescription.setVisible(false);
		}
		else {
			formula += " ))</html>";
			jLabelFitnessCaseTwo.setText(formula);
			jLabelFitnessBraceLeft.setVisible(true);
			jLabelFitnessCaseTwo.setVisible(true);
			jLabelFitnessCaseTwoDescription.setVisible(true);
			jLabelFitnessCaseOneDescription.setVisible(true);
		}
	}

	private void setElitismRateSelection() {
		if (jCheckBoxElitismRate.isSelected()) {
			jTextFieldElitismRate.setEditable(true);
		}
		else {
			jTextFieldElitismRate.setEditable(false);
		}
	}

	private void showExtendedTerminationCriterionSettings() {
		if (jComboBoxTerminationCriterion.getSelectedIndex() == 1) {
			jLabelTerminationDegree.setVisible(true);
			jLabelTerminationDegreeClose.setVisible(true);
			jTextFieldTerminationDegree.setText(
					String.valueOf(DEFAULT_DEGREE_OF_EQUALITY));
			jTextFieldTerminationDegree.setVisible(true);
			// TODO: Find a typical value.
			jTextFieldTerminationCriterion.setToolTipText("<html>Number of " +
			"consecutive generations which contain<br>(almost) the same " +
			"compositions<br>" +
			"Typical value: 20</html>");
		}
		else {
			if (jComboBoxTerminationCriterion.getSelectedIndex() == 0) {
				jTextFieldTerminationCriterion.setToolTipText("<html>Number " +
						"of generations created by the algorithm<br>" +
						"Typical value: 100-1000</html>");
			}
			else {
				// TODO: Find a typical value.
				jTextFieldTerminationCriterion.setToolTipText(
						"<html>Number of consecutive generations with the" +
						"same maximal fitness value<br>" +
						"Typical value: 20</html>");
			}
			jLabelTerminationDegree.setVisible(false);
			jLabelTerminationDegreeClose.setVisible(false);
			jTextFieldTerminationDegree.setVisible(false);
		}
	}


	
	/*	+-----------------------------------------------------------+
	 * 	| +-------------------------------------------------------+ |
	 * 	| |														  | |
	 * 	| |		 			   RESULTS METHODS			 		  | |
	 * 	| |														  | |
	 * 	| +-------------------------------------------------------+ |
	 * 	+-----------------------------------------------------------+
	 */
	
	private void buildResultTable() {
		if (jTabbedPane.getTabCount() > 0) {
			jTabbedPane.removeAll();
		}
		Map<String, Algorithm> algorithmsMap = getChosenAlgorithms();
		if (algorithmsMap == null) {
			return;
		}
		// COUNTER FOR EVERY CHOSEN ALGORITHM
		saveResultList = new LinkedList<String>();
		for (Map.Entry<String, Algorithm> entry : algorithmsMap.entrySet()) {
			showAlgorithmResults(entry.getValue(), entry.getKey());
		}		
	}
	
	private void showAlgorithmResults(Algorithm algorithm, 
			String algorithmTitle) {
		JScrollPane jScrollPane = new JScrollPane();
		this.jTabbedPane.addTab(algorithmTitle, jScrollPane);

		JPanel jPanelAlgorithmResult = new JPanel();
		GridBagLayout gblJPanelAlgorithmResult = new GridBagLayout();
		gblJPanelAlgorithmResult.columnWeights = new double[]{1.0};
		double[] rows = 
			new double[algorithm.getAlgorithmSolutionTiers().size() * 2];
		if (rows.length == 2) {
			rows[0] = 0.1;
			rows[1] = 0.9;
		}
		else {
			for (int rowCount = 0; rowCount < rows.length; rowCount++) {
				if (rowCount % 2 == 0) {
					rows[rowCount] = 90.0 / (rows.length / 2 + 1);
				}
				else {
					rows[rowCount] = 10.0 / (rows.length / 2 + 1);
				}
			}
		}
		
		gblJPanelAlgorithmResult.rowWeights = rows;
		jPanelAlgorithmResult.setLayout(gblJPanelAlgorithmResult);
		jScrollPane.setViewportView(jPanelAlgorithmResult);

		// COUNTER FOR ALL TIER TABLES
		for (int i = 1; i < rows.length; i = i + 2) {
			List<Composition> tierServiceCompositionList = 
				new LinkedList<Composition>(
						algorithm.getAlgorithmSolutionTiers().get(
								i / 2).getServiceCompositionList());
			int numberOfRows = 0;
			// COUNTER FOR COMPUTING THE NUMBER OF COMPOSITIONS PER TIER
			for (int rowCount = 0; rowCount < 
			tierServiceCompositionList.size(); rowCount++) {
				numberOfRows += tierServiceCompositionList.get(
						rowCount).getServiceCandidatesList().size();
			}
			
			// TABLE CONSTRUCTION
			String[] tierTablesColumnNames = {"# Service", 
					"Service Title", "Service Class", "Utility Value", "Costs", 
					"Response Time", "Availability"};
			ServiceSelectionTable jTableTier = new ServiceSelectionTable(
					numberOfRows + tierServiceCompositionList.size(), 
					tierTablesColumnNames.length, false);
			GridBagConstraints gbcJTableTier = new GridBagConstraints();
			gbcJTableTier.gridx = 0;
			gbcJTableTier.gridy = i;
			gbcJTableTier.fill = GridBagConstraints.HORIZONTAL;
			gbcJTableTier.anchor = GridBagConstraints.NORTH;
			jPanelAlgorithmResult.add(jTableTier, gbcJTableTier);
			
			jTableTier.setColumnTextAlignment(
					0, DefaultTableCellRenderer.CENTER);
			jTableTier.setColumnTextAlignment(
					2, DefaultTableCellRenderer.CENTER);
			jTableTier.setColumnTextAlignment(
					3, DefaultTableCellRenderer.CENTER);
			
			// COUNTER FOR CONSTRUCTION OF TABLE HEADERS
			for (int columnCount = 0; columnCount < 
			tierTablesColumnNames.length; columnCount++) {
				jTableTier.getColumnModel().getColumn(
						columnCount).setHeaderValue(
								tierTablesColumnNames[columnCount]);
			}
			if (i == 1) {
				GridBagConstraints gbc_tableHeader = new GridBagConstraints();
				gbc_tableHeader.gridx = 0;
				gbc_tableHeader.gridy = 0;
				gbc_tableHeader.fill = GridBagConstraints.HORIZONTAL;
				gbc_tableHeader.anchor = GridBagConstraints.SOUTH;
				jTableTier.getTableHeader().setVisible(true);
				jPanelAlgorithmResult.add(
						jTableTier.getTableHeader(), gbc_tableHeader);
			}
			int x = 0;
			// COUNTER FOR ALL ROWS OF A TIER
			for (int rowCount = 0; rowCount < numberOfRows; rowCount++) {
				jTableTier.setValueAt("<html><b>" + DECIMAL_FORMAT_FOUR.format(
						tierServiceCompositionList.get(rowCount).
						getUtility()) + "</b></html>", rowCount, 3);
				// Build String for Result-Export
				String resultLine = "";
				resultLine += algorithmTitle;
				resultLine += ";" + algorithm.getRuntime();
				resultLine += ";" + tierServiceCompositionList.get(rowCount).
						getUtility();
				resultLine += ";" + tierServiceCompositionList.get(rowCount).
						getQosVectorAggregated().getCosts();
				resultLine += ";" + tierServiceCompositionList.get(rowCount).
						getQosVectorAggregated().getResponseTime();
				resultLine += ";" + tierServiceCompositionList.get(rowCount).
						getQosVectorAggregated().getAvailability();				
				saveResultList.add(resultLine);
				
				if (getChosenConstraints().get(Constraint.COSTS) != null && 
						tierServiceCompositionList.get(rowCount).
						getQosVectorAggregated().getCosts() > 
						getChosenConstraints().get(Constraint.COSTS).
						getValue()) {
					jTableTier.setValueAt("<html><b><font color=red>" + 
							DECIMAL_FORMAT_TWO.format(
									tierServiceCompositionList.get(rowCount).
									getQosVectorAggregated().getCosts()) + 
									"</font></b></html>", rowCount + x, 4);
				}
				else {
					jTableTier.setValueAt("<html><b>" + 
							DECIMAL_FORMAT_TWO.format(
									tierServiceCompositionList.get(rowCount).
									getQosVectorAggregated().getCosts()) + 
									"</b></html>", rowCount + x, 4);
				}
				if (getChosenConstraints().get(Constraint.RESPONSE_TIME) != 
						null && tierServiceCompositionList.get(rowCount).
						getQosVectorAggregated().getResponseTime() > 
						getChosenConstraints().get(Constraint.RESPONSE_TIME).
						getValue()) {
					jTableTier.setValueAt("<html><b><font color=red>" + 
							DECIMAL_FORMAT_TWO.format(
									tierServiceCompositionList.get(rowCount).
									getQosVectorAggregated().
									getResponseTime()) + 
									"</font></b></html>", rowCount + x, 5);
				}
				else {
					jTableTier.setValueAt("<html><b>" + 
							DECIMAL_FORMAT_TWO.format(
									tierServiceCompositionList.get(rowCount).
									getQosVectorAggregated().
									getResponseTime()) + 
									"</b></html>", rowCount + x, 5);
				}
				if (getChosenConstraints().get(Constraint.AVAILABILITY) != 
						null && tierServiceCompositionList.get(rowCount).
						getQosVectorAggregated().getAvailability() < 
						getChosenConstraints().get(Constraint.AVAILABILITY).
						getValue()) {
					jTableTier.setValueAt("<html><b><font color=red>" + 
							DECIMAL_FORMAT_TWO.format(
									tierServiceCompositionList.get(rowCount).
									getQosVectorAggregated().
									getAvailability()) + 
									"</font></b></html>", rowCount + x, 6);
				}
				else {
					jTableTier.setValueAt("<html><b>" + 
							DECIMAL_FORMAT_TWO.format(
									tierServiceCompositionList.get(rowCount).
									getQosVectorAggregated().
									getAvailability()) + 
									"</b></html>", rowCount + x, 6);
				}
				x++;
				int candidateCount = 0;
				// COUNTER FOR ALL SERVICE CANDIDATES PER COMPOSITION
				for (candidateCount = 0; candidateCount < 
				tierServiceCompositionList.get(rowCount).
				getServiceCandidatesList().size(); candidateCount++) {
					// SERVICE CANDIDATE ID
					jTableTier.setValueAt(
							tierServiceCompositionList.get(rowCount).
							getServiceCandidatesList().
							get(candidateCount).getServiceCandidateId(), 
							rowCount + x + candidateCount, 0);
					// SERVICE CANDIDATE TITLE
					jTableTier.setValueAt(
							tierServiceCompositionList.get(rowCount).
							getServiceCandidatesList().
							get(candidateCount).getName(), 
							rowCount + x + candidateCount, 1);
					// SERVICE CLASS ID
					jTableTier.setValueAt(
							tierServiceCompositionList.get(rowCount).
							getServiceCandidatesList().
							get(candidateCount).getServiceClassId(), 
							rowCount + x + candidateCount, 2);
					// COSTS
					jTableTier.setValueAt(DECIMAL_FORMAT_TWO.format(
							tierServiceCompositionList.get(rowCount).
							getServiceCandidatesList().get(
									candidateCount).getQosVector().
									getCosts()), 
									rowCount + x + candidateCount, 4);
					// RESPONSE TIME
					jTableTier.setValueAt(DECIMAL_FORMAT_TWO.format(
							tierServiceCompositionList.get(rowCount).
							getServiceCandidatesList().get(
									candidateCount).getQosVector().
									getResponseTime()), 
									rowCount + x + candidateCount, 5);
					// AVAILABILITY
					jTableTier.setValueAt(DECIMAL_FORMAT_TWO.format(
							tierServiceCompositionList.get(rowCount).
							getServiceCandidatesList().get(
									candidateCount).getQosVector().
									getAvailability()), 
									rowCount + x + candidateCount, 6);
				}
				rowCount = rowCount + candidateCount - 1;
			}
			jTableTier.setEnabled(false);
			
			if (i + 1 < rows.length) {
				JSeparator jSeparatorTierTables = new JSeparator();
				GridBagConstraints gbcJSeparatorTierTables = 
					new GridBagConstraints();
				gbcJSeparatorTierTables.gridx = 0;
				gbcJSeparatorTierTables.gridy = i + 1;
				gbcJSeparatorTierTables.fill = GridBagConstraints.HORIZONTAL;
				gbcJSeparatorTierTables.anchor = GridBagConstraints.NORTH;
				gbcJSeparatorTierTables.insets = new Insets(10, 5, 10, 5);
				jPanelAlgorithmResult.add(
						jSeparatorTierTables, gbcJSeparatorTierTables);
			}
		}
		if (jPanelAlgorithmResult.getComponents().length == 0) {
			jPanelAlgorithmResult.add(new JLabel("<html><h1 color=red>" +
					"No Solution</h1></html>"));
		}
	}
	
	private void saveResults() {
		final ServiceSelectionFileChooser fileChooser = 
				new ServiceSelectionFileChooser("Result");
		if (!(fileChooser.showSaveDialog(MainFrame.this) == 
				JFileChooser.APPROVE_OPTION)) {
			return;
		}
		File file = fileChooser.getSelectedFile();
		checkOverwrite(file, "Results have");
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(file));
			String header = "Algorithm;Runtime;Utility;Costs;" +
					"Response Time;Availability";
			bufferedWriter.write(header);			
			for (String line : saveResultList) {				
				bufferedWriter.newLine();
				bufferedWriter.write(line);
			}
			bufferedWriter.close();
		} catch (IOException e1) {			
			e1.printStackTrace();
		}
	}
	
	private void showResultVisualization() {
		int[] serviceCandidatesPerClass = 
			new int[serviceClassesList.size()];
		for (int i = 0; i < serviceClassesList.size(); i++) {
			serviceCandidatesPerClass[i] = 
				serviceClassesList.get(i).
				getServiceCandidateList().size();
		}
		if (algorithmVisualization != null) {
			algorithmVisualization.closeWindow();
		}
		algorithmVisualization = 
				new AlgorithmsVisualization( 
						geneticAlgorithm.getNumberOfDifferentSolutions(),
						geneticAlgorithm.getMaxUtilityPerPopulation(),
						geneticAlgorithm.getAverageUtilityPerPopulation(),
						antAlgorithm.getOptUtilityPerIteration());
	}
	
	
	
	/*	+-----------------------------------------------------------+
	 * 	| +-------------------------------------------------------+ |
	 * 	| |														  | |
	 * 	| |				   	  OTHER GUI METHODS					  | |
	 * 	| |														  | |
	 * 	| +-------------------------------------------------------+ |
	 * 	+-----------------------------------------------------------+
	 */
	
	private void getUtilityFunction() {
		String utilityText = "<html>Utility Value(Composition)  =  ";
		boolean noConstraintsChosen = true;
		if (jCheckBoxMaxCosts.isSelected()) {
			utilityText += "(Costs<sub><small>norm</small></sub> * " + 
			(Double.parseDouble(jTextFieldCostsWeight.getText()) / 100.0) + 
			") + ";
			noConstraintsChosen = false;
		}
		if (jCheckBoxMaxResponseTime.isSelected()) {
			utilityText += "(Response Time<sub><small>norm</small></sub> * " + 
			(Double.parseDouble(
					jTextFieldResponseTimeWeight.getText()) / 100.0) + ") + ";
			noConstraintsChosen = false;
		}
		if (jCheckBoxMinAvailability.isSelected()) {
			utilityText += "(Availability<sub><small>norm</small></sub> * " + 
			(Double.parseDouble(
					jTextFieldAvailabilityWeight.getText()) / 100.0) + ")";
			noConstraintsChosen = false;
		}
		if (noConstraintsChosen) {
			jLabelUtilityText.setText("");
			return;
		}
		if (utilityText.endsWith("+ ")) {
			utilityText = utilityText.substring(0, utilityText.length() - 3);
		}
		jLabelUtilityText.setText(utilityText + "</html>");
	}
	
	private void writeErrorLogEntry(String entry) {
		textAreaLog.append("\n" + dateFormatLog.format(new Date()) + entry);
	}
	
	private void checkInputValue(JTextField textField, 
			int maxInput, int minInput, int defaultInput) {
		if (textField.equals(jTextFieldPopulationSize) && 
				serviceClassesList != null) {
//			int populationSize = Integer.parseInt(textField.getText());
			long maxPopulationSize = 1;
			for (ServiceClass serviceClass : serviceClassesList) {
				maxPopulationSize *= 
						serviceClass.getServiceCandidateList().size();
				if (maxPopulationSize < 0) {
					maxPopulationSize = Long.MAX_VALUE;
					break;
				}
			}
			if (maxPopulationSize < Long.MAX_VALUE) {
				if (Long.parseLong(
						textField.getText()) > maxPopulationSize) {
					textField.setText(String.valueOf(maxPopulationSize));
//					jLabelPopulationPercentage.setText("( = 100 % )");
					writeErrorLogEntry(
							"Value has to be between " + minInput + 
							" and " + maxPopulationSize);
					return;
				}
				else if (Long.parseLong(textField.getText()) < minInput) {
					textField.setText(String.valueOf(minInput));
//					jLabelPopulationPercentage.setText(
//							"( = " + DECIMAL_FORMAT_FOUR.format(
//									100.0 / maxPopulationSize) + " %)");
					writeErrorLogEntry(
							"Value has to be between " + minInput + 
							" and " + maxPopulationSize);
					return;
				}
				else {
					if (maxInput < Long.MAX_VALUE) {
//						jLabelPopulationPercentage.setText(
//								"( = " + DECIMAL_FORMAT_FOUR.format((double)
//										populationSize * 100.0 / 
//										maxPopulationSize) + 
//								" % )");
//						jLabelPopulationPercentage.setVisible(true);
					}
				}
			}
			else { 
//				jLabelPopulationPercentage.setVisible(false);
				writeErrorLogEntry(
						"Max. Population is too big to be computed");
			}
		}

		long input = 0;
		try {
			input = Long.parseLong(textField.getText());
		} catch (NumberFormatException e) {
			textField.setText(String.valueOf(defaultInput));
			writeErrorLogEntry("Value has to be from the type Integer");
		}
		if (input > maxInput || input < minInput) {
			writeErrorLogEntry("Value has to be between " + minInput + 
					" and " + maxInput);
			textField.setText(String.valueOf(defaultInput));
		}
	}
	
	private void checkEnableStartButton() {
		if (webServicesLoaded && correctWeights) {
			jButtonStart.setEnabled(true);
		}
		else {
			jButtonStart.setEnabled(false);
		}
	}
	
	private void chooseAlgorithm(String algorithm) {
		if (algorithm.equals("genAlg")) {
			if (!jCheckboxGeneticAlgorithm.isSelected()) {
				jTextFieldPopulationSize.setEditable(false);
				jCheckBoxElitismRate.setEnabled(false);
				jTextFieldElitismRate.setEditable(false);
				jTextFieldTerminationCriterion.setEditable(false);
				jComboBoxSelection.setEnabled(false);
				jComboBoxCrossover.setEnabled(false);
				jComboBoxTerminationCriterion.setEnabled(false);
			}
			else {
				jTextFieldPopulationSize.setEditable(true);
				jCheckBoxElitismRate.setEnabled(true);
				jTextFieldElitismRate.setEditable(true);
				jTextFieldTerminationCriterion.setEditable(true);
				jComboBoxSelection.setEnabled(true);
				jComboBoxCrossover.setEnabled(true);
				jComboBoxTerminationCriterion.setEnabled(true);
			}
		}
		else if (algorithm.equals("antAlg")) {
			if (!jCheckBoxAntColonyOptimization.isSelected()) {
				txtAntVariant.setEditable(false);
				txtAntIterations.setEditable(false);
				txtAntAnts.setEditable(false);
				txtAntAlpha.setEditable(false);
				txtAntBeta.setEditable(false);
				txtAntDilution.setEditable(false);
				txtAntPi.setEditable(false);
			}
			else {
				txtAntVariant.setEditable(true);
				txtAntIterations.setEditable(true);
				txtAntAnts.setEditable(true);
				txtAntAlpha.setEditable(true);
				txtAntBeta.setEditable(true);
				txtAntDilution.setEditable(true);
				txtAntPi.setEditable(true);;
			}
		}
		else {
			if (!jCheckBoxAnalyticAlgorithm.isSelected()) {
				jTableAnalyticAlgorithm.setEnabled(false);
			}
			else {
				jTableAnalyticAlgorithm.setEnabled(true);
			}
		}
	}
	
	
	
	/*	+-----------------------------------------------------------+
	 * 	| +-------------------------------------------------------+ |
	 * 	| |														  | |
	 * 	| |				   		 HELPER METHODS					  | |
	 * 	| |														  | |
	 * 	| +-------------------------------------------------------+ |
	 * 	+-----------------------------------------------------------+
	 */

	private Map<String, Constraint> getChosenConstraints() {
		Map<String, Constraint> constraintsMap = 
			new HashMap<String, Constraint>();
		if (jCheckBoxMaxCosts.isSelected()) {
			Constraint constraintCosts = new Constraint(Constraint.COSTS, 
					Double.valueOf(jTextFieldMaxCosts.getText()), 
					Double.parseDouble(jTextFieldCostsWeight.getText()));
			constraintsMap.put(constraintCosts.getTitle(), constraintCosts);
		}
		if (jCheckBoxMaxResponseTime.isSelected()) {
			Constraint constraintResponseTime = new Constraint(
					Constraint.RESPONSE_TIME, Double.valueOf(
							jTextFieldMaxResponseTime.getText()), 
							Double.parseDouble(
									jTextFieldResponseTimeWeight.getText()));
			constraintsMap.put(constraintResponseTime.getTitle(), 
					constraintResponseTime);
		}
		if (jCheckBoxMinAvailability.isSelected()) {
			Constraint constraintAvailability = new Constraint(
					Constraint.AVAILABILITY, (Double.valueOf(
							jTextFieldMinAvailability.getText())) / 100.0, 
							Double.parseDouble(
									jTextFieldAvailabilityWeight.getText()));
			constraintsMap.put(constraintAvailability.getTitle(), 
					constraintAvailability);
		}
		return constraintsMap;
	}

	private Map<String, Algorithm> getChosenAlgorithms() {
		Map<String, Algorithm> algorithmMap = new HashMap<String, Algorithm>();
		if (jCheckboxGeneticAlgorithm.isSelected()) {
			algorithmMap.put("Genetic Algorithm", geneticAlgorithm);
		}
		if (jCheckBoxAntColonyOptimization.isSelected()) {
			algorithmMap.put("Ant Colony Algorithm", antAlgorithm);
		}
		if (jCheckBoxAnalyticAlgorithm.isSelected()) {
			algorithmMap.put("Analytic Algorithm", analyticAlgorithm);
		}
		if (algorithmMap.size() == 0) {
			return null;
		}
		return algorithmMap;
	}
	
	// Determine the maximum value for each QoS attribute over all 
	// service candidates given to the method.
	// Note that "maximum" really means "maximum" and not "best".
	private QosVector determineQosMaxServiceCandidate(
			List<ServiceCandidate> serviceCandidates) {
		QosVector max = new QosVector(0.0, 0.0, 0.0);
		for (ServiceCandidate serviceCandidate : serviceCandidates) {
			QosVector qos = serviceCandidate.getQosVector();
			if (qos.getCosts() > max.getCosts()) {
				max.setCosts(qos.getCosts());
			}
			if (qos.getResponseTime() > max.getResponseTime()) {
				max.setResponseTime(qos.getResponseTime());
			}
			if (qos.getAvailability() > max.getAvailability()) {
				max.setAvailability(qos.getAvailability());
			}
		}
		return max;
	}
	
	// Determine the minimum value for each QoS attribute over all 
	// service candidates given to the method.
	// Note that "minimum" really means "minimum" and not "worst".
	private QosVector determineQosMinServiceCandidate(
			List<ServiceCandidate> serviceCandidates) {
		QosVector min = new QosVector(100000.0, 100000.0, 1.0);
		for (ServiceCandidate serviceCandidate : serviceCandidates) {
			QosVector qos = serviceCandidate.getQosVector();
			if (qos.getCosts() < min.getCosts()) {
				min.setCosts(qos.getCosts());
			}
			if (qos.getResponseTime() < min.getResponseTime()) {
				min.setResponseTime(qos.getResponseTime());
			}
			if (qos.getAvailability() < min.getAvailability()) {
				min.setAvailability(qos.getAvailability());
			}
		}
		return min;
	}
	
	// Determine the maximum value for each QoS attribute that can 
	// be obtained by always selecting the maximum values of the 
	// service candidates of each service class.
	// Note that "maximum" really means "maximum" and not "best".
	private QosVector determineQosMaxComposition(
			List<ServiceClass> serviceClasses) {
		double costs = 0.0;
		double responseTime = 0.0;
		double availability = 1.0;
		QosVector maxServiceCandidate;
		for (ServiceClass serviceClass : serviceClasses) {
			maxServiceCandidate = determineQosMaxServiceCandidate(
					serviceClass.getServiceCandidateList());
			costs += maxServiceCandidate.getCosts();
			responseTime += maxServiceCandidate.getResponseTime();
			availability *= maxServiceCandidate.getAvailability();
		}
		return new QosVector(costs, responseTime, availability);
	}
	
	// Determine the minimum value for each QoS attribute that can 
	// be obtained by always selecting the minimum values of the 
	// service candidates of each service class.
	// Note that "minimum" really means "minimum" and not "worst".
	private QosVector determineQosMinComposition(
			List<ServiceClass> serviceClasses) {
		double costs = 0.0;
		double responseTime = 0.0;
		double availability = 1.0;
		QosVector minServiceCandidate;
		for (ServiceClass serviceClass : serviceClasses) {
			minServiceCandidate = determineQosMinServiceCandidate(
					serviceClass.getServiceCandidateList());
			costs += minServiceCandidate.getCosts();
			responseTime += minServiceCandidate.getResponseTime();
			availability *= minServiceCandidate.getAvailability();
		}
		return new QosVector(costs, responseTime, availability);
	}
	
	private void checkOverwrite(File file, String customDescription) {
		if (!file.getName().endsWith(".csv")) {
			if (file.getName().contains(".")) {
				file = new File(file.getPath().substring(
						0, file.getPath().lastIndexOf(".")) + ".csv");
			}
			else {
				file = new File(file.getPath() + ".csv");
			}
		}
		if (file.exists()) {
			if (JOptionPane.showConfirmDialog(
					null, "<html>File already exists.<br>Overwrite?</html>", 
					"Warning", JOptionPane.YES_NO_OPTION) != 
						JOptionPane.YES_OPTION) {
				JOptionPane.showMessageDialog(
						null, "<html>" + customDescription + 
						" <b color=red>not</b> " + "been saved!</html>", 
						"Note", JOptionPane.WARNING_MESSAGE);
				writeErrorLogEntry("Save " + customDescription.substring(
						0, customDescription.lastIndexOf(" ")) + 
						" not completed");
				return;
			}
		}
	}
}