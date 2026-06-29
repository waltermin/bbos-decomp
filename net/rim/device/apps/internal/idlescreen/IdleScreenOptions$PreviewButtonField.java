package net.rim.device.apps.internal.idlescreen;

import net.rim.device.api.ui.component.ButtonField;

final class IdleScreenOptions$PreviewButtonField extends ButtonField {
   IdleScreenOptions$PreviewButtonField() {
      super(IdleScreenOptions._rb.getString(3), 12884901888L);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         IdleScreenApplication.show(true);
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      IdleScreenApplication.show(true);
      return true;
   }
}
