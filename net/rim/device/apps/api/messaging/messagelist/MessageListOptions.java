package net.rim.device.apps.api.messaging.messagelist;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.synchronization.SyncItem;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.api.messaging.FolderMerge;
import net.rim.device.apps.api.messaging.MessageEntryPoint;
import net.rim.device.apps.api.options.OptionsBase;
import net.rim.device.apps.api.ribbon.indicators.UnreadCount;
import net.rim.device.apps.api.ribbon.indicators.UnreadCountManager;
import net.rim.device.apps.api.ribbon.indicators.UnreadCountManager$CountOptions;
import net.rim.device.apps.api.utility.columninfo.ColumnInformation;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.vm.PersistentInteger;
import net.rim.vm.WeakReference;

public final class MessageListOptions extends OptionsBase implements UnreadCountManager$CountOptions {
   private MessageListOptions$PersistedMessageListOptions _persistedMessageListOptions;
   private static final long MESSAGE_LIST_OPTIONS_SYNC_ITEM = -7789810865029204428L;
   private static final long PERSISTED_MESSAGE_LIST_OPTIONS = 7237989745528696983L;
   public static final long GUID_OPTIONS_CHANGED = 4609271590317602928L;
   private static final int DEFAULT_GAP = 1;
   private static final int TIME_COLUMN_GAP = 2;
   private static final int ADDRESS_COLUMN_GAP = 2;
   private static final int DEFAULT_PRIORITY_WIDTH = 2;
   private static final int DEFAULT_STATUS_WIDTH = 11;
   public static final int INVALID = 0;
   public static final int DISPLAY_TIME = 1;
   public static final int DISPLAY_NAME = 2;
   public static final int CONFIRM_DELETE = 4;
   public static final int GLOBAL_DELETE_BOTH = 8;
   public static final int HIDE_FILED = 16;
   public static final int PIN_LEVEL_1 = 32;
   public static final int AUTO_MORE = 64;
   public static final int SPELL_CHECK_BEFORE_SEND = 128;
   public static final int ALLOW_READ_CONFIRM = 256;
   public static final int HIDE_SENT = 512;
   public static final int DISPLAY_NEW_MESSAGE_INDICATOR = 1024;
   public static final int HIGHSPEED_NETWORK_ONLY_FOR_AUTO_DOWNLOAD_ATTACHMENT = 2048;
   public static final int CONFIRM_MARK_PRIOR_OPENED = 4096;
   public static final int DELETE_HANDHELD_ONLY = 0;
   public static final int DELETE_HANDHELD_AND_DESKTOP = 1;
   public static final int DELETE_PROMPT = 2;
   public static final int DELETE_ON_NOT_SPECIFIED = -1;
   public static final short[] KEEP_MESSAGES_DURATION_CHOICES = new short[]{15, 30, 60, 90, 120, 150, 180, -1, 5, -12284, 26414, 26217, 10, 0, 1, -12278};
   private static final int DEFAULT_KEEP_MESSAGES_DURATION_INDEX = 1;
   private static final int FOREVER_KEEP_MESSAGES_DURATION_INDEX = KEEP_MESSAGES_DURATION_CHOICES.length - 1;
   private static short KEEP_MESSAGE_DURATION_CHOICE_VALUE_NOT_FOUND = -32768;
   public static final int MESSAGE_LIST_LINE_MODE_AUTO = 0;
   public static final int MESSAGE_LIST_LINE_MODE_ONE = 1;
   public static final int MESSAGE_LIST_LINE_MODE_TWO = 2;
   public static final int SMS_EMAIL_INBOX_THEME_CONTROLLED = 0;
   public static final int SMS_EMAIL_INBOX_COMBINED = 1;
   public static final int SMS_EMAIL_INBOX_SEPERATE = 2;
   public static final int DEFAULT_SMS_EMAIL_INBOX_INDEX = 0;
   public static final short AUTO_ATTACHMENT_DOWNLOAD_OFF = 0;
   public static final short AUTO_ATTACHMENT_DOWNLOAD_ON = 1;
   public static final short AUTO_ATTACHMENT_DOWNLOAD_HOME_NETWORK = 2;
   private static final int AUTO_MORE_TRIGGER = 600;
   public static final byte DSN_NOT_SPECIFIED = 0;
   public static final byte DSN_SPECIFIED = 1;
   public static final byte DSN_REQUEST_DELIVERY_RECEIPT = 2;
   public static final byte DSN_REQUEST_READ_RECEIPT = 4;
   public static final byte DSN_SEND_READ_RECEIPTS = 8;
   public static final byte DSN_SEND_READ_RECEIPTS_PROMPT = 16;
   public static final short LIST_SEPARATOR_NONE = 0;
   public static final short LIST_SEPARATOR_STRIPES = 1;
   public static final short LIST_SEPARATOR_LINES = 2;
   private static MessageListOptions _options;
   private static ServiceKey _serviceKey = new ServiceKey();
   private static WeakReference _sbWR = new WeakReference(null);
   public static boolean _addFromAddressBookEnabled = false;
   private static final long DISABLE_MESSAGE_LIST_ELLIPSES = 1678474675069446608L;

