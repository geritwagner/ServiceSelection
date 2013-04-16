package qos;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.JPanel;

public class GeneticAlgorithmVisualizationPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int[] chosenPopulation;
	private int[] maxPopulation;
	private List<Integer> numberOfDifferentSolutions;
	private List<Double> maxUtilityPerPopulation;
	private List<Double> averageUtilityPerPopulation;
	private int startPopulationSize;
	
	public GeneticAlgorithmVisualizationPanel(int[] maxPopulation,
			int[] chosenPopulation, List<Integer> numberOfDifferentSolutions, 
			int startPopulationSize, List<Double> maxUtilityPerPopulation,
			List<Double> averageUtilityPerPopulation) {
		this.chosenPopulation = chosenPopulation;
		this.maxPopulation = maxPopulation;
		this.numberOfDifferentSolutions = numberOfDifferentSolutions;
		this.maxUtilityPerPopulation = maxUtilityPerPopulation;
		this.averageUtilityPerPopulation = averageUtilityPerPopulation;
		this.startPopulationSize = startPopulationSize;
	}

	@Override
	public void paint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		AffineTransform normal = g.getTransform();
		AffineTransform rotated = g.getTransform();
		rotated.quadrantRotate(3);
		AffineTransform FLIP_X_COORDINATE = 
			new AffineTransform(1, 0, 0, -1, 0, getHeight());
//		String printClass = "Class ";
		int lineLength = 300 / chosenPopulation.length;
//		if (lineLength < 50) {
//			printClass = "";
//		}
		int lineHeight = 0;
		Font font = new Font("testFont", Font.ITALIC, 9);
		Font font2 = new Font("testFont", Font.BOLD, 12);
		
		g.setFont(font2);
//		g.drawString("Service Classes", 155, 250);
		g.drawString("Generations", 575, 250);
//		g.drawString("Start Population (related to Max. Population)", 70, 530);
		g.drawString("Generations", 575, 530);
		g.setTransform(rotated);
//		g.drawString("Number of Service Candidates", -205, 20);
		g.drawString("Number of Different Compositions", -215, 420);
		g.drawString("Utility Value", -425, 420);
		
//		// Composition of the start population (diversity)
		g.setFont(font);
		g.setTransform(normal);
//		for (int i = 0; i < maxPopulation.length; i++) {
//			if (lineHeight < maxPopulation[i]){
//				lineHeight = maxPopulation[i];
//			}
//		}
//		for (int i = 0; i <= lineHeight; i += 5) {
//			g.drawString(String.valueOf(i), 30, 220 - (190 / lineHeight) * i);
//		}
//		g.setTransform(FLIP_X_COORDINATE);
//		BasicStroke dashed = new BasicStroke(
//				1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 
//				10f, new float[] {2f}, 0f);
//		lineHeight = 190 / lineHeight;
//		for (int i = 0; i < chosenPopulation.length; i++) {
//			g.setColor(Color.ORANGE);
//			g.fillPolygon(new int[]{i * lineLength + 50, i * lineLength + 50, 
//					(i + 1) * lineLength + 50, (i + 1) * lineLength + 50}, 
//					new int[]{300, chosenPopulation[Math.max(0, i - 1)] * 
//					lineHeight + 300,
//					chosenPopulation[i] * lineHeight + 300, 300}, 4);
//			g.setColor(Color.BLACK);
//			g.setStroke(dashed);
//			g.drawLine(i * lineLength + 50, 
//					maxPopulation[Math.max(0, i - 1)] * lineHeight + 300, 
//					(i + 1) * lineLength + 50, 
//					maxPopulation[i] * lineHeight + 300);
//			g.setStroke(new BasicStroke());
//			g.setTransform(normal);
//			g.drawString(printClass + (i + 1), i * lineLength + 52, 233);
//			g.setTransform(FLIP_X_COORDINATE);
//		}
//		for (int i = 0; i < chosenPopulation.length; i++) {
//			g.drawLine((i + 1) * lineLength + 50, 300, 
//					(i + 1) * lineLength + 50, 305);
//		}
//		g.drawLine(50, 300, 350, 300);
//		g.drawLine(50, 300, 50, 500);
		
		
		
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
		int stepSize = 5;
		if (lineHeight > 50) {
			stepSize = 10;
		}
		for (int i = 0; i <= lineHeight; i += stepSize) {
			g.drawLine(450, 220 - (int) Math.round((190.0 / lineHeight) * i), 
					448, 220 - (int) Math.round((190.0 / lineHeight) * i));
			g.drawString("" + i, 430, 
					223 - (int) Math.round((190.0 / lineHeight) * i));
		}
		g.setTransform(FLIP_X_COORDINATE);
		for (int i = 0; i < numberOfDifferentSolutions.size() - 2; i++) {
			g.setColor(Color.RED);
			g.drawLine(i * lineLength + 450, 
					numberOfDifferentSolutions.get(i) * 
							(int) Math.round((190.0 / lineHeight)) + 300, 
							(i + 1) * lineLength + 450, 
							numberOfDifferentSolutions.get(i + 1) * 
							(int) Math.round((190.0 / lineHeight)) + 300);
			g.setColor(Color.BLACK);
			if ((i + 1) % 5 == 0) {
				g.drawLine((i + 1) * lineLength + 450, 300, 
						(i + 1) * lineLength + 450, 302);
				g.setTransform(normal);
				g.drawString(String.valueOf(i + 1), 
						(i + 1) * lineLength + 443, 233);
				g.setTransform(FLIP_X_COORDINATE);
			}
		}
		g.setColor(Color.BLACK);
		g.drawLine(450, 299, 750, 299);
		g.drawLine(450, 300, 450, 500);
		
		
		
		// Area 3
//		int numberOfMaxPop = 1;
//		for (int i = 0; i < maxPopulation.length; i++) {
//			numberOfMaxPop *= maxPopulation[i];
//		}
//		double quotient = (double) startPopulationSize / numberOfMaxPop;
//		
//		final int middleX = 200;
//		final int middleY = 125;
//		
//		double bigArea = (Math.PI / 4.0) * 300 * 200;
//		double chosenArea = quotient * bigArea;
//		
//		int width = (int) Math.round(
//				(Math.sqrt(chosenArea * 6.0 / Math.PI)));
//		int height = (int) Math.round(width * 2.0 / 3.0);
//	
//		g.fillOval((int)((middleX - 0.5 * width)), 
//				(int)((middleY - 0.5 * height)), 
//				width, height);
//
//		g.drawOval(50, 25, 300, 200);
		g.setTransform(normal);
//		g.drawString(new DecimalFormat("###.####").format(quotient * 100) + 
//				"%", 195, 585);
		
		// Area 4
		lineHeight = 20;
		for (int i = 0; i < 10; i++) {
			
			g.drawLine(450, 494 - lineHeight * i, 
					448, 494 - lineHeight * i);
			g.drawString("0." + i, 430, 
					497 - lineHeight * i);
			
		}
		g.drawLine(450, 494 - lineHeight * 10, 
				448, 494 - lineHeight * 10);
		g.drawString("1.0", 430, 
				497 - lineHeight * 10);

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

		g.drawLine(450, 25, 750, 25);
		g.drawLine(450, 25, 450, 225);
		
	}	
}
