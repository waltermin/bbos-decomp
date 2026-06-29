package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.apps.api.addressbook.AddressBookOptions;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;

class EmailComposeComboField$LookupVerb extends Verb {
   private final EmailComposeComboField this$0;

   public EmailComposeComboField$LookupVerb(EmailComposeComboField _1) {
      super(413952);
      this.this$0 = _1;
   }

   @Override
   public Object invoke(Object parameter) {
      AddressBookOptions options = AddressBookServices.getAddressBookOptions();
      if (options != null) {
         options.setComposePreference((byte)2);
      }

      this.this$0.createLookupModel();
      return null;
   }

   @Override
   public String toString() {
      String lookupCommand = AddressBookResources.getString(1717);
      return ((StringBuffer)(new Object())).append(lookupCommand).append(" ").append(this.this$0.convertToKeywordString(this.this$0.getText())).toString();
   }

   @Override
   public String toString(Object context) {
      return this.toString();
   }
}
