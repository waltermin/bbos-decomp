package net.rim.device.apps.internal.lbs;

import java.util.Enumeration;
import java.util.Stack;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.util.SimpleFolder;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

public final class FavouritesManager implements CollectionListener {
   int _lastSelectedNode;
   private static final String FOLDER_DELIMITER = "\u001f";
   public static final long HIERACRCHY_ID = -4319711987384773568L;
   private static final long GUID = 4428059215303450357L;
   private static FavouritesManager INSTANCE;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private FavouritesManager() {
      SimpleFolder folder = null;
      Collection collection = null;
      boolean var9 = false /* VF: Semaphore variable */;

      try {
         var9 = true;
         folder = getRootFolder();
         if (!(folder instanceof FavouritesSimpleFolder)) {
            FavouritesSimpleFolder.removeFolder(LBSApplication.UID, -4319711987384773568L);
         }

         collection = folder.getContainedItems();
         CollectionEventSource register = (CollectionEventSource)collection;
         register.addCollectionListener(this);
         ReadableList itemsR = (ReadableList)collection;
         LocationDocumentCollection ldc = LocationDocumentCollection.getInstance();
         if (!folder.containsSubFolders() && itemsR.size() == 0) {
            for (int i = 0; i < ldc.size(); i++) {
               addOrUpdateLocation((LocationSyncable)ldc.getAt(i), folder);
            }
         }

         for (int i = 0; i < ldc.size(); i++) {
            LocationSyncable loc = (LocationSyncable)ldc.getAt(i);
            if (loc.getFolderHeirarchies() == null) {
               addOrUpdateLocation(loc, folder);
            }
         }

         var9 = false;
      } finally {
         if (var9) {
            EventLogger.logEvent(
               LBSApplication.UID,
               ((StringBuffer)(new Object("FavouritesManager<init> NPE, folder=")))
                  .append(folder)
                  .append(", collection=")
                  .append(collection)
                  .toString()
                  .getBytes(),
               2
            );
            return;
         }
      }
   }

   public static final void registerOnStartup() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      INSTANCE = (FavouritesManager)ar.getOrWaitFor(4428059215303450357L);
      if (INSTANCE == null) {
         INSTANCE = new FavouritesManager();
         ar.put(4428059215303450357L, INSTANCE);
      }
   }

   static final FavouritesManager getInstance() {
      if (INSTANCE == null) {
         registerOnStartup();
      }

      return INSTANCE;
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
   }

   @Override
   public final void reset(Collection collection) {
   }

   static final void removeFolder(SimpleFolder folder) {
      if (folder != null) {
         SimpleFolder parent = (SimpleFolder)folder.getParentFolder();
         removeFolderItems(folder);
         Stack stack = (Stack)(new Object());
         stack.push(folder.getSubFolders());

         do {
            Enumeration e = (Enumeration)stack.pop();

            while (e.hasMoreElements()) {
               SimpleFolder f = (SimpleFolder)e.nextElement();
               if (f != null) {
                  stack.push(f.getSubFolders());
                  removeFolderItems(f);
                  folder = (SimpleFolder)f.getParentFolder();
                  if (folder != null) {
                     folder.removeSubFolder(f);
                  }
               }
            }
         } while (!stack.empty());

         if (parent != null) {
            parent.removeSubFolder(folder);
         }
      }
   }

   static final void removeFolderItems(SimpleFolder folder) {
      if (folder != null) {
         LocationDocumentCollection ldc = LocationDocumentCollection.getInstance();
         Collection collection = folder.getContainedItems();
         synchronized (collection) {
            ReadableList readList = (ReadableList)collection;

            for (int i = 0; i < readList.size(); i++) {
               LocationSyncable item = (LocationSyncable)readList.getAt(i);
               ldc.removeSyncObject(item);
            }

            ((WritableSet)collection).removeAll();
         }
      }
   }

   static final void removeLocation(LocationSyncable location, SimpleFolder folder) {
      if (folder == null) {
         folder = location.getFolderHeirarchies();
      }

      if (folder == null) {
         folder = getRootFolder();
         Stack stack = (Stack)(new Object());
         stack.push(folder.getSubFolders());

         do {
            ReadableList list = (ReadableList)folder.getContainedItems();
            if (list.getIndex(location) > -1) {
               break;
            }

            folder = null;
            Enumeration e = (Enumeration)stack.pop();

            while (e.hasMoreElements()) {
               SimpleFolder f = (SimpleFolder)e.nextElement();
               list = (ReadableList)f.getContainedItems();
               if (list.getIndex(location) > -1) {
                  folder = f;
                  break;
               }

               stack.push(f.getSubFolders());
            }
         } while (!stack.empty());
      }

      if (folder != null) {
         Collection collection = folder.getContainedItems();
         if (collection instanceof Object) {
            ((WritableSet)collection).remove(location);
         }
      }
   }

   static final void addOrUpdateLocation(LocationSyncable location, SimpleFolder folder) {
      if (folder == null) {
         folder = location.getFolderHeirarchies();
      }

      if (folder == null) {
         folder = getRootFolder();
      }

      Collection collection = folder.getContainedItems();
      if (collection instanceof Object) {
         ((WritableSet)collection).add(location);
      }
   }

   static final SimpleFolder getRootFolder() {
      SimpleFolder root = SimpleFolder.getInstance(LBSApplication.UID, -4319711987384773568L);
      if (root == null) {
         root = FavouritesSimpleFolder.createInstance(LBSApplication.UID, -4319711987384773568L, LBSResources.getString(77), null, 0);
      }

      return root;
   }

   public static final boolean hasFavourites() {
      return LocationDocumentCollection.getInstance().size() > 0;
   }

   public static final String createFolderHeirarchiesString(Folder folders) {
      if (folders == null) {
         return null;
      }

      StringBuffer delimitedFolders = (StringBuffer)(new Object());
      delimitedFolders.insert(0, ((StringBuffer)(new Object())).append(folders.getFriendlyName()).append("\u001f").toString());
      Folder parent = folders;

      while ((parent = parent.getParentFolder()) != null) {
         delimitedFolders.insert(0, ((StringBuffer)(new Object())).append(parent.getFriendlyName()).append("\u001f").toString());
      }

      return delimitedFolders.toString();
   }

   public static final SimpleFolder createFolderHeirarchies(String folders) {
      SimpleFolder folderHeirarchies = null;
      if (folders != null) {
         SimpleFolder currentFolder = getRootFolder();
         folderHeirarchies = currentFolder;
         int start = 0;

         int end;
         while ((end = folders.indexOf("\u001f", start)) > -1) {
            String currentName = folders.substring(start, end);
            SimpleFolder parentFolder = currentFolder;
            if (!currentName.equals(currentFolder.getFriendlyName())) {
               currentFolder = null;
               Enumeration e = parentFolder.getSubFolders();

               while (e.hasMoreElements()) {
                  SimpleFolder f = (SimpleFolder)e.nextElement();
                  if (currentName.equals(f.getFriendlyName())) {
                     currentFolder = f;
                     break;
                  }
               }

               if (currentFolder == null) {
                  long luid = -4319711987384773568L | UIDGenerator.getUID();
                  currentFolder = new FavouritesSimpleFolder(LBSApplication.UID, luid, currentName, parentFolder);
                  parentFolder.putFolder(currentFolder);
               }
            }

            folderHeirarchies = currentFolder;
            start = end + 1;
         }
      }

      return folderHeirarchies;
   }
}
