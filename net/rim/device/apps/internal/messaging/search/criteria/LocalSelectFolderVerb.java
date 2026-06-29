package net.rim.device.apps.internal.messaging.search.criteria;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.ui.SelectFolderVerb;
import net.rim.device.apps.internal.messaging.search.resources.SearchResources;

class LocalSelectFolderVerb extends SelectFolderVerb {
   FolderSelectionField _fsf;

   LocalSelectFolderVerb(FolderSelectionField fsf) {
      super(602416);
      this._fsf = fsf;
   }

   @Override
   public String toString() {
      return SearchResources.getString(41);
   }

   @Override
   public Object invoke(Object context) {
      Folder folder = (Folder)ContextObject.get(context, -1219344331000926502L);
      if (folder != null) {
         this._fsf.setSelectedFolder(folder);
         UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
      }

      return null;
   }

   @Override
   public boolean selectionMade() {
      return false;
   }

   @Override
   public void clearSelection() {
   }
}
