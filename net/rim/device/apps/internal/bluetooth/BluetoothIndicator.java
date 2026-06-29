package net.rim.device.apps.internal.bluetooth;

import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.ribbon.indicators.Indicator;
import net.rim.device.apps.api.ribbon.indicators.TestPoint;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.ui.IconCollection;

final class BluetoothIndicator implements Indicator, TestPoint {
   private IconCollection _icons = IconCollection.get("net_rim_bluetooth_banner", 1);
   private BluetoothDeviceManagerImpl _btManager;
   private static final int INDEX_ON = 0;
   private static final int INDEX_CONNECTED = 1;

   BluetoothIndicator(BluetoothDeviceManagerImpl btManager) {
      this._btManager = btManager;
   }

   @Override
   public final int draw(Graphics graphics, int width, int height, int flags) {
      int drawWidth = 0;
      int iconWidth = this._icons.getWidth(graphics.getFont());
      int xpos = 0;
      if ((flags & 1) == 0) {
         xpos = width - iconWidth;
      }

      int index = 0;
      if (this._btManager.isConnected()) {
         index = 1;
      }

      return this._icons.paint(graphics, xpos, 0, height, index);
   }

   @Override
   public final int getWidth(Graphics graphics) {
      return BluetoothME.isPowerOn() ? this._icons.getWidth(graphics.getFont()) : 0;
   }

   @Override
   public final int getHeight(Graphics graphics) {
      return this._icons.getHeight(graphics.getFont());
   }

   @Override
   public final void test(Object id, Object testvalue) {
   }

   @Override
   public final int getPriority() {
      return 3;
   }

   @Override
   public final String getTypeName() {
      return "bluetooth";
   }
}
