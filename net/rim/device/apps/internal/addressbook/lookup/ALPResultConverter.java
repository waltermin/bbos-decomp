package net.rim.device.apps.internal.addressbook.lookup;

import java.io.DataInput;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.utility.serialization.BaseConverter;
import net.rim.device.apps.api.utility.serialization.SerializationException;

class ALPResultConverter extends BaseConverter {
   @Override
   public boolean canConvert(Object parameters) {
      return true;
   }

   private static void badInput() throws SerializationException {
      throw new SerializationException("Unknown ALP encoding");
   }

   private RIMModel parseAddress(LengthEncodedValueDataBuffer in, int amtToRead) {
      int stop = in.getPosition() + amtToRead;
      Result$Address addr = new Result$Address();

      while (true) {
         if (in.eof()) {
            badInput();
         }

         if (in.getPosition() == stop) {
            RIMModel model = addr.convertToModel();
            if (model == null) {
               badInput();
            }

            return model;
         }

         addr.setField(in.readUnsignedByte(), in.readByteArray());
      }
   }

   private Result parseResult(LengthEncodedValueDataBuffer in, int transactionId, int action) {
      Result result = new Result(transactionId, action);

      while (true) {
         if (in.eof()) {
            badInput();
         }

         int field = in.readUnsignedByte();
         if (field == 0) {
            if (!result.isCoherent()) {
               badInput();
            }

            return result;
         }

         switch (field) {
            case 1:
               RIMModel addr = this.parseAddress(in, in.readCompressedInt());
               result.addItem(addr);
               break;
            case 3:
               in.ReadLengthEncodedValue();
               break;
            case 4:
               result.setAvailableMatches(in.ReadLengthEncodedValue());
               break;
            case 129:
               result.setErrorString(in.readByteArray());
               break;
            case 130:
               result.setErrorCode(in.ReadLengthEncodedValue());
               break;
            default:
               int len = in.readCompressedInt();
               in.skipBytes(len);
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public Object convert(DataInput input, Object contextObject) {
      Result result = null;
      if (input instanceof DataBuffer) {
         DataBuffer dataBuffer = (DataBuffer)input;
         LengthEncodedValueDataBuffer dataBufferTLE = new LengthEncodedValueDataBuffer();
         dataBufferTLE.setData(dataBuffer.getArray(), dataBuffer.getArrayPosition(), dataBuffer.getLength());
         boolean var10 = false /* VF: Semaphore variable */;

         try {
            var10 = true;
            if (dataBufferTLE.readUnsignedByte() == 16) {
               int transactionId = dataBufferTLE.readInt();
               boolean var13 = false /* VF: Semaphore variable */;

               try {
                  var13 = true;
                  int e = dataBufferTLE.readUnsignedByte();
                  result = this.parseResult(dataBufferTLE, transactionId, e);
                  var10 = false;
                  var13 = false;
               } finally {
                  if (var13) {
                     result = new Result(transactionId, 129);
                     result.setErrorCode(1);
                     return result;
                  }
               }
            } else {
               var10 = false;
            }
         } finally {
            if (var10) {
               badInput();
               return result;
            }
         }
      }

      return result;
   }
}
