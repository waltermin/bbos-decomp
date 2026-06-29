package net.rim.device.apps.internal.lbs;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.StringUtilities;

final class MapsOptionsScreen$MapsServiceRecord {
   private ServiceRecord _sr;
   private final MapsOptionsScreen this$0;

   private MapsOptionsScreen$MapsServiceRecord(MapsOptionsScreen this$0, ServiceRecord sr) {
      this.this$0 = this$0;
      this._sr = sr;
   }

   @Override
   public final String toString() {
      return this._sr.getName();
   }

   @Override
   public final boolean equals(Object object) {
      String name;
      String cid;
      if (object instanceof MapsOptionsScreen$MapsServiceRecord) {
         name = object.toString();
         cid = ((MapsOptionsScreen$MapsServiceRecord)object)._sr.getCid();
      } else {
         if (!(object instanceof Object)) {
            return super.equals(object);
         }

         name = ((ServiceRecord)object).getName();
         cid = ((ServiceRecord)object).getCid();
      }

      return StringUtilities.strEqualIgnoreCase(name, this.toString(), 1701707776) && StringUtilities.strEqualIgnoreCase(cid, this._sr.getCid(), 1701707776);
   }

   MapsOptionsScreen$MapsServiceRecord(MapsOptionsScreen x0, ServiceRecord x1, MapsOptionsScreen$1 x2) {
      this(x0, x1);
   }
}
