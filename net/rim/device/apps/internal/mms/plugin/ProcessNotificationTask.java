package net.rim.device.apps.internal.mms.plugin;

import net.rim.device.apps.internal.mms.api.MMSTask;
import net.rim.device.apps.internal.mms.service.MMSProtocolDataUnit;
import net.rim.device.apps.internal.mms.service.MMSServiceUtil;

final class ProcessNotificationTask implements MMSTask {
   private MMSProtocolDataUnit _pdu;

   public ProcessNotificationTask(MMSProtocolDataUnit pdu) {
      this._pdu = pdu;
   }

   @Override
   public final long getTaskThreadGuid() {
      return -3436621066262173388L;
   }

   @Override
   public final boolean requiresRadioCoverage() {
      return false;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         MMSServiceUtil.processPDU(this._pdu);
         var3 = false;
      } finally {
         if (var3) {
            System.out.println("MMS Notification pdu processed.");
         }
      }

      System.out.println("MMS Notification pdu processed.");
   }
}
