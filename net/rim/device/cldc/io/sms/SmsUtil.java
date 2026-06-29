package net.rim.device.cldc.io.sms;

import javax.microedition.io.Datagram;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.SmsAddress;
import net.rim.device.api.system.SMSPacketHeader;
import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

public final class SmsUtil {
   public static final byte IEID_CONCATENATEDSHORTMESSAGE = 0;
   public static final byte IEID_SPECIALSMS_MESSAGE_WAITING_INDICATION = 1;
   public static final byte IEID_RESERVED = 2;
   public static final byte IEID_APPLICATION_PORT_ADDRESS_8BITADDRESS = 4;
   public static final byte IEID_APPLICATION_PORT_ADDRESS_16BITADDRESS = 5;
   public static final byte IEID_SMSC_CONTROL_PARAMS = 6;
   public static final byte IEID_UDH_SOURCE_INDICATOR = 7;
   public static final byte IEID_CONCATENATEDSHORTMESSAGE_16BIT = 8;
   public static final byte IEID_WIRELESS_CONTROL_MESSAGE_PROTOCOL = 9;
   public static final byte IEID_REPLY_ADDRESS = 34;
   public static final int UDH_MSGWAITING_FLAG_STORE_MSG = 128;
   public static final int UDH_MSGWAITING_TYPE_MASK = 127;
   private static final int DATA_HEADER_SIZE = 0;
   private static final int SCK_MIN_SIZE = 7;
   private static final int GCMP_HEADER_SIZE = 6;
   private static final int KODIAK_WV_HEADER_SIZE = 7;
   public static String PROPERTY_USER_DATA_HEADER = "sms-udh";
   public static String PROPERTY_REF_NUMBER = "sms-ref-number";
   public static String PROPERTY_TOTAL_SEGMENTS = "sms-total-segments";
   public static String PROPERTY_SEGMENT_NUMBER = "sms-segment-number";

   public static final int encode(int port, byte[] dest, byte[] src, int srcOffset, int srcLength) {
      int offset = getHeaderSize(port);
      if (srcLength > dest.length - offset) {
         return -1;
      }

      switch (port) {
         case 65536:
         default:
            dest[0] = 47;
            dest[1] = 47;
            dest[2] = 71;
            dest[3] = 67;
            dest[4] = 77;
            dest[5] = 80;
         case 65535:
            System.arraycopy(src, srcOffset, dest, offset, srcLength);
            return srcLength + offset;
      }
   }

   public static final byte[] encodeUserDataHeader(byte informationElementid, byte[] fielddata) {
      byte[] header = new byte[0];
      switch (informationElementid) {
         case 5:
            if (fielddata.length != 4) {
               throw new IllegalArgumentException("SMSUtil:107");
            } else {
               Array.resize(header, 6);
               header[0] = 5;
               header[1] = 4;

               for (int i = 0; i < 4; i++) {
                  header[i + 2] = fielddata[i];
               }
            }
         default:
            return header;
      }
   }

   public static final DatagramBase encode(DatagramBase d, byte informationElementid, byte[] fielddata) {
      SmsAddress a = (SmsAddress)d.getAddressBase();
      SMSPacketHeader h = a.getHeader();
      byte[] originaldata = Arrays.copy(d.getData());
      int length = 0;
      int offset = 0;
      int originaldatalength = originaldata.length;
      if (!h.isUserDataHeaderPresent()) {
         h.setUserDataHeaderPresent(true);
      } else {
         length = originaldata[0];
         offset = 1;
         originaldatalength = originaldata.length - 1;
      }

      d.setLength(0);
      byte[] header = encodeUserDataHeader(informationElementid, fielddata);
      length += header.length;
      d.writeByte(length);
      d.write(header);
      d.write(originaldata, offset, originaldatalength);
      return d;
   }

