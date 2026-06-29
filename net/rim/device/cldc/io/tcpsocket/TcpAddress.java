package net.rim.device.cldc.io.tcpsocket;

import java.io.IOException;
import java.util.Vector;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.system.RadioException;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.cldc.io.dns.DNSResolverIPv4;
import net.rim.device.internal.io.tunnel.TunnelCredentialsProvider;
import net.rim.device.internal.system.TCPPacketHeader;

final class TcpAddress extends DatagramAddressBase {
   protected int _ipAddress;
   protected int _port;
   protected int _localPort;
   protected String _apnName;
   protected String _apnUsername;
   protected String _apnPassword;
   protected boolean _isListenAddress;
   public static int TCP_PORT_NONE = -1;
   public static int TCP_IP_ADDRESS_NONE = -1;
   protected static String SLASH_SLASH = "//";
   protected static String COMPARISON_STRING = "://";

   public TcpAddress() {
   }

   public TcpAddress(byte[] ipAddress, int destPort) {
      this(ipAddress, destPort, TCP_PORT_NONE, null);
   }

   public TcpAddress(byte[] ipAddress, int destPort, int srcPort) {
      this(ipAddress, destPort, srcPort, null);
   }

   public TcpAddress(byte[] ipAddress, int destPort, String apn) {
      this(ipAddress, destPort, TCP_PORT_NONE, apn, 0, apn != null ? apn.length() : 0);
   }

   public TcpAddress(byte[] ipAddress, int destPort, int srcPort, String apn) {
      this(ipAddress, destPort, srcPort, apn, 0, apn != null ? apn.length() : 0);
   }

   public TcpAddress(byte[] ipAddress, int destPort, int srcPort, String apn, int apnOffset, int apnLength) {
      if (apn != null && apn.length() > apnLength) {
         apn = apn.substring(apnOffset, apnLength);
      }

      this.processApnInfo(apn);
      this._ipAddress = TCPPacketHeader.IPv4ByteArrayToInt(ipAddress);
      if (this._ipAddress == TCP_IP_ADDRESS_NONE) {
         this._isListenAddress = true;
         if (destPort != TCP_PORT_NONE) {
            destPort = TCP_PORT_NONE;
         }
      }

      this._port = destPort;
      if (this._isListenAddress) {
         if (srcPort == TCP_PORT_NONE) {
            this._localPort = srcPort;
         } else {
            this._localPort = srcPort;
         }
      } else if (srcPort != TCP_PORT_NONE && destPort != TCP_PORT_NONE) {
         this._localPort = srcPort;
         this._port = destPort;
      } else {
         if (destPort == TCP_PORT_NONE) {
            throw new RuntimeException();
         }

         this._localPort = destPort;
      }

      super._address = makeAddress(false, this._ipAddress != 0 ? ipAddress : null, destPort, srcPort);
   }

   public TcpAddress(String address) {
      this(address, null);
   }

   public TcpAddress(String address, String apn) {
      if (address.length() == 0) {
         this._ipAddress = TCP_IP_ADDRESS_NONE;
         this._port = TCP_PORT_NONE;
         this._localPort = TCP_PORT_NONE;
         this._apnName = null;
         super._address = "";
      } else {
         this.processApnInfo(apn);
         this.parseAddress(address);
      }
   }

   private final void processApnInfo(String apn) {
      if (apn != null) {
         this._apnName = apn;
      } else {
         this._apnName = TunnelCredentialsProvider.getInstance().getApn();
         this._apnUsername = TunnelCredentialsProvider.getInstance().getApnUsername();
         this._apnPassword = TunnelCredentialsProvider.getInstance().getApnPassword();
      }
   }

   public final int getConnectionIpAddress() {
      return this.getIpAddress();
   }

   public final int getConnectionLocalPort() {
      return this.getLocalPort();
   }

   public final void setConnectionLocalPort(int port) {
      this.setLocalPort(port);
   }

   public final int getConnectionDestinationPort() {
      return this.getDestPort();
   }

   public final String getConnectionApn() {
      return this.getApnName();
   }

