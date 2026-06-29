package net.rim.device.api.ui.menu;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Menu;

public class MenuItemCallback extends MenuItem {
   private MenuHandler _handler;

   public MenuItemCallback(MenuHandler handler, ResourceBundle bundle, int id, int ordinal, int priority) {
      super(bundle, id, ordinal, priority);
      if (handler == null) {
         throw new NullPointerException();
      }

      this._handler = handler;
   }

   public MenuItemCallback(MenuHandler handler, String text, int ordinal, int priority) {
      super(text, ordinal, priority);
      if (handler == null) {
         throw new NullPointerException();
      }

      this._handler = handler;
   }

   public static void add(Menu menu, MenuHandler handler, ResourceBundle bundle, int[] data) {
      if (data.length % 3 != 0) {
         throw new IllegalArgumentException();
      }

      int end = data.length;

      for (int lv = 0; lv < end; lv += 3) {
         int id = data[lv];
         int ordinal = data[lv + 1];
         int priority = data[lv + 2];
         if (id == -1) {
            menu.add(MenuItem.separator(ordinal));
         } else {
            menu.add(new MenuItemCallback(handler, bundle, id, ordinal, priority));
         }
      }
   }

   public static void add(Menu menu, MenuHandler handler, ResourceBundle bundle, int[] ids, int ordinal, int priorityItem) {
      for (int id : ids) {
         int priority = priorityItem == id ? 0 : Integer.MAX_VALUE;
         if (id == -1) {
            menu.add(MenuItem.separator(ordinal));
         } else {
            menu.add(new MenuItemCallback(handler, bundle, id, ordinal, priority));
         }

         ordinal++;
      }
   }

   @Override
   public void run() {
      this._handler.menuInvoke(this.getId(), this);
   }
}
