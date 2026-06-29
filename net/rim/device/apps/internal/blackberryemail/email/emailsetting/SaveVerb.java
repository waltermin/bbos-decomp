package net.rim.device.apps.internal.blackberryemail.email.emailsetting;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.transmission.rim.RIMMessagingFolderManagement;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolder;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.blackberryemail.otasync.OTAMessageSync;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.vm.WeakReference;

final class SaveVerb extends Verb {
   WeakReference _folderRedirectionListWR;
   FolderRedirectionTreeField _folderRedirectTreeField;
   ServiceRecord _sr;
   static SaveVerb _theVerb = new SaveVerb(1131264);

   private SaveVerb(int ordering) {
      super(ordering, EmailResources.getResourceBundle(), 19);
   }

   static final SaveVerb getInstance(FolderRedirectionList folderRedirectionList, FolderRedirectionTreeField folderRedirectTreeField, ServiceRecord sr) {
      _theVerb._folderRedirectionListWR = (WeakReference)(new Object(folderRedirectionList));
      _theVerb._folderRedirectTreeField = folderRedirectTreeField;
      _theVerb._sr = sr;
      return _theVerb;
   }

   @Override
   public final Object invoke(Object parameter) {
      int[] folderCookies = this._folderRedirectTreeField.getFolderCookies();
      RIMMessagingFolderManagement packet = (RIMMessagingFolderManagement)(new Object());

      for (int i = folderCookies.length - 1; i >= 0; i--) {
         Object obj = this._folderRedirectTreeField.getCookie(folderCookies[i]);
         if (obj instanceof EmailHeaderModel) {
            EmailFolder ef = (EmailHeaderModel)obj;
            boolean redirected = ef.folderAttributesFlagsSet(8);
            if (redirected != this._folderRedirectTreeField.isRedirected(i, true)) {
               if (redirected) {
                  ef.clearFolderAttributesFlags(8);
               } else {
                  ef.setFolderAttributesFlags(8);
               }

               packet.addModifyFolderAttributes(ef.getFolderId(), ef.getFolderAttributes());
            }
         }
      }

      OTAMessageSync.getInstance().modifyFolderAttributesCommand(this._sr, packet);
      this._folderRedirectTreeField.setDirty(false);
      FolderRedirectionList folderRedirectionList = (FolderRedirectionList)this._folderRedirectionListWR.get();
      if (folderRedirectionList != null) {
         EmailHierarchy.removeEmailFolderListener(folderRedirectionList);
      }

      UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
      return null;
   }
}
