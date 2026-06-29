package net.rim.device.apps.internal.standardcalculator;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;

final class CenteringFieldManager extends Manager {
   CenteringFieldManager(Field field) {
      super(0);
      this.add(field);
   }

   @Override
   protected final void sublayout(int width, int height) {
      Field field = this.getField(0);
      this.layoutChild(field, Display.getWidth(), Display.getHeight());
      this.setPositionChild(field, width - field.getWidth() >> 1, height - field.getHeight() >> 1);
      this.setExtent(width, height);
   }
}
