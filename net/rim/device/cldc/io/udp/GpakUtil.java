package net.rim.device.cldc.io.udp;

import javax.microedition.io.Datagram;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;

public final class GpakUtil {
   private static final int HEADER_SIZE = 10;
   private static final int OFFSET_VERSION = 0;
   private static final int OFFSET_TYPE = 1;
   private static final int OFFSET_FIRST_INT = 2;
   private static final int OFFSET_SECOND_INT = 6;
   private static final byte VERSION_1 = 16;
   static final byte TYPE_DATA = 8;
   static final byte TYPE_GCMP = 16;
   private static final byte ERROR_NONE = 0;
   private static final byte ERROR_RESERVED_1 = 64;
   private static final byte ERROR_RESERVED_2 = 96;
   private static final byte ERROR_RESERVED_NIA_PACKET_ERROR = -128;
   private static final byte ERROR_RESERVED_NIA_PACKET_FAILED = -96;
   private static final byte ERROR_RESERVED_NIA_NO_TX = -64;
   private static final byte ERROR_VERSION = -32;
   private static final int TYPE_MASK = 24;
   private static final int ERROR_MASK = -32;
   static final int EMPTY_VALUE = 0;
   private static String STR = "net.rim.gpak";
   private static long GUID = 1571678071440390457L;
   private static final int RX_ERROR_ADDR_MATCH_1 = 1382116657;
   private static final int RX_ERROR_VERSION = 1383233138;
   private static final int RX_ERROR_UNKNOWN = 1383232878;
   private static final int RX_VERSION_UNKNOWN = 1383495022;

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
