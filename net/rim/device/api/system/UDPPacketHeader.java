package net.rim.device.api.system;

public final class UDPPacketHeader implements RadioPacketHeader {
   private int _sourceAddress;
   private int _destinationAddress;
   private int _sourcePort;
   private int _destinationPort;
   private int _accessPointNumber;

   public UDPPacketHeader() {
      this.reset();
   }

   @Override
   public final void reset() {
      this._sourceAddress = 0;
      this._destinationAddress = 0;
      this._sourcePort = 0;
      this._destinationPort = 0;
      this._accessPointNumber = 0;
   }

   public final byte[] getSourceAddress() {
      return IPv4IntToByteArray(this._sourceAddress);
   }

   public final byte[] getDestinationAddress() {
      return IPv4IntToByteArray(this._destinationAddress);
   }

   public final int getSourcePort() {
      return this._sourcePort;
   }

   public final int getDestinationPort() {
      return this._destinationPort;
   }

   public final int getAccessPointNumber() {
      return this._accessPointNumber;
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

   public final void setDestinationPort(int destinationPort) {
      this.checkPortRange(destinationPort);
      this._destinationPort = destinationPort;
   }

   public final void setAccessPointNumber(int accessPointNumber) {
      this._accessPointNumber = accessPointNumber;
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
      }

      int ch1 = address[0] & 255;
      int ch2 = address[1] & 255;
      int ch3 = address[2] & 255;
      int ch4 = address[3] & 255;
      return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
   }

   public static final int getMaxPacketSize() {
      return 1492;
   }
}
