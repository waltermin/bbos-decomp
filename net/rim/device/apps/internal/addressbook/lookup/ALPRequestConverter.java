package net.rim.device.apps.internal.addressbook.lookup;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.utility.serialization.BaseConverter;
import net.rim.device.apps.api.utility.serialization.SerializationException;

final class ALPRequestConverter extends BaseConverter {
   @Override
   public final boolean canConvert(Object parameters) {
      return true;
   }

   private static final byte[] genOrderField(long order) {
      if (order == -4388042602796535003L) {
         return new byte[]{33, 0, 57, 56, 0, 56, 57, 0, 1};
      }

      byte[] ret = new byte[]{56, 57, 0, 57, 56, 0, 33, 0, 1};
      if (order == -227891759293611117L) {
         ret[0] = 57;
         ret[1] = 56;
         ret[3] = 56;
         ret[4] = 57;
      }

      return ret;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final byte[] convert(Object inputObject, Object contextObject) throws SerializationException {
      if (!(inputObject instanceof Request)) {
         throw new SerializationException();
      }

      Request request = (Request)inputObject;
      LengthEncodedValueDataBuffer dataBuffer = new LengthEncodedValueDataBuffer();
      dataBuffer.writeByte(16);
      dataBuffer.writeInt(request._transactionId);
      dataBuffer.writeByte(1);
      String search = request._search;
      if (search != null && search.length() != 0) {
         String textType = CMIMEUtilities.getTextContentType();
         byte encodingByte = -1;
         boolean toBeEncoded = false;
         byte[] convertedBytes = ALPConfiguration.getEncodingData();
         if (convertedBytes != null && !ConverterUtilities.isIntellisyncCompatible(search)) {
            textType = CMIMEUtilities.getTextContentType(convertedBytes, false);
            encodingByte = CMIMEUtilities.parseEncoding(textType);
            toBeEncoded = encodingByte != -1;
         } else {
            toBeEncoded = false;
         }

         boolean var12 = false /* VF: Semaphore variable */;

         label78:
         try {
            var12 = true;
            convertedBytes = CMIMEUtilities.getTextByteArray(search, textType);
            var12 = false;
         } finally {
            if (var12) {
               convertedBytes = search.getBytes();
               toBeEncoded = false;
               System.out.println("ALP search string serialization error");
               break label78;
            }
         }

         if (!toBeEncoded) {
            dataBuffer.writeByte(2);
            dataBuffer.writeCompressedInt(convertedBytes.length);
         } else {
            dataBuffer.writeByte(9);
            dataBuffer.writeCompressedInt(convertedBytes.length + 1);
            dataBuffer.writeByte(encodingByte);
         }

         dataBuffer.write(convertedBytes, 0, convertedBytes.length);
         byte[] orderBytes = genOrderField(request._sortOrder);
         if (orderBytes != null) {
            dataBuffer.writeByte(8);
            dataBuffer.writeByteArray(orderBytes);
         }

         if (request._numMatches != 0) {
            dataBuffer.writeByte(6);
            dataBuffer.WriteLengthEncodedValue(request._numMatches);
         }

         if (request._desiredFields.length > 0) {
            dataBuffer.writeByte(7);
            dataBuffer.writeByteArray(request._desiredFields);
         }

         if (request._offsetIntoMatches != 0) {
            dataBuffer.writeByte(5);
            dataBuffer.WriteLengthEncodedValue(request._offsetIntoMatches);
         }

         dataBuffer.writeByte(0);
         return dataBuffer.toArray();
      } else {
         throw new SerializationException("ALP search string is invalid");
      }
   }
}
