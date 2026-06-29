package net.rim.device.apps.internal.ribbon;

import java.util.Hashtable;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.ribbon.launcher.ApplicationEntry;
import net.rim.device.apps.internal.ribbon.launcher.FolderEntryPointDescriptor;
import net.rim.device.apps.internal.ribbon.launcher.HierarchyManager;

final class DeleteVerb extends RibbonBarVerb {
   private static DeleteVerb _theVerb;

   private DeleteVerb(RibbonLauncherImpl ribbonApp) {
      super(ribbonApp, 614912);
   }

   @Override
   public final String toString() {
      return CommonResources.getString(1000);
   }

   static final Verb getInstance(RibbonLauncherImpl ribbonApp) {
      if (_theVerb == null) {
         _theVerb = new DeleteVerb(ribbonApp);
      }

      return _theVerb;
   }

   @Override
   public final Object invoke(Object parameter) {
      ApplicationEntry folder = super._ribbonApp._applicationIconArea.getSelectedApplication();
      if (folder.getDescriptor() instanceof FolderEntryPointDescriptor) {
         FolderEntryPointDescriptor desc = (FolderEntryPointDescriptor)folder.getDescriptor();
         String name = desc.get(1, "");
         String description = folder.getDescription(true);
         String msg = CommonResources.getString(1000) + " " + description + " ?";
         HierarchyManager hm = HierarchyManager.getInstance();
         ApplicationEntry[] list = hm.getAppsInFolder(name);
         if (list != null && list.length > 0) {
            msg = RibbonResources.getString(168) + "\n" + msg;
         }

         if (3 == Dialog.ask(2, msg, -1)) {
            hm.deleteFolder(folder, "");
            Hashtable activeFolders = HierarchyManager.getInstance().getActiveFolders();
            if (activeFolders != null && activeFolders.size() == 1) {
               FolderDialog.resetFolderSuffix();
            }
         }
      }

      return null;
   }
}
