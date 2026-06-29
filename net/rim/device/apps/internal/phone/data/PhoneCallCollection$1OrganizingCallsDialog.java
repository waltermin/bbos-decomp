package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.Dialog;

final class PhoneCallCollection$1OrganizingCallsDialog extends Dialog {
   private final Runnable val$updateRunnable;
   private final PhoneCallCollection this$0;

   public PhoneCallCollection$1OrganizingCallsDialog(PhoneCallCollection _1, String message, Bitmap bitmap, long style, Runnable _6) {
      super(message, null, null, 0, bitmap, style);
      this.this$0 = _1;
      this.val$updateRunnable = _6;
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         Runnable wrapperRunnable = new PhoneCallCollection$1OrganizingCallsDialog$1(this);
         ((Thread)(new Object(wrapperRunnable))).start();
      }
   }
}
