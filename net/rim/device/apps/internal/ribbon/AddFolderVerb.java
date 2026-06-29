package net.rim.device.apps.internal.ribbon;

import java.util.Hashtable;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.verb.Verb;

final class AddFolderVerb extends RibbonBarVerb {
   private static AddFolderVerb _theVerb;

   private AddFolderVerb(RibbonLauncherImpl ribbonApp) {
      super(ribbonApp, 33554432);
   }

   @Override
   public final String toString() {
      return RibbonResources.getString(162);
   }

   static final Verb getInstance(RibbonLauncherImpl ribbonApp) {
      if (_theVerb == null) {
         _theVerb = new AddFolderVerb(ribbonApp);
      }

      return _theVerb;
   }

   @Override
   public final Object invoke(Object parameter) {
      Manager manager = (Manager)super._ribbonApp._applicationIconArea;
      int finalPosition = manager.getFieldWithFocusIndex();
      Hashtable folders = super._ribbonApp._hierarchyManager.getActiveFolders();
      FolderDialog dialog = FolderDialog.getInstance();
      int result = dialog.doModal();
      String newFolderName = dialog.getResult();
      if (result >= 0 && newFolderName.length() > 0) {
         super._ribbonApp._hierarchyManager.addFolder(newFolderName, finalPosition, dialog.getFolderImageName());
      }

      return null;
   }
}