   public final boolean getFlag(int flag) {
      return (this._persistedMessageListOptions._flags & flag) != 0;
   }

   public final boolean setFlag(int flag, boolean on) {
      boolean flagChanged = false;
      if (on) {
         if ((this._persistedMessageListOptions._flags & flag) == 0) {
            this._persistedMessageListOptions._flags |= flag;
            return true;
         }
      } else if ((this._persistedMessageListOptions._flags & flag) != 0) {
         this._persistedMessageListOptions._flags &= ~flag;
         flagChanged = true;
      }

      return flagChanged;
   }

   public final Enumeration getServiceRecordKeys() {
      return this._persistedMessageListOptions._serviceSettings.keys();
   }

   public final int getDeleteOnLocation(String name, String uid, int defaultLocation) {
      if (name == null) {
         return this.getFlag(8) ? 1 : 0;
      }

      MessageListOptions$CMIMEServiceSetting serviceSetting = this.getCMIMEServiceSetting(name, uid);
      int result;
      if (serviceSetting != null && serviceSetting._deleteOnLocation != -1) {
         result = serviceSetting._deleteOnLocation;
      } else {
         result = defaultLocation;
         if (result == -1) {
            result = this.getFlag(8) ? 1 : 0;
         }
      }

      return result;
   }

   public final void setDeleteOnLocation(String name, String uid, int deleteOnLocation) {
      synchronized (_serviceKey) {
         _serviceKey.initialize(name, uid);
         MessageListOptions$CMIMEServiceSetting serviceSetting = (MessageListOptions$CMIMEServiceSetting)this._persistedMessageListOptions
            ._serviceSettings
            .get(_serviceKey);
         if (serviceSetting == null || serviceSetting._deleteOnLocation != deleteOnLocation) {
            if (serviceSetting == null) {
               this.logDeleteOnSetForNewService(name, uid, deleteOnLocation);
               serviceSetting = new MessageListOptions$CMIMEServiceSetting();
            } else {
               this.logDeleteOnChanged(name, uid, deleteOnLocation, _serviceKey, serviceSetting);
            }

            serviceSetting._deleteOnLocation = deleteOnLocation;
            this._persistedMessageListOptions._serviceSettings.put(new ServiceKey(name, uid), serviceSetting);
         }
      }
   }

   public final void removeDeleteOnLocation(String name, String uid) {
      synchronized (_serviceKey) {
         _serviceKey.initialize(name, uid);
         MessageListOptions$CMIMEServiceSetting serviceSetting = (MessageListOptions$CMIMEServiceSetting)this._persistedMessageListOptions
            ._serviceSettings
            .get(_serviceKey);
         if (serviceSetting != null) {
            if (serviceSetting._dsnSettings == 0) {
               this._persistedMessageListOptions._serviceSettings.remove(_serviceKey);
            } else {
               serviceSetting._deleteOnLocation = -1;
            }

            this.commit();
         }
      }
   }