   protected final void parseAddress(String address) throws IOException {
      int scan = 0;
      int delim = 0;
      int length = address.length();
      boolean haveDestPort = false;
      int index = 0;
      if (address.startsWith(SLASH_SLASH)) {
         index = 2;
      } else {
         index = address.indexOf(COMPARISON_STRING) + 1;
      }

      if (address.indexOf(58, index) == -1 && address.equals(SLASH_SLASH)) {
         address = address + ':';
      }

      if (length >= 2 && address.charAt(0) == '/' && address.charAt(1) == '/') {
         this._ipAddress = 0;
         delim = address.indexOf(58, 2);
         if (delim != -1 && delim != 2) {
            this._isListenAddress = false;
            if (!DatagramAddressBase.isDomainName(address, 2, delim)) {
               this._ipAddress = DatagramAddressBase.parseIpAddressInt(address, 2);
            } else {
               byte[] host = null;
               Vector hosts = DNSResolverIPv4.instance().getAddressByHostname(address.substring(2, delim), this._apnName);
               if (hosts != null && hosts.size() > 0) {
                  host = (byte[])hosts.elementAt(RandomSource.getInt(hosts.size()));
               }

               if (host == null) {
                  throw new IOException("DNS query returned no results");
               }

               StringBuffer sb = new StringBuffer(15);

               for (int i = 0; i < 4; i++) {
                  sb.append(host[i] & 255);
                  if (i != 3) {
                     sb.append('.');
                  }
               }

               String h = sb.toString();
               this._ipAddress = DatagramAddressBase.parseIpAddressInt(h, 0);
               sb = new StringBuffer(SLASH_SLASH.length() + h.length() + (address.length() - delim));
               sb.append(SLASH_SLASH);
               sb.append(h);
               sb.append(address.substring(delim, address.length()));
               address = sb.toString();
               length = address.length();
            }

            haveDestPort = true;
         } else {
            this._isListenAddress = true;
         }
      }

      this._port = this._localPort = TCP_PORT_NONE;
      delim = address.indexOf(58, 2);
      if ((haveDestPort || !haveDestPort && delim == 2) && length > delim && address.charAt(delim) == ':') {
         scan = delim + 1;
         delim = DatagramAddressBase.indexOfNextDelim(address, scan);
         if (delim <= scan) {
            throw new IllegalArgumentException("Bad DEST_PORT");
         }

         int ret = DatagramAddressBase.parseInt(address, scan, delim, 10);
         if (ret < 0 || ret > 65535) {
            throw new IllegalArgumentException("Invalid DEST_PORT");
         }

         if (haveDestPort) {
            this._port = ret;
         } else {
            this._localPort = ret;
         }

         if (haveDestPort) {
            if (length > delim && address.charAt(delim) == ';') {
               scan = delim + 1;
               delim = DatagramAddressBase.indexOfNextDelim(address, scan);
               if (delim <= scan) {
                  throw new IllegalArgumentException("Bad SRC_PORT");
               }

               try {
                  int ret0 = DatagramAddressBase.parseInt(address, scan, delim, 10);
                  if (ret0 < 0 || ret0 > 65535) {
                     throw new IllegalArgumentException("Invalid SRC_PORT");
                  }

                  this._localPort = ret0;
                  ControlledAccess.assertRRISignatures(true);
               } catch (IllegalArgumentException iae) {
                  this._localPort = TCP_PORT_NONE;
               } catch (ControlledAccessException cae) {
                  this._localPort = TCP_PORT_NONE;
               }
            } else {
               this._localPort = TCP_PORT_NONE;
            }
         }
      }

      super._address = makeAddress(false, this._ipAddress != 0 ? TCPPacketHeader.IPv4IntToByteArray(this._ipAddress) : null, this._port, this._localPort);
   }

   public final void setLocalPort(int port) {
      if (port != this.getLocalPort()) {
         super._address = makeAddress(false, this._ipAddress != 0 ? TCPPacketHeader.IPv4IntToByteArray(this._ipAddress) : null, this._port, port);
      }

      this._localPort = port;
   }

