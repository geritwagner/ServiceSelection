package qos;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.JFrame;

// TODO: This class is not very well-structured; but unless
//		 it is the first attempt to show what is possible 
//		 regarding to visualization, that's okay :)
public class GeneticAlgorithmsVisualization extends JFrame{

	private static final long serialVersionUID = 1L;
	private int[] chosenPopulation;
	private int[] maxPopulation;
	private List<Integer> numberOfDifferentSolutions;
	private int startPopulationSize;

	public GeneticAlgorithmsVisualization(int[] maxPopulation,
			int[] chosenPopulation, List<Integer> numberOfDifferentSolutions, 
			int startPopulationSize) {
		super("Result Visualization");
		this.chosenPopulation = chosenPopulation;
		this.maxPopulation = maxPopulation;
		this.numberOfDifferentSolutions = numberOfDifferentSolutions;
		this.startPopulationSize = startPopulationSize;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//		setSize(800, 600);
		setVisible(true);
		setResizable(false);
		setAlwaysOnTop(true);
		setBounds(50, 50, 800, 600);
	}

	@Override
	public void paint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		AffineTransform normal = g.getTransform();
		AffineTransform rotated = g.getTransform();
		rotated.quadrantRotate(3);
		AffineTransform FLIP_X_COORDINATE = 
			new AffineTransform(1, 0, 0, -1, 0, getHeight());
		String printClass = "Class ";
		int lineLength = 300 / chosenPopulation.length;
		if (lineLength < 50) {
			printClass = "";
		}
		int lineHeight = 0;
		Font font = new Font("testFont", Font.ITALIC, 9);
		Font font2 = new Font("testFont", Font.BOLD, 12);
		g.setFont(font);
		for (int i = 0; i < maxPopulation.length; i++) {
			if (lineHeight < maxPopulation[i]){
				lineHeight = maxPopulation[i];
			}
		}
		for (int i = 0; i <= lineHeight; i += 5) {
			g.drawString("" + i, 30, 252 - (190 / lineHeight) * i);
		}
		g.setFont(font2);
		
		
		
		// Composition of the start population (diversity)
		g.drawString("Service Classes", 155, 288);
		g.drawString("Generations", 575, 288);
		g.drawString("Start Population (related to Max. Population)", 90, 570);
		g.setTransform(rotated);
		g.drawString("Number of Service Candidates", -240, 20);
		g.drawString("Number of Different Compositions", -245, 420);
		g.setFont(font);
		g.setTransform(FLIP_X_COORDINATE);
		BasicStroke dashed = new BasicStroke(
				1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 
				10f, new float[] {2f}, 0f);
		lineHeight = 190 / lineHeight;
		for (int i = 0; i < chosenPopulation.length; i++) {
			g.setColor(Color.ORANGE);
			g.fillPolygon(new int[]{i * lineLength + 50, i * lineLength + 50, 
					(i + 1) * lineLength + 50, (i + 1) * lineLength + 50}, 
					new int[]{350, chosenPopulation[Math.max(0, i - 1)] * 
					lineHeight + 350,
					chosenPopulation[i] * lineHeight + 350, 350}, 4);
			g.setColor(Color.BLACK);
			g.setStroke(dashed);
			g.drawLine(i * lineLength + 50, 
					maxPopulation[Math.max(0, i - 1)] * lineHeight + 350, 
					(i + 1) * lineLength + 50, 
					maxPopulation[i] * lineHeight + 350);
			g.setStroke(new BasicStroke());
			g.setTransform(normal);
			g.drawString(printClass + (i + 1), i * lineLength + 65, 265);
			g.setTransform(FLIP_X_COORDINATE);
		}
		for (int i = 0; i < chosenPopulation.length; i++) {
			g.drawLine((i + 1) * lineLength + 50, 350, 
					(i + 1) * lineLength + 50, 355);
		}
		g.drawLine(50, 350, 350, 350);
		g.drawLine(50, 350, 50, 550);
		
		
		
		// TODO: y-axis not correct!
		// Number of different solutions per generation
		g.setTransform(normal);
		
		lineLength = 300 / Math.max(1, numberOfDifferentSolutions.size() - 1);
		lineHeight = 0;
		for (int i = 0; i < maxPopulation.length; i++) {
			if (lineHeight < numberOfDifferentSolutions.get(i)){
				lineHeight = numberOfDifferentSolutions.get(i);
			}
		}
		for (int i = 0; i <= lineHeight; i += 5) {
			g.drawString("" + i, 430, 252 - (190 / lineHeight) * i);
		}
		g.setStroke(dashed);
		g.drawLine(450, 252 - (190 / startPopulationSize) * (20 - lineHeight), 
				750, 252 - (190 / startPopulationSize) * (20 - lineHeight));
		g.setStroke(new BasicStroke());
		lineHeight = 190 / lineHeight;
		g.setTransform(FLIP_X_COORDINATE);
		for (int i = 0; i < numberOfDifferentSolutions.size() - 1; i++) {
			g.setColor(Color.RED);
			g.drawLine((i) * lineLength + 450, 
					numberOfDifferentSolutions.get(
							Math.max(0, i - 1)) * lineHeight + 350, 
							(i + 1) * lineLength + 450, 
							numberOfDifferentSolutions.get(
									i) * lineHeight + 350);
			g.setColor(Color.BLACK);
			g.drawLine((i + 1) * lineLength + 450, 350, 
					(i + 1) * lineLength + 450, 355);
			g.setTransform(normal);
			g.drawString(
					String.valueOf(i + 1), (i + 1) * lineLength + 447, 265);
			g.setTransform(FLIP_X_COORDINATE);
		}
		g.drawLine(450, 350, 750, 350);
		g.drawLine(450, 350, 450, 550);
		
		
		
		// Area 3
		int numberOfMaxPop = 1;
		for (int i = 0; i < maxPopulation.length; i++) {
			numberOfMaxPop *= maxPopulation[i];
		}
		double quotient = (double) startPopulationSize / numberOfMaxPop;
		
		final int middleX = 200;
		final int middleY = 150;
		
		double bigArea = (Math.PI / 4) * 300 * 200;
		double chosenArea = quotient * bigArea;
		
		int width = (int) Math.round(
				(Math.sqrt(chosenArea * 6 / Math.PI)));
		int height = (int) Math.round(width * 2 / 3);
	
		g.fillOval((int)((middleX - 0.5 * width)), 
				(int)((middleY - 0.5 * height)), 
				width, height);

		g.drawOval(50, 50, 300, 200);
		g.setTransform(normal);
		g.drawString(new DecimalFormat("###.####").format(quotient * 100) + 
				"%", 195, 585);
		
		
		
		// Area 4
//		g.setTransform(FLIP_X_COORDINATE);
//		g.drawLine(450, 50, 750, 50);
//		g.drawLine(450, 50, 450, 250);
		
	}
}

