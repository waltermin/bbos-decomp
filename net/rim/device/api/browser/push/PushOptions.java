package net.rim.device.api.browser.push;

import net.rim.device.api.synchronization.SyncItem;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.apps.api.options.OptionsBase;

public final class PushOptions extends OptionsBase {
   private PushOptions$PersistedPushOptions _persistedPushOptions;
   private static final long PUSH_OPTIONS_SYNC_ITEM;
   private static final long PERSISTED_PUSH_OPTIONS;
   public static final int MODE_AUTO;
   public static final int MODE_PROMPT;
   public static final int MODE_REJECT;
   public static final int MODE_ALLOW_ALL;
   public static final int MODE_ALLOW_SPECIFC;
   public static final int MODE_DENY_ALL;
   public static final int FLAG_ENABLE_PUSH;
   public static final int FLAG_MDS_ENABLE_PUSH;
   public static final int FLAG_WAP_ENABLE_PUSH;
   public static final int FLAG_ACCEPT_MODE_SL_MDS_MESSAGES;
   public static final int FLAG_ACCEPT_MODE_SI_MDS_MESSAGES;
   public static final int FLAG_ACCEPT_MODE_OTHER_MDS_MESSAGES;
   public static final int FLAG_ALLOW_OTHER_APPS;
   public static final int FLAG_FILTER_MODE_SL_MDS_MESSAGES;
   public static final int FLAG_FILTER_MODE_SI_MDS_MESSAGES;
   public static final int FLAG_FILTER_MODE_OTHER_MDS_MESSAGES;
   public static final int FLAG_FILTER_MODE_SL_SMS_MESSAGES;
   public static final int FLAG_FILTER_MODE_SI_SMS_MESSAGES;
   public static final int FLAG_FILTER_MODE_OTHER_SMS_MESSAGES;
   public static final int FLAG_FILTER_MODE_SL_IP_MESSAGES;
   public static final int FLAG_FILTER_MODE_SI_IP_MESSAGES;
   public static final int FLAG_FILTER_MODE_OTHER_IP_MESSAGES;
   public static final int FLAG_ACCEPT_MODE_SL_SMS_MESSAGES;
   public static final int FLAG_ACCEPT_MODE_SI_SMS_MESSAGES;
   public static final int FLAG_ACCEPT_MODE_OTHER_SMS_MESSAGES;
   public static final int FLAG_ACCEPT_MODE_SL_IP_MESSAGES;
   public static final int FLAG_ACCEPT_MODE_SI_IP_MESSAGES;
   public static final int FLAG_ACCEPT_MODE_OTHER_IP_MESSAGES;
   public static final int PROTOCOL_TYPE_IP;
   public static final int PROTOCOL_TYPE_SMS;
   public static final int PROTOCOL_TYPE_MDS;
   public static final int PUSH_TYPE_SL;
   public static final int PUSH_TYPE_SI;
   public static final int PUSH_TYPE_OTHER;
   private static PushOptions _options;

   private PushOptions() {
   }

   public static final PushOptions getOptions() {
      if (_options == null) {
         _options = new PushOptions();
      }

      return _options;
   }

