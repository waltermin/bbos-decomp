package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.apps.api.addressbook.AddressBookOptions;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.verb.Verb;

class EmailComposeComboField$AddFreeFormVerb extends Verb {
   private final EmailComposeComboField this$0;

   public EmailComposeComboField$AddFreeFormVerb(EmailComposeComboField _1) {
      super(0);
      this.this$0 = _1;
   }

   @Override
   public Object invoke(Object parameter) {
      AddressBookOptions options = AddressBookServices.getAddressBookOptions();
      if (options != null) {
         options.setComposePreference((byte)1);
      }

      String translatedText = this.this$0._messageType.translate(this.this$0.getText());
      if (translatedText != null) {
         this.this$0.setText(translatedText);
         this.this$0.setAddressFromString(translatedText, true, false);
      }

      return null;
   }

   @Override
   public String toString() {
      String addCommand = this.this$0._messageType.getPrefixString();
      String translatedText = this.this$0._messageType.translate(this.this$0.getText());
      return addCommand + translatedText;
   }
}
