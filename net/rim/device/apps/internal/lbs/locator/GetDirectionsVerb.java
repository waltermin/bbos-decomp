package net.rim.device.apps.internal.lbs.locator;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.lbs.LBSApplication;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class GetDirectionsVerb extends Verb {
   Directions _directions;

   public GetDirectionsVerb(Directions directions) {
      super(1265702);
      this._directions = directions;
   }

   @Override
   public final String toString() {
      return LBSResources.getString(31);
   }

   @Override
   public final Object invoke(Object context) {
      String routeXML = this._directions.getDirections();
      if (routeXML != null && routeXML.length() > 0) {
         LBSApplication.openDocument("text/vnd.rim.location", this._directions._rawXMLData);
      }

      return null;
   }
}
