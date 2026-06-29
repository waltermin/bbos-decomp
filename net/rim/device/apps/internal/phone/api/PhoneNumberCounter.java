package net.rim.device.apps.internal.phone.api;

import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;

final class PhoneNumberCounter implements Visitor {
   private int _count;

   public final int getCount() {
      return this._count;
   }

   public final void resetCount() {
      this._count = 0;
   }

   @Override
   public final boolean visit(Object o) {
      if (o instanceof AbstractPhoneNumberModel) {
         this._count++;
      }

      return true;
   }
}
