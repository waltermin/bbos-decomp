package net.rim.device.internal.ui.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

public class VerticalSpacerField extends Field {
   private int _height;

   public VerticalSpacerField(int height) {
      super(36028797018963968L);
      this._height = height;
   }

   @Override
   protected void layout(int width, int height) {
      this.setExtent(0, this._height);
   }

   @Override
   protected void paint(Graphics g) {
   }
}
