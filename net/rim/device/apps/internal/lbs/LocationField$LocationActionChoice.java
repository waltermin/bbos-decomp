package net.rim.device.apps.internal.lbs;

import net.rim.device.apps.internal.lbs.resources.LBSResources;

class LocationField$LocationActionChoice extends LocationField$LocationChoice {
   protected int _id;

   LocationField$LocationActionChoice(int id) {
      this._id = id;
   }

   @Override
   public String toString() {
      return ((StringBuffer)(new Object())).append(LBSResources.getString(this._id)).append("...").toString();
   }

   @Override
   public void onSelect() {
   }
}
