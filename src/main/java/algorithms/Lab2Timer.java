package algorithms;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.awt.geom.Line2D;

public class Lab2Timer 
{
   public static void main(String[] args)
   {
      int runs = 20; // how many runs do you want to average for a given n?
      int n = 1800; // change me!
      double bfAvg = 0.0, dacAvg = 0.0;
      Date startTime = null;
      Date endTime = null;

      long bfDelay, dacDelay;
      for(int r = 0; r<runs; r++)
      {
         final ArrayList<Line2D> lines = generateLines(n);
         
         BruteForceHiddenSurface bfhs = new BruteForceHiddenSurface(lines);
         startTime = new Date();
         ArrayList<Line2D> bfhsVisibleLines = bfhs.compute();
         endTime = new Date();
         bfDelay = endTime.getTime() - startTime.getTime();
         System.out.println("Time " + r + " for BF (ms) : " + bfDelay);
         bfAvg += bfDelay;
         
         DivideAndConquerHiddenSurface dachs = new DivideAndConquerHiddenSurface(lines);
         startTime = new Date();
         ArrayList<Line2D> dachsVisibleLines = dachs.compute();
         endTime = new Date();
         dacDelay = endTime.getTime() - startTime.getTime();
         System.out.println("Time " + r + " for DAC (ms): " + dacDelay);
         dacAvg += dacDelay;
         
         if (bfhsVisibleLines.size() != dachsVisibleLines.size())
        	System.err.println("ERROR: RETURNED LINES DO NOT MATCH");
         
   	     LineGraph bfhsGraph = new LineGraph(lines, "Brute Force Results");
   	     LineGraph dachsGraph = new LineGraph(lines, "Divide and Conquer Results");
   	     bfhsGraph.setVisibleLines(bfhsVisibleLines);
   	     dachsGraph.setVisibleLines(dachsVisibleLines);
   	     dachsGraph.setLocation(550, 40);
      }
      
      bfAvg /= runs;
      dacAvg /= runs;
      System.out.println("--- Averages for " + runs + " runs at " + n + " lines ---");
      System.out.println("Brute Force: " + bfAvg + " ms");
      System.out.println("Divide and Conquer: " + dacAvg + " ms");
   }

   private static ArrayList<Line2D> generateLines(int n)
   {
      Random rand = new Random();
      ArrayList<Line2D> lines = new ArrayList<Line2D>();
      int i = 0;

      while (i < n)
      {
         Line2D.Double newLine = new Line2D.Double(0,    rand.nextDouble()*2000-1000, 
                                                   1000, rand.nextDouble()*2000-1000);

         boolean intersects = true;
         for(int j = 0; j<lines.size(); j++)
         {
	        if(!lines.get(j).intersectsLine(newLine))
	        {
               intersects = false;
               break;
	        }
         }
    
         if(intersects)
         {
            lines.add(newLine);
            i++;
         }
      }

      return lines;
   }
}