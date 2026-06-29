package net.rim.device.apps.internal.explorer.Media;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

final class HorizontalSpacerField extends Field {
   public HorizontalSpacerField() {
      super(0);
   }

   @Override
   public final int getPreferredHeight() {
      return 0;
   }

   @Override
   public final int getPreferredWidth() {
      return 15;
   }

   @Override
   protected final void layout(int width, int height) {
      this.setExtent(Math.min(width, this.getPreferredWidth()), 0);
   }

   @Override
   protected final void paint(Graphics graphics) {
   }
}
