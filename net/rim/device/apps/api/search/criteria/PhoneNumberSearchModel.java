package net.rim.device.apps.api.search.criteria;

import net.rim.device.apps.api.search.SearchCriterion;

public class PhoneNumberSearchModel implements SearchCriterion {
   private String _queryString;

   public boolean setValue(String text) {
      this._queryString = text;
      return true;
   }

   @Override
   public int getType() {
      return 29;
   }

   @Override
   public Object getValue() {
      return this._queryString;
   }
}
