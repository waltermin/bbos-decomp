package net.rim.device.apps.internal.lbs.finder;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;

final class MessageRunnable implements Runnable {
   private String _msg;

   public MessageRunnable(String msg) {
      this._msg = msg;
   }

   @Override
   public final void run() {
      synchronized (Application.getEventLock()) {
         Dialog.alert(this._msg);
      }
   }
}
