package net.rim.device.api.io;

import java.io.IOException;
import java.util.Vector;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.system.RadioException;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.UDPPacketHeader;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.dns.DNSResolverIPv4;
import net.rim.device.internal.io.tunnel.TunnelCredentialsProvider;

public class UdpAddress extends DatagramAddressBase {
   protected int _ipAddress;
   protected int _destPort;
   protected int _srcPort;
   protected String _apn;
   protected String _apnUsername;
   protected String _apnPassword;
   protected int _type;
   public static final int TYPE_UDP = 1;
   public static final int TYPE_GPAK = 2;
   public static final int TYPE_GCMP = 4;
   private static String USERNAME = "tunnelauthusername";
   private static String PASSWORD = "tunnelauthpassword";

   public UdpAddress() {
      this.init(-1, -1, -1, null, -1);
   }

   public UdpAddress(byte[] ipAddress, int destPort, int srcPort, String apn, int type) {
      this.init(ipAddress != null ? DatagramAddressBase.readInt(ipAddress, 0) : -1, destPort, srcPort, apn, type);
   }

   public UdpAddress(byte[] ipAddress, int destPort, int srcPort, String apn, int apnOffset, int apnLength, int type) {
      this.init(
         ipAddress != null ? DatagramAddressBase.readInt(ipAddress, 0) : -1,
         destPort,
         srcPort,
         apn != null ? apn.substring(apnOffset, apnOffset + apnLength) : null,
         type
      );
   }

   public UdpAddress(int ipAddress, int destPort, int srcPort, String apn, int type) {
      this.init(ipAddress, destPort, srcPort, apn, type);
   }

   public UdpAddress(int ipAddress, int destPort, int srcPort, String apn, int apnOffset, int apnLength, int type) {
      this.init(ipAddress, destPort, srcPort, apn != null ? apn.substring(apnOffset, apnOffset + apnLength) : null, type);
   }

   public UdpAddress(DatagramAddressBase addressBase) {
      if (!(addressBase instanceof UdpAddress)) {
         this.setAddress(addressBase.getAddress());
      } else {
         UdpAddress addr = (UdpAddress)addressBase;
         this.init(addr._ipAddress, addr._destPort, addr._srcPort, addr._apn, addr._type);
         this.setApnUsername(addr._apnUsername);
         this.setApnPassword(addr._apnPassword);
      }
   }

   public UdpAddress(String address) {
      this.setAddress(address);
   }

   private void init(int ipAddress, int destPort, int srcPort, String apn, int type) {
      this._ipAddress = ipAddress;
      this._destPort = destPort;
      this._srcPort = srcPort;
      this._apn = apn;
      this._type = type;
      super._key = ipAddress ^ (destPort << 16 | srcPort & 65535) ^ (apn != null ? apn.hashCode() : 0) ^ type;
   }

   public byte[] getIpAddress() {
      byte[] ipAddress = null;
      if (this._ipAddress != -1) {
         ipAddress = new byte[4];
         DatagramAddressBase.writeInt(ipAddress, 0, this._ipAddress);
      }

      return ipAddress;
   }

   public int getIpAddressInt() {
      return this._ipAddress;
   }

   public int getDestPort() {
      return this._destPort;
   }

   public int getSrcPort() {
      return this._srcPort;
   }

   public String getApn() {
      return this._apn;
   }

   public void setApn(String apn) {
      this._apn = apn;
   }

   public String getApnUsername() {
      return this._apnUsername;
   }

   public void setApnUsername(String apnUsername) {
      this._apnUsername = apnUsername;
   }

   public String getApnPassword() {
      return this._apnPassword;
   }

   public void setApnPassword(String apnPassword) {
      this._apnPassword = apnPassword;
   }

   public void setSrcPort(int srcPort) {
      this._srcPort = srcPort;
   }

   public void setDestPort(int destPort) {
      this._destPort = destPort;
   }

   public int getApnOffset() {
      return 0;
   }

   public int getApnLength() {
      return this._apn != null ? this._apn.length() : 0;
   }

   public int getType() {
      return this._type;
   }

   @Override
   public void setAddress(String address) {
      try {
         setAddress(this, address, false, true);
      } catch (IOException var3) {
      }
   }

   public static final String resolveAddress(String address) {
      return setAddress(null, address, true, false);
   }

