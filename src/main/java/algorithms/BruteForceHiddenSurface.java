package algorithms;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;

public class BruteForceHiddenSurface
{
   private ArrayList<Line2D> lines;
   private MyLine[] myLines;

   // Don't change the public interfaces!
   public BruteForceHiddenSurface(ArrayList<Line2D> l)
   {
      lines = l;
      myLines = new MyLine[lines.size()];
      for (int i = 0; i < lines.size(); i++)
      {
    	 myLines[i] = new MyLine(lines.get(i));
      }
   }

   public ArrayList<Line2D> compute()
   {	   
	   ArrayList<Line2D> visibles = new ArrayList<Line2D>();

	      // Do we actually need to do anything?
	   if (lines.size() < 3)
		  return lines;
	   
	      // Start with line that has the greatest y-intercept
       MyLine currentLine = findGreatestYIntercept();
	   double lastX = -Double.MAX_VALUE;
	   
	      // Do the following over and over until we find no more lines that
	      // intersect to the right of the current line O(n(n + nlgn + lgn))
	   while (currentLine != null)
	   {
		     // Add this obviously visible line
		  visibles.add(currentLine);
		  
		     // Calculate intersections with all other lines
	      for (MyLine line : myLines)
	         line.computeIntersect(currentLine);
	         
	         // Sort the array by X-Intersects
	      Arrays.sort(myLines, MyLine.INTERSECTIONS);
	      
	         // Find the next intersection to the right of current line
	      currentLine = findNextIntersectingLine(lastX, 0, myLines.length - 2);
	      
	         // If we find a next line, update next x-coordinate to search from
	      if (currentLine != null)
	    	 lastX = currentLine.getNextIntersect().getX();
	   }
		  
       return visibles;
   }
   
   private MyLine findGreatestYIntercept()
   {
	  MyLine leastLine = myLines[0];

	  double leastY = leastLine.getY1();
	  for (int i = 1; i < myLines.length; i++)
	  {
		 if (myLines[i].getY1() > leastY)
		 {
			 leastLine = myLines[i];
			 leastY = leastLine.getY1();
		 }
	  }
	  
	  return leastLine;
   }
   
      // Recursively find next intersection to the right of current x-value
   private MyLine findNextIntersectingLine(double xValue, int start, int end)
   {
	  int difference = end - start;
	  if (difference <= 1)
	  {
		 if (myLines[start].getNextIntersect().getX() > xValue)
			return myLines[start];
		 else if (myLines[end].getNextIntersect().getX() > xValue)
			return myLines[end];
		 else
			return null;
	  }
	  else
	  {
	     int middle = (start + end) / 2;
	     if (myLines[middle].getNextIntersect().getX() > xValue)
		    return findNextIntersectingLine(xValue, start, middle);
	     else
		    return findNextIntersectingLine(xValue, middle + 1, end);
	  }
   }
}