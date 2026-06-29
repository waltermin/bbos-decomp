package net.rim.device.apps.internal.phone;

final class VoiceApp$7 implements Runnable {
   private final Object val$callInitContext;
   private final VoiceApp this$0;

   VoiceApp$7(VoiceApp _1, Object _2) {
      this.this$0 = _1;
      this.val$callInitContext = _2;
   }

   @Override
   public final void run() {
      VoiceApp.storeRedialInfo(this.val$callInitContext);
   }
}
