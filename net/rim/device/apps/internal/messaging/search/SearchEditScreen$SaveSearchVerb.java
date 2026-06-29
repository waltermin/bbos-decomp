package net.rim.device.apps.internal.messaging.search;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class SearchEditScreen$SaveSearchVerb extends Verb {
   private final SearchEditScreen this$0;

   public SearchEditScreen$SaveSearchVerb(SearchEditScreen _1) {
      super(1131264);
      this.this$0 = _1;
   }

   @Override
   public final String toString() {
      return CommonResource.getString(18);
   }

   @Override
   public final Object invoke(Object context) {
      SearchSaveScreen saveScreen = new SearchSaveScreen(this.this$0._search, this.this$0._fromRibbon, null);
      saveScreen.setModel(this.this$0.copyOfChanges());
      saveScreen.run();
      return null;
   }
}
