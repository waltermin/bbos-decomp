package net.rim.device.cldc.io.socketmanager;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.dns.DNSRequest;
import net.rim.device.cldc.io.dns.DNSResolverIPv4;
import net.rim.device.cldc.io.tunnel.Tunnel;
import net.rim.device.cldc.io.tunnel.TunnelConfig;
import net.rim.device.internal.io.tunnel.TunnelWorker;

public final class Protocol implements ConnectionBaseInterface {
   private StringBuffer _sharedBuffer = new StringBuffer();
   private Random _randomProvider = new Random();
   private static final int MAX_IPS_TO_TRY = 3;
   private static final char PORT_SEP = ':';
   private static final char PROTO_SEP = ':';
   private static final char PARM_LIST_SEP = ';';
   private static final char PARM_SEP = '=';
   private static final String SOCKMANAGER_PREFIX = "//";
   private static final char IP_ADDR_SEP = '.';
   private static final String INTERFACE = "interface";
   private static final String RETRY_NO_CONTEXT = "retrynocontext";
   private static final String APN = "APN";
   private static final String APN_USER = "TunnelAuthUsername";
   private static final String APN_PASS = "TunnelAuthPassword";
   private static final String DNS1 = "PrimaryDNS";
   private static final String DNS2 = "SecondaryDNS";
   private static final String SESSION_TIMEOUT = "sessiontimeout";
   private static final String DEFAULT_APN = "blackberry.net";
   private static final String DEFAULT_INTERFACE = "cellular";
   private static final String TUNNEL_DESC = "dnstcp";
   private static final int MAX_TUNNEL_ATTEMPTS = 2;
   private static final int TUNNEL_KICK = 30000;
   private static final String PREFIX = "socket://";
   private static final String DEVICE_SIDE_PARM = "deviceside=true";

