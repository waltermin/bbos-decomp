package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.system.Application;

class PhoneListView$DeleteStatusDialog$1 extends Thread {
   private final PhoneListView$DeleteStatusDialog this$1;

   PhoneListView$DeleteStatusDialog$1(PhoneListView$DeleteStatusDialog _1) {
      this.this$1 = _1;
   }

   @Override
   public void run() {
      for (int i = this.this$1._selectedItems.length - 1; i >= 0; i--) {
         this.this$1._phoneListView.delete(this.this$1.this$0.getInternalIndex(this.this$1._selectedItems[i]));
      }

      synchronized (Application.getEventLock()) {
         this.this$1.close();
      }
   }
}
