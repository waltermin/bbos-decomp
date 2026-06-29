package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.synchronization.SyncItem;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.options.OptionsBase;
import net.rim.device.internal.system.SystemPropertyManager;

public final class MMSOptions extends OptionsBase {
   private MMSOptionsData _mmsOptionsData;
   public static final int ALWAYS = 0;
   public static final int NEVER = 1;
   public static final int HOMEONLY = 2;
   public static final int UNSPECIFIED = -1;
   public static final int URLSCHEME_NONE = 0;
   public static final int URLSCHEME_VERIZON = 1;
   public static final int COMPOSEFROMSCHEME_INSERT_BY_MMSC = 0;
   public static final int COMPOSEFROMSCHEME_SET_BY_DEVICE = 1;
   public static final int FLAG_DENY_DELIVERY_NOTIFICATION = 1;
   public static final int FLAG_DENY_READ_NOTIFICATION = 2;
   public static final int FLAG_REJECT_ANONYMOUS_MESSAGES = 4;
   public static final int FLAG_REJECT_ADVERTISEMENTS = 8;
   public static final int FLAG_USE_GME_TRANSPORT = 16;
   public static final int FLAG_IGNORE_PRESENTATION_MODEL = 32;
   public static final int FLAG_REQUEST_DELIVERY_REPORT = 64;
   public static final int FLAG_REQUEST_READ_REPORT = 128;
   public static final int FLAG_DEBUG_DUMP_BYTES = 256;
   public static final int FLAG_DEBUG_DUMP_FIELDS = 512;
   private static final int FLAGS_UNINITIALIZED = Integer.MIN_VALUE;
   private static final long MMS_OPTIONS_GUID = 8677624854441102609L;
   private static MMSOptions _instance;
   private static final long MMS_OPTIONS_DATA_KEY = 572030951534635290L;

   public static final void registerOnceOnSystemStart() {
      MMSOptionsProvider.register();
      getInstance();
      SyncManager.getInstance().enableSynchronization(MMSOptionsSyncItem.getInstance());
      long MMS_CLIENT_OPTIONS_GUID = -4248845697481338338L;
      ApplicationRegistry.getApplicationRegistry().put(MMS_CLIENT_OPTIONS_GUID, new ExternalClientOptions());
      SystemPropertyManager spManager = SystemPropertyManager.getInstance();
      spManager.addProvider(new MMSOptions$1());
   }

   private MMSOptions() {
      PersistentObject persistentObject = this.getPersistentObject();
      this._mmsOptionsData = (MMSOptionsData)persistentObject.getContents();
      if (this._mmsOptionsData == null) {
         this._mmsOptionsData = new MMSOptionsData();
         persistentObject.setContents(this._mmsOptionsData, 51, false);
         this.resetAllOptions();
      }
   }

   public static final MMSOptions getInstance() {
      if (_instance == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _instance = (MMSOptions)ar.get(8677624854441102609L);
         if (_instance == null) {
            synchronized (ar) {
               _instance = (MMSOptions)ar.get(8677624854441102609L);
               if (_instance == null) {
                  _instance = new MMSOptions();
                  ar.put(8677624854441102609L, _instance);
               }
            }
         }
      }

      return _instance;
   }

   public final int getReceptionMode() {
      int mode = this._mmsOptionsData._receptionMode;
      if (mode == -1) {
         mode = MMSClientServiceBook.getDefaultReceptionMode();
      }

      return mode;
   }

   public final void setReceptionMode(int mode) {
      this._mmsOptionsData._receptionMode = mode;
      this.commit();
   }

   public final int getAutomaticRetrievalMode() {
      int mode = MMSClientServiceBook.getAutoRetrievalMode();
      if (mode == -1) {
         mode = this._mmsOptionsData._retrievalMode;
      }

      if (mode == -1) {
         mode = MMSClientServiceBook.getDefaultAutomaticRetrievalMode();
      }

      return mode;
   }

   public final void setAutomaticRetrievalMode(int mode) {
      this._mmsOptionsData._retrievalMode = mode;
      this.commit();
   }

   public final boolean getOptionFlag(int flag) {
      return (this.getOptionFlags() & flag) != 0;
   }

   public final void setOptionFlag(int flag, boolean value) {
      if ((this._mmsOptionsData._flags & -2147483648) != 0) {
         this.setOptionFlags(MMSClientServiceBook.getDefaultOptionFlags());
      }

      if (value) {
         this._mmsOptionsData.setFlags(flag);
      } else {
         this._mmsOptionsData.clearFlags(flag);
      }

      this.commit();
   }

   final int getOptionFlags() {
      return (this._mmsOptionsData._flags & -2147483648) != 0 ? MMSClientServiceBook.getDefaultOptionFlags() : this._mmsOptionsData._flags;
   }

   final void setOptionFlags(int flags) {
      this._mmsOptionsData._flags = flags;
      this.commit();
   }

   final void resetAllOptions() {
      this.setReceptionMode(-1);
      this.setAutomaticRetrievalMode(-1);
      this.setOptionFlags(Integer.MIN_VALUE);
   }

   @Override
   protected final SyncItem getSyncItem() {
      return MMSOptionsSyncItem.getInstance();
   }

   @Override
   protected final PersistentObject getPersistentObject() {
      return RIMPersistentStore.getPersistentObject(572030951534635290L);
   }
}
