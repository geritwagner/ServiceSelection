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
		Graphics2D g = (Graphics2D) graphics;
		AffineTransform normal = g.getTransform();
		AffineTransform rotated = g.getTransform();
		rotated.quadrantRotate(3);
		AffineTransform FLIP_X_COORDINATE = 
			new AffineTransform(1, 0, 0, -1, 0, getHeight());
		int lineLength = 300 / chosenPopulation.length;
		int lineHeight = 0;
		Font font = new Font("testFont", Font.ITALIC, 9);
		Font font2 = new Font("testFont", Font.BOLD, 12);
		g.setFont(font2);
		g.drawString("Generations", 165, 530);
		g.drawString("Generations", 565, 530);
		g.setTransform(rotated);
		g.drawString("Number of Different Compositions", -385, 20);
		g.drawString("Utility Value", -335, 420);
		g.setFont(font);
		g.setColor(Color.BLACK);

		
		
		
		
		// TODO: y-axis not correct!
		// Number of different solutions per generation
		g.setTransform(normal);
		lineLength = 300 / Math.max(1, numberOfDifferentSolutions.size() - 1);
		lineHeight = 0;
		for (int i = 0; i < numberOfDifferentSolutions.size() - 1; i++) {
			if (lineHeight < numberOfDifferentSolutions.get(i)) {
				lineHeight = numberOfDifferentSolutions.get(i);
			}
		}
		for (int i = 0; i <= lineHeight; i++) {
			
			if (i % 5 == 0) {
				g.drawLine(50, 
						494 - (int) Math.round((390.0 / lineHeight) * i), 
						44, 
						494 - (int) Math.round((390.0 / lineHeight) * i));
				g.drawString("" + i, 
						30, 
						497 - (int) Math.round((390.0 / lineHeight) * i));
			}
			else {
				g.drawLine(50, 
						494 - (int) Math.round((390.0 / lineHeight) * i), 
						48, 
						494 - (int) Math.round((390.0 / lineHeight) * i));
			}
		}
		g.drawString(String.valueOf(numberOfDifferentSolutions.size() - 1), 
				(numberOfDifferentSolutions.size() - 1) * lineLength + 43, 
				505);
		g.setTransform(FLIP_X_COORDINATE);
		for (int i = 0; i < numberOfDifferentSolutions.size() - 2; i++) {
			g.setColor(Color.RED);
			g.drawLine(i * lineLength + 50, 
					numberOfDifferentSolutions.get(i) * 
							(int) Math.round((390.0 / lineHeight)) + 25, 
							(i + 1) * lineLength + 50, 
							numberOfDifferentSolutions.get(i + 1) * 
							(int) Math.round((390.0 / lineHeight)) + 25);
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
		g.drawLine((numberOfDifferentSolutions.size() - 1) * lineLength + 50, 
				25, (numberOfDifferentSolutions.size() - 1) * lineLength + 50, 
				27);
		g.drawLine(50, 25, 350, 25);
		g.drawLine(50, 25, 50, 415);
		

		
		
		// Area 4
		g.setTransform(normal);
		lineHeight = 39;
		for (int i = 0; i < 10; i++) {
			g.drawLine(450, 494 - lineHeight * i, 445, 494 - lineHeight * i);
			g.drawString("0." + i, 430, 497 - lineHeight * i);
			
		}
		g.drawLine(450, 494 - lineHeight * 10, 
				448, 494 - lineHeight * 10);
		g.drawString("1.0", 430, 
				497 - lineHeight * 10);

		g.drawString(String.valueOf(maxUtilityPerPopulation.size() - 1), 
				(maxUtilityPerPopulation.size() - 1) * lineLength + 445, 505);
		g.setTransform(FLIP_X_COORDINATE);
		for (int i = 0; i < maxUtilityPerPopulation.size() - 2; i++) {
			g.setColor(Color.YELLOW);
			g.drawLine(i * lineLength + 450, 
					(int) Math.round(maxUtilityPerPopulation.get(i) * 10 * 
							lineHeight + 25), 
							(i + 1) * lineLength + 450, 
							(int) Math.round(maxUtilityPerPopulation.
									get(i + 1) * 10 * 
									lineHeight + 25));
			g.setColor(Color.GREEN);
			g.drawLine(i * lineLength + 450, 
					(int) Math.round(averageUtilityPerPopulation.get(i) * 10 * 
							lineHeight + 25), 
							(i + 1) * lineLength + 450, 
							(int) Math.round(averageUtilityPerPopulation.
									get(i + 1) * 10 * 
									lineHeight + 25));
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
		g.drawLine((maxUtilityPerPopulation.size() - 1) * lineLength + 450, 25, 
				(maxUtilityPerPopulation.size() - 1) * lineLength + 450, 27);
		g.drawLine(450, 25, 750, 25);
		g.drawLine(450, 25, 450, 415);
	}	
}
