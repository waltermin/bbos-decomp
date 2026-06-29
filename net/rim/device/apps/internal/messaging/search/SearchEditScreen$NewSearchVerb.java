package net.rim.device.apps.internal.messaging.search;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class SearchEditScreen$NewSearchVerb extends Verb {
   private final SearchEditScreen this$0;

   public SearchEditScreen$NewSearchVerb(SearchEditScreen _1) {
      super(1130752);
      this.this$0 = _1;
   }

   @Override
   public final String toString() {
      return CommonResource.getString(13);
   }

   @Override
   public final Object invoke(Object ctx) {
      this.this$0.setModel(SearchEditScreen.getDefaultSearch());
      return null;
   }
}