   @Override
   protected final PersistentObject getPersistentObject() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(3775803376412909139L);
      synchronized (persistentObject) {
         this._persistedPushOptions = (PushOptions$PersistedPushOptions)persistentObject.getContents();
         if (this._persistedPushOptions == null) {
            this._persistedPushOptions = new PushOptions$PersistedPushOptions();
            this.restoreDefaults();
            persistentObject.setContents(this._persistedPushOptions, 51);
            persistentObject.commit();
         }

         return persistentObject;
      }
   }

   @Override
   protected final SyncItem getSyncItem() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         SyncItem syncItem = (SyncItem)ar.get(-112614746840035147L);
         if (syncItem == null) {
            syncItem = new PushOptions$PushOptionsSyncItem();
            ar.put(-112614746840035147L, syncItem);
         }

         return syncItem;
      }
   }

   public final boolean getEnablePush() {
      return this._persistedPushOptions._enablePush;
   }

   public final boolean getWAPEnablePush() {
      return this._persistedPushOptions._wapEnablePush;
   }

   public final boolean getMDSEnablePush() {
      return this._persistedPushOptions._mdsEnablePush;
   }

   public final void setEnablePush(boolean enablePush, boolean restart) {
      this._persistedPushOptions._enablePush = enablePush;
      this.commit();
      if (restart) {
         PushProcessor processor = PushProcessor.getInstance();
         if (enablePush) {
            processor.start();
            return;
         }

         processor.stop();
      }
   }

   public final void setWAPEnablePush(boolean enablePush, boolean restart) {
      this._persistedPushOptions._wapEnablePush = enablePush;
      this.commit();
      if (restart) {
         PushProcessor processor = PushProcessor.getInstance();
         processor.stop();
         processor.start();
      }
   }

   public final void setMDSEnablePush(boolean enablePush, boolean restart) {
      this._persistedPushOptions._mdsEnablePush = enablePush;
      this.commit();
      if (restart) {
         PushProcessor processor = PushProcessor.getInstance();
         processor.stop();
         processor.start();
      }
   }

   public final boolean getAllowOtherApplications() {
      return this._persistedPushOptions._allowOtherApplications;
   }

   public final void setAllowOtherApplications(boolean allowOtherApplications) {
      this._persistedPushOptions._allowOtherApplications = allowOtherApplications;
   }

   public final String getFilterValue(int pushType, int protocolType) {
      return this._persistedPushOptions._filterValues[pushType][protocolType];
   }

   public final int getFilterMode(int pushType, int protocolType) {
      throw new RuntimeException("cod2jar: array load: unknown element");
   }

   public final int getAcceptMode(int pushType, int protocolType) {
      throw new RuntimeException("cod2jar: array load: unknown element");
   }

   public final void setFilterMode(int pushType, int protocolType, int mode, String value) {
      throw new RuntimeException("cod2jar: array store: unknown element");
   }

   public final void setAcceptMode(int pushType, int protocolType, int mode) {
      throw new RuntimeException("cod2jar: array store: unknown element");
   }

   public final int getDirtyMask() {
      return this._persistedPushOptions._dirtyMask;
   }

   public final void setDirtyMask(int value) {
      this._persistedPushOptions._dirtyMask = value;
   }

   public final void restoreDefaults() {
      this._persistedPushOptions._enablePush = true;
      this._persistedPushOptions._wapEnablePush = true;
      this._persistedPushOptions._mdsEnablePush = true;
      this._persistedPushOptions._allowOtherApplications = true;

      for (int i = 0; i < this._persistedPushOptions._filterModes.length; i++) {
         Arrays.fill((int[])this._persistedPushOptions._filterModes[i], 0);
      }

      for (int i = 0; i < this._persistedPushOptions._acceptModes.length; i++) {
         Arrays.fill((int[])this._persistedPushOptions._acceptModes[i], 0);
      }

      for (int i = 0; i < this._persistedPushOptions._acceptModes.length; i++) {
         for (int j = 0; j < this._persistedPushOptions._acceptModes[i].length; j++) {
            this._persistedPushOptions._filterValues[i][j] = null;
         }
      }

      this._persistedPushOptions._dirtyMask = 0;
      PushSource[] sources = WAPPushSource.getAllServicesInternal();
      if (sources != null) {
         for (int i = sources.length - 1; i >= 0; i--) {
            if (sources[i] instanceof WAPPushSource) {
               this.addFilters((WAPPushSource)sources[i]);
            }
         }
      }
   }

   private static final String union(String aString, String bString) {
      if (aString == null) {
         return bString;
      }

      if (bString == null) {
         return aString;
      }

      bString = bString.trim();
      StringTokenizer tokenizer = (StringTokenizer)(new Object(aString, ','));

      while (tokenizer.hasMoreTokens()) {
         String token = tokenizer.nextToken().trim();
         if (token.equals(bString)) {
            return aString;
         }
      }

      return ((StringBuffer)(new Object())).append(aString).append(',').append(bString).toString();
   }

   final void addFilters(WAPPushSource source) {
      if (source != null) {
         if (this._persistedPushOptions._dirtyMask != 0) {
            return;
         }

         int protocolType = -1;
         switch (source.getConnectionType()) {
            case 5:
               break;
            case 6:
            default:
               protocolType = 1;
               break;
            case 7:
               protocolType = 0;
         }

         if (protocolType != -1) {
            this.setAcceptMode(0, protocolType, Math.max(this.getAcceptMode(0, protocolType), source.getSLAcceptMode()));
            this.setFilterMode(
               0,
               protocolType,
               Math.max(this.getFilterMode(0, protocolType), source.getSLFilterMode()),
               union(this.getFilterValue(0, protocolType), source.getSLFilter())
            );
            this.setAcceptMode(1, protocolType, Math.max(this.getAcceptMode(1, protocolType), source.getSIAcceptMode()));
            this.setFilterMode(
               1,
               protocolType,
               Math.max(this.getFilterMode(1, protocolType), source.getSIFilterMode()),
               union(this.getFilterValue(1, protocolType), source.getSIFilter())
            );
            this.setAcceptMode(2, protocolType, Math.max(this.getAcceptMode(2, protocolType), source.getOtherAcceptMode()));
            this.setFilterMode(
               2,
               protocolType,
               Math.max(this.getFilterMode(2, protocolType), source.getOtherFilterMode()),
               union(this.getFilterValue(2, protocolType), source.getOtherFilter())
            );
         }
      }
   }
}
