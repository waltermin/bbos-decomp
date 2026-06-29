package net.rim.device.apps.internal.browser.page;

import net.rim.device.api.browser.field.Destroyable;
import net.rim.device.api.ui.Field;
import net.rim.device.apps.internal.browser.stack.CacheResult;

public final class PageCacheContent {
   private BrowserContentImpl _ui;
   private CacheResult _cacheResult;
   private int _style;

   public PageCacheContent(BrowserContentImpl ui, CacheResult cr, int style) {
      if (ui != null && cr != null) {
         this._ui = ui;
         this._cacheResult = cr;
         this._style = style;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final void destroy() {
      Field f = this._ui.getDisplayableContent();
      if (f instanceof Destroyable) {
         ((Destroyable)f).destroy();
      }
   }

   public final void setDestroyOnUndisplay(boolean value) {
      Field f = this._ui.getDisplayableContent();
      if (f instanceof Destroyable) {
         ((Destroyable)f).setDestroyMethod(value ? 0 : 1);
      }
   }

   public final CacheResult getCacheResult() {
      return this._cacheResult;
   }

   public final BrowserContentImpl getUI() {
      return this._ui;
   }

   public final int getStyle() {
      return this._style;
   }
}
