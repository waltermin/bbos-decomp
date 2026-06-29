package net.rim.device.apps.internal.blackberryemail.otasync;

import net.rim.device.api.util.IntComparator;
import net.rim.device.apps.internal.blackberryemail.EmailSyncState;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;

final class IndirectFolderIdComparator implements IntComparator {
   private EmailHierarchy _hierarchy;

   IndirectFolderIdComparator(EmailHierarchy hierarchy) {
      this._hierarchy = hierarchy;
   }

   @Override
   public final int compare(int o1, int o2) {
      int folder1 = EmailSyncState.getFolderId(this._hierarchy.getGhostMessageInfo(o1));
      int folder2 = EmailSyncState.getFolderId(this._hierarchy.getGhostMessageInfo(o2));
      return folder1 - folder2;
   }
}