   public final byte getDSNSettings(String name, String uid) {
      if (name == null) {
         return 0;
      }

      MessageListOptions$CMIMEServiceSetting serviceSetting = this.getCMIMEServiceSetting(name, uid);
      return serviceSetting == null ? 0 : serviceSetting._dsnSettings;
   }

   public final void setDSNSettings(String name, String uid, byte dsnSettings) {
      synchronized (_serviceKey) {
         _serviceKey.initialize(name, uid);
         MessageListOptions$CMIMEServiceSetting serviceSetting = (MessageListOptions$CMIMEServiceSetting)this._persistedMessageListOptions
            ._serviceSettings
            .get(_serviceKey);
         dsnSettings = (byte)(dsnSettings | 1);
         if (serviceSetting == null || serviceSetting._dsnSettings != dsnSettings) {
            if (serviceSetting == null) {
               serviceSetting = new MessageListOptions$CMIMEServiceSetting();
            }

            serviceSetting._dsnSettings = dsnSettings;
            this._persistedMessageListOptions._serviceSettings.put(new ServiceKey(name, uid), serviceSetting);
         }
      }
   }

   public final void removeDSNSettings(String name, String uid) {
      synchronized (_serviceKey) {
         _serviceKey.initialize(name, uid);
         MessageListOptions$CMIMEServiceSetting serviceSetting = (MessageListOptions$CMIMEServiceSetting)this._persistedMessageListOptions
            ._serviceSettings
            .get(_serviceKey);
         if (serviceSetting != null) {
            if (serviceSetting._deleteOnLocation == -1) {
               this._persistedMessageListOptions._serviceSettings.remove(_serviceKey);
            } else {
               serviceSetting._dsnSettings = 0;
            }

            this.commit();
         }
      }
   }

   public final short getKeepMessagesDuration() {
      short currentValue = this._persistedMessageListOptions._keepMessagesDuration;
      short itPolicyValue = this.getMaximumMessageDurationFromITPolicy();
      boolean itPolicyValueFound = itPolicyValue != KEEP_MESSAGE_DURATION_CHOICE_VALUE_NOT_FOUND;
      boolean itPolicyDurationIsForever = itPolicyValue == KEEP_MESSAGES_DURATION_CHOICES[FOREVER_KEEP_MESSAGES_DURATION_INDEX];
      return itPolicyValueFound && !itPolicyDurationIsForever && currentValue > itPolicyValue
         ? itPolicyValue
         : this._persistedMessageListOptions._keepMessagesDuration;
   }

   public final boolean setKeepMessagesDuration(short keepMessagesDuration) {
      short[] values = this.getKeepMessagesDurationChoices();

      for (int i = values.length - 1; i >= 0; i--) {
         if (keepMessagesDuration == values[i]) {
            this._persistedMessageListOptions._keepMessagesDuration = keepMessagesDuration;
            return true;
         }
      }

      return false;
   }

   public final short getKeepSavedMessagesDuration() {
      return (short)ITPolicy.getInteger(23, 5, KEEP_MESSAGE_DURATION_CHOICE_VALUE_NOT_FOUND);
   }

   public final boolean isKeepSavedMessagesDurationDefinedByItPolicy() {
      int badValue = Integer.MIN_VALUE;
      return badValue != ITPolicy.getInteger(23, 5, badValue);
   }

   public final Collection getMessageCollection() {
      return FolderMerge.getMergeCollection(this.getFlag(16) ? 2993144521330132876L : -5581791943352753293L);
   }

   public final Collection getMessageCollection(long emailHierarchyLuid) {
      return FolderMerge.getMergeCollection(FolderMerge.getMergeCollectionId(emailHierarchyLuid, this.getFlag(16) ? 734877078 : -271343505));
   }

