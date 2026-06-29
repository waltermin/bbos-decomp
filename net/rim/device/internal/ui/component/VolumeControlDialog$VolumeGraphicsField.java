package net.rim.device.internal.ui.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.internal.ui.IconCollection;

class VolumeControlDialog$VolumeGraphicsField extends Field {
   private int _index;
   private IconCollection _icons = IconCollection.get("net_rim_Volume", 11);
   private static final int NUM_ICONS = 11;
   private static final int DEFAULT_WIDTH = 30;
   private static final int DEFAULT_HEIGHT = 30;

   VolumeControlDialog$VolumeGraphicsField() {
      this._index = 0;
   }

   public void changeIndex(int index) {
      if (this._index != index) {
         this._index = index;
         this.invalidate();
      }
   }

   @Override
   protected void layout(int width, int height) {
      this.setExtent(this.getIconWidth(), this.getIconHeight());
   }

   private int getIconWidth() {
      return this._icons.getWidth(30, 30);
   }

   private int getIconHeight() {
      return this._icons.getHeight(30, 30);
   }

   @Override
   public void paint(Graphics graphics) {
      this._icons.paint(graphics, 0, 0, this.getIconWidth(), this.getIconHeight(), 0, this._index);
   }
}
