package net.rim.device.api.ui.menu;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.util.Arrays;

public final class CascadingMenuItem extends MenuItem {
   private MenuItem[] _subItems;

   public CascadingMenuItem(MenuItem[] subMenuItems, ResourceBundle bundle, int id, int ordinal, int priority) {
      super(bundle, id, ordinal, priority);
      this._subItems = subMenuItems;
   }

   public final void addItem(MenuItem item) {
      if (this._subItems != null) {
         Arrays.add(this._subItems, item);
      } else {
         this._subItems = new MenuItem[]{item};
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void invokeSubMenu(Menu parentMenu, int xOffset, int yOffset) {
      Menu menu = new Menu(327680);
      if (this._subItems != null) {
         for (int i = 0; i < this._subItems.length; i++) {
            menu.add(this._subItems[i]);
         }
      }

      menu.setOrigin(xOffset, yOffset);
      menu.setParentMenu(parentMenu);
      Field tempTarget = ContextMenu.getInstance().getTarget();
      Screen targetScreen = Menu.getTargetScreen();
      menu.setTargetScreenVirtual(targetScreen);
      boolean var9 = false /* VF: Semaphore variable */;

      try {
         var9 = true;
         menu.show();
         var9 = false;
      } finally {
         if (var9) {
            ContextMenu.getInstance().setTarget(tempTarget);
            Menu.setTargetScreen(targetScreen);
         }
      }

      ContextMenu.getInstance().setTarget(tempTarget);
      Menu.setTargetScreen(targetScreen);
   }

   @Override
   public final void run() {
   }
}
