package net.rim.device.apps.internal.addressbook.ui;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;

final class AddressBookListUI$NewAddressBookEntryVerb extends Verb {
   private Verb _innerVerb;
   private final AddressBookListUI this$0;

   AddressBookListUI$NewAddressBookEntryVerb(AddressBookListUI _1, Verb innerVerb) {
      super(innerVerb);
      this.this$0 = _1;
      this._innerVerb = innerVerb;
   }

   @Override
   public final Object invoke(Object parameter) {
      AddressBookListUI$AddToAddressBookStoreAction storeAction = new AddressBookListUI$AddToAddressBookStoreAction();
      ContextObject contextObject = ContextObject.clone(parameter);
      contextObject.put(7760782369919591658L, storeAction);
      this._innerVerb.invoke(contextObject);
      this.this$0.optionsUpdated(false);
      return storeAction.getStoredObject();
   }

   @Override
   public final String toString() {
      return this._innerVerb.toString();
   }
}
