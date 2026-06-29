package net.rim.device.apps.api.messaging;

import java.util.Enumeration;
import net.rim.device.api.collection.CollectionLock;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.vm.Array;

public class FolderHierarchies {
   private static final long MESSAGE_FOLDER_HIERARCHIES;
   private static final long FOLDER_HIERARCHIES_LOCK_OBJECT_APPLICATION_REGISTRY_LUID;
   private static Folder[] _folderHierarchies;

   private FolderHierarchies() {
   }

   public static Object getLockObject() {
      return CollectionLock.getGlobalLock();
   }

   public static void registerFolderHierarchy(Folder folderHierarchy) {
      long luid = folderHierarchy.getLUID();
      synchronized (_folderHierarchies) {
         int length = _folderHierarchies.length;

         for (int i = length - 1; i >= 0; i--) {
            if (luid == _folderHierarchies[i].getLUID()) {
               return;
            }
         }

         Array.resize(_folderHierarchies, length + 1);
         _folderHierarchies[length] = folderHierarchy;
      }
   }

   public static Folder deregisterFolderHierarchy(Folder folder) {
      long luid = folder.getLUID();
      synchronized (_folderHierarchies) {
         int length = _folderHierarchies.length;

         for (int i = length - 1; i >= 0; i--) {
            if (luid == _folderHierarchies[i].getLUID()) {
               Folder removedFolder = _folderHierarchies[i];
               _folderHierarchies[i] = _folderHierarchies[length - 1];
               Array.resize(_folderHierarchies, length - 1);
               return removedFolder;
            }
         }

         return null;
      }
   }

   public static Enumeration getFolderHierarchies() {
      return (Enumeration)(new Object(_folderHierarchies));
   }

   public static Folder getFolderHierarchy(long hierarchyId) {
      synchronized (_folderHierarchies) {
         for (int i = _folderHierarchies.length - 1; i >= 0; i--) {
            if (hierarchyId == _folderHierarchies[i].getLUID()) {
               return _folderHierarchies[i];
            }
         }

         return null;
      }
   }

   public static Folder getFolder(long folderId) {
      Folder foundFolder = null;

      for (int i = _folderHierarchies.length - 1; foundFolder == null && i >= 0; i--) {
         Folder folderHierarchy = _folderHierarchies[i];
         if (folderHierarchy != null) {
            foundFolder = folderHierarchy.getFolder(folderId);
         }
      }

      return foundFolder;
   }

   public static Folder getFolder(long hierarchyId, long folderId) {
      for (int i = _folderHierarchies.length - 1; i >= 0; i--) {
         Folder fh = _folderHierarchies[i];
         if (fh != null && fh.getLUID() == hierarchyId) {
            return fh.getFolder(folderId);
         }
      }

      return null;
   }

   static {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      _folderHierarchies = (Folder[])applicationRegistry.getOrWaitFor(515427541846697633L);
      if (_folderHierarchies == null) {
         _folderHierarchies = new Folder[0];
         applicationRegistry.put(515427541846697633L, _folderHierarchies);
      }
   }
}
