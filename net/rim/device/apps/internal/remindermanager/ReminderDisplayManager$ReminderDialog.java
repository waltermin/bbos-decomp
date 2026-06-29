package net.rim.device.apps.internal.remindermanager;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.Dialog;

final class ReminderDisplayManager$ReminderDialog extends Dialog {
   ReminderDisplayManager$ReminderDialog(String message, Object[] choices, int[] values, int defaultChoice, Bitmap bitmap, long style) {
      super(message, choices, values, defaultChoice, bitmap, style);
   }

   @Override
   public final void inHolster() {
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      Font normal = this.getFont();
      Font reduced = null;
      int curHeight = normal.getHeight();
      int maxHeight = Ui.convertSize(10, 3, 0);
      if (curHeight > maxHeight) {
         reduced = normal.derive(normal.getStyle(), maxHeight);
      }

      if (reduced != null) {
         normal = reduced;
      }

      this.setFont(normal);
   }
}
