package net.rim.device.apps.internal.phone.data;

import net.rim.device.apps.internal.phone.api.Visitor;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;

final class PhoneNumberFinder implements Visitor {
   private Object _searchPhoneNumber;
   private Object _matchingPhoneNumber;

   final void reset(Object phoneNumber) {
      this._searchPhoneNumber = phoneNumber;
      this._matchingPhoneNumber = null;
   }

   final Object getMatchingPhoneNumber() {
      return this._matchingPhoneNumber;
   }

   @Override
   public final boolean visit(Object o) {
      if (this._searchPhoneNumber == null) {
         return false;
      } else if (o instanceof Object && ((AbstractPhoneNumberModel)o).equals(this._searchPhoneNumber, true)) {
         this._matchingPhoneNumber = o;
         return false;
      } else {
         return true;
      }
   }
}
