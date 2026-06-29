package net.rim.blackberry.api.invoke;

import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.messagelist.ShowMessageAppVerb;
import net.rim.device.apps.api.messaging.search.MessageSearch;

class Invoke$OpenFolderVerb extends ShowMessageAppVerb {
   public Invoke$OpenFolderVerb() {
      super(0);
   }

   @Override
   public Object doInvoke(Object context) {
      if (context instanceof Folder) {
         MessageSearch.getInstance().folderSearch((Folder)context);
      }

      return null;
   }
}
