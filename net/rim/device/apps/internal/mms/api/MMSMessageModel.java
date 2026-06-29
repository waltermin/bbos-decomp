package net.rim.device.apps.internal.mms.api;

import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.HotKeyProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;

public interface MMSMessageModel extends PersistableRIMModel, ActionProvider, VerbProvider, HotKeyProvider, CloneProvider, SyncObject {
   long MMS_VERB_REPOSITORY = 806618162841601920L;
   int TX_COMPOSING = Integer.MAX_VALUE;
   int TX_PENDING = 134217727;
   int TX_SENDING = 67108863;
   int TX_SENT = 33554431;
   int TX_DELETED_NOT_READ = 16777215;
   int TX_EXPIRED = 8388607;
   int TX_RETRIEVED = 4194303;
   int TX_READ = 2097151;
   int TX_REJECTED = 1048575;
   int TX_DEFERRED = 524287;
   int TX_FORWARDED = 262143;
   int TX_ERROR_COVERAGE = 131071;
   int TX_ERROR = 8191;
   int TX_GENERAL_FAILURE = 16383;
   int RX_RECEIVING = 4095;
   int RX_RECEIVED = 2047;
   int RX_SIZE_EXCEEDS = 1023;
   int RX_ERROR_COVERAGE = 511;
   int RX_ERROR = 1;
   long UPDATE_FOLDER_ID = 6760675856762529805L;
   long UPDATE_PAYLOAD = -8063528366659490440L;
   long UPDATE_STATUS = -3923698019885371449L;
   long ADD_DELIVERY_REPORT = -8071174053402202672L;
   long ADD_READ_REPORT = -1919303899965957599L;
   long WMA_FLAG = 3826502739478037178L;

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
