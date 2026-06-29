package net.rim.device.api.ui.menu;

import net.rim.device.api.ui.MenuItem;

public interface MenuList {
   MenuItem getCurrentItem();

   void setCurrentItem(MenuItem var1);

   void setMenuItems(MenuItem[] var1);
}
