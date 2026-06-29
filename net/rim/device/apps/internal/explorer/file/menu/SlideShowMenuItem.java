package net.rim.device.apps.internal.explorer.file.menu;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.internal.explorer.file.ExploreManager;
import net.rim.device.apps.internal.explorer.file.FileItemField;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

public final class SlideShowMenuItem extends MenuItem {
   private ExploreManager _explorer;
   private FileItemField _item;

   public SlideShowMenuItem(ExploreManager mgr, FileItemField item) {
      super(ExplorerResources.getResourceBundleFamily(), 83, 569350, Integer.MAX_VALUE);
      this._explorer = mgr;
      this._item = item;
   }

   @Override
   public final void run() {
      this._explorer.openSlideShow(this._item);
   }
}
