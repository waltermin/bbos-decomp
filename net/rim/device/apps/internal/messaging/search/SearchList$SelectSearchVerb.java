package net.rim.device.apps.internal.messaging.search;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.editor.EditorUsingRIMModelFactory;
import net.rim.device.apps.internal.messaging.search.resources.SearchResources;

class SearchList$SelectSearchVerb extends Verb {
   RIMModel _model;
   private final SearchList this$0;

   @Override
   public String toString() {
      return SearchResources.getString(6);
   }

   SearchList$SelectSearchVerb(SearchList _1, RIMModel model) {
      super(611408);
      this.this$0 = _1;
      this._model = model;
   }

   @Override
   public Object invoke(Object context) {
      if (this._model != null) {
         this.this$0.prologue();
         UiApplication app = UiApplication.getUiApplication();
         app.popScreen(app.getActiveScreen());
         this._model = FilterModel.expandGroup(this._model);
         EditorUsingRIMModelFactory editor = this.this$0._search.getSearchEditScreen(this._model, this.this$0._fromRibbon);
         if (editor != null) {
            editor.go(false);
         }
      }

      return null;
   }
}