   public static final DatagramBase decode(Transport transport, SMSPacketHeader header, byte[] data) {
      int[] ports = null;
      byte[] userDataHeader = null;
      int refNumber = -1;
      int totalSegments = 1;
      int segmentNumber = 0;
      int dataLength = data.length;
      if (header.getProtocolMeaning() == 255) {
         if (dataLength > 3) {
            int messageType = data[0] & 255;
            totalSegments = data[1] & 255;
            segmentNumber = (data[2] & 255) + 1;
            refNumber = header.getID();
            int sizeToStrip = 3;
            if (messageType == 0 && segmentNumber == 1 && dataLength >= 7) {
               ports = new int[]{65535 & DatagramAddressBase.readShort(data, 5)};
               sizeToStrip = 7;
            }

            dataLength = data.length - sizeToStrip;
            System.arraycopy(data, sizeToStrip, data, 0, dataLength);
            Array.resize(data, dataLength);
         }
      } else if (header.isUserDataHeaderPresent() && dataLength != 0) {
         try {
            int userDataHeaderLength = data[0];
            int i = 1;

            while (i <= userDataHeaderLength) {
               int ieiType = data[i++];
               int ieiLength = data[i++];
               switch (ieiType) {
                  case 0:
                     if (ieiLength == 3) {
                        refNumber = data[i] & 255;
                        totalSegments = data[i + 1] & 255;
                        segmentNumber = data[i + 2] & 255;
                     }
                     break;
                  case 5:
                     if (ieiLength == 4) {
                        ports = new int[]{65535 & DatagramAddressBase.readShort(data, i), 65535 & DatagramAddressBase.readShort(data, i + 2)};
                     }
                     break;
                  case 8:
                     if (ieiLength == 4) {
                        refNumber = (data[i] & 255) << 8;
                        refNumber += data[i + 1] & 255;
                        totalSegments = data[i + 2] & 255;
                        segmentNumber = data[i + 3] & 255;
                     }
                     break;
                  case 34:
                     if (ieiLength < 3 || ieiLength > 43) {
                        break;
                     }

                     int index = i;
                     int addressLengthInNybbles = data[index++];
                     int type = data[index] >> 4 & 7;
                     int plan = data[index++] & 15;
                     StringBuffer number = new StringBuffer();
                     boolean lowerNybble = true;
                     int n = 0;

                     for (; n < addressLengthInNybbles && index < dataLength; n++) {
                        byte numberByte = (byte)((lowerNybble ? data[index] : data[index++] >> 4) & 15);
                        lowerNybble = !lowerNybble;
                        switch (numberByte) {
                           case 9:
                              number.append((char)(numberByte + 48));
                              break;
                           case 10:
                           default:
                              number.append('*');
                              break;
                           case 11:
                              number.append('#');
                              break;
                           case 12:
                              number.append('a');
                              break;
                           case 13:
                              number.append('b');
                              break;
                           case 14:
                              number.append('c');
                           case 15:
                        }
                     }

                     header.setCallbackAddress(number.toString(), type, plan);
                     break;
                  default:
                     int offset = 0;
                     if (userDataHeader == null) {
                        userDataHeader = new byte[ieiLength + 2];
                     } else {
                        offset = userDataHeader.length;
                        Array.resize(userDataHeader, offset + ieiLength + 2);
                     }

                     System.arraycopy(data, i - 2, userDataHeader, offset, ieiLength + 2);
               }

               i += ieiLength;
            }

            dataLength = data.length - userDataHeaderLength - 1;
            System.arraycopy(data, userDataHeaderLength + 1, data, 0, dataLength);
            Array.resize(data, dataLength);
         } catch (IndexOutOfBoundsException var23) {
         }
      } else if (dataLength >= 7 && data[0] == 47 && data[1] == 47 && data[2] == 83 && data[3] == 67 && data[4] == 75) {
         int wapHeaderLength;
         label154:
         for (wapHeaderLength = 0; wapHeaderLength < dataLength; wapHeaderLength++) {
            switch (data[wapHeaderLength]) {
               case 10:
               case 13:
               case 32:
                  break label154;
            }
         }

         if (data[5] == 76) {
            try {
               ports = new int[]{DatagramAddressBase.parseInt(data, 6, 10, 16)};
            } catch (IllegalArgumentException e) {
               ports = null;
            }

            if (wapHeaderLength == 20) {
               refNumber = DatagramAddressBase.parseInt(data, 14, 16, 16);
               totalSegments = DatagramAddressBase.parseInt(data, 16, 18, 16);
               segmentNumber = DatagramAddressBase.parseInt(data, 18, 20, 16);
            }
         } else {
            int portLength = 4;
            if (wapHeaderLength == 7) {
               portLength = 2;
            }

            try {
               ports = new int[]{DatagramAddressBase.parseInt(data, 5, 5 + portLength, 16)};
            } catch (IllegalArgumentException e) {
               ports = null;
            }
         }

         dataLength = data.length - wapHeaderLength - 1;
         System.arraycopy(data, wapHeaderLength + 1, data, 0, dataLength);
         Array.resize(data, dataLength);
      } else if (data.length >= 6 && data[0] == 47 && data[1] == 47 && data[2] == 71 && data[3] == 67 && data[4] == 77 && data[5] == 80) {
         ports = new int[]{65536};
         int length = data.length - 6;
         System.arraycopy(data, 6, data, 0, length);
         Array.resize(data, length);
      } else if (data.length >= 7 && data[0] == 47 && data[1] == 47 && data[2] == 75 && data[3] == 78 && data[4] == 112 && data[5] == 116) {
         ports = new int[]{65552};
      }

      DatagramBase dgram = new DatagramBase(data, 0, data.length);
      dgram.setAddressBase(new SmsAddress(header, ports));
      if (userDataHeader != null) {
         dgram.setProperty(PROPERTY_USER_DATA_HEADER, userDataHeader);
      }

      if (totalSegments > 1) {
         dgram.setProperty(PROPERTY_REF_NUMBER, new Integer(refNumber));
         dgram.setProperty(PROPERTY_TOTAL_SEGMENTS, new Integer(totalSegments));
         dgram.setProperty(PROPERTY_SEGMENT_NUMBER, new Integer(segmentNumber));
      }

      return dgram;
   }

