package net.rim.device.apps.internal.blackberryemail.folder;

import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.Persistable;

class EmailHierarchy$SingleMapObjectForCommit implements Persistable {
   private IntHashtable _folderIdToFolderMap;
   private LongHashtable _folderLuidToFolderMap;
   private final EmailHierarchy this$0;

   EmailHierarchy$SingleMapObjectForCommit(EmailHierarchy _1) {
      this.this$0 = _1;
      this._folderIdToFolderMap = new IntHashtable(64);
      this._folderLuidToFolderMap = new LongHashtable(64);
   }
}
