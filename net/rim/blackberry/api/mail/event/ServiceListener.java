package net.rim.blackberry.api.mail.event;

public interface ServiceListener {
   void folderCreated(FolderEvent var1);

   void folderDeleted(FolderEvent var1);

   void folderUpdated(FolderEvent var1);
}
