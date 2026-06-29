package net.rim.device.apps.internal.phone;

final class CallDisplayDialog$DismissDialogTimer$1 implements Runnable {
   private final CallDisplayDialog$DismissDialogTimer this$1;

   CallDisplayDialog$DismissDialogTimer$1(CallDisplayDialog$DismissDialogTimer _1) {
      this.this$1 = _1;
   }

   @Override
   public final void run() {
      this.this$1._timeoutRunnable.run();
      this.this$1.this$0.dismiss();
   }
}
