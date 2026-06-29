package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.api.ui.TextChangeListener;
import net.rim.tid.awt.event.InputMethodEvent;

final class PersonalNameField$1 implements TextChangeListener {
   private final PersonalNameField this$0;

   PersonalNameField$1(PersonalNameField _1) {
      this.this$0 = _1;
   }

   @Override
   public final void inputMethodTextChanged(Object source, InputMethodEvent event) {
      this.this$0.handleYomiFieldTextChange(source);
   }

   @Override
   public final void textValueChanged(Object source, int eventID) {
      this.this$0.handleYomiFieldTextChange(source);
   }
}
