package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.lookup.Request;

final class UpdateAddressCardVerb extends Verb {
   private Request _request;
   protected AddressCardModel _model;

   UpdateAddressCardVerb(AddressCardModel model) {
      super(0);
      this._model = model;
   }

   UpdateAddressCardVerb(AddressCardModel model, Request request) {
      this(model);
      this._request = request;
   }

   @Override
   public final Object invoke(Object newModel) {
      if (!(newModel instanceof Object)) {
         throw new Object();
      }

      if (this._request == null) {
         AddressCardUtilities.updateAddressBookEntry(this._model, newModel);
         return newModel;
      }

      boolean resolveToNewAddress = false;
      if (this._request.getSelectedAddress() == this._model) {
         resolveToNewAddress = true;
      }

      this._request.getResult().addItem(newModel);
      this._request.getResult().deleteItem(this._model);
      if (resolveToNewAddress) {
         this._request.setResolved(newModel);
      }

      return newModel;
   }
}