   public final void applyOptions(MessageEntryPoint entry) {
      if (entry != null) {
         if (MessageEntryPoint.findEntry("mainview").equals(entry)) {
            boolean hideFiled = this.getFlag(16);
            UnreadCountManager.setUnreadCountVisible(2, hideFiled);
            UnreadCountManager.setUnreadCountVisible(1, !hideFiled);
            entry.set(12, hideFiled ? UnreadCountManager.getUnreadCountObject(2) : UnreadCountManager.getUnreadCountObject(1));
         }

         this.displayNewMessageIcon(entry);
      }
   }

   public final int getAutoMoreTrigger() {
      return 600;
   }

   public final void updateColumnInfo(ColumnInformation columnInfo) {
      this.updateColumnInfo(columnInfo, Font.getDefault());
   }

   public final void updateColumnInfo(ColumnInformation columnInfo, Font font) {
      if (font == null) {
         font = Font.getDefault();
      }

      DateFormat shortTimeFormat = DateFormat.getInstance(7);
      int maxDigitWidth = 0;

      for (int lv = 0; lv <= 9; lv++) {
         maxDigitWidth = Math.max(maxDigitWidth, font.getAdvance((char)(48 + lv)));
      }

      long t_12_22pm = DateFormat.alignToHourMinute(172800000, 12, 22);
      StringBuffer sb = WeakReferenceUtilities.getStringBuffer(_sbWR);
      sb.setLength(0);
      shortTimeFormat.formatLocal(sb, t_12_22pm);
      int numDigits = 0;

      for (int lv = sb.length() - 1; lv > 0; lv--) {
         char ch = sb.charAt(lv);
         if (Character.isDigit(ch)) {
            sb.deleteCharAt(lv);
            numDigits++;
         }
      }

      int width1 = font.getBounds(sb, 0, sb.length()) + numDigits * maxDigitWidth;
      long t_12_22am = DateFormat.alignToHourMinute(172800000, 0, 22);
      sb.setLength(0);
      shortTimeFormat.formatLocal(sb, t_12_22am);
      numDigits = 0;

      for (int lv = sb.length() - 1; lv > 0; lv--) {
         char ch = sb.charAt(lv);
         if (Character.isDigit(ch)) {
            sb.deleteCharAt(lv);
            numDigits++;
         }
      }

      int width2 = font.getBounds(sb, 0, sb.length()) + numDigits * maxDigitWidth;
      int timePaintWidth = Math.max(width1, width2);
      int addressPaintWidth = this.getDefaultAddressWidth();
      if (!this.getFlag(1)) {
         addressPaintWidth += timePaintWidth >> 2;
         timePaintWidth = 0;
      }

      if (!this.getFlag(2)) {
         addressPaintWidth = 0;
      }

      int mode = this.getMessageListLineMode();
      if (addressPaintWidth == 0) {
         mode = 1;
      } else if (mode == 0) {
         Font boldFont = font.derive(1);
         int minimumWidth = boldFont.getBounds("BlackBerry");
         if (addressPaintWidth < minimumWidth) {
            mode = 2;
         } else {
            mode = 1;
         }
      }

      if (mode == 2) {
         columnInfo.mapColumn(0, 0);
         columnInfo.mapColumn(1, 1);
         columnInfo.mapColumn(2, 3);
         columnInfo.mapColumn(3, 4);
         columnInfo.mapColumn(4, 2);
      } else {
         columnInfo.mapColumn(0, 0);
         columnInfo.mapColumn(1, 1);
         columnInfo.mapColumn(2, 2);
         columnInfo.mapColumn(3, 3);
         columnInfo.mapColumn(4, 4);
      }

      columnInfo.resetColumnWidthsGapsAndOffsets();
      columnInfo.setColumnWidth(0, 2);
      columnInfo.setColumnWidthGap(1, Math.max(11, font.getHeight()), 1);
      int screenWidth = Display.getWidth();
      if (mode == 2) {
         int topLineMajorColumnIndex = 4;
         int bottomLineMajorColumnIndex = 3;
         int offset = columnInfo.getColumnOffset(topLineMajorColumnIndex);
         int topLineMajorColumnGap;
         int topLineMajorColumnWidth;
         if (timePaintWidth <= 0) {
            topLineMajorColumnWidth = screenWidth - offset;
            topLineMajorColumnGap = offset;
         } else {
            topLineMajorColumnWidth = screenWidth - offset - timePaintWidth - 1;
            topLineMajorColumnGap = 1;
         }

         columnInfo.setColumnWidthGap(topLineMajorColumnIndex, topLineMajorColumnWidth, topLineMajorColumnGap);
         columnInfo.setColumnWidthGap(2, timePaintWidth, offset);
         columnInfo.setColumnWidthGap(bottomLineMajorColumnIndex, screenWidth - offset, 0);
      } else {
         columnInfo.setColumnWidthGap(2, timePaintWidth, 2);
         columnInfo.setColumnWidthGap(3, addressPaintWidth, 2);
         int offset = columnInfo.getColumnOffset(4);
         columnInfo.setColumnWidthGap(4, screenWidth - offset, 0);
      }
   }

