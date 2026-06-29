package net.rim.device.cldc.io.mdp;

import javax.microedition.io.Connector;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.cldc.io.udp.GpakUtil;
import net.rim.device.cldc.io.udp.UdpInternalAddress;

public final class MdpUtil implements MdpConstants {
   private static final byte CTRL_ABORT_DATAGRAM = 0;
   private static final byte CTRL_REFUSE_DATAGRAM = 1;
   private static final byte CTRL_STATUS_REQUEST = 2;
   private static final byte CTRL_RECEIPT_CONFIRM = 3;
   private static final byte CTRL_REFUSE_VERSION = 5;
   private static final byte CTRL_PARAM_REQUEST = 12;
   private static final byte CTRL_PARAM_RESPONSE = 13;
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
   public static final int HEADER_SIZE_1 = 8;
   private static final int HEADER_SIZE = 3;
   private static final int HEADER_DIFF = 5;
   private static final int PACKET_SIZE_GPRS = 1300;
   private static final int PACKET_SIZE_CDMA = 1300;
   private static final int PACKET_SIZE_IDEN = 1300;
   private static final int PACKET_SIZE_802_11 = 1300;

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

   public static final MdpUtil$DatagramInfo decode(byte[] data, int offset, int length) {
      if (length <= 0) {
         return null;
      }

      MdpUtil$DatagramInfo info = new MdpUtil$DatagramInfo();
      byte temp = data[offset];
      info.moreFlag = (temp & 128) != 0;
      info.reference = temp & 127;
      if (length == 1) {
         if (info.reference == 0) {
            info.type = 1;
            return info;
         } else {
            info.type = 3;
            return info;
         }
      } else {
         temp = data[offset + 1];
         info.packetAckFlag = (temp & 128) != 0;
         info.sequence = temp & 127;
         if (length == 2) {
            info.type = 4;
            return info;
         }

         if (info.sequence != 127) {
            temp = data[offset + 2];
            info.extAckFlag = (temp & 128) != 0;
            info.maxSequence = temp & 127;
            int headerSize;
            if (info.sequence == 0) {
               if (length < 8) {
                  info.type = -2;
                  return info;
               }

               temp = data[offset + 3];
               info.datagramAckFlag = (temp & 128) != 0;
               info.optimizedAckFlag = (temp & 64) != 0;
               info.version = temp & 7;
               if (info.version != 0) {
                  info.type = -3;
                  return info;
               }

               info.srcPort = DatagramAddressBase.readShort(data, offset + 4) & '\uffff';
               info.destPort = DatagramAddressBase.readShort(data, offset + 6) & '\uffff';
               headerSize = 8;
            } else {
               headerSize = 3;
            }

            info.type = 2;
            info.data = data;
            info.offset = offset + headerSize;
            info.length = length - headerSize;
            return info;
         } else {
            switch (data[offset + 2]) {
               case 0:
                  info.type = 5;
                  return info;
               case 1:
                  info.type = 6;
                  return info;
               case 2:
                  info.type = 7;
                  return info;
               case 3:
                  info.type = 8;
                  info.maxSequence = -1;
                  info.data = data;
                  info.offset = offset + 3;
                  info.length = length - 3;
                  return info;
               case 5:
                  info.type = 10;
                  info.version = data[offset + 3] & 7;
                  return info;
               case 13:
                  info.type = 14;
                  info.data = data;
                  info.offset = offset + 3;
                  info.length = length - 3;
                  return info;
               default:
                  info.type = -1;
                  return info;
            }
         }
      }
   }

   public static final int getNominalLength() {
      switch (RadioInfo.getNetworkType()) {
         case 2:
            return 0;
         case 3:
         default:
            return 1300 - GpakUtil.getHeaderSize();
         case 4:
            return 1300 - GpakUtil.getHeaderSize();
         case 5:
            return 1300 - GpakUtil.getHeaderSize();
         case 6:
            return 1300 - GpakUtil.getHeaderSize();
         case 7:
            return 1300 - GpakUtil.getHeaderSize();
      }
   }

   public static final int getMaximumLength(int nativeLength) {
      return 127 * (nativeLength - 3) - 5;
   }

   public static final int getMaximumSequence(int length, int nativeLength) {
      return (length + 5 - 1) / (nativeLength - 3);
   }

   public static final int getOffsetFromSequence(int sequence, int nativeLength) {
      return sequence == 0 ? 0 : (nativeLength - 3) * sequence - 5;
   }

   public static final int getLengthFromMaxSequence(int maxSequence, int nativeLength) {
      return (maxSequence + 1) * (nativeLength - 3) - 5;
   }

   public static final MdpUtil$DatagramInfo makeDatagramInfo(int type) {
      MdpUtil$DatagramInfo info = new MdpUtil$DatagramInfo();
      info.type = type;
      return info;
   }

   public static final DatagramConnection makeNativeConnection() {
      switch (RadioInfo.getNetworkType()) {
         case 2:
            throw new RuntimeException("Unsupported network");
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         default:
            String address = UdpInternalAddress.makeAddress(true, null, -1, -1, null, 0, 0, 2, true);
            DatagramConnectionBase conn = (DatagramConnectionBase)Connector.open(address);
            conn.setFlag(1024, true);
            return conn;
      }
   }
}
