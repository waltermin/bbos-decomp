package net.rim.device.apps.internal.mms.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.MMSAttachment;

public final class MMSSyncConverter implements SyncConverter {
   private static final int FIRST_VERSION_SUPPORTED = 1;
   private static final int LAST_VERSION_SUPPORTED = 1;
   private static Factory _phoneNumberFactory;
   private static Factory _emailAddressFactory;
   private static ContextObject _context = new ContextObject();
   private static final int MESSAGE_FLAGS = 1;
   private static final int FOLDER_ID = 2;
   private static final int STATUS = 3;
   private static final int HTTP_ERROR_CODE = 4;
   private static final int MMS_RESPONSE_CODE = 5;
   private static final int WAP_IO_EXCEPTION_ERROR = 6;
   private static final int WAP_IO_EXCEPTION_ADDITIONAL_DATA = 7;
   private static final int CREATION_DATE = 50;
   private static final int PAYLOAD_ATTRIBUTE_NAME = 51;
   private static final int PAYLOAD_ATTRIBUTE_DATA_STRING = 52;
   private static final int DELIVERY_DATE = 53;
   private static final int READ_DATE = 54;
   private static final int DELIVERY_REPORT_ADDRESS = 60;
   private static final int DELIVERY_REPORT_DATE = 61;
   private static final int DELIVERY_REPORT_STATUS = 62;
   private static final int READ_REPORT_ADDRESS = 63;
   private static final int READ_REPORT_DATE = 64;
   private static final int READ_REPORT_STATUS = 65;
   private static final int LEGACY_PHONE_NUMBER_SENDER = 100;
   private static final int LEGACY_PHONE_NUMBER_TO = 101;
   private static final int LEGACY_PHONE_NUMBER_CC = 102;
   private static final int ATTACHMENT_NAME = 103;
   private static final int ATTACHMENT_DATA_BYTES = 104;
   private static final int ATTACHMENT_DATA_STREAM = 105;
   private static final int ATTACHMENT_DATA_TYPE = 107;
   private static final int ATTACHMENT_CHARSET = 108;
   private static final int EMAIL_ADDRESS_SENDER = 110;
   private static final int EMAIL_ADDRESS_TO = 111;
   private static final int EMAIL_ADDRESS_CC = 112;
   private static final int PHONE_NUMBER_SENDER = 113;
   private static final int PHONE_NUMBER_TO = 114;
   private static final int PHONE_NUMBER_CC = 115;

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (!(object instanceof MMSMessageModelImpl)) {
         return false;
      }

      MMSMessageModelImpl message = (MMSMessageModelImpl)object;
      MMSPayloadModelImpl payload = (MMSPayloadModelImpl)message.getPayload();
      SyncBuffer syncBuffer = new SyncBuffer(buffer, version, 0);
      syncBuffer.addInt(1, message.getFlags(), 4);
      syncBuffer.addLong(2, message.getFolderId());
      syncBuffer.addInt(3, message.getStatus(), 4);
      syncBuffer.addInt(4, message.getHttpErrorCode(), 4);
      syncBuffer.addInt(5, message.getMMSResponseCode(), 4);
      syncBuffer.addInt(6, message.getWAPIOExceptionError(), 4);
      syncBuffer.addInt(7, message.getWAPIOExceptionAdditionalData(), 4);
      putReadReports(syncBuffer, message);
      putDeliveryReports(syncBuffer, message);
      syncBuffer.addLong(50, payload.getCreationDate());
      if (message.getDeliveryDate() != 0) {
         syncBuffer.addLong(53, message.getDeliveryDate());
      }

      if (message.getReadDate() != 0) {
         syncBuffer.addLong(54, message.getReadDate());
      }

      Enumeration names = payload.attributeNames();
      if (names != null) {
         while (names.hasMoreElements()) {
            String attributeName = (String)names.nextElement();
            syncBuffer.addField(51, attributeName);
            syncBuffer.addField(52, payload.getAttribute(attributeName));
         }
      }

