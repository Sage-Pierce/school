package algorithms;

import java.lang.Integer;
import java.util.ArrayList;
import java.util.HashMap;

public class Airport 
{
   private ArrayList<Flight> flightPlan;
   private GMTtime readyTime;
   private HashMap<Airport, FlightList> adjList;
   private int GMToffset, weight;
   private String abbreviation;
   
   public Airport(String s) throws Exception
   {
      adjList = new HashMap<Airport, FlightList>();
      
      String split[] = s.split("\\s+");
      abbreviation = split[0];
      GMToffset = Integer.parseInt(split[1]);
      
      readyTime = new GMTtime(0, GMToffset, true);
   }
   
   public void addFlight(Flight flight)
   {
      FlightList list = adjList.get(flight.getDest());
      if (list == null)
      {
         list = new FlightList(flight.getDest());
         adjList.put(flight.getDest(), list);
      }
      
      list.addFlight(flight);
   }
   
   public void setFlightPlan(ArrayList<Flight> prevPlan, Flight newFlight)
   {
      flightPlan = new ArrayList<Flight>();
      for (int i = 0; i < prevPlan.size(); i++)
      {
         flightPlan.add(prevPlan.get(i));
      }
      
      flightPlan.add(newFlight);
   }
   
   public String stringPlan()
   {
      String string = "";
      
      for (int i = 0; i < flightPlan.size(); i++)
         string = string + flightPlan.get(i) + "\n";
      
      return string;
   }
   
   public String toString()
   {
      return abbreviation + " " + GMToffset;
   }

   public void reset()
   {
      weight = Integer.MAX_VALUE;
      flightPlan = new ArrayList<Flight>();
   }
   
   public void setReadyTime(GMTtime time) { readyTime = time; }
   public void setWeight(int w) { weight = w; }
   
   public ArrayList<Flight>            getFlightPlan() { return flightPlan; }
   public GMTtime                      getReadyTime() { return readyTime; }
   public HashMap<Airport, FlightList> getAdjList() { return adjList; }
   public int                          getOffset() { return GMToffset; }
   public int                          getWeight() { return weight; }
   public String                       getAbbr() { return abbreviation; }
}
