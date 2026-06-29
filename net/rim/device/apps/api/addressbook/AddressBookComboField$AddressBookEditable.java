package net.rim.device.apps.api.addressbook;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.BasicEditField;

public class AddressBookComboField$AddressBookEditable extends BasicEditField {
   private final AddressBookComboField this$0;

   public AddressBookComboField$AddressBookEditable(AddressBookComboField _1, String label) {
      this(_1, label, 255);
   }

   public AddressBookComboField$AddressBookEditable(AddressBookComboField _1, String label, int maxNumChars) {
      super(label, "", maxNumChars, 2684371840L);
      this.this$0 = _1;
   }

   @Override
   public void focusChangeNotify(int action) {
      super.focusChangeNotify(action);
      if (action == 1) {
         this.this$0._hasFocus = true;
         this.this$0.editFocusGained();
      } else {
         if (action == 3) {
            this.this$0._hasFocus = false;
            this.this$0.editFocusLost();
         }
      }
   }

   @Override
   public int processKeyEvent(int event, char key, int keycode, int time) {
      if (event == 513 && Keypad.key(keycode) == 10) {
         this.this$0.editEnter();
      }

      return super.processKeyEvent(event, key, keycode, time);
   }
}
