package net.rim.device.apps.internal.help;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;

final class RenderingThread$1 implements Runnable {
   private final RenderingThread this$0;

   RenderingThread$1(RenderingThread _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      synchronized (Application.getEventLock()) {
         Field contentField = this.this$0._browserField.getDisplayableContent();
         if (contentField != null) {
            Manager contentManager = contentField.getManager();
            if (contentManager != null) {
               contentManager.setFocus(0, this.this$0._focusPosition, 0);
               contentManager.setVerticalScroll(this.this$0._scrollPosition);
               contentManager.invalidate();
            }
         }
      }
   }
}