   public final void toggleDisableMessageListEllipses() {
      int id = PersistentInteger.getId(1678474675069446608L, 0);
      PersistentInteger.set(id, PersistentInteger.get(id) == 0 ? 1 : 0);
   }

   public final boolean getDisableMessageListEllipses() {
      int id = PersistentInteger.getId(1678474675069446608L, 0);
      return PersistentInteger.get(id) != 0;
   }

   public final void setListSeparatorAppearance(short mode) {
      switch (mode) {
         case -1:
            throw new IllegalArgumentException();
         case 0:
         case 1:
         case 2:
         default:
            this._persistedMessageListOptions._listSeparatorAppearance = mode;
      }
   }

   public final short getListSeparatorAppearance() {
      return this._persistedMessageListOptions._listSeparatorAppearance;
   }

   public final void setSMSEmailInbox(short mode) {
      switch (mode) {
         case -1:
            throw new IllegalArgumentException();
         case 0:
         case 1:
         case 2:
         default:
            this._persistedMessageListOptions._SMSEmailInbox = mode;
      }
   }

   public final void setAutoDownloadAttachments(short value) {
      switch (value) {
         case 0:
         case 1:
         case 2:
         default:
            this._persistedMessageListOptions._autoDownloadAttachments = value;
         case -1:
      }
   }

   public final short getAutoDownloadAttachments() {
      if (!isAutoAttachmentDownloadEnabled()) {
         return 0;
      }

      switch (this._persistedMessageListOptions._autoDownloadAttachments) {
         case -1:
            return getAutoDownloadAttachmentsDefault();
         case 0:
         case 1:
         case 2:
         default:
            return this._persistedMessageListOptions._autoDownloadAttachments;
      }
   }

   public final boolean getHighSpeedNetworkOnlyForAutoDownloadAttachment() {
      return this.getFlag(2048);
   }

   public final void setHighSpeedNetworkOnlyForAutoDownloadAttachment(boolean value) {
      this.setFlag(2048, value);
   }

   public final boolean getConfirmMarkPriorOpened() {
      return this.getFlag(4096);
   }

   public final void setConfirmMarkPriorOpened(boolean confirmMarkPriorOpened) {
      this.setFlag(4096, confirmMarkPriorOpened);
   }

   public final short[] getKeepMessagesDurationChoices() {
      short itPolicyMax = this.getMaximumMessageDurationFromITPolicy();
      if (itPolicyMax <= 0) {
         itPolicyMax = KEEP_MESSAGES_DURATION_CHOICES[FOREVER_KEEP_MESSAGES_DURATION_INDEX];
      }

      boolean itPolicyValueFound = itPolicyMax != KEEP_MESSAGE_DURATION_CHOICE_VALUE_NOT_FOUND;
      boolean itPolicyDurationIsForever = itPolicyMax == KEEP_MESSAGES_DURATION_CHOICES[FOREVER_KEEP_MESSAGES_DURATION_INDEX];
      short[] durations = null;
      if (!itPolicyDurationIsForever && itPolicyValueFound) {
         durations = new short[0];
         short currentValue = 0;
         boolean itPolicyMaxAdded = false;

         for (int i = 0; i < KEEP_MESSAGES_DURATION_CHOICES.length - 1; i++) {
            currentValue = KEEP_MESSAGES_DURATION_CHOICES[i];
            if (currentValue < itPolicyMax) {
               Arrays.add(durations, currentValue);
            } else {
               if (currentValue != itPolicyMax) {
                  if (!itPolicyMaxAdded) {
                     Arrays.add(durations, itPolicyMax);
                  }
                  break;
               }

               Arrays.add(durations, currentValue);
               itPolicyMaxAdded = true;
            }
         }

         return durations;
      } else {
         return KEEP_MESSAGES_DURATION_CHOICES;
      }
   }

