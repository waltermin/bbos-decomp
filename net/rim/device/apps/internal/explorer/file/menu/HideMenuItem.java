package net.rim.device.apps.internal.explorer.file.menu;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

public final class HideMenuItem extends MenuItem {
   public HideMenuItem() {
      super(ExplorerResources.getResourceBundleFamily(), 110, 268501008, Integer.MAX_VALUE);
   }

   @Override
   public final void run() {
      Application.getApplication().requestBackground();
   }
}
