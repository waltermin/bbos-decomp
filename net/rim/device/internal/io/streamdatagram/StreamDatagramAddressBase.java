package net.rim.device.internal.io.streamdatagram;

import java.io.IOException;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.DebugSupport;

public class StreamDatagramAddressBase extends DatagramAddressBase implements StreamDatagramConnectionConstants {
   protected int _ipAddress;
   protected int _port;
   protected int _localPort;
   protected String _apnName;
   protected String _apnUsername;
   protected String _apnPassword;
   protected boolean _isListenAddress;
   protected static String COMPARISON_STRING = "://";
   protected static String SLASH_SLASH = "//";
   private static String RAW_TCP = "raw-tcp";
   private static String TRUE = "true";
   private static String TCP_SCHEME = "tcp";
   private static String SIMULTCP_SCHEME = "simultcp";
   public static final int TCP_IP_ADDRESS_NONE = -1;
   public static final int TCP_PORT_NONE = -1;

   public String getApnName() {
      return this._apnName;
   }

   public void setApnName(String apn) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public String getApnUsername() {
      return this._apnUsername;
   }

   public void setApnUsername(String apnUsername) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public String getApnPassword() {
      return this._apnPassword;
   }

   public void setApnPassword(String apnPassword) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public int getLocalPort() {
      return this._localPort;
   }

   public void setLocalPort(int _1) {
      throw null;
   }

   public int getDestPort() {
      return this._port;
   }

   public boolean isListenAddress() {
      return this._isListenAddress;
   }

   public int getIpAddress() {
      return this._ipAddress;
   }

   public boolean compareApn(String apn) {
      return this._apnName.equalsIgnoreCase(apn);
   }

   public static int retrieveSettings(String address, String[] args) {
      String lname = StringUtilities.toLowerCase(address, 1701707776);
      int i = lname.indexOf(";apn");
      if (i < 0) {
         args[0] = null;
         args[1] = null;
         args[2] = null;
         return 75000;
      }

      int j = address.indexOf(59, ++i);
      args[0] = address.substring(i + 4, j < 0 ? address.length() : j);
      int ret;
      if ((i = lname.indexOf(";connectiontimeout=")) > 0) {
         j = address.indexOf(59, ++i);
         int k = Integer.parseInt(address.substring(i + 18, j < 0 ? address.length() : j));
         ret = k >= 4000 && k <= 75000 ? k : 75000;
      } else {
         ret = 75000;
      }

      if ((i = lname.indexOf(";tunnelauthusername")) > 0) {
         j = address.indexOf(59, ++i);
         args[1] = address.substring(i + 19, j < 0 ? address.length() : j);
      } else {
         args[1] = null;
      }

      if ((i = lname.indexOf(";tunnelauthpassword")) > 0) {
         j = address.indexOf(59, ++i);
         args[2] = address.substring(i + 19, j < 0 ? address.length() : j);
      } else {
         args[2] = null;
      }

      return ret;
   }

   public static int retrieveSessionTimeout(String address) {
      String lname = StringUtilities.toLowerCase(address, 1701707776);
      int paramStart = lname.indexOf(";sessiontimeout=");
      if (paramStart >= 0) {
         int paramEnd = address.indexOf(59, paramStart + 1);

         try {
            return Integer.parseInt(address.substring(paramStart + 16, paramEnd < 0 ? address.length() : paramEnd));
         } finally {
            return -1;
         }
      } else {
         return -1;
      }
   }

   protected void parseAddress(String _1) {
      throw null;
   }

   public String getConnectionAddress() {
      throw null;
   }

   public static String makeAddress(boolean open, byte[] ipAddress, int destPort, int srcPort) {
      return makeAddress(open, ipAddress, destPort, srcPort, null, 0, 0);
   }

   public static String makeAddress(boolean open, byte[] ipAddress, int destPort, int srcPort, String apn) {
      return makeAddress(open, ipAddress, destPort, srcPort, apn, 0, apn != null ? apn.length() : 0);
   }

   public static String makeAddress(boolean open, byte[] ipAddress, int destPort, int srcPort, String apn, int apnOffset, int apnLength) {
      StringBuffer buf = new StringBuffer(128);
      if (open) {
         buf.append(
            DeviceInfo.isSimulator() && !StringUtilities.strEqualIgnoreCase(DebugSupport.getenv(RAW_TCP), TRUE, 1701707776) ? SIMULTCP_SCHEME : TCP_SCHEME
         );
         buf.append('/');
         buf.append('/');
      }

      if (ipAddress != null && DatagramAddressBase.readInt(ipAddress, 0) != -1) {
         if (!open) {
            buf.append('/');
            buf.append('/');
         }

         convertIpAddressBytesToStringBuffer(ipAddress, buf);
      }

      if (destPort != -1) {
         buf.append(':');
         buf.append(destPort);
      }

      if (srcPort != -1) {
         buf.append((char)(destPort != -1 ? ';' : ':'));
         buf.append(srcPort);
      }

      return buf.toString();
   }

   public static String getPeerAddressInternal(int ipAddress) {
      StringBuffer buf = new StringBuffer(20);
      byte[] b = new byte[4];

      for (int i = 3; i >= 0; i--) {
         b[i] = (byte)(ipAddress & 0xFF);
         ipAddress >>= 8;
      }

      convertIpAddressBytesToStringBuffer(b, buf);
      return buf.toString();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static String getLocalAddressInternal(String apnName) throws IOException {
      if (apnName == null) {
         throw new IllegalArgumentException();
      }

      StringBuffer buf = new StringBuffer(20);
      boolean var4 = false /* VF: Semaphore variable */;

      try {
         var4 = true;
         convertIpAddressBytesToStringBuffer(RadioInfo.getIPAddress(RadioInfo.getAccessPointNumber(apnName)), buf);
         var4 = false;
      } finally {
         if (var4) {
            throw new IOException("Can't get local ip address");
         }
      }

      return buf.toString();
   }

   public static void convertIpAddressBytesToStringBuffer(byte[] ipAddress, StringBuffer buf) {
      if (ipAddress == null) {
         buf.append("255.255.255.255");
      } else {
         buf.append(ipAddress[0] & 255);
         buf.append('.');
         buf.append(ipAddress[1] & 255);
         buf.append('.');
         buf.append(ipAddress[2] & 255);
         buf.append('.');
         buf.append(ipAddress[3] & 255);
      }
   }
}
