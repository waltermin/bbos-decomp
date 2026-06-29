package net.rim.device.apps.internal.browser.push;

import net.rim.device.apps.internal.browser.stack.CacheResult;

public final class BrowserContentModel extends BrowserPushModel {
   private CacheResult _cacheResult;

   public BrowserContentModel(CacheResult cacheResult) {
      this._cacheResult = cacheResult;
   }

   @Override
   public final void run() {
      this.run(30, this._cacheResult.getURLWithoutFragment());
   }

   @Override
   protected final void doAction() {
      BrowserPushUtilities.addToCache(this._cacheResult, true);
   }
}
