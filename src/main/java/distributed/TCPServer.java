package distributed;

import java.net.*;
import java.io.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class TCPServer implements Runnable
{
   private static final int TIMEOUT = 5 * 1000; // 5 second timeout
	private static final String SYNCRHONIZE = "syncrhonize";
	private Theater theater;
	private Socket theClient;
	private int serverNum;
	private ArrayList<ServerEntry> myEntries;
	private LamportMutex lock;
	
	public TCPServer(Theater theTheater, Socket newClient, ArrayList<ServerEntry> entries, int myServerNum, LamportMutex theLock) throws IOException
	{
		theater = theTheater;
		theClient = newClient;
		serverNum = myServerNum;
	   myEntries = entries;
	   lock = theLock;
	}
	
	public void run()
	{
		try
		{
			boolean forced = false;
			BufferedReader din = new BufferedReader(new InputStreamReader(theClient.getInputStream()));
			PrintWriter pout = new PrintWriter(theClient.getOutputStream());
         
			String getline = din.readLine();
			StringTokenizer st = new StringTokenizer(getline);
			String tag = st.nextToken();
         
			if(tag.equals("force"))
			{
				tag = st.nextToken();
				forced = true;
			}
			if(tag.equals("request"))
			{
				System.out.println("Requested!");
				int srcID = Integer.parseInt(st.nextToken());
            	String msg = st.nextToken();
            	lock.handleMsg(msg,srcID,tag,this);
				
			}
			if(tag.equals("release"))
			{
				System.out.println("Released!");
				int srcID = Integer.parseInt(st.nextToken());
            	String msg = st.nextToken();
            	lock.handleMsg(msg,srcID,tag,this);
			}
			if(tag.equals("ack"))
			{
				System.out.println("ack");
				int src = Integer.parseInt(st.nextToken());
            	String msg = st.nextToken();
            	lock.handleMsg(msg,src,tag,this);
			}
			if(tag.equals(SYNCRHONIZE))
			{	
            lock.requestCS(this);
				ObjectOutputStream oos = new ObjectOutputStream(theClient.getOutputStream());
				oos.writeObject(theater);
            lock.releaseCS(this);
			}
			if(tag.equals("reserve"))
			{
				int seatNum;
				if(!forced)
				{
					lock.requestCS(this);
					broadcastMsg("force " + getline, 0);
					seatNum = theater.reserve(st.nextToken());
					lock.releaseCS(this);
				}
				else
				{
					seatNum = theater.reserve(st.nextToken());
				}
				if(seatNum == -1)
				{
					pout.println("Seat already booked against the name provided.");
				}
				else if(seatNum == 0)
				{
					pout.println("Sold out - No seat available.");
				}
				else
				{
					pout.println("Seat assigned to you is " + seatNum + ".");
				}
			}
			else if(tag.equals("bookSeat"))
			{
				//System.out.println(getline);
				String name = st.nextToken();
				//System.out.println(name);
				int seatNum = Integer.parseInt(st.nextToken());
				//System.out.println(seatNum);
				int success;
				if(!forced)
				{
					lock.requestCS(this);
					broadcastMsg("force " + getline, 0);
					success = theater.bookSeat(name, seatNum);
					lock.releaseCS(this);
				}
				else
				{
					success = theater.bookSeat(name, seatNum);
				}
				if(success == -1)
				{
					pout.println("Seat already booked against the name provided.");
				}
				else if(success == 0)
				{
					pout.println(seatNum + " is not available.");
				}
				else
				{
					pout.println("Seat assigned to you is " + seatNum + ".");
				}
			}
			else if(tag.equals("search"))
			{
				String name = st.nextToken();
				int seatNum;
				if(!forced)
				{
					lock.requestCS(this);
					broadcastMsg("force " + getline, 0);
					seatNum = theater.search(name);
					lock.releaseCS(this);
				}
				else
				{
					seatNum = theater.search(name);
				}
				if(seatNum == 0)
				{
					pout.println("No reservation found for " + name + ".");
				}
				else
				{
					pout.println(seatNum);
				}
			}
			else if(tag.equals("delete"))
			{
				String name = st.nextToken();
				int seatNum;
				if(!forced)
				{
					lock.requestCS(this);
					broadcastMsg("force " + getline, 0);
					seatNum = theater.delete(name);
					lock.releaseCS(this);
				}
				else
				{
					seatNum = theater.delete(name);
				}
				if(seatNum == 0)
				{
					pout.println("No reservation found for " + name + ".");
				}
				else
				{
					pout.println(seatNum);
				}
			}
			pout.flush();
			theClient.close();
		}
		catch (IOException e)
		{
			System.err.println(e);
		}
	}
	
	public static void main1(String[] args) throws IOException
	{
		Theater thisTheater = new Theater(100);
		int myServerNum = -1;
	   ArrayList<ServerEntry> entries = new ArrayList<ServerEntry>();
	   
      String filePath;
      if (args.length > 0)
         filePath = args[0];
      else
         filePath = "null";
         
	   String workingPath = System.getProperty("user.dir") +
	                        System.getProperty("file.separator") + filePath;
	   String defaultPath = System.getProperty("user.dir") +
	                        System.getProperty("file.separator") + "Servers.txt";

      File inFile = new File(filePath);
      if(!inFile.exists())
      {
         inFile = new File(workingPath);
         if(!inFile.exists())
            inFile = new File(defaultPath);
      }
	         
      BufferedReader file = new BufferedReader(new FileReader(inFile));      
      String line;
      while ((line = file.readLine()) != null)
      {
         String address[] = line.split(":");
         ServerEntry entry = new ServerEntry(address[0], address[1]);
         entries.add(entry);
	         
            // Is this entry me?
         if (InetAddress.getLocalHost().equals(InetAddress.getByAddress(entry.ipAddress)))
            myServerNum = entries.indexOf(entry); 
      }
	   
      LamportMutex tmpLock = new LamportMutex(myServerNum, entries.size());
      
      boolean synced = false;
      for (int i = 0; i < entries.size(); i++)
      {
         if (i == myServerNum) // Don't want to query myself
            continue;
	         
         ServerEntry entry = entries.get(i);
         try
         {
            Socket anotherServer = new Socket();
            InetAddress serverAddr = InetAddress.getByAddress(entry.ipAddress);
            anotherServer.connect(new InetSocketAddress(serverAddr, entry.port), TIMEOUT);
	            
	         PrintStream tmpPout = new PrintStream(anotherServer.getOutputStream());

            tmpPout.println(SYNCRHONIZE);
            tmpPout.flush();
	         ObjectInputStream ois = new ObjectInputStream(anotherServer.getInputStream());
	         thisTheater = (Theater)ois.readObject();
	         synced = true;
	         break;
	      }
	      catch (SocketTimeoutException e)
	      {
	         System.err.println("Error: Server socket timed out during theater" +
	                            " sync process. Trying next server.");
	      }
	      catch (Exception e)
	      {
	         e.printStackTrace();
	      }
	   }
	      
	   if (!synced)
	   {
	      System.out.println("This server created its own Theater object");
	      thisTheater = new Theater(100);
	   }
	   else
	   {
	      System.out.println("Synced!!");
      }
		
		System.out.println("Theater Server started:");
		
      try
		{
			ServerSocket listener = new ServerSocket(2710);
			while(true)
			{
				Socket aClient = listener.accept();
				
				TCPServer ns = new TCPServer(thisTheater, aClient, entries, myServerNum, tmpLock);
				Thread requestHandler = new Thread(ns);
				requestHandler.start();		
			}
		}
		catch (IOException e)
		{
			System.err.println("Server aborted" + e);
		}
	}
	
	
	public synchronized String broadcastMsg(String tag, int clock) {
		System.out.println("broadcastMsg");
    	String failedList = "";
    	for(int i = 0; i < myEntries.size(); i++){
    		System.out.println("Server " + i + "!");
    		if(i != serverNum){
    			if(sendMsg(i,tag, String.valueOf(clock))){
    				continue;
    			}
    			else{
    				failedList += i + " ";
    				System.out.println("Failed!");
    			}
    		}
    	}
    	return failedList;

    }
	
	public synchronized boolean sendMsg(int dest, String tag, String msg){
    	PrintStream pout = null;
    	try{
    		System.out.println("send msg");
    		if(dest != serverNum) {
    			Socket server = new Socket();
    			ServerEntry address = myEntries.get(dest);
    			String serverAddress = address.ip;
    			int port = address.port;
    			InetSocketAddress serv = new InetSocketAddress(serverAddress, port);
    			server.connect(serv, 500);
    			pout = new PrintStream(server.getOutputStream());
    			System.out.println(tag + " " + serverNum + " " + msg);
    			pout.println(tag + " " + serverNum + " " + msg);
    			pout.flush();
    			return true;
    			
    		}
    	} catch (IOException e) {
    		System.out.println("send failed");
    		return false;
    	}   	
    	return false;
    }
}