   public final int getMessageListLineMode() {
      return this._persistedMessageListOptions._messageListLineMode;
   }

   public final void setMessageListLineMode(int mode) {
      switch (mode) {
         case -1:
            throw new IllegalArgumentException();
         case 0:
         case 1:
         case 2:
         default:
            this._persistedMessageListOptions._messageListLineMode = mode;
      }
   }

   public final short getSMSEmailInbox() {
      return ThemeManager.getActiveTheme().getOption("CombinedInboxToggleNotAllowed") != null ? 0 : this._persistedMessageListOptions._SMSEmailInbox;
   }

   @Override
   public final boolean getDisplayNewMessageIndicatorDefaultValue() {
      return true;
   }

   @Override
   public final boolean getDisplayNewMessageIndicator() {
      return this.getFlag(1024);
   }

   @Override
   public final void setDisplayNewMessageIndicator(boolean value) {
      this.setFlag(1024, value);
   }

   @Override
   public final void setDisplayMessageCount(short index) {
      switch (index) {
         case -1:
            this._persistedMessageListOptions._displayMessageCount = 1;
            return;
         case 0:
         case 1:
         default:
            this._persistedMessageListOptions._displayMessageCount = index;
      }
   }

   @Override
   public final short getDisplayMessageCount() {
      switch (this._persistedMessageListOptions._displayMessageCount) {
         case -1:
            return 1;
         case 0:
         case 1:
         default:
            return this._persistedMessageListOptions._displayMessageCount;
      }
   }

   private final void logDeleteOnSetForNewService(String name, String uid, int deleteOnLocation) {
      StringBuffer sb = new StringBuffer();
      sb.append("Delete on being set for a new service (");
      sb.append("name:").append(name).append(",");
      sb.append("uid:").append(uid).append(",");
      sb.append("value:").append(deleteOnLocation).append(").");
      EventLogger.logEvent(-7509200465648525729L, sb.toString().getBytes(), 0);
   }

   private final void logDeleteOnChanged(
      String name, String uid, int deleteOnLocation, ServiceKey serviceKey, MessageListOptions$CMIMEServiceSetting oldSetting
   ) {
      StringBuffer sb = new StringBuffer();
      sb.append("Delete on being set for an existing service (");
      sb.append("newName:").append(name).append(", oldName:").append(serviceKey.getName()).append(": ");
      sb.append("newUID:").append(uid).append(", oldUID:").append(serviceKey.getUid()).append("; ");
      sb.append("newValue:").append(deleteOnLocation).append(", oldValue:").append(oldSetting._deleteOnLocation).append(").");
      EventLogger.logEvent(-7509200465648525729L, sb.toString().getBytes(), 0);
   }

   @Override
   public final void commit() {
      super.commit();
      RIMGlobalMessagePoster.postGlobalEvent(4609271590317602928L);
   }

   private final int getDefaultAddressWidth() {
      int screenWidth = Display.getWidth();
      return 54 * screenWidth / 160;
   }

   private static final boolean getHighSpeedNetworkOnlyDefault() {
      if (isAutoAttachmentDownloadEnabled()) {
         byte[] data = Branding.getData(24578);
         if (data == null) {
            return true;
         }

         switch (data[0]) {
            case -1:
               return true;
            case 0:
            default:
               return false;
            case 1:
               return true;
         }
      } else {
         return false;
      }
   }

