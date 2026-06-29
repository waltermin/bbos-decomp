package net.rim.device.apps.internal.explorer.file.menu;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

public final class PropertiesMenuItem extends MenuItem {
   private Object _fileItem;

   public PropertiesMenuItem(Object fileItem) {
      super(ExplorerResources.getResourceBundleFamily(), 65, 591110, Integer.MAX_VALUE);
      this._fileItem = fileItem;
   }

   @Override
   public final void run() {
      UiApplication.getUiApplication().pushScreen(new PropertiesDialog(this._fileItem));
   }
}
