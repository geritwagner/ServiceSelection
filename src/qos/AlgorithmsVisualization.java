package qos;

import java.util.List;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;

public class AlgorithmsVisualization extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private List<Integer> numberOfDifferentSolutions;
	private List<Double> maxUtilityPerPopulation;
	private List<Double> averageUtilityPerPopulation;

	public AlgorithmsVisualization(
			List<Integer> numberOfDifferentSolutions, 
			List<Double> maxUtilityPerPopulation,
			List<Double> averageUtilityPerPopulation) {
		super("Result Visualization");
		this.numberOfDifferentSolutions = numberOfDifferentSolutions;
		this.maxUtilityPerPopulation = maxUtilityPerPopulation;
		this.averageUtilityPerPopulation = averageUtilityPerPopulation;
		initializeFrame();
	}
	
	private void initializeFrame() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		setAlwaysOnTop(true);
		setBounds(50, 50, 800, 600);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbcTabbedPane = new GridBagConstraints();
		gbcTabbedPane.fill = GridBagConstraints.BOTH;
		gbcTabbedPane.gridx = 0;
		gbcTabbedPane.gridy = 0;
		getContentPane().add(tabbedPane, gbcTabbedPane);

		tabbedPane.addTab("Genetic Algorithm", null, 
				new GeneticAlgorithmVisualizationPanel(
						numberOfDifferentSolutions, maxUtilityPerPopulation, 
						averageUtilityPerPopulation), null);
		
		JPanel jPanelAntAlgorithm = new JPanel();
		tabbedPane.addTab("Ant Algorithm", null, jPanelAntAlgorithm, null);
	}
	
	public void closeWindow() {
		this.dispose();
	}
}

