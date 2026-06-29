package net.rim.device.api.system;

public final class MobitexPacketHeader implements RadioPacketHeader {
   public static final int TS_MESSAGE_OK;
   public static final int TS_MESSAGE_FROM_MAILBOX;
   public static final int TS_MESSAGE_IN_MAILBOX;
   public static final int TS_CANNOT_BE_REACHED;
   public static final int TS_ILLEGAL_MESSAGE;
   public static final int TS_NETWORK_CONGESTED;
   public static final int TS_TECHNICAL_ERROR;
   public static final int TS_DESTINATION_BUSY;
   public static final int PACKET_TYPE_TEXT;
   public static final int PACKET_TYPE_DATA;
   public static final int PACKET_TYPE_STATUS;
   public static final int PACKET_TYPE_HPDATA;
   public static final int FLAG_MAILBOX;
   public static final int FLAG_POSACK;
   public static final int FLAG_SENDLIST;
   public static final int FLAG_UNKNOWN;

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
