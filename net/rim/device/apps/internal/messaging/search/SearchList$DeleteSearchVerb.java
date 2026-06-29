package net.rim.device.apps.internal.messaging.search;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.messaging.search.resources.SearchResources;

class SearchList$DeleteSearchVerb extends Verb {
   int _index;
   RIMModel _model;
   private final SearchList this$0;

   @Override
   public String toString() {
      return SearchResources.getString(5);
   }

   SearchList$DeleteSearchVerb(SearchList _1, int index, RIMModel model) {
      super(611920);
      this.this$0 = _1;
      this._index = index;
      this._model = model;
   }

   @Override
   public Object invoke(Object context) {
      if (this._index >= 0) {
         int retVal = Dialog.ask(2, SearchResources.getString(40), 3);
         if (retVal == 3) {
            SearchList.access$000(this.this$0).delete(this._index);
            FilterModel fm = (FilterModel)this._model;
            this.this$0._search.returnHotKey(fm.getShortCutKey(), true);
            this.this$0._search.getCollection().remove(this._model);
         }
      }

      return null;
   }
}
