package net.rim.device.apps.internal.globalsearch;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

final class GlobalSearchScreen$SearchVerb extends Verb {
   private final GlobalSearchScreen this$0;

   public GlobalSearchScreen$SearchVerb(GlobalSearchScreen _1) {
      super(1131008);
      this.this$0 = _1;
   }

   @Override
   public final String toString() {
      return CommonResources.getString(9136);
   }

   @Override
   public final Object invoke(Object parameter) {
      this.this$0.performSearch();
      return null;
   }
}
