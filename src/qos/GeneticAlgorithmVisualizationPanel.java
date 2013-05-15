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
	private List<Integer> numberOfDifferentSolutions;
	private List<Double> maxUtilityPerPopulation;
	private List<Double> averageUtilityPerPopulation;
	
	private int lineLength = 0;
	private int lineUnit = 1;

	public GeneticAlgorithmVisualizationPanel(
			List<Integer> numberOfDifferentSolutions, 
			List<Double> maxUtilityPerPopulation,
			List<Double> averageUtilityPerPopulation) {
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
		// Determination of the line length
		int maxXCoordinate = determineLineLengthProperties();
		lineLength = 300 / lineLength;
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
		g.drawString("Fitness Value", -335, 415);
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
				g.drawLine(50, 25, 50, 425);
		// Dynamic determination of the length for one generation
		// (x-axis)
		// Height of the y-axis
		lineHeight = numberOfDifferentSolutions.get(0);
		int heightUnit = determineHeightUnit(lineHeight);
		// Legend for y-axis
		for (int i = 0; i <= lineHeight; i += heightUnit) {
			if ((i / heightUnit) % 5 == 0) {
				g.setTransform(FLIP_X_COORDINATE);
				g.drawLine(53,25 + (int) Math.round(
						(400.0 * i / lineHeight)), 
						47, 25 + (int) Math.round((
								400.0 * i / lineHeight)));
				g.setTransform(normal);
				g.drawString("" + i, 30, 494 - (int) Math.round(
						(400.0 / lineHeight) * i));
			}
//			else {
//				g.setTransform(FLIP_X_COORDINATE);
//				g.drawLine(52, 25 + (int) Math.round(
//						(390.0 / lineHeight) * i / heightUnit), 
//						50, 25 + (int) Math.round(
//								(390.0 / lineHeight) * i / heightUnit));
//			}
		}
		g.setTransform(FLIP_X_COORDINATE);
		// Drawing of the graph and the legend for x-axis
		for (int i = 0; i <= maxXCoordinate; i += lineUnit) {
			// Graph
			if (i < numberOfDifferentSolutions.size() - 1 - lineUnit) {
				g.setColor(Color.RED);
//				System.out.println(numberOfDifferentSolutions.get(i));
				g.drawLine(i /lineUnit * lineLength + 50, 
						(int) Math.round(numberOfDifferentSolutions.get(i) * 
								(400.0 / lineHeight)) + 25, (i /lineUnit + 1) * 
								lineLength + 50, 
								(int) Math.round(numberOfDifferentSolutions.
										get(i + lineUnit) * 
										(400.0 / lineHeight)) + 25);
			}
			
			// Legend for x-axis
			g.setColor(Color.BLACK);
			if ((i /lineUnit) % 5 == 0) {
				g.drawLine((i /lineUnit) * lineLength + 50, 25, 
						(i /lineUnit) * lineLength + 50, 27);
				g.setTransform(normal);
				g.drawString(String.valueOf((i)), 
						(i /lineUnit) * lineLength + 43, 505);
				g.setTransform(FLIP_X_COORDINATE);
			}
		}
		g.setColor(Color.BLACK);
		




		// MAX/AVERAGE FITNESS VALUE PER GENERATION
		g.setTransform(FLIP_X_COORDINATE);
		// Drawing of the coordinate system
		g.drawLine(450, 25, 750, 25);
		g.drawLine(450, 25, 450, 425);

		lineHeight = 20;
		// Legend for y-axis
		for (int i = 0; i < 10; i++) {
			g.setTransform(FLIP_X_COORDINATE);
			g.drawLine(452, 25 + lineHeight * i, 448, 25 + lineHeight * i);
			g.drawLine(452, 25 + lineHeight * (i + 10), 
					448, 25 + lineHeight * (i + 10));
			g.setTransform(normal);
			g.drawString("0." + i, 425, 494 - lineHeight * i);
			g.drawString("1." + i, 425, 494 - lineHeight * (i + 10));
		}
		// Manual insertion of the last index of the y-axis
		g.setTransform(FLIP_X_COORDINATE);
		g.drawLine(452, 25 + lineHeight * 20, 448, 25 + lineHeight * 20);
		g.setTransform(normal);
		g.drawString("2.0", 425, 494 - lineHeight * 20);

		// // Manual insertion of the last index of the x-axis
		g.setTransform(FLIP_X_COORDINATE);
		
//		System.out.println();
		
		// Drawing of the graphs and the legend for x-axis
		for (int i = 0; i <= maxXCoordinate; i += lineUnit) {
			// Max. fitness graph
			if (i < maxUtilityPerPopulation.size() - 1 - lineUnit) {
				g.setColor(Color.YELLOW);
				g.drawLine(i / lineUnit * lineLength + 450, 
						(int) Math.round(maxUtilityPerPopulation.get(i) * 10 * 
								lineHeight + 25), 
								(i / lineUnit + 1) * lineLength + 450, 
								(int) Math.round(maxUtilityPerPopulation.
										get(i + lineUnit) * 10 * 
										lineHeight + 25));
				// Average fitness graph
				g.setColor(Color.GREEN);
//				System.out.println(maxUtilityPerPopulation.get(i) + 
//						" - " + averageUtilityPerPopulation.get(i));
				g.drawLine(i / lineUnit * lineLength + 450, 
						(int) Math.round(averageUtilityPerPopulation.get(i) * 
								10 * lineHeight + 25), 
								(i / lineUnit + 1) * lineLength + 450, 
								(int) Math.round(averageUtilityPerPopulation.
										get(i + lineUnit) * 10 * 
										lineHeight + 25));
			}
			
			// Legend for x-axis
			if ((i /lineUnit) % 5 == 0) {
				g.setColor(Color.BLACK);
				g.drawLine((i /lineUnit) * lineLength + 450, 25, 
						(i /lineUnit) * lineLength + 450, 27);
				g.setTransform(normal);
				g.drawString(String.valueOf((i)), 
						(i /lineUnit) * lineLength + 445, 505);
				g.setTransform(FLIP_X_COORDINATE);
			}
		}
		g.setColor(Color.BLACK);
	}
	
	private int determineLineLengthProperties() {
		if ((numberOfDifferentSolutions.size() - 1) <= 10) {
			lineLength = 10;
			lineUnit = 1;
			return 10;
		}
		else if ((numberOfDifferentSolutions.size() - 1) <= 20) {
			lineLength = 20;
			lineUnit = 1;
			return 20;
		}
		else if ((numberOfDifferentSolutions.size() - 1) <= 30) {
			lineLength = 30;
			lineUnit = 1;
			return 30;
		}
		else if ((numberOfDifferentSolutions.size() - 1) <= 50) {
			lineLength = 50;
			lineUnit = 1;
			return 50;
		}
		else if ((numberOfDifferentSolutions.size() - 1) <= 75) {
			lineLength = 75;
			lineUnit = 1;
			return 75;
		}
		else if ((numberOfDifferentSolutions.size() - 1) <= 100) {
			lineLength = 100;
			lineUnit = 1;
			return 100;
		}
		else if ((numberOfDifferentSolutions.size() - 1) <= 150) {
			lineLength = 30;
			lineUnit = 5;
			return 150;
		}
		else if ((numberOfDifferentSolutions.size() - 1) <= 250) {
			lineLength = 50;
			lineUnit = 5;
			return 250;
		}
		else if ((numberOfDifferentSolutions.size() - 1) <= 500) {
			lineLength = 50;
			lineUnit = 10;
			return 500;
		}
		else if ((numberOfDifferentSolutions.size() - 1) <= 750) {
			lineLength = 75;
			lineUnit = 10;
			return 750;
		}
		else if ((numberOfDifferentSolutions.size() - 1) <= 1000) {
			lineLength = 100;
			lineUnit = 10;
			return 1000;
		}
		else if ((numberOfDifferentSolutions.size() - 1) <= 1500) {
			lineLength = 150;
			lineUnit = 10;
			return 1500;
		}
		else if ((numberOfDifferentSolutions.size() - 1) <= 3000) {
			lineLength = 30;
			lineUnit = 100;
			return 3000;
		}
		else if ((numberOfDifferentSolutions.size() - 1) <= 5000) {
			lineLength = 50;
			lineUnit = 100;
			return 5000;
		}
		else if ((numberOfDifferentSolutions.size() - 1) <= 7500) {
			lineLength = 75;
			lineUnit = 100;
			return 7500;
		}
		else if ((numberOfDifferentSolutions.size() - 1) <= 10000) {
			lineLength = 100;
			lineUnit = 100;
			return 10000;
		}
		else if ((numberOfDifferentSolutions.size() - 1) <= 15000) {
			lineLength = 150;
			lineUnit = 10;
			return 15000;
		}
		else {
			return 0;
		}
	}
	
	private int determineHeightUnit(int lineHeight) {
		if (lineHeight <= 100) {
			return 1;
		}
		else if (lineHeight <= 1000) {
			return 10;
		}
		else {
			return 100;
		}
	}
}
