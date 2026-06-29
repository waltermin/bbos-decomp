package net.rim.device.apps.internal.messaging.search;

import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.search.MessageSearch;

final class SearchVerb extends Verb implements EncryptableProvider {
   private RIMModel _model;

   @Override
   public final String toString() {
      return null;
   }

   SearchVerb(RIMModel m) {
      super(0);
      this._model = m;
   }

   @Override
   public final Object invoke(Object context) {
      FilterModel fm = (FilterModel)this._model;
      return fm.performSearch((MessageSearchImpl)MessageSearch.getInstance(), false, false, context);
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return !(this._model instanceof EncryptableProvider) ? true : ((EncryptableProvider)this._model).checkCrypt(compress, encrypt);
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      if (this._model instanceof EncryptableProvider) {
         Object newModel = ((EncryptableProvider)this._model).reCrypt(compress, encrypt);
         if (newModel != null) {
            this._model = (RIMModel)newModel;
         }
      }

      return null;
   }
}
