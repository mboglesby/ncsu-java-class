import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


public class Team6GraphWindow extends JPanel implements MouseListener {

	private JFrame graphWindow;
	private JFrame pointWindow;
	private JLabel pointLabel = new JLabel("", SwingConstants.CENTER);
	
	ArraysHolder graphInfo;
	TestingCalculator refCalculator;
	String refExpression;
	
	int xPixStart;
	int xPixRange;
	int yPixStart;
	int yPixRange;
	
	public Team6GraphWindow(String expression, TestingCalculator calculator, ArraysHolder arraysHolder) throws IllegalArgumentException {
		graphWindow = new JFrame(expression + " ; click anywhere for (x,y) value");
		graphWindow.getContentPane().add(this, "Center");
		graphWindow.setSize(800, 600);
		graphWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.addMouseListener(this);
		graphWindow.setVisible(true);
		
		pointWindow = new JFrame();
		pointWindow.getContentPane().add(pointLabel, "Center");
		pointWindow.setSize(100,100);
		pointWindow.setVisible(false);
		
		graphInfo = arraysHolder;
		refCalculator = calculator;
		refExpression = expression;
	}

	public static void main(String[] args) {
		System.out.println("Starting graph window from main method.");
		System.out.println("WARNING: main method for testing purposes only!");
		ArraysHolder test = new ArraysHolder();
		
		double[] aryx = {
				0, 1, 2, 3
		};
		test.xValues = aryx;
		System.out.println("xValues, length = " + test.xValues.length);
		for (double x : test.xValues)
			System.out.println(x);
		
		double[] aryy = {
				0, 1, 4, 9
		};
		test.expressionValues = aryy;
		System.out.println("yValues, length = " + test.expressionValues.length);
		for (double y : test.expressionValues)
			System.out.println(y);
		
		new Team6GraphWindow("x^2", new Team6Grapher(), test);
	}
	
	@Override
	public void paint(Graphics g) {
		// draw x axis
		int xNumInt = (int) (graphInfo.xValues[1] - graphInfo.xValues[0]);
		int xNum = graphInfo.xValues.length;
		int xInt = (graphWindow.getWidth() - 120)/(xNum - 1);
		int xPixRange = xInt*(xNum-1);
		int xPixStart = 80;
		ConcurrentHashMap<Integer,Integer> xPixels = new ConcurrentHashMap<Integer,Integer>();	// array index -> xPos
		int currXPos = xPixStart;
		DecimalFormat formatter = new DecimalFormat("###.##");
		formatter.setRoundingMode(RoundingMode.DOWN);
		for (int i=0; i<graphInfo.xValues.length; i++) {
			Double x = graphInfo.xValues[i];
			String xString = formatter.format(x);
			g.drawString(xString, currXPos, graphWindow.getHeight() - 30);
			g.drawString("|", currXPos, graphWindow.getHeight() - 60);
			xPixels.put(i, currXPos);
			currXPos += xInt;
		}
		
		// determing y range
		double lowY = graphInfo.expressionValues[0];
		double highY = graphInfo.expressionValues[0];
		for (Double y : graphInfo.expressionValues) {
			if (y < lowY)
				lowY = y;
			if (y > highY)
				highY = y;
		}
		
		// determine y interval
		int yNumInt;
		if ((highY - lowY) < 10)
			yNumInt = 1;
		else {
			lowY-=(lowY % 10);
			highY+=(10 - (highY % 10));
			yNumInt = (int) ((highY - lowY) / 5);
			yNumInt+=(10 - (yNumInt % 10));
		}
		
		// draw y axis
		int yNum = (int) (((highY - lowY) / yNumInt) + 2);
		int yInt = (graphWindow.getHeight() - 130)/(yNum - 1);
		int yNumRange = yNumInt*(yNum-1);
		int yPixRange = yInt*(yNum-1);
		int yPixStart = graphWindow.getHeight() - 90;
		int currYPos = yPixStart;
		for (Integer y = (int) lowY; y <= (highY + yNumInt); y+=yNumInt) {
			g.drawString(y.toString(), 10, currYPos);
			g.drawString("__", 40, currYPos);
			currYPos-=yInt;
		}
		
		// compute y pixel values
		ConcurrentHashMap<Integer,Integer> yPixels = new ConcurrentHashMap<Integer,Integer>();
		for (int i=0; i<graphInfo.expressionValues.length; i++) {
			double y = graphInfo.expressionValues[i];
			double yPct = (y-lowY)/yNumRange;
			int yPixel = (int) (yPixStart - (yPct * yPixRange));
			yPixels.put(i, yPixel);
		}
		
		// plot points
		for (int i=0; i<graphInfo.xValues.length; i++) {
			int x = xPixels.get(i);
			int y = yPixels.get(i);
			if (i == graphInfo.xValues.length-1) {
				g.fillOval(x, y, 2, 2);
			}
			else {
				g.fillOval(x, y, 2, 2);
				g.drawLine(x, y, xPixels.get(i+1), yPixels.get(i+1));
			}
		}
		
		this.xPixStart = xPixStart;
		this.xPixRange = xPixRange;
		this.yPixStart = yPixStart;
		this.yPixRange = yPixRange;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent me) {
		if ((me.getX() >= xPixStart) 
				&& (me.getX() <= xPixStart+xPixRange)
				&& (me.getY() <= yPixStart)
				&& (me.getY() >= yPixStart-yPixRange)) {
			double xPct = (double) (me.getX()-xPixStart)/xPixRange;
			double x = xPct*(graphInfo.xValues[graphInfo.xValues.length-1]-graphInfo.xValues[0])+graphInfo.xValues[0];
			double y = Double.parseDouble(refCalculator.evaluate(refExpression, String.valueOf(x)));
			
			DecimalFormat formatter = new DecimalFormat("###.##");
			formatter.setRoundingMode(RoundingMode.DOWN);
			String xString = formatter.format(x);
			String yString = formatter.format(y);
			pointLabel.setText("(" + xString + "," + yString + ")");
			pointWindow.setLocation(me.getX(),me.getY());
			pointWindow.setVisible(true);
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		pointWindow.setVisible(false);
	}

}
