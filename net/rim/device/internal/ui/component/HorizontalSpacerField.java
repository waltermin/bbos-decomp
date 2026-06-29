package net.rim.device.internal.ui.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

public class HorizontalSpacerField extends Field {
   private int _width;

   public HorizontalSpacerField(int width) {
      super(36028797018963968L);
      this._width = width;
   }

   @Override
   protected void layout(int width, int height) {
      this.setExtent(this._width, 0);
   }

   @Override
   protected void paint(Graphics g) {
   }
}
