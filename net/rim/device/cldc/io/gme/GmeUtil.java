package net.rim.device.cldc.io.gme;

import net.rim.device.api.io.DatagramBase;
import net.rim.device.cldc.io.daemon.TransportRegistry;

public final class GmeUtil {
   public static final int CID_NONE = 0;
   public static final int CID_IPPP = 3;
   public static final int CID_UNKNOWN = 4;
   private static final int TYPE_CID = 80;
   public static final int GME_TERROR_BAD_GME_FORMAT = 65;

   private GmeUtil() {
   }

   public static final int readContentIdInt(byte[] data, int offset, int length) {
      int index = 1;

      while (index < length) {
         byte type = data[offset + index];
         index++;
         if (type == 0) {
            index += 4;
            if (index >= length) {
               return 0;
            }

            if (data[offset + index] == 80) {
               type = (byte)((int)readCompressedInt(data, offset + ++index, length - index));
               if (type >= 0) {
                  index += getCompressedIntLength(data, offset + index);
                  if (index + type <= length) {
                     offset += index;
                     if (type != 4
                        || data[offset + 0] != 73 && data[offset + 0] != 105
                        || data[offset + 1] != 80 && data[offset + 1] != 112
                        || data[offset + 2] != 80 && data[offset + 2] != 112
                        || data[offset + 3] != 80 && data[offset + 2] != 112) {
                        return 4;
                     }

                     return 3;
                  }
               }
            }

            return 0;
         }

         int len = (int)readCompressedInt(data, offset + index, length - index);
         if (len < 0) {
            return 0;
         }

         index += getCompressedIntLength(data, offset + index) + len;
      }

      return 0;
   }

   public static final void sendTransactionErrorDatagram(DatagramBase datagram, int error, String str) {
      try {
         if (datagram instanceof GMEDatagram) {
            GMEDatagram dgram = (GMEDatagram)datagram;
            GMEDatagramInfo info = dgram.getDatagramInfo();
            ((Transport)TransportRegistry.get("net.rim.device.cldc.io.gme.Transport"))
               .sendTransactionErrorDatagram(info.subConnection, info, dgram.getTransactionId(), error, str);
            return;
         }
      } finally {
         return;
      }
   }

   private static final long readCompressedInt(byte[] data, int offset, int length) {
      long ret = 0;
      int index = 0;

      while (index < length) {
         byte val = data[offset + index];
         ret |= val & 127;
         if (ret > 4294967295L) {
            return -2;
         }

         if ((val & 128) == 0) {
            return ret;
         }

         index++;
         ret <<= 7;
      }

      return -1;
   }

   private static final int getCompressedIntLength(byte[] data, int offset) {
      int len = 0;

      while ((data[offset + len] & 128) != 0) {
         len++;
      }

      return len + 1;
   }
}
