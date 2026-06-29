package net.rim.device.apps.api.messaging.ui;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.ui.CommonResources;

public final class ViewFolderVerb extends Verb {
   private Folder _defaultFolder;
   private Folder _hierarchy;

   public ViewFolderVerb() {
      super(16861493);
   }

   public static final ViewFolderVerb getInstance() {
      return ApplicationRegistry.getApplicationRegistry().get(3746287353255662597L) != null ? new ViewFolderVerb() : null;
   }

   @Override
   public final String toString() {
      return CommonResources.getString(1351);
   }

   @Override
   public final Object invoke(Object context) {
      FolderList fl = new FolderList(this._defaultFolder);
      fl.setContext(new ContextObject(22));
      if (this._hierarchy != null) {
         fl.setHierarchies(new Folder[]{this._hierarchy});
      }

      Verb selectVerb = (Verb)ApplicationRegistry.getApplicationRegistry().get(3746287353255662597L);
      ViewFolderVerb$SelectWrapperVerb swv = new ViewFolderVerb$SelectWrapperVerb(selectVerb, context, null);
      fl.setSelectVerb(swv);
      fl.run();
      if (swv.selectionMade()) {
         UiApplication.getUiApplication().invokeLater((Runnable)selectVerb);
      }

      return context;
   }

   public final void setDefaultFolder(Folder defaultFolder) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final void setHierarchy(Folder hierarchy) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }
}
