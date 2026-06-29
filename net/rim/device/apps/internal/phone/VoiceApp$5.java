package net.rim.device.apps.internal.phone;

final class VoiceApp$5 implements Runnable {
   private final VoiceApp this$0;

   VoiceApp$5(VoiceApp _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0._hidingActiveScreen = false;
      if (!this.this$0._activeScreen.isDisplayed()) {
         try {
            this.this$0.pushScreen(this.this$0._activeScreen);
         } finally {
            return;
         }
      }
   }
}
