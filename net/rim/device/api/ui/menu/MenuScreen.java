package net.rim.device.api.ui.menu;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Menu;

public interface MenuScreen {
   void close();

   MenuItem getCurrentItem();

   Menu getMenu();

   boolean isDisplayed();

   void setAlignment(long var1, long var3);

   void setCurrentItem(MenuItem var1);

   void setList(MenuList var1);

   void setMenu(Menu var1);

   void setOrigin(int var1, int var2);
}
