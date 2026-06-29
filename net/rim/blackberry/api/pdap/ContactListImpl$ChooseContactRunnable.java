package net.rim.blackberry.api.pdap;

import net.rim.device.apps.api.addressbook.AddressSelectionContext;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;

class ContactListImpl$ChooseContactRunnable implements Runnable {
   RIMModel _chooseContactModel;
   private Verb _invokeAddressBookVerb;
   private AddressSelectionContext _selectionContext;

   ContactListImpl$ChooseContactRunnable(Verb invokeAddressBookVerb, AddressSelectionContext selectionContext) {
      this._invokeAddressBookVerb = invokeAddressBookVerb;
      this._selectionContext = selectionContext;
   }

   @Override
   public void run() {
      this._chooseContactModel = (RIMModel)this._invokeAddressBookVerb.invoke(this._selectionContext);
   }
}
