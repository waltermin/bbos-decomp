package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.wica.runtime.ui.internal.Focusable;

class WicletRadioButtonListField$WicletRadioButtonField extends RadioButtonField implements Focusable {
   WicletRadioButtonListField$WicletRadioButtonField(String label, RadioButtonGroup group, boolean selected) {
      super(label, group, selected, 0);
   }

   @Override
   public int moveFocus(int amount, int status, int time) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }
}
