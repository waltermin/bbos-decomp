package net.rim.device.apps.internal.lbs;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

final class LocationLabelComparator implements Comparator {
   final String getLabel(Object object) {
      if (!(object instanceof LocationSyncable)) {
         return (String)(object != null ? object : "");
      }

      LocationSyncable locationSyncObject = (LocationSyncable)object;
      return locationSyncObject.getLabel();
   }

   @Override
   public final int compare(Object object1, Object object2) {
      return StringUtilities.compareToIgnoreCase(this.getLabel(object1), this.getLabel(object2));
   }
}
