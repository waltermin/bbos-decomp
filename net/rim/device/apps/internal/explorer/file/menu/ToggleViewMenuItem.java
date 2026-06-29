package net.rim.device.apps.internal.explorer.file.menu;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.internal.explorer.file.FolderList;
import net.rim.device.apps.internal.explorer.file.options.ExplorerOptions;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

public final class ToggleViewMenuItem extends MenuItem {
   private FolderList _folderList;
   private int _viewModeIndex;
   private int _rootView;

   public ToggleViewMenuItem(FolderList folderList, int rootView, int viewModeIndex) {
      super(ExplorerResources.getStringArray(98)[viewModeIndex], 484608, 200);
      this._folderList = folderList;
      this._rootView = rootView;
      this._viewModeIndex = viewModeIndex;
   }

   @Override
   public final void run() {
      ExplorerOptions.getOptions().setViewMode(this._rootView, this._viewModeIndex);
      this._folderList.setViewMode(this._viewModeIndex);
   }
}
