package qos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.List;
import javax.swing.JPanel;

public class AntAlgorithmVisualizationPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Double> optUtilityPerIteration;
	
	private int lineLength = 0;
	private int lineUnit = 1;

	public AntAlgorithmVisualizationPanel(
			List<Double> optUtilityPerIteration) {
		this.optUtilityPerIteration = optUtilityPerIteration;
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
		g.drawString("Development of the optimal", 
				70, 25);
		g.drawString("Composition", 
				135, 50);		
		g.setFont(font2);
		// Main legend for all axes
		g.drawString("Iterations", 165, 530);		
		g.setTransform(rotated);
		g.drawString("Utility Value", -335, 20);		
		g.setTransform(normal);		
		// NUMBER OF DIFFERENT SOLUTIONS PER GENERATION
		g.setColor(Color.BLACK);
		g.setFont(font);
		g.setTransform(FLIP_X_COORDINATE);
		// Drawing of the coordinate system
				g.drawLine(50, 25, 350, 25);
				g.drawLine(50, 25, 50, 425);		
		// Height of the y-axis		
		lineHeight = 20;
		// Legend for y-axis
		for (int i = 0; i < 100; i+=5) {
			g.setTransform(FLIP_X_COORDINATE);
			g.drawLine(53, 25 + lineHeight * (i/5), 47, 25 + lineHeight * (i/5));			
			g.setTransform(normal);
			if (i==5) {
				g.drawString("0.0" + i, 25, 494 - lineHeight * (i/5));
			} else {
				g.drawString("0." + i, 25, 494 - lineHeight * (i/5));
			}						
		}
		// Manual insertion of the last index of the y-axis
		g.setTransform(FLIP_X_COORDINATE);
		g.drawLine(53, 25 + lineHeight * 20, 47, 25 + lineHeight * 20);
		g.setTransform(normal);
		g.drawString("1.0", 25, 494 - lineHeight * 20);					
		g.setTransform(FLIP_X_COORDINATE);
		// Drawing of the graph and the legend for x-axis
		for (int i = 0; i <= maxXCoordinate; i += lineUnit) {
			// Graph
			if (i < optUtilityPerIteration.size() - 1 - lineUnit) {
				g.setColor(Color.RED);
				g.drawLine((i /lineUnit) * lineLength + 50, 
						(int) Math.round(optUtilityPerIteration.get(i) * 
								400.0) + 25, (i /lineUnit + 1) * 
								lineLength + 50, 
								(int) Math.round(optUtilityPerIteration.
										get(i + lineUnit) * 
										400.0) + 25);
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
	}
	
	private int determineLineLengthProperties() {
		if ((optUtilityPerIteration.size() - 1) <= 10) {
			lineLength = 10;
			lineUnit = 1;
			return 10;
		}
		else if ((optUtilityPerIteration.size() - 1) <= 20) {
			lineLength = 20;
			lineUnit = 1;
			return 20;
		}
		else if ((optUtilityPerIteration.size() - 1) <= 30) {
			lineLength = 30;
			lineUnit = 1;
			return 30;
		}
		else if ((optUtilityPerIteration.size() - 1) <= 50) {
			lineLength = 50;
			lineUnit = 1;
			return 50;
		}
		else if ((optUtilityPerIteration.size() - 1) <= 75) {
			lineLength = 75;
			lineUnit = 1;
			return 75;
		}
		else if ((optUtilityPerIteration.size() - 1) <= 100) {
			lineLength = 100;
			lineUnit = 1;
			return 100;
		}
		else if ((optUtilityPerIteration.size() - 1) <= 150) {
			lineLength = 30;
			lineUnit = 5;
			return 150;
		}
		else if ((optUtilityPerIteration.size() - 1) <= 250) {
			lineLength = 50;
			lineUnit = 5;
			return 250;
		}
		else if ((optUtilityPerIteration.size() - 1) <= 500) {
			lineLength = 50;
			lineUnit = 10;
			return 500;
		}
		else if ((optUtilityPerIteration.size() - 1) <= 750) {
			lineLength = 75;
			lineUnit = 10;
			return 750;
		}
		else if ((optUtilityPerIteration.size() - 1) <= 1000) {
			lineLength = 100;
			lineUnit = 10;
			return 1000;
		}
		else if ((optUtilityPerIteration.size() - 1) <= 1500) {
			lineLength = 150;
			lineUnit = 10;
			return 1500;
		}
		else if ((optUtilityPerIteration.size() - 1) <= 3000) {
			lineLength = 30;
			lineUnit = 100;
			return 3000;
		}
		else if ((optUtilityPerIteration.size() - 1) <= 5000) {
			lineLength = 50;
			lineUnit = 100;
			return 5000;
		}
		else if ((optUtilityPerIteration.size() - 1) <= 7500) {
			lineLength = 75;
			lineUnit = 100;
			return 7500;
		}
		else if ((optUtilityPerIteration.size() - 1) <= 10000) {
			lineLength = 100;
			lineUnit = 100;
			return 10000;
		}
		else if ((optUtilityPerIteration.size() - 1) <= 15000) {
			lineLength = 150;
			lineUnit = 10;
			return 15000;
		}
		else {
			return 0;
		}
	}	
}
