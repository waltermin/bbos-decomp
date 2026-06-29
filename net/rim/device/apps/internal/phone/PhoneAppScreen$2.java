package net.rim.device.apps.internal.phone;

import net.rim.device.apps.internal.phone.api.PTTKeyHandler;

final class PhoneAppScreen$2 implements Runnable {
   private final PTTKeyHandler val$finalPTTKeyHdler;
   private final Object val$selectedItem;
   private final PhoneAppScreen this$0;

   PhoneAppScreen$2(PhoneAppScreen _1, PTTKeyHandler _2, Object _3) {
      this.this$0 = _1;
      this.val$finalPTTKeyHdler = _2;
      this.val$selectedItem = _3;
   }

   @Override
   public final void run() {
      this.val$finalPTTKeyHdler.onPTTKeyPressedAndHeld(this.val$selectedItem);
   }
}
