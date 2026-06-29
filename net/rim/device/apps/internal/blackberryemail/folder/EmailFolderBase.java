package net.rim.device.apps.internal.blackberryemail.folder;

import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.internal.blackberryemail.ServiceRecordProvider;

public interface EmailFolderBase extends Folder, SyncObject, ServiceRecordProvider, Persistable {
   int LOCAL_FOLDER = 1;
   int NO_MESSAGES_ALLOWED = 2;
   int REDIRECTION_SUPPORTED = 4;
   int REDIRECTION_STATUS = 8;

   int getFolderId();

   int getFolderType();

   boolean isInFolderDatabase();

   EmailHierarchy getEmailHierarchy();

   boolean removeIfPossible();

   void putFolder(EmailFolder var1);

   void removeFolder(EmailFolder var1);

   int getActiveFolderCount();

   int getActiveFolders(Object[] var1, int var2);

   void commitSubtree(boolean var1);

   void removeMessagesFromSubtree(boolean var1);

   void setFolderAttributes(int var1);

   int getFolderAttributes();
}
