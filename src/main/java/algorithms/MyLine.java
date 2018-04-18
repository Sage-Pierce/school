package algorithms;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Comparator;

@SuppressWarnings("serial")
public class MyLine extends Line2D.Double
{
	  // Allows sorting of MyLines by non-decreasing slope
   public static Comparator<MyLine> SLOPE = new Comparator<MyLine>(){
      public int compare(MyLine l1, MyLine l2)
      {
         double diff = l1.getSlope() - l2.getSlope();
         if (diff > 0.0)
            return 1;
         else if (diff < 0.0)
            return -1;
         else
            return 0;
      }
      public boolean equals(Object obj) { return false; }
   };

      // Allows sorting of MyLines in order of the x-components of their
      // intersections with the next line (nulls at end)
   public static Comparator<MyLine> INTERSECTIONS = new Comparator<MyLine>(){
      public int compare(MyLine l1, MyLine l2)
      {
         if (l2.getNextIntersect() == null)
            return -1;
         else if (l1.getNextIntersect() == null)
            return 1;
         else
         {
            double diff = l1.getNextIntersect().getX() -
                          l2.getNextIntersect().getX();
            if (diff > 0.0)
               return 1;
            else if (diff < 0.0)
               return -1;
            else
               return 0;
         } 
      }
      public boolean equals(Object obj) { return false; }
   };

      // Private variables
   private static int lineCount = 0;
   private double slope, yIntercept;
   private int lineID;
   private Point2D nextIntersect;

      // Constructors - compute slope, Y-Intercept, and initialize other vars
   public MyLine(Line2D line) { this(line.getX1(), line.getY1(), line.getX2(), line.getY2()); }
   public MyLine(double x1, double y1, double x2, double y2)
   {
      super(x1, y1, x2, y2);

      slope = (y2 - y1) / (x2 - x1);
      yIntercept = y1 - (slope * x1);
      nextIntersect = null;
      lineID = lineCount++;
   }
   
      // Set intersection point with another line
   public Point2D computeIntersect(MyLine anotherLine)
   {
	  if ((anotherLine != null) && (this != anotherLine))
	     nextIntersect = intersection(this, anotherLine);
	  else
		 nextIntersect = null;
	  
	  return nextIntersect;
   }

      // Compute determinate
   private static double det(double a, double b, double c, double d) { return a * d - b * c; }
   
      // Compute and intersection of two MyLines
   public static Point2D intersection(MyLine firstLine, MyLine secondLine)
   {
	  Point2D.Double intersection = new Point2D.Double();
      
	  if(!firstLine.intersectsLine(secondLine))
         return null;

      double x1 = firstLine.getX1(), x2 = firstLine.getX2(), x3 = secondLine.getX1(), x4 = secondLine.getX2(),
             y1 = firstLine.getY1(), y2 = firstLine.getY2(), y3 = secondLine.getY1(), y4 = secondLine.getY2();

      intersection.x = det(det(x1, y1, x2, y2), x1 - x2, det(x3, y3, x4, y4), x3 - x4) /
                       det(x1 - x2, y1 - y2, x3 - x4, y3 - y4);

      intersection.y = det(det(x1, y1, x2, y2), y1 - y2, det(x3, y3, x4, y4), y3 - y4) /
                       det(x1 - x2, y1 - y2, x3 - x4, y3 - y4);
	  
      return intersection;
   }
   
   /************************OVERRIDDEN AND ACCESSOR METHODS*************************/
   public double evaluate(double x) { return (x * slope) + yIntercept; }
   public double getSlope() { return slope; }
   public double getYIntercept() { return yIntercept; }
   public int getID() { return lineID; }
   public Point2D getNextIntersect() { return nextIntersect; }
   public String toString() { return getX1() + ", " + getY1() + ", " + getX2() + ", " + getY2(); }
}
