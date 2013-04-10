package qos;

import java.util.List;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JScrollPane;
import java.awt.GridBagConstraints;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;

// TODO: This class is not very well-structured; but unless
//		 it is the first attempt to show what is possible 
//		 regarding to visualization, that's okay :)
public class AlgorithmsVisualization extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private int[] chosenPopulation;
	private int[] maxPopulation;
	private List<Integer> numberOfDifferentSolutions;
	private List<Double> maxUtilityPerPopulation;
	private List<Double> averageUtilityPerPopulation;
	private int startPopulationSize;

	public AlgorithmsVisualization(int[] maxPopulation,
			int[] chosenPopulation, List<Integer> numberOfDifferentSolutions, 
			int startPopulationSize, List<Double> maxUtilityPerPopulation,
			List<Double> averageUtilityPerPopulation) {
		super("Result Visualization");
		this.chosenPopulation = chosenPopulation;
		this.maxPopulation = maxPopulation;
		this.numberOfDifferentSolutions = numberOfDifferentSolutions;
		this.maxUtilityPerPopulation = maxUtilityPerPopulation;
		this.averageUtilityPerPopulation = averageUtilityPerPopulation;
		this.startPopulationSize = startPopulationSize;
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
				new GeneticAlgorithmVisualizationPanel(maxPopulation, 
						chosenPopulation, numberOfDifferentSolutions, 
						startPopulationSize, maxUtilityPerPopulation, 
						averageUtilityPerPopulation), null);
		
		JPanel jPanelAntAlgorithm = new JPanel();
		tabbedPane.addTab("Ant Algorithm", null, jPanelAntAlgorithm, null);
		
		JPanel jPanelAnalyticAlgorithm = new JPanel();
		tabbedPane.addTab("Analytic Algorithm", null, 
				jPanelAnalyticAlgorithm, null);
	}
	
	public void closeWindow() {
		this.dispose();
	}
}

