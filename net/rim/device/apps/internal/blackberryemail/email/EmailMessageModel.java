package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.addressbook.AddressReference;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.messaging.messagelist.DeleteConfirmationModel;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;

public interface EmailMessageModel extends PersistableRIMModel, ReadableList, WritableSet, DeleteConfirmationModel {
   int TX_COMPOSING = Integer.MAX_VALUE;
   int TX_RETRIEVING_KEY = 1073741823;
   int TX_COMPRESSING = 536870911;
   int TX_ENCRYPTING = 268435455;
   int TX_PENDING = 134217727;
   int TX_SENDING = 67108863;
   int TX_SENT = 33554431;
   int TX_MAILBOXED = 8388607;
   int TX_DELIVERED = 4194303;
   int TX_READ = 2097151;
   int TX_ERROR = 8191;
   int TX_GENERAL_FAILURE = 16383;
   int TX_ERROR_RESENDING = 32767;
   int RX_RECEIVING = 4095;
   int RX_RECEIVED = 2047;
   int RX_ERROR = 1;
   int OPENED = 1;
   int FILED = 2;
   int MOVED = 4;
   int SAVED_THEN_ORPHANED = 8;
   int SAVED = 16;
   int REPLY_ALLOWED = 32;
   int REQUEST_READ_ACK = 64;
   int MORE_AVAILABLE = 256;
   int MORE_REQUEST_SENT = 1024;
   int BODY_TRUNCATED = 4096;
   int IS_PIN_MESSAGE = 8192;
   int SIGNED_RECEIPT_SENT = 16384;
   int WAS_ENCRYPTED = 32768;
   int AUTO_MORE_REQUESTED = 65536;
   int PROCESSED_FOR_SMIME_CAPABILITIES = 131072;
   int DELETED = 262144;
   int RX_PAGE = 524288;
   int GLOBAL_SCRAMBLING_KEY_USED = 1048576;
   int REQUEST_DELIVERY_ACK = 2097152;
   int USER_MODIFIED_DSN = 4194304;
   int IS_IMPLUS_MESSAGE = 8388608;
   int WAS_SECURE = 16777216;
   int IS_UNFETCHED = 33554432;
   int IS_NEW = 67108864;

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
