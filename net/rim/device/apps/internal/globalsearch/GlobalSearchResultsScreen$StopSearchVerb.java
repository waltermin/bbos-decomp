package net.rim.device.apps.internal.globalsearch;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

final class GlobalSearchResultsScreen$StopSearchVerb extends Verb {
   private final GlobalSearchResultsScreen this$0;

   public GlobalSearchResultsScreen$StopSearchVerb(GlobalSearchResultsScreen _1) {
      super(268500992);
      this.this$0 = _1;
   }

   @Override
   public final String toString() {
      return CommonResources.getString(9142);
   }

   @Override
   public final Object invoke(Object parameter) {
      this.this$0.stopSearch();
      return null;
   }
}