   final byte[] toIPAddress(String value) {
      int index1 = 0;
      byte[] valToReturn = new byte[4];

      for (int i = 0; i < 4; i++) {
         int index2 = value.indexOf(46, index1);
         if (index2 < index1) {
            index2 = value.length();
         }

         int temp;
         try {
            temp = Integer.parseInt(value.substring(index1, index2));
         } finally {
            ;
         }

         valToReturn[i] = (byte)(0xFF & temp);
         index1 = index2 + 1;
      }

      return valToReturn;
   }

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) throws IOException {
      Protocol$ConnectionParams parms = new Protocol$ConnectionParams(null);
      Connection connToReturn = null;
      this.parseName(name, parms);
      Tunnel tunnel = this.activateTunnel(parms);
      int max = 3;

      for (int i = 0; i < max; i++) {
         Vector endPoints = this.lookup(parms, tunnel);
         int selectedIP = this.select(endPoints);
         connToReturn = this.tryConnection(parms, endPoints, selectedIP);
         if (connToReturn != null) {
            break;
         }

         this.markEndpointBad(parms, endPoints, selectedIP);
         if (endPoints.size() <= 1) {
            break;
         }
      }

      if (connToReturn == null) {
         throw new IOException("socketmanager couldn't open connection");
      }

      tunnel.close();
      return connToReturn;
   }

   @Override
   public final int getProperties(String name) {
      return 0;
   }

   final String IPtoString(byte[] ipAddress) {
      StringBuffer sharedBuffer = this._sharedBuffer;
      sharedBuffer.setLength(0);
      int max = ipAddress.length;

      for (int i = 0; i < max; i++) {
         if (i != 0) {
            sharedBuffer.append('.');
         }

         sharedBuffer.append(Integer.toString(ipAddress[i] & 255));
      }

      return sharedBuffer.toString();
   }

   private final void getParms(String name, int startOfParms, Protocol$ConnectionParams parms) {
      int startOfName = startOfParms;
      int len = name.length();

      while (true) {
         int endOfName = name.indexOf(61, startOfName);
         if (endOfName < startOfParms) {
            return;
         }

         int startOfValue = endOfName + 1;
         int endOfValue = name.indexOf(59, startOfValue);
         if (endOfValue < startOfValue) {
            endOfValue = len;
         }

         this.interpretNameValue(name, startOfName, endOfName, startOfValue, endOfValue, parms);
         startOfName = endOfValue + 1;
      }
   }

   private final void interpretNameValue(String name, int startOfName, int endOfName, int startOfValue, int endOfValue, Protocol$ConnectionParams parms) {
      char startChar = CharacterUtilities.toUpperCase(name.charAt(startOfName), 1701707776);
      String value = name.substring(startOfValue, endOfValue);
      switch (startChar) {
         case 'A':
            if (!StringUtilities.regionMatches(name, true, startOfName, "APN", 0, 3, 1701707776)) {
               return;
            }

            parms._apn = value;
            return;
         case 'I':
            if (!StringUtilities.regionMatches(name, true, startOfName, "interface", 0, 9, 1701707776)) {
               return;
            }

            parms._interface = value;
            return;
         case 'P':
            if (!StringUtilities.regionMatches(name, true, startOfName, "PrimaryDNS", 0, 10, 1701707776)) {
               return;
            }

            parms._primaryDNS = this.toIPAddress(value);
            if (parms._primaryDNS == null) {
               throw new IllegalArgumentException("bad primary dns");
            }
            break;
         case 'R':
            if (!StringUtilities.regionMatches(name, true, startOfName, "retrynocontext", 0, 14, 1701707776)) {
               return;
            }

            parms._retryNoContext = value;
            break;
         case 'S':
            if (StringUtilities.regionMatches(name, true, startOfName, "SecondaryDNS", 0, 12, 1701707776)) {
               parms._secondaryDNS = this.toIPAddress(value);
               if (parms._secondaryDNS == null) {
                  throw new IllegalArgumentException("bad secondary dns");
               }
            } else if (StringUtilities.regionMatches(name, true, startOfName, "sessiontimeout", 0, 14, 1701707776)) {
               try {
                  parms._sessionTimeout = Integer.parseInt(value);
                  return;
               } finally {
                  return;
               }
            }
            break;
         case 'T':
            if (StringUtilities.regionMatches(name, true, startOfName, "TunnelAuthUsername", 0, 18, 1701707776)) {
               parms._username = value;
               return;
            }

            if (StringUtilities.regionMatches(name, true, startOfName, "TunnelAuthPassword", 0, 18, 1701707776)) {
               parms._password = value;
               return;
            }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void parseName(String name, Protocol$ConnectionParams parms) {
      int index1 = name.indexOf("//");
      if (index1 < 0) {
         throw new IllegalArgumentException("socketmanager:// missing");
      }

      index1 += 2;
      int index2 = name.indexOf(58, index1);
      if (index2 < index1) {
         throw new IllegalArgumentException("socketmanager missing second colon");
      }

      parms._hostName = name.substring(index1, index2);
      index1 = index2;
      index2 = name.indexOf(59, ++index1);
      String temp;
      if (index2 < index1) {
         temp = name.substring(index1);
      } else {
         temp = name.substring(index1, index2);
         index2++;
         this.getParms(name, ++index2, parms);
      }

      try {
         parms._port = Integer.parseInt(temp);
      } catch (Throwable var8) {
         throw new IllegalArgumentException(e.getMessage());
      }
   }

   private final Tunnel activateTunnel(Protocol$ConnectionParams parms) throws IOException {
      String apn;
      String username;
      String password;
      if (parms._interface.equalsIgnoreCase("wifi")) {
         apn = WLAN.WLAN_PSEUDO_APN;
         username = null;
         password = null;
      } else {
         apn = parms._apn;
         username = parms._username;
         password = parms._password;
      }

      TunnelWorker tw = new TunnelWorker();
      TunnelConfig config = new TunnelConfig(apn, "dnstcp", null, username, password, tw);
      if (parms._sessionTimeout >= 0) {
         config.setLingerTimeout(parms._sessionTimeout);
      }

      int i = 0;

      while (true) {
         try {
            return tw.open(config);
         } finally {
            if (i >= 1) {
               ;
            } else {
               if (++i >= 2) {
                  throw new IOException("Tunnel opening timed out!");
               }
               continue;
            }
         }
      }
   }

   private final Vector lookup(Protocol$ConnectionParams parms, Tunnel tunnel) {
      DNSRequest req = new DNSRequest(parms._hostName, null, tunnel.getIdentifier());
      if (parms._primaryDNS != null) {
         req.setPrimaryDnsIp(parms._primaryDNS);
      }

      if (parms._secondaryDNS != null) {
         req.setSecondaryDnsIp(parms._secondaryDNS);
      }

      return DNSResolverIPv4.instance().doBlockingQuery(req);
   }

   private final int select(Vector endpoints) {
      int size = endpoints.size();
      long randval = 2147483647 & this._randomProvider.nextInt();
      randval = randval * size / Integer.MAX_VALUE;
      int result = (int)(Integer.MAX_VALUE & randval);
      if (result >= size) {
         result = size - 1;
      }

      return result;
   }

   private final Connection tryConnection(Protocol$ConnectionParams parms, Vector endPoints, int selectedIP) {
      Connection conn = null;
      String address = this.IPtoString((byte[])endPoints.elementAt(selectedIP));
      StringBuffer sharedBuffer = this._sharedBuffer;
      sharedBuffer.setLength(0);
      sharedBuffer.append("socket://");
      sharedBuffer.append(address);
      sharedBuffer.append(':');
      sharedBuffer.append(parms._port);
      sharedBuffer.append(';');
      sharedBuffer.append("deviceside=true");
      sharedBuffer.append(';');
      sharedBuffer.append("APN");
      sharedBuffer.append('=');
      sharedBuffer.append(parms._apn);
      if (parms._username != null) {
         sharedBuffer.append(';');
         sharedBuffer.append("TunnelAuthUsername");
         sharedBuffer.append('=');
         sharedBuffer.append(parms._username);
      }

      if (parms._password != null) {
         sharedBuffer.append(';');
         sharedBuffer.append("TunnelAuthPassword");
         sharedBuffer.append('=');
         sharedBuffer.append(parms._password);
      }

      sharedBuffer.append(';');
      sharedBuffer.append("interface");
      sharedBuffer.append('=');
      sharedBuffer.append(parms._interface);
      if (parms._retryNoContext != null) {
         sharedBuffer.append(';');
         sharedBuffer.append("retrynocontext");
         sharedBuffer.append('=');
         sharedBuffer.append(parms._retryNoContext);
      }

      if (parms._sessionTimeout >= 0) {
         sharedBuffer.append(';');
         sharedBuffer.append("sessiontimeout");
         sharedBuffer.append('=');
         sharedBuffer.append(parms._sessionTimeout);
      }

      address = sharedBuffer.toString();

      try {
         conn = Connector.open(address);
      } finally {
         return conn;
      }

      return conn;
   }

   private final void markEndpointBad(Protocol$ConnectionParams parms, Vector endpoints, int selectedIP) {
      DNSResolverIPv4.instance().clearResultFromCache(parms._hostName, endpoints.elementAt(selectedIP));
   }
}
