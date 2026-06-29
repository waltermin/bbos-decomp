package net.rim.device.apps.internal.diagnostic;

import net.rim.device.api.util.DataBuffer;

public final class MdpUtil {
   public static final int MAXIMUM_REFERENCE;
   public static final int TYPE_PING;
   public static final int TYPE_DATA;
   public static final int TYPE_DATAGRAM_ACK;
   public static final int TYPE_PACKET_ACK;
   public static final int TYPE_ABORT_DATAGRAM;
   public static final int TYPE_REFUSE_DATAGRAM;
   public static final int TYPE_STATUS_REQUEST;
   public static final int TYPE_RECEIPT_CONFIRM;
   public static final int TYPE_REFUSE_VERSION;
   public static final int TYPE_PARAM_REQUEST;
   public static final int TYPE_PARAM_RESPONSE;
   public static final int TYPE_ERROR_UNKNOWN;
   public static final int TYPE_ERROR_REFUSE;
   public static final int TYPE_ERROR_VERSION;
   private static final byte CTRL_ABORT_DATAGRAM;
   private static final byte CTRL_REFUSE_DATAGRAM;
   private static final byte CTRL_STATUS_REQUEST;
   private static final byte CTRL_RECEIPT_CONFIRM;
   private static final byte CTRL_REFUSE_VERSION;
   private static final byte CTRL_PARAM_REQUEST;
   private static final byte CTRL_PARAM_RESPONSE;
   private static final int VERSION;
   private static final int MASK_MORE;
   private static final int MASK_REFERENCE;
   private static final int MASK_PACK_REQ;
   private static final int MASK_SEQUENCE;
   private static final int MASK_EACK_REQ;
   private static final int MASK_MAX_SEQUENCE;
   private static final int MASK_DACK_REQ;
   private static final int MASK_OACK_REQ;
   private static final int MASK_VERSION;
   private static final int REF_PING_PACKET;
   private static final int SEQ_CTRL_PACKET;
   private static final int MAX_PACKETS;
   public static final int HEADER_SIZE_1;
   private static final int HEADER_SIZE;
   private static final int HEADER_DIFF;
   private static final int PACKET_SIZE_GPRS;
   private static final int PACKET_SIZE_CDMA;
   private static final int PACKET_SIZE_IDEN;
   private static final int PACKET_SIZE_802_11;
   private static final int PACKET_SIZE_MOBITEX;

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
