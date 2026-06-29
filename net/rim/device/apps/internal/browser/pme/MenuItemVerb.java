package net.rim.device.apps.internal.browser.pme;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.api.framework.verb.Verb;

public final class MenuItemVerb extends Verb {
   private MenuItem _menuItem;

   public MenuItemVerb(MenuItem menuItem) {
      super(1);
      this._menuItem = menuItem;
   }

   public final void setMenuItem(MenuItem menuItem) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final Object invoke(Object context) {
      if (this._menuItem != null) {
         this._menuItem.run();
      }

      return null;
   }
}
