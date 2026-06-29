package net.rim.device.apps.internal.messaging.search;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

class SearchSaveScreen$SaveVerb extends Verb {
   private final SearchSaveScreen this$0;

   public SearchSaveScreen$SaveVerb(SearchSaveScreen _1) {
      super(1131264);
      this.this$0 = _1;
   }

   @Override
   public String toString() {
      return CommonResource.getString(18);
   }

   @Override
   public Object invoke(Object context) {
      SearchSaveScreen.access$000(this.this$0);
      return null;
   }
}
