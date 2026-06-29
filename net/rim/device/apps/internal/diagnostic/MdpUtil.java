package net.rim.device.apps.internal.diagnostic;

import net.rim.device.api.util.DataBuffer;

public final class MdpUtil {
   public static final int MAXIMUM_REFERENCE = 127;
   public static final int TYPE_PING = 1;
   public static final int TYPE_DATA = 2;
   public static final int TYPE_DATAGRAM_ACK = 3;
   public static final int TYPE_PACKET_ACK = 4;
   public static final int TYPE_ABORT_DATAGRAM = 5;
   public static final int TYPE_REFUSE_DATAGRAM = 6;
   public static final int TYPE_STATUS_REQUEST = 7;
   public static final int TYPE_RECEIPT_CONFIRM = 8;
   public static final int TYPE_REFUSE_VERSION = 10;
   public static final int TYPE_PARAM_REQUEST = 13;
   public static final int TYPE_PARAM_RESPONSE = 14;
   public static final int TYPE_ERROR_UNKNOWN = -1;
   public static final int TYPE_ERROR_REFUSE = -2;
   public static final int TYPE_ERROR_VERSION = -3;
   private static final byte CTRL_ABORT_DATAGRAM = 0;
   private static final byte CTRL_REFUSE_DATAGRAM = 1;
   private static final byte CTRL_STATUS_REQUEST = 2;
   private static final byte CTRL_RECEIPT_CONFIRM = 3;
   private static final byte CTRL_REFUSE_VERSION = 5;
   private static final byte CTRL_PARAM_REQUEST = 12;
   private static final byte CTRL_PARAM_RESPONSE = 13;
   private static final int VERSION = 0;
   private static final int MASK_MORE = 128;
   private static final int MASK_REFERENCE = 127;
   private static final int MASK_PACK_REQ = 128;
   private static final int MASK_SEQUENCE = 127;
   private static final int MASK_EACK_REQ = 128;
   private static final int MASK_MAX_SEQUENCE = 127;
   private static final int MASK_DACK_REQ = 128;
   private static final int MASK_OACK_REQ = 64;
   private static final int MASK_VERSION = 7;
   private static final int REF_PING_PACKET = 0;
   private static final int SEQ_CTRL_PACKET = 127;
   private static final int MAX_PACKETS = 127;
   public static final int HEADER_SIZE_1 = 8;
   private static final int HEADER_SIZE = 3;
   private static final int HEADER_DIFF = 5;
   private static final int PACKET_SIZE_GPRS = 1300;
   private static final int PACKET_SIZE_CDMA = 1300;
   private static final int PACKET_SIZE_IDEN = 1300;
   private static final int PACKET_SIZE_802_11 = 1300;
   private static final int PACKET_SIZE_MOBITEX = 512;

   public static final void encode(DataBuffer buf, MdpUtil$DatagramInfo info) {
      buf.reset();
      switch (info.type) {
         case 0:
         case 5:
         case 9:
         case 11:
         case 12:
            break;
         case 1:
         default:
            buf.writeByte((info.moreFlag ? 128 : 0) | 0);
            return;
         case 2:
            int index = getOffsetFromSequence(info.sequence, info.nativeLength);
            int header = info.sequence == 0 ? 8 : 3;
            int chunk = Math.min(info.length - index, info.nativeLength - header);
            buf.ensureLength(chunk + header);
            buf.writeByte((info.moreFlag ? 128 : 0) | info.reference);
            buf.writeByte((info.packetAckFlag ? 128 : 0) | info.sequence);
            buf.writeByte(info.maxSequence);
            if (info.sequence == 0) {
               buf.writeByte((info.datagramAckFlag ? 128 : 0) | (info.optimizedAckFlag ? 64 : 0) | 0);
               buf.writeShort(info.srcPort);
               buf.writeShort(info.destPort);
            }

            buf.write(info.data, info.offset + index, chunk);
            return;
         case 3:
            buf.writeByte(info.reference);
            return;
         case 4:
            buf.ensureLength(2);
            buf.writeByte(info.reference);
            buf.writeByte(info.sequence);
            return;
         case 6:
            buf.ensureLength(3);
            buf.writeByte(info.reference);
            buf.writeByte(127);
            buf.writeByte(1);
            return;
         case 7:
            buf.ensureLength(3);
            buf.writeByte((info.moreFlag ? 128 : 0) | info.reference);
            buf.writeByte((info.packetAckFlag ? 128 : 0) | 127);
            buf.writeByte(2);
            return;
         case 8:
            buf.ensureLength(3 + info.length);
            buf.writeByte(info.reference);
            buf.writeByte(127);
            buf.writeByte(3);
            if (info.data != null) {
               buf.write(info.data, 0, info.length);
               return;
            }
            break;
         case 10:
            buf.ensureLength(4);
            buf.writeByte(info.reference);
            buf.writeByte(127);
            buf.writeByte(5);
            buf.writeByte(0);
            break;
         case 13:
            buf.setLength(3);
            buf.writeByte(info.reference);
            buf.writeByte((info.packetAckFlag ? 128 : 0) | 127);
            buf.writeByte(12);
            return;
      }
   }

   public static final int getOffsetFromSequence(int sequence, int nativeLength) {
      return sequence == 0 ? 0 : (nativeLength - 3) * sequence - 5;
   }

   public static final MdpUtil$DatagramInfo makeDatagramInfo(int type) {
      MdpUtil$DatagramInfo info = new MdpUtil$DatagramInfo();
      info.type = type;
      return info;
   }
}
