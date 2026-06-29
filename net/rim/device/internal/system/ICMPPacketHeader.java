package net.rim.device.internal.system;

import net.rim.device.api.system.RadioPacketHeader;

public final class ICMPPacketHeader implements RadioPacketHeader {
   private int _sourceAddress;
   private int _destinationAddress;
   private int _accessPointNumber;
   private byte _type;
   private byte _code;
   private short _checksum;
   private byte _ttl;
   public static final int TYPE_ECHO_REPLY = 0;
   public static final int TYPE_DEST_UNREACHABLE = 3;
   public static final int TYPE_SOURCE_QUENCH = 4;
   public static final int TYPE_REDIRECT = 5;
   public static final int TYPE_ECHO = 8;
   public static final int TYPE_TIME_EXCEEDED = 11;
   public static final int TYPE_PARAMETER_PROBLEM = 12;
   public static final int TYPE_TIMESTAMP = 13;
   public static final int TYPE_TIMESTAMP_REPLY = 14;
   public static final int TYPE_INFO_REQUEST = 15;
   public static final int TYPE_INFO_REPLY = 16;
   public static final int MAX_ICMP_PACKET_SIZE = 1488;

   public ICMPPacketHeader() {
      this.reset();
   }

   @Override
   public final void reset() {
      this._sourceAddress = 0;
      this._destinationAddress = 0;
      this._accessPointNumber = 0;
      this._type = 0;
      this._code = 0;
      this._checksum = 0;
   }

   public final byte[] getSourceAddress() {
      return IPv4IntToByteArray(this._sourceAddress);
   }

   public final byte[] getDestinationAddress() {
      return IPv4IntToByteArray(this._destinationAddress);
   }

   public final int getAccessPointNumber() {
      return this._accessPointNumber;
   }

   public final int getType() {
      return this._type & 0xFF;
   }

   public final int getCode() {
      return this._code & 0xFF;
   }

   public final int getChecksum() {
      return this._checksum & 0xFF;
   }

   public final byte getTTL() {
      return this._ttl;
   }

   public final void setSourceAddress(byte[] address) {
      this._sourceAddress = IPv4ByteArrayToInt(address);
   }

   public final void setDestinationAddress(byte[] address) {
      this._destinationAddress = IPv4ByteArrayToInt(address);
   }

   public final void setAccessPointNumber(int accessPointNumber) {
      this._accessPointNumber = accessPointNumber;
   }

   public final void setType(int type) {
      this._type = (byte)type;
   }

   public final void setCode(int code) {
      this._code = (byte)code;
   }

   public final void setChecksum(int checksum) {
      this._checksum = (short)checksum;
   }

   public final void setTTL(byte ttl) {
      this._ttl = ttl;
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
}
