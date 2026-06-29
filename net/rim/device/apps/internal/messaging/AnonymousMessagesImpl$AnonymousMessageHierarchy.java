package net.rim.device.apps.internal.messaging;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.FolderMerge;
import net.rim.device.apps.api.messaging.NonpersistedUtilityFolders;

final class AnonymousMessagesImpl$AnonymousMessageHierarchy implements Folder {
   private AnonymousMessagesImpl$PersistedUtilityFolder _mainFolder = new AnonymousMessagesImpl$PersistedUtilityFolder(-77939598941636974L);
   private AnonymousMessagesImpl$PersistedUtilityFolder _savedThenOrphanedFolder;

   public AnonymousMessagesImpl$AnonymousMessageHierarchy() {
      FolderMerge.registerMergedFolder(-5581791943352753293L, this._mainFolder);
      FolderMerge.registerMergedFolder(2993144521330132876L, this._mainFolder);
      FolderMerge.registerMergedFolder(7509894771240321003L, this._mainFolder);
      this._savedThenOrphanedFolder = new AnonymousMessagesImpl$PersistedUtilityFolder(-4135505141387831963L);
      FolderMerge.registerMergedFolder(6368823655991217730L, this._savedThenOrphanedFolder);
      ReadableList list = (ReadableList)this._mainFolder.getContainedItems();

      for (int index = 0; index < list.size(); index++) {
         AnonymousMessagesImpl$AnonymousMessageModel message = (AnonymousMessagesImpl$AnonymousMessageModel)list.getAt(index);
         if (message._saved) {
            NonpersistedUtilityFolders.addMessageToUtilityFolder(7175316403005034194L, message);
         }
      }
   }

   final boolean isFirstBoot() {
      return this._mainFolder.wasCreated();
   }

   @Override
   public final long getLUID() {
      return 2261407540084218132L;
   }

   @Override
   public final String getFriendlyName() {
      return "Anonymousway Ierarchyhay";
   }

   @Override
   public final Folder getFolder(long folderUid) {
      if (folderUid == 2261407540084218132L) {
         return this;
      } else if (folderUid == -77939598941636974L) {
         return this._mainFolder;
      } else {
         return folderUid == -4135505141387831963L ? this._savedThenOrphanedFolder : null;
      }
   }

   @Override
   public final Folder getParentFolder() {
      return null;
   }

   @Override
   public final Enumeration getSubFolders() {
      synchronized (FolderHierarchies.getLockObject()) {
         Object[] objects = new Object[]{this._mainFolder, this._savedThenOrphanedFolder};
         return (Enumeration)(new Object(objects));
      }
   }

   @Override
   public final boolean containsSubFolders() {
      return true;
   }

   @Override
   public final Collection getContainedItems() {
      return null;
   }

   @Override
   public final Folder getBaseFolder() {
      return this._mainFolder;
   }

   @Override
   public final boolean canContainItems() {
      return true;
   }

   @Override
   public final boolean isVisible(Object context) {
      return false;
   }
}
