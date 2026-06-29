package net.rim.device.apps.internal.globalsearch;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

final class GlobalSearchResultsScreen$BackVerb extends Verb {
   private final GlobalSearchResultsScreen this$0;

   public GlobalSearchResultsScreen$BackVerb(GlobalSearchResultsScreen _1) {
      super(268500993);
      this.this$0 = _1;
   }

   @Override
   public final String toString() {
      return CommonResources.getString(9033);
   }

   @Override
   public final Object invoke(Object parameter) {
      this.this$0.cleanupSearches(true);
      GlobalSearchResultsScreen.access$201(this.this$0);
      return null;
   }
}
