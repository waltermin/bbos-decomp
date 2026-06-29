package net.rim.device.internal.EScreens;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.MenuItem;

public class EScreenExtendedMenuRepository {
   protected static final long GUID;

   public static EScreenExtendedMenuRepository getInstance() {
      return (EScreenExtendedMenuRepository)ApplicationRegistry.getApplicationRegistry().get(-8538258899775408506L);
   }

   public MenuItem[] getExtendedMenu() {
      throw null;
   }
}
