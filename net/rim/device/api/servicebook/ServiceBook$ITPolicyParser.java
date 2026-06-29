package net.rim.device.api.servicebook;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.TLEFieldController;
import net.rim.device.api.util.TLEUtilities;

final class ServiceBook$ITPolicyParser implements TLEFieldController {
   private ServiceBook$SBRestriction[] _restrictions = new ServiceBook$SBRestriction[0];
   private int _version = 32;

   public ServiceBook$ITPolicyParser() {
   }

   public final void setVersion(int v) {
      this._version = v;
   }

   public final ServiceBook$SBRestriction[] getRestrictions() {
      if ((this._version & 240) == 16 && this._restrictions.length != 0) {
         Arrays.insertAt(this._restrictions, new ServiceBook$SBRestriction(2), 0);
      }

      return this._restrictions;
   }

   @Override
   public final boolean processField(int type, int length, DataBuffer db) {
      ServiceBook$SBRestriction r;
      if (type == 1) {
         r = new ServiceBook$SBRestriction(1);
      } else {
         if (type != 2) {
            return false;
         }

         r = new ServiceBook$SBRestriction(2);
      }

      TLEUtilities.parseField(db, r, length);
      Arrays.add(this._restrictions, r);
      return true;
   }

   @Override
   public final void dumpField(int type, DataBuffer db) {
   }
}
