package net.rim.device.apps.internal.sms;

import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ColumnPaintProvider;
import net.rim.device.apps.api.framework.model.ColumnPainter;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.FolderProvider;
import net.rim.device.apps.api.framework.model.HotKeyProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.KeyUtilities;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.model.VisibilityControl;
import net.rim.device.apps.api.framework.registration.ModelViewListenerRegistry;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.MessageIcons;
import net.rim.device.apps.api.messaging.NonpersistedUtilityFolders;
import net.rim.device.apps.api.messaging.messagelist.MessageAttachment;
import net.rim.device.apps.api.messaging.messagelist.MessageListOptions;
import net.rim.device.apps.api.messaging.messagelist.MessagePartsProvider;
import net.rim.device.apps.api.messaging.util.MessagingUtil;
import net.rim.device.apps.api.ribbon.indicators.UnreadCountManager;
import net.rim.device.apps.api.search.Match;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.utility.framework.ModelScreen;
import net.rim.device.apps.api.utility.lowMemory.PurgeProvider;
import net.rim.device.apps.internal.sms.resources.SMSResources;
import net.rim.device.apps.internal.sms.ui.ViewerProvider;
import net.rim.device.internal.deviceoptions.SMSOptions;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public class SMSModel
   implements PersistableRIMModel,
   SyncObject,
   VerbProvider,
   FieldProvider,
   ColumnPaintProvider,
   KeyProvider,
   MatchProvider,
   ConversionProvider,
   HotKeyProvider,
   ActionProvider,
   FolderProvider,
   PurgeProvider,
   EncryptableProvider,
   ViewerProvider,
   MessagePartsProvider,
   VisibilityControl {
   private int _overallMessageStatus;
   private int[] _recipientStatus;
   protected int[] _recipientError;
   private int _messageFlags;
   public long _folderId;
   public SMSPayloadModel _payload;
   private boolean _subjectEllipsisRequired;
   private int _subjectStringOffset = -1;
   public static final int STATUS_TX_COMPOSING = Integer.MAX_VALUE;
   public static final int STATUS_TX_RETRIEVING_KEY = 1073741823;
   public static final int STATUS_TX_COMPRESSING = 536870911;
   public static final int STATUS_TX_ENCRYPTING = 268435455;
   public static final int STATUS_TX_PENDING = 134217727;
   public static final int STATUS_TX_SENDING = 67108863;
   public static final int STATUS_TX_SENT = 33554431;
   public static final int STATUS_TX_MAILBOXED = 8388607;
   public static final int STATUS_TX_DELIVERED = 4194303;
   public static final int STATUS_TX_READ = 2097151;
   public static final int STATUS_TX_ERROR = 8191;
   public static final int STATUS_TX_GENERAL_FAILURE = 16383;
   public static final int STATUS_RX_RECEIVING = 4095;
   public static final int STATUS_RX_RECEIVED = 2047;
   public static final int STATUS_RX_ERROR = 1;
   public static final int OFFSET_DATAGRAM_ID = 0;
   public static final int DATAGRAM_MASK = Integer.MAX_VALUE;
   public static final int USE_SMART_DIALING_MASK = Integer.MIN_VALUE;
   private static final int OFFSET_MESSAGE_STATUS = 1;
   protected static final int OFFSET_MESSAGE_ID = 1;
   public static final int INVALID_MESSAGE_ID = 0;
   protected static final int OFFSET_MESSAGE_TRANSMISSION_ERROR = 2;
   public static final int INVALID_MESSAGE_TRANSMISSION_ERROR = 0;
   protected static final int OFFSET_CAUSE_CODE = 3;
   private static final int OFFSET_PER_STATUS_ENTRY = 2;
   protected static final int OFFSET_PER_ERROR_ENTRY = 4;
   public static final int INVALID_CAUSE_CODE = -1;
   public static final int FLAG_OPENED = 1;
   public static final int FLAG_SAVED_THEN_ORPHANED = 8;
   public static final int FLAG_SAVED = 16;
   public static final int FLAG_BEGINNING_OF_THREAD = 32;
   public static final int FLAG_USE_SMART_DIALING = 64;
   public static final int FLAG_ERROR_STATUS_OCCURRED = 128;
   public static final int FLAG_NEW = 256;
   private static WeakReference _messageHeaderDataBufferWR = new WeakReference(null);
   private static final int INVALID_SUBJECT_OFFSET = -1;
   private static final int MAXIMUM_SUBJECT_LENGTH = 35;

   public void setStatus(int messageStatus, int messageID, int causeCode, int messageTransmissionError, int datagramID) {
      this._overallMessageStatus = messageStatus;
      if (datagramID != 0) {
         int total = this._recipientStatus.length;

         for (int i = 0; i < total; i += 2) {
            if (this.getDatagramID_ViaStartIndex(i) == datagramID) {
               this._recipientStatus[i + 1] = messageStatus;
               break;
            }
         }

         if (messageStatus == 8191) {
            this.setFlags(128);
            if (this._recipientError == null) {
               this._recipientError = new int[0];
            } else {
               int errorStartIndex = this.findRecipientErrorStartIndex(datagramID);
               if (errorStartIndex > -1) {
                  this.setSingleRecipientError(errorStartIndex, messageID, causeCode, messageTransmissionError);
                  return;
               }
            }

            Array.resize(this._recipientError, 4);
            int errorStartIndex = this._recipientError.length - 4;
            this._recipientError[errorStartIndex + 0] = datagramID;
            this.setSingleRecipientError(errorStartIndex, messageID, causeCode, messageTransmissionError);
         }
      }
   }

   @Override
   public Verb getVerbs(Object _1, Verb[] _2) {
      throw null;
   }

   @Override
   public int match(Object criteria) {
      if (!(criteria instanceof SearchCriterion)) {
         SMSPayloadModel var6 = this._payload;
         SearchCriterion[] crit = (SearchCriterion[])criteria;
         int n = crit.length;

         while (--n >= 0) {
            SearchCriterion c = crit[n];
            if (Match.performMatch(this, c) != 1 && Match.performMatch(var6, c) != 1) {
               return 0;
            }
         }

         return 1;
      } else {
         SearchCriterion crit = (SearchCriterion)criteria;
         boolean match;
         switch (crit.getType()) {
            case 8:
            case 15:
            case 17:
            case 18:
            case 20:
            case 21:
            case 23:
            case 27:
               return -1;
            case 9:
               match = this.inbound();
               break;
            case 10:
               match = !this.inbound();
               break;
            case 11:
               match = this.flagsSet(16);
               break;
            case 12:
            case 14:
            case 16:
            case 19:
            case 25:
            case 26:
            default:
               match = false;
               break;
            case 13:
               match = true;
               break;
            case 22:
               match = this.isDraft();
               break;
            case 24:
               match = (Integer)crit.getValue() == this.getUID();
               break;
            case 28:
               match = this.inbound() && !this.opened();
         }

         return match ? 1 : 0;
      }
   }

   @Override
   public int getUID() {
      return (int)this._payload._creationDate;
   }

   @Override
   public boolean perform(long actionId, Object context) {
      Object delegateUi = SMSUiRegistry.getRegistry().getCurrentUi();
      if (delegateUi != null && delegateUi instanceof ActionProvider) {
         ContextObject contObj = ContextObject.castOrCreate(context);
         ContextObject.put(contObj, 250, this);
         if (((ActionProvider)delegateUi).perform(actionId, contObj)) {
            return true;
         }
      }

      if (actionId != 4951292880494466830L) {
         if (actionId == 6099736323056465049L) {
            new SMSOpenVerb(this).invoke(context);
            return true;
         }

         if (actionId == -198247372487919817L) {
            this.performActionsWhenMessageIsReallyAboutToBeBlownAway();
            return true;
         }

         if (actionId == -6225946334564270161L) {
            this.changeStatus(1, 0, 0, 0, true, true, true, null);
            return true;
         }

         if (actionId == -3967872215949752466L) {
            this.delete(context, false);
            return true;
         }

         if (actionId == -8570780006855731756L) {
            this.changeStatus(16, 0, 0, 0, true, true, false, null);
            return true;
         }

         if (actionId == 5803508244060051872L) {
            this.changeStatus(1, 0, 0, 0, true, true, true, null);
            return true;
         }

         if (actionId == -8629311385729242560L) {
            this.changeStatus(0, 1, 0, 0, true, true, true, null);
            return true;
         }

         if (actionId == -5544992959212130441L) {
            return this.inbound() && !this.flagsSet(1);
         }

         if (actionId == 477896226347912237L) {
            return this.inbound() && this.flagsSet(1);
         }

         if (actionId == 6780594967363292755L) {
            this.delete(context, true);
            return true;
         }

         if (actionId == 635678369939227345L) {
            return !this.isDraft();
         }

         if (actionId == 278390328807340479L) {
            if (this.inbound() && !this.flagsSet(1)) {
               return true;
            }
         } else if (actionId == 3103370408204507200L) {
            if (this.flagsSet(16)) {
               return true;
            }
         } else if (actionId == 3456946836994320775L) {
            if (this.flagsSet(8)) {
               return true;
            }
         } else if (actionId == -4201671119995560115L) {
            int status = this.getOverallStatus();
            if (status == 8191 || status == 16383) {
               return true;
            }
         } else if (actionId == -1042102706756508802L) {
            if (!this.flagsSet(16)) {
               return true;
            }
         } else if (actionId == 5213547777258110094L) {
            if (this.isNew()) {
               this.clearFlags(256);
               PersistentObject.commit(this);
               this.notifyCollection();
               decrementCount(false, true);
               return true;
            }
         } else if (actionId == -6072303684925088654L) {
            return this.isNew();
         }

         return false;
      } else {
         if (context instanceof Folder) {
            Folder folder = (Folder)context;
            long folderId = folder.getLUID();
            if (this._folderId != folderId) {
               this._folderId = folderId;
            }
         }

         if (this.inbound() && !this.flagsSet(1)) {
            long addressCardUID = -1;
            Object obj = ContextObject.get(context, 252);
            if (obj instanceof AddressCardModel) {
               addressCardUID = ((AddressCardModel)obj).getUID();
            }

            incrementCount(true, this.flagsSet(256));
         }

         if (this.notYetSent()) {
            this.setStatus(16383, 0, 0, 0, 0);
         }

         if (this.isInSavedMessagesCache()) {
            NonpersistedUtilityFolders.addMessageToUtilityFolder(7175316403005034194L, this);
         }

         return true;
      }
   }

   @Override
   public boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 55) && ContextObject.getFlag(context, 19)) {
         PersistableRIMModel[] addresses = this._payload.getAddresses();
         int addressTotal = addresses.length;
         int transmissionError = 0;
         if (addressTotal > 0 && this._recipientError != null) {
            int errorStartIndex = this.findRecipientErrorStartIndex(this.getDatagramID(1));
            if (errorStartIndex > -1) {
               transmissionError = this._recipientError[errorStartIndex + 2];
            }
         }

         SyncBuffer syncBuffer = (SyncBuffer)target;
         DataBuffer _messageHeaderDataBuffer = WeakReferenceUtilities.getDataBuffer(_messageHeaderDataBufferWR, false);
         _messageHeaderDataBuffer.setPosition(0);
         _messageHeaderDataBuffer.writeBoolean(this.inbound());
         _messageHeaderDataBuffer.writeInt(this.getFlags());
         _messageHeaderDataBuffer.writeInt(this.getOverallStatus());
         _messageHeaderDataBuffer.writeInt(transmissionError);
         _messageHeaderDataBuffer.writeLong(this._payload._creationDate);
         _messageHeaderDataBuffer.writeLong(this._payload._transmissionDate);
         _messageHeaderDataBuffer.writeInt(this._payload.getData0());
         _messageHeaderDataBuffer.writeInt(this._payload.getData1());
         _messageHeaderDataBuffer.writeInt(this.getType());
         syncBuffer.addBytes(1, _messageHeaderDataBuffer.getArray());
         syncBuffer.addInts(9, this._recipientStatus);
         if (this._recipientError != null) {
            syncBuffer.addInts(10, this._recipientError);
         }

         syncBuffer.addInts(11, this._payload.getTONAndNPI());
         ContextObject contextObject = ContextObject.clone(context);

         for (int i = 0; i < addressTotal; i++) {
            RIMModel address = addresses[i];
            ConversionProvider addressConverter = (ConversionProvider)address;
            contextObject.putIntegerData(address instanceof EmailAddressModel ? 8 : 2);
            if (!addressConverter.convert(contextObject, syncBuffer)) {
               return false;
            }
         }

         RIMModel callbackAddress = this._payload.getCallbackAddress();
         if (callbackAddress instanceof ConversionProvider) {
            ConversionProvider addressConverter = (ConversionProvider)callbackAddress;
            contextObject.putIntegerData(6);
            if (!addressConverter.convert(contextObject, syncBuffer)) {
               return false;
            }
         }

         byte[] data = this._payload.getData();
         if (data != null) {
            syncBuffer.addBytes(4, data);
         }

         if (this._payload._userDataHeader != null) {
            syncBuffer.addBytes(5, this._payload._userDataHeader);
         }

         syncBuffer.addBytes(7, this._payload._byteFields);
         return true;
      } else {
         return false;
      }
   }

   public void removeAddresses() {
      this.resetStatus();
      this._payload.resetAddresses();
   }

   @Override
   public Object invokeHotkey(Object _1, int _2) {
      throw null;
   }

   public void copyAddresses(SMSModel sourceModel) {
      int newSize = (sourceModel._payload.getAddresses().length - this._payload.getAddresses().length) * 2;
      int oldSize = this._recipientStatus.length;
      this._payload.copyAddresses(sourceModel);
      Array.resize(this._recipientStatus, newSize);
      int i = oldSize;

      for (int j = 0; i < newSize; j += 2) {
         this.setUseSmartDialing_ViaStartIndex(i, sourceModel.useSmartDialing_ViaStartIndex(j));
         i += 2;
      }
   }

   public int getOverallStatus() {
      return isSuccessfullySent(this._overallMessageStatus) && this.flagsSet(128) ? 8191 : this._overallMessageStatus;
   }

   public boolean useSmartDialing(int index) {
      return this.useSmartDialing_ViaStartIndex(this.indexIntoStatusArray(index));
   }

   public void setDatagramID(int index, int datagramID) {
      int datagramIDOffset = this.indexIntoStatusArray(index) + 0;
      int oldDatagramID = this._recipientStatus[datagramIDOffset] & 2147483647;
      this._recipientStatus[datagramIDOffset] = this._recipientStatus[datagramIDOffset] & -2147483648;
      this._recipientStatus[datagramIDOffset] = this._recipientStatus[datagramIDOffset] | datagramID & 2147483647;
      if (oldDatagramID != 0 && this._recipientError != null) {
         int errorStartIndex = this.findRecipientErrorStartIndex(oldDatagramID);
         if (errorStartIndex > -1 && errorStartIndex + 4 <= this._recipientError.length) {
            for (int i = 0; i < 4; i++) {
               Arrays.removeAt(this._recipientError, errorStartIndex);
            }

            if (this._recipientError.length == 0) {
               this._recipientError = null;
            }
         }
      }
   }

   public int getStatus(int index) {
      return this.getStatus_ViaStartIndex(this.indexIntoStatusArray(index));
   }

   protected int getDatagramID(int index) {
      return this.getDatagramID_ViaStartIndex(this.indexIntoStatusArray(index));
   }

   protected int getStatus_ViaStartIndex(int startIndex) {
      return this._recipientStatus[startIndex + 1];
   }

   protected int getDatagramID_ViaStartIndex(int startIndex) {
      return this._recipientStatus[startIndex + 0] & 2147483647;
   }

   protected int findRecipientErrorStartIndex(int datagramID) {
      int total = this._recipientError.length;

      for (int j = 0; j < total; j += 4) {
         if (datagramID == this._recipientError[j + 0]) {
            return j;
         }
      }

      return -1;
   }

   protected int indexIntoStatusArray(int index) {
      int startIndex = index * 2;
      if (startIndex <= this._recipientStatus.length - 1 && startIndex >= 0) {
         return startIndex;
      }

      EventLogger.logEvent(-5553929614158418545L, "SMS Message Status Array out of bounds".getBytes(), 2);
      return this._recipientStatus.length - 2;
   }

   public void setFlags(int mask) {
      this._messageFlags |= mask;
   }

   public void clearFlags(int mask) {
      this._messageFlags &= ~mask;
   }

   public int getFlags() {
      return this._messageFlags;
   }

   public boolean flagsSet(int flags) {
      return (this._messageFlags & flags) != 0;
   }

   public void changeAddress(RIMModel oldAddress, PersistableRIMModel newAddress) {
      int index = this._payload.changeAddress(oldAddress, newAddress);
      this.setUseSmartDialing_ViaStartIndex(this.indexIntoStatusArray(index), this.flagsSet(64));
   }

   public void addAddress(PersistableRIMModel newAddress) {
      this.addAddress(newAddress, SMSPayloadModel.getTON(newAddress), 0);
   }

   public boolean isInSavedMessagesCache() {
      return this.flagsSet(16) && !this.flagsSet(8);
   }

   public boolean isDraft() {
      return this.getOverallStatus() == Integer.MAX_VALUE;
   }

   public boolean opened() {
      return this.flagsSet(1);
   }

   public void addAddress(PersistableRIMModel newAddress, int TON, int NPI) {
      this.addEmptyStatus();
      this._payload.addAddress(newAddress, TON, NPI);
   }

   protected void delete(Object context, boolean commitChanges) {
      if (SMSOptions.getMessageListUiId() != 0 && context != null && !((ContextObject)context).getFlag(37) && !((ContextObject)context).getFlag(52)) {
         MessageThread messageThread = Storage.getMessageThread(this._payload.getFirstAddress());
         int nextItem = KeyUtilities.mapKeyToIndex(messageThread, Storage.getLongKeyProviderAdaptor(), this._payload._creationDate);
         if (nextItem != -1) {
            int i;
            for (i = nextItem; i < messageThread.size(); i++) {
               SMSModel current = (SMSModel)messageThread.getAt(i);
               if (current != this && current.flagsSet(32)) {
                  i--;
                  break;
               }
            }

            if (i == messageThread.size()) {
               i--;
            }

            for (; i >= 0; i--) {
               Object element = messageThread.getAt(i);
               if (element instanceof SMSModel) {
                  SMSModel model = (SMSModel)element;
                  delete(model, context, commitChanges);
                  if (model.flagsSet(32)) {
                     return;
                  }
               }
            }

            return;
         }
      }

      delete(this, context, commitChanges);
   }

   protected void performActionsWhenMessageIsReallyAboutToBeBlownAway() {
      this.changeStatus(1, 0, 0, 0, false, true, true, null);
      NonpersistedUtilityFolders.removeMessageFromUtilityFolder(7175316403005034194L, this);
   }

   public void restoreStatusArray(int[] data) {
      this._recipientStatus = data;
   }

   public void changeStatus(
      int flagsToSet, int flagsToClear, int newStatus, int transmissionError, boolean commit, boolean notify, boolean uiInitiated, Object context
   ) {
      if ((flagsToSet & flagsToClear) != 0) {
         throw new IllegalArgumentException("Trying to set and clear the same bit");
      }

      int causeCode = -1;
      int messageID = 0;
      int datagramID = 0;
      if (context instanceof ContextObject) {
         messageID = ContextObject.getIntegerData(context, 0);
         Integer datagramIDObject = (Integer)ContextObject.get(context, -8210557334250400979L);
         if (datagramIDObject != null) {
            datagramID = datagramIDObject;
         }
      }

      if (messageID != 0) {
         causeCode = (messageID & 0xFF0000) >> 16;
         messageID &= 255;
      }

      if (SMSOptions.getMessageListUiId() != 0 && context != null && !((ContextObject)context).getFlag(37) && !((ContextObject)context).getFlag(52)) {
         MessageThread messageThread = Storage.getMessageThread(this._payload.getFirstAddress());
         int nextItem = KeyUtilities.mapKeyToIndex(messageThread, Storage.getLongKeyProviderAdaptor(), this._payload._creationDate);
         if (nextItem != -1) {
            int i;
            for (i = nextItem; i < messageThread.size(); i++) {
               SMSModel current = (SMSModel)messageThread.getAt(i);
               if (current != this && current.flagsSet(32)) {
                  i--;
                  break;
               }
            }

            if (i == messageThread.size()) {
               i--;
            }

            for (; i >= 0; i--) {
               Object element = messageThread.getAt(i);
               if (element instanceof SMSModel) {
                  SMSModel model = (SMSModel)element;
                  model.changeStatus(flagsToSet, flagsToClear, newStatus, messageID, causeCode, transmissionError, datagramID, commit, notify, uiInitiated);
                  if (model.flagsSet(32)) {
                     return;
                  }
               }
            }

            return;
         }
      }

      this.changeStatus(flagsToSet, flagsToClear, newStatus, messageID, causeCode, transmissionError, datagramID, commit, notify, uiInitiated);
   }

   void changeStatus(
      int flagsToSet,
      int flagsToClear,
      int newStatus,
      int messageID,
      int causeCode,
      int transmissionError,
      int datagramID,
      boolean commit,
      boolean notify,
      boolean uiInitiated
   ) {
      int currentlySetFlags = this.getFlags();
      if (flagsToSet != 0 && ((flagsToSet ^ currentlySetFlags) & flagsToSet) != 0
         || flagsToClear != 0 && ((flagsToClear ^ ~currentlySetFlags) & flagsToClear) != 0
         || newStatus != 0) {
         if (this.inbound()) {
            if ((flagsToSet & 1) != 0 && !this.flagsSet(1)) {
               decrementCount(true, this.isNew());
               this.clearFlags(256);
               SMSService.getInstance().cancelImmediateNotifications(this);
               if (this._folderId == -441701525336570016L && this._payload._segmentIDs != null) {
                  int[] ids = this._payload._segmentIDs;

                  for (int i = 0; i < ids.length; i++) {
                     SIMManager.getInstance().markSMSMessageAsRead(ids[i]);
                  }
               }
            } else if ((flagsToClear & 1) != 0 && this.flagsSet(1)) {
               boolean isNew = ((flagsToSet & 256) != 0 && !this.flagsSet(256)) | this.flagsSet(256);
               incrementCount(true, isNew);
            }
         }

         if (!this.flagsSet(16) && (flagsToSet & 16) != 0) {
            NonpersistedUtilityFolders.addMessageToUtilityFolder(7175316403005034194L, this);
            if (uiInitiated) {
               MessageListOptions options = MessageListOptions.getOptions();
               if (options.isKeepSavedMessagesDurationDefinedByItPolicy()) {
                  int durationFromItPolicy = options.getKeepSavedMessagesDuration();
                  StringBuffer sb = new StringBuffer();
                  sb.append(CommonResources.getString(9167));
                  sb.append(" ");
                  if (durationFromItPolicy == -1) {
                     sb.append(CommonResources.getString(9145));
                  } else {
                     sb.append(durationFromItPolicy);
                     sb.append(" ");
                     sb.append(CommonResources.getString(9144));
                  }

                  Status.show(sb.toString());
               } else {
                  Status.show(CommonResources.getString(5001));
               }
            }
         }

         if (this.flagsSet(16) && (flagsToClear & 16) != 0) {
            NonpersistedUtilityFolders.removeMessageFromUtilityFolder(7175316403005034194L, this);
         }

         if (!this.flagsSet(8) && (flagsToSet & 8) != 0) {
            NonpersistedUtilityFolders.removeMessageFromUtilityFolder(7175316403005034194L, this);
         }

         this.setFlags(flagsToSet);
         this.clearFlags(flagsToClear);
         if (newStatus != 0) {
            this.setStatus(newStatus, messageID, causeCode, transmissionError, datagramID);
         }

         if (commit) {
            PersistentObject.commit(this);
         }

         if (notify) {
            this.notifyCollection();
         }
      }
   }

   protected boolean isNew() {
      return this.flagsSet(256);
   }

   public boolean isAddressEmpty(Object _1) {
      throw null;
   }

   public void setBody(String text) {
      this._payload.setText(text);
      this._subjectStringOffset = -1;
      this.notifyCollection();
   }

   public int getOverallStatusIcon() {
      return this.getStatusIcon(this.getOverallStatus());
   }

   public int getRecipientStatusIcon(int index) {
      return this.getStatusIcon(this.getStatus_ViaStartIndex(this.indexIntoStatusArray(index)));
   }

   public void setData(byte[] data) {
      this._payload.setData(data);
      this.notifyCollection();
   }

   public boolean addSegment(int totalSegments, int segmentNumber, int segmentID, byte[] data) {
      boolean ret = this._payload.addSegment(totalSegments, segmentNumber, segmentID, data);
      if (ret && totalSegments != 1) {
         this.notifyCollection();
      }

      return ret;
   }

   public int addVoicemailMessageOverSMSVerbs(Verb[] verbs, int verbInsertionIndex, Object context) {
      int count = 0;
      ContextObject contextObject = ContextObject.castOrCreate(context);
      VerbFactory[] factories = VerbFactoryRepository.getVerbFactories(1091597397839321678L);
      if (factories != null && factories.length > 0) {
         for (int i = 0; i < factories.length; i++) {
            VerbFactory factory = factories[i];
            if (factory != null) {
               contextObject.put(-302973558008181267L, this);
               Verb[] voicemailVerbs = factory.getVerbs(contextObject);
               if (voicemailVerbs != null && voicemailVerbs.length > 0) {
                  Array.resize(verbs, verbs.length + voicemailVerbs.length);

                  for (int j = 0; j < voicemailVerbs.length; j++) {
                     if (voicemailVerbs[j] != null) {
                        verbs[verbInsertionIndex++] = voicemailVerbs[j];
                        count++;
                     }
                  }
               }
            }
         }
      }

      return count;
   }

   public void restoreErrorArray(int[] data) {
      this._recipientError = data;
   }

   protected int getType() {
      throw null;
   }

   public void removeAddress(RIMModel address) {
      int index = this._payload.removeAddress(address);
      int statusStartIndex = this.indexIntoStatusArray(index);
      if (this._recipientError != null) {
         int datagramID = this.getDatagramID_ViaStartIndex(statusStartIndex);
         int total = this._recipientError.length;

         for (int j = 0; j < total; j += 4) {
            if (datagramID == this._recipientError[j + 0]) {
               for (int i = 0; i < 4; i++) {
                  Arrays.removeAt(this._recipientError, j);
               }
               break;
            }
         }
      }

      for (int i = 0; i < 2; i++) {
         Arrays.removeAt(this._recipientStatus, statusStartIndex);
      }
   }

   protected void paintAddress(ColumnPainter painter, Object context) {
      PersistableRIMModel[] addresses = this._payload.getAddresses();
      int total = addresses.length;

      for (int i = 0; i < total; i++) {
         painter.drawModel(3, addresses[i], context);
      }
   }

   public void ungroupMessage() {
      if (ObjectGroup.isInGroup(this._payload)) {
         this._payload = (SMSPayloadModel)ObjectGroup.expandGroup(this._payload);
      }
   }

   public void groupMessage(boolean compress, boolean encrypt) {
      if (!this._payload.checkCrypt(compress, encrypt)) {
         this._payload.reCrypt(compress, encrypt);
      }

      ObjectGroup.createGroupIgnoreTooBig(this._payload);
   }

   public void groupMessage() {
      this.groupMessage(true, true);
   }

   public boolean isSuccessfullySent() {
      return isSuccessfullySent(this._overallMessageStatus) && !this.flagsSet(128);
   }

   public boolean notYetSent() {
      return notYetSent(this._overallMessageStatus);
   }

   @Override
   public boolean checkCrypt(boolean compress, boolean encrypt) {
      return this._payload.checkCrypt(compress, encrypt);
   }

   @Override
   public void purge(int purgeType) {
      LowMemoryManager.markAsRecoverable(this);
      this.perform(6780594967363292755L, null);
   }

   @Override
   public boolean canPurge(int purgeType) {
      if (purgeType != 0 && purgeType != 1) {
         return purgeType == 3 ? this.flagsSet(16) : false;
      } else {
         return !this.flagsSet(16);
      }
   }

   @Override
   public void setFolderId(long id) {
      this._folderId = id;
   }

   @Override
   public Object reCrypt(boolean compress, boolean encrypt) {
      this.ungroupMessage();
      this.groupMessage(compress, encrypt);
      return null;
   }

   @Override
   public void paint(ColumnPainter painter, Object context) {
      painter.setPriority(this._payload.getPaintPriority());
      painter.drawIcon(1, MessageIcons.getIcons(), this.getOverallStatusIcon());
      painter.drawTime(2, this._payload._creationDate);
      this.paintAddress(painter, context);
      if (!this.flagsSet(1) && this.inbound()) {
         painter.setEmphasis(true);
      }

      painter.drawText(4, this._payload.getTextSummary(), false);
   }

   @Override
   public long getFolderId() {
      return this._folderId;
   }

   @Override
   public int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      if (keyRequested != -7628247220259263034L && keyRequested != 92199951187614847L) {
         return 0;
      }

      keyArray[index] = this._payload._creationDate;
      return 1;
   }

   @Override
   public String getSubject() {
      String subjectText = this._payload.getText(true);
      if (subjectText == null) {
         return "";
      }

      int max = subjectText.length();
      int i = 0;

      while (i < max && subjectText.charAt(i) == '\n') {
         i++;
      }

      if (i > 0) {
         subjectText = subjectText.substring(i);
      }

      if (this._subjectStringOffset == -1) {
         this._subjectEllipsisRequired = false;
         int index = Math.min(35, subjectText.length());
         int returnIndex = subjectText.indexOf(10);
         if (returnIndex > -1) {
            index = Math.min(index, returnIndex);
         }

         if (index == 35) {
            subjectText = subjectText.substring(0, index);
            int periodIndex = subjectText.lastIndexOf(46);
            int exclamationIndex = subjectText.lastIndexOf(33);
            int questionIndex = subjectText.lastIndexOf(63);
            if (periodIndex > -1) {
               index = Math.min(index, periodIndex + 1);
            }

            if (exclamationIndex > -1) {
               index = Math.min(index, exclamationIndex + 1);
            }

            if (questionIndex > -1) {
               index = Math.min(index, questionIndex + 1);
            }

            if (index == 35) {
               int commaIndex = subjectText.lastIndexOf(44);
               if (commaIndex > -1) {
                  index = Math.min(index, commaIndex + 1);
               }
            }

            if (index == 35) {
               int spaceIndex = subjectText.lastIndexOf(32);
               if (spaceIndex > -1) {
                  index = Math.min(index, spaceIndex);
               }

               this._subjectEllipsisRequired = true;
            }
         }

         this._subjectStringOffset = index;
      }

      return this._subjectEllipsisRequired ? subjectText.substring(0, this._subjectStringOffset) + "..." : subjectText.substring(0, this._subjectStringOffset);
   }

   @Override
   public String getBody() {
      return this._payload.getText();
   }

   @Override
   public String getName() {
      return SMSResources.getString(391);
   }

   @Override
   public String getSender() {
      return this.inbound() ? this._payload.getFirstAddress().toString() : null;
   }

   @Override
   public boolean allowDescriptiveForwardHeader() {
      return true;
   }

   @Override
   public String[] getRecipients() {
      if (this.inbound()) {
         return null;
      }

      PersistableRIMModel[] addresses = this._payload.getAddresses();
      int total = addresses.length;
      String[] recipients = new String[total];

      for (int i = 0; i < total; i++) {
         recipients[i] = addresses[i].toString();
      }

      return recipients;
   }

   @Override
   public MessageAttachment[] getAttachments() {
      return null;
   }

   @Override
   public long getSentDate() {
      return this.notYetSent() ? 0 : this._payload.getDisplayDate();
   }

   @Override
   public int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      if (keyRequested == -4145532165335996154L) {
         Object address = this._payload.getFirstAddress();
         if (address instanceof KeyProvider) {
            return ((KeyProvider)address).getKeys(context, keyArray, index, keyRequested);
         }
      }

      return 0;
   }

   @Override
   public int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public int getOrder(Object context) {
      return 5650;
   }

   @Override
   public boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public byte getVisibilityFlags() {
      return this.visibilityForStatus(this.getOverallStatus());
   }

   @Override
   public boolean grabDataFromField(Field field, Object context) {
      return true;
   }

   @Override
   public void setRead(Object context) {
      this.changeStatus(1, 0, 0, 0, true, true, true, context);
   }

   @Override
   public Field getField(Object _1) {
      throw null;
   }

   @Override
   public boolean inbound() {
      return this._payload._inbound;
   }

   @Override
   public ModelScreen getViewer(Object _1) {
      throw null;
   }

   private static void decrementCount(boolean updateUnread, boolean updateNew) {
      UnreadCountManager.decrementUnreadCount(4, updateNew, updateUnread);
      UnreadCountManager.decrementUnreadCount(11, updateNew, updateUnread);
   }

   private void resetStatus() {
      this._recipientStatus = new int[0];
      this._recipientError = null;
   }

   private boolean useSmartDialing_ViaStartIndex(int startIndex) {
      return (this._recipientStatus[startIndex + 0] & -2147483648) != 0;
   }

   private void addEmptyStatus() {
      int startIndex = this._recipientStatus.length;
      Array.resize(this._recipientStatus, startIndex + 2);
      this.setUseSmartDialing_ViaStartIndex(startIndex, this.flagsSet(64));
   }

   private void setUseSmartDialing_ViaStartIndex(int startIndex, boolean useSmartDialing) {
      if (useSmartDialing) {
         this._recipientStatus[startIndex + 0] = this._recipientStatus[startIndex + 0] | -2147483648;
      } else {
         this._recipientStatus[startIndex + 0] = this._recipientStatus[startIndex + 0] & -2147483648;
      }
   }

   private int getStatusIcon(int status) {
      if (this.inbound()) {
         if (status == 1) {
            return 65539;
         } else {
            return this.opened() ? 65538 : 65537;
         }
      } else {
         switch (status) {
            case 8191:
            case 16383:
               return 65548;
            case 2097151:
               return 65547;
            case 4194303:
               return 65546;
            case 33554431:
               return 65545;
            case 67108863:
               return 65544;
            case 134217727:
               return 65543;
            case Integer.MAX_VALUE:
               return 65540;
            default:
               return 0;
         }
      }
   }

   public static final boolean notYetSent(int messageStatus) {
      switch (messageStatus) {
         case 4095:
         case 67108863:
         case 134217727:
         case 536870911:
         case 1073741823:
            return true;
         default:
            return false;
      }
   }

   protected static void delete(SMSModel model, Object context, boolean commitChanges) {
      if (ContextObject.getFlag(context, 52)) {
         if (ContextObject.getFlag(context, 22) || ContextObject.getFlag(context, 78)) {
            ContextObject c = ContextObject.clone(context);
            c.clearFlag(52);
            c.clearFlags(22, 78);
            if (!model.flagsSet(8)) {
               model.delete(c, commitChanges);
            }

            if (model.flagsSet(8)) {
               c.setFlag(52);
               model.delete(c, commitChanges);
            }
         } else if (model.flagsSet(8)) {
            model.performActionsWhenMessageIsReallyAboutToBeBlownAway();
            Storage.removeMessage(model);
         } else {
            model.changeStatus(0, 16, 0, 0, commitChanges, true, false, null);
         }
      } else if (model.flagsSet(16)) {
         Storage.moveMessage(model, model.getFolderId(), -4468584479793228955L);
         model.changeStatus(8, 0, 0, 0, 0, 0, 0, commitChanges, true, false);
      } else {
         model.performActionsWhenMessageIsReallyAboutToBeBlownAway();
         Storage.removeMessage(model);
      }
   }

   private byte visibilityForStatus(int status) {
      byte flags = 0;
      switch (status) {
         default:
            flags = (byte)(flags | 1);
         case 2097151:
         case 4194303:
         case 8388607:
         case 33554431:
            if (this._payload.getSegmentCount() > 1) {
               flags = (byte)(flags | 1);
            }

            return (byte)(flags | (this.isNew() ? 8 : 0));
      }
   }

   public static final boolean isSuccessfullySent(int messageStatus) {
      switch (messageStatus) {
         case 2097151:
         case 4194303:
         case 8388607:
         case 33554431:
            return true;
         default:
            return false;
      }
   }

   private void setSingleRecipientError(int startIndex, int messageID, int causeCode, int messageTransmissionError) {
      this._recipientError[startIndex + 2] = messageTransmissionError;
      this._recipientError[startIndex + 3] = causeCode;
      this._recipientError[startIndex + 1] = messageID;
   }

   public SMSModel(Object initialData) {
      this.resetStatus();
      if (ContextObject.getFlag(initialData, 117)) {
         this.setFlags(64);
      } else if (initialData instanceof SMSModel) {
         SMSModel model = (SMSModel)initialData;
         if (model.flagsSet(64)) {
            this.setFlags(64);
         }
      }

      this._payload = new SMSPayloadModel(initialData);

      for (int i = this._payload.getAddresses().length - 1; i > -1; i--) {
         this.addEmptyStatus();
      }

      if (ContextObject.getFlag(initialData, 38)) {
         this.setFlags(1);
         if (ContextObject.getFlag(initialData, 104)) {
            this.changeStatus(0, 0, 2047, 0, false, false, false, null);
         } else {
            this.changeStatus(256, 1, 2047, 0, false, false, false, null);
         }
      } else {
         this.changeStatus(0, 0, Integer.MAX_VALUE, 0, false, false, false, null);
      }
   }

   private void notifyCollection() {
      long folderId = this._folderId;
      Folder folder = Storage.getSMSFolder(folderId);
      if (folder != null) {
         CollectionListener collection = (CollectionListener)folder.getContainedItems();
         MessagingUtil.robustElementUpdated(collection, this);
      }

      if (ModelViewListenerRegistry.isViewerUp(0, this, this)) {
         ContextObject c = new ContextObject();
         c.setFlag(27);
         ModelViewListenerRegistry.notifyOfOpenedModelChange(this, this, c);
      }

      if (this.isInSavedMessagesCache()) {
         NonpersistedUtilityFolders.updateMessageInUtilityFolder(7175316403005034194L, this);
      }
   }

   private static void incrementCount(boolean updateUnread, boolean updateNew) {
      UnreadCountManager.incrementUnreadCount(4, updateNew, updateUnread);
      UnreadCountManager.incrementUnreadCount(11, updateNew, updateUnread);
   }
}
