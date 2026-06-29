package net.rim.device.apps.internal.blackberryemail.email.emailsetting;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.ui.FolderList;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

final class SelectAllFolderVerb extends Verb {
   private boolean _select;
   private FolderRedirectionTreeField _folderRedirectTreeField;
   private FolderList _folderList;

   public SelectAllFolderVerb(FolderList folderList, boolean select) {
      super(16865360, EmailResources.getResourceBundle(), select ? 199 : 1004);
      this._folderList = folderList;
      this._select = select;
   }

   public final void setTreeField(FolderRedirectionTreeField folderRedirectTreeField) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final Object invoke(Object parameter) {
      this._folderRedirectTreeField.selectAll(this._select);
      this._folderList.invalidate();
      return null;
   }
}
