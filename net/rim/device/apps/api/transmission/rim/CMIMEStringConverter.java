package net.rim.device.apps.api.transmission.rim;

import com.sun.cldc.i18n.Helper;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.utility.serialization.BaseConverter;
import net.rim.device.apps.api.utility.serialization.SerializationException;

public final class CMIMEStringConverter extends BaseConverter {
   private static CMIMEStringConverter _instance;

   @Override
   public final boolean canConvert(Object parameters) {
      return true;
   }

   public static final synchronized CMIMEStringConverter getInstance() {
      if (_instance == null) {
         _instance = new CMIMEStringConverter();
      }

      return _instance;
   }

   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Object convert(byte[] inputBytes, Object contextObject) throws SerializationException {
      if (inputBytes != null && inputBytes.length != 0) {
         Parameters params = (Parameters)contextObject;
         String enc = null;
         byte[] rawType = null;
         int dataStart = 0;
         int dataLength = inputBytes.length;
         if (params != null) {
            rawType = params.getFirst((byte)1);
         }

         if (rawType != null && rawType.length > 0 && rawType[0] == 8) {
            byte encodingByte = inputBytes[0];
            dataStart = 1;
            if ((encodingByte & 128) == 128) {
               boolean var15 = false /* VF: Semaphore variable */;

               label135:
               try {
                  var15 = true;
                  encodingByte = (byte)(encodingByte & -129);
                  DataBuffer ex = new DataBuffer();
                  ex.setData(inputBytes, dataStart, dataLength - 1);
                  CMIMEParameters futureDataParams = new CMIMEParameters(ex, 2, 2);
                  dataStart = futureDataParams.read((byte)0);
                  if (ex.getArrayPosition() > dataStart) {
                     dataStart += ex.getArrayStart();
                  }

                  dataLength -= dataStart;
                  if (dataLength < 0) {
                     throw new SerializationException();
                  }

                  var15 = false;
               } finally {
                  if (var15) {
                     dataStart = 1;
                     dataLength = inputBytes.length - dataStart;
                     break label135;
                  }
               }
            } else {
               dataLength--;
            }

            enc = CMIMEUtilities.getEncoding(encodingByte);
            if (enc == null || enc.length() == 0) {
               enc = CMIMEUtilities.CMIME_DEFAULT_EMAIL_ENCODING;
            }
         } else {
            enc = this.getEncoding(CMIMEContentType.getFullType(rawType));
         }

         Object input = null;

         try {
            if (dataLength > 0) {
               input = Helper.byteToCharArray(inputBytes, dataStart, dataLength, enc);
            }
         } catch (Throwable var16) {
            throw new SerializationException(ex.toString());
         }

         if (input == null) {
            return "";
         } else {
            return input instanceof byte[] ? new String((byte[])input, 0, ((byte[])input).length) : new String((char[])input, 0, ((char[])input).length);
         }
      } else {
         return "";
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final byte[] convert(Object inputObject, Object contextObject) throws SerializationException {
      if (inputObject == null) {
         return null;
      }

      String input = (String)inputObject;
      String type = (String)contextObject;
      String enc = this.getEncoding(type);
      char[] output = input.toCharArray();

      try {
         return Helper.charToByteArray(output, 0, output.length, enc);
      } catch (Throwable var10) {
         throw new SerializationException(ex.toString());
      }
   }

   private final String getEncoding(String type) {
      String enc = CMIMEUtilities.fetchEncodingCharset(type);
      return enc != null ? enc : CMIMEUtilities.CMIME_DEFAULT_EMAIL_ENCODING;
   }
}
