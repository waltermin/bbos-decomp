package net.rim.device.apps.internal.phone;

final class CallManager$CallFailedAlertDialog$1 implements Runnable {
   private final CallManager$CallFailedAlertDialog this$0;

   CallManager$CallFailedAlertDialog$1(CallManager$CallFailedAlertDialog _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.close();
   }
}
