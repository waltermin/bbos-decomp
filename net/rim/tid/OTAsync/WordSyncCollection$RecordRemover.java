package net.rim.tid.OTAsync;

import net.rim.device.api.synchronization.SyncObject;

class WordSyncCollection$RecordRemover implements Runnable {
   private SyncObject _record;
   private WordSyncCollection _instance;
   private final WordSyncCollection this$0;

   WordSyncCollection$RecordRemover(WordSyncCollection _1, WordSyncCollection instance, SyncObject record) {
      this.this$0 = _1;
      this._instance = instance;
      this._record = record;
   }

   @Override
   public void run() {
      this.this$0._listeners.fireElementRemoved(this._instance, this._record);
   }
}
