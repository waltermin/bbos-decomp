package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.ribbon.launcher.ApplicationEntry;
import net.rim.device.apps.internal.ribbon.launcher.FolderEntryPointDescriptor;
import net.rim.device.apps.internal.ribbon.launcher.HierarchyManager;
import net.rim.device.apps.internal.ribbon.launcher.RibbonIconField;

final class EditFolderPropertiesVerb extends RibbonBarVerb {
   private static EditFolderPropertiesVerb _theVerb;

   private EditFolderPropertiesVerb(RibbonLauncherImpl ribbonApp) {
      super(ribbonApp, 33554432);
   }

   @Override
   public final String toString() {
      return RibbonResources.getString(164);
   }

   static final Verb getInstance(RibbonLauncherImpl ribbonApp) {
      if (_theVerb == null) {
         _theVerb = new EditFolderPropertiesVerb(ribbonApp);
      }

      return _theVerb;
   }

   @Override
   public final Object invoke(Object parameter) {
      ApplicationEntry folder = super._ribbonApp._applicationIconArea.getSelectedApplication();
      if (folder.getDescriptor() instanceof FolderEntryPointDescriptor) {
         FolderEntryPointDescriptor descriptor = (FolderEntryPointDescriptor)folder.getDescriptor();
         int position = ((Manager)super._ribbonApp._applicationIconArea).getFieldWithFocusIndex();
         String folderName = folder.getDescription(true);
         FolderDialog dialog = FolderDialog.getInstance(folder);
         int result = dialog.doModal();
         if (result >= 0 && folderName.length() > 0) {
            RibbonIconField field = folder.getRibbonIcon();
            HierarchyManager hm = HierarchyManager.getInstance();
            field = hm.renameFolder(folder, dialog.getResult(), position, dialog.getFolderImageName());
            descriptor.resetIcon();
            ((Manager)super._ribbonApp._applicationIconArea).delete(field);
            ((Manager)super._ribbonApp._applicationIconArea).insert(field, position);
            field.setFocus();
         }
      }

      return null;
   }
}
