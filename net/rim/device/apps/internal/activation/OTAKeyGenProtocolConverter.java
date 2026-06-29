package net.rim.device.apps.internal.activation;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.utility.serialization.BaseConverter;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.api.utility.serialization.ConverterDescriptor;
import net.rim.vm.Array;

final class OTAKeyGenProtocolConverter extends BaseConverter implements ConverterDescriptor {
   public static final long ID = 4560650054989280205L;
   public static final String RIM_OTAKEYGEN_PROTOCOL = "net.rim.OTAKeyGenProtocol";

   protected final String readStringField(DataBuffer dataBuffer, int length) {
      if (length > 0) {
         byte[] bytes = new byte[length];

         try {
            dataBuffer.readFully(bytes);
            if (bytes[length - 1] == 0) {
               Array.resize(bytes, length - 1);
            }

            return (String)(new Object(bytes));
         } finally {
            ;
         }
      } else {
         return null;
      }
   }

   protected final void writeStringField(DataBuffer dataBuffer, int fieldId, String value) {
      if (value != null) {
         byte[] bytes = value.getBytes();
         if (bytes.length > 0) {
            dataBuffer.writeByte(fieldId);
            dataBuffer.writeCompressedInt(bytes.length);
            dataBuffer.write(bytes, 0, bytes.length);
         }
      }
   }

   @Override
   public final Object getContext() {
      return TransmissionServiceManager.get(-7467774798685319400L).getContext();
   }

   @Override
   public final Converter createConverterInstance(String type) {
      return type.equals("net.rim.OTAKeyGenProtocol") ? this : null;
   }

   @Override
   public final boolean canConvert(Object inputObject, Object contextObject) {
      return inputObject instanceof OTAKeyGenEvent;
   }

   @Override
   public final boolean canConvert(byte[] inputBytes, Object contextObject) {
      byte command = inputBytes != null && inputBytes.length > 0 ? inputBytes[0] : -1;
      switch (command) {
         case 0:
            return false;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         default:
            return true;
      }
   }

   @Override
   public final Object convert(byte[] inputBytes, Object contextObject) {
      DataBuffer dataBuffer = (DataBuffer)(new Object(inputBytes, 0, inputBytes.length, true));
      OTAKeyGenEvent event = null;

      try {
         byte keyGenerationType = dataBuffer.readByte();
         dataBuffer.readByte();
         int transactionId = dataBuffer.readInt();
         byte command = dataBuffer.readByte();
         dataBuffer.readCompressedInt();
         event = new OTAKeyGenEvent();
         event._command = command;
         event._keyGenerationType = keyGenerationType;
         event._transactionId = transactionId;
         switch (command) {
            case 1:
               logEvent(1431194446, 2, command);
               throw new Object();
            case 2:
            default:
               while (!dataBuffer.eof()) {
                  byte[] buffer = null;
                  byte fieldId = dataBuffer.readByte();
                  int fieldLength = dataBuffer.readCompressedInt();
                  switch (fieldId) {
                     case 0:
                     case 4:
                     case 6:
                     case 7:
                        logEvent(1431194438, 2, fieldId);
                        throw new Object();
                     case 1:
                     default:
                        buffer = new byte[fieldLength];
                        dataBuffer.readFully(buffer);
                        event._serviceAuthenticationPublicKey = buffer;
                        break;
                     case 2:
                        buffer = new byte[fieldLength];
                        dataBuffer.readFully(buffer);
                        event._serviceLongTermPublicKey = buffer;
                        break;
                     case 3:
                        if (fieldLength != 1) {
                           throw new Object();
                        }

                        event._encryptionAlgorithm = dataBuffer.readByte();
                        break;
                     case 5:
                        event._fullKeyId = this.readStringField(dataBuffer, fieldLength);
                        break;
                     case 8:
                        event._serviceUID = this.readStringField(dataBuffer, fieldLength);
                        break;
                     case 9:
                        buffer = new byte[fieldLength];
                        dataBuffer.readFully(buffer);
                        event._keyHash = buffer;
                  }
               }
               break;
            case 3:
               while (!dataBuffer.eof()) {
                  byte[] buffer = null;
                  byte var14 = dataBuffer.readByte();
                  int var17 = dataBuffer.readCompressedInt();
                  switch (var14) {
                     case 5:
                        event._fullKeyId = this.readStringField(dataBuffer, var17);
                        break;
                     case 9:
                        buffer = new byte[var17];
                        dataBuffer.readFully(buffer);
                        event._keyHash = buffer;
                        break;
                     default:
                        logEvent(1431194438, 2, var14);
                        throw new Object();
                  }
               }
               break;
            case 4:
               while (!dataBuffer.eof()) {
                  byte var13 = dataBuffer.readByte();
                  int var16 = dataBuffer.readCompressedInt();
                  event._abortReason = var13;
                  switch (var13) {
                  }
               }
               break;
            case 5:
               while (!dataBuffer.eof()) {
                  byte fieldId = dataBuffer.readByte();
                  int fieldLength = dataBuffer.readCompressedInt();
                  switch (fieldId) {
                     case 5:
                        event._fullKeyId = this.readStringField(dataBuffer, fieldLength);
                  }
               }
         }

         return event;
      } finally {
         event._abortReason = 1;
         throw new Object("Malformed OTAKEYGEN packet.");
      }
   }

