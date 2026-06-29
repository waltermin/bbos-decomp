package net.rim.device.api.system;

public final class MobitexPacketHeader implements RadioPacketHeader {
   public static final int TS_MESSAGE_OK = 0;
   public static final int TS_MESSAGE_FROM_MAILBOX = 1;
   public static final int TS_MESSAGE_IN_MAILBOX = 2;
   public static final int TS_CANNOT_BE_REACHED = 3;
   public static final int TS_ILLEGAL_MESSAGE = 4;
   public static final int TS_NETWORK_CONGESTED = 5;
   public static final int TS_TECHNICAL_ERROR = 6;
   public static final int TS_DESTINATION_BUSY = 7;
   public static final int PACKET_TYPE_TEXT = 1;
   public static final int PACKET_TYPE_DATA = 2;
   public static final int PACKET_TYPE_STATUS = 3;
   public static final int PACKET_TYPE_HPDATA = 4;
   public static final int FLAG_MAILBOX = 1;
   public static final int FLAG_POSACK = 2;
   public static final int FLAG_SENDLIST = 4;
   public static final int FLAG_UNKNOWN = 8;

   public MobitexPacketHeader() {
      throw new UnsupportedOperationException();
   }

   @Override
   public final void reset() {
   }

   public final void setSourceAddress(int sourceAddress) {
   }

   public final int getSourceAddress() {
      return 0;
   }

   public final void setDestinationAddress(int destinationAddress) {
   }

   public final int getDestinationAddress() {
      return 0;
   }

   public final void setPacketType(int packetType) {
   }

   public final int getPacketType() {
      return 0;
   }

   public final void setPacketFlags(int packetFlags) {
   }

   public final int getPacketFlags() {
      return 0;
   }

   public final void setHPID(int hpid) {
   }

   public final int getHPID() {
      return 0;
   }

   public final int getTrafficState() {
      return 0;
   }

   public static final int getMaxPacketSize() {
      return 0;
   }
}
