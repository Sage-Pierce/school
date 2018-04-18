package algorithms;

import java.util.HashMap;

public class Flight 
{
   private String airline, flightNumber;
   private Airport source, destination;
   private GMTtime start, end;
   private int weight;
   
   public Flight(String s, HashMap<String, Airport> airports)
   {
      String split[] = s.split("\\s+");

      airline = split[0];
      flightNumber = split[1];
      source = airports.get(split[2]);
      destination = airports.get(split[5]);
      
      int local;
      boolean am;
      
      local = Integer.parseInt(split[3]);
      if (split[4].equals("A"))
         am = true;
      else
         am = false;
      
      start = new GMTtime(local, source.getOffset(), am);
      
      local = Integer.parseInt(split[6]);
      if (split[7].equals("A"))
         am = true;
      else
         am = false;
      
      end = new GMTtime(local, destination.getOffset(), am);
      
      weight = end.minutesSince(start);
   }
   
   public String toString()
   {
      return airline + "-" + flightNumber + " " + source.getAbbr() + " " + start + " " +
             destination.getAbbr() + " " + end +  " (" + (weight / 60) + " hours " +
             (weight % 60) + " minutes)";
   }
   
   public Airport getDest() { return destination; }
   public Airport getSource() { return source; }
   public GMTtime getStart() { return start; }
   public GMTtime getEnd() { return end; }
   public int     getWeight() { return weight; }
}
