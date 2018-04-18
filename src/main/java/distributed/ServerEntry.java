package distributed;

public class ServerEntry
{
   public String ip;
   public int port;
   public byte ipAddress[];
   
   public ServerEntry(String addr, String pt)
   {
      port = Integer.parseInt(pt);
      ip = addr;
      String ipBits[] = addr.split("\\.");
      ipAddress = new byte[ipBits.length];
      for(int i = 0; i < ipBits.length; i++)
      {
         ipAddress[i] = (byte)Integer.parseInt(ipBits[i]);
      }
   }
}