   private static final String setAddress(UdpAddress udpAddress, String address, boolean resolve, boolean parse) {
      int delim = 0;
      int length = address.length();
      int ipAddress = -1;
      int destPort = -1;
      int srcPort = -1;
      String apn = null;
      int type = -1;
      int addressFQDN = -1;
      boolean serverConnection = false;
      if (length >= 2 && address.charAt(0) == '/' && address.charAt(1) == '/') {
         addressFQDN = DatagramAddressBase.indexOfNextDelim(address, 2);
         if (!resolve || !DatagramAddressBase.isDomainName(address, 2, addressFQDN)) {
            if (!parse) {
               return address;
            }

            if (addressFQDN > 2) {
               ipAddress = DatagramAddressBase.parseIpAddressInt(address, 2);
               if (ipAddress == -1) {
                  throw new IllegalArgumentException("Invalid IP_ADDRESS");
               }
            } else {
               serverConnection = true;

               try {
                  ControlledAccess.assertRRISignatures(true);
               } catch (ControlledAccessException cae) {
                  throw new IllegalArgumentException("Invalid IP_ADDRESS");
               }
            }
         }

         delim = addressFQDN;
      }

      if (length > delim && address.charAt(delim) == ':') {
         int scan = delim + 1;
         delim = DatagramAddressBase.indexOfNextDelim(address, scan);
         if (delim <= scan) {
            throw new IllegalArgumentException("Bad DEST_PORT");
         }

         int ret = DatagramAddressBase.parseInt(address, scan, delim, 10);
         if (ret < 0 || ret > 65535) {
            throw new IllegalArgumentException("Invalid DEST_PORT");
         }

         destPort = ret;
      }

      if (!serverConnection && length > delim && address.charAt(delim) == ';') {
         int scan = delim + 1;
         delim = DatagramAddressBase.indexOfNextDelim(address, scan);
         if (delim <= scan) {
            throw new IllegalArgumentException("Bad SRC_PORT");
         }

         int ret = DatagramAddressBase.parseInt(address, scan, delim, 10);
         if (ret < 0 || ret > 65535) {
            throw new IllegalArgumentException("Invalid SRC_PORT");
         }

         srcPort = ret;
      }

      if (length > delim && address.charAt(delim) == '/') {
         int scan = delim + 1;
         delim = DatagramAddressBase.indexOfNextDelim(address, scan);
         if (delim < scan) {
            throw new IllegalArgumentException("Bad APN");
         }

         apn = address.substring(scan, delim);
      }

      int ret = 0;

      while (length > delim && address.charAt(delim) == '|') {
         int scan = delim + 1;
         delim = DatagramAddressBase.indexOfNextDelim(address, scan);
         if (delim <= scan) {
            throw new IllegalArgumentException("Bad TYPE");
         }

         int len = delim - scan;
         if (StringUtilities.regionMatches(address, false, scan, "UDP", 0, 3, 1701707776) && len == 3) {
            ret |= 1;
         } else if (StringUtilities.regionMatches(address, false, scan, "GPAK", 0, 4, 1701707776) && len == 4) {
            ret |= 2;
         } else {
            if (!StringUtilities.regionMatches(address, false, scan, "GCMP", 0, 4, 1701707776) || len != 4) {
               throw new IllegalArgumentException("Invalid TYPE");
            }

            ret |= 4;
         }
      }

      if (ret != 0) {
         type = ret;
      }

      String apnUsername = null;
      String apnPassword = null;
      if (delim < length) {
         String[] apnCredentials = new String[2];
         delim = parseApnCredentials(address, delim - 1, apnCredentials);
         apnUsername = apnCredentials[0];
         apnPassword = apnCredentials[1];
         if (apn == null && (apnUsername != null || apnPassword != null)) {
            throw new IllegalArgumentException("Unspecified APN");
         }
      }

      if (delim < length) {
         throw new IllegalArgumentException("Bad address");
      }

      if (resolve) {
         byte[] byteAddress = null;
         if (addressFQDN != -1) {
            byteAddress = resolveAddress(address.substring(2, addressFQDN), apn, true);
         }

         if (byteAddress != null && byteAddress.length >= 4 && DatagramAddressBase.readInt(byteAddress, 0) != -1) {
            StringBuffer buf = new StringBuffer(9 + (length - addressFQDN));
            buf.append(address.substring(0, 2));

            for (int i = 0; i < 4; i++) {
               buf.append(byteAddress[i] & 255);
               if (i != 3) {
                  buf.append('.');
               }
            }

            buf.append(address.substring(addressFQDN, length));
            address = buf.toString();
            ipAddress = UDPPacketHeader.IPv4ByteArrayToInt(byteAddress);
         }
      }

      if (udpAddress != null) {
         udpAddress._address = address;
         udpAddress.init(ipAddress, destPort, srcPort, apn, type);
         udpAddress.setApnUsername(apnUsername);
         udpAddress.setApnPassword(apnPassword);
      }

      return address;
   }

