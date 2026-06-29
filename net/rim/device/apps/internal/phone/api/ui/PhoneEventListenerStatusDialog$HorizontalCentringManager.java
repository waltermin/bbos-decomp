package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;

final class PhoneEventListenerStatusDialog$HorizontalCentringManager extends Manager {
   PhoneEventListenerStatusDialog$HorizontalCentringManager() {
      super(1152921504606846976L);
   }

   @Override
   protected final void sublayout(int width, int height) {
      Field field = this.getField(0);
      this.layoutChild(field, width, height);
      int wid = field.getWidth();
      int ht = field.getHeight();
      this.setPositionChild(field, (width >> 1) - (wid >> 1), 0);
      this.setExtent(width, ht + 3);
   }
}
