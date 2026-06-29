package net.rim.device.apps.api.ui;

class ProgressIndicator$1 implements Runnable {
   private final ProgressIndicator this$0;

   ProgressIndicator$1(ProgressIndicator _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0._uiApplication.popScreen(this.this$0._popupScreen);
   }
}
