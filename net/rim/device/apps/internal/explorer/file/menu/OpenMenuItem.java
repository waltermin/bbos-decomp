package net.rim.device.apps.internal.explorer.file.menu;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.internal.explorer.file.ExploreManager;
import net.rim.device.apps.internal.explorer.file.FileItemField;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

public final class OpenMenuItem extends MenuItem {
   private ExploreManager _mgr;
   private FileItemField _selectedItem;

   public OpenMenuItem(ExploreManager mgr, FileItemField selectedItem) {
      super(ExplorerResources.getResourceBundleFamily(), getResourceId(selectedItem.getMediaType()), 569345, 100);
      this._mgr = mgr;
      this._selectedItem = selectedItem;
   }

   private static final int getResourceId(int mediaType) {
      switch (mediaType) {
         case 2:
         case 3:
         case 7:
            return 92;
         default:
            return 5;
      }
   }

   @Override
   public final void run() {
      this._mgr.openItem(this._selectedItem);
   }
}
