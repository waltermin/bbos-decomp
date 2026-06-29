package net.rim.device.apps.internal.addressbook.lookup;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class SearchViewScreen$CancelScreenVerb extends Verb {
   private final SearchViewScreen this$0;

   private SearchViewScreen$CancelScreenVerb(SearchViewScreen _1) {
      super(268500992);
      this.this$0 = _1;
   }

   @Override
   public final String toString() {
      return CommonResource.getString(19);
   }

   @Override
   public final Object invoke(Object parameter) {
      this.this$0.terminateScreen();
      return null;
   }

   SearchViewScreen$CancelScreenVerb(SearchViewScreen x0, SearchViewScreen$1 x1) {
      this(x0);
   }
}