   @Override
   public String getAddress() {
      if (super._address == null) {
         byte[] ipAddress = new byte[4];
         DatagramAddressBase.writeInt(ipAddress, 0, this._ipAddress);
         super._address = makeAddress(false, ipAddress, this._destPort, this._srcPort, this._apn, this._type, this._apnUsername, this._apnPassword);
      }

      return super._address;
   }

   @Override
   public void swap() {
      int port = this._destPort;
      this._destPort = this._srcPort;
      this._srcPort = port;
      super._address = null;
   }

   @Override
   public boolean equals(Object addressBase) {
      if (addressBase == this) {
         return true;
      }

      if (!(addressBase instanceof UdpAddress)) {
         return false;
      }

      UdpAddress address = (UdpAddress)addressBase;
      return (this._ipAddress == -1 || address._ipAddress == -1 || this._ipAddress == address._ipAddress)
         && (this._destPort == -1 || address._destPort == -1 || this._destPort == address._destPort)
         && (this._srcPort == -1 || address._srcPort == -1 || this._srcPort == address._srcPort)
         && (this._apn == null || address._apn == null || this._apn.equalsIgnoreCase(address._apn))
         && (this._type == -1 || address._type == -1 || (this._type & address._type) != 0);
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 31 * hash + this._ipAddress;
      hash = 31 * hash + this._destPort;
      hash = 31 * hash + this._srcPort;
      if (this._apn != null) {
         hash = 31 * hash + this._apn.hashCode();
      }

      return 31 * hash + this._type;
   }

   public boolean compareApn(String apn, int apnOffset, int apnLength) {
      return this._apn.length() == apnLength && StringUtilities.regionMatches(this._apn, true, 0, apn, apnOffset, apnLength, 1701707776);
   }

   public static String makeAddress(boolean open, byte[] ipAddress, int destPort, int srcPort, String apn, int type) {
      return makeAddress(open, ipAddress, destPort, srcPort, apn, type, null, null);
   }

   public static String makeAddress(boolean open, byte[] ipAddress, int destPort, int srcPort, String apn, int apnOffset, int apnLength, int type) {
      return makeAddress(open, ipAddress, destPort, srcPort, apn != null ? apn.substring(apnOffset, apnOffset + apnLength) : null, type, null, null);
   }

   public static String makeAddress(boolean open, byte[] ipAddress, int destPort, int srcPort, String apn, int type, String apnUsername, String apnPassword) {
      StringBuffer buf = new StringBuffer(128);
      appendAddress(buf, open, ipAddress, destPort, srcPort, apn, type, apnUsername, apnPassword);
      return buf.toString();
   }

   public static void appendAddress(
      StringBuffer buf, boolean open, byte[] ipAddress, int destPort, int srcPort, String apn, int apnOffset, int apnLength, int type
   ) {
      appendAddress(buf, open, ipAddress, destPort, srcPort, apn != null ? apn.substring(apnOffset, apnOffset + apnLength) : null, type, null, null);
   }

   public static void appendAddress(StringBuffer buf, boolean open, byte[] ipAddress, int destPort, int srcPort, String apn, int type) {
      appendAddress(buf, open, ipAddress, destPort, srcPort, apn, type, null, null);
   }

   private static void appendAddress(
      StringBuffer buf, boolean open, byte[] ipAddress, int destPort, int srcPort, String apn, int type, String apnUsername, String apnPassword
   ) {
      if (open) {
         buf.append("udp:");
         buf.append('/');
         buf.append('/');
      }

      if (ipAddress != null && DatagramAddressBase.readInt(ipAddress, 0) != -1) {
         if (!open) {
            buf.append('/');
            buf.append('/');
         }

         buf.append(ipAddress[0] & 255);
         buf.append('.');
         buf.append(ipAddress[1] & 255);
         buf.append('.');
         buf.append(ipAddress[2] & 255);
         buf.append('.');
         buf.append(ipAddress[3] & 255);
      }

      if (destPort != -1) {
         buf.append(':');
         buf.append(destPort);
      }

      if (srcPort != -1) {
         buf.append(';');
         buf.append(srcPort);
      }

      if (apn != null) {
         buf.append('/');
         buf.append(apn);
      }

      if (type != -1) {
         if ((type & 1) != 0) {
            buf.append("|UDP");
         }

         if ((type & 2) != 0) {
            buf.append("|GPAK");
         }

         if ((type & 4) != 0) {
            buf.append("|GCMP");
         }
      }

      if (apnUsername != null) {
         buf.append(';');
         buf.append(USERNAME);
         buf.append('=');
         buf.append(apnUsername);
      }

      if (apnPassword != null) {
         buf.append(';');
         buf.append(PASSWORD);
         buf.append('=');
         buf.append(apnPassword);
      }
   }

