package net.rim.device.apps.internal.explorer.Media;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

final class VerticalSpacerField extends Field {
   public VerticalSpacerField() {
      super(0);
   }

   @Override
   public final int getPreferredHeight() {
      return this.getFont().getHeight() >> 1;
   }

   @Override
   protected final void layout(int width, int height) {
      this.setExtent(width, Math.min(height, this.getPreferredHeight()));
   }

   @Override
   protected final void paint(Graphics graphics) {
   }
}
