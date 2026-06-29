package net.rim.device.apps.internal.messaging.search;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

final class SearchEditScreen$ExecuteSearchVerb extends Verb {
   private final SearchEditScreen this$0;

   public SearchEditScreen$ExecuteSearchVerb(SearchEditScreen _1) {
      super(1131008);
      this.this$0 = _1;
   }

   @Override
   public final String toString() {
      return CommonResources.getString(9136);
   }

   @Override
   public final Object invoke(Object ctx) {
      ReadableList newModel = this.this$0.copyOfChanges();
      if (!this.this$0.isDirty()) {
         ((FilterModel)newModel)._titleModel = ((FilterModel)this.this$0.getModel())._titleModel;
      } else {
         ((FilterModel)newModel)._titleModel = null;
      }

      UiApplication.getUiApplication().popScreen(this.this$0);
      ((FilterModel)newModel).performSearch(this.this$0._search, false, this.this$0._fromRibbon, ctx);
      return null;
   }
}
