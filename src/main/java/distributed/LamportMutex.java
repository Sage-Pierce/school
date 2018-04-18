package distributed;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;

public class LamportMutex {
	
	public DirectClock v;
	int myId;
	int numServer;
	//TCPServer Server;
    public int[] q; // request queue
    public LamportMutex(int id, int size) {
        myId = id;
        numServer = size;
        //Server = server;
        v = new DirectClock(numServer, myId);
        q = new int[numServer];
        for (int j = 0; j < numServer; j++) 
            q[j] = Integer.MAX_VALUE; // infinity
    }
    public synchronized void requestCS(TCPServer Server) {

        v.tick();
        q[myId] = v.getValue(myId);
        String unconnected = Server.broadcastMsg("request", q[myId]);
        StringTokenizer string = new StringTokenizer(unconnected);
        while(string.hasMoreElements())
        {
        	int src = Integer.parseInt(string.nextToken());
        	q[src] = Integer.MAX_VALUE;
        	v.receiveAction(src, q[myId] + 1);
        	
        }
        try
        {
        	while (!okayCS())
        		wait();
        }
        catch(InterruptedException e)
        {
        	e.printStackTrace();
        	System.exit(1);
        }
        
    }
    
    
    public synchronized void releaseCS(TCPServer Server) {
    	//synchronized
    	System.out.println("Server" + myId + " releaseCS");
    	
        q[myId] =  Integer.MAX_VALUE; // infinity
        Server.broadcastMsg("release", v.getValue(myId));  
        notifyAll();
        
    }
    
    public Boolean okayCS() {
        for (int j = 0; j < numServer; j++){
            if (isGreater(q[myId], myId, q[j], j))
                return false;
            if (isGreater(q[myId], myId, v.getValue(j), j))
                return false;
        }
        return true;
    }
    boolean isGreater(int entry1, int pid1, int entry2, int pid2) {
        return ((entry1 > entry2) 
                || ((entry1 == entry2) && (pid1 > pid2)));
    }
    
    
    /*public synchronized void handleMsg(String m, int src, String tag) {  
    	//synchronized
    	System.out.println("Server" + myId + " HandleMsg");
    	
    	
        int timeStamp = Integer.parseInt(m);
        v.receiveAction(src, timeStamp);
        System.out.println("Server" + myId + "receiving " + tag);
        if (tag.equals("request")) {
            q[src] = timeStamp;
            Server.sendMsg(src, "ack", String.valueOf(v.getValue(myId)));
        } else if (tag.equals("release")) {
        	q[src] = -1;     
        }
        notifyAll();
        
       
    }*/
    
    public synchronized void handleMsg(String m, int src, String tag, TCPServer Server) {
        int timeStamp = Integer.parseInt(m);
        v.receiveAction(src, timeStamp);
        if (tag.equals("request")) {
            q[src] = timeStamp;
            Server.sendMsg(src, "ack", String.valueOf(v.getValue(myId)));
        } else if (tag.equals("release"))
            q[src] = Integer.MAX_VALUE;
        notifyAll(); // okayCS() may be true now
    }
 
}

