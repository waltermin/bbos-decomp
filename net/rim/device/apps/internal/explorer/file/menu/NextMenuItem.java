package net.rim.device.apps.internal.explorer.file.menu;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.apps.internal.explorer.file.ExploreManager;
import net.rim.device.apps.internal.explorer.file.FileItemField;
import net.rim.device.apps.internal.explorer.file.render.RenderScreen;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

public final class NextMenuItem extends MenuItem {
   private FileItemField _field;
   private ExploreManager _manager;
   private Screen _screenToClose;

   public NextMenuItem(ExploreManager manager, Screen closeScreen, FileItemField field) {
      super(ExplorerResources.getResourceBundleFamily(), 32, 591104, 0);
      this._manager = manager;
      this._field = field;
      this._screenToClose = closeScreen;
   }

   @Override
   public final void run() {
      if (this._screenToClose.isDisplayed()) {
         if (this._screenToClose instanceof RenderScreen) {
            RenderScreen renderScreen = (RenderScreen)this._screenToClose;
            if (!renderScreen.isSelectedSet()) {
               this._manager.getFileView().setSelectedItem(this._field.getName());
            }

            if (renderScreen.isSlide()) {
               renderScreen.gotoSlide(this._field);
               return;
            }
         }

         this._screenToClose.close();
         this._manager.openFile(this._field);
      }
   }
}