   private final short getMaximumMessageDurationFromITPolicy() {
      return (short)ITPolicy.getInteger(23, 4, KEEP_MESSAGE_DURATION_CHOICE_VALUE_NOT_FOUND);
   }

   private static final short getAutoDownloadAttachmentsDefault() {
      if (isAutoAttachmentDownloadEnabled()) {
         byte[] data = Branding.getData(24577);
         if (data == null) {
            return 0;
         }

         switch (data[0]) {
            case -1:
               return 0;
            case 0:
            default:
               return 0;
            case 1:
               return 1;
            case 2:
               return 2;
         }
      } else {
         return 0;
      }
   }

   private MessageListOptions() {
   }

   static final boolean isAutoAttachmentDownloadEnabled() {
      if (ITPolicyInternal.isITPolicyEnabled()) {
         boolean allowed = ITPolicy.getBoolean(23, 8, false);
         if (!allowed) {
            return false;
         }
      }

      byte[] vsmData = Branding.getData(24576);
      return vsmData != null && vsmData.length != 0 ? vsmData[0] == 1 : false;
   }

   private final void displayNewMessageIcon(MessageEntryPoint entry) {
      Object count = entry.get(12, (Object)null);
      if (count != null && count instanceof UnreadCount) {
         entry.set(2, ((UnreadCount)count).hasNewStatus() ? "new" : (String)null);
      }
   }

   @Override
   protected final SyncItem getSyncItem() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         SyncItem syncItem = (SyncItem)ar.get(-7789810865029204428L);
         if (syncItem == null) {
            syncItem = new MessageListOptions$MessageListOptionsSyncItem();
            ar.put(-7789810865029204428L, syncItem);
         }

         return syncItem;
      }
   }

   private final MessageListOptions$CMIMEServiceSetting getCMIMEServiceSetting(String name, String uid) {
      MessageListOptions$CMIMEServiceSetting serviceSetting;
      synchronized (_serviceKey) {
         _serviceKey.initialize(name, uid);
         serviceSetting = (MessageListOptions$CMIMEServiceSetting)this._persistedMessageListOptions._serviceSettings.get(_serviceKey);
      }

      if (serviceSetting != null) {
         return serviceSetting;
      }

      synchronized (this._persistedMessageListOptions._serviceSettings) {
         Enumeration e = this._persistedMessageListOptions._serviceSettings.keys();
         ServiceKey nameMatch = null;
         ServiceKey uidMatch = null;

         while (e.hasMoreElements()) {
            ServiceKey key = (ServiceKey)e.nextElement();
            if (StringUtilities.strEqualIgnoreCase(key.getName(), name, 1701707776)) {
               nameMatch = key;
            }

            if (StringUtilities.strEqualIgnoreCase(key.getUid(), uid, 1701707776)) {
               uidMatch = key;
            }
         }

         if (nameMatch != null) {
            serviceSetting = (MessageListOptions$CMIMEServiceSetting)this._persistedMessageListOptions._serviceSettings.get(nameMatch);
         } else if (uidMatch != null) {
            serviceSetting = (MessageListOptions$CMIMEServiceSetting)this._persistedMessageListOptions._serviceSettings.get(uidMatch);
         }

         return serviceSetting;
      }
   }

   @Override
   protected final PersistentObject getPersistentObject() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(7237989745528696983L);
      synchronized (persistentObject) {
         this._persistedMessageListOptions = (MessageListOptions$PersistedMessageListOptions)persistentObject.getContents();
         if (this._persistedMessageListOptions == null) {
            this._persistedMessageListOptions = new MessageListOptions$PersistedMessageListOptions();
            persistentObject.setContents(this._persistedMessageListOptions, 51, false);
            persistentObject.commit();
         }

         return persistentObject;
      }
   }

   public static final MessageListOptions getOptions() {
      if (_options == null) {
         _options = new MessageListOptions();
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         ar.replace(7056244251443120672L, _options);
      }

      return _options;
   }
}
