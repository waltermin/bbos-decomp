package net.rim.device.apps.internal.bluetooth;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.ui.ConnectionIcons;
import net.rim.device.internal.ui.IconCollection;

class ConnectionStateField extends Field {
   private BluetoothProfileManager _profileManager;
   private BluetoothDevice _device;
   private int _height;
   private static IconCollection _icons = ConnectionIcons.ICONS;

   ConnectionStateField(BluetoothProfileManager profileManager, BluetoothDevice device) {
      this._profileManager = profileManager;
      this._device = device;
   }

   @Override
   protected void layout(int width, int height) {
      Font font = this.getFont();
      int fontHeight = font.getHeight();
      int iconHeight = _icons.getHeight(font);
      this._height = Math.max(fontHeight, iconHeight);
      this.setExtent(width, this._height);
   }

   @Override
   protected void paint(Graphics graphics) {
      Font font = graphics.getFont();
      int icon = 0;
      switch (this._profileManager.getState(this._device)) {
         case -1:
            break;
         case 0:
         default:
            icon = 0;
            break;
         case 1:
            icon = 1;
            break;
         case 2:
            icon = 2;
      }

      int iconX = this.getContentWidth() - _icons.getWidth(font);
      int textY = Math.max(0, this._height - font.getHeight() >> 1);
      graphics.drawText(this._profileManager.getName(), 0, textY, 70, iconX);
      _icons.paint(graphics, iconX, 0, this._height, icon);
   }

   void repaint() {
      this.invalidate();
   }
}
