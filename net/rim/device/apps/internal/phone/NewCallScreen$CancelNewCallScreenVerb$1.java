package net.rim.device.apps.internal.phone;

final class NewCallScreen$CancelNewCallScreenVerb$1 implements Runnable {
   private final NewCallScreen$CancelNewCallScreenVerb this$1;

   NewCallScreen$CancelNewCallScreenVerb$1(NewCallScreen$CancelNewCallScreenVerb _1) {
      this.this$1 = _1;
   }

   @Override
   public final void run() {
      this.this$1.this$0._voiceApp.popScreen(this.this$1._screen);
   }
}
