package net.rim.device.apps.internal.addressbook.ui;

import net.rim.device.apps.api.framework.verb.Verb;

final class AddressBookListUI$CustomActionVerb extends Verb {
   private Verb _verb;
   private final AddressBookListUI this$0;

   public AddressBookListUI$CustomActionVerb(AddressBookListUI _1, Verb verb) {
      super(verb);
      this.this$0 = _1;
      this._verb = verb;
   }

   @Override
   public final Object invoke(Object parameter) {
      this.this$0.doVerbAction(this._verb);
      return null;
   }

   @Override
   public final String toString() {
      return this._verb.toString();
   }
}
