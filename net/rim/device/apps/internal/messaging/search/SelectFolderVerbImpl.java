package net.rim.device.apps.internal.messaging.search;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FolderProvider;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.search.MessageSearch;
import net.rim.device.apps.api.messaging.ui.SelectFolderVerb;
import net.rim.device.apps.internal.messaging.search.resources.SearchResources;

final class SelectFolderVerbImpl extends SelectFolderVerb implements Runnable {
   private Folder _folder;
   private boolean _selected = false;

   public SelectFolderVerbImpl() {
      super(602416);
   }

   @Override
   public final String toString() {
      return SearchResources.getString(41);
   }

   @Override
   public final Object invoke(Object context) {
      this._selected = true;
      Folder f = null;
      Object o = ContextObject.get(context, 254);
      if (!(o instanceof ContextObject)) {
         if (!(o instanceof FolderProvider)) {
            if (!(context instanceof Folder)) {
               f = (Folder)ContextObject.get(context, -1219344331000926502L);
            } else {
               f = (Folder)context;
            }
         } else {
            FolderProvider fp = (FolderProvider)o;
            f = FolderHierarchies.getFolder(fp.getFolderId());
         }
      } else {
         ContextObject contextObject = (ContextObject)o;
         f = (Folder)ContextObject.get(contextObject, -1219344331000926502L);
      }

      if (f != null) {
         UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
         this._folder = f;
      }

      return null;
   }

   @Override
   public final void run() {
      MessageSearch.getInstance().folderSearch(this._folder);
   }

   @Override
   public final boolean selectionMade() {
      return this._selected;
   }

   @Override
   public final void clearSelection() {
      this._folder = null;
      this._selected = false;
   }
}