   public static final int getHeaderSize(int port) {
      switch (port) {
         case 65535:
            return 0;
         case 65536:
         default:
            return 6;
      }
   }

   public static final void constructSegment(
      Datagram datagram, SMSPacketHeader header, byte[] data, int totalSegments, int segment, int refNumber, boolean alreadyHasUDH
   ) {
      int length;
      if (!alreadyHasUDH) {
         header.setUserDataHeaderPresent(true);
         length = 0;
      } else {
         length = data[0];
      }

      int messageCoding = header.getMessageCoding();
      int bitsPerByte = SMSPacketHeader.getBitsPerCharacter(messageCoding) / SMSPacketHeader.getBytesPerCharacter(messageCoding);
      int dataSize = SMSPacketHeader.getBitsPerSegment(messageCoding, length) / bitsPerByte;
      int dataOffset = segment * dataSize;
      int dataLength = Math.min(data.length - dataOffset, dataSize);
      datagram.setLength(0);
      length += 5;
      datagram.writeByte(length);
      datagram.write(0);
      datagram.write(3);
      datagram.write(refNumber);
      datagram.write(totalSegments);
      datagram.write(segment + 1);
      if (segment == 0) {
         if (alreadyHasUDH) {
            datagram.write(data, 1, dataLength - 1);
         } else {
            datagram.write(data, 0, dataLength);
         }
      } else {
         if (alreadyHasUDH) {
            datagram.write(data, 1, data[0]);
         }

         datagram.write(data, dataOffset, dataLength);
      }
   }

   public static final void constructSegmentCDMA(Datagram datagram, SMSPacketHeader header, byte[] data, int totalSegments, int segment) {
      int messageCoding = header.getMessageCoding();
      int bitsPerByte = SMSPacketHeader.getBitsPerCharacter(messageCoding) / SMSPacketHeader.getBytesPerCharacter(messageCoding);
      int dataSize = SMSPacketHeader.getBitsPerSegmentCDMA(messageCoding, 3) / bitsPerByte;
      int dataOffset = segment * dataSize;
      int dataLength = Math.min(data.length - dataOffset, dataSize);
      datagram.setLength(0);
      datagram.write(0);
      datagram.write(totalSegments);
      datagram.write(segment);
      datagram.write(data, dataOffset, dataLength);
   }

   public static final byte[] decodeWDPData(byte[] data, int offset, int length) {
      int newDataLength = length >> 1;
      byte[] newData = new byte[newDataLength];

      try {
         for (int i = 0; i < newDataLength; i++) {
            newData[i] = (byte)DatagramAddressBase.parseInt(data, offset, offset + 2, 16);
            offset += 2;
         }

         return newData;
      } catch (IllegalArgumentException ex) {
         return null;
      }
   }
}
