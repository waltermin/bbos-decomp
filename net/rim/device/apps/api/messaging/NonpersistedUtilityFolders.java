package net.rim.device.apps.api.messaging;

import java.util.Enumeration;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.model.RIMModel;

public final class NonpersistedUtilityFolders {
   private static final long NON_PERSISTED_UTILITY_FOLDERS = -2509380403571927795L;
   public static final long SAVED_MESSAGES_DISPLAY_CACHE_FOLDER = 7175316403005034194L;

   private NonpersistedUtilityFolders() {
   }

   public static final Folder getFolder(long folderLuid) {
      Folder folder = null;
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      folder = (Folder)registry.get(-2509380403571927795L + folderLuid);
      if (folder == null) {
         synchronized (FolderHierarchies.getLockObject()) {
            synchronized (registry) {
               folder = (Folder)registry.get(-2509380403571927795L + folderLuid);
               if (folder == null) {
                  folder = new NonPersistedUtilityFolder(folderLuid);
                  registry.put(-2509380403571927795L + folderLuid, folder);
               }
            }

            return folder;
         }
      } else {
         return folder;
      }
   }

   public static final void addMessageToUtilityFolder(long folderId, RIMModel message) {
      synchronized (FolderHierarchies.getLockObject()) {
         Folder utilityFolder = getFolder(folderId);
         WritableSet writableSet = (WritableSet)utilityFolder.getContainedItems();
         writableSet.add(message);
      }
   }

   public static final void removeMessageFromUtilityFolder(long folderId, RIMModel message) {
      synchronized (FolderHierarchies.getLockObject()) {
         Folder utilityFolder = getFolder(folderId);
         WritableSet writableSet = (WritableSet)utilityFolder.getContainedItems();
         writableSet.remove(message);
      }
   }

   public static final void updateMessageInUtilityFolder(long folderId, RIMModel message) {
      synchronized (FolderHierarchies.getLockObject()) {
         Folder utilityFolder = getFolder(folderId);
         CollectionListener listener = (CollectionListener)utilityFolder.getContainedItems();
         listener.elementUpdated(null, message, message);
      }
   }

   public static final void ensureUtilityFolderAddedToMerge(long folderId, long mergeId) {
      synchronized (FolderHierarchies.getLockObject()) {
         boolean utilityFolderAlreadyAdded = false;
         Enumeration enumeration = FolderMerge.getMergedFolders(mergeId);
         if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
               Folder folder = (Folder)enumeration.nextElement();
               if (folder.getLUID() == folderId) {
                  utilityFolderAlreadyAdded = true;
               }
            }
         }

         if (!utilityFolderAlreadyAdded) {
            Folder utilityFolder = getFolder(folderId);
            FolderMerge.registerMergedFolder(mergeId, utilityFolder);
         }
      }
   }
}
