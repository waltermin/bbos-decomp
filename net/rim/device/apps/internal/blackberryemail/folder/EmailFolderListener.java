package net.rim.device.apps.internal.blackberryemail.folder;

public interface EmailFolderListener {
   void folderAdded(EmailFolder var1);

   void folderRemoved(EmailFolder var1);

   void folderUpdated(EmailFolder var1);
}
