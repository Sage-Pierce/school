package algorithms;

public class GMTtime implements Comparable<GMTtime>
{
   public boolean am;              //true iff AM
   public int localTime;           //local time as represented in flight-data.txt (hhmm)
   public int offset;              //needed to adjust local time to gmtTime
   private int localMilitaryTime;  //local time in 24 hour clock
   private int localHours;         //hour in local time (on a 12 hour clock)
   private int minutes;            //minutes (between 0 and 59)
   private int gmtTime;            //GMT time on a 24 hour clock in hhmm form
   private int gmtInMinutes;       //GMT time converted into minutes (hour * 60 + minutes)

   // Constructor
   //   locateTime is in the form hhmm given by flight-data.txt
   //   city is the Airport for the time (from which the GMT conv can be found)
   //   am is true if and only if the localTime is AM (as opposed to PM)
   //
   public GMTtime(int lt, int gmtOffset, boolean a)
   {
      localTime = lt;
      offset = gmtOffset;
      am = a;
      minutes = localTime % 100;                 //minutes not affected by time zone
      localHours = (localTime - minutes) / 100;  //hours in local time

      localMilitaryTime = localTime;             //initialize military time to local time
      if (!am && localHours != 12)               //  to convert 1-11PM into 24 hour clock
        localMilitaryTime += 1200;               //     add 12 hours
      else if (am && localHours == 12)           //  to convert 12AM into 24 hour clock
        localMilitaryTime -= 1200;               //     subtract 12 hours

      gmtTime = localMilitaryTime - offset;    
      int hours = (gmtTime - minutes) / 100;
      gmtInMinutes = (hours * 60) + minutes;
   }
   
   public GMTtime(GMTtime time) { this (time.localTime, time.offset, time.am); }
   
   public void add(int numMinutes)
   {
      int hrs = (numMinutes / 60);
      int mins = numMinutes % 60;
      
      mins = minutes + mins;
      if (mins >= 60)
      {
         hrs = hrs + (mins / 60);
         mins = mins % 60;
      }
      
      localMilitaryTime = (((localMilitaryTime / 100) + hrs) % 24) * 100;
      localMilitaryTime = localMilitaryTime + mins;
      
      localHours = (localMilitaryTime % 1200) / 100; 
      minutes = mins;
      localTime = (localHours * 100) + minutes;
      if (localMilitaryTime >= 1200)
         am = false;
      else
         am = true;
      
      gmtTime = localMilitaryTime - offset;
      int hours = (gmtTime - minutes) / 100;
      gmtInMinutes = (hours * 60) + minutes;
   }
   
   public int compareTo(GMTtime time)
   {
      return gmtInMinutes - time.gmtInMinutes;
   }

   public boolean after(GMTtime time)
   {
      if (gmtInMinutes >= time.gmtInMinutes)
         return true;
      else
         return false;
   }
   
   public int minutesSince(GMTtime time)
   {
     int duration = gmtInMinutes - time.gmtInMinutes;
     if (duration < 0)
       duration += 1440;
     
     return duration;
   }
   
   public String toString()
   {
     String out = localTime + " ";
     if (am)
       out += "AM";
     else
       out += "PM";
     
     return out;
   }
 }
