package net.rim.device.apps.internal.mms.plugin;

import net.rim.device.apps.internal.mms.service.BackgroundTaskThread;
import net.rim.device.apps.internal.mms.service.MMSProtocolDataUnit;

final class ConverterRunnable implements Runnable {
   private MMSProtocolDataUnit _pdu;

   ConverterRunnable(MMSProtocolDataUnit pdu) {
      this._pdu = pdu;
   }

   @Override
   public final void run() {
      System.out.println("MMS Notification pdu queued.");
      BackgroundTaskThread.addTask(new ProcessNotificationTask(this._pdu));
   }
}
