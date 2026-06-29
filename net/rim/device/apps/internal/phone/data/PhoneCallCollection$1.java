package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.apps.api.messaging.util.SimpleFolder;

class PhoneCallCollection$1 implements Runnable {
   private final CallLogCollection val$clc;
   private final PhoneCallCollection this$0;

   PhoneCallCollection$1(PhoneCallCollection _1, CallLogCollection _2) {
      this.this$0 = _1;
      this.val$clc = _2;
   }

   @Override
   public void run() {
      SimpleFolder[] folders = PhoneFolders.getPhoneFolders();
      if (folders != null) {
         for (int i = 0; i < folders.length; i++) {
            SimpleFolder folder = folders[i];
            ReadableList logs = (ReadableList)folder.getContainedItems();

            for (int j = logs.size() - 1; j >= 0; j--) {
               PhoneCallModelImpl log = (PhoneCallModelImpl)logs.getAt(j);
               if (log != null) {
                  this.val$clc.callLogInitialized(log);
               }
            }
         }
      }

      if (this.this$0._deferredCallLogAdditions != null && this.this$0._deferredCallLogAdditions.length > 0) {
         Object[] logs = this.this$0._deferredCallLogAdditions;
         this.this$0._deferredCallLogAdditions = null;
         PhoneFolders.addCallLogs(logs, false);
      }

      CallLogCollection.getInstance().onInitializationComplete();
   }
}
