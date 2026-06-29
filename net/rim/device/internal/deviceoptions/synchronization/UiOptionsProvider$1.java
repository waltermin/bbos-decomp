package net.rim.device.internal.deviceoptions.synchronization;

import net.rim.device.api.synchronization.SyncObject;

class UiOptionsProvider$1 implements Runnable {
   private final UiOptionsProvider this$0;

   UiOptionsProvider$1(UiOptionsProvider _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      SyncObject dummyObject = new OptionsSyncCollection$DummySyncObject(-1860137122);
      this.this$0._syncCollection.removeSyncObject(dummyObject);
   }
}
