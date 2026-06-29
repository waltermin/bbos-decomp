package net.rim.device.apps.internal.phone;

final class PhoneNumberInput$2 implements Runnable {
   private final PhoneNumberInput this$0;

   PhoneNumberInput$2(PhoneNumberInput _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      synchronized (this) {
         if (this.this$0._delayingEchoId != -1) {
            int cursorPos = this.this$0.getCursorPosition();
            this.this$0._delayingEchoId = -1;
            if (this.this$0.isValidInput()) {
               this.this$0.echoTone(this.this$0._bufferedString.charAt(cursorPos));
            }

            this.this$0.setCursorPosition(++cursorPos);
            PhoneNumberInput.access$500(this.this$0);
         }
      }
   }
}
