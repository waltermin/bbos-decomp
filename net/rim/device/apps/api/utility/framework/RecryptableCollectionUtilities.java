package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.Phone;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.vm.Process;

public final class RecryptableCollectionUtilities {
   private static final int MIN_IDLE_TIME_SEC;
   private static final int MIN_IDLE_TIME_MSEC;
   private static final long ID;
   private static final int COMPLETE;
   private static final int WAIT_FOR_IDLE;
   private static final int WAIT_FOR_UNLOCK;
   private static SyncManager _syncManager = SyncManager.getInstance();

   private RecryptableCollectionUtilities() {
   }

   private static final boolean isIdle() {
      return Phone.isSupported() && Phone.getInstance().isActive() ? false : DeviceInfo.getIdleTime() >= 5 && !_syncManager.isSerialSyncInProgress();
   }

   public static final void recrypt(RecryptableCollection collection, Object collectionLock, Object collectionCookie, int contentProtectionGeneration) {
      recrypt(collection, collectionLock, collectionCookie, contentProtectionGeneration, true, true);
   }

   public static final void recrypt(
      RecryptableCollection collection, Object collectionLock, Object collectionCookie, int contentProtectionGeneration, boolean compress, boolean encrypt
   ) {
      if (collection != null && collectionLock != null) {
         Object ticket = PersistentContent.getTicket();
         synchronized (ApplicationRegistry.getApplicationRegistry().getObject(2843204529516853295L)) {
            while (true) {
               switch (recrypt2(ticket, collection, collectionLock, collectionCookie, contentProtectionGeneration, compress, encrypt)) {
                  case -1:
                     continue;
                  case 0:
                     collection.commit(collectionCookie);
                     return;
                  case 1:
                     break;
                  case 2:
                  default:
                     ticket = PersistentContent.waitForTicket();
               }

               do {
                  try {
                     Thread.sleep(5000);
                  } finally {
                     continue;
                  }
               } while (!isIdle());
            }
         }
      } else {
         throw new Object();
      }
   }

   private static final int recrypt2(
      Object ticket,
      RecryptableCollection collection,
      Object collectionLock,
      Object collectionCookie,
      int contentProtectionGeneration,
      boolean compress,
      boolean encrypt
   ) {
      int index = -1;

      while (true) {
         int loop = -1;
         Object oldObject;
         Object newObject;
         synchronized (collectionLock) {
            int collectionSize = collection.getSize(collectionCookie);

            while (true) {
               if (++loop >= collectionSize) {
                  return 0;
               }

               if (contentProtectionGeneration != PersistentContent.getModeGeneration()) {
                  return 0;
               }

               if (!isIdle()) {
                  return 1;
               }

               if (++index >= collectionSize) {
                  index = 0;
               }

               oldObject = collection.getElementAt(index, collectionCookie);
               if (oldObject instanceof EncryptableProvider) {
                  EncryptableProvider encryptable = (EncryptableProvider)oldObject;
                  if (!encryptable.checkCrypt(compress, encrypt)) {
                     if (ticket == null && PersistentContent.isEncrypted(oldObject)) {
                        return 2;
                     }

                     newObject = encryptable.reCrypt(compress, encrypt);
                     if (newObject != null) {
                        collection.replaceElementAt(oldObject, newObject, index, collectionCookie);
                     }
                     break;
                  }
               }
            }
         }

         if (newObject != null) {
            collection.updateListeners(oldObject, newObject, collectionCookie);
            Process.waitForIdle(250);
         }
      }
   }
}
