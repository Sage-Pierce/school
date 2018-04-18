package distributed;

import java.io.Serializable;
import java.lang.Integer;
import java.util.TreeMap;

public class Theater implements Serializable
{
   private TreeMap<String, Integer> people;
   private boolean seats[];
   
   public Theater(int size)
   {
      seats = new boolean[size];
      
      for (int i = 0; i < seats.length; i++)
         seats[i] = true;
      
      people = new TreeMap<String,Integer>();
   }
   
   private synchronized int getSeat()
   {
      int i = 0;
      
      while (!seats[i] && i < seats.length)
         i++;
      
      if (i == seats.length)
         return 0;
      else
         return i + 1;
   }
   
   public synchronized int reserve(String name)
   {
      return bookSeat(name, getSeat());
   }
   
   public synchronized int bookSeat(String name, int seat)
   {
	   Integer exists = people.get(name);
	   if (exists != null) // Name already exists
		   return -1;
	   
	   // Check if valid seat number and available
      if ((seat <= 0) || (seat > seats.length) || !seats[seat - 1])
         return 0;

      seats[seat - 1] = false;
      people.put(name, new Integer(seat));
      return seat;
   }
   
   public synchronized int search(String name)
   {
      Integer seat = people.get(name);
      
      if (seat == null) // Doesn't exist
         return 0;
      else
         return seat.intValue();
   }
   
   public synchronized int delete(String name)
   {
      int seat = search(name);
      
      if (seat == 0) // Doesn't exist
         return 0;
      else
      {
         people.remove(name);
         seats[seat - 1] = true;
         return seat;
      }
   }
}
