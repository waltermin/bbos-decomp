package net.rim.device.apps.api.transmission.rim;

import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.messaging.messagelist.MessageListOptions;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.internal.i18n.UnicodeServiceUtilities;

public final class CMIMEUtilities implements CMIMEConstants {
   private static final int ENTRY_POINT_PARAMETERS_SUBFIELD;
   private static final byte ENTRY_POINT_CONTENT_URL;
   private static final byte ENTRY_POINT_ICON_VERSION;
   public static String CMIME_DEFAULT_EMAIL_ENCODING = "windows-1252\r";
   public static byte[] _encodingsArray;
   private static byte HINTS = 112;

   public static final Parameters getEntryPointParameters(ServiceRecord sr) {
      if (sr != null) {
         byte[] parametersAsBytes = parseApplicationDataForParameter(sr.getApplicationData(), (byte)-128);
         if (parametersAsBytes != null) {
            try {
               DataBuffer buffer = (DataBuffer)(new Object());
               buffer.setData(parametersAsBytes, 0, parametersAsBytes.length);
               Parameters parameters = new Parameters(2, 4);
               parameters.read(buffer, (byte)0);
               return parameters;
            } finally {
               return null;
            }
         }
      }

      return null;
   }

   public static final String getEntryPointContentURL(Parameters entryPointParameters) {
      if (entryPointParameters != null) {
         try {
            boolean isEncoded = entryPointParameters.has((byte)-127);
            byte contentURLTag = 1;
            if (isEncoded) {
               contentURLTag = (byte)(contentURLTag | 128);
            }

            byte[] contentURL = entryPointParameters.getFirst(contentURLTag);
            if (contentURL != null) {
               return UnicodeServiceUtilities.readString(contentURL, 0, contentURL.length, isEncoded);
            }
         } finally {
            ;
         }
      }

      return null;
   }

   public static final int getEntryPointIconVersion(Parameters entryPointParameters) {
      return decodeInteger(entryPointParameters.getFirst((byte)2));
   }

   public static final String getEmailAddress(int recordIdInt) {
      ServiceRecord record = ServiceBook.getSB().getRecordById(recordIdInt);
      return getEmailAddress(record);
   }

   public static final String getEmailAddress(ServiceRecord aServiceRecord) {
      return aServiceRecord != null ? getEmailAddress(aServiceRecord.getApplicationData()) : null;
   }

   public static final String getEmailAddress() {
      RIMMessagingService service = (RIMMessagingService)TransmissionServiceManager.get(8399767144006445082L);
      if (service != null) {
         ServiceRecord record = service.getOutgoingServiceRecord();
         return getEmailAddress(record);
      } else {
         return null;
      }
   }

   private static final String getEmailAddress(byte[] applicationDataByteArray) {
      String address = null;
      String[] addressStrings = decodeAddress(parseApplicationDataForParameter(applicationDataByteArray, (byte)16));
      if (addressStrings != null && addressStrings.length != 0) {
         address = getAddressPart(addressStrings);
      }

      return address;
   }

   public static final String getSecondLevelDomain(String emailAddress) {
      int atPosition = emailAddress.indexOf(64);
      if (atPosition < 0) {
         return null;
      }

      int lastDotPosition = emailAddress.lastIndexOf(46);
      if (lastDotPosition <= atPosition) {
         return null;
      }

      int startPosition = atPosition + 1;
      int secondLastDotPosition = emailAddress.lastIndexOf(46, lastDotPosition - 1);
      if (secondLastDotPosition > atPosition) {
         startPosition = secondLastDotPosition + 1;
      }

      return emailAddress.substring(startPosition);
   }

   public static final long getProfileSourceIDForService(ServiceRecord sr) {
      long result = -1;
      if (sr != null) {
         String emailAddress = getEmailAddress(sr);
         if (emailAddress == null) {
            emailAddress = sr.getUid();
         }

         if (emailAddress != null) {
            result = -1845850109141581824L;
            result |= CRC32.update(0, emailAddress.getBytes());
         }
      }

      return result;
   }

