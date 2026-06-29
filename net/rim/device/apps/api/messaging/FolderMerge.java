package net.rim.device.apps.api.messaging;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionCombiner;
import net.rim.device.api.collection.LongKeyProviderAdaptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.LongHashtable;

public class FolderMerge {
   private static final long MERGES_HASHTABLE = 928076599182245850L;
   private static LongHashtable _mergesHashtable = ApplicationRegistry.getApplicationRegistry().getLongHashtable(928076599182245850L);
   private static final long MERGE_COLLECTIONS = 3670675554506915300L;
   public static final long MERGE_ID_MESSAGE_CENTRE_ALL = -5581791943352753293L;
   public static final long MERGE_ID_MESSAGE_CENTRE_HIDE_FILED = 2993144521330132876L;
   public static final long MERGE_ID_SAVED_MESSAGES = 6368823655991217730L;
   public static final long MERGE_ID_SEARCH = 7509894771240321003L;
   public static final long MERGE_ID_SMS = -7118119043524803584L;
   public static final long MERGE_ID_MMS = -942103673428357213L;
   public static final long MERGE_ID_SMS_AND_MMS = -4696470826620059293L;
   public static final int MERGE_TYPE_ALL = -271343505;
   public static final int MERGE_TYPE_HIDE_FILED = 734877078;
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
            merge = new LongHashtable();
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
