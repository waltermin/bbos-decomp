package net.rim.device.apps.api.options;

import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.synchronization.SyncItem;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.util.Arrays;

public class OptionsBase {
   private PersistentObject _persistentObject;
   private SyncItem _syncItem;
   private OptionsChangeListener[] _optionsChangeListeners;

   protected OptionsBase() {
      this(true);
   }

   protected OptionsBase(boolean initialize) {
      this(true, false);
   }

   protected OptionsBase(boolean initialize, boolean nullSyncItemPermitted) {
      if (initialize) {
         this.initialize(nullSyncItemPermitted);
      }
   }

   protected void initialize() {
      this.initialize(false);
   }

   private void initialize(boolean nullSyncItemPermitted) {
      this._persistentObject = this.getPersistentObject();
      this._syncItem = this.getSyncItem();
      if (this._persistentObject == null) {
         throw new Object("getPersistentObject() may not return null!");
      }

      if (this._syncItem == null && !nullSyncItemPermitted) {
         throw new Object("getSyncItem() may not return null!");
      }
   }

   protected SyncItem getSyncItem() {
      throw null;
   }

   protected PersistentObject getPersistentObject() {
      throw null;
   }

   public void commit() {
      this._persistentObject.commit();
      if (this._syncItem instanceof Object) {
         ((OTASyncCapableSyncItem)this._syncItem).fireSyncItemUpdated();
      }
   }

   public void enableSynchronization() {
      if (this._syncItem != null) {
         SyncManager manager = SyncManager.getInstance();
         if (manager != null) {
            manager.enableSynchronization(this._syncItem);
         }
      }
   }

   public synchronized void addOptionsChangeListener(OptionsChangeListener listener) {
      if (this._optionsChangeListeners == null) {
         this._optionsChangeListeners = new OptionsChangeListener[0];
      }

      Arrays.add(this._optionsChangeListeners, listener);
   }

   public synchronized void removeOptionsChangeListener(OptionsChangeListener listener) {
      if (this._optionsChangeListeners != null) {
         Arrays.remove(this._optionsChangeListeners, listener);
      }
   }

   protected synchronized void fireOptionsChanged(int changedOptions) {
      if (this._optionsChangeListeners != null) {
         int numListeners = this._optionsChangeListeners.length;

         for (int i = 0; i < numListeners; i++) {
            this._optionsChangeListeners[i].optionsChanged(changedOptions);
         }
      }
   }
}