   public static final byte[] getServerEncoding(byte[] aApplicationData) {
      return aApplicationData != null ? parseApplicationDataForParameter(aApplicationData, (byte)96) : null;
   }

   public static final byte[] getEncodings() {
      if (_encodingsArray == null) {
         _encodingsArray = UnicodeServiceUtilities.getSupportedEncodings();
      }

      return _encodingsArray;
   }

   public static final boolean getServiceCapability(ServiceRecord sr, byte capabilitiesSubfield, byte capability, boolean defaultValue) {
      if (sr != null) {
         int serviceCapabilities = getCapabilities(sr.getApplicationData(), capabilitiesSubfield);
         if (serviceCapabilities != -1) {
            if ((serviceCapabilities & capability) != 0) {
               return true;
            }

            return false;
         }
      }

      return defaultValue;
   }

   public static final boolean getMoreAllAllowed(ServiceRecord sr) {
      return getServiceCapability(sr, (byte)-15, (byte)64, true);
   }

   public static final boolean isOutOfOfficeEndDateSupported(ServiceRecord sr) {
      if (sr != null) {
         int sc = getCapabilities(sr.getApplicationData(), (byte)-14);
         if (sc != -1) {
            if ((sc & 1) != 0) {
               return true;
            }

            return false;
         }
      }

      return false;
   }

   private static final int getCapabilities(byte[] applicationDataByteArray, byte subfieldId) {
      int capabilities = -1;
      byte[] b = parseApplicationDataForParameter(applicationDataByteArray, subfieldId);
      if (b != null) {
         for (int i = 0; i < b.length; i++) {
            capabilities = (capabilities << 8) + b[i];
         }
      }

      return capabilities;
   }

   private static final byte[] parseApplicationDataForParameter(byte[] applicationDataByteArray, byte parameterToFetchAgainst) {
      if (applicationDataByteArray != null && applicationDataByteArray.length > 0) {
         try {
            DataBuffer buffer = (DataBuffer)(new Object());
            buffer.setData(applicationDataByteArray, 0, applicationDataByteArray.length);
            buffer.readUnsignedByte();
            Parameters parameters = new Parameters(12, 4);
            parameters.read(buffer, (byte)0);
            return parameters.getFirst(parameterToFetchAgainst);
         } finally {
            ;
         }
      } else {
         return null;
      }
   }

   public static final int calculateLengthOfCompressedLength(int lengthInt) {
      int result = 1;
      if (lengthInt >= 268435456) {
         return 5;
      }

      if (lengthInt >= 2097152) {
         return 4;
      }

      if (lengthInt >= 16384) {
         return 3;
      }

      if (lengthInt >= 128) {
         result = 2;
      }

      return result;
   }

   public static final String[] decodeAddress(byte[] byteArray) {
      return decodeAddress(byteArray, false);
   }

   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final String[] decodeAddress(byte[] byteArray, boolean isUnicodeEnabled) {
      String[] address = null;
      if (byteArray != null && byteArray.length > 0) {
         int offset = -1;
         int length = byteArray.length;
         int count = 0;
         int firstDataByte = 0;
         String encString = CMIME_DEFAULT_EMAIL_ENCODING;
         if (isUnicodeEnabled) {
            byte encodingByte = byteArray[0];
            int var19 = 1;
            int var24 = true;
            if ((encodingByte & 128) == 128) {
               boolean var15 = false /* VF: Semaphore variable */;

               label147:
               try {
                  var15 = true;
                  encodingByte = (byte)(encodingByte & -129);
                  DataBuffer stringLength = new Object();
                  ((DataBuffer)stringLength).setData(byteArray, var19, length - var19);
                  CMIMEParameters futureDataParams = new CMIMEParameters((DataBuffer)stringLength, 2, 2);
                  firstDataByte = var19 = futureDataParams.read((byte)0);
                  if (((DataBuffer)stringLength).getArrayPosition() > var19) {
                     var19 += ((DataBuffer)stringLength).getArrayStart();
                     firstDataByte = var19;
                  }

                  if (firstDataByte >= length) {
                     return address;
                  }

                  var15 = false;
               } finally {
                  if (var15) {
                     int var21 = true;
                     firstDataByte = 1;
                     break label147;
                  }
               }
            } else {
               int var20 = true;
               firstDataByte = 1;
            }

            encString = getEncoding(encodingByte);
            if (encString == null || encString.length() == 0) {
               encString = CMIME_DEFAULT_EMAIL_ENCODING;
            }
         }

         try {
            String wholeAddress = (String)(new Object(byteArray, firstDataByte, length - firstDataByte, encString));
            if (wholeAddress != null && wholeAddress.length() > 0) {
               int stringLength = wholeAddress.length();
               offset = wholeAddress.indexOf(0);
               if (offset == -1) {
                  address = new Object[]{wholeAddress};
               } else {
                  if (offset != 0 && offset != stringLength - 1 || stringLength <= 1) {
                     return new Object[]{wholeAddress.substring(0, offset), wholeAddress.substring(offset + 1, stringLength)};
                  }

                  address = new Object[1];
                  if (offset == 0) {
                     address[0] = wholeAddress.substring(1, stringLength);
                  } else {
                     address[0] = wholeAddress.substring(0, stringLength - 1);
                  }
               }
            }
         } catch (Throwable var16) {
            System.out.println(e.getMessage());
            return address;
         }
      }

      return address;
   }

