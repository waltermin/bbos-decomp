package com.sun.cldc.i18n.j2me;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.util.StringUtilities;

final class ConversionDataRegistryHelper {
   private final String DEFAULT_ENCODING = "ISO-8859-1";
   private String[] _availableEncodings = new String[]{"US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "Windows-1252", "UTF-16LE"};
   private int _supportedEncodingsNum = this._availableEncodings.length;
   private ConversionDataRegistryHelper$EncodingMappingData[] _encodingMappingTable = new ConversionDataRegistryHelper$EncodingMappingData[]{
      new ConversionDataRegistryHelper$EncodingMappingData("UTF-8", 27, 0, "BBCondensed"),
      new ConversionDataRegistryHelper$EncodingMappingData("ISO-8859-1", 1, 1701707776, "System"),
      new ConversionDataRegistryHelper$EncodingMappingData("US-ASCII", 0, 1701707776, "System"),
      new ConversionDataRegistryHelper$EncodingMappingData("UTF-16BE", 28, 0, "BBCondensed"),
      new ConversionDataRegistryHelper$EncodingMappingData("windows-1252", 18, 1701707776, "System"),
      new ConversionDataRegistryHelper$EncodingMappingData("UTF-16LE", 39, 0, "BBCondensed")
   };
   private static final int CDBF_CONVERSION_SIGNATURE;
   private static final int CDBF_CURRENT_VERSION_MAJOR;
   private static final int CDBF_CURRENT_VERSION_MINOR;
   private static final int ENC_INCREMENT_NUMBER;
   private static final int STRIP_FLAG;

   final synchronized String[] getSupportedEncodings() {
      return this._availableEncodings;
   }

   final synchronized boolean loadConversionData(String encodingName, int encodingID, int locale, String typeface, byte[][][] bData) {
      try {
         if (bData != null && bData.length != 0 && bData[0] != null) {
            byte success = 0;
            byte[] data = (byte[])bData[0];
            DataInputStream stream = new DataInputStream(new ByteArrayInputStream(data));
            int headerSize;
            if ((headerSize = this.readHeader(stream)) == 0) {
               return false;
            }

            int encsHeaderSize = this.readLEValue(stream, 2);
            int encsNumber = this.readLEValue(stream, 2);
            int encodingOffset = headerSize + encsHeaderSize;
            int encodingDataLength = 0;
            int flag = 0;
            int bufferPosition = headerSize + 4;
            boolean exit = false;
            int i = 0;

            while (i < encsNumber && !exit) {
               int id = this.readLEValue(stream, 2);
               encodingOffset += this.readLEValue(stream, 2);
               int encodingNamesLength = this.readLEValue(stream, 2);
               bufferPosition += 6;
               if (i + 1 < encsNumber) {
                  encodingDataLength = (data[bufferPosition + encodingNamesLength + 2] & 255 | (data[bufferPosition + encodingNamesLength + 3] & 255) << 8)
                     - (encodingOffset - (headerSize + encsHeaderSize));
               } else {
                  encodingDataLength = data.length - encodingOffset;
               }

               if (encodingID != -1 && encodingID != id) {
                  bufferPosition += encodingNamesLength;
                  stream.skip(encodingNamesLength);
               } else {
                  int encodingNamesNumber = stream.readUnsignedByte();
                  bufferPosition++;

                  for (int j = 0; j < encodingNamesNumber && !exit; flag = 0) {
                     int encodingNameLength = stream.readUnsignedByte();
                     if (encodingNameLength > 0) {
                        String currentName;
                        if (encodingName == null || encodingID != id && encodingID != -1) {
                           currentName = new String(data, bufferPosition + 1, encodingNameLength);
                        } else {
                           currentName = encodingName;
                           exit = true;
                        }

                        stream.skip(encodingNameLength);
                        bufferPosition += encodingNameLength + 1;
                        int existingID = this.getEncodingIDLocal(currentName);
                        if (existingID == -1) {
                           flag = 1;
                        } else if (existingID == id) {
                           for (int k = 0; k < this._encodingMappingTable.length; k++) {
                              ConversionDataRegistryHelper$EncodingMappingData eData = this._encodingMappingTable[k];
                              if (eData != null && eData._id == id && eData._encodingDataLength != encodingDataLength) {
                                 flag = 2;
                                 break;
                              }
                           }
                        }

                        if (flag == 1) {
                           if (this._supportedEncodingsNum == this._encodingMappingTable.length) {
                              ConversionDataRegistryHelper$EncodingMappingData[] arr = new ConversionDataRegistryHelper$EncodingMappingData[this._supportedEncodingsNum
                                 + 5];
                              System.arraycopy(this._encodingMappingTable, 0, arr, 0, this._encodingMappingTable.length);
                              this._encodingMappingTable = arr;
                           }

                           this._encodingMappingTable[this._supportedEncodingsNum++] = new ConversionDataRegistryHelper$EncodingMappingData(
                              currentName, id, locale, typeface
                           );
                           this._encodingMappingTable[this._supportedEncodingsNum - 1]._binaryData = bData;
                           this._encodingMappingTable[this._supportedEncodingsNum - 1]._encodingDataOffset = encodingOffset;
                           this._encodingMappingTable[this._supportedEncodingsNum - 1]._encodingDataLength = encodingDataLength;
                           success = (byte)(success | 1);
                        } else if (flag == 2) {
                           for (int l = 0; l < this._encodingMappingTable.length; l++) {
                              ConversionDataRegistryHelper$EncodingMappingData eData = this._encodingMappingTable[l];
                              if (eData != null && eData._id == id) {
                                 eData._binaryData = bData;
                                 eData._encodingDataOffset = encodingOffset;
                                 eData._encodingDataLength = encodingDataLength;
                                 eData._locale = locale;
                                 eData._typeface = typeface;
                                 success = (byte)(success | 2);
                                 break;
                              }
                           }
                        }
                     }

                     j++;
                  }
               }

               i++;
               encodingOffset = headerSize + encsHeaderSize;
               int var31 = false;
            }

            if ((success & 1) == 1) {
               this.updateAvailableEncodings();
            }

            stream.close();
            return success != 0;
         } else {
            return false;
         }
      } catch (Exception e) {
         return false;
      }
   }

   final boolean isSupported(String encoding) {
      ConversionDataRegistryHelper$EncodingMappingData eData = this.runOverTheData(encoding);
      return eData != null && (this.isAlgorithmicallySupported(eData._id) || eData._binaryData != null);
   }

   final int getSuggestedLocale(String encoding) {
      ConversionDataRegistryHelper$EncodingMappingData eData = this.runOverTheData(encoding);
      return eData != null ? eData._locale : -1;
   }

   final String getSuggestedTypeface(String encoding) {
      ConversionDataRegistryHelper$EncodingMappingData eData = this.runOverTheData(encoding);
      if (eData != null) {
         return eData._typeface;
      }

      Font f = FontRegistry.getDefaultFont();
      return f != null ? f.getFontFamily().getName() : FontRegistry.DEFAULT_FAMILY;
   }

   final String getSuggestedTypeface(int localeCode) {
      ConversionDataRegistryHelper$EncodingMappingData eData = this.runOverTheData(localeCode);
      if (eData != null) {
         return eData._typeface;
      }

      Font f = FontRegistry.getDefaultFont();
      return f != null ? f.getFontFamily().getName() : FontRegistry.DEFAULT_FAMILY;
   }

   final String getSuggestedEncoding(int localeCode) {
      ConversionDataRegistryHelper$EncodingMappingData eData = this.runOverTheData(localeCode);
      return eData != null ? eData._encoding : "ISO-8859-1";
   }

   final synchronized byte[][][] getConversionData(int id, int[] dataOffset) {
      id &= -268435457;
      int length = this._encodingMappingTable.length;

      for (int i = 0; i < length; i++) {
         ConversionDataRegistryHelper$EncodingMappingData eData = this._encodingMappingTable[i];
         if (eData != null && eData._id == id) {
            dataOffset[0] = eData._encodingDataOffset;
            return eData._binaryData;
         }
      }

      return (byte[][][])((byte[][])null);
   }

   final int getEncodingIDLocal(String encoding) {
      ConversionDataRegistryHelper$EncodingMappingData eData = this.runOverTheData(encoding);
      if (eData != null) {
         if (encoding.length() > eData._encoding.length()) {
            return encoding.charAt(eData._encoding.length()) == 13 ? eData._id | 268435456 : eData._id;
         } else {
            return eData._id;
         }
      } else {
         return -1;
      }
   }

   final int getEncodingID(String encoding) {
      ConversionDataRegistryHelper$EncodingMappingData eData = this.runOverTheData(encoding);
      if (eData != null && (this.isAlgorithmicallySupported(eData._id) || eData._binaryData != null)) {
         if (encoding.length() <= eData._encoding.length()) {
            return eData._id;
         } else {
            return encoding.charAt(eData._encoding.length()) == 13 ? eData._id | 268435456 : eData._id;
         }
      } else {
         return -1;
      }
   }

   private final synchronized ConversionDataRegistryHelper$EncodingMappingData runOverTheData(String encoding) {
      int length = this._encodingMappingTable.length;

      for (int i = 0; i < length; i++) {
         ConversionDataRegistryHelper$EncodingMappingData eData = this._encodingMappingTable[i];
         if (eData != null && StringUtilities.startsWithIgnoreCase(encoding, eData._encoding, 1701707776)) {
            return eData;
         }
      }

      return null;
   }

   private final synchronized ConversionDataRegistryHelper$EncodingMappingData runOverTheData(int locale) {
      int length = this._encodingMappingTable.length;

      for (int i = 0; i < length; i++) {
         ConversionDataRegistryHelper$EncodingMappingData eData = this._encodingMappingTable[i];
         if (eData != null && eData._locale == locale) {
            return eData;
         }
      }

      return null;
   }

   private final int readHeader(DataInputStream stream) {
      try {
         int signature = this.readLEValue(stream, 4);
         stream.skip(2);
         int versionMajor = this.readLEValue(stream, 2);
         int versionMinor = this.readLEValue(stream, 2);
         if (signature == 1717724259 && versionMajor <= 1 && (versionMajor != 1 || versionMinor <= 0)) {
            stream.skip(2);
            return 12;
         } else {
            return 0;
         }
      } catch (IOException ioe) {
         return 0;
      }
   }

   private final int readLEValue(DataInputStream stream, int length) {
      int value = 0;

      for (int i = 0; i < length; i++) {
         value |= stream.readUnsignedByte() << i * 8;
      }

      return value;
   }

   private final void updateAvailableEncodings() {
      String[] swap = new String[this._supportedEncodingsNum];
      int length = this._encodingMappingTable.length;
      int i = 0;

      for (int j = 0; i < swap.length && j < length; j++) {
         ConversionDataRegistryHelper$EncodingMappingData eData = this._encodingMappingTable[i];
         if (eData != null && (this.isAlgorithmicallySupported(eData._id) || eData._binaryData != null)) {
            swap[i++] = eData._encoding;
         }
      }

      this._availableEncodings = swap;
   }

   private final boolean isAlgorithmicallySupported(int id) {
      switch (id) {
         case 0:
         case 1:
         case 18:
         case 27:
         case 28:
         case 39:
            return true;
         default:
            return false;
      }
   }
}
