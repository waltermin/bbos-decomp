package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

final class ViewCalendarPopupScreen$ColorSwatchField extends Field {
   int _height;
   private int _color;

   public ViewCalendarPopupScreen$ColorSwatchField(int height, int color) {
      super(51539607552L);
      this._height = height;
      this._color = color;
   }

   @Override
   public final int getPreferredWidth() {
      return this._height;
   }

   @Override
   public final int getPreferredHeight() {
      return this._height;
   }

   @Override
   protected final void layout(int width, int height) {
      this.setExtent(this.getPreferredWidth(), this.getPreferredHeight());
   }

   @Override
   protected final void paint(Graphics graphics) {
      if (this._color != -1) {
         graphics.setColor(this._color);
         graphics.fillRect(0, 0, this._height, this._height);
         graphics.setColor(0);
         graphics.drawRect(0, 0, this._height, this._height);
      }
   }
}
