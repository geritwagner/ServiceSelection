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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	
	private JCheckBox jCheckBoxMaxPrice;
	private JCheckBox jCheckBoxMaxCosts;
	private JCheckBox jCheckBoxMaxResponseTime;
	private JCheckBox jCheckBoxMinAvailability;
	private JCheckBox jCheckBoxMinReliability;
	
	private JTextField jTextFieldMaxPrice;
	private JTextField jTextFieldMaxCosts;
	private JTextField jTextFieldMaxResponseTime;
	private JTextField jTextFieldMinAvailability;
	private JTextField jTextFieldMinReliability;
	
	private JSpinner jSpinnerNumberResultTiers;
	
	private JSlider jSliderMaxPrice;
	private JSlider jSliderMaxCosts;
	private JSlider jSliderMaxResponseTime;
	private JSlider jSliderMinAvailability;
	private JSlider jSliderMinReliability;
	
	private JTable jTableServiceClasses;
	private JTable jTableWebServices;
	
	private JLabel jLabelNumerator;
	private JLabel jLabelDenominator;
	private JLabel jLabelWeightedPenalty;
	
	private JCheckBox jCheckboxGeneticAlgorithm;
	private JCheckBox jCheckBoxAntColonyOptimization;
	private JCheckBox jCheckBoxAnalyticAlgorithm;
	
	private JTable jTableGeneticAlgorithm;
	private JTable jTableAntAlgorithm;
	private JTable jTableAnalyticAlgorithm;
	
	private JProgressBar jProgressBarGeneticAlgorithm;
	private JProgressBar jProgressBarAntAlgorithm;
	private JProgressBar jProgressBarAnalyticAlgorithm;
	
	private JTable jTableGeneralResults;
	private JTable jTableTier;

	private JTabbedPane jTabbedPane;
	private JScrollPane jScrollPaneGeneticAlgorithm;
	
	private JButton jButtonStart;
	private JButton jButtonVisualize;
	
	private static MainFrame frame;
	
	
	public static final int MAX_PRICE = 10000;
	public static final int MAX_COSTS = 10000;
	public static final int MAX_RESPONSE_TIME = 10000;
	public static final int MAX_AVAILABILITY = 100;
	public static final int MAX_RELIABILITY = 100;
	public static final int MAX_PENALTY_FACTOR = 100;
	
	
	private List<ServiceClass> serviceClassesList = 
			new LinkedList<ServiceClass>();
	private List<ServiceCandidate> serviceCandidatesList = 
    		new LinkedList<ServiceCandidate>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {

		// FRAME SETTINGS
		setTitle("test_gui");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 850);

		// MAIN CONTENT PANEL CONFIGURATION
		contentPane = new JPanel();
		contentPane.setBorder(new LineBorder(new Color(0, 0, 0)));
		setContentPane(contentPane);
		GridBagLayout gblContentPane = new GridBagLayout();
		gblContentPane.columnWeights = new double[]{0.4, 0.4, 0.2};
		gblContentPane.rowWeights = new double[]{
				0.025, 0.225, 0.05, 0.25, 0.01, 0.025, 0.415};
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
		
		jButtonVisualize = new JButton("Visualize");
		jButtonVisualize.setEnabled(false);
		GridBagConstraints gbcJButtonVisualize = new GridBagConstraints();
		gbcJButtonVisualize.insets = new Insets(0, 0, 5, 0);
		gbcJButtonVisualize.gridx = 2;
		gbcJButtonVisualize.gridy = 5;
		contentPane.add(jButtonVisualize, gbcJButtonVisualize);
		
		initializeMenuBar();
		initializeGeneralSettingsPanel(contentPane);
		initializeCenterArea(contentPane);
		initializeGeneticAlgorithmPanel(contentPane);
		initializeAntAlgorithmPanel(contentPane);
		initializeAnalyticAlgorithmPanel(contentPane);
		initializeTabbedResultsPanel(contentPane);
		initializeGeneralResultsPanel(contentPane);
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
		JMenuItem jMenuItemLoad = new JMenuItem("Load");
		
		final JFileChooser fileChooser = new JFileChooser() {
	        {
	            setFileFilter(new FileFilter() {
	                @Override
	                public boolean accept(File f) {
	                    return f.getName().toLowerCase().endsWith("csv") || 
	                    		f.isDirectory();
	                }
	                @Override
	                public String getDescription() {
	                    return "CSV Datei (Comma Seperated Values)";
	                }
	            });
	        }
	    };
	    
	    jMenuItemLoad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!(fileChooser.showOpenDialog(MainFrame.this) == 
						JFileChooser.APPROVE_OPTION)) {
                    return;
				}
                final File file = fileChooser.getSelectedFile();
                if (file == null) {
                    return;
                }
                loadWebServices(file);
                jButtonStart.setEnabled(true);
			}
		});
		
		jMenuFile.add(jMenuItemLoad);
		JMenuItem jMenuItemSave = new JMenuItem("Save");
		jMenuFile.add(jMenuItemSave);
		JMenuItem jMenuItemExit = new JMenuItem("Exit");
		jMenuItemExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		jMenuFile.add(jMenuItemExit);

		JMenu jMenuEdit = new JMenu("Constraints");
		jMenuBar.add(jMenuEdit);
		JMenuItem jMenuItemLoadDefaultConstraints = 
				new JMenuItem("Use Default Values");
		jMenuItemLoadDefaultConstraints.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setDefaultConstraints();
					}
				});
		jMenuEdit.add(jMenuItemLoadDefaultConstraints);
		JMenuItem jMenuItemLoadRandomConstraints = 
				new JMenuItem("Use Random Values");
		jMenuItemLoadRandomConstraints.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						setRandomConstraints();
					}
				});
		jMenuEdit.add(jMenuItemLoadRandomConstraints);
		JMenuItem jMenuItemLoadConstraints = new JMenuItem("Load Constraints");
		jMenuEdit.add(jMenuItemLoadConstraints);
		JMenuItem jMenuItemSaveConstraints = new JMenuItem("Save Constraints");
		jMenuEdit.add(jMenuItemSaveConstraints);

		JMenu jMenuOther = new JMenu("?");
		jMenuBar.add(jMenuOther);
		JMenuItem jMenuItemHelp = new JMenuItem("Help");
		jMenuOther.add(jMenuItemHelp);
		JMenuItem jMenuItemSupport = new JMenuItem("Support");
		jMenuOther.add(jMenuItemSupport);
		JMenuItem jMenuItemAbout = new JMenuItem("About");
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
				0.15, 0.6, 0.1, 0.05};
		gblJPanelQosConstraints.rowWeights = new double[]{
				0.125, 0.125, 0.125, 0.125, 0.125, 0.125, 0.125};
		jPanelQosConstraints.setLayout(gblJPanelQosConstraints);
		
		JLabel jLabelQosConstraints = new JLabel("QoS Constraints:");
		GridBagConstraints gbcJLabelQosConstraints = new GridBagConstraints();
		gbcJLabelQosConstraints.gridwidth = 4;
		gbcJLabelQosConstraints.insets = new Insets(0, 0, 5, 0);
		gbcJLabelQosConstraints.gridx = 0;
		gbcJLabelQosConstraints.gridy = 0;
		jPanelQosConstraints.add(jLabelQosConstraints, gbcJLabelQosConstraints);
		
		JLabel jLabelResultTiers = new JLabel("Number of Result Tiers");
		GridBagConstraints gbcJLabelResultTiers = new GridBagConstraints();
		gbcJLabelResultTiers.insets = new Insets(0, 18, 5, 5);
		gbcJLabelResultTiers.gridx = 0;
		gbcJLabelResultTiers.gridy = 1;
		gbcJLabelResultTiers.anchor = GridBagConstraints.WEST;
		jPanelQosConstraints.add(jLabelResultTiers, gbcJLabelResultTiers);
		
		jSpinnerNumberResultTiers = 
			new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
		jSpinnerNumberResultTiers.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
			}
		});
		jSpinnerNumberResultTiers.setPreferredSize(new Dimension(35, 25));
		GridBagConstraints gbcJSpinnerNumberResultTiers = 
				new GridBagConstraints();
		gbcJSpinnerNumberResultTiers.insets = new Insets(0, 0, 5, 5);
		gbcJSpinnerNumberResultTiers.gridx = 2;
		gbcJSpinnerNumberResultTiers.gridy = 1;
		jPanelQosConstraints.add(
				jSpinnerNumberResultTiers, gbcJSpinnerNumberResultTiers);
		
		jCheckBoxMaxPrice = new JCheckBox("Max. Price");
		jCheckBoxMaxPrice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				buildGeneticAlgorithmWeightsTable();
			}
		});
		jCheckBoxMaxPrice.setSelected(true);
		GridBagConstraints gbcJCheckBoxMaxPrice = new GridBagConstraints();
		gbcJCheckBoxMaxPrice.insets = new Insets(0, 0, 5, 5);
		gbcJCheckBoxMaxPrice.gridx = 0;
		gbcJCheckBoxMaxPrice.gridy = 2;
		gbcJCheckBoxMaxPrice.anchor = GridBagConstraints.WEST;
		jPanelQosConstraints.add(jCheckBoxMaxPrice, gbcJCheckBoxMaxPrice);
		
		jSliderMaxPrice = new JSlider();
		GridBagConstraints gbcJSliderMaxPrice = new GridBagConstraints();
		gbcJSliderMaxPrice.insets = new Insets(0, 0, 5, 5);
		gbcJSliderMaxPrice.gridx = 1;
		gbcJSliderMaxPrice.gridy = 2;
		gbcJSliderMaxPrice.fill = GridBagConstraints.BOTH;
		jPanelQosConstraints.add(jSliderMaxPrice, gbcJSliderMaxPrice);
		jSliderMaxPrice.setMaximum(MAX_PRICE);
		jSliderMaxPrice.setMinimum(0);
		jSliderMaxPrice.setValue(MAX_PRICE / 2);
		jSliderMaxPrice.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				useConstraintSlider(jTextFieldMaxPrice, jSliderMaxPrice);
			}
		});
		
		jTextFieldMaxPrice = 
				new JTextField(String.valueOf(jSliderMaxPrice.getValue()));
		GridBagConstraints gbcJTextFieldMaxPrice = new GridBagConstraints();
		gbcJTextFieldMaxPrice.insets = new Insets(0, 0, 5, 5);
		gbcJTextFieldMaxPrice.fill = GridBagConstraints.HORIZONTAL;
		gbcJTextFieldMaxPrice.gridx = 2;
		gbcJTextFieldMaxPrice.gridy = 2;
		jPanelQosConstraints.add(jTextFieldMaxPrice, gbcJTextFieldMaxPrice);
		
		JLabel jLabelMaxPrice = new JLabel("\u20AC");
		GridBagConstraints gbcJLabelMaxPrice = new GridBagConstraints();
		gbcJLabelMaxPrice.insets = new Insets(0, 0, 5, 0);
		gbcJLabelMaxPrice.gridx = 3;
		gbcJLabelMaxPrice.gridy = 2;
		jPanelQosConstraints.add(jLabelMaxPrice, gbcJLabelMaxPrice);
		
		jCheckBoxMaxCosts = new JCheckBox("Max. Costs");
		jCheckBoxMaxCosts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buildGeneticAlgorithmWeightsTable();
			}
		});
		jCheckBoxMaxCosts.setSelected(true);
		GridBagConstraints gbcJCheckBoxMaxCosts = new GridBagConstraints();
		gbcJCheckBoxMaxCosts.insets = new Insets(0, 0, 5, 5);
		gbcJCheckBoxMaxCosts.gridx = 0;
		gbcJCheckBoxMaxCosts.gridy = 3;
		gbcJCheckBoxMaxCosts.anchor = GridBagConstraints.WEST;
		jPanelQosConstraints.add(jCheckBoxMaxCosts, gbcJCheckBoxMaxCosts);
		
		jSliderMaxCosts = new JSlider();
		jSliderMaxCosts.setMaximum(MAX_COSTS);
		jSliderMaxCosts.setMinimum(0);
		jSliderMaxCosts.setValue(MAX_COSTS / 2);
		jSliderMaxCosts.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				useConstraintSlider(jTextFieldMaxCosts, jSliderMaxCosts);
			}
		});
		GridBagConstraints gbcJSliderMaxCosts = new GridBagConstraints();
		gbcJSliderMaxCosts.insets = new Insets(0, 0, 5, 5);
		gbcJSliderMaxCosts.gridx = 1;
		gbcJSliderMaxCosts.gridy = 3;
		gbcJSliderMaxCosts.fill = GridBagConstraints.BOTH;
		jPanelQosConstraints.add(jSliderMaxCosts, gbcJSliderMaxCosts);
		
		jTextFieldMaxCosts = 
				new JTextField(String.valueOf(jSliderMaxCosts.getValue()));
		GridBagConstraints gbcJTextFieldMaxCosts = new GridBagConstraints();
		gbcJTextFieldMaxCosts.insets = new Insets(0, 0, 5, 5);
		gbcJTextFieldMaxCosts.fill = GridBagConstraints.HORIZONTAL;
		gbcJTextFieldMaxCosts.gridx = 2;
		gbcJTextFieldMaxCosts.gridy = 3;
		jPanelQosConstraints.add(jTextFieldMaxCosts, gbcJTextFieldMaxCosts);
		
		JLabel jLabelMaxCosts = new JLabel("\u20AC");
		GridBagConstraints gbcJLabelMaxCosts = new GridBagConstraints();
		gbcJLabelMaxCosts.insets = new Insets(0, 0, 5, 0);
		gbcJLabelMaxCosts.gridx = 3;
		gbcJLabelMaxCosts.gridy = 3;
		jPanelQosConstraints.add(jLabelMaxCosts, gbcJLabelMaxCosts);
		
		jCheckBoxMaxResponseTime = new JCheckBox("Max. Response Time");
		jCheckBoxMaxResponseTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				buildGeneticAlgorithmWeightsTable();
			}
		});
		jCheckBoxMaxResponseTime.setSelected(true);
		GridBagConstraints gbcJCheckBoxMaxResponseTime = 
			new GridBagConstraints();
		gbcJCheckBoxMaxResponseTime.insets = new Insets(0, 0, 5, 5);
		gbcJCheckBoxMaxResponseTime.gridx = 0;
		gbcJCheckBoxMaxResponseTime.gridy = 4;
		gbcJCheckBoxMaxResponseTime.anchor = GridBagConstraints.WEST;
		jPanelQosConstraints.add(
				jCheckBoxMaxResponseTime, gbcJCheckBoxMaxResponseTime);
		
		jSliderMaxResponseTime = new JSlider();
		jSliderMaxResponseTime.setMaximum(MAX_RESPONSE_TIME);
		jSliderMaxResponseTime.setMinimum(0);
		jSliderMaxResponseTime.setValue(MAX_RESPONSE_TIME / 2);
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
		gbcJSliderMaxResponseTime.gridy = 4;
		gbcJSliderMaxResponseTime.fill = GridBagConstraints.BOTH;
		jPanelQosConstraints.add(
				jSliderMaxResponseTime, gbcJSliderMaxResponseTime);
		
		jTextFieldMaxResponseTime = new JTextField(
				String.valueOf(jSliderMaxResponseTime.getValue()));
		GridBagConstraints gbcJTextFieldMaxResponseTime = 
			new GridBagConstraints();
		gbcJTextFieldMaxResponseTime.insets = new Insets(0, 0, 5, 5);
		gbcJTextFieldMaxResponseTime.fill = GridBagConstraints.HORIZONTAL;
		gbcJTextFieldMaxResponseTime.gridx = 2;
		gbcJTextFieldMaxResponseTime.gridy = 4;
		jPanelQosConstraints.add(
				jTextFieldMaxResponseTime, gbcJTextFieldMaxResponseTime);
		
		JLabel jLabelMaxResponseTime = new JLabel("ms");
		GridBagConstraints gbcJLabelMaxResponseTime = 
			new GridBagConstraints();
		gbcJLabelMaxResponseTime.insets = new Insets(0, 0, 5, 0);
		gbcJLabelMaxResponseTime.gridx = 3;
		gbcJLabelMaxResponseTime.gridy = 4;
		jPanelQosConstraints.add(
				jLabelMaxResponseTime, gbcJLabelMaxResponseTime);
		
		jCheckBoxMinAvailability = new JCheckBox("Min. Availability");
		jCheckBoxMinAvailability.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				buildGeneticAlgorithmWeightsTable();
			}
		});
		jCheckBoxMinAvailability.setSelected(true);
		GridBagConstraints gbcJCheckBoxMinAvailability = 
			new GridBagConstraints();
		gbcJCheckBoxMinAvailability.insets = new Insets(0, 0, 5, 5);
		gbcJCheckBoxMinAvailability.gridx = 0;
		gbcJCheckBoxMinAvailability.gridy = 5;
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
		gbcJSliderMinAvailability.gridy = 5;
		gbcJSliderMinAvailability.fill = GridBagConstraints.BOTH;
		jPanelQosConstraints.add(
				jSliderMinAvailability, gbcJSliderMinAvailability);
		
		jTextFieldMinAvailability = new JTextField(
				String.valueOf(jSliderMinAvailability.getValue()));
		GridBagConstraints gbcJTextFieldMinAvailability = 
			new GridBagConstraints();
		gbcJTextFieldMinAvailability.insets = new Insets(0, 0, 5, 5);
		gbcJTextFieldMinAvailability.fill = GridBagConstraints.HORIZONTAL;
		gbcJTextFieldMinAvailability.gridx = 2;
		gbcJTextFieldMinAvailability.gridy = 5;
		jPanelQosConstraints.add(
				jTextFieldMinAvailability, gbcJTextFieldMinAvailability);
		
		JLabel jLabelMinAvailability = new JLabel("%");
		GridBagConstraints gbcJLabelMinAvailability = new GridBagConstraints();
		gbcJLabelMinAvailability.insets = new Insets(0, 0, 5, 0);
		gbcJLabelMinAvailability.gridx = 3;
		gbcJLabelMinAvailability.gridy = 5;
		jPanelQosConstraints.add(
				jLabelMinAvailability, gbcJLabelMinAvailability);
		
		jCheckBoxMinReliability = new JCheckBox("Min. Reliability");
		jCheckBoxMinReliability.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buildGeneticAlgorithmWeightsTable();
			}
		});
		jCheckBoxMinReliability.setSelected(true);
		GridBagConstraints gbcJCheckBoxMinReliability = 
				new GridBagConstraints();
		gbcJCheckBoxMinReliability.insets = new Insets(0, 0, 0, 5);
		gbcJCheckBoxMinReliability.gridx = 0;
		gbcJCheckBoxMinReliability.gridy = 6;
		gbcJCheckBoxMinReliability.anchor = GridBagConstraints.WEST;
		jPanelQosConstraints.add(
				jCheckBoxMinReliability, gbcJCheckBoxMinReliability);
		
		jSliderMinReliability = new JSlider();
		jSliderMinReliability.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				useConstraintSlider(
						jTextFieldMinReliability, jSliderMinReliability);
			}
		});
		GridBagConstraints gbcJSliderMinReliability = new GridBagConstraints();
		gbcJSliderMinReliability.insets = new Insets(0, 0, 0, 5);
		gbcJSliderMinReliability.gridx = 1;
		gbcJSliderMinReliability.gridy = 6;
		gbcJSliderMinReliability.fill = GridBagConstraints.BOTH;
		jPanelQosConstraints.add(
				jSliderMinReliability, gbcJSliderMinReliability);
		
		jTextFieldMinReliability = new JTextField(
				String.valueOf(jSliderMinReliability.getValue()));
		GridBagConstraints gbcJTextFieldMinReliability = 
			new GridBagConstraints();
		gbcJTextFieldMinReliability.insets = new Insets(0, 0, 0, 5);
		gbcJTextFieldMinReliability.fill = GridBagConstraints.HORIZONTAL;
		gbcJTextFieldMinReliability.gridx = 2;
		gbcJTextFieldMinReliability.gridy = 6;
		jPanelQosConstraints.add(
				jTextFieldMinReliability, gbcJTextFieldMinReliability);
		
		JLabel jLabelMinReliability = new JLabel("%");
		GridBagConstraints gbcJLabelMinReliability = new GridBagConstraints();
		gbcJLabelMinReliability.gridx = 3;
		gbcJLabelMinReliability.gridy = 6;
		jPanelQosConstraints.add(
				jLabelMinReliability, gbcJLabelMinReliability);
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

		jTableServiceClasses = new JTable();
		jTableServiceClasses.setModel(new BasicTableModel(0, 3, true));
		jTableServiceClasses.getColumnModel().getColumn(0).setHeaderValue(
				"Selection");
		jTableServiceClasses.getColumnModel().getColumn(1).setHeaderValue(
				"ID");
		jTableServiceClasses.getColumnModel().getColumn(2).setHeaderValue(
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
		
		jTableWebServices = new JTable();
		jTableWebServices.setModel(new BasicTableModel(0, 10, true));
		jTableWebServices.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jTableWebServices.getColumnModel().getColumn(0).setHeaderValue(
				"Selection");
		jTableWebServices.getColumnModel().getColumn(1).setHeaderValue(
				"ID");
		jTableWebServices.getColumnModel().getColumn(2).setHeaderValue(
				"Name");
		jTableWebServices.getColumnModel().getColumn(3).setHeaderValue(
				"Provider");
		jTableWebServices.getColumnModel().getColumn(4).setHeaderValue(
				"Price");
		jTableWebServices.getColumnModel().getColumn(5).setHeaderValue(
				"Costs");
		jTableWebServices.getColumnModel().getColumn(6).setHeaderValue(
				"Response Time");
		jTableWebServices.getColumnModel().getColumn(7).setHeaderValue(
				"Availability");
		jTableWebServices.getColumnModel().getColumn(8).setHeaderValue(
				"Reliability");
		jTableWebServices.getColumnModel().getColumn(9).setHeaderValue(
				"Utility");
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
		
		Font fontFormula = new Font("formula", Font.ITALIC, 14);
		jLabelNumerator = new JLabel("Numerator");
		jLabelNumerator.setFont(fontFormula);
		GridBagConstraints gbcJLabelNumerator = new GridBagConstraints();
		gbcJLabelNumerator.insets = new Insets(0, 0, 5, 5);
		gbcJLabelNumerator.gridx = 1;
		gbcJLabelNumerator.gridy = 0;
		jPanelUtilityFunction.add(jLabelNumerator, gbcJLabelNumerator);
		
		JSeparator jSeparatorFormula = new JSeparator();
		GridBagConstraints gbcJSeparatorFormula = new GridBagConstraints();
		gbcJSeparatorFormula.insets = new Insets(2, 0, 5, 5);
		gbcJSeparatorFormula.fill = GridBagConstraints.HORIZONTAL;
		gbcJSeparatorFormula.gridx = 1;
		gbcJSeparatorFormula.gridy = 1;
		jPanelUtilityFunction.add(jSeparatorFormula, gbcJSeparatorFormula);
		
		jLabelDenominator = new JLabel("Denominator");
		jLabelDenominator.setFont(fontFormula);
		GridBagConstraints gbcJLabelDenominator = new GridBagConstraints();
		gbcJLabelDenominator.insets = new Insets(0, 0, 0, 5);
		gbcJLabelDenominator.gridx = 1;
		gbcJLabelDenominator.gridy = 2;
		jPanelUtilityFunction.add(jLabelDenominator, gbcJLabelDenominator);
		
		jLabelWeightedPenalty = new JLabel("+ Weighted Penalty Factor");
		jLabelWeightedPenalty.setFont(fontFormula);
		GridBagConstraints gbc_lblWeightedPenalty = new GridBagConstraints();
		gbc_lblWeightedPenalty.gridheight = 3;
		gbc_lblWeightedPenalty.anchor = GridBagConstraints.WEST;
		gbc_lblWeightedPenalty.insets = new Insets(5, 3, 5, 5);
		gbc_lblWeightedPenalty.gridx = 2;
		gbc_lblWeightedPenalty.gridy = 0;
		jPanelUtilityFunction.add(jLabelWeightedPenalty, gbc_lblWeightedPenalty);
		
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
//		jPanelGeneticAlgorithm.setBorder(
//				new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbcGeneticAlgorithm = new GridBagConstraints();
		gbcGeneticAlgorithm.gridheight = 1;
		gbcGeneticAlgorithm.insets = new Insets(0, 5, 5, 5);
		gbcGeneticAlgorithm.fill = GridBagConstraints.BOTH;
		gbcGeneticAlgorithm.gridx = 0;
		gbcGeneticAlgorithm.gridy = 3;
		contentPane.add(jPanelGeneticAlgorithm, gbcGeneticAlgorithm);
		GridBagLayout gblJPanelGeneticAlgorithm = new GridBagLayout();
		gblJPanelGeneticAlgorithm.columnWeights = new double[]{1.0};
		gblJPanelGeneticAlgorithm.rowWeights = new double[]{0.0, 1.0, 0.0};
		jPanelGeneticAlgorithm.setLayout(gblJPanelGeneticAlgorithm);
		
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
		
		jScrollPaneGeneticAlgorithm = new JScrollPane();
		jScrollPaneGeneticAlgorithm.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbcJScrollPaneGeneticAlgorithm = 
				new GridBagConstraints();
		gbcJScrollPaneGeneticAlgorithm.insets = new Insets(0, 0, 5, 0);
		gbcJScrollPaneGeneticAlgorithm.fill = GridBagConstraints.BOTH;
		gbcJScrollPaneGeneticAlgorithm.gridx = 0;
		gbcJScrollPaneGeneticAlgorithm.gridy = 1;
		jPanelGeneticAlgorithm.add(
				jScrollPaneGeneticAlgorithm, gbcJScrollPaneGeneticAlgorithm);
		
		buildGeneticAlgorithmWeightsTable();
		jProgressBarGeneticAlgorithm = new JProgressBar();
		jProgressBarGeneticAlgorithm.setStringPainted(true);
		GridBagConstraints gbcJProgressBarGeneticAlgorithm = 
				new GridBagConstraints();
		gbcJProgressBarGeneticAlgorithm.fill = GridBagConstraints.HORIZONTAL;
		gbcJProgressBarGeneticAlgorithm.insets = new Insets(0, 0, 5, 0);
		gbcJProgressBarGeneticAlgorithm.gridx = 0;
		gbcJProgressBarGeneticAlgorithm.gridy = 2;
		jPanelGeneticAlgorithm.add(
				jProgressBarGeneticAlgorithm, gbcJProgressBarGeneticAlgorithm);
	}
	
	private void initializeAntAlgorithmPanel(JPanel contentPane) {
		JPanel jPanelAntAlgorithm = new JPanel();
//		jPanelAntAlgorithm.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbcPanelAntAlgorithm = new GridBagConstraints();
		gbcPanelAntAlgorithm.gridheight = 1;
		gbcPanelAntAlgorithm.insets = new Insets(0, 0, 5, 5);
		gbcPanelAntAlgorithm.fill = GridBagConstraints.BOTH;
		gbcPanelAntAlgorithm.gridx = 1;
		gbcPanelAntAlgorithm.gridy = 3;
		contentPane.add(jPanelAntAlgorithm, gbcPanelAntAlgorithm);
		GridBagLayout gblJPanelAntAlgorithm = new GridBagLayout();
		gblJPanelAntAlgorithm.columnWeights = new double[]{1.0};
		gblJPanelAntAlgorithm.rowWeights = new double[]{0.0, 1.0, 0.0};
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
		jScrollPaneAntAlgorithm.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbcJScrollPaneAntAlgorithm = 
				new GridBagConstraints();
		gbcJScrollPaneAntAlgorithm.insets = new Insets(0, 0, 5, 0);
		gbcJScrollPaneAntAlgorithm.fill = GridBagConstraints.BOTH;
		gbcJScrollPaneAntAlgorithm.gridx = 0;
		gbcJScrollPaneAntAlgorithm.gridy = 1;
		jPanelAntAlgorithm.add(
				jScrollPaneAntAlgorithm, gbcJScrollPaneAntAlgorithm);
		
		jTableAntAlgorithm = new JTable();
		jScrollPaneAntAlgorithm.setViewportView(jTableAntAlgorithm);
		
		jProgressBarAntAlgorithm = new JProgressBar();
		jProgressBarAntAlgorithm.setStringPainted(true);
		GridBagConstraints gbcJProgressBarAntAlgorithm = 
				new GridBagConstraints();
		gbcJProgressBarAntAlgorithm.fill = GridBagConstraints.HORIZONTAL;
		gbcJProgressBarAntAlgorithm.insets = new Insets(0, 0, 5, 0);
		gbcJProgressBarAntAlgorithm.gridx = 0;
		gbcJProgressBarAntAlgorithm.gridy = 2;
		jPanelAntAlgorithm.add(
				jProgressBarAntAlgorithm, gbcJProgressBarAntAlgorithm);
	}
	
	private void initializeAnalyticAlgorithmPanel(JPanel contentPane) {
		JPanel jPanelAnalyticAlgorithm = new JPanel();
//		jPanelAnalyticAlgorithm.setBorder(
//				new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbcJPanelAnalyticAlgorithm =
				new GridBagConstraints();
		gbcJPanelAnalyticAlgorithm.gridheight = 1;
		gbcJPanelAnalyticAlgorithm.insets = new Insets(0, 0, 5, 5);
		gbcJPanelAnalyticAlgorithm.fill = GridBagConstraints.BOTH;
		gbcJPanelAnalyticAlgorithm.gridx = 2;
		gbcJPanelAnalyticAlgorithm.gridy = 3;
		contentPane.add(jPanelAnalyticAlgorithm, gbcJPanelAnalyticAlgorithm);
		GridBagLayout gblJPanelAnalyticAlgorithm = new GridBagLayout();
		gblJPanelAnalyticAlgorithm.columnWeights = new double[]{1.0};
		gblJPanelAnalyticAlgorithm.rowWeights = new double[]{0.0, 1.0, 0.0};
		jPanelAnalyticAlgorithm.setLayout(gblJPanelAnalyticAlgorithm);
		
		jCheckBoxAnalyticAlgorithm = 
				new JCheckBox("Analytic Algorithm");
		jCheckBoxAnalyticAlgorithm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chooseAlgorithm("analyticAlg");
			}
		});
		jCheckBoxAnalyticAlgorithm.setSelected(true);
		GridBagConstraints gbcJCheckBoxAnalyticAlgorithm = 
			new GridBagConstraints();
		gbcJCheckBoxAnalyticAlgorithm.anchor = GridBagConstraints.NORTH;
		gbcJCheckBoxAnalyticAlgorithm.insets = new Insets(0, 0, 5, 0);
		gbcJCheckBoxAnalyticAlgorithm.gridx = 0;
		gbcJCheckBoxAnalyticAlgorithm.gridy = 0;
		jPanelAnalyticAlgorithm.add(
				jCheckBoxAnalyticAlgorithm, gbcJCheckBoxAnalyticAlgorithm);
		
		JScrollPane jScrollPaneAnalyticAlgorithm = new JScrollPane();
		GridBagConstraints gbcJScrollPaneAnalyticAlgorithm = 
			new GridBagConstraints();
		gbcJScrollPaneAnalyticAlgorithm.insets = new Insets(0, 0, 5, 0);
		gbcJScrollPaneAnalyticAlgorithm.fill = GridBagConstraints.BOTH;
		gbcJScrollPaneAnalyticAlgorithm.gridx = 0;
		gbcJScrollPaneAnalyticAlgorithm.gridy = 1;
		jPanelAnalyticAlgorithm.add(
				jScrollPaneAnalyticAlgorithm, gbcJScrollPaneAnalyticAlgorithm);
		
		jTableAnalyticAlgorithm = new JTable();
		// TODO: BETTER SOLUTION FOR THIS LISTENER...
		jTableAnalyticAlgorithm.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (jTableAnalyticAlgorithm.getSelectedRow() == 0 && 
						jTableAnalyticAlgorithm.getSelectedColumn() == 0) {
					if ((boolean) jTableAnalyticAlgorithm.getValueAt(0, 0)) {
						jTableAnalyticAlgorithm.setValueAt(false, 1, 0);
					}
					else {
						jTableAnalyticAlgorithm.setValueAt(true, 1, 0);
					}
				}
				else if (jTableAnalyticAlgorithm.getSelectedRow() == 1 && 
						jTableAnalyticAlgorithm.getSelectedColumn() == 0) {
					if ((boolean) jTableAnalyticAlgorithm.getValueAt(1, 0)) {
						jTableAnalyticAlgorithm.setValueAt(false, 0, 0);
					}
					else {
						jTableAnalyticAlgorithm.setValueAt(true, 0, 0);
					}
				}
			}
		});
		jTableAnalyticAlgorithm.setModel(new BasicTableModel(2, 2, true));
		jTableAnalyticAlgorithm.getColumnModel().getColumn(0).setHeaderValue(
				"Selection");
		jTableAnalyticAlgorithm.getColumnModel().getColumn(1).setHeaderValue(
				"Service Title");
		jTableAnalyticAlgorithm.setValueAt(true, 0, 0);
		jTableAnalyticAlgorithm.setValueAt(false, 1, 0);
		jTableAnalyticAlgorithm.setValueAt("Enumeration", 0, 1);
		jTableAnalyticAlgorithm.setValueAt("Branch and Bound", 1, 1);
		jScrollPaneAnalyticAlgorithm.setViewportView(jTableAnalyticAlgorithm);
		
		jProgressBarAnalyticAlgorithm = new JProgressBar();
		jProgressBarAnalyticAlgorithm.setStringPainted(true);
		GridBagConstraints gbcJProgressBarAnalyticAlgorithm = 
			new GridBagConstraints();
		gbcJProgressBarAnalyticAlgorithm.fill = GridBagConstraints.HORIZONTAL;
		gbcJProgressBarAnalyticAlgorithm.insets = new Insets(0, 0, 5, 0);
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
		
		String[][] generalResultsData = {
				{"Runtime:", "x ms"},
				{"Runtime (Gen Alg):", "x1 ms"},
				{"Runtime (Ant Alg):", "x2 ms"},
				{"Runtime (Analyt Alg):", "x3 ms"},
				{"#Services Tier 1:", "y1"},
				{"#Services Tier 2:", "y2"},
				{"#Services Tier 3:", "y3"},
		};
		String[] generalResultsColumnNames = {"Variable", "Value"};
		
		JScrollPane jScrollPaneResults = new JScrollPane();
		jScrollPaneResults.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbcJScrollPaneResults = new GridBagConstraints();
		gbcJScrollPaneResults.insets = new Insets(0, 0, 0, 5);
		gbcJScrollPaneResults.fill = GridBagConstraints.BOTH;
		gbcJScrollPaneResults.gridx = 0;
		gbcJScrollPaneResults.gridy = 0;
		jPanelGeneralResults.add(jScrollPaneResults, gbcJScrollPaneResults);
		jTableGeneralResults = new JTable(
				generalResultsData, generalResultsColumnNames);
		setColumnWidthRelative(jTableGeneralResults, new double[] {0.8, 0.2});
		setColumnTextAlignment(jTableGeneralResults, 1, 
				DefaultTableCellRenderer.RIGHT);
		jTableGeneralResults.setEnabled(false);
		jScrollPaneResults.setViewportView(jTableGeneralResults);
	}
	
	
	private void loadWebServices(File file) {
		// Delete previously loaded web services.
		serviceCandidatesList.removeAll(serviceCandidatesList);
		serviceClassesList.removeAll(serviceClassesList);
		
		BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            
            // Load service candidates headers.
            String[] headerArray = bufferedReader.readLine().split(";");
            
            // Load web services data.
            String[] serviceCandidateArray;
            while (bufferedReader.ready()) {
                serviceCandidateArray = bufferedReader.readLine().split(";");
                int serviceClassId = Integer.parseInt(
                		serviceCandidateArray[0]);
                String serviceClassName = serviceCandidateArray[1];
                int serviceCandidateId = Integer.parseInt(
                		serviceCandidateArray[2]);
                String name = serviceCandidateArray[3];
                String provider = serviceCandidateArray[4];
                double price = Double.parseDouble(serviceCandidateArray[5]);
                double costs = Double.parseDouble(serviceCandidateArray[6]);
                double responseTime = Double.parseDouble(
                		serviceCandidateArray[7]);
                double availability = Double.parseDouble(
                		serviceCandidateArray[8]);
                double reliability = Double.parseDouble(
                		serviceCandidateArray[9]);
                
                // Create and save service candidates.
                QosVector qosVector = new QosVector(price, costs, 
                		responseTime, availability, reliability);
                ServiceCandidate serviceCandidate = new ServiceCandidate(
                		serviceClassId, serviceClassName, serviceCandidateId, 
                		name, provider, qosVector);
                serviceCandidatesList.add(serviceCandidate);
                
                // Create and save service classes. Assign service candidates 
                // to service classes.
                boolean serviceClassAlreadyCreated = false;
                for (ServiceClass serviceClass : serviceClassesList) {
                	if (serviceClass.getServiceClassId() == serviceClassId) {
                		serviceClassAlreadyCreated = true;
                		serviceClass.getServiceCandidateList().add(serviceCandidate);
                		break;
                	}
                }
                if (! serviceClassAlreadyCreated) {
                	ServiceClass serviceClass = new ServiceClass(serviceClassId, 
                			serviceClassName, new LinkedList<ServiceCandidate>());
                	serviceClassesList.add(serviceClass);
                	serviceClass.getServiceCandidateList().add(serviceCandidate);
                }
            }
            
            // Write service classes headers.
            jTableServiceClasses.setModel(new BasicTableModel(
            		serviceClassesList.size(), 3, true));
            setColumnWidthRelative(jTableServiceClasses, 
            		new double[] {0.3, 0.1, 0.6});
            TableColumnModel serviceClassesColumnModel = 
            		jTableServiceClasses.getColumnModel();
            serviceClassesColumnModel.getColumn(0).setHeaderValue("Selection");
            serviceClassesColumnModel.getColumn(1).setHeaderValue("ID");
            serviceClassesColumnModel.getColumn(2).setHeaderValue("Name");
            setColumnTextAlignment(
            		jTableServiceClasses, 1, DefaultTableCellRenderer.CENTER);
            
            // Write service classes data.
            for (int k = 0 ; k < serviceClassesList.size() ; k++) {
            	ServiceClass serviceClass = serviceClassesList.get(k);
            	jTableServiceClasses.setValueAt(true, k, 0);
            	jTableServiceClasses.setValueAt(
            			serviceClass.getServiceClassId(), k, 1);
            	jTableServiceClasses.setValueAt(serviceClass.getName(), k, 2);
            }
            
            // Write service candidates headers (first line of input file!). 
            // Columns "serviceClassId" and "serviceClassName" will not be
            // shown here.
            jTableWebServices.setModel(new BasicTableModel(
            		serviceCandidatesList.size(), 10, true));
            TableColumnModel webServicesColumnModel = 
            		jTableWebServices.getColumnModel();
            webServicesColumnModel.getColumn(0).setHeaderValue("Selection");
            for (int k = 1 ; k < 10 ; k++) {
            	webServicesColumnModel.getColumn(k).setHeaderValue(
            			headerArray[k+1]);
            }
            setColumnTextAlignment(
    				jTableWebServices, 1, DefaultTableCellRenderer.CENTER);
    		for (int count = 4; count < 10; count++) {
    			setColumnTextAlignment(jTableWebServices, count, 
    					DefaultTableCellRenderer.RIGHT);
    		}
            // Write service candidates data.
            for (int k = 0 ; k < serviceCandidatesList.size() ; k++) {
            	ServiceCandidate serviceCandidate = 
            			serviceCandidatesList.get(k);
            	QosVector qosVector = serviceCandidate.getQosVector();
            	jTableWebServices.setValueAt(true, k, 0);
                jTableWebServices.setValueAt(
                		serviceCandidate.getServiceCandidateId(), k, 1);
                jTableWebServices.setValueAt(serviceCandidate.getName(), k, 2);
                jTableWebServices.setValueAt(
                		serviceCandidate.getProvider(), k, 3);
                jTableWebServices.setValueAt(qosVector.getPrice(), k, 4);
                jTableWebServices.setValueAt(qosVector.getCosts(), k, 5);
                jTableWebServices.setValueAt(
                		qosVector.getResponseTime(), k, 6);
                jTableWebServices.setValueAt
                (qosVector.getAvailability(), k, 7);
                jTableWebServices.setValueAt(qosVector.getReliability(), k, 8);
                jTableWebServices.setValueAt("Utility", k, 9);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	
	private void useConstraintSlider(JTextField textfield, JSlider slider) {
		textfield.setText(String.valueOf(slider.getValue()));
	}
	
	private void buildResultTable() {
		if (jSpinnerNumberResultTiers.getValue() instanceof Integer) {
			
			String chosenConstraintsCs = "Algorithm;# Composition;" +
					"# Service;Service Title;Service Class; Utility Value;";
			if (jCheckBoxMaxCosts.isSelected()) {
				chosenConstraintsCs += "Costs;";
			}
			if (jCheckBoxMaxPrice.isSelected()) {
				chosenConstraintsCs += "Price;";
			}
			if (jCheckBoxMaxResponseTime.isSelected()) {
				chosenConstraintsCs += "Response Time;";
			}
			if (jCheckBoxMinAvailability.isSelected()) {
				chosenConstraintsCs += "Availability;";
			}
			if (jCheckBoxMinReliability.isSelected()) {
				chosenConstraintsCs += "Reliability";
			}
			String[] tierTablesColumnNames = chosenConstraintsCs.split(";");

			if (this.jTabbedPane.getTabCount() > 0) {
				jTabbedPane.removeAll();
			}
			for (int count = 0; count < (int) jSpinnerNumberResultTiers.
					getValue(); count++) {
				JScrollPane jScrollPane = new JScrollPane();
				this.jTabbedPane.addTab("Tier " + String.valueOf(count + 1), 
						jScrollPane);
				jTableTier = new JTable(new BasicTableModel(
						20, tierTablesColumnNames.length, false));
				jTableTier.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				for (int innerCount = 0; innerCount < 
						tierTablesColumnNames.length; innerCount++) {
					jTableTier.getColumnModel().getColumn(
							innerCount).setHeaderValue(
									tierTablesColumnNames[innerCount]);
				}
				jTableTier.setEnabled(false);
				jScrollPane.setViewportView(jTableTier);
			}			
			
		}
	}
	
	// TODO: EXPAND LATER...
	private void pressStartButton() {
		buildResultTable();
		Map<String, Constraint> constraintsMap = getChosenConstraints();
		printChosenConstraintsToConsole(constraintsMap);
		doEnumeration(constraintsMap);
		jButtonVisualize.setEnabled(true);
	}
	
	private void chooseAlgorithm(String algorithm) {
		if (algorithm.equals("genAlg")) {
			if (!jCheckboxGeneticAlgorithm.isSelected()) {
				jTableGeneticAlgorithm.setEnabled(false);
			}
			else {
				jTableGeneticAlgorithm.setEnabled(true);
			}
		}
		else if (algorithm.equals("antAlg")) {
			
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
	
	private void buildGeneticAlgorithmWeightsTable() {
		jTableGeneticAlgorithm = new JTable();
		int weightCount = 1;
		String numerator = "";
		String denominator = "";
		String geneticAlgorithmWeights = "";
		if (jCheckBoxMaxPrice.isSelected()) {
			geneticAlgorithmWeights += "w" + weightCount + " (Price),15,%;";
			numerator = "w" + weightCount + " * MaxPrice";
			weightCount++;
		}
		if (jCheckBoxMaxCosts.isSelected()) {
			geneticAlgorithmWeights += "w" + weightCount + " (Costs),15,%;";
			if (numerator.equals("")) {
				numerator = "w" + weightCount + " * MaxCosts";
			}
			else {
				numerator += " + w" + weightCount + " * MaxCosts";
			}
			weightCount++;
		}
		if (jCheckBoxMaxResponseTime.isSelected()) {
			geneticAlgorithmWeights += 
					"w" + weightCount + " (Response Time),15,%;";
			if (numerator.equals("")) {
				numerator = "w" + weightCount + " * MaxResponseTime";
			}
			else {
				numerator += " + w" + weightCount + " * MaxResponseTime";
			}
			weightCount++;
		}
		if (jCheckBoxMinAvailability.isSelected()) {
			geneticAlgorithmWeights += 
					"w" + weightCount + " (Availability),15,%;";
			denominator = "w" + weightCount + " * MinAvailability ";
			weightCount++;
		}
		if (jCheckBoxMinReliability.isSelected()) {
			geneticAlgorithmWeights += 
					"w" + weightCount + " (Reliability),15,%;";
			if (denominator.equals("")) {
				denominator = "w" + weightCount + " * MinReliability";
			}
			else {
				denominator += " + w" + weightCount + " * MinReliability";
			}
			weightCount++;
		}
		if (numerator.equals("")) {
			numerator = "1";
		}
		if (denominator.equals("")) {
			denominator = "1";
		}
		jLabelNumerator.setText(numerator);
		jLabelDenominator.setText(denominator);
		geneticAlgorithmWeights += 
				"w" + weightCount + " (Penalty Factor),25,%;";
		jLabelWeightedPenalty.setText(
				"+ w" + weightCount + " * PenaltyFactor");
		jTableGeneticAlgorithm.setModel(
				new BasicTableModel(weightCount, 3, false));
		String[] geneticAlgorithmColumnNames = 
				geneticAlgorithmWeights.split(";");
		for (int count = 0; count < weightCount; count++) {
			for (int innerCount = 0; innerCount < 3; innerCount++) {
				jTableGeneticAlgorithm.setValueAt(
						geneticAlgorithmColumnNames[count].split(
								",")[innerCount], count, innerCount);
			}
		}
		jTableGeneticAlgorithm.getColumnModel().getColumn(0).setHeaderValue(
				"Weight");
		jTableGeneticAlgorithm.getColumnModel().getColumn(1).setHeaderValue(
				"Value");
		jTableGeneticAlgorithm.getColumnModel().getColumn(2).setHeaderValue(
				"Unit");
		setColumnTextAlignment(jTableGeneticAlgorithm, 0,  
				DefaultTableCellRenderer.LEFT);
		setColumnTextAlignment(jTableGeneticAlgorithm, 1,  
				DefaultTableCellRenderer.CENTER);
		setColumnTextAlignment(jTableGeneticAlgorithm, 2,  
				DefaultTableCellRenderer.CENTER);
		setColumnWidthRelative(jTableGeneticAlgorithm, new double[] 
				{0.8, 0.1, 0.1});
		jScrollPaneGeneticAlgorithm.setViewportView(jTableGeneticAlgorithm);
	}
	
	private void doEnumeration(Map<String, Constraint> constraintsMap) {
		long runtime = System.currentTimeMillis();
		AnalyticAlgorithm analyticAlgorithm = new AnalyticAlgorithm(
				serviceClassesList, serviceCandidatesList, constraintsMap);
		if (jCheckBoxAnalyticAlgorithm.isSelected()) {
			analyticAlgorithm.start(jProgressBarAnalyticAlgorithm);
		}
		runtime = System.currentTimeMillis() - runtime;
		jTableGeneralResults.setValueAt(runtime + " ms", 3, 1);
	}
	
	// ELEMENTS OF DOUBLE[] COLUMNWIDTHPERCENTAGES 
	// HAVE TO BE 1 IN SUM
	private void setColumnWidthRelative(
			JTable table, double[] columnWidthPercentages) {
		double tableWidth = table.getPreferredSize().getWidth();
		for (int count = 0; count < columnWidthPercentages.length; count++) {
			table.getColumnModel().getColumn(count).setPreferredWidth(
					(int)((columnWidthPercentages[count] * tableWidth) + 0.5));
		}
	}
	
	private void setColumnTextAlignment(
			JTable table, int column, int columnAlignment) {
		DefaultTableCellRenderer defaultRenderer = 
				new DefaultTableCellRenderer();
		defaultRenderer.setHorizontalAlignment(columnAlignment);
		table.getColumnModel().getColumn(column).setCellRenderer(
				defaultRenderer);
	}
	
	private Map<String, Constraint> getChosenConstraints() {
		Map<String, Constraint> constraintsMap = 
				new HashMap<String, Constraint>();
		int numberOfChosenConstraints = 0;
		if (jCheckBoxMaxPrice.isSelected()) {
			Constraint constraintPrice = new Constraint(Constraint.PRICE, 
					Double.valueOf(jTextFieldMaxPrice.getText()), 
					Double.parseDouble(
							(String) jTableGeneticAlgorithm.getValueAt(
							numberOfChosenConstraints, 1)));
			constraintsMap.put(constraintPrice.getTitle(), constraintPrice);
			numberOfChosenConstraints++;
		}
		if (jCheckBoxMaxCosts.isSelected()) {
			Constraint constraintCosts = new Constraint(Constraint.COSTS, 
					Double.valueOf(jTextFieldMaxCosts.getText()), 
					Double.parseDouble(
							(String) jTableGeneticAlgorithm.getValueAt(
							numberOfChosenConstraints, 1)));
			constraintsMap.put(constraintCosts.getTitle(), constraintCosts);
			numberOfChosenConstraints++;
		}
		if (jCheckBoxMaxResponseTime.isSelected()) {
			Constraint constraintResponseTime = new Constraint(
					Constraint.RESPONSE_TIME, Double.valueOf(
							jTextFieldMaxResponseTime.getText()), 
							Double.parseDouble(
							(String) jTableGeneticAlgorithm.getValueAt(
							numberOfChosenConstraints, 1)));
			constraintsMap.put(constraintResponseTime.getTitle(), 
					constraintResponseTime);
			numberOfChosenConstraints++;
		}
		if (jCheckBoxMinAvailability.isSelected()) {
			Constraint constraintAvailability = new Constraint(
					Constraint.AVAILABILITY, (Double.valueOf(
							jTextFieldMinAvailability.getText())) / 100.0, 
							Double.parseDouble(
							(String) jTableGeneticAlgorithm.getValueAt(
							numberOfChosenConstraints, 1)));
			constraintsMap.put(constraintAvailability.getTitle(), 
					constraintAvailability);
			numberOfChosenConstraints++;
		}
		if (jCheckBoxMinReliability.isSelected()) {
			Constraint constraintReliability = new Constraint(
					Constraint.RELIABILITY, (Double.valueOf(
							jTextFieldMinReliability.getText())) / 100.0, 
							Double.parseDouble(
							(String) jTableGeneticAlgorithm.getValueAt(
							numberOfChosenConstraints, 1)));
			constraintsMap.put(constraintReliability.getTitle(), 
					constraintReliability);
			numberOfChosenConstraints++;
		}
		Constraint constraintPenaltyFactor = new Constraint(
				Constraint.PENALTY_FACTOR, 0, Double.parseDouble(
						(String) jTableGeneticAlgorithm.getValueAt(
						numberOfChosenConstraints, 1)));
		constraintsMap.put(constraintPenaltyFactor.getTitle(), 
				constraintPenaltyFactor);
		return constraintsMap;
	}
	
	private void printChosenConstraintsToConsole(
			Map<String, Constraint> constraintsMap) {
		System.out.println("CHOSEN CONSTRAINTS:\n--------");
		System.out.println(constraintsMap.get(Constraint.PRICE));
		System.out.println(constraintsMap.get(Constraint.COSTS));
		System.out.println(constraintsMap.get(Constraint.RESPONSE_TIME));
		System.out.println(constraintsMap.get(Constraint.AVAILABILITY));
		System.out.println(constraintsMap.get(Constraint.RELIABILITY));
	}
	
	private void setRandomConstraints() {
		jSliderMaxPrice.setValue((int) (Math.random() * MAX_PRICE));
		jSliderMaxCosts.setValue((int) (Math.random() * MAX_COSTS));
		jSliderMaxResponseTime.setValue(
				(int) (Math.random() * MAX_RESPONSE_TIME));
		jSliderMinAvailability.setValue(
				(int) (Math.random() * MAX_AVAILABILITY));
		jSliderMinReliability.setValue(
				(int) (Math.random() * MAX_RELIABILITY));
	}
	
	private void setDefaultConstraints() {
		jSliderMaxPrice.setValue(MAX_PRICE / 2);
		jSliderMaxCosts.setValue(MAX_COSTS / 2);
		jSliderMaxResponseTime.setValue(MAX_RESPONSE_TIME / 2);
		jSliderMinAvailability.setValue(MAX_AVAILABILITY / 2);
		jSliderMinReliability.setValue(MAX_RELIABILITY / 2);
	}
	
	private void resetProgram() {
		frame.dispose();
		frame = new MainFrame();
		frame.setVisible(true);
	}
}
