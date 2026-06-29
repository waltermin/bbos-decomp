package net.rim.device.apps.internal.addressbook.ui;

import net.rim.device.apps.api.ui.ExitVerb;

final class AddressBookListUI$CloseApplicationVerb extends ExitVerb {
   private final AddressBookListUI this$0;

   AddressBookListUI$CloseApplicationVerb(AddressBookListUI _1) {
      super(4, null);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object parameter) {
      this.this$0.terminateScreen();
      return null;
   }
}
