package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolder;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.wica.runtime.access.internal.data.enumeration.EmailFolderEnumConverter;
import net.rim.wica.runtime.access.internal.data.handlers.IntFieldHandler;

final class EmailCollection$FolderHandler implements IntFieldHandler {
   private EmailCollection$FolderHandler() {
   }

   @Override
   public final int getValue(Object item) {
      if (item instanceof Object) {
         EmailMessageModel model = (EmailMessageModel)item;
         EmailFolder folder = EmailHierarchy.getEmailFolder(model.getFolderId());
         if (folder != null) {
            return EmailFolderEnumConverter.deviceToCommon(folder.getFolderType());
         }
      }

      return -1;
   }

   EmailCollection$FolderHandler(EmailCollection$1 x0) {
      this();
   }
}
