package net.rim.device.apps.api.setupwizard;

import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.NullField;

class ScrollingNullField extends NullField {
   @Override
   public void getFocusRect(XYRect rect) {
      rect.set(0, this.getManager().getVerticalScroll(), 0, 0);
   }
}