   public static final String[][][] decodeAddresses(byte[][][] byteArrays) {
      return decodeAddresses(byteArrays, false);
   }

   public static final String[][][] decodeAddresses(byte[][][] byteArrays, boolean isUnicodeEnabled) {
      String[][][] addresses = (Object[][])null;
      if (byteArrays != null) {
         int length = byteArrays.length;
         addresses = new Object[length][][];

         for (int count = 0; count < length; count++) {
            addresses[count] = decodeAddress((byte[])byteArrays[count], isUnicodeEnabled);
         }
      }

      return addresses;
   }

   public static final String getAddressPart(String[] addressStrings) {
      return addressStrings != null && addressStrings.length >= 1 ? addressStrings[0] : null;
   }

   public static final String getFriendlyPart(String[] addressStrings) {
      return addressStrings != null && addressStrings.length > 1 ? addressStrings[1] : null;
   }

   public static final int newDeviceSideIdentifier() {
      int identifier = RandomSource.getInt();
      return identifier >= 0 ? identifier : -identifier;
   }

   public static final int decodeInteger(Parameters aParameters, byte nameByte) {
      byte[] bytes = aParameters.getFirst(nameByte);
      return bytes != null ? decodeInteger(bytes) : -1;
   }

   public static final int decodeInteger(byte[] byteArray) {
      int value = 0;
      if (byteArray != null) {
         int len = byteArray.length;
         if (len <= 4) {
            for (int i = 0; i < len; i++) {
               value = value << 8 | byteArray[i] & 255;
            }
         }
      }

      return value;
   }

   public static final int getGMEInteger(byte[][][] byteArrays, int indexInt) {
      int integer = 0;
      if (byteArrays != null && byteArrays.length > indexInt) {
         integer = decodeInteger((byte[])byteArrays[indexInt]);
      }

      return integer;
   }

   public static final int getGMEInteger(byte[][][] byteArrays, int indexInt, int defualtValue) {
      int integer = defualtValue;
      if (byteArrays != null && byteArrays.length > indexInt) {
         integer = decodeInteger((byte[])byteArrays[indexInt]);
      }

      return integer;
   }

   public static final Object getTextObject(byte[] byteArray) {
      return getTextObject(byteArray, null);
   }

   public static final Object getTextObject(byte[] byteArray, Object context) {
      return CMIMEConverterRegistry.getDefaultConverter("text/plain").convert(byteArray, context);
   }

   public static final Object getTextObject(byte[] byteArray, boolean isEncoded) {
      return byteArray != null && byteArray.length != 0 ? getTextObject(byteArray, 0, byteArray.length, isEncoded, null) : "";
   }

