package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.synchronization.StateInfoListener;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.apps.api.framework.model.StateProvider;
import net.rim.device.apps.internal.blackberryemail.EmailSyncState;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolder;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;

class EmailMessageSyncBase extends MessageSync implements StateInfoListener {
   @Override
   public boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      return false;
   }

   @Override
   public SyncObject getSyncObject(int uid) {
      return null;
   }

   @Override
   void commitChanges() {
      for (int i = EmailHierarchy.getEmailHierarchyCount() - 1; i >= 0; i--) {
         EmailHierarchy.getEmailHierarchy(i).commitSubtree(false);
      }
   }

   @Override
   void cleanupAllFolderPreselectorsAfterSync() {
      for (int i = EmailHierarchy.getEmailHierarchyCount() - 1; i >= 0; i--) {
         FolderPreselector.getInstance(EmailHierarchy.getEmailHierarchy(i)).cleanupRecommendedFolderQuickly();
      }
   }

   @Override
   public void setStateInfo(SyncObject object, int version, int state) {
      ((StateProvider)object).setStateInfo(state, MessageSync._syncContextObject);
   }

   @Override
   public int getStateInfo(SyncObject object) {
      int state = 0;
      if (object instanceof EmailMessageModelImpl) {
         EmailMessageModelImpl message = (EmailMessageModelImpl)object;
         state = message.getStateInfo(MessageSync._syncContextObject);
         EmailFolder folder = EmailMessageSync.getEmailFolder(message.getFolderId());
         if (folder != null && folder.getFolderType() != 80) {
            state |= EmailSyncState.makeStateInfo(0, false, false, this.getUseFlag(folder), false);
         }
      }

      return state;
   }
}
