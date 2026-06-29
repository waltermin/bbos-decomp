package net.rim.device.apps.internal.mms.api;

import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.HotKeyProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;

public interface MMSMessageModel extends PersistableRIMModel, ActionProvider, VerbProvider, HotKeyProvider, CloneProvider, SyncObject {
   long MMS_VERB_REPOSITORY;
   int TX_COMPOSING;
   int TX_PENDING;
   int TX_SENDING;
   int TX_SENT;
   int TX_DELETED_NOT_READ;
   int TX_EXPIRED;
   int TX_RETRIEVED;
   int TX_READ;
   int TX_REJECTED;
   int TX_DEFERRED;
   int TX_FORWARDED;
   int TX_ERROR_COVERAGE;
   int TX_ERROR;
   int TX_GENERAL_FAILURE;
   int RX_RECEIVING;
   int RX_RECEIVED;
   int RX_SIZE_EXCEEDS;
   int RX_ERROR_COVERAGE;
   int RX_ERROR;
   long UPDATE_FOLDER_ID;
   long UPDATE_PAYLOAD;
   long UPDATE_STATUS;
   long ADD_DELIVERY_REPORT;
   long ADD_READ_REPORT;
   long WMA_FLAG;

   @Override
   int getUID();

   int getStatus();

   int getDeliveryReportCount();

   int getReadReportCount();

   MMSStatusReport getReadReport(int var1);

   MMSStatusReport getDeliveryReport(int var1);

   long getDeliveryDate();

   long getReadDate();

   int getHttpErrorCode();

   int getMMSResponseCode();

   int getWAPIOExceptionError();

   int getWAPIOExceptionAdditionalData();

   boolean isSmartDialed();

   boolean isInbound();

   boolean isOpened();

   boolean isUnopened();

   boolean isSaved();

   boolean isSavedThenOrphaned();

   boolean isSuccessfullySent();

   boolean isForwardLocked();

   long getFolderId();

   MMSPayloadModel getPayload();

   AttachmentDataProvider getAttachmentDataProvider();

   boolean isNew();
}
