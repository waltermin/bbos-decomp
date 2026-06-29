package net.rim.tid.OTAsync;

import java.util.Vector;
import net.rim.device.api.synchronization.SyncObject;

class WordSyncCollection$Remover implements Runnable {
   Vector _records;
   WordSyncCollection _instance;
   private final WordSyncCollection this$0;

   public WordSyncCollection$Remover(WordSyncCollection _1, WordSyncCollection instance, Vector elements) {
      this.this$0 = _1;
      this._records = elements;
      this._instance = instance;
   }

   @Override
   public void run() {
      if (this.this$0._debugOutputEnabled) {
         System.err.println("<<Remover:run>>");
      }

      synchronized (this.this$0._cache) {
         for (int i = this._records.size() - 1; i >= 0; i--) {
            SyncObject so = (SyncObject)this._records.elementAt(i);
            this.this$0._cache.remove(so.getUID());
            this.this$0._elements.remove(so.getUID());
            this.this$0._listeners.fireElementRemoved(this._instance, so);
            if (this._instance._testListener != null) {
               this._instance._testListener.wordRemoved(so.getUID());
            }

            if (this.this$0._debugOutputEnabled) {
               System.err.println("Word is removed: " + so.toString());
            }
         }
      }
   }
}
