package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.wica.runtime.ui.internal.Focusable;

class WicletCheckboxListField$WicletCheckboxField extends CheckboxField implements Focusable {
   WicletCheckboxListField$WicletCheckboxField(String value, boolean isSelected) {
      super(value, isSelected, 1152921504606846976L);
   }

   @Override
   public int moveFocus(int amount, int status, int time) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public void getFocusRect(XYRect rect) {
      super.getFocusRect(rect);
      rect.y = 0;
   }
}
