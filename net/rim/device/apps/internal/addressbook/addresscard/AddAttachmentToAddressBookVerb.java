package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;

final class AddAttachmentToAddressBookVerb extends Verb {
   private AddressCardModel _addressCard;

   AddAttachmentToAddressBookVerb(AddressCardModel addressCard) {
      super(16867328, AddressBookResources.getResourceBundleFamily(), 1300);
      this._addressCard = addressCard;
   }

   @Override
   public final Object invoke(Object parameter) {
      AddressCardModel addressCard = AddressCardUtilities.expandGroup(this._addressCard);
      addressCard = AddressCardModelFactory.compressCard(addressCard);
      AddressCardUtilities.addToAddressBook(addressCard);
      return addressCard;
   }
}
