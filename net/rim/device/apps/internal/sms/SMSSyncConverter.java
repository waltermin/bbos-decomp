package net.rim.device.apps.internal.sms;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;

public final class SMSSyncConverter implements SyncConverter {
   public static final int CURRENT_VERSION;
   public static final int FIRST_VERSION_SUPPORTED;
   public static final int FAKE_DATAGRAM_ID;
   private static final ContextObject INBOUND_CONTEXT = (ContextObject)(new Object(38));
   private static Factory _phoneNumberFactory;
   private static Factory _emailAddressFactory;
   private static ContextObject _smsConversionContext = (ContextObject)(new Object(55, 19));

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (object == null) {
         return false;
      }

      ConversionProvider converter = (ConversionProvider)object;
      SyncBuffer syncBuffer = (SyncBuffer)(new Object(buffer, version, 0));
      return converter.convert(_smsConversionContext, syncBuffer);
   }

   @Override
   public final SyncObject convert(DataBuffer dataBuffer, int version, int uid) {
      if (version < 1) {
         return null;
      }

      int transmissionError = 0;
      int status = 33554431;
      int flags = 0;
      PersistableRIMModel address = null;
      SMSModel message = null;
      SyncBuffer syncBuffer = (SyncBuffer)(new Object(dataBuffer, version, uid));

      try {
         while (!syncBuffer.isEmpty()) {
            int position = syncBuffer.getPosition();
            int fieldType = syncBuffer.getFieldType();
            if (fieldType != 255) {
               switch (fieldType) {
                  case 0:
                     break;
                  case 1:
                  default:
                     dataBuffer.readShort();
                     dataBuffer.readByte();
                     boolean inbound = dataBuffer.readBoolean();
                     flags = dataBuffer.readInt();
                     status = dataBuffer.readInt();
                     transmissionError = dataBuffer.readInt();
                     long creationDate = dataBuffer.readLong();
                     long transmissionDate = dataBuffer.readLong();
                     int data0 = 0;
                     int data1 = 0;
                     if (version >= 2) {
                        data0 = dataBuffer.readInt();
                        data1 = dataBuffer.readInt();
                     }

                     int msgType = 0;
                     if (version >= 4) {
                        msgType = dataBuffer.readInt();
                     }

                     if (inbound) {
                        message = SMSModelFactory.createSMSModel(msgType, INBOUND_CONTEXT);
                     } else {
                        message = SMSModelFactory.createSMSModel(msgType, null);
                     }

                     message._payload._creationDate = creationDate;
                     message._payload._transmissionDate = transmissionDate;
                     if (version >= 5) {
                        message.changeStatus(flags, 0, status, transmissionError, false, false, false, null);
                     }

                     message._payload.setData0(data0);
                     message._payload.setData1(data1);
                     break;
                  case 2:
                     address = this.extractAddress(syncBuffer, false);
                     if (message != null && address != null && version >= 5) {
                        message.addAddress(address);
                     }
                     break;
                  case 3:
                     String s = ConverterUtilities.readString(dataBuffer, true);
                     byte[] tfiData = null;

                     label204:
                     try {
                        tfiData = s.getBytes(SMSService.getSmsEncoder(message._payload.getByteField(0)));
                     } finally {
                        break label204;
                     }

                     if (message != null) {
                        message.setData(tfiData);
                     }
                     break;
                  case 4:
                     byte[] dfiData = ConverterUtilities.readByteArray(dataBuffer, true);
                     if (message != null) {
                        message.setData(dfiData);
                     }
                     break;
                  case 5:
                     byte[] udhfiData = ConverterUtilities.readByteArray(dataBuffer, true);
                     if (message != null) {
                        message._payload._userDataHeader = udhfiData;
                     }
                     break;
                  case 6:
                     address = this.extractAddress(syncBuffer, false);
                     if (message != null && address != null) {
                        message._payload.setCallbackAddress(address);
                     }
                     break;
                  case 7:
                     byte[] bfiData = ConverterUtilities.readByteArray(dataBuffer, true);
                     if (message != null) {
                        message._payload._byteFields = bfiData;
                     }
                     break;
                  case 8:
                     address = this.extractAddress(syncBuffer, true);
                     if (message != null && address != null && version >= 5) {
                        message.addAddress(address);
                     }
                     break;
                  case 9:
                     int[] safiData = ConverterUtilities.readIntArray(dataBuffer);
                     if (message != null) {
                        message.restoreStatusArray(safiData);
                     }
                     break;
                  case 10:
                     int[] eafiData = ConverterUtilities.readIntArray(dataBuffer);
                     if (message != null) {
                        message.restoreErrorArray(eafiData);
                     }
                     break;
                  case 11:
                     int[] tnafiData = ConverterUtilities.readIntArray(dataBuffer);
                     if (message != null) {
                        message._payload.setTONAndNPI(tnafiData);
                     }
               }
            }

            syncBuffer.setPosition(position);
            syncBuffer.skipField();
         }
      } finally {
         ;
      }

      if (message != null) {
         if (version < 5 && address != null) {
            ContextObject ctx = (ContextObject)(new Object());
            Integer datagramIDObject = (Integer)(new Object(1));
            ContextObject.put(ctx, -8210557334250400979L, datagramIDObject);
            int TON = message._payload.getByteField(8);
            int NPI = message._payload.getByteField(9);
            message.addAddress(address, TON, NPI);
            message.setDatagramID(0, 1);
            message.changeStatus(flags, 0, status, transmissionError, false, false, false, ctx);
         }

         message.groupMessage();
      }

      return message;
   }

   private final PersistableRIMModel extractAddress(SyncBuffer syncBuffer, boolean email) {
      Factory factory = null;
      if (email) {
         if (_emailAddressFactory == null) {
            _emailAddressFactory = (Factory)ApplicationRegistry.getApplicationRegistry().get(-2985347935260258684L);
         }

         factory = _emailAddressFactory;
      } else {
         if (_phoneNumberFactory == null) {
            _phoneNumberFactory = (Factory)ApplicationRegistry.getApplicationRegistry().get(3797587162219887872L);
         }

         factory = _phoneNumberFactory;
      }

      if (factory == null) {
         return null;
      }

      _smsConversionContext.put(255, syncBuffer);
      PersistableRIMModel address = (PersistableRIMModel)factory.createInstance(_smsConversionContext);
      _smsConversionContext.remove(255);
      return address;
   }
}
