package net.rim.device.apps.internal.blackberryemail.otasync;

import net.rim.device.api.util.Comparator;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolder;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;

final class FolderIdComparator implements Comparator {
   FolderIdComparator(EmailHierarchy hierarchy) {
   }

   @Override
   public final int compare(Object o1, Object o2) {
      EmailFolder folder1 = EmailHierarchy.getEmailFolder(((EmailMessageModel)o1).getFolderId());
      EmailFolder folder2 = EmailHierarchy.getEmailFolder(((EmailMessageModel)o2).getFolderId());
      if (folder1 != null && folder2 != null) {
         int folderId1 = folder1.getFolderId();
         int folderId2 = folder2.getFolderId();
         return folderId1 - folderId2;
      } else {
         return 0;
      }
   }
}
