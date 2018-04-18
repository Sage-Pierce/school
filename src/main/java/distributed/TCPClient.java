package distributed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class TCPClient 
{
   private static final int TIMEOUT = 5 * 1000; // 5 second timeout
   private ArrayList<ServerEntry> entries;
	private BufferedReader din, file;
   private File inFile;
   private int serverNum;
   private InetAddress serverAddr;
	private PrintStream pout;
   private Socket client;
   
   public TCPClient(String filePath) throws IOException
   {
      serverNum = 0;
      entries = new ArrayList<ServerEntry>();
      
      String workingPath = System.getProperty("user.dir") +
                           System.getProperty("file.separator") + filePath;
      String defaultPath = System.getProperty("user.dir") +
                           System.getProperty("file.separator") + "Servers.txt";

      inFile = new File(filePath);
      if(!inFile.exists())
      {
         inFile = new File(workingPath);
         if(!inFile.exists())
            inFile = new File(defaultPath);
      }
         
      file = new BufferedReader(new FileReader(inFile));
      
      String line;
      while ((line = file.readLine()) != null)
      {
         String address[] = line.split(":");
         ServerEntry entry = new ServerEntry(address[0], address[1]);
         entries.add(entry);
      }
   }
	
	private void generateSocket() throws IOException 
   {
      din = null;
      pout = null;
      
      while ((din == null) || (pout == null))
      {
         try
         {
            ServerEntry entry = entries.get(serverNum);
            serverNum++;
            if (serverNum >= entries.size())
               serverNum = 0;
	   	
            if (client != null)
               client.close();
            
            client = new Socket();
            serverAddr = InetAddress.getByAddress(entry.ipAddress);
            client.connect(new InetSocketAddress(serverAddr, entry.port), TIMEOUT);
            
            din = new BufferedReader(new InputStreamReader(client.getInputStream()));
		      pout = new PrintStream(client.getOutputStream());
         } catch (Exception e) 
         {
            System.err.println("Error: Client socket timed out. " +
                               "Cyclically trying next server...");
         }
      }
	}
   
   public String send (String send) throws Exception
   {
      generateSocket();
		pout.println(send);
		pout.flush();
      return din.readLine();
   }
   
   private class ServerEntry
   {
      public byte ipAddress[];
      public int port;
      
      private ServerEntry(String addr, String pt)
      {
         port = Integer.parseInt(pt);

         String ipBits[] = addr.split("\\.");
         ipAddress = new byte[ipBits.length];
         for(int i = 0; i < ipBits.length; i++)
         {
            ipAddress[i] = (byte)Integer.parseInt(ipBits[i]);
         }
      }
   }
	
   public static void main1(String[] args)
   {
      String input, response;
      BufferedReader in;
      
      try
      {
         in = new BufferedReader(new InputStreamReader(System.in));
         
         TCPClient myClient;
         if (args.length > 0)
            myClient = new TCPClient(args[0]);
         else
            myClient = new TCPClient("null");
         
         do
         {
            System.out.print("Enter command: ");
            input = in.readLine();
            response = myClient.send(input);
            System.out.println(response);
         }while(!input.equals("Quit"));
      }catch(Exception e){ e.printStackTrace(); }
   }
}
