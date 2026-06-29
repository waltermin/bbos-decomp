package net.rim.device.apps.internal.browser.core;

final class DoAutoUpdate implements Runnable {
   private BrowserImpl _browser;

   public DoAutoUpdate(BrowserImpl browser) {
      this._browser = browser;
   }

   @Override
   public final void run() {
      this._browser.startResourceValidationThread(true, false, null, System.currentTimeMillis());
   }
}
