package net.rim.device.apps.internal.browser.bookmark;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.page.PageModel;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.verbs.ShowUrlVerb;

final class SendBookmarkVerb extends Verb {
   private PageModel _bookmark;

   public SendBookmarkVerb(PageModel bookmark) {
      super(1312144, BrowserResources.getResourceBundle(), 800);
      this._bookmark = bookmark;
   }

   @Override
   public final Object invoke(Object context) {
      ShowUrlVerb.sendAddress(this._bookmark.getUrl(), this._bookmark.getTitle(), 800);
      return null;
   }
}
