package net.rim.device.apps.internal.lbs;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.util.FolderCollection;
import net.rim.device.apps.api.messaging.util.SimpleFolder;

final class FavouritesSimpleFolder extends SimpleFolder implements Persistable {
   static final void removeFolder(long applicationFamily, long hierarchyId) {
      RIMPersistentStore.destroyPersistentObject(4848635152656490553L + applicationFamily + hierarchyId);
   }

   public static final SimpleFolder createInstance(long applicationFamily, long hierarchyId, String friendlyName, SimpleFolder parentFolder, int flags) {
      SimpleFolder hierarchy = null;
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(4848635152656490553L + applicationFamily + hierarchyId);
      synchronized (persistentObject) {
         hierarchy = (SimpleFolder)persistentObject.getContents();
         if (hierarchy == null) {
            hierarchy = new FavouritesSimpleFolder(applicationFamily, hierarchyId, friendlyName, parentFolder, flags, persistentObject);
            persistentObject.setContents(hierarchy, 51);
            persistentObject.commit();
         }

         return hierarchy;
      }
   }

   private FavouritesSimpleFolder(
      long applicationFamily, long uid, String firendlyName, SimpleFolder parentFolder, int flags, PersistentObject persistentObject
   ) {
      super(applicationFamily, uid, firendlyName, null, parentFolder, flags);
      super._persistentObject = persistentObject;
   }

   public FavouritesSimpleFolder(long applicationFamily, long uid, String firendlyName, SimpleFolder parent) {
      super(applicationFamily, uid, firendlyName, null, parent);
   }

   @Override
   public final Collection getContainedItems() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      FolderCollection folderCollection = (FolderCollection)ar.getOrWaitFor(super._applicationFamily + super._uid);
      if (folderCollection == null) {
         folderCollection = new FavouritesCollection();
         ar.put(super._applicationFamily + super._uid, folderCollection);
      }

      folderCollection.initialize(super._applicationFamily, super._uid, SimpleFolder._longKeyProviderAdaptor, this);
      return folderCollection;
   }

   @Override
   public final void removeSubFolder(Folder folder) {
      super.removeSubFolder(folder);
      ((SimpleFolder)folder).setParentFolder(null);
   }

   @Override
   protected final void commit() {
      if (super._parentFolder != null || super._persistentObject != null) {
         super.commit();
      }
   }
}
