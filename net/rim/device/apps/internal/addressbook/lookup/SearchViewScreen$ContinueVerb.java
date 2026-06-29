package net.rim.device.apps.internal.addressbook.lookup;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

class SearchViewScreen$ContinueVerb extends Verb {
   private final SearchViewScreen this$0;

   SearchViewScreen$ContinueVerb(SearchViewScreen _1) {
      super(327936);
      this.this$0 = _1;
   }

   @Override
   public String toString() {
      return CommonResources.getString(800);
   }

   @Override
   public Object invoke(Object parameter) {
      this.this$0._manager.resolveRequestItem(this.this$0._request, SearchViewScreen.access$800(this.this$0));
      this.this$0.terminateScreen();
      return null;
   }
}
