package net.rim.device.apps.internal.browser.verbs;

import net.rim.device.api.system.Application;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class LoadImageVerb extends Verb {
   private BrowserContentImpl _browserContent;
   private String _urlToLoad;

   public LoadImageVerb(BrowserContentImpl browserContent, String urlToLoad) {
      super(341328);
      this._browserContent = browserContent;
      this._urlToLoad = urlToLoad;
   }

   @Override
   public final String toString() {
      return BrowserResources.getString(712);
   }

   @Override
   public final Object invoke(Object context) {
      if (this._browserContent != null && this._urlToLoad != null) {
         Application.getApplication().invokeLater(new LoadImageVerb$1(this));
      }

      return null;
   }
}
