package net.rim.device.apps.internal.browser.verbs;

import net.rim.device.apps.internal.browser.page.BrowserContentImpl;

class MoreImagesRunnable implements Runnable {
   private boolean _doMoreAll;
   private BrowserContentImpl _browserContent;

   MoreImagesRunnable(BrowserContentImpl browserContent, boolean doMoreAll) {
      this._doMoreAll = doMoreAll;
      this._browserContent = browserContent;
   }

   @Override
   public void run() {
      this._browserContent.requestMoreImages(this._doMoreAll);
   }
}
