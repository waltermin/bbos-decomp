package net.rim.device.internal.system;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RadioPacketHeader;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.DebugSupport;

public final class TCPPacketHeader implements RadioPacketHeader {
   public int _sourceAddress;
   public int _destinationAddress;
   private int _sourcePort;
   private int _destinationPort;
   public int _accessPointNumber;
   public int _sequenceNumber;
   public int _acknowledgementNumber;
   public int _flags;
   public int _window;
   public int _urgentPointer;
   public int _dataOffset;
   public int _controlCode;
   public int _controlDescription;
   public int _socketID;
   public boolean _isSimulTcpPacket;
   private static final int MIN_IP_HEADER_SIZE;
   private static final int MIN_TCP_HEADER_SIZE;
   private static int MAX_TCP_PAYLOAD_SIZE = 1400;

   @Override
   public final void reset() {
      this._sourceAddress = 0;
      this._destinationAddress = 0;
      this._sourcePort = 0;
      this._destinationPort = 0;
      this._accessPointNumber = 0;
      this._sequenceNumber = 0;
      this._acknowledgementNumber = 0;
      this._flags = 0;
      this._window = 0;
      this._urgentPointer = 0;
      this._dataOffset = 0;
      if (this._isSimulTcpPacket) {
         this._controlCode = 0;
         this._controlDescription = 0;
         this._socketID = 0;
      }
   }

   public final byte[] getSourceAddress() {
      return IPv4IntToByteArray(this._sourceAddress);
   }

   public final byte[] getDestinationAddress() {
      return IPv4IntToByteArray(this._destinationAddress);
   }

   public final void setSourceAddress(byte[] address) {
      this._sourceAddress = IPv4ByteArrayToInt(address);
   }

   public final void setDestinationAddress(byte[] address) {
      this._destinationAddress = IPv4ByteArrayToInt(address);
   }

   public final void setSourcePort(int sourcePort) {
      this.checkPortRange(sourcePort);
      this._sourcePort = sourcePort;
   }

   public final int getSourcePort() {
      return this._sourcePort;
   }

   public final void setDestinationPort(int destinationPort) {
      this.checkPortRange(destinationPort);
      this._destinationPort = destinationPort;
   }

   public final int getDestinationPort() {
      return this._destinationPort;
   }

   private final void checkPortRange(int port) {
      if (port < 0 || port > 65535) {
         throw new IllegalArgumentException("port out range:" + port);
      }
   }

   public static final byte[] IPv4IntToByteArray(int address) {
      return new byte[]{(byte)(address >>> 24 & 0xFF), (byte)(address >>> 16 & 0xFF), (byte)(address >>> 8 & 0xFF), (byte)(address & 0xFF)};
   }

   public static final int IPv4ByteArrayToInt(byte[] address) {
      if (address.length < 4) {
         throw new IllegalArgumentException("invalid IP address: " + address);
      } else {
         return (address[0] & 0xFF) << 24 | (address[1] & 0xFF) << 16 | (address[2] & 0xFF) << 8 | address[3] & 0xFF;
      }
   }

   public static final int getMaxPacketSize() {
      return MAX_TCP_PAYLOAD_SIZE;
   }

   public static final int getMaxPacketSize(int apnId) {
      if (apnId >= 0) {
         byte[] mtu = RadioInternal.getNetworkParameter(apnId, 102, 0);
         if (mtu != null && mtu.length > 1) {
            int value = (mtu[1] & 255) << 8 | mtu[0] & 255;
            if (value > 0) {
               return Math.min(value, getMaxPacketSize());
            }
         }
      }

      return getMaxPacketSize();
   }

   static {
      try {
         if (DeviceInfo.isSimulator() && StringUtilities.strEqualIgnoreCase(DebugSupport.getenv("raw-tcp"), "true", 1701707776)) {
            MAX_TCP_PAYLOAD_SIZE = 1060;
            return;
         }
      } catch (Throwable var1) {
      }
   }
}
