package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.apps.api.addressbook.AddressBookComboField$AddressBookController;

public class EmailComposeComboField$ComposeController extends AddressBookComboField$AddressBookController {
   private final EmailComposeComboField this$0;

   public EmailComposeComboField$ComposeController(EmailComposeComboField _1) {
      super(_1);
      this.this$0 = _1;
   }

   @Override
   protected void textChanged(String newText, int context) {
      if (!this.this$0._suspendUpdates) {
         this.this$0.setAddressFromString(newText, false, true);
         this.this$0._translatedText = this.this$0._messageType.translate(newText);
         if (newText.length() == 0) {
            this.this$0._userChangedSelection = false;
         }

         super.textChanged(newText, context);
      }
   }
}
