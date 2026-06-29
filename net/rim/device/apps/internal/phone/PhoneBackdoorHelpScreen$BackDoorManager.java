package net.rim.device.apps.internal.phone;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.VerticalFieldManager;

final class PhoneBackdoorHelpScreen$BackDoorManager extends VerticalFieldManager implements ListFieldCallback {
   private static final long MANAGER_FLAGS = 3459063580983296000L;

   PhoneBackdoorHelpScreen$BackDoorManager() {
      super(3459063580983296000L);
      ListField _listField = new ListField();
      _listField.setCallback(this);
      this.add(_listField);
      _listField.setSize(PhoneBackdoorHelpScreen._backdoorStrings.length);
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      graphics.drawText(PhoneBackdoorHelpScreen._backdoorStrings[index], 0, y);
   }

   @Override
   public final Object get(ListField listField, int index) {
      return "";
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return 10;
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return 0;
   }
}
