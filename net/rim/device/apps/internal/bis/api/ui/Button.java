package net.rim.device.apps.internal.bis.api.ui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.ButtonField;

public final class Button extends ButtonField {
   private static final int BUTTON_FONT_SIZE_PT;

   public Button(String label) {
      super(12884967424L);
      this.setLabel(label);
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      this.setFont(Font.getDefault().derive(1, 5, 3));
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      super.trackwheelClick(status, time);
      return true;
   }
}
