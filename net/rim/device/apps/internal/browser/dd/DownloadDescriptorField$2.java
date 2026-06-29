package net.rim.device.apps.internal.browser.dd;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.PopupScreen;

final class DownloadDescriptorField$2 extends PopupScreen {
   private final DownloadDescriptorField this$0;

   DownloadDescriptorField$2(DownloadDescriptorField _1, Manager x0) {
      super(x0);
      this.this$0 = _1;
   }

   @Override
   public final void onDisplay() {
      super.onDisplay();
      new Thread(this.this$0._downloadThread).start();
      this.this$0._statusButton.setFocus();
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      if (c != 27 && c != '\n') {
         return super.keyChar(c, status, time);
      }

      this.this$0.fieldChanged(this.this$0._statusButton, 0);
      return true;
   }
}
