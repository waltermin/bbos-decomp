package net.rim.device.apps.internal.phone.data;

import net.rim.device.apps.internal.phone.api.Visitor;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;

final class MatchingWorkNumberFinder implements Visitor {
   private Object _matchingWorkNumber;
   private Object _phoneNumber;

   final void reset(Object phoneNumber) {
      this._phoneNumber = phoneNumber;
      this._matchingWorkNumber = null;
   }

   final Object getMatchingWorkNumber() {
      return this._matchingWorkNumber;
   }

   @Override
   public final boolean visit(Object o) {
      if (this._phoneNumber == null) {
         return false;
      }

      if (o instanceof AbstractPhoneNumberModel) {
         AbstractPhoneNumberModel apnm = (AbstractPhoneNumberModel)o;
         int type = apnm.getType();
         if ((type == 1 || type == 2) && apnm.equals(this._phoneNumber, true)) {
            this._matchingWorkNumber = apnm;
            return false;
         }
      }

      return true;
   }
}
