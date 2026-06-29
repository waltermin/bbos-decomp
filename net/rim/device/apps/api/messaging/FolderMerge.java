package net.rim.device.apps.api.messaging;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionCombiner;
import net.rim.device.api.collection.LongKeyProviderAdaptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.LongHashtable;

public class FolderMerge {
   private static final long MERGES_HASHTABLE;
   private static LongHashtable _mergesHashtable = ApplicationRegistry.getApplicationRegistry().getLongHashtable(928076599182245850L);
   private static final long MERGE_COLLECTIONS;
   public static final long MERGE_ID_MESSAGE_CENTRE_ALL;
   public static final long MERGE_ID_MESSAGE_CENTRE_HIDE_FILED;
   public static final long MERGE_ID_SAVED_MESSAGES;
   public static final long MERGE_ID_SEARCH;
   public static final long MERGE_ID_SMS;
   public static final long MERGE_ID_MMS;
   public static final long MERGE_ID_SMS_AND_MMS;
   public static final int MERGE_TYPE_ALL;
   public static final int MERGE_TYPE_HIDE_FILED;
   protected static LongKeyProviderAdaptor _longKeyProviderAdaptor = new DateSortKeyProviderIndirection();

   private FolderMerge() {
   }

   public static Collection getMergeCollection(long mergeId) {
      MergedCollection mergeCollection = null;
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      mergeCollection = (MergedCollection)ar.getOrWaitFor(mergeId + 3670675554506915300L);
      if (mergeCollection == null) {
         mergeCollection = new MergedCollection(_longKeyProviderAdaptor);
         ar.put(mergeId + 3670675554506915300L, mergeCollection);
         Enumeration mergedFoldersEnumeration = getMergedFolders(mergeId);
         if (mergedFoldersEnumeration != null) {
            while (mergedFoldersEnumeration.hasMoreElements()) {
               Folder folder = (Folder)mergedFoldersEnumeration.nextElement();
               Collection collection = folder.getContainedItems();
               mergeCollection.addSource(collection);
            }
         }
      }

      return mergeCollection;
   }

   public static void registerMergedFolder(long mergeId, Folder folder) {
      LongHashtable merge;
      synchronized (_mergesHashtable) {
         merge = (LongHashtable)_mergesHashtable.get(mergeId);
         if (merge == null) {
            merge = (LongHashtable)(new Object());
            _mergesHashtable.put(mergeId, merge);
         }
      }

      long uid = folder.getLUID();
      merge.put(uid, folder);
      CollectionCombiner collectionCombiner = (CollectionCombiner)getMergeCollection(mergeId);
      Collection collectionToAdd = folder.getContainedItems();
      collectionCombiner.addSource(collectionToAdd);
   }

   public static Folder deregisterMergedFolder(long mergeId, Folder folder) {
      LongHashtable merge = (LongHashtable)_mergesHashtable.get(mergeId);
      if (merge == null) {
         return null;
      }

      long uid = folder.getLUID();
      Folder removedObject = (Folder)merge.remove(uid);
      CollectionCombiner collectionCombiner = (CollectionCombiner)getMergeCollection(mergeId);
      Collection collectionToRemove = folder.getContainedItems();
      collectionCombiner.removeSource(collectionToRemove);
      return removedObject;
   }

   public static Enumeration getMergedFolders(long mergeId) {
      LongHashtable merge = (LongHashtable)_mergesHashtable.get(mergeId);
      return merge == null ? null : merge.elements();
   }

   public static long getMergeCollectionId(long emailHierarchyLuid, int mergeCollectionType) {
      return emailHierarchyLuid + mergeCollectionType;
   }
}
