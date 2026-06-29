package net.rim.device.api.crypto.keystore;

import java.util.Vector;
import net.rim.device.api.system.DeviceInfo;

class KeyStoreAddressInjector$WorkerThread extends Thread {
   Vector _items;
   private final KeyStoreAddressInjector this$0;

   KeyStoreAddressInjector$WorkerThread(KeyStoreAddressInjector _1, Vector items) {
      this.this$0 = _1;
      this._items = items;
   }

   @Override
   public void run() {
      this.setPriority(1);
      int size = this._items.size();

      for (int i = 0; i < size; i++) {
         while (DeviceInfo.getIdleTime() < 10) {
            try {
               Thread.sleep(10000);
            } finally {
               continue;
            }
         }

         KeyStoreData data = (KeyStoreData)this._items.elementAt(i);
         this.this$0.processCertificate(data);
      }
   }
}
