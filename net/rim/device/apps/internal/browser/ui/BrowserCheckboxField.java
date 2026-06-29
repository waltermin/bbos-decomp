package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.component.CheckboxField;

public class BrowserCheckboxField extends CheckboxField {
   public BrowserCheckboxField(String label, boolean checked) {
      this(label, checked, 0);
   }

   public BrowserCheckboxField(String label, boolean checked, long style) {
      super(label, checked, style | 1073741824);
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      return (status & 1) == 0 ? super.trackwheelClick(status, time) : false;
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (this.isEditable() && key == '\n') {
         this.keyChar(' ', 0, 0);
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }
}
