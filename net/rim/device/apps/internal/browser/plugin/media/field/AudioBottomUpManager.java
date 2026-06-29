package net.rim.device.apps.internal.browser.plugin.media.field;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;

final class AudioBottomUpManager extends Manager {
   AudioBottomUpManager() {
      super(0);
   }

   @Override
   protected final void sublayout(int width, int height) {
      int availableHeight = height;

      for (int i = this.getFieldCount() - 1; i >= 0; i--) {
         Field field = this.getField(i);
         this.layoutChild(field, width, availableHeight);
         availableHeight -= field.getHeight();
         int xCoord = width - field.getWidth() >> 1;
         this.setPositionChild(field, xCoord, availableHeight);
      }

      this.setExtent(width, height);
      this.setVirtualExtent(width, height);
   }
}
