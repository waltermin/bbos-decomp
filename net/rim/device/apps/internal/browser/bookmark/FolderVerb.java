package net.rim.device.apps.internal.browser.bookmark;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.util.SimpleFolder;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.store.BrowserFolders;
import net.rim.device.apps.internal.browser.ui.DialogEnterSubFolderName;

final class FolderVerb extends Verb {
   public FolderVerb(int resourceId, int ordering) {
      super(ordering, BrowserResources.getResourceBundle(), resourceId);
   }

   @Override
   public final Object invoke(Object context) {
      SimpleFolder selectedFolder = null;
      if (!(context instanceof ContextObject)) {
         if (context instanceof SimpleFolder) {
            selectedFolder = (SimpleFolder)context;
         }
      } else {
         ContextObject contextObject = (ContextObject)context;
         selectedFolder = (SimpleFolder)contextObject.get(-1219344331000926502L);
      }

      if (selectedFolder != null) {
         switch (super._rbKey) {
            case 167:
               break;
            case 168:
            default:
               DialogEnterSubFolderName enterSubFolderNamex = new DialogEnterSubFolderName(selectedFolder, "");
               String subFolderNamex = enterSubFolderNamex.getName();
               if (subFolderNamex != null) {
                  long luid = BrowserFolders.makeUniqueLUID(selectedFolder);
                  SimpleFolder newSubFolder = new SimpleFolder(
                     BrowserFolders.BROWSER_FAMILY, luid, subFolderNamex, BrowserFolders.BROWSER_FOLDER_COLLECTION_CLASS, selectedFolder, 1
                  );
                  BrowserFolders.addSubFolder(selectedFolder, newSubFolder);
                  return null;
               }
               break;
            case 169:
               String message = MessageFormat.format(BrowserResources.getString(282), new String[]{selectedFolder.getFriendlyName()});
               if (Dialog.ask(2, message) == 3) {
                  SimpleFolder parentFolder = (SimpleFolder)selectedFolder.getParentFolder();
                  long defaultFolderID = BookmarksFolderList.getDefaultFolderID();
                  if (selectedFolder.getLUID() == defaultFolderID || selectedFolder.getFolder(defaultFolderID) != null) {
                     BookmarksFolderList.setDefaultFolderID(parentFolder.getLUID());
                  }

                  BrowserFolders.deleteFolder(selectedFolder);
               }
               break;
            case 170:
               DialogEnterSubFolderName enterSubFolderName = new DialogEnterSubFolderName(selectedFolder.getParentFolder(), selectedFolder.getFriendlyName());
               String subFolderName = enterSubFolderName.getName();
               if (subFolderName != null) {
                  BrowserFolders.renameFolder(selectedFolder, subFolderName);
                  return null;
               }
         }
      }

      return null;
   }
}
