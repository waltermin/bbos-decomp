package net.rim.device.apps.internal.lbs;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class DisplayMapVerb extends Verb {
   Location _location;

   public DisplayMapVerb(Location location) {
      super(1265702);
      this._location = location;
   }

   @Override
   public final String toString() {
      return LBSResources.getString(31);
   }

   @Override
   public final Object invoke(Object context) {
      LBSApplication.displayMap(this._location);
      return null;
   }
}
