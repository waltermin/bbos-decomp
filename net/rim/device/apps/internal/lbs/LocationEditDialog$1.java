package net.rim.device.apps.internal.lbs;

import net.rim.device.apps.api.messaging.util.SimpleFolder;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class LocationEditDialog$1 extends LBSMenuItem {
   private final LocationEditDialog this$0;

   LocationEditDialog$1(LocationEditDialog this$0, int x0, int x1) {
      super(x0, x1);
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      FavouritesScreen favs = new FavouritesScreen(true);
      SimpleFolder folder = favs.getSelectedFolder();
      if (folder != null) {
         this.this$0._folder = folder;
      }

      if (this.this$0._folder.getParentFolder() == null) {
         this.this$0._folder = FavouritesManager.getRootFolder();
      }

      this.this$0._folderField.setText(LBSResources.getString(315) + ' ' + this.this$0._folder.getFriendlyName());
   }
}
