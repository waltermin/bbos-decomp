package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.OTASyncPriorityProvider;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncCollectionStatusProvider;
import net.rim.device.api.synchronization.SyncEventListener;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.FolderMerge;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.vm.Array;

final class SavedEmailOTASync
   extends EmailMessageSyncBase
   implements CollectionEventSource,
   CollectionListener,
   OTASyncCapable,
   OTASyncPriorityProvider,
   SyncCollectionStatusProvider,
   SyncEventListener {
   private CollectionListenerManager _collectionListenerManager = new CollectionListenerManager();

   public SavedEmailOTASync() {
      CollectionEventSource eventSource = (CollectionEventSource)FolderMerge.getMergeCollection(6368823655991217730L);
      eventSource.addCollectionListener(this);
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return null;
   }

   @Override
   public final int getSyncPriority() {
      return 10;
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      boolean result = super.addSyncObject(object);
      if (result) {
         this.checkForDuplicates(object);
      }

      return result;
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      if (oldObject instanceof EmailMessageModelImpl && newObject instanceof EmailMessageModelImpl) {
         EmailMessageModelImpl oldEmail = (EmailMessageModelImpl)oldObject;
         EmailMessageModelImpl newEmail = (EmailMessageModelImpl)newObject;
         BodyModel newBody = newEmail.getBodyModel();
         BodyModel oldBody = oldEmail.getBodyModel();
         int oldLength = oldBody != null ? oldBody.getText().length() : 0;
         int newLength = newBody != null ? newBody.getText().length() : 0;
         if (newLength >= oldLength) {
            MessageSync.removeMessagePermanently(oldObject);
            this.elementRemoved(this, oldObject);
            this.addSyncObject(newObject);
            this.elementUpdated(this, oldObject, newObject);
            return true;
         }
      }

      return false;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      ReadableList collection = (ReadableList)FolderMerge.getMergeCollection(6368823655991217730L);
      synchronized (FolderHierarchies.getLockObject()) {
         int len = collection.size();

         for (int i = len - 1; i >= 0; i--) {
            Object o = collection.getAt(i);
            if (o instanceof EmailMessageModelImpl) {
               EmailMessageModelImpl email = (EmailMessageModelImpl)o;
               if (!email.flagsSet(8192)) {
                  EmailHierarchy.removeMessage(email, email.getFolderId());
               }
            }
         }

         return true;
      }
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      ReadableList collection = (ReadableList)FolderMerge.getMergeCollection(6368823655991217730L);
      int len = collection.size();
      SyncObject[] objects = new SyncObject[len];
      LowMemoryManager.poll();
      synchronized (FolderHierarchies.getLockObject()) {
         int idx = 0;

         for (int i = 0; i < len; i++) {
            Object o = collection.getAt(i);
            if (o instanceof EmailMessageModelImpl) {
               EmailMessageModelImpl email = (EmailMessageModelImpl)o;
               if (!email.flagsSet(8192)) {
                  objects[idx++] = email;
               }
            }
         }

         Array.resize(objects, idx);
         return objects;
      }
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      ReadableList collection = (ReadableList)FolderMerge.getMergeCollection(6368823655991217730L);
      int len = collection.size();

      for (int i = 0; i < len; i++) {
         SyncObject element = (SyncObject)collection.getAt(i);
         if (element.getUID() == uid) {
            return element;
         }
      }

      return null;
   }

   @Override
   public final int getSyncObjectCount() {
      int count = 0;
      synchronized (FolderHierarchies.getLockObject()) {
         ReadableList collection = (ReadableList)FolderMerge.getMergeCollection(6368823655991217730L);
         int len = collection.size();

         for (int i = 0; i < len; i++) {
            Object o = collection.getAt(i);
            if (o instanceof EmailMessageModelImpl) {
               EmailMessageModelImpl email = (EmailMessageModelImpl)o;
               if (!email.flagsSet(8192)) {
                  count++;
               }
            }
         }

         return count;
      }
   }

   @Override
   public final String getSyncName() {
      return "Saved Email Messages";
   }

   @Override
   public final void reset(Collection collection) {
      this._collectionListenerManager.fireReset(this);
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      if (element instanceof EmailMessageModelImpl) {
         EmailMessageModelImpl email = (EmailMessageModelImpl)element;
         if (!email.flagsSet(8192)) {
            this._collectionListenerManager.fireElementAdded(this, element);
         }
      }
   }

   private final void checkForDuplicates(Object object) {
      if (object instanceof EmailMessageModelImpl) {
         EmailMessageModelImpl email = (EmailMessageModelImpl)object;
         long folderId = email.getFolderId();
         EmailHierarchy hierarchy = EmailHierarchy.getEmailHierarchyForFolder(folderId);
         ReadableList[] collections = new ReadableList[]{
            (ReadableList)hierarchy.getFiledFolder().getContainedItems(), (ReadableList)hierarchy.getUnfiledFolder().getContainedItems()
         };
         synchronized (FolderHierarchies.getLockObject()) {
            for (int i = 0; i < collections.length; i++) {
               int len = collections[i].size();
               int refId = email.getCMIMEReferenceIdentifier();

               for (int j = 0; j < len; j++) {
                  Object obj = collections[i].getAt(j);
                  EmailMessageModelImpl currEmail = (EmailMessageModelImpl)obj;
                  if (obj instanceof EmailMessageModelImpl && currEmail != email && currEmail.getCMIMEReferenceIdentifier() == refId) {
                     EmailHierarchy.removeMessage(
                        currEmail, currEmail.flagsSet(2) ? hierarchy.getFiledFolder().getLUID() : hierarchy.getUnfiledFolder().getLUID()
                     );
                     return;
                  }
               }
            }
         }
      }
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (newElement instanceof EmailMessageModelImpl) {
         EmailMessageModelImpl email = (EmailMessageModelImpl)newElement;
         if (!email.flagsSet(8192)) {
            this._collectionListenerManager.fireElementUpdated(this, oldElement, newElement);
         }
      }
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      if (element instanceof EmailMessageModelImpl) {
         EmailMessageModelImpl email = (EmailMessageModelImpl)element;
         if (!email.flagsSet(8192)) {
            this._collectionListenerManager.fireElementRemoved(this, element);
         }
      }
   }

   @Override
   public final void addCollectionListener(Object listener) {
      this._collectionListenerManager.addCollectionListener(listener);
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      this._collectionListenerManager.removeCollectionListener(listener);
   }

   @Override
   public final void syncEventOccurred(int eventId, Object object) {
   }

   @Override
   public final boolean isWritableForSerialSync() {
      return false;
   }

   @Override
   public final boolean isReadableForSerialSync() {
      return true;
   }

   @Override
   public final boolean isWritableForOTASL() {
      return false;
   }

   @Override
   public final int getOTASLControlMask() {
      return 0;
   }
}