   public static final Object getTextObject(byte[] byteArray, int offset, int length, boolean isEncoded, CMIMEParameters params) {
      if (byteArray != null && byteArray.length != 0 && offset >= 0 && length >= 0 && offset + length <= byteArray.length) {
         String enc = null;
         int i = 0;
         if (isEncoded) {
            i = 1;
            byte encodingByte = byteArray[offset];
            if ((encodingByte & 128) == 128) {
               try {
                  encodingByte = (byte)(encodingByte & -129);
                  String encodingName = getEncoding(encodingByte);
                  if (encodingName != null && encodingName.length() > 0) {
                     DataBuffer dataBuffer = (DataBuffer)(new Object());
                     offset++;
                     length--;
                     int var17 = 0;
                     dataBuffer.setData(byteArray, offset + var17, length - var17);
                     CMIMEParameters futureDataParams = params;
                     if (futureDataParams == null) {
                        futureDataParams = new CMIMEParameters(dataBuffer, 2, 2);
                     } else {
                        futureDataParams.setDataBuffer(dataBuffer);
                     }

                     i = futureDataParams.read((byte)0);
                     if (dataBuffer.getArrayPosition() <= i) {
                        i -= dataBuffer.getArrayStart();
                     }

                     if (i >= length) {
                        return "";
                     }
                  }
               } finally {
                  ;
               }
            }

            enc = getEncoding(encodingByte);
         }

         if (enc == null || enc.length() == 0) {
            enc = CMIME_DEFAULT_EMAIL_ENCODING;
         }

         try {
            return new Object(byteArray, offset + i, length - i, enc);
         } finally {
            return new Object(byteArray, offset + i, length - i);
         }
      } else {
         return "";
      }
   }

   public static final String getTextContentType() {
      return getTextContentType(null);
   }

   public static final String getTextContentType(ServiceRecord sRecord) {
      return sRecord != null ? getTextContentType(sRecord.getApplicationData(), true) : "text/plain";
   }

   public static final String getTextContentType(byte[] aApplicationData, boolean parseToGetData) {
      if (aApplicationData != null) {
         byte curEnc = resolveEncoding(parseToGetData ? getServerEncoding(aApplicationData) : aApplicationData);
         if (curEnc != -1) {
            curEnc = (byte)(curEnc & (byte)(~HINTS));
            return ((StringBuffer)(new Object("text/plain;charset="))).append(getEncoding(curEnc)).toString();
         }
      }

      return "text/plain";
   }

   public static final String getTextContentType(byte encoding) {
      if (encoding != -1) {
         encoding = (byte)(encoding & (byte)(~HINTS));
         String encodingName = getEncoding(encoding);
         if (encodingName != null && encodingName.length() > 0) {
            return ((StringBuffer)(new Object("text/plain;charset="))).append(encodingName).toString();
         }
      }

      return "text/plain";
   }

