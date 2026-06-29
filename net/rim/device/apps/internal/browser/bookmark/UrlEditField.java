package net.rim.device.apps.internal.browser.bookmark;

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.ui.BrowserUrlEditField;

final class UrlEditField extends BrowserUrlEditField {
   private String _initialValue;

   public UrlEditField(String label, String initialValue, int maxNumChars, long style, String clearValue) {
      super(label, initialValue, maxNumChars, style, clearValue);
      this._initialValue = initialValue;
   }

   @Override
   protected final void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      if (this.isDirty()) {
         contextMenu.addItem(new UrlEditField$1(this, BrowserResources.getString(546), 100, 1));
      }

      contextMenu.addItem(new UrlEditField$2(this, BrowserResources.getString(356), 1000, 1));
   }

   @Override
   public final void clear(int context) {
      super.clear(context);
      this.setDirty(true);
   }
}
