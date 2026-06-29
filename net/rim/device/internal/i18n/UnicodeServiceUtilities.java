package net.rim.device.internal.i18n;

import net.rim.device.api.i18n.Locale;

public final class UnicodeServiceUtilities implements UnicodeServiceConstants {
   private static UnicodeServiceProvider _defaultUnicodeServiceProvider = new UnicodeServiceUtilities$DefaultUnicodeServiceProvider(null);

   public static final UnicodeServiceProvider getUnicodeServiceProvider() {
      return _defaultUnicodeServiceProvider;
   }

   public static final synchronized byte[] getSupportedEncodings() {
      return _defaultUnicodeServiceProvider.getSupportedEncodings();
   }

   public static final byte resolveEncoding(byte[] supportedEncodings, byte[] encodingsToDecideAgainst) {
      return _defaultUnicodeServiceProvider.resolveEncoding(supportedEncodings, encodingsToDecideAgainst);
   }

   public static final byte resolveEncoding(byte[] encodingsToDecideAgainst) {
      return _defaultUnicodeServiceProvider.resolveEncoding(getSupportedEncodings(), encodingsToDecideAgainst);
   }

   public static final String getEncoding(byte encodingType) {
      if ((encodingType & 128) == 128) {
         encodingType = (byte)(encodingType & -129);
      }

      switch ((byte)(encodingType & 112)) {
         case 16:
         case 32:
         case 48:
         case 64:
            encodingType = (byte)(encodingType & -113);
         default:
            return _defaultUnicodeServiceProvider.getEncoding(encodingType);
      }
   }

   public static final byte getEncoding(String encodingType) {
      return _defaultUnicodeServiceProvider.getEncoding(encodingType);
   }

   public static final byte getPreferredEncoding() {
      Locale locale = Locale.getDefaultInputForSystem();
      return (byte)(Locale.isLatinOneCharacterSetLocale(locale) ? 0 : 1);
   }

   public static final String readString(byte[] dataBuffer, int readPosition, int lengthToRead, boolean encoded) {
      if (lengthToRead >= 1 && readPosition + lengthToRead <= dataBuffer.length) {
         String encodingName = null;
         if (encoded) {
            byte encoding = dataBuffer[readPosition++];
            lengthToRead--;
            if ((encoding & 128) == 128) {
               int futureDataLength = detectFutureData(dataBuffer, readPosition - 1, lengthToRead + 1);
               if (futureDataLength < 0 || futureDataLength >= lengthToRead) {
                  return "";
               }

               readPosition += futureDataLength;
               lengthToRead -= futureDataLength;
            }

            encodingName = getEncoding(encoding);
            if (encodingName == null || encodingName.length() == 0) {
               encodingName = "";
            }
         } else {
            if (dataBuffer[readPosition + lengthToRead - 1] == 0) {
               lengthToRead--;
            }

            encodingName = "windows-1252\r";
         }

         return new String(dataBuffer, readPosition, lengthToRead, encodingName);
      } else {
         return null;
      }
   }

   public static final int detectFutureData(byte[] dataBuffer, int readPosition, int lengthToRead) {
      if (lengthToRead >= 1 && readPosition + lengthToRead <= dataBuffer.length) {
         byte encoding = dataBuffer[readPosition++];
         lengthToRead--;
         int oldPosition = readPosition;
         String encodingName = null;
         if ((encoding & 128) == 128) {
            encoding = (byte)(encoding & -129);
            encodingName = getEncoding(encoding);
            if (encodingName != null && encodingName.length() > 0) {
               int lastPosition = readPosition + lengthToRead;

               while (readPosition < lastPosition) {
                  if (dataBuffer[readPosition++] == 0) {
                     return readPosition - oldPosition;
                  }

                  if (readPosition < lastPosition) {
                     int i = 0;
                     int used = 0;

                     while (readPosition < lastPosition) {
                        i |= dataBuffer[readPosition++] & 127;
                        if ((dataBuffer[readPosition - 1] & 128) == 0) {
                           readPosition += i;
                           break;
                        }

                        used++;
                        if (used > 4 || used == 4 && (i & 234881024) != 0) {
                           readPosition = lastPosition + 1;
                           break;
                        }

                        i <<= 7;
                     }
                  }
               }

               if (lastPosition - readPosition < 0) {
                  return -1;
               }

               return 0;
            }
         }

         return 0;
      } else {
         return 0;
      }
   }
}
