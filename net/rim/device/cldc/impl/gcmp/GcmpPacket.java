package net.rim.device.cldc.impl.gcmp;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.DataBuffer;

public final class GcmpPacket {
   public int mask;
   public byte[] challengeMessage;
   public byte challengeResponseScheme;
   public byte[] challengeResponse;
   public byte challengeResult;
   public String apn;
   public byte[] ipAddress;
   public int destinationPort;
   public int sourcePort;
   public int pingTmo;
   public boolean conditionalPinging;
   public int tunnelHoldTmo;
   public int ackRequestId;
   public int ackResponseId;
   public boolean ackInfo;
   public static final int MASK_INFO_REQUEST = 1;
   public static final int MASK_CHALLENGE_MESSAGE = 2;
   public static final int MASK_CHALLENGE_RESPONSE = 4;
   public static final int MASK_CHALLENGE_RESULT = 8;
   public static final int MASK_CHALLENGE_REQUEST = 16;
   public static final int MASK_TUNNEL_ID = 32;
   public static final int MASK_NETWORK_ADDRESS = 64;
   public static final int MASK_PING_TMO = 128;
   public static final int MASK_TUNNEL_HOLD_TMO = 256;
   public static final int MASK_SHUTTING_DOWN = 512;
   public static final int MASK_ACK_REQUEST = 1024;
   public static final int MASK_ACK_RESPONSE = 2048;
   public static final int MASK_ACK_INFO = 4096;
   public static final byte CHALLENGE_SCHEME_HMAC = 2;
   public static final byte CHALLENGE_RESULT_FAILED = 0;
   public static final byte CHALLENGE_RESULT_PASSED = 1;
   public static final byte CHALLENGE_RESULT_NOT_SUPPORTED = 2;
   private static final byte TYPE_TERMINATOR = 0;
   private static final byte TYPE_INFO_REQUEST = 1;
   private static final byte TYPE_CHALLENGE_MESSAGE = 2;
   private static final byte TYPE_CHALLENGE_RESPONSE = 3;
   private static final byte TYPE_CHALLENGE_RESULT = 4;
   private static final byte TYPE_CHALLENGE_REQUEST = 5;
   private static final byte TYPE_TUNNEL_ID = 64;
   private static final byte TYPE_NETWORK_ADDRESS = 65;
   private static final byte TYPE_PING_TMO = 66;
   private static final byte TYPE_TUNNEL_HOLD_TMO = 67;
   private static final byte TYPE_SHUTTING_DOWN = 68;
   private static final byte TYPE_ACK_REQUEST = 69;
   private static final byte TYPE_ACK_RESPONSE = 70;
   private static final byte TYPE_ACK_INFO = 71;
   private static final byte BYTE_FALSE = 0;
   private static final byte BYTE_TRUE = 1;

   public final void reset() {
      this.mask = 0;
      this.challengeMessage = null;
      this.challengeResponseScheme = 0;
      this.challengeResponse = null;
      this.challengeResult = 0;
      this.apn = null;
      this.ipAddress = null;
      this.destinationPort = 0;
      this.sourcePort = 0;
      this.pingTmo = 0;
      this.conditionalPinging = false;
      this.tunnelHoldTmo = 0;
      this.ackRequestId = 0;
      this.ackResponseId = 0;
      this.ackInfo = false;
   }

   public final void encode(DataBuffer buf) {
      buf.setLength(0);
      if ((this.mask & 16) != 0) {
         buf.writeByte(5);
         buf.writeByte(0);
      }

      if ((this.mask & 4) != 0) {
         buf.writeByte(3);
         if (this.challengeResponse != null) {
            buf.writeCompressedInt(1 + this.challengeResponse.length);
            buf.writeByte(this.challengeResponseScheme);
            buf.write(this.challengeResponse);
         } else {
            buf.writeByte(0);
         }
      }

      if ((this.mask & 2) != 0) {
         buf.writeByte(2);
         if (this.challengeMessage != null) {
            buf.writeCompressedInt(this.challengeMessage.length);
            buf.write(this.challengeMessage);
         } else {
            buf.writeByte(0);
         }
      }

      if ((this.mask & 128) != 0) {
         buf.writeByte(66);
         buf.writeByte(DataBuffer.getCompressedIntSize(this.pingTmo) + 1);
         buf.writeCompressedInt(this.pingTmo);
         buf.writeByte(this.conditionalPinging ? 1 : 0);
      }

      if ((this.mask & 256) != 0) {
         buf.writeByte(67);
         buf.writeByte(DataBuffer.getCompressedIntSize(this.tunnelHoldTmo));
         buf.writeCompressedInt(this.tunnelHoldTmo);
      }

      if ((this.mask & 4096) != 0) {
         buf.writeByte(71);
         buf.writeByte(1);
         buf.writeByte(this.ackInfo ? 1 : 0);
      }

      if ((this.mask & 1024) != 0) {
         buf.writeByte(69);
         buf.writeByte(4);
         buf.writeInt(this.ackRequestId);
      }

      if ((this.mask & 2048) != 0) {
         buf.writeByte(70);
         buf.writeByte(4);
         buf.writeInt(this.ackResponseId);
      }

      if ((this.mask & 512) != 0) {
         buf.writeByte(68);
         buf.writeByte(0);
      }

      buf.writeByte(0);
      buf.rewind();
   }

