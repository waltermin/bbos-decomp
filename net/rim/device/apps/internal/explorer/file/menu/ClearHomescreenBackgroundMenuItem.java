package net.rim.device.apps.internal.explorer.file.menu;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

public final class ClearHomescreenBackgroundMenuItem extends MenuItem {
   public ClearHomescreenBackgroundMenuItem() {
      super(ExplorerResources.getResourceBundleFamily(), 9, 565265, Integer.MAX_VALUE);
   }

   @Override
   public final void run() {
      RibbonLauncher.getInstance().setBackgroundImage(null, null);
   }
}
