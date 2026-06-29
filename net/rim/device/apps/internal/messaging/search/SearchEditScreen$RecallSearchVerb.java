package net.rim.device.apps.internal.messaging.search;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.messaging.search.resources.SearchResources;

final class SearchEditScreen$RecallSearchVerb extends Verb {
   private final SearchEditScreen this$0;

   public SearchEditScreen$RecallSearchVerb(SearchEditScreen _1) {
      super(1131520);
      this.this$0 = _1;
   }

   @Override
   public final String toString() {
      return SearchResources.getString(12);
   }

   @Override
   public final Object invoke(Object ctx) {
      SearchList list = new SearchList(this.this$0._search, this.this$0._fromRibbon, this.this$0._search.getKeywordList());
      list.run();
      return null;
   }
}
