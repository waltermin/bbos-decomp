package net.rim.device.cldc.io.udp;

import javax.microedition.io.Datagram;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;

public final class GpakUtil {
   private static final int HEADER_SIZE;
   private static final int OFFSET_VERSION;
   private static final int OFFSET_TYPE;
   private static final int OFFSET_FIRST_INT;
   private static final int OFFSET_SECOND_INT;
   private static final byte VERSION_1;
   static final byte TYPE_DATA;
   static final byte TYPE_GCMP;
   private static final byte ERROR_NONE;
   private static final byte ERROR_RESERVED_1;
   private static final byte ERROR_RESERVED_2;
   private static final byte ERROR_RESERVED_NIA_PACKET_ERROR;
   private static final byte ERROR_RESERVED_NIA_PACKET_FAILED;
   private static final byte ERROR_RESERVED_NIA_NO_TX;
   private static final byte ERROR_VERSION;
   private static final int TYPE_MASK;
   private static final int ERROR_MASK;
   static final int EMPTY_VALUE;
   private static String STR = "net.rim.gpak";
   private static long GUID = 1571678071440390457L;
   private static final int RX_ERROR_ADDR_MATCH_1;
   private static final int RX_ERROR_VERSION;
   private static final int RX_ERROR_UNKNOWN;
   private static final int RX_VERSION_UNKNOWN;

   public static final int getHeaderSize() {
      return 10;
   }

   static final int encode(byte type, byte[] dest, byte[] src, int srcOffset, int srcLength, int gpakHostAddress) {
      dest[0] = 16;
      dest[1] = type;
      DatagramAddressBase.writeInt(dest, 2, DeviceInfo.getDeviceId());
      DatagramAddressBase.writeInt(dest, 6, gpakHostAddress);
      System.arraycopy(src, srcOffset, dest, 10, srcLength);
      return srcLength + 10;
   }

   static final int decode(Datagram datagram) {
      return decode(datagram, null);
   }

   static final int decode(Datagram datagram, UdpInternalAddress gpakHostAddress) {
      byte[] data = datagram.getData();
      int offset = datagram.getOffset();
      int length = datagram.getLength();
      int type = decodeGpak(data, offset, length, gpakHostAddress);
      switch (type) {
         case 2:
         case 4:
            datagram.setData(data, offset + 10, length - 10);
         default:
            return type;
      }
   }

   static final int decode(byte[] data) {
      return decodeGpak(data, 0, data.length, null);
   }

   private static final int decodeGpak(byte[] data, int offset, int length, UdpInternalAddress gpakHostAddress) {
      if (length >= 1) {
         switch (data[offset + 0]) {
            case 16:
               if (length >= 10) {
                  return decodeVersionOne(data[offset + 1] & 24, data, offset, gpakHostAddress);
               }
         }
      }

      return 1;
   }

   private static final int decodeVersionOne(int type, byte[] data, int offset, UdpInternalAddress gpakHostAddress) {
      switch (type) {
         case 8:
         case 16:
            if (DatagramAddressBase.readInt(data, offset + 6) == DeviceInfo.getDeviceId()) {
               if (checkError(data[offset + 1], (byte)16)) {
                  return 1;
               } else {
                  if (type == 8) {
                     if (gpakHostAddress != null) {
                        gpakHostAddress.setGpakHostAddress(DatagramAddressBase.readInt(data, offset + 2));
                     }

                     return 2;
                  }

                  return 4;
               }
            } else {
               EventLogger.logEvent(GUID, 1382116657, 3);
            }
         default:
            return 1;
      }
   }

   private static final boolean checkError(int type, byte version) {
      switch (type & -32) {
         case -32:
            EventLogger.logEvent(GUID, 1383233138, 2);
            return true;
         case 0:
            return false;
         default:
            EventLogger.logEvent(GUID, 1383232878, 2);
            return true;
      }
   }

   static {
      EventLogger.register(GUID, STR, 2);
   }
}
