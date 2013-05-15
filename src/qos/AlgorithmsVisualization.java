package qos;

import java.util.List;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JTabbedPane;

public class AlgorithmsVisualization extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private List<Integer> numberOfDifferentSolutions;
	private List<Double> maxUtilityPerPopulation;
	private List<Double> averageUtilityPerPopulation;
	private List<Double> optUtilityPerIteration;
	private boolean isGeneticAlgorithmChosen;
	private boolean isAntAlgorithmChosen;

	public AlgorithmsVisualization(
			List<Integer> numberOfDifferentSolutions, 
			List<Double> maxUtilityPerPopulation,
			List<Double> averageUtilityPerPopulation,
			List<Double> optUtilityPerIteration,
			boolean isGeneticAlgorithmChosen,
			boolean isAntAlgorithmChosen) {
		super("Result Visualization");
		this.numberOfDifferentSolutions = numberOfDifferentSolutions;
		this.maxUtilityPerPopulation = maxUtilityPerPopulation;
		this.averageUtilityPerPopulation = averageUtilityPerPopulation;
		this.optUtilityPerIteration = optUtilityPerIteration;
		this.isGeneticAlgorithmChosen = isGeneticAlgorithmChosen;
		this.isAntAlgorithmChosen = isAntAlgorithmChosen;
		initializeFrame();
	}
	
	public AlgorithmsVisualization(
			List<Integer> numberOfDifferentSolutions, 
			List<Double> maxUtilityPerPopulation,
			List<Double> averageUtilityPerPopulation,
			boolean isGeneticAlgorithmChosen,
			boolean isAntAlgorithmChosen) {
		super("Result Visualization");
		this.numberOfDifferentSolutions = numberOfDifferentSolutions;
		this.maxUtilityPerPopulation = maxUtilityPerPopulation;
		this.averageUtilityPerPopulation = averageUtilityPerPopulation;
		this.isGeneticAlgorithmChosen = isGeneticAlgorithmChosen;
		this.isAntAlgorithmChosen = isAntAlgorithmChosen;
		initializeFrame();
	}
	
	public AlgorithmsVisualization(
			List<Double> optUtilityPerIteration,
			boolean isGeneticAlgorithmChosen,
			boolean isAntAlgorithmChosen) {
		super("Result Visualization");
		this.optUtilityPerIteration = optUtilityPerIteration;
		this.isGeneticAlgorithmChosen = isGeneticAlgorithmChosen;
		this.isAntAlgorithmChosen = isAntAlgorithmChosen;
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

		if (isGeneticAlgorithmChosen) {
			tabbedPane.addTab("Genetic Algorithm", null, 
					new GeneticAlgorithmVisualizationPanel(
							numberOfDifferentSolutions, 
							maxUtilityPerPopulation, 
							averageUtilityPerPopulation), null);
		}
		if (isAntAlgorithmChosen) {
			tabbedPane.addTab("Ant Algorithm", null, 
					new AntAlgorithmVisualizationPanel(
							optUtilityPerIteration), null);
		}
	}
	
	public void closeWindow() {
		this.dispose();
	}
}

