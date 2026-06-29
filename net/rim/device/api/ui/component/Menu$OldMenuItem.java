package net.rim.device.api.ui.component;

import net.rim.device.api.ui.MenuItem;

class Menu$OldMenuItem extends MenuItem {
   Object _cookie;
   int _id;

   public Menu$OldMenuItem(String text, Object cookie, int id) {
      super(text, 0, 0);
      this._cookie = cookie;
      this._id = id;
   }

   public Object getCookie() {
      return this._cookie;
   }

   @Override
   public int getId() {
      return this._id;
   }

   @Override
   public void run() {
      if (this._cookie instanceof MenuItem) {
         MenuItem item = (MenuItem)this._cookie;
         item.run();
      }
   }
}
