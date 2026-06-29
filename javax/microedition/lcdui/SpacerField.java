package javax.microedition.lcdui;

import net.rim.device.api.ui.Field;

class SpacerField extends Field {
   private int _width;
   private int _height;

   SpacerField(int width, int height) {
      super(3458764513820540928L);
      this.setSize(width, height);
   }

   void setSize(int width, int height) {
      this._width = width;
      this._height = height;
      this.invalidate();
   }

   @Override
   protected void layout(int width, int height) {
      this.setExtent(this._width, this._height);
   }

   @Override
   public int getPreferredWidth() {
      return this._width;
   }

   @Override
   public int getPreferredHeight() {
      return this._height;
   }

   @Override
   public boolean isFocusable() {
      return false;
   }

   @Override
   protected void paint(net.rim.device.api.ui.Graphics graphics) {
   }
}
