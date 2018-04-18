package algorithms;

public class FlightList 
{
   private Airport destination;
   private MyHeap<GMTtime, Flight> flights;
   
   public FlightList(Airport dest)
   {
      destination = dest;
      flights = new MyHeap<GMTtime, Flight>();
   }
   
   public FlightList()
   {
      this(null);
   }
   
   public void addFlight(Flight flight)
   {
      if (flights.getSize() == flights.getCapacity())
         flights.ensureCapacity(flights.getCapacity() * 2);
         
      flights.put(flight.getStart(), flight);
   }
   
   public Flight getNextFlight(GMTtime time)
   {
      MyHeap<GMTtime, Flight> copy = new MyHeap<GMTtime, Flight>(flights);
      
      while (!copy.isEmpty())
      {
         Flight flight = copy.extractMin();
         if (flight.getStart().after(time))
            return flight;
      }
      
      return flights.min();
   }
   
   public Airport getDest() { return destination; }
   public MyHeap<GMTtime, Flight> getFlights() { return flights; }
}
