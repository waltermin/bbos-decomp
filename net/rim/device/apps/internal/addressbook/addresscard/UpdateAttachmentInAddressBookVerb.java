package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;

final class UpdateAttachmentInAddressBookVerb extends Verb {
   private Object _oldAddressCard;
   private AddressCardModel _newAddressCard;

   UpdateAttachmentInAddressBookVerb(Object oldAddressCard, AddressCardModel newAddressCard) {
      super(16867328, AddressBookResources.getResourceBundleFamily(), 1302);
      this._oldAddressCard = oldAddressCard;
      this._newAddressCard = newAddressCard;
   }

   @Override
   public final Object invoke(Object parameter) {
      AddressCardModel addressCard = AddressCardUtilities.expandGroup(this._newAddressCard);
      if (this._oldAddressCard instanceof Object) {
         AddressCardModel oldAddressCard = (AddressCardModel)this._oldAddressCard;
         addressCard.setUID(oldAddressCard.getUID());
      }

      AddressCardUtilities.updateAddressBookEntry(this._oldAddressCard, addressCard);
      return addressCard;
   }
}
