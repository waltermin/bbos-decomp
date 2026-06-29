package net.rim.device.apps.api.search.criteria;

import net.rim.device.apps.api.search.SearchCriterion;

public class IdSearchModel implements SearchCriterion {
   private Integer _id;

   public void setValue(int id) {
      this._id = new Integer(id);
   }

   @Override
   public int getType() {
      return 24;
   }

   @Override
   public Object getValue() {
      return this._id;
   }
}
