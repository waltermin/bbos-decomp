package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.VerbMenuItem;

final class DocViewAttachmentViewerModel$CustomVerticalFieldManager extends VerticalFieldManager {
   public DocViewAttachmentViewerModel$CustomVerticalFieldManager() {
   }

   @Override
   protected final boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               if (!DeviceInfo.isInHolster() && AttachmentViewerFactory.isAttachmentViewingEnabled() && this.getFieldWithFocus() instanceof CustomBitmapField) {
                  try {
                     MenuItem menuVerb = Ui.getUiEngine().getActiveScreen().getDefaultMenuItem(65536);
                     if (menuVerb instanceof VerbMenuItem) {
                        Verb vb = ((VerbMenuItem)menuVerb).getVerb();
                        if (vb instanceof OpenAttachmentVerb) {
                           menuVerb.run();
                           handled = true;
                        }
                     }

                     ContextMenu.getInstance().setTarget(null);
                     Menu.setTargetScreen(null);
                     return handled;
                  } finally {
                     return handled;
                  }
               }
         }
      }

      return handled;
   }
}
