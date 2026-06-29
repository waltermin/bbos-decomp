package net.rim.device.internal.deviceoptions.synchronization;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.Application;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.deviceoptions.OptionsProvider;
import net.rim.device.internal.ui.UiOptionsRegistry;

class UiOptionsProvider implements OptionsProvider, CollectionListener {
   private OptionsSyncCollection _syncCollection;
   private static final int UID = -1860137122;

   @Override
   public void reset(Collection collection) {
   }

   @Override
   public void getOptionsData(DataBuffer buffer) {
      UiOptionsRegistry.getInstance().getOptionsData(buffer);
   }

   @Override
   public void setOptionsData(DataBuffer buffer) {
      UiOptionsRegistry registry = UiOptionsRegistry.getInstance();
      if (registry.getLegacyUiOptionsState() != 1) {
         registry.setOptionsData(buffer);
      }

      this.purgeLegacyData(false);
   }

   @Override
   public int getUID() {
      return -1860137122;
   }

   @Override
   public void elementUpdated(Collection collection, Object newObject, Object oldObject) {
   }

   @Override
   public void elementRemoved(Collection collection, Object object) {
      if (object instanceof Object && ((SyncObject)object).getUID() == -1860137122 && UiOptionsRegistry.getInstance().getLegacyUiOptionsState() == 3) {
         this.cleanUpAndDie();
      }
   }

   @Override
   public void elementAdded(Collection collection, Object object) {
      if (object instanceof Object && ((SyncObject)object).getUID() == -1860137122 && UiOptionsRegistry.getInstance().getLegacyUiOptionsState() == 3) {
         this.callForSyncObjectRemoval(1000);
      }
   }

   UiOptionsProvider(OptionsSyncCollection syncCollection, int state) {
      this._syncCollection = syncCollection;
      this._syncCollection.addCollectionListener(this);
      switch (state) {
         case 1:
            this.purgeLegacyData(true);
            return;
         case 3:
            this.callForSyncObjectRemoval(0);
      }
   }

   private void purgeLegacyData(boolean immediatePurge) {
      if (immediatePurge) {
         this._syncCollection.removeSyncObject(this);
         this.cleanUpAndDie();
      } else {
         UiOptionsRegistry.getInstance().setLegacyUiOptionsState(3);
      }
   }

   private void cleanUpAndDie() {
      UiOptionsRegistry.getInstance().setLegacyUiOptionsState(2);
      this._syncCollection.removeLegacyOptionsProvider(this);
      this._syncCollection.removeCollectionListener(this);
   }

   private void callForSyncObjectRemoval(int delay) {
      Application.getApplication().invokeLater(new UiOptionsProvider$1(this), delay, false);
   }
}
