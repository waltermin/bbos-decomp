package net.rim.device.apps.internal.messaging.search;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class SearchList$CancelListVerb extends Verb {
   private final SearchList this$0;

   SearchList$CancelListVerb(SearchList _1) {
      super(268500992);
      this.this$0 = _1;
   }

   @Override
   public final String toString() {
      return CommonResource.getString(19);
   }

   @Override
   public final Object invoke(Object context) {
      this.this$0.prologue();
      return null;
   }
}
