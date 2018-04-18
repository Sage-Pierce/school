package algorithms;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class Driver extends JFrame implements ActionListener
{
   private static final Font BIG_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 14);
   private static final Font SMALL_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);
   private static final int CHECK_IN_DELAY = 120; // minutes
   private static final int LAYOVER_DELAY = 60; // minutes
   private static final String UNSELECTED = "Pick...";
   private static final String AM = "AM";
   private static final String PM = "PM";
   
   private HashMap<String, Airport> airports;
   private JComboBox source, destination;
   private JButton search;
   private JPanel content;
   private JTextArea itenerary;
   private TimeSelector startTime, stayTime; 
   
   public Driver()
   {
         // Initialization of desired variables
      super("Flight Planner");
      setResizable(false);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLocation(30, 30);
      UIManager.put("Label.font", BIG_FONT);
      airports = new HashMap<String, Airport>();
      
         // This is the panel where the user will input info
      JPanel inputPanel = new JPanel();
      inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
      
         // Source airport
      JPanel sourcePanel = new JPanel();
      sourcePanel.setLayout(new BoxLayout(sourcePanel, BoxLayout.X_AXIS));
      source = new JComboBox(new String[]{UNSELECTED});
      sourcePanel.add(new JLabel("Starting airport:           "));
      sourcePanel.add(source);
      sourcePanel.add(Box.createHorizontalGlue());
      inputPanel.add(sourcePanel);
      inputPanel.add(Box.createVerticalStrut(5));
      
         // Source airport arrival time
      JPanel startPanel = new JPanel();
      startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.X_AXIS));
      startTime = new TimeSelector(false);
      startPanel.add( new JLabel("Start airport arrival time: "));
      startPanel.add(startTime);
      startPanel.add(Box.createHorizontalGlue());
      inputPanel.add(startPanel);
      inputPanel.add(Box.createVerticalStrut(5));
      
         // Destination airport
      JPanel destinPanel = new JPanel();
      destinPanel.setLayout(new BoxLayout(destinPanel, BoxLayout.X_AXIS));
      destination = new JComboBox(new String[]{UNSELECTED});
      destinPanel.add(new JLabel("Destination airport:        "));
      destinPanel.add(destination);
      destinPanel.add(Box.createHorizontalGlue());
      inputPanel.add(destinPanel);
      inputPanel.add(Box.createVerticalStrut(10));
      
         // How long do you want to stay?
      JPanel stayPanel = new JPanel(new BorderLayout());
      stayTime = new TimeSelector(true);
      stayPanel.add(new JLabel("How long do you want to stay?", JLabel.LEFT), BorderLayout.NORTH);
      stayPanel.add(stayTime, BorderLayout.WEST);
      inputPanel.add(stayPanel);
      inputPanel.add(Box.createVerticalStrut(15));
      inputPanel.add(Box.createVerticalGlue());
      
         // Search Button
      search = new JButton("Search");
      search.addActionListener(this);
      inputPanel.add(search);
      inputPanel.add(Box.createVerticalStrut(10));
      
         // Travel info will be displayed here
      itenerary = new JTextArea(17, 61);
      itenerary.setFont(SMALL_FONT);
      itenerary.setEditable(false);
      itenerary.setLineWrap(true);
      itenerary.setText("Use input parameters above to make itenerary...");
      JScrollPane itenPane = new JScrollPane(itenerary);
      itenPane.setBorder(BorderFactory.createTitledBorder("Itenerary: "));
      
         // Crunch everything to the left
      JPanel northPanel = new JPanel(new BorderLayout());
      northPanel.add(inputPanel, BorderLayout.WEST);
      
         // Add all content to appropriate areas
      content = new JPanel(new BorderLayout());
      content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      content.add(northPanel, BorderLayout.NORTH);
      content.add(itenPane, BorderLayout.CENTER);
      
         // Display GUI and wait for data file input
      getContentPane().add(content);
      pack();
      enableComponents(content, false);
      setVisible(true);
      processFiles();
      
         // Make a list of all possible airports
      String apList[] = new String[airports.size()];
      int i = 0;
      for (Airport ap : airports.values())
      {
         apList[i] = ap.getAbbr();
         i++;
      }

         // Alphabetically sort and add airports to comboBox list
      Arrays.sort(apList);
      for (int j = 0; j < apList.length; j++)
      {
         source.addItem(apList[j]);
         destination.addItem(apList[j]);
      }
      source.removeItem(UNSELECTED);
      destination.removeItem(UNSELECTED);
      
         // Enable GUI
      enableComponents(content, true);
   }
   
      // Search button pushed
   public void actionPerformed(ActionEvent e)
   {
         // Make sure source and destination airports are not the same
      if (source.getSelectedItem().equals(destination.getSelectedItem()))
      {
         JOptionPane.showMessageDialog(this, "Start and Destination airports may NOT be the same.", 
                                       "Error", JOptionPane.ERROR_MESSAGE);
         return;
      }
      
         // Gather desired flight info
      Airport sourceAirport = airports.get((String)source.getSelectedItem());
      Airport destinationAirport = airports.get((String)destination.getSelectedItem());
      int start = (startTime.getHour() * 100) + startTime.getMinute();
      GMTtime gmtStart = new GMTtime(start, sourceAirport.getOffset(), startTime.isAM());

         // Run Dijkstra's algorithm from source airport
      dijkstra(sourceAirport, gmtStart, destinationAirport);
      
         // Can we actually get there?
      String plan = "";
      if (destinationAirport.getWeight() < Integer.MAX_VALUE)
      {
         String outgoingPlan = destinationAirport.stringPlan();
         int outgoingTime = destinationAirport.getWeight() - LAYOVER_DELAY;
         plan = plan + "Outgoing flight plan: " + sourceAirport.getAbbr() + " to "
                                                + destinationAirport.getAbbr() + "\n";
         plan = plan + "Total transit time: " + (outgoingTime / 60) + " Hours " +
                                                (outgoingTime % 60) + " Minutes*\n";
         plan = plan + "Flight(s): \n";
         plan = plan + outgoingPlan;
         plan = plan + "----------------------------------------------\n";
      }
      else
      {
         plan = plan + "Sorry! Unfortunately, it looks like it is impossible\n";
         plan = plan + "to get from " + sourceAirport.getAbbr() + " to " + destinationAirport.getAbbr();
         itenerary.setText(plan);
         return;
      }
      
         // Compute the return airport minimum arrival time
      GMTtime gmtReturn = destinationAirport.getReadyTime();
      gmtReturn.add(-LAYOVER_DELAY);
      gmtReturn.add((60 * stayTime.getHour()) + stayTime.getMinute());
      
         // Run Dijkstra's algorithm from destination airport
      dijkstra(destinationAirport, gmtReturn, sourceAirport);
      
         // Can we actually get back?
      if (sourceAirport.getWeight() < Integer.MAX_VALUE)
      {
         String returnPlan = sourceAirport.stringPlan();
         GMTtime latestReturn = new GMTtime(sourceAirport.getFlightPlan().get(0).getStart());
         latestReturn.add(-CHECK_IN_DELAY);
         int returnTime = sourceAirport.getWeight() - latestReturn.minutesSince(gmtReturn) - LAYOVER_DELAY;
         plan = plan + "Returning flight plan: " + destinationAirport.getAbbr() + " to "
                                                 + sourceAirport.getAbbr() + "\n";
         plan = plan + "Time you'll be ready to get to " + destinationAirport.getAbbr() + ": " + gmtReturn + "\n";
         plan = plan + "Latest time to get to " + destinationAirport.getAbbr() + ": " + latestReturn + "\n";
         plan = plan + "Minimum total transit time: " + (returnTime / 60) + " Hours " +
                                                        (returnTime % 60) + " Minutes*\n";
         plan = plan + "Flight(s): \n";
         plan = plan + returnPlan + "\n";
      }
      else
      {
         plan = plan + "Sorry! Unfortunately, it looks like it is impossible\n";
         plan = plan + "to get back from " + destinationAirport.getAbbr() + " to " + sourceAirport.getAbbr() + "\n\n";
      }

      plan = plan + "*NOTE: Transit times include 2 hour security/bag check buffer\n";
      plan = plan + "       and 1 hour layover buffer for connecting flights.";
      itenerary.setText(plan);
   }
   
   public void dijkstra(Airport source, GMTtime startTime, Airport destination)
   {
      GMTtime readyTime = new GMTtime(startTime);
      readyTime.add(CHECK_IN_DELAY);
      int startingWeight = CHECK_IN_DELAY;
      MyHeap<Integer, Airport> heap = new MyHeap<Integer, Airport>(airports.size());
      
         // First, reset all Airports
      for (Airport airport : airports.values())
         airport.reset();
      
         // Set starting airport variables
      source.setReadyTime(readyTime);
      source.setWeight(startingWeight);
      heap.put(new Integer(source.getWeight()), source);
      Airport airport = null;
      
         // Keep going till heap is empty or we reach destination
      while (!heap.isEmpty() && (airport != destination))
      {
         airport = heap.extractMin();
         readyTime = airport.getReadyTime();
         
            // Iterate through adjacency list
         for (FlightList list : airport.getAdjList().values())
         {
               // Find next flight out of here to next airport
            Flight flight = list.getNextFlight(readyTime);
            Airport endPoint = list.getDest();
            
               // Destination weight = (current airport weight) + (time before take off) +
               //                      (flight time) + (necessary lay over delay)
            int newWeight = airport.getWeight() + flight.getStart().minutesSince(readyTime) + 
                            flight.getWeight() + LAYOVER_DELAY;
            
            if (newWeight < endPoint.getWeight())
            {
               endPoint.setWeight(newWeight);
               endPoint.setFlightPlan(airport.getFlightPlan(), flight);
               GMTtime newReadyTime = new GMTtime(flight.getEnd());
               newReadyTime.add(LAYOVER_DELAY);
               endPoint.setReadyTime(newReadyTime);
               
               heap.put(new Integer(endPoint.getWeight()), endPoint);
            }
         }
      }
   }
   
   private void processFiles()
   {
      File apFile = null, flightFile = null;
      
         // Ask for Airport Data File
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setDialogTitle("Open Airport Data File");
      fileChooser.setSelectedFile(new File("airport-data.txt"));
      int result = fileChooser.showOpenDialog(this);
      
         // Check to see if a file was given
      if (result == JFileChooser.APPROVE_OPTION)
         apFile = fileChooser.getSelectedFile();
      else
      {
         System.out.println("Program Halted: No airport data file selected.");
         System.exit(0);
      }
      
         // Ask for Flight Data File
      fileChooser = new JFileChooser();
      fileChooser.setDialogTitle("Open Flight Data File");
      fileChooser.setSelectedFile(new File("flight-data.txt"));
      result = fileChooser.showOpenDialog(this);

         // Check to see if a file was given
      if (result == JFileChooser.APPROVE_OPTION)
         flightFile = fileChooser.getSelectedFile();
      else
      {
         System.out.println("Program Halted: No flight data file selected.");
         System.exit(0);
      }
      
         // Attempt to read entire Airport Data File
      BufferedReader reader;
      InputStream stream;
      String line;
      try
      {
         stream = new FileInputStream(apFile);
         reader = new BufferedReader(new InputStreamReader(stream));
         
            // This is the number of airports. I don't care about this
         line = reader.readLine();
         while ((line = reader.readLine()) != null) 
         {
            if (!line.trim().equals(""))
            {
               Airport airport = new Airport(line);
               airports.put(airport.getAbbr(), airport);
            }
         } 
         
         stream.close();
         reader.close();
      }
      catch (Exception e)
      {
         System.err.println("Error with Airport Data file I/O");
         System.err.println("File may not exist at given path or is formatted incorrectly");
         System.err.println("Path given: " + apFile.getPath());
         e.printStackTrace();
         System.exit(1);
      }
      
         // Attempt to read entire Flight Data File
      try
      {
         stream = new FileInputStream(flightFile);
         reader = new BufferedReader(new InputStreamReader(stream));

            // This is the number of airports. I don't care about this
         line = reader.readLine();
         while ((line = reader.readLine()) != null) 
         {
            if (!line.trim().equals(""))
            {
               Flight flight = new Flight(line, airports);
               flight.getSource().addFlight(flight);
            }
         }  
         
         stream.close();
         reader.close();
      }
      catch (Exception e)
      {
         System.err.println("Error with Flight Data file I/O");
         System.err.println("File may not exist at given path or is formatted incorrectly");
         System.err.println("Path given: " + apFile.getPath());
         e.printStackTrace();
         System.exit(1);
      }
   }
   

      // Show all airports and flights from these airports
   @SuppressWarnings("unused")
   private void dump()
   {
      for (Airport airport : airports.values())
      {
         System.out.println("------- " + airport + " --------");
         HashMap<Airport, FlightList> adjList = airport.getAdjList();
         
         for (FlightList list : adjList.values())
         {
            System.out.println("++++++ FLIGHTS TO " + list.getDest().getAbbr() + " FROM " + airport.getAbbr() + " ++++++");
            MyHeap<GMTtime, Flight> flights = new MyHeap<GMTtime, Flight>(list.getFlights());
            int size = flights.getSize();
            for (int i = 0; i < size; i++)
            {
               System.out.println(flights.extractMin());
            }
         }
      }
   }
   
      // Enable/Disable user input.
   private void enableComponents(Container cont, boolean enable)
   {
      Component comps[] = cont.getComponents();
      for (int i = 0; i < comps.length; i++)
      {
         if (comps[i] instanceof JComponent)
         {
            ((JComponent)comps[i]).setEnabled(enable);
         }
         
         if (comps[i] instanceof Container)
         {
            enableComponents((Container)comps[i], enable);
         }
      }
   }
   
   private class TimeSelector extends JPanel
   {
      private JComboBox ampm;
      private SpinnerNumberModel hourModel, minuteModel;
      
      public TimeSelector(boolean timespan)
      {
         setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
         
         if (timespan)
            hourModel = new CyclicSpinnerModel(24, 0, 999, 1, true);
         else
            hourModel = new CyclicSpinnerModel(12, 1, 12, 1, true);
         
         JSpinner hourSpinner = new JSpinner(hourModel);
         
         minuteModel = new CyclicSpinnerModel(0, 0, 59, 1);
         JSpinner minuteSpinner = new JSpinner(minuteModel);
         
         if (!timespan)
            minuteSpinner.setEditor(new JSpinner.NumberEditor(minuteSpinner, "00"));
         
         ampm = new JComboBox(new String[]{AM, PM});
         ampm.setSelectedItem(PM);
         
         add(hourSpinner);
         if(timespan)
            add(new JLabel(" Hour(s) "));
         else
            add(new JLabel(":"));
         add(minuteSpinner);
         add(Box.createHorizontalStrut(5));
         if(timespan)
            add(new JLabel(" Minute(s) "));
         else
            add(ampm);
         add(Box.createHorizontalGlue());
      }
      
      public int getHour() { return hourModel.getNumber().intValue(); }
      public int getMinute() { return minuteModel.getNumber().intValue(); }
      public boolean isAM() { return ((String)ampm.getSelectedItem()).equals(AM); }
      
      private class CyclicSpinnerModel extends SpinnerNumberModel
      {
         private boolean trigger;

         private CyclicSpinnerModel(int value, int min, int max, int step)
         {
            this(value, min, max, step, false);
         }
         
         private CyclicSpinnerModel(int value, int min, int max, int step, boolean trig)
         {
            super(value, min, max, step);
            trigger = trig;
         }

         public Object getNextValue()
         {
            Integer nextValue = (Integer)getValue() + (Integer)getStepSize();
            if (trigger && (nextValue.equals((Integer)getMaximum())))
               ampm.setSelectedIndex(1 - ampm.getSelectedIndex());

            if (((Integer)getValue()).equals((Integer)getMaximum()))
               return getMinimum();
            else
               return nextValue;
         }

         public Object getPreviousValue()
         {
            if (trigger && (((Integer)getValue()).equals((Integer)getMaximum())))
               ampm.setSelectedIndex(1 - ampm.getSelectedIndex());

            if (((Integer)getValue()).equals((Integer)getMinimum()))
               return getMaximum();
            else
               return (Integer)getValue() - (Integer)getStepSize();
         }
      }
   }
   
   public static void main(String args[])
   {
      @SuppressWarnings("unused")
      Driver driver = new Driver();
   }
}
