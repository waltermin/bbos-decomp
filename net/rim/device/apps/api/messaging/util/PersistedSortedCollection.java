package net.rim.device.apps.api.messaging.util;

import net.rim.device.api.collection.LongKeyProviderAdaptor;
import net.rim.device.api.collection.LongKeyProviderAdaptorComparator;
import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.messaging.FolderHierarchies;

public class PersistedSortedCollection extends SortedCollection {
   protected PersistentObject _collectionsPersistentObject;
   protected static final long SORTED_FOLDER_COLLECTIONS = 2113200491979413031L;

   @Override
   public boolean initialize(long applicationFamily, long folderId, LongKeyProviderAdaptor longKeyProviderAdaptor, Object context) {
      return this.initialize(applicationFamily, folderId, new LongKeyProviderAdaptorComparator(longKeyProviderAdaptor), context);
   }

   @Override
   public boolean initialize(long applicationFamily, long folderId, Comparator comparator, Object context) {
      synchronized (FolderHierarchies.getLockObject()) {
         boolean created = false;
         super._comparator = comparator;
         boolean noMessages = true;
         if (super._useBigVector && super._messagesAsBigVector != null) {
            noMessages = false;
         } else if (!super._useBigVector && super._messagesAsArray != null) {
            noMessages = false;
         }

         if (noMessages || folderId != super._folderId) {
            LongHashtable collections = null;
            this._collectionsPersistentObject = RIMPersistentStore.getPersistentObject(applicationFamily);
            collections = (LongHashtable)this._collectionsPersistentObject.getContents();
            if (collections == null) {
               collections = new LongHashtable();
               this._collectionsPersistentObject.setContents(collections, 51);
               this._collectionsPersistentObject.commit();
               created = true;
            }

            if (super._folderId != 0 && !noMessages) {
               if (super._useBigVector) {
                  collections.put(folderId, super._messagesAsBigVector);
               } else {
                  collections.put(folderId, super._messagesAsArray);
               }

               collections.remove(super._folderId);
               super._folderId = folderId;
               this._collectionsPersistentObject.commit();
            } else {
               super._folderId = folderId;
               Object o = collections.get(super._folderId);
               if (o == null) {
                  super._useBigVector = false;
                  super._messagesAsArray = new Object[0];
                  collections.put(super._folderId, super._messagesAsArray);
                  this._collectionsPersistentObject.commit();
               } else if (o instanceof Object[]) {
                  super._useBigVector = false;
                  super._messagesAsArray = (Object[])o;
               } else {
                  super._useBigVector = true;
                  super._messagesAsBigVector = (BigVector)o;
               }

               this.traverseItemsToInitialize(context);
            }
         }

         return created;
      }
   }

   public void destroy() {
      synchronized (FolderHierarchies.getLockObject()) {
         if (this._collectionsPersistentObject != null) {
            LongHashtable collections = (LongHashtable)this._collectionsPersistentObject.getContents();
            if (collections != null) {
               collections.remove(super._folderId);
            }

            this._collectionsPersistentObject = null;
            super._folderId = 0;
         }
      }
   }

   private void commitCollection() {
      if (super._useBigVector) {
         PersistentObject.commit(super._messagesAsBigVector);
      } else {
         PersistentObject.commit(super._messagesAsArray);
      }
   }

   @Override
   public void add(Object message) {
      synchronized (FolderHierarchies.getLockObject()) {
         super.add(message);
         this.commitCollection();
      }
   }

   public void addMessageWithoutCommittingFolder(Object message) {
      synchronized (FolderHierarchies.getLockObject()) {
         this.suspendNotification(null);
         super.insertElementAtCorrectLocation(message);
         PersistentObject.commit(message);
      }
   }

   public void addBatchedMessagesWithoutCommittingFolder(Object[] messages) {
      synchronized (FolderHierarchies.getLockObject()) {
         this.suspendNotification(null);

         for (int i = messages.length - 1; i > -1; i--) {
            super.insertElementAtCorrectLocation(messages[i]);
            PersistentObject.commit(messages[i]);
         }
      }
   }

   public void commitFolder() {
      synchronized (FolderHierarchies.getLockObject()) {
         this.commitCollection();
         this.resumeNotification(null);
      }
   }

   @Override
   public void remove(Object item, boolean forceNotification) {
      synchronized (FolderHierarchies.getLockObject()) {
         super.remove(item, forceNotification);
         this.commitCollection();
      }
   }

   @Override
   public void removeAll() {
      synchronized (FolderHierarchies.getLockObject()) {
         super.removeAll();
         this.commitCollection();
      }
   }

   @Override
   protected void promote() {
      super.promote();
      if (this._collectionsPersistentObject != null) {
         LongHashtable collections = (LongHashtable)this._collectionsPersistentObject.getContents();
         if (collections != null) {
            collections.put(super._folderId, super._messagesAsBigVector);
         }

         this._collectionsPersistentObject.commit();
      }
   }

   @Override
   protected void demote() {
      super.demote();
      if (this._collectionsPersistentObject != null) {
         LongHashtable collections = (LongHashtable)this._collectionsPersistentObject.getContents();
         if (collections != null) {
            collections.put(super._folderId, super._messagesAsArray);
         }

         this._collectionsPersistentObject.commit();
      }
   }
}
