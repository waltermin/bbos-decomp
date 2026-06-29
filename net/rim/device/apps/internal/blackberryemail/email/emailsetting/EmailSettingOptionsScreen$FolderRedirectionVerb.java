package net.rim.device.apps.internal.blackberryemail.email.emailsetting;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.util.FileMessageVerb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

class EmailSettingOptionsScreen$FolderRedirectionVerb extends FileMessageVerb {
   private final EmailSettingOptionsScreen this$0;

   public EmailSettingOptionsScreen$FolderRedirectionVerb(EmailSettingOptionsScreen _1, int ordering) {
      super(ordering, CommonResources.getResourceBundle(), 9131, null);
      this.this$0 = _1;
   }

   @Override
   public Object invoke(Object context) {
      Folder folder = this.getFolder();
      EmailSettingOptionsScreen$ServiceInfo info = this.this$0.getSelectedServiceInfo();
      if (folder != null && info != null) {
         FolderRedirectionList folderList = new FolderRedirectionList(folder, EmailResources.getString(162), true, info.getSR());
         EmailSettingCollectionImpl.getInstance(String.valueOf(info.getSR().getUserId())).removeCollectionListener(this.this$0._weakReference);
         folderList.run();
         EmailSettingCollectionImpl.getInstance(String.valueOf(info.getSR().getUserId())).addCollectionListener(this.this$0._weakReference);
      }

      return null;
   }

   private Folder getFolder() {
      EmailSettingOptionsScreen$ServiceInfo info = this.this$0.getSelectedServiceInfo();
      if (info != null) {
         ServiceRecord sr = info.getSR();
         return EmailHierarchy.getEmailHierarchy(sr, true);
      } else {
         return null;
      }
   }
}
