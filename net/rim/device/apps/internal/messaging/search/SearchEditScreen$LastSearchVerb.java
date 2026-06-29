package net.rim.device.apps.internal.messaging.search;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.messaging.search.resources.SearchResources;

final class SearchEditScreen$LastSearchVerb extends Verb {
   private final SearchEditScreen this$0;

   public SearchEditScreen$LastSearchVerb(SearchEditScreen _1) {
      super(1131776);
      this.this$0 = _1;
   }

   @Override
   public final String toString() {
      return SearchResources.getString(50);
   }

   @Override
   public final Object invoke(Object ctx) {
      FilterModel fm = ((SearchCollection)this.this$0._search.getCollection()).getLastFilter();
      this.this$0.setModel(fm);
      return fm;
   }
}