   @Override
   public final byte[] convert(Object inputObject, Object context) {
      if (!(inputObject instanceof OTAKeyGenEvent)) {
         return null;
      }

      OTAKeyGenEvent event = (OTAKeyGenEvent)inputObject;
      DataBuffer dataBuffer = (DataBuffer)(new Object());
      dataBuffer.writeByte(event._keyGenerationType);
      dataBuffer.writeByte(16);
      dataBuffer.writeInt(event._transactionId);
      dataBuffer.writeByte(event._command);
      DataBuffer payload = (DataBuffer)(new Object());
      if (event._command == 1) {
         this.writeBinaryField(payload, 1, event._deviceAuthenticationPublicKey);
         if (event._keyGenerationType == 1 || event._keyGenerationType == 2) {
            this.writeByteField(payload, 11, event._reKeyAlgorithm);
            this.writeBinaryField(payload, 2, event._deviceLongTermPublicKey);
         }

         this.writeByteField(payload, 3, event._encryptionAlgorithm);
         this.writeIntegerField(payload, 4, event._keySequenceHint);
         if (event._fullKeyId != null) {
            this.writeStringField(payload, 5, event._fullKeyId);
         }

         if (event._keyGenerationType == 1) {
            this.writeIntegerField(payload, 6, event._pin);
            this.writeByteField(payload, 7, event._networkType);
            if (event._deviceCapabilities != null) {
               this.writeBinaryField(payload, 10, event._deviceCapabilities);
            }
         }
      }

      if (event._command == 3) {
         this.writeStringField(payload, 5, event._fullKeyId);
         this.writeBinaryField(payload, 9, event._keyHash);
      }

      if (event._command == 4) {
         payload.writeByte(event._abortReason);
         payload.writeCompressedInt(0);
      }

      payload.trim();
      byte[] payloadBytes = payload.getArray();
      dataBuffer.writeByteArray(payloadBytes);
      dataBuffer.trim();
      return dataBuffer.getArray();
   }

   private final void writeIntegerField(DataBuffer dataBuffer, int fieldId, int value) {
      dataBuffer.writeByte(fieldId);
      dataBuffer.writeCompressedInt(4);
      dataBuffer.writeInt(value);
   }

   private static final void logEvent(int eventId, int level, int value) {
      EventLogger.logEvent(1200380696048604626L, value, level);
   }

   private final void writeByteField(DataBuffer dataBuffer, int fieldId, byte value) {
      dataBuffer.writeByte(fieldId);
      dataBuffer.writeCompressedInt(1);
      dataBuffer.writeByte(value);
   }

   private final void writeBinaryField(DataBuffer dataBuffer, int fieldId, byte[] data) {
      dataBuffer.writeByte(fieldId);
      dataBuffer.writeByteArray(data);
   }

   @Override
   public final boolean canConvert(Object inputObject) {
      return this.canConvert(inputObject, null);
   }
}
