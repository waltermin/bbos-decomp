package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

class StatusField$BlankField extends Field {
   private int _width;
   private int _height;

   public StatusField$BlankField(int width, int height) {
      super(36028797018963968L);
      this._width = width;
      this._height = height;
   }

   @Override
   protected void layout(int width, int height) {
      width = Math.min(width, this._width);
      height = Math.min(height, this._height);
      this.setExtent(width, height);
   }

   @Override
   protected void paint(Graphics g) {
   }
}
