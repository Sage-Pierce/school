package algorithms;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class LineGraph extends JPanel
{
   private static final double XRANGE = 1000, YRANGE = 2000;
   private static final double XOFFSET = 500;
   private static final int WIDTH = 500, HEIGHT = 500; // Default width and height

   private ArrayList<Line2D> lines, visibleLines;
   private JFrame paintFrame;
   
   public LineGraph(ArrayList<Line2D> l, String title) 
   {
      setSize(WIDTH, HEIGHT);
      lines = l;
      visibleLines = null;
      
	  paintFrame = new JFrame(title);
	  paintFrame.getContentPane().add(this, java.awt.BorderLayout.CENTER);
	  paintFrame.setLocation(40, 40);
	  paintFrame.pack();
	  paintFrame.setVisible(true);
   }
 
   public void paintComponent(Graphics g) 
   {
      super.paintComponent(g);
      g.translate(WIDTH / 2, HEIGHT / 2);
      drawXYAxes(g);
      drawLines(g);
   }
 
   private void drawXYAxes(Graphics g)
   {
      java.awt.Dimension size = getSize();   // Get the panel's size
      int hBound = size.width /2;            // Use it to set the bounds
      int vBound = size.height / 2;
      int tic = 5;
      g.setColor(java.awt.Color.gray);
      
      g.drawLine(-hBound, 0, hBound, 0);               // Draw X-axis
      for (int k = -hBound; k <= hBound; k+=10) 
         g.drawLine(k, tic, k, -tic);
     
      g.drawLine(0, vBound, 0, -vBound);   // Draw Y-axis
      for (int k = -vBound; k <= vBound; k+=10) 
         g.drawLine(-tic, k, +tic, k);     
   }
   
   private void drawLines(Graphics g)
   {
	  g.setColor(java.awt.Color.black);
      for (Line2D line : lines)
      {
     	 drawLine(g, line);
      }
      
      if (visibleLines != null)
      {
    	 g.setColor(java.awt.Color.blue);
    	 ((Graphics2D)g).setStroke(new BasicStroke(3.0f));
    	 for (Line2D line : visibleLines)
    	 {
    		drawLine(g, line);
    	 }
      }
   }
   
   private void drawLine(Graphics g, Line2D line)
   {
	  double x1, y1, x2, y2;

	  x1 = line.getX1() - XOFFSET;
   	  x1 = (x1 * WIDTH) / XRANGE;
   	  x2 = line.getX2() - XOFFSET;
   	  x2 = (x2 * WIDTH) / XRANGE;
   	  y1 = line.getY1();
   	  y1 = (y1 * HEIGHT) / YRANGE;
   	  y2 = line.getY2();
   	  y2 = (y2 * HEIGHT) / YRANGE;

   	  g.drawLine((int)x1, (int)-y1, (int)x2, (int)-y2);
   	  
  	  if (line instanceof MyLine)
  	  {
  		 g.drawString("" + ((MyLine)line).getID(), (int)x1 + 5, -((int)y1 + 5));
  	  }
   }
   
   public void setVisibleLines(ArrayList<Line2D> vl)
   {
	   if (vl.get(0) instanceof MyLine)
	   {
		  visibleLines = new ArrayList<Line2D>();
		  ((MyLine)vl.get(vl.size() - 1)).computeIntersect(null);
		  Point2D lastPoint = vl.get(0).getP1();
		  
		  for (int i = 0; i < vl.size() - 1; i++)
		  {
	         ((MyLine)vl.get(i)).computeIntersect(((MyLine)vl.get(i + 1)));
	         visibleLines.add(new Line2D.Double(lastPoint, ((MyLine)vl.get(i)).getNextIntersect()));
	         lastPoint = ((MyLine)vl.get(i)).getNextIntersect();
		  }
		  
		  visibleLines.add(new Line2D.Double(lastPoint, vl.get(vl.size() - 1).getP2()));
	   }
	   else
	      visibleLines = vl;
	   
	   repaint();
   }
   
   public void setLocation(int x, int y) { paintFrame.setLocation(x, y); }
   public Dimension getMinimumSize() { return getPreferredSize(); }
   public Dimension getMaximumSize() { return getPreferredSize(); }
   public Dimension getPreferredSize() { return new Dimension(WIDTH, HEIGHT); }
}
