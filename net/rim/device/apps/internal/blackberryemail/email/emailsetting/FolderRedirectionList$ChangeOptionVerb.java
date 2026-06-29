package net.rim.device.apps.internal.blackberryemail.email.emailsetting;

import net.rim.device.apps.api.messaging.ui.SelectFolderVerb;
import net.rim.device.apps.api.ui.CommonResources;

final class FolderRedirectionList$ChangeOptionVerb extends SelectFolderVerb {
   private boolean _selectionMade;
   private final FolderRedirectionList this$0;

   public FolderRedirectionList$ChangeOptionVerb(FolderRedirectionList _1) {
      super(30270, CommonResources.getResourceBundle(), 900);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object parameter) {
      this.this$0.toggleFolder();
      this._selectionMade = true;
      return null;
   }

   @Override
   public final boolean selectionMade() {
      return this._selectionMade;
   }

   @Override
   public final void clearSelection() {
      this._selectionMade = false;
   }
}
