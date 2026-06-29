package net.rim.device.apps.internal.blackberryemail.folder;

import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.vm.PersistentInteger;

final class EmailFolderId {
   private int _persistentId = PersistentInteger.getId(4908121320859842266L, -1);
   private int _deviceFolderIdPersistentId = PersistentInteger.getId(7943593562601989565L, 1);
   private SyncCollection _syncCollection = new EmailFolderId$EmailFolderIdSyncCollection(this);
   private static final long FOLDER_ID_DATA = 4908121320859842266L;
   private static final long DEVICE_FOLDER_ID_DATA = 7943593562601989565L;
   private static final int FIRST_VALUE = -1;
   private static final int DEVICE_FOLDER_ID_FIRST_VALUE = 1;
   private static EmailFolderId _instance;

   private EmailFolderId() {
   }

   private final synchronized int getNextFolderIdImpl() {
      int value = PersistentInteger.get(this._deviceFolderIdPersistentId);
      PersistentInteger.set(this._deviceFolderIdPersistentId, value + 1);
      return value;
   }

   public static final int getNextFolderId() {
      return _instance.getNextFolderIdImpl();
   }

   public static final SyncCollection getSyncCollection() {
      return _instance._syncCollection;
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         _instance = (EmailFolderId)ar.get(4908121320859842266L);
         if (_instance == null) {
            _instance = new EmailFolderId();
            ar.put(4908121320859842266L, _instance);
         }
      }
   }
}
