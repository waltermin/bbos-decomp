package net.rim.device.apps.internal.phone;

final class VoiceApp$6 implements Runnable {
   private final VoiceApp this$0;

   VoiceApp$6(VoiceApp _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (!this.this$0._active) {
         this.this$0.popActiveScreen();
      }
   }
}