   public static byte[] parseIpAddress(String address, int offset) {
      byte[] ipAddress = new byte[4];
      DatagramAddressBase.writeInt(ipAddress, 0, DatagramAddressBase.parseIpAddressInt(address, offset));
      return ipAddress;
   }

   public static String[] retrieveApnSettings(String address) {
      String[] apnSettings = null;
      if (address != null && address.length() > 0) {
         String apn = parseApn(address, 2);
         if (apn != null && apn.length() > 0) {
            apnSettings = new String[]{apn, null, null};
            int index = address.indexOf(59);
            if (index > 0) {
               parseApnCredentials(address, index - 1, apnSettings);
               apnSettings[1] = apnSettings[0];
               apnSettings[2] = apnSettings[1];
               apnSettings[0] = apn;
            }
         }
      }

      return apnSettings;
   }

   protected static String parseApn(String address, int offset) {
      if (address != null && offset > 0 && offset < address.length()) {
         String apn = null;
         int index = address.indexOf(47, offset);
         if (index >= 0) {
            int end = DatagramAddressBase.indexOfNextDelim(address, ++index);
            if (end > index) {
               apn = address.substring(index, end);
            }
         }

         return apn;
      } else {
         return null;
      }
   }

   protected static int parseApnCredentials(String address, int offset, String[] types) {
      if (address != null && offset >= 0 && offset < address.length()) {
         try {
            types[0] = USERNAME;
            types[1] = PASSWORD;
            int i = 0;
            int length = address.length();
            int delim = offset;
            String apnUsername = null;
            String apnPassword = null;

            while (true) {
               delim = address.indexOf(59, delim);
               if (delim != -1 && delim + 1 < length) {
                  label60: {
                     int equalSign = address.indexOf(61, delim + 1);
                     int paramLength = equalSign - (delim + 1);
                     if (equalSign != -1 && paramLength > 0) {
                        int j = 0;

                        while (
                           j < types.length
                              && (types[j] == null || !StringUtilities.regionMatches(address, true, delim + 1, types[j], 0, paramLength, 1701707776))
                        ) {
                           j++;
                        }

                        if (j < types.length) {
                           types[j] = null;
                           i++;
                           delim = address.indexOf(59, equalSign);
                           if (delim == -1) {
                              delim = length;
                           }

                           String value = address.substring(equalSign + 1, delim);
                           switch (j) {
                              case -1:
                                 break label60;
                              case 0:
                              default:
                                 apnUsername = value;
                                 break label60;
                              case 1:
                                 apnPassword = value;
                                 break label60;
                           }
                        }
                     }

                     delim++;
                  }

                  if (i < types.length && delim < length) {
                     continue;
                  }
                  break;
               }

               delim = length;
               break;
            }

            types[0] = apnUsername;
            types[1] = apnPassword;
            return delim;
         } catch (Exception e) {
            types[0] = types[1] = null;
         }
      }

      return offset;
   }

   private static byte[] resolveAddress(String address, String apn, boolean randomize) throws IOException {
      if (address != null && address.length() != 0) {
         if (apn == null) {
            apn = TunnelCredentialsProvider.getInstance().getApn();
         }

         byte[] host = null;

         try {
            int apnId = RadioInfo.getAccessPointNumber(apn);
            Vector hosts = DNSResolverIPv4.instance().getAddressByHostname(address, apnId);
            if (hosts != null && hosts.size() > 0) {
               host = (byte[])hosts.elementAt(!randomize ? 0 : RandomSource.getInt(hosts.size()));
            }

            if (host == null) {
               throw new IOException("DNS query returned no results");
            } else {
               return host;
            }
         } catch (RadioException re) {
            throw new IOException("APN failure");
         }
      } else {
         return null;
      }
   }
}
