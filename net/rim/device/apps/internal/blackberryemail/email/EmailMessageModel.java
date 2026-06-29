package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.addressbook.AddressReference;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.messaging.messagelist.DeleteConfirmationModel;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;

public interface EmailMessageModel extends PersistableRIMModel, ReadableList, WritableSet, DeleteConfirmationModel {
   int TX_COMPOSING;
   int TX_RETRIEVING_KEY;
   int TX_COMPRESSING;
   int TX_ENCRYPTING;
   int TX_PENDING;
   int TX_SENDING;
   int TX_SENT;
   int TX_MAILBOXED;
   int TX_DELIVERED;
   int TX_READ;
   int TX_ERROR;
   int TX_GENERAL_FAILURE;
   int TX_ERROR_RESENDING;
   int RX_RECEIVING;
   int RX_RECEIVED;
   int RX_ERROR;
   int OPENED;
   int FILED;
   int MOVED;
   int SAVED_THEN_ORPHANED;
   int SAVED;
   int REPLY_ALLOWED;
   int REQUEST_READ_ACK;
   int MORE_AVAILABLE;
   int MORE_REQUEST_SENT;
   int BODY_TRUNCATED;
   int IS_PIN_MESSAGE;
   int SIGNED_RECEIPT_SENT;
   int WAS_ENCRYPTED;
   int AUTO_MORE_REQUESTED;
   int PROCESSED_FOR_SMIME_CAPABILITIES;
   int DELETED;
   int RX_PAGE;
   int GLOBAL_SCRAMBLING_KEY_USED;
   int REQUEST_DELIVERY_ACK;
   int USER_MODIFIED_DSN;
   int IS_IMPLUS_MESSAGE;
   int WAS_SECURE;
   int IS_UNFETCHED;
   int IS_NEW;

   boolean changeStatus(int var1, int var2, int var3, int var4, boolean var5, boolean var6, boolean var7, boolean var8, Object var9);

   void setRecipientType(byte var1);

   byte getRecipientType();

   void setCMIMEReferenceIdentifier(int var1);

   int getCMIMEReferenceIdentifier();

   void setGMEReferenceIdentifier(int var1);

   int getGMEReferenceIdentifier();

   byte getNotificationLevel();

   void setNotificationLevel(byte var1);

   void setTimestamp(long var1);

   long getTimestamp();

   byte getPriority();

   void setPriority(byte var1);

   byte getSensitivity();

   void setSensitivity(byte var1);

   void setStatus(int var1, int var2);

   int getStatus();

   int getTransmissionError();

   void setTransmissionErrorMessage(String var1);

   String getTransmissionErrorMessage();

   void setFlags(int var1);

   void clearFlags(int var1);

   int getFlags();

   boolean flagsSet(int var1);

   void setInbound(boolean var1);

   boolean inbound();

   void setFolderId(long var1);

   long getFolderId();

   void setType(byte var1);

   byte getType();

   int getUID();

   void setCursorPosition(int var1);

   int getCursorPosition();

   void setPayload(EmailPayloadModel var1);

   EmailPayloadModel getPayload();

   void setAttachmentCount(int var1);

   int getAttachmentCount();

   String getSubject();

   String getBody();

   BodyModel getBodyModel();

   ServiceRecord getServiceRecordForMessage();

   AddressReference getSenderInfo();

   byte getEncoding();

   void setEncoding(byte var1);

   boolean scheduleResend(int var1);

   int autoResendAttempts();

   void setIsNNE(boolean var1);

   boolean isNNE();

   boolean isNew();
}
