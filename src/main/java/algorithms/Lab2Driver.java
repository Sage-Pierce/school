package algorithms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.geom.Line2D;

import javax.swing.JFileChooser;

public class Lab2Driver 
{
   public static void main(String[] args)
   {
      final ArrayList<Line2D> lines = parseLinesFile();
	  LineGraph bfhsGraph = new LineGraph(lines, "Brute Force Results");
	  LineGraph dachsGraph = new LineGraph(lines, "Divide and Conquer Results");
      
      BruteForceHiddenSurface bfhs = new BruteForceHiddenSurface(lines);
      ArrayList<Line2D> bfhsVisibleLines = bfhs.compute();
      DivideAndConquerHiddenSurface dachs = new DivideAndConquerHiddenSurface(lines);
      ArrayList<Line2D> dachsVisibleLines = dachs.compute();

      System.out.println("Brute Force: ");
      for (Line2D line : bfhsVisibleLines)
    	 System.out.println(line);
      
      System.out.println("\nDivide and Conquer: ");
      for (Line2D line : dachsVisibleLines)
    	 System.out.println(line);

	  bfhsGraph.setVisibleLines(bfhsVisibleLines);
	  dachsGraph.setVisibleLines(dachsVisibleLines);
	  dachsGraph.setLocation(550, 40);
   }

   private static ArrayList<Line2D> parseLinesFile()
   {
      BufferedReader r = null;
      ArrayList<Line2D> toReturn = new ArrayList<Line2D>();
      
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setDialogTitle("Open Lines Data File");
      fileChooser.setSelectedFile(new File("lines.txt"));
      fileChooser.showOpenDialog(null);
      File file = fileChooser.getSelectedFile();

      try
      {
         InputStream is = new FileInputStream(file);
         r = new BufferedReader(new InputStreamReader(is));

         String nextline = r.readLine();
         String[] ys = null;
         while(nextline != null)
         {
	        ys = nextline.split(" ");
	        toReturn.add(new Line2D.Double(0,    java.lang.Double.parseDouble(ys[0]), 
                                           1000, java.lang.Double.parseDouble(ys[1])));
            nextline = r.readLine();
         }
         
         r.close();
      } catch (IOException e)
      {
         System.out.println("IOException while opening/reading from " + file);
         System.exit(0);
      }
      
      return toReturn;
   }
}

