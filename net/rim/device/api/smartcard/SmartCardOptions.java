package net.rim.device.api.smartcard;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Persistable;

public final class SmartCardOptions implements Persistable {
   private boolean _allowLockOnCardRemoval;
   private boolean _allowPINCaching;
   private boolean _enableLEDFlashingOnOpenSession;
   private static final long SYNC_ID;
   private static final long ID;
   private static PersistentObject _persist = RIMPersistentStore.getPersistentObject(-2650598348482057462L);

   public static final SmartCardOptions getInstance() {
      return (SmartCardOptions)_persist.getContents();
   }

   private final SmartCardOptions$SmartCardOptionsSyncItem getSmartCardOptionsSyncItem() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      SmartCardOptions$SmartCardOptionsSyncItem smartCardOptionsSyncItem = (SmartCardOptions$SmartCardOptionsSyncItem)registry.getOrWaitFor(
         6548238223947587527L
      );
      if (smartCardOptionsSyncItem == null) {
         smartCardOptionsSyncItem = new SmartCardOptions$SmartCardOptionsSyncItem(this);
         registry.put(6548238223947587527L, smartCardOptionsSyncItem);
      }

      return smartCardOptionsSyncItem;
   }

   public static final void register() {
      SmartCardOptions$SmartCardOptionsSyncItem smartCardOptionsSyncItem = getInstance().getSmartCardOptionsSyncItem();
      SyncManager syncManager = SyncManager.getInstance();
      if (syncManager != null) {
         syncManager.enableSynchronization(smartCardOptionsSyncItem);
      }
   }

   private final void resetOptions() {
      this._allowLockOnCardRemoval = false;
      this._allowPINCaching = false;
      this._enableLEDFlashingOnOpenSession = true;
      _persist.commit();
   }

   private final void commit() {
      _persist.commit();
      this.getSmartCardOptionsSyncItem().fireSyncItemUpdated();
   }

   public final void setAllowLockOnCardRemoval(boolean allowLockOnCardRemoval) {
      this._allowLockOnCardRemoval = allowLockOnCardRemoval;
      this.commit();
   }

   public final boolean getAllowLockOnCardRemoval() {
      return ITPolicy.getBoolean(24, 1, false) ? true : this._allowLockOnCardRemoval;
   }

   public final void setAllowPINCaching(boolean allowPINCaching) {
      this._allowPINCaching = allowPINCaching;
      this.commit();
   }

   public final boolean getAllowPINCaching() {
      return !ITPolicy.getBoolean(24, 50, false) ? false : this._allowPINCaching;
   }

   public final void setEnableLEDFlashingOnOpenSession(boolean enableLEDFlashingOnOpenSession) {
      this._enableLEDFlashingOnOpenSession = enableLEDFlashingOnOpenSession;
      this.commit();
   }

   public final boolean getEnableLEDFlashingOnOpenSession() {
      return this._enableLEDFlashingOnOpenSession;
   }

   static {
      synchronized (_persist) {
         if (!(_persist.getContents() instanceof SmartCardOptions)) {
            SmartCardOptions smartCardOptions = new SmartCardOptions();
            _persist.setContents(smartCardOptions, 4801362);
            smartCardOptions.resetOptions();
         }
      }
   }
}
