package net.rim.device.apps.api.transmission.rim;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;

public final class CMIMEContentType implements CMIMEConstants {
   private String _fullContentType;
   private static String[][] TYPE_WORDS = new Object[][]{
      {"unknown/", "unknown"},
      {"multipart/", "mixed", "report"},
      {"text/", "plain", "html"},
      {"application/", "octet-stream", "postscript"},
      {"message/", "delivery-status", "rfc822"},
      {"image/", "jpeg"},
      {"audio/", "basic"},
      {"video/", "mpeg"},
      {"text/", "plain"}
   };

   public CMIMEContentType(byte[] contentTypeBytes) {
      this.setBytes(contentTypeBytes);
   }

   public final void setBytes(byte[] contentTypeBytes) {
      this._fullContentType = getFullType(contentTypeBytes);
   }

   public final void updateContentType(String data) {
      this._fullContentType = ((StringBuffer)(new Object())).append(this._fullContentType).append(data).toString();
   }

   public final String getBaseType() {
      return getBaseType(this._fullContentType);
   }

   public static final String getBaseType(String contentTypeString) {
      return ((StringBuffer)(new Object())).append(getMajorType(contentTypeString)).append('/').append(getMinorType(contentTypeString)).toString();
   }

   public static final String getBaseType(byte[] contentTypeBytes) {
      return getBaseType(getFullType(contentTypeBytes));
   }

   public final String getFullType() {
      return this._fullContentType;
   }

   public static final String getFullType(byte[] byteArray) {
      if (byteArray != null && byteArray.length >= 2) {
         String typeAndSubtype = null;
         if (byteArray.length == 2) {
            int type = byteArray[0] & 255;
            if (type > 0 && type < TYPE_WORDS.length) {
               String[] words = TYPE_WORDS[type];
               StringBuffer typeAndSubtypeBuffer = (StringBuffer)(new Object());
               typeAndSubtypeBuffer.append(words[0]);
               int subtype = byteArray[1] & 255;
               typeAndSubtypeBuffer.append(subtype > 0 && subtype < words.length ? words[subtype] : TYPE_WORDS[0][1]);
               typeAndSubtype = typeAndSubtypeBuffer.toString();
            }
         } else {
            byte[] bytes = null;
            StringBuffer typeAndSubtypeBuffer = (StringBuffer)(new Object());
            DataBuffer dataBuffer = (DataBuffer)(new Object());
            dataBuffer.setData(byteArray, 0, byteArray.length);

            try {
               boolean decodeSubtype = true;
               boolean typeIsPredefined = false;
               int type = dataBuffer.readUnsignedByte();
               if (type == 255) {
                  bytes = dataBuffer.readByteArray();
                  if (bytes != null && bytes.length > 0) {
                     typeAndSubtypeBuffer.append((String)(new Object(bytes)));
                     typeAndSubtypeBuffer.append('/');
                  }
               } else if (type > 0 && type < TYPE_WORDS.length) {
                  typeIsPredefined = true;
                  typeAndSubtypeBuffer.append(TYPE_WORDS[type][0]);
               } else {
                  decodeSubtype = false;
               }

               if (decodeSubtype) {
                  int subtype = dataBuffer.readUnsignedByte();
                  if (subtype == 255) {
                     bytes = dataBuffer.readByteArray();
                     if (bytes != null && bytes.length > 0) {
                        typeAndSubtypeBuffer.append((String)(new Object(bytes)));
                     }
                  } else if (!typeIsPredefined) {
                     typeAndSubtypeBuffer.append(TYPE_WORDS[0][1]);
                  } else {
                     String[] words = TYPE_WORDS[type];
                     typeAndSubtypeBuffer.append(subtype > 0 && subtype < words.length ? words[subtype] : TYPE_WORDS[0][1]);
                  }
               }

               int extraParametersLength = dataBuffer.getLength() - dataBuffer.getPosition();
               if (extraParametersLength > 0) {
                  byte[] parameterBytes = new byte[extraParametersLength];
                  dataBuffer.readFully(parameterBytes);
                  typeAndSubtypeBuffer.append(';');
                  typeAndSubtypeBuffer.append((String)(new Object(parameterBytes)));
               }

               typeAndSubtype = typeAndSubtypeBuffer.toString();
            } finally {
               ;
            }
         }

         return typeAndSubtype != null && typeAndSubtype.length() > 0 ? typeAndSubtype : "unknown/unknown";
      } else {
         return "unknown/unknown";
      }
   }

   public final String getMajorType() {
      return getMajorType(this._fullContentType);
   }

