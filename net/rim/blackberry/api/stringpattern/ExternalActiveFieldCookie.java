package net.rim.blackberry.api.stringpattern;

import java.util.Vector;
import net.rim.blackberry.api.menuitem.ApplicationMenuItem;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ActiveFieldCookie;
import net.rim.device.api.ui.component.CookieProvider;

final class ExternalActiveFieldCookie implements ActiveFieldCookie {
   private ExternalActiveFieldCookie$CookieMenuItem[] _items;
   private ApplicationDescriptor _application;

   ExternalActiveFieldCookie(ApplicationMenuItem[] menuItems, ApplicationDescriptor application) {
      this._application = application;
      this._items = new ExternalActiveFieldCookie$CookieMenuItem[menuItems.length];

      for (int i = 0; i < menuItems.length; i++) {
         this._items[i] = new ExternalActiveFieldCookie$CookieMenuItem(menuItems[i], application);
      }
   }

   final void initialize(String matchedString) {
      for (int i = 0; i < this._items.length; i++) {
         this._items[i].initialize(matchedString);
      }
   }

   final ApplicationDescriptor getApplication() {
      return this._application;
   }

   @Override
   public final MenuItem getFocusVerbs(CookieProvider provider, Object context, Vector items) {
      for (int i = 0; i < this._items.length; i++) {
         items.addElement(this._items[i]);
      }

      return this._items[0];
   }

   @Override
   public final boolean invokeApplicationKeyVerb() {
      this._items[0].run();
      return true;
   }
}
