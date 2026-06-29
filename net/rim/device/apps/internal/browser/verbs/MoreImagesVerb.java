package net.rim.device.apps.internal.browser.verbs;

import net.rim.device.api.system.Application;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class MoreImagesVerb extends Verb {
   private boolean _doMoreAll;
   private BrowserContentImpl _browserContent;
   private static final int DESCRIPTION_MORE_IMAGES = 460;
   private static final int DESCRIPTION_ALL_IMAGES = 461;

   public MoreImagesVerb(BrowserContentImpl browserContent, boolean doMoreAll) {
      super(doMoreAll ? 344069 : 344064);
      this._doMoreAll = doMoreAll;
      this._browserContent = browserContent;
   }

   @Override
   public final String toString() {
      return BrowserResources.getString(this._doMoreAll ? 461 : 460);
   }

   @Override
   public final Object invoke(Object context) {
      requestImages(this._browserContent, this._doMoreAll);
      return null;
   }

   public static final void requestImages(BrowserContentImpl browserContent, boolean doMoreAll) {
      if (browserContent != null && browserContent.hasUnrequestedImages()) {
         Application.getApplication().invokeLater(new MoreImagesRunnable(browserContent, doMoreAll));
      }
   }
}
