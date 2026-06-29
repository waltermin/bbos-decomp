package net.rim.device.apps.api.addressbook;

import net.rim.device.api.ui.component.ComboFieldController;

public class AddressBookComboField$AddressBookController extends ComboFieldController {
   private final AddressBookComboField this$0;

   public AddressBookComboField$AddressBookController(AddressBookComboField _1) {
      this.this$0 = _1;
   }

   @Override
   protected void textChanged(String newText, int context) {
      if ((context & -2147483648) == 0) {
         this.this$0.initiateSearch(newText);
      }
   }

   @Override
   protected void select(Object selection, int type) {
      this.this$0.addressSelected(selection, type);
   }

   @Override
   protected void escape() {
      this.this$0.setText("");
      this.this$0._stVariants.update("");
      super._comboField.hideDropList();
   }
}
