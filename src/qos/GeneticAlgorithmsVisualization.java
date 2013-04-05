package qos;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;

public class GeneticAlgorithmsVisualization extends JFrame{

	private static final long serialVersionUID = 1L;
	int[] chosenPopulation;
	int[] maxPopulation;

	public GeneticAlgorithmsVisualization(int[] maxPopulation,
			int[] chosenPopulation) {
		super("Result Visualization");
		this.chosenPopulation = chosenPopulation;
		this.maxPopulation = maxPopulation;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(400, 300);
		setVisible(true);
		setResizable(false);
		setAlwaysOnTop(true);
		setBounds(50, 50, 400, 300);
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
		g.drawString("Service Classes", 155, 288);
		g.setTransform(rotated);
		g.drawString("Number of Service Candidates", -240, 20);
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
					new int[]{50, chosenPopulation[Math.max(0, i - 1)] * 
					lineHeight + 50,
					chosenPopulation[i] * lineHeight + 50, 50}, 4);
			g.setColor(Color.BLACK);
			g.setStroke(dashed);
			g.drawLine(i * lineLength + 50, 
					maxPopulation[Math.max(0, i - 1)] * lineHeight + 50, 
					(i + 1) * lineLength + 50, 
					maxPopulation[i] * lineHeight + 50);
			g.setStroke(new BasicStroke());
			g.setTransform(normal);
			g.drawString(printClass + (i + 1), i * lineLength + 65, 265);
			g.setTransform(FLIP_X_COORDINATE);
			g.drawLine((i + 1) * lineLength + 50, 50, 
					(i + 1) * lineLength + 50, 55);
		}
		g.drawLine(50, 50, 350, 50);
		g.drawLine(50, 50, 50, 250);
	}
}

