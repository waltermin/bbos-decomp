package net.rim.device.apps.internal.blackberryemail.folder;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncItem;
import net.rim.device.api.util.DataBuffer;
import net.rim.vm.PersistentInteger;

class EmailFolderId$EmailFolderIdSyncCollection extends SyncItem {
   private final EmailFolderId this$0;

   EmailFolderId$EmailFolderIdSyncCollection(EmailFolderId _1) {
      this.this$0 = _1;
   }

   @Override
   public boolean setSyncData(DataBuffer buffer, int version) {
      try {
         synchronized (this.this$0) {
            PersistentInteger.set(this.this$0._persistentId, ConverterUtilities.readInt(buffer));
         }

         return true;
      } finally {
         ;
      }
   }

   @Override
   public boolean getSyncData(DataBuffer buffer, int version) {
      ConverterUtilities.convertInt(buffer, 0, PersistentInteger.get(this.this$0._persistentId), 4);
      return true;
   }

   @Override
   public int getSyncVersion() {
      return 0;
   }

   @Override
   public String getSyncName() {
      return "Folder Id";
   }

   @Override
   public String getSyncName(Locale locale) {
      return null;
   }
}
