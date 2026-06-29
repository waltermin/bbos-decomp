package net.rim.device.apps.internal.messaging.search;

import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.messaging.search.resources.SearchResources;

class SearchList$EditSearchVerb extends Verb {
   RIMModel _model;
   private final SearchList this$0;

   @Override
   public String toString() {
      return SearchResources.getString(15);
   }

   SearchList$EditSearchVerb(SearchList _1, RIMModel model) {
      super(611408);
      this.this$0 = _1;
      this._model = model;
   }

   @Override
   public Object invoke(Object context) {
      if (this._model != null) {
         SearchSaveScreen temp = this.this$0._search.getSearchSaveScreen(this._model, this.this$0._fromRibbon);
         if (temp != null) {
            temp.run();
         }
      }

      return null;
   }
}
