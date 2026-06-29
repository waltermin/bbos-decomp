package net.rim.device.apps.internal.browser.bookmark;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

final class BookmarksScreen$MoveVerb extends Verb {
   private final BookmarksScreen this$0;

   public BookmarksScreen$MoveVerb(BookmarksScreen _1, boolean bookmark) {
      super(bookmark ? 1312128 : 1315840, BrowserResources.getResourceBundle(), bookmark ? 467 : 662);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object parameter) {
      this.this$0.enableMoveMode(true);
      return null;
   }
}