   public static final byte[] getTextByteArray(Object anObject, Object aContext) {
      return CMIMEConverterRegistry.getDefaultConverter("text/plain").convert(anObject, aContext);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final Parameters createContentDispositionParameters(RIMMessagingOutgoingMessage target, String fileName) {
      if (target != null && fileName != null) {
         byte[] fileNameBytes = null;
         boolean isEncoded = target.isEncoded() && !ConverterUtilities.isIntellisyncCompatible(fileName);
         byte encByte = -1;
         if (isEncoded) {
            String enc = fetchEncodingCharset(target.getTextType());
            boolean var12 = false /* VF: Semaphore variable */;

            label84:
            try {
               var12 = true;
               fileNameBytes = fileName.getBytes(enc);
               encByte = getEncoding(enc);
               var12 = false;
            } finally {
               if (var12) {
                  fileNameBytes = fileName.getBytes();
                  isEncoded = false;
                  break label84;
               }
            }
         } else {
            boolean var9 = false /* VF: Semaphore variable */;

            label81:
            try {
               var9 = true;
               fileNameBytes = fileName.getBytes(CMIME_DEFAULT_EMAIL_ENCODING);
               var9 = false;
            } finally {
               if (var9) {
                  fileNameBytes = fileName.getBytes();
                  break label81;
               }
            }
         }

         Parameters p = new Parameters((DataBuffer)(new Object()), 1, 1);
         if (isEncoded) {
            p.add((byte)-6, encByte, fileNameBytes);
         } else {
            p.add((byte)-14, fileNameBytes);
         }

         DataBuffer db = (DataBuffer)(new Object());
         db.writeByte(1);
         if (isEncoded) {
            db.writeByte(129);
            db.writeCompressedInt(fileNameBytes.length + 1);
            db.writeByte(encByte);
            db.write(fileNameBytes, 0, fileNameBytes.length);
         } else {
            db.writeByte(1);
            db.writeByteArray(fileNameBytes);
         }

         p.add((byte)2, db.getArray());
         return p;
      } else {
         return null;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final boolean addTextEncoded(DataBuffer buffer, String text, byte encodingCode, boolean completeData, byte forcedEncoding) {
      if (text != null && buffer != null) {
         int length = text.length();
         int textLengthCompressedSize = calculateLengthOfCompressedLength(length);
         boolean encoded = true;
         String encoding = null;
         byte[] bytes = null;
         buffer.ensureCapacity(length + textLengthCompressedSize);
         byte[] bufferBytes = buffer.getArray();
         int arrayPosition = buffer.getArrayPosition();
         if (completeData) {
            buffer.setPosition(arrayPosition + textLengthCompressedSize);
         }

         encoded = !ConverterUtilities.writeStringDefault(buffer, text);
         boolean forcedEncode = !encoded && forcedEncoding != -1;
         if (forcedEncode) {
            encodingCode = forcedEncoding;
            encoded = true;
         }

         if (encoded) {
            encoding = getEncoding(encodingCode);
            if (encoding == null || encoding.length() == 0) {
               encoding = CMIME_DEFAULT_EMAIL_ENCODING;
               encoded = false;
            }

            boolean var15 = false /* VF: Semaphore variable */;

            label121:
            try {
               var15 = true;
               bytes = text.getBytes(encoding);
               if (bytes != null) {
                  length = bytes.length;
                  textLengthCompressedSize = calculateLengthOfCompressedLength(length + (encoded ? 1 : 0));
                  buffer.ensureCapacity(length + textLengthCompressedSize + (encoded ? 1 : 0));
                  var15 = false;
               } else {
                  var15 = false;
               }
            } finally {
               if (var15) {
                  bytes = text.getBytes();
                  encoded = false;
                  if (bytes != null) {
                     length = bytes.length;
                     textLengthCompressedSize = calculateLengthOfCompressedLength(length);
                     buffer.ensureCapacity(length + textLengthCompressedSize);
                  } else {
                     length = 0;
                  }
                  break label121;
               }
            }
         }

         buffer.setPosition(arrayPosition);
         if (completeData) {
            buffer.writeCompressedInt(length + (encoded ? 1 : 0));
         }

         if (encoded) {
            if (completeData) {
               buffer.writeByte(encodingCode);
            }

            if (bytes != null) {
               arrayPosition = buffer.getArrayPosition();
               System.arraycopy(bytes, 0, bufferBytes, arrayPosition, length);
            }
         }

         buffer.skipBytes(length);
         buffer.trim(false);
         return forcedEncode && encoded ? false : encoded;
      } else {
         return false;
      }
   }

   public static final String getEncoding(byte encodingType) {
      return UnicodeServiceUtilities.getEncoding(encodingType);
   }

   public static final byte getEncoding(String encodingType) {
      return UnicodeServiceUtilities.getEncoding(encodingType);
   }

   public static final byte resolveEncoding(byte[] encodingsToDecideAgainst) {
      return addHints(UnicodeServiceUtilities.resolveEncoding(getEncodings(), encodingsToDecideAgainst));
   }

   public static final byte addHints(byte encoding) {
      if (encoding != -1) {
         Locale locale = Locale.getDefaultInputForSystem();
         if (locale != null) {
            switch (locale.getCode()) {
               case 1784741888:
               case 1784760912:
                  return (byte)(encoding | 16);
               case 1802436608:
                  return (byte)(encoding | 32);
               case 2053653326:
                  return (byte)(encoding | 48);
               case 2053654603:
               case 2053657687:
                  encoding = (byte)(encoding | 64);
            }
         }
      }

      return encoding;
   }

   public static final byte addHints(byte encoding, Font font) {
      encoding = (byte)(encoding & ~HINTS);
      if (font != null) {
         switch (font.getStyle() & 7168) {
            case 1024:
               encoding = (byte)(encoding | 64);
               break;
            case 2048:
               return (byte)(encoding | 48);
            case 3072:
               return (byte)(encoding | 16);
            case 4096:
               return (byte)(encoding | 32);
         }
      }

      return encoding;
   }

   public static final byte replaceHints(byte originalEncoding, byte newEncoding) {
      if (newEncoding != -1 && originalEncoding != -1) {
         originalEncoding = (byte)(originalEncoding & ~HINTS);
         newEncoding = (byte)(newEncoding & HINTS);
         originalEncoding = (byte)(originalEncoding | newEncoding);
      }

      return originalEncoding;
   }

   public static final int retrieveHintsLocale(byte encoding) {
      if (encoding != -1) {
         encoding = (byte)(encoding & HINTS);
         switch (encoding) {
            case 16:
               return 1784760912;
            case 32:
               return 1802436608;
            case 48:
               return 2053653326;
            case 64:
               return 2053657687;
         }
      }

      return -1;
   }

   public static final Font getSuggestedFontForEncoding(byte encoding) {
      int hint = getFontHint(encoding);
      if (hint != -1) {
         Font f = FontRegistry.getDefaultFont();
         int style = f.getStyle() & -3;
         style &= -7169;
         return f.derive(style | hint);
      } else {
         return null;
      }
   }

   private static final int getFontHint(byte encoding) {
      int hint = -1;
      switch (retrieveHintsLocale(encoding)) {
         case 1784760912:
            return 3072;
         case 1802436608:
            return 4096;
         case 2053653326:
            return 2048;
         case 2053657687:
            hint = 1024;
         default:
            return hint;
      }
   }

   public static final Font getSuggestedFontForEncoding(byte encoding, Font font) {
      int hint = getFontHint(encoding);
      if (hint != -1 && font != null) {
         Font f = FontRegistry.getDefaultFont();
         int style = f.getStyle() & -3;
         style &= -7169;
         return f.derive(style | hint, font.getHeight(), 0, font.getAntialiasMode(), font.getEffects(), font.getTransform());
      } else {
         return null;
      }
   }

   public static final byte parseEncoding(String data) {
      return getEncoding(fetchEncodingCharset(data));
   }

   static final String fetchEncodingCharset(String data) {
      if (data != null) {
         int index = data.indexOf("charset");
         if (index != -1) {
            index = data.indexOf(61, index + 5);
            if (index != -1) {
               int end = data.indexOf(59, index + 1);
               if (end == -1) {
                  end = data.length();
               }

               int tmp = data.indexOf(34, ++index);
               if (tmp != -1 && tmp < end) {
                  index = tmp + 1;
                  tmp = data.indexOf(34, index);
                  if (tmp != -1 && tmp < end) {
                     end = tmp;
                  }
               }

               return data.substring(index, end).trim();
            }
         }
      }

      return null;
   }

   public static final int getDeleteOnLocation(ServiceRecord serviceRecord) {
      String name = null;
      String uid = null;
      int defaultLocation = -1;
      if (serviceRecord != null) {
         name = serviceRecord.getName();
         uid = serviceRecord.getUid();
         defaultLocation = getDeleteOnLocationDefault(serviceRecord);
      }

      return MessageListOptions.getOptions().getDeleteOnLocation(name, uid, defaultLocation);
   }

   private static final int getDeleteOnLocationDefault(ServiceRecord serviceRecord) {
      byte[] applicationData = serviceRecord.getApplicationData();
      if (applicationData != null) {
         int index = 1;
         int count = applicationData.length;

         try {
            while (index < count && applicationData[index] != 0) {
               if (applicationData[index] == 82) {
                  if (applicationData[++index] == 1) {
                     index++;
                     switch (applicationData[index]) {
                        case 0:
                        default:
                           return 0;
                        case 1:
                           return 1;
                        case 2:
                           return 2;
                     }
                  }
               } else {
                  index = ++index + (applicationData[index] & 255) + 1;
               }
            }
         } finally {
            return -1;
         }
      }

      return -1;
   }

   public static final boolean canSendEmail() {
      TransmissionService transmissionService = TransmissionServiceManager.get(8399767144006445082L);
      return (transmissionService.getStatus() & 1) != 0;
   }

   public static final long getNativeAttachmentMfhMaxTotalSizeFromServiceRecord(ServiceRecord serviceRecord) {
      return getLongValueFromServiceRecord(serviceRecord, (byte)-108);
   }

   public static final long getNativeAttachmentMfhMaxSizeFromServiceRecord(ServiceRecord serviceRecord) {
      return getLongValueFromServiceRecord(serviceRecord, (byte)-107);
   }

   public static final long getNativeAttachmentMthMaxSize(ServiceRecord serviceRecord) {
      return getLongValueFromServiceRecord(serviceRecord, (byte)-106);
   }

   public static final long getNativeAttachmentChunkSizeFromServiceRecord(ServiceRecord serviceRecord) {
      return getLongValueFromServiceRecord(serviceRecord, (byte)-109);
   }

   private static final int getLongValueFromServiceRecord(ServiceRecord serviceRecord, byte tag) {
      if (serviceRecord == null) {
         return 0;
      }

      byte[] data = parseApplicationDataForParameter(serviceRecord.getApplicationData(), tag);
      return decodeInteger(data);
   }

   public static final boolean isLargeAttachmentUploadAllowed(ServiceRecord sr) {
      if (!isFileAttachmentAllowedByItPolicy()) {
         return false;
      }

      long serviceRecordMaxAttachmentSize = getNativeAttachmentMfhMaxSizeFromServiceRecord(sr);
      long itPolicyValue = ITPolicy.getInteger(23, 6, -1);
      return itPolicyValue == -1 ? serviceRecordMaxAttachmentSize > 0 : serviceRecordMaxAttachmentSize > 0 && itPolicyValue > 0;
   }

   public static final long getNativeAttachmentMfhMaxSize(ServiceRecord serviceRecord) {
      long itPolicyValue = ITPolicy.getInteger(23, 7, -1);
      return itPolicyValue == -1
         ? getNativeAttachmentMfhMaxSizeFromServiceRecord(serviceRecord)
         : Math.min(itPolicyValue, getNativeAttachmentMfhMaxSizeFromServiceRecord(serviceRecord));
   }

   public static final long getNativeAttachmentMfhMaxTotalSize(ServiceRecord serviceRecord) {
      long itPolicyValue = ITPolicy.getInteger(23, 6, -1);
      return itPolicyValue == -1
         ? getNativeAttachmentMfhMaxTotalSizeFromServiceRecord(serviceRecord)
         : Math.min(itPolicyValue, getNativeAttachmentMfhMaxTotalSizeFromServiceRecord(serviceRecord));
   }

   public static final boolean isFileAttachmentAllowedByItPolicy() {
      return 0 != ITPolicy.getInteger(23, 7, -1);
   }

   public static final boolean isLargeAttachmentUploadSupportedByAnyCMIMEService() {
      ServiceBook sb = ServiceBook.getSB();

      for (ServiceRecord record : sb.findRecordsByCid("CMIME")) {
         if (isLargeAttachmentUploadAllowed(record)) {
            return true;
         }
      }

      return false;
   }

   public static final ServiceRecord findFirstAvailableServiceRecordSupportingNativeAttachment() {
      ServiceBook sb = ServiceBook.getSB();

      for (ServiceRecord record : sb.findRecordsByCid("CMIME")) {
         if (isLargeAttachmentUploadAllowed(record)) {
            return record;
         }
      }

      return null;
   }
}
