package net.rim.device.apps.api.messaging.util;

import java.util.Enumeration;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.utility.framework.RecryptableCollectionUtilities;

public final class PersistedSortedCollectionEncryptor {
   private PersistedSortedCollectionEncryptor() {
   }

   public static final void crypt(long applicationFamily, int contentProtectionGeneration) {
      PersistentObject collectionsPersistentObject = RIMPersistentStore.getPersistentObject(applicationFamily);
      if (collectionsPersistentObject != null) {
         LongHashtable collections = (LongHashtable)collectionsPersistentObject.getContents();
         if (collections != null) {
            Enumeration enumeration = collections.elements();

            while (enumeration.hasMoreElements()) {
               Object messageContainer = enumeration.nextElement();
               RecryptableCollectionUtilities.recrypt(
                  new PersistedSortedCollectionEncryptor$RecryptHelper(collectionsPersistentObject),
                  FolderHierarchies.getLockObject(),
                  messageContainer,
                  contentProtectionGeneration
               );
            }
         }
      }
   }
}