      putAddress(syncBuffer, 113, 110, payload.getSender());
      putAddresses(syncBuffer, 114, 111, payload.getRecipients());
      putAddresses(syncBuffer, 115, 112, payload.getCcRecipients());
      names = payload.attachmentNames();
      if (names != null) {
         while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            if (name != null) {
               MMSAttachment attachment = payload.getAttachment(name);
               syncBuffer.addField(103, name);
               syncBuffer.addInt(107, attachment.getType(), 4);
               syncBuffer.addField(108, attachment.getCharset());
               writeAttachmentBytes(syncBuffer, attachment.getData());
            }
         }
      }

      return true;
   }

   @Override
   public final SyncObject convert(DataBuffer dataBuffer, int version, int uid) {
      if (version >= 1 && version <= 1) {
         MMSMessageModelImpl message = new MMSMessageModelImpl();
         MMSPayloadModelImpl payload = new MMSPayloadModelImpl();
         String attachmentName = null;
         String attributeName = null;
         int attachmentType = -1;
         String attachmentCharset = null;
         String deliveryAddress = null;
         String readAddress = null;
         long deliveryDate = -1;
         long readDate = -1;
         int deliveryStatus = -1;
         int readStatus = -1;
         SyncBuffer syncBuffer = new SyncBuffer(dataBuffer, version, uid);

         try {
            while (!syncBuffer.isEmpty()) {
               int position = syncBuffer.getPosition();
               int fieldType = syncBuffer.getFieldType(true);
               if (fieldType != 255) {
                  switch (fieldType) {
                     case 1:
                        message.clearFlags();
                        message.setFlags(syncBuffer.getInt());
                        break;
                     case 2:
                        message.setFolderId(syncBuffer.getLong());
                        break;
                     case 3:
                        message.setStatus(syncBuffer.getInt());
                        break;
                     case 4:
                        message.setHttpErrorCode(syncBuffer.getInt());
                        break;
                     case 5:
                        message.setMMSResponseCode(syncBuffer.getInt());
                        break;
                     case 6:
                        message.setWAPIOExceptionError(syncBuffer.getInt());
                        break;
                     case 7:
                        message.setWAPIOExceptionAdditionalData(syncBuffer.getInt());
                        break;
                     case 50:
                        payload.setCreationDate(syncBuffer.getLong());
                        break;
                     case 51:
                        attributeName = syncBuffer.getString();
                        break;
                     case 52:
                        String strData = getString(syncBuffer);
                        if (attributeName != null) {
                           payload.setAttribute(attributeName, strData);
                           attributeName = null;
                        }
                        break;
                     case 53:
                        message.setDeliveryDate(syncBuffer.getLong());
                        break;
                     case 54:
                        message.setReadDate(syncBuffer.getLong());
                        break;
                     case 60:
                        deliveryAddress = syncBuffer.getString();
                        break;
                     case 61:
                        deliveryDate = syncBuffer.getLong();
                        break;
                     case 62:
                        deliveryStatus = syncBuffer.getInt();
                        break;
                     case 63:
                        readAddress = syncBuffer.getString();
                        break;
                     case 64:
                        readDate = syncBuffer.getLong();
                        break;
                     case 65:
                        readStatus = syncBuffer.getInt();
                        break;
                     case 100:
                        payload.setSender(readLegacyPhoneNumberModel(syncBuffer));
                        break;
                     case 101:
                        payload.addRecipient(readLegacyPhoneNumberModel(syncBuffer));
                        break;
                     case 102:
                        payload.addCcRecipient(readLegacyPhoneNumberModel(syncBuffer));
                        break;
                     case 103:
                        attachmentName = syncBuffer.getString();
                        break;
                     case 104:
                     case 105:
                        byte[] byteData = readAttachmentBytes(syncBuffer, fieldType == 105);
                        if (attachmentName != null) {
                           MMSAttachment attachment = new PersistedAttachmentImpl(attachmentName, attachmentType, byteData, attachmentCharset);
                           payload.addAttachment(attachment);
                           attachmentName = null;
                           attachmentType = -1;
                           attachmentCharset = null;
                        }
                        break;
                     case 107:
                        attachmentType = syncBuffer.getInt();
                        break;
                     case 108:
                        attachmentCharset = syncBuffer.getString();
                        break;
                     case 110:
                        payload.setSender(createEmailAddressModel(getString(syncBuffer)));
                        break;
                     case 111:
                        payload.addRecipient(createEmailAddressModel(getString(syncBuffer)));
                        break;
                     case 112:
                        payload.addCcRecipient(createEmailAddressModel(getString(syncBuffer)));
                        break;
                     case 113:
                        payload.setSender(createPhoneNumberModel(getString(syncBuffer)));
                        break;
                     case 114:
                        payload.addRecipient(createPhoneNumberModel(getString(syncBuffer)));
                        break;
                     case 115:
                        payload.addCcRecipient(createPhoneNumberModel(getString(syncBuffer)));
                  }
               }

               if (readAddress != null && readDate != -1 && readStatus != -1) {
                  message.addReadReport(new MMSStatusReportImpl(readAddress, readDate, readStatus));
                  readAddress = null;
                  readDate = -1;
                  readStatus = -1;
               }

               if (deliveryAddress != null && deliveryDate != -1 && deliveryStatus != -1) {
                  message.addDeliveryReport(new MMSStatusReportImpl(deliveryAddress, deliveryDate, deliveryStatus));
                  deliveryAddress = null;
                  deliveryDate = -1;
                  deliveryStatus = -1;
               }

               syncBuffer.setPosition(position);
               syncBuffer.skipField();
            }
         } finally {
            ;
         }

         message.setPayload(payload);
         return message;
      } else {
         return null;
      }
   }

   private static final byte[] readAttachmentBytes(SyncBuffer syncBuffer, boolean isStream) {
      if (isStream) {
         ByteArrayOutputStream oStream = new ByteArrayOutputStream();
         ConverterUtilities.readByteStream(syncBuffer.getDataBuffer(), true, oStream);
         return oStream.toByteArray();
      } else {
         return syncBuffer.getBytes();
      }
   }

   private static final void writeAttachmentBytes(SyncBuffer syncBuffer, byte[] data) {
      int len = data.length;
      if (len > 32767) {
         ConverterUtilities.writeByteStream(syncBuffer.getDataBuffer(), 105, new ByteArrayInputStream(data), len);
      } else {
         syncBuffer.addBytes(104, data);
      }
   }

   private static final String getString(SyncBuffer syncBuffer) {
      String str = syncBuffer.getString();
      return str == null ? "" : str;
   }

   private static final RIMModel readLegacyPhoneNumberModel(SyncBuffer syncBuffer) {
      ContextObject legacyContext = new ContextObject(19, 55);
      legacyContext.put(255, syncBuffer);
      return (RIMModel)_phoneNumberFactory.createInstance(legacyContext);
   }

   private static final RIMModel createPhoneNumberModel(String number) {
      _context.put(253, number);
      return (RIMModel)_phoneNumberFactory.createInstance(_context);
   }

   private static final RIMModel createEmailAddressModel(String address) {
      _context.put(253, address);
      return (RIMModel)_emailAddressFactory.createInstance(_context);
   }

   private static final void putAddresses(SyncBuffer syncBuffer, int phoneTag, int emailTag, Vector list) {
      if (list != null && list.size() > 0) {
         for (int idx = 0; idx < list.size(); idx++) {
            putAddress(syncBuffer, phoneTag, emailTag, (RIMModel)list.elementAt(idx));
         }
      }
   }

   private static final void putReadReports(SyncBuffer syncBuffer, MMSMessageModelImpl message) {
      int count;
      if ((count = message.getReadReportCount()) > 0) {
         for (int i = 0; i < count; i++) {
            MMSStatusReportImpl report = (MMSStatusReportImpl)message.getReadReport(i);
            syncBuffer.addField(63, report.getAddress());
            syncBuffer.addLong(64, report.getDate());
            syncBuffer.addInt(65, report.getStatus(), 4);
         }
      }
   }

   private static final void putDeliveryReports(SyncBuffer syncBuffer, MMSMessageModelImpl message) {
      int count;
      if ((count = message.getDeliveryReportCount()) > 0) {
         for (int i = 0; i < count; i++) {
            MMSStatusReportImpl report = (MMSStatusReportImpl)message.getDeliveryReport(i);
            syncBuffer.addField(60, report.getAddress());
            syncBuffer.addLong(61, report.getDate());
            syncBuffer.addInt(62, report.getStatus(), 4);
         }
      }
   }

   private static final void putAddress(SyncBuffer syncBuffer, int phoneTag, int emailTag, RIMModel model) {
      if (model != null) {
         int tag = MMSUtilities.isPhoneNumber(model) ? phoneTag : emailTag;
         syncBuffer.addField(tag, model.toString());
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _phoneNumberFactory = (Factory)ar.waitFor(3797587162219887872L);
      _emailAddressFactory = (Factory)ar.waitFor(-2985347935260258684L);
   }
}
