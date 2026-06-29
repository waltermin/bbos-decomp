package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;

public class BrowserRadioButtonField extends RadioButtonField {
   public BrowserRadioButtonField(String label) {
   }

   public BrowserRadioButtonField(String label, RadioButtonGroup group, boolean selected) {
   }

   public BrowserRadioButtonField(String label, RadioButtonGroup group, boolean selected, long style) {
      super(label, group, selected, style);
   }

   @Override
   protected boolean navigationClick(int status, int time) {
      return (status & 1073741824) != 0 && (status & 1) != 0 ? false : this.click();
   }

   @Override
   protected boolean navigationUnclick(int status, int time) {
      return this.isEditable();
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      return (status & 1) == 0 ? this.click() : false;
   }

   private boolean click() {
      if (this.isEditable()) {
         this.keyChar(' ', 0, 0);
         return true;
      } else {
         return false;
      }
   }
}
