package net.rim.device.apps.internal.browser.webfeed;

import net.rim.device.api.ui.UserInputEventListener;

final class WebFeedPopup$Listener implements UserInputEventListener {
   private WebFeedPopup _outer;

   public WebFeedPopup$Listener(WebFeedPopup outer) {
      this._outer = outer;
   }

   private final void destroyMe() {
      if (this._outer.isDisplayed()) {
         this._outer.close();
      }
   }

   @Override
   public final void onUserInput(int device, int flags) {
      this.destroyMe();
   }
}
