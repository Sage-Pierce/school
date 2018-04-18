package algorithms;

import java.awt.geom.Line2D;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;

public class DivideAndConquerHiddenSurface
{
   private ArrayList<Line2D> linesList;
   private MyLine[] myLines;

   // Don't change the public interfaces!
   public DivideAndConquerHiddenSurface(ArrayList<Line2D> l)
   {
	  linesList = l;
	  myLines = new MyLine[linesList.size()];
	  for (int i = 0; i < linesList.size(); i++)
	  {
		 if (linesList.get(i) instanceof MyLine)
			myLines[i] = (MyLine)linesList.get(i);
		 else
	        myLines[i] = new MyLine(linesList.get(i));
	  }
   }

     // O(nlgn + nlgn + n)
   public ArrayList<Line2D> compute()
   {
	     // Sort by slope and start computation
	  Arrays.sort(myLines, MyLine.SLOPE);
      MyLine[] visibleLines = splitCompute(myLines);
      
         // Must return ArrayList
      ArrayList<Line2D> toReturn = new ArrayList<Line2D>();
      for (MyLine line : visibleLines)
    	  toReturn.add(line);
      
      return toReturn;
   }
   
      // O(nlgn)
   private MyLine[] splitCompute(MyLine[] lines)
   {
	  if (lines.length < 2) // Less than 2 lines
		 return lines;
	  if (lines.length == 2)  // 2 Lines so just compute intersections
	  {
		 lines[0].computeIntersect(lines[1]);
		 lines[1].computeIntersect(null);
		 return lines;
	  }
	  else if (lines.length == 3) // Decide if middle one is visible and compute intersections
	  {
		 if (MyLine.intersection(lines[1], lines[0]).getX() <
		     MyLine.intersection(lines[1], lines[2]).getX())
		 {
			lines[0].computeIntersect(lines[1]);
			lines[1].computeIntersect(lines[2]);
			lines[2].computeIntersect(null);
	        return lines;
		 }
		 else
		 {
			MyLine[] toReturn = new MyLine[] {lines[0], lines[2]};
			toReturn[0].computeIntersect(toReturn[1]);
			toReturn[1].computeIntersect(null);
			return toReturn;
		 }
	  }
	  else // Larger than 3 so divide
	  {
		 MyLine[] left = Arrays.copyOfRange(lines, 0, (int)Math.ceil((double)lines.length / 2.0));
		 MyLine[] right = Arrays.copyOfRange(lines, left.length, lines.length);
		 
		 MyLine[] visibleLeft = splitCompute(left);
		 MyLine[] visibleRight = splitCompute(right);
		 
		 return merge(visibleLeft, visibleRight);
	  }
   }
   
   private MyLine[] merge(MyLine[] left, MyLine[] right)
   {
	     // Merge left and right in to single list and sort by intersections
	  MyLine[] interSorted = new MyLine[left.length + right.length];
	  System.arraycopy(left, 0, interSorted, 0, left.length);
	  System.arraycopy(right, 0, interSorted, left.length, right.length);
	  Arrays.sort(interSorted, MyLine.INTERSECTIONS);

	     // Find the indices at which it is first true the right subproblem is
	     // greater than the left subproblem
	  int leftIdx = 0, rightIdx = 0;
	  for (int i = 0; i < interSorted.length - 2; i++)
	  {
		 if (right[rightIdx].evaluate(interSorted[i].getNextIntersect().getX()) >
		     left[leftIdx].evaluate(interSorted[i].getNextIntersect().getX()))
            break;
			 
		 if (interSorted[i] == left[leftIdx]) 
			leftIdx++;
		 else   // (interSorted[i] == right[rightIdx])
			rightIdx++;
	  }
	  
	     // Compute the visible intersection and combine visible lines
	  left[leftIdx].computeIntersect(right[rightIdx]);
	  MyLine[] visibleLines = new MyLine[leftIdx + 1 + right.length - rightIdx];
	  System.arraycopy(left, 0, visibleLines, 0, leftIdx + 1);
	  System.arraycopy(right, rightIdx, visibleLines, leftIdx + 1, right.length - rightIdx);
	  return visibleLines;
   }
}