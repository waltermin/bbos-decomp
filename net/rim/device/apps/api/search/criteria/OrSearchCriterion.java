package net.rim.device.apps.api.search.criteria;

import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.search.SearchCriterion;

public final class OrSearchCriterion implements SearchCriterion, EncryptableProvider {
   private Object[] _criteria;

   public final boolean setValue(Object[] criteria) {
      if (criteria != null && criteria.length < 1) {
         this._criteria = null;
         return false;
      } else {
         this._criteria = criteria;
         return true;
      }
   }

   @Override
   public final int getType() {
      return 23;
   }

   @Override
   public final Object getValue() {
      return this._criteria;
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      for (int i = this._criteria.length - 1; i >= 0; i--) {
         if (this._criteria[i] instanceof Object && !((EncryptableProvider)this._criteria[i]).checkCrypt(true, true)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      for (int i = this._criteria.length - 1; i >= 0; i--) {
         Object var10000 = this._criteria[i];
         if (this._criteria[i] instanceof Object) {
            ((EncryptableProvider)var10000).reCrypt(true, true);
         }
      }

      return this;
   }
}