   public static final String getMajorType(String contentTypeString) {
      int separator = contentTypeString.indexOf(47);
      return separator > 0 ? StringUtilities.toLowerCase(contentTypeString.substring(0, separator), 1701707776) : "unknown/";
   }

   public final String getMinorType() {
      return getMinorType(this._fullContentType);
   }

   public static final String getMinorType(String contentTypeString) {
      int separator = contentTypeString.indexOf(47);
      int semiColon = contentTypeString.indexOf(59);
      if (semiColon > 0) {
         return StringUtilities.toLowerCase(contentTypeString.substring(separator + 1, semiColon), 1701707776);
      } else {
         return separator >= 0 ? StringUtilities.toLowerCase(contentTypeString.substring(separator + 1), 1701707776) : "unknown";
      }
   }

   public final boolean isTypeOpaque() {
      return !StringUtilities.startsWithIgnoreCase(this._fullContentType, TYPE_WORDS[1][0], 1701707776);
   }

   public final boolean isTextType() {
      return StringUtilities.compareToIgnoreCase("text/plain", this.getBaseType(), 1701707776) == 0;
   }

   public static final void encodeType(String typeString, DataBuffer aDataBuffer) {
      encodeType(typeString, aDataBuffer, false);
   }

   public static final void encodeType(String typeString, DataBuffer aDataBuffer, boolean isUnicodeEnabled) {
      int indexOfDivider = typeString.indexOf(47);
      if (indexOfDivider > 0 && typeString.length() > indexOfDivider + 1) {
         String type = typeString.substring(0, indexOfDivider + 1);
         int optionalInfoIndex = typeString.indexOf(59);
         if (optionalInfoIndex == -1) {
            optionalInfoIndex = typeString.length();
         }

         String subtype = typeString.substring(indexOfDivider + 1, optionalInfoIndex);
         int numberOfTypes = TYPE_WORDS.length;
         String[] words = null;
         boolean done = false;

         for (int typeIndex = 1; typeIndex < numberOfTypes; typeIndex++) {
            words = TYPE_WORDS[typeIndex];
            int numberOfSubtypes = words.length;
            if (StringUtilities.compareToIgnoreCase(type, words[0], 1701707776) == 0) {
               for (int subtypeIndex = 1; subtypeIndex < numberOfSubtypes; subtypeIndex++) {
                  if (StringUtilities.compareToIgnoreCase(subtype, words[subtypeIndex], 1701707776) == 0) {
                     if (isUnicodeEnabled && typeIndex == 2) {
                        typeIndex = 8;
                        optionalInfoIndex = typeString.length();
                     }

                     int contextTypeLen = 2;
                     byte[] optInfo = null;
                     if (optionalInfoIndex != typeString.length()) {
                        contextTypeLen += typeString.length() - optionalInfoIndex - 1;
                        optInfo = typeString.substring(optionalInfoIndex + 1).getBytes();
                     }

                     aDataBuffer.writeCompressedInt(contextTypeLen);
                     aDataBuffer.write(typeIndex);
                     aDataBuffer.write(subtypeIndex);
                     if (contextTypeLen > 2) {
                        aDataBuffer.writeByteArray(optInfo);
                     }

                     done = true;
                     return;
                  }
               }

               if (!done) {
                  byte[] subtypeBytes = subtype.getBytes();
                  int subtypeBytesLength = subtypeBytes.length;
                  aDataBuffer.writeCompressedInt(2 + CMIMEUtilities.calculateLengthOfCompressedLength(subtypeBytesLength) + subtypeBytesLength);
                  aDataBuffer.write(typeIndex);
                  aDataBuffer.write(-1);
                  aDataBuffer.writeByteArray(subtypeBytes);
                  done = true;
                  break;
               }
            }
         }

         if (!done) {
            byte[] typeBytes = type.substring(0, type.length() - 1).getBytes();
            int typeBytesLength = typeBytes.length;
            byte[] subtypeBytes = subtype.getBytes();
            int subtypeBytesLength = subtypeBytes.length;
            aDataBuffer.writeCompressedInt(
               2
                  + CMIMEUtilities.calculateLengthOfCompressedLength(typeBytesLength)
                  + typeBytesLength
                  + CMIMEUtilities.calculateLengthOfCompressedLength(subtypeBytesLength)
                  + subtypeBytesLength
            );
            aDataBuffer.writeByte(-1);
            aDataBuffer.writeByteArray(typeBytes);
            aDataBuffer.writeByte(-1);
            aDataBuffer.writeByteArray(subtypeBytes);
            return;
         }
      } else {
         aDataBuffer.writeCompressedInt(2);
         aDataBuffer.write(2);
         aDataBuffer.write(1);
      }
   }
}
