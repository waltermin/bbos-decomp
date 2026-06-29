package net.rim.device.apps.internal.phone.data;

class PhoneCallCollection$2 implements Runnable {
   private final Object[] val$deferredCallLogs;
   private final PhoneCallCollection this$0;

   PhoneCallCollection$2(PhoneCallCollection _1, Object[] _2) {
      this.this$0 = _1;
      this.val$deferredCallLogs = _2;
   }

   @Override
   public void run() {
      PhoneFolders.addCallLogs(this.val$deferredCallLogs, false);
   }
}