   public final String getConnectionAddress() {
      return makeAddress(true, this._ipAddress != 0 ? TCPPacketHeader.IPv4IntToByteArray(this._ipAddress) : null, this._port, this._localPort);
   }

   public final int getIpAddress() {
      return this._ipAddress;
   }

   @Override
   public final boolean equals(Object dgramAddress) {
      if (dgramAddress == this) {
         return true;
      }

      if (!(dgramAddress instanceof TcpAddress)) {
         return false;
      }

      TcpAddress address = (TcpAddress)dgramAddress;
      return (this._ipAddress == TCP_IP_ADDRESS_NONE || address._ipAddress == TCP_IP_ADDRESS_NONE || this._ipAddress == address._ipAddress)
         && (this._port == TCP_PORT_NONE || address._port == TCP_PORT_NONE || this._port == address._port)
         && (this._localPort == TCP_PORT_NONE || address._localPort == TCP_PORT_NONE || this._localPort == address._localPort)
         && (this._apnName == null || this.compareApn(address._apnName));
   }

   @Override
   public final int hashCode() {
      int hash = 7;
      hash = 31 * hash + this._ipAddress;
      hash = 31 * hash + this._port;
      hash = 31 * hash + this._localPort;
      if (this._apnName != null) {
         hash = 31 * hash + this._apnName.hashCode();
      }

      return hash;
   }

   public final String getApnName() {
      return this._apnName;
   }

   public final void setApnName(String apn) {
      this._apnName = apn;
   }

   public final String getApnUsername() {
      return this._apnUsername;
   }

   public final void setApnUsername(String apnUsername) {
      this._apnUsername = apnUsername;
   }

   public final String getApnPassword() {
      return this._apnPassword;
   }

   public final void setApnPassword(String apnPassword) {
      this._apnPassword = apnPassword;
   }

   public final int getLocalPort() {
      return this._localPort;
   }

   public final int getDestPort() {
      return this._port;
   }

   public final boolean isListenAddress() {
      return this._isListenAddress;
   }

   public final boolean compareApn(String apn) {
      return this._apnName.equalsIgnoreCase(apn);
   }

   public static final String makeAddress(boolean open, byte[] ipAddress, int destPort, int srcPort) {
      return makeAddress(open, ipAddress, destPort, srcPort, null, 0, 0);
   }

   public static final String makeAddress(boolean open, byte[] ipAddress, int destPort, int srcPort, String apn) {
      return makeAddress(open, ipAddress, destPort, srcPort, apn, 0, apn != null ? apn.length() : 0);
   }

   public static final String makeAddress(boolean open, byte[] ipAddress, int destPort, int srcPort, String apn, int apnOffset, int apnLength) {
      StringBuffer buf = new StringBuffer(128);
      if (open) {
         buf.append("tcpcocket://");
      }

      if (ipAddress != null && DatagramAddressBase.readInt(ipAddress, 0) != TCP_IP_ADDRESS_NONE) {
         if (!open) {
            buf.append('/');
            buf.append('/');
         }

         convertIpAddressBytesToStringBuffer(ipAddress, buf);
      }

      if (destPort != TCP_PORT_NONE) {
         buf.append(':');
         buf.append(destPort);
      }

      if (srcPort != TCP_PORT_NONE) {
         buf.append((char)(destPort != -1 ? ';' : ':'));
         buf.append(srcPort);
      }

      return buf.toString();
   }

   public static final void convertIpAddressBytesToStringBuffer(byte[] ipAddress, StringBuffer buf) {
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

   public final String getLocalAddress() throws IOException {
      if (this._apnName == null) {
         throw new IOException();
      }

      StringBuffer buf = new StringBuffer(20);

      try {
         convertIpAddressBytesToStringBuffer(RadioInfo.getIPAddress(RadioInfo.getAccessPointNumber(this._apnName)), buf);
      } catch (RadioException e) {
         throw new IOException("Can't get local ip address");
      }

      return buf.toString();
   }
}
