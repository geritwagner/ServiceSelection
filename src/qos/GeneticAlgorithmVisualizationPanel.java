package qos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.List;

import javax.swing.JPanel;

public class GeneticAlgorithmVisualizationPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int[] chosenPopulation;
	private List<Integer> numberOfDifferentSolutions;
	private List<Double> maxUtilityPerPopulation;
	private List<Double> averageUtilityPerPopulation;

	public GeneticAlgorithmVisualizationPanel(int[] chosenPopulation, 
			List<Integer> numberOfDifferentSolutions, 
			List<Double> maxUtilityPerPopulation,
			List<Double> averageUtilityPerPopulation) {
		this.chosenPopulation = chosenPopulation;
		this.numberOfDifferentSolutions = numberOfDifferentSolutions;
		this.maxUtilityPerPopulation = maxUtilityPerPopulation;
		this.averageUtilityPerPopulation = averageUtilityPerPopulation;
	}

	@Override
	public void paint(Graphics graphics) {
		// BASIC SETTINGS
		Graphics2D g = (Graphics2D) graphics;
		AffineTransform normal = g.getTransform();
		AffineTransform rotated = g.getTransform();
		rotated.quadrantRotate(3);
		AffineTransform FLIP_X_COORDINATE = 
				new AffineTransform(1, 0, 0, -1, 0, getHeight());
		int lineLength = 300 / chosenPopulation.length;
		int lineHeight = 0;
		Font font = new Font("normalFont", Font.ITALIC, 9);
		Font font2 = new Font("axisFont", Font.BOLD, 12);
		Font font3 = new Font("headlineFont", Font.BOLD, 18);
		// Headlines
		g.setFont(font3);
		g.drawString("Number of Different Solutions", 
				70, 25);
		g.drawString("per Generation", 
				135, 50);
		g.drawString("Max/Average Fitness Value", 
				480, 25);
		g.drawString("per Generation", 
				530, 50);
		g.setFont(font2);
		// Main legend for all axes
		g.drawString("Generations", 165, 530);
		g.drawString("Generations", 565, 530);
		g.setTransform(rotated);
		g.drawString("Number of Different Compositions", -385, 20);
		g.drawString("Fitness Value", -335, 420);
		// Graph legend for chart to the right
		g.setTransform(normal);
		g.fillRect(650, 100, 100, 50);
		g.setColor(Color.YELLOW);
		g.drawString("Max. Fitness", 665, 120);
		g.setColor(Color.GREEN);
		g.drawString("Avg. Fitness", 665, 140);




		
		// NUMBER OF DIFFERENT SOLUTIONS PER GENERATION
		g.setColor(Color.BLACK);
		g.setFont(font);
		g.setTransform(FLIP_X_COORDINATE);
		// Drawing of the coordinate system
				g.drawLine(50, 25, 350, 25);
				g.drawLine(50, 25, 50, 415);
		// Dynamic determination of the length for one generation
		// (x-axis)
		lineLength = 300 / Math.max(1, numberOfDifferentSolutions.size() - 1);
		// Height of the y-axis
		lineHeight = numberOfDifferentSolutions.get(0);
		// Legend for y-axis
		for (int i = 0; i <= lineHeight; i++) {
			if (i % 5 == 0) {
				g.setTransform(FLIP_X_COORDINATE);
				g.drawLine(53, 
						25 + (int) Math.round((390.0 / lineHeight) * i), 
						47, 
						25 + (int) Math.round((390.0 / lineHeight) * i));
				g.setTransform(normal);
				g.drawString("" + i, 30, 
						497 - (int) Math.round((390.0 / lineHeight) * i));
			}
			else {
				g.setTransform(FLIP_X_COORDINATE);
				g.drawLine(52, 
						25 + (int) Math.round((390.0 / lineHeight) * i), 
						50, 
						25 + (int) Math.round((390.0 / lineHeight) * i));
			}
		}
		// Manual insertion of the last index of the x-axis
		g.drawString(String.valueOf(numberOfDifferentSolutions.size() - 1), 
				(numberOfDifferentSolutions.size() - 1) * lineLength + 43, 
				505);
		g.setTransform(FLIP_X_COORDINATE);
		// Drawing of the graph and the legend for x-axis
		for (int i = 0; i < numberOfDifferentSolutions.size() - 2; i++) {
			// Graph
			g.setColor(Color.RED);
			g.drawLine(i * lineLength + 50, 
					(int) Math.round(numberOfDifferentSolutions.get(i) * 
							(390.0 / lineHeight)) + 25, 
							(i + 1) * lineLength + 50, 
							(int) Math.round(numberOfDifferentSolutions.
									get(i + 1) * (390.0 / lineHeight)) + 25);
			// Legend for x-axis
			g.setColor(Color.BLACK);
			if ((i + 1) % 5 == 0) {
				g.drawLine((i + 1) * lineLength + 50, 25, 
						(i + 1) * lineLength + 50, 27);
				g.setTransform(normal);
				g.drawString(String.valueOf(i + 1), 
						(i + 1) * lineLength + 43, 505);
				g.setTransform(FLIP_X_COORDINATE);
			}
		}
		g.setColor(Color.BLACK);
		// Manual insertion of the last scale line of the x-axis
		g.drawLine((numberOfDifferentSolutions.size() - 1) * lineLength + 50, 
				25, (numberOfDifferentSolutions.size() - 1) * lineLength + 50, 
				27);
		




		// MAX/AVERAGE FITNESS VALUE PER GENERATION
		g.setTransform(FLIP_X_COORDINATE);
		// Drawing of the coordinate system
		g.drawLine(450, 25, 750, 25);
		g.drawLine(450, 25, 450, 415);
		g.setTransform(normal);
		
		lineHeight = 39;
		// Legend for y-axis
		for (int i = 0; i < 10; i++) {
			g.drawLine(450, 494 - lineHeight * i, 445, 494 - lineHeight * i);
			g.drawString("0." + i, 430, 497 - lineHeight * i);

		}
		// Manual insertion of the last index of the y-axis
		g.drawLine(450, 494 - lineHeight * 10, 
				445, 494 - lineHeight * 10);
		g.drawString("1.0", 430, 
				497 - lineHeight * 10);

		// // Manual insertion of the last index of the x-axis
		g.drawString(String.valueOf(maxUtilityPerPopulation.size() - 1), 
				(maxUtilityPerPopulation.size() - 1) * lineLength + 445, 505);
		g.setTransform(FLIP_X_COORDINATE);
		
		// Drawing of the graphs and the legend for x-axis
		for (int i = 0; i < maxUtilityPerPopulation.size() - 2; i++) {
			// Max. fitness graph
			g.setColor(Color.YELLOW);
			g.drawLine(i * lineLength + 450, 
					(int) Math.round(maxUtilityPerPopulation.get(i) * 10 * 
							lineHeight + 25), 
							(i + 1) * lineLength + 450, 
							(int) Math.round(maxUtilityPerPopulation.
									get(i + 1) * 10 * 
									lineHeight + 25));
			// Average fitness graph
			g.setColor(Color.GREEN);
			g.drawLine(i * lineLength + 450, 
					(int) Math.round(averageUtilityPerPopulation.get(i) * 10 * 
							lineHeight + 25), 
							(i + 1) * lineLength + 450, 
							(int) Math.round(averageUtilityPerPopulation.
									get(i + 1) * 10 * 
									lineHeight + 25));
			// Legend for x-axis
			if ((i + 1) % 5 == 0) {
				g.setColor(Color.BLACK);
				g.drawLine((i + 1) * lineLength + 450, 25, 
						(i + 1) * lineLength + 450, 27);
				g.setTransform(normal);
				g.drawString(String.valueOf(i + 1), 
						(i + 1) * lineLength + 445, 505);
				g.setTransform(FLIP_X_COORDINATE);
			}
		}
		g.setColor(Color.BLACK);
		// Manual insertion of the last scale line of the x-axis
		g.drawLine((maxUtilityPerPopulation.size() - 1) * lineLength + 450, 25, 
				(maxUtilityPerPopulation.size() - 1) * lineLength + 450, 27);
	}	
}