   public final void decode(DataBuffer buf) {
      try {
         while (true) {
            byte type = buf.readByte();
            if (type == 0) {
               if (buf.available() != 0) {
                  EventLogger.logEvent(-1673931206114386243L, 1195594289, 3);
               }

               return;
            }

            int length = buf.readCompressedInt();
            switch (type) {
               case 1:
                  this.mask |= 1;
                  if (length != 0) {
                     EventLogger.logEvent(-1673931206114386243L, 1195594289, 3);
                     buf.skipBytes(length);
                  }
                  break;
               case 2:
                  this.mask |= 2;
                  if (length > 0 && length <= 200) {
                     this.challengeMessage = new byte[length];
                     buf.readFully(this.challengeMessage, 0, length);
                     break;
                  }

                  EventLogger.logEvent(-1673931206114386243L, 1195594289, 3);
                  buf.skipBytes(length);
                  break;
               case 3:
                  this.mask |= 4;
                  if (length > 0 && length <= 200) {
                     this.challengeResponseScheme = buf.readByte();
                     this.challengeResponse = new byte[length - 1];
                     buf.readFully(this.challengeResponse, 0, length - 1);
                     break;
                  }

                  EventLogger.logEvent(-1673931206114386243L, 1195594289, 3);
                  buf.skipBytes(length);
                  break;
               case 4:
                  this.mask |= 8;
                  if (length == 1) {
                     this.challengeResult = buf.readByte();
                     break;
                  }

                  EventLogger.logEvent(-1673931206114386243L, 1195594289, 3);
                  buf.skipBytes(length);
                  break;
               case 64:
                  this.mask |= 32;
                  if (length >= 0 && length <= 127) {
                     this.apn = (String)(new Object(buf.getArray(), buf.getArrayPosition(), length));
                  } else {
                     EventLogger.logEvent(-1673931206114386243L, 1195594289, 3);
                  }

                  buf.skipBytes(length);
                  break;
               case 65:
                  this.mask |= 64;
                  if (length == 8) {
                     this.ipAddress = new byte[4];
                     buf.readFully(this.ipAddress, 0, 4);
                     this.destinationPort = buf.readUnsignedShort();
                     this.sourcePort = buf.readUnsignedShort();
                     break;
                  }

                  EventLogger.logEvent(-1673931206114386243L, 1195594289, 3);
                  buf.skipBytes(length);
                  break;
               case 66:
                  this.mask |= 128;
                  if (length == 0) {
                     break;
                  }

                  int pos = buf.getPosition();
                  int ping = buf.readCompressedInt();
                  boolean cond = buf.readByte() != 0;
                  if (buf.getPosition() == pos + length) {
                     this.pingTmo = ping;
                     this.conditionalPinging = cond;
                     break;
                  }

                  EventLogger.logEvent(-1673931206114386243L, 1195594289, 3);
                  buf.setPosition(pos);
                  buf.skipBytes(length);
                  break;
               case 67:
                  this.mask |= 256;
                  if (length == 0) {
                     break;
                  }

                  int pos = buf.getPosition();
                  int hold = buf.readCompressedInt();
                  if (buf.getPosition() == pos + length) {
                     this.tunnelHoldTmo = hold;
                     break;
                  }

                  EventLogger.logEvent(-1673931206114386243L, 1195594289, 3);
                  buf.setPosition(pos);
                  buf.skipBytes(length);
                  break;
               case 69:
                  this.mask |= 1024;
                  if (length == 4) {
                     this.ackRequestId = buf.readInt();
                     break;
                  }

                  EventLogger.logEvent(-1673931206114386243L, 1195594289, 3);
                  buf.skipBytes(length);
                  break;
               case 70:
                  this.mask |= 2048;
                  if (length == 4) {
                     this.ackResponseId = buf.readInt();
                     break;
                  }

                  EventLogger.logEvent(-1673931206114386243L, 1195594289, 3);
                  buf.skipBytes(length);
                  break;
               case 71:
                  this.mask |= 4096;
                  if (length == 1) {
                     this.ackInfo = buf.readByte() != 0;
                  } else if (length != 0) {
                     EventLogger.logEvent(-1673931206114386243L, 1195594289, 3);
                     buf.skipBytes(length);
                  }
                  break;
               default:
                  EventLogger.logEvent(-1673931206114386243L, 1195594290, 3);
                  buf.skipBytes(length);
            }
         }
      } finally {
         EventLogger.logEvent(-1673931206114386243L, 1195594291, 3);
         return;
      }
   }
}
