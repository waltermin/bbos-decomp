package net.rim.device.apps.internal.explorer.file.menu;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.api.framework.file.FileSelectionFilter;
import net.rim.device.apps.internal.explorer.file.ExploreManager;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

public final class ExploreMenuItem extends MenuItem {
   private ExploreManager _mgr;

   public ExploreMenuItem(ExploreManager mgr) {
      super(ExplorerResources.getResourceBundleFamily(), 116, 569352, Integer.MAX_VALUE);
      this._mgr = mgr;
   }

   @Override
   public final void run() {
      this._mgr.explore((FileSelectionFilter)(new Object(0, this._mgr.getFilter().getSelectFilter())));
   }
}
