package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;

final class SpeedDialCallerIDField extends Manager {
   SpeedDialCallerIDField(Field cidiField, char speedDialKey) {
      super(0);
      this.add(cidiField);
      this.add(new SpeedDialKeyField(speedDialKey));
   }

   @Override
   protected final void sublayout(int width, int height) {
      Field callerIDField = this.getField(0);
      Field sdKeyField = this.getField(1);
      this.layoutChild(sdKeyField, width, height);
      int sdKeyFieldWidth = sdKeyField.getWidth();
      this.setPositionChild(sdKeyField, width - sdKeyFieldWidth, 0);
      this.setPositionChild(callerIDField, 0, 0);
      this.layoutChild(callerIDField, width - sdKeyFieldWidth, height);
      this.setExtent(width, callerIDField.getHeight());
   }
}
