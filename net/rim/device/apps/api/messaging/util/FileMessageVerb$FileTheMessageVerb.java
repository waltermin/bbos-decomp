package net.rim.device.apps.api.messaging.util;

import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.DefaultProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.ConditionalVerb;
import net.rim.device.apps.api.messaging.FilingTarget;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.ui.SelectFolderVerb;

final class FileMessageVerb$FileTheMessageVerb extends SelectFolderVerb implements ConditionalVerb {
   private final FileMessageVerb this$0;

   public FileMessageVerb$FileTheMessageVerb(FileMessageVerb _1) {
      super(602416);
      this.this$0 = _1;
   }

   @Override
   public final void clearSelection() {
   }

   @Override
   public final boolean selectionMade() {
      return false;
   }

   @Override
   public final boolean canInvoke(Object context) {
      Folder folder = (Folder)ContextObject.get(context, -1219344331000926502L);
      if (folder != null) {
         if (!folder.canContainItems()) {
            return false;
         }

         if (folder instanceof FilingTarget) {
            FilingTarget target = (FilingTarget)folder;
            return target.canFile(null);
         }
      }

      return true;
   }

   @Override
   public final Object invoke(Object context) {
      synchronized (RIMPersistentStore.getSynchObject()) {
         synchronized (FolderHierarchies.getLockObject()) {
            Folder destinationFolder = (Folder)ContextObject.get(context, -1219344331000926502L);
            if (this.this$0._selectedItems != null) {
               int numberOfItems = this.this$0._selectedItems.length;

               for (int i = numberOfItems - 1; i >= 0; i--) {
                  try {
                     RIMModel model = this.this$0._selectedItems[i];
                     this.moveTheMessage(model, destinationFolder, this.this$0._fileMessageContext);
                  } finally {
                     break;
                  }
               }
            } else {
               this.moveTheMessage(this.this$0._model, destinationFolder, this.this$0._fileMessageContext);
            }
         }
      }

      UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
      return null;
   }

   @Override
   public final String toString() {
      return this.this$0.toString();
   }

   private final void moveTheMessage(RIMModel model, Folder destinationFolder, Object context) {
      synchronized (this.this$0._fileMessageContext) {
         this.this$0._fileMessageContext.put(-1219344331000926502L, destinationFolder);
         if (model instanceof ActionProvider) {
            ActionProvider actionProvider = (ActionProvider)model;
            actionProvider.perform(1092577344890817449L, this.this$0._fileMessageContext);
         }

         if (model instanceof DefaultProvider) {
            DefaultProvider defaultProvider = (DefaultProvider)model;
            defaultProvider.updateDefault(destinationFolder, this.this$0._fileMessageContext);
         }
      }
   }
}
